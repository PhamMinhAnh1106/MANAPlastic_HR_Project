import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, ElementRef, ViewChild, OnInit, AfterViewInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { auditVariable, deleteRule, deleteVariable, getAuditUsers, getRules, getVariables, saveRule, saveVariable } from '../../../../../services/pages/features/hr/payroll/rules.services';

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
  selector: 'app-payrollrules',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payrollrules.html',
  styleUrl: './payrollrules.scss',
})
export class Payrollrules implements OnInit, AfterViewInit {

  // --- STATE ---
  activeTab: 'rules' | 'variables' = 'rules';
  uiMode: 'formula' | 'visual' | 'json' = 'formula';
  varMode: 'wizard' | 'sql' = 'wizard';

  // Data Lists
  rules: Rule[] = [];
  variables: Variable[] = [];
  employees: any[] = [];

  // Current Selections
  currentRule: Rule = { ruleCode: 'NEW_RULE', name: '', status: 'DRAFT', dslJson: {} };
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
  simPeriod: string = '2025-11';
  simResult: string = '---';
  simDebug: string = '';
  isSimulating = false;

  // CodeMirror Instance
  sqlEditorInstance: any;
  @ViewChild('sqlTextarea') sqlTextarea!: ElementRef;

  // --- DEFINITIONS ---
  RULE_TYPES: any = {
    'ADD': { label: '➕ Cộng (Add)', group: 'math', args: ['left', 'right'] },
    'SUB': { label: '➖ Trừ (Sub)', group: 'math', args: ['left', 'right'] },
    'MUL': { label: '✖ Nhân (Mul)', group: 'math', args: ['left', 'right'] },
    'DIV': { label: '➗ Chia (Div)', group: 'math', args: ['left', 'right'] },
    'GT': { label: '> Lớn hơn', group: 'comp', args: ['left', 'right'] },
    'LT': { label: '< Nhỏ hơn', group: 'comp', args: ['left', 'right'] },
    'GTE': { label: '>= Lớn hơn bằng', group: 'comp', args: ['left', 'right'] },
    'LTE': { label: '<= Nhỏ hơn bằng', group: 'comp', args: ['left', 'right'] },
    'IF_ELSE': { label: '❓ Nếu...Thì... (IF)', group: 'logic', args: ['condition', 'true_case', 'false_case'] },
    'VARIABLE': { label: '📦 Biến số (Data)', group: 'data', args: [] },
    'CONSTANT': { label: '#️⃣ Số cố định', group: 'const', args: [] }
  };

  objectKeys = Object.keys;

  constructor(
    private cdr: ChangeDetectorRef,
    private sanitizer: DomSanitizer
  ) { }

  ngOnInit() {
    this.loadRules();
    this.loadVariables();
    this.loadEmployees();
    this.resetRuleEditor();
  }

  ngAfterViewInit() {
    if (this.sqlTextarea) {
      this.sqlEditorInstance = CodeMirror.fromTextArea(this.sqlTextarea.nativeElement, {
        mode: 'text/x-sql',
        theme: 'default',
        lineNumbers: true,
        matchBrackets: true
      });

      this.sqlEditorInstance.on('change', (cm: any) => {
        this.currentVar.sqlQuery = cm.getValue();
      });
    }
  }

  // --- HIGHLIGHT FORMULA LOGIC ---
  get formattedFormula(): SafeHtml {
    if (!this.formulaInput) return '';
    return this.highlightFormula(this.formulaInput);
  }

  highlightFormula(text: string): SafeHtml {
    if (!text) return '';
    // Tách token: Toán tử, Ngoặc, Khoảng trắng, và các từ (Biến/Số)
    const tokens = text.split(/([+\-*/()<>!=&| ]+)/);
    let html = '';
    const knownVars = new Set(this.variables.map(v => v.code));

    tokens.forEach(token => {
      const trimmed = token.trim();
      if (!trimmed) {
        html += token; // Giữ nguyên khoảng trắng
      } else if (knownVars.has(trimmed)) {
        html += `<span class="badge-var">${token}</span>`;
      } else if (!isNaN(Number(trimmed))) {
        html += `<span class="token-number">${token}</span>`;
      } else if (['+', '-', '*', '/', '(', ')', '>', '<', '=', '&', '|'].some(op => token.includes(op))) {
        let opHtml = '';
        for (let char of token) {
          if (char === '(' || char === ')') {
            opHtml += `<span class="token-bracket">${char}</span>`;
          } else if (['+', '-', '*', '/'].includes(char)) {
            opHtml += `<span class="token-op">${char}</span>`;
          } else {
            opHtml += char;
          }
        }
        html += opHtml;
      } else {
        html += `<span class="token-unknown">${token}</span>`;
      }
    });

    return this.sanitizer.bypassSecurityTrustHtml(html);
  }


