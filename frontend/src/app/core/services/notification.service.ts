import { Injectable, inject } from "@angular/core";
import { MatSnackBar } from "@angular/material/snack-bar";

@Injectable({ providedIn: "root" })
export class NotificationService {
  private readonly snack = inject(MatSnackBar);
  success(msg: string) {
    this.snack.open(msg, "OK", { duration: 3000, panelClass: "snack-success" });
  }
  error(msg: string) {
    this.snack.open(msg, "Close", {
      duration: 5000,
      panelClass: "snack-error",
    });
  }
  info(msg: string) {
    this.snack.open(msg, "OK", { duration: 3000 });
  }
}
