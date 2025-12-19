import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { chukyluong } from '../chukyluong/chukyluong';
import { LeavePolicies } from '../leave-policies/leave-policies';
import { Overtime } from '../overtime/overtime';
import { Tax } from '../tax/tax';

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [
    CommonModule,
    chukyluong,
    LeavePolicies,
    Overtime,
    Tax
  ],
  templateUrl: './main.html',
  styleUrl: './main.scss',
})
export class Main {

  // 1. Kiểm tra trong sessionStorage xem có tab nào đã lưu chưa
  // Nếu có thì dùng tab đó, nếu không thì mặc định là 'chukyluong'
  currentTab: string = sessionStorage.getItem('activeLegalTab') || 'chukyluong';

  // Danh sách Tabs cấu hình
  tabs = [
    { id: 'chukyluong', label: 'Chu kỳ lương' },
    { id: 'leave-policies', label: 'Chính sách nghỉ phép' },
    { id: 'overtime', label: 'Làm thêm giờ (OT)' },
    { id: 'tax', label: 'Thuế thu nhập' }
  ];

  selectTab(tabId: string): void {
    this.currentTab = tabId;
    // 2. Khi người dùng bấm chuyển tab, lưu ngay ID của tab đó vào sessionStorage
    sessionStorage.setItem('activeLegalTab', tabId);
  }

  getActiveTabLabel(): string {
    const active = this.tabs.find(t => t.id === this.currentTab);
    return active ? active.label : '';
  }
}