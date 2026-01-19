import { CommonModule, NgFor, NgIf, DatePipe } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

// --- Imports Shared Components ---
import { Loading } from '../../../shared/loading/loading';
import { Alert } from '../../../shared/alert/alert';
import { Comfirm } from '../../../shared/comfirm/comfirm';
import { Reschedule } from '../../../shared/reschedule/reschedule';

// --- Imports Services & Utils ---
import {
  GetScheduleEmployeeDraft,
  GetScheduleEmployeeoffice,
  GetScheduleManagerdraft,
  GetScheduleManageroffice,
  UpSchedule,
  ChangeScheduleDraftManager,
  getReschedule,
  ApproveReschedule,
  RejectReschedule,
  RescheduleAPI,
  AddNewEmployee,
} from '../../../../services/pages/features/employee/shedule.services';
import { ShiftChangeRequest } from '../../../../interface/schedule.interface';
import { getAllSchedules, scheduleList } from '../../../../utils/listSchedule.utils';
import { findUserbyUsername } from '../../../../utils/finduser.utils';


// --- Interface Definitions ---
export interface userSchedule { employeeId: number; employeeFullName: string; drafts?: any[]; }
interface DayObj {
  date: Date;
  inMonth: boolean;
  isDayOff: boolean;
  shiftName?: string;
  shifts?: any[];
}
interface GroupedRequest {
  employeeName: string;
  departmentName?: string;
  items: ShiftChangeRequest[];
}

// Interface cho ngày ngoại lệ (theo ngày cụ thể)
export interface SpecificDateShift {
  date: string;    // yyyy-MM-dd
  shiftId: number;
}

// Interface form mới
export interface NewEmployeeFormInterface {
  username: string;
  startDate: string;
  endDate?: string; // Thêm ngày kết thúc
  shiftId: number;  // Ca chính
  specificDays: SpecificDateShift[]; // Danh sách các ngày khác ca
}

@Component({
  selector: 'app-schedule',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIf, NgFor, DatePipe, Loading, Alert, Comfirm, Reschedule],
  templateUrl: './schedule.html',
  styleUrl: './schedule.scss',
})
export class Schedule implements OnInit {

  // --- General Properties ---
  role: string = '';
  selectStatus: string = '';
  statusSchedule: string | null = '';

  // --- Calendar State ---
  selectedMonth: number = new Date().getMonth() + 1;
  selectedYear: number = new Date().getFullYear();

  weeks: any[] = [];
  dayData: any[] = [];
  dayShiftMap: { [date: string]: any[] } = {};

  // --- UI/Modal States ---
  isloading: boolean = false;
  isconfirm: boolean = false;
  isalert: boolean = false;
  confirmMessage: string = '';
  alertmessage: string = '';
  alertType: boolean = true;

  showOperationsMenu: boolean = false;

  // Reschedule Modal
  isShowRescheduleModal: boolean = false;
  selectedDate: string = '';

  // Day Details Popup
  showDayDetailsPopup: boolean = false;
  selectedDayForDetails: any = null;

  // Edit Shift Popup (Manager)
  selectedShiftData: any = null;
  shiftId: number | null = null;
  selectedShiftType: number = 8; // Mặc định là 8H
  reason: string = '';
  list: any[] = [];

  // --- ADD EMPLOYEE POPUP STATE ---
  showAddEmployeePopup: boolean = false;
  shiftListForAdd: any[] = [];

  // Form Model
  newEmployeeForm: NewEmployeeFormInterface = {
    username: '',
    startDate: '',
    endDate: '',
    shiftId: 0,
    specificDays: []
  };

  // --- SEARCH USER LOGIC ---
  searchSubject = new Subject<string>(); // Subject để handle debounce
  userSearchResults: any[] = []; // Chứa kết quả tìm kiếm
  showUserSearchDropdown: boolean = false; // Toggle dropdown

