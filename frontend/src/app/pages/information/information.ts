import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { information } from '../../interface/user/user.interface';
import { CookieService } from 'ngx-cookie-service';
import { getdataRole } from '../../services/pages/getPageRole.service';
import { CommonModule, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UpdateAccount } from '../../services/pages/user.service';

@Component({
  selector: 'app-information',
  imports: [NgIf, CommonModule, FormsModule],
  templateUrl: './information.html',
  styleUrl: './information.scss',
})
export class Information implements OnInit {
  constructor(private cookieService: CookieService, private cdr: ChangeDetectorRef) { }
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
    bankName: ""
  }

  userInfo: information = {
    userID: 0,
    username: "",
    fullname: "",
    cccd: BigInt(0),
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
  }
  startEdit() {
    this.isEditing = true;
    this.formdata = { ...this.userInfo };
  }
  cancelEdit() {
    this.isEditing = false;
    this.formdata = {};
  }
  async saveChanges() {
    if (this.formdata.phonenumber != null) {
      if (this.formdata.phonenumber.split("")[0] != 0)
        alert("so dien thoai phai bat dau tu so 0 ");
      if (this.formdata.phonenumber.length < 10 || this.formdata.phonenumber.length > 12) {
        alert("so dien thoai khong hop le (phai tu 10 den 12 so ");
      }
    }
    const res = await UpdateAccount(this.formdata, this.role) as { data: any; status: number };
    if (res.status == 200) {
      this.isEditing = false;
      alert("them thanh cong");
    } else {
      alert("them that bai");
    }
  }
  async ngOnInit(): Promise<void> {
    this.role = await this.cookieService.get("role");

    await this.getInformation();
    this.cdr.detectChanges();
  }
}
