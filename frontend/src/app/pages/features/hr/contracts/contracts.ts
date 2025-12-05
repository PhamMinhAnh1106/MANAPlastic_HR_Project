import { DecimalPipe, DatePipe, NgFor, NgIf, NgClass } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, Injectable } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Loading } from '../../../shared/loading/loading';
import { CheckContractByIdEmployee, ExportFileDataContracts, FillterContract, FillterContractByIdEmployee } from '../../../../services/pages/features/hr/contracts.service';
import { buildQueryParams } from '../../../../utils/filters.utils';
import { Alert } from '../../../shared/alert/alert';

@Component({
  selector: 'app-contracts',
  standalone: true,
  imports: [FormsModule, NgIf, NgFor, NgClass, Loading, Alert, DecimalPipe, DatePipe],
  templateUrl: './contracts.html',
  styleUrls: ['./contracts.scss'],
})
export class Contracts implements OnInit {
  tab: string = 'check';
  isloading: boolean = false;
  employeeId: string = '';

  // Alert Props
  isalert: boolean = false;
  notifyMessage = "";
  notifyType: boolean = true;

  // Check Props
  messageCheckContract = "";

  // Search Props
  filters = {
    username: '',
    type: '',
    status: '',
    allowanceToxicType: '',
    startdate: '',
    enddate: ''
  };

  // Popup Props
  showPopup = false;
  popupMode: 'message' | 'list' = 'message';
  listContracts: any[] = [];

  // --- PAGINATION PROPS ---
  page: number = 0;
  size: number = 5;
  totalPages: number = 0;
  totalElements: number = 0;
  pageSizeOptions: number[] = [5, 10, 20, 50];

  statusContract = [
    'DRAFT',
    'ACTIVE',
    'EXPIRING_SOON',
    'EXPIRED',
    'TERMINATED',
    'HISTORY'
  ];

  constructor(private router: Router, private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    const savedTab = sessionStorage.getItem('activeTab');
    if (savedTab) {
      this.tab = savedTab;
    }
  }

  changeTab(tabName: string) {
    this.tab = tabName;
    sessionStorage.setItem('activeTab', tabName);
  }

  showNotification(message: string, type: boolean) {
    this.notifyMessage = message;
    this.notifyType = type;
    this.isalert = true;
  }

  closePopup() {
    this.showPopup = false;
  }

  addContract() {
    this.router.navigate(["/home/contracts/add"]);
  }

  // --- LOGIC CHECK (Giữ nguyên) ---
  async checkSignedContract() {
    if (this.employeeId == '') {
      this.showNotification("Vui lòng nhập Mã Nhân Viên", false);
      return;
    }
    this.isloading = true;
    try {
      const id = Number(this.employeeId);
      const res = await CheckContractByIdEmployee(id) as { data: string, status: number };
      this.messageCheckContract = res.data;
      this.popupMode = 'message';
      this.showPopup = true;
    } catch (e) {
      this.showNotification("Có lỗi xảy ra", false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  async viewEmployeeContracts() {
    if (this.employeeId == '') {
      this.showNotification("Vui lòng nhập Mã Nhân Viên", false);
      return;
    }
    this.isloading = true;
    try {
      const id = Number(this.employeeId);
      // API này chưa có phân trang theo yêu cầu cũ, hiển thị list tĩnh
      const res = await FillterContractByIdEmployee(id) as { data: any, status: number };

      if (res.status == 200) {
        this.listContracts = res.data;
      } else {
        this.listContracts = [];
      }
      this.popupMode = "list";
      // Reset phân trang UI để ẩn thanh phân trang hoặc hiển thị full
      this.totalElements = this.listContracts.length;
      this.totalPages = 1;

      this.showPopup = true;
    } catch (e) {
      this.showNotification("Có lỗi xảy ra", false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  // --- LOGIC SEARCH (CẬP NHẬT PHÂN TRANG) ---

  // 1. Hàm tìm kiếm khi nhấn nút "Tìm kiếm" (Reset về trang 0)
  async searchContract() {
    this.page = 0;
    await this.fetchContracts();
  }

  // 2. Hàm gọi API thực tế
  async fetchContracts() {
    const query = this.buildQuery(this.filters);
    this.isloading = true;
    try {
      // Gọi API FillterContract với page và size
      const res = await FillterContract(query, this.page, this.size) as { data: any, status: number };

      if (res.status == 200 && res.data) {
        // Xử lý dữ liệu trả về. 
        // Giả định res.data có cấu trúc Page (content, totalPages, totalElements)
        // Nếu API trả về mảng trực tiếp, cần điều chỉnh lại backend hoặc frontend

        if (Array.isArray(res.data)) {
          // Fallback nếu API vẫn trả về Array thay vì Page Object
          this.listContracts = res.data;
          this.totalElements = res.data.length;
          this.totalPages = 1;
        } else {
          // Cấu trúc chuẩn phân trang
          this.listContracts = res.data.content || [];
          this.totalPages = res.data.totalPages || 0;
          this.totalElements = res.data.totalElements || 0;
        }

        this.popupMode = "list";
        this.showPopup = true;
      } else {
        this.listContracts = [];
        this.totalElements = 0;
        this.showNotification("Không tìm thấy kết quả", false);
      }
    } catch (e) {
      this.showNotification("Có lỗi xảy ra", false);
      this.listContracts = [];
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  // 3. Sự kiện chuyển trang
  onPageChange(newPage: number) {
    if (newPage >= 0 && newPage < this.totalPages) {
      this.page = newPage;
      this.fetchContracts();
    }
  }

  // 4. Sự kiện đổi số dòng/trang
  onPageSizeChange() {
    this.page = 0;
    this.fetchContracts();
  }

  // --- UTILS ---
  buildQuery(filters: any): string {
    const params = new URLSearchParams();
    Object.keys(filters).forEach(key => {
      const value = filters[key];
      if (value !== null && value !== undefined && value !== '') {
        params.append(key, value);
      }
    });
    return params.toString();
  }

  async ExportExcel() {
    const query = buildQueryParams(this.filters);
    await ExportFileDataContracts(query);
  }

  copyLink(url: string) {
    navigator.clipboard.writeText(url)
      .then(() => {
        this.showNotification('Đã sao chép liên kết!', true);
      })
      .catch(err => {
        console.error('Lỗi copy link:', err);
      });
  }

  // Translate Functions
  translateContractStatus(status: string): string {
    switch (status) {
      case 'DRAFT': return 'Bản nháp';
      case 'ACTIVE': return 'Đang hiệu lực';
      case 'EXPIRING_SOON': return 'Sắp hết hạn';
      case 'EXPIRED': return 'Đã hết hạn';
      case 'TERMINATED': return 'Đã chấm dứt';
      case 'HISTORY': return 'Lịch sử';
      default: return status;
    }
  }

  getVietnameseContractType(type: string): string {
    switch (type) {
      case 'INDEFINITE': return 'Không thời hạn';
      case 'FIXED_TERM': return 'Có thời hạn';
      case 'PROBATION': return 'Thử việc';
      default: return type;
    }
  }

  translateAllowanceType(type: string): string {
    switch (type) {
      case 'NONE': return 'Không';
      case 'CASH': return 'Tiền mặt';
      case 'IN_KIND': return 'Hiện vật';
      default: return type;
    }
  }
}