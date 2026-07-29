import { Injectable, signal } from "@angular/core";
import { STORAGE_KEYS } from "@core/constants/storage.keys";

type Mode = "light" | "dark";

@Injectable({ providedIn: "root" })
export class ThemeService {
  readonly mode = signal<Mode>("light");

  init(): void {
    const saved =
      (localStorage.getItem(STORAGE_KEYS.theme) as Mode | null) ?? "light";
    this.set(saved);
  }
  toggle(): void {
    this.set(this.mode() === "light" ? "dark" : "light");
  }
  set(mode: Mode): void {
    this.mode.set(mode);
    document.body.classList.toggle("dark-theme", mode === "dark");
    localStorage.setItem(STORAGE_KEYS.theme, mode);
  }
}
