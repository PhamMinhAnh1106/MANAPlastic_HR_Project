import { ChangeDetectorRef, Component, OnInit, OnDestroy } from '@angular/core';
import {
  Changepermission,
  Deletepermission,
  Getpermission,
  GetpermissionRole,
  putPermissionRole,
  type putPermissionRole as PutRoleForm
} from '../../../../services/pages/features/admin/permision.service';
import { NgFor, NgIf, CommonModule } from '@angular/common';
import { Alert } from '../../../shared/alert/alert';
import { Loading } from '../../../shared/loading/loading';
import { Comfirm } from '../../../shared/comfirm/comfirm';
import { FormsModule } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';
import { GetOneAccountInfo } from '../../../../services/pages/features/hr/accountManager.service';
// Import RxJS và hàm tìm kiếm từ utils
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { findUserbyUsername } from '../../../../utils/finduser.utils';
// Lưu ý: Hãy sửa lại đường dẫn import này cho đúng với cấu trúc thư mục thực tế của bạn

interface UserInfo {
  userID: number;
  username: string;
  fullname: string;
  roleName: string;
  departmentName?: string;
  email?: string;
}

interface ChangePer {
  username: string;
  permissionId: number;
  activePermission: number;
}

@Component({
  selector: 'app-permissions',
  standalone: true,
  imports: [CommonModule, Alert, Loading, Comfirm, FormsModule],
  templateUrl: './permissions.html',
  styleUrl: './permissions.scss',
})
export class Permissions implements OnInit, OnDestroy {
  // --- TABS CONFIG ---
  activeTab: 'user' | 'role' = 'user';

  // --- USER TAB VARIABLES ---
  searchUsername: string = '';
  filterKeyword: string = '';
  filterStatus: string = 'all';
  filterCode: string = '';
  filterId: number | null = null;
  filterOverride: boolean = false;

  availableCodes: string[] = [];
  availableIds: number[] = [];
  currentUser: UserInfo | null = null;
  permissions: any[] = [];

  currentPage: number = 0;
  pageSize: number = 10;
  isLastPage: boolean = false;

  // --- AUTOCOMPLETE VARIABLES (NEW) ---
  showUserSearchDropdown: boolean = false;
  userSearchResults: any[] = [];
  private searchSubject = new Subject<string>();
  private searchSubscription: Subscription | undefined;

  // --- ROLE TAB VARIABLES ---
  rolesList: any[] = [
    { id: 1, roleName: 'Admin' },
    { id: 2, roleName: 'HR' },
    { id: 3, roleName: 'Manager' },
    { id: 4, roleName: 'Employee' }
  ];

  selectedRoleId: number | null = null;
  rolePermissions: any[] = [];
  rolePage: number = 0;
  roleSize: number = 10;
  isRoleLastPage: boolean = false;

  // --- COMMON UI STATES ---
  isloading: boolean = false;
  isalert: boolean = false;
  notifyMessage: string = '';
  notifyType: boolean = true;
  isconfirm: boolean = false;
  confirmMessage: string = '';

  private itemToDelete: any = null;
  showEditModal: boolean = false;
  selectedPermission: any = null;

  constructor(private cookie: CookieService, private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.initRoles();

    // Thiết lập Debounce cho tìm kiếm user
    this.searchSubscription = this.searchSubject.pipe(
      debounceTime(300), // Đợi 300ms sau khi ngừng gõ
      distinctUntilChanged() // Chỉ tìm nếu giá trị thay đổi
    ).subscribe(searchValue => {
      this.performUserSearch(searchValue);
    });
  }

  ngOnDestroy(): void {
    // Hủy subscription để tránh leak memory
    if (this.searchSubscription) {
      this.searchSubscription.unsubscribe();
    }
  }

  showNotification(message: string, type: boolean) {
    this.notifyMessage = message;
    this.notifyType = type;
    this.isalert = true;
    this.cdr.detectChanges();
    setTimeout(() => {
      this.isalert = false;
      this.cdr.detectChanges();
    }, 3000);
  }

  switchTab(tab: 'user' | 'role') {
    this.activeTab = tab;
    this.cdr.detectChanges();
  }

  // ==========================================
  // TAB 1: USER LOGIC (UPDATED WITH AUTOCOMPLETE)
  // ==========================================

  // Hàm bắt sự kiện input từ HTML
  onSearchUser(event: any) {
    const value = event.target.value;
    // Nếu rỗng thì ẩn dropdown
    if (!value || value.trim() === '') {
      this.showUserSearchDropdown = false;
      this.userSearchResults = [];
      return;
    }
    // Push vào subject để debounce
    this.searchSubject.next(value);
  }

