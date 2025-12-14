import { Component, signal } from '@angular/core';
import { Fillterpayslip } from '../../../../../services/pages/features/hr/payroll/payslip.services';
import { HttpParams } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { DecimalPipe, NgFor, NgIf } from '@angular/common';
interface PayrollItem {
  userId: number;
  fullName: string;
  departmentName: string;
  jobType: string;
  payPeriod: string;
  baseSalary: number;
  actualWorkDays: number;
  totalOvertimePay: number | null;
  totalAllowance: number | null;
  totalIncome: number;
  taxableIncome: number | null;
  pit: number;
  netSalary: number;
  bhxhEmp: number;
  bhytEmp: number;
  bhtnEmp: number;
  bhxhComp: number;
  bhytComp: number;
  bhtnComp: number;
}

interface PageableResponse {
  content: PayrollItem[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
}
@Component({
  selector: 'app-filterpayslip',
  imports: [FormsModule, NgIf, NgFor, DecimalPipe],
  templateUrl: './filterpayslip.html',
  styleUrl: './filterpayslip.scss',
})
export class Filterpayslip {
  data = signal<PageableResponse | null>(null);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);
  page = signal<number>(0);
  size = signal<number>(10);
  sortField = signal<string | null>(null);
  sortDirection = signal<'asc' | 'desc'>('asc');

  filters = {
    departmentId: null as number | null,
    minSalary: null as number | null,
    maxSalary: null as number | null,
    payPeriod: '' as string
  };

  ngOnInit() {
    this.fetchData();
  }

  // --- Logic Tạo Query String (Đã bỏ params sort) ---
  private buildQueryParams(): HttpParams {
    let params = new HttpParams()
      .set('page', this.page().toString())
      .set('size', this.size().toString());

    if (this.filters.departmentId) params = params.set('departmentId', this.filters.departmentId.toString());
    if (this.filters.minSalary) params = params.set('minSalary', this.filters.minSalary.toString());
    if (this.filters.maxSalary) params = params.set('maxSalary', this.filters.maxSalary.toString());
    if (this.filters.payPeriod) params = params.set('payPeriod', this.filters.payPeriod);

    // Không thêm sort vào params nữa vì chúng ta sort client-side
    return params;
  }

  // --- Logic Xử lý Click Header để Sort (Client-side) ---
  onSort(field: keyof PayrollItem | string) {
    // 1. Cập nhật state hướng sort
    if (this.sortField() === field) {
      this.sortDirection.update(d => d === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortField.set(field);
      this.sortDirection.set('asc');
    }

    // 2. Lấy data hiện tại
    const currentData = this.data();
    if (!currentData || !currentData.content) return;

    // 3. COPY và SORT mảng content
    const sortedContent = [...currentData.content].sort((a, b) => {
      // Ép kiểu để truy cập thuộc tính động
      const valueA = (a as any)[field];
      const valueB = (b as any)[field];

      const direction = this.sortDirection() === 'asc' ? 1 : -1;

      // Xử lý null/undefined
      if (valueA == null) return 1; // Đẩy null xuống cuối
      if (valueB == null) return -1;

      // So sánh chuỗi
      if (typeof valueA === 'string' && typeof valueB === 'string') {
        return valueA.localeCompare(valueB) * direction;
      }

      // So sánh số
      if (valueA < valueB) return -1 * direction;
      if (valueA > valueB) return 1 * direction;
      return 0;
    });

    // 4. Cập nhật lại signal data (chỉ thay đổi content, giữ nguyên pagination info)
    this.data.set({
      ...currentData,
      content: sortedContent
    });
  }

  async fetchData() {
    this.loading.set(true);
    this.error.set(null);
    // Reset sort state khi load data mới từ server
    this.sortField.set(null);
    this.sortDirection.set('asc');

    const queryString = this.buildQueryParams().toString();

    try {
      const response = await Fillterpayslip(queryString);
      this.data.set(response);
    } catch (err: any) {
      console.error('API Error:', err);
      this.error.set(err.message || 'Không thể tải dữ liệu.');
      this.data.set(null);
    } finally {
      this.loading.set(false);
    }
  }

  onFilter() {
    this.page.set(0);
    this.fetchData();
  }

  changePage(newPage: number) {
    if (newPage >= 0 && (!this.data() || newPage < (this.data()?.totalPages || 0))) {
      this.page.set(newPage);
      this.fetchData();
    }
  }

  resetFilters() {
    this.filters = {
      departmentId: null,
      minSalary: null,
      maxSalary: null,
      payPeriod: ''
    };
    this.page.set(0);
    this.fetchData();
  }
}
