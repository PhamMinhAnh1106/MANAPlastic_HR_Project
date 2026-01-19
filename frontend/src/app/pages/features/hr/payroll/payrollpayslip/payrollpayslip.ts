import { ChangeDetectorRef, Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

// Import services existing in your project
import { getAuditUsers } from '../../../../../services/pages/features/hr/payroll/rules.services';
import { getMyPayslip, getUserPayrollDetail } from '../../../../../services/pages/features/hr/payroll/payslip.services';
// Import the util file (Adjust the path to where finduser.utils.ts is actually located in your project)
import { findUserbyUsername } from '../../../../../utils/finduser.utils';

// --- INTERFACES ---
export interface AllowanceDetail {
  name: string;
  amount: number;
  quantity: number;
  note: string;
}

export interface PayslipResponse {
  userId: number;
  fullName: string;
  departmentName: string;
  jobType: string;
  payPeriod: string;
  status: string;
  baseSalary: number;
  standardWorkDays: number;
  actualWorkDays: number;
  totalAllowance: number;
  totalOvertimePay: number;
  totalBonus: number;
  totalPunishment: number;
  totalIncome: number;
  taxableIncome: number | null;
  pit: number;
  bhxhEmp: number;
  bhytEmp: number;
  bhtnEmp: number;
  totalInsuranceEmp: number;
  bhxhComp: number;
  bhytComp: number;
  bhtnComp: number;
  netSalary: number;
  allowanceDetails: AllowanceDetail[];
}

export interface PayslipItem {
  name: string;
  value: number;
  note?: string;
}

@Component({
  selector: 'app-payslip',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payrollpayslip.html',
  styleUrls: ['./payrollpayslip.scss']
})
export class Payrollpayslip implements OnInit, OnDestroy {
  // --- STATE ---
  role: string = '';
  activeTab: 'MY_PAYSLIP' | 'EMP_PAYSLIP' = 'MY_PAYSLIP';

  // Filters
  months = Array.from({ length: 12 }, (_, i) => i + 1);
  years = Array.from({ length: 8 }, (_, i) => 2023 + i);

  selectedMonth: number = new Date().getMonth() + 1;
  selectedYear: number = new Date().getFullYear();

  // --- SEARCH STATE (New) ---
  selectedEmpId: string | number = '';
  selectedEmpDisplay: string = ''; // Text shown in input

  searchSubject = new Subject<string>();
  searchSubscription: Subscription | undefined;
  userSearchResults: any[] = [];
  showUserSearchDropdown = false;
  isSearching = false;

  // Data
  payslipData: PayslipResponse | null = null;

  // UI State
  loading = false;
  errorMsg = '';
  hasSearched = false;

  constructor(private cdr: ChangeDetectorRef) { }

  async ngOnInit(): Promise<void> {
    // 1. Check Role
    this.role = this.getCookie('role')?.toUpperCase() || 'USER';

    // 2. Setup Search Listener (Debounce logic)
    if (this.role === 'HR') {
      this.searchSubscription = this.searchSubject.pipe(
        debounceTime(400), // Wait 400ms after typing stops
        distinctUntilChanged() // Only run if value changed
      ).subscribe(searchText => {
        this.performUserSearch(searchText);
      });
    }
  }

  ngOnDestroy(): void {
    if (this.searchSubscription) {
      this.searchSubscription.unsubscribe();
    }
  }

  // --- ACTIONS ---

  async switchTab(tab: 'MY_PAYSLIP' | 'EMP_PAYSLIP') {
    this.activeTab = tab;
    this.errorMsg = '';
    this.payslipData = null;
    this.hasSearched = false;

    // Reset search state
    this.selectedEmpId = '';
    this.selectedEmpDisplay = '';
    this.showUserSearchDropdown = false;
  }

  async onViewClick() {
    this.hasSearched = true;
    await this.loadData();
    this.cdr.detectChanges();
  }

  // --- SEARCH LOGIC (New) ---

  onSearchUser(event: any) {
    const value = event.target.value;
    this.selectedEmpDisplay = value; // Update UI immediately

    // If input is cleared
    if (!value || value.trim() === '') {
      this.showUserSearchDropdown = false;
      this.userSearchResults = [];
      this.selectedEmpId = ''; // Reset ID
      return;
    }

    // Push to subject for debounce
    this.isSearching = true;
    this.searchSubject.next(value);
  }

  async performUserSearch(keyword: string) {
    try {
      // Call the utils function
      const res = await findUserbyUsername(keyword) as any;

      // Adapt based on actual API response structure (assuming array or {data: []})
      const users = Array.isArray(res) ? res : (res?.data || []);

      if (users && users.length > 0) {
        this.userSearchResults = users.slice(0, 5); // Limit results
        this.showUserSearchDropdown = true;
      } else {
        this.userSearchResults = [];
        this.showUserSearchDropdown = false;
      }
    } catch (error) {
      console.error("Search error", error);
      this.userSearchResults = [];
      this.showUserSearchDropdown = false;
    } finally {
      this.isSearching = false;
      this.cdr.detectChanges();
    }
  }

  selectSearchUser(user: any) {
    // 1. Save ID for API call
    this.selectedEmpId = user.userID || user.id;

    // 2. Display formatted name
    this.selectedEmpDisplay = `${user.username} - ${user.fullname || user.fullName}`;

    // 3. Close dropdown
    this.showUserSearchDropdown = false;
  }

  onInputBlur() {
    // Delay closing to allow click event on list item to fire first
    setTimeout(() => {
      this.showUserSearchDropdown = false;
    }, 200);
  }

  // --- DATA LOADING ---

  async loadData() {
    this.loading = true;
    this.errorMsg = '';
    this.payslipData = null;

    try {
      let data: any;

      if (this.role === 'HR' && this.activeTab === 'EMP_PAYSLIP') {
        if (!this.selectedEmpId) {
          throw new Error("Vui lòng tìm kiếm và chọn nhân viên trước.");
        }
        // Call API
        data = await getUserPayrollDetail(this.selectedEmpId, this.selectedMonth, this.selectedYear);
      } else {
        // Call API Me
        data = await getMyPayslip(this.selectedMonth, this.selectedYear);
      }

      if (data) {
        this.payslipData = data as PayslipResponse;
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
    if (!this.payslipData?.payPeriod) return '--/----';
    return this.payslipData.payPeriod.split('-').reverse().join('/');
  }

  get traceId(): string {
    if (!this.payslipData) return '---';
    return `PAY-${this.payslipData.payPeriod}-${this.payslipData.userId}`;
  }

  // Map fields to Income Table
  get incomeItems(): PayslipItem[] {
    if (!this.payslipData) return [];
    const items: PayslipItem[] = [];

    if (this.payslipData.allowanceDetails) {
      this.payslipData.allowanceDetails.forEach(allowance => {
        if (allowance.amount !== 0 || allowance.note) {
          items.push({
            name: allowance.name,
            value: allowance.amount,
            note: allowance.note
          });
        }
      });
    }

    if (this.payslipData.totalOvertimePay > 0) {
      items.push({ name: 'Làm thêm giờ (Overtime)', value: this.payslipData.totalOvertimePay });
    }

    if (this.payslipData.totalBonus > 0) {
      items.push({ name: 'Thưởng (Bonus)', value: this.payslipData.totalBonus });
    }

    return items;
  }

  // Map fields to Deduction Table
  get deductionItems(): PayslipItem[] {
    if (!this.payslipData) return [];
    const items: PayslipItem[] = [];

    if (this.payslipData.bhxhEmp > 0) items.push({ name: 'BHXH (NV đóng)', value: this.payslipData.bhxhEmp });
    if (this.payslipData.bhytEmp > 0) items.push({ name: 'BHYT (NV đóng)', value: this.payslipData.bhytEmp });
    if (this.payslipData.bhtnEmp > 0) items.push({ name: 'BHTN (NV đóng)', value: this.payslipData.bhtnEmp });
    if (this.payslipData.pit > 0) items.push({ name: 'Thuế TNCN', value: this.payslipData.pit });
    if (this.payslipData.totalPunishment > 0) items.push({ name: 'Phạt vi phạm', value: this.payslipData.totalPunishment });

    return items;
  }

  get totalDeduction(): number {
    if (!this.payslipData) return 0;
    return (this.payslipData.totalInsuranceEmp || 0) + (this.payslipData.pit || 0) + (this.payslipData.totalPunishment || 0);
  }

  get companyCostItems(): PayslipItem[] {
    if (!this.payslipData) return [];
    const items: PayslipItem[] = [];

    if (this.payslipData.bhxhComp > 0) items.push({ name: 'BHXH (Công ty đóng)', value: this.payslipData.bhxhComp });
    if (this.payslipData.bhytComp > 0) items.push({ name: 'BHYT (Công ty đóng)', value: this.payslipData.bhytComp });
    if (this.payslipData.bhtnComp > 0) items.push({ name: 'BHTN (Công ty đóng)', value: this.payslipData.bhtnComp });

    return items;
  }

  private getCookie(name: string): string | null {
    const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
    if (match) return match[2];
    return null;
  }
}