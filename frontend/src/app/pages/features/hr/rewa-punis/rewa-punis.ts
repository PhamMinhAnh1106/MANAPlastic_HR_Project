import { CommonModule } from "@angular/common";
import { ChangeDetectorRef, Component, OnInit } from "@angular/core";
import { FormsModule } from "@angular/forms";
// Import các service từ dự án của bạn (sẽ báo lỗi trên preview nhưng đúng với local)
import { AddRewaPunis, DeleteRewaPunis, GetRewaPunis, RewaPunis, UpdateRewaPunis } from "../../../../services/pages/features/hr/rewaPunis.service";
import { Loading } from "../../../shared/loading/loading";
import { Alert } from "../../../shared/alert/alert";
import { Comfirm } from "../../../shared/comfirm/comfirm";

import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
// Import file utils tìm kiếm user
import { findUserbyUsername } from "../../../../utils/finduser.utils";

@Component({
  selector: 'app-rewa-punis',
  standalone: true,
  imports: [CommonModule, FormsModule, Loading, Alert, Comfirm],
  templateUrl: './rewa-punis.html',
  styleUrls: ['./rewa-punis.scss']
})
export class RewaPunisComponent implements OnInit {
  constructor(private cdr: ChangeDetectorRef) { }
  // --- STATE ---
  dataList: RewaPunis[] = [];
  page = 0;
  size = 10;

  // Custom Components State
  isconfirm: boolean = false;
  isalert: boolean = false;
  isloading: boolean = false;
  confirmMessage = '';
  alertmessage = '';
  alertType: boolean = true;

  // State hỗ trợ logic
  searchParams = { keyword: '', type: '', status: '' };
  showModal = false;
  isEditMode = false;
  currentItem: RewaPunis = this.getEmptyItem();
  editingId: number | null = null;
  itemToDelete: RewaPunis | null = null;

  // --- USER SEARCH STATE ---
  searchSubject = new Subject<string>();
  userSearchResults: any[] = [];
  showUserSearchDropdown = false;
  // Biến hiển thị tên trên input (VD: "Nguyen Van A - #123")
  searchDisplayValue: string = '';

  ngOnInit() {
    this.fetchData();

    // Cấu hình debounce cho tìm kiếm user (chờ 300ms sau khi gõ mới gọi API)
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(keyword => {
      this.performUserSearch(keyword);
    });
  }

