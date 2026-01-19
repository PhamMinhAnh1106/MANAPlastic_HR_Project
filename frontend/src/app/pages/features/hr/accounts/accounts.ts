import { CommonModule, NgFor, NgIf, NgClass, DatePipe } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { GetAccountInfo, GetOneAccountInfo, UpdateAccounthr } from '../../../../services/pages/features/hr/accountManager.service';
import { Department, information } from '../../../../interface/user/user.interface';
import { CookieService } from 'ngx-cookie-service';
import { FilterUser } from '../../../../utils/filters.utils';
import { Loading } from '../../../shared/loading/loading';
import { Comfirm } from '../../../shared/comfirm/comfirm';
import { Alert } from '../../../shared/alert/alert';
import { Router } from '@angular/router';
import { Subject, of, from, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, catchError, map } from 'rxjs/operators';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [CommonModule, FormsModule, NgFor, NgIf, NgClass, Loading, Comfirm, Alert],
  templateUrl: './accounts.html',
  styleUrl: './accounts.scss',
})
export class Accounts implements OnInit, OnDestroy {
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
  pageSize: number = 2;
  totalPages: number = 0;
  totalElements: number = 0;
  pageSizeOptions: number[] = [2, 5, 10, 20, 50];

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

  // --- Search Suggestion Variables ---
  private searchSubject = new Subject<string>();
  searchSubscription?: Subscription;
  searchSuggestions: any[] = [];
  showSuggestions: boolean = false;
  isSearching: boolean = false;

  ngOnInit(): void {
    this.role = this.cookie.get("role");
    this.setupLiveSearch();
    this.filterEmployees();
  }

  ngOnDestroy(): void {
    if (this.searchSubscription) {
      this.searchSubscription.unsubscribe();
    }
  }

  setupLiveSearch() {
    this.searchSubscription = this.searchSubject.pipe(
      debounceTime(1000),
      distinctUntilChanged(),
      switchMap(keyword => {
        const safeKeyword = String(keyword || '');
        if (safeKeyword.trim() === '') {
          this.showSuggestions = false;
          return of({ content: [] });
        }
        this.isSearching = true;
        this.cdr.detectChanges();

        return from(GetOneAccountInfo(safeKeyword, this.role)).pipe(
          catchError(error => {
            console.error('Search error:', error);
            return of({ content: [] });
          })
        );
      })
    ).subscribe((res: any) => {
      this.isSearching = false;
      if (res && res.content) {
        if (Array.isArray(res.content)) {
          this.searchSuggestions = res.content;
        } else if (res.content.userID) {
          this.searchSuggestions = [res.content];
        } else {
          this.searchSuggestions = [];
        }
      } else {
        this.searchSuggestions = [];
      }
      this.showSuggestions = true;
      this.cdr.detectChanges();
    });
  }

  onSearchInput(event: any) {
    const value = event.target.value;
    this.searchSubject.next(value);
  }

  selectSuggestion(item: any) {
    this.filter.userID = String(item.username);
    this.showSuggestions = false;
    this.onPageSizeChange();
  }

  hideSuggestions() {
    setTimeout(() => {
      this.showSuggestions = false;
      this.cdr.detectChanges();
    }, 1000);
  }

  showNotification(message: string, type: boolean) {
    this.notifyMessage = message;
    this.notifyType = type;
    this.isalert = true;

    // Auto hide alert after 3 seconds if needed, though app-alert usually handles this
    // setTimeout(() => { this.isalert = false; this.cdr.detectChanges(); }, 3000);
    this.cdr.detectChanges();
  }

  toggleAdvancedFilter() {
    this.showAdvancedFilter = !this.showAdvancedFilter;
  }

  onHireDateStartChange() {
    if (!this.filter.hireDateStart) {
      this.filter.hireDateEnd = '';
    }
  }

