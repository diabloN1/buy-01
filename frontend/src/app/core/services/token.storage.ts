import { Injectable } from "@angular/core";
import { STORAGE_KEYS } from "@core/constants/storage.keys";

@Injectable({ providedIn: "root" })
export class TokenStorage {
  getToken(): string | null {
    return sessionStorage.getItem(STORAGE_KEYS.token);
  }

  setToken(token: string): void {
    sessionStorage.setItem(STORAGE_KEYS.token, token);
  }

  clear(): void {
    sessionStorage.removeItem(STORAGE_KEYS.token);
  }
}
