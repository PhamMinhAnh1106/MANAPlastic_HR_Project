import { DecimalPipe, DatePipe, NgFor, NgIf, NgClass } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { DomSanitizer, SafeUrl, SafeResourceUrl } from '@angular/platform-browser';
import { Loading } from '../../../../shared/loading/loading';
import { Alert } from '../../../../shared/alert/alert';
import { Comfirm } from '../../../../shared/comfirm/comfirm';
import {
  CheckContractByIdEmployee,
  EditContract,
  ExportFileDataContracts,
  FillterContract,
  FillterContractByIdEmployee,
  getContractPdfUrl,
  getcontracttemplate,

} from '../../../../../services/pages/features/hr/contracts.service';
import { getContractFile } from '../../../../../utils/getimage.utils';
import { buildQueryParams } from '../../../../../utils/filters.utils';
import { ActiveaddAccount } from '../../../../../services/pages/features/admin/addAccount.service';
import { ContractRequest, CreateContractRequest, CreateContractrequest, getContractHistoryVersion, getContractRequests, ManageContractRequest } from '../../../../../services/pages/features/hr/contractRequest.service';

// --- INTERFACES ---
export interface Allowance {
  id?: number;
  allowanceCategoryId?: number;
  allowanceName: string;
  allowanceType: string;
  amount: number;
  isTaxable: boolean;
  isInsuranceBase: boolean;
  taxFreeAmount: number;
}

export interface CreateContractPayload {
  userId: number | null;
  departmentId: number;
  roleId: number;
  fullname: string;
  contractname?: string;
  cccd: string;
  email: string;
  phone: string;
  address: string;
  dob: string;
  gender: string;
  contractType: string;
  workType: string;
  templateId: number;
  signDate?: string;
  startDate: string;
  endDate: string | null;
  baseSalary: number;
  insuranceSalary?: number;
  insurancePercent: number;
  allowanceToxicType: string;
  allowances: Allowance[];
}

export interface EditContractParams extends CreateContractPayload {
  id: number;
}

export interface role {
  roleId: number;
  roleName: string;
}

export const Role: role[] = [
  { roleId: 1, roleName: "Admin" },
  { roleId: 2, roleName: "HR" },
  { roleId: 3, roleName: "Manager" },
  { roleId: 4, roleName: "Employee" },
];

export interface ContractTemplate {
  id: number;
  name: string;
  type?: string;
  content?: string;
  isActive?: boolean;
}

// Request & History Interfaces
export interface RequestData {
  requestId: number;
  reason: string;
  newFileUrl: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  createdAt: string;
  reviewedAt: string;
  adminNote: string;
  requesterId: number;
  requesterName: string;
  adminName: string;
  contractId: number;
  oldFileUrl: string;
  contractOwnerName: string;
}

export interface HistoryData {
  historyId: number;
  oldFileUrl: string;
  archivedAt: string;
  reasonForChange: string;
  requestedBy: string;
  contractId: number;
  approvedBy: string;
}

declare const api: any;

@Component({
  selector: 'app-contracts',
  standalone: true,
  imports: [FormsModule, NgIf, NgFor, NgClass, Loading, Alert, Comfirm, DecimalPipe, DatePipe],
  templateUrl: './contracts.html',
  styleUrls: ['./contracts.scss'],
})
export class Contracts implements OnInit {
  tab: string = 'search';
  isloading: boolean = false;
  employeeId: string = '';
  isAdmin: boolean = false;

  listRoles = Role;
  listTemplates: ContractTemplate[] = [];

  // [NEW] Request & History Lists
  requestKeyword: string = '';
  requestStatusFilter: string = 'ALL'; // Filter Status
  listRequests: RequestData[] = [];
  filteredRequests: RequestData[] = []; // Local Filtered List

  listHistory: HistoryData[] = [];
  historyContractId: number | null = null;
  hasSearchedHistory: boolean = false;

  // Alert Props
  isalert: boolean = false;
  notifyMessage = "";
  notifyType: boolean = true;
  messageCheckContract = "";

  // Confirm Props
  isconfirm: boolean = false;
  confirmMessage: string = "";
  private confirmResolve: ((result: boolean) => void) | null = null;

  // Search Props
  filters = {
    username: '',
    type: '',
    status: '',
    allowanceToxicType: '',
    startdate: '',
    enddate: ''
  };

  showPopup = false;
  popupMode: 'message' | 'list' = 'message';
  listContracts: any[] = [];
  recentDrafts: any[] = [];

