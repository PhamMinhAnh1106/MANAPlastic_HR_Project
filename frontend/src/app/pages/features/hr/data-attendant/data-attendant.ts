import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser'; // Import Sanitizer
import { CookieService } from 'ngx-cookie-service';

// Import các component/service
import { Loading } from '../../../shared/loading/loading';
import { Alert } from '../../../shared/alert/alert';
import { Comfirm } from '../../../shared/comfirm/comfirm';
import {
  ApproveAttendanceRequest,
  AttendanceRequest,
  GetDataAttenden,
  RejectAttendanceRequest,

} from '../../../../services/pages/features/hr/datacheck.service';
import { getImageAttendance } from '../../../../utils/getimage.utils';

@Component({
  selector: 'app-data-attendant',
  standalone: true, // Thêm standalone: true nếu bạn đang dùng Angular Standalone Component
  imports: [CommonModule, FormsModule, Loading, Alert],
  templateUrl: './data-attendant.html',
  styleUrl: './data-attendant.scss',
})
export class DataAttendant implements OnInit {
  // --- Alert & Confirm Props ---
  isconfirm: boolean = false;
  isalert: boolean = false;
  isloading: boolean = false; // Loading toàn trang
  confirmMessage = '';
  alertmessage = '';
  alertType: boolean = true;
  actionType: 'approve' | 'reject' | '' = '';

  Onalert(message: string, type: boolean) {
    this.isalert = true;
    this.alertmessage = message;
    this.alertType = type;
  }

  // --- Main Data Props ---
  data: any = null;
  originalContent: any[] = []; // Dữ liệu gốc để lọc client-side
  isLoading = false; // Loading bảng dữ liệu
  Math = Math;
  currentUserRole: string = '';

  // --- Filter Props ---
  isFilterOpen: boolean = false;
  currentStatusFilter: string = '';

  // --- Modal Props ---
  selectedRequest: any = null; // Dùng 'any' để tránh lỗi type nếu AttendanceRequest chưa cập nhật field imgProof
  isRejecting = false;
  rejectComment = '';
  isAdding = false;

  // --- Image Handling Props ---
  proofImageUrl: SafeUrl | null = null;
  isLoadingImage: boolean = false;
  private currentBlobUrl: string | null = null;

  constructor(
    private cookieService: CookieService,
    private cdr: ChangeDetectorRef,
    private sanitizer: DomSanitizer // Inject Sanitizer
  ) { }

  onConfirmResult(result: boolean) { }

  ngOnInit() {
    this.currentUserRole = this.cookieService.get('role');
    this.loadData(0, 10);
  }

  // --- FILTER LOGIC ---
  toggleFilter(event: Event) {
    event.stopPropagation();
    this.isFilterOpen = !this.isFilterOpen;
  }

  filterByStatus(status: string) {
    this.currentStatusFilter = status;
    this.isFilterOpen = false;
    this.applyClientFilter();
  }

  applyClientFilter() {
    if (!this.data) return;
    if (this.currentStatusFilter === '') {
      this.data.content = [...this.originalContent];
    } else {
      this.data.content = this.originalContent.filter((item: any) => item.status === this.currentStatusFilter);
    }
  }

  // --- LOAD DATA ---
  async loadData(page: number, size: number) {
    this.isLoading = true;
    try {
      const response: any = await GetDataAttenden(page, size);
      if (response) {
        this.data = response;
        this.originalContent = [...(response.content || [])];
        this.applyClientFilter();
      } else {
        this.data = null;
        this.originalContent = [];
      }
    } catch (e) {
      console.error(e);
      this.data = null;
      this.originalContent = [];
    } finally {
      this.isLoading = false;
      this.cdr.detectChanges();
    }
  }

  // --- PAGINATION helpers ---
  get pageNumbers(): number[] {
    if (!this.data) return [];
    return Array.from({ length: this.data.totalPages }, (_, i) => i);
  }

  changePage(page: number) {
    if (!this.data || page < 0 || page >= this.data.totalPages) return;
    this.loadData(page, this.data.size);
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'APPROVED': return 'status-approved';
      case 'REJECTED': return 'status-rejected';
      default: return 'status-pending';
    }
  }

  // --- MODAL ACTIONS (Updated with Image API) ---
  async openDetailModal(item: any) {
    this.selectedRequest = item;
    this.isRejecting = false;
    this.rejectComment = '';

    // Reset ảnh cũ
    this.proofImageUrl = null;
    if (this.currentBlobUrl) {
      URL.revokeObjectURL(this.currentBlobUrl);
      this.currentBlobUrl = null;
    }

    // Nếu item có tên ảnh (imgProof), gọi API lấy ảnh
    if (item.imgProof) {
      this.isLoadingImage = true;
      try {
        const blobData = await getImageAttendance(item.imgProof);

        if (blobData && blobData instanceof Blob) {
          const objectUrl = URL.createObjectURL(blobData);
          this.currentBlobUrl = objectUrl;
          this.proofImageUrl = this.sanitizer.bypassSecurityTrustUrl(objectUrl);
        } else {
          // Không phải blob hoặc lỗi
          this.proofImageUrl = null;
        }
      } catch (error) {
        console.error("Lỗi tải ảnh:", error);
        this.proofImageUrl = null;
      } finally {
        this.isLoadingImage = false;
        this.cdr.detectChanges();
      }
    } else {
      // Không có tên ảnh trong data
      this.isLoadingImage = false;
      this.proofImageUrl = null;
    }
  }

  closeDetailModal() {
    this.selectedRequest = null;
    this.proofImageUrl = null;
    // Dọn dẹp bộ nhớ
    if (this.currentBlobUrl) {
      URL.revokeObjectURL(this.currentBlobUrl);
      this.currentBlobUrl = null;
    }
  }

  startReject() {
    this.isRejecting = true;
  }

  cancelReject() {
    this.isRejecting = false;
    this.rejectComment = '';
  }

  // --- API ACTIONS ---
  async approve() {
    if (!this.selectedRequest) return;
    // this.isloading = true; // Uncomment nếu muốn hiện loading toàn trang khi duyệt
    const res: any = await ApproveAttendanceRequest(this.selectedRequest.requestId);
    if (res.status === 200) {
      this.Onalert(res.data, true);
      // this.isloading = false;
      this.closeDetailModal();
      this.loadData(this.data?.number || 0, 10);
    } else {
      this.Onalert(res.response.data.message, false);
    }

  }

  async confirmReject() {
    if (!this.selectedRequest || !this.rejectComment) return;
    // this.isloading = true;
    const res: any = await RejectAttendanceRequest(this.selectedRequest.requestId, this.rejectComment);
    if (res.status === 200) {
      this.Onalert(res.data, true);
      this.closeDetailModal();
      this.loadData(this.data?.number || 0, 10);
    } else {
      this.Onalert(res.response.data.message, false);
    }

  }

  openAddModal() { this.isAdding = true; }
  closeAddModal() { this.isAdding = false; }
  submitAdd() {
    this.closeAddModal();
    this.loadData(0, 10);
  }
}