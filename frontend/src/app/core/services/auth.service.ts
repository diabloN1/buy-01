import { Injectable, computed, inject, signal } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
import { finalize, Observable, shareReplay, tap } from "rxjs";
import { CurrentUserService } from "./current-user.service";

import { API } from "../config/api.config";
import {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  UserRole,
} from "../models/user.model";

import { TokenStorage } from "./token.storage";
import { decodeJwt, isExpired } from "./jwt.util";

@Injectable({ providedIn: "root" })
export class AuthService {
  private readonly currentUser = inject(CurrentUserService);
  private readonly http = inject(HttpClient);
  private readonly storage = inject(TokenStorage);
  private readonly router = inject(Router);

  private readonly _token = signal<string | null>(this.storage.getToken());

  private refreshInProgress = false;
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

  constructor() {
    this.validateSession();
  }

  login(body: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(API.base + API.auth.login, body, {
        withCredentials: true,
      })
      .pipe(
        tap((r) => {
          this.setSession(r);
          this.currentUser.load();
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
          this.currentUser.load();
        })
      );
  }

  logout(redirect = true): Observable<void> {
    return this.http
      .post<void>(
        API.base + API.auth.logout,
        {},
        {
          withCredentials: true,
        }
      )
      .pipe(
        tap(() => {
          this.storage.clear();
          this._token.set(null);
          this.currentUser.clear();

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

  private validateSession(): void {
    const token = this.storage.getToken();

    const stale = !token || isExpired(token);

    if (stale) {
      this.storage.clear();
      this._token.set(null);
    } else {
      this._token.set(token);
      this.currentUser.load();
    }
  }

  refresh(): Observable<AuthResponse> {
    if (this.refreshInProgress && this.refreshObservable) {
      return this.refreshObservable;
    }

    this.refreshInProgress = true;

    this.refreshObservable = this.http
      .post<AuthResponse>(
        API.base + API.auth.refresh,
        {},
        {
          withCredentials: true,
        }
      )
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
    this._token.set(res.accessToken);
  }
}