  // ================= TABS & MODES =================
  switchTab(tab: 'rules' | 'variables') {
    this.activeTab = tab;
    if (tab === 'variables' && this.sqlEditorInstance) {
      setTimeout(() => this.sqlEditorInstance.refresh(), 100);
    }
  }

  setRuleMode(mode: 'formula' | 'visual' | 'json') {
    if (this.uiMode === 'json' && mode === 'visual') {
      try {
        this.currentRule.dslJson = JSON.parse(this.jsonInput);
      } catch (e) {
        alert('JSON lỗi, không thể chuyển sang Visual Mode');
        return;
      }
    } else if (mode === 'json') {
      this.jsonInput = JSON.stringify(this.currentRule.dslJson, null, 2);
    }
    this.uiMode = mode;
  }

  setVarMode(mode: 'wizard' | 'sql') {
    this.varMode = mode;
    if (mode === 'sql' && this.sqlEditorInstance) {
      setTimeout(() => this.sqlEditorInstance.refresh(), 100);
    }
  }

  // ================= RULE LOGIC =================
  async loadRules() {
    const data = await getRules();
    if (data && Array.isArray(data)) {
      this.rules = data;
      this.cdr.detectChanges();
    }
  }

  resetRuleEditor() {
    this.currentRule = {
      ruleId: undefined, // Reset về undefined để ẩn nút xóa
      ruleCode: 'NEW_RULE',
      name: '',
      status: 'DRAFT',
      dslJson: { type: 'CONSTANT', value: 0 }
    };
    this.formulaInput = '';
    this.jsonInput = '{}';
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
      this.setRuleMode('formula');
    } else {
      this.setRuleMode('visual');
    }

