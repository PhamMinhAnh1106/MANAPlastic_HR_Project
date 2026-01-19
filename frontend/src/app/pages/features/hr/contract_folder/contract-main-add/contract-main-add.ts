import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormArray, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router, RouterModule } from '@angular/router'; // Import Router

import { Allowance, contracttemplate, createContract, CreateContractPayload, getcontracttemplate }
  from '../../../../../services/pages/features/hr/contracts.service';
import { Loading } from '../../../../shared/loading/loading';
import { Alert } from '../../../../shared/alert/alert';
import { Comfirm } from '../../../../shared/comfirm/comfirm';
import { Department, Role } from '../../../../../interface/user/user.interface';

// --- CUSTOM VALIDATOR: Không cho chọn ngày quá khứ ---
function futureDateValidator(control: AbstractControl): ValidationErrors | null {
  if (!control.value) return null;

  const selectedDate = new Date(control.value);
  const today = new Date();
  today.setHours(0, 0, 0, 0); // Reset giờ về 0 để so sánh ngày

  if (selectedDate < today) {
    return { pastDate: true };
  }
  return null;
}

@Component({
  selector: 'app-contract-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, Loading, Alert, Comfirm, RouterModule],
  templateUrl: './contract-main-add.html',
  styleUrls: ['./contract-main-add.scss']
})
export class ContractCreate implements OnInit {
  constructor(
    private cdr: ChangeDetectorRef,
    private router: Router
  ) { }

  private fb = inject(FormBuilder);

  // Expose data to template
  departmentList = Department;
  roleList = Role;

  get todayStr(): string {
    return new Date().toISOString().split('T')[0];
  }

  // --- STATE ---
  isloading: boolean = false;
  isconfirm: boolean = false;
  confirmMessage = "";
  isalert: boolean = false;
  notifyMessage = "";
  notifyType: boolean = true;

  currentContractId: number | null = null;

  sections = {
    config: true,
    profile: true,
    salary: true,
    allowance: true
  };

  allTemplates: contracttemplate[] = [];
  filteredTemplates: contracttemplate[] = [];

  // Form Definition
  contractForm: FormGroup = this.fb.group({
    contractType: ['FIXED_TERM', Validators.required],
    templateId: ['', Validators.required],
    fullname: ['Nguyễn Văn A', Validators.required],
    cccd: ['079123456789', [Validators.required, Validators.pattern(/^\d{12}$/)]],
    dob: ['1995-01-01', Validators.required],
    gender: ['MALE'],
    phone: ['0987654321', [Validators.pattern(/^0\d{9}$/)]],
    email: ['a.nguyen@test.com', [Validators.email]],
    address: ['TP. Thủ Đức, TP. Hồ Chí Minh'],

    departmentId: [1, [Validators.required]],
    roleId: [4, [Validators.required]],

    workType: ['FULLTIME'],
    startDate: [this.todayStr, [Validators.required, futureDateValidator]],
    endDate: [''],

    baseSalary: [15000000, [Validators.required, Validators.min(1)]],
    insurancePercent: [100, [Validators.min(0), Validators.max(100)]],
    allowances: this.fb.array([])
  });

  get allowances() {
    return this.contractForm.get('allowances') as FormArray;
  }

  ngOnInit() {
    this.loadTemplates();
    this.addAllowance();

    this.contractForm.get('contractType')?.valueChanges.subscribe(val => {
      const endDateControl = this.contractForm.get('endDate');
      if (val === 'INDEFINITE') {
        endDateControl?.setValue(null);
        endDateControl?.disable();
      } else {
        endDateControl?.enable();
      }
      this.filterTemplates();
    });
  }

  showNotification(message: string, type: boolean) {
    this.notifyMessage = message;
    this.notifyType = type;
    this.isalert = true;
  }

  async loadTemplates() {
    this.isloading = true;
    this.allTemplates = await getcontracttemplate();
    this.filterTemplates();
    this.isloading = false;
  }

