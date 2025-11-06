import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { DecodeTokenRole } from '../../utils/token.utils';
import { Loout_service } from '../../services/pages/login.service';

@Component({
  selector: 'app-home',
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements OnInit {
  constructor(private cookieService: CookieService, private router: Router, private cdr: ChangeDetectorRef) { }

  //cac thuoc tinh khoi tao de luu tru 
  token: string = "";
  role: string[] = [];
  icon_handleBar: any;
  isUserOpen = false;
  isAddOpen = false;
  featureAdd: any = [{ name: "", path: "" }]


  toggleUserDropdown() {
    this.isUserOpen = !this.isUserOpen;
    this.isAddOpen = false; // đóng dropdown khác nếu mở
  }

  toggleAddDropdown() {
    this.isAddOpen = !this.isAddOpen;
    this.isUserOpen = false;
    this.cdr.detectChanges();
    if (this.isAddOpen == true) {
      switch (this.role[0].toLowerCase()) {
        case "admin":
          this.featureAdd = [{ name: "Cấp tài khoản", path: "/home/add/account" }
          ];
          break;
        case "hr":
          break;
        case "employee":
          break;
        case "manager":
          break;
      }
    }
  }
  openAddAccount() {
    console.log("Cấp tài khoản");
  }

  CheckLogin() {
    this.token = this.cookieService.get('access_token');
    if (this.token == '') {
      this.router.navigate(['/login']);
    }

  }
  navItems = [
    { label: 'Trang Chủ', path: '/home/info' },


  ];

  checkrole() {
    const icon = [{
      iconName: "person", path: "/home/info", task: [{ name: "Xem tài khoản", path: "/home/info" }]
    },
    ];
    this.role = DecodeTokenRole(this.token);
    if (this.role.length > 0)
      this.cookieService.set("role", this.role[0], { path: "/" });
    switch (this.role[0]) {
      case "Admin":
        const icon_admin = [{ iconName: "group", path: "/home/user/account", task: [{ name: "Quản Lí Nhân sự", path: "/home/user/account" }] }];
        icon.push(...icon_admin)
        this.icon_handleBar = icon;
        break;
      case "HR":
        const icon_hr = [
          { iconName: "group", path: "/home/user/account", task: [{ name: "Quản Lí Nhân sự", path: "/home/user/account" }], },
          { iconName: "calendar_month", path: "/home/user/attendant", task: [{ name: "Quản Lí chấm công", path: "/home/user/attendant" }] },
        ];
        icon.push(...icon_hr)
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
  activeIndex: number | null = 0;

  toggleSubmenu(index: number) {
    if (this.activeIndex === index) {
      this.activeIndex = null; // nếu click lại cùng icon → đóng submenu
    } else {
      this.activeIndex = index; // mở submenu icon khác
    }
  }

  changepass() {
    this.router.navigate(["/home/changepassword"])
  }

  async logout() {
    const res = await Loout_service() as { status: number };
    if (res.status == 200) {

      this.cookieService.delete("access_token");
      this.cookieService.delete("refreshToken");
      this.cookieService.delete("role");
      this.cdr.detectChanges();
      this.router.navigate(['/login']);
    }

  }

  ngOnInit() {
    this.CheckLogin();
    this.checkrole();

  }
}
