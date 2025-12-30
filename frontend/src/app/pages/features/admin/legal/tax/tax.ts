import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe, NgFor, NgIf } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { getTaxAll, getTaxQuery, postTax, putTax, SystemSetting } from '../../../../../services/pages/features/admin/legal.service';

// Import Shared Components
import { Alert } from '../../../../shared/alert/alert';
import { Comfirm } from '../../../../shared/comfirm/comfirm';
import { Loading } from '../../../../shared/loading/loading';

@Component({
  selector: 'app-tax',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, NgIf, NgFor, DatePipe, Alert, Comfirm, Loading],
  templateUrl: './tax.html',
  styleUrls: ['./tax.scss'],
})
export class Tax implements OnInit {
  // --- UI STATE ---
  isconfirm: boolean = false;
  isalert: boolean = false;
  isloading: boolean = false;

  confirmMessage = '';
  alertmessage = '';
  alertType: boolean = true; // true = success, false = error

  // --- DATA STATE ---
  taxList: SystemSetting[] = [];
  isSubmitting = false; // Local loading for modal button

  // Modal State
  showModal = false;
  isEditing = false;
  currentId: number | null = null;
  searchQuery = '';

  taxForm: FormGroup;
  private fb = inject(FormBuilder);

  constructor(private cdr: ChangeDetectorRef) {
    this.taxForm = this.fb.group({
      settingKey: ['', Validators.required],
      value: [null, [Validators.required, Validators.min(0)]],
      effectiveDate: [new Date().toISOString().split('T')[0], Validators.required],
      description: [''],
      isActive: [true]
    });
  }

  ngOnInit(): void {
    this.loadData();
  }

  // --- SHARED UI HANDLERS ---
  Onalert(message: string, type: boolean) {
    this.isalert = true;
    this.alertmessage = message;
    this.alertType = type;
    setTimeout(() => this.isalert = false, 3000);
  }

  onConfirmResult(event: boolean) {
    this.isconfirm = false;
    // Hiện tại trang này chưa có chức năng xóa cần confirm, 
    // nhưng giữ handler này để mở rộng sau này hoặc nếu thêm chức năng xóa.
  }

  // --- LOGIC GỌI API ---

  async loadData() {
    this.isloading = true;
    try {
      if (this.searchQuery.trim()) {
        this.taxList = await getTaxQuery(this.searchQuery);
      } else {
        this.taxList = await getTaxAll();
      }
    } catch (error: any) {
      console.error('Lỗi tải dữ liệu:', error);
      this.Onalert('Có lỗi xảy ra khi tải dữ liệu.', false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  onSearch() {
    this.loadData();
  }

  async onSubmit() {
    if (this.taxForm.invalid) {
      this.taxForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    const formValue = this.taxForm.value;

    try {
      let res: any;
      if (this.isEditing && this.currentId) {
        res = await putTax(this.currentId, formValue);
      } else {
        res = await postTax(formValue);
      }

      // Kiểm tra kết quả trả về (giả định cấu trúc trả về tương tự các trang kia)
      if (res && (res.status === 200 || res.status === 201 || res.data)) {
        this.Onalert(this.isEditing ? 'Cập nhật thành công!' : 'Thêm mới thành công!', true);
        this.closeModal();
        this.loadData();
      } else {
        throw new Error('Lưu thất bại');
      }
    } catch (error: any) {
      console.error('Lỗi lưu dữ liệu:', error);
      this.Onalert('Lỗi khi lưu: ' + (error.message || 'Không xác định'), false);
    } finally {
      this.isSubmitting = false;
      this.cdr.detectChanges();
    }
  }

  // --- MODAL & FORM HELPERS ---

  openCreateModal() {
    this.isEditing = false;
    this.currentId = null;
    this.taxForm.reset({
      settingKey: '',
      value: null,
      effectiveDate: new Date().toISOString().split('T')[0],
      description: '',
      isActive: true
    });
    this.showModal = true;
  }

  openEditModal(item: SystemSetting) {
    this.isEditing = true;
    this.currentId = item.id;

    // Format date cho input type="date"
    const formattedDate = item.effectiveDate ? item.effectiveDate.split('T')[0] : '';

    this.taxForm.patchValue({
      settingKey: item.settingKey,
      value: item.value,
      effectiveDate: formattedDate,
      description: item.description,
      isActive: item.isActive
    });
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
  }

  isFieldInvalid(field: string): boolean {
    const control = this.taxForm.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }
}