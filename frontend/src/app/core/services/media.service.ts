import { Injectable, inject } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";

import { API } from "@core/config/api.config";
import { MediaImage } from "@core/models/media.model";
import { Paginated } from "@core/models/paginated.model";

@Injectable({
  providedIn: "root",
})
export class MediaService {
  private readonly http = inject(HttpClient);

  count(): Observable<number> {
    return this.http.get<number>(API.base + API.media.count);
  }

  getMediaByUser(userId: string, page = 0, size = 20): Observable<Paginated<MediaImage>> {
    const params = new HttpParams()
      .set("page", page.toString())
      .set("size", size.toString());

    return this.http.get<Paginated<MediaImage>>(
      API.base + API.media.byUser(userId),
      { params }
    );
  }

  deleteMedia(id: string): Observable<void> {
    return this.http.delete<void>(API.base + API.media.delete(id));
  }

  deleteAvatar(id: string): Observable<number> {
    return this.http.delete<number>(API.base + API.media.delete(id));
  }

  getImageUrl(id: string): string {
    return `${API.base}${API.media.byId(id)}`;
  }
}
