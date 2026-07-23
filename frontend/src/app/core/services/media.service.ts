import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";

import { API } from "@core/config/api.config";

@Injectable({
  providedIn: "root",
})
export class MediaService {
  private readonly http = inject(HttpClient);

  count(): Observable<number> {
    return this.http.get<number>(API.base + API.media.count);
  }
}
