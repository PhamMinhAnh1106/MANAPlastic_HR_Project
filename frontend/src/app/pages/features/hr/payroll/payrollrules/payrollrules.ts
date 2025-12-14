import { ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, ViewChild, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { auditVariable, deleteRule, deleteVariable, getAuditUsers, getRules, getVariables, saveRule, saveVariable } from '../../../../../services/pages/features/hr/payroll/rules.services';

// *************************************************************
// CHÚ Ý: ĐÃ LOẠI BỎ MỌI IMPORT API HOẶC MOCK DATA TẠI ĐÂY
// Bạn cần tự import các service/function API thực tế của mình.
// *************************************************************

// Giả định thư viện CodeMirror có sẵn (cần được import global hoặc qua script tag)
declare var CodeMirror: any;

interface Rule {
  ruleId?: number;
  ruleCode: string;
  name: string;
  status: string;
  dslJson: any;
}

interface Variable {
  variableId: number | undefined;
  id?: number;
  code: string;
  name: string;
  description?: string;
  sqlQuery?: string;
  sQLQuery?: string;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './payrollrules.html',
  styleUrls: ['./payrollrules.scss'],
})
export class Payrollrules implements OnInit, AfterViewInit {

  // --- STATE ---
  activeTab: 'rules' | 'variables' = 'rules';
  uiMode: 'formula' | 'visual' | 'json' = 'formula';
  varMode: 'wizard' | 'sql' = 'wizard';

  // Data Lists - KHÔNG CÓ MOCK DATA
  rules: Rule[] = [];
  variables: Variable[] = [];
  employees: any[] = []; // Cần fetch từ API

  // Current Selections
  currentRule: Rule = { ruleCode: 'NEW_RULE', name: '', status: 'DRAFT', dslJson: { type: 'CONSTANT', value: 0 } };
  currentVar: Variable = {
    code: '', name: '', sqlQuery: '',
    variableId: undefined
  };

  // Rule Editor Inputs
  formulaInput: string = '';
  jsonInput: string = '{}';

  // Wizard Inputs
  wizSource: string = '';
  wizResultSql: string = '';

  // Simulator Inputs
  simEmployeeId: string = '';
  simPeriod: string = new Date().toISOString().substring(0, 7); // Default to current month/year
  simResult: string = '---';
  simDebug: string = '';
  isSimulating = false;

  // CodeMirror Instance
  sqlEditorInstance: any;
  @ViewChild('sqlTextarea') sqlTextarea!: ElementRef;

  // --- DEFINITIONS ---
  RULE_TYPES: any = {
    'ADD': { label: '➕ Cộng (Add)', group: 'math', args: ['left', 'right'], op: '+' },
    'SUB': { label: '➖ Trừ (Sub)', group: 'math', args: ['left', 'right'], op: '-' },
    'MUL': { label: '✖ Nhân (Mul)', group: 'math', args: ['left', 'right'], op: '*' },
    'DIV': { label: '➗ Chia (Div)', group: 'math', args: ['left', 'right'], op: '/' },
    'GT': { label: '> Lớn hơn', group: 'comp', args: ['left', 'right'], op: '>' },
    'LT': { label: '< Nhỏ hơn', group: 'comp', args: ['left', 'right'], op: '<' },
    'GTE': { label: '>= Lớn hơn bằng', group: 'comp', args: ['left', 'right'], op: '>=' },
    'LTE': { label: '<= Nhỏ hơn bằng', group: 'comp', args: ['left', 'right'], op: '<=' },
    'IF_ELSE': { label: '❓ Nếu...Thì... (IF)', group: 'logic', args: ['condition', 'true_case', 'false_case'] },
    'VARIABLE': { label: '📦 Biến số (Data)', group: 'data', args: [] },
    'CONSTANT': { label: '#️⃣ Số cố định', group: 'const', args: [] }
  };

  objectKeys = Object.keys;

  constructor(
    private cdr: ChangeDetectorRef,
    private sanitizer: DomSanitizer,
  ) { }

  ngOnInit() {
    const savedTab = sessionStorage.getItem('payroll_active_tab');
    if (savedTab === 'rules' || savedTab === 'variables') {
      this.activeTab = savedTab;
    }
    // TODO: Bỏ gọi mock, thay bằng gọi API thực tế
    this.loadRules();
    this.loadVariables();
    this.loadEmployees();
    this.resetRuleEditor();
  }

