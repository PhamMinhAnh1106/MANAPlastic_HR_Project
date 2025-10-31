import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { isEmpty } from 'rxjs';
import { DecodeTokenRole } from '../../utils/token.utils';
import { getdataRole } from '../../services/pages/getPageRole.service';
import { information } from '../../interface/user/user.interface';

@Component({
  selector: 'app-home',
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements OnInit {
  constructor(private cookieService: CookieService, private router: Router) { }
  token: string = "";
  role: string[] = [];
  CheckLogin() {
    this.token = this.cookieService.get('access_token');
    if (this.token == '') {
      this.router.navigate(['/login']);
    }

  }
  navItems = [
    { label: 'Trang Chủ', path: '/home' },
    { label: 'Thông tin cá nhân', path: '/home/info' },

  ];

  checkrole() {
    this.role = DecodeTokenRole(this.token);
    if (this.role.length > 0)
      this.cookieService.set("role", this.role[0], { path: "/" });
  }


  logout() {
    this.cookieService.delete("access_token");
    this.cookieService.delete("refreshToken");
    this.cookieService.delete("role");
  }

  async ngOnInit(): Promise<void> {
    await this.CheckLogin();
    await this.checkrole();

  }
}
