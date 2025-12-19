import { CommonModule, NgFor, NgIf } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Tablemonth } from '../../../shared/tablemonth/tablemonth';
import { GetScheduleEmployeeDraft, GetScheduleEmployeeoffice, GetScheduleManagerdraft, GetScheduleManageroffice, UpSchedule } from '../../../../services/pages/features/employee/shedule.services';
import { CookieService } from 'ngx-cookie-service';
import { Router } from '@angular/router';
import { Loading } from '../../../shared/loading/loading';
import { Alert } from '../../../shared/alert/alert';
import { Comfirm } from '../../../shared/comfirm/comfirm';
import { Reschedule } from '../../../shared/reschedule/reschedule';

@Component({
  selector: 'app-schedule',
  standalone: true,
  imports: [CommonModule, FormsModule, Tablemonth, NgIf, Loading, Alert, Comfirm, Reschedule],
  templateUrl: './schedule.html',
  styleUrl: './schedule.scss',
})
export class Schedule implements OnInit {
  constructor(private cdr: ChangeDetectorRef, private cookie: CookieService,
    private router: Router
  ) { }
  index = {
    row: 0,
    col: 0
  };
  year: string = "";
  month: string = "";
  role: string = '';
  selectStatus = '';
  date: string = '';
  ////////////////////////
  isconfirm: boolean = false;
  isalert: boolean = false;
  isloading: boolean = false;
  confirmMessage = '';
  alertmessage = '';
  alertType: boolean = true;
  actionType: 'approve' | 'reject' | '' = '';
  statusSchedule = "";
  Onalert(message: string, type: boolean) {
    this.isalert = true;
    this.alertmessage = message;
    this.alertType = type;
  }
  /////////////////////////

  // quan li xin doi ca lam
  isShowRescheduleModal: boolean = false;
  selectedDate: string = '';

  // Hàm xử lý khi click vào ngày trên lịch
  handleDayClick(event: any) {
    // --- ĐIỀU KIỆN MỚI ---
    // Chỉ cho phép mở modal khi đang xem Lịch chính thức (Employee: 1, Manager: 3)
    if (this.selectStatus !== '1' && this.selectStatus !== '3') {
      return;
    }

    // 1. Lấy dữ liệu ngày được click
    this.selectedDate = event.date;

    // 2. Mở Modal lên
    if (this.dayData.length != 0) {
      const today = this.dayData.find(item => item.date == this.selectedDate);

      // Kiểm tra: phải tồn tại ngày đó và có ca làm việc (shiftId != null) mới cho đổi
      if (today && today.shiftId != null) {
        this.isShowRescheduleModal = true;
      }
    }
  }

  ///
  dayData: any[] = [];

  handleMonthYear(event: any) {
    this.year = event.year;
    this.month = event.month;
    this.cdr.detectChanges();
  }
  autoSchedule() {
    this.router.navigate(["home/schedule/auto"]);
  }
  public async loadData() {
    const year_month = `${this.year}-${String(this.month).padStart(2, '0')}`;

    let res: any[] = [];
    switch (this.selectStatus) {
      case '0':
        res = await GetScheduleEmployeeDraft(year_month);
        break;
      case '1':
        res = await GetScheduleEmployeeoffice(year_month);
        break;
      case '2':
        res = await GetScheduleManagerdraft(year_month);
        this.statusSchedule = "draft";
        sessionStorage.setItem("statusSchedule", this.statusSchedule);
        this.cdr.detectChanges();
        break;
      case '3':
        res = await GetScheduleManageroffice(year_month);
        this.statusSchedule = "office";
        sessionStorage.setItem("statusSchedule", this.statusSchedule);
        this.cdr.detectChanges();
        console.log(this.statusSchedule)

        break;
      default:
        res = [];
        break;
    }

    this.dayData = res;
    this.cdr.detectChanges();
  }

  chooseSchedule() {
    this.loadData();
  }
  registerShift() {
    this.router.navigate(["/home/schedule/register"])
  }
  UpOfficeSchedule() {

    this.isconfirm = true;
    this.confirmMessage = "Bạn có chắc muốn duyệt lịch này ?";

  }
  async onConfirmResult(event: any) {
    this.isloading = true;
    const year_month = `${this.year}-${this.month}`;
    if (event == true) {
      this.isconfirm = false;
      const res = await UpSchedule(year_month) as { data: string, status: number };
      if (res.status = 200) {
        this.isloading = false;
        setTimeout(() => {
          this.cdr.detectChanges();
        }, 2000);
        this.Onalert(res.data, true);
        return;
      }
      this.isloading = false;
      setTimeout(() => {
        this.cdr.detectChanges();
      }, 2000);
      this.Onalert(res.data, false);

    } else {
      this.isloading = false;

      this.isconfirm = false;
    }
  }

  ngOnInit() {
    // Đảm bảo cookie trả về string trước khi toLowerCase() để tránh lỗi null
    const roleCookie = this.cookie.get("role");
    this.role = roleCookie ? roleCookie.toLowerCase() : '';
  }
}