  ngAfterViewInit() {
    setTimeout(() => {
      if (this.sqlTextarea && typeof CodeMirror !== 'undefined') {
        this.sqlEditorInstance = CodeMirror.fromTextArea(this.sqlTextarea.nativeElement, {
          mode: 'text/x-sql',
          theme: 'default',
          lineNumbers: true,
          matchBrackets: true
        });

        this.sqlEditorInstance.on('change', (cm: any) => {
          const value = cm.getValue();
          this.currentVar.sqlQuery = value;
          // Đồng bộ hóa CodeMirror value với wizResultSql khi người dùng gõ
          this.wizResultSql = value;
          this.cdr.detectChanges();
        });

        if (this.currentVar.sqlQuery) {
          this.sqlEditorInstance.setValue(this.currentVar.sqlQuery);
        }
      }
    }, 100);
  }

  // --- LOGIC CHUYỂN AST (RAW) SANG TEXT FORMULA ---
  convertAstToString(node: any): string {
    if (!node) return '';

    if (node.type === 'VARIABLE') {
      return node.name || 'UNKNOWN_VAR';
    }

    if (node.type === 'CONSTANT') {
      return node.value !== undefined ? String(node.value) : '0';
    }

    const typeDef = this.RULE_TYPES[node.type];
    if (typeDef && typeDef.op) {
      const left = this.convertAstToString(node.left);
      const right = this.convertAstToString(node.right);
      return `(${left} ${typeDef.op} ${right})`;
    }

    if (node.type === 'IF_ELSE') {
      const cond = this.convertAstToString(node.condition);
      const trueCase = this.convertAstToString(node.true_case);
      const falseCase = this.convertAstToString(node.false_case);
      return `IF (${cond}) THEN { ${trueCase} } ELSE { ${falseCase} }`;
    }

    if (node.type === 'RAW_FORMULA') {
      return node.expression || '';
    }

    return '';
  }

  // --- HIGHLIGHT FORMULA LOGIC ---
  get formattedFormula(): SafeHtml {
    if (!this.formulaInput) return '';
    return this.highlightFormula(this.formulaInput);
  }

  highlightFormula(text: string): SafeHtml {
    if (!text) return '';
    const tokens = text.split(/([+\-*/()<>!=&|{}\s]+)/);
    let html = '';
    const knownVars = new Set(this.variables.map(v => v.code));

    tokens.forEach(token => {
      const trimmed = token.trim();
      if (!trimmed) {
        html += token;
      } else if (knownVars.has(trimmed)) {
        html += `<span class="badge-var">${token}</span>`;
      } else if (!isNaN(Number(trimmed))) {
        html += `<span class="token-number">${token}</span>`;
      } else if (['IF', 'THEN', 'ELSE'].includes(trimmed.toUpperCase())) {
        html += `<span class="token-keyword">${token}</span>`;
      } else if (['+', '-', '*', '/', '>', '<', '=', '&', '|'].includes(trimmed)) {
        html += `<span class="token-op">${token}</span>`;
      } else if (['(', ')', '{', '}'].includes(trimmed)) {
        html += `<span class="token-bracket">${token}</span>`;
      } else {
        html += `<span class="token-unknown">${token}</span>`;
      }
    });

    return this.sanitizer.bypassSecurityTrustHtml(html);
  }

  // ================= TABS & MODES =================
  switchTab(tab: 'rules' | 'variables') {
    this.activeTab = tab;
    sessionStorage.setItem('payroll_active_tab', tab);

    if (tab === 'variables' && this.varMode === 'sql' && this.sqlEditorInstance) {
      setTimeout(() => this.sqlEditorInstance.refresh(), 100);
    }
    this.cdr.detectChanges();
  }

  setRuleMode(mode: 'formula' | 'visual' | 'json') {
    if (this.uiMode === 'json' && mode === 'visual') {
      try {
        this.currentRule.dslJson = JSON.parse(this.jsonInput);
      } catch (e) {
        // Thay alert bằng console.error theo hướng dẫn
        console.error('JSON lỗi, không thể chuyển sang Visual Mode');
        return;
      }
    } else if (mode === 'json') {
      this.jsonInput = JSON.stringify(this.currentRule.dslJson, null, 2);
    }
    this.uiMode = mode;
    this.cdr.detectChanges();
  }

  setVarMode(mode: 'wizard' | 'sql') {
    this.varMode = mode;
    if (mode === 'sql' && this.sqlEditorInstance) {
      this.sqlEditorInstance.setValue(this.currentVar.sqlQuery || '');
      setTimeout(() => this.sqlEditorInstance.refresh(), 100);
    }
    this.cdr.detectChanges();
  }

