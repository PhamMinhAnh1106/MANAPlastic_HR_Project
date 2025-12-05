import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { leaverequestRegister } from '../../../../interface/leaverequest.interface';
import { Registerleaverequest } from '../../../../services/pages/features/employee/leaverequest.services';
import { Alert } from '../../../shared/alert/alert';
import { Comfirm } from '../../../shared/comfirm/comfirm';
import { Loading } from '../../../shared/loading/loading';

@Component({
  selector: 'app-addleaverequest',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    Alert, Comfirm, Loading
  ],
  templateUrl: './addleaverequest.html',
  styleUrl: './addleaverequest.scss',
})
export class ADdleaverequest {
  constructor(private router: Router) { }

  // --- 1. KHAI BÁO BIẾN CHO HIỆU ỨNG ---
  isloading = false;
  isconfirm = false;
  isalert = false;
  confirmMessage = '';
  notifyMessage = '';
  // SỬA LẠI: notifyType chỉ nhận boolean (true = success, false = error/warning)
  notifyType: boolean = true;
  // ------------------------------------------------

  leaveBalance = [
    { leaveId: "ANNUAL", leaveType: "AL (Anually Leave)" },
    { leaveId: "SICK", leaveType: "SL (Sick Leave)" },
    { leaveId: "MATERNITY", leaveType: "ML (Maternity Leave)" },
    { leaveId: "PARTENITY", leaveType: "PL (Paternity Leave)" },
    { leaveId: "UNPAID", leaveType: "UL (Unpaid Leave)" }
  ];

  leaveRequest: leaverequestRegister = {
    leavetype: '',
    startdate: '',
    enddate: '',
    reason: ''
  };

  // --- 2. HÀM KIỂM TRA TRƯỚC KHI GỬI ---
  preSubmitCheck() {
    if (!this.leaveRequest.leavetype || !this.leaveRequest.startdate || !this.leaveRequest.enddate || !this.leaveRequest.reason) {
      // Warning -> false
      this.showAlert("Vui lòng nhập đầy đủ thông tin!", false);
      return;
    }
    // Mở popup xác nhận
    this.confirmMessage = "Bạn có chắc chắn muốn gửi đơn đăng ký này?";
    this.isconfirm = true;
  }

  // --- 3. HÀM XỬ LÝ KẾT QUẢ XÁC NHẬN ---
  async onConfirmResult(confirmed: boolean) {
    this.isconfirm = false; // Đóng popup
    if (confirmed) {
      await this.submitLeaveRequest(); // Nếu đồng ý thì mới gọi API
    }
  }

  // --- 4. CẬP NHẬT HÀM SUBMIT ---
  async submitLeaveRequest() {
    // Bật loading
    this.isloading = true;

    try {
      const res = await Registerleaverequest(this.leaveRequest) as { status: number, data: string };

      // Tắt loading khi có kết quả
      this.isloading = false;

      if (res.status == 201) {
        // Success -> true
        this.showAlert(res.data, true);

        // Đợi 1.5s cho người dùng đọc thông báo rồi mới chuyển trang
        setTimeout(() => {
          this.router.navigate(["/home/leaverequest"]);
        }, 1500);
        return;
      }

      // Error -> false
      this.showAlert(res.data, false);

    } catch (error) {
      this.isloading = false;
      // Error -> false
      this.showAlert("Lỗi hệ thống, vui lòng thử lại sau!", false);
    }
  }

  // --- 5. HÀM HELPER HIỂN THỊ ALERT (ĐÃ SỬA TYPE) ---
  showAlert(message: string, type: boolean) {
    this.notifyMessage = message;
    this.notifyType = type;
    this.isalert = true;
  }

  closeForm() {
    this.router.navigate(["/home/leaverequest"]);
  }
}