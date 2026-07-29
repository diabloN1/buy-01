import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { API } from "@core/config/api.config";
import { UpdateProfileRequest, User } from "@core/models/user.model";

@Injectable({ providedIn: "root" })
export class ProfileService {
  private readonly http = inject(HttpClient);

  me(): Observable<User> {
    return this.http.get<User>(API.base + API.profile.me);
  }

  update(body: UpdateProfileRequest): Observable<User> {
    return this.http.put<User>(API.base + API.profile.me, body);
  }

  uploadAvatar(file: File): Observable<User> {
    const formData = new FormData();
    formData.append("image", file);

    return this.http.post<User>(API.base + API.profile.avatar, formData);
  }

  deleteAvatar(): Observable<void> {
    return this.http.delete<void>(API.base + API.profile.avatar);
  }
}
