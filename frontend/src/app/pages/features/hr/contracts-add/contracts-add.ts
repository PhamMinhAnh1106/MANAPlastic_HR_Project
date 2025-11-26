import { ChangeDetectorRef, Component } from '@angular/core';
import { contracts, statusContract } from '../../../../interface/contract.interface';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { NgFor, NgIf } from '@angular/common';
import { AddNewContract } from '../../../../services/pages/features/hr/contracts.service';
import { Loading } from '../../../shared/loading/loading';
import { Alert } from '../../../shared/alert/alert';
import { Comfirm } from '../../../shared/comfirm/comfirm';

@Component({
  selector: 'app-contracts-add',
  imports: [FormsModule, NgFor, NgIf, Loading, Alert, Comfirm],
  templateUrl: './contracts-add.html',
  styleUrl: './contracts-add.scss',
})
export class ContractsAdd {
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
  contractType = statusContract;
  contract: contracts = {
    userId: 0,
    contractName: "",
    type: "",
    baseSalary: "",
    insuranceSalary: "",
    allowanceToxicType: "",
    signDate: "",
    startDate: "",
    endDate: "",
    file: ""
  };
  isOpen = true;

  selectedFile: File | null = null;

  handleFileSelect(event: any) {
    this.selectedFile = event.target.files[0] || null;
  }

  async submit() {
    this.isconfirm = true;
    this.confirmMessage = "Bạn có chắc muốn thêm hợp đồng này?";

  }
  async onConfirmResult(event: any) {
    if (event == true) {
      this.isconfirm = false;
      if (!this.selectedFile) {
        this.showNotification("Vui lòng chọn file hợp đồng", false);
        return;
      }
      const formData = new FormData();
      formData.append("userId", this.contract.userId.toString());
      formData.append("contractName", this.contract.contractName);
      formData.append("type", this.contract.type);
      formData.append("baseSalary", this.contract.baseSalary);
      formData.append("insuranceSalary", this.contract.insuranceSalary);
      formData.append("allowanceToxicType", this.contract.allowanceToxicType);
      formData.append("signDate", this.contract.signDate);
      formData.append("startDate", this.contract.startDate);
      formData.append("endDate", this.contract.endDate);
      formData.append("file", this.selectedFile);
      const res = await AddNewContract(formData) as { data: string, status: number };
      this.isloading = true;

      if (res.status == 200) {
        this.isloading = false;
        this.showNotification(res.data, true);
        this.closePopup();

        setTimeout(() => this.cdr.detectChanges(), 1000);
        return;
      }
      this.isloading = false;
      this.showNotification(res.data, false);
      this.closePopup();
      setTimeout(() => this.cdr.detectChanges(), 1000);

    } else {
      this.isconfirm = false;

    }
  }
  closePopup() {
    this.router.navigate(["home/contracts"]);
  }
}
