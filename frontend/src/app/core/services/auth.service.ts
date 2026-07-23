import { Injectable, computed, inject, signal } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
import { finalize, Observable, shareReplay, tap, throwError } from "rxjs";
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
  private readonly _token = signal<string | null>(this.storage.getToken());

  private refreshInProgress = false;
  private refreshObservable?: Observable<AuthResponse>;

  readonly token = this._token.asReadonly();
  readonly user = this._user.asReadonly();
  readonly isAuthenticated = computed(() => {
    const user = this._user();
    const t = this.storage.getToken();

    return !!t && !isExpired(t) && !!user;
  });
  readonly role = computed<UserRole | null>(() => this._user()?.role ?? null);
  readonly isSeller = computed(() => this.role() === "SELLER");
  readonly isAdmin = computed(() => this.role() === "ADMIN");

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

    this._token.set(null);
    this._user.set(null);

    if (this.expiryTimer) {
      clearTimeout(this.expiryTimer);
    }

    if (redirect) {
      this.router.navigateByUrl("/auth/login");
    }
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
    const refreshToken = this.storage.getRefreshToken();
    const user = this.storage.getUser();

    const stale = !token || !refreshToken || !user || isExpired(token);

    if (stale) {
      this.storage.clear();
      this._token.set(null);
      this._user.set(null);
    } else {
      this._token.set(token);
      this._user.set(user);
    }
  }
  refresh(): Observable<AuthResponse> {
    if (this.refreshInProgress && this.refreshObservable) {
      return this.refreshObservable;
    }

    const refreshToken = this.storage.getRefreshToken();

    if (!refreshToken) {
      this.logout(false);
      console.log("Hereeee !!!!!!!!11");
      return throwError(() => new Error("Missing refresh token")); // We used the ThrowError from rxjs cause the      interceptor expect Observable machi normal js exception soo to be catched we used ThrowError that returns Observable<never>
    }

    this.refreshInProgress = true;

    this.refreshObservable = this.http
      .post<AuthResponse>(API.base + API.auth.refresh, {
        refreshToken,
      })
      .pipe(
        tap((response) => this.setSession(response)),
        finalize(() => {
          this.refreshInProgress = false;
          this.refreshObservable = undefined;
        }),
        shareReplay(1)
      );

    return this.refreshObservable;
  }

  private setSession(res: AuthResponse): void {
    this.storage.setToken(res.accessToken);
    this.storage.setRefreshToken(res.refreshToken);
    this.storage.setUser(res.user);

    this._token.set(res.accessToken);
    this._user.set(res.user);
  }
}
