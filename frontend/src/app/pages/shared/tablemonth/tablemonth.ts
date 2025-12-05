import { NgFor, NgIf, SlicePipe } from '@angular/common';
import { ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';
import { scheduleList } from '../../../utils/listSchedule.utils';

// --- IMPORT GỐC (Bỏ comment khi dùng trong dự án thật) ---
// import { userSchedule } from '../../../interface/schedule.interface';
// import { scheduleList } from '../../../utils/listSchedule.utils';
// import { Loading } from '../loading/loading';
// import { Alert } from '../alert/alert';
// import { Comfirm } from '../comfirm/comfirm';
// import { ChangeScheduleDraftManager, ChangeScheduleOfficeManager } from '../../../services/pages/features/employee/shedule.services';
// import { Schedule } from '../../features/manager/schedule/schedule';

// --- ĐỊNH NGHĨA TẠM THỜI ĐỂ FIX LỖI COMPILE (Xóa khi dùng thật) ---
export interface userSchedule { employeeId: number; employeeFullName: string; drafts?: any[]; }
export const ChangeScheduleDraftManager = async (payload: any) => ({ status: 200, data: 'Cập nhật nháp thành công' });
export const ChangeScheduleOfficeManager = async (payload: any) => ({ status: 200, data: 'Cập nhật chính thức thành công' });
export class Schedule { loadData() { console.log('Reload data'); } }

@Component({ selector: 'app-loading', standalone: true, template: '<div style="position:fixed;top:50%;left:50%;background:black;color:white;padding:10px">Loading...</div>' }) export class Loading { }
@Component({ selector: 'app-alert', standalone: true, template: '<div style="position:fixed;top:10px;right:10px;background:green;color:white;padding:10px" *ngIf="message">{{message}}</div>' }) export class Alert { @Input() message: any; @Input() notifyType: any; }
@Component({ selector: 'app-comfirm', standalone: true, template: '<div style="position:fixed;top:50%;left:50%;transform:translate(-50%,-50%);background:white;border:1px solid #ccc;padding:20px" *ngIf="message"><p>{{message}}</p><button (click)="result.emit(true)">OK</button><button (click)="result.emit(false)">Cancel</button></div>' }) export class Comfirm { @Input() message: any; @Output() result = new EventEmitter(); }
// -------------------------------------------------------------

interface DayObj {
  date: Date;
  inMonth: boolean;
  isDayOff: boolean;
  shiftName?: string;
  shifts?: any[];
}

@Component({
  selector: 'app-tablemonth',
  standalone: true,
  imports: [NgFor, FormsModule, NgIf, Loading, Alert, Comfirm],
  templateUrl: './tablemonth.html',
  styleUrl: './tablemonth.scss',
  providers: [Schedule] // Provider tạm cho class Schedule mock
})
export class Tablemonth implements OnInit, OnChanges {
  // --- Output Events mới ---
  @Output() statusChange = new EventEmitter<string>(); // Bắn trạng thái lọc ra ngoài
  @Output() upOffice = new EventEmitter<void>();       // Sự kiện đăng lịch
  @Output() autoSchedule = new EventEmitter<void>();   // Sự kiện xếp lịch
  @Output() register = new EventEmitter<void>();       // Sự kiện đăng ký ca (employee)

  selectStatus: string = ''; // Biến lưu trạng thái select box

  showDayDetailsPopup: boolean = false;
  selectedDayForDetails: any = null;

  constructor(
    private cdr: ChangeDetectorRef,
    private cookie: CookieService,
    private schedule: Schedule
  ) { }

  // --- Emit Handlers (Đẩy sự kiện ra cha) ---
  emitChooseSchedule() {
    this.statusChange.emit(this.selectStatus);
  }

  emitUpOffice() {
    this.upOffice.emit();
  }

  emitAutoSchedule() {
    this.autoSchedule.emit();
  }

  emitRegister() {
    this.register.emit();
  }
  // ------------------------------------------

  onDetailShiftClick(shift: any) {
    if (this.role === 'manager') {
      this.openEditShift(this.selectedDayForDetails, shift);
      this.closeDayDetailsPopup();
    }
  }

  openDayDetailsPopup(day: any) {
    const dateObj = new Date(day.date);
    const dayStr = ('0' + dateObj.getDate()).slice(-2);
    const monthStr = ('0' + (dateObj.getMonth() + 1)).slice(-2);
    const yearStr = dateObj.getFullYear();

    this.selectedDayForDetails = {
      ...day,
      dateStr: `${dayStr}/${monthStr}/${yearStr}`
    };
    this.showDayDetailsPopup = true;
  }

  closeDayDetailsPopup() {
    this.showDayDetailsPopup = false;
    this.selectedDayForDetails = null;
  }

  selectedMonth: number = new Date().getMonth() + 1;
  selectedYear: number = new Date().getFullYear();

  // UI State
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

  weeks: any[] = [];
  @Input() dayData: any[] = [];
  role: string = '';
  @Output() selectDay = new EventEmitter<any>();
  @Output() monthYearChange = new EventEmitter<{ month: number, year: number }>();

  dayShiftMap: { [date: string]: any[] } = {};
  statusSchedule: string | null = '';

  ngOnInit() {
    this.role = this.cookie.get("role")?.toLowerCase() || 'manager'; // Fallback nếu chưa có cookie
    this.cdr.detectChanges();
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

  isToday(date: Date): boolean {
    const today = new Date();
    return date.getDate() === today.getDate() &&
      date.getMonth() === today.getMonth() &&
      date.getFullYear() === today.getFullYear();
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

    let safetyCount = 0;
    while ((current <= lastDay || current.getDay() !== 1) && safetyCount < 42) {
      let week = [];
      for (let i = 0; i < 7; i++) {
        const dayObj: DayObj = {
          date: new Date(current),
          inMonth: current.getMonth() === firstDay.getMonth(),
          isDayOff: false,
          shifts: []
        };
        const currentDate = this.formatDate(dayObj.date);
        dayObj['shifts'] = this.dayShiftMap[currentDate] || [];
        week.push(dayObj);
        current.setDate(current.getDate() + 1);
      }
      this.weeks.push(week);
      if (current.getMonth() !== firstDay.getMonth() && current.getDate() > 7) break;
      safetyCount++;
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

  selectedShiftData: any = null;
  shiftId: number | null = null;
  list: any[] = [];

  openEditShift(day: any, shift: any) {
    this.selectedShiftData = {
      employeeId: shift.employeeId,
      employeeFullName: shift.employeeFullName,
      date: typeof day.date === 'string' ? day.date : this.formatDate(day.date),
      oldShiftId: shift.shiftId,
      isDayOff: shift.isDayOff
    };
    this.shiftId = shift.shiftId;
    this.changeType(8);
  }

  changeType(hours: number) {
    scheduleList(hours, this.list);
  }

  async onConfirmResult(event: any) {
    if (!this.shiftId) return this.Onalert("Bạn chưa chọn ca!", false);

    this.isconfirm = false;

    if (event == true) {
      this.statusSchedule = sessionStorage.getItem("statusSchedule");
      this.isloading = true;
      const payload = {
        employeeId: this.selectedShiftData.employeeId,
        date: this.selectedShiftData.date,
        shiftId: this.shiftId,
        isDayOff: this.shiftId >= 53
      };

      let res: { data: string, status: number } = { data: "", status: 0 };

      if (this.statusSchedule == "draft") {
        res = await ChangeScheduleDraftManager(payload) as { data: string, status: number };
      } else if (this.statusSchedule == "office") {
        res = await ChangeScheduleOfficeManager(payload) as { data: string, status: number };
      } else {
        // Fallback cho preview nếu không có status
        res = await ChangeScheduleDraftManager(payload) as { data: string, status: number };
      }

      if (res.status == 200) {
        this.isloading = false;
        this.selectedShiftData = null;
        this.schedule.loadData();
        setTimeout(() => this.cdr.detectChanges(), 2000);
        this.Onalert(res.data, true);
        return;
      }

      this.isloading = false;
      setTimeout(() => this.cdr.detectChanges(), 2000);
      this.Onalert(res.data, false);
      this.selectedShiftData = null;
      this.monthYearChange.emit({ month: this.selectedMonth, year: this.selectedYear });
    }
  }

  saveShift() {
    this.isconfirm = true;
    this.confirmMessage = "Bạn có chắc muốn thay đổi thông tin ca làm này ?";
  }
}