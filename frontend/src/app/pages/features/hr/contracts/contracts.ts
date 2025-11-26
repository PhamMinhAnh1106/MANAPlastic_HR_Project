import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Loading } from '../../../shared/loading/loading';
import { CheckContractByIdEmployee, ExportFileDataAttendance, ExportFileDataContracts, FillterContract, FillterContractByIdEmployee } from '../../../../services/pages/features/hr/contracts.service';
import { Department } from '../../../../interface/user/user.interface';

@Component({
  selector: 'app-contracts',
  imports: [FormsModule, NgIf, Loading, NgFor],
  templateUrl: './contracts.html',
  styleUrl: './contracts.scss',
})
export class Contracts {
  constructor(private router: Router, private cdr: ChangeDetectorRef) { }
  tab: string = 'check';
  isloading: boolean = false;
  employeeId: string = '';
  messageCheckContract = "";
  statusContract = [
    'DRAFT',
    'ACTIVE',
    'EXPIRING_SOON',
    'EXPIRED',
    'TERMINATED',
    'HISTORY'
  ];
  department = Department;
  filters = {
    username: '',
    type: '',
    status: '',
    allowance: '',
    startdate: '',
    enddate: ''
  };
  showPopup = false;          // bật tắt popup
  popupMode: 'message' | 'list' | 'export' = 'message';
  exportType: string = '';
  selectedDepartment = '';
  selectedstatus = '';
  listContracts: any[] = [];  // hiển thị list

  closePopup() {
    this.showPopup = false;
  }
  addContract() {
    this.router.navigate(["/home/contracts/add"]);
  }

  async checkSignedContract() {
    this.isloading = true;
    const id = Number(this.employeeId);
    const res = await CheckContractByIdEmployee(id) as { data: string, status: number };
    if (res.status == 200) {
      this.isloading = false;
      this.messageCheckContract = res.data;
      this.popupMode = 'message';
      this.showPopup = true;
      setTimeout(() => this.cdr.detectChanges(), 1000);
      return;
    }
    this.isloading = false;
    this.messageCheckContract = res.data;
    this.popupMode = 'message';
    this.showPopup = true;
  }

  async viewEmployeeContracts() {
    this.isloading = true;
    const id = Number(this.employeeId);
    const res = await FillterContractByIdEmployee(id) as { data: any, status: number };
    this.popupMode = "list";
    this.showPopup = true;
    setTimeout(() => this.cdr.detectChanges(), 1000);

    if (res.status == 200) {
      this.isloading = false;
      this.listContracts = res.data;
      setTimeout(() => this.cdr.detectChanges(), 1000);
      return;
    }
  }

  buildQuery(filters: any): string {
    const params = new URLSearchParams();

    Object.keys(filters).forEach(key => {
      const value = filters[key];
      if (value !== null && value !== undefined && value !== '') {
        params.append(key, value);
      }
    });

    return params.toString();
  }


  async searchContract() {
    const query = this.buildQuery(this.filters);
    this.isloading = true;
    const res = await FillterContract(query) as { data: any, status: number };
    if (res.status == 200) {
      this.isloading = false;
      this.popupMode = "list";
      this.showPopup = true;
      this.listContracts = res.data;
      this.cdr.detectChanges();
      return;
    }
    this.isloading = false;

  }

  exportAttendance() {
    this.popupMode = 'export';
    this.exportType = 'exportAttendance';
    this.showPopup = true;

  }

  exportContract() {
    this.popupMode = 'export';
    this.exportType = 'exportContract';
    this.showPopup = true;

  }
  confirmExport() {
    if (this.exportType == 'exportContract') {
      ExportFileDataContracts(this.selectedstatus);
    } else if (this.exportType == 'exportAttendance') {
      ExportFileDataAttendance(Number(this.selectedDepartment));
    }
  }
}
