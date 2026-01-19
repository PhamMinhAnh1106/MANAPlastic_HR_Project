import { CommonModule, NgFor, NgIf, NgClass, DatePipe } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser'; // Import Sanitizer
import { CookieService } from 'ngx-cookie-service';

// Service Imports
import { DeleteAttendant, GetAttendants } from '../../../../services/pages/features/hr/attendant.service';
import { ExportFileDataAttendance } from '../../../../services/pages/features/hr/contracts.service';
import { AddNewAttendanceRequests } from '../../../../services/pages/features/hr/datacheck.service';

// Components
import { Loading } from '../../../shared/loading/loading';
import { Alert } from '../../../shared/alert/alert';
import { Comfirm } from '../../../shared/comfirm/comfirm';
import { DataAttendant } from '../data-attendant/data-attendant';
import { getImageChamcong } from '../../../../utils/getimage.utils';
import { getAllSchedules } from '../../../../utils/listSchedule.utils';

interface attendance {
  attendanceId: number,
  attendanceDate: string,
  userName: string,
  fullNameUser: string,
  checkIn: string,
  checkOut: string,
  checkInImg: string,
  checkOutImg: string,
  shiftId: number,
  shiftName: string,
  status: string,
  estimatedSalary: number // Thêm trường lương ước tính
}

@Component({
  selector: 'app-attendant',
  standalone: true,
  imports: [CommonModule, FormsModule, NgFor, NgIf, NgClass, Loading, Alert, Comfirm, DataAttendant],
  templateUrl: './attendant.html',
  styleUrl: './attendant.scss',
})
export class Attendant implements OnInit {

  currentDate: string;
  constructor(
    private cdr: ChangeDetectorRef,
    private cookie: CookieService,
    private sanitizer: DomSanitizer // Inject Sanitizer
  ) {
    const today = new Date();
    const year = today.getFullYear();
    const month = ('0' + (today.getMonth() + 1)).slice(-2);
    const day = ('0' + today.getDate()).slice(-2);

    this.currentDate = `${year}-${month}-${day}`;
  }






  role: string = "";
  id: number = 0;

  // --- TAB STATE ---
  activeTab: 'list' | 'approval' = 'list';

  // Pagination States
  page: number = 0;
  size: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;
  pageSizeOptions: number[] = [5, 10, 20, 50];

  filter = {
    date: '',
    month: '',
    year: '',
    departmentId: '',
    status: ''
  };

  // --- PROOF MODAL STATE (Updated) ---
  selectedProof: any = null;

  // Variables for Proof Images
  proofCheckInUrl: SafeUrl | null = null;
  proofCheckOutUrl: SafeUrl | null = null;
  isLoadingCheckIn: boolean = false;
  isLoadingCheckOut: boolean = false;

  // Store raw blob urls to revoke later
  private rawCheckInUrl: string | null = null;
  private rawCheckOutUrl: string | null = null;

  // States UI
  isconfirm: boolean = false;
  isalert: boolean = false;
  isloading: boolean = false;
  confirmMessage = '';
  alertmessage = '';
  alertType: boolean = true;

  attendance: attendance[] = [];
  selectedAttendance: any = null;

  // --- VARIABLES CHO FORM BỔ SUNG CÔNG ---
  isAdding: boolean = false;
  selectedFile: File | null = null;
  selectedFileName: string = '';

  shiftsList: any[] = [];

  newRequest = {
    date: '',
    time: '08:00',
    requestType: 'CHECK_IN',
    shiftId: 36,
    reason: ''
  };

  // Data cho Filter
  months = Array.from({ length: 12 }, (_, i) => i + 1);
  years = [2023, 2024, 2025, 2026, 2027];
  departments = [
    { id: 1, name: 'Phòng Ban Nhân Sự' },
    { id: 2, name: 'Phòng Ban IT' },
    { id: 3, name: 'Phòng Ban Kỹ Thuật' },
    { id: 4, name: 'Phòng Ban Sản Xuất' },
    { id: 5, name: 'Phòng Ban In Ấn' },
    { id: 6, name: 'Phòng Ban Chăm Sóc Khách Hàng' },
  ];

  // --- TAB FUNCTION ---
  switchTab(tab: 'list' | 'approval') {
    this.activeTab = tab;
    sessionStorage.setItem('attendantActiveTab', tab);
    if (tab === 'list') {
      this.filterAttendance();
    }
  }

  // --- LOGIC HIỂN THỊ MINH CHỨNG (NEW API) ---
  showProof(att: any) {
    this.selectedProof = att;
    this.resetProofImages();

    // 1. Load Check-In Image
    if (att.checkInImg) {
      this.loadCheckInImage(att.checkInImg);
    }

    // 2. Load Check-Out Image
    if (att.checkOutImg) {
      this.loadCheckOutImage(att.checkOutImg);
    }
  }

  async loadCheckInImage(fileName: string) {
    this.isLoadingCheckIn = true;
    try {

      const apiName = fileName.startsWith('/') ? fileName : `/${fileName}`;
      const blob = await getImageChamcong(apiName);

      if (blob && blob instanceof Blob) {
        const url = URL.createObjectURL(blob);
        this.rawCheckInUrl = url;
        this.proofCheckInUrl = this.sanitizer.bypassSecurityTrustUrl(url);
      } else {
        this.proofCheckInUrl = null;
      }
    } catch (error) {
      console.error("Error loading checkin img:", error);
      this.proofCheckInUrl = null;
    } finally {
      this.isLoadingCheckIn = false;
      this.cdr.detectChanges();
    }
  }

