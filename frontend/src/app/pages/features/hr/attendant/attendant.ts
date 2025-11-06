import { CommonModule, NgFor } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DeleteAttendant, GetAttendants } from '../../../../services/pages/features/hr/attendant.service';
import { CookieService } from 'ngx-cookie-service';

interface attendance {
  attendanceId: number,
  attendanceDate: string,
  checkIn: string,
  checkOut: string,
  checkInImg: string,
  checkOutImg: string
  shiftId: number,
  status: string
}

@Component({
  selector: 'app-attendant',
  imports: [CommonModule, FormsModule, NgFor],
  templateUrl: './attendant.html',
  styleUrl: './attendant.scss',
})
export class Attendant {
  constructor(private cdr: ChangeDetectorRef, private cookie: CookieService) { }
  filter = {
    date: '',
    month: '',
    year: '',
    departmentId: '',
    status: ''
  };

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
      console.log(query)
      const res = await GetAttendants(query) as attendance[];
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
    console.log('Saved:', updated);
    this.selectedAttendance = null;
  }
  async deleteAttendance(id: number) {
    if (confirm('Bạn có chắc chắn muốn xóa chấm công này không?')) {
      const res = await DeleteAttendant(id) as { data: string, status: number };
      if (res.status == 200) {
        this.filterAttendance();
        alert(res.data);
        return;
      }
      alert("xoa that bai")
    }
  }

}
