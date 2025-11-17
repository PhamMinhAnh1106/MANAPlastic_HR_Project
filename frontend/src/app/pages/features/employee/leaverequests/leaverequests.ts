import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { leaverequestBalance, leaverequests } from '../../../../interface/leaverequest.interface';
import { Deleteleaverequest, getBalanceleaverequest, getleaverequest } from '../../../../services/pages/features/employee/leaverequest.services';
import { NgClass, NgFor } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-leaverequests',
  imports: [NgFor, NgClass],
  templateUrl: './leaverequests.html',
  styleUrl: './leaverequests.scss',
})
export class Leaverequests implements OnInit {
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
  async deleteRequest(id: number) {
    if (!confirm("Bạn có chắc muốn xóa đơn nghỉ này?")) return;
    const res = await Deleteleaverequest(id) as { status: number, data: string };
    if (res.status == 200) {
      alert(res.data);
      this.leaverequest = await this.getLeaverequest();
      this.cdr.detectChanges();
      return;
    }
    alert(res.data);
  }
  async ngOnInit() {
    this.leaverequest = await this.getLeaverequest();
    this.leaverequestBl = await this.getLeaverequestbalance();
    this.cdr.detectChanges();

  }
}
