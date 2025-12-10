import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuditUser, PayslipItem, PayslipResponse } from '../../../../../interface/paysplip.interface';
import { getAuditUsers } from '../../../../../services/pages/features/hr/payroll/rules.services';
import { getMyPayslip, getUserPayrollDetail } from '../../../../../services/pages/features/hr/payroll/payslip.services';

@Component({
  selector: 'app-payslip',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './Payrollpayslip.html',
  styleUrls: ['./Payrollpayslip.scss']
})
export class Payrollpayslip implements OnInit {
  // --- STATE ---
  role: string = '';
  activeTab: 'MY_PAYSLIP' | 'EMP_PAYSLIP' = 'MY_PAYSLIP';

  // Filters
  months = Array.from({ length: 12 }, (_, i) => i + 1);
  years = Array.from({ length: 8 }, (_, i) => 2023 + i);

  selectedMonth: number = new Date().getMonth() + 1;
  selectedYear: number = new Date().getFullYear();
  selectedEmpId: string | number = '';

  // Data
  auditUsers: AuditUser[] = [];
  payslipData: PayslipResponse | null = null;

  // UI State
  loading = false;
  errorMsg = '';
  hasSearched = false; // Biến cờ để kiểm soát việc hiển thị thông báo ban đầu

  readonly HIDDEN_RULES = ['TOTAL_INCOME', 'NET_SALARY', 'TAXABLE_INCOME', 'INSURANCE_AMT', 'ASSESSABLE_INCOME'];

  constructor(private cdr: ChangeDetectorRef) { }

  async ngOnInit(): Promise<void> {
    // 1. Check Role
    this.role = this.getCookie('role')?.toUpperCase() || 'USER';

    // 2. Load danh sách nhân viên nếu là HR
    if (this.role === 'HR') {
      await this.loadAuditUsers();

    }

    // 3. QUAN TRỌNG: KHÔNG gọi loadData() ở đây nữa theo yêu cầu của bạn.
    // Người dùng phải bấm nút mới load.
  }

  // --- ACTIONS ---

  async switchTab(tab: 'MY_PAYSLIP' | 'EMP_PAYSLIP') {
    this.activeTab = tab;
    this.errorMsg = '';
    this.payslipData = null;
    this.hasSearched = false; // Reset trạng thái tìm kiếm

    // KHÔNG tự động load khi chuyển tab nữa
  }

  async onViewClick() {
    this.hasSearched = true; // Đánh dấu là đã bấm nút tìm
    await this.loadData();
    this.cdr.detectChanges();
  }



  // --- DATA LOADING ---

  async loadAuditUsers() {
    this.auditUsers = await getAuditUsers();

  }

  async loadData() {
    this.loading = true;
    this.errorMsg = '';
    this.payslipData = null;

    try {
      let data: PayslipResponse;

      if (this.role === 'HR' && this.activeTab === 'EMP_PAYSLIP') {
        if (!this.selectedEmpId) {
          throw new Error("Vui lòng chọn nhân viên cần xem.");
        }
        data = await getUserPayrollDetail(this.selectedEmpId, this.selectedMonth, this.selectedYear);
        this.cdr.detectChanges();

      } else {
        data = await getMyPayslip(this.selectedMonth, this.selectedYear);
        this.cdr.detectChanges();

      }

      if (data) {
        this.payslipData = data;
      } else {
        throw new Error("Không có dữ liệu trả về.");
      }

    } catch (error: any) {
      console.error(error);
      this.errorMsg = error.response?.data?.message || error.message || "Có lỗi xảy ra khi tải dữ liệu.";
    } finally {
      this.loading = false;
    }
  }

  // --- HELPERS ---

  get formattedPeriod(): string {
    if (!this.payslipData?.header?.payperiod) return '--/----';
    return this.payslipData.header.payperiod.split('-').reverse().join('/');
  }

  get traceId(): string {
    if (!this.payslipData) return '---';
    return `PAY-${this.payslipData.header.payperiod}-${this.payslipData.header.userID}`;
  }

  get taxableIncome(): number {
    return this.payslipData?.deductions.find(i => i.rule_code === 'TAXABLE_INCOME')?.value || 0;
  }

  get totalCalculatedDeduction(): number {
    if (!this.payslipData) return 0;
    return this.payslipData.deductions
      .filter(i => !this.HIDDEN_RULES.includes(i.rule_code))
      .reduce((sum, item) => sum + Math.abs(item.value), 0);
  }

  filterItems(items: PayslipItem[]): PayslipItem[] {
    return items.filter(i => !this.HIDDEN_RULES.includes(i.rule_code) && Math.abs(i.value) > 0);
  }

  private getCookie(name: string): string | null {
    const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
    if (match) return match[2];
    return null;
  }
}