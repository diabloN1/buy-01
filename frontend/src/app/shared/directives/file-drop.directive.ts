import {
  Directive,
  EventEmitter,
  HostBinding,
  HostListener,
  Output,
} from "@angular/core";

@Directive({ selector: "[appFileDrop]", standalone: true })
export class FileDropDirective {
  @Output() filesDropped = new EventEmitter<FileList>();
  @HostBinding("class.is-dragover") isOver = false;

  @HostListener("dragover", ["$event"]) onOver(e: DragEvent) {
    e.preventDefault();
    this.isOver = true;
  }
  @HostListener("dragleave", ["$event"]) onLeave(e: DragEvent) {
    e.preventDefault();
    this.isOver = false;
  }
  @HostListener("drop", ["$event"]) onDrop(e: DragEvent) {
    e.preventDefault();
    this.isOver = false;
    if (e.dataTransfer?.files?.length)
      this.filesDropped.emit(e.dataTransfer.files);
  }
}
