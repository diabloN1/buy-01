import { Injectable } from "@angular/core";
import { STORAGE_KEYS } from "../constants/storage.keys";
import { User } from "../models/user.model";

@Injectable({ providedIn: "root" })
export class TokenStorage {
  getToken(): string | null {
    return sessionStorage.getItem(STORAGE_KEYS.token);
  }

  setToken(token: string): void {
    sessionStorage.setItem(STORAGE_KEYS.token, token);
  }

  getRefreshToken(): string | null {
    return sessionStorage.getItem(STORAGE_KEYS.refreshToken);
  }

  setRefreshToken(token: string): void {
    sessionStorage.setItem(STORAGE_KEYS.refreshToken, token);
  }

  getUser(): User | null {
    const raw = sessionStorage.getItem(STORAGE_KEYS.user);
    return raw ? JSON.parse(raw) : null;
  }

  setUser(user: User): void {
    sessionStorage.setItem(STORAGE_KEYS.user, JSON.stringify(user));
  }

  clear(): void {
    sessionStorage.removeItem(STORAGE_KEYS.token);
    sessionStorage.removeItem(STORAGE_KEYS.refreshToken);
    sessionStorage.removeItem(STORAGE_KEYS.user);
  }
}
