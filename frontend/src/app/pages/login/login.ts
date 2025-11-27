import { NgIf } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Login_service } from '../../services/pages/login.service';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { Loading } from '../shared/loading/loading';

interface response_api {
  status: number,
  data: string | null
}

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [NgIf, FormsModule, Loading],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  constructor(private cdr: ChangeDetectorRef, private router: Router, private cookieService: CookieService) { }
  username: string = "";
  password: string = "";
  message: string = "";
  isSuccess: boolean = true;
  isloading: boolean = false;
  async login() {
    try {
      this.isloading = true;
      const res = await Login_service(this.username, this.password);
      if (typeof res === 'string') {
        this.message = res;
        this.isSuccess = false;
        return;
      }
      if (res.status == 200) {
        const { token, refreshToken } = res.data;
        this.isloading = false;
        this.isSuccess = true;
        this.message = "Đăng nhập thành công";
        this.cookieService.set("access_token", token, { path: "/" });
        this.cookieService.set("refreshToken", refreshToken, { path: "/" });
        this.router.navigate(['/home/info/user']);
      }
    } catch (error) {
      this.isloading = false;

      this.message = "Tài khoản mật khẩu sai";
      this.isSuccess = false;
    } finally {
      this.isloading = false;

      this.cdr.detectChanges();
    }


    setTimeout(() => {
      this.message = "";
      this.cdr.detectChanges();

    }, 3000);



  }
  forgotPassword() {
    // Ví dụ: redirect tới trang quên mật khẩu
    this.router.navigate(['/forgotpassword']);
  }

}
