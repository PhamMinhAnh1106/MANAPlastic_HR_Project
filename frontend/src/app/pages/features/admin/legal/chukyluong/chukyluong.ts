import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { chukiluong, ChuKyLuongItem, getChukiluong, postChukiluong, putChukiluong } from '../../../../../services/pages/features/admin/legal.service';
import { Alert } from '../../../../shared/alert/alert';
import { Comfirm } from '../../../../shared/comfirm/comfirm';
import { Loading } from '../../../../shared/loading/loading';



// --- MAIN COMPONENT ---
@Component({
  selector: 'app-chukyluong',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, NgIf, NgFor, Alert, Comfirm, Loading],
  templateUrl: './chukyluong.html',
  styleUrls: ['./chukyluong.scss']
})
export class chukyluong implements OnInit {
  isconfirm: boolean = false;
  isalert: boolean = false;
  isloading: boolean = false;
  confirmMessage = '';
  alertmessage = '';
  alertType: boolean = true;

  Onalert(message: string, type: boolean) {
    this.isalert = true;
    this.alertmessage = message;
    this.alertType = type;
  }
  private fb = inject(FormBuilder);

  currentYear: number = new Date().getFullYear();
  items: ChuKyLuongItem[] = [];
  isLoading = false;
  isSubmitting = false;

  showGenerateModal = false;
  showEditModal = false;
  selectedItem: ChuKyLuongItem | null = null;
  message: { type: 'success' | 'error', text: string } | null = null;

  generateForm: FormGroup;
  editForm: FormGroup;

  constructor(private cdr: ChangeDetectorRef) {
    this.generateForm = this.fb.group({
      year: [this.currentYear, Validators.required],
      standardWorkDays: [24, [Validators.required, Validators.min(24)]],
      cycleStartDay: [1, [Validators.required, Validators.min(1), Validators.max(31)]],
      isCyclePreviousMonth: [false]
    });

    this.editForm = this.fb.group({
      standardWorkDays: [0, [Validators.required, Validators.min(24)]]
    });
  }

  ngOnInit() {
    this.loadData();
    this.cdr.detectChanges();

  }

  async loadData() {
    this.isLoading = true;
    this.message = null;
    try {
      // Gọi trực tiếp hàm API
      const data = await getChukiluong(this.currentYear.toString());
      this.cdr.detectChanges();

      if (Array.isArray(data)) {
        this.items = data.sort((a, b) => a.month - b.month);
        this.cdr.detectChanges();

      } else {
        this.items = [];
        // Nếu API trả về lỗi thay vì mảng, có thể xử lý ở đây
      }
    } catch (error) {
      this.showMessage('error', 'Không thể tải dữ liệu chu kỳ lương.');
    } finally {
      this.isLoading = false;
      this.cdr.detectChanges();

    }
  }

  changeYear(delta: number) {
    this.currentYear += delta;
    this.loadData();
  }

  openGenerateModal() {
    this.generateForm.patchValue({
      year: this.currentYear,
      standardWorkDays: 24,
      cycleStartDay: 1,
      isCyclePreviousMonth: false
    });
    this.showGenerateModal = true;
    this.message = null;
  }

  openEditModal(item: ChuKyLuongItem) {
    this.selectedItem = item;
    this.editForm.patchValue({
      standardWorkDays: item.standardWorkDays
    });
    this.showEditModal = true;
    this.message = null;
  }

  closeModals() {
    this.showGenerateModal = false;
    this.showEditModal = false;
    this.selectedItem = null;
  }

  async onGenerateSubmit() {
    if (this.generateForm.invalid) return;

    this.isSubmitting = true;

    // Mapping form angular sang interface chukiluong
    const formValue: chukiluong = {
      year: this.generateForm.value.year.toString(),
      standardWorkDays: this.generateForm.value.standardWorkDays,
      cycleStartDay: this.generateForm.value.cycleStartDay,
      isCyclePreviousMonth: this.generateForm.value.isCyclePreviousMonth
    };

    try {
      const res: any = await postChukiluong(formValue);

      // Kiểm tra status trả về từ hàm postChukiluong
      if (res && res.status === 200) {
        this.Onalert(res.data, true);
        this.closeModals();

        if (parseInt(formValue.year || '0') !== this.currentYear) {
          this.currentYear = parseInt(formValue.year || '0');
        }
        this.loadData();
      } else {
        this.Onalert(res.data || 'Lỗi khi tạo chu kỳ lương.', false);
      }
    } catch (error) {
      this.showMessage('error', 'Lỗi hệ thống khi tạo chu kỳ lương.');
    } finally {
      this.isSubmitting = false;
    }
  }

  async onEditSubmit() {
    if (this.editForm.invalid || !this.selectedItem) return;

    this.isSubmitting = true;
    const formValue: chukiluong = {
      standardWorkDays: this.editForm.value.standardWorkDays
    };

    try {
      const res: any = await putChukiluong(this.selectedItem.id, formValue);

      if (res && res.status === 200) {
        this.Onalert(res.data, true);
        this.closeModals();
        this.loadData();
      } else {
        this.Onalert(res.data || 'Lỗi khi cập nhật chu kỳ.', false);
      }
    } catch (error) {
      this.showMessage('error', 'Lỗi khi cập nhật.');
    } finally {
      this.isSubmitting = false;
    }
  }
  async onConfirmResult(event: any) { }
  showMessage(type: 'success' | 'error', text: string) {
    this.message = { type, text };
    if (type === 'success') {
      setTimeout(() => {
        if (this.message?.text === text) {
          this.message = null;
        }
      }, 3000);
    }
  }
}