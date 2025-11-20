import { CommonModule, NgFor } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';
import { Approveleaverequest, getleaverequestManage, Rejectleaverequest } from '../../../../services/pages/features/employee/leaverequest.services';

@Component({
  selector: 'app-leaverequestcheck',
  imports: [CommonModule, FormsModule, NgFor],
  templateUrl: './leaverequestcheck.html',
  styleUrl: './leaverequestcheck.scss',
})
export class Leaverequestcheck {
  constructor(private cdr: ChangeDetectorRef, private cookie: CookieService) { }
  role: string = "";
  filter = {
    username: '',

  };
  leaveRequests: any[] = [];

  async filterLeave() {
    const res = await getleaverequestManage(this.filter.username);
    this.leaveRequests = res;
    this.cdr.detectChanges();
  }

  async approve(id: number) {
    if (!confirm("Bạn có chắc muốn Duyệt đơn nghỉ này?")) return;
    const res = await Approveleaverequest(id) as { status: number, data: string };
    if (res.status == 201) {
      alert(res.data);
      this.filterLeave();
      return;
    }
    alert(res.data);
  }

  async reject(id: number) {
    if (!confirm("Bạn có chắc muốn từ chối đơn nghỉ này?")) return;

    const res = await Rejectleaverequest(id) as { status: number, data: string };

    if (res.status == 201) {
      alert(res.data);
      this.filterLeave();
      return;
    }
    alert(res.data);

  }


}
