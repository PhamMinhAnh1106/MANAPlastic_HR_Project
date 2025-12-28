import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

// Import service logic (nhưng ta sẽ tự định nghĩa Interface ở dưới để tránh lỗi)
import { ActivityLogsService } from '../../../../services/pages/features/admin/activityLogs.service';

// --- QUAN TRỌNG: Định nghĩa Interface ngay tại đây để Template hiểu ---
export interface ActivityLogs {
  id: number;
  action: string;
  actiontime: string; // ISO datetime
  userID: number | null;
  logType: 'INFO' | 'WARN' | 'ERROR' | string;
  details: string;
  username: string;
}

@Component({
  selector: 'app-activity-logs',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './activity-logs.html',
  styleUrls: ['./activity-logs.scss']
})
export class ActivityLogs implements OnInit {
  // Signals
  logs = signal<ActivityLogs[]>([]);
  isLoading = signal<boolean>(false);
  currentPage = signal<number>(0);
  totalPages = signal<number>(0);
  selectedLog = signal<ActivityLogs | null>(null);

  searchQuery: string = '';
  pageSize: number = 10;

  constructor() { }

  ngOnInit() {
    this.fetchLogs();
  }

  async fetchLogs() {
    this.isLoading.set(true);
    try {
      const queryParam = this.searchQuery ? `query=${this.searchQuery}` : '';

      // Gọi service
      // Lưu ý: Đảm bảo ActivityLogsService trong api.service.ts trả về đúng dữ liệu
      const data: any = await ActivityLogsService(this.currentPage(), this.pageSize, queryParam);

      if (data) {
        if (Array.isArray(data)) {
          this.logs.set(data);
        } else if (data.content) {
          this.logs.set(data.content);
          this.totalPages.set(data.totalPages);
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