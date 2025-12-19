import { ChangeDetectorRef, Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RescheduleAPI, rescheduleinterface } from '../../../services/pages/features/employee/shedule.services';
import { Alert } from '../alert/alert';
import { Loading } from '../loading/loading';
import { Comfirm } from '../comfirm/comfirm';
import { scheduleList } from '../../../utils/listSchedule.utils';

@Component({
  selector: 'app-reschedule',
  standalone: true,
  imports: [CommonModule, FormsModule, Alert, Loading, Comfirm],
  templateUrl: './reschedule.html',
  styleUrl: './reschedule.scss',
})
export class Reschedule {
  @Input() date: string = ""; // Ngày cần đổi lịch
  @Output() closeEvent = new EventEmitter<void>(); // Sự kiện đóng popup

  // Các biến logic UI
  isconfirm: boolean = false;
  isalert: boolean = false;
  isloading: boolean = false;

  confirmMessage = '';
  alertmessage = '';
  alertType: boolean = true;

  // Dữ liệu form
  reason: string = '';
  newShiftId: any = null; // Bind với select
  selectedType: number = 8; // Mặc định ca 8H
  list: any[] = []; // Danh sách ca làm việc

  constructor(private cdr: ChangeDetectorRef) {
    // Khởi tạo mặc định list cho ca 8h khi mở lên (nếu cần)
    this.changeType(8);
  }

  // --- Logic Xử lý thông báo ---
  Onalert(message: string, type: boolean) {
    this.isalert = true;
    this.alertmessage = message;
    this.alertType = type;
  }

  // --- Logic Form & Select ---

  changeType(hours: number) {
    scheduleList(hours, this.list);
  }

  // --- Logic Submit ---
  async reschedule() {
    // Validate cơ bản
    if (!this.newShiftId) {
      this.Onalert("Vui lòng chọn ca làm việc mới!", false);
      return;
    }
    if (!this.reason.trim()) {
      this.Onalert("Vui lòng nhập lý do!", false);
      return;
    }

    this.isconfirm = true;
    this.confirmMessage = `Bạn có chắc muốn đăng ký đổi sang ca mới vào ngày ${this.date} không?`;
  }

  async onConfirmResult(event: any) {
    // Ẩn confirm dialog ngay lập tức
    this.isconfirm = false;

    if (event === true) {
      this.isloading = true;

      const form: rescheduleinterface = {
        targetDate: this.date,
        newShiftId: Number(this.newShiftId), // Đảm bảo là number
        reason: this.reason
      };

      try {
        const res = await RescheduleAPI(form) as { data: any, status: number } | any;
        // Giả sử res trả về object có field 'message' hoặc check status
        if (res.status == 200) {
          this.Onalert("Đăng ký đổi lịch thành công!", true);
          // Reset form sau khi thành công
          this.reason = '';
          this.newShiftId = null;
          return;
        }
        if (res.status == 400) {
          this.Onalert(res.response.data.message, false);
        }
      } finally {
        this.isloading = false;
        this.cdr.detectChanges();
      }
    }
  }

  closeModal() {
    this.closeEvent.emit();
  }
}