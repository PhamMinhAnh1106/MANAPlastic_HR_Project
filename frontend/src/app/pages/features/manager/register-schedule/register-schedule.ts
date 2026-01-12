import { CommonModule, NgFor, NgIf } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
// Note: Assuming these imports exist or you might need to define the interface locally if not available
import { RegisterScheduleEmployee } from '../../../../services/pages/features/employee/shedule.services';
import { scheduleList } from '../../../../utils/listSchedule.utils';
import { Loading } from '../../../shared/loading/loading';
import { Alert } from '../../../shared/alert/alert';
import { Comfirm } from '../../../shared/comfirm/comfirm';

// Define the interfaces based on the requirement
export interface specificDays {
  date: string;
  shiftId: number;
}

export interface registerShiftRoute {
  fromDate: string;
  toDate: string;
  rangeShiftId: number;
  specificDays?: specificDays[];
}

@Component({
  selector: 'app-register-schedule',
  imports: [CommonModule, FormsModule, NgFor, NgIf, Loading, Alert, Comfirm],
  templateUrl: './register-schedule.html',
  styleUrl: './register-schedule.scss',
})
export class RegisterSchedule implements OnInit {
  constructor(private router: Router, private cdr: ChangeDetectorRef) { }

  ////////////////////////
  isconfirm: boolean = false;
  isalert: boolean = false;
  isloading: boolean = false;
  confirmMessage = '';
  alertmessage = '';
  alertType: boolean = true;
  actionType: 'approve' | 'reject' | '' = '';

  Onalert(message: string, type: boolean) {
    this.isalert = true;
    this.alertmessage = message;
    this.alertType = type;
  }
  /////////////////////////

  // New model properties
  fromDate: string = "";
  toDate: string = "";
  rangeShiftId: any = "";

  // Array for specific days
  specificDays: specificDays[] = [];

  isOpen = true;
  list: any[] = [];

  closePopup() {
    this.router.navigate(["/home/schedule"]);
  }

  changeType(hours: number) {
    scheduleList(hours, this.list); // gọi API lấy ca theo số giờ
  }

  // Add a new specific day row
  addSpecificDay() {
    this.specificDays.push({
      date: '',
      shiftId: null as any // Placeholder, bound to ngModel
    });
  }

  // Remove a specific day row
  removeSpecificDay(index: number) {
    this.specificDays.splice(index, 1);
  }

  async onConfirmResult(event: any) {
    if (event == true) {
      this.isloading = true;
      this.isconfirm = false;

      // Construct the payload matching registerShiftRoute interface
      const payload: registerShiftRoute = {
        fromDate: this.fromDate,
        toDate: this.toDate,
        rangeShiftId: Number(this.rangeShiftId),
        // Only include specificDays if there are any
        specificDays: this.specificDays.length > 0 ? this.specificDays.map(d => ({
          date: d.date,
          shiftId: Number(d.shiftId)
        })) : undefined
      };

      try {
        const res = await RegisterScheduleEmployee(payload) as { data: string, status: number };

        if (res.status == 200) {
          this.isconfirm = false;
          this.isloading = false;
          this.Onalert("Đăng ký thành công", true);
          this.cdr.detectChanges();
          return;
        }

        this.isconfirm = false;
        this.isloading = false;
        this.Onalert("Đăng ký thất bại: " + (res.data || "Lỗi không xác định"), false);
        this.cdr.detectChanges();

      } catch (error) {
        this.isconfirm = false;
        this.isloading = false;
        this.Onalert("Đã xảy ra lỗi kết nối", false);
      }

    } else {
      this.isconfirm = false;
    }
  }

  submit() {
    // Basic client-side validation logic if needed
    if (new Date(this.fromDate) > new Date(this.toDate)) {
      this.Onalert("Ngày bắt đầu không được lớn hơn ngày kết thúc", false);
      return;
    }

    this.isconfirm = true;
    this.confirmMessage = "Bạn có chắc muốn đăng ký lịch này?";
  }

  ngOnInit(): void {
  }
}