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
  standalone: true, // Đã thêm standalone: true vì bạn dùng imports
  imports: [NgIf, CommonModule, FormsModule, Loading, Comfirm, Alert],
  templateUrl: './information.html',
  styleUrl: './information.scss',
})
export class Information implements OnInit {
  constructor(private cookieService: CookieService, private cdr: ChangeDetectorRef) { }

  //////////////////////////////////////////////
  isEditing = false;

  // Bạn nên đảm bảo gender ở đây khớp kiểu với interface
  formdata: any = {
    fullname: "",
    cccd: '',
    email: "",
    phonenumber: "",
    gender: 'MALE', // Giá trị mặc định nên là string nếu dùng select value
    birth: "",
    address: "",
    bankAccount: "",
    bankName: "",
    departmentID: 0
  }

  // LƯU Ý: Hãy kiểm tra file user.interface.ts, 
  // trường 'gender' trong interface 'information' nên là string, không phải boolean
  userInfo: information = {
    userID: 0,
    username: "",
    fullname: "",
    cccd: "",
    email: "",
    phonenumber: "",
    gender: 'MALE', // Mock string để tránh lỗi khởi tạo
    birth: "",
    address: "",
    bankAccount: "",
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
  }

  /////////////////////////////////

  async getInformation() {
    const res = await getdataRole(this.role);
    // Map dữ liệu từ API vào biến userInfo
    this.userInfo = {
      userID: res.userID,
      username: res.username,
      fullname: res.fullname,
      cccd: res.cccd,
      email: res.email,
      phonenumber: res.phonenumber,
      gender: res.gender, // Đảm bảo API trả về 'MALE'/'FEMALE'
      birth: res.birth,
      address: res.address,
      bankAccount: res.bankAccount,
      bankName: res.bankName,
      hireDate: res.hireDate,
      roleName: res.roleName,
      departmentID: res.departmentID
    }
    sessionStorage.setItem("departmentId", String(this.userInfo.departmentID));
    this.cdr.detectChanges();
  }

  getDepartment(id: number) {
    const dept = Department.find(d => d.departmentId === id);
    return dept ? dept.departmentName : 'Chưa có thông tin';
  }

  startEdit() {
    this.isEditing = true;
    // Clone object để tránh binding ngược khi chưa lưu
    this.formdata = { ...this.userInfo };
  }

  cancelEdit() {
    this.isEditing = false;
    this.formdata = {};
  }

  async onConfirmResult(event: any) {
    if (event === true) {
      // Validate đơn giản
      if (this.formdata.phonenumber) {
        if (this.formdata.phonenumber.toString().charAt(0) !== '0') {
          this.onalert("Số điện thoại phải bắt đầu bằng số 0", false);
          return; // Thêm return để dừng xử lý
        }
        if (this.formdata.phonenumber.length < 10 || this.formdata.phonenumber.length > 12) {
          this.onalert("Số điện thoại không hợp lệ (phải từ 10 đến 12 số)", false);
          return;
        }
      }

      console.log("Saving data:", this.formdata);

      try {
        const res = await UpdateAccount(this.formdata, this.role) as { data: any; status: number };
        if (res.status == 200) {
          this.onalert(res.data, true);
          this.isloading = false;
          this.isconfirm = false;
          this.isEditing = false;
          this.getInformation(); // Load lại dữ liệu mới
          this.cdr.detectChanges();
        } else {
          this.onalert(res.data, false);
        }
      } catch (error) {
        this.onalert("Lỗi kết nối server", false);
      }

    } else {
      this.isconfirm = false;
      this.isloading = false;
    }
  }

  saveChanges() {
    this.isconfirm = true;
    this.confirmMessage = "Bạn có chắc muốn sửa thông tin?";
  }

  ngOnInit() {
    this.role = this.cookieService.get("role");
    this.getInformation();
    this.cdr.detectChanges();
  }
}