  // ================= RULE LOGIC =================
  async loadRules() {
    // TODO: Thay bằng gọi API thực tế
    const data = await getRules();
    if (data && Array.isArray(data)) {
      this.rules = data.filter((r: Rule) => r.status !== 'RETIRED');
      this.cdr.detectChanges();
    }
  }

  resetRuleEditor() {
    this.currentRule = {
      ruleId: undefined,
      ruleCode: 'NEW_RULE',
      name: '',
      status: 'DRAFT',
      dslJson: { type: 'CONSTANT', value: 0 }
    };
    this.formulaInput = '';
    this.jsonInput = JSON.stringify(this.currentRule.dslJson, null, 2);
    this.setRuleMode('formula');
    this.cdr.detectChanges();
  }

  selectRule(rule: Rule) {
    this.currentRule = { ...rule };
    this.formulaInput = '';

    if (typeof this.currentRule.dslJson === 'string') {
      try {
        this.currentRule.dslJson = JSON.parse(this.currentRule.dslJson);
      } catch (e) {
        this.currentRule.dslJson = {};
      }
    }

    if (this.currentRule.dslJson?.type === 'RAW_FORMULA') {
      this.formulaInput = this.currentRule.dslJson.expression;
    } else {
      this.formulaInput = this.convertAstToString(this.currentRule.dslJson);
    }

    this.setRuleMode('formula');

    this.jsonInput = JSON.stringify(this.currentRule.dslJson, null, 2);
    this.cdr.detectChanges();
  }

  updateNodeType(node: any, newType: string) {
    node.type = newType;
    const typeDef = this.RULE_TYPES[newType];
    if (typeDef && typeDef.args) {
      typeDef.args.forEach((arg: string) => {
        if (!node[arg]) node[arg] = { type: 'CONSTANT', value: 0 };
      });
    }
    if (newType === 'VARIABLE' && !node.name) node.name = '';
    if (newType === 'CONSTANT' && node.value === undefined) node.value = 0;
    this.cdr.detectChanges();
  }

  insertOperator(op: string) {
    this.formulaInput += ` ${op} `;
  }

  insertVariable(code: string) {
    if (this.activeTab === 'rules' && this.uiMode === 'formula') {
      this.formulaInput += code;
    } else {
      const tempInput = document.createElement('textarea');
      tempInput.value = code;
      document.body.appendChild(tempInput);
      tempInput.select();
      document.execCommand('copy');
      document.body.removeChild(tempInput);
      console.log(`Đã copy mã biến: ${code}`);
    }
  }

  async saveRule() {
    if (this.uiMode === 'formula') {
      this.currentRule.dslJson = { type: 'RAW_FORMULA', expression: this.formulaInput };
    } else if (this.uiMode === 'json') {
      try {
        this.currentRule.dslJson = JSON.parse(this.jsonInput);
      } catch (e: any) { console.error('JSON Invalid:', e); return; }
    }

    if (!this.currentRule.ruleCode || !this.currentRule.name) {
      console.error('Mã Rule và Tên Rule không được để trống.');
      return;
    }

    const payload = {
      ruleId: this.currentRule.ruleId,
      code: this.currentRule.ruleCode.toUpperCase().trim(),
      ruleCode: this.currentRule.ruleCode.toUpperCase().trim(),
      name: this.currentRule.name,
      dsl: JSON.stringify(this.currentRule.dslJson)
    };

    // TODO: Thay bằng gọi API thực tế
    try {
      await saveRule(payload);
      console.log('Đã lưu Rule thành công!');
      await this.loadRules();
    } catch (err: any) {
      console.error('Lỗi khi lưu Rule:', err);
    }
  }

  async deleteRule() {
    if (!this.currentRule.ruleId) return;
    if (window.confirm('Bạn có chắc chắn muốn xóa rule này?')) {
      // TODO: Thay bằng gọi API thực tế
      try {
        await deleteRule(this.currentRule.ruleId);
        console.log('Đã xóa rule!');
        await this.loadRules();
        this.resetRuleEditor();
      } catch (err: any) {
        console.error('Lỗi khi xóa:', err);
      }
    }
  }

  // ================= VARIABLE LOGIC =================
  async loadVariables() {
    // TODO: Thay bằng gọi API thực tế
    const data = await getVariables();
    if (data && Array.isArray(data)) {
      this.variables = data;
      this.cdr.detectChanges();
    }
  }

