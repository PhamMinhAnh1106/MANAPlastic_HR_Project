import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import {
  Changepermission,
  Deletepermission,
  Getpermission,
  GetpermissionRole,
  putPermissionRole,
  type putPermissionRole as PutRoleForm
} from '../../../../services/pages/features/admin/permision.service';
import { NgFor, NgIf, NgClass } from '@angular/common';
import { Alert } from '../../../shared/alert/alert';
import { Loading } from '../../../shared/loading/loading';
import { Comfirm } from '../../../shared/comfirm/comfirm';
import { FormsModule } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';
// import { getdataRole } from '../../../../services/pages/getPageRole.service'; // Không cần gọi API lấy list role nữa
import { GetOneAccountInfo } from '../../../../services/pages/features/hr/accountManager.service';

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
  imports: [NgIf, NgFor, NgClass, Alert, Loading, Comfirm, FormsModule],
  templateUrl: './permissions.html',
  styleUrl: './permissions.scss',
})
export class Permissions implements OnInit {
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

  // --- ROLE TAB VARIABLES ---
  // CẤU HÌNH CỐ ĐỊNH DANH SÁCH ROLE THEO YÊU CẦU
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
    // Khởi tạo tab Role với dữ liệu mặc định
    this.initRoles();
  }

  showNotification(message: string, type: boolean) {
    this.notifyMessage = message;
    this.notifyType = type;
    this.isalert = true;
    setTimeout(() => (this.isalert = false), 3000);
  }

  // --- TAB HANDLING ---
  switchTab(tab: 'user' | 'role') {
    this.activeTab = tab;
  }

  // ==========================================
  // TAB 1: USER LOGIC
  // ==========================================

  private buildQueryString(): string {
    let query = '';
    if (this.filterKeyword && this.filterKeyword.trim()) query += `&keyword=${encodeURIComponent(this.filterKeyword.trim())}`;
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
    this.searchPermission(false);
  }

  resetFilter() {
    this.filterKeyword = '';
    this.filterCode = '';
    this.filterId = null;
    this.filterStatus = 'all';
    this.filterOverride = false;
    this.applyFilter();
  }

  async searchPermission(isNewUserSearch: boolean = false) {
    if (!this.searchUsername.trim()) {
      this.showNotification("Vui lòng nhập username", false);
      return;
    }

    if (isNewUserSearch) {
      this.currentPage = 0;
      this.isLastPage = false;
      this.permissions = [];
      this.currentUser = null;
      this.filterKeyword = '';
      this.filterCode = '';
      this.filterId = null;
      this.filterStatus = 'all';
      this.filterOverride = false;
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
        if (userRes && userRes.content) {
          this.currentUser = userRes.content[0] || userRes;
        } else {
          this.showNotification("Không tìm thấy thông tin nhân viên này", false);
        }
      }

      if (permRes && Array.isArray(permRes)) {
        this.isLastPage = permRes.length < this.pageSize;
        this.permissions = permRes.filter((p: any) => p.enabledByRole === true);

        // Populate filters options based on data
        if (this.permissions.length > 0) {
          const codes = new Set(this.permissions.map((p: any) => p.permissionCode));
          const ids = new Set(this.permissions.map((p: any) => p.permissionId));
          this.availableCodes = Array.from(codes) as string[];
          this.availableIds = Array.from(ids) as number[];
          this.availableIds.sort((a, b) => a - b);
          this.availableCodes.sort();
        } else if (isNewUserSearch) {
          this.availableCodes = [];
          this.availableIds = [];
        }
      } else {
        this.permissions = [];
        this.isLastPage = true;
      }
    } catch (error) {
      console.error(error);
      this.showNotification("Lỗi hệ thống hoặc không tìm thấy dữ liệu", false);
      if (isNewUserSearch) this.currentUser = null;
      this.permissions = [];
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  changePage(delta: number) {
    const newPage = this.currentPage + delta;
    if (newPage < 0 || (delta > 0 && this.isLastPage)) return;
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

    const form: ChangePer = {
      username: this.searchUsername,
      permissionId: this.selectedPermission.permissionId,
      activePermission: value
    };

    const res = await Changepermission(form);
    this.isloading = false;

    if (res) {
      this.showNotification(`Cập nhật thành công!`, true);
      this.searchPermission(false);
    } else {
      this.showNotification("Cập nhật thất bại", false);
    }
  }

  onDeleteClick(permission: any) {
    this.itemToDelete = permission;
    this.confirmMessage = `Bạn có chắc muốn xóa thiết lập quyền "${permission.description}" (Reset về mặc định)?`;
    this.isconfirm = true;
  }

  async onConfirmResult(confirmed: boolean) {
    this.isconfirm = false;
    if (confirmed && this.itemToDelete) {
      this.isloading = true;
      const res = await Deletepermission(this.itemToDelete.permissionId, this.searchUsername);
      this.isloading = false;

      if (res) {
        this.showNotification("Đã reset quyền thành công!", true);
        this.searchPermission(false);
      } else {
        this.showNotification("Lỗi khi xóa", false);
      }
      this.itemToDelete = null;
    }
  }

  // ==========================================
  // TAB 2: ROLE LOGIC
  // ==========================================

  initRoles() {
    // Không gọi API getdataRole nữa, dùng danh sách tĩnh đã khai báo ở trên
    // rolesList = [{id:1, roleName:'Admin'}, ...]

    // Tự động chọn Role đầu tiên (Admin)
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
    }
  }

  async getRolePermissions() {
    if (!this.selectedRoleId) return;
    this.isloading = true;
    try {
      const res = await GetpermissionRole(this.selectedRoleId, this.rolePage, this.roleSize);

      // Xử lý response API
      if (res && res.content) {
        this.rolePermissions = res.content;
        this.isRoleLastPage = res.last;
        if (typeof res.number === 'number') {
          this.rolePage = res.number;
        }
      } else if (Array.isArray(res)) {
        this.rolePermissions = res;
        this.isRoleLastPage = res.length < this.roleSize;
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
    if (newPage < 0 || (delta > 0 && this.isRoleLastPage)) return;
    this.rolePage = newPage;
    this.getRolePermissions();
  }

  async toggleRolePermission(permission: any) {
    if (!this.selectedRoleId) return;

    const newActiveState = !permission.active;
    const previousState = permission.active;

    // Cập nhật UI ngay lập tức
    permission.active = newActiveState;

    const form: PutRoleForm = {
      roleId: this.selectedRoleId,
      permissionId: permission.permissionId,
      active: newActiveState
    };

    try {
      const res = await putPermissionRole(form) as { data: string, status: number };
      if (res && (res.status === 200 || res.data)) {
        this.showNotification("Cập nhật quyền Role thành công", true);
      } else {
        permission.active = previousState;
        this.showNotification("Cập nhật thất bại", false);
      }
    } catch (error) {
      permission.active = previousState;
      this.showNotification("Lỗi hệ thống", false);
    }
  }
}