  // Hàm gọi API tìm kiếm (từ file utils)
  async performUserSearch(username: string) {
    try {
      // Gọi hàm API được yêu cầu
      const res = await findUserbyUsername(username) as any[];

      // Giới hạn 5 kết quả
      if (res && Array.isArray(res)) {
        this.userSearchResults = res.slice(0, 5);
        this.showUserSearchDropdown = this.userSearchResults.length > 0;
      } else {
        this.userSearchResults = [];
        this.showUserSearchDropdown = false;
      }
    } catch (error) {
      console.error(error);
      this.userSearchResults = [];
      this.showUserSearchDropdown = false;
    }
    this.cdr.detectChanges();
  }

  // Hàm chọn user từ dropdown
  selectSearchUser(user: any) {
    // Gán giá trị vào ô input
    this.searchUsername = user.username || user.fullName || '';

    // Ẩn dropdown
    this.showUserSearchDropdown = false;

    // Tự động gọi hàm tìm kiếm quyền chính
    this.searchPermission(true);
  }

  // --- Logic cũ vẫn giữ nguyên ---

  private buildQueryString(): string {
    let query = '';
    if (this.filterKeyword?.trim()) query += `&keyword=${encodeURIComponent(this.filterKeyword.trim())}`;
    if (this.filterCode) query += `&permissionCode=${encodeURIComponent(this.filterCode)}`;
    if (this.filterId) query += `&permissionId=${this.filterId}`;
    if (this.filterStatus !== 'all') {
      if (this.filterStatus === 'null') query += `&activeStatus=null`;
      else query += `&activeStatus=${this.filterStatus}`;
    }
    if (this.filterOverride) query += `&onlyOverride=true`;
    return query;
  }

  applyFilter() {
    this.currentPage = 0;
    this.permissions = [];
    this.searchPermission(false);
  }

  resetFilter() {
    this.filterKeyword = '';
    this.filterCode = '';
    this.filterId = null;
    this.filterStatus = 'all';
    this.filterOverride = false;
    this.applyFilter();
    this.cdr.detectChanges();
  }

