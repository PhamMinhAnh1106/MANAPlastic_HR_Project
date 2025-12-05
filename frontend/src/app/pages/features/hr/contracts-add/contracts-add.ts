import { ChangeDetectorRef, Component } from '@angular/core';
import { contracts, statusContract, TypeContract } from '../../../../interface/contract.interface';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { NgFor, NgIf, NgClass } from '@angular/common'; // Thêm NgClass
import { AddNewContract } from '../../../../services/pages/features/hr/contracts.service';
import { Loading } from '../../../shared/loading/loading';
import { Alert } from '../../../shared/alert/alert';
import { Comfirm } from '../../../shared/comfirm/comfirm';

@Component({
  selector: 'app-contracts-add',
  standalone: true,
  imports: [FormsModule, NgFor, NgIf, Loading, Alert, Comfirm],
  templateUrl: './contracts-add.html',
  styleUrl: './contracts-add.scss',
})
export class ContractsAdd {
  // States
  isloading: boolean = false;
  isconfirm: boolean = false;
  confirmMessage = "";
  isalert: boolean = false;
  notifyMessage = "";
  notifyType: boolean = true;
  isOpen = true; // Control modal visibility

  contractType = TypeContract;
  contract: contracts = {
    userId: 0,
    userName: "",
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

  selectedFile: File | null = null;

  constructor(private router: Router, private cdr: ChangeDetectorRef) { }

  showNotification(message: string, type: boolean) {
    this.notifyMessage = message;
    this.notifyType = type;
    this.isalert = true;
  }

  getVietnameseContractType(type: string): string {
    switch (type) {
      case 'INDEFINITE': return 'Hợp đồng Không thời hạn';
      case 'FIXED_TERM': return 'Hợp đồng có thời hạn';
      case 'PROBATION': return 'Hợp đồng Thử việc';
      default: return 'Không xác định';
    }
  }

  handleFileSelect(event: any) {
    this.selectedFile = event.target.files[0] || null;
  }

  async submit() {
    this.isconfirm = true;
    this.confirmMessage = "Bạn có chắc muốn thêm hợp đồng này?";
  }

  async onConfirmResult(event: any) {
    this.isconfirm = false;

    if (event == true) {
      if (!this.selectedFile) {
        this.showNotification("Vui lòng chọn file hợp đồng", false);
        return;
      }

      this.isloading = true;
      const formData = new FormData();
      formData.append("userName", this.contract.userName);
      formData.append("contractName", this.contract.contractName);
      formData.append("type", this.contract.type);
      formData.append("baseSalary", this.contract.baseSalary);
      formData.append("insuranceSalary", this.contract.insuranceSalary);
      formData.append("allowanceToxicType", this.contract.allowanceToxicType);
      formData.append("signDate", this.contract.signDate);
      formData.append("startDate", this.contract.startDate);
      formData.append("endDate", this.contract.endDate);
      formData.append("file", this.selectedFile);

      try {
        const res = await AddNewContract(formData) as { data: string, status: number };

        if (res.status == 200) {
          this.showNotification(res.data, true);
          setTimeout(() => {
            this.closePopup();
          }, 1000);
        } else {
          this.showNotification(res.data, false);
        }
      } catch (error) {
        this.showNotification("Có lỗi xảy ra khi thêm hợp đồng", false);
      } finally {
        this.isloading = false;
        this.cdr.detectChanges();
      }
    }
  }

  closePopup() {
    this.router.navigate(["home/contracts"]);
  }
}