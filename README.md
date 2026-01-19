# Đồ án tốt nghiệp: Website Quản Lý Nhân Sự MANAPlastic

> **Mô tả:** Dự án xây dựng Website quản lý nhân sự cho công ty sản xuất bao bì nhựa MANAPlastic, tích hợp ứng dụng chấm công QR Code.

* **Live Demo - Sponsor By Hola Group:** [https://erpmini.cloud](https://erpmini.cloud)
* **Timesheet theo dõi tiến độ:** [Google Spreadsheet](https://docs.google.com/spreadsheets/d/1Ouz-jxjV6gg4vwenk94TxB3wv66ZlYrlJ33Hpq8s_h8/edit?gid=0#gid=0)

---

## 👥 Thành viên thực hiện

| Họ tên | Vai trò | Trách nhiệm |
| :--- | :--- | :--- |
| **Phạm Minh Anh** | Leader | Backend (Spring Boot) |
| **Nguyễn Ngọc Ân** | Member | Frontend (Angular) |

**Giảng viên hướng dẫn:** Thầy Nguyễn Minh Sang (Lecturer at FPT University)

---

## 🔐 Tài khoản đăng nhập (Demo)

| Quyền hạn | Username | Password |
| :--- | :--- | :--- |
| **Admin** | `admin` | `admin123` |
| **HR Manager** | `hr_manager` | `hr123` |
| **Quản Lý (Lead)** | `inan_lead` | `ql123` |
| **Nhân viên** | `000050` | `123123` |

---

## ⚙️ Hướng dẫn cài đặt

### Bước 1: Build Frontend (Angular)
1.  Vào thư mục `frontend`, mở terminal và chạy lần lượt các lệnh:
    ```bash
    npm i
    ng build
    ```
2.  Sau khi build xong, vào thư mục `/dist/Fe_luanvan`.
3.  Copy **toàn bộ** nội dung bên trong (bao gồm cả các thư mục con của `/browser`) ra bên ngoài cùng cấp với `/browser`.
4.  Copy và dán toàn bộ thư mục đó vào đường dẫn Backend:
    `\MANAPlastic_HR_Project\backend\src\main\resources\static`

### Bước 2: Cấu hình Backend (Spring Boot)
Mở file `application.properties` tại đường dẫn:
`\MANAPlastic_HR_Project\backend\src\main\resources\application.properties`

Cập nhật cấu hình như sau:

```properties
# Server
server.port=8080
server.address=0.0.0.0

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/manaplastic_hr
spring.datasource.username=root
spring.datasource.password=

# Email Sender Configuration
spring.mail.username=gmail_cua_ban@gmail.com
spring.mail.password=pass_key_app_cua_ban