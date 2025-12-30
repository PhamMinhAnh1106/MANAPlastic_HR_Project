import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

// Import service từ đường dẫn gốc của bạn
import { ActivityLogsService } from '../../../../services/pages/features/admin/activityLogs.service';

// Interface đã cập nhật theo cấu trúc data mới
export interface ActivityLogs {
  logID: number;
  action: string;
  actionTime: string; // ISO datetime string
  logType: 'INFO' | 'WARNING' | 'DANGER' | string;
  details: string;
  executorName: string;
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
      const queryParam = this.searchQuery ? `keyword=${this.searchQuery}` : '';

      // Gọi service thực tế thay vì mock data
      // Giả định ActivityLogsService là một hàm async trả về dữ liệu
      const data: any = await ActivityLogsService(this.currentPage(), this.pageSize, queryParam);

      if (data) {
        // Xử lý dữ liệu trả về tùy theo cấu trúc response của API
        if (Array.isArray(data)) {
          this.logs.set(data);
        } else if (data.content) {
          // Trường hợp trả về dạng Pageable (content, totalPages,...)
          this.logs.set(data.content);
          // Cập nhật totalPages nếu có
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