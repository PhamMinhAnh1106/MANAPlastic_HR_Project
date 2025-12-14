import { CommonModule, NgFor, NgIf, NgClass, DatePipe } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { GetAccountInfo, GetOneAccountInfo, UpdateAccounthr } from '../../../../services/pages/features/hr/accountManager.service';
import { Department } from '../../../../interface/user/user.interface';
import { CookieService } from 'ngx-cookie-service';
import { FilterUser } from '../../../../utils/filters.utils';
import { Loading } from '../../../shared/loading/loading';
import { Comfirm } from '../../../shared/comfirm/comfirm';
import { Alert } from '../../../shared/alert/alert';

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

  constructor(private cdr: ChangeDetectorRef, private cookie: CookieService) { }

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
    // Đã xóa hàm this.filterEmployees() ở đây để không tự động load dữ liệu
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

    if (query.length > 0) {
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
  }

  openEditModal(emp: any) {
    this.selectedEmployee = { ...emp };
  }
  async filterEmployees() {
    // Nếu filter.userID rỗng thì truyền giá trị mặc định là 0
    if (this.filter.userID == "") {

      this.showNotification("Còn thiếu username hoặc id nhân viên", false);
      return;
    }
    const keyword = this.filter.userID;


    this.isloading = true;

    // Reset mảng dữ liệu hiển thị
    if (this.employee.length > 0) this.employee = [];
    console.log(keyword);
    // Gọi API mới với page và size

    const res = await GetOneAccountInfo(keyword, this.role);

    this.isloading = false;

    if (res && res.content) {
      this.employee.push(res.content);
      this.totalPages = res.totalPages;
      this.totalElements = res.totalElements;
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
  }

  async onConfirmResult(event: any) {
    this.isconfirm = false;
    if (event === true) {
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
          this.employee[0].sort((a: any, b: any) => Number(a.gender) - Number(b.gender));
        } else {
          this.employee[0].sort((a: any, b: any) => Number(b.gender) - Number(a.gender));
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