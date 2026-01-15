import { ChangeDetectorRef, Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';

// Import services
import {
  ApproveOverTimeRequest,
  CreateOverTimeRequest,
  CreateOverTimeRequestI,
  GetOverTimeRequest,
  OverTimeRequest,
  RejectOverTimeRequest,
  ApproveOverTimeRequestI,
  ScanDailyOt
} from '../../../../services/pages/features/manager/manageOT.service';
import { Alert } from '../../../shared/alert/alert';
import { Comfirm } from '../../../shared/comfirm/comfirm';
import { Loading } from '../../../shared/loading/loading';


@Component({
  selector: 'app-manage-overtime',
  standalone: true,
  imports: [CommonModule, FormsModule, Alert, Comfirm, Loading],
  templateUrl: './manage-overtime.html',
  styleUrls: ['./manage-overtime.scss']
})
export class OtManager implements OnInit {
  constructor(private cdr: ChangeDetectorRef, private cookie: CookieService) { }

  // State Data
  requests: OverTimeRequest[] = [];
  selectedRequest: OverTimeRequest | null = null;
  isCreateOpen = false;
  currentRole: string = '';

  // Scan Modal State (Cập nhật dữ liệu)
  isScanOpen: boolean = false;
  scanDate: string = new Date().toISOString().split('T')[0];

  // Review Modal State (Duyệt/Từ chối có nhập liệu)
  isReviewOpen: boolean = false;
  reviewType: 'approve' | 'reject' | null = null;
  reviewData: ApproveOverTimeRequestI = {
    finalPaidHours: '0',
    note: ''
  };

  // Form Model Create
  newRequest: CreateOverTimeRequestI = {
    date: new Date().toISOString().split('T')[0],
    startTime: '17:30',
    endTime: '18:30',
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

  ngOnInit() {
    const roleCookie = this.cookie.get('role');
    this.currentRole = roleCookie ? roleCookie.toLowerCase() : '';
    this.loadData();
  }

  showAlert(message: string, isSuccess: boolean = true) {
    this.alertmessage = message;
    this.alertType = isSuccess;
    this.isalert = true;
    this.cdr.detectChanges();

    // Tự động ẩn sau 3s
    setTimeout(() => {
      this.isalert = false;
      this.cdr.detectChanges();
    }, 3000);
  }

  // --- API CALLS ---

  async loadData() {
    this.isloading = true;
    this.cdr.detectChanges();
    try {
      const data = await GetOverTimeRequest();
      if (typeof data === 'string' && data.includes('co loi')) {
        throw new Error(data);
      }
      this.requests = Array.isArray(data.content) ? data.content : [];
    } catch (e: any) {
      console.error(e);
      // Mock data nếu API lỗi để demo UI
      this.requests = [
        {
          requestId: 1,
          date: '2023-10-27',
          startTime: '18:00',
          endTime: '20:00',
          totalHours: 2,
          finalPaidHours: 0,
          status: 'PENDING',
          reason: 'Chạy dự án gấp',
          isSystemGenerated: false,
          employeeId: 'EMP001',
          employeeName: 'Nguyễn Văn A',
          departmentName: 'IT',
          managerName: '',
          hrName: '',
          createdAt: '2023-10-27T10:00:00',
          updatedAt: '2023-10-27T10:00:00',
          details: [
            { overtimeTypeName: 'Ngày thường', hours: 2, startTime: '18:00', endTime: '20:00' }
          ]
        }
      ];
      this.showAlert('Đang hiển thị dữ liệu mẫu do không kết nối được API thực.', false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  // --- SCAN ACTIONS ---

  openScanModal() {
    this.scanDate = new Date().toISOString().split('T')[0]; // Reset về ngày hiện tại
    this.isScanOpen = true;
    this.cdr.detectChanges();
  }

  async submitScan() {
    this.isloading = true;
    this.cdr.detectChanges();

    try {
      const res: any = await ScanDailyOt(this.scanDate);

      // Kiểm tra response (Giả sử status 200 là thành công)
      if (res && res.status === 200) {
        this.showAlert('Cập nhật dữ liệu thành công!', true);
        this.isScanOpen = false;
        await this.loadData(); // Load lại dữ liệu sau khi scan
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

  // --- REVIEW ACTION HANDLERS (Approve/Reject with Note) ---

  openReviewAction(type: 'approve' | 'reject') {
    if (!this.selectedRequest) return;

    this.reviewType = type;
    this.reviewData = {
      finalPaidHours: this.selectedRequest.totalHours.toString(),
      note: ''
    };

    this.isReviewOpen = true;
    this.cdr.detectChanges();
  }

  async submitReview() {
    if (!this.selectedRequest || !this.reviewType) return;

    this.isloading = true;

    try {
      let res: any;

      if (this.reviewType === 'approve') {
        res = await ApproveOverTimeRequest(
          this.reviewData,
          this.selectedRequest.requestId,
          this.currentRole
        );
      } else {
        res = await RejectOverTimeRequest(
          this.selectedRequest.requestId,
          this.reviewData
        );
      }

      if (res && res.status === 200) {
        this.showAlert(res.data, true);

        if (this.reviewType === 'approve') {
          this.selectedRequest.status = 'APPROVED';
          this.selectedRequest.finalPaidHours = parseFloat(this.reviewData.finalPaidHours);
        } else {
          this.selectedRequest.status = 'REJECTED';
        }

        this.isReviewOpen = false;
        this.closeDetail();
        await this.loadData();
      } else {
        this.showAlert(res.data?.response?.data?.message || 'Có lỗi xảy ra', false);
        console.log(res);
      }
    } catch (e: any) {
      this.showAlert(e, false);
      console.log("loi" + e);
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
    const isAuthorized = this.currentRole === 'manager' || this.currentRole === 'hr';
    const status = this.selectedRequest.status ? this.selectedRequest.status.toUpperCase() : '';
    const isPending = status.includes('PENDING');
    return isAuthorized && isPending;
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