import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';
import { userSchedule } from '../../../interface/schedule.interface';
import { scheduleList } from '../../../utils/listSchedule.utils';
import { Loading } from '../loading/loading';
import { Alert } from '../alert/alert';
import { Comfirm } from '../comfirm/comfirm';
import { ChangeScheduleManager } from '../../../services/pages/features/employee/shedule.services';
interface DayObj {
  date: Date;
  inMonth: boolean;
  isDayOff: boolean;
  shiftName?: string;
  shifts?: any[]; // optional, có thể chưa có ca
}
@Component({
  selector: 'app-tablemonth',
  imports: [NgFor, FormsModule, NgIf, Loading, Alert, Comfirm],
  templateUrl: './tablemonth.html',
  styleUrl: './tablemonth.scss',
})
export class Tablemonth implements OnInit, OnChanges {

  selectedMonth: number = new Date().getMonth() + 1;
  selectedYear: number = new Date().getFullYear();
  ////////////////////////
  isconfirm: boolean = false;
  isalert: boolean = false;
  isloading: boolean = false;
  confirmMessage = '';
  alertmessage = '';
  alertType: boolean = true;

  Onalert(message: string, type: boolean) {
    this.isalert = true;
    this.alertmessage = message;
    this.alertType = type;
  }
  /////////////////////////
  weeks: any[] = [];

  @Input() dayData: any[] = []; // dữ liệu từ API (employee hoặc manager)
  role: string = '';
  @Output() selectDay = new EventEmitter<any>();
  @Output() monthYearChange = new EventEmitter<{ month: number, year: number }>();

  dayShiftMap: { [date: string]: any[] } = {}; // map ngày → list ca

  constructor(private cdr: ChangeDetectorRef, private cookie: CookieService) { }

  ngOnInit() {
    this.role = this.cookie.get("role").toLowerCase();
    this.cdr.detectChanges()
    this.prepareDayShiftMap();
    this.generateCalendar();
    this.emitMonthYear();
  }

  ngOnChanges() {
    this.prepareDayShiftMap();
    this.generateCalendar();
  }

  formatDate(date: Date): string {
    const y = date.getFullYear();
    const m = ('0' + (date.getMonth() + 1)).slice(-2);
    const d = ('0' + date.getDate()).slice(-2);
    return `${y}-${m}-${d}`;
  }

  prepareDayShiftMap() {


    this.dayShiftMap = {};



    if (this.role === 'employee') {
      this.dayData.forEach(d => {
        const date = d.date;
        this.dayShiftMap[date] = [{
          shiftName: d.shiftName,
          shiftId: d.shiftId,
          isDayOff: d.isDayOff
        }];
      });

    } else if (this.role === 'manager') {


      this.dayData.forEach((emp: userSchedule) => {
        if (!emp.drafts) return;

        emp.drafts.forEach(shift => {
          const date = shift.date;
          if (!this.dayShiftMap[date]) this.dayShiftMap[date] = [];
          this.dayShiftMap[date].push({
            employeeId: emp.employeeId,
            employeeFullName: emp.employeeFullName,
            shiftName: shift.shiftName || 'OFF',
            shiftId: shift.shiftId,
            isDayOff: shift.isDayOff
          });
        });
      });
    }
  }




  generateCalendar() {
    this.weeks = [];
    const firstDay = new Date(this.selectedYear, this.selectedMonth - 1, 1);
    const lastDay = new Date(this.selectedYear, this.selectedMonth, 0);

    let startDate = new Date(firstDay);
    startDate.setDate(firstDay.getDate() - ((firstDay.getDay() + 6) % 7));
    let current = new Date(startDate);

    while (current <= lastDay || current.getDay() !== 1) {
      let week = [];
      for (let i = 0; i < 7; i++) {
        const dayObj: DayObj = {
          date: new Date(current),
          inMonth: current.getMonth() === firstDay.getMonth(),
          isDayOff: false,
          shifts: []

        };

        const currentDate = this.formatDate(dayObj.date);
        if (this.dayShiftMap[currentDate]) {
          dayObj['shifts'] = this.dayShiftMap[currentDate];
        } else {
          dayObj['shifts'] = [];
        }

        week.push(dayObj);

        current.setDate(current.getDate() + 1);
      }
      this.weeks.push(week);
    }
  }

  onDayClick(day: any, rowIndex: number, colIndex: number) {
    if (!day.inMonth) return;
    this.selectDay.emit({
      date: this.formatDate(day.date),
      row: rowIndex,
      col: colIndex,
      shifts: day.shifts
    });
  }

  showShiftInfo(shift: any, event: MouseEvent) {
    if (this.role == "employee") return;
  }

  onMonthYearChange() {
    this.generateCalendar();
    this.emitMonthYear();
  }

  private emitMonthYear() {
    this.monthYearChange.emit({
      month: this.selectedMonth,
      year: this.selectedYear
    });
  }



  selectedShiftData: any = null;   // chứa employeeId, date, shiftId
  shiftId: number | null = null;   // dropdown chọn ca
  list: any[] = [];                // list ca (API trả về)

  // Mở popup chỉnh sửa ca
  openEditShift(day: any, shift: any) {
    this.selectedShiftData = {
      employeeId: shift.employeeId,
      employeeFullName: shift.employeeFullName,
      date: this.formatDate(day.date),
      oldShiftId: shift.shiftId,
      isDayOff: shift.isDayOff
    };

    this.shiftId = shift.shiftId; // gán ca hiện tại
  }
  changeType(hours: number) {
    scheduleList(hours, this.list);
  }
  async onConfirmResult(event: any) {

    if (!this.shiftId) return this.Onalert("Bạn chưa chọn ca!", false);
    this.isconfirm = false;
    if (event == true) {
      this.isloading = true;
      const payload = {
        employeeId: this.selectedShiftData.employeeId,
        date: this.selectedShiftData.date,
        shiftId: this.shiftId,
        isDayOff: this.shiftId >= 53
      };
      const res = await ChangeScheduleManager(payload) as { data: string, status: number };
      if (res.status == 200) {
        this.isloading = false;
        this.selectedShiftData = null;
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


      this.selectedShiftData = null; // đóng popup
      this.monthYearChange.emit({ month: this.selectedMonth, year: this.selectedYear });
    }
  }

  saveShift() {
    this.isconfirm = true;
    this.confirmMessage = "Bạn Có chắc muốn thay đổi thông tin ca làm này ?";

  }
}
