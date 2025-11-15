import { CommonModule, NgFor, NgIf } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ChangeSchedule, schedule, userSchedule } from '../../../../interface/schedule.interface';
import { GetScheduleEmployeeoffice, GetScheduleEmployeeDraft, GetScheduleManagerdraft, GetScheduleManageroffice, ChangeScheduleManager, UpSchedule } from '../../../../services/pages/features/employee/shedule.services';
import { CookieService } from 'ngx-cookie-service';
import { scheduleList } from '../../../../utils/listSchedule.utils';
import { ChangePassword } from '../../../user/change-password/change-password';

@Component({
  selector: 'app-schedule',
  imports: [CommonModule, FormsModule, NgFor],
  templateUrl: './schedule.html',
  styleUrl: './schedule.scss',
})
export class Schedule implements OnInit {

  constructor(private cdr: ChangeDetectorRef, private cookie: CookieService) { }
  role: string = "";
  filteredData: schedule[] = [];
  filterManagerData: userSchedule[] = [];
  // Bộ lọc chỉ chọn tháng
  filter = {
    month: '',
    year: '',
    schedule: 0
  };
  date: string = "";
  shiftId: any = "";
  isdateoff: boolean = false;
  list: any[] = []
  isPopupOpen = false;

  editData = {
    employeeId: null,
    date: '',
    shiftId: null,
    isDayOff: false
  };


  months = Array.from({ length: 12 }, (_, i) => i + 1);
  years = ["2025", "2026"]
  schedule = [{ name: "Lịch Nháp", value: "0" }, { name: "Chính thức", value: "1" }]

  // Hàm lọc theo tháng
  async filterAttendance() {
    this.filteredData.length = 0;
    if (this.role == "employee") {
      this.filterManagerData.length = 0;
      if (this.filter.schedule == 0) {
        const month_year = `${this.filter.year}-${this.filter.month}`;
        const res = await GetScheduleEmployeeDraft(month_year);
        this.filteredData = res;

        this.cdr.detectChanges();
      } else {
        const month_year = `${this.filter.year}-${this.filter.month}`;
        const res = await GetScheduleEmployeeoffice(month_year);
        this.filteredData = res;
        this.cdr.detectChanges();

      }
    } else {
      this.filterManagerData.length = 0;
      if (this.filter.schedule == 0) {
        const month_year = `${this.filter.year}-${this.filter.month}`;
        const res = await GetScheduleManagerdraft(month_year);
        this.filterManagerData = res;
        this.cdr.detectChanges();
      } else {
        const month_year = `${this.filter.year}-${this.filter.month}`;
        const res = await GetScheduleManageroffice(month_year);
        this.filterManagerData = res;
        this.cdr.detectChanges();
      }
    }
  }
  changeType(hours: number) {
    scheduleList(hours, this.list);
  }
  editShift(att: any) {
    this.editData = {
      employeeId: att.employeeId,
      date: att.selectedDraft?.date || '',
      shiftId: att.selectedDraft?.shiftId || null,
      isDayOff: att.selectedDraft?.isDayOff || false
    };
    this.isPopupOpen = true;
  }



  closePopup() {
    this.isPopupOpen = false;
  }

  async submitEdit() {
    const forms: ChangeSchedule = {
      employeeId: Number(this.editData.employeeId),
      date: this.editData.date,
      shiftId: this.shiftId,
      isDayOff: this.editData.isDayOff
    }
    const res = await ChangeScheduleManager(forms) as { data: string, status: number };
    if (res.status == 200) {
      alert(res.data);
      return;
    }
    alert(res.data);

    this.isPopupOpen = false;
  }
  async upSchedule() {
    const month_year = `${this.filter.year}-${this.filter.month}`;
    const res = await UpSchedule(month_year) as { data: string, status: number };
    if (res.status) {
      alert(res.data);
      return;
    }
    alert(res.data);

  }
  ngOnInit(): void {
    this.role = this.cookie.get("role").toLowerCase();
  }
}

