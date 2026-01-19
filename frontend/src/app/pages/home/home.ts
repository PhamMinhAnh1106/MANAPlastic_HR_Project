import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChangeDetectorRef, Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';

import { DecodeTokenRole } from '../../utils/token.utils';
import { Loout_service } from '../../services/pages/login.service';
import { getNotificationContract } from '../../services/pages/features/hr/contracts.service';
import { ThemeService } from '../../services/theme.service';

interface ContractNotification {
  id: number;
  contractCode: string;
  employeeName: string;
  endDate: string;
  daysRemaining: number;
}

// 1. KHO DỮ LIỆU (MASTER DATA)
// Định nghĩa danh sách chức năng duy nhất, dùng ID làm khóa chính.
const MENU_MASTER_DATA = [
  { id: 1, name: "Trang chủ", path: "/home/info", icon: "home" },
  { id: 2, name: "Quản Lý Tài Khoản", path: "/home/user/account", icon: "manage_accounts" },
  { id: 3, name: "Cấp Quyền Hạn", path: "/home/permission", icon: "manage_accounts" },
  { id: 4, name: "Quản Lý Chấm Công", path: "/home/user/attendance", icon: "event_available" },
  { id: 5, name: "Lịch Làm Việc", path: "/home/schedule", icon: "event_available" },
  { id: 6, name: "Quản Lý Phép", path: "/home/leaverequest/manage", icon: "event_available" },
  { id: 18, name: "Quản Lý Ngày OverTime", path: "/home/manage/overtime", icon: "event_available" },
  { id: 7, name: "Đăng Ký Nghỉ Phép", path: "/home/leaverequest", icon: "flight_takeoff" },
  { id: 8, name: "Quản Lý Hợp Đồng", path: "/home/contracts", icon: "article" },
  { id: 9, name: "Hợp Đồng Mẫu", path: "/home/contracts/edit", icon: "article" },
  { id: 10, name: "Cấu Hình Lương", path: "/home/payroll/rules", icon: "paid" },
  { id: 11, name: "Tính Lương", path: "/home/payroll", icon: "paid" },
  { id: 12, name: "Xem Lương", path: "/home/payroll/payslip", icon: "paid" },
  { id: 13, name: "Lọc DS Lương", path: "/home/payroll/payslip/filter", icon: "paid" },
  { id: 14, name: "Quản Lý Thưởng/Phạt", path: "/home/user/reward-punish", icon: "paid" },
  { id: 19, name: "Kiểm Tra Lương", path: "/home/salarydate", icon: "paid" },
  { id: 15, name: "Quản Lý Cấu Hình Luật", path: "/home/law", icon: "gavel" },
  { id: 16, name: "Quản Lý Hoạt Động", path: "/home/activity-logs", icon: "event_note" },
  { id: 17, name: "Quản Lý Hồ Sơ", path: "/home/user/Mydocuments", icon: "document_search" },
];

// Helper để lấy thông tin item nhanh theo ID
const getMenuItem = (id: number) => MENU_MASTER_DATA.find(item => item.id === id);