  // --- API METHODS ---
  async fetchData() {
    this.isloading = true;

    const queryParts = [];
    if (this.searchParams.keyword) queryParts.push(`keyword=${this.searchParams.keyword}`);
    if (this.searchParams.type) queryParts.push(`type=${this.searchParams.type}`);
    if (this.searchParams.status) queryParts.push(`status=${this.searchParams.status}`);
    const paramString = queryParts.join('&');

    try {
      const result: any = await GetRewaPunis(paramString, this.page, this.size);

      if (result && result.content) {
        this.dataList = result.content;
      } else if (Array.isArray(result)) {
        this.dataList = result;
      } else {
        this.dataList = [];
      }
    } catch (error) {
      console.error(error);
      this.dataList = [];
      this.Onalert("Lỗi tải dữ liệu", false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  onSearch() {
    this.page = 0;
    this.fetchData();
  }

  changePage(delta: number) {
    this.page += delta;
    if (this.page < 0) this.page = 0;
    this.fetchData();
  }

  // --- USER SEARCH LOGIC ---
  onInputSearchUser(event: any) {
    const value = event.target.value;
    this.searchDisplayValue = value;

    if (!value || value.trim() === '') {
      this.showUserSearchDropdown = false;
      this.userSearchResults = [];
      // Nếu xóa hết text tìm kiếm, có thể reset userID hoặc giữ nguyên tùy logic
      return;
    }
    // Đẩy giá trị vào subject để debounce
    this.searchSubject.next(value);
  }

  async performUserSearch(keyword: string) {
    try {
      // Gọi hàm từ finduser.utils.ts
      const res: any = await findUserbyUsername(keyword);

      // Kiểm tra kết quả trả về (giả sử API trả về mảng user hoặc object chứa mảng)
      let users = [];
      if (Array.isArray(res)) {
        users = res;
      } else if (res && Array.isArray(res.data)) {
        users = res.data;
      }

      if (users.length > 0) {
        this.userSearchResults = users.slice(0, 5); // Lấy tối đa 5 kết quả
        this.showUserSearchDropdown = true;
      } else {
        this.userSearchResults = [];
        this.showUserSearchDropdown = false;
      }
    } catch (error) {
      console.error("Search user error:", error);
      this.userSearchResults = [];
      this.showUserSearchDropdown = false;
    }
    this.cdr.detectChanges();
  }

  selectSearchUser(user: any) {
    // Mapping dữ liệu user vào form
    // Giả sử user object có id/userID và fullName/username
    this.currentItem.userID = user.id || user.userID;

    // Cập nhật text hiển thị
    const name = user.fullName || user.username || '';
    this.searchDisplayValue = `${name} (#${this.currentItem.userID})`;

    // Ẩn dropdown
    this.showUserSearchDropdown = false;
  }

  // --- CUSTOM ALERT & CONFIRM LOGIC ---
  Onalert(message: string, type: boolean) {
    this.isalert = true;
    this.alertmessage = message;
    this.alertType = type;
  }

  onDelete(item: RewaPunis) {
    this.itemToDelete = item;
    this.confirmMessage = `Bạn có chắc chắn muốn xóa phiếu của nhân viên #${item.userID}?`;
    this.isconfirm = true;
  }

  async onConfirmResult(result: boolean) {
    this.isconfirm = false;

    if (result && this.itemToDelete) {
      this.isloading = true;
      try {
        const res: any = await DeleteRewaPunis(this.itemToDelete.rewaid);

        if (typeof res === 'string' && res.includes('co loi xay ra')) {
          this.Onalert(res, false);
        } else {
          this.Onalert("Xóa thành công!", true);
          this.fetchData();
        }
      } catch (e) {
        this.Onalert("Có lỗi xảy ra khi xóa", false);
      } finally {
        this.isloading = false;
        this.itemToDelete = null;
        this.cdr.detectChanges();
      }
    } else {
      this.itemToDelete = null;
    }
  }

  // --- MODAL & FORM LOGIC ---
  getEmptyItem(): RewaPunis {
    return {
      rewaid: 0,
      userID: 0,
      type: 'REWARD',
      reason: '',
      decisionDate: new Date().toISOString().split('T')[0],
      amount: 0,
      isTaxExempt: false,
      status: 'PENDING'
    };
  }

  openModal(item?: RewaPunis) {
    // Reset trạng thái search
    this.showUserSearchDropdown = false;
    this.userSearchResults = [];

    if (item) {
      this.isEditMode = true;
      this.currentItem = { ...item };
      this.editingId = item.rewaid;
      // Nếu đang sửa, hiển thị ID lên ô search để user biết
      this.searchDisplayValue = item.userName ? `${item.userName} (#${item.userID})` : `#${item.userID}`;
    } else {
      this.isEditMode = false;
      this.currentItem = this.getEmptyItem();
      this.editingId = null;
      this.searchDisplayValue = '';
    }
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
  }

  async saveData() {
    if (!this.currentItem.userID || !this.currentItem.amount) {
      this.Onalert('Vui lòng chọn nhân viên và nhập số tiền', false);
      return;
    }

    this.isloading = true;

    try {
      let result: any;

      if (this.isEditMode && this.editingId) {
        result = await UpdateRewaPunis(this.editingId, this.currentItem);
      } else {
        result = await AddRewaPunis(this.currentItem);
      }

      console.log("API Result:", result);

      // --- KHẮC PHỤC LỖI STATUS 200 NHƯNG VẪN VÀO CATCH ---
      // Kiểm tra kỹ các trường hợp thành công
      const isSuccess = result === 200 || result?.status === 200 || result?.success === true;

      if (isSuccess) {
        this.Onalert(this.isEditMode ? "Cập nhật thành công!" : "Tạo mới thành công!", true);
        this.closeModal();
        this.fetchData();
      } else {
        // Chỉ cố gắng lấy message lỗi nếu không phải là thành công
        // Sử dụng optional chaining (?.) để tránh lỗi undefined
        const errorMsg = result?.response?.data?.message || result?.message || "Có lỗi xảy ra từ phía server";
        this.Onalert(errorMsg, false);
      }

    } catch (error: any) {
      console.error("SaveData Error:", error);
      // Fallback an toàn cho catch
      const errorMsg = error?.response?.data?.message || "Có lỗi xảy ra (Lỗi hệ thống)";
      this.Onalert(errorMsg, false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }
}