    this.jsonInput = JSON.stringify(this.currentRule.dslJson, null, 2);
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
  }

  insertOperator(op: string) {
    this.formulaInput += ` ${op} `;
  }

  insertVariable(code: string) {
    if (this.uiMode === 'formula') {
      this.formulaInput += code;
    } else {
      alert(`Đã copy mã biến: ${code}`);
      navigator.clipboard.writeText(code);
    }
  }

  async saveRule() {
    if (this.uiMode === 'formula') {
      this.currentRule.dslJson = { type: 'RAW_FORMULA', expression: this.formulaInput };
    } else if (this.uiMode === 'json') {
      try {
        this.currentRule.dslJson = JSON.parse(this.jsonInput);
      } catch (e) { alert('JSON Invalid'); return; }
    }

    const payload = {
      ruleId: this.currentRule.ruleId,
      code: this.currentRule.ruleCode,
      ruleCode: this.currentRule.ruleCode,
      name: this.currentRule.name,
      dsl: JSON.stringify(this.currentRule.dslJson)
    };

    try {
      await saveRule(payload);
      alert('Đã lưu Rule thành công!');
      await this.loadRules();
    } catch (err: any) {
      alert('Lỗi khi lưu Rule: ' + (err?.response?.data?.message || err.message));
    }
  }

  async deleteRule() {
    if (!this.currentRule.ruleId) return;
    if (confirm('Bạn có chắc chắn muốn xóa rule này?')) {
      try {
        await deleteRule(this.currentRule.ruleId);
        alert('Đã xóa rule!');
        await this.loadRules();
        this.resetRuleEditor();
      } catch (err: any) {
        alert('Lỗi khi xóa: ' + (err?.response?.data?.message || err.message));
      }
    }
  }

  // ================= VARIABLE LOGIC =================
  async loadVariables() {
    const data = await getVariables();
    if (data && Array.isArray(data)) {
      this.variables = data;
      this.cdr.detectChanges();

    }
  }

  resetVarEditor() {
    this.currentVar = { code: '', name: '', sqlQuery: '', variableId: undefined };
    if (this.sqlEditorInstance) this.sqlEditorInstance.setValue('');
    this.setVarMode('wizard');
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

    if (this.sqlEditorInstance) {
      this.sqlEditorInstance.setValue(query);
      setTimeout(() => this.sqlEditorInstance.refresh(), 50);
    }
    this.setVarMode('wizard');
  }

  onWizSourceChange() {
    const map: any = {
      'contract': "SELECT COALESCE(basesalary, 0) FROM contracts WHERE userID = :userId AND Status = 'ACTIVE' LIMIT 1",
      'attendance': "SELECT COUNT(*) FROM attendances WHERE userID = :userId AND date BETWEEN :startDate AND :endDate AND status = 'PRESENT'",
      'overtime': "SELECT COALESCE(SUM(hours), 0) FROM overtime WHERE userid = :userId AND date BETWEEN :startDate AND :endDate",
      'reward': "SELECT COALESCE(SUM(Amount), 0) FROM rewardpunishmentdecisions WHERE UserID = :userId AND Type = 'REWARD'",
      'dependents': "SELECT COUNT(*) FROM dependents WHERE userID = :userId AND istaxdeductible = 1"
    };
    this.wizResultSql = map[this.wizSource] || '';
    if (this.sqlEditorInstance) this.sqlEditorInstance.setValue(this.wizResultSql);
    this.currentVar.sqlQuery = this.wizResultSql;
  }

  async saveVariable() {
    const payload = {
      id: this.currentVar.id || this.currentVar.variableId || null,
      code: this.currentVar.code,
      name: this.currentVar.name,
      description: this.currentVar.description,
      sqlQuery: this.currentVar.sqlQuery
    };

    try {
      await saveVariable(payload);
      alert('Đã lưu Biến số!');
      await this.loadVariables();
    } catch (err: any) {
      alert('Lỗi lưu biến: ' + (err?.response?.data || err.message));
    }
  }

  async deleteVariable() {
    if (!this.currentVar.id && !this.currentVar.variableId) return;
    if (confirm("Xóa biến này?")) {
      try {
        const id = this.currentVar.id || this.currentVar.variableId;
        if (id) { await deleteVariable(id); }
        await this.loadVariables();
        this.resetVarEditor();
      } catch (err: any) {
        alert("Lỗi xóa biến: " + err.message);
      }
    }
  }

  // ================= SIMULATOR =================
  async loadEmployees() {
    const data = await getAuditUsers();
    if (data && Array.isArray(data)) {
      this.employees = data;
      this.cdr.detectChanges();

    }
  }

  async testVariable() {

    if (!this.currentVar.sqlQuery) { alert('Chưa có SQL!'); return; }
    if (!this.simEmployeeId) { alert('Chọn nhân viên!'); return; }
    if (!this.simPeriod) { alert('Chọn kỳ lương!'); return; }

    this.isSimulating = true;
    this.simResult = 'Đang tính toán...';
    this.simDebug = `Executing SQL for User ID: ${this.simEmployeeId}...`;

    const [year, month] = this.simPeriod.split('-');

    const payload = {
      sql: this.currentVar.sqlQuery,
      userId: parseInt(this.simEmployeeId),
      month: parseInt(month),
      year: parseInt(year)
    };

    try {
      const data = await auditVariable(payload);
      this.isSimulating = false;

      const val = data.result;
      if (val !== null && !isNaN(parseFloat(val))) {
        this.simResult = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
        this.cdr.detectChanges();
      } else {
        this.simResult = val;
      }
      this.simDebug = `Context: ${data.auditContext || 'OK'}`;

    } catch (err: any) {
      this.isSimulating = false;
      this.simResult = 'LỖI SQL';
      this.simDebug = err?.response?.data || err.message;
    }
  }

  getTypeGroup(type: string): string {
    return this.RULE_TYPES[type]?.group || 'const';
  }
}