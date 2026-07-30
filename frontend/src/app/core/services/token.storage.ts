import { Injectable } from "@angular/core";
import { STORAGE_KEYS } from "@core/constants/storage.keys";

@Injectable({ providedIn: "root" })
export class TokenStorage {
  getToken(): string | null {
    return localStorage.getItem(STORAGE_KEYS.token);
  }

  setToken(token: string): void {
    localStorage.setItem(STORAGE_KEYS.token, token);
  }

  clear(): void {
    localStorage.removeItem(STORAGE_KEYS.token);
  }
}
