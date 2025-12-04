import { CommonModule, NgFor, NgIf } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DeleteAttendant, GetAttendants } from '../../../../services/pages/features/hr/attendant.service';
import { CookieService } from 'ngx-cookie-service';
import { Loading } from '../../../shared/loading/loading';
import { Alert } from '../../../shared/alert/alert';
import { Comfirm } from '../../../shared/comfirm/comfirm';
import { ExportFileDataAttendance } from '../../../../services/pages/features/hr/contracts.service';

interface attendance {
  attendanceId: number,
  attendanceDate: string,
  userName: string,
  fullNameUser: string
  checkIn: string,
  checkOut: string,
  checkInImg: string,
  checkOutImg: string
  shiftId: number,
  shiftName: string,
  status: string
}

@Component({
  selector: 'app-attendant',
  imports: [CommonModule, FormsModule, NgFor, NgIf, Loading, Alert, Comfirm],
  templateUrl: './attendant.html',
  styleUrl: './attendant.scss',
})
export class Attendant implements OnInit {
  constructor(private cdr: ChangeDetectorRef, private cookie: CookieService) { }
  role: string = "";
  id: number = 0;
  filter = {
    date: '',
    month: '',
    year: '',
    departmentId: '',
    status: ''
  };
  selectedProof: any = null;
  ////////////////////////
  isconfirm: boolean = false;
  isalert: boolean = false;
  isloading: boolean = false;
  confirmMessage = '';
  alertmessage = '';
  alertType: boolean = true;

  Onalert(message: string, type: boolean) {
    this.isalert = true;
    this.alertmessage = message;
    this.alertType = type;
  }
  /////////////////////////
  showProof(att: any) {
    this.selectedProof = att;
  }


  closeProof() {
    this.selectedProof = null;
  }

  months = Array.from({ length: 12 }, (_, i) => i + 1);
  years = [2023, 2024, 2025];
  departments = [
    { id: 1, name: 'Phòng Ban Nhân Sự' },
    { id: 2, name: 'Phòng Ban IT' },
    { id: 3, name: 'Phòng Ban Kỹ Thuật' },
    { id: 4, name: 'Phòng Ban Sản Xuất' },
    { id: 5, name: 'Phòng Ban In Ấn' },
    { id: 6, name: 'Phòng Ban Chăm Sóc Khách Hàng' },

  ];

  attendance: attendance[] = [
  ];

  selectedAttendance: any = null;

  async filterAttendance() {
    const query = Object.entries(this.filter)
      .filter(([_, value]) => value !== '')
      .map(([key, value]) => `${key}=${encodeURIComponent(value)}`)
      .join('&');
    if (query.length > 0) {
      const res = await GetAttendants(query, this.role) as attendance[];
      this.attendance = [...res];
      this.cdr.detectChanges();
    }

  }

  openEditModal(att: any) {
    this.selectedAttendance = { ...att };
  }

  cancelEdit() {
    this.selectedAttendance = null;
  }

  saveAttendance(updated: any) {
    this.selectedAttendance = null;
  }
  translateStatus(status: string): string {
    switch (status) {
      case 'PRESENT':
        return "Có mặt";
      case 'ABSENT':
        return "Vắng mặt";
      case 'LATE_AND_EARLY':
        return "Đi trễ / Về sớm";
      case 'ON_LEAVE':
        return "Nghỉ phép";
      case 'MISSING_OUTPUT_DATA':
        return "Thiếu dữ liệu check-out";
      case 'MISSING_INPUT_DATA':
        return "Thiếu dữ liệu check-in";
      default:
        return "Không xác định";
    }
  }

  getTime(datetime: string): string {
    const date = new Date(datetime);
    return date.toTimeString().split(" ")[0]; // HH:mm:ss
  }
  async onConfirmResult(event: any) {
    if (event == true) {
      this.isconfirm = false;
      if (this.role == "hr") {
        this.isloading = true;
        const res = await DeleteAttendant(this.id) as { data: string, status: number };
        if (res.status == 200) {
          this.isloading = false
          this.filterAttendance();
          this.Onalert("Xóa thành công", true);
          return;
        }
        this.isloading = false
        this.Onalert("Xóa Thất Bại", false);
      }

    } else {
      this.isconfirm = false;

    }
  }
  deleteAttendance(id: number) {
    this.isconfirm = true;
    this.confirmMessage = "Bạn chắc chắn muốn xóa dữ liệu này ?"
    this.id = id;
  }

  buildQueryParams(filter: any): string {
    const params = [];

    for (const key in filter) {
      if (filter[key] !== "") {
        params.push(`${key}=${encodeURIComponent(filter[key])}`);
      }
    }

    return params.join("&");
  }

  async exportfile() {
    const query = this.buildQueryParams(this.filter);
    await ExportFileDataAttendance(query);
  }
  ngOnInit(): void {
    this.role = this.cookie.get("role").toLowerCase();
  }

}
