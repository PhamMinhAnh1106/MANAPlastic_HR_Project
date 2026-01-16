import { Component, OnInit, AfterViewInit, ViewChild, ElementRef, OnDestroy, ChangeDetectorRef, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// Import API Service
import {
  contracttemplate,
  contracttemplateupdate,
  contracttemplatecreate,
  getcontracttemplate,
  getcontracttemplatebyid,
  updatecontracttemplate,
  createcontracttemplate
} from '../../../../../services/pages/features/hr/contracts.service';
import { Loading } from '../../../../shared/loading/loading';
import { Alert } from '../../../../shared/alert/alert';
import { Comfirm } from '../../../../shared/comfirm/comfirm';

export interface VariableItem {
  code: string;
}

export interface VariableGroup {
  title: string;
  items: VariableItem[];
  isTable?: boolean;
}

@Component({
  selector: 'app-contract-manager',
  standalone: true,
  imports: [CommonModule, FormsModule, Loading, Alert, Comfirm],
  templateUrl: './contract-main.html',
  styleUrls: ['./contract-main.scss']
})
export class ContractManager implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('editorElement') editorElement!: ElementRef;

  editorInstance: any = null;
  isSidebarOpen = false;
  isToolsOpen = false;

  // Shared Components State
  isloading: boolean = false;
  isconfirm: boolean = false;
  confirmMessage = "";
  isalert: boolean = false;
  notifyMessage = "";
  notifyType: boolean = true;

  // Callbacks
  private pendingConfirmAction: (() => void) | null = null;

  isEditMode = true;
  templates: contracttemplate[] = [];
  selectedTemplateId: number = 0;

  currentTemplate: contracttemplate = {
    id: 0,
    templateID: 0,
    name: '',
    type: '',
    content: ''
  };

  variableGroups: VariableGroup[] = [
    {
      title: 'Thông tin chung',
      items: [
        { code: '{{contract_code}}' },
        { code: '{{day}}' }, { code: '{{month}}' }, { code: '{{year}}' }
      ]
    },
    {
      title: 'Thông tin nhân sự',
      items: [
        { code: '{{employee_name}}' }, { code: '{{cccd}}' }, { code: '{{address}}' }
      ]
    },
    {
      title: 'Vị trí & Lương',
      items: [
        { code: '{{position}}' }, { code: '{{base_salary}}' }, { code: '{{standard_hours}}' }
      ]
    },
    {
      title: 'Bảng dữ liệu',
      isTable: true,
      items: [
        { code: '{{allowances_table}}' }
      ]
    }
  ];

  constructor(private cdr: ChangeDetectorRef) {
    this.loadExternalScripts();
  }

  // --- [NEW] Bắt sự kiện Ctrl + S ---
  @HostListener('window:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) {
    // Kiểm tra nếu phím Ctrl (hoặc Cmd trên Mac) + S được nhấn
    if ((event.ctrlKey || event.metaKey) && event.key === 's') {
      event.preventDefault(); // Ngăn trình duyệt mở hộp thoại Save

      // Chỉ lưu nếu không có popup (loading, confirm) đang hiển thị để tránh xung đột
      if (!this.isloading && !this.isconfirm && !this.isalert) {
        this.handleSave();
      }
    }
  }

  ngOnInit() {
    this.fetchTemplatesList();
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.initCKEditor();
    }, 500);
  }

  ngOnDestroy() {
    if (this.editorInstance) {
      this.editorInstance.destroy().catch((error: any) => console.log(error));
    }
  }

  // --- Notification Helpers ---
  showNotification(message: string, type: boolean) {
    this.notifyMessage = message;
    this.notifyType = type;
    this.isalert = true;
  }

  showConfirm(message: string, action: () => void) {
    this.confirmMessage = message;
    this.pendingConfirmAction = action;
    this.isconfirm = true;
  }

  onConfirmResult(result: boolean) {
    this.isconfirm = false;
    if (result && this.pendingConfirmAction) {
      this.pendingConfirmAction();
      this.cdr.detectChanges();
    }
    this.pendingConfirmAction = null;
  }

  // --- UI Helpers ---
  toggleSidebar() {
    this.isSidebarOpen = !this.isSidebarOpen;
    if (this.isSidebarOpen) this.isToolsOpen = false;
  }

  toggleTools() {
    this.isToolsOpen = !this.isToolsOpen;
  }

  closeAllMenus() {
    this.isSidebarOpen = false;
    this.isToolsOpen = false;
  }

  private loadExternalScripts() {
    const linkFA = document.createElement('link');
    linkFA.rel = 'stylesheet';
    linkFA.href = 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css';
    document.head.appendChild(linkFA);

    const linkCK = document.createElement('link');
    linkCK.rel = 'stylesheet';
    linkCK.href = 'https://cdn.ckeditor.com/ckeditor5/43.3.1/ckeditor5.css';
    document.head.appendChild(linkCK);

    const scriptMammoth = document.createElement('script');
    scriptMammoth.src = 'https://cdnjs.cloudflare.com/ajax/libs/mammoth/1.6.0/mammoth.browser.min.js';
    document.body.appendChild(scriptMammoth);
  }

  async initCKEditor() {
    try {
      // @ts-ignore
      const CKEDITOR = await import(/* webpackIgnore: true */ 'https://cdn.ckeditor.com/ckeditor5/43.3.1/ckeditor5.js');

      const {
        ClassicEditor, Essentials, Bold, Italic, Underline, Strikethrough,
        Paragraph, Heading, Font, Alignment, List, ListProperties,
        Link, BlockQuote,
        Table, TableToolbar, TableProperties, TableCellProperties, TableColumnResize,
        Indent, IndentBlock, HorizontalLine, PageBreak,
        RemoveFormat, SpecialCharacters, FindAndReplace, SelectAll,
        SourceEditing, GeneralHtmlSupport, PasteFromOffice,
        Image, ImageToolbar, ImageCaption, ImageStyle, ImageResize, ImageUpload
      } = CKEDITOR;

      const editor = await ClassicEditor.create(this.editorElement.nativeElement, {
        plugins: [
          Essentials, Bold, Italic, Underline, Strikethrough, Paragraph, Heading,
          Font, Alignment, List, ListProperties, Link, BlockQuote,
          Table, TableToolbar, TableProperties, TableCellProperties, TableColumnResize,
          Indent, IndentBlock, HorizontalLine, PageBreak,
          RemoveFormat, SpecialCharacters, FindAndReplace, SelectAll,
          SourceEditing, GeneralHtmlSupport, PasteFromOffice,
          Image, ImageToolbar, ImageCaption, ImageStyle, ImageResize, ImageUpload
        ],
        toolbar: {
          items: [
            'sourceEditing', '|',
            'undo', 'redo', '|',
            'findAndReplace', 'selectAll', '|',
            'heading', '|',
            'fontFamily', 'fontSize', 'fontColor', 'fontBackgroundColor', '|',
            'bold', 'italic', 'underline', 'strikethrough', '|',
            'alignment', '|',
            'bulletedList', 'numberedList', '|',
            'outdent', 'indent', '|',
            'insertTable', 'link', 'blockQuote', 'horizontalLine', 'pageBreak', '|',
            'removeFormat', 'specialCharacters'
          ],
          shouldNotGroupWhenFull: false
        },
        fontFamily: {
          options: [
            'default',
            'Times New Roman, Times, serif',
            'Arial, Helvetica, sans-serif',
            'Verdana, Geneva, sans-serif',
            'Courier New, Courier, monospace',
            'Tahoma, Geneva, sans-serif'
          ],
          supportAllValues: true
        },
        fontSize: {
          options: [
            9, 10, 11, 12, 13, 'default', 14, 15, 16, 17, 18, 20, 22, 24, 26, 28, 36, 48
          ],
          supportAllValues: true
        },
        list: {
          properties: {
            styles: true,
            startIndex: true,
            reversed: true
          }
        },
        table: {
          contentToolbar: [
            'tableColumn', 'tableRow', 'mergeTableCells',
            'tableProperties', 'tableCellProperties'
          ],
          tableProperties: {
            defaultProperties: {
              borderStyle: 'solid',
              borderColor: 'black',
              borderWidth: '1px',
              alignment: 'center'
            }
          }
        },
        htmlSupport: {
          allow: [
            {
              name: /.*/,
              attributes: true,
              classes: true,
              styles: true
            }
          ]
        },
        pasteFromOffice: {
          visual: true
        },
        image: {
          toolbar: [
            'imageStyle:inline', 'imageStyle:block', 'imageStyle:side',
            '|', 'toggleImageCaption', 'imageTextAlternative'
          ]
        }
      });

      this.editorInstance = editor;

      if (this.currentTemplate.content) {
        this.editorInstance.setData(this.currentTemplate.content);
      }

    } catch (error: any) {
      console.error("❌ Lỗi khởi tạo Editor:", error);
      this.showNotification("Không thể khởi tạo bộ soạn thảo (Kiểm tra kết nối mạng): " + error.message, false);
    }
  }

  async fetchTemplatesList() {
    try {
      const data = await getcontracttemplate();
      if (Array.isArray(data)) {
        this.templates = data;
        if (this.templates.length > 0 && this.selectedTemplateId === 0 && this.isEditMode) {
          this.selectedTemplateId = this.templates[0].id;
          this.loadTemplateDetails(this.selectedTemplateId);
        }
      }
    } catch (e: any) {
      console.error(e);
      this.showNotification("Không thể tải danh sách mẫu", false);
    } finally {
      this.cdr.detectChanges();
    }
  }

  async loadTemplateDetails(id: number) {
    if (!id || id === 0) return;

    this.isEditMode = true;
    this.isloading = true;
    if (this.editorInstance) this.editorInstance.enableReadOnlyMode("loading");

    try {
      const data = await getcontracttemplatebyid(id);
      if (data) {
        this.currentTemplate = data;
        if (this.editorInstance) {
          this.editorInstance.setData(this.currentTemplate.content || "");
        }
      }
    } catch (e: any) {
      console.error(e);
      this.showNotification("Không thể tải chi tiết mẫu", false);
    } finally {
      this.isloading = false;
      if (this.editorInstance) this.editorInstance.disableReadOnlyMode("loading");
      this.cdr.detectChanges();
    }
  }

  startCreateNew() {
    this.showConfirm("Bạn có chắc muốn tạo mới? Dữ liệu hiện tại chưa lưu sẽ bị mất.", () => {
      this.isEditMode = false;
      this.selectedTemplateId = 0;
      this.isToolsOpen = false;

      this.currentTemplate = {
        id: 0,
        templateID: 0,
        name: '',
        type: '',
        content: ''
      };

      if (this.editorInstance) {
        this.editorInstance.setData("");
      }

      this.showNotification("Đã chuyển sang chế độ tạo mới", true);
    });
  }

  async handleSave() {
    const content = this.editorInstance ? this.editorInstance.getData() : "";

    if (!this.currentTemplate.name) {
      this.showNotification("Vui lòng nhập tên mẫu hợp đồng", false);
      return;
    }

    const actionName = this.isEditMode ? "cập nhật" : "tạo mới";
    this.showConfirm(`Bạn có chắc chắn muốn ${actionName} mẫu hợp đồng này không?`, async () => {

      if (this.isEditMode) {
        if (!this.selectedTemplateId) {
          this.showNotification("Vui lòng chọn một mẫu để cập nhật", false);
          return;
        }

        const payload: contracttemplateupdate = {
          templateID: this.currentTemplate.templateID,
          name: this.currentTemplate.name,
          type: this.currentTemplate.type,
          content: content
        };

        try {
          this.isloading = true;
          const res: any = await updatecontracttemplate(this.selectedTemplateId, payload);
          if (res.status === 200 || res.status === 204) {
            this.showNotification("Cập nhật hợp đồng thành công!", true);
            const idx = this.templates.findIndex(t => t.id === this.selectedTemplateId);
            if (idx !== -1) this.templates[idx] = { ...this.currentTemplate, content };
          } else {
            this.showNotification("Lỗi khi cập nhật dữ liệu", false);
          }
        } catch (e) {
          this.showNotification("Lỗi kết nối server", false);
        } finally {
          this.isloading = false;
          this.cdr.detectChanges();
        }

      } else {
        const payload: contracttemplatecreate = {
          name: this.currentTemplate.name,
          type: this.currentTemplate.type || "OTHER",
          content: content
        };

        try {
          this.isloading = true;
          const res: any = await createcontracttemplate(payload);
          if (res.status === 200 || res.status === 201) {
            this.showNotification("Thêm mới hợp đồng thành công!", true);
            await this.fetchTemplatesList();
            this.isEditMode = true;
          } else {
            this.showNotification("Lỗi khi thêm mới", false);
          }
        } catch (e) {
          this.showNotification("Lỗi kết nối server", false);
        } finally {
          this.isloading = false;
        }
      }
    });
  }

  onFileSelected(event: any) {
    this.isToolsOpen = false;
    const file = event.target.files[0];
    if (!file) return;

    this.showConfirm("Import file sẽ thay thế nội dung hiện tại. Bạn có muốn tiếp tục?", () => {
      this.isEditMode = false;
      this.selectedTemplateId = 0;
      this.currentTemplate.name = file.name.split('.')[0];

      const isWord = file.name.toLowerCase().endsWith('.docx');
      const reader = new FileReader();

      if (isWord) {
        reader.onload = (e: any) => {
          const arrayBuffer = e.target.result;
          // @ts-ignore
          if (typeof mammoth === 'undefined') {
            this.showNotification("Thư viện đọc Word chưa tải xong", false);
            return;
          }
          // @ts-ignore
          mammoth.convertToHtml({ arrayBuffer: arrayBuffer })
            .then((result: any) => {
              if (this.editorInstance) {
                this.editorInstance.setData(result.value);
                this.showNotification("Đã tải nội dung từ file Word", true);
              }
            })
            .catch((err: any) => {
              this.showNotification("Không đọc được file Word: " + err.message, false);
            });
        };
        reader.readAsArrayBuffer(file);
      } else {
        reader.onload = (e: any) => {
          if (this.editorInstance) {
            this.editorInstance.setData(e.target.result);
            this.showNotification("Đã tải nội dung từ file", true);
          }
        };
        reader.readAsText(file);
      }
      event.target.value = '';
    });
  }

  onTemplateChange() {
    this.isEditMode = true;
    this.loadTemplateDetails(Number(this.selectedTemplateId));
  }

  insertVariable(text: string) {
    if (!this.editorInstance) return;
    this.editorInstance.model.change((writer: any) => {
      const insertPosition = this.editorInstance.model.document.selection.getFirstPosition();
      writer.insertText(text, { bold: true, fontColor: '#0d6efd' }, insertPosition);
    });
    if (window.innerWidth <= 768) {
      this.isSidebarOpen = false;
    }
  }
}