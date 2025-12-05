import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-comfirm',
  imports: [],
  templateUrl: './comfirm.html',
  styleUrl: './comfirm.scss',
})
export class Comfirm {
  @Input() title: string = "Xác nhận"; // Thêm tiêu đề
  @Input() message: string = "Bạn có chắc muốn thực hiện hành động này?";

  @Output() result = new EventEmitter<boolean>();

  onConfirm() {
    this.result.emit(true);
  }

  onCancel() {
    this.result.emit(false);
  }
}