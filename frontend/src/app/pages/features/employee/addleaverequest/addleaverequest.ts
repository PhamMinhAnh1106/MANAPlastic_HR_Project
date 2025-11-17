import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { leaverequestRegister } from '../../../../interface/leaverequest.interface';
import { Registerleaverequest } from '../../../../services/pages/features/employee/leaverequest.services';

@Component({
  selector: 'app-addleaverequest',
  imports: [CommonModule, FormsModule],
  templateUrl: './addleaverequest.html',
  styleUrl: './addleaverequest.scss',
})
export class ADdleaverequest {
  constructor(private router: Router) { }
  leaveBalance = [
    { leaveType: "AL (Anually Leave)" },
    { leaveType: "SL (Sick Leave)" },
    { leaveType: "ML (Maternity Leave)" }
  ];

  leaveRequest: leaverequestRegister = {
    leavetype: '',
    startdate: '',
    enddate: '',
    reason: ''
  };

  async submitLeaveRequest() {
    if (!this.leaveRequest.leavetype || !this.leaveRequest.startdate || !this.leaveRequest.enddate || !this.leaveRequest.reason) {
      alert("Vui lòng nhập đầy đủ thông tin!");
      return;
    }
    const res = await Registerleaverequest(this.leaveRequest) as { status: number, data: string };
    if (res.status == 201) {
      alert(res.data);
      this.router.navigate(["/home/leaverequest"]);

      return;
    }
    alert(res.data);

  }

  closeForm() {
    this.router.navigate(["/home/leaverequest"]);
  }
}
