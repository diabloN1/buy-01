import { Injectable, inject, signal } from "@angular/core";
import { finalize } from "rxjs";

import { User } from "@core/models/user.model";
import { UserService } from "./user.service";

@Injectable({
  providedIn: "root",
})
export class CurrentUserService {
  private readonly userService = inject(UserService);

  private readonly _user = signal<User | null>(null);
  private readonly _loading = signal(false);

  readonly user = this._user.asReadonly();
  readonly loading = this._loading.asReadonly();

  load(force = false): void {
    if (this._loading()) {
      return;
    }

    if (!force && this._user()) {
      return;
    }

    this._loading.set(true);

    this.userService
      .me()
      .pipe(
        finalize(() => {
          this._loading.set(false);
        })
      )
      .subscribe({
        next: (user) => this._user.set(user),
        error: () => this._user.set(null),
      });
  }

  refresh(): void {
    this.load(true);
  }

  clear(): void {
    this._user.set(null);
  }
}
