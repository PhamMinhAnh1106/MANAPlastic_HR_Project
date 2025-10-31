import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { information } from '../../interface/user/user.interface';
import { CookieService } from 'ngx-cookie-service';
import { getdataRole } from '../../services/pages/getPageRole.service';
import { CommonModule, NgIf } from '@angular/common';

@Component({
  selector: 'app-information',
  imports: [NgIf, CommonModule],
  templateUrl: './information.html',
  styleUrl: './information.scss',
})
export class Information implements OnInit {
  constructor(private cookieService: CookieService, private cdr: ChangeDetectorRef) { }
  userInfo: information = {
    userID: 0,
    username: "",
    fullname: "",
    email: "",
    phonenumber: "",
    address: "",
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
      email: res.email,
      phonenumber: res.phonenumber,
      address: res.address,
      roleName: res.roleName,
      departmentID: res.departmentID
    }

  }

  async ngOnInit(): Promise<void> {
    this.role = await this.cookieService.get("role");
    await this.getInformation();
    this.cdr.detectChanges();
  }
}
