// checkrole() {
//     const icon: any[] = [{
//       iconName: "home", path: "/home/info", task: [{ name: "Trang chủ", path: "/home/info" }]
//     }];
//     this.role = DecodeTokenRole(this.token);
//     if (this.role.length > 0)
//       this.cookieService.set("role", this.role[0], { path: "/" });

//     const currentRole = this.role[0] || '';

//     switch (currentRole) {
//       case "Admin":
//         const icon_admin = [
//           // 1. Quản trị hệ thống & Tài khoản (Gốc của Admin)
//           {
//             iconName: "manage_accounts",
//             path: "/home/user/account",
//             task: [
//               { name: "Quản Lý Tài Khoản", path: "/home/user/account" },
//               { name: "Cấp Quyền Hạn", path: "/home/permission" }
//             ]
//           },
//           // 2. Nhân sự & Chấm công (Lấy từ HR + Manager)
//           {
//             iconName: "event_available",
//             path: "/home/user/attendance",
//             task: [
//               { name: "Quản Lý Chấm Công", path: "/home/user/attendance" },
//               { name: "Lịch Làm Việc", path: "/home/schedule" }, // Thêm từ Manager
//               { name: "Quản Lý Phép", path: "/home/leaverequest/manage" },
//               { name: "Đăng Ký Nghỉ Phép", path: "/home/leaverequest" } // Admin cũng có thể cần nghỉ phép
//             ]
//           },
//           // 3. Hợp đồng (Lấy từ HR)
//           {
//             iconName: "article",
//             path: "/home/contracts",
//             task: [
//               { name: "Quản Lý Hợp Đồng", path: "/home/contracts/edit" },
//               { name: "Kiểm tra Hợp Đồng", path: "/home/contracts" },
//               { name: "Thêm Hợp Đồng", path: "/home/contracts/edit/add" }
//             ],
//           },
//           // 4. Lương & Thưởng (Gộp Admin + HR)
//           {
//             iconName: "paid", // Hoặc dùng icon currency_exchange của HR
//             path: "/home/payroll",
//             task: [
//               { name: "Cấu Hình Lương", path: "/home/payroll/rules" },
//               { name: "Tính Lương", path: "/home/payroll" },
//               { name: "Xem Lương", path: "/home/payroll/payslip" },
//               { name: "Lọc DS Lương", path: "/home/payroll/payslip/filter" },
//               { name: "Quản Lý Thưởng/Phạt", path: "/home/user/reward-punish" }
//             ],
//           },
//           // 5. Luật (Gốc của Admin)
//           {
//             iconName: "gavel",
//             path: "/home/law",
//             task: [{ name: "Quản Lý Cấu Hình Luật", path: "/home/law" }],
//           },
//           // 6. Logs hệ thống (Gốc của Admin)
//           {
//             iconName: "event_note",
//             path: "/home/activity-logs",
//             task: [{ name: "Quản Lý Hoạt Động", path: "/home/activity-logs" }],
//           },
//         ];
//         icon.push(...icon_admin)
//         this.icon_handleBar = icon;
//         break;

//       case "HR":
//         const icon_hr = [
//           {
//             iconName: "manage_accounts",
//             path: "/home/user/account",
//             task: [{ name: "Quản Lý Nhân Sự", path: "/home/user/account" }],
//           },
//           {
//             iconName: "event_available",
//             path: "/home/user/attendance",
//             task: [{ name: "Quản Lý Chấm Công", path: "/home/user/attendance" }, { name: "Quản Lý Phép", path: "/home/leaverequest/manage" }]
//           },
//           {
//             iconName: "article",
//             path: "/home/contracts",
//             task: [{ name: "Quản Lý Hợp Đồng", path: "/home/contracts/edit" }, { name: "kiểm tra Hợp Đồng", path: "/home/contracts" }
//               , { name: "Thêm Hợp Đồng", path: "/home/contracts/edit/add" }
//             ],
//           },
//           {
//             iconName: "currency_exchange",
//             path: "/home/payroll",
//             task: [{ name: "Tính Lương", path: "/home/payroll" },
//             { name: "Cấu Hình Lương", path: "/home/payroll/rules" },
//             { name: "Xem Lương", path: "/home/payroll/payslip" },
//             { name: "Lọc DS Lương", path: "/home/payroll/payslip/filter" },
//             { name: "Quản Lý Thưởng/Phạt", path: "/home/user/reward-punish" }
//             ],
//           }, {
//             iconName: "gavel",
//             path: "/home/law",
//             task: [{ name: "Quản Lý Cấu Hình Luật", path: "/home/law" }],
//           },
//         ];
//         icon.push(...icon_hr)
//         this.icon_handleBar = icon;
//         break;

//       case "Manager":
//         const icon_manager = [
//           {
//             iconName: "edit_calendar",
//             path: "/home/user/attendance",
//             task: [{ name: "Quản Lý Chấm Công", path: "/home/user/attendance" }, { name: "Lịch Làm Việc", path: "/home/schedule" }]
//           },
//           {
//             iconName: "flight_takeoff",
//             path: "/home/leaverequest",
//             task: [{ name: "Nghỉ Phép", path: "/home/leaverequest" }, { name: "Quản Lý Phép", path: "/home/leaverequest/manage" }]
//           },
//           {
//             iconName: "receipt_long",
//             path: "/home/payroll/payslip",
//             task: [{ name: "Xem Lương", path: "/home/payroll/payslip" },],
//           },

//         ];
//         icon.push(...icon_manager)
//         this.icon_handleBar = icon;
//         break;

//       case "Employee":
//         const icon_employee = [
//           {
//             iconName: "calendar_month",
//             path: "/home/user/attendance",
//             task: [{ name: "Quản Lý Chấm Công", path: "/home/user/attendance" }, { name: "Lịch Làm Việc", path: "/home/schedule" }]
//           },
//           {
//             iconName: "beach_access",
//             path: "/home/leaverequest",
//             task: [{ name: "Nghỉ Phép", path: "/home/leaverequest" }]
//           },
//           {
//             iconName: "payments",
//             path: "/home/payroll/payslip",
//             task: [{ name: "Xem Lương", path: "/home/payroll/payslip" }],
//           },
//         ];
//         icon.push(...icon_employee)
//         this.icon_handleBar = icon;
//         break;
//     }
//   }