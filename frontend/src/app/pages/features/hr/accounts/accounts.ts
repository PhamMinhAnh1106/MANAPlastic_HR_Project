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
  searchSuggestions: any[] = []; // Danh sách gợi ý hiển thị trong popup
  showSuggestions: boolean = false; // Trạng thái hiển thị popup
  isSearching: boolean = false; // Trạng thái đang gọi API background

  ngOnInit(): void {
    this.role = this.cookie.get("role");

    // Setup Live Search Subscription
    this.setupLiveSearch();

    // Gọi hàm load dữ liệu ban đầu
    this.filterEmployees();
  }

  ngOnDestroy(): void {
    // Unsubscribe để tránh memory leak khi component bị hủy
    if (this.searchSubscription) {
      this.searchSubscription.unsubscribe();
    }
  }

  setupLiveSearch() {
    this.searchSubscription = this.searchSubject.pipe(
      // 1. Chờ 1000ms (1s) sau khi người dùng ngừng gõ
      debounceTime(1000),

      // 2. Chỉ tiếp tục nếu giá trị thay đổi so với lần trước
      distinctUntilChanged(),

      // 3. Hủy request cũ nếu có request mới (switchMap)
      switchMap(keyword => {
        // FIX: Chuyển về string an toàn trước khi trim
        const safeKeyword = String(keyword || '');

        if (safeKeyword.trim() === '') {
          this.showSuggestions = false;
          return of({ content: [] }); // Trả về mảng rỗng nếu không có keyword
        }

        this.isSearching = true; // Bật loading nhỏ nếu cần
        this.cdr.detectChanges();

        // Gọi API (Chuyển Promise sang Observable bằng 'from')
        return from(GetOneAccountInfo(safeKeyword, this.role)).pipe(
          catchError(error => {
            console.error('Search error:', error);
            return of({ content: [] }); // Trả về rỗng khi lỗi
          })
        );
      })
    ).subscribe((res: any) => {
      this.isSearching = false;

      // Xử lý kết quả trả về
      // Giả sử API trả về object có content. Nếu content là array thì lấy, nếu là object đơn lẻ thì bọc vào array
      if (res && res.content) {
        if (Array.isArray(res.content)) {
          this.searchSuggestions = res.content;
        } else if (res.content.userID) {
          // Trường hợp API trả về 1 object duy nhất
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

  // Hàm được gọi mỗi khi user gõ vào ô input (từ template)
  onSearchInput(event: any) {
    const value = event.target.value;
    this.searchSubject.next(value);
  }

  // Khi user chọn 1 item trong popup
  selectSuggestion(item: any) {
    // FIX: Chuyển về string để tránh lỗi type mismatch
    this.filter.userID = String(item.username);
    this.showSuggestions = false;
    this.onPageSizeChange(); // Gọi hàm lọc chính để hiển thị dữ liệu ra bảng
  }

  // Ẩn popup khi click ra ngoài hoặc focus out (tùy chọn, ở đây mình xử lý đơn giản bằng click overlay hoặc delay)
  hideSuggestions() {
    // Delay nhỏ để kịp nhận sự kiện click vào item
    setTimeout(() => {
      this.showSuggestions = false;
      this.cdr.detectChanges();
    }, 1000);
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

  onPageChange(page: number) {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.filterEmployees();
    }
  }

  searchAll = 0;
  onPageSizeChange() {
    this.currentPage = 0;
    this.filterEmployees();
  }

  async applyAdvancedFilter() {
    const query = Object.entries(this.filter)
      .filter(([_, value]) => value !== '')
      .map(([key, value]) => `${key}=${encodeURIComponent(value)}`)
      .join('&');

    if (this.employee.length > 0) this.employee = [];
    try {
      this.isloading = true;
      const res = await FilterUser(query, this.role);
      if (res.content.length > 0) {
        this.employee.push(res.content);
        this.toggleAdvancedFilter();
        this.totalPages = 1;
        this.totalElements = res.length;
      }
    } catch (error) {
      // 4. Xử lý lỗi (API chết, mất mạng, lỗi code backend...)
      console.error('Lỗi khi lọc nâng cao:', error);

      // Ví dụ: Hiển thị thông báo cho user (nếu có service notification)
      // this.notificationService.error('Đã xảy ra lỗi khi tải dữ liệu.');

    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  openEditModal(emp: any) {
    this.selectedEmployee = { ...emp };
  }

  async filterEmployees() {
    this.isloading = true;

    // Reset mảng dữ liệu hiển thị
    if (this.employee.length > 0) this.employee = [];

    // FIX: Đảm bảo keyword luôn là string
    const rawKeyword = this.filter.userID;
    const keyword = rawKeyword ? String(rawKeyword) : '';

    let res: any;

    if (keyword && keyword.trim() !== '') {
      res = await GetOneAccountInfo(keyword, this.role);
    }

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
          this.showNotification("Số điện thoại không hợp lệ (phải từ 10 đến 12 số)", false);
          return;

        }
      }
      this.isloading = true;
      const res = await UpdateAccounthr(this.emp, this.role) as { data: any, status: number };

      this.isloading = false;
      if (res.status == 200) {
        this.showNotification(res.data, true);
        this.selectedEmployee = null;
        this.filterEmployees(); // Reload lại trang hiện tại
      } else {
        this.showNotification(res.data.response.data.message, false);
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