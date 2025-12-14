import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
// Restore original imports
import { DecodeTokenRole } from '../../utils/token.utils';
import { Loout_service } from '../../services/pages/login.service';
import { ApproveReschedule, getReschedule, RejectReschedule } from '../../services/pages/features/employee/shedule.services';
import { ShiftChangeRequest } from '../../interface/schedule.interface';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
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
    // Không cần check click outside ở đây nữa vì đã dùng Overlay modal
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
  isNotifiOpen = false;
  featureAdd: any = [{ name: "", path: "" }]

  // List of pending requests
  pendingRequests: ShiftChangeRequest[] = [];

  toggleUserDropdown() {
    this.isUserOpen = !this.isUserOpen;
    this.isAddOpen = false;
    this.isNotifiOpen = false;
  }

  toggleAddDropdown() {
    this.isAddOpen = !this.isAddOpen;
    this.isUserOpen = false;
    this.isNotifiOpen = false;
    this.cdr.detectChanges();
    if (this.isAddOpen == true) {
      if (this.role.length > 0) {
        // Safe check for role existence
        const currentRole = this.role[0] ? this.role[0].toLowerCase() : '';
        switch (currentRole) {
          case "admin":
            this.featureAdd = [{ name: "Cấp tài khoản", path: "/home/add/account" }, { name: "Cấp quyền hạn", path: "/home/permission" }];
            break;
          case "hr":
            this.featureAdd = [{ name: "Thêm hợp đồng", path: "/home/contracts/add" }];
            break;
          case "employee":
            this.featureAdd = [
              { name: "Đăng Ký Phép", path: "/home/leaverequest/add" }
            ];
            break;
          case "manager":
            this.featureAdd = [{ name: "Lịch làm việc", path: "/home/schedule" }, { name: "Xếp ca làm việc", path: "/home/schedule/auto" }];
            break;
        }
      }
    }
  }

  toggleNotifiDropdown() {
    // Luôn mở để xem, kể cả khi 0 thông báo (để hiển thị empty state)
    this.isNotifiOpen = !this.isNotifiOpen;
    this.isAddOpen = false;
    this.isUserOpen = false;
  }

  closeNotification() {
    this.isNotifiOpen = false;
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
            task: [{ name: "Quản Lí Nhân sự", path: "/home/user/account" }]
          },
          {
            iconName: "paid", // Khác biệt: Quản lý tiền lương
            path: "/home/payroll",
            task: [{ name: "Quản Lí lương", path: "/home/payroll" }, { name: "Tính Lương", path: "/home/payroll/rules" }],
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
            task: [{ name: "Quản Lí lương", path: "/home/payroll" }, { name: "Tính Lương", path: "/home/payroll/rules" }, { name: "Xem lương", path: "/home/payroll/payslip" }, { name: "Lọc DS Lương", path: "/home/payroll/payslip/filter" }],
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
            task: [{ name: "Quản Lí chấm công", path: "/home/user/attendance" }, { name: "Lịch làm việc", path: "/home/schedule" }, { name: "Quản lí phép", path: "/home/leaverequest/manage" }]
          },
          {
            iconName: "flight_takeoff", // Khác biệt: Nghỉ phép
            path: "/home/leaverequest",
            task: [{ name: "Nghỉ Phép", path: "/home/leaverequest" }]
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
  countnotifi: number = 0;
  query = '';

  async getNotification() {
    const res = await getReschedule(this.query) as any[];
    if (res && Array.isArray(res)) {
      const pending = res.filter((item: ShiftChangeRequest) => item.status == 'PENDING');
      this.pendingRequests = pending;
      this.countnotifi = pending.length;
      this.cdr.detectChanges();
    }
  }

  // --- LOGIC DUYỆT / TỪ CHỐI ---
  async onApprove(id: number, event: Event) {
    event.stopPropagation();
    console.log("Approve request ID:", id);
    // TODO: Call API to approve here
    await ApproveReschedule(id);

    // Refresh list UI 
    this.pendingRequests = this.pendingRequests.filter(req => req.id !== id);
    this.countnotifi = this.pendingRequests.length;
    if (this.countnotifi === 0) {
      // Có thể đóng modal nếu muốn: this.isNotifiOpen = false;
    }
    this.getNotification();
  }

  async onReject(id: number, event: Event) {
    event.stopPropagation();
    console.log("Reject request ID:", id);
    // TODO: Call API to reject here
    await RejectReschedule(id);

    // Refresh list UI
    this.pendingRequests = this.pendingRequests.filter(req => req.id !== id);
    this.countnotifi = this.pendingRequests.length;
    if (this.countnotifi === 0) {
      // Có thể đóng modal nếu muốn
    }
    this.getNotification();
  }

  ngOnInit() {
    this.CheckLogin();
    this.checkrole();
    this.getNotification();

  }
}