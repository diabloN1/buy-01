import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  imports: [MatProgressSpinnerModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="wrap">
      <mat-spinner [diameter]="diameter" />
      @if (label) { <span class="muted">{{ label }}</span> }
    </div>
  `,
  styles: [`.wrap { display:flex; align-items:center; justify-content:center; gap:12px; padding:24px; }`],
})
export class LoadingSpinnerComponent {
  @Input() diameter = 32;
  @Input() label = '';
}
