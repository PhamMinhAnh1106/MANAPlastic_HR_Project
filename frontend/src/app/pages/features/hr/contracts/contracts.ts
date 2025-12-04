import { DecimalPipe, NgFor, NgIf } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Loading } from '../../../shared/loading/loading';
import { CheckContractByIdEmployee, ExportFileDataContracts, FillterContract, FillterContractByIdEmployee } from '../../../../services/pages/features/hr/contracts.service';
import { buildQueryParams } from '../../../../utils/filters.utils';
import { Alert } from '../../../shared/alert/alert';

@Component({
  selector: 'app-contracts',
  imports: [FormsModule, NgIf, Loading, NgFor, Alert],
  templateUrl: './contracts.html',
  styleUrls: ['./contracts.scss'], // sửa từ styleUrl -> styleUrls
})
export class Contracts implements OnInit {
  tab: string = 'check';
  isloading: boolean = false;
  employeeId: string = '';
  messageCheckContract = "";
  isalert: boolean = false;
  notifyMessage = "";
  notifyType: boolean = true;
  showNotification(message: string, type: boolean) {
    this.notifyMessage = message;
    this.notifyType = type;
    this.isalert = true;
  }

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
  translateContractStatus(status: string): string {
    switch (status) {
      case 'DRAFT':
        return 'Bản nháp';
      case 'ACTIVE':
        return 'Đang hoạt động';
      case 'EXPIRING_SOON':
        return 'Sắp hết hạn';
      case 'EXPIRED':
        return 'Đã hết hạn';
      case 'TERMINATED':
        return 'Đã chấm dứt';
      case 'HISTORY':
        return 'Lịch sử';
      default:
        return 'Không xác định';
    }
  }

  constructor(private router: Router, private cdr: ChangeDetectorRef) { }

  closePopup() {
    this.showPopup = false;
  }

  addContract() {
    this.router.navigate(["/home/contracts/add"]);
  }

  async checkSignedContract() {
    if (this.employeeId == '') {
      this.showNotification("Chưa Điền Mã Nhân Viên", false);
      return;
    }
    this.isloading = true;
    const id = Number(this.employeeId);
    const res = await CheckContractByIdEmployee(id) as { data: string, status: number };
    this.isloading = false;
    this.messageCheckContract = res.data;
    this.popupMode = 'message';
    this.showPopup = true;
    setTimeout(() => this.cdr.detectChanges(), 1000);
  }
  getVietnameseContractType(type: string): string {
    switch (type) {
      case 'INDEFINITE':
        return 'Hợp đồng Không thời hạn';
      case 'FIXED_TERM':
        return 'Hợp đồng có thời hạn';
      case 'PROBATION':
        return 'Hợp đồng Thử việc';
      default:
        return 'Không xác định';
    }
  }
  async viewEmployeeContracts() {
    if (this.employeeId == '') {
      this.showNotification("Chưa Điền Mã Nhân Viên", false);
      return;
    }
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
  translateAllowanceType(type: string): string {
    switch (type) {
      case 'NONE':
        return 'Không áp dụng';
      case 'CASH':
        return 'Chi trả bằng Tiền mặt';
      case 'IN_KIND':
        return 'Chi trả bằng Hiện vật';
      default:
        return 'Không xác định';
    }
  }
  async searchContract() {
    const query = this.buildQuery(this.filters);
    this.isloading = true;
    const res = await FillterContract(query) as { data: any, status: number };
    this.isloading = false;
    if (res.status == 200) {
      this.listContracts = res.data.sort((a: { id: number; }, b: { id: number; }) => a.id - b.id);
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
  changeTab(tabName: string) {
    this.tab = tabName;
    sessionStorage.setItem('activeTab', tabName);
  }
  ngOnInit(): void {
    const savedTab = sessionStorage.getItem('activeTab');
    if (savedTab) {
      this.tab = savedTab;
    }
  }
}
