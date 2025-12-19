import { ChangeDetectorRef, Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormsModule } from '@angular/forms';
// Import các function API và interface từ service của bạn
import {
  LeavePolicy,
  LeaveTypeDetail,
  PostLeavePolicy,
  getleavePolicies,
  postleavePolicies,
  putleavePolicies,
  DeleteleavePolicies
} from '../../../../../services/pages/features/admin/legal.service';

@Component({
  selector: 'app-leave-policies',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, NgFor, NgIf],
  templateUrl: './leave-policies.html',
  styleUrls: ['./leave-policies.scss'],
})
export class LeavePolicies implements OnInit {
  // State Signals
  policies = signal<LeavePolicy[]>([]);
  isLoading = signal<boolean>(false);
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

  // NOTE: List này có thể lấy từ API getLeaveTypes nếu có, hoặc để tĩnh như hiện tại
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
    this.cdr.detectChanges();

  }

  // --- API INTEGRATION ---

  async fetchPolicies() {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    try {
      // Gọi function từ service thay vì this.http.get
      // Convert year to string as per your API definition: getleavePolicies(year: string)
      const data = await getleavePolicies();

      // Kiểm tra nếu API trả về lỗi (theo khối try/catch trong định nghĩa hàm của bạn)
      if (data instanceof Error) {
        throw data;
      }

      this.policies.set(data as LeavePolicy[]);
    } catch (error: any) {
      console.error('Lỗi khi tải dữ liệu:', error);
      this.errorMessage.set(`Không thể tải dữ liệu: ${error.message || 'Lỗi không xác định'}`);
      this.policies.set([]);
    } finally {
      this.isLoading.set(false);
    }
  }

  async onSubmit() {
    if (this.policyForm.invalid) {
      this.policyForm.markAllAsTouched();
      return;
    }

    this.isSaving = true;
    this.errorMessage.set(null);

    // Mapping Form Data
    const formValue = this.policyForm.value;
    const selectedType = this.staticLeaveTypes.find(t => t.id === +formValue.leaveTypeId);

    const payload: PostLeavePolicy = {
      description: formValue.description,
      days: formValue.days,
      minYearsService: formValue.minYearsService,
      maxYearsService: formValue.maxYearsService,
      leaveTypeId: formValue.leaveTypeId,
      genderTarget: formValue.genderTarget,
      leaveType: selectedType?.shiftnameAsEnum
    };

    try {
      let res;
      if (this.isEditMode && this.currentPolicyId) {
        // Call Service: PUT
        res = await putleavePolicies(this.currentPolicyId, payload);
      } else {
        // Call Service: POST
        res = await postleavePolicies(payload);
      }

      // Check result (based on your service definition returning {data, status} or error)
      if (res instanceof Error) {
        throw res;
      }

      // Nếu thành công (status 200/201), tải lại dữ liệu
      await this.fetchPolicies();
      this.closeModal();

    } catch (error: any) {
      console.error('Lỗi khi lưu:', error);
      this.errorMessage.set(`Lỗi khi lưu: ${error.message || 'Backend không phản hồi'}`);
    } finally {
      this.isSaving = false;
    }
  }

  async deletePolicy(id: number) {
    if (!confirm('Bạn có chắc chắn muốn xóa chính sách này?')) return;

    try {
      // Call Service: DELETE
      const res = await DeleteleavePolicies(id);

      if (res instanceof Error) {
        throw res;
      }

      await this.fetchPolicies();
    } catch (error: any) {
      console.error('Lỗi khi xóa:', error);
      alert(`Không thể xóa: ${error.message || 'Lỗi kết nối'}`);
    }
  }

  // --- UI ACTIONS ---

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
        leaveTypeId: policy.leavetypeid?.id, // Thêm optional chaining đề phòng null
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
  }

  getBadgeColor(type: string): string {
    const t = (type || '').toLowerCase();
    if (t.includes('annual')) return 'annual';
    if (t.includes('sick')) return 'sick';
    if (t.includes('maternity')) return 'maternity';
    return 'default';
  }
}