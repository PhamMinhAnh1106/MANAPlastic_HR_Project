import { ChangeDetectorRef, Component, OnInit, signal } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormsModule } from '@angular/forms';

import {
  getOvertime,
  postOvertime,
  putOvertime,
  DeleteOvertime,
  OvertimeType,
  OvertimeCreateRequest
} from '../../../../../services/pages/features/admin/legal.service';

// Import shared components
import { Alert } from '../../../../shared/alert/alert';
import { Comfirm } from '../../../../shared/comfirm/comfirm';
import { Loading } from '../../../../shared/loading/loading';

@Component({
  selector: 'app-overtime',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, NgIf, NgFor, Alert, Comfirm, Loading],
  templateUrl: './overtime.html',
  styleUrls: ['./overtime.scss']
})
export class Overtime implements OnInit {
  // --- UI STATE SIGNALS & VARIABLES ---
  isconfirm: boolean = false;
  isalert: boolean = false;
  isloading: boolean = false;

  confirmMessage = '';
  alertmessage = '';
  alertType: boolean = true; // true = success, false = error

  // --- DATA STATE ---
  overtimeList: OvertimeType[] = [];
  submitting: boolean = false;
  showModal: boolean = false;
  isEditing: boolean = false;
  editingId: number | null = null;

  // Biến tạm để xử lý confirm
  pendingAction: 'delete' | null = null;
  pendingId: number | null = null;

  otForm: FormGroup;

  constructor(private fb: FormBuilder, private cdr: ChangeDetectorRef) {
    this.otForm = this.fb.group({
      otCode: ['', Validators.required],
      otName: ['', Validators.required],
      rate: [1.0, [Validators.required, Validators.min(0)]],
      calculationType: ['MULTIPLIER', Validators.required],
      isTaxExemptPart: [false],
      taxExemptFormula: ['NONE'],
      taxExemptPercentage: [0],
      description: ['']
    });
  }

  ngOnInit() {
    this.loadData();
  }

  // --- SHARED UI HANDLERS ---
  Onalert(message: string, type: boolean) {
    this.isalert = true;
    this.alertmessage = message;
    this.alertType = type;
    setTimeout(() => this.isalert = false, 3000); // Auto hide
  }

  async onConfirmResult(event: boolean) {
    this.isconfirm = false;
    if (event && this.pendingAction === 'delete' && this.pendingId) {
      await this.confirmDelete(this.pendingId);
    }
    // Reset pending state
    this.pendingAction = null;
    this.pendingId = null;
  }

  // --- DATA LOADING ---
  async loadData() {
    this.isloading = true;
    try {
      const res = await getOvertime();
      if (res instanceof Error || (res && (res as any).status && (res as any).status !== 200)) {
        this.overtimeList = [];
      } else {
        this.overtimeList = Array.isArray(res) ? res : [];
      }
    } catch (error) {
      this.Onalert('Lỗi tải dữ liệu', false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  // --- DELETE LOGIC ---
  deleteItem(id: number) {
    this.pendingAction = 'delete';
    this.pendingId = id;
    this.confirmMessage = 'Bạn có chắc chắn muốn xóa loại làm thêm giờ này không?';
    this.isconfirm = true;
  }

  async confirmDelete(id: number) {
    this.isloading = true;
    try {
      const res: any = await DeleteOvertime(id);
      if (res.status === 200 || res.status === 204) {
        this.Onalert('Xóa thành công!', true);
        this.overtimeList = this.overtimeList.filter(x => x.id !== id);
      } else {
        this.Onalert(res.data, false);
      }
    } catch (error: any) {
      this.Onalert(error.response.data.message || "Đã xảy ra lỗi khi xóa", false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  // --- MODAL & FORM LOGIC ---
  openModal(item?: OvertimeType) {
    this.showModal = true;
    if (item) {
      this.isEditing = true;
      this.editingId = item.id;
      this.otForm.patchValue({
        otCode: item.otCode,
        otName: item.otName,
        rate: item.rate,
        calculationType: item.calculationType,
        isTaxExemptPart: item.isTaxExemptPart,
        taxExemptFormula: item.taxExemptFormula || 'NONE',
        taxExemptPercentage: item.taxExemptPercentage || 0,
        description: item.description
      });
    } else {
      this.isEditing = false;
      this.editingId = null;
      this.otForm.reset({
        rate: 1.0,
        calculationType: 'MULTIPLIER',
        isTaxExemptPart: false,
        taxExemptFormula: 'NONE',
        taxExemptPercentage: 0
      });
    }
  }

  closeModal() {
    this.showModal = false;
    this.otForm.reset();
  }

  async onSubmit() {
    if (this.otForm.invalid) {
      this.otForm.markAllAsTouched();
      return;
    }

    this.submitting = true;
    const formValue: OvertimeCreateRequest = this.otForm.value;

    try {
      let res: any;
      if (this.isEditing && this.editingId) {
        res = await putOvertime(this.editingId, formValue);
      } else {
        res = await postOvertime(formValue);
      }

      if (res && (res.status === 200 || res.status === 201)) {
        this.Onalert(this.isEditing ? 'Cập nhật thành công!' : 'Thêm mới thành công!', true);
        this.closeModal();
        await this.loadData();
      } else {
        this.Onalert(res?.reponse.data.message || 'Lưu thất bại', false);
      }
    } catch (error: any) {
      this.Onalert(error, false);
    } finally {
      this.submitting = false;
      this.cdr.detectChanges();
    }
  }
}