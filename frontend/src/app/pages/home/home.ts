import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
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
  constructor(private cookieService: CookieService, private router: Router, private cdr: ChangeDetectorRef) { }
  token: string = "";
  role: string[] = [];
  icon_handleBar: any;



  CheckLogin() {
    this.token = this.cookieService.get('access_token');
    if (this.token == '') {
      this.router.navigate(['/login']);
    }

  }
  navItems = [
    { label: 'Trang Chủ', path: '/home' },


  ];

  checkrole() {
    const icon = [{ iconName: "person", path: "/home/info", task: [{ name: "xem tai khoan", path: "/home/info" }, { name: "doi mat khau", path: "/home/changepassword" }] }];
    this.role = DecodeTokenRole(this.token);
    if (this.role.length > 0)
      this.cookieService.set("role", this.role[0], { path: "/" });
    switch (this.role[0]) {
      case "Admin":
        const icon_admin = [{ iconName: "group", path: "/", task: [] }];
        icon.push(...icon_admin)
        this.icon_handleBar = icon;
        break;
      case "HR":
        this.icon_handleBar = icon;
        break;
      case "Manager":
        this.icon_handleBar = icon;
        break;
      case "Employee":
        this.icon_handleBar = icon;
        break;
    }
  }


  async logout() {
    this.cookieService.delete("access_token");
    this.cookieService.delete("refreshToken");
    this.cookieService.delete("role");
    this.cdr.detectChanges();
    await this.router.navigate(['/login']);

  }

  async ngOnInit(): Promise<void> {
    await this.CheckLogin();
    await this.checkrole();

  }
}
