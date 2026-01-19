import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject } from '@angular/core';
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
  cdr = inject(ChangeDetectorRef);
  currentDate: string;

  constructor(private router: Router) {
    const today = new Date();
    const year = today.getFullYear();
    const month = ('0' + (today.getMonth() + 1)).slice(-2);
    const day = ('0' + today.getDate()).slice(-2);

    this.currentDate = `${year}-${month}-${day}`;
  }

  // --- VARIABLES ---
  isloading = false;
  isconfirm = false;
  isalert = false;
  confirmMessage = '';
  notifyMessage = '';
  notifyType: boolean = true;

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

  // Hàm này giờ chỉ còn dùng để bật popup xác nhận
  // Vì nút Lưu đã bị disable nếu thiếu dữ liệu, nên check logic if ở đây là dự phòng an toàn
  preSubmitCheck() {
    if (!this.leaveRequest.leavetype || !this.leaveRequest.startdate || !this.leaveRequest.enddate || !this.leaveRequest.reason) {
      this.showAlert("Vui lòng nhập đầy đủ thông tin!", false);
      return;
    }
    this.confirmMessage = "Bạn có chắc chắn muốn gửi đơn đăng ký này?";
    this.isconfirm = true;
  }

  async onConfirmResult(confirmed: boolean) {
    this.isconfirm = false;
    if (confirmed) {
      await this.submitLeaveRequest();
    }
  }

  async submitLeaveRequest() {
    // Nếu gọi trực tiếp từ nút Save (đã check valid form), ta có thể gửi luôn
    // Hoặc gọi preSubmitCheck() nếu muốn hiện popup Confirm trước khi gửi.
    // Hiện tại code HTML đang gọi trực tiếp submitLeaveRequest(), mình giữ nguyên để không đổi luồng của bạn.

    this.isloading = true;

    try {
      const res: any = await Registerleaverequest(this.leaveRequest);
      this.isloading = false;

      if (res.status == 201) {
        this.showAlert(res.data, true);
        setTimeout(() => {
          this.router.navigate(["/home/leaverequest"]);
        }, 1500);
        return;
      }

      this.showAlert(res.response.data.message, false);

    } catch (error: any) {
      this.isloading = false;
      this.showAlert(error, false);
      console.log(error);
    } finally {
      this.cdr.detectChanges();
    }
  }

  showAlert(message: string, type: boolean) {
    this.notifyMessage = message;
    this.notifyType = type;
    this.isalert = true;
  }

  closeForm() {
    this.router.navigate(["/home/leaverequest"]);
  }
}