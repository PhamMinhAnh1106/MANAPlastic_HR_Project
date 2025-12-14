import { ChangeDetectorRef, Component } from '@angular/core';
import {
  Changepermission,
  Deletepermission,
  Getpermission,
} from '../../../../services/pages/features/admin/permision.service';
import { NgFor, NgIf } from '@angular/common';
import { Alert } from '../../../shared/alert/alert';
import { Loading } from '../../../shared/loading/loading';
import { Comfirm } from '../../../shared/comfirm/comfirm';
import { FormsModule } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';
import { getdataRole } from '../../../../services/pages/getPageRole.service';
import { GetAccountInfo, GetOneAccountInfo } from '../../../../services/pages/features/hr/accountManager.service';

// Interface định nghĩa dữ liệu trả về từ API User Info
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
  imports: [NgIf, NgFor, Alert, Loading, Comfirm, FormsModule],
  templateUrl: './permissions.html',
  styleUrl: './permissions.scss',
})
export class Permissions {
  // --- State Variables ---
  searchUsername: string = '';

  // Biến lưu thông tin User hiển thị ở phần trên
  currentUser: UserInfo | null = null;

  // Biến lưu danh sách quyền hiển thị ở bảng dưới
  permissions: any[] = [];

  // Pagination
  currentPage: number = 0;
  pageSize: number = 10;
  isLastPage: boolean = false;

  // UI States
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

  showNotification(message: string, type: boolean) {
    this.notifyMessage = message;
    this.notifyType = type;
    this.isalert = true;
    setTimeout(() => this.isalert = false, 3000);
  }

  // --- TÌM KIẾM ---
  async searchPermission(isNewSearch: boolean = true) {
    // 1. Validate đầu vào
    if (!this.searchUsername.trim()) {
      this.showNotification("Vui lòng nhập username", false);
      return;
    }

    // 2. Reset trạng thái nếu là tìm kiếm mới
    if (isNewSearch) {
      this.currentPage = 0;
      this.isLastPage = false;
      this.permissions = [];
      this.currentUser = null;
    }

    this.isloading = true;

    try {
      // 3. Gọi song song 2 API:
      // - API 1: Lấy thông tin User (Avatar, tên, role...) dựa trên username
      // - API 2: Lấy danh sách Quyền (Permissions) dựa trên username
      const role = this.cookie.get('role').toLowerCase();
      const [userRes, permRes] = await Promise.all([
        GetOneAccountInfo(this.searchUsername, role),
        Getpermission(this.searchUsername, this.currentPage, this.pageSize)
      ]);

      // --- Xử lý kết quả User Info ---
      if (isNewSearch) {
        if (userRes.content) {
          // Gán dữ liệu vào biến currentUser để hiển thị lên HTML
          // Kiểm tra cấu trúc API trả về (có thể là userRes hoặc userRes.content)
          this.currentUser = userRes.content[0] || userRes;
          console.log(this.currentUser)
        } else {
          this.showNotification("Không tìm thấy thông tin nhân viên này", false);
        }
      }

      // --- Xử lý kết quả Permissions ---
      if (permRes && Array.isArray(permRes)) {
        if (permRes.length < this.pageSize) {
          this.isLastPage = true;
        }
        // Filter chỉ lấy các quyền được phép bởi Role (tùy logic backend)
        this.permissions = permRes.filter((p: any) => p.enabledByRole === true);

        console.log(this.permissions)
        this.isloading = false;
        this.cdr.detectChanges();
      } else {
        this.permissions = [];
      }

    } catch (error) {
      console.error(error);
      this.showNotification("Lỗi hệ thống hoặc không tìm thấy dữ liệu", false);
      this.currentUser = null;
      this.permissions = [];
    } finally {
      this.isloading = false;
    }
  }

  // Phân trang
  changePage(delta: number) {
    const newPage = this.currentPage + delta;
    if (newPage < 0 || (delta > 0 && this.isLastPage)) return;
    this.currentPage = newPage;
    this.searchPermission(false); // False nghĩa là không cần load lại User Info
  }

  // --- CÁC HÀM XỬ LÝ QUYỀN (GIỮ NGUYÊN) ---

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
      this.searchPermission(false); // Reload lại bảng quyền
    } else {
      this.showNotification("Cập nhật thất bại", false);
    }
  }

  // --- XÓA QUYỀN (RESET) ---
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
}