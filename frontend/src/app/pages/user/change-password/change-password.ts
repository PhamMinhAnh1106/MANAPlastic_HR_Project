import { CommonModule, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';

// Import service thực tế của bạn
import { changePassword } from '../../../services/pages/user.service';

// Import các shared components
import { Loading } from '../../shared/loading/loading';
import { Alert } from '../../shared/alert/alert';
import { Router } from '@angular/router';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIf, Loading, Alert],
  templateUrl: './change-password.html',
  styleUrls: ['./change-password.scss'],
})
export class ChangePassword {
  constructor(private router: Router, private cookie: CookieService) { }
  // --- Form Data ---
  oldPassword = '';
  newPassword = '';
  renewPassword = '';
  role: string = '';

  // --- UI State ---
  isOpen: boolean = true;      // Trạng thái mở Modal
  isLoading: boolean = false;   // Trạng thái loading khi gọi API

  // --- Alert State (Cho app-alert) ---
  isAlert: boolean = false;
  alertMessage: string = '';
  alertType: boolean = true;    // true: Success, false: Error (hoặc Warning)


  /**
   * Mở Modal (Component cha sẽ gọi hàm này thông qua ViewChild)
   */
  openModal() {
    this.isOpen = true;
    this.resetForm();
  }

  /**
   * Đóng Modal
   */
  closeModal(event?: Event) {
    if (event) event.stopPropagation();
    this.router.navigate(["/home/info"])
  }

  /**
   * Ngăn chặn click event nổi bọt (để click vào modal không bị đóng)
   */
  preventClose(event: Event) {
    event.stopPropagation();
  }

  /**
   * Reset form về trạng thái ban đầu
   */
  resetForm() {
    this.oldPassword = '';
    this.newPassword = '';
    this.renewPassword = '';
    this.isLoading = false;
    this.isAlert = false;
  }

  /**
   * Hiển thị thông báo qua App-Alert
   */
  showAlert(message: string, isSuccess: boolean) {
    this.alertMessage = message;
    this.alertType = isSuccess;
    this.isAlert = true;

    // Tự động ẩn alert sau 3s (nếu component Alert chưa tự xử lý)
    // setTimeout(() => { this.isAlert = false; }, 3000);
  }

  /**
   * Xử lý lưu mật khẩu
   */
  async savePassword() {
    // 1. Validate cơ bản
    if (!this.oldPassword || !this.newPassword || !this.renewPassword) {
      this.showAlert('Vui lòng nhập đầy đủ thông tin!', false);
      return;
    }

    if (this.newPassword !== this.renewPassword) {
      this.showAlert('Mật khẩu xác nhận không khớp!', false);
      return;
    }

    // 2. Gọi API
    this.isLoading = true;
    this.isAlert = false; // Ẩn alert cũ nếu có

    try {
      this.role = this.cookie.get("role");

      // Gọi hàm service (đã import)
      const res = await changePassword(this.oldPassword, this.newPassword, this.role) as { data: string, status: number };

      if (res.status === 200) {
        this.showAlert(res.data || 'Đổi mật khẩu thành công!', true);

        // Đóng modal sau 1.5s để người dùng kịp đọc thông báo thành công
        setTimeout(() => {
          this.closeModal();
        }, 1500);
      } else {
        this.showAlert(res.data || 'Đổi mật khẩu thất bại.', false);
      }
    } catch (error) {
      console.error(error);
      this.showAlert('Đã có lỗi xảy ra từ hệ thống.', false);
    } finally {
      this.isLoading = false;
    }
  }
}