  async loadCheckOutImage(fileName: string) {
    this.isLoadingCheckOut = true;
    try {
      const apiName = fileName.startsWith('/') ? fileName : `/${fileName}`;
      const blob = await getImageChamcong(apiName);

      if (blob && blob instanceof Blob) {
        const url = URL.createObjectURL(blob);
        this.rawCheckOutUrl = url;
        this.proofCheckOutUrl = this.sanitizer.bypassSecurityTrustUrl(url);
      } else {
        this.proofCheckOutUrl = null;
      }
    } catch (error) {
      console.error("Error loading checkout img:", error);
      this.proofCheckOutUrl = null;
    } finally {
      this.isLoadingCheckOut = false;
      this.cdr.detectChanges();
    }
  }

  resetProofImages() {
    this.proofCheckInUrl = null;
    this.proofCheckOutUrl = null;
    this.isLoadingCheckIn = false;
    this.isLoadingCheckOut = false;

    // Revoke old URLs
    if (this.rawCheckInUrl) URL.revokeObjectURL(this.rawCheckInUrl);
    if (this.rawCheckOutUrl) URL.revokeObjectURL(this.rawCheckOutUrl);

    this.rawCheckInUrl = null;
    this.rawCheckOutUrl = null;
  }

  closeProof() {
    this.selectedProof = null;
    this.resetProofImages();
  }

  // --- LOGIC MODAL BỔ SUNG ---
  openAddModal() {
    this.newRequest = {
      date: new Date().toISOString().split('T')[0],
      time: '08:00',
      requestType: 'CHECK_IN',
      shiftId: 36,
      reason: ''
    };
    this.selectedFile = null;
    this.selectedFileName = '';
    this.isAdding = true;
  }

  closeAddModal() {
    this.isAdding = false;
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      this.selectedFileName = file.name;
    }
  }

  async submitAdd() {
    if (!this.newRequest.date || !this.newRequest.reason) {
      this.Onalert("Vui lòng nhập đầy đủ ngày và lý do!", false);
      return;
    }
    if (!this.selectedFile) {
      this.Onalert("Vui lòng tải lên ảnh minh chứng!", false);
      return;
    }

    this.isloading = true;
    try {
      const checkInTimeCombined = `${this.newRequest.date}T${this.newRequest.time}:00`;
      const payload = {
        date: this.newRequest.date,
        requestType: this.newRequest.requestType,
        shiftId: this.newRequest.shiftId,
        checkInTime: checkInTimeCombined,
        reason: this.newRequest.reason,
        file: this.selectedFile
      };

      const res: any = await AddNewAttendanceRequests(payload);

      if (res.data && res.status === 200) {
        this.Onalert(res.data, true);
        this.closeAddModal();
        if (this.activeTab === 'list') {
          this.filterAttendance();
        }
      } else {
        this.Onalert(res.data.response.data.message, false);
      }
    } catch (error: any) {
      this.Onalert(error.response.data.message, false);
      console.error(error);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  // ... (Other helpers: Onalert, filterAttendance, etc. keep same)
  Onalert(message: string, type: boolean) {
    this.isalert = true;
    this.alertmessage = message;
    this.alertType = type;
  }

  async filterAttendance() {
    this.isloading = true;
    const query = Object.entries(this.filter)
      .filter(([_, value]) => value !== '')
      .map(([key, value]) => `${key}=${encodeURIComponent(value)}`)
      .join('&');

    const res = await GetAttendants(query, this.page, this.size);
    this.isloading = false;

    if (res) {
      this.attendance = res.content || [];
      this.totalPages = res.totalPages || 0;
      this.totalElements = res.totalElements || 0;
    } else {
      this.attendance = [];
      this.totalElements = 0;
    }
    this.cdr.detectChanges();
  }

  onSearch() {
    this.page = 0;
    this.filterAttendance();
  }

  onPageChange(newPage: number) {
    if (newPage >= 0 && newPage < this.totalPages) {
      this.page = newPage;
      this.filterAttendance();
    }
  }

  onPageSizeChange() {
    this.page = 0;
    this.filterAttendance();
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
    return date.toTimeString().split(" ")[0];
  }

  async onConfirmResult(event: any) {
    if (event == true) {
      this.isconfirm = false;
      if (this.role == "hr") {
        this.isloading = true;
        const res = await DeleteAttendant(this.id) as { data: string, status: number };
        if (res.status == 200) {
          this.isloading = false;
          this.filterAttendance();
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

  async exportfile() {
    const query = this.buildQueryParams(this.filter);
    await ExportFileDataAttendance(query);
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

  ngOnInit(): void {
    this.shiftsList = getAllSchedules();
    if (this.cookie.get("role")) {
      this.role = this.cookie.get("role").toLowerCase();
    }
    const savedTab = sessionStorage.getItem('attendantActiveTab');
    if (savedTab === 'list' || savedTab === 'approval') {
      this.activeTab = savedTab;
    }
    if (this.activeTab === 'list') {
      this.filterAttendance();
    }
  }
}