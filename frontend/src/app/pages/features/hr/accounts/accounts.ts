import { CommonModule, NgFor } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { GetAccountInfo, UpdateAccounthr } from '../../../../services/pages/features/hr/accountManager.service';
import { Department, information } from '../../../../interface/user/user.interface';
import { CookieService } from 'ngx-cookie-service';
import { FilterUser } from '../../../../utils/filters.utils';


@Component({
  selector: 'app-accounts',
  imports: [CommonModule, FormsModule, NgFor],
  templateUrl: './accounts.html',
  styleUrl: './accounts.scss',
})
export class Accounts implements OnInit {
  constructor(private cdr: ChangeDetectorRef, private cookie: CookieService) { }
  employee: any = [];
  editID: number | null = null;
  role: string = "";

  selectedEmployee: any = null;
  /////////////////////
  showAdvancedFilter = false;

  filter = {
    userID: '',
    username: '',
    departmentId: '',
    keyword: '',
    status: '',
    hireDateStart: '',
    hireDateEnd: ''
  };
  department = Department;
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
      const res = await FilterUser(query, this.role);
      this.employee.push(res);
      this.toggleAdvancedFilter();
      this.cdr.detectChanges();
    }
  }
  /////////////////////

  openEditModal(emp: any) {
    this.selectedEmployee = { ...emp };
  }
  async filterEmployees() {
    const keyword = Number(this.filter.userID);
    const res = await GetAccountInfo(keyword, this.role);

    if (this.employee.length > 0)
      this.employee = [];
    this.employee.push(res);
    this.cdr.detectChanges();
  }

  async saveEmployee(emp: any) {
    const res = await UpdateAccounthr(emp, this.role) as { data: string, status: number };
    if (res.status == 200) {
      alert(res.data);
      this.selectedEmployee = null;
      this.cdr.detectChanges();
      return;
    }
    alert("thêm thất bại")
  }

  cancelEdit() {
    this.selectedEmployee = null;

  }
  ngOnInit(): void {
    this.role = this.cookie.get("role");

  }
}
