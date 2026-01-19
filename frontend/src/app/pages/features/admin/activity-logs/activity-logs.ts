import { Component, OnInit, OnDestroy, signal, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { ActivityLogsService } from '../../../../services/pages/features/admin/activityLogs.service';
import { findUserbyUsername } from '../../../../utils/finduser.utils';


export interface ActivityLogs {
  logID: number;
  action: string;
  actionTime: string; // ISO datetime string
  logType: 'INFO' | 'WARNING' | 'DANGER' | string;
  details: string;
  executorName: string;
  performedBy: string;
}



@Component({
  selector: 'app-activity-logs',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './activity-logs.html',
  styleUrls: ['./activity-logs.scss']
})
export class ActivityLogs implements OnInit, OnDestroy {
  // --- Signals hiển thị Logs ---
  logs = signal<ActivityLogs[]>([]);
  isLoading = signal<boolean>(false);
  currentPage = signal<number>(0);
  totalPages = signal<number>(0);
  selectedLog = signal<ActivityLogs | null>(null);

  // --- Search Variables ---
  searchQuery: string = '';
  pageSize: number = 10;

  // --- User Autocomplete Variables ---
  searchSubject = new Subject<string>();
  searchSubscription: Subscription | undefined;

  // Danh sách user gợi ý (dùng signal để update UI reactive)
  userSearchResults = signal<any[]>([]);
  showUserSearchDropdown = signal<boolean>(false);

  constructor(private cdr: ChangeDetectorRef) { }

  ngOnInit() {
    this.fetchLogs();
    this.setupSearchSubscription();
  }

  ngOnDestroy() {
    if (this.searchSubscription) {
      this.searchSubscription.unsubscribe();
    }
  }

  // --- LOGIC TÌM KIẾM AUTOCOMPLETE ---

  // 1. Setup RxJS Debounce để tránh spam API khi gõ phím
  setupSearchSubscription() {
    this.searchSubscription = this.searchSubject
      .pipe(
        debounceTime(300), // Chờ 300ms sau khi ngừng gõ
        distinctUntilChanged() // Chỉ xử lý nếu giá trị khác lần trước
      )
      .subscribe((value) => {
        this.performUserSearch(value);
      });
  }

  // 2. Hàm gọi khi người dùng gõ vào ô input
  onSearchInput(event: any) {
    const value = event.target.value;
    this.searchQuery = value; // Update giá trị ngModel

    // Nếu input rỗng -> ẩn dropdown
    if (!value || value.trim() === '') {
      this.showUserSearchDropdown.set(false);
      this.userSearchResults.set([]);
      return;
    }
    // Đẩy giá trị vào Subject để xử lý debounce
    this.searchSubject.next(value);
  }

  // 3. Gọi API tìm user
  async performUserSearch(username: string) {
    try {
      // Gọi hàm từ finduser.utils.ts (đã mock ở trên)
      const res = await findUserbyUsername(username) as any[];

      if (res && Array.isArray(res) && res.length > 0) {
        this.userSearchResults.set(res.slice(0, 5)); // Lấy tối đa 5 kết quả
        this.showUserSearchDropdown.set(true);
      } else {
        this.userSearchResults.set([]);
        this.showUserSearchDropdown.set(false);
      }
    } catch (error) {
      console.error("Lỗi tìm kiếm user:", error);
      this.userSearchResults.set([]);
      this.showUserSearchDropdown.set(false);
    }
  }

  // 4. Khi người dùng chọn 1 user từ dropdown
  selectSearchUser(user: any) {
    // Ưu tiên username, nếu không có lấy fullName
    const selectedName = user.username || user.fullName || '';
    this.searchQuery = selectedName;

    // Ẩn dropdown
    this.showUserSearchDropdown.set(false);

    // Trigger tìm kiếm Logs ngay lập tức
    this.onSearch();
  }

  // 5. Ẩn dropdown khi click ra ngoài (Blur)
  onBlurSearch() {
    // Delay nhỏ để kịp nhận sự kiện click vào item trong dropdown trước khi nó biến mất
    setTimeout(() => {
      this.showUserSearchDropdown.set(false);
    }, 200);
  }

  // --- LOGIC LOGS CORE ---

  async fetchLogs() {
    this.isLoading.set(true);
    try {
      const queryParam = this.searchQuery ? `keyword=${this.searchQuery}` : '';

      const data: any = await ActivityLogsService(this.currentPage(), this.pageSize, queryParam);

      if (data) {
        // Xử lý dữ liệu trả về tùy theo cấu trúc response của API
        if (Array.isArray(data)) {
          this.logs.set(data);
        } else if (data.content) {
          // Trường hợp trả về dạng Pageable (content, totalPages,...)
          this.logs.set(data.content);
          if (data.totalPages) {
            this.totalPages.set(data.totalPages);
          }
        }
      }
    } catch (error) {
      console.error("Lỗi tải logs:", error);
      this.logs.set([]);
    } finally {
      this.isLoading.set(false);
    }
  }

  onSearch() {
    this.currentPage.set(0);
    this.fetchLogs();
    this.showUserSearchDropdown.set(false);
  }

  changePage(newPage: number) {
    if (newPage >= 0 && newPage < this.totalPages()) {
      this.currentPage.set(newPage);
      this.fetchLogs();
    }
  }

  openDetailModal(log: ActivityLogs) {
    this.selectedLog.set(log);
  }

  closeModal() {
    this.selectedLog.set(null);
  }
}