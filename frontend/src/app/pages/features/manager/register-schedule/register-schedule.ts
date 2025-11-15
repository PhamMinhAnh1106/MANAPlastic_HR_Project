import { CommonModule, NgFor } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { schedule } from '../../../../interface/schedule.interface';
import { RegisterScheduleEmployee } from '../../../../services/pages/features/employee/shedule.services';
import { scheduleList } from '../../../../utils/listSchedule.utils';

@Component({
  selector: 'app-register-schedule',
  imports: [CommonModule, FormsModule, NgFor,],
  templateUrl: './register-schedule.html',
  styleUrl: './register-schedule.scss',
})
export class RegisterSchedule {
  constructor(private router: Router) { }

  date: string = "";
  shiftId: any = "";
  isdateoff: boolean = false;


  shifts: any[] = [];
  selectedShift: any = null;
  isOpen = true;
  closePopup() {
    this.router.navigate(["/home/info"]);
  }
  list: any[] = [];

  changeType(hours: number) {
    scheduleList(hours, this.list); // gọi API lấy ca theo số giờ
  }

  async submit() {
    if (this.shiftId > 52)
      this.isdateoff = true;

    const forms: schedule = {
      date: this.date,
      shiftId: this.shiftId,
      isDayOff: this.isdateoff
    }
    const res = await RegisterScheduleEmployee(forms) as { data: string, status: number };
    if (res.status == 200) {

      alert(res.data)
      return;
    }
    alert("dang ky that bai")
  }

}
