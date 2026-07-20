import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [RouterLink, MatButtonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section class="wrap">
      <h1>404</h1>
      <p class="muted">The page you're looking for doesn't exist.</p>
      <a mat-flat-button color="primary" routerLink="/">Go home</a>
    </section>
  `,
  styles: [`.wrap{display:flex;flex-direction:column;align-items:center;justify-content:center;gap:12px;min-height:60vh;text-align:center}h1{font-size:80px;margin:0}`],
})
export class NotFoundPage {}
