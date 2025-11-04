-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1:3306
-- Thời gian đã tạo: Th10 04, 2025 lúc 03:09 PM
-- Phiên bản máy phục vụ: 8.2.0
-- Phiên bản PHP: 8.2.13

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `manaplastic_hr`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `activitylogs`
--

DROP TABLE IF EXISTS `activitylogs`;
CREATE TABLE IF NOT EXISTS `activitylogs` (
  `logID` int NOT NULL AUTO_INCREMENT,
  `action` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `actiontime` datetime DEFAULT CURRENT_TIMESTAMP,
  `userID` int DEFAULT NULL,
  PRIMARY KEY (`logID`),
  KEY `userID` (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `attendancelogs`
--

DROP TABLE IF EXISTS `attendancelogs`;
CREATE TABLE IF NOT EXISTS `attendancelogs` (
  `attendanceLogID` int NOT NULL AUTO_INCREMENT,
  `timestamp` datetime NOT NULL,
  `img_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `userID` int DEFAULT NULL,
  PRIMARY KEY (`attendanceLogID`),
  KEY `userID` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `attendancelogs`
--

INSERT INTO `attendancelogs` (`attendanceLogID`, `timestamp`, `img_url`, `userID`) VALUES
(1, '2025-11-01 07:58:15', 'https://img.url/checkin_6_1.jpg', 6),
(2, '2025-11-01 17:02:30', 'https://img.url/checkout_6_1.jpg', 6),
(3, '2025-11-01 05:55:10', 'https://img.url/checkin_8_1.jpg', 8),
(4, '2025-11-01 11:30:00', 'https://img.url/scan_mid.jpg', 8),
(5, '2025-11-01 14:01:00', 'https://img.url/checkout_8_1.jpg', 8),
(6, '2025-11-01 08:22:45', 'https://img.url/checkin_12_1.jpg', 12),
(7, '2025-11-01 17:01:00', 'https://img.url/checkout_12_1.jpg', 12),
(8, '2025-11-01 08:10:00', 'https://img.url/checkin_5_1.jpg', 5),
(9, '2025-11-01 16:45:00', 'https://img.url/checkout_5_1.jpg', 5),
(10, '2025-11-02 05:59:00', 'https://img.url/checkin_8_3.jpg', 8),
(11, '2025-11-02 17:05:00', 'https://img.url/checkout_9_1.jpg', 9);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `attendances`
--

DROP TABLE IF EXISTS `attendances`;
CREATE TABLE IF NOT EXISTS `attendances` (
  `attendanceID` int NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL,
  `checkin` datetime DEFAULT NULL,
  `checkout` datetime DEFAULT NULL,
  `checkin_img_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `checkout_img_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('PRESENT','ABSENT','LATE_AND_EARLY','ON_LEAVE','MISSING_OUTPUT_DATA','MISSING_INPUT_DATA') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ABSENT',
  `shiftID` int DEFAULT NULL,
  `userID` int DEFAULT NULL,
  PRIMARY KEY (`attendanceID`),
  UNIQUE KEY `UQ_User_Date` (`userID`,`date`),
  KEY `shiftID` (`shiftID`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `attendances`
--

INSERT INTO `attendances` (`attendanceID`, `date`, `checkin`, `checkout`, `checkin_img_url`, `checkout_img_url`, `status`, `shiftID`, `userID`) VALUES
(6, '2025-11-01', '2025-11-01 07:58:15', '2025-11-01 17:02:30', 'https://img.url/checkin_6_1.jpg', 'https://img.url/checkout_6_1.jpg', 'PRESENT', 1, 6),
(7, '2025-11-01', '2025-11-01 05:55:10', '2025-11-01 14:01:00', 'https://img.url/checkin_8_1.jpg', 'https://img.url/checkout_8_1.jpg', 'PRESENT', 2, 8),
(8, '2025-11-01', '2025-11-01 08:22:45', '2025-11-01 17:01:00', 'https://img.url/checkin_12_1.jpg', 'https://img.url/checkout_12_1.jpg', 'PRESENT', 1, 12),
(9, '2025-11-01', '2025-11-01 08:10:00', '2025-11-01 16:45:00', 'https://img.url/checkin_5_1.jpg', 'https://img.url/checkout_5_1.jpg', 'PRESENT', 1, 5),
(10, '2025-11-02', '2025-11-02 05:59:00', NULL, 'https://img.url/checkin_8_3.jpg', NULL, 'MISSING_OUTPUT_DATA', 2, 8),
(11, '2025-11-02', NULL, '2025-11-02 17:05:00', NULL, 'https://img.url/checkout_9_1.jpg', 'MISSING_INPUT_DATA', 1, 9),
(12, '2025-11-02', NULL, NULL, NULL, NULL, 'ABSENT', 1, 6),
(13, '2025-11-02', NULL, NULL, NULL, NULL, 'ON_LEAVE', 1, 10),
(14, '2025-11-04', '2025-11-04 08:00:00', '2025-11-04 17:00:00', NULL, NULL, 'PRESENT', 1, 6),
(15, '2025-11-04', '2025-11-04 08:30:00', NULL, NULL, NULL, 'MISSING_OUTPUT_DATA', 2, 8);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `contracts`
--

DROP TABLE IF EXISTS `contracts`;
CREATE TABLE IF NOT EXISTS `contracts` (
  `contractID` int NOT NULL AUTO_INCREMENT,
  `contractname` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `basesalary` decimal(15,2) NOT NULL,
  `fileurl` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `signdate` date NOT NULL,
  `startdate` date NOT NULL,
  `enddate` date DEFAULT NULL,
  `userID` int NOT NULL,
  PRIMARY KEY (`contractID`),
  UNIQUE KEY `userID` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `contracts`
--

INSERT INTO `contracts` (`contractID`, `contractname`, `type`, `basesalary`, `fileurl`, `signdate`, `startdate`, `enddate`, `userID`) VALUES
(1, 'HĐLĐ Admin', 'Full-time', 30000000.00, NULL, '2023-01-01', '2023-01-01', NULL, 1),
(2, 'HĐLĐ IT', 'Full-time', 15000000.00, NULL, '2023-05-10', '2023-05-10', NULL, 2),
(3, 'HĐLĐ Trưởng phòng HR', 'Full-time', 25000000.00, NULL, '2023-02-15', '2023-02-15', NULL, 3),
(4, 'HĐLĐ Nhân viên HR', 'Full-time', 12000000.00, NULL, '2023-08-01', '2023-08-01', NULL, 4),
(5, 'HĐLĐ Trưởng nhóm Kỹ thuật', 'Full-time', 22000000.00, NULL, '2023-03-01', '2023-03-01', NULL, 5),
(6, 'HĐLĐ Nhân viên Kỹ thuật', 'Full-time', 10000000.00, NULL, '2023-09-10', '2023-09-10', NULL, 6),
(7, 'HĐLĐ Trưởng ca Sản xuất', 'Full-time', 20000000.00, NULL, '2023-03-02', '2023-03-02', NULL, 7),
(8, 'HĐLĐ Công nhân Sản xuất', 'Full-time', 9000000.00, NULL, '2023-09-15', '2023-09-15', NULL, 8),
(9, 'HĐLĐ Trưởng nhóm In ấn', 'Full-time', 18000000.00, NULL, '2023-04-01', '2023-04-01', NULL, 9),
(10, 'HĐLĐ Công nhân In ấn', 'Full-time', 9000000.00, NULL, '2023-10-01', '2023-10-01', NULL, 10),
(11, 'HĐLĐ Trưởng nhóm CSKH', 'Full-time', 17000000.00, NULL, '2023-05-01', '2023-05-01', NULL, 11),
(12, 'HĐLĐ Nhân viên CSKH', 'Full-time', 8000000.00, NULL, '2023-11-01', '2023-11-01', NULL, 12);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `departments`
--

DROP TABLE IF EXISTS `departments`;
CREATE TABLE IF NOT EXISTS `departments` (
  `departmentID` int NOT NULL AUTO_INCREMENT,
  `departmentname` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `managerID` int DEFAULT NULL,
  PRIMARY KEY (`departmentID`),
  KEY `FK_Department_Manager` (`managerID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `departments`
--

INSERT INTO `departments` (`departmentID`, `departmentname`, `managerID`) VALUES
(1, 'Phòng Ban Nhân Sự', 3),
(2, 'Phòng Ban IT', 1),
(3, 'Phòng Ban Kỹ Thuật', 5),
(4, 'Phòng Ban Sản Xuất', 7),
(5, 'Phòng Ban In Ấn', 9),
(6, 'Phòng Ban Chăm Sóc Khách Hàng', 11);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `leaverequests`
--

DROP TABLE IF EXISTS `leaverequests`;
CREATE TABLE IF NOT EXISTS `leaverequests` (
  `leaverequestID` int NOT NULL AUTO_INCREMENT,
  `leavetype` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `startdate` date NOT NULL,
  `enddate` date NOT NULL,
  `reason` text COLLATE utf8mb4_unicode_ci,
  `status` enum('pending','approved','rejected') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending',
  `requestdate` date DEFAULT NULL,
  `userID` int DEFAULT NULL,
  PRIMARY KEY (`leaverequestID`),
  KEY `userID` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `leaverequests`
--

INSERT INTO `leaverequests` (`leaverequestID`, `leavetype`, `startdate`, `enddate`, `reason`, `status`, `requestdate`, `userID`) VALUES
(1, 'Nghỉ ốm', '2025-10-29', '2025-10-29', 'Bị sốt', 'approved', '2025-10-28', 6),
(2, 'Nghỉ phép năm', '2025-11-05', '2025-11-06', 'Gia đình có việc', 'pending', '2025-10-28', 12),
(3, 'Nghỉ phép năm', '2025-10-20', '2025-10-20', 'Việc cá nhân', 'rejected', '2025-10-19', 8);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `payrolldetails`
--

DROP TABLE IF EXISTS `payrolldetails`;
CREATE TABLE IF NOT EXISTS `payrolldetails` (
  `paydetailID` int NOT NULL AUTO_INCREMENT,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` enum('bonus','deduction','allowance') COLLATE utf8mb4_unicode_ci NOT NULL,
  `amount` decimal(15,2) NOT NULL,
  `payID` int DEFAULT NULL,
  PRIMARY KEY (`paydetailID`),
  KEY `payID` (`payID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `payrolldetails`
--

INSERT INTO `payrolldetails` (`paydetailID`, `description`, `type`, `amount`, `payID`) VALUES
(1, 'Thưởng chuyên cần', 'bonus', 500000.00, 1),
(2, 'Thưởng vượt năng suất', 'bonus', 200000.00, 2),
(3, 'Phạt đi trễ', 'deduction', 100000.00, 2);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `payrolls`
--

DROP TABLE IF EXISTS `payrolls`;
CREATE TABLE IF NOT EXISTS `payrolls` (
  `payID` int NOT NULL AUTO_INCREMENT,
  `basesalary` decimal(15,2) NOT NULL,
  `bonus` decimal(15,2) DEFAULT '0.00',
  `penalty` decimal(15,2) DEFAULT '0.00',
  `hoursofwork` float DEFAULT NULL,
  `VAT` decimal(15,2) DEFAULT '0.00',
  `netsalary` decimal(15,2) NOT NULL,
  `payperiod` varchar(7) COLLATE utf8mb4_unicode_ci NOT NULL,
  `userID` int DEFAULT NULL,
  PRIMARY KEY (`payID`),
  UNIQUE KEY `UQ_User_Period` (`userID`,`payperiod`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `payrolls`
--

INSERT INTO `payrolls` (`payID`, `basesalary`, `bonus`, `penalty`, `hoursofwork`, `VAT`, `netsalary`, `payperiod`, `userID`) VALUES
(1, 10000000.00, 500000.00, 0.00, 176, 0.00, 10500000.00, '2025-09', 6),
(2, 9000000.00, 200000.00, 100000.00, 180, 0.00, 9100000.00, '2025-09', 8);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `permissions`
--

DROP TABLE IF EXISTS `permissions`;
CREATE TABLE IF NOT EXISTS `permissions` (
  `permissionID` int NOT NULL AUTO_INCREMENT,
  `permissionname` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`permissionID`),
  UNIQUE KEY `permissionname` (`permissionname`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `permissions`
--

INSERT INTO `permissions` (`permissionID`, `permissionname`, `description`) VALUES
(1, 'manage_all_users', 'Tạo, sửa, xóa tất cả người dùng'),
(2, 'manage_department_users', 'Quản lý người dùng trong phòng ban của mình'),
(3, 'manage_payroll', 'Quản lý, tính toán lương cho mọi người'),
(4, 'view_own_payroll', 'Chỉ xem được bảng lương của bản thân'),
(5, 'manage_leave_requests', 'Duyệt/từ chối đơn nghỉ phép của mọi người'),
(6, 'approve_leave_requests', 'Duyệt/tối chối đơn của phòng ban mình'),
(7, 'request_leave', 'Tạo đơn xin nghỉ phép'),
(8, 'manage_attendance', 'Quản lý chấm công của mọi người'),
(9, 'view_own_attendance', 'Xem chấm công của bản thân');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `roles`
--

DROP TABLE IF EXISTS `roles`;
CREATE TABLE IF NOT EXISTS `roles` (
  `roleID` int NOT NULL AUTO_INCREMENT,
  `rolename` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`roleID`),
  UNIQUE KEY `rolename` (`rolename`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `roles`
--

INSERT INTO `roles` (`roleID`, `rolename`) VALUES
(1, 'Admin'),
(4, 'Employee'),
(2, 'HR'),
(3, 'Manager');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `rolespermissions`
--

DROP TABLE IF EXISTS `rolespermissions`;
CREATE TABLE IF NOT EXISTS `rolespermissions` (
  `roleID` int NOT NULL,
  `permissionID` int NOT NULL,
  PRIMARY KEY (`roleID`,`permissionID`),
  KEY `permissionID` (`permissionID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `rolespermissions`
--

INSERT INTO `rolespermissions` (`roleID`, `permissionID`) VALUES
(1, 1),
(2, 1),
(3, 2),
(1, 3),
(2, 3),
(2, 4),
(3, 4),
(4, 4),
(1, 5),
(2, 5),
(3, 6),
(2, 7),
(3, 7),
(4, 7),
(1, 8),
(2, 8),
(2, 9),
(3, 9),
(4, 9);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `shifts`
--

DROP TABLE IF EXISTS `shifts`;
CREATE TABLE IF NOT EXISTS `shifts` (
  `shiftID` int NOT NULL AUTO_INCREMENT,
  `shiftname` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `starttime` time NOT NULL,
  `endtime` time NOT NULL,
  PRIMARY KEY (`shiftID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `shifts`
--

INSERT INTO `shifts` (`shiftID`, `shiftname`, `starttime`, `endtime`) VALUES
(1, 'Hành chính', '08:00:00', '17:00:00'),
(2, 'Ca Sáng (SX)', '06:00:00', '14:00:00'),
(3, 'Ca Chiều (SX)', '14:00:00', '22:00:00'),
(4, 'Ca Đêm (SX)', '22:00:00', '06:00:00');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `userpermissions`
--

DROP TABLE IF EXISTS `userpermissions`;
CREATE TABLE IF NOT EXISTS `userpermissions` (
  `userID` int NOT NULL,
  `permissionID` int NOT NULL,
  `activepermission` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`userID`,`permissionID`),
  KEY `permissionID` (`permissionID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `userID` int NOT NULL AUTO_INCREMENT,
  `username` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fullname` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cccd` bigint DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phonenumber` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `birth` date DEFAULT NULL,
  `gender` tinyint(1) DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bankaccount` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bankname` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hiredate` date DEFAULT NULL,
  `status` enum('active','inactive','on_leave') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active',
  `roleID` int DEFAULT NULL,
  `departmentID` int DEFAULT NULL,
  PRIMARY KEY (`userID`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `roleID` (`roleID`),
  KEY `departmentID` (`departmentID`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`userID`, `username`, `password`, `fullname`, `cccd`, `email`, `phonenumber`, `birth`, `gender`, `address`, `bankaccount`, `bankname`, `hiredate`, `status`, `roleID`, `departmentID`) VALUES
(1, 'admin', '$2a$10$2sQzJxjvMcMeSNOSsysqjOQZzWIpwvHKIdwdeZ.EqQDM6QKcufj0q', 'Phạm Minh Anh', 123456789874, 'admin@manaplastic.com', NULL, NULL, NULL, NULL, NULL, NULL, '2023-01-01', 'active', 1, 2),
(2, 'it_support', '$2a$10$skyfJgN4n.Z2GMTP7GLnneUFL4cSm1DWoJdSsYGvF06flQTGz1GBC', 'Lê Hỗ Trợ IT', NULL, 'it.support@manaplastic.com', NULL, NULL, NULL, NULL, NULL, NULL, '2023-05-10', 'active', 1, 2),
(3, 'hr_manager', '$2a$10$yVs4Kv0e36Kcb8wesofM3enjSu/Kicj5TFJm6YavsG5TDd2kLtsqy', 'Nguyễn Thị Nhân Sự', NULL, 'hr.manager@manaplastic.com', NULL, NULL, NULL, NULL, NULL, NULL, '2023-02-15', 'active', 2, 1),
(4, 'hr_staff', '$2a$10$wnTMZHPSgAkKLfwNCY3cE.ufDKVPHrdWvaJ5oL.o0dKj7kxBMJNXG', 'Phạm Văn Tuyển Dụng', NULL, 'hr.staff@manaplastic.com', NULL, NULL, NULL, NULL, NULL, NULL, '2023-08-01', 'active', 2, 1),
(5, 'kythuat_lead', '$2a$10$QP/JCd6cnNgRkFr3BYzbKOSRMIbdI6Y1fE6D.w.p1xfbvQV9y5UJe', 'Võ Văn Kỹ Thuật', NULL, 'kythuat.lead@manaplastic.com', NULL, NULL, NULL, NULL, NULL, NULL, '2023-03-01', 'active', 3, 3),
(6, 'kythuat_staff', '$2a$10$TXgpVb3lT7f/1YQxy2i/he2wiepriI6XqcPO2WcxG2xBLt0V9T8HO', 'Hoàng Thị Máy Móc', NULL, 'kythuat.staff@manaplastic.com', NULL, NULL, NULL, NULL, NULL, NULL, '2023-09-10', 'active', 4, 3),
(7, 'sanxuat_lead', '$2a$10$4b25FwYYUWVDgrItKmyyJ.ntqK6S6SJFqLHabuDL.xsoP/yScAov.', 'Trịnh Hữu Sản Xuất', NULL, 'sanxuat.lead@manaplastic.com', NULL, NULL, NULL, NULL, NULL, NULL, '2023-03-02', 'active', 3, 4),
(8, 'sanxuat_staff', '$2a$10$TxHjoxC0uVKLIDznDhNWmuF73p7LTpVr1Lf1uGdn1lw2lo8gR0ApS', 'Đặng Văn Vận Hành', NULL, 'sanxuat.staff@manaplastic.com', NULL, NULL, NULL, NULL, NULL, NULL, '2023-09-15', 'active', 4, 4),
(9, 'inan_lead', '$2a$10$6v5RYvb1wRZZ333NkxGDLuVRAgXeG0xHcfsxiBQHV0tXRpvai2yYS', 'Bùi Văn Mực', NULL, 'inan.lead@manaplastic.com', NULL, NULL, NULL, NULL, NULL, NULL, '2023-04-01', 'active', 3, 5),
(10, 'inan_staff', '$2a$10$JN0oA3nYZmxvy4FXcXyMHuTBXs4xWWzcoFkXTgSbwgoqyHGmEG3Pm', 'Lý Thị In', NULL, 'inan.staff@manaplastic.com', NULL, NULL, NULL, NULL, NULL, NULL, '2023-10-01', 'active', 4, 5),
(11, 'cskh_lead', '$2a$10$bm/qxbS1udqc01zrB/p/HuNdfz1kH9oFBVpfuLoTaGFLZKEuysMF.', 'Đỗ Thị Khách Hàng', NULL, 'cskh.lead@manaplastic.com', NULL, NULL, NULL, NULL, NULL, NULL, '2023-05-01', 'active', 3, 6),
(12, 'cskh_staff', '$2a$10$tHV/qqOG68rkmYyYL82LIe5v1hnm.lzY.SlZclzOZoV6j13l6LtD.', 'Mạc Văn Hài Lòng', NULL, 'cskh.staff@manaplastic.com', NULL, NULL, NULL, NULL, NULL, NULL, '2023-11-01', 'active', 4, 6),
(13, '57540101', '$2a$10$xPIdXJigdb91ZNCAFFtTu.4RLJpIYAaQGJ70VlaE2wSblfQznOlDi', 'Phạm Minh Anh HR', 123456789876, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'active', 2, 1),
(14, '52082901', '$2a$10$jeaJPcpV3IQMoxGHBLw2teRExwzuD0ZB9ubxat7yzrqgAgfWn.CvS', 'testAddAccountHR', 79203031165, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'active', 2, 1),
(15, '71939801', '$2a$10$U0qh1Bel43Hp2K6CbsOCXefQQRgRqPJMU5Sah3eIflWTGeSkZlrPG', 'testAddAccountNV', 123456789000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'active', 4, 5);

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `activitylogs`
--
ALTER TABLE `activitylogs`
  ADD CONSTRAINT `activitylogs_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `attendancelogs`
--
ALTER TABLE `attendancelogs`
  ADD CONSTRAINT `attendancelogs_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `attendances`
--
ALTER TABLE `attendances`
  ADD CONSTRAINT `attendances_ibfk_1` FOREIGN KEY (`shiftID`) REFERENCES `shifts` (`shiftID`) ON UPDATE CASCADE,
  ADD CONSTRAINT `attendances_ibfk_2` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `contracts`
--
ALTER TABLE `contracts`
  ADD CONSTRAINT `contracts_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `departments`
--
ALTER TABLE `departments`
  ADD CONSTRAINT `FK_Department_Manager` FOREIGN KEY (`managerID`) REFERENCES `users` (`userID`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `leaverequests`
--
ALTER TABLE `leaverequests`
  ADD CONSTRAINT `leaverequests_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `payrolldetails`
--
ALTER TABLE `payrolldetails`
  ADD CONSTRAINT `payrolldetails_ibfk_1` FOREIGN KEY (`payID`) REFERENCES `payrolls` (`payID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `payrolls`
--
ALTER TABLE `payrolls`
  ADD CONSTRAINT `payrolls_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `rolespermissions`
--
ALTER TABLE `rolespermissions`
  ADD CONSTRAINT `rolespermissions_ibfk_1` FOREIGN KEY (`roleID`) REFERENCES `roles` (`roleID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `rolespermissions_ibfk_2` FOREIGN KEY (`permissionID`) REFERENCES `permissions` (`permissionID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `userpermissions`
--
ALTER TABLE `userpermissions`
  ADD CONSTRAINT `userpermissions_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `userpermissions_ibfk_2` FOREIGN KEY (`permissionID`) REFERENCES `permissions` (`permissionID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`roleID`) REFERENCES `roles` (`roleID`) ON UPDATE CASCADE,
  ADD CONSTRAINT `users_ibfk_2` FOREIGN KEY (`departmentID`) REFERENCES `departments` (`departmentID`) ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
