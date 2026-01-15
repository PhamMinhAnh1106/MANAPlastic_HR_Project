import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';

// Services
import {
  GetDateSalary,
  GetDateSalaryI,
  GetMonthlySalary,
  GetMonthlySalaryI,
  RecalculateSalary
} from '../../../../services/pages/features/employee/salaryday.service';

// Shared Components
import { Alert } from '../../../shared/alert/alert';
import { Comfirm } from '../../../shared/comfirm/comfirm';
import { Loading } from '../../../shared/loading/loading';

@Component({
  selector: 'app-salary-date',
  standalone: true,
  imports: [CommonModule, FormsModule, Alert, Comfirm, Loading],
  templateUrl: './salarydate.html',
  styleUrls: ['./salarydate.scss']
})
export class salarydate implements OnInit {

  // --- STATE VARIABLES ---
  activeTab: string = 'monthly'; // 'monthly' | 'daily'
  currentRole: string = '';

  // Filter Data
  currentMonth: number = new Date().getMonth() + 1;
  currentYear: number = new Date().getFullYear();

  // Salary Data
  dailySalary: GetDateSalaryI | null = null;
  monthlySalary: GetMonthlySalaryI | null = null;

  // UI States
  isloading: boolean = false;

  // Alert State
  isalert: boolean = false;
  alertmessage: string = "";
  alertType: boolean = true; // true = success, false = error

  // Confirm State
  isconfirm: boolean = false;
  confirmMessage: string = "";
  private pendingAction: (() => void) | null = null;

  constructor(
    private cookie: CookieService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    // Lấy role và chuẩn hóa về chữ thường để so sánh
    this.currentRole = (this.cookie.get('role') || '').toLowerCase();

    // Mặc định load dữ liệu của tab hiện tại
    this.loadDataByTab();
  }

  // --- TAB HANDLING ---
  switchTab(tab: string) {
    this.activeTab = tab;
    this.loadDataByTab();
  }

  loadDataByTab() {
    if (this.activeTab === 'daily') {
      this.fetchDailyData();
    } else {
      this.fetchMonthlyData();
    }
  }

  // --- API CALLS ---

  async fetchDailyData() {
    this.isloading = true;
    try {
      const res: any = await GetDateSalary();
      // Kiểm tra nếu service trả về string lỗi
      if (this.isErrorResponse(res)) {
        this.dailySalary = null;
        // Optional: this.showAlert(res.response?.data?.message || res, false);
      } else {
        this.dailySalary = res as GetDateSalaryI;
      }
    } catch (e) {
      this.showAlert('Không thể tải dữ liệu lương ngày.', false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  async fetchMonthlyData() {
    this.isloading = true;
    try {
      const res: any = await GetMonthlySalary(this.currentMonth, this.currentYear);

      if (this.isErrorResponse(res)) {
        this.monthlySalary = null;
        // console.error(res);
      } else {
        this.monthlySalary = res as GetMonthlySalaryI;
      }
    } catch (e) {
      this.showAlert('Không thể tải dữ liệu lương tháng.', false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  async handleRecalculate() {
    this.isloading = true;
    try {
      const res: any = await RecalculateSalary(this.currentMonth, this.currentYear);

      if (this.isErrorResponse(res)) {
        const msg = res.response?.data?.message || typeof res === 'string' ? res : 'Có lỗi xảy ra';
        this.showAlert(msg, false);
      } else {
        const successMsg = typeof res === 'string' ? res : 'Cập nhật lương thành công!';
        this.showAlert(successMsg, true);

        // Reload lại dữ liệu sau khi tính toán
        this.loadDataByTab();
      }
    } catch (e) {
      this.showAlert('Lỗi hệ thống khi tính toán lại.', false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  // --- ACTIONS ---

  // Chỉ HR mới gọi được hàm này (đã ẩn nút ở HTML, chặn thêm ở đây cho chắc)
  onRecalculateClick() {
    if (this.currentRole !== 'hr') return;

    this.confirmMessage = `Bạn có chắc chắn muốn tính toán lại lương cho tháng ${this.currentMonth}/${this.currentYear}?`;
    this.pendingAction = () => this.handleRecalculate();
    this.isconfirm = true;
  }

  // --- HELPERS ---

  // Hàm check lỗi dựa trên cấu trúc service bạn cung cấp (trả về string hoặc object response lỗi)
  isErrorResponse(res: any): boolean {
    if (!res) return true;
    if (typeof res === 'string' && res.includes('co loi xay ra')) return true;
    if (res.response?.data?.message) return true; // Cấu trúc axios error thường gặp
    return false;
  }

  showAlert(message: string, isSuccess: boolean) {
    this.alertmessage = message;
    this.alertType = isSuccess;
    this.isalert = true;
    // Tự động đóng sau 3s
    setTimeout(() => this.isalert = false, 3000);
  }

  onConfirmResult(result: boolean) {
    this.isconfirm = false;
    if (result && this.pendingAction) {
      this.pendingAction();
    }
    this.pendingAction = null;
  }
}