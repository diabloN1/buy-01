import { Injectable } from "@angular/core";
import { STORAGE_KEYS } from "../constants/storage.keys";
import { User } from "../models/user.model";

@Injectable({ providedIn: "root" })
export class TokenStorage {
  private memoryToken: string | null = null;

  getToken(): string | null {
    return this.memoryToken ?? sessionStorage.getItem(STORAGE_KEYS.token);
  }

  setToken(token: string): void {
    console.trace("setToken");

    this.memoryToken = token;
    sessionStorage.setItem(STORAGE_KEYS.token, token);
  }

  getUser(): User | null {
    const raw = sessionStorage.getItem(STORAGE_KEYS.user);
    return raw ? JSON.parse(raw) : null;
  }

  setUser(user: User): void {
    console.trace("setUser");

    sessionStorage.setItem(STORAGE_KEYS.user, JSON.stringify(user));
  }

  clear(): void {
    console.trace("TokenStorage.clear() CALLED");

    this.memoryToken = null;

    sessionStorage.removeItem(STORAGE_KEYS.token);
    sessionStorage.removeItem(STORAGE_KEYS.user);
  }
}
