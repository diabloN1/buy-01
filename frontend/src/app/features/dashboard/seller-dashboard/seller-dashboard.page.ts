import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
  signal,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterLink } from "@angular/router";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { AuthService } from "@core/services/auth.service";
import { ProductService } from "@core/services/product.service";
import { Product } from "@core/models/product.model";
import { ProductCardComponent } from "@shared/components/product-card.component";
import { LoadingSpinnerComponent } from "@shared/components/loading-spinner.component";

@Component({
  selector: "app-seller-dashboard",
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CommonModule,
    RouterLink,
    MatIconModule,
    MatButtonModule,
    ProductCardComponent,
    LoadingSpinnerComponent,
  ],
  template: `
    <section class="container">
      <h1>Welcome, {{ auth.user()?.name }}</h1>
      <p class="muted">Here's a snapshot of your store.</p>

      <div class="stats">
        <div class="app-card stat">
          <div class="k">Total products</div>
          <div class="v">{{ mine().length }}</div>
        </div>
        <div class="app-card stat">
          <div class="k">Total images</div>
          <div class="v">{{ totalImages() }}</div>
        </div>
        <div class="app-card stat">
          <div class="k">Latest upload</div>
          <div class="v small">{{ mine().length ? latest().name : "—" }}</div>
        </div>
      </div>

      <div class="actions">
        <a mat-flat-button color="primary" routerLink="/seller/products/new"
          ><mat-icon>add</mat-icon> New product</a
        >

        <a mat-stroked-button routerLink="/seller/products"
          ><mat-icon>inventory_2</mat-icon> My products</a
        >
      </div>

      <h2>Latest products</h2>
      @if (loading()) { <app-loading-spinner /> } @else {
      <div class="grid">
        @for (p of mine().slice(0, 4); track p.id) {
        <app-product-card [product]="p" /> }
      </div>
      }
    </section>
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
        position: relative;
        border: 1px solid var(--app-border);
        overflow: hidden;
      }
      .stat::before {
        content: "";
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 3px;
        background: var(--app-gradient);
      }
      .k {
        color: var(--app-muted);
        font-size: 13.5px;
        font-weight: 500;
        text-transform: uppercase;
        letter-spacing: 0.05em;
      }
      .v {
        font-size: 32px;
        font-weight: 800;
        margin-top: 8px;
        color: var(--app-fg);
        letter-spacing: -0.02em;
      }
      .v.small {
        font-size: 16px;
        font-weight: 600;
        text-overflow: ellipsis;
        overflow: hidden;
        white-space: nowrap;
      }
      .actions {
        display: flex;
        gap: 14px;
        flex-wrap: wrap;
        margin: 24px 0 40px;
      }
      .grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
        gap: 24px;
      }
      h1 {
        margin-bottom: 4px;
        font-weight: 800;
        letter-spacing: -0.025em;
      }
    `,
  ],
})
export class SellerDashboardPage {
  readonly auth = inject(AuthService);
  private readonly svc = inject(ProductService);
  readonly items = signal<Product[]>([]);
  readonly loading = signal(true);
  readonly mine = computed(() => {
    const uid = this.auth.user()?.id;
    return this.items().filter((p) => p.userId === uid);
  });
  readonly totalImages = computed(() =>
    this.mine().reduce((n, p) => n + p.images.length, 0)
  );
  readonly latest = computed(() => this.mine()[0]);

  constructor() {
    this.svc.list(1, 50).subscribe({
      next: (r) => {
        this.items.set(r.content);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }
}
