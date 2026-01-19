import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormsModule } from '@angular/forms';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { CookieService } from 'ngx-cookie-service';

// Import các component/service gốc của dự án
import { Alert } from '../../../shared/alert/alert';
import { Comfirm } from '../../../shared/comfirm/comfirm';
import { Loading } from '../../../shared/loading/loading';
import { DocumentItem, getDocumentEmployeebyid, getDocumentFile, getDocumentsByUserId, getListDocumentEmployee, getMyDocuments, uploadProfile } from '../../../../services/pages/documentUser.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, Alert, Comfirm, Loading],
  templateUrl: './documentsuser.html',
  styleUrls: ['./documentsuser.scss']
})
export class Documentsuser implements OnInit {

  private fb = inject(FormBuilder);
  private sanitizer = inject(DomSanitizer);

  // --- State Variables ---
  isconfirm: boolean = false;
  isalert: boolean = false;
  isloading: boolean = false;
  confirmMessage = '';
  alertmessage = '';
  alertType: boolean = true;

  private pendingConfirmAction: (() => void) | null = null;

  // --- App State ---
  currentRole: string = '';
  userId: number = 0;

  // Tab Management: 'approval', 'search', 'personal'
  activeTab: string = 'personal';

  // Data Sources
  hrDocuments: DocumentItem[] = []; // List duyệt (HR)
  myDocuments: DocumentItem[] = []; // List cá nhân (All roles)
  searchedDocuments: DocumentItem[] = []; // List tìm kiếm (HR)

  // Search State
  searchUserId: string = '';
  searchKeyword: string = 'ALL';
  hasSearched: boolean = false;

  // --- Forms & Modals ---
  uploadForm: FormGroup;
  selectedFile: File | null = null;
  selectedFileName: string = '';
  fileError: boolean = false;

  showUploadModal: boolean = false;
  showApprovalModal: boolean = false;
  showPreviewModal: boolean = false;

  approvalTarget: DocumentItem | null = null;
  approvalAction: string = '';
  approvalNote: string = '';
  documentID: number = 0;
  previewUrl: SafeResourceUrl | null = null;

  constructor(private cookie: CookieService, private cdr: ChangeDetectorRef) {
    this.uploadForm = this.fb.group({
      documentName: ['', Validators.required],
      documentType: ['', Validators.required],
      issuingAuthority: ['', Validators.required],
      issueDate: ['', Validators.required],
      expiryDate: [''],
      note: ['']
    });
  }

  ngOnInit() {
    this.currentRole = this.cookie.get('role').toLowerCase();

    const storedUserId = sessionStorage.getItem('userId');
    if (storedUserId) {
      this.userId = Number(storedUserId);
    }

    // Determine initial tab
    const storedTab = sessionStorage.getItem('activeTab');
    if (storedTab && ['approval', 'search', 'personal'].includes(storedTab)) {
      if (this.currentRole !== 'hr' && (storedTab === 'approval' || storedTab === 'search')) {
        this.switchTab('personal');
      } else {
        this.activeTab = storedTab;
      }
    } else {
      this.activeTab = (this.currentRole === 'hr') ? 'approval' : 'personal';
    }

    this.loadData();
  }

  // --- Tab Handling ---
  switchTab(tab: string) {
    this.activeTab = tab;
    sessionStorage.setItem('activeTab', tab);

    if (tab !== 'search') {
      this.hasSearched = false;
      this.searchedDocuments = [];
    }
    this.loadData();
  }

