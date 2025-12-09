import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core';
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
  @ViewChild('addDrop') addDrop!: ElementRef;
  @ViewChild('userDrop') userDrop!: ElementRef;
  @HostListener('document:click', ['$event'])
  onClickOutside(event: MouseEvent) {
    const target = event.target as Node;

    // --- ADD DROPDOWN ---
    if (this.isAddOpen && this.addDrop && !this.addDrop.nativeElement.contains(target)) {
      this.isAddOpen = false;
    }

    // --- USER DROPDOWN ---
    if (this.isUserOpen && this.userDrop && !this.userDrop.nativeElement.contains(target)) {
      this.isUserOpen = false;
    }
  }



  isMenuOpen: boolean = false; // Biến trạng thái menu mobile

  // Hàm bật tắt menu
  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  // Hàm đóng menu (dùng khi click vào overlay hoặc link)
  closeMenu() {
    this.isMenuOpen = false;
  }
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
          this.featureAdd = [{ name: "Thêm hợp đồng", path: "/home/contracts/add" }
          ];
          break;
        case "employee":
          this.featureAdd = [
            { name: "Đăng Ký Phép", path: "/home/leaverequest/add" }
          ];
          break;
        case "manager":
          this.featureAdd = [{ name: "Lịch làm việc", path: "/home/schedule" }, { name: "Xếp ca làm việc", path: "/home/schedule/auto" }]

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


  checkrole() {
    const icon = [{
      iconName: "home", path: "/home/info", task: [{ name: "Trang chủ", path: "/home/info" }]
    },
    ];
    this.role = DecodeTokenRole(this.token);
    if (this.role.length > 0)
      this.cookieService.set("role", this.role[0], { path: "/" });
    switch (this.role[0]) {
      case "Admin":
        const icon_admin = [
          { iconName: "group", path: "/home/user/account", task: [{ name: "Quản Lí Nhân sự", path: "/home/user/account" }] },
          { iconName: "receipt", path: "/home/payroll/rules", task: [{ name: "Quản Lí lương", path: "/home/payroll/rules" }], },

        ];
        icon.push(...icon_admin)
        this.icon_handleBar = icon;
        break;
      case "HR":
        const icon_hr = [
          { iconName: "group", path: "/home/user/account", task: [{ name: "Quản Lí Nhân sự", path: "/home/user/account" }], },
          {
            iconName: "calendar_month", path: "/home/user/attendance", task: [{ name: "Quản Lí chấm công", path: "/home/user/attendance" },
            { name: "Quản lí phép", path: "/home/leaverequest/manage" }]
          },
          { iconName: "contract", path: "/home/contracts", task: [{ name: "Quản Lí hợp đồng", path: "/home/contracts" }], },
          { iconName: "receipt", path: "/home/payroll", task: [{ name: "Quản Lí lương", path: "/home/payroll" }], },

        ];
        icon.push(...icon_hr)
        this.icon_handleBar = icon;
        break;
      case "Manager":
        const icon_manager = [
          {
            iconName: "calendar_month", path: "/home/user/attendance", task: [{ name: "Quản Lí chấm công", path: "/home/user/attendance" },
            { name: "Lịch làm việc", path: "/home/schedule" }, { name: "Quản lí phép", path: "/home/leaverequest/manage" }]
          }, { iconName: "assignment_add", path: "/home/leaverequest", task: [{ name: "Nghỉ Phép", path: "/home/leaverequest" }] }
        ]
        icon.push(...icon_manager)
        this.icon_handleBar = icon;
        break;
      case "Employee":
        const icon_employee = [
          {
            iconName: "calendar_month", path: "/home/user/attendance", task: [{ name: "Quản Lí chấm công", path: "/home/user/attendance" },
            { name: "Lịch làm việc", path: "/home/schedule" },
            ]
          }, { iconName: "assignment_add", path: "/home/leaverequest", task: [{ name: "Nghỉ Phép", path: "/home/leaverequest" }] }
        ]
        icon.push(...icon_employee)
        this.icon_handleBar = icon;
        break;
    }
  }
  activeIndex: number | null = 0;

  toggleSubmenu(index: number) {
    if (this.activeIndex === index) {
      this.activeIndex = null;
    } else {
      this.activeIndex = index;
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

  async ngOnInit() {
    this.CheckLogin();
    this.checkrole();
  }
}
