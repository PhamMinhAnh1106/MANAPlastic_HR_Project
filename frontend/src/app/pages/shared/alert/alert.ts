import { NgClass } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-alert',
  imports: [NgClass],
  templateUrl: './alert.html',
  styleUrl: './alert.scss',
})
export class Alert {

  @Input() message: string = "";
  @Input() notifyType: boolean = true;  // true = success, false = error

  @Output() hidden = new EventEmitter<void>();

  ngOnInit(): void {
    setTimeout(() => {
      this.hidden.emit();   // gửi event để tắt notify trong trang cha
    }, 5000);
  }
}