  resetVarEditor() {
    this.currentVar = { code: '', name: '', sqlQuery: '', variableId: undefined, description: '' };
    this.wizSource = '';
    this.wizResultSql = '';
    if (this.sqlEditorInstance) this.sqlEditorInstance.setValue('');
    this.setVarMode('wizard');
    this.cdr.detectChanges();
  }

  selectVar(v: Variable) {
    const query = v.sqlQuery || v.sQLQuery || '';

    this.currentVar = {
      id: v.id || v.variableId,
      variableId: v.id || v.variableId,
      code: v.code,
      name: v.name,
      description: v.description,
      sqlQuery: query
    };

    this.wizSource = v.code;
    // Đồng bộ wizResultSql với câu lệnh SQL của biến vừa chọn
    this.wizResultSql = query;

    if (this.sqlEditorInstance) {
      this.sqlEditorInstance.setValue(query);
      setTimeout(() => this.sqlEditorInstance.refresh(), 50);
    }
    this.setVarMode('wizard');
    this.cdr.detectChanges();
  }

  onWizSourceChange() {
    const selectedVar = this.variables.find(v => v.code === this.wizSource);

    if (selectedVar) {
      const sql = selectedVar.sqlQuery || selectedVar.sQLQuery || '';

      // Đồng bộ wizResultSql với câu lệnh SQL của biến vừa chọn trong Wizard
      this.wizResultSql = sql;
      this.currentVar.sqlQuery = sql;
      this.currentVar.description = selectedVar.description;

      if (this.sqlEditorInstance) {
        this.sqlEditorInstance.setValue(sql);
      }
    } else {
      this.wizResultSql = '';
      this.currentVar.sqlQuery = '';
    }
    this.cdr.detectChanges();
  }

  async saveVariable() {
    if (!this.currentVar.code || !this.currentVar.name || !this.currentVar.sqlQuery) {
      console.error('Vui lòng điền đủ Mã, Tên và Câu lệnh SQL.');
      return;
    }

    const payload = {
      id: this.currentVar.id || this.currentVar.variableId || null,
      code: this.currentVar.code.toUpperCase().trim(),
      name: this.currentVar.name,
      description: this.currentVar.description,
      sqlQuery: this.currentVar.sqlQuery
    };

    // TODO: Thay bằng gọi API thực tế
    try {
      await saveVariable(payload);
      console.log('Đã lưu Biến số!');
      await this.loadVariables();
    } catch (err: any) {
      console.error('Lỗi lưu biến:', err);
    }
  }

  async deleteVariable() {
    const id = this.currentVar.id || this.currentVar.variableId;
    if (!id) return;
    if (window.confirm("Xóa biến này?")) {
      // TODO: Thay bằng gọi API thực tế
      try {
        await deleteVariable(id);
        await this.loadVariables();
        this.resetVarEditor();
      } catch (err: any) {
        console.error("Lỗi xóa biến:", err);
      }
    }
  }

  // ================= SIMULATOR =================
  async loadEmployees() {
    // TODO: Thay bằng gọi API thực tế
    const data = await getAuditUsers();
    if (data && Array.isArray(data)) {
      this.employees = data;
      this.cdr.detectChanges();
    }
  }

  async testVariable() {
    if (!this.currentVar.sqlQuery) { console.error('Chưa có SQL!'); return; }
    if (!this.simEmployeeId) { console.error('Chọn nhân viên!'); return; }
    if (!this.simPeriod) { console.error('Chọn kỳ lương!'); return; }

    this.isSimulating = true;
    this.simResult = 'Đang tính toán...';
    this.simDebug = `Executing SQL for User ID: ${this.simEmployeeId}...`;
    this.cdr.detectChanges();

    const [year, month] = this.simPeriod.split('-');

    const payload = {
      sql: this.currentVar.sqlQuery,
      userId: parseInt(this.simEmployeeId),
      month: parseInt(month),
      year: parseInt(year)
    };

    // TODO: Thay bằng gọi API thực tế
    try {
      const data = await auditVariable(payload);
      this.isSimulating = false;

      const val = data.result;
      if (val !== null && !isNaN(parseFloat(val))) {
        this.simResult = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
      } else {
        this.simResult = String(val);
      }
      this.simDebug = `Context: ${data.auditContext || 'OK'}`;

    } catch (err: any) {
      this.isSimulating = false;
      this.simResult = 'LỖI SQL';
      this.simDebug = err?.response?.data || err.message || 'Lỗi không xác định.';
    }
    this.cdr.detectChanges();
  }

  getTypeGroup(type: string): string {
    return this.RULE_TYPES[type]?.group || 'const';
  }
}