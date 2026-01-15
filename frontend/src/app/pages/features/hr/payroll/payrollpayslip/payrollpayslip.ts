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
  styleUrls: ['./payrollpayslip.scss']
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
  hasSearched = false;

  // CÁC QUY TẮC CẦN ẨN KHỎI BẢNG CHI TIẾT (Vì đã hiển thị ở phần tổng hoặc header)
  readonly HIDDEN_RULES = ['TOTAL_INCOME', 'NET_SALARY', 'TAXABLE_INCOME', 'INSURANCE_AMT', 'ASSESSABLE_INCOME'];

  constructor(private cdr: ChangeDetectorRef) { }

  async ngOnInit(): Promise<void> {
    // 1. Check Role
    this.role = this.getCookie('role')?.toUpperCase() || 'USER';

    // 2. Load danh sách nhân viên nếu là HR
    if (this.role === 'HR') {
      await this.loadAuditUsers();
    }
  }

  // --- ACTIONS ---

  async switchTab(tab: 'MY_PAYSLIP' | 'EMP_PAYSLIP') {
    this.activeTab = tab;
    this.errorMsg = '';
    this.payslipData = null;
    this.hasSearched = false;
    this.selectedEmpId = ''; // Reset selection khi chuyển tab
  }

  async onViewClick() {
    this.hasSearched = true;
    await this.loadData();
    this.cdr.detectChanges();
  }

  // --- DATA LOADING ---

  async loadAuditUsers() {
    try {
      this.auditUsers = await getAuditUsers();
    } catch (e) {
      console.error("Error loading audit users", e);
    }
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
        // Gọi API lấy lương nhân viên
        data = await getUserPayrollDetail(this.selectedEmpId, this.selectedMonth, this.selectedYear);
      } else {
        // Gọi API lấy lương bản thân
        data = await getMyPayslip(this.selectedMonth, this.selectedYear);
      }

      if (data) {
        this.payslipData = data;
        // console.log('Payslip Data:', this.payslipData);
      } else {
        throw new Error("Không có dữ liệu trả về.");
      }

    } catch (error: any) {
      console.error(error);
      this.errorMsg = error.response?.data?.message || error.message || "Có lỗi xảy ra khi tải dữ liệu.";
    } finally {
      this.loading = false;
      this.cdr.detectChanges();
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

  // Lấy giá trị Thu nhập chịu thuế từ mảng deductions
  get taxableIncome(): number {
    return this.payslipData?.deductions.find(i => i.rule_code === 'TAXABLE_INCOME')?.value || 0;
  }

  // Tính tổng các khoản khấu trừ (loại bỏ các dòng tổng hợp ẩn)
  get totalCalculatedDeduction(): number {
    if (!this.payslipData) return 0;
    return this.payslipData.deductions
      .filter(i => !this.HIDDEN_RULES.includes(i.rule_code))
      .reduce((sum, item) => sum + Math.abs(item.value), 0);
  }

  // Lọc các item để hiển thị lên bảng (loại bỏ các dòng ẩn và dòng có giá trị 0 nếu muốn)
  filterItems(items: PayslipItem[]): PayslipItem[] {
    if (!items) return [];
    return items.filter(i => !this.HIDDEN_RULES.includes(i.rule_code) && Math.abs(i.value) > 0);
  }

  private getCookie(name: string): string | null {
    const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
    if (match) return match[2];
    return null;
  }
}