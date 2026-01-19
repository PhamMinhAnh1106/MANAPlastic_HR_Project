import { ChangeDetectorRef, Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormsModule } from '@angular/forms';

import {
  LeavePolicy,
  LeaveTypeDetail,
  PostLeavePolicy,
  getleavePolicies,
  postleavePolicies,
  putleavePolicies,
  DeleteleavePolicies
} from '../../../../../services/pages/features/admin/legal.service';
import { Alert } from '../../../../shared/alert/alert';
import { Comfirm } from '../../../../shared/comfirm/comfirm';
import { Loading } from '../../../../shared/loading/loading'; // Đã thêm

@Component({
  selector: 'app-leave-policies',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, NgFor, NgIf, Alert, Comfirm, Loading],
  templateUrl: './leave-policies.html',
  styleUrls: ['./leave-policies.scss'],
})
export class LeavePolicies implements OnInit {

  isconfirm: boolean = false;
  isalert: boolean = false;
  // Thêm biến isloading để dùng với app-loading
  isloading: boolean = false;

  confirmMessage = '';
  alertmessage = '';
  alertType: boolean = true;

  // Biến để lưu tạm payload và loại hành động chờ xác nhận
  pendingPayload: PostLeavePolicy | null = null;
  submittype: 'create' | 'update' | 'delete' | '' = '';

  Onalert(message: string, type: boolean) {
    this.isalert = true;
    this.alertmessage = message;
    this.alertType = type;
    setTimeout(() => this.isalert = false, 3000);
  }

  // State Signals
  policies = signal<LeavePolicy[]>([]);
  // isLoading signal vẫn giữ cho logic nội bộ nếu cần, nhưng đồng bộ với isloading biến thường

  isModalOpen = signal<boolean>(false);
  errorMessage = signal<string | null>(null);

  // Data & Forms
  selectedYear: number = new Date().getFullYear();
  isEditMode = false;
  currentPolicyId: number | null = null;
  isSaving = false;
  policyForm: FormGroup;

  // Dependency Injection
  private fb = inject(FormBuilder);

  staticLeaveTypes: LeaveTypeDetail[] = [
    { id: 53, shiftname: 'AL (Anually Leave)', starttime: '08:00:00', endtime: '17:00:00', durationHours: 8, shiftnameAsEnum: 'ANNUAL' },
    { id: 54, shiftname: 'SL (Sick Leave)', starttime: '00:00:00', endtime: '00:00:00', durationHours: 0, shiftnameAsEnum: 'SICK' },
    { id: 55, shiftname: 'UP (Unpaid Leave)', starttime: '00:00:00', endtime: '00:00:00', durationHours: 0, shiftnameAsEnum: 'UNPAID' },
    { id: 56, shiftname: 'ML (Maternity Leave)', starttime: '00:00:00', endtime: '00:00:00', durationHours: 0, shiftnameAsEnum: 'MATERNITY' }
  ];

  constructor(private cdr: ChangeDetectorRef) {
    this.policyForm = this.fb.group({
      description: ['', Validators.required],
      days: [0, [Validators.required, Validators.min(0)]],
      minYearsService: [0, [Validators.required, Validators.min(0)]],
      maxYearsService: [null],
      leaveTypeId: [null, Validators.required],
      genderTarget: [null]
    });
  }

  ngOnInit() {
    this.fetchPolicies();
  }

  async fetchPolicies() {
    this.isloading = true; // Bật loading UI
    this.errorMessage.set(null);
    try {
      const data = await getleavePolicies();
      if (data instanceof Error) {
        throw data;
      }
      this.policies.set(data as LeavePolicy[]);
    } catch (error: any) {
      this.errorMessage.set(`Không thể tải dữ liệu: ${error.message || 'Lỗi không xác định'}`);
      this.policies.set([]);
    } finally {
      this.isloading = false; // Tắt loading UI
      this.cdr.detectChanges();
    }
  }

