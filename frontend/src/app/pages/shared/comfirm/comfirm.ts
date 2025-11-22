import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-comfirm',
  imports: [],
  templateUrl: './comfirm.html',
  styleUrl: './comfirm.scss',
})
export class Comfirm {
  @Input() message: string = "Bạn có chắc muốn thực hiện?";
  @Output() result = new EventEmitter<boolean>();

  onConfirm() {
    this.result.emit(true);
  }

  onCancel() {
    this.result.emit(false);
  }
}
