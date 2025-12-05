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

  // --- LOGIC FETCH DATA ---
  async filterLeave() {
    this.isloading = true;
    try {
      // Gọi API với username, page, size
      const res: any = await getleaverequestManage(this.filter.username, this.page, this.size);

      if (res) {
        // Gán dữ liệu phân trang từ API
        // Lưu ý: Nếu API chưa hỗ trợ lọc status ở Backend, việc lọc ở Frontend trên trang hiện tại 
        // có thể dẫn đến trang bị trống dù còn dữ liệu ở trang khác. 
        // Tốt nhất API nên hỗ trợ param &status=...

        let content = res.content || [];

        // Logic lọc client-side (giữ nguyên logic cũ của bạn áp dụng lên page hiện tại)
        if (this.filter.status !== '') {
          content = content.filter((item: { status: string; }) => item.status === this.filter.status);
        }

        this.leaveRequests = content;
        this.totalPages = res.totalPages || 0;
        this.totalElements = res.totalElements || 0;
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

  // --- PAGINATION HANDLERS ---

  // Khi bấm nút Lọc hoặc Enter -> Reset về trang 0
  onSearch() {
    this.page = 0;
    this.filterLeave();
  }

  // Chuyển trang
  onPageChange(newPage: number) {
    if (newPage >= 0 && newPage < this.totalPages) {
      this.page = newPage;
      this.filterLeave();
    }
  }

  // Đổi số lượng dòng hiển thị
  onPageSizeChange() {
    this.page = 0; // Reset về trang đầu
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
      if (this.actionType === 'approve') {
        const res = await Approveleaverequest(this.id) as { data: string, status: number };
        if (res.status === 200 || res.status === 201) {
          this.Onalert(res.data, true);
          await this.filterLeave(); // Load lại data
        } else {
          this.Onalert(res.data || "Có lỗi xảy ra", false);
        }
      }
      else if (this.actionType === 'reject') {
        const res = await Rejectleaverequest(this.id) as { data: string, status: number };
        if (res.status === 200 || res.status === 201) {
          this.Onalert(res.data, true);
          await this.filterLeave();
        } else {
          this.Onalert(res.data || "Có lỗi xảy ra", false);
        }
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

  }
}