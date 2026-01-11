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
  @ViewChild('notifiDrop') notifiDrop!: ElementRef;

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

  // --- QUẢN LÝ SIDEBAR MOBILE ---
  isSidebarOpen: boolean = false;

  toggleSidebar() {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  closeSidebar() {
    this.isSidebarOpen = false;
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
    this.isNotifiOpen = false;
  }

  toggleNotifiDropdown() {
    this.isNotifiOpen = !this.isNotifiOpen;
    this.isUserOpen = false;
    this.isAddOpen = false;
  }

  openSettings() {
    this.isSettingsOpen = true;
    this.isUserOpen = false;
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

    const currentRole = this.role[0] || '';

    switch (currentRole) {
      case "Admin":
        const icon_admin = [
          // 1. Quản trị hệ thống & Tài khoản (Gốc của Admin)
          {
            iconName: "manage_accounts",
            path: "/home/user/account",
            task: [
              { name: "Quản Lý Tài Khoản", path: "/home/user/account" },
              { name: "Cấp Quyền Hạn", path: "/home/permission" }
            ]
          },
          // 2. Nhân sự & Chấm công (Lấy từ HR + Manager)
          {
            iconName: "event_available",
            path: "/home/user/attendance",
            task: [
              { name: "Quản Lý Chấm Công", path: "/home/user/attendance" },
              { name: "Lịch Làm Việc", path: "/home/schedule" }, // Thêm từ Manager
              { name: "Quản Lý Phép", path: "/home/leaverequest/manage" },
              { name: "Đăng Ký Nghỉ Phép", path: "/home/leaverequest" } // Admin cũng có thể cần nghỉ phép
            ]
          },
          // 3. Hợp đồng (Lấy từ HR)
          {
            iconName: "article",
            path: "/home/contracts",
            task: [
              { name: "Quản Lý Hợp Đồng", path: "/home/contracts/edit" },
              { name: "Kiểm tra Hợp Đồng", path: "/home/contracts" },
              { name: "Thêm Hợp Đồng", path: "/home/contracts/edit/add" }
            ],
          },
          // 4. Lương & Thưởng (Gộp Admin + HR)
          {
            iconName: "paid", // Hoặc dùng icon currency_exchange của HR
            path: "/home/payroll",
            task: [
              { name: "Cấu Hình Lương", path: "/home/payroll/rules" },
              { name: "Tính Lương", path: "/home/payroll" },
              { name: "Xem Lương", path: "/home/payroll/payslip" },
              { name: "Lọc DS Lương", path: "/home/payroll/payslip/filter" },
              { name: "Quản Lý Thưởng/Phạt", path: "/home/user/reward-punish" }
            ],
          },
          // 5. Luật (Gốc của Admin)
          {
            iconName: "gavel",
            path: "/home/law",
            task: [{ name: "Quản Lý Cấu Hình Luật", path: "/home/law" }],
          },
          // 6. Logs hệ thống (Gốc của Admin)
          {
            iconName: "event_note",
            path: "/home/activity-logs",
            task: [{ name: "Quản Lý Hoạt Động", path: "/home/activity-logs" }],
          },
        ];
        icon.push(...icon_admin)
        this.icon_handleBar = icon;
        break;

      case "HR":
        const icon_hr = [
          {
            iconName: "manage_accounts",
            path: "/home/user/account",
            task: [{ name: "Quản Lý Nhân Sự", path: "/home/user/account" }],
          },
          {
            iconName: "event_available",
            path: "/home/user/attendance",
            task: [{ name: "Quản Lý Chấm Công", path: "/home/user/attendance" }, { name: "Quản Lý Phép", path: "/home/leaverequest/manage" }]
          },
          {
            iconName: "article",
            path: "/home/contracts",
            task: [{ name: "Quản Lý Hợp Đồng", path: "/home/contracts" },
            { name: "Hợp Đồng Mẫu", path: "/home/contracts/edit" }
              // , { name: "Thêm Hợp Đồng", path: "/home/contracts/edit/add" }
            ],
          },
          {
            iconName: "document_search",
            path: "/home/user/Mydocuments",
            task: [{ name: "Quản Lý Hồ Sơ", path: "/home/user/Mydocuments" }]
          },
          {
            iconName: "currency_exchange",
            path: "/home/payroll",
            task: [{ name: "Tính Lương", path: "/home/payroll" },
            { name: "Cấu Hình Lương", path: "/home/payroll/rules" },
            { name: "Xem Lương", path: "/home/payroll/payslip" },
            { name: "Lọc DS Lương", path: "/home/payroll/payslip/filter" },
            { name: "Quản Lý Thưởng/Phạt", path: "/home/user/reward-punish" }
            ],
          }, {
            iconName: "gavel",
            path: "/home/law",
            task: [{ name: "Quản Lý Cấu Hình Luật", path: "/home/law" }],
          },
        ];
        icon.push(...icon_hr)
        this.icon_handleBar = icon;
        break;

      case "Manager":
        const icon_manager = [
          {
            iconName: "edit_calendar",
            path: "/home/user/attendance",
            task: [{ name: "Quản Lý Chấm Công", path: "/home/user/attendance" }, { name: "Lịch Làm Việc", path: "/home/schedule" }]
          },
          {
            iconName: "flight_takeoff",
            path: "/home/leaverequest",
            task: [{ name: "Nghỉ Phép", path: "/home/leaverequest" }, { name: "Quản Lý Phép", path: "/home/leaverequest/manage" }]
          },
          {
            iconName: "document_search",
            path: "/home/user/Mydocuments",
            task: [{ name: "Quản Lý Hồ Sơ", path: "/home/user/Mydocuments" }]
          },
          {
            iconName: "receipt_long",
            path: "/home/payroll/payslip",
            task: [{ name: "Xem Lương", path: "/home/payroll/payslip" },],
          },

        ];
        icon.push(...icon_manager)
        this.icon_handleBar = icon;
        break;

      case "Employee":
        const icon_employee = [
          {
            iconName: "calendar_month",
            path: "/home/user/attendance",
            task: [{ name: "Quản Lý Chấm Công", path: "/home/user/attendance" }, { name: "Lịch Làm Việc", path: "/home/schedule" }]
          },
          {
            iconName: "beach_access",
            path: "/home/leaverequest",
            task: [{ name: "Nghỉ Phép", path: "/home/leaverequest" }]
          },
          {
            iconName: "document_search",
            path: "/home/user/Mydocuments",
            task: [{ name: "Quản Lý Hồ Sơ", path: "/home/user/Mydocuments" }]
          },
          {
            iconName: "payments",
            path: "/home/payroll/payslip",
            task: [{ name: "Xem Lương", path: "/home/payroll/payslip" }],
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

  async loadNotifications() {
    if (this.role[0] !== 'HR') {
      return;
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
    const savedDarkMode = sessionStorage.getItem('darkMode');
    this.isDarkMode = savedDarkMode === 'true';
    this.applyDarkMode();

    this.loadNotifications();
  }
}