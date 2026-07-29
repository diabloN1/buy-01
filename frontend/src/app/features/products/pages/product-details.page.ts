import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
  signal,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { ActivatedRoute, RouterLink } from "@angular/router";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { toSignal } from "@angular/core/rxjs-interop";
import { shareReplay, switchMap } from "rxjs";
import { ProductService } from "@core/services/product.service";
import { Product } from "@core/models/product.model";
import { LoadingSpinnerComponent } from "@shared/components/loading-spinner.component";
import { SafeUrlPipe } from "@shared/pipes/safe-url.pipe";
import { UserService } from "@core/services/user.service";
import { UserWidget } from "@core/models/user.model";
import { CurrentUserService } from "@core/services/current-user.service";

@Component({
  selector: "app-product-details",
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CommonModule,
    RouterLink,
    MatButtonModule,
    MatIconModule,
    LoadingSpinnerComponent,
    SafeUrlPipe,
  ],
  template: `
    <section class="container">
      @if (product(); as p) {
      <a mat-button routerLink="/products"
        ><mat-icon>arrow_back</mat-icon> Back</a
      >
      <div class="grid">
        <div class="gallery app-card">
          @if (activeImage(); as img) {
          <img [src]="img | safeUrl" [alt]="p.name" />
          } @else {
          <div class="ph"><mat-icon>image</mat-icon></div>
          } @if (p.images.length) {
          <div class="thumbs">
            @for (image of p.images; track image.id) {
            <button
              type="button"
              class="thumb"
              (click)="active.set($index)"
              [class.on]="active() === $index"
            >
              <img [src]="image.url | safeUrl" alt="" />
            </button>
            }
          </div>
          }
        </div>
        <div class="info stack">
          <h1>{{ p.name }}</h1>
          <div class="price">{{ p.price | currency }}</div>
          @if (seller(); as seller) {
          <div class="seller">
            @if (seller.avatar) {
            <img
              class="seller-avatar"
              [src]="seller.avatar.url | safeUrl"
              [alt]="seller.name"
            />
            } @else {
            <div class="seller-avatar-placeholder">
              <mat-icon>person</mat-icon>
            </div>
            }

            <div class="">
              <span class="seller-label">Seller</span>
              <strong> - {{ seller.name }}</strong>
            </div>
          </div>
          }
          <p class="description">{{ p.description }}</p>
          <div class="stock-tag">
            <mat-icon
              style="font-size: 18px; width: 18px; height: 18px; margin-right: 4px;"
              >inventory_2</mat-icon
            >
            In stock: {{ p.quantity }}
          </div>
          @if (ownedByMe()) {
          <div class="actions">
            <a
              mat-stroked-button
              [routerLink]="['/seller/products', p.id, 'edit']"
              ><mat-icon>edit</mat-icon> Edit product</a
            >
          </div>
          }
        </div>
      </div>
      } @else {
      <app-loading-spinner label="Loading…" />
      }
    </section>
  `,
  styles: [
    `
      .grid {
        display: grid;
        grid-template-columns: 1.2fr 1fr;
        gap: 32px;
        margin-top: 24px;
        align-items: start;
      }
      @media (max-width: 800px) {
        .grid {
          grid-template-columns: 1fr;
          gap: 24px;
        }
      }
      .gallery {
        padding: 16px;
      }
      .gallery img {
        width: 100%;
        aspect-ratio: 4/3;
        object-fit: cover;
        border-radius: var(--app-radius);
      }
      .ph {
        aspect-ratio: 4/3;
        display: flex;
        align-items: center;
        justify-content: center;
        background: var(--app-bg);
        border-radius: var(--app-radius);
      }
      .ph mat-icon {
        font-size: 48px;
        width: 48px;
        height: 48px;
        color: var(--app-muted);
        opacity: 0.5;
      }
      .thumbs {
        display: flex;
        gap: 10px;
        margin-top: 16px;
        flex-wrap: wrap;
      }
      .thumb {
        border: 2px solid transparent;
        padding: 0;
        background: none;
        border-radius: var(--app-radius-sm);
        overflow: hidden;
        cursor: pointer;
        transition: all 0.2s ease;
        box-shadow: var(--app-shadow);
      }
      .thumb.on {
        border-color: var(--app-primary);
        transform: translateY(-2px);
        box-shadow: var(--app-glow);
      }
      .thumb img {
        width: 72px;
        height: 72px;
        object-fit: cover;
        display: block;
      }
      .info {
        padding: 8px 0;
      }
      .price {
        font-size: 28px;
        font-weight: 800;
        color: var(--app-fg);
        letter-spacing: -0.01em;
        margin: 12px 0 20px;
      }
      h1 {
        margin: 0;
        font-size: clamp(1.8rem, 4vw, 2.5rem);
        font-weight: 800;
        letter-spacing: -0.025em;
        line-height: 1.2;
      }
      .description {
        font-size: 15px;
        line-height: 1.6;
        color: var(--app-fg);
        opacity: 0.9;
        margin-bottom: 24px;
      }
      .stock-tag {
        font-size: 13px;
        font-weight: 600;
        color: var(--app-muted);
        margin: 16px 0;
        display: inline-flex;
        align-items: center;
      }
      .actions {
        margin-top: 24px;
      }
      .seller {
        display: flex;
        align-items: center;
        gap: 12px;
      }

      .seller-avatar,
      .seller-avatar-placeholder {
        width: 48px;
        height: 48px;
        border-radius: 50%;
      }

      .seller-avatar {
        object-fit: cover;
      }

      .seller-avatar-placeholder {
        display: flex;
        align-items: center;
        justify-content: center;
        background: var(--app-bg);
        color: var(--app-muted);
      }

      .seller-info {
        display: flex;
        flex-direction: column;
        gap: 2px;
      }

      .seller-label {
        font-size: 12px;
        color: var(--app-muted);
      }
    `,
  ],
})
export class ProductDetailsPage {
  private readonly route = inject(ActivatedRoute);
  private readonly svc = inject(ProductService);
  private readonly userService = inject(UserService);
  readonly currentUser = inject(CurrentUserService);

  private readonly product$ = this.route.paramMap.pipe(
    switchMap((params) => this.svc.get(params.get("id")!)),
    shareReplay({ bufferSize: 1, refCount: true })
  );

  readonly product = toSignal<Product | undefined>(this.product$, {
    initialValue: undefined,
  });

  readonly seller = toSignal<UserWidget | undefined>(
    this.product$.pipe(
      switchMap((product) => this.userService.getWidget(product.userId))
    ),
    { initialValue: undefined }
  );

  readonly active = signal(0);

  readonly activeImage = computed(
    () => this.product()?.images?.[this.active()]?.url ?? null
  );

  readonly ownedByMe = computed(() => {
    const p = this.product();
    const u = this.currentUser.user();

    return !!p && !!u && p.userId === u.id;
  });
}