  // Edit Props
  showEditPopup: boolean = false;
  isReadOnly: boolean = false;
  showExportButton: boolean = false;
  editForm: EditContractParams = this.getEmptyEditForm();
  currentStatus: string = '';
  currentStatusRaw: string = '';
  selectedFileName: string = '';
  displayDepartmentName: string = '';
  displayRoleName: string = '';

  // Active Props
  showActivePopup: boolean = false;
  selectedActiveContractId: number | null = null;
  activeFile: File | null = null;
  activeFileName: string = '';
  isActiveConfirmed: boolean = false;

  // Request Props
  showRequestPopup: boolean = false;
  requestUpdateForm = {
    contractId: 0,
    contractName: '',
    reason: '',
    file: null as File | null,
    fileName: ''
  };

  // Review Props
  showReviewPopup: boolean = false;
  reviewForm: RequestData | null = null;
  adminNoteInput: string = '';

  // History Props
  showHistoryPopup: boolean = false;

  // Preview Props
  showImagePreview = false;
  previewContentUrl: SafeUrl | SafeResourceUrl | null = null;
  fileType: 'image' | 'pdf' | null = null;
  private currentBlobUrl: string | null = null;

  // Pagination
  page: number = 0;
  size: number = 5;
  totalPages: number = 0;
  totalElements: number = 0;

  statusContract = ['ACTIVE', 'HISTORY', 'TERMINATED', 'DRAFT'];
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
    this.checkUserRole();
    this.loadRecentDrafts();
    this.fetchTemplates();

