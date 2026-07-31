import {
  ChangeDetectionStrategy,
  Component,
  inject,
  signal,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormControl, ReactiveFormsModule } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatPaginatorModule, PageEvent } from "@angular/material/paginator";
import { MatIconModule } from "@angular/material/icon";
import { debounceTime, distinctUntilChanged } from "rxjs";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { ProductService } from "@core/services/product.service";
import { Product } from "@core/models/product.model";
import { ProductCardComponent } from "@shared/components/product-card.component";
import { LoadingSpinnerComponent } from "@shared/components/loading-spinner.component";
import { EmptyStateComponent } from "@shared/components/empty-state.component";

@Component({
  selector: "app-product-list",
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatPaginatorModule,
    MatIconModule,
    ProductCardComponent,
    LoadingSpinnerComponent,
    EmptyStateComponent,
  ],
  template: `
    <section class="container">
      <div class="row toolbar">
        <h1 class="grow">Products</h1>
        <mat-form-field appearance="outline" class="search">
          <mat-icon matPrefix>search</mat-icon>
          <mat-label>Search</mat-label>
          <input matInput [formControl]="q" placeholder="Search products…" />
        </mat-form-field>
      </div>
      @if (loading()) {
        <app-loading-spinner label="Loading…" />
      } @else if (!items().length) {
        <app-empty-state
          icon="search_off"
          title="No products found"
          description="Try a different search."
        />
      } @else {
        <div class="grid">
          @for (p of items(); track p.id) {
            <app-product-card [product]="p" />
          }
        </div>
        <mat-paginator
          [length]="total()"
          [pageSize]="pageSize()"
          [pageIndex]="page() - 1"
          [pageSizeOptions]="[12, 24, 48]"
          (page)="onPage($event)"
        />
      }
    </section>
  `,
  styles: [
    `
      .toolbar {
        margin: 24px 0 32px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        flex-wrap: wrap;
        gap: 16px;
      }
      .search {
        width: 360px;
        max-width: 100%;
      }
      .grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
        gap: 24px;
        margin-bottom: 32px;
      }
      mat-paginator {
        background: transparent;
      }
    `,
  ],
})
export class ProductListPage {
  private readonly svc = inject(ProductService);
  readonly q = new FormControl("", { nonNullable: true });
  readonly items = signal<Product[]>([]);
  readonly total = signal(0);
  readonly page = signal(1);
  readonly pageSize = signal(12);
  readonly loading = signal(true);

  constructor() {
    this.load();
    this.q.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged(), takeUntilDestroyed())
      .subscribe(() => {
        this.page.set(1);
        this.load();
      });
  }
  onPage(e: PageEvent) {
    this.page.set(e.pageIndex + 1);
    this.pageSize.set(e.pageSize);
    this.load();
  }
  private load() {
    this.loading.set(true);
    this.svc
      .list(this.page(), this.pageSize(), this.q.value || undefined)
      .subscribe({
        next: (r) => {
          this.items.set(r.content);
          this.total.set(r.totalElements);
          this.loading.set(false);
        },
        error: () => this.loading.set(false),
      });
  }
}
