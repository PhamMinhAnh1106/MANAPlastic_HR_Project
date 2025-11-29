import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Loading } from '../../../shared/loading/loading';
import { CheckContractByIdEmployee, ExportFileDataContracts, FillterContract, FillterContractByIdEmployee } from '../../../../services/pages/features/hr/contracts.service';
import { buildQueryParams } from '../../../../utils/filters.utils';

@Component({
  selector: 'app-contracts',
  imports: [FormsModule, NgIf, Loading, NgFor],
  templateUrl: './contracts.html',
  styleUrls: ['./contracts.scss'], // sửa từ styleUrl -> styleUrls
})
export class Contracts {
  tab: string = 'check';
  isloading: boolean = false;
  employeeId: string = '';
  messageCheckContract = "";
  filters = {
    username: '',
    type: '',
    status: '',
    allowanceToxicType: '',
    startdate: '',
    enddate: ''
  };
  showPopup = false;          // bật tắt popup
  popupMode: 'message' | 'list' = 'message';
  listContracts: any[] = [];  // hiển thị list
  statusContract = [
    'DRAFT',
    'ACTIVE',
    'EXPIRING_SOON',
    'EXPIRED',
    'TERMINATED',
    'HISTORY'
  ];

  constructor(private router: Router, private cdr: ChangeDetectorRef) { }

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
    this.isloading = false;
    this.messageCheckContract = res.data;
    this.popupMode = 'message';
    this.showPopup = true;
    setTimeout(() => this.cdr.detectChanges(), 1000);
  }

  async viewEmployeeContracts() {
    this.isloading = true;
    const id = Number(this.employeeId);
    const res = await FillterContractByIdEmployee(id) as { data: any, status: number };
    this.isloading = false;

    if (res.status == 200) {
      this.listContracts = res.data;

    } else {
      this.listContracts = [];
    }

    this.popupMode = "list";
    this.showPopup = true;
    setTimeout(() => this.cdr.detectChanges(), 1000);
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
    this.isloading = false;

    if (res.status == 200) {
      this.listContracts = res.data;
      this.popupMode = "list";
      this.showPopup = true;
      this.cdr.detectChanges();
    }
  }

  async ExportExcel() {
    const query = buildQueryParams(this.filters);
    console.log(query)

    await ExportFileDataContracts(query);
  }

  copyLink(url: string) {
    navigator.clipboard.writeText(url)
      .then(() => {
        alert('Đã copy link!');
      })
      .catch(err => {
        console.error('Lỗi copy link:', err);
      });
  }

}
