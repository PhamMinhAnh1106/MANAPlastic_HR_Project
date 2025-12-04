import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgClass, NgFor, NgIf } from '@angular/common';
import { getAllSchedules, scheduleList } from '../../../../utils/listSchedule.utils';
import { Loading } from '../../../shared/loading/loading';
import { Alert } from '../../../shared/alert/alert';
import { Comfirm } from '../../../shared/comfirm/comfirm';
import { AutoAssignSchedule, CheckAutoAssignSchedule, GetRequirementsAutoSchedule, RequirementsAutoSchedule } from '../../../../services/pages/features/manager/autoSchedule.service';
import { reqAutoSchedule } from '../../../../interface/autoSchedule';

@Component({
  selector: 'app-auto-schedule',
  standalone: true,
  imports: [FormsModule, NgFor, NgIf, NgClass, Loading, Alert, Comfirm],
  templateUrl: './auto-schedule.html',
  styleUrls: ['./auto-schedule.scss']
})
export class AutoSchedule {
  constructor(private cdr: ChangeDetectorRef) { }
  ////////////////////////
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
  /////////////////////////
  //form api schedule auto add rule
  departmentId = "";
  totalStaffNeeded = "";
  //
  formSchedule = {
    departmentId: '',
    shiftId: '',
    totalStaffNeeded: '',
    rules: [] as { requiredSkillGrade: string; minStaffCount: string }[],
    requiredSkillGrade: '',  // input tạm
    minStaffCount: ''        // input tạm
  };
  list: any[] = [];       // danh sách ca
  shiftId: any = '';
  changeType(hours: number) {
    scheduleList(hours, this.list);
  }
  rulesPopupVisible = false;
  draftPopupVisible = false;
  draftData: any[] = [];

  // Thêm rule nhưng KHÔNG reset input
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
      // không reset input
      this.formSchedule.requiredSkillGrade = '';
      this.formSchedule.minStaffCount = '';
    }
  }

  // Kiểm tra nút Save hiển thị
  get hasRules() {
    return this.formSchedule.rules.length > 0;
  }

  showRules() {
    this.rulesPopupVisible = true;
  }

  closeRulesPopup() {
    this.rulesPopupVisible = false;
  }

  // async checkDraftSchedule() {
  //   if (this.saveyear == "" || this.savemonth == "") {
  //     this.Onalert("Hãy chọn tháng và năm", false);
  //     return;
  //   }
  //   const month_year = `${this.saveyear}-${this.savemonth}`;
  //   const res = await CheckAutoAssignSchedule(month_year);
  //   this.draftData = res;
  //   this.draftPopupVisible = true;
  //   this.cdr.detectChanges();

  // }

  closeDraftPopup() {
    this.draftPopupVisible = false;
  }

  viewSchedule() {
    window.location.href = '/home/schedule';
  }

  // saveSchedule() {
  //   this.isconfirm = true;
  //   this.confirmMessage = "Bạn Muốn thêm tiêu chí này ?"

  // }

  requirementPopupVisible: boolean = false;
  rulesData: any = [];
  rulesDatalength = 0;
  shiftName = getAllSchedules();

  getShiftName(id: number) {
    const found = this.shiftName.find((x: any) => x.shift_id === id);
    return found ? found.shift_name : id;
  }
  // async checkRules() {
  //   if (this.saveyear == "" || this.savemonth == "") {
  //     this.Onalert("Hãy chọn tháng và năm", false);
  //     return;
  //   }
  //   this.requirementPopupVisible = true;
  //   const res = await GetRequirementsAutoSchedule();
  //   this.rulesData = res;
  //   this.rulesDatalength = res.length;
  //   console.log(this.rulesData);
  //   this.cdr.detectChanges();

  // }
  closeRequirementPopup() {
    this.requirementPopupVisible = false;
  }

  month: number[] = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];
  year: number[] = [2024, 2025, 2026, 2027];
  savemonth = '';
  saveyear = '';
  async autoassign() {
    const month_year = `${this.saveyear}-${this.savemonth}`;
    this.isloading = true;
    const res = await AutoAssignSchedule(month_year) as { data: string, status: number };
    if (res.status == 200) {
      this.isloading = false;
      this.Onalert(res.data, true);
      setTimeout(() => this.cdr.detectChanges(), 1000);
      return;
    }
    this.isloading = false;
    this.Onalert(res.data, false);
    setTimeout(() => this.cdr.detectChanges(), 1000);
  }
  async onConfirmResult(event: any) {
    const form: reqAutoSchedule = {
      departmentId: this.formSchedule.departmentId,
      shiftId: this.formSchedule.shiftId,
      totalStaffNeeded: this.formSchedule.totalStaffNeeded,
      rules: this.formSchedule.rules as { requiredSkillGrade: string; minStaffCount: string }[]
    };
    if (event == true) {
      this.isconfirm = false;
      this.isloading = true;
      const res = await RequirementsAutoSchedule(form);
      if (res == 201) {
        this.isloading = false;
        this.Onalert("Thêm thành công", true);
        this.rulesPopupVisible = false;
        setTimeout(() => this.cdr.detectChanges(), 1000);
        return;
      }
      this.isloading = false;
      this.Onalert("Thêm thất bại", false);
      setTimeout(() => this.cdr.detectChanges(), 1000);

    } else {
      this.isconfirm = false
    }
  }

  // Biến tạm cho hàng input trong bảng Rule
  tempSkill: string = '';
  tempMinStaff: string = '';

  // Hàm thêm Rule vào bảng tạm (thay cho addRule cũ)
  addRuleToTable() {
    if (!this.tempSkill || !this.tempMinStaff) {
      this.Onalert("Vui lòng nhập đầy đủ Cấp độ và Số lượng!", false);
      return;
    }

    this.formSchedule.rules.push({
      requiredSkillGrade: this.tempSkill,
      minStaffCount: this.tempMinStaff
    });

    // Reset input tạm
    this.tempSkill = '';
    this.tempMinStaff = '';
  }

  // Hàm xóa Rule khỏi bảng
  removeRule(index: number) {
    this.formSchedule.rules.splice(index, 1);
  }

  // Hàm Save (cập nhật để lấy shiftId đúng)
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

  // Hàm kiểm tra tháng/năm trước khi mở popup
  validateDateSelection(): boolean {
    if (!this.saveyear || !this.savemonth) {
      this.Onalert("Vui lòng chọn Tháng và Năm trước!", false);
      return false;
    }
    return true;
  }

  // Cập nhật các hàm mở popup để dùng validate
  async checkRules() {
    if (!this.validateDateSelection()) return;

    this.requirementPopupVisible = true;
    const res = await GetRequirementsAutoSchedule();
    // Lưu ý: Cần truyền tháng/năm vào API GetRequirements nếu API hỗ trợ lọc
    this.rulesData = res;
    this.rulesDatalength = res.length;
    this.cdr.detectChanges();
  }

  async checkDraftSchedule() {
    if (!this.validateDateSelection()) return;

    const month_year = `${this.saveyear}-${this.savemonth}`;
    const res = await CheckAutoAssignSchedule(month_year);
    this.draftData = res;
    this.draftPopupVisible = true;
    this.cdr.detectChanges();
  }
  async updateRuleData() {

  }
}
