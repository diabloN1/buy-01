import {
  ApplicationConfig,
  provideZoneChangeDetection,
  ErrorHandler,
  provideAppInitializer,
  inject,
} from "@angular/core";
import {
  provideRouter,
  withComponentInputBinding,
  withInMemoryScrolling,
} from "@angular/router";
import { provideAnimations } from "@angular/platform-browser/animations";
import { provideHttpClient, withInterceptors } from "@angular/common/http";
import { APP_ROUTES } from "./app.routes";
import { tokenInterceptor } from "./core/interceptors/token.interceptor";
import { errorInterceptor } from "./core/interceptors/error.interceptor";
import { GlobalErrorHandler } from "./core/services/global-error-handler";
import { SessionService } from "@core/services/session.service";

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),

    provideRouter(
      APP_ROUTES,
      withComponentInputBinding(),
      withInMemoryScrolling({
        scrollPositionRestoration: "top",
      })
    ),

    provideAnimations(),

    provideHttpClient(withInterceptors([tokenInterceptor, errorInterceptor])),

    provideAppInitializer(() => {
      const session = inject(SessionService);
      return session.initialize();
    }),

    {
      provide: ErrorHandler,
      useClass: GlobalErrorHandler,
    },
  ],
};
