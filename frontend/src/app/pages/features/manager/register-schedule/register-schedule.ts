import { CommonModule, NgFor, NgIf } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { schedule } from '../../../../interface/schedule.interface';
import { RegisterScheduleEmployee } from '../../../../services/pages/features/employee/shedule.services';
import { scheduleList } from '../../../../utils/listSchedule.utils';
import { Loading } from '../../../shared/loading/loading';
import { Alert } from '../../../shared/alert/alert';
import { Comfirm } from '../../../shared/comfirm/comfirm';

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
  date: string = "";
  shiftId: any = "";
  isdateoff: boolean = false;


  shifts: any[] = [];
  selectedShift: any = null;
  isOpen = true;
  closePopup() {
    this.router.navigate(["/home/schedule"]);
  }
  list: any[] = [];

  changeType(hours: number) {
    scheduleList(hours, this.list); // gọi API lấy ca theo số giờ
  }
  async onConfirmResult(event: any) {
    if (event == true) {
      this.isloading = true;
      this.isconfirm = false;
      if (this.shiftId > 52)
        this.isdateoff = true;

      const forms: schedule = {
        date: this.date,
        shiftId: this.shiftId,
        isDayOff: this.isdateoff
      }
      const res = await RegisterScheduleEmployee(forms) as { data: string, status: number };
      if (res.status == 200) {
        this.isconfirm = false;
        this.isloading = false;

        this.Onalert(res.data, true);
        this.cdr.detectChanges();
        return;
      }
      this.isconfirm = false;
      this.isloading = false;

      this.Onalert("dang ky that bai", false);
      this.cdr.detectChanges();

    } else {

      this.isconfirm = false;

    }
  }
  submit() {
    this.isconfirm = true;
    this.confirmMessage = "Bạn có chắc muốn đăng ký lịch này ?";
  }

  ngOnInit(): void {
  }
}