    if (this.tab === 'requests') {
      this.searchRequests();
    }
    this.cdr.detectChanges();
  }

  async fetchTemplates() {
    try {
      const res: any = await getcontracttemplate();
      if (res && Array.isArray(res)) {
        this.listTemplates = res;
      } else if (res && res.data && Array.isArray(res.data)) {
        this.listTemplates = res.data;
      } else if (res && res.id) {
        this.listTemplates = [res];
      } else {
        this.listTemplates = [];
      }
    } catch (e) {
      this.listTemplates = [];
    }
    this.cdr.detectChanges();
  }

  getEmptyEditForm(): EditContractParams {
    return {
      id: 0, userId: null, departmentId: 0, roleId: 0, fullname: '', contractname: '', cccd: '', email: '', phone: '', address: '', dob: '', gender: 'MALE', contractType: 'FIXED_TERM', workType: 'FULLTIME', templateId: 0, signDate: '', startDate: '', endDate: null, baseSalary: 0, insuranceSalary: 0, insurancePercent: 0, allowanceToxicType: 'NONE', allowances: []
    };
  }

  checkUserRole() {
    try {
      const userStr = localStorage.getItem('user');
      if (userStr) {
        const user = JSON.parse(userStr);
        this.isAdmin = user.role === 'ADMIN' || user.role === 'HR' || (Array.isArray(user.roles) && (user.roles.includes('ADMIN') || user.roles.includes('HR')));
      }
    } catch (e) {
      this.isAdmin = false;
    }
    this.cdr.detectChanges();
  }

  changeTab(tabName: string) {
    this.tab = tabName;
    sessionStorage.setItem('activeTab', tabName);
    if (tabName === 'search') {
      this.loadRecentDrafts();
    } else if (tabName === 'requests') {
      this.searchRequests();
    } else if (tabName === 'history') {
      this.listHistory = [];
      this.historyContractId = null;
      this.hasSearchedHistory = false;
    }
    this.cdr.detectChanges();
  }

  async loadRecentDrafts() {
    try {
      const query = "status=DRAFT";
      const res = await FillterContract(query, 0, 5) as { data: any, status: number };
      if (res.status == 200 && res.data) {
        if (Array.isArray(res.data)) {
          this.recentDrafts = res.data;
        } else {
          this.recentDrafts = res.data.content || [];
        }
        this.recentDrafts.sort((a, b) => b.id - a.id);
      } else {
        this.recentDrafts = [];
      }
    } catch (e) {
      this.recentDrafts = [];
    } finally {
      this.cdr.detectChanges();
    }
  }

  showNotification(message: string, type: boolean) {
    this.notifyMessage = message;
    this.notifyType = type;
    this.isalert = true;
    this.cdr.detectChanges();
  }

  async showConfirmDialog(message: string): Promise<boolean> {
    this.confirmMessage = message;
    this.isconfirm = true;
    this.cdr.detectChanges();
    return new Promise<boolean>((resolve) => {
      this.confirmResolve = resolve;
    });
  }

  onConfirmResult(result: boolean) {
    this.isconfirm = false;
    if (this.confirmResolve) {
      this.confirmResolve(result);
      this.confirmResolve = null;
    }
    this.cdr.detectChanges();
  }

  closePopup() {
    this.showPopup = false;
    this.cdr.detectChanges();
  }

  addContract() {
    this.router.navigate(["/home/contracts/edit/add"]);
  }

  openActivePopup(contract: any) {
    this.selectedActiveContractId = contract.id;
    this.activeFile = null;
    this.activeFileName = '';
    this.isActiveConfirmed = false;
    this.showActivePopup = true;
    this.cdr.detectChanges();
  }

  closeActivePopup() {
    this.showActivePopup = false;
    this.activeFile = null;
    this.activeFileName = '';
    this.cdr.detectChanges();
  }

  onActiveFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      if (file.type !== 'application/pdf') {
        this.showNotification("Chỉ chấp nhận file PDF", false);
        event.target.value = null;
        return;
      }
      this.activeFile = file;
      this.activeFileName = file.name;
    }
    this.cdr.detectChanges();
  }

  async submitActiveContract() {
    if (!this.activeFile) {
      this.showNotification("Vui lòng chọn file PDF", false);
      return;
    }
    if (!this.isActiveConfirmed) {
      this.showNotification("Vui lòng xác nhận chịu trách nhiệm trước khi Active.", false);
      return;
    }

    // Custom confirm
    const isConfirmed = await this.showConfirmDialog("Bạn có chắc chắn muốn lưu active tài khoản này? Sau khi active sẽ không thể thay đổi?");
    if (!isConfirmed) return;

    this.isloading = true;
    try {
      const res: any = await ActiveaddAccount(this.activeFile, this.selectedActiveContractId || 0);
      if (res && res.status === 200) {
        this.showNotification("Active hợp đồng thành công!", true);
        this.closeActivePopup();
        this.loadRecentDrafts();
        if (this.showPopup && this.popupMode === 'list') {
          this.fetchContracts();
        }
      } else {
        this.showNotification("Active thất bại: " + (res?.message || res.response.data.message), false);
      }
    } catch (e) {
      this.showNotification("Có lỗi xảy ra: " + e, false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  openEditContract(c: any) {
    this.isReadOnly = c.status !== 'DRAFT';

    this.showExportButton = this.isReadOnly;

    this.currentStatus = this.translateContractStatus(c.status);
    this.currentStatusRaw = c.status;

    this.displayDepartmentName = c.departmentName || 'Không có thông tin';

    let derivedRoleId = c.roleId || 0;
    if (c.roleName) {
      const foundRoleByName = Role.find(r => r.roleName === c.roleName);
      if (foundRoleByName) {
        derivedRoleId = foundRoleByName.roleId;
      }
    } else if (c.roleId) {
      // Logic fallback if needed
    }

    this.editForm = {
      id: c.id,
      contractname: c.contractName || c.contractname || '',
      contractType: c.type || c.contractType || 'FIXED_TERM',
      workType: c.workType || 'FULLTIME',
      templateId: c.templateId || 0,

      baseSalary: c.baseSalary || c.basesalary || 0,
      insurancePercent: c.insurancePercent || 0,
      insuranceSalary: c.insuranceSalary || (c.baseSalary && c.insurancePercent ? (c.baseSalary * c.insurancePercent / 100) : 0),
      allowanceToxicType: c.allowanceToxicType || 'NONE',

      signDate: this.formatDateForInput(c.signDate),
      startDate: this.formatDateForInput(c.startDate || c.signdate),
      endDate: this.formatDateForInput(c.endDate),

      userId: c.userId || null,
      departmentId: c.departmentId || 0,
      roleId: derivedRoleId,
      fullname: c.fullname || '',
      cccd: c.cccd || '',
      email: c.email || '',
      phone: c.phone || '',
      address: c.address || '',
      dob: c.dob ? this.formatDateForInput(c.dob) : '',
      gender: c.gender || 'MALE',

      allowances: Array.isArray(c.allowances) ? c.allowances.map((al: any) => ({
        id: al.id,
        allowanceCategoryId: al.allowanceCategoryId,
        allowanceName: al.allowanceName,
        allowanceType: al.allowanceType || 'MONTHLY',
        amount: al.amount || 0,
        isTaxable: al.isTaxable || false,
        isInsuranceBase: al.isInsuranceBase || false,
        taxFreeAmount: al.taxFreeAmount || 0
      })) : []
    };

    this.selectedFileName = '';
    this.showEditPopup = true;
    this.cdr.detectChanges();
  }

  addAllowance() {
    if (this.isReadOnly) return;
    this.editForm.allowances.push({
      allowanceName: '',
      allowanceType: 'MONTHLY',
      amount: 0,
      isTaxable: false,
      isInsuranceBase: false,
      taxFreeAmount: 0
    });
    this.cdr.detectChanges();
  }

  removeAllowance(index: number) {
    if (this.isReadOnly) return;
    this.editForm.allowances.splice(index, 1);
    this.cdr.detectChanges();
  }

  closeEditPopup() {
    this.showEditPopup = false;
    this.cdr.detectChanges();
  }

  onFileSelected(event: any) {
    if (!this.isAdmin || this.isReadOnly) return;
    const file: File = event.target.files[0];
    if (file) {
      const validTypes = ['application/pdf', 'image/jpeg', 'image/png', 'image/jpg'];
      if (!validTypes.includes(file.type)) {
        this.showNotification("Chỉ chấp nhận file PDF hoặc Ảnh (JPG, PNG)", false);
        return;
      }
      this.selectedFileName = file.name;
    }
    this.cdr.detectChanges();
  }

  async submitEditContract() {
    if (this.isReadOnly) return;
    if (!this.editForm.startDate) {
      this.showNotification("Ngày bắt đầu là bắt buộc", false);
      return;
    }
    if (this.editForm.baseSalary < 0) {
      this.showNotification("Lương không được âm", false);
      return;
    }

    // [ADDED] Confirm for Update
    const isConfirmed = await this.showConfirmDialog("Bạn có chắc chắn muốn cập nhật thông tin hợp đồng này?");
    if (!isConfirmed) return;

    this.isloading = true;
    try {
      const res: any = await EditContract(this.editForm);
      if (res && res.status === 200) {
        this.showNotification("Cập nhật hợp đồng thành công!", true);

        this.showExportButton = true;

        this.loadRecentDrafts();
        if (this.showPopup && this.popupMode === 'list') {
          this.fetchContracts();
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

  async exportContractPdf() {
    if (!this.editForm.id) return;

    // Custom confirm
    const confirmed = await this.showConfirmDialog("Tải xuống file hợp đồng PDF?");
    if (!confirmed) return;

    this.isloading = true;
    const resultUrl = await getContractPdfUrl(this.editForm.id);
    this.isloading = false;

    if (resultUrl && resultUrl.startsWith('blob:')) {
      window.open(resultUrl, '_blank');
    } else {
      this.showNotification(resultUrl, false);
    }
    this.cdr.detectChanges();
  }

  // --- NEW METHOD: View Generated PDF ---
  async viewPrintContract(id: number) {
    this.isloading = true;
    try {
      // Gọi service lấy file blob/stream
      const response: any = await getContractPdfUrl(id);

      // Xử lý dữ liệu trả về để tạo Blob
      let blobData;
      if (response instanceof Blob) {
        blobData = response;
      } else if (response && response.data) {
        blobData = new Blob([response.data], { type: 'application/pdf' });
      } else {
        // Trường hợp trả về text/string hoặc khác, cố gắng ép kiểu
        blobData = new Blob([response], { type: 'application/pdf' });
      }

      // Tạo URL và hiển thị popup
      const url = URL.createObjectURL(blobData);
      this.previewContentUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
      this.fileType = 'pdf';
      this.showImagePreview = true;
      this.currentBlobUrl = url; // Lưu để revoke sau này
    } catch (error) {
      console.error(error);
      this.showNotification("Không thể tải file in hợp đồng. Vui lòng thử lại sau.", false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  // --- REQUEST UPDATE LOGIC (HR) ---
  openRequestUpdatePopup(c: any) {
    this.requestUpdateForm = {
      contractId: c.id,
      contractName: c.contractname || c.contractName || `Hợp đồng #${c.id}`,
      reason: '',
      file: null,
      fileName: ''
    };
    this.showRequestPopup = true;
    this.cdr.detectChanges();
  }

  closeRequestPopup() {
    this.showRequestPopup = false;
    this.cdr.detectChanges();
  }

  onRequestFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      if (file.type !== 'application/pdf') {
        this.showNotification("Chỉ chấp nhận file PDF", false);
        return;
      }
      this.requestUpdateForm.file = file;
      this.requestUpdateForm.fileName = file.name;
    }
    this.cdr.detectChanges();
  }

  async submitRequestUpdate() {
    if (!this.requestUpdateForm.file) {
      this.showNotification("Vui lòng đính kèm file hợp đồng mới", false);
      return;
    }
    if (!this.requestUpdateForm.reason.trim()) {
      this.showNotification("Vui lòng nhập lý do cập nhật", false);
      return;
    }

    const isConfirmed = await this.showConfirmDialog("Bạn có chắc chắn muốn gửi yêu cầu cập nhật hợp đồng này?");
    if (!isConfirmed) return;

    this.isloading = true;
    try {
      const payload: CreateContractrequest = {
        contractId: this.requestUpdateForm.contractId,
        reason: this.requestUpdateForm.reason,
        file: this.requestUpdateForm.file
      };

      await CreateContractRequest(payload);
      this.showNotification("Gửi yêu cầu thành công!", true);
      this.closeRequestPopup();
      if (this.tab === 'requests') this.searchRequests();
    } catch (e) {
      this.showNotification("Gửi yêu cầu thất bại: " + e, false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  // --- REQUESTS TAB LOGIC (ADMIN/HR) ---
  async searchRequests() {
    this.isloading = true;
    try {
      const res: any = await getContractRequests(this.requestKeyword);

      if (res?.content && Array.isArray(res.content)) {
        this.listRequests = res.content;
      } else if (Array.isArray(res)) {
        this.listRequests = res;
      } else {
        this.listRequests = [];
      }


      // Apply local filter after fetching
      this.filterRequestsLocal();
    } catch (e) {
      this.listRequests = [];
      this.filteredRequests = [];
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  // Filter list on client side
  filterRequestsLocal() {
    if (this.requestStatusFilter === 'ALL') {
      this.filteredRequests = this.listRequests;
    } else {
      this.filteredRequests = this.listRequests.filter(req => req.status === this.requestStatusFilter);
    }
    this.cdr.detectChanges();
  }

  openReviewPopup(req: RequestData) {
    this.reviewForm = req;
    this.adminNoteInput = '';
    this.showReviewPopup = true;
    this.cdr.detectChanges();
  }

  closeReviewPopup() {
    this.showReviewPopup = false;
    this.reviewForm = null;
    this.cdr.detectChanges();
  }

  async submitReview(status: 'APPROVED' | 'REJECTED') {
    if (!this.reviewForm) return;

    const actionText = status === 'APPROVED' ? 'DUYỆT' : 'TỪ CHỐI';
    const isConfirmed = await this.showConfirmDialog(`Bạn có chắc chắn muốn ${actionText} yêu cầu này?`);
    if (!isConfirmed) return;

    this.isloading = true;
    try {
      const body: ContractRequest = {
        status: status,
        adminNote: this.adminNoteInput
      };
      const res = await ManageContractRequest(this.reviewForm.requestId, body) as { data: any, status: number };
      if (res && res.status === 200) {
        this.showNotification(`Đã ${actionText} yêu cầu thành công!`, true);
        this.closeReviewPopup();
        this.searchRequests();
      } else {
        this.showNotification("Xử lý thất bại.", false);
      }
    } catch (e) {
      this.showNotification("Lỗi server: " + e, false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  // --- HISTORY TAB LOGIC ---
  async searchHistory() {
    if (!this.historyContractId) {
      this.showNotification("Vui lòng nhập ID hợp đồng", false);
      return;
    }
    this.isloading = true;
    this.hasSearchedHistory = true;
    try {
      const res = await getContractHistoryVersion(this.historyContractId);
      if (Array.isArray(res)) {
        this.listHistory = res;
      } else if (res && Array.isArray(res.data)) {
        this.listHistory = res.data;
      } else {
        this.listHistory = [];
      }
    } catch (e) {
      this.listHistory = [];
      this.showNotification("Không tìm thấy lịch sử hoặc lỗi server", false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  async viewContractHistory(contractId: number) {
    this.historyContractId = contractId;
    this.tab = 'history'; // Switch to history tab
    this.showPopup = false; // [NEW] Close search popup if open
    this.searchHistory(); // Load history for the selected ID
    this.cdr.detectChanges();
  }

  closeHistoryPopup() {
    this.showHistoryPopup = false;
    this.listHistory = [];
    this.cdr.detectChanges();
  }

  // --- UTILS ---
  formatDateForInput(dateStr: string | Date): string {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) return '';
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2);
    const day = ('0' + date.getDate()).slice(-2);
    return `${year}-${month}-${day}`;
  }

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
    this.cdr.detectChanges();
  }

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