  // --- Data Loading ---
  async loadData() {
    this.isloading = true;
    try {
      if (this.activeTab === 'approval' && this.currentRole === 'hr') {
        const data = await getListDocumentEmployee();
        this.hrDocuments = Array.isArray(data) ? data : [];
      }
      else if (this.activeTab === 'personal') {
        const data = await getMyDocuments();
        this.myDocuments = Array.isArray(data.content) ? data.content : [];
      }
    } catch (error) {
      console.error(error);
      this.Onalert('Lỗi tải dữ liệu', false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  // --- Actions ---

  async onSearchEmployee() {
    if (!this.searchUserId.trim()) {
      this.Onalert('Vui lòng nhập ID nhân viên.', false);
      return;
    }

    const userIdNum = parseInt(this.searchUserId);
    if (isNaN(userIdNum)) {
      this.Onalert('ID nhân viên phải là số.', false);
      return;
    }

    this.isloading = true;
    this.hasSearched = true;
    try {
      const data = await getDocumentEmployeebyid(userIdNum, this.searchKeyword);
      this.searchedDocuments = Array.isArray(data.content) ? data.content : [];
    } catch (error) {
      console.error(error);
      this.searchedDocuments = [];
      this.Onalert('Không tìm thấy dữ liệu.', false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  // --- Upload Logic ---
  openUploadModal() { this.showUploadModal = true; }

  closeUploadModal() {
    this.showUploadModal = false;
    this.uploadForm.reset({ documentType: '' });
    this.selectedFile = null;
    this.selectedFileName = '';
    this.fileError = false;
  }

  onFileSelect(event: any) {
    if (event.target.files.length > 0) {
      this.selectedFile = event.target.files[0];
      this.selectedFileName = this.selectedFile?.name || '';
      this.fileError = false;
    }
  }

  isFieldInvalid(field: string): boolean {
    const control = this.uploadForm.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  async onUpload() {
    this.uploadForm.markAllAsTouched();
    if (!this.selectedFile) this.fileError = true;
    if (this.uploadForm.invalid || !this.selectedFile) return;

    this.openConfirmDialog('Bạn có chắc chắn muốn tải lên hồ sơ này?', async () => {
      this.isloading = true;
      try {
        const formVal = this.uploadForm.value;
        const uploadData: any = { ...formVal, file: this.selectedFile, userId: this.userId };
        await uploadProfile(uploadData);
        this.Onalert('Upload thành công!', true);
        this.closeUploadModal();
        this.loadData();
      } catch (error) {
        this.Onalert('Upload thất bại.', false);
      } finally {
        this.isloading = false;
        this.cdr.detectChanges();
      }
    });
  }

  // =========================================================
  // FIX: XỬ LÝ BLOB ĐỂ HIỂN THỊ FILE PDF
  // =========================================================
  async viewFile(filename: string) {
    if (!filename) return;
    this.isloading = true;
    try {
      const response = await getDocumentFile(filename); // Gọi API

      let blob: Blob;

      if (response instanceof Blob) {
        // Optimization: If it's already a Blob, you often don't need to wrap it again
        blob = response;
      } else if (response) {
        // It is not a Blob, but it is not null (e.g., a string or buffer)
        blob = new Blob([response as BlobPart], { type: 'application/pdf' });
      } else {
        // 'response' is null or undefined. Create an empty Blob.
        blob = new Blob([], { type: 'application/pdf' });
      }

      // Tạo Object URL để nhúng vào iframe
      const url = window.URL.createObjectURL(blob);

      // Sanitize URL để Angular chấp nhận
      this.previewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
      this.showPreviewModal = true;

    } catch (error) {
      console.error(error);
      this.Onalert('Lỗi khi tải file xem trước.', false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  closePreview() {
    this.showPreviewModal = false;
    // Dọn dẹp URL khi đóng modal (Optional, nhưng tốt cho memory)
    this.previewUrl = null;
  }

  async downloadFile(filename: string) {
    if (!filename) return;
    this.isloading = true;
    try {
      const response = await getDocumentFile(filename);

      let blob: Blob;

      if (response instanceof Blob) {
        // Optimization: If it's already a Blob, you often don't need to wrap it again
        blob = response;
      } else if (response) {
        // It is not a Blob, but it is not null (e.g., a string or buffer)
        blob = new Blob([response as BlobPart], { type: 'application/pdf' });
      } else {
        // 'response' is null or undefined. Create an empty Blob.
        blob = new Blob([], { type: 'application/pdf' });
      }

      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);

      setTimeout(() => window.URL.revokeObjectURL(url), 100);

    } catch (error) {
      console.error(error);
      this.Onalert('Lỗi tải xuống.', false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }
  // =========================================================

  // --- Approval Logic ---
  openApprovalModal(documentID: number, doc: DocumentItem, action: string) {
    this.documentID = documentID;
    this.approvalTarget = doc;
    this.approvalAction = action;
    this.approvalNote = '';
    this.showApprovalModal = true;
  }

  async submitApproval() {
    if (this.approvalNote == '') {
      this.Onalert('Vui lòng nhập lý do.', false);
      return;
    }
    const actionText = this.approvalAction === 'APPROVED' ? 'duyệt' : 'từ chối';

    this.openConfirmDialog(`Bạn có chắc chắn muốn ${actionText} hồ sơ này?`, async () => {
      this.isloading = true;
      try {
        const res = await getDocumentsByUserId({
          status: this.approvalAction,
          note: this.approvalNote
        }, this.documentID) as { data: any, status: number, response: any };

        if (res.status == 200) {
          this.showApprovalModal = false;
          this.Onalert(`${actionText} hồ sơ thành công!`, true);
          this.loadData();
          return;
        }
        this.Onalert(res.response?.data?.message || 'Lỗi xử lý', false);

      } catch (error) {
        this.Onalert('Lỗi xử lý yêu cầu.', false);
      } finally {
        this.isloading = false;
        this.cdr.detectChanges();
      }
    });
  }

  // --- Shared Helpers ---
  Onalert(message: string, type: boolean) {
    this.isalert = true;
    this.alertmessage = message;
    this.alertType = type;
  }

  openConfirmDialog(message: string, action: () => void) {
    this.confirmMessage = message;
    this.pendingConfirmAction = action;
    this.isconfirm = true;
  }

  onConfirmResult(result: boolean) {
    this.isconfirm = false;
    if (result && this.pendingConfirmAction) {
      this.pendingConfirmAction();
    }
    this.pendingConfirmAction = null;
  }

  getStatusClass(status: string): string {
    if (!status) return '';
    const s = status.toUpperCase();
    if (s.includes('PENDING')) return 'status-pending';
    if (s.includes('APPROVE')) return 'status-approved';
    if (s.includes('REJECT')) return 'status-rejected';
    return '';
  }
}