  filterTemplates() {
    const type = this.contractForm.get('contractType')?.value;
    this.filteredTemplates = this.allTemplates.filter(t =>
      t.type && (t.type === type || t.type === 'ALL')
    );

    const currentId = Number(this.contractForm.get('templateId')?.value);
    const exists = this.filteredTemplates.find(t => (t.templateID || t.id) === currentId);
    if (!exists) {
      this.contractForm.patchValue({ templateId: '' });
    }
  }

  addAllowance() {
    const group = this.fb.group({
      allowanceName: ['', Validators.required],
      allowanceAmount: [0, [Validators.required, Validators.min(1)]],
      isTaxable: [false]
    });
    this.allowances.push(group);
  }

  removeAllowance(index: number) {
    this.allowances.removeAt(index);
  }

  toggleSection(key: keyof typeof this.sections) {
    this.sections[key] = !this.sections[key];
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.contractForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  // --- ACTIONS ---

  // Hàm đóng Modal
  closeModal() {
    // Quay lại danh sách hợp đồng
    this.router.navigate(['/home/contracts']);
  }

  resetForm() {
    this.confirmMessage = "Bạn có chắc muốn làm mới form? Dữ liệu chưa lưu sẽ mất.";
    this.isconfirm = true;
  }

  onConfirmResult(result: boolean) {
    this.isconfirm = false;
    if (result) {
      this.contractForm.reset({
        contractType: 'FIXED_TERM',
        fullname: '',
        cccd: '',
        baseSalary: 0,
        insurancePercent: 100,
        startDate: this.todayStr,
        departmentId: 1,
        roleId: 4
      });
      this.allowances.clear();
      this.addAllowance();
      this.currentContractId = null;
      this.sections = { config: true, profile: true, salary: true, allowance: true };

      this.showNotification("Đã làm mới form", true);
    }
  }

  async onSubmit() {
    if (this.contractForm.invalid) {
      this.contractForm.markAllAsTouched();
      this.sections = { config: true, profile: true, salary: true, allowance: true };
      this.showNotification("Vui lòng kiểm tra lại các trường báo đỏ.", false);
      return;
    }

    this.isloading = true;
    const formVal = this.contractForm.getRawValue();

    const payload: CreateContractPayload = {
      userId: null,
      departmentId: Number(formVal.departmentId),
      roleId: Number(formVal.roleId),
      fullname: formVal.fullname,
      cccd: formVal.cccd,
      email: formVal.email,
      phone: formVal.phone,
      address: formVal.address,
      dob: formVal.dob,
      gender: formVal.gender,
      contractType: formVal.contractType,
      workType: formVal.workType,
      templateId: Number(formVal.templateId),
      startDate: formVal.startDate,
      endDate: formVal.endDate || null,
      baseSalary: Number(formVal.baseSalary),
      insurancePercent: Number(formVal.insurancePercent),
      allowanceToxicType: '',
      allowances: formVal.allowances
        .filter((a: any) => a.allowanceName && a.allowanceAmount)
        .map((a: any): Allowance => ({
          allowanceName: a.allowanceName,
          allowanceType: "MONTHLY",
          amount: Number(a.allowanceAmount),
          isTaxable: a.isTaxable,
          isInsuranceBase: false,
          taxFreeAmount: 0
        }))
    };

    try {
      const result = await createContract(payload) as { data: any, status: number, response?: any };
      this.isloading = false;

      if (result && result.status === 200 && result.data) {
        this.currentContractId = result.data.contractId || result.data.id;
        this.sections.config = false;
        this.showNotification(result.data.message, true);

        // Đóng modal sau khi lưu thành công (delay một chút để user đọc thông báo)
        setTimeout(() => {
          this.closeModal();
        }, 1000);

      } else {
        this.showNotification(result.response.data.message, false);
      }
    } catch (error) {
      this.showNotification(`${error}`, false);
    } finally {
      this.cdr.detectChanges();
    }
  }
}