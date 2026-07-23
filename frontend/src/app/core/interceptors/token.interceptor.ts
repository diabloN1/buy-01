import { HttpErrorResponse, HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";
import { catchError, switchMap, throwError } from "rxjs";

import { API } from "../config/api.config";
import { AuthService } from "../services/auth.service";
import { TokenStorage } from "../services/token.storage";

export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
  const storage = inject(TokenStorage);
  const auth = inject(AuthService);

  const token = storage.getToken();

  const authRequest =
    req.url.includes(API.auth.login) ||
    req.url.includes(API.auth.register) ||
    req.url.includes(API.auth.refresh);

  const request =
    token && !authRequest
      ? req.clone({
          setHeaders: {
            Authorization: `Bearer ${token}`,
          },
        })
      : req;

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status !== 401 || authRequest) {
        return throwError(() => error);
      }

      return auth.refresh().pipe(
        switchMap(() => {
          const newToken = storage.getToken();

          if (!newToken) {
            return throwError(() => error);
          }

          return next(
            req.clone({
              setHeaders: {
                Authorization: `Bearer ${newToken}`,
              },
            })
          );
        }),

        catchError((refreshError) => {
          auth.logout();

          return throwError(() => refreshError);
        })
      );
    })
  );
};
