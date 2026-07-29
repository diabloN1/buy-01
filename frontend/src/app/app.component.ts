import { ChangeDetectionStrategy, Component, inject } from "@angular/core";
import { RouterOutlet } from "@angular/router";
import { MainLayoutComponent } from "@core/layout/main-layout.component";
import { ThemeService } from "@core/services/theme.service";

@Component({
  selector: "app-root",
  standalone: true,
  imports: [RouterOutlet, MainLayoutComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<app-main-layout><router-outlet /></app-main-layout>`,
})
export class AppComponent {
  private readonly theme = inject(ThemeService);
  constructor() {
    this.theme.init();
  }
}
