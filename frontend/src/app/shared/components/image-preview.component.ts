import {
  ChangeDetectionStrategy,
  Component,
  input,
  output,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";

@Component({
  selector: "app-image-preview",
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, MatButtonModule, MatIconModule],
  template: `
    @if (open()) {
      <div class="lightbox-backdrop" (click)="closed.emit()">
        <div class="lightbox-content" (click)="$event.stopPropagation()">
          <div class="lightbox-header">
            <span class="lightbox-title">{{ title() }}</span>

            <button mat-icon-button (click)="closed.emit()">
              <mat-icon>close</mat-icon>
            </button>
          </div>

          <div class="lightbox-body">
            <img
              [src]="imageUrl()"
              [alt]="title()"
              class="lightbox-img"
            />
          </div>

          <div class="lightbox-footer">
            <ng-content />
          </div>
        </div>
      </div>
    }
  `,
  styles: [
    `
      .lightbox-backdrop {
        position: fixed;
        inset: 0;
        background: rgba(0, 0, 0, 0.75);
        backdrop-filter: blur(4px);
        z-index: 1000;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 24px;
      }

      .lightbox-content {
        background: var(--app-surface, #fff);
        border-radius: 16px;
        max-width: 800px;
        width: 100%;
        max-height: 90vh;
        display: flex;
        flex-direction: column;
        overflow: hidden;
        box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
      }

      .lightbox-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px 20px;
        border-bottom: 1px solid var(--app-border, #e2e8f0);
      }

      .lightbox-title {
        font-weight: 600;
        font-size: 18px;
      }

      .lightbox-body {
        padding: 20px;
        display: flex;
        justify-content: center;
        align-items: center;
        background: #090d16;
        overflow: auto;
      }

      .lightbox-img {
        max-width: 100%;
        max-height: 60vh;
        object-fit: contain;
        border-radius: 8px;
      }

      .lightbox-footer {
        padding: 16px 20px;
        border-top: 1px solid var(--app-border, #e2e8f0);
        display: flex;
        justify-content: space-between;
        align-items: center;
      }
    `,
  ],
})
export class ImagePreviewComponent {
  readonly open = input.required<boolean>();
  readonly imageUrl = input.required<string>();
  readonly title = input("Image Preview");

  readonly closed = output<void>();
}