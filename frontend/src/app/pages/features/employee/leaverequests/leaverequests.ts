import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { leaverequestBalance, leaverequests } from '../../../../interface/leaverequest.interface';
import { Deleteleaverequest, getBalanceleaverequest, getleaverequest } from '../../../../services/pages/features/employee/leaverequest.services';
import { NgClass, NgFor, NgIf } from '@angular/common';
import { Router } from '@angular/router';
import { Alert } from '../../../shared/alert/alert';
import { Comfirm } from '../../../shared/comfirm/comfirm';

@Component({
  selector: 'app-leaverequests',
  imports: [NgFor, NgClass, NgIf, Alert, Comfirm],
  templateUrl: './leaverequests.html',
  styleUrl: './leaverequests.scss',
})
export class Leaverequests implements OnInit {
  ////////
  isloading: boolean = false;
  isconfirm: boolean = false;
  confirmMessage = "";
  isalert: boolean = false;
  notifyMessage = "";
  notifyType: boolean = true;
  showNotification(message: string, type: boolean) {
    this.notifyMessage = message;
    this.notifyType = type;
    this.isalert = true;
  }


  ////////////////////////////
  id = 0;
  leaverequest: leaverequests[] = []
  leaverequestBl: leaverequestBalance[] = []
  constructor(private cdr: ChangeDetectorRef, private router: Router) { }
  async getLeaverequest() {
    const res = await getleaverequest();
    return res;
  }
  add() {
    this.router.navigate(["/home/leaverequest/add"])
  }
  async getLeaverequestbalance() {
    const res = await getBalanceleaverequest();
    return res;
  }
  async onConfirmResult(event: any) {
    if (event == true) {
      this.isconfirm = false;
      const res = await Deleteleaverequest(this.id) as { status: number, data: string };
      if (res.status == 200) {
        this.showNotification(res.data, true);
        this.leaverequest = await this.getLeaverequest();
        this.cdr.detectChanges();
        return;
      }
      this.showNotification("ko the xoa", false);
    } else {
      this.isconfirm = false;
    }
  }
  deleteRequest(id: number) {
    this.id = id;
    this.isconfirm = true;
    this.confirmMessage = "Bạn có chắc muốn xóa đơn nghỉ này?";

  }
  async ngOnInit() {
    this.leaverequest = await this.getLeaverequest();
    this.leaverequestBl = await this.getLeaverequestbalance();
    this.cdr.detectChanges();

  }
}