// Helper để tạo nhóm menu
function buildGroup(iconName: string, rootPath: string, childIds: number[]) {
  const tasks = childIds
    .map(id => getMenuItem(id))
    .filter(item => item !== undefined) // Loại bỏ item không tồn tại
    .map(item => ({
      name: item!.name,
      path: item!.path
      // Có thể thêm icon cho item con ở đây nếu giao diện hỗ trợ
    }));

  if (tasks.length === 0) return null; // Không tạo nhóm nếu không có item con nào hợp lệ

  return {
    iconName: iconName,
    path: rootPath,
    task: tasks
  };
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet, FormsModule, DatePipe],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements OnInit {
  constructor(
    private cookieService: CookieService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private themeService: ThemeService
  ) { }

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
    this.isDarkMode = this.themeService.getCurrentTheme() === 'dark';
  }

  closeSettings() {
    this.isSettingsOpen = false;
  }

  toggleTheme() {
    this.themeService.toggleTheme();
    this.isDarkMode = this.themeService.getCurrentTheme() === 'dark';
    this.isUserOpen = false; // Close dropdown after toggle
  }

  toggleDarkMode() {
    this.themeService.toggleTheme();
    this.isDarkMode = this.themeService.getCurrentTheme() === 'dark';
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
    // 1. Luôn có trang chủ (ID 1)
    const homeItem = getMenuItem(1);
    const icon: any[] = homeItem ? [{
      iconName: homeItem.icon,
      path: homeItem.path,
      task: [{ name: homeItem.name, path: homeItem.path }]
    }] : [];

    // 2. Lấy Role từ token
    this.role = DecodeTokenRole(this.token);
    if (this.role.length > 0)
      this.cookieService.set("role", this.role[0], { path: "/" });

    const currentRole = this.role[0] || '';
    let roleMenus: any[] = [];

    // 3. Cấu hình Menu cho từng Role dựa trên ID
    switch (currentRole) {
      case "Admin":
        roleMenus = [
          // Quản trị hệ thống & Tài khoản (Gốc Admin) - ID: 2, 3
          buildGroup("manage_accounts", "/home/user/account", [2, 3]),

          // Nhân sự & Chấm công (Từ HR + Manager) - ID: 4, 5, 6, 7
          buildGroup("event_available", "/home/user/attendance", [4, 5, 6, 7]),

          // Hợp đồng (Từ HR) - ID: 8, 9
          buildGroup("article", "/home/contracts", [8, 9]),

          // Lương & Thưởng (Gộp Admin + HR) - ID: 10, 11, 12, 13, 14
          buildGroup("paid", "/home/payroll", [10, 11, 12, 13, 14, 19]),

          // Luật (Gốc Admin) - ID: 15
          buildGroup("gavel", "/home/law", [15]),

          // Logs hệ thống (Gốc Admin) - ID: 16
          buildGroup("event_note", "/home/activity-logs", [16]),

          // Hồ sơ - ID: 17
          buildGroup("document_search", "/home/user/Mydocuments", [17]),
        ];
        break;

      case "HR":
        roleMenus = [
          // Quản lý nhân sự - ID: 2
          buildGroup("manage_accounts", "/home/user/account", [2]),

          // Chấm công & Phép - ID: 4, 6
          buildGroup("event_available", "/home/user/attendance", [4, 6, 18]),

          // Hợp đồng - ID: 8, 9
          buildGroup("article", "/home/contracts", [8, 9]),

          // Hồ sơ - ID: 17
          buildGroup("document_search", "/home/user/Mydocuments", [17]),

          // Lương - ID: 11, 10, 12, 13, 14
          buildGroup("currency_exchange", "/home/payroll", [11, 10, 12, 13, 14, 19]),

          // Luật - ID: 15
          buildGroup("gavel", "/home/law", [15]),
        ];
        break;

      case "Manager":
        roleMenus = [
          // Chấm công Manager - ID: 4, 5
          buildGroup("edit_calendar", "/home/user/attendance", [4, 5, 18]),

          // Nghỉ phép - ID: 7, 6
          buildGroup("flight_takeoff", "/home/leaverequest", [7]),

          // Hồ sơ - ID: 17
          buildGroup("document_search", "/home/user/Mydocuments", [17]),

          // Xem lương - ID: 12
          buildGroup("receipt_long", "/home/payroll/payslip", [12, 19]),
        ];
        break;

      case "Employee":
        roleMenus = [
          // Chấm công Employee - ID: 4, 5
          buildGroup("calendar_month", "/home/user/attendance", [4, 5, 18]),

          // Nghỉ phép - ID: 7
          buildGroup("beach_access", "/home/leaverequest", [7]),

          // Hồ sơ - ID: 17
          buildGroup("document_search", "/home/user/Mydocuments", [17]),

          // Xem lương - ID: 12
          buildGroup("payments", "/home/payroll/payslip", [12, 19]),
        ];
        break;
    }

    // 4. Lọc bỏ các group null (nếu có lỗi ID) và gộp vào icon chính
    const validMenus = roleMenus.filter(g => g !== null);
    icon.push(...validMenus);

    this.icon_handleBar = icon;
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
    this.isDarkMode = this.themeService.getCurrentTheme() === 'dark';

    // Subscribe to theme changes
    this.themeService.theme$.subscribe(theme => {
      this.isDarkMode = theme === 'dark';
    });

    this.loadNotifications();
  }
}