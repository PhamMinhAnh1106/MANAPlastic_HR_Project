import { CommonModule, NgFor, NgIf, NgClass, DatePipe } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { GetAccountInfo, GetOneAccountInfo, UpdateAccounthr } from '../../../../services/pages/features/hr/accountManager.service';
import { Department, information } from '../../../../interface/user/user.interface';
import { CookieService } from 'ngx-cookie-service';
import { FilterUser } from '../../../../utils/filters.utils';
import { Loading } from '../../../shared/loading/loading';
import { Comfirm } from '../../../shared/comfirm/comfirm';
import { Alert } from '../../../shared/alert/alert';
import { Router } from '@angular/router';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [CommonModule, FormsModule, NgFor, NgIf, NgClass, Loading, Comfirm, Alert],
  templateUrl: './accounts.html',
  styleUrl: './accounts.scss',
})
export class Accounts implements OnInit {
  sortByIdDesc: boolean = false;
  sortByUsernameDesc: boolean = false;
  sortByGenderDesc: boolean = false;
  sortByBirthdayDesc: boolean = false;

  constructor(private cdr: ChangeDetectorRef, private cookie: CookieService, private route: Router) { }

  employee: any = [];
  editID: number | null = null;
  role: string = "";

  // Pagination States
  currentPage: number = 0;
  pageSize: number = 2; // Set mặc định là 2 theo yêu cầu
  totalPages: number = 0;
  totalElements: number = 0;
  pageSizeOptions: number[] = [2, 5, 10, 20, 50]; // Các tùy chọn kích thước trang

  // States
  isloading: boolean = false;
  isconfirm: boolean = false;
  confirmMessage = "";
  isalert: boolean = false;
  notifyMessage = "";
  notifyType: boolean = true;

  selectedEmployee: any = null;
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

  ngOnInit(): void {
    this.role = this.cookie.get("role");
    // Gọi hàm load dữ liệu ngay khi khởi tạo
    this.filterEmployees();
  }

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

  // Xử lý thay đổi trang
  onPageChange(page: number) {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.filterEmployees();
    }
  }
  searchAll = 0;
  // Xử lý thay đổi số lượng item trên trang hoặc khi bấm Lọc
  onPageSizeChange() {
    this.currentPage = 0;
    this.filterEmployees();
  }

  async applyAdvancedFilter() {
    const query = Object.entries(this.filter)
      .filter(([_, value]) => value !== '')
      .map(([key, value]) => `${key}=${encodeURIComponent(value)}`)
      .join('&');

    this.isloading = true;
    if (this.employee.length > 0) this.employee = [];

    const res = await FilterUser(query, this.role);

    if (res.content.length > 0) {
      this.employee.push(res.content);
      this.toggleAdvancedFilter();
      this.totalPages = 1;
      this.totalElements = res.length;
    }

    this.isloading = false;
    this.cdr.detectChanges();
  }

  openEditModal(emp: any) {
    this.selectedEmployee = { ...emp };
  }

  async filterEmployees() {
    this.isloading = true;

    // Reset mảng dữ liệu hiển thị
    if (this.employee.length > 0) this.employee = [];

    const keyword = this.filter.userID;
    let res: any;

    // LOGIC PHÂN TRANG & TÌM KIẾM ĐƯỢC CẬP NHẬT
    // Nếu có keyword -> Dùng API Tìm kiếm (GetOneAccountInfo)
    if (keyword && keyword.trim() !== '') {
      res = await GetOneAccountInfo(keyword, this.role);
    }
    // Nếu không có keyword -> Dùng API Lấy danh sách phân trang (GetAccountInfo)


    this.isloading = false;

    if (res && res.content) {
      this.employee.push(res.content);
      this.totalPages = res.totalPages;
      this.totalElements = res.totalElements;
      this.cdr.detectChanges();
    } else {
      this.totalPages = 0;
      this.totalElements = 0;
    }

    this.cdr.detectChanges();
  }

  saveEmployee(emp: any) {
    this.isconfirm = true;
    this.confirmMessage = "Bạn có chắc muốn sửa thông tin nhân viên này?";
    this.emp = emp;
    console.log(this.emp)
  }

  async onConfirmResult(event: any) {
    this.isconfirm = false;
    if (event === true) {
      if (this.emp.cccd.length != 12) {
        this.showNotification("CCCD phải đúng 12 chữ số", false);
        return;
      }
      if (this.emp.phonenumber.length != 10) {

        if (this.emp.phonenumber.toString().charAt(0) !== '0') {
          this.showNotification("Số điện thoại phải bắt đầu bằng số 0", false);
          return;
        }
        if (this.emp.phonenumber.length < 10) {
          console.log("Phone number length:");
          this.showNotification("Số điện thoại không hợp lệ (phải từ 10 đến 12 số)", false);
          return;

        }
      }
      this.isloading = true;
      const res = await UpdateAccounthr(this.emp, this.role) as { data: string, status: number };

      this.isloading = false;
      if (res.status == 200) {
        this.showNotification(res.data, true);
        this.selectedEmployee = null;
        this.filterEmployees(); // Reload lại trang hiện tại
      } else {
        this.showNotification(res.data, false);
      }
      this.cdr.detectChanges();
    }
  }

  cancelEdit() {
    this.selectedEmployee = null;
  }
  addaccount() {
    this.route.navigate(['/home/add/account'])
  }
  sort(x: any) {
    if (!this.employee || this.employee.length === 0) return;

    switch (x) {
      case 'id':
        if (this.sortByIdDesc) {
          this.employee[0].sort((a: any, b: any) => a.userID - b.userID);
        } else {
          this.employee[0].sort((a: any, b: any) => b.userID - a.userID);
        }
        this.sortByIdDesc = !this.sortByIdDesc;
        break;

      case 'name':
        if (this.sortByUsernameDesc) {
          this.employee[0].sort((a: any, b: any) => (a.username || '').localeCompare(b.username || ''));
        } else {
          this.employee[0].sort((a: any, b: any) => (b.username || '').localeCompare(a.username || ''));
        }
        this.sortByUsernameDesc = !this.sortByUsernameDesc;
        break;

      case 'gender':
        if (this.sortByGenderDesc) {
          this.employee[0].sort((a: any, b: any) =>
            a.gender.localeCompare(b.gender)
          );
        } else {
          this.employee[0].sort((a: any, b: any) =>
            b.gender.localeCompare(a.gender)
          );
        }
        this.sortByGenderDesc = !this.sortByGenderDesc;
        break;

      case 'born':
        if (this.sortByBirthdayDesc) {
          this.employee[0].sort((a: any, b: any) => new Date(a.birth).getTime() - new Date(b.birth).getTime());
        } else {
          this.employee[0].sort((a: any, b: any) => new Date(b.birth).getTime() - new Date(a.birth).getTime());
        }
        this.sortByBirthdayDesc = !this.sortByBirthdayDesc;
        break;
    }
    this.cdr.detectChanges();
  }
}