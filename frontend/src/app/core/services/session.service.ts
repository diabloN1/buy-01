import { Injectable, inject } from "@angular/core";
import { EMPTY, Observable, switchMap, tap, catchError, of } from "rxjs";

import { AuthService } from "./auth.service";
import { isExpired } from "./jwt.util";

@Injectable({
  providedIn: "root",
})
export class SessionService {
  private readonly auth = inject(AuthService);

  initialize(): Observable<void> {
    const token = this.auth.token();

    if (!token) {
      return of(undefined);
    }

    if (!isExpired(token)) {
      return of(undefined);
    }

    return this.auth.refresh().pipe(
      switchMap(() => of(undefined)),
      catchError(() => {
        this.auth.clearSession();
        return EMPTY;
      }),
    );
  }
}
