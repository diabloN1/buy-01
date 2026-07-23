import {
  ChangeDetectionStrategy,
  Component,
  inject,
  signal,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterLink } from "@angular/router";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { forkJoin } from "rxjs";

import { UserService } from "@core/services/user.service";
import { MediaService } from "@core/services/media.service";
import { AuthService } from "@core/services/auth.service";
import { ProductService } from "@core/services/product.service";

import { LoadingSpinnerComponent } from "@shared/components/loading-spinner.component";

@Component({
  selector: "app-admin-dashboard",
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CommonModule,
    RouterLink,
    MatButtonModule,
    MatIconModule,
    LoadingSpinnerComponent,
  ],
  template: `
    @if (loading()) {
    <app-loading-spinner />
    } @else {

    <section class="container">
      <h1>Welcome, {{ auth.user()?.name }}</h1>
      <p class="muted">Marketplace overview.</p>

      <div class="stats">
        <div class="app-card stat">
          <div class="k">Total products</div>
          <div class="v">{{ totalProducts() }}</div>
        </div>

        <div class="app-card stat">
          <div class="k">Total images</div>
          <div class="v">{{ totalImages() }}</div>
        </div>

        <div class="app-card stat">
          <div class="k">Total users</div>
          <div class="v">{{ totalUsers() }}</div>
        </div>
      </div>

      <div class="actions">
        <a mat-flat-button color="primary" routerLink="/admin/products">
          <mat-icon>inventory_2</mat-icon>
          Manage Products
        </a>

        <a mat-stroked-button routerLink="/admin/users">
          <mat-icon>group</mat-icon>
          Manage Users
        </a>
      </div>
    </section>
    }
  `,
  styles: [
    `
      .stats {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
        gap: 20px;
        margin: 24px 0;
      }

      .stat {
        padding: 24px;
      }

      .k {
        color: var(--app-muted);
        font-size: 13px;
        text-transform: uppercase;
      }

      .v {
        font-size: 32px;
        font-weight: 700;
        margin-top: 8px;
      }

      .actions {
        display: flex;
        gap: 16px;
        flex-wrap: wrap;
      }
    `,
  ],
})
export class AdminDashboardPage {
  readonly auth = inject(AuthService);
  private readonly productService = inject(ProductService);
  private readonly userService = inject(UserService);
  private readonly mediaService = inject(MediaService);

  readonly loading = signal(true);

  readonly totalProducts = signal(0);
  readonly totalUsers = signal(0);
  readonly totalImages = signal(0);

  constructor() {
    forkJoin({
      products: this.productService.count(),
      users: this.userService.count(),
      images: this.mediaService.count(),
    }).subscribe({
      next: (counts) => {
        this.totalProducts.set(counts.products);
        this.totalUsers.set(counts.users);
        this.totalImages.set(counts.images);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }
}
