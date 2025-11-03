import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';
import { changePassword } from '../../services/pages/user.service';

@Component({
  selector: 'app-change-password',
  imports: [CommonModule, FormsModule],
  templateUrl: './change-password.html',
  styleUrl: './change-password.scss',
})
export class ChangePassword {
  constructor(private cookie: CookieService) { }
  role: string = "";
  showForm = false;
  oldPassword = '';
  newPassword = '';

  async savePassword() {
    this.role = this.cookie.get("role");
    if (!this.oldPassword || !this.newPassword) {
      alert('Vui lòng nhập đầy đủ mật khẩu!');
      return;
    }

    const res = await changePassword(this.oldPassword, this.newPassword, this.role) as { data: string, status: number };
    if (res.status == 200) {
      alert(res.data);
      this.showForm = false;
    }
    alert(res.data);


  }
}
