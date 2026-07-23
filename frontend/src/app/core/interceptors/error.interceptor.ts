import { HttpErrorResponse, HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";
import { Router } from "@angular/router";
import { catchError, throwError } from "rxjs";
import { AuthService } from "../services/auth.service";
import { NotificationService } from "../services/notification.service";

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const notify = inject(NotificationService);

  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 403) {
        notify.error("You do not have permission to perform this action.");
      } else if (err.status === 0) {
        notify.error("Network error. Please check your connection.");
      } else if (err.status >= 500) {
        notify.error("Server error. Please try again later.");
      } else if (err.status !== 401) {
        const msg =
          (err.error && (err.error.message || err.error.error)) || err.message;

        if (msg) notify.error(msg);
      }

      return throwError(() => err);
    })
  );
};
