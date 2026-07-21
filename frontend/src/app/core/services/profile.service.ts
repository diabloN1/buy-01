import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { API } from "../config/api.config";
import { UpdateProfileRequest, User } from "../models/user.model";

@Injectable({ providedIn: "root" })
export class ProfileService {
  private readonly http = inject(HttpClient);
  me(): Observable<User> {
    return this.http.get<User>(API.base + API.profile.me);
  }
  update(body: UpdateProfileRequest): Observable<User> {
    return this.http.put<User>(API.base + API.profile.me, body);
  }
}
