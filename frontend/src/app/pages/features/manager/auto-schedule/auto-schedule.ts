import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgClass, NgFor, NgIf } from '@angular/common';
import { getAllSchedules, scheduleList } from '../../../../utils/listSchedule.utils';
import { Loading } from '../../../shared/loading/loading';
import { Alert } from '../../../shared/alert/alert';
import { Comfirm } from '../../../shared/comfirm/comfirm';
import { AutoAssignSchedule, CheckAutoAssignSchedule, GetRequirementsAutoSchedule, RequirementsAutoSchedule, updateRuleData } from '../../../../services/pages/features/manager/autoSchedule.service';
import { reqAutoSchedule } from '../../../../interface/autoSchedule';

// Define Form Interface cho Edit
interface formRule {
  requirementId: number;
  totalStaffNeeded: number;
  rules: {
    ruleId?: number;
    requiredSkillGrade: string;
    minStaffCount: number;
  }[];
}

@Component({
  selector: 'app-auto-schedule',
  standalone: true,
  imports: [FormsModule, NgFor, NgIf, NgClass, Loading, Alert, Comfirm],
  templateUrl: './auto-schedule.html',
  styleUrls: ['./auto-schedule.scss']
})
export class AutoSchedule {
  constructor(private cdr: ChangeDetectorRef) { }

  // Alert Props
  isconfirm: boolean = false;
  isalert: boolean = false;
  isloading: boolean = false;
  confirmMessage = '';
  alertmessage = '';
  alertType: boolean = true;
  actionType: 'approve' | 'reject' | '' = '';

  Onalert(message: string, type: boolean) {
    this.isalert = true;
    this.alertmessage = message;
    this.alertType = type;
  }

  // --- FORM ADD NEW ---
  departmentId = "";
  totalStaffNeeded = "";
  formSchedule = {
    departmentId: '',
    shiftId: '',
    totalStaffNeeded: '',
    rules: [] as { requiredSkillGrade: string; minStaffCount: string }[],
    requiredSkillGrade: '',  // input tạm
    minStaffCount: ''        // input tạm
  };
  list: any[] = [];
  shiftId: any = '';

  changeType(hours: number) {
    scheduleList(hours, this.list);
  }

  // --- POPUPS & DATA LISTS ---
  rulesPopupVisible = false;
  draftPopupVisible = false;
  requirementPopupVisible = false;
  draftData: any[] = [];
  rulesData: any = [];
  rulesDatalength = 0;
  shiftName = getAllSchedules();

  month: string[] = ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'];
  year: number[] = [2024, 2025, 2026, 2027];
  savemonth = '';
  saveyear = '';

  // --- EDIT MODAL PROPS ---
  isEditing: boolean = false;
  editingData: any = {
    requirementId: 0,
    shiftId: 0,
    totalStaffNeeded: 0,
    rules: []
  };

  // --- METHODS ---

  getShiftName(id: number) {
    const found = this.shiftName.find((x: any) => x.shift_id === id);
    return found ? found.shift_name : id;
  }

  closeRequirementPopup() { this.requirementPopupVisible = false; }
  closeDraftPopup() { this.draftPopupVisible = false; }
  closeRulesPopup() { this.rulesPopupVisible = false; }
  showRules() { this.rulesPopupVisible = true; }

  // Điều hướng về trang lịch
  goBack() { window.location.href = '/home/schedule'; }
  viewSchedule() { window.location.href = '/home/schedule'; }

  // --- ADD LOGIC ---
  addRule() {
    if (this.shiftId == "" || this.formSchedule.totalStaffNeeded == "")
      this.Onalert("Hãy Điền Đủ Thông Tin", false);

    if (this.formSchedule.shiftId == "") {
      this.formSchedule.departmentId = sessionStorage.getItem("departmentId") ?? '';
      this.formSchedule.shiftId = this.shiftId;
    }
    if (this.formSchedule.requiredSkillGrade && this.formSchedule.minStaffCount) {
      this.formSchedule.rules.push({
        requiredSkillGrade: this.formSchedule.requiredSkillGrade,
        minStaffCount: this.formSchedule.minStaffCount
      });
      this.formSchedule.requiredSkillGrade = '';
      this.formSchedule.minStaffCount = '';
    }
  }

  addRuleToTable() {
    if (!this.tempSkill || !this.tempMinStaff) {
      this.Onalert("Vui lòng nhập đầy đủ Cấp độ và Số lượng!", false);
      return;
    }
    this.formSchedule.rules.push({
      requiredSkillGrade: this.tempSkill,
      minStaffCount: this.tempMinStaff
    });
    this.tempSkill = '';
    this.tempMinStaff = '';
  }

  removeRule(index: number) {
    this.formSchedule.rules.splice(index, 1);
  }

  get hasRules() { return this.formSchedule.rules.length > 0; }
  tempSkill: string = '';
  tempMinStaff: string = '';

