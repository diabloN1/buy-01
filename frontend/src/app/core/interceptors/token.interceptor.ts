import { HttpErrorResponse, HttpInterceptorFn } from "@angular/common/http";
import { Injector, inject } from "@angular/core";
import { catchError, switchMap, throwError } from "rxjs";

import { API } from "@core/config/api.config";
import { AuthService } from "@core/services/auth.service";
import { TokenStorage } from "@core/services/token.storage";

export const tokenInterceptor: HttpInterceptorFn = (req, next) => {

  const auth = inject(AuthService);
  const storage = inject(TokenStorage);

  // console.log("Interceptor token:", auth.token());

  const token = storage.getToken();

  const isAuthRequest =
    req.url.includes(API.auth.login) ||
    req.url.includes(API.auth.register) ||
    req.url.includes(API.auth.refresh);

  const request =
    token && !isAuthRequest
      ? req.clone({
          setHeaders: {
            Authorization: `Bearer ${token}`,
          },
        })
      : req;

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status !== 401 || isAuthRequest) {
        return throwError(() => error);
      }

      return auth.refresh().pipe(
        switchMap(() => {
          const newToken = auth.token();

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
          auth.clearSession();

          return throwError(() => refreshError);
        })
      );
    })
  );
};
