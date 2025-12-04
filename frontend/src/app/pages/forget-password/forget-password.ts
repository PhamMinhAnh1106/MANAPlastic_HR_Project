import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { Loading } from '../shared/loading/loading';
import { Alert } from '../shared/alert/alert';
import { checkOtpByEmail, getOtpByEmail, resetNewPassword } from '../../services/pages/forgetpassword.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-forget-password',
  imports: [FormsModule, NgIf, Loading, Alert],
  templateUrl: './forget-password.html',
  styleUrl: './forget-password.scss',
})
export class ForgetPassword implements OnInit {
  constructor(private cdr: ChangeDetectorRef, private router: Router) { }
  step = 1; // 1: nhập email, 2: nhập OTP, 3: nhập mật khẩu mới
  email = '';
  otp = '';
  newPassword = '';
  confirmPassword = '';
  resetToken = '';
  message = '';
  ////////////////////////

  isalert: boolean = false;
  isloading: boolean = false;

  alertmessage = '';
  alertType: boolean = true;

  Onalert(message: string, type: boolean) {
    this.isalert = true;
    this.alertmessage = message;
    this.alertType = type;
  }
  ///////////////////////// 

  backToLogin() {
    this.router.navigate(["/login"])
  }
  async sendEmail(email: string) {
    if (!email) {
      this.Onalert('Vui lòng nhập email', false);
      return;
    }
    this.isloading = true;
    const res = await getOtpByEmail(email) as { data: { message: string } | { error: string }, status: number };
    if (res.status == 200) {
      this.isloading = false;
      setTimeout(() => {
        this.cdr.detectChanges();
      }, 1000);
      if ("message" in res.data) {
        this.email = email;
        sessionStorage.setItem('email', email);

        this.Onalert(res.data.message, true);
        this.step = 2;
      }
      return;
    }
    this.isloading = false;
    if ("error" in res.data) {
      this.Onalert(res.data.error, false);
    }
  }

  async verifyOtp(otp: string) {
    this.otp = otp;
    if (!this.otp) {
      this.Onalert('Vui lòng nhập OTP', false);
      return;
    }
    this.isloading = true;
    const res = await checkOtpByEmail(this.email, this.otp) as { data: { resetToken: string }, status: number };
    if (res.status == 200) {
      this.isloading = false;
      setTimeout(() => {
        this.cdr.detectChanges();
      }, 1000);
      this.resetToken = res.data.resetToken;
      this.step = 3;
      return;
    }
    this.isloading = false;
    this.Onalert("OTP không chính xác!", false);


  }

  async resetPassword() {
    if (!this.newPassword || this.newPassword !== this.confirmPassword) {
      this.Onalert('Mật khẩu không khớp', false);
      return;
    }
    this.isloading = true;
    const res = await resetNewPassword(this.resetToken, this.newPassword) as { data: { message: string } | { error: string }, status: number };
    if (res.status == 200) {
      this.isloading = false;
      setTimeout(() => {
        this.cdr.detectChanges();
      }, 1000);
      if ("message" in res.data) {
        this.Onalert(res.data.message, true);
        setTimeout(() => {
          sessionStorage.removeItem('email');
          this.router.navigate(["/login"]);
        }, 2000)

      }

      return;
    }
    this.isloading = false;
    if ("error" in res.data) {
      this.Onalert(res.data.error, false);
    }

  }
  ngOnInit(): void {

    const emailsession = sessionStorage.getItem("email");
    if (emailsession) {
      this.email = emailsession;
      this.step = 2;
    }

  }

}
