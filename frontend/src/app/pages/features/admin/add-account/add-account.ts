import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { addAccount } from '../../../../services/pages/features/admin/addAccount.service';
import { Department } from '../../../../interface/user/user.interface';
import { Loading } from '../../../shared/loading/loading';
import { Alert } from '../../../shared/alert/alert';
import { Comfirm } from '../../../shared/comfirm/comfirm';

@Component({
  selector: 'app-add-account',
  imports: [CommonModule, FormsModule, Loading, Alert, Comfirm],
  templateUrl: './add-account.html',
  styleUrl: './add-account.scss',
})
export class AddAccount {
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
  constructor(private router: Router, private cdr: ChangeDetectorRef) { }
  account = {
    fullname: '',
    cccd: BigInt(0),
    role: 0,
    department: 0
  };
  department = Department;
  saveAccount() {
    this.isconfirm = true;
    this.confirmMessage = "Bạn có chắc muốn thêm thông tin nhân viên này?";

  }
  async onConfirmResult(event: any) {
    if (event == true) {

      if (this.account.fullname == '' || this.account.cccd.toString().length < 10 || this.account.role.toString() == "" || this.account.department.toString() == "")
        this.showNotification("vui long dien du thong tin", false);
      this.isloading = true;
      const res = await addAccount(this.account) as { data: string, status: number };
      if (res.status == 201) {
        this.isconfirm = false;
        this.isloading = false;
        this.showNotification(res.data, true);
        this.account = {
          fullname: '',
          cccd: BigInt(0),
          role: 0,
          department: 0
        };
        this.cdr.detectChanges();
        return;
      }
      this.isconfirm = false;
      this.isloading = false;
      this.showNotification("them that bai !", false);
    }
  }

  closeForm() {
    this.router.navigate(["/home/info"]);
  }

}
