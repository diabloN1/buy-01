import { Injectable, inject } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";

import { API } from "@core/config/api.config";
import { Paginated } from "@core/models/paginated.model";
import { User, UserWidget } from "@core/models/user.model";

@Injectable({
  providedIn: "root",
})
export class UserService {
  private readonly http = inject(HttpClient);

  list(page = 1, pageSize = 10): Observable<Paginated<User>> {
    const params = new HttpParams().set("page", page - 1).set("size", pageSize);

    return this.http.get<Paginated<User>>(API.base + API.users.root, {
      params,
    });
  }

  get(id: string): Observable<User> {
    return this.http.get<User>(API.base + API.users.byId(id));
  }


  getWidget(id: string): Observable<UserWidget> {
    return this.http.get<UserWidget>(API.base + API.users.widgetbyId(id));
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(API.base + API.users.byId(id));
  }

  count(): Observable<number> {
    return this.http.get<number>(API.base + API.users.count);
  }
}
