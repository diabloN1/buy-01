import { Injectable, computed, inject, signal } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
import { Observable, tap } from "rxjs";
import { API } from "../config/api.config";
import {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  User,
  UserRole,
} from "../models/user.model";
import { TokenStorage } from "./token.storage";
import { isExpired } from "./jwt.util";

@Injectable({ providedIn: "root" })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly storage = inject(TokenStorage);
  private readonly router = inject(Router);

  private readonly _user = signal<User | null>(this.storage.getUser());
  readonly user = this._user.asReadonly();
  readonly isAuthenticated = computed(() => {
    const t = this.storage.getToken();
    return !!t && !isExpired(t) && !!this._user();
  });
  readonly role = computed<UserRole | null>(() => this._user()?.role ?? null);
  readonly isSeller = computed(() => this.role() === "SELLER");

  private expiryTimer: ReturnType<typeof setTimeout> | null = null;

  constructor() {
    this.validateSession();
  }

  login(body: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(API.base + API.auth.login, body)
      .pipe(tap((r) => this.setSession(r)));
  }
  register(body: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(API.base + API.auth.register, body)
      .pipe(tap((r) => this.setSession(r)));
  }
  logout(redirect = true): void {
    this.storage.clear();
    this._user.set(null);
    if (this.expiryTimer) clearTimeout(this.expiryTimer);
    if (redirect) this.router.navigateByUrl("/auth/login");
  }

  hasRole(roles: UserRole[]): boolean {
    const r = this.role();
    return !!r && roles.includes(r);
  }

  /**
   * Called once at app startup.
   * Clears the session if the stored token is expired or if
   * the token and user record are in an inconsistent state.
   * This prevents stale sessionStorage tokens from being attached
   * to requests by the interceptor after a page refresh.
   */
  private validateSession(): void {
    const token = this.storage.getToken();
    const user = this.storage.getUser();
    const stale = !token || !user || isExpired(token);
    if (stale) {
      this.storage.clear();
      this._user.set(null);
    } else {
      this.scheduleAutoLogout(token);
    }
  }

  private setSession(res: AuthResponse): void {
    this.storage.setToken(res.token);
    this.storage.setUser(res.user);
    this._user.set(res.user);

    this.scheduleAutoLogout(res.token);
  }

  private scheduleAutoLogout(token: string): void {
    if (this.expiryTimer) clearTimeout(this.expiryTimer);
    const [, payload] = token.split(".");
    try {
      const { exp } = JSON.parse(atob(payload)) as { exp?: number };
      if (!exp) return;
      const ms = exp * 1000 - Date.now();
      if (ms <= 0) return this.logout();
      this.expiryTimer = setTimeout(() => this.logout(), ms);
    } catch {
      /* ignore */
    }
  }
}
