import { CommonModule } from "@angular/common";
import { ChangeDetectorRef, Component, OnInit } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { AddRewaPunis, DeleteRewaPunis, GetRewaPunis, RewaPunis, UpdateRewaPunis } from "../../../../services/pages/features/hr/rewaPunis.service";
import { Loading } from "../../../shared/loading/loading";
import { Alert } from "../../../shared/alert/alert";
import { Comfirm } from "../../../shared/comfirm/comfirm";


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

  ngOnInit() {
    this.fetchData();
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

  // --- CUSTOM ALERT & CONFIRM LOGIC ---
  Onalert(message: string, type: boolean) {
    this.isalert = true;
    this.alertmessage = message;
    this.alertType = type;
  }

  onDelete(item: RewaPunis) {
    this.itemToDelete = item;
    // Hiển thị userID để người dùng biết đang xóa của ai, nhưng logic xóa sẽ dùng rewaid
    this.confirmMessage = `Bạn có chắc chắn muốn xóa phiếu của nhân viên #${item.userID}?`;
    this.isconfirm = true;
  }

  async onConfirmResult(result: boolean) {
    this.isconfirm = false;

    if (result && this.itemToDelete) {
      this.isloading = true;
      try {
        // CẬP NHẬT: Dùng rewaid để xóa
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
      rewaid: 0, // CẬP NHẬT: Thêm rewaid
      userID: 0,
      type: 'REWARD',
      reason: '',
      decisionDate: new Date().toISOString().split('T')[0],
      amount: 0,
      isTaxExempt: false,
      status: 'Pending'
    };
  }

  openModal(item?: RewaPunis) {
    if (item) {
      this.isEditMode = true;
      this.currentItem = { ...item };
      // CẬP NHẬT: Lấy rewaid làm ID để sửa
      this.editingId = item.rewaid;
    } else {
      this.isEditMode = false;
      this.currentItem = this.getEmptyItem();
      this.editingId = null;
    }
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
  }

  async saveData() {
    if (!this.currentItem.userID || !this.currentItem.amount) {
      this.Onalert('Vui lòng nhập ID nhân viên và số tiền', false);
      return;
    }

    this.isloading = true;
    let result: any;
    console.log(this.editingId)
    try {
      if (this.isEditMode && this.editingId) {
        // CẬP NHẬT: Gọi API update với rewaid (editingId)
        result = await UpdateRewaPunis(this.editingId, this.currentItem);
      } else {
        // result = await AddRewaPunis(this.currentItem);
      }

      if (typeof result === 'string' && result.includes('co loi xay ra')) {
        this.Onalert(result, false);
      } else {
        this.Onalert(this.isEditMode ? "Cập nhật thành công!" : "Thêm mới thành công!", true);
        this.closeModal();
        this.fetchData();
      }
    } catch (error) {
      this.Onalert("Có lỗi xảy ra", false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }
}