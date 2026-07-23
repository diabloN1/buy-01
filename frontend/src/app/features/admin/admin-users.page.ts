import {
  ChangeDetectionStrategy,
  Component,
  inject,
  signal,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { MatTableModule } from "@angular/material/table";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatDialog, MatDialogModule } from "@angular/material/dialog";
import { MatPaginatorModule, PageEvent } from "@angular/material/paginator";

import { LoadingSpinnerComponent } from "@shared/components/loading-spinner.component";
import { EmptyStateComponent } from "@shared/components/empty-state.component";
import { ConfirmDialogComponent } from "@shared/components/confirm-dialog.component";
import { NotificationService } from "@core/services/notification.service";

import { UserService } from "@core/services/user.service";
import { User } from "@core/models/user.model";

@Component({
  selector: "app-admin-users",
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatPaginatorModule,
    LoadingSpinnerComponent,
    EmptyStateComponent,
  ],
  template: `
    <section class="container">
      <div class="row">
        <h1 class="grow">All users</h1>
      </div>

      @if (loading()) {
      <app-loading-spinner />
      } @else if (!items().length) {
      <app-empty-state
        icon="group"
        title="No users found"
        description="There are currently no registered users."
      />
      } @else {
      <div class="app-card table-wrap">
        <table mat-table [dataSource]="items()">
          <ng-container matColumnDef="name">
            <th mat-header-cell *matHeaderCellDef>Name</th>
            <td mat-cell *matCellDef="let u">
              {{ u.name }}
            </td>
          </ng-container>

          <ng-container matColumnDef="email">
            <th mat-header-cell *matHeaderCellDef>Email</th>
            <td mat-cell *matCellDef="let u">
              {{ u.email }}
            </td>
          </ng-container>

          <ng-container matColumnDef="role">
            <th mat-header-cell *matHeaderCellDef>Role</th>
            <td mat-cell *matCellDef="let u">
              {{ u.role }}
            </td>
          </ng-container>

          <ng-container matColumnDef="actions">
            <th mat-header-cell *matHeaderCellDef></th>

            <td mat-cell *matCellDef="let u">
              <button
                mat-icon-button
                color="warn"
                (click)="remove(u)"
                aria-label="Delete user"
              >
                <mat-icon>delete</mat-icon>
              </button>
            </td>
          </ng-container>

          <tr mat-header-row *matHeaderRowDef="cols"></tr>
          <tr mat-row *matRowDef="let row; columns: cols"></tr>
        </table>

        <mat-paginator
          [length]="total()"
          [pageSize]="pageSize()"
          [pageIndex]="page() - 1"
          [pageSizeOptions]="[10, 25, 50]"
          (page)="onPage($event)"
        >
        </mat-paginator>
      </div>
      }
    </section>
  `,
  styles: [
    `
      .table-wrap {
        overflow: hidden;
        border: 1px solid var(--app-border);
        border-radius: var(--app-radius);
        box-shadow: var(--app-shadow);
        margin-top: 24px;
      }

      table {
        width: 100%;
      }

      th.mat-mdc-header-cell {
        font-weight: 600;
      }

      td.mat-mdc-cell {
        vertical-align: middle;
      }
    `,
  ],
})
export class AdminUsersPage {
  private readonly svc = inject(UserService);

  private readonly dialog = inject(MatDialog);
  private readonly notify = inject(NotificationService);

  readonly cols = ["name", "email", "role", "actions"];

  readonly items = signal<User[]>([]);
  readonly total = signal(0);
  readonly page = signal(1);
  readonly pageSize = signal(10);
  readonly loading = signal(true);

  constructor() {
    this.load();
  }

  onPage(e: PageEvent) {
    this.page.set(e.pageIndex + 1);
    this.pageSize.set(e.pageSize);

    this.load();
  }

  private load() {
    this.loading.set(true);

    this.svc.list(this.page(), this.pageSize()).subscribe({
      next: (r) => {
        this.items.set(r.content);
        this.total.set(r.totalElements);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }
  remove(user: any) {
    this.dialog
      .open(ConfirmDialogComponent, {
        data: {
          title: "Delete user",
          message: `Delete "${user.name}"?`,
          danger: true,
          confirmLabel: "Delete",
        },
      })
      .afterClosed()
      .subscribe((ok) => {
        if (!ok) return;

        this.svc.delete(user.id).subscribe(() => {
          this.notify.success("User deleted");
          this.load();
        });
      });
  }
}
