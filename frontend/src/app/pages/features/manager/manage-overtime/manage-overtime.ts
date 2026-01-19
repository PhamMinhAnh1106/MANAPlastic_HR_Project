import { ChangeDetectorRef, Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
// Lưu ý: Đảm bảo các đường dẫn import này chính xác trong dự án của bạn
import { Alert } from '../../../shared/alert/alert';
import { Comfirm } from '../../../shared/comfirm/comfirm';
import { Loading } from '../../../shared/loading/loading';
import { ApproveOverTimeRequest, ApproveOverTimeRequestI, CreateOverTimeRequest, CreateOverTimeRequestI, GetOverTimeRequest, OverTimeRequest, RejectOverTimeRequest, ScanDailyOt } from '../../../../services/pages/features/manager/manageOT.service';
import { Department } from '../../../../interface/user/user.interface';
import { findUserbyUsername } from '../../../../utils/finduser.utils';
import { getOvertime } from '../../../../services/pages/features/admin/legal.service';

@Component({
  selector: 'app-manage-overtime',
  standalone: true,
  imports: [CommonModule, FormsModule, Alert, Comfirm, Loading],
  templateUrl: './manage-overtime.html',
  styleUrls: ['./manage-overtime.scss']
})
export class OtManager implements OnInit, OnDestroy {
  constructor(private cdr: ChangeDetectorRef, private cookie: CookieService) { }

  // State Data
  requests: OverTimeRequest[] = [];
  selectedRequest: OverTimeRequest | null = null;
  isCreateOpen = false;
  currentRole: string = '';

  // Filter State
  filters = {
    userName: '',
    departmentId: '',
    status: '',
    fromDate: '',
    toDate: ''
  };

  // --- USER SEARCH STATE ---
  searchSubject = new Subject<string>();
  searchSubscription: Subscription | undefined;
  userSearchResults: any[] = [];
  showUserSearchDropdown: boolean = false;

  // Date Range Popup State
  isDatePopupOpen: boolean = false;
  tempFromDate: string = '';
  tempToDate: string = '';

  // List phòng ban
  departments = Department;

  // Scan Modal State
  isScanOpen: boolean = false;
  scanDate: string = new Date().toISOString().split('T')[0];

  // Review Modal State
  isReviewOpen: boolean = false;
  reviewType: 'approve' | 'reject' | null = null;

  // Data model cho Approve (Đã cập nhật: tách date/time)
  approveData: any = {
    note: '',
    details: []
  };

  // Data model cho Reject
  rejectReason: string = '';

  // Form Model Create
  newRequest: CreateOverTimeRequestI = {
    date: new Date().toISOString().split('T')[0],
    startTime: '00:00',
    endTime: '00:30',
    reason: '',
    overtimetypeid: 1
  };

  // UI States
  isloading: boolean = false;
  isconfirm: boolean = false;
  isalert: boolean = false;
  confirmMessage: string = '';
  alertmessage: string = '';
  alertType: boolean = true;

  isManager: boolean = false;

  overtimeTypes: { otName: string, id: number }[] = [];

  async getovertimetype() {
    try {
      const data = await getOvertime();
      if (Array.isArray(data)) {
        this.overtimeTypes = data.map((ot: any) => ({
          otName: ot.otName,
          id: ot.id
        }));
      }

    } catch (error) {
      console.error(error);
    } finally {
      this.cdr.detectChanges();
    }
  }

  ngOnInit() {
    this.getovertimetype()

    this.currentRole = this.cookie.get('role').toLowerCase();
    // Logic hiển thị nút duyệt hoặc các tính năng dành riêng cho Manager
    if (this.currentRole == 'manager') this.isManager = true;

    this.searchSubscription = this.searchSubject.pipe(
      debounceTime(400),
      distinctUntilChanged()
    ).subscribe(searchText => {
      this.performUserSearch(searchText);
    });

    this.loadData();
  }

  ngOnDestroy() {
    if (this.searchSubscription) {
      this.searchSubscription.unsubscribe();
    }
  }

  showAlert(message: string, isSuccess: boolean = true) {
    this.alertmessage = message;
    this.alertType = isSuccess;
    this.isalert = true;
    this.cdr.detectChanges();

    setTimeout(() => {
      this.isalert = false;
      this.cdr.detectChanges();
    }, 3000);
  }

  // --- USER SEARCH HANDLERS ---

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
    this.filters.userName = user.username || user.fullName || '';
    this.showUserSearchDropdown = false;
  }

  closeSearchDropdown() {
    setTimeout(() => {
      this.showUserSearchDropdown = false;
      this.cdr.detectChanges();
    }, 200);
  }

  // --- API CALLS ---

  async loadData() {
    this.isloading = true;
    this.cdr.detectChanges();
    try {
      const params = new URLSearchParams();
      if (this.filters.userName.trim()) params.append('keyword', this.filters.userName.trim());
      if (this.filters.departmentId) params.append('departmentId', this.filters.departmentId);
      if (this.filters.status) params.append('status', this.filters.status);
      if (this.filters.fromDate) params.append('fromDate', this.filters.fromDate);
      if (this.filters.toDate) params.append('toDate', this.filters.toDate);

      const queryString = params.toString();
      const data = await GetOverTimeRequest(queryString);

      if (typeof data === 'string' && data.includes('co loi')) {
        throw new Error(data);
      }
      this.requests = Array.isArray(data.content) ? data.content : [];
    } catch (e: any) {
      console.error(e);
      this.requests = [];
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  resetFilters() {
    this.filters = {
      userName: '',
      departmentId: '',
      status: '',
      fromDate: '',
      toDate: ''
    };
    this.tempFromDate = '';
    this.tempToDate = '';
    this.loadData();
  }

  // --- DATE RANGE POPUP HANDLERS ---
  toggleDatePopup() {
    if (!this.isDatePopupOpen) {
      this.tempFromDate = this.filters.fromDate;
      this.tempToDate = this.filters.toDate;
    }
    this.isDatePopupOpen = !this.isDatePopupOpen;
  }

  applyDateRange() {
    this.filters.fromDate = this.tempFromDate;
    this.filters.toDate = this.tempToDate;
    this.isDatePopupOpen = false;
    this.loadData();
  }

  get dateRangeDisplay(): string {
    if (this.filters.fromDate && this.filters.toDate) {
      return `${this.formatDate(this.filters.fromDate)} - ${this.formatDate(this.filters.toDate)}`;
    } else if (this.filters.fromDate) {
      return `Từ ${this.formatDate(this.filters.fromDate)}`;
    } else if (this.filters.toDate) {
      return `Đến ${this.formatDate(this.filters.toDate)}`;
    }
    return 'thời gian';
  }

  private formatDate(dateStr: string): string {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return `${date.getDate()}/${date.getMonth() + 1}`;
  }

  // --- TIME CALCULATION HANDLER (AUTO UPDATE HOURS) ---
  // Hàm này giờ đây sẽ xử lý tách Date và Time
  calculateDuration(item: any) {
    if (!item.startDateStr || !item.startTimeStr || !item.endDateStr || !item.endTimeStr) return;

    // Ghép ngày + giờ lại để tính toán
    const startStr = `${item.startDateStr}T${item.startTimeStr}`;
    const endStr = `${item.endDateStr}T${item.endTimeStr}`;

    const start = new Date(startStr);
    const end = new Date(endStr);

    // Tính toán diff bằng milliseconds
    const diffMs = end.getTime() - start.getTime();

    if (diffMs <= 0) {
      item.hours = 0;
      return;
    }

    const diffHrs = diffMs / (1000 * 60 * 60);
    // Làm tròn 2 chữ số thập phân
    item.hours = parseFloat(diffHrs.toFixed(2));
  }

  // --- SCAN ACTIONS ---

  openScanModal() {
    this.scanDate = new Date().toISOString().split('T')[0];
    this.isScanOpen = true;
    this.cdr.detectChanges();
  }

  async submitScan() {
    this.isloading = true;
    this.cdr.detectChanges();
    try {
      const res: any = await ScanDailyOt(this.scanDate);
      if (res && res.status === 200) {
        this.showAlert('Cập nhật dữ liệu thành công!', true);
        this.isScanOpen = false;
        await this.loadData();
      } else {
        const msg = res?.data?.message || res?.message || 'Lỗi không xác định';
        this.showAlert('Lỗi cập nhật: ' + msg, false);
      }
    } catch (e: any) {
      this.showAlert('Lỗi hệ thống: ' + e, false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  // --- CREATE ACTIONS ---

  async submitCreate() {
    this.isloading = true;
    this.cdr.detectChanges();
    try {
      const res: any = await CreateOverTimeRequest(this.newRequest);
      if (res && res.status === 200) {
        this.showAlert('Tạo đơn thành công!', true);
        this.isCreateOpen = false;
        this.newRequest.reason = '';
        await this.loadData();
      } else {
        this.showAlert('Lỗi tạo đơn: ' + (res?.message || 'Unknown error'), false);
      }
    } catch (e: any) {
      this.showAlert('Lỗi hệ thống: ' + e, false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  // --- REVIEW ACTION HANDLERS (Approve/Reject) ---

  // Helper tách ngày và giờ từ chuỗi ISO
  private splitDateTime(dateStr: string, defaultDate: string) {
    try {
      // Input: "2026-01-20T20:00:00" hoặc "20:00" (nếu lỗi data)
      let fullDate = dateStr;

      // Nếu chỉ có giờ, ghép với defaultDate
      if (dateStr.length <= 8 && !dateStr.includes('T')) {
        fullDate = `${defaultDate}T${dateStr}`;
      } else if (!dateStr.includes('T') && dateStr.length === 10) {
        // Chỉ có ngày
        fullDate = `${dateStr}T00:00:00`;
      }

      // Xử lý an toàn nếu chuỗi vẫn không đúng
      if (!fullDate.includes('T')) {
        fullDate = new Date().toISOString();
      }

      const parts = fullDate.split('T');
      const datePart = parts[0]; // YYYY-MM-DD
      const timePart = parts[1].substring(0, 5); // HH:mm

      return { date: datePart, time: timePart };
    } catch (e) {
      return { date: defaultDate.split('T')[0], time: '00:00' };
    }
  }

  openReviewAction(type: 'approve' | 'reject') {
    if (!this.selectedRequest) return;

    this.reviewType = type;

    if (type === 'approve') {
      const baseDate = this.selectedRequest.date ? this.selectedRequest.date.split('T')[0] : new Date().toISOString().split('T')[0];

      const details = this.selectedRequest.details.map((d: any) => {
        const startSplit = this.splitDateTime(d.startTime, baseDate);
        const endSplit = this.splitDateTime(d.endTime, baseDate);

        return {
          id: d.id || null,
          overtimeTypeID: d.overtimeTypeID || d.overtimeTypeId || 1,

          // Tách riêng các trường để bind vào 2 input
          startDateStr: startSplit.date,
          startTimeStr: startSplit.time,

          endDateStr: endSplit.date,
          endTimeStr: endSplit.time,

          hours: d.hours
        };
      });

      this.approveData = {
        note: '',
        details: details
      };
    } else {
      this.rejectReason = '';
    }

    this.isReviewOpen = true;
    this.cdr.detectChanges();
  }

  async submitReview() {
    if (!this.selectedRequest || !this.reviewType) return;

    this.isloading = true;
    this.cdr.detectChanges();

    try {
      let res: any;

      if (this.reviewType === 'approve') {
        // Gom lại ngày + giờ thành chuỗi ISO để gửi API
        const payloadDetails = this.approveData.details.map((d: any) => {
          const startFull = `${d.startDateStr}T${d.startTimeStr}:00`;
          const endFull = `${d.endDateStr}T${d.endTimeStr}:00`;

          return {
            id: d.id,
            overtimeTypeID: d.overtimeTypeID,
            startTime: startFull,
            endTime: endFull,
            hours: d.hours
          };
        });

        const finalPayload: ApproveOverTimeRequestI = {
          note: this.approveData.note,
          details: payloadDetails
        };

        res = await ApproveOverTimeRequest(
          finalPayload,
          this.selectedRequest.requestId,
          this.currentRole
        );
      } else {
        res = await RejectOverTimeRequest(
          this.selectedRequest.requestId,
          { reason: this.rejectReason }
        );
      }

      if (res && res.status === 200) {
        this.showAlert(res.data || 'Thao tác thành công', true);

        if (this.reviewType === 'approve') {
          this.selectedRequest.status = 'APPROVED';
          const totalPaid = this.approveData.details.reduce((sum: number, d: any) => sum + Number(d.hours), 0);
          this.selectedRequest.finalPaidHours = totalPaid;
        } else {
          this.selectedRequest.status = 'REJECTED';
        }

        this.isReviewOpen = false;
        this.closeDetail();
        await this.loadData();
      } else {
        const errorMsg = res.data?.response?.data?.message || res.data || 'Có lỗi xảy ra';
        this.showAlert(errorMsg, false);
      }
    } catch (e: any) {
      this.showAlert('Lỗi hệ thống: ' + e, false);
      console.log("Error:", e);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  // --- UI HELPERS ---

  getStatusClass(status: string): string {
    const s = status ? status.toUpperCase() : '';
    if (s.includes('APPROVED')) return 'status-approved';
    if (s.includes('REJECTED') || s.includes('CANCEL')) return 'status-rejected';
    if (s.includes('PENDING')) return 'status-pending';
    return 'status-default';
  }

  canApprove(): boolean {
    if (!this.selectedRequest) return false;
    const status = this.selectedRequest.status ? this.selectedRequest.status.toUpperCase() : '';

    // Logic mới: HR chỉ được duyệt đơn ở trạng thái PENDING_HR
    if (this.currentRole === 'hr') {
      return status === 'PENDING_HR';
    }



    return false;
  }

  viewDetail(req: OverTimeRequest) {
    this.selectedRequest = req;
    this.cdr.detectChanges();
  }

  closeDetail() {
    this.selectedRequest = null;
    this.cdr.detectChanges();
  }

  openCreateModal() {
    this.isCreateOpen = true;
    this.cdr.detectChanges();
  }

  onConfirmResult(result: boolean) {
    this.isconfirm = false;
  }
}