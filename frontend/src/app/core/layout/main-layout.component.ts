import {
  ChangeDetectionStrategy,
  Component,
  effect,
  inject,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterLink, RouterLinkActive } from "@angular/router";
import { MatToolbarModule } from "@angular/material/toolbar";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatMenuModule } from "@angular/material/menu";
import { MatDividerModule } from "@angular/material/divider";
import { AuthService } from "../services/auth.service";
import { ThemeService } from "../services/theme.service";

@Component({
  selector: "app-main-layout",
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CommonModule,
    RouterLink,
    RouterLinkActive,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatDividerModule,
  ],
  template: `
    <mat-toolbar class="app-toolbar">
      <a routerLink="/" class="brand"
        ><mat-icon>storefront</mat-icon><span>Marketplace</span></a
      >
      <nav class="nav">
        <a mat-button routerLink="/products" routerLinkActive="active"
          >Products</a
        >
        @if (auth.isSeller()) {
        <a mat-button routerLink="/dashboard" routerLinkActive="active"
          >Dashboard</a
        >
        <a mat-button routerLink="/seller/products" routerLinkActive="active"
          >My Products</a
        >
        <a mat-button routerLink="/media" routerLinkActive="active">Media</a>
        }
      </nav>
      <span class="grow"></span>
      <button
        mat-icon-button
        (click)="theme.toggle()"
        [attr.aria-label]="'Toggle theme'"
      >
        <mat-icon>{{
          theme.mode() === "dark" ? "light_mode" : "dark_mode"
        }}</mat-icon>
      </button>
      @if (auth.isAuthenticated()) {
      <button mat-button [matMenuTriggerFor]="menu">
        <mat-icon>account_circle</mat-icon>
        <span class="user-name">{{ auth.user()?.name }}</span>
      </button>
      <mat-menu #menu="matMenu">
        <a mat-menu-item routerLink="/profile"
          ><mat-icon>person</mat-icon>Profile</a
        >
        <mat-divider />
        <button mat-menu-item (click)="auth.logout()">
          <mat-icon>logout</mat-icon>Log out
        </button>
      </mat-menu>
      } @else {
      <a mat-button routerLink="/auth/login">Login</a>
      <a mat-flat-button color="primary" routerLink="/auth/register">Sign up</a>
      }
    </mat-toolbar>
    <main class="app-main">
      <ng-content />
    </main>
    <footer class="app-footer muted">© {{ year }} Marketplace</footer>
  `,
  styles: [
    `
      :host {
        display: flex;
        flex-direction: column;
        min-height: 100vh;
      }
      .app-toolbar {
        position: sticky;
        top: 0;
        z-index: 10;
        gap: 8px;
        background: var(--app-surface);
        border-bottom: 1px solid var(--app-border);
        color: var(--app-fg);
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.02);
      }
      .brand {
        display: inline-flex;
        align-items: center;
        gap: 8px;
        text-decoration: none;
        color: var(--app-fg);
        font-weight: 800;
        font-size: 1.15rem;
        letter-spacing: -0.02em;
      }
      .brand mat-icon {
        color: var(--app-primary);
        font-size: 24px;
        width: 24px;
        height: 24px;
      }
      .nav {
        display: flex;
        gap: 6px;
        margin-left: 20px;
      }
      .nav a {
        font-weight: 500;
        font-size: 13.5px;
        opacity: 0.85;
        transition: all 0.2s ease;
        border-radius: var(--app-radius-sm);
      }
      .nav a:hover {
        opacity: 1;
        background: rgba(99, 102, 241, 0.04);
      }
      .nav .active {
        background: var(--app-primary-light);
        color: var(--app-primary);
        opacity: 1;
      }
      .grow {
        flex: 1;
      }
      .user-name {
        margin-left: 6px;
        font-weight: 500;
      }
      .app-main {
        flex: 1;
        background: var(--app-bg);
      }
      .app-footer {
        text-align: center;
        padding: 32px 24px;
        font-size: 12px;
        border-top: 1px solid var(--app-border);
        background: var(--app-surface);
        letter-spacing: 0.05em;
        font-weight: 500;
        text-transform: uppercase;
      }
      @media (max-width: 720px) {
        .nav {
          display: none;
        }
        .user-name {
          display: none;
        }
      }
    `,
  ],
})
export class MainLayoutComponent {
  readonly auth = inject(AuthService);
  readonly theme = inject(ThemeService);
  readonly year = new Date().getFullYear();

  constructor() {
    effect(() => {
      console.log("AUTH", {
        authenticated: this.auth.isAuthenticated(),
        user: this.auth.user(),
      });
    });
  }
}
