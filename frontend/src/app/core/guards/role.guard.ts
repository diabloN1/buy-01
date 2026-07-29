import { CanActivateFn, Router } from "@angular/router";
import { inject } from "@angular/core";
import { AuthService } from "@core/services/auth.service";
import { UserRole } from "@core/models/user.model";
import { NotificationService } from "@core/services/notification.service";

export const roleGuard =
  (roles: UserRole[]): CanActivateFn =>
  () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    const notify = inject(NotificationService);
    if (auth.hasRole(roles)) return true;
    notify.error("Access denied.");
    return router.createUrlTree(["/"]);
  };