  onSubmit() {
    if (this.policyForm.invalid) {
      this.policyForm.markAllAsTouched();
      return;
    }
    this.errorMessage.set(null);
    const formValue = this.policyForm.value;
    const selectedType = this.staticLeaveTypes.find(t => t.id === +formValue.leaveTypeId);

    this.pendingPayload = {
      description: formValue.description,
      days: formValue.days,
      minYearsService: formValue.minYearsService,
      maxYearsService: formValue.maxYearsService,
      leaveTypeId: formValue.leaveTypeId,
      genderTarget: formValue.genderTarget,
      leaveType: selectedType?.shiftnameAsEnum
    };

    if (this.isEditMode && this.currentPolicyId) {
      this.submittype = "update";
      this.confirmMessage = "Bạn có chắc chắn muốn cập nhật chính sách này?";
    } else {
      this.submittype = "create";
      this.confirmMessage = "Bạn có chắc chắn muốn tạo chính sách này?";
    }
    this.isconfirm = true;
  }

  async onConfirmResult(event: any) {
    this.isconfirm = false;

    if (
      event !== true ||
      (this.submittype !== 'delete' && !this.pendingPayload) ||
      (this.submittype === 'delete' && !this.Idelete)
    ) {
      this.pendingPayload = null;
      this.Idelete = 0;
      return;
    }

    this.isloading = true; // Dùng loading chung thay vì isSaving cục bộ cho modal

    try {
      let res: any;
      if (this.submittype === "create") {
        res = await postleavePolicies(this.pendingPayload!);
      }
      else if (this.submittype === "update" && this.currentPolicyId) {
        res = await putleavePolicies(this.currentPolicyId, this.pendingPayload!);
      }
      else if (this.submittype === "delete") {
        const deleteRes = await DeleteleavePolicies(this.Idelete) as { data: string, status: number };
        if (deleteRes.status !== 200) {
          throw new Error(deleteRes.data || 'Lỗi khi xóa');
        }
        res = deleteRes;
      }

      if (res instanceof Error) {
        throw res;
      }

      this.Onalert("Thao tác thành công!", true);
      await this.fetchPolicies();
      this.closeModal();

    } catch (error: any) {
      this.errorMessage.set(`Lỗi: ${error.response.data.message || 'Hệ thống đang bận'}`);
      this.Onalert(error.response.data.message || "Đã xảy ra lỗi, vui lòng thử lại.", false);
    } finally {
      this.isloading = false;
      this.isSaving = false;
      this.pendingPayload = null;
      this.Idelete = 0;
      this.submittype = '';
      this.cdr.detectChanges();
    }
  }

  Idelete: number = 0;
  async deletePolicy(id: number) {
    this.Idelete = id;
    this.submittype = "delete";
    this.pendingPayload = null;
    this.confirmMessage = "Bạn có chắc chắn muốn xóa chính sách này?";
    this.isconfirm = true;
  }

  openModal(policy?: LeavePolicy) {
    this.isModalOpen.set(true);
    this.errorMessage.set(null);
    if (policy) {
      this.isEditMode = true;
      this.currentPolicyId = policy.id;
      this.policyForm.patchValue({
        description: policy.description,
        days: policy.days,
        minYearsService: policy.minyearsservice,
        maxYearsService: policy.maxyearsservice,
        leaveTypeId: policy.leavetypeid?.id,
        genderTarget: policy.gendertarget
      });
    } else {
      this.isEditMode = false;
      this.currentPolicyId = null;
      this.policyForm.reset({
        days: 12,
        minYearsService: 0,
        genderTarget: null
      });
    }
  }

  closeModal() {
    this.isModalOpen.set(false);
    this.pendingPayload = null;
  }

  getBadgeColor(type: string): string {
    const t = (type || '').toLowerCase();
    if (t.includes('annual')) return 'annual';
    if (t.includes('sick')) return 'sick';
    if (t.includes('maternity')) return 'maternity';
    return 'default';
  }
}