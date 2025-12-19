import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
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


@Component({
  selector: 'app-overtime',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, NgIf, NgFor],
  templateUrl: './overtime.html',
  styleUrls: ['./overtime.scss']
})
export class Overtime implements OnInit {
  overtimeList: OvertimeType[] = [];
  years: number[] = [];
  loading: boolean = false;
  submitting: boolean = false;
  showModal: boolean = false;
  isEditing: boolean = false;
  editingId: number | null = null;

  otForm: FormGroup;

  constructor(private fb: FormBuilder, private cdr: ChangeDetectorRef) {
    // Khởi tạo năm hiện tại và list năm


    // Init Form
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
    this.cdr.detectChanges();

  }

  async loadData() {
    this.loading = true;
    try {
      // Gọi API thực tế
      const res = await getOvertime();

      // Kiểm tra nếu API trả về lỗi (theo cấu trúc try-catch của bạn trả về error object)
      if (res instanceof Error || (res && res.status && res.status !== 200)) {
        console.error(res);
        this.overtimeList = [];
        this.cdr.detectChanges();

      } else {
        // Giả định res là mảng data trực tiếp theo hàm getOvertime bạn cung cấp
        this.overtimeList = Array.isArray(res) ? res : [];
        this.cdr.detectChanges();

      }
    } catch (error) {
      console.error(error);
      alert('Lỗi tải dữ liệu');
    } finally {
      this.loading = false;
      this.cdr.detectChanges();

    }
  }

  async deleteItem(id: number) {
    if (!confirm('Bạn có chắc chắn muốn xóa loại OT này không?')) return;

    try {
      const res: any = await DeleteOvertime(id);

      if (res.status === 200 || res.status === 204) {
        alert('Xóa thành công!');
        this.loadData();

        // Reload lại data hoặc filter mảng local để tránh gọi API lại
        this.overtimeList = this.overtimeList.filter(x => x.id !== id);
        this.cdr.detectChanges();

      } else {
        alert('Xóa thất bại');
      }
    } catch (error) {
      console.error(error);
      alert('Đã xảy ra lỗi khi xóa');
      this.cdr.detectChanges();

    }
  }

  // --- FORM HANDLING ---

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
    if (this.otForm.invalid) return;

    this.submitting = true;
    const formValue: OvertimeCreateRequest = this.otForm.value;

    try {
      let res: any;

      if (this.isEditing && this.editingId) {
        // Gọi API Update
        res = await putOvertime(this.editingId, formValue);
      } else {
        // Gọi API Create
        res = await postOvertime(formValue);
      }

      if (res && (res.status === 200 || res.status === 201)) {
        alert(this.isEditing ? 'Cập nhật thành công!' : 'Thêm mới thành công!');
        this.closeModal();
        this.loadData(); // Reload data từ server
      } else {
        alert('Lưu thất bại: ' + (res?.message || 'Lỗi không xác định'));
      }

    } catch (error) {
      console.error(error);
      alert('Có lỗi xảy ra khi lưu');
    } finally {
      this.submitting = false;
    }
  }
}