  // Notification / Approval Modal
  isNotifiOpen: boolean = false;
  countnotifi: number = 0;
  allRequests: ShiftChangeRequest[] = [];
  displayGroupedRequests: GroupedRequest[] = [];
  currentTab: 'PENDING' | 'APPROVED' | 'REJECTED' = 'PENDING';

  constructor(
    private cdr: ChangeDetectorRef,
    private cookie: CookieService,
    private router: Router
  ) { }

  ngOnInit() {
    const roleCookie = this.cookie.get("role");
    this.role = roleCookie ? roleCookie.toLowerCase() : 'manager';

    this.shiftListForAdd = getAllSchedules();

    this.prepareDayShiftMap();
    this.generateCalendar();

    if (this.role === 'manager' || this.role === 'employee') {
      this.getNotification();
    }

    // Setup debounce cho search input
    this.searchSubject.pipe(
      debounceTime(300), // Đợi 1s sau lần nhập cuối cùng
      distinctUntilChanged() // Chỉ gọi nếu giá trị thay đổi
    ).subscribe(searchTerm => {
      this.performUserSearch(searchTerm);
    });
  }

  // ... (Giữ nguyên các hàm calendar logic) ...
  toggleOperationsMenu() { this.showOperationsMenu = !this.showOperationsMenu; }
  onMonthYearChange() { this.generateCalendar(); }

