import { Injectable, computed, inject, signal } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
import { finalize, Observable, shareReplay, tap } from "rxjs";

import { API } from "@core/config/api.config";
import {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  UserRole,
} from "@core/models/user.model";

import { TokenStorage } from "./token.storage";
import { decodeJwt, isExpired } from "./jwt.util";

@Injectable({ providedIn: "root" })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly storage = inject(TokenStorage);
  private readonly router = inject(Router);

  private readonly _token = signal<string | null>(this.storage.getToken());

  private refreshObservable?: Observable<AuthResponse>;

  readonly token = this._token.asReadonly();

  readonly payload = computed(() => {
    const token = this._token();

    if (!token || isExpired(token)) {
      return null;
    }

    return decodeJwt(token);
  });

  readonly isAuthenticated = computed(() => {
    const token = this._token();
    return !!token && !isExpired(token);
  });

  readonly role = computed<UserRole | null>(() => this.payload()?.role ?? null);

  readonly userId = computed(() => this.payload()?.sub ?? null);

  readonly isSeller = computed(() => this.role() === "SELLER");

  readonly isAdmin = computed(() => this.role() === "ADMIN");

  login(body: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(API.base + API.auth.login, body, {
        withCredentials: true,
      })
      .pipe(
        tap((r) => {
          this.setSession(r);
        })
      );
  }

  register(body: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(API.base + API.auth.register, body, {
        withCredentials: true,
      })
      .pipe(
        tap((r) => {
          this.setSession(r);
        })
      );
  }

  logout(redirect = true): Observable<void> {
    return this.http
      .post<void>(API.base + API.auth.logout, {}, { withCredentials: true })
      .pipe(
        tap(() => {
          this.clearSession();

          if (redirect) {
            this.router.navigateByUrl("/auth/login");
          }
        })
      );
  }

  hasRole(roles: UserRole[]): boolean {
    const role = this.role();
    return !!role && roles.includes(role);
  }

  refresh(): Observable<AuthResponse> {
    if (this.refreshObservable) {
      return this.refreshObservable;
    }

    this.refreshObservable = this.http
      .post<AuthResponse>(
        API.base + API.auth.refresh,
        {},
        { withCredentials: true }
      )
      .pipe(
        tap((auth) => this.setSession(auth)),
        finalize(() => (this.refreshObservable = undefined)),
        shareReplay(1)
      );

    return this.refreshObservable;
  }

  setSession(auth: AuthResponse): void {
    console.log("Saving token", auth.accessToken);

    this.storage.setToken(auth.accessToken);
    this._token.set(auth.accessToken);

    console.log("Signal token:", this._token());
    console.log("Storage token:", this.storage.getToken());
  }

  restoreToken(): void {
    const token = this.storage.getToken();

    if (!token) {
      return;
    }

    this._token.set(token);
  }

  clearSession(): void {
    this.storage.clear();
    this._token.set(null);
  }
}