  saveSchedule() {
    if (!this.shiftId || !this.formSchedule.totalStaffNeeded) {
      this.Onalert("Vui lòng chọn Ca và nhập Tổng số NV cần!", false);
      return;
    }
    this.formSchedule.shiftId = this.shiftId;
    this.formSchedule.departmentId = sessionStorage.getItem("departmentId") ?? '';
    this.isconfirm = true;
    this.confirmMessage = "Bạn có chắc muốn lưu tiêu chí này?";
  }

  // --- API CALLS ---
  async autoassign() {
    const month_year = `${this.saveyear}-${this.savemonth}`;
    this.isloading = true;
    const res = await AutoAssignSchedule(month_year) as { data: string, status: number };
    this.isloading = false;
    this.cdr.detectChanges(); // Cập nhật UI ngay sau khi tắt loading

    if (res.status == 200) {
      this.Onalert(res.data, true);
    } else {
      this.Onalert(res.data, false);
    }
    // Giữ nguyên setTimeout để đảm bảo alert hiển thị nếu có transition
    setTimeout(() => this.cdr.detectChanges(), 1000);
  }

  async onConfirmResult(event: any) {
    if (event == true) {
      this.isconfirm = false;
      const form: reqAutoSchedule = {
        departmentId: this.formSchedule.departmentId,
        shiftId: this.formSchedule.shiftId,
        totalStaffNeeded: this.formSchedule.totalStaffNeeded,
        rules: this.formSchedule.rules as { requiredSkillGrade: string; minStaffCount: string }[]
      };
      this.isloading = true;
      const res = await RequirementsAutoSchedule(form);
      this.isloading = false;
      this.cdr.detectChanges(); // Cập nhật UI

      if (res == 201) {
        this.Onalert("Thêm thành công", true);
        // Reset form sau khi lưu
        this.formSchedule.rules = [];
        this.formSchedule.totalStaffNeeded = '';
        this.rulesPopupVisible = false;
      } else {
        this.Onalert("Thêm thất bại", false);
      }
      setTimeout(() => this.cdr.detectChanges(), 1000);
    } else {
      this.isconfirm = false;
    }
  }

  // --- CHECK / VIEW ---
  validateDateSelection(): boolean {
    if (!this.saveyear || !this.savemonth) {
      this.Onalert("Vui lòng chọn Tháng và Năm trước!", false);
      return false;
    }
    return true;
  }

  async checkRules() {
    if (!this.validateDateSelection()) return;
    this.isloading = true;
    this.cdr.detectChanges(); // Trigger show loading

    const res = await GetRequirementsAutoSchedule();
    this.isloading = false;
    this.rulesData = res;
    this.rulesDatalength = res.length;
    this.requirementPopupVisible = true;
    this.cdr.detectChanges(); // Cập nhật UI với dữ liệu mới
  }

  async checkDraftSchedule() {
    if (!this.validateDateSelection()) return;
    const month_year = `${this.saveyear}-${this.savemonth}`;
    this.isloading = true;
    this.cdr.detectChanges(); // Trigger show loading

    const res = await CheckAutoAssignSchedule(month_year);
    this.isloading = false;
    this.draftData = res;
    this.draftPopupVisible = true;
    this.cdr.detectChanges(); // Cập nhật UI với dữ liệu mới
  }

  // --- EDIT FUNCTIONS ---
  openEditModal(req: any) {
    // Deep clone để không ảnh hưởng dữ liệu hiển thị
    this.editingData = JSON.parse(JSON.stringify(req));
    this.isEditing = true;
  }

  closeEditModal() {
    this.isEditing = false;
    this.editingData = { requirementId: 0, shiftId: 0, totalStaffNeeded: 0, rules: [] };
  }

  addRuleInEdit() {
    this.editingData.rules.push({
      requiredSkillGrade: '',
      minStaffCount: 0
    });
  }

  removeRuleInEdit(index: number) {
    this.editingData.rules.splice(index, 1);
  }

  async saveEditRule() {
    if (!this.editingData.totalStaffNeeded || this.editingData.rules.length === 0) {
      this.Onalert("Vui lòng nhập đủ thông tin", false);
      return;
    }

    this.isloading = true;
    this.cdr.detectChanges();

    // Map dữ liệu sang formRule interface
    const form: any = {
      requirementId: this.editingData.requirementId,
      totalStaffNeeded: this.editingData.totalStaffNeeded,
      rules: this.editingData.rules.map((r: any) => ({
        ruleId: r.ruleId, // Giữ lại ID nếu có để backend biết là update
        requiredSkillGrade: r.requiredSkillGrade,
        minStaffCount: r.minStaffCount
      }))
    };

    const res = await updateRuleData(form);
    this.isloading = false;
    this.cdr.detectChanges(); // Update UI tắt loading

    // Kiểm tra kết quả trả về từ service
    if (res && typeof res === 'string' && res.includes("co loi xay ra")) {
      this.Onalert(res, false);
    } else {
      this.Onalert("Cập nhật thành công!", true);
      this.closeEditModal();
      this.checkRules(); // Load lại danh sách mới nhất
    }
    this.cdr.detectChanges();
  }
}