import { CommonModule, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';
import { changePassword } from '../../../services/pages/user.service';
import { Loading } from '../../shared/loading/loading';
import { Alert } from '../../shared/alert/alert';
import { Comfirm } from '../../shared/comfirm/comfirm';


@Component({
  selector: 'app-change-password',
  imports: [CommonModule, FormsModule, NgIf, Loading, Alert, Comfirm],
  templateUrl: './change-password.html',
  styleUrl: './change-password.scss',
})
export class ChangePassword {
  constructor(private cookie: CookieService) { }
  role: string = "";
  oldPassword = '';
  newPassword = '';
  RenewPassword = '';
  ////////////////////////
  isconfirm: boolean = false;
  isalert: boolean = false;
  isloading: boolean = false;
  confirmMessage = '';
  alertmessage = '';
  alertType: boolean = true;
  actionType: 'approve' | 'reject' | '' = '';

  Onalert(message: string, type: boolean) {
    this.isalert = true;
    this.alertmessage = message;
    this.alertType = type;
  }
  /////////////////////////
  async onConfirmResult(event: any) {
    this.isconfirm = false;
    if (event == true) {
      this.role = this.cookie.get("role");
      if (!this.oldPassword || !this.newPassword) {
        this.Onalert('Vui lòng nhập đầy đủ mật khẩu!', false);
        return;
      }
      const res = await changePassword(this.oldPassword, this.newPassword, this.role) as { data: string, status: number };
      if (res.status == 200) {
        this.Onalert(res.data, true);
      }
      this.Onalert(res.data, false);
    }
  }
  async savePassword() {
    this.isconfirm = true;
    this.confirmMessage = "Bạn Có chắc muốn đổi mật khẩu không ?"
  }
}
