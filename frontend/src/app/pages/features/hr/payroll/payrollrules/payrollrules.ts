import { ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, ViewChild, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
// Import API gốc của bạn
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

  // State quản lý việc sửa biến
  isEditingVar: boolean = false;
  originalVarState: Variable | null = null; // Lưu trạng thái gốc để hoàn tác

  rules: Rule[] = [];
  variables: Variable[] = [];
  employees: any[] = [];

  currentRule: Rule = { ruleCode: 'NEW_RULE', name: '', status: 'DRAFT', dslJson: { type: 'CONSTANT', value: 0 } };
  currentVar: Variable = { code: '', name: '', sqlQuery: '', variableId: undefined };

  formulaInput: string = '';
  jsonInput: string = '{}';

  wizSource: string = '';
  wizResultSql: string = '';

  simEmployeeId: string = '';
  simPeriod: string = new Date().toISOString().substring(0, 7);
  simResult: string = '---';
  simDebug: string = '';
  isSimulating = false;

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
    'VARIABLE': { label: '📦 Biến số (Var)', group: 'data', args: [] },
    'REFERENCE': { label: '🔗 Tham chiếu (Ref)', group: 'data', args: [] },
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
    this.loadRules();
    this.loadVariables();
    this.loadEmployees();
    this.resetRuleEditor();
  }

  ngAfterViewInit() {
    setTimeout(() => {
      if (this.sqlTextarea && typeof CodeMirror !== 'undefined') {
        this.sqlEditorInstance = CodeMirror.fromTextArea(this.sqlTextarea.nativeElement, {
          mode: 'text/x-sql', theme: 'default', lineNumbers: true, matchBrackets: true,
          readOnly: 'nocursor' // Mặc định Read-only
        });
        this.sqlEditorInstance.on('change', (cm: any) => {
          // Chỉ cập nhật giá trị nếu đang ở chế độ sửa
          if (this.isEditingVar) {
            const value = cm.getValue();
            this.currentVar.sqlQuery = value;
            this.wizResultSql = value;
            this.cdr.detectChanges();
          }
        });
        if (this.currentVar.sqlQuery) this.sqlEditorInstance.setValue(this.currentVar.sqlQuery);
      }
    }, 100);
  }

  // --- LOGIC ---
  convertAstToString(node: any): string {
    if (!node) return '';
    if (node.type === 'VARIABLE' || node.type === 'REFERENCE') return node.name || 'UNKNOWN_VAR';
    if (node.type === 'CONSTANT') return node.value !== undefined ? String(node.value) : '0';
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
    if (node.type === 'RAW_FORMULA') return node.expression || '';
    return '';
  }

  get formattedFormula(): SafeHtml {
    return this.highlightFormula(this.formulaInput);
  }

  highlightFormula(text: string): SafeHtml {
    if (!text) return '';
    const tokens = text.split(/([+\-*/()<>!=&|{}\s]+)/);
    let html = '';
    const knownVars = new Set([...this.variables.map(v => v.code), ...this.rules.map(r => r.ruleCode)]);
    tokens.forEach(token => {
      const trimmed = token.trim();
      if (!trimmed) html += token;
      else if (knownVars.has(trimmed)) html += `<span class="badge-var">${token}</span>`;
      else if (!isNaN(Number(trimmed))) html += `<span class="token-number">${token}</span>`;
      else if (['IF', 'THEN', 'ELSE'].includes(trimmed.toUpperCase())) html += `<span class="token-keyword">${token}</span>`;
      else if (['+', '-', '*', '/', '>', '<', '=', '&', '|'].includes(trimmed)) html += `<span class="token-op">${token}</span>`;
      else if (['(', ')', '{', '}'].includes(trimmed)) html += `<span class="token-bracket">${token}</span>`;
      else html += `<span class="token-unknown">${token}</span>`;
    });
    return this.sanitizer.bypassSecurityTrustHtml(html);
  }

  // --- ACTIONS ---
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
      try { this.currentRule.dslJson = JSON.parse(this.jsonInput); } catch (e) { return; }
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

  async loadRules() {
    try {
      const data = await getRules();
      if (data && Array.isArray(data)) {
        this.rules = data.filter((r: Rule) => r.status !== 'RETIRED');
        this.cdr.detectChanges();
      }
    } catch (e) { console.error(e); }
  }

  resetRuleEditor() {
    this.currentRule = { ruleId: undefined, ruleCode: 'NEW_RULE', name: '', status: 'DRAFT', dslJson: { type: 'CONSTANT', value: 0 } };
    this.formulaInput = '';
    this.jsonInput = JSON.stringify(this.currentRule.dslJson, null, 2);
    this.setRuleMode('formula');
    this.cdr.detectChanges();
  }

  selectRule(rule: Rule) {
    this.currentRule = { ...rule };
    if (typeof this.currentRule.dslJson === 'string') {
      try { this.currentRule.dslJson = JSON.parse(this.currentRule.dslJson); } catch (e) { this.currentRule.dslJson = {}; }
    }
    if (this.currentRule.dslJson?.type === 'RAW_FORMULA') this.formulaInput = this.currentRule.dslJson.expression;
    else this.formulaInput = this.convertAstToString(this.currentRule.dslJson);
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
    if ((newType === 'VARIABLE' || newType === 'REFERENCE') && !node.name) node.name = '';
    if (newType === 'CONSTANT' && node.value === undefined) node.value = 0;
    this.cdr.detectChanges();
  }

  insertOperator(op: string) { this.formulaInput += ` ${op} `; }

  insertVariable(code: string) {
    if (this.activeTab === 'rules' && this.uiMode === 'formula') this.formulaInput += code;
    else {
      // Copy to clipboard fallback
      const el = document.createElement('textarea');
      el.value = code;
      document.body.appendChild(el);
      el.select();
      document.execCommand('copy');
      document.body.removeChild(el);
    }
  }

  async saveRule() {
    if (this.uiMode === 'formula') this.currentRule.dslJson = { type: 'RAW_FORMULA', expression: this.formulaInput };
    else if (this.uiMode === 'json') try { this.currentRule.dslJson = JSON.parse(this.jsonInput); } catch (e) { return; }

    const payload = {
      ruleId: this.currentRule.ruleId,
      code: this.currentRule.ruleCode.toUpperCase().trim(),
      ruleCode: this.currentRule.ruleCode.toUpperCase().trim(),
      name: this.currentRule.name,
      dsl: JSON.stringify(this.currentRule.dslJson)
    };
    await saveRule(payload);
    await this.loadRules();
  }

  async deleteRule() {
    if (!this.currentRule.ruleId) return;
    if (window.confirm('Delete rule?')) {
      await deleteRule(this.currentRule.ruleId);
      await this.loadRules();
      this.resetRuleEditor();
    }
  }

  async loadVariables() {
    try {
      const data = await getVariables();
      if (data && Array.isArray(data)) {
        this.variables = data;
        this.cdr.detectChanges();
      }
    } catch (e) { console.error(e); }
  }

  // --- LOGIC SỬA BIẾN (NEW) ---

  resetVarEditor() {
    // Chế độ tạo mới: Cho phép sửa tất cả
    this.currentVar = { code: '', name: '', sqlQuery: '', variableId: undefined, description: '' };
    this.wizSource = ''; this.wizResultSql = '';

    this.isEditingVar = true; // Bật chế độ sửa
    this.originalVarState = null; // Không có state gốc để revert

    if (this.sqlEditorInstance) {
      this.sqlEditorInstance.setValue('');
      this.sqlEditorInstance.setOption('readOnly', false); // Mở khóa editor
    }
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
    this.wizResultSql = query;

    // Chế độ xem: Khóa tất cả
    this.isEditingVar = false;
    this.originalVarState = null;

    if (this.sqlEditorInstance) {
      this.sqlEditorInstance.setValue(query);
      this.sqlEditorInstance.setOption('readOnly', 'nocursor'); // Khóa editor
      setTimeout(() => this.sqlEditorInstance.refresh(), 50);
    }
    this.setVarMode('wizard');
    this.cdr.detectChanges();
  }

  startEditVar() {
    // Lưu trạng thái gốc
    this.originalVarState = { ...this.currentVar };
    this.isEditingVar = true;
    // Mở khóa Editor
    if (this.sqlEditorInstance) {
      this.sqlEditorInstance.setOption('readOnly', false);
    }
    this.cdr.detectChanges();
  }

  cancelEditVar() {
    // Revert lại dữ liệu cũ
    if (this.originalVarState) {
      this.currentVar = { ...this.originalVarState };
      this.wizResultSql = this.currentVar.sqlQuery || '';
      if (this.sqlEditorInstance) {
        this.sqlEditorInstance.setValue(this.currentVar.sqlQuery || '');
      }
    } else {
      // Nếu đang tạo mới mà hủy -> Về rỗng hoặc list (ở đây ta chỉ clear form và set readonly)
      this.isEditingVar = false;
    }

    // Nếu là biến có sẵn -> về chế độ Read-only
    if (this.currentVar.id || this.currentVar.variableId) {
      this.isEditingVar = false;
      this.originalVarState = null;
      if (this.sqlEditorInstance) {
        this.sqlEditorInstance.setOption('readOnly', 'nocursor');
      }
    }
    this.cdr.detectChanges();
  }

  onWizSourceChange() {
    // Chỉ cho phép chọn template khi đang ở chế độ sửa
    if (!this.isEditingVar) return;

    const selectedVar = this.variables.find(v => v.code === this.wizSource);
    if (selectedVar) {
      const sql = selectedVar.sqlQuery || selectedVar.sQLQuery || '';
      this.wizResultSql = sql;
      this.currentVar.sqlQuery = sql;
      this.currentVar.description = selectedVar.description;
      if (this.sqlEditorInstance) this.sqlEditorInstance.setValue(sql);
    } else {
      this.wizResultSql = '';
      this.currentVar.sqlQuery = '';
    }
    this.cdr.detectChanges();
  }

  async saveVariable() {
    if (!this.isEditingVar) return; // Guard

    const payload = { id: this.currentVar.id || this.currentVar.variableId, code: this.currentVar.code, name: this.currentVar.name, description: this.currentVar.description, sqlQuery: this.currentVar.sqlQuery };
    await saveVariable(payload);
    await this.loadVariables();

    // Sau khi lưu xong -> Về chế độ xem
    this.isEditingVar = false;
    this.originalVarState = null;
    if (this.sqlEditorInstance) {
      this.sqlEditorInstance.setOption('readOnly', 'nocursor');
    }
    this.cdr.detectChanges();
  }

  async deleteVariable() {
    const id = this.currentVar.id || this.currentVar.variableId;
    if (id && window.confirm("Delete variable?")) {
      await deleteVariable(id);
      await this.loadVariables();
      this.resetVarEditor();
    }
  }

  async loadEmployees() {
    const data = await getAuditUsers();
    if (data) { this.employees = data; this.cdr.detectChanges(); }
  }

  async testVariable() {
    if (!this.currentVar.sqlQuery || !this.simEmployeeId) return;
    this.isSimulating = true; this.simResult = 'Computing...';
    const [year, month] = this.simPeriod.split('-');
    const payload = { sql: this.currentVar.sqlQuery, userId: parseInt(this.simEmployeeId), month: parseInt(month), year: parseInt(year) };
    try {
      const data = await auditVariable(payload);
      this.isSimulating = false;
      const val = data.result;
      const numVal = Number(val);
      if (val !== null && !isNaN(numVal)) {
        this.simResult = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(numVal);
      } else {
        this.simResult = String(val);
      }
      this.simDebug = `Context: ${data.auditContext || 'OK'}`;
    } catch (err: any) {
      this.isSimulating = false; this.simResult = 'ERROR';
      this.simDebug = err?.message || 'Unknown Error';
    }
    this.cdr.detectChanges();
  }

  getTypeGroup(type: string): string {
    return this.RULE_TYPES[type]?.group || 'const';
  }
}