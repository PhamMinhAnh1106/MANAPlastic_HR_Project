import { NgFor, NgIf, DatePipe, CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, OnInit, Output, HostListener, ElementRef, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';
import { scheduleList } from '../../../utils/listSchedule.utils';
import { Loading } from '../loading/loading';
import { Alert } from '../alert/alert';
import { Comfirm } from '../comfirm/comfirm';
import {
  ChangeScheduleDraftManager,
  getReschedule,
  ApproveReschedule,
  RejectReschedule,
  RescheduleAPI
} from '../../../services/pages/features/employee/shedule.services';
import { ShiftChangeRequest } from '../../../interface/schedule.interface';

// --- ĐỊNH NGHĨA TẠM THỜI (Giữ nguyên) ---
export interface userSchedule { employeeId: number; employeeFullName: string; drafts?: any[]; }
export class Schedule { loadData() { console.log('Reload data'); } }

interface DayObj {
  date: Date;
  inMonth: boolean;
  isDayOff: boolean;
  shiftName?: string;
  shifts?: any[];
}

// Interface cho cấu trúc gom nhóm
interface GroupedRequest {
  employeeName: string;
  departmentName?: string;
  items: ShiftChangeRequest[];
}

@Component({
  selector: 'app-tablemonth',
  standalone: true,
  imports: [NgFor, FormsModule, NgIf, Loading, Alert, Comfirm, DatePipe, CommonModule],
  templateUrl: './tablemonth.html',
  styleUrl: './tablemonth.scss',
  providers: [Schedule]
})
export class Tablemonth implements OnInit, OnChanges {
  // --- Output Events ---
  @Output() statusChange = new EventEmitter<string>();
  @Output() upOffice = new EventEmitter<void>();
  @Output() autoSchedule = new EventEmitter<void>();
  @Output() register = new EventEmitter<void>();

  selectStatus: string = '';

  showDayDetailsPopup: boolean = false;
  selectedDayForDetails: any = null;

  // --- Notification & Modal State ---
  isNotifiOpen: boolean = false;
  countnotifi: number = 0; // Chỉ đếm pending

  // Dữ liệu gốc
  allRequests: ShiftChangeRequest[] = [];

  // Tab State
  currentTab: 'PENDING' | 'APPROVED' | 'REJECTED' = 'PENDING';

  // Dữ liệu đã lọc và gom nhóm để hiển thị
  displayGroupedRequests: GroupedRequest[] = [];

  constructor(
    private cdr: ChangeDetectorRef,
    private cookie: CookieService,
    private schedule: Schedule
  ) { }

  // --- Notification Logic ---
  toggleNotifiModal() {
    this.isNotifiOpen = !this.isNotifiOpen;
    if (this.isNotifiOpen) {
      this.getNotification(); // Refresh data khi mở
    }
  }

  closeNotifiModal() {
    this.isNotifiOpen = false;
  }

  setTab(tab: 'PENDING' | 'APPROVED' | 'REJECTED') {
    this.currentTab = tab;
    this.processDisplayData();
  }

  async getNotification() {
    // Gọi API lấy toàn bộ danh sách
    const res = await getReschedule('') as any[];
    if (res && Array.isArray(res)) {
      this.allRequests = res;

      // Đếm số lượng Pending để hiển thị badge
      this.countnotifi = this.allRequests.filter(req => req.status === 'PENDING').length;

      // Xử lý dữ liệu hiển thị theo Tab hiện tại
      this.processDisplayData();

      this.cdr.detectChanges();
    }
  }

  // Hàm xử lý gom nhóm dữ liệu
  processDisplayData() {
    // 1. Lọc theo Tab hiện tại
    const filtered = this.allRequests.filter(req => req.status === this.currentTab);

    // 2. Gom nhóm theo nhân viên
    const groups: { [key: string]: GroupedRequest } = {};

    filtered.forEach(req => {
      const key = req.employeeName; // Hoặc dùng employeeId nếu muốn chính xác hơn
      if (!groups[key]) {
        groups[key] = {
          employeeName: req.employeeName,
          departmentName: req.departmentName,
          items: []
        };
      }
      groups[key].items.push(req);
    });

    // 3. Chuyển object thành array
    this.displayGroupedRequests = Object.values(groups);
  }

  async onApprove(id: number, event: Event) {
    event.stopPropagation();
    this.isloading = true;
    try {
      await ApproveReschedule(id);
      this.Onalert("Đã duyệt đơn đổi ca", true);
      await this.getNotification();     // Reload list & update badge
      this.schedule.loadData();         // Reload calendar
    } catch (e) {
      this.Onalert("Lỗi khi duyệt đơn", false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();

    }
  }

  async onReject(id: number, event: Event) {
    event.stopPropagation();
    this.isloading = true;
    try {
      await RejectReschedule(id);
      this.Onalert("Đã từ chối đơn đổi ca", true);
      await this.getNotification(); // Reload list & update badge
    } catch (e) {
      this.Onalert("Lỗi khi từ chối đơn", false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  // --- Các hàm Calendar cũ giữ nguyên ---
  // ... (Emit Handlers, Calendar Generation, Popups logic không thay đổi)

  emitChooseSchedule() { this.statusChange.emit(this.selectStatus); }
  emitUpOffice() { this.upOffice.emit(); }
  emitAutoSchedule() { this.autoSchedule.emit(); }
  emitRegister() { this.register.emit(); }

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
    this.selectedDayForDetails = { ...day, dateStr: `${dayStr}/${monthStr}/${yearStr}` };
    this.showDayDetailsPopup = true;
  }

  closeDayDetailsPopup() {
    this.showDayDetailsPopup = false;
    this.selectedDayForDetails = null;
  }

  selectedMonth: number = new Date().getMonth() + 1;
  selectedYear: number = new Date().getFullYear();
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
    this.role = this.cookie.get("role")?.toLowerCase() || 'manager';
    this.cdr.detectChanges();
    this.prepareDayShiftMap();
    this.generateCalendar();
    this.emitMonthYear();
    if (this.role === 'manager' || this.role === 'employee') {
      this.getNotification();
    }
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
    return date.getDate() === today.getDate() && date.getMonth() === today.getMonth() && date.getFullYear() === today.getFullYear();
  }
  prepareDayShiftMap() {
    this.dayShiftMap = {};
    if (this.role === 'employee') {
      this.dayData.forEach(d => {
        const date = d.date;
        this.dayShiftMap[date] = [{ shiftName: d.shiftName, shiftId: d.shiftId, isDayOff: d.isDayOff }];
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
    this.selectDay.emit({ date: this.formatDate(day.date), row: rowIndex, col: colIndex, shifts: day.shifts });
  }
  onMonthYearChange() { this.generateCalendar(); this.emitMonthYear(); }
  private emitMonthYear() { this.monthYearChange.emit({ month: this.selectedMonth, year: this.selectedYear }); }
  selectedShiftData: any = null;
  shiftId: number | null = null;
  list: any[] = [];
  openEditShift(day: any, shift: any) {
    this.selectedShiftData = {
      employeeId: shift.employeeId, employeeFullName: shift.employeeFullName, date: typeof day.date === 'string' ? day.date : this.formatDate(day.date), oldShiftId: shift.shiftId, isDayOff: shift.isDayOff
    };
    this.shiftId = shift.shiftId;
    this.changeType(8);
  }


  reason: string = '';
  changeType(hours: number) { scheduleList(hours, this.list); }
  async onConfirmResult(event: any) {
    if (!this.shiftId) return this.Onalert("Bạn chưa chọn ca!", false);
    this.isconfirm = false;
    if (event == true) {
      this.statusSchedule = sessionStorage.getItem("statusSchedule");
      this.isloading = true;
      const payload = { employeeId: this.selectedShiftData.employeeId, date: this.selectedShiftData.date, shiftId: this.shiftId, isDayOff: this.shiftId >= 53 };
      const reschedule = {
        targetDate: this.selectedShiftData.date,
        newShiftId: this.shiftId,
        reason: this.reason
      }
      let res: { data: any, status: number } = { data: "", status: 0 };
      if (this.statusSchedule == "draft") { res = await ChangeScheduleDraftManager(payload) as { data: string, status: number }; }
      else if (this.statusSchedule == "office") { res = await RescheduleAPI(reschedule) as { data: string, status: number }; }
      else { res = await ChangeScheduleDraftManager(payload) as { data: string, status: number }; }
      if (res.status == 200) {
        this.isloading = false; this.selectedShiftData = null; this.schedule.loadData(); setTimeout(() => this.cdr.detectChanges(), 2000); this.Onalert(res.data, true); return;
      }
      this.isloading = false; setTimeout(() => this.cdr.detectChanges(), 2000); this.Onalert(res.data.message, false); this.selectedShiftData = null; this.monthYearChange.emit({ month: this.selectedMonth, year: this.selectedYear });
    }
  }
  saveShift() { this.isconfirm = true; this.confirmMessage = "Bạn có chắc muốn thay đổi thông tin ca làm này ?"; }
}