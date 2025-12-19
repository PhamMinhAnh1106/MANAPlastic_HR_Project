import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { addAccount } from '../../../../services/pages/features/admin/addAccount.service';
import { Department } from '../../../../interface/user/user.interface';
import { Loading } from '../../../shared/loading/loading';
import { Alert } from '../../../shared/alert/alert';
import { Comfirm } from '../../../shared/comfirm/comfirm';

@Component({
  selector: 'app-add-account',
  standalone: true,
  imports: [CommonModule, FormsModule, Loading, Alert, Comfirm],
  templateUrl: './add-account.html',
  styleUrl: './add-account.scss',
})
export class AddAccount {
  // State
  isloading: boolean = false;
  isconfirm: boolean = false;
  confirmMessage = "";
  isalert: boolean = false;
  notifyMessage = "";
  notifyType: boolean = true;

  // Data
  department = Department;
  account = {
    fullname: '',
    cccd: '',
    gender: '',
    role: 0,
    department: 0
  };

  constructor(private router: Router, private cdr: ChangeDetectorRef) { }

  showNotification(message: string, type: boolean) {
    this.notifyMessage = message;
    this.notifyType = type;
    this.isalert = true;
  }

  saveAccount() {
    // Validation cơ bản trước khi mở confirm
    if (!this.account.fullname.trim()) {
      this.showNotification("Vui lòng nhập Họ và Tên", false);
      return;
    }
    if (!this.account.cccd.trim() || this.account.cccd.length < 9) { // CCCD thường 9 hoặc 12 số
      this.showNotification("Số CCCD không hợp lệ", false);
      return;
    }
    if (Number(this.account.role) === 0) {
      this.showNotification("Vui lòng chọn Vai trò", false);
      return;
    }
    if (Number(this.account.department) === 0) {
      this.showNotification("Vui lòng chọn Phòng ban", false);
      return;
    }

    this.isconfirm = true;
    this.confirmMessage = "Bạn có chắc muốn cấp tài khoản cho nhân viên này?";
  }

  async onConfirmResult(event: any) {
    this.isconfirm = false;

    if (event === true) {
      this.isloading = true;
      try {
        const res = await addAccount(this.account) as { data: string, status: number };

        if (res.status === 201) {
          this.showNotification(res.data, true);
          // Reset form
          this.account = {
            fullname: '',
            cccd: '',
            gender: '',
            role: 0,
            department: 0
          };
        } else {
          this.showNotification("Thêm thất bại: " + res.data, false);
        }
      } catch (error) {
        this.showNotification("Lỗi kết nối server", false);
      } finally {
        this.isloading = false;
        this.cdr.detectChanges();
      }
    }
  }

  closeForm() {
    this.router.navigate(["/home/user/account"]);
  }
}