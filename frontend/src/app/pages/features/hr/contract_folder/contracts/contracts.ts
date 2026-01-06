import { DecimalPipe, DatePipe, NgFor, NgIf, NgClass } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { DomSanitizer, SafeUrl, SafeResourceUrl } from '@angular/platform-browser';
import { Loading } from '../../../../shared/loading/loading';
import { Alert } from '../../../../shared/alert/alert';
import { CheckContractByIdEmployee, EditContract, EditContractInterface, ExportFileDataContracts, FillterContract, FillterContractByIdEmployee } from '../../../../../services/pages/features/hr/contracts.service';
import { getContractFile } from '../../../../../utils/getimage.utils';
import { buildQueryParams } from '../../../../../utils/filters.utils';

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

  // --- EDIT POPUP PROPS ---
  showEditPopup: boolean = false;

  // [UPDATE] Cập nhật form theo Interface mới
  editForm: EditContractInterface = {
    id: 0,
    username: '',
    contractName: '',
    type: 'FIXED_TERM',
    baseSalary: 0,
    insuranceSalary: 0,
    allowanceToxicType: 'NONE',
    signDate: '',
    startDate: '',
    endDate: '',
    status: 'ACTIVE',
    file: null
  };

  // Biến hiển thị tên file đã chọn
  selectedFileName: string = '';

  // --- FILE PREVIEW PROPS ---
  showImagePreview = false;
  previewContentUrl: SafeUrl | SafeResourceUrl | null = null;
  fileType: 'image' | 'pdf' | null = null;
  private currentBlobUrl: string | null = null;

  // --- PAGINATION PROPS ---
  page: number = 0;
  size: number = 5;
  totalPages: number = 0;
  totalElements: number = 0;
  pageSizeOptions: number[] = [5, 10, 20, 50];

  statusContract = [
    'ACTIVE', 'HISTORY', 'TERMINATED'
  ];

  contractTypes = [
    { value: 'INDEFINITE', label: 'Không xác định thời hạn' },
    { value: 'FIXED_TERM', label: 'Có thời hạn' },
    { value: 'PROBATION', label: 'Thử việc' }
  ];

  allowanceToxicTypes = [
    { value: 'NONE', label: 'Không áp dụng' },
    { value: 'CASH', label: 'Tiền mặt' },
    { value: 'IN_KIND', label: 'Hiện vật' }
  ];

  constructor(
    private router: Router,
    private cdr: ChangeDetectorRef,
    private sanitizer: DomSanitizer
  ) { }

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

  // --- [UPDATE] LOGIC SỬA HỢP ĐỒNG ---
  openEditContract(contract: any) {
    // Map dữ liệu từ row (thường là snake_case hoặc lowercase từ DB) sang CamelCase của Interface
    this.editForm = {
      id: contract.id,
      username: contract.username || '',
      contractName: contract.contractname || '',
      type: contract.type || 'FIXED_TERM',
      baseSalary: contract.basesalary || 0,
      // Kiểm tra xem API trả về tên trường là gì, ở đây giả định là insurancesalary
      insuranceSalary: contract.insurancesalary || 0,
      allowanceToxicType: contract.allowancetoxictype || 'NONE',
      signDate: this.formatDateForInput(contract.signdate),
      startDate: this.formatDateForInput(contract.startdate),
      endDate: this.formatDateForInput(contract.enddate),
      status: contract.status || 'ACTIVE',
      file: null // Reset file
    };

    this.selectedFileName = ''; // Reset tên file hiển thị
    this.showEditPopup = true;
  }

  closeEditPopup() {
    this.showEditPopup = false;
  }

  // Xử lý khi người dùng chọn file
  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      const validTypes = ['application/pdf', 'image/jpeg', 'image/png', 'image/jpg'];
      if (!validTypes.includes(file.type)) {
        this.showNotification("Chỉ chấp nhận file PDF hoặc Ảnh (JPG, PNG)", false);
        return;
      }
      if (file.size > 5 * 1024 * 1024) {
        this.showNotification("File không được quá 5MB", false);
        return;
      }
      this.editForm.file = file;
      this.selectedFileName = file.name;
    }
  }

  async submitEditContract() {
    // Validate các trường bắt buộc mới
    if (!this.editForm.contractName || !this.editForm.signDate || !this.editForm.startDate || !this.editForm.type || !this.editForm.status) {
      this.showNotification("Vui lòng điền các thông tin bắt buộc (*)", false);
      return;
    }

    if (this.editForm.baseSalary < 0 || this.editForm.insuranceSalary < 0) {
      this.showNotification("Lương không được âm", false);
      return;
    }

    this.isloading = true;
    try {
      const res: any = await EditContract(this.editForm);

      if (res && res.status === 200) {
        this.showNotification("Cập nhật hợp đồng thành công!", true);
        this.closeEditPopup();

        // Refresh lại list
        if (this.popupMode === 'list') {
          if (this.employeeId) {
            this.viewEmployeeContracts();
          } else {
            this.searchContract();
          }
        }
      } else {
        this.showNotification("Cập nhật thất bại: " + (res?.data?.message || "Lỗi không xác định"), false);
      }
    } catch (e) {
      this.showNotification("Lỗi kết nối server", false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  formatDateForInput(dateStr: string | Date): string {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) return '';
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2);
    const day = ('0' + date.getDate()).slice(-2);
    return `${year}-${month}-${day}`;
  }


  // --- LOGIC XEM FILE ---
  async viewContractImage(fileName: string) {
    if (!fileName) return;

    this.isloading = true;
    this.showImagePreview = true;

    if (this.currentBlobUrl) {
      URL.revokeObjectURL(this.currentBlobUrl);
      this.currentBlobUrl = null;
    }
    this.previewContentUrl = null;
    this.fileType = null;

    try {
      const blobData = await getContractFile(fileName);

      if (blobData && blobData instanceof Blob) {
        this.currentBlobUrl = URL.createObjectURL(blobData);
        const mimeType = blobData.type;

        if (mimeType.includes('pdf')) {
          this.fileType = 'pdf';
          this.previewContentUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.currentBlobUrl);
        } else {
          this.fileType = 'image';
          this.previewContentUrl = this.sanitizer.bypassSecurityTrustUrl(this.currentBlobUrl);
        }
      } else {
        this.previewContentUrl = null;
      }
    } catch (error) {
      console.error(error);
      this.previewContentUrl = null;
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  closeImagePreview() {
    this.showImagePreview = false;
    this.previewContentUrl = null;
    this.fileType = null;
    if (this.currentBlobUrl) {
      URL.revokeObjectURL(this.currentBlobUrl);
      this.currentBlobUrl = null;
    }
  }

  // --- LOGIC CHECK & SEARCH ---
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
      const res = await FillterContractByIdEmployee(id) as { data: any, status: number };

      if (res.status == 200) {
        this.listContracts = res.data;
      } else {
        this.listContracts = [];
      }
      this.popupMode = "list";
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

  async searchContract() {
    this.page = 0;
    await this.fetchContracts();
  }

  async fetchContracts() {
    const query = this.buildQuery(this.filters);
    this.isloading = true;
    try {
      const res = await FillterContract(query, this.page, this.size) as { data: any, status: number };

      if (res.status == 200 && res.data) {
        if (Array.isArray(res.data)) {
          this.listContracts = res.data;
          this.totalElements = res.data.length;
          this.totalPages = 1;
        } else {
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

  onPageChange(newPage: number) {
    if (newPage >= 0 && newPage < this.totalPages) {
      this.page = newPage;
      this.fetchContracts();
    }
  }

  onPageSizeChange() {
    this.page = 0;
    this.fetchContracts();
  }

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
    this.cdr.detectChanges();
  }

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
}