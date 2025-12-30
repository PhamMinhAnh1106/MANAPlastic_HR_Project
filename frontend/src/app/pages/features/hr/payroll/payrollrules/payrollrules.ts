import { ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, ViewChild, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
// Import API gốc của bạn
import { auditVariable, deleteRule, deleteVariable, getAuditUsers, getRules, getVariables, saveRule, saveVariable } from '../../../../../services/pages/features/hr/payroll/rules.services';
import { Alert } from '../../../../shared/alert/alert';
import { Comfirm } from '../../../../shared/comfirm/comfirm';

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
  imports: [CommonModule, FormsModule, Alert, Comfirm],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './payrollrules.html',
  styleUrls: ['./payrollrules.scss'],
})
export class Payrollrules implements OnInit, AfterViewInit {
  // --- CONFIRM & ALERT STATE ---
  isconfirm: boolean = false;
  confirmMessage: string = '';
  confirmType: 'RULE' | 'VARIABLE' | 'RULE_SAVE' = 'RULE';

  isalert: boolean = false;
  alertmessage: string = '';
  alertType: boolean = false;

  Onalert(message: string, type: boolean) {
    this.isalert = true;
    this.alertmessage = message;
    this.alertType = type;
    this.cdr.detectChanges();
  }

  // --- STATE ---
  activeTab: 'rules' | 'variables' = 'rules';
  uiMode: 'formula' | 'visual' | 'json' = 'formula';
  varMode: 'wizard' | 'sql' = 'wizard';

