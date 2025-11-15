import { CommonModule, NgFor } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { schedule } from '../../../../interface/schedule.interface';
import { RegisterScheduleEmployee } from '../../../../services/pages/features/employee/shedule.services';

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
  scheduleList(time: number) {

    // Nếu time = 8 thì cộng 9 giờ
    const duration = (time === 8) ? time + 1 : time;

    // Tạo prefix C601 hoặc C801
    const prefix = `C${time}`;
    let shiftId: number = 0;
    if (time == 6) {
      this.list.length = 0;
      shiftId = 4;
    } else {
      this.list.length = 0;
      shiftId = 28;
    }
    for (let i = 1; i <= 24; i++) {

      // Giờ bắt đầu
      const startHour = i % 24;

      // Giờ kết thúc (quay vòng 24h)
      const endHour = (startHour + duration) % 24;

      // Format 01:00:00
      const start = startHour.toString().padStart(2, "0") + ":00:00";
      const end = endHour.toString().padStart(2, "0") + ":00:00";

      this.list.push({
        shift_id: shiftId + i,
        shift_name: `${prefix}${i.toString().padStart(2, "0")}`,
        start_time: start,
        end_time: end
      });
    }

    return this.list;
  }
  changeType(hours: number) {
    this.scheduleList(hours); // gọi API lấy ca theo số giờ
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
