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
    private sanitizer: DomSanitizer
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

  // --- LOGIC CHUYỂN AST (RAW) SANG TEXT FORMULA ---
  // Hàm đệ quy để duyệt cây JSON và tạo chuỗi
  convertAstToString(node: any): string {
    if (!node) return '';

    // 1. Nếu là Biến
    if (node.type === 'VARIABLE') {
      return node.name || 'UNKNOWN_VAR';
    }

    // 2. Nếu là Hằng số
    if (node.type === 'CONSTANT') {
      return node.value !== undefined ? String(node.value) : '0';
    }

    // 3. Nếu là Phép toán (ADD, SUB, MUL, DIV, GT, LT...)
    const typeDef = this.RULE_TYPES[node.type];
    if (typeDef && typeDef.op) {
      const left = this.convertAstToString(node.left);
      const right = this.convertAstToString(node.right);
      return `(${left} ${typeDef.op} ${right})`;
    }

    // 4. Nếu là Logic IF_ELSE
    if (node.type === 'IF_ELSE') {
      const cond = this.convertAstToString(node.condition);
      const trueCase = this.convertAstToString(node.true_case);
      const falseCase = this.convertAstToString(node.false_case);
      return `IF ${cond} THEN { ${trueCase} } ELSE { ${falseCase} }`;
    }

    // 5. Nếu là RAW_FORMULA (trường hợp đặc biệt)
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
    const tokens = text.split(/([+\-*/()<>!=&| ]+)/);
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
      } else if (['+', '-', '*', '/', '(', ')', '>', '<', '=', '&', '|', 'IF', 'THEN', 'ELSE', '{', '}'].some(op => token.includes(op))) {
        // Highlight keyword IF/THEN/ELSE
        if (['IF', 'THEN', 'ELSE'].includes(trimmed)) {
          html += `<span class="token-keyword">${token}</span>`;
        } else {
          // Toán tử
          let opHtml = '';
          for (let char of token) {
            if (char === '(' || char === ')' || char === '{' || char === '}') {
              opHtml += `<span class="token-bracket">${char}</span>`;
            } else if (['+', '-', '*', '/'].includes(char)) {
              opHtml += `<span class="token-op">${char}</span>`;
            } else {
              opHtml += char;
            }
          }
          html += opHtml;
        }
      } else {
        html += `<span class="token-unknown">${token}</span>`;
      }
    });

    return this.sanitizer.bypassSecurityTrustHtml(html);
  }

  // ================= TABS & MODES =================
  switchTab(tab: 'rules' | 'variables') {
    this.activeTab = tab;

    // 2. LƯU TRẠNG THÁI VÀO SESSION KHI CLICK
    sessionStorage.setItem('payroll_active_tab', tab);

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
      this.rules = data.filter((r: Rule) => r.status !== 'RETIRED');
      this.cdr.detectChanges();
    }
  }

  resetRuleEditor() {
    this.currentRule = {
      ruleId: undefined,
      ruleCode: '',
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

    // 1. Parse JSON string nếu cần
    if (typeof this.currentRule.dslJson === 'string') {
      try {
        this.currentRule.dslJson = JSON.parse(this.currentRule.dslJson);
      } catch (e) {
        this.currentRule.dslJson = {};
      }
    }

    // 2. Xử lý hiển thị Text Formula (Reverse Parsing)
    // Nếu nó đã là RAW_FORMULA thì lấy expression
    if (this.currentRule.dslJson?.type === 'RAW_FORMULA') {
      this.formulaInput = this.currentRule.dslJson.expression;
    } else {
      // Nếu là cây AST (ADD, MUL...) -> Chuyển đổi sang Text để hiển thị
      this.formulaInput = this.convertAstToString(this.currentRule.dslJson);
    }

    // Mặc định vào tab Formula để xem trước
    this.setRuleMode('formula');

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
    // Nếu đang ở mode Formula -> Lưu dạng RAW_FORMULA
    // (Bởi vì user có thể đã sửa text, parse ngược lại thành cây AST rất phức tạp)
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
        this.cdr.detectChanges();

      }
      this.simDebug = `Context: ${data.auditContext || 'OK'}`;

    } catch (err: any) {
      this.isSimulating = false;
      this.simResult = 'LỖI SQL';
      this.cdr.detectChanges();

      this.simDebug = err?.response?.data || err.message;
    }
  }

  getTypeGroup(type: string): string {
    return this.RULE_TYPES[type]?.group || 'const';
  }
}