  isEditingVar: boolean = false;
  originalVarState: Variable | null = null;

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
    'REFERENCE': { label: '🔗 Tham chiếu (Rule)', group: 'data', args: [] },
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
          readOnly: 'nocursor'
        });
        this.sqlEditorInstance.on('change', (cm: any) => {
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

  // --- LOGIC CONVERT AST <-> STRING ---

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

  // --- NEW: PARSER LOGIC (Text -> Tree) ---

  /**
   * Phân tích chuỗi công thức thành JSON Tree (AST)
   * Hỗ trợ: +, -, *, /, (), Biến, Số
   */
  parseFormulaToAst(expression: string): any {
    if (!expression || !expression.trim()) return { type: 'CONSTANT', value: 0 };

    // 1. Tokenize: Tách chuỗi thành các token
    // Regex bắt: Số, Tên biến (chữ cái bắt đầu), hoặc toán tử
    const tokens = expression.match(/([a-zA-Z_]\w*|\d+(\.\d+)?|[+\-*/()])/g);
    if (!tokens) return { type: 'CONSTANT', value: 0 };

    const outputQueue: any[] = [];
    const operatorStack: string[] = [];

    const precedence: any = {
      '*': 3, '/': 3,
      '+': 2, '-': 2,
      '(': 1
    };

    // 2. Shunting Yard Algorithm (Infix to Postfix)
    for (const token of tokens) {
      if (!isNaN(Number(token))) {
        // Là số
        outputQueue.push({ type: 'CONSTANT', value: Number(token) });
      } else if (/^[a-zA-Z_]/.test(token)) {
        // Là Biến hoặc Rule Reference
        // Kiểm tra xem nó là Rule hay Variable để gán type đúng
        const isRule = this.rules.some(r => r.ruleCode === token);
        outputQueue.push({
          type: isRule ? 'REFERENCE' : 'VARIABLE',
          name: token
        });
      } else if (token === '(') {
        operatorStack.push(token);
      } else if (token === ')') {
        while (operatorStack.length > 0 && operatorStack[operatorStack.length - 1] !== '(') {
          this.processOperator(operatorStack.pop()!, outputQueue);
        }
        operatorStack.pop(); // Pop '('
      } else if (['+', '-', '*', '/'].includes(token)) {
        while (
          operatorStack.length > 0 &&
          precedence[operatorStack[operatorStack.length - 1]] >= precedence[token]
        ) {
          this.processOperator(operatorStack.pop()!, outputQueue);
        }
        operatorStack.push(token);
      }
    }

    while (operatorStack.length > 0) {
      this.processOperator(operatorStack.pop()!, outputQueue);
    }

    // 3. Result is the root of the tree
    if (outputQueue.length === 0) return { type: 'CONSTANT', value: 0 };
    return outputQueue[0];
  }

  processOperator(op: string, stack: any[]) {
    // Lấy 2 phần tử cuối làm left, right
    // Lưu ý: Stack là LIFO, nên phần tử lấy ra đầu tiên là Right
    const right = stack.pop() || { type: 'CONSTANT', value: 0 };
    const left = stack.pop() || { type: 'CONSTANT', value: 0 };

    let type = 'ADD';
    if (op === '+') type = 'ADD';
    if (op === '-') type = 'SUB';
    if (op === '*') type = 'MUL';
    if (op === '/') type = 'DIV';

    stack.push({
      type: type,
      left: left,
      right: right
    });
  }

  // --- END PARSER LOGIC ---

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
    // 1. Chuyển từ JSON -> Visual
    if (this.uiMode === 'json' && mode === 'visual') {
      try { this.currentRule.dslJson = JSON.parse(this.jsonInput); } catch (e) { return; }
    }
    // 2. Chuyển từ Formula -> Visual/Json (Tự động Parse)
    else if (this.uiMode === 'formula' && (mode === 'visual' || mode === 'json')) {
      try {
        // Thử parse công thức hiện tại sang Tree
        const ast = this.parseFormulaToAst(this.formulaInput);
        this.currentRule.dslJson = ast;
        this.jsonInput = JSON.stringify(ast, null, 2);
      } catch (e) {
        console.warn("Không thể parse công thức", e);
        // Nếu lỗi thì giữ nguyên hoặc về mặc định
      }
    }
    // 3. Chuyển sang JSON để hiển thị
    else if (mode === 'json') {
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
      const el = document.createElement('textarea');
      el.value = code;
      document.body.appendChild(el);
      el.select();
      document.execCommand('copy');
      document.body.removeChild(el);
      this.Onalert(`Đã copy mã biến ${code}`, true);
    }
  }

  // --- SAVE RULE FLOW ---

  preSaveRule() {
    this.confirmType = 'RULE_SAVE';
    this.confirmMessage = 'Bạn có chắc chắn muốn lưu cấu hình Rule này không?';
    this.isconfirm = true;
    this.cdr.detectChanges();
  }

  async executeSaveRule() {
    // --- PARSER INTEGRATION ---
    // Nếu đang ở chế độ Text (Formula), ta thử Parse nó sang Tree trước khi lưu
    // Thay vì lưu RAW_FORMULA, ta lưu Tree chuẩn.
    if (this.uiMode === 'formula') {
      try {
        const parsedAst = this.parseFormulaToAst(this.formulaInput);
        this.currentRule.dslJson = parsedAst;
        console.log('Parsed AST:', parsedAst); // Debug
      } catch (e) {
        // Nếu parse lỗi (ví dụ IF ELSE phức tạp chưa support parser), fallback về RAW
        console.warn('Parser failed, falling back to RAW_FORMULA');
        this.currentRule.dslJson = { type: 'RAW_FORMULA', expression: this.formulaInput };
      }
    }
    else if (this.uiMode === 'json') {
      try {
        this.currentRule.dslJson = JSON.parse(this.jsonInput);
      } catch (e) {
        this.Onalert("JSON không hợp lệ!", false);
        return;
      }
    } else if (this.uiMode === 'visual') {
      if (!this.currentRule.dslJson) this.currentRule.dslJson = { type: 'CONSTANT', value: 0 };
    }

    const payload = {
      ruleId: this.currentRule.ruleId,
      code: this.currentRule.ruleCode.toUpperCase().trim(),
      ruleCode: this.currentRule.ruleCode.toUpperCase().trim(),
      name: this.currentRule.name,
      dsl: JSON.stringify(this.currentRule.dslJson)
    };

    try {
      const res = await saveRule(payload);
      if (res) {
        this.Onalert("Lưu Rule thành công!", true);
        await this.loadRules();
      } else {
        this.Onalert("Lưu thất bại! Kiểm tra lại mã lỗi.", false);
      }
    } catch (err: any) {
      this.Onalert("Lỗi hệ thống: " + err.message, false);
    }
  }

  // --- ACTIONS: DELETE ---
  async deleteRule() {
    this.confirmType = 'RULE';
    this.confirmMessage = 'Bạn có chắc chắn muốn xóa rule này không?';
    this.isconfirm = true;
  }

  async deleteVariable() {
    this.confirmType = 'VARIABLE';
    this.confirmMessage = 'Bạn có chắc chắn muốn xóa biến này không?';
    this.isconfirm = true;
  }

  async onConfirmResult(event: any) {
    this.isconfirm = false;
    if (event !== true) return;

    if (this.confirmType === 'RULE') {
      if (!this.currentRule.ruleId) return;
      const res = await deleteRule(this.currentRule.ruleId);
      if (res) { this.Onalert(res, true); await this.loadRules(); this.resetRuleEditor(); }
      else { this.Onalert(res, false); }
    }
    else if (this.confirmType === 'VARIABLE') {
      const id = this.currentVar.id || this.currentVar.variableId;
      if (!id) return;
      const res = await deleteVariable(id);
      if (res) { this.Onalert(res, true); await this.loadVariables(); this.resetVarEditor(); }
      else { this.Onalert(res, false); }
    }
    else if (this.confirmType === 'RULE_SAVE') {
      await this.executeSaveRule();
    }
    this.cdr.detectChanges();
  }

  async loadVariables() {
    try {
      const data = await getVariables();
      if (data && Array.isArray(data)) { this.variables = data; this.cdr.detectChanges(); }
    } catch (e) { console.error(e); }
  }

  resetVarEditor() {
    this.currentVar = { code: '', name: '', sqlQuery: '', variableId: undefined, description: '' };
    this.wizSource = ''; this.wizResultSql = '';
    this.isEditingVar = true;
    this.originalVarState = null;
    if (this.sqlEditorInstance) {
      this.sqlEditorInstance.setValue('');
      this.sqlEditorInstance.setOption('readOnly', false);
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
    this.isEditingVar = false;
    this.originalVarState = null;
    if (this.sqlEditorInstance) {
      this.sqlEditorInstance.setValue(query);
      this.sqlEditorInstance.setOption('readOnly', 'nocursor');
      setTimeout(() => this.sqlEditorInstance.refresh(), 50);
    }
    this.setVarMode('wizard');
    this.cdr.detectChanges();
  }

  startEditVar() {
    this.originalVarState = { ...this.currentVar };
    this.isEditingVar = true;
    if (this.sqlEditorInstance) { this.sqlEditorInstance.setOption('readOnly', false); }
    this.cdr.detectChanges();
  }

  cancelEditVar() {
    if (this.originalVarState) {
      this.currentVar = { ...this.originalVarState };
      this.wizResultSql = this.currentVar.sqlQuery || '';
      if (this.sqlEditorInstance) this.sqlEditorInstance.setValue(this.currentVar.sqlQuery || '');
    } else { this.isEditingVar = false; }
    if (this.currentVar.id || this.currentVar.variableId) {
      this.isEditingVar = false;
      this.originalVarState = null;
      if (this.sqlEditorInstance) this.sqlEditorInstance.setOption('readOnly', 'nocursor');
    }
    this.cdr.detectChanges();
  }

  onWizSourceChange() {
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
    if (!this.isEditingVar) return;
    const payload = { id: this.currentVar.id || this.currentVar.variableId, code: this.currentVar.code, name: this.currentVar.name, description: this.currentVar.description, sqlQuery: this.currentVar.sqlQuery };
    const res = await saveVariable(payload);
    if (res) { this.Onalert(res, true); await this.loadVariables(); }
    this.isEditingVar = false;
    this.originalVarState = null;
    if (this.sqlEditorInstance) { this.sqlEditorInstance.setOption('readOnly', 'nocursor'); }
    this.cdr.detectChanges();
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