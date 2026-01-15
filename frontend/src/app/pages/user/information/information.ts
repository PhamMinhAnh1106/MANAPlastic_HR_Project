import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { CommonModule, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Department, department, information } from '../../../interface/user/user.interface';
import { getdataRole } from '../../../services/pages/getPageRole.service';
import { UpdateAccount } from '../../../services/pages/user.service';
import { Loading } from '../../shared/loading/loading';
import { Comfirm } from '../../shared/comfirm/comfirm';
import { Alert } from '../../shared/alert/alert';

@Component({
  selector: 'app-information',
  standalone: true,
  imports: [NgIf, CommonModule, FormsModule, Loading, Comfirm, Alert],
  templateUrl: './information.html',
  styleUrl: './information.scss',
})
export class Information implements OnInit {
  constructor(private cookieService: CookieService, private cdr: ChangeDetectorRef) { }

  //////////////////////////////////////////////
  isEditing = false;

  formdata: any = {
    fullname: "",
    cccd: '',
    email: "",
    phonenumber: "",
    gender: 'MALE',
    birth: "",
    address: "",
    bankAccount: "",
    bankName: "",
    departmentID: 0
  }

  userInfo: information = {
    userID: 0,
    username: "",
    fullname: "",
    cccd: "",
    email: "",
    phonenumber: "",
    gender: 'MALE',
    birth: "",
    address: "",
    bankAccount: "",
    status: "",
    bankName: "",
    hireDate: "",
    roleName: "",
    departmentID: 0
  }

  role: string = "";

  /////////////////////////
  isloading: boolean = false;

  isconfirm: boolean = false;
  confirmMessage = "";

  isalert: boolean = false;
  notifyMessage = "";
  notifyType: boolean = true;

  onalert(message: string, type: boolean) {
    this.isalert = true;
    this.notifyMessage = message;
    this.notifyType = type;
    // Tự động tắt alert sau 3s nếu cần thiết
    // setTimeout(() => this.isalert = false, 3000);
  }

  /////////////////////////////////

  async getInformation() {
    this.isloading = true; // Thêm loading khi get data
    try {
      const res = await getdataRole(this.role);
      this.userInfo = {
        userID: res.userID,
        username: res.username,
        fullname: res.fullname,
        cccd: res.cccd,
        email: res.email,
        phonenumber: res.phonenumber,
        gender: res.gender || 'MALE',
        birth: res.birth,
        address: res.address,
        bankAccount: res.bankAccount,
        status: res.status,
        bankName: res.bankName,
        hireDate: res.hireDate,
        roleName: res.roleName,
        departmentID: res.departmentID
      }
      sessionStorage.setItem("departmentId", String(this.userInfo.departmentID));
      sessionStorage.setItem("userId", String(this.userInfo.userID));
    } catch (e) {
      console.error(e);
      this.onalert("Không thể tải thông tin người dùng", false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  getDepartment(id: number) {
    const dept = Department.find(d => d.departmentId === id);
    return dept ? dept.departmentName : 'Chưa có thông tin';
  }

  startEdit() {
    this.isEditing = true;
    this.formdata = JSON.parse(JSON.stringify(this.userInfo));

    if (this.formdata.birth) {
      const date = new Date(this.formdata.birth);
      if (!isNaN(date.getTime())) {
        this.formdata.birth = date.toISOString().split('T')[0];
      }
    }
  }

  cancelEdit() {
    this.isEditing = false;
    this.formdata = {};
  }

  validateForm(): boolean {
    // 1. Validate tên
    if (!this.formdata.fullname || this.formdata.fullname.trim().length === 0) {
      this.onalert("Họ và tên không được để trống", false);
      return false;
    }

    // 2. Validate CCCD/CMND (9 hoặc 12 số)
    if (this.formdata.cccd) {
      const cccdRegex = /^[0-9]{9}$|^[0-9]{12}$/;
      if (!cccdRegex.test(this.formdata.cccd)) {
        this.onalert("CCCD/CMND phải là 9 hoặc 12 chữ số", false);
        return false;
      }
    }

    // 3. Validate Ngày sinh (Không được lớn hơn hiện tại)
    if (this.formdata.birth) {
      const birthDate = new Date(this.formdata.birth);
      const today = new Date();
      // Reset giờ phút giây của today để so sánh chính xác ngày
      today.setHours(0, 0, 0, 0);

      if (birthDate > today) {
        this.onalert("Ngày sinh không được vượt quá ngày hiện tại", false);
        return false;
      }
    }

    // 4. Validate Email
    if (this.formdata.email) {
      const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
      if (!emailRegex.test(this.formdata.email)) {
        this.onalert("Địa chỉ email không hợp lệ", false);
        return false;
      }
    }

    // 5. Validate Số điện thoại
    if (this.formdata.phonenumber) {
      const phoneStr = this.formdata.phonenumber.toString();
      const phoneRegex = /(84|0[3|5|7|8|9])+([0-9]{8})\b/; // Regex cơ bản cho sđt VN

      if (phoneStr.charAt(0) !== '0') {
        this.onalert("Số điện thoại phải bắt đầu bằng số 0", false);
        return false;
      }

      if (phoneStr.length < 10 || phoneStr.length > 11) { // Thường là 10 số
        this.onalert("Số điện thoại phải có 10-11 chữ số", false);
        return false;
      }

      if (!/^\d+$/.test(phoneStr)) {
        this.onalert("Số điện thoại chỉ được chứa ký tự số", false);
        return false;
      }
    }

    return true;
  }

  saveChanges() {
    // Gọi validate TRƯỚC khi hiện popup confirm
    if (this.validateForm()) {
      this.isconfirm = true;
      this.confirmMessage = "Bạn có chắc muốn lưu thay đổi thông tin này?";
    }
  }

  async onConfirmResult(event: any) {
    if (event === true) {
      this.isloading = true;
      try {
        const res = await UpdateAccount(this.formdata, this.role) as any;
        if (res.status == 200) {
          this.onalert(res.data || "Cập nhật thành công!", true);
          this.isEditing = false;
          await this.getInformation();
        } else {
          // Xử lý lỗi từ server trả về
          const errorMsg = res.response?.data?.message || "Cập nhật thất bại";
          this.onalert(errorMsg, false);
        }
      } catch (error) {
        this.onalert("Lỗi kết nối server, vui lòng thử lại sau.", false);
      } finally {
        this.isloading = false;
        this.isconfirm = false;
        this.cdr.detectChanges();
      }
    } else {
      this.isconfirm = false;
    }
  }

  ngOnInit() {
    this.role = this.cookieService.get("role");
    this.getInformation();
  }
}