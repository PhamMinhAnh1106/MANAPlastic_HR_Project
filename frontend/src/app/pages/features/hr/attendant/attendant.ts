import { CommonModule, NgFor, NgIf, NgClass, DatePipe } from '@angular/common';
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
  standalone: true,
  imports: [CommonModule, FormsModule, NgFor, NgIf, NgClass, Loading, Alert, Comfirm],
  templateUrl: './attendant.html',
  styleUrl: './attendant.scss',
})
export class Attendant implements OnInit {
  constructor(private cdr: ChangeDetectorRef, private cookie: CookieService) { }

  role: string = "";
  id: number = 0;

  // Pagination States
  page: number = 0;       // Trang hiện tại (bắt đầu từ 0)
  size: number = 10;      // Số dòng mỗi trang
  totalPages: number = 0; // Tổng số trang
  totalElements: number = 0; // Tổng số bản ghi
  pageSizeOptions: number[] = [5, 10, 20, 50]; // Các tùy chọn hiển thị

  filter = {
    date: '',
    month: '',
    year: '',
    departmentId: '',
    status: ''
  };

  selectedProof: any = null;

  // States UI
  isconfirm: boolean = false;
  isalert: boolean = false;
  isloading: boolean = false;
  confirmMessage = '';
  alertmessage = '';
  alertType: boolean = true;

  attendance: attendance[] = [];
  selectedAttendance: any = null;

  // Data cho Filter
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

  Onalert(message: string, type: boolean) {
    this.isalert = true;
    this.alertmessage = message;
    this.alertType = type;
  }

  showProof(att: any) {
    this.selectedProof = att;
  }

  closeProof() {
    this.selectedProof = null;
  }

  // --- LOGIC PHÂN TRANG MỚI ---

  // Hàm gọi API lấy dữ liệu (đã cập nhật page/size)
  async filterAttendance() {
    this.isloading = true;

    const query = Object.entries(this.filter)
      .filter(([_, value]) => value !== '')
      .map(([key, value]) => `${key}=${encodeURIComponent(value)}`)
      .join('&');

    // Gọi API với query param, role, page và size
    // Lưu ý: page backend thường bắt đầu từ 0
    const res = await GetAttendants(query, this.role, this.page, this.size);

    this.isloading = false;

    if (res) {
      // Giả định response trả về cấu trúc Page của Spring Boot: { content, totalPages, totalElements, ... }
      // Nếu API trả về khác, bạn cần map lại cho đúng
      this.attendance = res.content || [];
      this.totalPages = res.totalPages || 0;
      this.totalElements = res.totalElements || 0;
    } else {
      this.attendance = [];
      this.totalElements = 0;
    }

    this.cdr.detectChanges();
  }

  // Khi người dùng bấm nút Lọc -> Reset về trang 0
  onSearch() {
    this.page = 0;
    this.filterAttendance();
  }

  // Khi đổi trang (Prev/Next)
  onPageChange(newPage: number) {
    if (newPage >= 0 && newPage < this.totalPages) {
      this.page = newPage;
      this.filterAttendance();
    }
  }

  // Khi đổi số lượng dòng hiển thị (5, 10, 20...)
  onPageSizeChange() {
    this.page = 0; // Reset về trang đầu
    this.filterAttendance();
  }

  // -----------------------------

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
      case 'PRESENT': return "Có mặt";
      case 'ABSENT': return "Vắng mặt";
      case 'LATE_AND_EARLY': return "Đi trễ / Về sớm";
      case 'ON_LEAVE': return "Nghỉ phép";
      case 'MISSING_OUTPUT_DATA': return "Thiếu dữ liệu check-out";
      case 'MISSING_INPUT_DATA': return "Thiếu dữ liệu check-in";
      default: return "Không xác định";
    }
  }

  getTime(datetime: string): string {
    if (!datetime) return "-";
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
          this.isloading = false;
          this.filterAttendance(); // Reload lại trang hiện tại
          this.Onalert("Xóa thành công", true);
          return;
        }
        this.isloading = false;
        this.Onalert("Xóa Thất Bại", false);
      }
    } else {
      this.isconfirm = false;
    }
  }

  deleteAttendance(id: number) {
    this.isconfirm = true;
    this.confirmMessage = "Bạn chắc chắn muốn xóa dữ liệu này ?";
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
    // Kiểm tra role và load dữ liệu lần đầu
    if (this.cookie.get("role")) {
      this.role = this.cookie.get("role").toLowerCase();
    }
  }
}