  onPageChange(page: number) {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.filterEmployees();
    }
  }

  onPageSizeChange() {
    this.currentPage = 0;
    this.filterEmployees();
  }

  // --- CẬP NHẬT LOGIC TRY-CATCH CHO FILTER ---
  async applyAdvancedFilter() {
    const query = Object.entries(this.filter)
      .filter(([_, value]) => value !== '')
      .map(([key, value]) => `${key}=${encodeURIComponent(value)}`)
      .join('&');

    if (this.employee.length > 0) this.employee = [];

    this.isloading = true;
    try {
      const res = await FilterUser(query, this.role);

      if (res && res.content && res.content.length > 0) {
        this.employee.push(res.content);
        this.toggleAdvancedFilter(); // Đóng bộ lọc khi thành công
        this.totalPages = 1;
        this.totalElements = res.length || res.content.length;
        this.showNotification(`Tìm thấy ${this.totalElements} kết quả`, true);
      } else {
        this.showNotification("Không tìm thấy dữ liệu phù hợp", false);
        this.totalPages = 0;
        this.totalElements = 0;
      }
    } catch (error: any) {
      console.error('Lỗi khi lọc nâng cao:', error);
      // Lấy thông báo lỗi chi tiết
      const msg = error?.response?.data?.message || error?.message || "Đã xảy ra lỗi khi lọc dữ liệu.";
      this.showNotification(msg, false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  openEditModal(emp: any) {
    this.selectedEmployee = { ...emp };
  }

  // --- CẬP NHẬT LOGIC TRY-CATCH CHO LOAD DANH SÁCH ---
  async filterEmployees() {
    this.isloading = true;
    if (this.employee.length > 0) this.employee = [];

    const rawKeyword = this.filter.userID;
    const keyword = rawKeyword ? String(rawKeyword) : '';

    try {
      let res: any;
      if (keyword && keyword.trim() !== '') {
        res = await GetOneAccountInfo(keyword, this.role);
      }
      // Note: Nếu không có keyword, bạn có thể cần gọi API Get All User ở đây nếu logic yêu cầu
      // Hiện tại code cũ chỉ gọi GetOneAccountInfo khi có keyword hoặc init

      if (res && res.content) {
        this.employee.push(res.content);
        this.totalPages = res.totalPages || 1;
        this.totalElements = res.totalElements || 1;
      } else {
        this.totalPages = 0;
        this.totalElements = 0;
      }
    } catch (error: any) {
      console.error("Lỗi tải dữ liệu:", error);
      const msg = error?.response?.data?.message || "Không thể tải danh sách nhân viên";
      this.showNotification(msg, false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  saveEmployee(emp: any) {
    this.emp = emp;
    this.isconfirm = true;
    this.confirmMessage = "Bạn có chắc muốn sửa thông tin nhân viên này?";
  }

  // --- CẬP NHẬT LOGIC TRY-CATCH CHO UPDATE (QUAN TRỌNG) ---
  async onConfirmResult(event: any) {
    this.isconfirm = false; // Đóng popup confirm ngay lập tức

    if (event === true) {
      // Validate dữ liệu client-side
      if (this.emp.cccd.length != 12) {
        this.showNotification("CCCD phải đúng 12 chữ số", false);
        return;
      }
      if (this.emp.phonenumber.length != 10) {
        if (this.emp.phonenumber.toString().charAt(0) !== '0') {
          this.showNotification("Số điện thoại phải bắt đầu bằng số 0", false);
          return;
        }
        this.showNotification("Số điện thoại không hợp lệ (phải có 10 số)", false);
        return;
      }
      if (this.emp.bankAccount)

        this.isloading = true;
      try {
        // Gọi API cập nhật
        const res: any = await UpdateAccounthr(this.emp, this.role);

        if (res && res.status == 200) {
          // Thành công
          const successMsg = typeof res.data === 'string' ? res.data : "Cập nhật thành công!";
          this.showNotification(successMsg, true);
          this.selectedEmployee = null; // Đóng Modal Edit
          this.filterEmployees(); // Tải lại dữ liệu nếu cần
        } else {
          // Trường hợp status != 200 nhưng không vào catch
          // Cố gắng lấy message sâu nhất
          const errorMsg = res?.response?.data?.message;
          this.showNotification(errorMsg, false);
        }
      } catch (error: any) {
        // Trường hợp API trả về lỗi (400, 401, 500...)
        console.error("Lỗi cập nhật:", error);

        // Trả đủ thông tin lỗi từ backend
        const errorMsg = error?.response?.data?.message  // Lỗi từ backend trả về (thường gặp nhất)
          || error?.error?.message
          || error?.message
          || "Lỗi hệ thống khi cập nhật";

        this.showNotification(errorMsg, false);
      } finally {
        this.isloading = false;
        this.cdr.detectChanges();
      }
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
          this.employee[0].sort((a: any, b: any) => a.gender.localeCompare(b.gender));
        } else {
          this.employee[0].sort((a: any, b: any) => b.gender.localeCompare(a.gender));
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