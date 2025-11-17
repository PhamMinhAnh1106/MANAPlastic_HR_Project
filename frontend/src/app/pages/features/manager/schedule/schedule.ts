import { CommonModule, NgFor, NgIf } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ChangeSchedule, schedule, userSchedule } from '../../../../interface/schedule.interface';
import { GetScheduleEmployeeoffice, GetScheduleEmployeeDraft, GetScheduleManagerdraft, GetScheduleManageroffice, ChangeScheduleManager, UpSchedule } from '../../../../services/pages/features/employee/shedule.services';
import { CookieService } from 'ngx-cookie-service';
import { scheduleList } from '../../../../utils/listSchedule.utils';
import { Tablemonth } from '../../../shared/tablemonth/tablemonth';

@Component({
  selector: 'app-schedule',
  imports: [CommonModule, FormsModule, Tablemonth],
  templateUrl: './schedule.html',
  styleUrl: './schedule.scss',
})
export class Schedule implements OnInit {

  @ViewChild(Tablemonth) tb!: Tablemonth; // lấy instance Tablemonth trên template

  async ngOnInit() {
    // chờ view init xong để tb đã render
    setTimeout(async () => {
      if (!this.tb) return;
      const month = this.tb.month;
      const year = this.tb.year;
      const my = `${year}-${month}`;
      const apiData = await GetScheduleEmployeeDraft(my); // API trả về mảng {date, shiftName,...}

      const days = this.tb.getAllDays();
      this.tb.mergedDays = this.tb.mergeSchedule(days, apiData);
      console.log("MERGED DATA:");
      console.table(this.tb.mergedDays);
    }, 0);
  }
}

