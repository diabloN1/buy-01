import { Injectable, inject, signal } from "@angular/core";
import { User } from "@core/models/user.model";
import { UserService } from "./user.service";

@Injectable({
  providedIn: "root",
})
export class CurrentUserService {
  private readonly userService = inject(UserService);

  private readonly _user = signal<User | null>(null);

  readonly user = this._user.asReadonly();

  load(): void {
    this.userService.me().subscribe({
      next: (user) => this._user.set(user),
      error: () => this._user.set(null),
    });
  }

  clear(): void {
    this._user.set(null);
  }
}
