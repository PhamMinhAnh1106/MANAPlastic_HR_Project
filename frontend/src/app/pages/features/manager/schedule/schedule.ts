import { CommonModule, NgFor, NgIf } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { schedule, userSchedule } from '../../../../interface/schedule.interface';
import { GetScheduleEmployeeoffice, GetScheduleEmployeeDraft, GetScheduleManagerdraft, GetScheduleManageroffice } from '../../../../services/pages/features/employee/shedule.services';
import { CookieService } from 'ngx-cookie-service';

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
  ngOnInit(): void {
    this.role = this.cookie.get("role").toLowerCase();
  }
}

