import { CommonModule, NgClass, NgFor, NgIf } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';
import { Approveleaverequest, getleaverequestManage, Rejectleaverequest } from '../../../../services/pages/features/employee/leaverequest.services';
import { leaverequests } from '../../../../interface/leaverequest.interface';
import { Loading } from '../../../shared/loading/loading';
import { Alert } from '../../../shared/alert/alert';
import { Comfirm } from '../../../shared/comfirm/comfirm';

@Component({
  selector: 'app-leaverequestcheck',
  standalone: true,
  imports: [CommonModule, FormsModule, NgFor, NgIf, NgClass, Loading, Alert, Comfirm],
  templateUrl: './leaverequestcheck.html',
  styleUrl: './leaverequestcheck.scss',
})
export class Leaverequestcheck implements OnInit {
  constructor(private cdr: ChangeDetectorRef, private cookie: CookieService) { }

  role: string = "";

  filter = {
    username: '',
    status: ''
  };
  leaveRequests: leaverequests[] = [];

  // Biến chứa thống kê
  stats = {
    pending: 0,
    approved: 0,
    rejected: 0
  };

  id: number = 0;

  // --- PAGINATION STATES ---
  page: number = 0;
  size: number = 5;
  totalPages: number = 0;
  totalElements: number = 0;
  pageSizeOptions: number[] = [5, 10, 20, 50];

  // Confirm & Alert States
  isconfirm: boolean = false;
  isalert: boolean = false;
  isloading: boolean = false;
  confirmMessage = '';
  alertmessage = '';
  alertType: boolean = true;
  actionType: 'approve' | 'reject' | '' = '';

  Onalert(message: string, type: boolean) {
    this.isalert = true;
    this.alertmessage = message;
    this.alertType = type;
  }

  // --- LOGIC FETCH DATA (Bảng dữ liệu) ---
  async filterLeave() {
    this.isloading = true;
    try {
      // Gọi API lấy dữ liệu cho bảng (có phân trang và filter hiện tại)
      const res: any = await getleaverequestManage(this.filter.username, this.page, this.size);

      if (res) {
        let content = res.content || [];

        // Logic lọc client-side (nếu cần thiết)
        if (this.filter.status !== '') {
          content = content.filter((item: { status: string; }) => item.status === this.filter.status);
        }

        this.leaveRequests = content;
        this.totalPages = res.totalPages || 0;
        this.totalElements = res.totalElements || 0;

        // ĐÃ XÓA: Không gọi calculateStats ở đây nữa để tránh tính lại khi filter

      } else {
        this.leaveRequests = [];
        this.totalElements = 0;
      }

    } catch (error) {
      this.Onalert("Lỗi tải dữ liệu", false);
      this.leaveRequests = [];
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  // --- LOGIC TÍNH STATS (Chạy 1 lần) ---
  async loadInitialStats() {
    try {
      // Gọi API riêng để lấy dữ liệu thống kê tổng quát.
      // Lưu ý: Để thống kê chính xác, ta cần lấy toàn bộ dữ liệu (hoặc số lượng lớn) mà KHÔNG có filter status.
      // Ta truyền username rỗng và page size lớn (ví dụ 1000) để đếm được tổng quan.
      const res: any = await getleaverequestManage('', 0, 1000);

      if (res && res.content) {
        this.calculateStats(res.content);
      }
    } catch (error) {
      console.error("Lỗi tải thống kê", error);
    }
  }

  // Hàm tính toán thống kê
  calculateStats(data: leaverequests[]) {
    this.stats.pending = data.filter(item => item.status === 'PENDING').length;
    this.stats.approved = data.filter(item => item.status === 'APPROVED').length;
    this.stats.rejected = data.filter(item => item.status === 'REJECTED').length;
  }

  resetStats() {
    this.stats = { pending: 0, approved: 0, rejected: 0 };
  }

  // Hàm hỗ trợ click vào thẻ thống kê để lọc nhanh
  quickFilter(status: string) {
    // Nếu đang chọn status đó rồi thì bỏ chọn (về tất cả)
    if (this.filter.status === status) {
      this.filter.status = '';
    } else {
      this.filter.status = status;
    }
    this.onSearch();
  }

  // --- PAGINATION HANDLERS ---
  onSearch() {
    this.page = 0;
    this.filterLeave();
  }

  onPageChange(newPage: number) {
    if (newPage >= 0 && newPage < this.totalPages) {
      this.page = newPage;
      this.filterLeave();
    }
  }

  onPageSizeChange() {
    this.page = 0;
    this.filterLeave();
  }

  // --- LOGIC CONFIRMATION ---
  approve(id: number) {
    this.isconfirm = true;
    this.confirmMessage = "Bạn có chắc muốn duyệt đơn này?";
    this.id = id;
    this.actionType = 'approve';
  }

  reject(id: number) {
    this.isconfirm = true;
    this.confirmMessage = "Bạn có chắc muốn từ chối đơn này?";
    this.id = id;
    this.actionType = 'reject';
  }

  async onConfirmResult(event: any) {
    if (!event) {
      this.isconfirm = false;
      this.actionType = '';
      return;
    }

    this.isconfirm = false;
    this.isloading = true;

    try {
      let res: { data: string, status: number };

      if (this.actionType === 'approve') {
        res = await Approveleaverequest(this.id) as { data: string, status: number };
      } else {
        res = await Rejectleaverequest(this.id) as { data: string, status: number };
      }

      if (res.status === 200 || res.status === 201) {
        this.Onalert(res.data, true);
        await this.filterLeave(); // Load lại bảng dữ liệu
        await this.loadInitialStats(); // Cập nhật lại stats sau khi duyệt/từ chối thành công
      } else {
        this.Onalert(res.data || "Có lỗi xảy ra", false);
      }

    } catch (error) {
      this.Onalert("Lỗi kết nối máy chủ", false);
    } finally {
      this.isloading = false;
      this.actionType = '';
      this.cdr.detectChanges();
    }
  }

  // --- UTILS ---
  getVietnameseLeaveType(type: string): string {
    switch (type) {
      case 'ANNUAL': return 'Phép Năm';
      case 'SICK': return 'Nghỉ Ốm';
      case 'MATERNITY': return 'Thai Sản (Mẹ)';
      case 'PATERNITY': return 'Thai Sản (Cha)';
      case 'UNPAID': return 'Không Lương';
      default: return type;
    }
  }

  ngOnInit(): void {
    if (this.cookie.get('role')) {
      this.role = this.cookie.get('role');
    }
    // Load dữ liệu bảng
    this.filterLeave();

    // Load thống kê (chạy riêng biệt)
    this.loadInitialStats();
  }
}