import { HttpErrorResponse, HttpInterceptorFn } from "@angular/common/http";
import { Injector, inject } from "@angular/core";
import { catchError, switchMap, throwError } from "rxjs";

import { API } from "../config/api.config";
import { AuthService } from "../services/auth.service";
import { TokenStorage } from "../services/token.storage";

export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
  const injector = inject(Injector);

  const auth = injector.get(AuthService);
  const storage = inject(TokenStorage);

  console.log("Interceptor token:", auth.token());

  const token = storage.getToken();

  const isAuthRequest =
    req.url.includes(API.auth.login) ||
    req.url.includes(API.auth.register) ||
    req.url.includes(API.auth.refresh);

  console.log("Request:", req.url);
  console.log("Is auth request:", isAuthRequest);

  if (token && !isAuthRequest) {
    console.log("Adding Authorization header");
  }

  const request =
    token && !isAuthRequest
      ? req.clone({
          setHeaders: {
            Authorization: `Bearer ${token}`,
          },
        })
      : req;

  console.log(request.headers.get("Authorization"));

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
