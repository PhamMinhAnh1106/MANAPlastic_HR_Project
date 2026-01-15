import { CommonModule, NgClass, NgFor, NgIf } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, OnDestroy } from '@angular/core'; // Thêm OnDestroy
import { FormsModule } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';
import { Subject, Subscription } from 'rxjs'; // Import RxJS
import { debounceTime, distinctUntilChanged } from 'rxjs/operators'; // Import Operators
import { Approveleaverequest, getleaverequestManage, Rejectleaverequest } from '../../../../services/pages/features/employee/leaverequest.services';
import { leaverequests } from '../../../../interface/leaverequest.interface';
import { Loading } from '../../../shared/loading/loading';
import { Alert } from '../../../shared/alert/alert';
import { Comfirm } from '../../../shared/comfirm/comfirm';

// Đảm bảo đường dẫn import đúng tới file utils bạn đã tạo
// Ví dụ: import { findUserbyUsername } from '../../../../utils/finduser.utils';
import { findUserbyUsername } from '../../../../utils/finduser.utils';

@Component({
  selector: 'app-leaverequestcheck',
  standalone: true,
  imports: [CommonModule, FormsModule, NgFor, NgIf, NgClass, Loading, Alert, Comfirm],
  templateUrl: './leaverequestcheck.html',
  styleUrl: './leaverequestcheck.scss',
})
export class Leaverequestcheck implements OnInit, OnDestroy {
  constructor(private cdr: ChangeDetectorRef, private cookie: CookieService) { }

  role: string = "";

  filter = {
    username: '',
    status: ''
  };
  leaveRequests: leaverequests[] = [];

  // Stats
  stats = { pending: 0, approved: 0, rejected: 0 };

  id: number = 0;

  // Pagination
  page: number = 0;
  size: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;
  pageSizeOptions: number[] = [10, 20, 50, 100];

  // UI States
  isconfirm: boolean = false;
  isalert: boolean = false;
  isloading: boolean = false;
  confirmMessage = '';
  alertmessage = '';
  alertType: boolean = true;
  actionType: 'approve' | 'reject' | '' = '';

  // --- AUTOCOMPLETE VARIABLES ---
  userSearchResults: any[] = [];
  showUserSearchDropdown: boolean = false;
  searchSubject = new Subject<string>();
  private searchSubscription: Subscription | undefined;

  Onalert(message: string, type: boolean) {
    this.isalert = true;
    this.alertmessage = message;
    this.alertType = type;
  }

  // --- AUTOCOMPLETE LOGIC ---
  onSearchUser(event: any) {
    const value = event.target.value;
    // Cập nhật giá trị filter ngay khi gõ
    this.filter.username = value;

    // Nếu rỗng thì ẩn dropdown
    if (!value || value.trim() === '') {
      this.showUserSearchDropdown = false;
      this.userSearchResults = [];
      return;
    }
    // Push vào subject để debounce
    this.searchSubject.next(value);
  }

  async performUserSearch(username: string) {
    try {
      // Gọi hàm từ file utils
      const res = await findUserbyUsername(username) as any[];

      // Giới hạn 5 kết quả
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
    // Gán giá trị vào filter
    this.filter.username = user.username || user.fullName || '';

    // Ẩn dropdown
    this.showUserSearchDropdown = false;

    // Gọi tìm kiếm dữ liệu bảng ngay lập tức
    this.onSearch();
  }

  // Ẩn dropdown khi click ra ngoài (đơn giản hóa bằng cách dùng delay để click event kịp chạy)
  hideDropdownDelayed() {
    setTimeout(() => {
      this.showUserSearchDropdown = false;
    }, 200);
  }

  // --- MAIN LOGIC ---
  async filterLeave() {
    this.isloading = true;
    try {
      const res: any = await getleaverequestManage(this.filter.username, this.page, this.size);
      if (res) {
        let content = res.content || [];
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

  async loadInitialStats() {
    try {
      const res: any = await getleaverequestManage('', 0, 1000);
      if (res && res.content) {
        this.calculateStats(res.content);
      }
    } catch (error) {
      console.error("Lỗi tải thống kê", error);
    }
  }

  calculateStats(data: leaverequests[]) {
    this.stats.pending = data.filter(item => item.status === 'PENDING').length;
    this.stats.approved = data.filter(item => item.status === 'APPROVED').length;
    this.stats.rejected = data.filter(item => item.status === 'REJECTED').length;
  }

  quickFilter(status: string) {
    if (this.filter.status === status) {
      this.filter.status = '';
    } else {
      this.filter.status = status;
    }
    this.onSearch();
  }

  onSearch() {
    this.page = 0;
    this.showUserSearchDropdown = false; // Ẩn dropdown gợi ý khi bấm tìm kiếm
    this.filterLeave();
  }

  onPageChange(newPage: number) {
    if (newPage >= 0 && newPage < this.totalPages) {
      this.page = newPage;
      this.filterLeave();
    }
  }

  onPageSizeChange() {
    localStorage.setItem('leaveRequestPageSize', this.size.toString());
    this.page = 0;
    this.filterLeave();
  }

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
        await this.filterLeave();
        await this.loadInitialStats();
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

    // Load size preference
    const savedSize = localStorage.getItem('leaveRequestPageSize');
    if (savedSize) {
      this.size = Number(savedSize);
    } else {
      this.size = 10;
    }

    // Init Data
    this.filterLeave();
    this.loadInitialStats();

    // --- SETUP DEBOUNCE SEARCH ---
    this.searchSubscription = this.searchSubject.pipe(
      debounceTime(400), // Chờ 400ms sau khi ngừng gõ
      distinctUntilChanged()
    ).subscribe(searchTerm => {
      this.performUserSearch(searchTerm);
    });
  }

  ngOnDestroy(): void {
    if (this.searchSubscription) {
      this.searchSubscription.unsubscribe();
    }
  }
}