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
import { ProductService } from "@core/services/product.service";
import { Product } from "@core/models/product.model";
import { ProductCardComponent } from "@shared/components/product-card.component";
import { LoadingSpinnerComponent } from "@shared/components/loading-spinner.component";
import { EmptyStateComponent } from "@shared/components/empty-state.component";
import { AuthService } from "@core/services/auth.service";

@Component({
  selector: "app-home",
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CommonModule,
    RouterLink,
    MatButtonModule,
    MatIconModule,
    ProductCardComponent,
    LoadingSpinnerComponent,
    EmptyStateComponent,
  ],
  template: `
    <section class="hero">
      <div class="container hero-inner">
        <div>
          <h1>Discover products from independent sellers</h1>
          <p class="hero-subtitle">
            A modern marketplace for buyers and sellers.
          </p>
          <div class="cta">
            <a mat-flat-button color="primary" routerLink="/products"
              >Browse products</a
            >
            @if (authSvc.isAuthenticated()) {
              <a mat-stroked-button routerLink="/profile">
                View your profile
              </a>
            } @else {
              <a mat-stroked-button routerLink="/auth/register">
                Become a seller
              </a>
            }
          </div>
        </div>
      </div>
    </section>
    <section class="container">
      <div class="row">
        <h2 class="grow">Latest products</h2>
        <a mat-button routerLink="/products"
          >See all <mat-icon>arrow_forward</mat-icon></a
        >
      </div>
      @if (loading()) {
        <app-loading-spinner label="Loading products…" />
      } @else if (!items().length) {
        <app-empty-state
          icon="storefront"
          title="No products yet"
          description="Check back soon."
        />
      } @else {
        <div class="grid">
          @for (p of items(); track p.id) {
            <app-product-card [product]="p" />
          }
        </div>
      }
    </section>
  `,
  styles: [
    `
      .hero {
        background:
          radial-gradient(
            circle at 10% 20%,
            rgba(99, 102, 241, 0.08) 0%,
            transparent 60%
          ),
          radial-gradient(
            circle at 90% 80%,
            rgba(139, 92, 246, 0.06) 0%,
            transparent 70%
          );
        border-bottom: 1px solid var(--app-border);
        overflow: hidden;
        position: relative;
      }
      .hero::before {
        content: "";
        position: absolute;
        top: -100px;
        right: -100px;
        width: 300px;
        height: 300px;
        background: rgba(99, 102, 241, 0.15);
        filter: blur(80px);
        border-radius: 50%;
        pointer-events: none;
      }
      .hero-inner {
        padding: 96px 24px;
        text-align: center;
        max-width: 800px;
        margin: 0 auto;
      }
      h1 {
        font-size: clamp(2.2rem, 5vw, 3.5rem);
        margin: 0 0 16px;
        letter-spacing: -0.03em;
        line-height: 1.15;
        font-weight: 800;
        color: var(--app-fg);
      }
      .hero-subtitle {
        font-size: clamp(1.1rem, 2vw, 1.3rem);
        line-height: 1.6;
        max-width: 600px;
        margin: 0 auto;
        color: var(--app-muted);
      }
      .cta {
        display: flex;
        gap: 16px;
        justify-content: center;
        margin-top: 32px;
        flex-wrap: wrap;
      }
      h2 {
        margin: 40px 0 24px;
        font-weight: 800;
        font-size: 1.75rem;
        letter-spacing: -0.02em;
      }
      .grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
        gap: 24px;
      }
    `,
  ],
})
export class HomePage {
  private readonly svc = inject(ProductService);
  readonly authSvc = inject(AuthService);
  readonly items = signal<Product[]>([]);
  readonly loading = signal(true);
  constructor() {
    this.svc.list(1, 8).subscribe({
      next: (r) => {
        this.items.set(r.content);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }
}
