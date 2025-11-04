import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { addAccount } from '../../../../services/pages/features/admin/addAccount.service';

@Component({
  selector: 'app-add-account',
  imports: [CommonModule, FormsModule],
  templateUrl: './add-account.html',
  styleUrl: './add-account.scss',
})
export class AddAccount {
  constructor(private router: Router, private cdr: ChangeDetectorRef) { }
  account = {
    fullname: '',
    cccd: BigInt(0),
    role: 0
  };

  async saveAccount() {
    if (this.account.fullname == '' || this.account.cccd.toString().length < 10 || this.account.role.toString() == "")
      alert("vui long dien du thong tin");
    const res = await addAccount(this.account) as { data: string, status: number };
    if (res.status == 201) {
      alert(res.data);
      this.account = {
        fullname: '',
        cccd: BigInt(0),
        role: 0
      };
      this.cdr.detectChanges();
      return;
    }
    alert("them that bai !");

  }

  closeForm() {
    this.router.navigate(["/home/info"]);
  }

}
