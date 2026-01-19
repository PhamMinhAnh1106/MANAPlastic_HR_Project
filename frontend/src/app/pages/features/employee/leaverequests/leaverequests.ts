import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; // Dùng CommonModule thay vì import lẻ NgFor, NgIf
import { Router } from '@angular/router';

// Imports từ project của bạn
import { leaverequestBalance, leaverequests } from '../../../../interface/leaverequest.interface';
import { Deleteleaverequest, getBalanceleaverequest, getleaverequest } from '../../../../services/pages/features/employee/leaverequest.services';
import { Alert } from '../../../shared/alert/alert';
import { Comfirm } from '../../../shared/comfirm/comfirm';
import { Loading } from '../../../shared/loading/loading';

// Interface mở rộng để phục vụ hiển thị giao diện (thêm icon và màu)
interface UILeaveBalance extends leaverequestBalance {
  icon?: string;
  colorClass?: string;
}

@Component({
  selector: 'app-leaverequests',
  standalone: true,
  imports: [CommonModule, Alert, Comfirm, Loading], // Gom NgFor, NgIf, NgClass vào CommonModule
  templateUrl: './leaverequests.html',
  styleUrl: './leaverequests.scss',
})
export class Leaverequests implements OnInit {
  // --- STATE VARIABLES ---
  isloading: boolean = false;

  // Confirm Modal State
  isconfirm: boolean = false;
  confirmMessage = "";
  idToDelete = 0; // Đổi tên biến id -> idToDelete cho rõ nghĩa

  // Alert/Notification State
  isalert: boolean = false;
  notifyMessage = "";
  notifyType: boolean = true; // true: success, false: error

  // Data State
  leaverequest: leaverequests[] = [];
  leaverequestBl: UILeaveBalance[] = []; // Dùng interface mở rộng

  constructor(private cdr: ChangeDetectorRef, private router: Router) { }

  // --- LIFECYCLE ---
  async ngOnInit() {
    this.isloading = true;
    try {
      // Gọi song song 2 API để tiết kiệm thời gian
      const [requests, balances] = await Promise.all([
        this.getLeaverequest(),
        this.getLeaverequestbalance()
      ]);

      this.leaverequest = requests;
      // Map data balance thô sang data có giao diện (icon, màu)
      this.leaverequestBl = this.mapBalanceData(balances);
      console.log(this.leaverequestBl)
    } catch (error) {
      console.error("Lỗi tải dữ liệu:", error);
      this.showNotification("Không thể tải dữ liệu", false);
    } finally {
      this.isloading = false;
      this.cdr.detectChanges();
    }
  }

  // --- API HELPERS ---
  async getLeaverequest() {
    return await getleaverequest();
  }

  async getLeaverequestbalance() {
    return await getBalanceleaverequest();
  }

  // --- UI LOGIC ---

  /**
   * Hàm này giúp gán Icon và Màu sắc cho từng loại phép
   * API chỉ trả về text, UI cần class CSS
   */
  mapBalanceData(data: leaverequestBalance[]): UILeaveBalance[] {
    if (!data) return [];

    return data.map(item => {
      let icon = 'star'; // Default icon
      let colorClass = 'blue-card'; // Default color

      // Logic gán icon/màu dựa trên tên loại phép
      // Bạn hãy sửa lại string so sánh ('Phép năm') cho khớp với DB của bạn
      const typeName = item.leaveType ? item.leaveType.toLowerCase() : '';

      if (typeName.includes('năm') || typeName.includes('annual')) {
        icon = 'calendar_month';
        colorClass = 'blue-card';
      } else if (typeName.includes('ốm') || typeName.includes('sick')) {
        icon = 'medical_services';
        colorClass = 'orange-card';
      } else if (typeName.includes('bù') || typeName.includes('comp')) {
        icon = 'timelapse';
        colorClass = 'purple-card';
      }

      return {
        ...item,
        icon: icon,
        colorClass: colorClass
      };
    });
  }

  add() {
    this.router.navigate(["/home/leaverequest/add"]);
  }

  showNotification(message: string, type: boolean) {
    this.notifyMessage = message;
    this.notifyType = type;
    this.isalert = true;

    // Tự động ẩn thông báo sau 3s (tùy chọn)
    setTimeout(() => {
      this.isalert = false;
      this.cdr.detectChanges();
    }, 3000);
  }

  // --- DELETE LOGIC ---
  deleteRequest(id: number) {
    this.idToDelete = id;
    this.confirmMessage = "Bạn có chắc chắn muốn xóa đơn nghỉ này không?";
    this.isconfirm = true;
  }

  async onConfirmResult(event: boolean) {
    // Đóng modal confirm ngay lập tức
    this.isconfirm = false;

    if (event === true) {
      this.isloading = true; // Hiện loading khi đang xóa
      try {
        const res: any = await Deleteleaverequest(this.idToDelete);

        if (res.status === 200) {
          this.showNotification("Xóa thành công!", true);
          // Reload lại danh sách sau khi xóa
          this.leaverequest = await this.getLeaverequest();
        } else {
          this.showNotification(res.response.data.message || "Không thể xóa đơn này", false);
        }
      } catch (error) {
        this.showNotification("Lỗi hệ thống khi xóa", false);
      } finally {
        this.isloading = false;
        this.cdr.detectChanges();
      }
    }
  }
}