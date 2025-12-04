import { CommonModule, NgFor } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { GetAccountInfo, UpdateAccounthr } from '../../../../services/pages/features/hr/accountManager.service';
import { Department, information } from '../../../../interface/user/user.interface';
import { CookieService } from 'ngx-cookie-service';
import { FilterUser } from '../../../../utils/filters.utils';
import { Loading } from '../../../shared/loading/loading';
import { Comfirm } from '../../../shared/comfirm/comfirm';
import { Alert } from '../../../shared/alert/alert';


@Component({
  selector: 'app-accounts',
  imports: [CommonModule, FormsModule, NgFor, Loading, Comfirm, Alert],
  templateUrl: './accounts.html',
  styleUrl: './accounts.scss',
})
export class Accounts implements OnInit {
  sortByIdDesc: boolean = false;
  sortByUsernameDesc: boolean = false;
  sortByGenderDesc: boolean = false;
  sortByBirthdayDesc: boolean = false;
  constructor(private cdr: ChangeDetectorRef, private cookie: CookieService) { }
  employee: any = [];
  editID: number | null = null;
  role: string = "";
  // box hien thi
  isloading: boolean = false;
  isconfirm: boolean = false;
  confirmMessage = "";
  isalert: boolean = false;
  notifyMessage = "";
  notifyType: boolean = true;
  ///
  selectedEmployee: any = null;
  /////////////////////
  showAdvancedFilter = false;
  emp: any = {};
  filter = {
    userID: '',
    username: '',
    departmentId: '',
    departmentName: '',
    keyword: '',
    status: '',
    hireDateStart: '',
    hireDateEnd: ''
  };
  department = Department;
  showNotification(message: string, type: boolean) {
    this.notifyMessage = message;
    this.notifyType = type;
    this.isalert = true;
  }
  toggleAdvancedFilter() {
    this.showAdvancedFilter = !this.showAdvancedFilter;
  }

  onHireDateStartChange() {
    if (!this.filter.hireDateStart) {
      this.filter.hireDateEnd = '';
    }
  }

  async applyAdvancedFilter() {
    const query = Object.entries(this.filter)
      .filter(([_, value]) => value !== '')
      .map(([key, value]) => `${key}=${encodeURIComponent(value)}`)
      .join('&');
    if (query.length > 0) {
      if (this.employee.length > 0) {
        this.employee.length = [];
      }
      this.isloading = true;
      const res = await FilterUser(query, this.role);
      if (res.length > 0) {
        this.isloading = false;
        this.employee.push(res);
        this.toggleAdvancedFilter();
        this.cdr.detectChanges();
      }
    }
  }
  /////////////////////

  openEditModal(emp: any) {
    this.selectedEmployee = { ...emp };
  }
  async filterEmployees() {
    const keyword = Number(this.filter.userID);
    this.isloading = true;
    const res = await GetAccountInfo(keyword, this.role);

    if (this.employee.length > 0) {
      this.isloading = false;
      this.employee = [];
    }
    this.isloading = false;

    this.employee.push(res);
    this.cdr.detectChanges();
  }

  saveEmployee(emp: any) {
    this.isconfirm = true;
    this.confirmMessage = "Bạn có chắc muốn sửa thông tin nhân viên này?";
    this.emp = emp;
  }
  async onConfirmResult(event: any) {
    this.isloading = true;
    this.isconfirm = false;
    if (event === true) {

      const res = await UpdateAccounthr(this.emp, this.role) as { data: string, status: number };
      if (res.status == 200) {
        this.isloading = false;
        this.showNotification(res.data, true);
        this.selectedEmployee = null;
        this.cdr.detectChanges();
        return;
      }
      this.showNotification(res.data, false);
    } else {
      this.isloading = false;
    }

  }
  cancelEdit() {
    this.selectedEmployee = null;

  }
  ngOnInit(): void {
    this.role = this.cookie.get("role");

  }
  sort(x: any) {

    switch (x) {
      case 'id':
        // Nếu hiện tại đang DESC → sort ASC
        if (this.sortByIdDesc) {
          const data = this.employee[0].slice().sort(
            (a: { userID: number }, b: { userID: number }) => a.userID - b.userID
          );
          this.employee[0] = data;
        }
        // Nếu hiện tại đang ASC → sort DESC
        else {
          const data = this.employee[0].slice().sort(
            (a: { userID: number }, b: { userID: number }) => b.userID - a.userID
          );
          this.employee[0] = data;
        }

        // Đảo lại trạng thái toggle
        this.sortByIdDesc = !this.sortByIdDesc;

        this.cdr.detectChanges();
        break;
      case 'name':
        if (this.sortByUsernameDesc) {
          // Sort tăng dần (A → Z)
          const data = this.employee[0].slice().sort(
            (a: any, b: any) => a.username.localeCompare(b.username)
          );
          this.employee[0] = data;
        } else {
          // Sort giảm dần (Z → A)
          const data = this.employee[0].slice().sort(
            (a: any, b: any) => b.username.localeCompare(a.username)
          );
          this.employee[0] = data;
        }

        // Toggle
        this.sortByUsernameDesc = !this.sortByUsernameDesc;

        this.cdr.detectChanges();
        break;
      case 'gender':
        if (this.sortByGenderDesc) {
          // Tăng dần: Nữ (false) → Nam (true)
          const data = this.employee[0].slice().sort(
            (a: any, b: any) => Number(a.gender) - Number(b.gender)
          );
          this.employee[0] = data;
        } else {
          // Giảm dần: Nam (true) → Nữ (false)
          const data = this.employee[0].slice().sort(
            (a: any, b: any) => Number(b.gender) - Number(a.gender)
          );
          this.employee[0] = data;
        }

        // Toggle
        this.sortByGenderDesc = !this.sortByGenderDesc;

        this.cdr.detectChanges();
        break;
      case 'born':
        if (this.sortByBirthdayDesc) {
          // Tăng dần: Ngày cũ (1990 → 2000)
          const data = this.employee[0].slice().sort(
            (a: any, b: any) =>
              new Date(a.birth).getTime() - new Date(b.birth).getTime()
          );
          this.employee[0] = data;
        } else {
          // Giảm dần: Ngày mới (2000 → 1990)
          const data = this.employee[0].slice().sort(
            (a: any, b: any) =>
              new Date(b.birth).getTime() - new Date(a.birth).getTime()
          );
          this.employee[0] = data;
        }

        // Toggle đảo chiều
        this.sortByBirthdayDesc = !this.sortByBirthdayDesc;

        this.cdr.detectChanges();
        break;


    }
  }
}
