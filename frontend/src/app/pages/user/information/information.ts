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
    cccd: BigInt(0),
    email: "",
    phonenumber: "",
    gender: false,
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
    gender: false,
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
    this.userInfo = {
      userID: res.userID,
      username: res.username,
      fullname: res.fullname,
      cccd: res.cccd,
      email: res.email,
      phonenumber: res.phonenumber,
      gender: res.gender,
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
    this.formdata = { ...this.userInfo };
  }
  cancelEdit() {
    this.isEditing = false;
    this.formdata = {};
  }
  async onConfirmResult(event: any) {
    if (event === true) {
      this.isloading = true;
      if (this.formdata.phonenumber != null) {
        if (this.formdata.phonenumber.split("")[0] != 0)
          this.onalert("so dien thoai phai bat dau tu so 0 ", false);
        if (this.formdata.phonenumber.length < 10 || this.formdata.phonenumber.length > 12) {
          this.onalert("so dien thoai khong hop le (phai tu 10 den 12 so ", false);
        }
      }
      const res = await UpdateAccount(this.formdata, this.role) as { data: any; status: number };
      if (res.status == 200) {
        this.onalert(res.data, true);
        this.isloading = false;
        this.isconfirm = false;
        this.isEditing = false;
        this.getInformation();
        this.cdr.detectChanges();
      } else {
        this.onalert(res.data, false);
      }
    } else {
      this.isconfirm = false;

      this.isloading = false;
    }
  }
  saveChanges() {
    this.isconfirm = true;
    this.confirmMessage = "Bạn có chắc muốn sửa thông ?";
  }
  ngOnInit() {
    this.role = this.cookieService.get("role");
    this.getInformation();
    this.cdr.detectChanges();
  }
}
