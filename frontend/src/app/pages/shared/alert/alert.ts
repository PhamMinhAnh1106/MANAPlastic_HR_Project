import { CommonModule, NgClass, NgIf } from '@angular/common'; // Nhớ import CommonModule hoặc NgIf
import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';

// Định nghĩa kiểu cho clean code

@Component({
  selector: 'app-alert',
  standalone: true,
  imports: [NgClass, NgIf], // NgIf dùng để ẩn hiện icon
  templateUrl: './alert.html',
  styleUrl: './alert.scss',
})
export class Alert implements OnInit, OnDestroy {

  @Input() message: string = "";

  // Dùng string thay vì boolean để dễ mở rộng (ví dụ sau này thêm 'warning')
  @Input() notifyType = false;

  @Output() hidden = new EventEmitter<void>();

  private timeoutId: any;

  ngOnInit(): void {
    // Tự động đóng sau 5 giây (khớp với animation fadeOut trong CSS)
    this.timeoutId = setTimeout(() => {
      this.close();
    }, 5000);
  }

  // Hàm đóng thủ công (khi user bấm nút X)
  onClose(): void {
    this.close();
  }

  private close(): void {
    this.hidden.emit();
  }

  // Dọn dẹp timer nếu component bị hủy đột ngột
  ngOnDestroy(): void {
    if (this.timeoutId) {
      clearTimeout(this.timeoutId);
    }
  }
}