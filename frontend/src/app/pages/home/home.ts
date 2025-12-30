import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChangeDetectorRef, Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';

import { DecodeTokenRole } from '../../utils/token.utils';
import { Loout_service } from '../../services/pages/login.service';
import { getNotificationContract } from '../../services/pages/features/hr/contracts.service';

interface ContractNotification {
  id: number;
  contractCode: string;
  employeeName: string;
  endDate: string;
  daysRemaining: number;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet, FormsModule, DatePipe],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements OnInit {
  constructor(private cookieService: CookieService, private router: Router, private cdr: ChangeDetectorRef) { }

  @ViewChild('addDrop') addDrop!: ElementRef;
  @ViewChild('userDrop') userDrop!: ElementRef;
  @ViewChild('notifiDrop') notifiDrop!: ElementRef; // Thêm viewchild cho thông báo

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

    // --- NOTIFICATION DROPDOWN ---
    if (this.isNotifiOpen && this.notifiDrop && !this.notifiDrop.nativeElement.contains(target)) {
      this.isNotifiOpen = false;
    }
  }

  isMenuOpen: boolean = false;

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  closeMenu() {
    this.isMenuOpen = false;
  }

  token: string = "";
  role: string[] = [];
  icon_handleBar: any;
  isUserOpen = false;
  isAddOpen = false;
  isSettingsOpen = false;
  isDarkMode = false;

  // --- BIẾN CHO THÔNG BÁO ---
  isNotifiOpen = false;
  notificationCount = 0;
  notifications: ContractNotification[] = [];

  featureAdd: any = [{ name: "", path: "" }]


  toggleUserDropdown() {
    this.isUserOpen = !this.isUserOpen;
    this.isAddOpen = false;
    this.isNotifiOpen = false; // Đóng thông báo khi mở user
  }

  toggleNotifiDropdown() {
    this.isNotifiOpen = !this.isNotifiOpen;
    this.isUserOpen = false; // Đóng user khi mở thông báo
    this.isAddOpen = false;
  }

  openSettings() {
    this.isSettingsOpen = true;
    this.isUserOpen = false;
    // Load dark mode state from sessionStorage
    const savedDarkMode = sessionStorage.getItem('darkMode');
    this.isDarkMode = savedDarkMode === 'true';
    this.applyDarkMode();
  }

  closeSettings() {
    this.isSettingsOpen = false;
  }

  toggleDarkMode() {
    this.isDarkMode = !this.isDarkMode;
    sessionStorage.setItem('darkMode', this.isDarkMode.toString());
    this.applyDarkMode();
  }

  applyDarkMode() {
    if (this.isDarkMode) {
      document.documentElement.classList.add('dark-mode');
      document.body.classList.add('dark-mode');
    } else {
      document.documentElement.classList.remove('dark-mode');
      document.body.classList.remove('dark-mode');
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
    const icon: any[] = [{
      iconName: "home", path: "/home/info", task: [{ name: "Trang chủ", path: "/home/info" }]
    }];
    this.role = DecodeTokenRole(this.token);
    if (this.role.length > 0)
      this.cookieService.set("role", this.role[0], { path: "/" });

    // Check role again safely
    const currentRole = this.role[0] || '';

    switch (currentRole) {
      case "Admin":
        const icon_admin = [
          {
            iconName: "manage_accounts", // Khác biệt: Quản lý user
            path: "/home/user/account",
            task: [{ name: "Quản Lý Tài Khoản", path: "/home/user/account" }, { name: "Cấp quyền hạn", path: "/home/permission" }]
          },
          {
            iconName: "paid", // Khác biệt: Quản lý tiền lương
            path: "/home/payroll",
            task: [{ name: "Cấu Hình Lương", path: "/home/payroll/rules" }],
          }, {
            iconName: "gavel", // Khác biệt: Hợp đồng (document)
            path: "/home/law",
            task: [{ name: "Quản Lý cấu hình luật", path: "/home/law" }],
          },
          {
            iconName: "event_note", // Khác biệt: Quản lý hoạt động
            path: "/home/activity-logs",
            task: [{ name: "Quản Lý hoạt động", path: "/home/activity-logs" }],
          },
        ];
        icon.push(...icon_admin)
        this.icon_handleBar = icon;
        break;

      case "HR":
        const icon_hr = [
          {
            iconName: "manage_accounts", // Quản lý nhân sự
            path: "/home/user/account",
            task: [{ name: "Quản Lí Nhân sự", path: "/home/user/account" }],
          },
          {
            iconName: "event_available", // Khác biệt: Chấm công/Phép
            path: "/home/user/attendance",
            task: [{ name: "Quản Lí chấm công", path: "/home/user/attendance" }, { name: "Quản lí phép", path: "/home/leaverequest/manage" }]
          },
          {
            iconName: "article", // Khác biệt: Hợp đồng (document)
            path: "/home/contracts",
            task: [{ name: "Quản Lí hợp đồng", path: "/home/contracts" }],
          },
          {
            iconName: "currency_exchange", // Khác biệt: Tiền tệ/Lương
            path: "/home/payroll",
            task: [{ name: "Tính Lương", path: "/home/payroll" }, { name: "Cấu Hình Lương", path: "/home/payroll/rules" }, { name: "Xem lương", path: "/home/payroll/payslip" }, { name: "Lọc DS Lương", path: "/home/payroll/payslip/filter" }],
          }, {
            iconName: "gavel", // Khác biệt: Hợp đồng (document)
            path: "/home/law",
            task: [{ name: "quản lí cấu hình luật", path: "/home/law" }],
          },
        ];
        icon.push(...icon_hr)
        this.icon_handleBar = icon;
        break;

      case "Manager":
        const icon_manager = [
          {
            iconName: "edit_calendar", // Khác biệt: Quản lý lịch
            path: "/home/user/attendance",
            task: [{ name: "Quản Lí chấm công", path: "/home/user/attendance" }, { name: "Lịch làm việc", path: "/home/schedule" }]
          },
          {
            iconName: "flight_takeoff", // Khác biệt: Nghỉ phép
            path: "/home/leaverequest",
            task: [{ name: "Nghỉ Phép", path: "/home/leaverequest" }, { name: "Quản lí phép", path: "/home/leaverequest/manage" }]
          },
          {
            iconName: "receipt_long", // Khác biệt: Hóa đơn/Phiếu lương
            path: "/home/payroll/payslip",
            task: [{ name: "Xem lương", path: "/home/payroll/payslip" },],
          },

        ];
        icon.push(...icon_manager)
        this.icon_handleBar = icon;
        break;

      case "Employee":
        const icon_employee = [
          {
            iconName: "calendar_month", // Lịch cơ bản
            path: "/home/user/attendance",
            task: [{ name: "Quản Lí chấm công", path: "/home/user/attendance" }, { name: "Lịch làm việc", path: "/home/schedule" }]
          },
          {
            iconName: "beach_access", // Khác biệt: Nghỉ mát/Phép
            path: "/home/leaverequest",
            task: [{ name: "Nghỉ Phép", path: "/home/leaverequest" }]
          },
          {
            iconName: "payments", // Thanh toán/Lương
            path: "/home/payroll/payslip",
            task: [{ name: "Xem lương", path: "/home/payroll/payslip" }],
          },
        ];
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

  // --- HÀM LOAD THÔNG BÁO ---
  async loadNotifications() {
    if (this.role[0] !== 'HR') {
      return; // Chỉ load thông báo cho HR
    }
    const res = await getNotificationContract();
    if (res && Array.isArray(res)) {
      this.notifications = res;
      this.notificationCount = res.length;
    }
    this.cdr.detectChanges();
  }

  ngOnInit() {
    this.CheckLogin();
    this.checkrole();
    // Load dark mode on init from sessionStorage
    const savedDarkMode = sessionStorage.getItem('darkMode');
    this.isDarkMode = savedDarkMode === 'true';
    this.applyDarkMode();

    // Gọi API lấy thông báo
    this.loadNotifications();
  }
}