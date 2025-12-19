import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe, NgFor, NgIf } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators, NgModel } from '@angular/forms';
import { getTaxAll, getTaxQuery, postTax, putTax, SystemSetting } from '../../../../../services/pages/features/admin/legal.service';

@Component({
  selector: 'app-tax',
  imports: [ReactiveFormsModule, DatePipe, FormsModule, NgIf, NgFor],
  templateUrl: './tax.html',
  styleUrl: './tax.scss',
})
export class Tax implements OnInit {
  taxList: SystemSetting[] = [];
  isLoading = false;
  isSubmitting = false;
  errorMessage = '';

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

  // --- LOGIC GỌI API ---

  async loadData() {
    this.isLoading = true;
    this.errorMessage = '';

    try {
      if (this.searchQuery.trim()) {
        this.taxList = await getTaxQuery(this.searchQuery);
        this.cdr.detectChanges();
      } else {
        this.taxList = await getTaxAll();
        this.cdr.detectChanges();

      }
    } catch (error: any) {
      console.error('Lỗi tải dữ liệu:', error);
      this.errorMessage = 'Có lỗi xảy ra khi tải dữ liệu. Vui lòng thử lại.';
    } finally {
      this.isLoading = false;
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
    this.errorMessage = '';
    const formValue = this.taxForm.value;

    try {
      let res;
      if (this.isEditing && this.currentId) {
        // Gọi hàm putTax
        res = await putTax(this.currentId, formValue) as { data: string, status: number };
      } else {
        // Gọi hàm postTax
        res = await postTax(formValue) as { data: string, status: number };
      }

      // Kiểm tra kết quả trả về
      if (res && (res.status === 200 || res.status === 201 || res.data)) {
        this.closeModal();
        this.loadData(); // Tải lại danh sách
      } else {
        throw new Error('Lưu thất bại');
      }
    } catch (error: any) {
      console.error('Lỗi lưu dữ liệu:', error);
      this.errorMessage = 'Lỗi khi lưu: ' + (error.message || 'Không xác định');
    } finally {
      this.isSubmitting = false;
      this.cdr.detectChanges();
    }
  }

  // --- UI HELPERS ---

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
    this.errorMessage = '';
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
    this.errorMessage = '';
  }

  closeModal() {
    this.showModal = false;
  }

  isFieldInvalid(field: string): boolean {
    const control = this.taxForm.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }
}