  async searchPermission(isNewUserSearch: boolean = false) {
    // Ẩn dropdown gợi ý khi bắt đầu tìm kiếm chính thức
    this.showUserSearchDropdown = false;

    if (!this.searchUsername.trim()) {
      this.showNotification("Vui lòng nhập username", false);
      return;
    }

    if (isNewUserSearch) {
      this.currentPage = 0;
      this.isLastPage = false;
      this.permissions = [];
      this.currentUser = null;
    }

    this.isloading = true;

    try {
      const role = this.cookie.get('role').toLowerCase();
      const queryString = this.buildQueryString();

      const promises: any[] = [
        Getpermission(this.searchUsername, this.currentPage, this.pageSize, queryString)
      ];

      if (isNewUserSearch) {
        promises.push(GetOneAccountInfo(this.searchUsername, role));
      }

      const results = await Promise.all(promises);
      const permRes = results[0];
      const userRes = isNewUserSearch ? results[1] : null;

      if (isNewUserSearch) {
        if (userRes && (userRes.content || userRes.fullname)) {
          this.currentUser = Array.isArray(userRes.content) ? userRes.content[0] : userRes;
        } else {
          this.showNotification("Không tìm thấy thông tin nhân viên (HR)", false);
        }
      }

      if (Array.isArray(permRes)) {
        if (permRes.length === 0 && this.currentPage > 0) {
          this.currentPage--;
          this.isLastPage = true;
          this.showNotification("Đã hiển thị hết dữ liệu.", true);
        } else {
          this.permissions = permRes.filter((p: any) => p.enabledByRole === true);
          this.isLastPage = permRes.length < this.pageSize;

          if (this.permissions.length === 0 && this.currentPage === 0) {
            this.isLastPage = true;
          }
        }

        if (this.permissions.length > 0) {
          const codes = new Set(this.permissions.map((p: any) => p.permissionCode));
          const ids = new Set(this.permissions.map((p: any) => p.permissionId));
          if (this.availableCodes.length === 0) {
            this.availableCodes = Array.from(codes) as string[];
            this.availableCodes.sort();
          }
          if (this.availableIds.length === 0) {
            this.availableIds = Array.from(ids) as number[];
            this.availableIds.sort((a, b) => a - b);
          }
        }
      } else {
        this.permissions = [];
        this.isLastPage = true;
      }

    } catch (error) {
      console.error(error);
      this.showNotification("Lỗi hệ thống hoặc không kết nối được server", false);
      if (isNewUserSearch) this.permissions = [];
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  changePage(delta: number) {
    const newPage = this.currentPage + delta;
    if (delta > 0 && this.isLastPage) return;
    if (newPage < 0) return;

    this.currentPage = newPage;
    this.searchPermission(false);
  }

  openEditModal(permission: any) {
    this.selectedPermission = { ...permission };
    this.showEditModal = true;
  }

  closeEditModal() {
    this.showEditModal = false;
    this.selectedPermission = null;
  }

  async savePermissionChange(value: number) {
    if (!this.selectedPermission || !this.searchUsername) return;
    this.showEditModal = false;
    this.isloading = true;
    this.cdr.detectChanges();

    const form: ChangePer = {
      username: this.searchUsername,
      permissionId: this.selectedPermission.permissionId,
      activePermission: value
    };

    try {
      const res = await Changepermission(form);
      if (res) {
        this.showNotification(`Cập nhật thành công!`, true);
        this.searchPermission(false);
      } else {
        this.showNotification("Cập nhật thất bại", false);
      }
    } catch (e) {
      this.showNotification("Lỗi kết nối", false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  onDeleteClick(permission: any) {
    this.itemToDelete = permission;
    this.confirmMessage = `Reset quyền "${permission.description}" về mặc định?`;
    this.isconfirm = true;
  }

  async onConfirmResult(confirmed: boolean) {
    this.isconfirm = false;
    this.cdr.detectChanges();

    if (confirmed && this.itemToDelete) {
      this.isloading = true;
      this.cdr.detectChanges();

      try {
        const res = await Deletepermission(this.itemToDelete.permissionId, this.searchUsername);
        if (res) {
          this.showNotification("Đã reset quyền thành công!", true);
          this.searchPermission(false);
        } else {
          this.showNotification("Lỗi khi xóa", false);
        }
      } catch (e) {
        this.showNotification("Lỗi kết nối", false);
      } finally {
        this.isloading = false;
        this.itemToDelete = null;
        this.cdr.detectChanges();
      }
    }
  }

  // ==========================================
  // TAB 2: ROLE LOGIC
  // ==========================================

  initRoles() {
    if (this.rolesList.length > 0) {
      this.selectedRoleId = this.rolesList[0].id;
      this.getRolePermissions();
    }
  }

  async onRoleSelectChange() {
    if (this.selectedRoleId) {
      this.rolePage = 0;
      this.getRolePermissions();
    } else {
      this.rolePermissions = [];
      this.cdr.detectChanges();
    }
  }

  async getRolePermissions() {
    if (!this.selectedRoleId) return;
    this.isloading = true;
    this.cdr.detectChanges();

    try {
      const res = await GetpermissionRole(this.selectedRoleId, this.rolePage, this.roleSize);

      if (res && res.content) {
        const newData = res.content;

        if (newData.length === 0 && this.rolePage > 0) {
          this.rolePage--;
          this.isRoleLastPage = true;
          this.showNotification("Hết danh sách quyền.", true);
        } else {
          this.rolePermissions = newData;
          this.isRoleLastPage = res.last !== undefined ? res.last : (newData.length < this.roleSize);
          if (typeof res.number === 'number') {
            this.rolePage = res.number;
          }
        }
      } else if (Array.isArray(res)) {
        if (res.length === 0 && this.rolePage > 0) {
          this.rolePage--;
          this.isRoleLastPage = true;
        } else {
          this.rolePermissions = res;
          this.isRoleLastPage = res.length < this.roleSize;
        }
      } else {
        this.rolePermissions = [];
        this.isRoleLastPage = true;
      }
    } catch (error) {
      console.error(error);
      this.showNotification("Lỗi tải quyền của Role", false);
      this.rolePermissions = [];
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  changeRolePage(delta: number) {
    const newPage = this.rolePage + delta;
    if (newPage < 0) return;
    if (delta > 0 && this.isRoleLastPage) return;

    this.rolePage = newPage;
    this.getRolePermissions();
  }

  async toggleRolePermission(permission: any) {
    if (!this.selectedRoleId) return;

    const previousState = permission.active;
    permission.active = !previousState;
    this.cdr.detectChanges();

    const form: PutRoleForm = {
      roleId: this.selectedRoleId,
      permissionId: permission.permissionId,
      active: permission.active
    };

    try {
      const res: any = await putPermissionRole(form);
      const isSuccess = (res && res.status === 200) || (res && res.data) || res === true;

      if (isSuccess) {
        this.showNotification("Đã cập nhật cấu hình Role", true);
      } else {
        throw new Error("Failed");
      }
    } catch (error) {
      permission.active = previousState;
      this.showNotification("Lỗi cập nhật server", false);
      this.cdr.detectChanges();
    }
  }
}