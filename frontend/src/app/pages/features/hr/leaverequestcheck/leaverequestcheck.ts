import { CommonModule, NgFor, NgIf } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';
import { Approveleaverequest, getleaverequestManage, Rejectleaverequest } from '../../../../services/pages/features/employee/leaverequest.services';
import { leaverequests } from '../../../../interface/leaverequest.interface';
import { Loading } from '../../../shared/loading/loading';
import { Alert } from '../../../shared/alert/alert';
import { Comfirm } from '../../../shared/comfirm/comfirm';

@Component({
  selector: 'app-leaverequestcheck',
  imports: [CommonModule, FormsModule, NgFor, NgIf, Loading, Alert, Comfirm],
  templateUrl: './leaverequestcheck.html',
  styleUrl: './leaverequestcheck.scss',
})
export class Leaverequestcheck {
  constructor(private cdr: ChangeDetectorRef, private cookie: CookieService) { }
  role: string = "";
  filter = {
    username: '',
    status: ''
  };
  leaveRequests: leaverequests[] = [];

  id: number = 0;
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
  async filterLeave() {
    const res: leaverequests[] = await getleaverequestManage(this.filter.username);
    if (this.filter.status == '') {
      this.leaveRequests = res;
      this.cdr.detectChanges();

      return;
    }
    this.leaveRequests = res.filter(item => item.status === this.filter.status)
    this.cdr.detectChanges();
  }

  async onConfirmResultApprove(event: any) {
    if (event == true) {
      this.isconfirm = false;
      const res = await Approveleaverequest(this.id) as { status: number, data: string };
      if (res.status == 201) {
        this.Onalert(res.data, true);
        this.filterLeave();
        return;
      }
      this.Onalert(res.data, false);

    } else {
      this.isconfirm = false;
    }
  }
  async onConfirmResult(event: any) {
    if (!event) {
      this.isconfirm = false;
      return;
    }

    this.isconfirm = false;

    if (this.actionType === 'approve') {
      const res = await Approveleaverequest(this.id) as { data: string, status: number };
      this.Onalert(res.data, res.status === 201);
      this.filterLeave();
    }

    if (this.actionType === 'reject') {
      const res = await Rejectleaverequest(this.id) as { data: string, status: number };
      this.Onalert(res.data, res.status === 201);
      this.filterLeave();
    }

    this.actionType = '';
  }
  approve(id: number) {
    this.isconfirm = true;
    this.confirmMessage = "Bạn có chắc muốn duyệt đơn này ?";
    this.id = id;
    this.actionType = 'approve';
  }

  reject(id: number) {
    this.isconfirm = true;
    this.confirmMessage = "Bạn có chắc muốn từ chối đơn này ?";
    this.id = id;
    this.actionType = 'reject';
  }


}