  // ... (Giữ nguyên loadData, prepareDayShiftMap, generateCalendar, onDayClick, popup logic cũ) ...
  async loadData() {
    this.isloading = true;
    const monthStr = String(this.selectedMonth).padStart(2, '0');
    const yearMonth = `${this.selectedYear}-${monthStr}`;
    let res: any[] = [];
    try {
      switch (this.selectStatus) {
        case '0': res = await GetScheduleEmployeeDraft(yearMonth); break;
        case '1': res = await GetScheduleEmployeeoffice(yearMonth); break;
        case '2':
          res = await GetScheduleManagerdraft(yearMonth);
          this.statusSchedule = "draft";
          sessionStorage.setItem("statusSchedule", this.statusSchedule);
          break;
        case '3':
          res = await GetScheduleManageroffice(yearMonth);
          this.statusSchedule = "office";
          sessionStorage.setItem("statusSchedule", this.statusSchedule);
          break;
        default: res = []; break;
      }
      this.dayData = res || [];
      this.prepareDayShiftMap();
      this.generateCalendar();
    } catch (error) {
      console.error(error);
      this.Onalert("Có lỗi xảy ra khi tải dữ liệu", false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  // === CẬP NHẬT LOGIC TẠI ĐÂY ===
  prepareDayShiftMap() {
    this.dayShiftMap = {};
    if (!this.dayData || this.dayData.length === 0) return;

    if (this.role === 'employee') {
      this.dayData.forEach(d => {
        const date = d.date;
        // Kiểm tra nếu shiftId >= 53 (AL, SL...) HOẶC có property dayOff = true thì ẩn
        const isDayOff = d.isDayOff || d.dayOff || (d.shiftId >= 53);

        this.dayShiftMap[date] = [{
          shiftName: d.shiftName,
          shiftId: d.shiftId,
          isDayOff: isDayOff
        }];
      });
    } else if (this.role === 'manager') {
      this.dayData.forEach((emp: userSchedule) => {
        if (!emp.drafts) return;
        emp.drafts.forEach(shift => {
          const date = shift.date;
          if (!this.dayShiftMap[date]) this.dayShiftMap[date] = [];

          // Kiểm tra tương tự cho Manager (thêm check shift.dayOff)
          const isDayOff = shift.isDayOff || shift.dayOff || (shift.shiftId >= 53);

          this.dayShiftMap[date].push({
            employeeId: emp.employeeId,
            employeeFullName: emp.employeeFullName,
            shiftName: shift.shiftName || 'OFF',
            shiftId: shift.shiftId,
            isDayOff: isDayOff
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
    const dayOfWeek = firstDay.getDay();
    const diff = dayOfWeek === 0 ? 6 : dayOfWeek - 1;
    startDate.setDate(firstDay.getDate() - diff);
    let current = new Date(startDate);
    let safetyCount = 0;
    while ((current <= lastDay || this.weeks.length < 6) && safetyCount < 42) {
      let week = [];
      for (let i = 0; i < 7; i++) {
        const dateStr = this.formatDate(current);
        const dayObj: DayObj = {
          date: new Date(current),
          inMonth: current.getMonth() === firstDay.getMonth(),
          isDayOff: false,
          shifts: this.dayShiftMap[dateStr] || []
        };
        // Logic cũ: lấy trạng thái ngày nghỉ của ca đầu tiên
        if (dayObj.shifts && dayObj.shifts.length > 0) {
          dayObj.isDayOff = dayObj.shifts[0].isDayOff;
        }
        week.push(dayObj);
        current.setDate(current.getDate() + 1);
      }
      this.weeks.push(week);
      if (current.getMonth() !== firstDay.getMonth() && current.getDate() > 7 && this.weeks.length >= 5) break;
      safetyCount++;
    }
  }

  onDayClick(day: any) {
    if (!day.inMonth) return;
    const dateStr = this.formatDate(day.date);
    if (this.role === 'employee' || this.selectStatus === '1' || this.selectStatus === '3') {
      this.selectedDate = dateStr;
      this.isShowRescheduleModal = true;
    }
  }

  openDayDetailsPopup(day: any) {
    const dateObj = new Date(day.date);
    const dayStr = ('0' + dateObj.getDate()).slice(-2);
    const monthStr = ('0' + (dateObj.getMonth() + 1)).slice(-2);
    this.selectedDayForDetails = { ...day, dateStr: `${dayStr}/${monthStr}/${dateObj.getFullYear()}` };
    this.showDayDetailsPopup = true;
  }
  closeDayDetailsPopup() { this.showDayDetailsPopup = false; this.selectedDayForDetails = null; }
  onDetailShiftClick(shift: any) {
    if (this.role === 'manager') {
      this.openEditShift(this.selectedDayForDetails, shift);
      this.closeDayDetailsPopup();
    }
  }

  openEditShift(day: any, shift: any) {
    this.selectedShiftData = {
      employeeId: shift.employeeId,
      employeeFullName: shift.employeeFullName,
      date: typeof day.date === 'string' ? day.date : this.formatDate(day.date),
      oldShiftId: shift.shiftId,
      isDayOff: shift.isDayOff
    };

    const currentShiftId = shift.shiftId;

    // --- LOGIC MỚI: Tự động phát hiện loại ca (8H/6H) ---

    // 1. Nếu là ca nghỉ phép (>=53) -> Reset form, không chọn ca nào
    if (currentShiftId >= 53) {
      this.shiftId = null;
      this.selectedShiftType = 8; // Mặc định về 8H
      this.changeType(8);
      return;
    }

    // 2. Nếu là ca thường, kiểm tra xem nó thuộc 8H hay 6H
    this.list = [];
    scheduleList(8, this.list);
    const foundIn8H = this.list.some(s => s.shift_id == currentShiftId);

    if (foundIn8H) {
      this.selectedShiftType = 8;
      this.shiftId = currentShiftId;
    } else {
      this.list = [];
      scheduleList(6, this.list);
      const foundIn6H = this.list.some(s => s.shift_id == currentShiftId);

      if (foundIn6H) {
        this.selectedShiftType = 6;
        this.shiftId = currentShiftId;
      } else {
        this.selectedShiftType = 8;
        this.changeType(8);
        this.shiftId = currentShiftId;
      }
    }
  }

  changeType(hours: number) {
    this.selectedShiftType = hours;
    this.list = []; // Clear list cũ trước khi load
    scheduleList(hours, this.list);
  }

  async saveShift() {
    this.isconfirm = true;
    this.confirmMessage = "Bạn có chắc muốn thay đổi thông tin ca làm này ?";
    this.actionType = 'EDIT_SHIFT';
  }

  autoScheduleRoute() { this.router.navigate(["home/schedule/auto"]); }
  registerShiftRoute() { this.router.navigate(["/home/schedule/register"]); }

  upOfficeSchedule() {
    this.isconfirm = true;
    this.confirmMessage = "Bạn có chắc muốn duyệt lịch này ?";
    this.actionType = 'UPOFFICE';
  }

  actionType: string = '';
  async onConfirmResult(event: any) {
    this.isconfirm = false;
    if (event !== true) return;
    this.isloading = true;

    if (this.actionType === 'UPOFFICE') {
      const mStr = String(this.selectedMonth).padStart(2, '0');
      const param = `${this.selectedYear}-${mStr}`;
      try {
        const res = await UpSchedule(param) as { data: string, status: number };
        if (res.status === 200) this.Onalert(res.data, true);
        else this.Onalert(res.data, false);
      } catch (e) { this.Onalert("Lỗi duyệt lịch", false); }
      this.actionType = '';
    } else {
      if (!this.shiftId) { this.isloading = false; return this.Onalert("Bạn chưa chọn ca!", false); }
      const payload = {
        employeeId: this.selectedShiftData.employeeId,
        date: this.selectedShiftData.date,
        shiftId: this.shiftId,
        isDayOff: this.shiftId >= 53
      };
      const reschedulePayload = {
        targetDate: this.selectedShiftData.date,
        newShiftId: this.shiftId,
        reason: this.reason
      };
      try {
        let res: { data: any, status: number } = { data: "", status: 0 };
        if (this.statusSchedule == "draft") res = await ChangeScheduleDraftManager(payload) as any;
        else if (this.statusSchedule == "office") res = await RescheduleAPI(reschedulePayload) as any;
        else res = await ChangeScheduleDraftManager(payload) as any;

        if (res.status == 200) {
          this.Onalert(typeof res.data === 'string' ? res.data : "Thành công", true);
          this.selectedShiftData = null;
          this.loadData();
        } else this.Onalert(res.data?.message || "Thất bại", false);
      } catch (e) { this.Onalert("Lỗi API", false); }
    }
    this.isloading = false;
    this.cdr.detectChanges();
  }

  // ==========================================
  // 4. ADD NEW EMPLOYEE LOGIC (UPDATED RANGE + SEARCH)
  // ==========================================

  // -- Search User Functions --
  onSearchUser(event: any) {
    const value = event.target.value;
    if (!value || value.trim() === '') {
      this.showUserSearchDropdown = false;
      this.userSearchResults = [];
      return;
    }
    this.searchSubject.next(value);
  }

  async performUserSearch(username: string) {
    try {
      const res = await findUserbyUsername(username) as any[];
      if (res && Array.isArray(res)) {
        this.userSearchResults = res.slice(0, 5);
        this.showUserSearchDropdown = this.userSearchResults.length > 0;
      } else {
        this.userSearchResults = [];
        this.showUserSearchDropdown = false;
      }
    } catch (error) {
      console.error(error);
      this.userSearchResults = [];
      this.showUserSearchDropdown = false;
    }
    this.cdr.detectChanges();
  }

  selectSearchUser(user: any) {
    this.newEmployeeForm.username = user.username || user.fullName || '';
    this.showUserSearchDropdown = false;
  }

  openAddEmployeePopup() {
    this.showAddEmployeePopup = true;
    this.newEmployeeForm = {
      username: '',
      startDate: '',
      endDate: '',
      shiftId: 0,
      specificDays: []
    };
    this.userSearchResults = [];
    this.showUserSearchDropdown = false;
  }

  closeAddEmployeePopup() {
    this.showAddEmployeePopup = false;
  }

  addSpecificDay() {
    this.newEmployeeForm.specificDays.push({ date: '', shiftId: 0 });
  }

  removeSpecificDay(index: number) {
    this.newEmployeeForm.specificDays.splice(index, 1);
  }

  async submitNewEmployee() {
    if (!this.newEmployeeForm.username || !this.newEmployeeForm.startDate || !this.newEmployeeForm.endDate || !this.newEmployeeForm.shiftId) {
      this.Onalert("Vui lòng điền tên, ngày bắt đầu, ngày kết thúc và ca chính!", false);
      return;
    }

    if (new Date(this.newEmployeeForm.startDate) > new Date(this.newEmployeeForm.endDate)) {
      this.Onalert("Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc!", false);
      return;
    }

    const invalidSpecificDay = this.newEmployeeForm.specificDays.find(d => !d.date || !d.shiftId);
    if (invalidSpecificDay) {
      this.Onalert("Vui lòng điền đầy đủ thông tin cho các ngày ngoại lệ!", false);
      return;
    }

    this.isloading = true;

    try {
      const payload: any = {
        username: this.newEmployeeForm.username,
        startDate: this.newEmployeeForm.startDate,
        endDate: this.newEmployeeForm.endDate,
        shiftId: Number(this.newEmployeeForm.shiftId),
        specificDays: this.newEmployeeForm.specificDays.map(d => ({
          date: d.date,
          shiftId: Number(d.shiftId),
          dayoff: Number(d.shiftId) >= 53
        }))
      };

      const res = await AddNewEmployee(payload) as any;

      if (res.status === 200) {
        this.Onalert("Thêm nhân viên và lịch thành công!", true);
        this.closeAddEmployeePopup();
        this.loadData();
      } else {
        this.Onalert(res.response?.data?.message || res.message || "Lỗi thêm mới", false);
      }
    } catch (e) {
      console.error(e);
      this.Onalert("Lỗi kết nối server!", false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  // ... (Giữ nguyên Notification utils) ...
  toggleNotifiModal() {
    this.isNotifiOpen = !this.isNotifiOpen;
    if (this.isNotifiOpen) this.getNotification();
  }
  closeNotifiModal() { this.isNotifiOpen = false; }
  setTab(tab: 'PENDING' | 'APPROVED' | 'REJECTED') {
    this.currentTab = tab;
    this.processDisplayData();
  }
  async getNotification() {
    const res = await getReschedule('') as any[];
    if (res && Array.isArray(res)) {
      this.allRequests = res;
      this.countnotifi = this.allRequests.filter(req => req.status === 'PENDING').length;
      this.processDisplayData();
      this.cdr.detectChanges();
    }
  }
  processDisplayData() {
    const filtered = this.allRequests.filter(req => req.status === this.currentTab);
    const groups: { [key: string]: GroupedRequest } = {};
    filtered.forEach(req => {
      const key = req.employeeName;
      if (!groups[key]) groups[key] = { employeeName: req.employeeName, departmentName: req.departmentName, items: [] };
      groups[key].items.push(req);
    });
    this.displayGroupedRequests = Object.values(groups);
  }
  async onApproveReschedule(id: number, event: Event) {
    event.stopPropagation();
    this.isloading = true;
    try {
      await ApproveReschedule(id);
      this.Onalert("Đã duyệt đơn", true);
      await this.getNotification();
      this.loadData();
    } catch (e) { this.Onalert("Lỗi duyệt đơn", false); }
    finally { this.isloading = false; this.cdr.detectChanges(); }
  }
  async onRejectReschedule(id: number, event: Event) {
    event.stopPropagation();
    this.isloading = true;
    try {
      await RejectReschedule(id);
      this.Onalert("Đã từ chối đơn", true);
      await this.getNotification();
    } catch (e) { this.Onalert("Lỗi từ chối", false); }
    finally { this.isloading = false; this.cdr.detectChanges(); }
  }

  Onalert(message: string, type: boolean) {
    this.isalert = true;
    this.alertmessage = message;
    this.alertType = type;
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
}