import { Component, inject, CUSTOM_ELEMENTS_SCHEMA, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { calculatePayroll, RulesPayroll } from '../../../../../services/pages/features/hr/payroll/main.services';

// Interface cho cấu trúc dữ liệu
interface DebugRecord {
  code: string;
  name?: string;
  value: number;
  formula_dsl?: any;
  input_desc?: string;
  input_sql?: string;
  safeHtmlDisplay?: SafeHtml;
  cssClass?: string;
  codeStyle?: string;
  typeBadge?: SafeHtml;
}

@Component({
  selector: 'app-payrollmain',
  standalone: true,
  imports: [CommonModule, FormsModule], // Ensure shared components are imported if needed
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: './payrollmain.html',
  styleUrls: ['./payrollmain.scss'],
})
export class Payrollmain {
  private sanitizer = inject(DomSanitizer);
  constructor(private cdr: ChangeDetectorRef) { }

  // App State
  usernameDisplay = 'Admin';
  month = 11;
  year = 2025;
  empId = 1;

  // Data State
  runStatus: SafeHtml = '';
  debugData: DebugRecord[] = [];

  // --- COMPONENT STATES (Loading, Confirm, Alert) ---
  isloading = false;

  // Confirm Dialog State
  isconfirm = false;
  confirmMessage = '';

  // Alert Dialog State
  isalert = false;
  alertmessage = '';
  alertType = false;

  // --- NEW: FORMULA MODAL STATE ---
  isFormulaModalOpen = false;
  selectedRow: DebugRecord | null = null;

  // --- MODAL METHODS ---
  openFormulaModal(row: DebugRecord) {
    this.selectedRow = row;
    this.isFormulaModalOpen = true;
  }

  closeFormulaModal() {
    this.isFormulaModalOpen = false;
    this.selectedRow = null;
  }

  // --- HELPER METHODS FOR COMPONENTS ---
  showAlert(message: string, type: boolean) {
    this.alertmessage = message;
    this.alertType = type;
    this.isalert = true;
  }

  onConfirmResult(result: any) {
    this.isconfirm = false;
    if (result) {
      console.log('Confirmed!');
    }
  }

  // --- BUSINESS LOGIC ---
  async runCalculation() {
    this.isloading = true;
    this.runStatus = '';
    const m = String(this.month);
    const y = String(this.year);

    try {
      const response = await calculatePayroll(m, y);

      if (response && typeof response === 'string' && response.startsWith('Error')) {
        const errorMsg = response;
        this.runStatus = this.sanitizer.bypassSecurityTrustHtml(
          `<span class="text-red-500 font-bold"><i class="fa-solid fa-triangle-exclamation"></i> ${errorMsg}</span>`
        );
        this.showAlert(errorMsg, false);
      } else if (response) {
        this.runStatus = this.sanitizer.bypassSecurityTrustHtml(
          `<span class="text-emerald-600 font-bold"><i class="fa-solid fa-check-circle"></i> Thành công! ${response}</span>`
        );
        this.showAlert(`Tính lương thành công! ${response}`, true);
        await this.fetchDetails();
      } else {
        throw new Error("Không nhận được phản hồi từ server");
      }
    } catch (err: any) {
      const msg = err.message || err;
      this.runStatus = this.sanitizer.bypassSecurityTrustHtml(
        `<span class="text-red-500 font-bold">Lỗi kết nối: ${msg}</span>`
      );
      this.showAlert(`Lỗi kết nối: ${msg}`, false);
    } finally {
      this.isloading = false;
    }
  }

  async fetchDetails() {
    if (!this.empId) {
      this.showAlert("Vui lòng nhập ID nhân viên!", false);
      return;
    }

    this.isloading = true;
    this.debugData = [];
    const m = String(this.month);
    const y = String(this.year);

    try {
      const data = await RulesPayroll(this.empId, m, y);
      if (Array.isArray(data)) {
        this.debugData = data.map((row: DebugRecord) => this.processRow(row));
        this.cdr.detectChanges();
      } else {
        console.warn("Dữ liệu trả về không phải là mảng:", data);
        this.showAlert('Không tìm thấy dữ liệu cho nhân viên này', false);
      }
    } catch (err) {
      console.error(err);
      this.showAlert('Lỗi khi tải dữ liệu chi tiết', false);
    } finally {
      this.isloading = false;
    }
  }

  // --- HELPER: Process Row Data ---
  processRow(row: DebugRecord): DebugRecord {
    row.cssClass = "row-hover";

    if (row.code === 'NET_SALARY') {
      row.cssClass = "row-highlight-success";
      row.codeStyle = "code-salary";
    } else if (row.code === 'PIT_TAX') {
      row.cssClass = "row-highlight-danger";
      row.codeStyle = "code-tax";
    } else if (row.code === 'TOTAL_INCOME' || row.code === 'TAXABLE_INCOME') {
      row.codeStyle = "code-income";
    }

    let htmlContent = '';

    if (row.formula_dsl) {
      row.typeBadge = this.sanitizer.bypassSecurityTrustHtml(`<span class="badge badge-rule">Rule</span>`);
      const parsedDSL = this.parseDSL(row.formula_dsl);
      htmlContent = `${parsedDSL}`; // Direct DSL content
    } else {
      row.typeBadge = this.sanitizer.bypassSecurityTrustHtml(`<span class="badge badge-input">Input</span>`);
      const sqlTooltip = row.input_sql ?
        `<div class="sql-tooltip">${row.input_sql}</div>` : '';

      htmlContent = `
        <div style="display:flex; flex-direction:column; gap:8px;">
            <div style="font-weight:600; color:#334155;">
              <i class="fa-solid fa-database"></i> ${row.input_desc || 'Dữ liệu thô từ DB'}
            </div>
            ${sqlTooltip}
        </div>`;
    }

    row.safeHtmlDisplay = this.sanitizer.bypassSecurityTrustHtml(htmlContent);
    return row;
  }

  // --- CORE: PARSER JSON DSL ---
  parseDSL(jsonStr: any): string {
    if (!jsonStr) return 'null';
    try {
      let cleanJson = jsonStr;
      if (typeof jsonStr === 'string' && (jsonStr.startsWith('"') || jsonStr.includes('\\'))) {
        try { cleanJson = JSON.parse(jsonStr); } catch (e) { }
      }
      const node = typeof cleanJson === 'string' ? JSON.parse(cleanJson) : cleanJson;
      return this.recursiveParse(node);
    } catch (e) {
      return `<span style="color:red">Invalid JSON DSL</span>`;
    }
  }

  recursiveParse(node: any): string {
    if (!node) return '<span style="color:#cbd5e1">null</span>';
    const type = node.type;

    // 1. Math Ops
    if (['ADD', 'SUB', 'MUL', 'DIV'].includes(type)) {
      let op = '', opClass = '';
      if (type === 'ADD') { op = '+'; opClass = 'op-add'; }
      if (type === 'SUB') { op = '-'; opClass = 'op-sub'; }
      if (type === 'MUL') { op = '×'; opClass = 'op-mul'; }
      if (type === 'DIV') { op = '÷'; opClass = 'op-mul'; }

      return `<span style="font-family:monospace">(${this.recursiveParse(node.left)} <b class="math-op ${opClass}">${op}</b> ${this.recursiveParse(node.right)})</span>`;
    }

    // 2. Variables
    if (type === 'VARIABLE' || type === 'REFERENCE') {
      return `<span style="background:#eff6ff; color:#1d4ed8; padding:2px 4px; border-radius:4px; font-weight:bold; border:1px solid #dbeafe; font-size:0.8em;">${node.name}</span>`;
    }

    // 3. Constants
    if (type === 'CONSTANT') {
      let valDisplay = node.value;
      if (typeof node.value === 'number') valDisplay = new Intl.NumberFormat('en-US').format(node.value);
      return `<span style="color:#059669; font-weight:bold; font-family:monospace;">${valDisplay}</span>`;
    }

    // 4. Logic (IF/ELSE)
    if (type === 'IF_ELSE') {
      return `
        <div class="logic-tree">
            <div class="logic-branch">
                <span class="logic-label label-if">IF</span> ${this.recursiveParse(node.condition)}
            </div>
            <div class="logic-branch" style="padding-left:16px; border-left:1px solid #86efac;">
                <span class="logic-label label-then">THEN</span> ${this.recursiveParse(node.true_case)}
            </div>
            <div class="logic-branch" style="padding-left:16px; border-left:1px solid #fca5a5;">
                <span class="logic-label label-else">ELSE</span> ${this.recursiveParse(node.false_case)}
            </div>
        </div>
      `;
    }

    // 5. Comparisons
    if (['GT', 'LT', 'GTE', 'LTE', 'EQ'].includes(type)) {
      let op = type;
      if (type === 'GT') op = '>';
      if (type === 'LT') op = '<';
      if (type === 'GTE') op = '≥';
      if (type === 'LTE') op = '≤';
      if (type === 'EQ') op = '=';
      return `<span style="background:#fff7ed; padding:2px; border:1px solid #ffedd5; border-radius:4px;">${this.recursiveParse(node.left)} <b style="color:#ea580c; margin:0 4px;">${op}</b> ${this.recursiveParse(node.right)}</span>`;
    }

    return `<span style="color:gray">Unknown(${type})</span>`;
  }
}