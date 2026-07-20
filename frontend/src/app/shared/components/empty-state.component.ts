import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-empty-state',
  standalone: true,
  imports: [MatIconModule, MatButtonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="empty">
      <mat-icon>{{ icon }}</mat-icon>
      <h3>{{ title }}</h3>
      @if (description) { <p class="muted">{{ description }}</p> }
      @if (actionLabel) { <button mat-flat-button color="primary" (click)="action.emit()">{{ actionLabel }}</button> }
    </div>
  `,
  styles: [`
    .empty { display:flex; flex-direction:column; align-items:center; gap:8px; padding:48px 16px; text-align:center; }
    mat-icon { font-size: 48px; width: 48px; height: 48px; color: var(--app-muted); }
    h3 { margin: 8px 0 0; }
  `],
})
export class EmptyStateComponent {
  @Input() icon = 'inbox';
  @Input() title = 'Nothing here yet';
  @Input() description = '';
  @Input() actionLabel = '';
  @Output() action = new EventEmitter<void>();
}
