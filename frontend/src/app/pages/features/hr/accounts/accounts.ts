import { CommonModule, NgFor } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { GetAccountInfo, UpdateAccounthr } from '../../../../services/pages/features/hr/accountManager.service';
import { information } from '../../../../interface/user/user.interface';


@Component({
  selector: 'app-accounts',
  imports: [CommonModule, FormsModule, NgFor],
  templateUrl: './accounts.html',
  styleUrl: './accounts.scss',
})
export class Accounts implements OnInit {
  constructor(private cdr: ChangeDetectorRef) { }
  employee: any = [];
  editID: number | null = null;

  filter = {
    userID: '',
    username: '',
    departmentID: ''
  };
  selectedEmployee: any = null;

  openEditModal(emp: any) {
    this.selectedEmployee = { ...emp };
  }
  async filterEmployees() {
    const userID = Number(this.filter.userID);
    const res = await GetAccountInfo(userID);
    const exists = this.employee.some((item: information) => item.userID === res.userID);
    if (!exists) {
      this.employee.push(res);
    }
    this.cdr.detectChanges();
  }

  async saveEmployee(emp: any) {
    const res = await UpdateAccounthr(emp) as { data: string, status: number };
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

  }
}
