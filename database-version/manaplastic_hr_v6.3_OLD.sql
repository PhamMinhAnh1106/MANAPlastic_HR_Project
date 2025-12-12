-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1:3306
-- Thời gian đã tạo: Th12 11, 2025 lúc 11:28 AM
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
  `logType` enum('INFO','WARNING','ERROR','DANGER') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'INFO',
  `details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `actiontime` datetime DEFAULT CURRENT_TIMESTAMP,
  `userID` int DEFAULT NULL,
  PRIMARY KEY (`logID`),
  KEY `userID` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `activitylogs`
--

INSERT INTO `activitylogs` (`logID`, `action`, `logType`, `details`, `actiontime`, `userID`) VALUES
(1, 'SELF_APPROVAL_SCHEDULE', 'WARNING', 'CẢNH BÁO: Quản lý Bùi Văn Mực đã tự duyệt yêu cầu đổi ca. Lý do: Em bận việc gia đình buổi sáng nên xin đổi sang ca chiều', '2025-12-11 09:54:17', 9),
(2, 'SELF_APPROVAL_SCHEDULE', 'WARNING', 'CẢNH BÁO: Quản lý Bùi Văn Mực đã tự duyệt yêu cầu đổi ca. Lý do: Em bận việc gia đình buổi sáng nên xin đổi sang ca sáng', '2025-12-11 09:55:01', 9);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `attendancelogs`
--

DROP TABLE IF EXISTS `attendancelogs`;
CREATE TABLE IF NOT EXISTS `attendancelogs` (
  `attendanceLogID` int NOT NULL AUTO_INCREMENT,
  `timestamp` datetime NOT NULL,
  `imgurl` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `userID` int DEFAULT NULL,
  PRIMARY KEY (`attendanceLogID`),
  KEY `userID` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `attendancelogs`
--

INSERT INTO `attendancelogs` (`attendanceLogID`, `timestamp`, `imgurl`, `userID`) VALUES
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
(11, '2025-11-02 17:05:00', 'https://img.url/checkout_9_1.jpg', 9),
(16, '2025-11-19 11:00:12', '/uploads/47358d64-7cf1-4a33-8e1c-c5efbb7553d0.jpg', 20),
(17, '2025-11-19 11:02:27', '/uploads/238acd2f-fd35-46f8-a36f-c4bfbe93b13d.jpg', 20);

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
  `checkinimgurl` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `checkoutimgurl` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('PRESENT','ABSENT','LATE_AND_EARLY','ON_LEAVE','MISSING_OUTPUT_DATA','MISSING_INPUT_DATA') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ABSENT',
  `shiftID` int DEFAULT NULL,
  `userID` int DEFAULT NULL,
  `checkInLogID` int DEFAULT NULL,
  `checkOutLogID` int DEFAULT NULL,
  PRIMARY KEY (`attendanceID`),
  UNIQUE KEY `UQ_User_Date` (`userID`,`date`),
  KEY `shiftID` (`shiftID`),
  KEY `FK_Attendance_CheckInLog` (`checkInLogID`),
  KEY `FK_Attendance_CheckOutLog` (`checkOutLogID`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `attendances`
--

INSERT INTO `attendances` (`attendanceID`, `date`, `checkin`, `checkout`, `checkinimgurl`, `checkoutimgurl`, `status`, `shiftID`, `userID`, `checkInLogID`, `checkOutLogID`) VALUES
(6, '2025-11-01', '2025-11-01 07:58:15', '2025-11-01 17:02:30', 'https://img.url/checkin_6_1.jpg', 'https://img.url/checkout_6_1.jpg', 'PRESENT', 1, 6, 1, 2),
(7, '2025-11-01', '2025-11-01 05:55:10', '2025-11-01 14:01:00', 'https://img.url/checkin_8_1.jpg', 'https://img.url/checkout_8_1.jpg', 'PRESENT', 2, 8, 3, 5),
(8, '2025-11-01', '2025-11-01 08:22:45', '2025-11-01 17:01:00', 'https://img.url/checkin_12_1.jpg', 'https://img.url/checkout_12_1.jpg', 'PRESENT', 1, 12, 6, 7),
(9, '2025-11-01', '2025-11-01 08:10:00', '2025-11-01 16:45:00', 'https://img.url/checkin_5_1.jpg', 'https://img.url/checkout_5_1.jpg', 'PRESENT', 1, 5, 8, 9),
(10, '2025-11-02', '2025-11-02 05:59:00', NULL, 'https://img.url/checkin_8_3.jpg', NULL, 'MISSING_OUTPUT_DATA', 2, 8, 10, NULL),
(11, '2025-11-02', NULL, '2025-11-02 17:05:00', NULL, 'https://img.url/checkout_9_1.jpg', 'MISSING_INPUT_DATA', 1, 9, NULL, 11),
(12, '2025-11-02', NULL, NULL, NULL, NULL, 'ABSENT', 1, 6, NULL, NULL),
(13, '2025-11-02', NULL, NULL, NULL, NULL, 'ON_LEAVE', 1, 10, NULL, NULL),
(14, '2025-11-04', '2025-11-04 08:00:00', '2025-11-04 17:00:00', NULL, NULL, 'PRESENT', 1, 6, NULL, NULL),
(15, '2025-11-04', '2025-11-04 08:30:00', NULL, NULL, NULL, 'MISSING_OUTPUT_DATA', 2, 8, NULL, NULL),
(21, '2025-11-01', '2025-11-01 07:55:00', '2025-11-01 17:05:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(22, '2025-11-03', '2025-11-03 07:50:00', '2025-11-03 17:00:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(23, '2025-11-04', '2025-11-04 07:55:00', '2025-11-04 17:10:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(24, '2025-11-05', '2025-11-05 07:58:00', '2025-11-05 17:05:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(25, '2025-11-06', '2025-11-06 07:45:00', '2025-11-06 17:00:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(26, '2025-11-07', '2025-11-07 07:55:00', '2025-11-07 17:05:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(27, '2025-11-08', '2025-11-08 07:59:00', '2025-11-08 17:00:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(28, '2025-11-10', '2025-11-10 07:55:00', '2025-11-10 17:05:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(29, '2025-11-11', '2025-11-11 07:52:00', '2025-11-11 17:00:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(30, '2025-11-12', '2025-11-12 07:55:00', '2025-11-12 17:15:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(31, '2025-11-13', '2025-11-13 08:00:00', '2025-11-13 17:05:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(32, '2025-11-14', '2025-11-14 07:55:00', '2025-11-14 17:00:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(33, '2025-11-15', '2025-11-15 07:55:00', '2025-11-15 17:05:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(34, '2025-11-17', '2025-11-17 07:50:00', '2025-11-17 17:00:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(35, '2025-11-18', '2025-11-18 07:55:00', '2025-11-18 17:10:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(36, '2025-11-19', '2025-11-19 07:55:00', '2025-11-19 17:05:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(37, '2025-11-20', '2025-11-20 07:45:00', '2025-11-20 17:00:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(38, '2025-11-21', '2025-11-21 07:55:00', '2025-11-21 17:05:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(39, '2025-11-22', '2025-11-22 07:59:00', '2025-11-22 17:00:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(40, '2025-11-24', '2025-11-24 07:55:00', '2025-11-24 17:05:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(41, '2025-11-25', '2025-11-25 07:52:00', '2025-11-25 17:00:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(42, '2025-11-26', '2025-11-26 07:55:00', '2025-11-26 17:15:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(43, '2025-11-27', '2025-11-27 08:00:00', '2025-11-27 17:05:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(44, '2025-11-28', '2025-11-28 07:55:00', '2025-11-28 17:00:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(45, '2025-11-29', '2025-11-29 07:55:00', '2025-11-29 17:05:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(46, '2025-11-30', '2025-11-30 07:55:00', '2025-11-30 17:05:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `contractallowances`
--

DROP TABLE IF EXISTS `contractallowances`;
CREATE TABLE IF NOT EXISTS `contractallowances` (
  `ContractAllowanceID` int NOT NULL AUTO_INCREMENT,
  `ContractID` int NOT NULL,
  `AllowanceName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Tên phụ cấp',
  `AllowanceType` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'OTHER' COMMENT 'Loại phụ cấp: MEAL, RESPONSIBILITY, TOXIC, PHONE, OTHER',
  `Amount` decimal(15,2) NOT NULL DEFAULT '0.00',
  `IsTaxable` tinyint(1) DEFAULT '1' COMMENT '1=Chịu thuế, 0=Không',
  `IsInsuranceBase` tinyint(1) DEFAULT '0' COMMENT '1=Tính đóng BHXH, 0=Không',
  `TaxFreeAmount` decimal(15,2) DEFAULT '0.00' COMMENT 'Số tiền miễn thuế trong khoản phụ cấp này',
  PRIMARY KEY (`ContractAllowanceID`),
  KEY `FK_Allowance_Contract` (`ContractID`),
  KEY `idx_allowance_type` (`AllowanceType`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `contractallowances`
--

INSERT INTO `contractallowances` (`ContractAllowanceID`, `ContractID`, `AllowanceName`, `AllowanceType`, `Amount`, `IsTaxable`, `IsInsuranceBase`, `TaxFreeAmount`) VALUES
(1, 9, 'Phụ cấp trách nhiệm', 'RESPONSIBILITY', 2000000.00, 1, 1, 0.00),
(2, 9, 'Hỗ trợ ăn ca', 'MEAL', 730000.00, 0, 0, 730000.00),
(3, 9, 'Phụ cấp độc hại (Tiền mặt)', 'TOXIC', 500000.00, 1, 0, 500000.00),
(4, 1, 'Hỗ trợ ăn ca', 'MEAL', 730000.00, 0, 0, 730000.00),
(5, 2, 'Hỗ trợ ăn ca', 'MEAL', 730000.00, 0, 0, 730000.00),
(6, 3, 'Hỗ trợ ăn ca', 'MEAL', 730000.00, 0, 0, 730000.00),
(7, 4, 'Hỗ trợ ăn ca', 'MEAL', 730000.00, 0, 0, 730000.00),
(8, 5, 'Hỗ trợ ăn ca', 'MEAL', 730000.00, 0, 0, 730000.00),
(9, 6, 'Hỗ trợ ăn ca', 'MEAL', 730000.00, 0, 0, 730000.00),
(10, 7, 'Hỗ trợ ăn ca', 'MEAL', 730000.00, 0, 0, 730000.00),
(11, 8, 'Hỗ trợ ăn ca', 'MEAL', 730000.00, 0, 0, 730000.00),
(12, 9, 'Hỗ trợ ăn ca', 'MEAL', 730000.00, 0, 0, 730000.00),
(13, 10, 'Hỗ trợ ăn ca', 'MEAL', 730000.00, 0, 0, 730000.00),
(14, 11, 'Hỗ trợ ăn ca', 'MEAL', 730000.00, 0, 0, 730000.00),
(15, 12, 'Hỗ trợ ăn ca', 'MEAL', 730000.00, 0, 0, 730000.00),
(16, 13, 'Hỗ trợ ăn ca', 'MEAL', 730000.00, 0, 0, 730000.00),
(17, 14, 'Hỗ trợ ăn ca', 'MEAL', 730000.00, 0, 0, 730000.00),
(18, 15, 'Hỗ trợ ăn ca', 'MEAL', 730000.00, 0, 0, 730000.00),
(19, 16, 'Hỗ trợ ăn ca', 'MEAL', 730000.00, 0, 0, 730000.00),
(20, 17, 'Hỗ trợ ăn ca', 'MEAL', 730000.00, 0, 0, 730000.00),
(21, 18, 'Hỗ trợ ăn ca', 'MEAL', 730000.00, 0, 0, 730000.00),
(22, 19, 'Hỗ trợ ăn ca', 'MEAL', 730000.00, 0, 0, 730000.00),
(23, 20, 'Hỗ trợ ăn ca', 'MEAL', 730000.00, 0, 0, 730000.00),
(35, 1, 'Phụ cấp trách nhiệm', 'RESPONSIBILITY', 3000000.00, 1, 1, 0.00),
(36, 3, 'Phụ cấp trách nhiệm', 'RESPONSIBILITY', 3000000.00, 1, 1, 0.00),
(37, 5, 'Phụ cấp trách nhiệm', 'RESPONSIBILITY', 3000000.00, 1, 1, 0.00),
(38, 7, 'Phụ cấp trách nhiệm', 'RESPONSIBILITY', 3000000.00, 1, 1, 0.00),
(39, 9, 'Phụ cấp trách nhiệm', 'RESPONSIBILITY', 3000000.00, 1, 1, 0.00),
(40, 11, 'Phụ cấp trách nhiệm', 'RESPONSIBILITY', 3000000.00, 1, 1, 0.00),
(42, 6, 'Phụ cấp độc hại (Tiền mặt)', 'TOXIC', 500000.00, 1, 0, 500000.00),
(43, 8, 'Phụ cấp độc hại (Tiền mặt)', 'TOXIC', 500000.00, 1, 0, 500000.00),
(44, 17, 'Phụ cấp độc hại (Tiền mặt)', 'TOXIC', 500000.00, 1, 0, 500000.00),
(45, 18, 'Phụ cấp độc hại (Tiền mặt)', 'TOXIC', 500000.00, 1, 0, 500000.00),
(49, 4, 'Hỗ trợ điện thoại & Xăng xe', 'MEAL', 400000.00, 1, 0, 0.00),
(50, 11, 'Hỗ trợ điện thoại & Xăng xe', 'MEAL', 400000.00, 1, 0, 0.00),
(51, 12, 'Hỗ trợ điện thoại & Xăng xe', 'MEAL', 400000.00, 1, 0, 0.00),
(52, 13, 'Hỗ trợ điện thoại & Xăng xe', 'MEAL', 400000.00, 1, 0, 0.00),
(53, 14, 'Hỗ trợ điện thoại & Xăng xe', 'MEAL', 400000.00, 1, 0, 0.00),
(54, 19, 'Hỗ trợ điện thoại & Xăng xe', 'MEAL', 400000.00, 1, 0, 0.00),
(56, 10, 'Hỗ trợ ăn ca', 'MEAL', 730000.00, 0, 0, 730000.00);

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
  `InsuranceSalary` decimal(15,2) DEFAULT '0.00' COMMENT 'Lương đóng BHXH',
  `AllowanceToxicType` enum('NONE','CASH','IN_KIND') COLLATE utf8mb4_unicode_ci DEFAULT 'NONE' COMMENT 'Phụ cấp độc hại',
  `fileurl` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `signdate` date NOT NULL,
  `startdate` date NOT NULL,
  `enddate` date DEFAULT NULL,
  `Status` enum('DRAFT','ACTIVE','EXPIRING_SOON','EXPIRED','TERMINATED','HISTORY') COLLATE utf8mb4_unicode_ci DEFAULT 'DRAFT' COMMENT 'Trạng thái hợp đồng',
  `userID` int NOT NULL,
  PRIMARY KEY (`contractID`),
  KEY `IDX_Contract_User` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `contracts`
--

INSERT INTO `contracts` (`contractID`, `contractname`, `type`, `basesalary`, `InsuranceSalary`, `AllowanceToxicType`, `fileurl`, `signdate`, `startdate`, `enddate`, `Status`, `userID`) VALUES
(1, 'HĐLĐ Admin', 'INDEFINITE', 30000000.00, 30000000.00, 'NONE', NULL, '2023-01-01', '2023-01-01', NULL, 'ACTIVE', 1),
(2, 'HĐLĐ IT', 'FIXED_TERM', 15000000.00, 15000000.00, 'NONE', NULL, '2023-05-10', '2023-05-10', '2026-05-10', 'ACTIVE', 2),
(3, 'HĐLĐ Trưởng phòng HR', 'INDEFINITE', 25000000.00, 25000000.00, 'NONE', NULL, '2023-02-15', '2023-02-15', NULL, 'ACTIVE', 3),
(4, 'HĐLĐ Nhân viên HR', 'FIXED_TERM', 12000000.00, 12000000.00, 'NONE', NULL, '2023-08-01', '2023-08-01', '2026-08-01', 'ACTIVE', 4),
(5, 'HĐLĐ Trưởng nhóm Kỹ thuật', 'FIXED_TERM', 22000000.00, 22000000.00, 'IN_KIND', NULL, '2023-03-01', '2023-03-01', '2026-03-01', 'ACTIVE', 5),
(6, 'HĐLĐ Nhân viên Kỹ thuật', 'FIXED_TERM', 10000000.00, 10000000.00, 'CASH', NULL, '2023-09-10', '2023-09-10', '2026-09-10', 'ACTIVE', 6),
(7, 'HĐLĐ Trưởng ca Sản xuất', 'INDEFINITE', 20000000.00, 20000000.00, 'IN_KIND', NULL, '2023-03-02', '2023-03-02', NULL, 'ACTIVE', 7),
(8, 'HĐLĐ Công nhân Sản xuất', 'FIXED_TERM', 9000000.00, 9000000.00, 'CASH', NULL, '2023-09-15', '2023-09-15', '2026-09-15', 'ACTIVE', 8),
(9, 'HĐLĐ Trưởng nhóm In ấn', 'FIXED_TERM', 18000000.00, 18000000.00, 'IN_KIND', NULL, '2023-04-01', '2023-04-01', '2026-04-01', 'ACTIVE', 9),
(10, 'HĐLĐ Công nhân In ấn', 'FIXED_TERM', 9000000.00, 9000000.00, 'IN_KIND', NULL, '2023-10-01', '2023-10-01', '2026-10-01', 'ACTIVE', 10),
(11, 'HĐLĐ Trưởng nhóm CSKH', 'FIXED_TERM', 17000000.00, 17000000.00, 'NONE', NULL, '2023-05-01', '2023-05-01', '2026-05-01', 'ACTIVE', 11),
(12, 'HĐLĐ Nhân viên CSKH', 'FIXED_TERM', 8000000.00, 8000000.00, 'NONE', NULL, '2023-11-01', '2023-11-01', '2026-11-01', 'ACTIVE', 12),
(13, 'HĐLĐ Nhân viên HR', 'FIXED_TERM', 14000000.00, 14000000.00, 'NONE', NULL, '2024-04-28', '2024-04-28', '2026-04-28', 'ACTIVE', 13),
(14, 'HĐLĐ Thực tập sinh HR', 'FIXED_TERM', 5000000.00, 0.00, 'NONE', NULL, '2023-02-09', '2023-02-09', '2026-02-09', 'ACTIVE', 14),
(15, 'HĐLĐ Nhân viên In ấn', 'FIXED_TERM', 9500000.00, 9500000.00, 'IN_KIND', NULL, '2023-02-23', '2023-02-23', '2026-02-23', 'ACTIVE', 15),
(16, 'HĐLĐ Nhân viên In ấn', 'FIXED_TERM', 9200000.00, 9200000.00, 'IN_KIND', NULL, '2024-10-13', '2024-10-13', '2026-10-13', 'ACTIVE', 16),
(17, 'HĐLĐ Nhân viên In ấn', 'FIXED_TERM', 8800000.00, 8800000.00, 'CASH', NULL, '2023-08-03', '2023-08-03', '2026-08-03', 'ACTIVE', 17),
(18, 'HĐLĐ Nhân viên In ấn', 'FIXED_TERM', 9000000.00, 9000000.00, 'CASH', NULL, '2024-02-17', '2024-02-17', '2026-02-17', 'HISTORY', 18),
(19, 'HĐLĐ Chuyên viên Tuyển dụng', 'FIXED_TERM', 12000000.00, 12000000.00, 'NONE', NULL, '2024-11-27', '2024-11-27', '2025-11-27', 'HISTORY', 19),
(20, 'HĐLĐ Nhân viên In ấn (Thử việc)', 'PROBATION', 7500000.00, 0.00, 'IN_KIND', NULL, '2025-11-15', '2025-11-15', '2026-01-15', 'ACTIVE', 20),
(21, 'Hợp đồng Tái ký 2026 - test add hdld', 'FIXED_TERM', 15000000.00, 15000000.00, 'NONE', '/uploads/contracts/be665f42-7761-447b-985e-fa5afc98cf90.pdf', '2025-11-21', '2025-11-28', '2026-11-28', 'ACTIVE', 19),
(22, 'Hợp đồng Tái ký 2026 - test add hdld', 'FIXED_TERM', 15000000.00, 15000000.00, 'NONE', '/uploads/contracts/ed354bd8-4ef6-411e-a987-f461080a20c0.pdf', '2025-11-21', '2025-11-28', '2026-11-28', 'ACTIVE', 18),
(25, 'HDLD chính thức 12 tháng', 'FIXED_TERM', 15000000.00, 15000000.00, 'NONE', '/uploads/contracts/54ee9112-26eb-4675-91e2-be668549c24f.pdf', '2025-11-27', '2025-11-28', '2026-11-28', 'ACTIVE', 21);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `departments`
--

DROP TABLE IF EXISTS `departments`;
CREATE TABLE IF NOT EXISTS `departments` (
  `departmentID` int NOT NULL AUTO_INCREMENT,
  `departmentname` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `managerID` int DEFAULT NULL,
  `isoffice` bit(1) DEFAULT b'0',
  PRIMARY KEY (`departmentID`),
  KEY `FK_Department_Manager` (`managerID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `departments`
--

INSERT INTO `departments` (`departmentID`, `departmentname`, `managerID`, `isoffice`) VALUES
(1, 'Phòng Ban Nhân Sự', 3, b'1'),
(2, 'Phòng Ban IT', 1, b'1'),
(3, 'Phòng Ban Kỹ Thuật', 5, b'0'),
(4, 'Phòng Ban Sản Xuất', 7, b'0'),
(5, 'Phòng Ban In Ấn', 9, b'0'),
(6, 'Phòng Ban Chăm Sóc Khách Hàng', 11, b'1');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `dependents`
--

DROP TABLE IF EXISTS `dependents`;
CREATE TABLE IF NOT EXISTS `dependents` (
  `dependentID` int NOT NULL AUTO_INCREMENT,
  `userID` int NOT NULL COMMENT 'FK: Nhân viên liên quan (từ bảng users)',
  `fullname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Họ tên người phụ thuộc',
  `relationship` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Mối quan hệ (Vợ, Chồng, Con...)',
  `birth` date DEFAULT NULL COMMENT 'Ngày sinh',
  `gender` tinyint(1) DEFAULT NULL COMMENT '0=Nữ, 1=Nam',
  `idcardnumber` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'CCCD hoặc Giấy khai sinh',
  `phonenumber` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `istaxdeductible` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1=Có đăng ký giảm trừ gia cảnh, 0=Không',
  PRIMARY KEY (`dependentID`),
  KEY `FK_Dependent_User` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Người phụ thuộc của nhân viên';

--
-- Đang đổ dữ liệu cho bảng `dependents`
--

INSERT INTO `dependents` (`dependentID`, `userID`, `fullname`, `relationship`, `birth`, `gender`, `idcardnumber`, `phonenumber`, `istaxdeductible`) VALUES
(1, 1, 'Phạm Minh Anh Vợ', 'Vợ', '1990-05-10', 0, '001090001234', '0901000101', 1),
(2, 1, 'Phạm Minh Anh Con', 'Con', '2020-08-20', 1, '001220005678', '0901000102', 1),
(3, 2, 'Lê Hỗ Trợ IT Cha', 'Cha', '1965-02-15', 1, '002065001111', '0902000201', 1),
(4, 3, 'Nguyễn Thị Nhân Sự Chồng', 'Chồng', '1988-11-30', 1, '003088002222', '0903000301', 1),
(5, 4, 'Phạm Văn Tuyển Dụng Con', 'Con', '2021-03-14', 0, '004221003333', '0904000401', 1),
(6, 5, 'Võ Văn Kỹ Thuật Vợ', 'Vợ', '1992-07-25', 0, '005092004444', '0905000501', 1),
(7, 5, 'Võ Văn Kỹ Thuật Con', 'Con', '2023-01-10', 1, '005223005555', '0905000502', 1),
(8, 6, 'Hoàng Thị Máy Móc Con', 'Con', '2020-12-01', 1, '006220006666', '0906000601', 1),
(9, 7, 'Trịnh Hữu Sản Xuất Vợ', 'Vợ', '1993-01-20', 0, '007093007777', '0907000701', 1),
(10, 8, 'Đặng Văn Vận Hành Mẹ', 'Mẹ', '1970-04-05', 0, '008070008888', '0908000801', 1),
(11, 9, 'Bùi Văn Mực Vợ', 'Vợ', '1994-06-18', 0, '009094009999', '0909000901', 1),
(12, 9, 'Bùi Văn Mực Con Trai', 'Con', '2022-09-02', 1, '009222001010', '0909000902', 1),
(13, 10, 'Lý Thị In Con Gái', 'Con', '2024-01-07', 0, '010224001212', '0910000101', 1),
(14, 11, 'Đỗ Thị Khách Hàng Chồng', 'Chồng', '1991-08-11', 1, '011091001313', '0911000111', 1),
(15, 12, 'Mạc Văn Hài Lòng Vợ', 'Vợ', '1995-10-03', 0, '012095001414', '0912000121', 1),
(16, 13, 'Phạm Minh Anh HR Con', 'Con', '2021-11-11', 0, '013221001515', '0913000131', 1),
(17, 14, 'testAddAccountHR Cha', 'Cha', '1968-02-22', 1, '014068001616', '0914000141', 1),
(18, 15, 'testAddAccountNV Mẹ', 'Mẹ', '1972-03-23', 0, '015072001717', '0915000151', 1),
(19, 16, 'Phạm Nhân Viên Vợ', 'Vợ', '1998-04-24', 0, '016098001818', '0916000161', 1),
(20, 17, 'testAddAccountNVinan 17 Con', 'Con', '2023-05-25', 1, '017223001919', '0917000171', 1),
(21, 18, 'testAddAccountNVinan 18 Vợ', 'Vợ', '1999-06-26', 0, '018099002020', '0918000181', 1),
(22, 19, 'Phạm Minh Anh Test Pass Vợ', 'Vợ', '2000-07-27', 0, '019000002121', '0919000191', 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `employeedraftschedule`
--

DROP TABLE IF EXISTS `employeedraftschedule`;
CREATE TABLE IF NOT EXISTS `employeedraftschedule` (
  `draftID` int NOT NULL AUTO_INCREMENT,
  `employeeID` int NOT NULL COMMENT 'Nhân viên đăng ký (FK từ bảng users)',
  `date` date NOT NULL COMMENT 'Ngày làm việc đăng ký',
  `shiftID` int DEFAULT NULL COMMENT 'Ca làm việc muốn đăng ký (FK từ bảng shifts). NULL nếu để trống.',
  `isdayoff` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1 = muốn xin nghỉ, 0 = đi làm',
  `monthyear` varchar(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Tháng/Năm áp dụng lịch (ví dụ: 2025-12)',
  `registrationdate` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian gửi đăng ký',
  PRIMARY KEY (`draftID`),
  UNIQUE KEY `UQ_Employee_Date_Preference` (`employeeID`,`date`) COMMENT 'Mỗi nhân viên chỉ có 1 đăng ký/ngày',
  KEY `IX_preference_month_year` (`monthyear`),
  KEY `FK_preferences_shift` (`shiftID`)
) ENGINE=InnoDB AUTO_INCREMENT=373 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `employeedraftschedule`
--

INSERT INTO `employeedraftschedule` (`draftID`, `employeeID`, `date`, `shiftID`, `isdayoff`, `monthyear`, `registrationdate`) VALUES
(218, 7, '2025-12-01', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(219, 8, '2025-12-01', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(220, 7, '2025-12-02', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(221, 8, '2025-12-02', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(222, 7, '2025-12-03', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(223, 8, '2025-12-03', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(224, 7, '2025-12-04', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(225, 8, '2025-12-04', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(226, 7, '2025-12-05', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(227, 8, '2025-12-05', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(228, 7, '2025-12-06', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(229, 8, '2025-12-06', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(230, 7, '2025-12-07', NULL, 1, '2025-12', '2025-11-27 06:48:02'),
(231, 8, '2025-12-07', NULL, 1, '2025-12', '2025-11-27 06:48:02'),
(232, 8, '2025-12-08', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(233, 7, '2025-12-08', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(234, 8, '2025-12-09', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(235, 7, '2025-12-09', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(236, 8, '2025-12-10', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(237, 7, '2025-12-10', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(238, 8, '2025-12-11', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(239, 7, '2025-12-11', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(240, 8, '2025-12-12', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(241, 7, '2025-12-12', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(242, 8, '2025-12-13', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(243, 7, '2025-12-13', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(244, 7, '2025-12-14', NULL, 1, '2025-12', '2025-11-27 06:48:02'),
(245, 8, '2025-12-14', NULL, 1, '2025-12', '2025-11-27 06:48:02'),
(246, 7, '2025-12-15', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(247, 8, '2025-12-15', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(248, 7, '2025-12-16', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(249, 8, '2025-12-16', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(250, 7, '2025-12-17', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(251, 8, '2025-12-17', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(252, 7, '2025-12-18', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(253, 8, '2025-12-18', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(254, 7, '2025-12-19', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(255, 8, '2025-12-19', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(256, 7, '2025-12-20', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(257, 8, '2025-12-20', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(258, 7, '2025-12-21', NULL, 1, '2025-12', '2025-11-27 06:48:02'),
(259, 8, '2025-12-21', NULL, 1, '2025-12', '2025-11-27 06:48:02'),
(260, 8, '2025-12-22', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(261, 7, '2025-12-22', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(262, 8, '2025-12-23', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(263, 7, '2025-12-23', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(264, 8, '2025-12-24', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(265, 7, '2025-12-24', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(266, 8, '2025-12-25', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(267, 7, '2025-12-25', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(268, 8, '2025-12-26', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(269, 7, '2025-12-26', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(270, 8, '2025-12-27', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(271, 7, '2025-12-27', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(272, 7, '2025-12-28', NULL, 1, '2025-12', '2025-11-27 06:48:02'),
(273, 8, '2025-12-28', NULL, 1, '2025-12', '2025-11-27 06:48:02'),
(274, 7, '2025-12-29', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(275, 8, '2025-12-29', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(276, 7, '2025-12-30', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(277, 8, '2025-12-30', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(278, 7, '2025-12-31', 36, 0, '2025-12', '2025-11-27 06:48:02'),
(279, 8, '2025-12-31', 41, 0, '2025-12', '2025-11-27 06:48:02'),
(280, 5, '2025-12-01', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(281, 6, '2025-12-01', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(282, 21, '2025-12-01', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(283, 5, '2025-12-02', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(284, 6, '2025-12-02', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(285, 21, '2025-12-02', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(286, 5, '2025-12-03', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(287, 6, '2025-12-03', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(288, 21, '2025-12-03', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(289, 5, '2025-12-04', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(290, 6, '2025-12-04', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(291, 21, '2025-12-04', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(292, 5, '2025-12-05', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(293, 6, '2025-12-05', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(294, 21, '2025-12-05', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(295, 5, '2025-12-06', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(296, 6, '2025-12-06', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(297, 21, '2025-12-06', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(298, 5, '2025-12-07', NULL, 1, '2025-12', '2025-11-28 10:00:18'),
(299, 6, '2025-12-07', NULL, 1, '2025-12', '2025-11-28 10:00:18'),
(300, 21, '2025-12-07', NULL, 1, '2025-12', '2025-11-28 10:00:18'),
(301, 5, '2025-12-08', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(302, 21, '2025-12-08', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(303, 6, '2025-12-08', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(304, 5, '2025-12-09', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(305, 21, '2025-12-09', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(306, 6, '2025-12-09', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(307, 5, '2025-12-10', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(308, 21, '2025-12-10', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(309, 6, '2025-12-10', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(310, 5, '2025-12-11', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(311, 21, '2025-12-11', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(312, 6, '2025-12-11', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(313, 5, '2025-12-12', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(314, 21, '2025-12-12', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(315, 6, '2025-12-12', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(316, 5, '2025-12-13', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(317, 21, '2025-12-13', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(318, 6, '2025-12-13', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(319, 5, '2025-12-14', NULL, 1, '2025-12', '2025-11-28 10:00:18'),
(320, 6, '2025-12-14', NULL, 1, '2025-12', '2025-11-28 10:00:18'),
(321, 21, '2025-12-14', NULL, 1, '2025-12', '2025-11-28 10:00:18'),
(322, 5, '2025-12-15', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(323, 6, '2025-12-15', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(324, 21, '2025-12-15', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(325, 5, '2025-12-16', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(326, 6, '2025-12-16', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(327, 21, '2025-12-16', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(328, 5, '2025-12-17', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(329, 6, '2025-12-17', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(330, 21, '2025-12-17', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(331, 6, '2025-12-18', 40, 0, '2025-12', '2025-11-28 10:53:07'),
(332, 5, '2025-12-18', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(333, 21, '2025-12-18', NULL, 1, '2025-12', '2025-11-28 10:00:18'),
(334, 21, '2025-12-19', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(335, 6, '2025-12-19', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(336, 5, '2025-12-19', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(337, 21, '2025-12-20', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(338, 6, '2025-12-20', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(339, 5, '2025-12-20', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(340, 21, '2025-12-21', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(341, 5, '2025-12-21', NULL, 1, '2025-12', '2025-11-28 10:00:18'),
(342, 6, '2025-12-21', NULL, 1, '2025-12', '2025-11-28 10:00:18'),
(343, 21, '2025-12-22', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(344, 6, '2025-12-22', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(345, 5, '2025-12-22', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(346, 21, '2025-12-23', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(347, 6, '2025-12-23', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(348, 5, '2025-12-23', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(349, 21, '2025-12-24', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(350, 6, '2025-12-24', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(351, 5, '2025-12-24', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(352, 5, '2025-12-25', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(353, 6, '2025-12-25', NULL, 1, '2025-12', '2025-11-28 10:00:18'),
(354, 21, '2025-12-25', NULL, 1, '2025-12', '2025-11-28 10:00:18'),
(355, 21, '2025-12-26', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(356, 6, '2025-12-26', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(357, 5, '2025-12-26', NULL, 1, '2025-12', '2025-11-28 10:00:18'),
(358, 21, '2025-12-27', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(359, 5, '2025-12-27', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(360, 6, '2025-12-27', NULL, 1, '2025-12', '2025-11-28 10:00:18'),
(361, 21, '2025-12-28', 36, 0, '2025-12', '2025-11-28 10:00:18'),
(362, 6, '2025-12-28', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(363, 5, '2025-12-28', NULL, 1, '2025-12', '2025-11-28 10:00:18'),
(364, 5, '2025-12-29', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(365, 6, '2025-12-29', NULL, 1, '2025-12', '2025-11-28 10:00:18'),
(366, 21, '2025-12-29', NULL, 1, '2025-12', '2025-11-28 10:00:18'),
(367, 6, '2025-12-30', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(368, 5, '2025-12-30', NULL, 1, '2025-12', '2025-11-28 10:00:18'),
(369, 21, '2025-12-30', NULL, 1, '2025-12', '2025-11-28 10:00:18'),
(370, 5, '2025-12-31', 41, 0, '2025-12', '2025-11-28 10:00:18'),
(371, 6, '2025-12-31', NULL, 1, '2025-12', '2025-11-28 10:00:18'),
(372, 21, '2025-12-31', NULL, 1, '2025-12', '2025-11-28 10:00:18');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `employeeofficialschedule`
--

DROP TABLE IF EXISTS `employeeofficialschedule`;
CREATE TABLE IF NOT EXISTS `employeeofficialschedule` (
  `officialID` int NOT NULL AUTO_INCREMENT,
  `employeeID` int NOT NULL COMMENT 'Nhân viên được xếp lịch (FK từ bảng users)',
  `date` date NOT NULL COMMENT 'Ngày làm việc',
  `shiftID` int DEFAULT NULL COMMENT 'Ca làm việc chính thức (FK từ bảng shifts). NULL nếu là ngày nghỉ.',
  `isdayoff` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1 = ngày nghỉ, 0 = đi làm',
  `monthyear` varchar(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Tháng/Năm áp dụng lịch (ví dụ: 2025-12)',
  `approvedbymanagerID` int DEFAULT NULL COMMENT 'Manager đã duyệt (FK từ bảng users)',
  `publisheddate` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Ngày hoàn tất/công bố',
  PRIMARY KEY (`officialID`),
  UNIQUE KEY `UQ_Employee_Date_Schedule` (`employeeID`,`date`) COMMENT 'Mỗi nhân viên chỉ có 1 lịch/ngày',
  KEY `IX_schedule_month_year` (`monthyear`),
  KEY `FK_schedule_shift` (`shiftID`),
  KEY `FK_schedule_manager` (`approvedbymanagerID`)
) ENGINE=InnoDB AUTO_INCREMENT=233 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `employeeofficialschedule`
--

INSERT INTO `employeeofficialschedule` (`officialID`, `employeeID`, `date`, `shiftID`, `isdayoff`, `monthyear`, `approvedbymanagerID`, `publisheddate`) VALUES
(7, 20, '2025-11-19', 46, 0, '2025-11', 1, '2025-11-19 17:43:29'),
(14, 21, '2025-11-29', 46, 0, '2025-11', 21, '2025-11-19 17:43:29'),
(15, 21, '2025-11-30', 46, 0, '2025-11', 21, '2025-11-19 17:43:29'),
(16, 15, '2025-12-04', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(17, 15, '2025-12-03', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(18, 9, '2025-12-01', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(19, 10, '2025-12-01', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(20, 15, '2025-12-01', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(21, 16, '2025-12-01', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(22, 17, '2025-12-01', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(23, 18, '2025-12-01', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(24, 20, '2025-12-01', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(25, 9, '2025-12-02', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(26, 20, '2025-12-02', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(27, 10, '2025-12-02', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(28, 15, '2025-12-02', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(29, 16, '2025-12-02', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(30, 17, '2025-12-02', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(31, 18, '2025-12-02', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(32, 9, '2025-12-03', 36, 0, '2025-12', 9, '2025-12-11 09:55:01'),
(33, 18, '2025-12-03', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(34, 20, '2025-12-03', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(35, 10, '2025-12-03', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(36, 16, '2025-12-03', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(37, 17, '2025-12-03', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(38, 9, '2025-12-04', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(39, 18, '2025-12-04', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(40, 20, '2025-12-04', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(41, 10, '2025-12-04', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(42, 16, '2025-12-04', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(43, 17, '2025-12-04', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(44, 15, '2025-12-05', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(45, 18, '2025-12-05', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(46, 20, '2025-12-05', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(47, 9, '2025-12-05', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(48, 10, '2025-12-05', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(49, 16, '2025-12-05', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(50, 17, '2025-12-05', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(51, 15, '2025-12-06', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(52, 17, '2025-12-06', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(53, 18, '2025-12-06', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(54, 9, '2025-12-06', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(55, 20, '2025-12-06', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(56, 10, '2025-12-06', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(57, 16, '2025-12-06', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(58, 15, '2025-12-07', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(59, 16, '2025-12-07', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(60, 17, '2025-12-07', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(61, 18, '2025-12-07', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(62, 20, '2025-12-07', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(63, 9, '2025-12-07', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(64, 10, '2025-12-07', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(65, 9, '2025-12-08', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(66, 16, '2025-12-08', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(67, 10, '2025-12-08', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(68, 15, '2025-12-08', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(69, 18, '2025-12-08', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(70, 17, '2025-12-08', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(71, 20, '2025-12-08', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(72, 9, '2025-12-09', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(73, 20, '2025-12-09', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(74, 16, '2025-12-09', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(75, 15, '2025-12-09', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(76, 10, '2025-12-09', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(77, 17, '2025-12-09', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(78, 18, '2025-12-09', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(79, 9, '2025-12-10', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(80, 18, '2025-12-10', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(81, 20, '2025-12-10', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(82, 15, '2025-12-10', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(83, 10, '2025-12-10', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(84, 16, '2025-12-10', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(85, 17, '2025-12-10', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(86, 9, '2025-12-11', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(87, 17, '2025-12-11', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(88, 18, '2025-12-11', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(89, 20, '2025-12-11', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(90, 10, '2025-12-11', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(91, 16, '2025-12-11', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(92, 15, '2025-12-11', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(93, 15, '2025-12-12', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(94, 17, '2025-12-12', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(95, 18, '2025-12-12', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(96, 9, '2025-12-12', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(97, 20, '2025-12-12', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(98, 10, '2025-12-12', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(99, 16, '2025-12-12', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(100, 15, '2025-12-13', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(101, 16, '2025-12-13', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(102, 17, '2025-12-13', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(103, 9, '2025-12-13', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(104, 18, '2025-12-13', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(105, 20, '2025-12-13', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(106, 10, '2025-12-13', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(107, 15, '2025-12-14', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(108, 10, '2025-12-14', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(109, 16, '2025-12-14', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(110, 18, '2025-12-14', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(111, 20, '2025-12-14', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(112, 17, '2025-12-14', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(113, 9, '2025-12-14', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(114, 9, '2025-12-15', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(115, 10, '2025-12-15', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(116, 16, '2025-12-15', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(117, 15, '2025-12-15', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(118, 18, '2025-12-15', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(119, 17, '2025-12-15', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(120, 20, '2025-12-15', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(121, 9, '2025-12-16', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(122, 20, '2025-12-16', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(123, 10, '2025-12-16', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(124, 15, '2025-12-16', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(125, 16, '2025-12-16', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(126, 17, '2025-12-16', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(127, 18, '2025-12-16', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(128, 9, '2025-12-17', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(129, 18, '2025-12-17', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(130, 20, '2025-12-17', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(131, 15, '2025-12-17', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(132, 10, '2025-12-17', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(133, 16, '2025-12-17', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(134, 17, '2025-12-17', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(135, 9, '2025-12-18', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(136, 17, '2025-12-18', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(137, 18, '2025-12-18', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(138, 20, '2025-12-18', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(139, 10, '2025-12-18', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(140, 16, '2025-12-18', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(141, 15, '2025-12-18', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(142, 15, '2025-12-19', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(143, 17, '2025-12-19', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(144, 18, '2025-12-19', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(145, 9, '2025-12-19', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(146, 20, '2025-12-19', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(147, 10, '2025-12-19', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(148, 16, '2025-12-19', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(149, 15, '2025-12-20', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(150, 16, '2025-12-20', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(151, 17, '2025-12-20', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(152, 9, '2025-12-20', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(153, 18, '2025-12-20', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(154, 20, '2025-12-20', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(155, 10, '2025-12-20', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(156, 15, '2025-12-21', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(157, 10, '2025-12-21', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(158, 16, '2025-12-21', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(159, 18, '2025-12-21', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(160, 17, '2025-12-21', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(161, 20, '2025-12-21', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(162, 9, '2025-12-21', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(163, 15, '2025-12-22', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(164, 10, '2025-12-22', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(165, 16, '2025-12-22', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(166, 9, '2025-12-22', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(167, 18, '2025-12-22', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(168, 17, '2025-12-22', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(169, 20, '2025-12-22', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(170, 15, '2025-12-23', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(171, 20, '2025-12-23', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(172, 10, '2025-12-23', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(173, 9, '2025-12-23', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(174, 16, '2025-12-23', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(175, 17, '2025-12-23', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(176, 18, '2025-12-23', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(177, 15, '2025-12-24', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(178, 20, '2025-12-24', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(179, 18, '2025-12-24', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(180, 9, '2025-12-24', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(181, 10, '2025-12-24', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(182, 16, '2025-12-24', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(183, 17, '2025-12-24', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(184, 17, '2025-12-25', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(185, 20, '2025-12-25', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(186, 18, '2025-12-25', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(187, 9, '2025-12-25', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(188, 10, '2025-12-25', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(189, 16, '2025-12-25', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(190, 15, '2025-12-25', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(191, 15, '2025-12-26', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(192, 17, '2025-12-26', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(193, 20, '2025-12-26', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(194, 9, '2025-12-26', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(195, 18, '2025-12-26', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(196, 10, '2025-12-26', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(197, 16, '2025-12-26', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(198, 16, '2025-12-27', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(199, 17, '2025-12-27', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(200, 20, '2025-12-27', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(201, 15, '2025-12-27', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(202, 18, '2025-12-27', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(203, 9, '2025-12-27', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(204, 10, '2025-12-27', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(205, 10, '2025-12-28', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(206, 16, '2025-12-28', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(207, 17, '2025-12-28', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(208, 15, '2025-12-28', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(209, 20, '2025-12-28', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(210, 18, '2025-12-28', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(211, 9, '2025-12-28', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(212, 9, '2025-12-29', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(213, 10, '2025-12-29', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(214, 16, '2025-12-29', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(215, 15, '2025-12-29', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(216, 17, '2025-12-29', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(217, 18, '2025-12-29', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(218, 20, '2025-12-29', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(219, 9, '2025-12-30', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(220, 20, '2025-12-30', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(221, 10, '2025-12-30', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(222, 15, '2025-12-30', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(223, 16, '2025-12-30', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(224, 17, '2025-12-30', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(225, 18, '2025-12-30', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(226, 9, '2025-12-31', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(227, 18, '2025-12-31', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(228, 20, '2025-12-31', 36, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(229, 15, '2025-12-31', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(230, 10, '2025-12-31', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(231, 16, '2025-12-31', 41, 0, '2025-12', 9, '2025-12-11 09:45:58'),
(232, 17, '2025-12-31', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `leavebalance`
--

DROP TABLE IF EXISTS `leavebalance`;
CREATE TABLE IF NOT EXISTS `leavebalance` (
  `userID` int NOT NULL COMMENT 'FK: Bảng users',
  `leavetypeid` int NOT NULL COMMENT 'FK: Bảng shifts (tham chiếu ID của AL, SL, PL...)',
  `year` int NOT NULL COMMENT 'Năm áp dụng (VD: 2025)',
  `totalgranted` int NOT NULL DEFAULT '0',
  `carriedover` int NOT NULL DEFAULT '0',
  `daysused` int NOT NULL DEFAULT '0',
  `lastupdated` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`userID`,`leavetypeid`,`year`) COMMENT 'Mỗi NV chỉ có 1 số dư cho 1 loại phép/năm',
  KEY `FK_leavebalance_Shift` (`leavetypeid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Số dư phép của từng nhân viên';

--
-- Đang đổ dữ liệu cho bảng `leavebalance`
--

INSERT INTO `leavebalance` (`userID`, `leavetypeid`, `year`, `totalgranted`, `carriedover`, `daysused`, `lastupdated`) VALUES
(1, 53, 2025, 12, 0, 0, '2025-11-14 22:14:46'),
(1, 54, 2025, 30, 0, 0, '2025-11-14 22:14:46'),
(1, 55, 2025, 14, 0, 0, '2025-11-14 22:14:46'),
(2, 53, 2025, 12, 0, 0, '2025-11-14 22:14:46'),
(2, 54, 2025, 30, 0, 0, '2025-11-14 22:14:46'),
(2, 55, 2025, 14, 0, 0, '2025-11-14 22:14:46'),
(3, 53, 2025, 12, 0, 0, '2025-11-14 22:14:46'),
(3, 54, 2025, 30, 0, 0, '2025-11-14 22:14:46'),
(3, 56, 2025, 180, 0, 0, '2025-11-14 22:14:46'),
(4, 53, 2025, 12, 0, 0, '2025-11-14 22:14:46'),
(4, 54, 2025, 30, 0, 0, '2025-11-14 22:14:46'),
(4, 55, 2025, 14, 0, 0, '2025-11-14 22:14:46'),
(5, 53, 2025, 12, 0, 0, '2025-11-14 22:14:46'),
(5, 54, 2025, 30, 0, 0, '2025-11-14 22:14:46'),
(5, 56, 2025, 180, 0, 0, '2025-11-14 22:14:46'),
(6, 53, 2025, 12, 0, 0, '2025-11-14 22:14:46'),
(6, 54, 2025, 30, 0, 0, '2025-11-14 22:14:46'),
(6, 55, 2025, 14, 0, 0, '2025-11-14 22:14:46'),
(7, 53, 2025, 12, 0, 0, '2025-11-14 22:14:46'),
(7, 54, 2025, 30, 0, 0, '2025-11-14 22:14:46'),
(7, 55, 2025, 14, 0, 0, '2025-11-14 22:14:46'),
(8, 53, 2025, 12, 0, 0, '2025-11-14 22:14:46'),
(8, 54, 2025, 30, 0, 0, '2025-11-14 22:14:46'),
(8, 56, 2025, 180, 0, 0, '2025-11-14 22:14:46'),
(9, 53, 2025, 12, 0, 0, '2025-11-14 22:14:46'),
(9, 54, 2025, 30, 0, 0, '2025-11-14 22:14:46'),
(9, 56, 2025, 180, 0, 0, '2025-11-14 22:14:46'),
(10, 53, 2025, 12, 0, 0, '2025-11-14 22:14:46'),
(10, 54, 2025, 30, 0, 0, '2025-11-14 22:14:46'),
(10, 55, 2025, 14, 0, 0, '2025-11-14 22:14:46'),
(11, 53, 2025, 12, 0, 0, '2025-11-14 22:14:46'),
(11, 54, 2025, 30, 0, 0, '2025-11-14 22:14:46'),
(11, 55, 2025, 14, 0, 0, '2025-11-14 22:14:46'),
(12, 53, 2025, 12, 0, 0, '2025-11-14 22:14:46'),
(12, 54, 2025, 30, 0, 0, '2025-11-14 22:14:46'),
(12, 56, 2025, 180, 0, 0, '2025-11-14 22:14:46'),
(13, 53, 2025, 12, 0, 0, '2025-11-14 22:14:46'),
(13, 54, 2025, 30, 0, 0, '2025-11-14 22:14:46'),
(13, 56, 2025, 180, 0, 0, '2025-11-14 22:14:46'),
(14, 53, 2025, 12, 0, 0, '2025-11-14 22:14:46'),
(14, 54, 2025, 30, 0, 0, '2025-11-14 22:14:46'),
(14, 55, 2025, 14, 0, 0, '2025-11-14 22:14:46'),
(15, 53, 2025, 12, 0, 0, '2025-11-14 22:14:46'),
(15, 54, 2025, 30, 0, 0, '2025-11-14 22:14:46'),
(15, 56, 2025, 180, 0, 0, '2025-11-14 22:14:46'),
(16, 53, 2025, 12, 0, 0, '2025-11-14 22:14:46'),
(16, 54, 2025, 30, 0, 0, '2025-11-14 22:14:46'),
(16, 56, 2025, 180, 0, 0, '2025-11-14 22:14:46'),
(17, 53, 2025, 12, 0, 0, '2025-11-14 22:14:46'),
(17, 54, 2025, 30, 0, 0, '2025-11-14 22:14:46'),
(17, 56, 2025, 180, 0, 0, '2025-11-14 22:14:46'),
(18, 53, 2025, 12, 0, 1, '2025-11-14 22:14:46'),
(18, 54, 2025, 30, 0, 1, '2025-11-14 22:14:46'),
(18, 56, 2025, 180, 0, 0, '2025-11-14 22:14:46'),
(19, 53, 2025, 12, 0, 0, '2025-11-14 22:14:46'),
(19, 54, 2025, 30, 0, 0, '2025-11-14 22:14:46'),
(19, 55, 2025, 14, 0, 0, '2025-11-14 22:14:46'),
(22, 53, 2025, 12, 0, 0, '2025-12-10 16:08:26'),
(22, 54, 2025, 30, 0, 0, '2025-12-10 16:08:26'),
(22, 55, 2025, 14, 0, 0, '2025-12-10 16:08:26'),
(22, 56, 2025, 0, 0, 0, '2025-12-10 16:08:26'),
(22, 57, 2025, 0, 0, 0, '2025-12-10 16:08:26'),
(23, 53, 2025, 12, 0, 0, '2025-12-10 09:15:47'),
(23, 54, 2025, 30, 0, 0, '2025-12-10 09:15:47'),
(23, 55, 2025, 14, 0, 0, '2025-12-10 09:15:47'),
(23, 56, 2025, 0, 0, 0, '2025-12-10 09:15:47'),
(23, 57, 2025, 0, 0, 0, '2025-12-10 09:15:47');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `leavepolicy`
--

DROP TABLE IF EXISTS `leavepolicy`;
CREATE TABLE IF NOT EXISTS `leavepolicy` (
  `policyID` int NOT NULL AUTO_INCREMENT,
  `leavetype` enum('ANNUAL','SICK','MATERNITY','PATERNITY') COLLATE utf8mb4_unicode_ci NOT NULL,
  `minyearsservice` int NOT NULL COMMENT 'Thâm niên tối thiểu (năm)',
  `maxyearsservice` int DEFAULT NULL,
  `jobtype` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gendertarget` enum('MALE','FEMALE') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `days` int NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Mô tả (VD: "Nhân viên dưới 5 năm")',
  `leavetypeid` int DEFAULT NULL,
  PRIMARY KEY (`policyID`),
  KEY `FK_Policy_Shift` (`leavetypeid`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Chính sách phép năm theo thâm niên';

--
-- Đang đổ dữ liệu cho bảng `leavepolicy`
--

INSERT INTO `leavepolicy` (`policyID`, `leavetype`, `minyearsservice`, `maxyearsservice`, `jobtype`, `gendertarget`, `days`, `description`, `leavetypeid`) VALUES
(1, 'ANNUAL', 0, 5, NULL, NULL, 12, 'Nhân viên có thâm niên dưới 5 năm', 53),
(2, 'ANNUAL', 5, 10, NULL, NULL, 13, 'Nhân viên có thâm niên từ 5 đến dưới 10 năm', 53),
(3, 'ANNUAL', 10, NULL, NULL, NULL, 14, 'Nhân viên có thâm niên từ 10 năm trở lên', 53),
(7, 'SICK', 0, 15, 'NORMAL', NULL, 30, 'Phép ốm cho nhân viên đóng BHXH dưới 15 năm', 54),
(8, 'SICK', 15, 30, 'NORMAL', NULL, 40, 'Phép ốm cho nhân viên đóng BHXH từ 15 năm - 30 năm', 54),
(9, 'SICK', 30, NULL, 'NORMAL', NULL, 60, 'Phép ốm cho nhân viên đóng BHXH từ 30 năm trở lên', 54),
(10, 'MATERNITY', 0, NULL, NULL, 'FEMALE', 180, 'Nghỉ thai sản', 56),
(11, 'PATERNITY', 0, NULL, NULL, 'MALE', 14, 'Nhân viên có vợ sinh con', 55),
(12, 'SICK', 0, 15, 'DANGER', NULL, 40, 'Phép ốm (Độc hại) cho nhân viên đóng BHXH dưới 15 năm', 54),
(13, 'SICK', 15, 30, 'DANGER', NULL, 50, 'Phép ốm (Độc hại) cho nhân viên đóng BHXH từ 15 năm - 30 năm', 54),
(14, 'SICK', 30, NULL, 'DANGER', NULL, 70, 'Phép ốm (Độc hại) cho nhân viên đóng BHXH từ 30 năm trở lên', 54);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `leaverequests`
--

DROP TABLE IF EXISTS `leaverequests`;
CREATE TABLE IF NOT EXISTS `leaverequests` (
  `leaverequestID` int NOT NULL AUTO_INCREMENT,
  `leavetype` enum('ANNUAL','SICK','MATERNITY','PATERNITY','UNPAID') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ANNUAL',
  `startdate` date NOT NULL,
  `enddate` date NOT NULL,
  `reason` text COLLATE utf8mb4_unicode_ci,
  `status` enum('PENDING','APPROVED','REJECTED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `requestdate` date DEFAULT NULL,
  `userID` int DEFAULT NULL,
  `shiftID` int DEFAULT NULL,
  PRIMARY KEY (`leaverequestID`),
  KEY `userID` (`userID`),
  KEY `FK_Request_Shift` (`shiftID`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `leaverequests`
--

INSERT INTO `leaverequests` (`leaverequestID`, `leavetype`, `startdate`, `enddate`, `reason`, `status`, `requestdate`, `userID`, `shiftID`) VALUES
(1, 'SICK', '2025-10-29', '2025-10-29', 'Bị sốt', 'APPROVED', '2025-10-28', 6, 54),
(2, 'ANNUAL', '2025-11-05', '2025-11-06', 'Gia đình có việc', 'PENDING', '2025-10-28', 12, 53),
(3, 'ANNUAL', '2025-10-20', '2025-10-20', 'Việc cá nhân', 'REJECTED', '2025-10-19', 8, 53),
(5, 'SICK', '2025-12-01', '2025-12-02', 'Test API tạo đơn từ Postman', 'PENDING', '2025-11-13', 15, 54),
(6, 'SICK', '2025-12-06', '2025-12-06', 'Test API tạo đơn từ Postman', 'REJECTED', '2025-11-13', 18, 54),
(7, 'ANNUAL', '2025-12-02', '2025-12-02', 'đi chơi', 'APPROVED', '2025-11-24', 18, 53),
(8, 'SICK', '2025-12-03', '2025-12-03', 'bệnh Ho ', 'APPROVED', '2025-11-24', 18, 54),
(9, 'ANNUAL', '2025-12-25', '2025-12-26', 'Nghỉ phép test chức năng', 'PENDING', NULL, 6, NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `monthlypayrollconfigs`
--

DROP TABLE IF EXISTS `monthlypayrollconfigs`;
CREATE TABLE IF NOT EXISTS `monthlypayrollconfigs` (
  `ConfigID` int NOT NULL AUTO_INCREMENT,
  `Month` int NOT NULL,
  `Year` int NOT NULL,
  `CycleStartDate` date NOT NULL,
  `CycleEndDate` date NOT NULL,
  `StandardWorkDays` decimal(4,2) DEFAULT '26.00',
  PRIMARY KEY (`ConfigID`),
  UNIQUE KEY `UQ_Month_Year` (`Month`,`Year`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `monthlypayrollconfigs`
--

INSERT INTO `monthlypayrollconfigs` (`ConfigID`, `Month`, `Year`, `CycleStartDate`, `CycleEndDate`, `StandardWorkDays`) VALUES
(1, 1, 2025, '2024-12-26', '2025-01-25', 26.00),
(2, 2, 2025, '2025-01-26', '2025-02-25', 24.00),
(3, 3, 2025, '2025-02-26', '2025-03-25', 26.00),
(4, 4, 2025, '2025-03-26', '2025-04-25', 26.00),
(5, 5, 2025, '2025-04-26', '2025-05-25', 26.00),
(6, 6, 2025, '2025-05-26', '2025-06-25', 26.00),
(7, 7, 2025, '2025-06-26', '2025-07-25', 26.00),
(8, 8, 2025, '2025-07-26', '2025-08-25', 26.00),
(9, 9, 2025, '2025-08-26', '2025-09-25', 26.00),
(10, 10, 2025, '2025-09-26', '2025-10-25', 26.00),
(11, 11, 2025, '2025-10-26', '2025-11-25', 26.00),
(12, 12, 2025, '2025-11-26', '2025-12-25', 26.00);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `overtime`
--

DROP TABLE IF EXISTS `overtime`;
CREATE TABLE IF NOT EXISTS `overtime` (
  `id` int NOT NULL AUTO_INCREMENT,
  `userid` int NOT NULL COMMENT 'ID nhân viên (FK)',
  `overtimetypeid` int NOT NULL COMMENT 'ID loại tăng ca (FK)',
  `date` date NOT NULL COMMENT 'Ngày làm thêm giờ',
  `hours` double NOT NULL COMMENT 'Số giờ làm thêm',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'APPROVED' COMMENT 'Trạng thái: APPROVED, PENDING, REJECTED',
  `reason` text COLLATE utf8mb4_unicode_ci COMMENT 'Lý do làm thêm',
  PRIMARY KEY (`id`),
  KEY `fk_overtime_users` (`userid`),
  KEY `fk_overtime_type` (`overtimetypeid`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `overtime`
--

INSERT INTO `overtime` (`id`, `userid`, `overtimetypeid`, `date`, `hours`, `status`, `reason`) VALUES
(1, 1, 1, '2025-10-15', 3, 'APPROVED', 'Chạy deadline dự án'),
(2, 1, 2, '2025-10-19', 4, 'APPROVED', 'Bảo trì hệ thống cuối tuần'),
(3, 2, 1, '2025-10-20', 2, 'PENDING', 'Hỗ trợ khách hàng muộn');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `overtimetypes`
--

DROP TABLE IF EXISTS `overtimetypes`;
CREATE TABLE IF NOT EXISTS `overtimetypes` (
  `OvertimeTypeID` int NOT NULL AUTO_INCREMENT,
  `OtCode` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `OtName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `Rate` decimal(5,2) NOT NULL COMMENT 'Hệ số nhân (1.5, 2.0...)',
  `CalculationType` enum('MULTIPLIER','ADDITIVE','FIXED_AMOUNT') COLLATE utf8mb4_unicode_ci DEFAULT 'MULTIPLIER' COMMENT 'MULTIPLIER: nhân hệ số (1.5x, 2x), ADDITIVE: cộng thêm % (+30%, +50%), FIXED_AMOUNT: số tiền cố định',
  `IsTaxExemptPart` tinyint(1) DEFAULT '1' COMMENT '1=Phần chênh lệch được miễn thuế',
  `TaxExemptFormula` enum('NONE','EXCESS_ONLY','FULL_AMOUNT','PERCENTAGE','CUSTOM') COLLATE utf8mb4_unicode_ci DEFAULT 'EXCESS_ONLY' COMMENT 'NONE: không miễn thuế, EXCESS_ONLY: chỉ phần vượt, FULL_AMOUNT: toàn bộ, PERCENTAGE: theo %, CUSTOM: công thức riêng',
  `TaxExemptPercentage` decimal(5,2) DEFAULT '0.00' COMMENT 'Tỷ lệ % được miễn thuế (dùng khi TaxExemptFormula = PERCENTAGE)',
  `Description` text COLLATE utf8mb4_unicode_ci COMMENT 'Mô tả chi tiết cách tính OT này',
  PRIMARY KEY (`OvertimeTypeID`),
  UNIQUE KEY `OtCode` (`OtCode`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `overtimetypes`
--

INSERT INTO `overtimetypes` (`OvertimeTypeID`, `OtCode`, `OtName`, `Rate`, `CalculationType`, `IsTaxExemptPart`, `TaxExemptFormula`, `TaxExemptPercentage`, `Description`) VALUES
(1, 'OT_WEEKDAY', 'Làm thêm ngày thường', 1.50, 'MULTIPLIER', 1, 'EXCESS_ONLY', 0.00, 'Làm thêm giờ ngày thường: 150% lương giờ. Phần vượt 100% được miễn thuế.'),
(2, 'OT_WEEKEND', 'Làm thêm ngày nghỉ tuần', 2.00, 'MULTIPLIER', 1, 'EXCESS_ONLY', 0.00, 'Làm thêm giờ ngày nghỉ tuần: 200% lương giờ. Phần vượt 100% được miễn thuế.'),
(3, 'OT_HOLIDAY', 'Làm thêm ngày Lễ/Tết', 3.00, 'MULTIPLIER', 1, 'EXCESS_ONLY', 0.00, 'Làm thêm giờ ngày Lễ/Tết: 300% lương giờ. Phần vượt 100% được miễn thuế.'),
(4, 'OT_NIGHT', 'Làm việc ban đêm (Phụ trội)', 0.30, 'ADDITIVE', 1, 'EXCESS_ONLY', 0.00, 'Phụ trội làm đêm (22h-6h): +30% lương giờ. Toàn bộ chịu thuế.');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `payrolls`
--

DROP TABLE IF EXISTS `payrolls`;
CREATE TABLE IF NOT EXISTS `payrolls` (
  `payID` int NOT NULL AUTO_INCREMENT,
  `netsalary` decimal(15,2) NOT NULL,
  `payperiod` varchar(7) COLLATE utf8mb4_unicode_ci NOT NULL,
  `userID` int DEFAULT NULL,
  `totalincome` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT 'Tổng thu nhập (Gross)',
  `status` enum('DRAFT','FINAL','PAID') COLLATE utf8mb4_unicode_ci DEFAULT 'DRAFT',
  `pit` decimal(15,2) DEFAULT '0.00' COMMENT 'Thuế TNCN',
  `bhxh_emp` decimal(15,2) DEFAULT '0.00' COMMENT 'BHXH NV',
  `bhyt_emp` decimal(15,2) DEFAULT '0.00' COMMENT 'BHYT NV',
  `bhtn_emp` decimal(15,2) DEFAULT '0.00' COMMENT 'BHTN NV',
  `basesalarysnapshot` decimal(15,2) DEFAULT '0.00',
  `insurancesalarysnapshot` decimal(15,2) DEFAULT '0.00',
  `standardworkdays` decimal(4,2) DEFAULT '26.00',
  `actualworkdays` decimal(4,2) DEFAULT '0.00',
  `othours` decimal(5,2) DEFAULT '0.00',
  `dependentcount` int DEFAULT '0',
  `bhxh_comp` decimal(15,2) DEFAULT '0.00' COMMENT 'BHXH Công ty đóng (17.5%)',
  `bhyt_comp` decimal(15,2) DEFAULT '0.00' COMMENT 'BHYT Công ty đóng (3%)',
  `bhtn_comp` decimal(15,2) DEFAULT '0.00' COMMENT 'BHTN Công ty đóng (1%)',
  PRIMARY KEY (`payID`),
  UNIQUE KEY `UQ_User_Period` (`userID`,`payperiod`),
  UNIQUE KEY `uk_user_period` (`userID`,`payperiod`)
) ENGINE=InnoDB AUTO_INCREMENT=866 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `payrolls`
--

INSERT INTO `payrolls` (`payID`, `netsalary`, `payperiod`, `userID`, `totalincome`, `status`, `pit`, `bhxh_emp`, `bhyt_emp`, `bhtn_emp`, `basesalarysnapshot`, `insurancesalarysnapshot`, `standardworkdays`, `actualworkdays`, `othours`, `dependentcount`, `bhxh_comp`, `bhyt_comp`, `bhtn_comp`) VALUES
(1, 10500000.00, '2025-09', 6, 0.00, 'DRAFT', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 26.00, 0.00, 0.00, 0, 0.00, 0.00, 0.00),
(2, 9100000.00, '2025-09', 8, 0.00, 'DRAFT', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 26.00, 0.00, 0.00, 0, 0.00, 0.00, 0.00),
(569, -3150000.00, '2025-11', 1, 0.00, 'DRAFT', 0.00, 2400000.00, 450000.00, 300000.00, 30000000.00, 30000000.00, 26.00, 0.00, 0.00, 2, 5250000.00, 900000.00, 300000.00),
(570, -1575000.00, '2025-11', 2, 0.00, 'DRAFT', 0.00, 1200000.00, 225000.00, 150000.00, 15000000.00, 15000000.00, 26.00, 0.00, 0.00, 1, 2625000.00, 450000.00, 150000.00),
(571, -2625000.00, '2025-11', 3, 0.00, 'DRAFT', 0.00, 2000000.00, 375000.00, 250000.00, 25000000.00, 25000000.00, 26.00, 0.00, 0.00, 1, 4375000.00, 750000.00, 250000.00),
(572, -1260000.00, '2025-11', 4, 0.00, 'DRAFT', 0.00, 960000.00, 180000.00, 120000.00, 12000000.00, 12000000.00, 26.00, 0.00, 0.00, 1, 2100000.00, 360000.00, 120000.00),
(573, -1320384.62, '2025-11', 5, 989615.38, 'DRAFT', 0.00, 1760000.00, 330000.00, 220000.00, 22000000.00, 22000000.00, 26.00, 1.00, 0.00, 2, 3850000.00, 660000.00, 220000.00),
(574, -586153.84, '2025-11', 6, 663846.16, 'DRAFT', 0.00, 800000.00, 150000.00, 100000.00, 10000000.00, 10000000.00, 26.00, 2.00, 0.00, 1, 1750000.00, 300000.00, 100000.00),
(575, -2100000.00, '2025-11', 7, 0.00, 'DRAFT', 0.00, 1600000.00, 300000.00, 200000.00, 20000000.00, 20000000.00, 26.00, 0.00, 0.00, 1, 3500000.00, 600000.00, 200000.00),
(576, 1448461.54, '2025-11', 8, 2393461.54, 'DRAFT', 0.00, 720000.00, 135000.00, 90000.00, 9000000.00, 9000000.00, 26.00, 1.00, 0.00, 1, 1575000.00, 270000.00, 90000.00),
(577, -1890000.00, '2025-11', 9, 0.00, 'DRAFT', 0.00, 1440000.00, 270000.00, 180000.00, 18000000.00, 18000000.00, 26.00, 0.00, 0.00, 2, 3150000.00, 540000.00, 180000.00),
(578, -1945000.00, '2025-11', 10, -500000.00, 'DRAFT', 0.00, 720000.00, 135000.00, 90000.00, 9000000.00, 9000000.00, 26.00, 0.00, 0.00, 1, 1575000.00, 270000.00, 90000.00),
(579, -1785000.00, '2025-11', 11, 0.00, 'DRAFT', 0.00, 1360000.00, 255000.00, 170000.00, 17000000.00, 17000000.00, 26.00, 0.00, 0.00, 1, 2975000.00, 510000.00, 170000.00),
(580, -488846.15, '2025-11', 12, 351153.85, 'DRAFT', 0.00, 640000.00, 120000.00, 80000.00, 8000000.00, 8000000.00, 26.00, 1.00, 0.00, 1, 1400000.00, 240000.00, 80000.00),
(581, -1470000.00, '2025-11', 13, 0.00, 'DRAFT', 0.00, 1120000.00, 210000.00, 140000.00, 14000000.00, 14000000.00, 26.00, 0.00, 0.00, 1, 2450000.00, 420000.00, 140000.00),
(582, 0.00, '2025-11', 14, 0.00, 'DRAFT', 0.00, 0.00, 0.00, 0.00, 5000000.00, 0.00, 26.00, 0.00, 0.00, 1, 0.00, 0.00, 0.00),
(583, -997500.00, '2025-11', 15, 0.00, 'DRAFT', 0.00, 760000.00, 142500.00, 95000.00, 9500000.00, 9500000.00, 26.00, 0.00, 0.00, 1, 1662500.00, 285000.00, 95000.00),
(584, -2966000.00, '2025-11', 16, -1000000.00, 'DRAFT', 0.00, 736000.00, 138000.00, 92000.00, 9200000.00, 9200000.00, 26.00, 0.00, 0.00, 1, 1610000.00, 276000.00, 92000.00),
(585, -924000.00, '2025-11', 17, 0.00, 'DRAFT', 0.00, 704000.00, 132000.00, 88000.00, 8800000.00, 8800000.00, 26.00, 0.00, 0.00, 1, 1540000.00, 264000.00, 88000.00),
(586, -1575000.00, '2025-11', 18, 0.00, 'DRAFT', 0.00, 1200000.00, 225000.00, 150000.00, 15000000.00, 15000000.00, 26.00, 0.00, 0.00, 1, 2625000.00, 450000.00, 150000.00),
(587, -1575000.00, '2025-11', 19, 0.00, 'DRAFT', 0.00, 1200000.00, 225000.00, 150000.00, 15000000.00, 15000000.00, 26.00, 0.00, 0.00, 1, 2625000.00, 450000.00, 150000.00),
(588, 0.00, '2025-11', 20, 0.00, 'DRAFT', 0.00, 0.00, 0.00, 0.00, 7500000.00, 0.00, 26.00, 0.00, 0.00, 0, 0.00, 0.00, 0.00),
(589, 10540384.68, '2025-11', 21, 12115384.68, 'DRAFT', 0.00, 1200000.00, 225000.00, 150000.00, 15000000.00, 15000000.00, 26.00, 21.00, 0.00, 0, 2625000.00, 450000.00, 150000.00),
(611, 0.00, '2025-11', 22, 0.00, 'DRAFT', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 26.00, 0.00, 0.00, 0, 0.00, 0.00, 0.00),
(612, 0.00, '2025-11', 23, 0.00, 'DRAFT', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 26.00, 0.00, 0.00, 0, 0.00, 0.00, 0.00);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `permissions`
--

DROP TABLE IF EXISTS `permissions`;
CREATE TABLE IF NOT EXISTS `permissions` (
  `permissionID` int NOT NULL AUTO_INCREMENT,
  `permissionname` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`permissionID`),
  UNIQUE KEY `permissionname` (`permissionname`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `permissions`
--

INSERT INTO `permissions` (`permissionID`, `permissionname`, `description`, `is_active`) VALUES
(1, 'LOGIN', 'Đăng nhập vào hệ thống', 1),
(2, 'CHANGE_PASS', 'Đổi mật khẩu / Quên mật khẩu', 1),
(3, 'PROFILE_VIEW', 'Xem thông tin tài khoản cá nhân', 1),
(4, 'PROFILE_UPDATE', 'Cập nhật thông tin cá nhân (SĐT, ĐC...)', 1),
(5, 'ACCOUNT_VIEW_LIST', 'Xem danh sách tài khoản/nhân sự', 1),
(6, 'ACCOUNT_VIEW_DETAIL', 'Xem chi tiết thông tin tài khoản khác', 1),
(7, 'ACCOUNT_CREATE', 'Tạo tài khoản/Hồ sơ nhân sự mới', 1),
(8, 'ACCOUNT_UPDATE', 'Sửa thông tin tài khoản/Hồ sơ nhân sự', 1),
(9, 'ACCOUNT_DELETE', 'Xóa hoặc Vô hiệu hóa tài khoản', 1),
(10, 'ACCOUNT_RESET_PASS', 'Cấp lại mật khẩu cho user', 1),
(11, 'ACCOUNT_PERMISSION', 'Thiết lập phân quyền truy cập', 1),
(12, 'SYS_LOG_VIEW', 'Xem lịch sử hoạt động hệ thống', 1),
(13, 'SYS_AUDIT_VIEW', 'Theo dõi thao tác tài khoản', 1),
(14, 'ATTENDANCE_VIEW_SELF', 'Xem dữ liệu chấm công của mình', 1),
(15, 'ATTENDANCE_REQ_CREATE', 'Yêu cầu bổ sung dữ liệu chấm công', 1),
(16, 'ATTENDANCE_VIEW_DEPT', 'Xem chấm công nhân viên phòng ban', 1),
(17, 'ATTENDANCE_VIEW_ALL', 'Xem chấm công toàn công ty', 1),
(18, 'ATTENDANCE_UPDATE', 'Sửa/Xóa dữ liệu chấm công', 1),
(19, 'ATTENDANCE_APPROVE', 'Duyệt/Từ chối yêu cầu bổ sung công', 1),
(20, 'ATTENDANCE_EXPORT', 'Xuất báo cáo chấm công', 1),
(21, 'SHIFT_VIEW', 'Xem lịch/ca làm việc', 1),
(22, 'SHIFT_ASSIGN', 'Phân ca/Sửa ca làm việc', 1),
(23, 'SHIFT_REGISTER', 'Đăng ký ca làm việc', 1),
(24, 'LEAVE_CREATE', 'Tạo yêu cầu nghỉ phép/Làm việc', 1),
(25, 'LEAVE_VIEW_SELF', 'Xem lịch sử nghỉ phép cá nhân', 1),
(26, 'LEAVE_CANCEL', 'Hủy yêu cầu nghỉ phép', 1),
(27, 'LEAVE_VIEW_DEPT', 'Xem danh sách nghỉ phép phòng ban', 1),
(28, 'LEAVE_VIEW_ALL', 'Xem danh sách nghỉ phép toàn công ty', 1),
(29, 'LEAVE_APPROVE', 'Duyệt/Từ chối đơn nghỉ phép', 1),
(30, 'PAYROLL_VIEW_SELF', 'Xem phiếu lương cá nhân', 1),
(31, 'PAYROLL_CALCULATE', 'Tính lương hàng tháng', 1),
(32, 'PAYROLL_VIEW_ALL', 'Xem danh sách lương toàn công ty', 1),
(33, 'PAYROLL_REWARD_VIEW', 'Xem danh sách Thưởng/Phạt', 1),
(34, 'PAYROLL_EXPORT', 'Xuất báo cáo lương', 1),
(35, 'CONTRACT_VIEW', 'Xem danh sách hợp đồng', 1),
(36, 'CONTRACT_CREATE', 'Thêm hợp đồng lao động', 1),
(37, 'CONTRACT_UPDATE', 'Cập nhật/Gia hạn hợp đồng', 1),
(38, 'CONTRACT_EXPORT', 'Xuất báo cáo hợp đồng', 1),
(39, 'EVALUATE_VIEW', 'Xem đánh giá quá trình làm việc', 1),
(40, 'EVALUATE_CREATE', 'Thực hiện đánh giá nhân viên', 1),
(41, 'SYS_MAIL_PAYROLL', 'Gửi mail phiếu lương', 1),
(42, 'SYS_MAIL_SCHEDULE', 'Gửi mail lịch làm việc', 1),
(43, 'SYS_MAIL_LEAVE', 'Gửi mail thông báo đơn phép', 1),
(44, 'SYS_MAIL_CONTRACT', 'Gửi mail cảnh báo hết hạn HĐ', 1),
(45, 'SYS_IMPORT_QR', 'Nhận dữ liệu chấm công QR', 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `requirementrules`
--

DROP TABLE IF EXISTS `requirementrules`;
CREATE TABLE IF NOT EXISTS `requirementrules` (
  `ruleID` int NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
  `requirementID` int NOT NULL COMMENT 'Liên kết tới schedule_requirements',
  `requiredskillGrade` int NOT NULL COMMENT 'Cấp kỹ năng yêu cầu (ví dụ: 3)',
  `minstaffcount` int NOT NULL DEFAULT '1' COMMENT 'Số lượng nhân viên TỐI THIỂU phải có kỹ năng này (ví dụ: 1)',
  PRIMARY KEY (`ruleID`),
  KEY `FK_rule_req` (`requirementID`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Quy tắc kỹ năng chi tiết cho một yêu cầu nhân sự';

--
-- Đang đổ dữ liệu cho bảng `requirementrules`
--

INSERT INTO `requirementrules` (`ruleID`, `requirementID`, `requiredskillGrade`, `minstaffcount`) VALUES
(7, 2, 3, 1),
(9, 3, 3, 1),
(11, 7, 1, 1),
(12, 6, 3, 0),
(14, 12, 3, 2),
(15, 12, 1, 1),
(16, 14, 1, 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `rewardpunishmentdecisions`
--

DROP TABLE IF EXISTS `rewardpunishmentdecisions`;
CREATE TABLE IF NOT EXISTS `rewardpunishmentdecisions` (
  `DecisionID` int NOT NULL AUTO_INCREMENT,
  `UserID` int NOT NULL,
  `Type` enum('REWARD','PUNISHMENT') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Khen thưởng hoặc Kỷ luật',
  `Reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Lý do',
  `DecisionDate` date NOT NULL COMMENT 'Ngày ra quyết định',
  `Amount` decimal(15,2) DEFAULT '0.00' COMMENT 'Số tiền',
  `IsTaxExempt` tinyint(1) DEFAULT '0' COMMENT '1=Miễn thuế TNCN, 0=Chịu thuế',
  `Status` enum('PENDING','APPROVED','PROCESSED','CANCELLED') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING' COMMENT 'Processed=Đã tính vào lương',
  PRIMARY KEY (`DecisionID`),
  KEY `FK_Decision_User` (`UserID`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `rewardpunishmentdecisions`
--

INSERT INTO `rewardpunishmentdecisions` (`DecisionID`, `UserID`, `Type`, `Reason`, `DecisionDate`, `Amount`, `IsTaxExempt`, `Status`) VALUES
(1, 10, 'PUNISHMENT', 'Vi phạm quy trình vận hành máy in làm hỏng vật tư', '2025-11-05', 500000.00, 0, 'PROCESSED'),
(2, 10, 'REWARD', 'Đạt thành tích nhân viên xuất sắc tháng 11', '2025-11-28', 1000000.00, 1, 'PROCESSED'),
(3, 6, 'PUNISHMENT', 'Đi muộn 3 lần trong tháng 11', '2025-11-25', 200000.00, 0, 'APPROVED'),
(4, 16, 'PUNISHMENT', 'Làm hỏng bản in số lượng lớn (Lỗi vận hành)', '2025-11-20', 1000000.00, 0, 'APPROVED'),
(5, 12, 'REWARD', 'Nhân viên CSKH có phản hồi tốt nhất tháng', '2025-11-30', 500000.00, 1, 'APPROVED'),
(6, 8, 'REWARD', 'Sáng kiến cải tiến quy trình đóng gói', '2025-11-15', 2000000.00, 1, 'APPROVED'),
(7, 5, 'REWARD', 'Hoàn thành dự án bảo trì sớm hạn', '2025-11-28', 3000000.00, 1, 'APPROVED'),
(8, 6, 'REWARD', 'Thưởng chuyên cần tháng 09/2025', '2025-09-30', 500000.00, 1, 'PROCESSED'),
(9, 8, 'REWARD', 'Thưởng vượt năng suất tháng 09/2025', '2025-09-30', 200000.00, 1, 'PROCESSED'),
(10, 8, 'PUNISHMENT', 'Vi phạm quy định giờ giấc (Đi trễ)', '2025-09-15', 100000.00, 0, 'PROCESSED');

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
(3, 1),
(4, 1),
(1, 2),
(2, 2),
(3, 2),
(4, 2),
(1, 3),
(2, 3),
(3, 3),
(4, 3),
(1, 4),
(2, 4),
(3, 4),
(4, 4),
(1, 5),
(2, 5),
(3, 5),
(1, 6),
(2, 6),
(3, 6),
(1, 7),
(2, 7),
(1, 8),
(2, 8),
(1, 9),
(1, 10),
(1, 11),
(1, 12),
(1, 13),
(4, 14),
(3, 15),
(3, 16),
(2, 17),
(2, 18),
(2, 19),
(3, 19),
(2, 20),
(3, 21),
(4, 21),
(3, 22),
(4, 23),
(3, 24),
(4, 24),
(3, 25),
(4, 25),
(4, 26),
(3, 27),
(2, 28),
(2, 29),
(3, 29),
(2, 30),
(3, 30),
(4, 30),
(2, 31),
(2, 32),
(2, 33),
(2, 34),
(2, 35),
(2, 36),
(2, 37),
(2, 38),
(3, 39),
(3, 40);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `salaryvariables`
--

DROP TABLE IF EXISTS `salaryvariables`;
CREATE TABLE IF NOT EXISTS `salaryvariables` (
  `VariableID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Description` text COLLATE utf8mb4_unicode_ci,
  `SQLQuery` text COLLATE utf8mb4_unicode_ci COMMENT 'Câu lệnh Native SQL để lấy giá trị',
  `rule_id` int DEFAULT NULL,
  `dsl_version_id` int DEFAULT NULL,
  `builderMetadata` json DEFAULT NULL COMMENT 'Lưu cấu hình JSON gốc từ UI để phục vụ việc load lại form sửa',
  PRIMARY KEY (`VariableID`),
  UNIQUE KEY `Code` (`Code`),
  KEY `fk_salaryvariables_rule_fix` (`rule_id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `salaryvariables`
--

INSERT INTO `salaryvariables` (`VariableID`, `Name`, `Code`, `Description`, `SQLQuery`, `rule_id`, `dsl_version_id`, `builderMetadata`) VALUES
(1, 'Lương cơ bản (HĐLĐ)', 'BASE_SALARY', 'Lấy từ bảng Contracts', 'SELECT basesalary FROM contracts WHERE userID = :userId AND Status = \'ACTIVE\' LIMIT 1', NULL, NULL, '{\"code\": \"BASE_SALARY\", \"mode\": \"RAW_SQL\", \"name\": \"Lương cơ bản (HĐLĐ)\", \"sqlQuery\": \"SELECT basesalary FROM contracts WHERE userID = :userId AND Status = \'ACTIVE\' LIMIT 1\", \"trueValue\": 0, \"falseValue\": 0, \"description\": \"Lấy từ bảng Contracts\"}'),
(2, 'Ngày công chuẩn tháng', 'STD_DAYS', 'Lấy từ MonthlyPayrollConfigs', 'SELECT StandardWorkDays FROM monthlypayrollconfigs WHERE Month = :month AND Year = :year', NULL, NULL, '{\"code\": \"STD_DAYS\", \"mode\": \"RAW_SQL\", \"name\": \"Ngày công chuẩn tháng\", \"sqlQuery\": \"SELECT StandardWorkDays FROM monthlypayrollconfigs WHERE Month = :month AND Year = :year\", \"trueValue\": 0, \"falseValue\": 0, \"description\": \"Lấy từ MonthlyPayrollConfigs\"}'),
(3, 'Ngày công đi làm thực tế', 'REAL_WORK_DAYS', 'Tổng số ngày có mặt + Nghỉ phép có lương', 'SELECT COUNT(*) FROM attendances WHERE userID = :userId AND status = \'PRESENT\' AND date BETWEEN :startDate AND :endDate', NULL, NULL, '{\"code\": \"REAL_WORK_DAYS\", \"mode\": \"RAW_SQL\", \"name\": \"Ngày công đi làm thực tế\", \"sqlQuery\": \"SELECT COUNT(*) FROM attendances WHERE userID = :userId AND status = \'PRESENT\' AND date BETWEEN :startDate AND :endDate\", \"trueValue\": 0, \"falseValue\": 0, \"description\": \"Tổng số ngày có mặt + Nghỉ phép có lương\"}'),
(4, 'Tổng tiền Phụ cấp', 'TOTAL_ALLOWANCE', 'Tổng bảng ContractAllowances', 'SELECT SUM(Amount) FROM contractallowances ca JOIN contracts c ON ca.ContractID = c.contractID WHERE c.userID = :userId AND c.Status = \'ACTIVE\'', NULL, NULL, '{\"code\": \"TOTAL_ALLOWANCE\", \"mode\": \"RAW_SQL\", \"name\": \"Tổng tiền Phụ cấp\", \"sqlQuery\": \"SELECT SUM(Amount) FROM contractallowances ca JOIN contracts c ON ca.ContractID = c.contractID WHERE c.userID = :userId AND c.Status = \'ACTIVE\'\", \"trueValue\": 0, \"falseValue\": 0, \"description\": \"Tổng bảng ContractAllowances\"}'),
(5, 'Tổng tiền Thưởng', 'TOTAL_REWARD', 'Tổng RewardPunishmentDecisions loại REWARD', 'SELECT SUM(Amount) FROM rewardpunishmentdecisions WHERE UserID = :userId AND Type = \'REWARD\' AND Status IN (\'APPROVED\',\'PROCESSED\') AND DecisionDate BETWEEN :startDate AND :endDate', NULL, NULL, '{\"code\": \"TOTAL_REWARD\", \"mode\": \"RAW_SQL\", \"name\": \"Tổng tiền Thưởng\", \"sqlQuery\": \"SELECT SUM(Amount) FROM rewardpunishmentdecisions WHERE UserID = :userId AND Type = \'REWARD\' AND Status IN (\'APPROVED\',\'PROCESSED\') AND DecisionDate BETWEEN :startDate AND :endDate\", \"trueValue\": 0, \"falseValue\": 0, \"description\": \"Tổng RewardPunishmentDecisions loại REWARD\"}'),
(6, 'Tổng tiền Phạt', 'TOTAL_PENALTY', 'Tổng RewardPunishmentDecisions loại PUNISHMENT', 'SELECT SUM(Amount) FROM rewardpunishmentdecisions WHERE UserID = :userId AND Type = \'PUNISHMENT\' AND Status IN (\'APPROVED\',\'PROCESSED\') AND DecisionDate BETWEEN :startDate AND :endDate', NULL, NULL, '{\"code\": \"TOTAL_PENALTY\", \"mode\": \"RAW_SQL\", \"name\": \"Tổng tiền Phạt\", \"sqlQuery\": \"SELECT SUM(Amount) FROM rewardpunishmentdecisions WHERE UserID = :userId AND Type = \'PUNISHMENT\' AND Status IN (\'APPROVED\',\'PROCESSED\') AND DecisionDate BETWEEN :startDate AND :endDate\", \"trueValue\": 0, \"falseValue\": 0, \"description\": \"Tổng RewardPunishmentDecisions loại PUNISHMENT\"}'),
(7, 'Giờ tăng ca', 'TOTAL_OT_CONVERTED', 'Tổng số giờ làm thêm', 'SELECT COALESCE(SUM(o.hours * ot.Rate), 0) FROM overtime o JOIN overtimetypes ot ON o.overtimetypeid = ot.OvertimeTypeID WHERE o.userid = :userId AND o.status = \'APPROVED\' AND o.date BETWEEN :startDate AND :endDate', NULL, NULL, '{\"code\": \"TOTAL_OT_CONVERTED\", \"mode\": \"RAW_SQL\", \"name\": \"Giờ tăng ca\", \"sqlQuery\": \"SELECT COALESCE(SUM(o.hours * ot.Rate), 0) FROM overtime o JOIN overtimetypes ot ON o.overtimetypeid = ot.OvertimeTypeID WHERE o.userid = :userId AND o.status = \'APPROVED\' AND o.date BETWEEN :startDate AND :endDate\", \"trueValue\": 0, \"falseValue\": 0, \"description\": \"Tổng số giờ làm thêm\"}'),
(8, 'Lương đóng bảo hiểm', 'INSURANCE_SALARY', 'Lấy từ bảng Contracts', 'SELECT InsuranceSalary FROM contracts WHERE userID = :userId AND Status = \'ACTIVE\' LIMIT 1', NULL, NULL, '{\"code\": \"INSURANCE_SALARY\", \"mode\": \"RAW_SQL\", \"name\": \"Lương đóng bảo hiểm\", \"sqlQuery\": \"SELECT InsuranceSalary FROM contracts WHERE userID = :userId AND Status = \'ACTIVE\' LIMIT 1\", \"trueValue\": 0, \"falseValue\": 0, \"description\": \"Lấy từ bảng Contracts\"}'),
(9, 'Tổng thu nhập chịu thuế', 'TAXABLE_INCOME', 'Tổng thu nhập - Các khoản miễn thuế', NULL, NULL, NULL, '{\"code\": \"TAXABLE_INCOME\", \"mode\": \"CALCULATED\", \"name\": \"Tổng thu nhập chịu thuế\", \"description\": \"Biến được tính toán tự động bởi hệ thống (Computed Variable)\"}'),
(10, 'Tổng thu nhập miễn thuế', 'TAX_EXEMPT_INCOME', 'Tổng TaxFreeAmount + Chênh lệch OT', '\nSELECT \n    (SELECT COALESCE(SUM(TaxFreeAmount), 0) FROM contractallowances ca JOIN contracts c ON ca.ContractID = c.contractID WHERE c.userID = :userId AND c.Status = \'ACTIVE\')\n    +\n    (SELECT COALESCE(SUM(Amount), 0) FROM rewardpunishmentdecisions WHERE UserID = :userId AND Type = \'REWARD\' AND Status = \'APPROVED\' AND IsTaxExempt = 1 AND DecisionDate BETWEEN :startDate AND :endDate)\n', NULL, NULL, '{\"code\": \"TAX_EXEMPT_INCOME\", \"mode\": \"RAW_SQL\", \"name\": \"Tổng thu nhập miễn thuế\", \"sqlQuery\": \"\\nSELECT \\n    (SELECT COALESCE(SUM(TaxFreeAmount), 0) FROM contractallowances ca JOIN contracts c ON ca.ContractID = c.contractID WHERE c.userID = :userId AND c.Status = \'ACTIVE\')\\n    +\\n    (SELECT COALESCE(SUM(Amount), 0) FROM rewardpunishmentdecisions WHERE UserID = :userId AND Type = \'REWARD\' AND Status = \'APPROVED\' AND IsTaxExempt = 1 AND DecisionDate BETWEEN :startDate AND :endDate)\\n\", \"trueValue\": 0, \"falseValue\": 0, \"description\": \"Tổng TaxFreeAmount + Chênh lệch OT\"}'),
(11, 'Giảm trừ gia cảnh', 'FAMILY_DEDUCTION', '11tr + (Người phụ thuộc * 4.4tr)', '\nSELECT \n    (SELECT Value FROM taxsettings WHERE SettingKey = \'PERSONAL_DEDUCTION\' ORDER BY EffectiveDate DESC LIMIT 1) \n    + \n    (\n      (SELECT COALESCE(COUNT(*), 0) FROM dependents WHERE userID = :userId AND istaxdeductible = 1) \n      * (SELECT Value FROM taxsettings WHERE SettingKey = \'DEPENDENT_DEDUCTION\' ORDER BY EffectiveDate DESC LIMIT 1)\n    )\n', NULL, NULL, '{\"code\": \"FAMILY_DEDUCTION\", \"mode\": \"RAW_SQL\", \"name\": \"Giảm trừ gia cảnh\", \"sqlQuery\": \"\\nSELECT \\n    (SELECT Value FROM taxsettings WHERE SettingKey = \'PERSONAL_DEDUCTION\' ORDER BY EffectiveDate DESC LIMIT 1) \\n    + \\n    (\\n      (SELECT COALESCE(COUNT(*), 0) FROM dependents WHERE userID = :userId AND istaxdeductible = 1) \\n      * (SELECT Value FROM taxsettings WHERE SettingKey = \'DEPENDENT_DEDUCTION\' ORDER BY EffectiveDate DESC LIMIT 1)\\n    )\\n\", \"trueValue\": 0, \"falseValue\": 0, \"description\": \"11tr + (Người phụ thuộc * 4.4tr)\"}'),
(12, 'Thu nhập tính thuế', 'ASSESSABLE_INCOME', 'Thu nhập chịu thuế - Bảo hiểm - Giảm trừ gia cảnh', NULL, NULL, NULL, '{\"code\": \"ASSESSABLE_INCOME\", \"mode\": \"CALCULATED\", \"name\": \"Thu nhập tính thuế\", \"description\": \"Biến được tính toán tự động bởi hệ thống (Computed Variable)\"}'),
(13, 'Số người phụ thuộc', 'DEPENDENT_COUNT', 'Lấy từ bảng dependents', 'SELECT COALESCE(COUNT(*), 0) FROM dependents WHERE userID = :userId AND istaxdeductible = 1', NULL, NULL, '{\"code\": \"DEPENDENT_COUNT\", \"mode\": \"RAW_SQL\", \"name\": \"Số người phụ thuộc\", \"sqlQuery\": \"SELECT COALESCE(COUNT(*), 0) FROM dependents WHERE userID = :userId AND istaxdeductible = 1\", \"trueValue\": 0, \"falseValue\": 0, \"description\": \"Lấy từ bảng dependents\"}'),
(14, 'Lương cơ sở nhà nước', 'BASIC_SALARY_STATE', 'Lấy từ bảng TaxSettings', 'SELECT Value FROM taxsettings WHERE SettingKey = \'BASIC_SALARY_STATE\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1', NULL, NULL, '{\"code\": \"BASIC_SALARY_STATE\", \"mode\": \"RAW_SQL\", \"name\": \"Lương cơ sở nhà nước\", \"sqlQuery\": \"SELECT Value FROM taxsettings WHERE SettingKey = \'BASIC_SALARY_STATE\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1\", \"trueValue\": 0, \"falseValue\": 0, \"description\": \"Lấy từ bảng TaxSettings\"}'),
(15, 'Lương tối thiểu vùng', 'REGION_MIN_SALARY', 'Lấy từ bảng TaxSettings', 'SELECT Value FROM taxsettings WHERE SettingKey = \'REGION_MIN_SALARY\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1', NULL, NULL, '{\"code\": \"REGION_MIN_SALARY\", \"mode\": \"RAW_SQL\", \"name\": \"Lương tối thiểu vùng\", \"sqlQuery\": \"SELECT Value FROM taxsettings WHERE SettingKey = \'REGION_MIN_SALARY\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1\", \"trueValue\": 0, \"falseValue\": 0, \"description\": \"Lấy từ bảng TaxSettings\"}'),
(16, 'Hệ số trần bảo hiểm', 'INSURANCE_CAP_MULTIPLIER', 'Lấy từ bảng TaxSettings (thường là 20 lần)', 'SELECT Value FROM taxsettings WHERE SettingKey = \'INSURANCE_CAP_MULTIPLIER\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1', NULL, NULL, '{\"code\": \"INSURANCE_CAP_MULTIPLIER\", \"mode\": \"RAW_SQL\", \"name\": \"Hệ số trần bảo hiểm\", \"sqlQuery\": \"SELECT Value FROM taxsettings WHERE SettingKey = \'INSURANCE_CAP_MULTIPLIER\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1\", \"trueValue\": 0, \"falseValue\": 0, \"description\": \"Lấy từ bảng TaxSettings (thường là 20 lần)\"}'),
(17, 'Giới hạn ăn ca miễn thuế', 'LUNCH_ALLOWANCE_LIMIT', 'Lấy từ bảng TaxSettings', 'SELECT Value FROM taxsettings WHERE SettingKey = \'LUNCH_ALLOWANCE_LIMIT\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1', NULL, NULL, '{\"code\": \"LUNCH_ALLOWANCE_LIMIT\", \"mode\": \"RAW_SQL\", \"name\": \"Giới hạn ăn ca miễn thuế\", \"sqlQuery\": \"SELECT Value FROM taxsettings WHERE SettingKey = \'LUNCH_ALLOWANCE_LIMIT\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1\", \"trueValue\": 0, \"falseValue\": 0, \"description\": \"Lấy từ bảng TaxSettings\"}'),
(25, 'Mức giảm trừ bản thân', 'PERSONAL_DEDUCTION', 'Lấy từ bảng TaxSettings', 'SELECT Value FROM taxsettings WHERE SettingKey = \'PERSONAL_DEDUCTION\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1', NULL, NULL, NULL),
(26, 'Mức giảm trừ người phụ thuộc', 'DEPENDENT_DEDUCTION', 'Lấy từ bảng TaxSettings', 'SELECT Value FROM taxsettings WHERE SettingKey = \'DEPENDENT_DEDUCTION\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1', NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `salary_rule`
--

DROP TABLE IF EXISTS `salary_rule`;
CREATE TABLE IF NOT EXISTS `salary_rule` (
  `rule_id` int NOT NULL AUTO_INCREMENT,
  `rule_code` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `status` enum('DRAFT','APPROVED','RETIRED') COLLATE utf8mb4_unicode_ci DEFAULT 'DRAFT',
  `current_version_id` int DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `priority` int DEFAULT '10',
  PRIMARY KEY (`rule_id`),
  UNIQUE KEY `rule_code` (`rule_code`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `salary_rule`
--

INSERT INTO `salary_rule` (`rule_id`, `rule_code`, `name`, `description`, `status`, `current_version_id`, `created_at`, `priority`) VALUES
(1, 'WORK_SALARY', 'Lương theo ngày công (gồm PC)', NULL, 'APPROVED', 1, '2025-12-01 01:47:36', 1),
(2, 'INSURANCE_AMT', 'Tiền bảo hiểm (10.5%)', NULL, 'RETIRED', 2, '2025-12-01 01:47:36', 2),
(3, 'TOTAL_INCOME', 'Tổng thu nhập thực tế (Gross)', NULL, 'APPROVED', 3, '2025-12-01 01:47:36', 3),
(4, 'TAXABLE_INCOME', 'Thu nhập tính thuế', NULL, 'APPROVED', 16, '2025-12-01 01:47:36', 5),
(5, 'PIT_TAX', 'Thuế TNCN', NULL, 'APPROVED', 5, '2025-12-01 01:47:36', 6),
(6, 'NET_SALARY', 'Lương thực lĩnh (NET)', NULL, 'APPROVED', 17, '2025-12-01 01:47:36', 99),
(19, 'ATTENDANCE_BONUS', 'Thưởng chuyên cần', NULL, 'APPROVED', 7, NULL, 10),
(20, 'BHXH_EMP', 'BHXH Nhân viên (8%)', NULL, 'APPROVED', 8, '2025-12-02 21:00:44', 4),
(21, 'BHYT_EMP', 'BHYT Nhân viên (1.5%)', NULL, 'APPROVED', 9, '2025-12-02 21:00:44', 4),
(22, 'BHTN_EMP', 'BHTN Nhân viên (1%)', NULL, 'APPROVED', 10, '2025-12-02 21:00:44', 4),
(24, 'BHXH_COMP', 'BHXH Công ty chi trả (17.5%)', 'Phần BHXH doanh nghiệp đóng', 'APPROVED', 18, '2025-12-11 14:48:14', 90),
(25, 'BHYT_COMP', 'BHYT Công ty chi trả (3%)', 'Phần BHYT doanh nghiệp đóng', 'APPROVED', 19, '2025-12-11 14:48:14', 90),
(26, 'BHTN_COMP', 'BHTN Công ty chi trả (1%)', 'Phần BHTN doanh nghiệp đóng', 'APPROVED', 20, '2025-12-11 14:48:14', 90);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `salary_rule_version`
--

DROP TABLE IF EXISTS `salary_rule_version`;
CREATE TABLE IF NOT EXISTS `salary_rule_version` (
  `version_id` int NOT NULL AUTO_INCREMENT,
  `rule_id` int DEFAULT NULL,
  `version_number` int DEFAULT NULL,
  `dsl_json` json DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`version_id`),
  KEY `rule_id` (`rule_id`)
) ENGINE=MyISAM AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `salary_rule_version`
--

INSERT INTO `salary_rule_version` (`version_id`, `rule_id`, `version_number`, `dsl_json`, `created_at`) VALUES
(1, 1, 1, '{\"left\": {\"left\": {\"left\": {\"name\": \"BASE_SALARY\", \"type\": \"VARIABLE\"}, \"type\": \"ADD\", \"right\": {\"name\": \"TOTAL_ALLOWANCE\", \"type\": \"VARIABLE\"}}, \"type\": \"DIV\", \"right\": {\"name\": \"STD_DAYS\", \"type\": \"VARIABLE\"}}, \"type\": \"MUL\", \"right\": {\"name\": \"REAL_WORK_DAYS\", \"type\": \"VARIABLE\"}}', '2025-12-01 01:47:36'),
(2, 2, 1, '{\"left\": {\"name\": \"INSURANCE_SALARY\", \"type\": \"VARIABLE\"}, \"type\": \"MUL\", \"right\": {\"type\": \"CONSTANT\", \"value\": 0.105}}', '2025-12-01 01:47:36'),
(3, 3, 1, '{\"left\": {\"left\": {\"left\": {\"name\": \"WORK_SALARY\", \"type\": \"REFERENCE\"}, \"type\": \"ADD\", \"right\": {\"name\": \"TOTAL_REWARD\", \"type\": \"VARIABLE\"}}, \"type\": \"ADD\", \"right\": {\"name\": \"TOTAL_OT_HOURS_CONVERTED\", \"type\": \"VARIABLE\"}}, \"type\": \"SUB\", \"right\": {\"name\": \"TOTAL_PENALTY\", \"type\": \"VARIABLE\"}}', '2025-12-01 01:47:36'),
(4, 4, 1, '{\"left\": {\"left\": {\"left\": {\"name\": \"TOTAL_INCOME\", \"type\": \"REFERENCE\"}, \"type\": \"SUB\", \"right\": {\"name\": \"TAX_EXEMPT_INCOME\", \"type\": \"VARIABLE\"}}, \"type\": \"SUB\", \"right\": {\"name\": \"INSURANCE_AMT\", \"type\": \"REFERENCE\"}}, \"type\": \"SUB\", \"right\": {\"left\": {\"name\": \"PERSONAL_DEDUCTION\", \"type\": \"VARIABLE\"}, \"type\": \"ADD\", \"right\": {\"left\": {\"name\": \"DEPENDENT_COUNT\", \"type\": \"VARIABLE\"}, \"type\": \"MUL\", \"right\": {\"name\": \"DEPENDENT_DEDUCTION\", \"type\": \"VARIABLE\"}}}}', '2025-12-01 01:47:36'),
(5, 5, 1, '{\"type\": \"IF_ELSE\", \"condition\": {\"left\": {\"name\": \"TAXABLE_INCOME\", \"type\": \"REFERENCE\"}, \"type\": \"GT\", \"right\": {\"type\": \"CONSTANT\", \"value\": 0}}, \"true_case\": {\"type\": \"IF_ELSE\", \"condition\": {\"left\": {\"name\": \"TAXABLE_INCOME\", \"type\": \"REFERENCE\"}, \"type\": \"GT\", \"right\": {\"type\": \"CONSTANT\", \"value\": 80000000}}, \"true_case\": {\"left\": {\"left\": {\"name\": \"TAXABLE_INCOME\", \"type\": \"REFERENCE\"}, \"type\": \"MUL\", \"right\": {\"type\": \"CONSTANT\", \"value\": 0.35}}, \"type\": \"SUB\", \"right\": {\"type\": \"CONSTANT\", \"value\": 9850000}}, \"false_case\": {\"type\": \"IF_ELSE\", \"condition\": {\"left\": {\"name\": \"TAXABLE_INCOME\", \"type\": \"REFERENCE\"}, \"type\": \"GT\", \"right\": {\"type\": \"CONSTANT\", \"value\": 52000000}}, \"true_case\": {\"left\": {\"left\": {\"name\": \"TAXABLE_INCOME\", \"type\": \"REFERENCE\"}, \"type\": \"MUL\", \"right\": {\"type\": \"CONSTANT\", \"value\": 0.3}}, \"type\": \"SUB\", \"right\": {\"type\": \"CONSTANT\", \"value\": 5850000}}, \"false_case\": {\"type\": \"IF_ELSE\", \"condition\": {\"left\": {\"name\": \"TAXABLE_INCOME\", \"type\": \"REFERENCE\"}, \"type\": \"GT\", \"right\": {\"type\": \"CONSTANT\", \"value\": 32000000}}, \"true_case\": {\"left\": {\"left\": {\"name\": \"TAXABLE_INCOME\", \"type\": \"REFERENCE\"}, \"type\": \"MUL\", \"right\": {\"type\": \"CONSTANT\", \"value\": 0.25}}, \"type\": \"SUB\", \"right\": {\"type\": \"CONSTANT\", \"value\": 3250000}}, \"false_case\": {\"type\": \"IF_ELSE\", \"condition\": {\"left\": {\"name\": \"TAXABLE_INCOME\", \"type\": \"REFERENCE\"}, \"type\": \"GT\", \"right\": {\"type\": \"CONSTANT\", \"value\": 18000000}}, \"true_case\": {\"left\": {\"left\": {\"name\": \"TAXABLE_INCOME\", \"type\": \"REFERENCE\"}, \"type\": \"MUL\", \"right\": {\"type\": \"CONSTANT\", \"value\": 0.2}}, \"type\": \"SUB\", \"right\": {\"type\": \"CONSTANT\", \"value\": 1650000}}, \"false_case\": {\"type\": \"IF_ELSE\", \"condition\": {\"left\": {\"name\": \"TAXABLE_INCOME\", \"type\": \"REFERENCE\"}, \"type\": \"GT\", \"right\": {\"type\": \"CONSTANT\", \"value\": 10000000}}, \"true_case\": {\"left\": {\"left\": {\"name\": \"TAXABLE_INCOME\", \"type\": \"REFERENCE\"}, \"type\": \"MUL\", \"right\": {\"type\": \"CONSTANT\", \"value\": 0.15}}, \"type\": \"SUB\", \"right\": {\"type\": \"CONSTANT\", \"value\": 750000}}, \"false_case\": {\"type\": \"IF_ELSE\", \"condition\": {\"left\": {\"name\": \"TAXABLE_INCOME\", \"type\": \"REFERENCE\"}, \"type\": \"GT\", \"right\": {\"type\": \"CONSTANT\", \"value\": 5000000}}, \"true_case\": {\"left\": {\"left\": {\"name\": \"TAXABLE_INCOME\", \"type\": \"REFERENCE\"}, \"type\": \"MUL\", \"right\": {\"type\": \"CONSTANT\", \"value\": 0.1}}, \"type\": \"SUB\", \"right\": {\"type\": \"CONSTANT\", \"value\": 250000}}, \"false_case\": {\"left\": {\"name\": \"TAXABLE_INCOME\", \"type\": \"REFERENCE\"}, \"type\": \"MUL\", \"right\": {\"type\": \"CONSTANT\", \"value\": 0.05}}}}}}}}, \"false_case\": {\"type\": \"CONSTANT\", \"value\": 0}}', '2025-12-01 01:47:36'),
(6, 6, 1, '{\"left\": {\"left\": {\"name\": \"TOTAL_INCOME\", \"type\": \"REFERENCE\"}, \"type\": \"SUB\", \"right\": {\"name\": \"INSURANCE_AMT\", \"type\": \"REFERENCE\"}}, \"type\": \"SUB\", \"right\": {\"name\": \"PIT_TAX\", \"type\": \"REFERENCE\"}}', '2025-12-01 01:47:36'),
(7, 19, 1, '{\"type\": \"IF_ELSE\", \"condition\": {\"left\": {\"name\": \"LATE_COUNT\", \"type\": \"VARIABLE\"}, \"type\": \"LTE\", \"right\": {\"type\": \"CONSTANT\", \"value\": 0}}, \"true_case\": {\"type\": \"CONSTANT\", \"value\": 50000}, \"false_case\": {\"type\": \"CONSTANT\", \"value\": 0}}', '2025-12-02 14:36:58'),
(8, 20, 1, '{\"left\": {\"name\": \"INSURANCE_SALARY\", \"type\": \"VARIABLE\"}, \"type\": \"MUL\", \"right\": {\"type\": \"CONSTANT\", \"value\": 0.08}}', '2025-12-02 21:00:44'),
(9, 21, 1, '{\"left\": {\"name\": \"INSURANCE_SALARY\", \"type\": \"VARIABLE\"}, \"type\": \"MUL\", \"right\": {\"type\": \"CONSTANT\", \"value\": 0.015}}', '2025-12-02 21:00:44'),
(10, 22, 1, '{\"left\": {\"name\": \"INSURANCE_SALARY\", \"type\": \"VARIABLE\"}, \"type\": \"MUL\", \"right\": {\"type\": \"CONSTANT\", \"value\": 0.01}}', '2025-12-02 21:00:44'),
(11, 4, 2, '{\"left\": {\"left\": {\"left\": {\"left\": {\"left\": {\"name\": \"TOTAL_INCOME\", \"type\": \"REFERENCE\"}, \"type\": \"SUB\", \"right\": {\"name\": \"TAX_EXEMPT_INCOME\", \"type\": \"VARIABLE\"}}, \"type\": \"SUB\", \"right\": {\"name\": \"BHXH_EMP\", \"type\": \"REFERENCE\"}}, \"type\": \"SUB\", \"right\": {\"name\": \"BHYT_EMP\", \"type\": \"REFERENCE\"}}, \"type\": \"SUB\", \"right\": {\"name\": \"BHTN_EMP\", \"type\": \"REFERENCE\"}}, \"type\": \"SUB\", \"right\": {\"left\": {\"name\": \"PERSONAL_DEDUCTION\", \"type\": \"VARIABLE\"}, \"type\": \"ADD\", \"right\": {\"left\": {\"name\": \"DEPENDENT_COUNT\", \"type\": \"VARIABLE\"}, \"type\": \"MUL\", \"right\": {\"name\": \"DEPENDENT_DEDUCTION\", \"type\": \"VARIABLE\"}}}}', '2025-12-02 21:00:44'),
(12, 6, 2, '{\"left\": {\"left\": {\"left\": {\"left\": {\"left\": {\"name\": \"TOTAL_INCOME\", \"type\": \"REFERENCE\"}, \"type\": \"SUB\", \"right\": {\"name\": \"BHXH_EMP\", \"type\": \"REFERENCE\"}}, \"type\": \"SUB\", \"right\": {\"name\": \"BHYT_EMP\", \"type\": \"REFERENCE\"}}, \"type\": \"SUB\", \"right\": {\"name\": \"BHTN_EMP\", \"type\": \"REFERENCE\"}}, \"type\": \"SUB\", \"right\": {\"name\": \"PIT_TAX\", \"type\": \"REFERENCE\"}}, \"type\": \"SUB\", \"right\": {\"name\": \"TOTAL_PENALTY\", \"type\": \"VARIABLE\"}}', '2025-12-02 21:00:44'),
(13, 20, 1, '{\"left\": {\"name\": \"INSURANCE_SALARY\", \"type\": \"VARIABLE\"}, \"type\": \"MUL\", \"right\": {\"type\": \"CONSTANT\", \"value\": 0.08}}', '2025-12-02 21:09:24'),
(14, 21, 1, '{\"left\": {\"name\": \"INSURANCE_SALARY\", \"type\": \"VARIABLE\"}, \"type\": \"MUL\", \"right\": {\"type\": \"CONSTANT\", \"value\": 0.015}}', '2025-12-02 21:09:24'),
(15, 22, 1, '{\"left\": {\"name\": \"INSURANCE_SALARY\", \"type\": \"VARIABLE\"}, \"type\": \"MUL\", \"right\": {\"type\": \"CONSTANT\", \"value\": 0.01}}', '2025-12-02 21:09:24'),
(16, 4, 3, '{\"left\": {\"left\": {\"left\": {\"left\": {\"left\": {\"name\": \"TOTAL_INCOME\", \"type\": \"REFERENCE\"}, \"type\": \"SUB\", \"right\": {\"name\": \"TAX_EXEMPT_INCOME\", \"type\": \"VARIABLE\"}}, \"type\": \"SUB\", \"right\": {\"name\": \"BHXH_EMP\", \"type\": \"REFERENCE\"}}, \"type\": \"SUB\", \"right\": {\"name\": \"BHYT_EMP\", \"type\": \"REFERENCE\"}}, \"type\": \"SUB\", \"right\": {\"name\": \"BHTN_EMP\", \"type\": \"REFERENCE\"}}, \"type\": \"SUB\", \"right\": {\"left\": {\"name\": \"PERSONAL_DEDUCTION\", \"type\": \"VARIABLE\"}, \"type\": \"ADD\", \"right\": {\"left\": {\"name\": \"DEPENDENT_COUNT\", \"type\": \"VARIABLE\"}, \"type\": \"MUL\", \"right\": {\"name\": \"DEPENDENT_DEDUCTION\", \"type\": \"VARIABLE\"}}}}', '2025-12-02 21:09:24'),
(17, 6, 3, '{\"left\": {\"left\": {\"left\": {\"left\": {\"left\": {\"name\": \"TOTAL_INCOME\", \"type\": \"REFERENCE\"}, \"type\": \"SUB\", \"right\": {\"name\": \"BHXH_EMP\", \"type\": \"REFERENCE\"}}, \"type\": \"SUB\", \"right\": {\"name\": \"BHYT_EMP\", \"type\": \"REFERENCE\"}}, \"type\": \"SUB\", \"right\": {\"name\": \"BHTN_EMP\", \"type\": \"REFERENCE\"}}, \"type\": \"SUB\", \"right\": {\"name\": \"PIT_TAX\", \"type\": \"REFERENCE\"}}, \"type\": \"SUB\", \"right\": {\"name\": \"TOTAL_PENALTY\", \"type\": \"VARIABLE\"}}', '2025-12-02 21:09:24'),
(18, 24, 1, '{\"left\": {\"name\": \"INSURANCE_SALARY\", \"type\": \"VARIABLE\"}, \"type\": \"MUL\", \"right\": {\"type\": \"CONSTANT\", \"value\": 0.175}}', '2025-12-11 14:48:43'),
(19, 25, 1, '{\"left\": {\"name\": \"INSURANCE_SALARY\", \"type\": \"VARIABLE\"}, \"type\": \"MUL\", \"right\": {\"type\": \"CONSTANT\", \"value\": 0.03}}', '2025-12-11 14:48:43'),
(20, 26, 1, '{\"left\": {\"name\": \"INSURANCE_SALARY\", \"type\": \"VARIABLE\"}, \"type\": \"MUL\", \"right\": {\"type\": \"CONSTANT\", \"value\": 0.01}}', '2025-12-11 14:48:43');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `salary_variable_cache`
--

DROP TABLE IF EXISTS `salary_variable_cache`;
CREATE TABLE IF NOT EXISTS `salary_variable_cache` (
  `cache_id` int NOT NULL AUTO_INCREMENT,
  `variable_id` int DEFAULT NULL COMMENT 'FK tới salaryvariables (Input)',
  `rule_id` int DEFAULT NULL COMMENT 'FK tới salary_rule (Output/Formula)',
  `employee_id` int DEFAULT NULL,
  `payperiod` varchar(7) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `value` decimal(20,2) DEFAULT NULL,
  `evaluated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`cache_id`),
  UNIQUE KEY `UQ_Cache_Var` (`variable_id`,`employee_id`,`payperiod`),
  UNIQUE KEY `UQ_Cache_Rule` (`rule_id`,`employee_id`,`payperiod`)
) ENGINE=MyISAM AUTO_INCREMENT=668 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `salary_variable_cache`
--

INSERT INTO `salary_variable_cache` (`cache_id`, `variable_id`, `rule_id`, `employee_id`, `payperiod`, `value`, `evaluated_at`) VALUES
(1, 26, NULL, 1, '2025-11', 4400000.00, '2025-12-11 15:24:38'),
(2, 2, NULL, 1, '2025-11', 26.00, '2025-12-11 15:24:38'),
(3, 7, NULL, 1, '2025-11', 0.00, '2025-12-11 15:24:38'),
(4, 15, NULL, 1, '2025-11', 4960000.00, '2025-12-11 15:24:38'),
(5, 4, NULL, 1, '2025-11', 3730000.00, '2025-12-11 15:24:38'),
(6, 16, NULL, 1, '2025-11', 20.00, '2025-12-11 15:24:38'),
(7, 25, NULL, 1, '2025-11', 11000000.00, '2025-12-11 15:24:38'),
(8, 5, NULL, 1, '2025-11', 0.00, '2025-12-11 15:24:38'),
(9, 14, NULL, 1, '2025-11', 2340000.00, '2025-12-11 15:24:38'),
(10, 13, NULL, 1, '2025-11', 2.00, '2025-12-11 15:24:38'),
(11, 17, NULL, 1, '2025-11', 1000000.00, '2025-12-11 15:24:38'),
(12, 3, NULL, 1, '2025-11', 0.00, '2025-12-11 15:24:38'),
(13, 6, NULL, 1, '2025-11', 0.00, '2025-12-11 15:24:38'),
(14, 8, NULL, 1, '2025-11', 30000000.00, '2025-12-11 15:24:38'),
(15, 10, NULL, 1, '2025-11', 730000.00, '2025-12-11 15:24:38'),
(16, 1, NULL, 1, '2025-11', 30000000.00, '2025-12-11 15:24:38'),
(17, 11, NULL, 1, '2025-11', 19800000.00, '2025-12-11 15:24:38'),
(18, NULL, 1, 1, '2025-11', 0.00, '2025-12-11 15:24:38'),
(19, NULL, 3, 1, '2025-11', 0.00, '2025-12-11 15:24:38'),
(20, NULL, 20, 1, '2025-11', 2400000.00, '2025-12-11 15:24:38'),
(21, NULL, 21, 1, '2025-11', 450000.00, '2025-12-11 15:24:38'),
(22, NULL, 22, 1, '2025-11', 300000.00, '2025-12-11 15:24:38'),
(23, NULL, 4, 1, '2025-11', -23680000.00, '2025-12-11 15:24:38'),
(24, NULL, 5, 1, '2025-11', 0.00, '2025-12-11 15:24:38'),
(25, NULL, 19, 1, '2025-11', 0.00, '2025-12-11 15:24:38'),
(26, NULL, 24, 1, '2025-11', 5250000.00, '2025-12-11 15:24:38'),
(27, NULL, 25, 1, '2025-11', 900000.00, '2025-12-11 15:24:38'),
(28, NULL, 26, 1, '2025-11', 300000.00, '2025-12-11 15:24:38'),
(29, NULL, 6, 1, '2025-11', -3150000.00, '2025-12-11 15:24:38'),
(30, 26, NULL, 2, '2025-11', 4400000.00, '2025-12-11 15:24:38'),
(31, 2, NULL, 2, '2025-11', 26.00, '2025-12-11 15:24:38'),
(32, 7, NULL, 2, '2025-11', 0.00, '2025-12-11 15:24:38'),
(33, 15, NULL, 2, '2025-11', 4960000.00, '2025-12-11 15:24:38'),
(34, 4, NULL, 2, '2025-11', 730000.00, '2025-12-11 15:24:38'),
(35, 16, NULL, 2, '2025-11', 20.00, '2025-12-11 15:24:38'),
(36, 25, NULL, 2, '2025-11', 11000000.00, '2025-12-11 15:24:38'),
(37, 5, NULL, 2, '2025-11', 0.00, '2025-12-11 15:24:38'),
(38, 14, NULL, 2, '2025-11', 2340000.00, '2025-12-11 15:24:38'),
(39, 13, NULL, 2, '2025-11', 1.00, '2025-12-11 15:24:38'),
(40, 17, NULL, 2, '2025-11', 1000000.00, '2025-12-11 15:24:38'),
(41, 3, NULL, 2, '2025-11', 0.00, '2025-12-11 15:24:38'),
(42, 6, NULL, 2, '2025-11', 0.00, '2025-12-11 15:24:38'),
(43, 8, NULL, 2, '2025-11', 15000000.00, '2025-12-11 15:24:38'),
(44, 10, NULL, 2, '2025-11', 730000.00, '2025-12-11 15:24:38'),
(45, 1, NULL, 2, '2025-11', 15000000.00, '2025-12-11 15:24:38'),
(46, 11, NULL, 2, '2025-11', 15400000.00, '2025-12-11 15:24:38'),
(47, NULL, 1, 2, '2025-11', 0.00, '2025-12-11 15:24:38'),
(48, NULL, 3, 2, '2025-11', 0.00, '2025-12-11 15:24:38'),
(49, NULL, 20, 2, '2025-11', 1200000.00, '2025-12-11 15:24:38'),
(50, NULL, 21, 2, '2025-11', 225000.00, '2025-12-11 15:24:38'),
(51, NULL, 22, 2, '2025-11', 150000.00, '2025-12-11 15:24:38'),
(52, NULL, 4, 2, '2025-11', -17705000.00, '2025-12-11 15:24:38'),
(53, NULL, 5, 2, '2025-11', 0.00, '2025-12-11 15:24:38'),
(54, NULL, 19, 2, '2025-11', 0.00, '2025-12-11 15:24:38'),
(55, NULL, 24, 2, '2025-11', 2625000.00, '2025-12-11 15:24:38'),
(56, NULL, 25, 2, '2025-11', 450000.00, '2025-12-11 15:24:38'),
(57, NULL, 26, 2, '2025-11', 150000.00, '2025-12-11 15:24:38'),
(58, NULL, 6, 2, '2025-11', -1575000.00, '2025-12-11 15:24:38'),
(59, 26, NULL, 3, '2025-11', 4400000.00, '2025-12-11 15:24:38'),
(60, 2, NULL, 3, '2025-11', 26.00, '2025-12-11 15:24:38'),
(61, 7, NULL, 3, '2025-11', 0.00, '2025-12-11 15:24:38'),
(62, 15, NULL, 3, '2025-11', 4960000.00, '2025-12-11 15:24:38'),
(63, 4, NULL, 3, '2025-11', 3730000.00, '2025-12-11 15:24:38'),
(64, 16, NULL, 3, '2025-11', 20.00, '2025-12-11 15:24:38'),
(65, 25, NULL, 3, '2025-11', 11000000.00, '2025-12-11 15:24:38'),
(66, 5, NULL, 3, '2025-11', 0.00, '2025-12-11 15:24:38'),
(67, 14, NULL, 3, '2025-11', 2340000.00, '2025-12-11 15:24:38'),
(68, 13, NULL, 3, '2025-11', 1.00, '2025-12-11 15:24:38'),
(69, 17, NULL, 3, '2025-11', 1000000.00, '2025-12-11 15:24:38'),
(70, 3, NULL, 3, '2025-11', 0.00, '2025-12-11 15:24:38'),
(71, 6, NULL, 3, '2025-11', 0.00, '2025-12-11 15:24:38'),
(72, 8, NULL, 3, '2025-11', 25000000.00, '2025-12-11 15:24:38'),
(73, 10, NULL, 3, '2025-11', 730000.00, '2025-12-11 15:24:38'),
(74, 1, NULL, 3, '2025-11', 25000000.00, '2025-12-11 15:24:38'),
(75, 11, NULL, 3, '2025-11', 15400000.00, '2025-12-11 15:24:38'),
(76, NULL, 1, 3, '2025-11', 0.00, '2025-12-11 15:24:38'),
(77, NULL, 3, 3, '2025-11', 0.00, '2025-12-11 15:24:38'),
(78, NULL, 20, 3, '2025-11', 2000000.00, '2025-12-11 15:24:38'),
(79, NULL, 21, 3, '2025-11', 375000.00, '2025-12-11 15:24:38'),
(80, NULL, 22, 3, '2025-11', 250000.00, '2025-12-11 15:24:38'),
(81, NULL, 4, 3, '2025-11', -18755000.00, '2025-12-11 15:24:38'),
(82, NULL, 5, 3, '2025-11', 0.00, '2025-12-11 15:24:38'),
(83, NULL, 19, 3, '2025-11', 0.00, '2025-12-11 15:24:38'),
(84, NULL, 24, 3, '2025-11', 4375000.00, '2025-12-11 15:24:38'),
(85, NULL, 25, 3, '2025-11', 750000.00, '2025-12-11 15:24:38'),
(86, NULL, 26, 3, '2025-11', 250000.00, '2025-12-11 15:24:38'),
(87, NULL, 6, 3, '2025-11', -2625000.00, '2025-12-11 15:24:38'),
(88, 26, NULL, 4, '2025-11', 4400000.00, '2025-12-11 15:24:38'),
(89, 2, NULL, 4, '2025-11', 26.00, '2025-12-11 15:24:38'),
(90, 7, NULL, 4, '2025-11', 0.00, '2025-12-11 15:24:38'),
(91, 15, NULL, 4, '2025-11', 4960000.00, '2025-12-11 15:24:38'),
(92, 4, NULL, 4, '2025-11', 1130000.00, '2025-12-11 15:24:38'),
(93, 16, NULL, 4, '2025-11', 20.00, '2025-12-11 15:24:38'),
(94, 25, NULL, 4, '2025-11', 11000000.00, '2025-12-11 15:24:38'),
(95, 5, NULL, 4, '2025-11', 0.00, '2025-12-11 15:24:38'),
(96, 14, NULL, 4, '2025-11', 2340000.00, '2025-12-11 15:24:38'),
(97, 13, NULL, 4, '2025-11', 1.00, '2025-12-11 15:24:38'),
(98, 17, NULL, 4, '2025-11', 1000000.00, '2025-12-11 15:24:38'),
(99, 3, NULL, 4, '2025-11', 0.00, '2025-12-11 15:24:38'),
(100, 6, NULL, 4, '2025-11', 0.00, '2025-12-11 15:24:38'),
(101, 8, NULL, 4, '2025-11', 12000000.00, '2025-12-11 15:24:38'),
(102, 10, NULL, 4, '2025-11', 730000.00, '2025-12-11 15:24:38'),
(103, 1, NULL, 4, '2025-11', 12000000.00, '2025-12-11 15:24:38'),
(104, 11, NULL, 4, '2025-11', 15400000.00, '2025-12-11 15:24:38'),
(105, NULL, 1, 4, '2025-11', 0.00, '2025-12-11 15:24:38'),
(106, NULL, 3, 4, '2025-11', 0.00, '2025-12-11 15:24:38'),
(107, NULL, 20, 4, '2025-11', 960000.00, '2025-12-11 15:24:38'),
(108, NULL, 21, 4, '2025-11', 180000.00, '2025-12-11 15:24:38'),
(109, NULL, 22, 4, '2025-11', 120000.00, '2025-12-11 15:24:38'),
(110, NULL, 4, 4, '2025-11', -17390000.00, '2025-12-11 15:24:38'),
(111, NULL, 5, 4, '2025-11', 0.00, '2025-12-11 15:24:38'),
(112, NULL, 19, 4, '2025-11', 0.00, '2025-12-11 15:24:38'),
(113, NULL, 24, 4, '2025-11', 2100000.00, '2025-12-11 15:24:38'),
(114, NULL, 25, 4, '2025-11', 360000.00, '2025-12-11 15:24:38'),
(115, NULL, 26, 4, '2025-11', 120000.00, '2025-12-11 15:24:38'),
(116, NULL, 6, 4, '2025-11', -1260000.00, '2025-12-11 15:24:38'),
(117, 26, NULL, 5, '2025-11', 4400000.00, '2025-12-11 15:24:38'),
(118, 2, NULL, 5, '2025-11', 26.00, '2025-12-11 15:24:38'),
(119, 7, NULL, 5, '2025-11', 0.00, '2025-12-11 15:24:38'),
(120, 15, NULL, 5, '2025-11', 4960000.00, '2025-12-11 15:24:38'),
(121, 4, NULL, 5, '2025-11', 3730000.00, '2025-12-11 15:24:38'),
(122, 16, NULL, 5, '2025-11', 20.00, '2025-12-11 15:24:38'),
(123, 25, NULL, 5, '2025-11', 11000000.00, '2025-12-11 15:24:38'),
(124, 5, NULL, 5, '2025-11', 0.00, '2025-12-11 15:24:38'),
(125, 14, NULL, 5, '2025-11', 2340000.00, '2025-12-11 15:24:38'),
(126, 13, NULL, 5, '2025-11', 2.00, '2025-12-11 15:24:38'),
(127, 17, NULL, 5, '2025-11', 1000000.00, '2025-12-11 15:24:38'),
(128, 3, NULL, 5, '2025-11', 1.00, '2025-12-11 15:24:38'),
(129, 6, NULL, 5, '2025-11', 0.00, '2025-12-11 15:24:38'),
(130, 8, NULL, 5, '2025-11', 22000000.00, '2025-12-11 15:24:38'),
(131, 10, NULL, 5, '2025-11', 730000.00, '2025-12-11 15:24:38'),
(132, 1, NULL, 5, '2025-11', 22000000.00, '2025-12-11 15:24:38'),
(133, 11, NULL, 5, '2025-11', 19800000.00, '2025-12-11 15:24:38'),
(134, NULL, 1, 5, '2025-11', 989615.38, '2025-12-11 15:24:38'),
(135, NULL, 3, 5, '2025-11', 989615.38, '2025-12-11 15:24:38'),
(136, NULL, 20, 5, '2025-11', 1760000.00, '2025-12-11 15:24:38'),
(137, NULL, 21, 5, '2025-11', 330000.00, '2025-12-11 15:24:38'),
(138, NULL, 22, 5, '2025-11', 220000.00, '2025-12-11 15:24:38'),
(139, NULL, 4, 5, '2025-11', -21850384.62, '2025-12-11 15:24:38'),
(140, NULL, 5, 5, '2025-11', 0.00, '2025-12-11 15:24:38'),
(141, NULL, 19, 5, '2025-11', 0.00, '2025-12-11 15:24:38'),
(142, NULL, 24, 5, '2025-11', 3850000.00, '2025-12-11 15:24:38'),
(143, NULL, 25, 5, '2025-11', 660000.00, '2025-12-11 15:24:38'),
(144, NULL, 26, 5, '2025-11', 220000.00, '2025-12-11 15:24:38'),
(145, NULL, 6, 5, '2025-11', -1320384.62, '2025-12-11 15:24:38'),
(146, 26, NULL, 6, '2025-11', 4400000.00, '2025-12-11 15:24:39'),
(147, 2, NULL, 6, '2025-11', 26.00, '2025-12-11 15:24:39'),
(148, 7, NULL, 6, '2025-11', 0.00, '2025-12-11 15:24:39'),
(149, 15, NULL, 6, '2025-11', 4960000.00, '2025-12-11 15:24:39'),
(150, 4, NULL, 6, '2025-11', 1230000.00, '2025-12-11 15:24:39'),
(151, 16, NULL, 6, '2025-11', 20.00, '2025-12-11 15:24:39'),
(152, 25, NULL, 6, '2025-11', 11000000.00, '2025-12-11 15:24:39'),
(153, 5, NULL, 6, '2025-11', 0.00, '2025-12-11 15:24:39'),
(154, 14, NULL, 6, '2025-11', 2340000.00, '2025-12-11 15:24:39'),
(155, 13, NULL, 6, '2025-11', 1.00, '2025-12-11 15:24:39'),
(156, 17, NULL, 6, '2025-11', 1000000.00, '2025-12-11 15:24:39'),
(157, 3, NULL, 6, '2025-11', 2.00, '2025-12-11 15:24:39'),
(158, 6, NULL, 6, '2025-11', 200000.00, '2025-12-11 15:24:39'),
(159, 8, NULL, 6, '2025-11', 10000000.00, '2025-12-11 15:24:39'),
(160, 10, NULL, 6, '2025-11', 1230000.00, '2025-12-11 15:24:39'),
(161, 1, NULL, 6, '2025-11', 10000000.00, '2025-12-11 15:24:39'),
(162, 11, NULL, 6, '2025-11', 15400000.00, '2025-12-11 15:24:39'),
(163, NULL, 1, 6, '2025-11', 863846.16, '2025-12-11 15:24:39'),
(164, NULL, 3, 6, '2025-11', 663846.16, '2025-12-11 15:24:39'),
(165, NULL, 20, 6, '2025-11', 800000.00, '2025-12-11 15:24:39'),
(166, NULL, 21, 6, '2025-11', 150000.00, '2025-12-11 15:24:39'),
(167, NULL, 22, 6, '2025-11', 100000.00, '2025-12-11 15:24:39'),
(168, NULL, 4, 6, '2025-11', -17016153.84, '2025-12-11 15:24:39'),
(169, NULL, 5, 6, '2025-11', 0.00, '2025-12-11 15:24:39'),
(170, NULL, 19, 6, '2025-11', 0.00, '2025-12-11 15:24:39'),
(171, NULL, 24, 6, '2025-11', 1750000.00, '2025-12-11 15:24:39'),
(172, NULL, 25, 6, '2025-11', 300000.00, '2025-12-11 15:24:39'),
(173, NULL, 26, 6, '2025-11', 100000.00, '2025-12-11 15:24:39'),
(174, NULL, 6, 6, '2025-11', -586153.84, '2025-12-11 15:24:39'),
(175, 26, NULL, 7, '2025-11', 4400000.00, '2025-12-11 15:24:39'),
(176, 2, NULL, 7, '2025-11', 26.00, '2025-12-11 15:24:39'),
(177, 7, NULL, 7, '2025-11', 0.00, '2025-12-11 15:24:39'),
(178, 15, NULL, 7, '2025-11', 4960000.00, '2025-12-11 15:24:39'),
(179, 4, NULL, 7, '2025-11', 3730000.00, '2025-12-11 15:24:39'),
(180, 16, NULL, 7, '2025-11', 20.00, '2025-12-11 15:24:39'),
(181, 25, NULL, 7, '2025-11', 11000000.00, '2025-12-11 15:24:39'),
(182, 5, NULL, 7, '2025-11', 0.00, '2025-12-11 15:24:39'),
(183, 14, NULL, 7, '2025-11', 2340000.00, '2025-12-11 15:24:39'),
(184, 13, NULL, 7, '2025-11', 1.00, '2025-12-11 15:24:39'),
(185, 17, NULL, 7, '2025-11', 1000000.00, '2025-12-11 15:24:39'),
(186, 3, NULL, 7, '2025-11', 0.00, '2025-12-11 15:24:39'),
(187, 6, NULL, 7, '2025-11', 0.00, '2025-12-11 15:24:39'),
(188, 8, NULL, 7, '2025-11', 20000000.00, '2025-12-11 15:24:39'),
(189, 10, NULL, 7, '2025-11', 730000.00, '2025-12-11 15:24:39'),
(190, 1, NULL, 7, '2025-11', 20000000.00, '2025-12-11 15:24:39'),
(191, 11, NULL, 7, '2025-11', 15400000.00, '2025-12-11 15:24:39'),
(192, NULL, 1, 7, '2025-11', 0.00, '2025-12-11 15:24:39'),
(193, NULL, 3, 7, '2025-11', 0.00, '2025-12-11 15:24:39'),
(194, NULL, 20, 7, '2025-11', 1600000.00, '2025-12-11 15:24:39'),
(195, NULL, 21, 7, '2025-11', 300000.00, '2025-12-11 15:24:39'),
(196, NULL, 22, 7, '2025-11', 200000.00, '2025-12-11 15:24:39'),
(197, NULL, 4, 7, '2025-11', -18230000.00, '2025-12-11 15:24:39'),
(198, NULL, 5, 7, '2025-11', 0.00, '2025-12-11 15:24:39'),
(199, NULL, 19, 7, '2025-11', 0.00, '2025-12-11 15:24:39'),
(200, NULL, 24, 7, '2025-11', 3500000.00, '2025-12-11 15:24:39'),
(201, NULL, 25, 7, '2025-11', 600000.00, '2025-12-11 15:24:39'),
(202, NULL, 26, 7, '2025-11', 200000.00, '2025-12-11 15:24:39'),
(203, NULL, 6, 7, '2025-11', -2100000.00, '2025-12-11 15:24:39'),
(204, 26, NULL, 8, '2025-11', 4400000.00, '2025-12-11 15:24:39'),
(205, 2, NULL, 8, '2025-11', 26.00, '2025-12-11 15:24:39'),
(206, 7, NULL, 8, '2025-11', 0.00, '2025-12-11 15:24:39'),
(207, 15, NULL, 8, '2025-11', 4960000.00, '2025-12-11 15:24:39'),
(208, 4, NULL, 8, '2025-11', 1230000.00, '2025-12-11 15:24:39'),
(209, 16, NULL, 8, '2025-11', 20.00, '2025-12-11 15:24:39'),
(210, 25, NULL, 8, '2025-11', 11000000.00, '2025-12-11 15:24:39'),
(211, 5, NULL, 8, '2025-11', 2000000.00, '2025-12-11 15:24:39'),
(212, 14, NULL, 8, '2025-11', 2340000.00, '2025-12-11 15:24:39'),
(213, 13, NULL, 8, '2025-11', 1.00, '2025-12-11 15:24:39'),
(214, 17, NULL, 8, '2025-11', 1000000.00, '2025-12-11 15:24:39'),
(215, 3, NULL, 8, '2025-11', 1.00, '2025-12-11 15:24:39'),
(216, 6, NULL, 8, '2025-11', 0.00, '2025-12-11 15:24:39'),
(217, 8, NULL, 8, '2025-11', 9000000.00, '2025-12-11 15:24:39'),
(218, 10, NULL, 8, '2025-11', 3230000.00, '2025-12-11 15:24:39'),
(219, 1, NULL, 8, '2025-11', 9000000.00, '2025-12-11 15:24:39'),
(220, 11, NULL, 8, '2025-11', 15400000.00, '2025-12-11 15:24:39'),
(221, NULL, 1, 8, '2025-11', 393461.54, '2025-12-11 15:24:39'),
(222, NULL, 3, 8, '2025-11', 2393461.54, '2025-12-11 15:24:39'),
(223, NULL, 20, 8, '2025-11', 720000.00, '2025-12-11 15:24:39'),
(224, NULL, 21, 8, '2025-11', 135000.00, '2025-12-11 15:24:39'),
(225, NULL, 22, 8, '2025-11', 90000.00, '2025-12-11 15:24:39'),
(226, NULL, 4, 8, '2025-11', -17181538.46, '2025-12-11 15:24:39'),
(227, NULL, 5, 8, '2025-11', 0.00, '2025-12-11 15:24:39'),
(228, NULL, 19, 8, '2025-11', 0.00, '2025-12-11 15:24:39'),
(229, NULL, 24, 8, '2025-11', 1575000.00, '2025-12-11 15:24:39'),
(230, NULL, 25, 8, '2025-11', 270000.00, '2025-12-11 15:24:39'),
(231, NULL, 26, 8, '2025-11', 90000.00, '2025-12-11 15:24:39'),
(232, NULL, 6, 8, '2025-11', 1448461.54, '2025-12-11 15:24:39'),
(233, 26, NULL, 9, '2025-11', 4400000.00, '2025-12-11 15:24:39'),
(234, 2, NULL, 9, '2025-11', 26.00, '2025-12-11 15:24:39'),
(235, 7, NULL, 9, '2025-11', 0.00, '2025-12-11 15:24:39'),
(236, 15, NULL, 9, '2025-11', 4960000.00, '2025-12-11 15:24:39'),
(237, 4, NULL, 9, '2025-11', 6960000.00, '2025-12-11 15:24:39'),
(238, 16, NULL, 9, '2025-11', 20.00, '2025-12-11 15:24:39'),
(239, 25, NULL, 9, '2025-11', 11000000.00, '2025-12-11 15:24:39'),
(240, 5, NULL, 9, '2025-11', 0.00, '2025-12-11 15:24:39'),
(241, 14, NULL, 9, '2025-11', 2340000.00, '2025-12-11 15:24:39'),
(242, 13, NULL, 9, '2025-11', 2.00, '2025-12-11 15:24:39'),
(243, 17, NULL, 9, '2025-11', 1000000.00, '2025-12-11 15:24:39'),
(244, 3, NULL, 9, '2025-11', 0.00, '2025-12-11 15:24:39'),
(245, 6, NULL, 9, '2025-11', 0.00, '2025-12-11 15:24:39'),
(246, 8, NULL, 9, '2025-11', 18000000.00, '2025-12-11 15:24:39'),
(247, 10, NULL, 9, '2025-11', 1960000.00, '2025-12-11 15:24:39'),
(248, 1, NULL, 9, '2025-11', 18000000.00, '2025-12-11 15:24:39'),
(249, 11, NULL, 9, '2025-11', 19800000.00, '2025-12-11 15:24:39'),
(250, NULL, 1, 9, '2025-11', 0.00, '2025-12-11 15:24:39'),
(251, NULL, 3, 9, '2025-11', 0.00, '2025-12-11 15:24:39'),
(252, NULL, 20, 9, '2025-11', 1440000.00, '2025-12-11 15:24:39'),
(253, NULL, 21, 9, '2025-11', 270000.00, '2025-12-11 15:24:39'),
(254, NULL, 22, 9, '2025-11', 180000.00, '2025-12-11 15:24:39'),
(255, NULL, 4, 9, '2025-11', -23650000.00, '2025-12-11 15:24:39'),
(256, NULL, 5, 9, '2025-11', 0.00, '2025-12-11 15:24:39'),
(257, NULL, 19, 9, '2025-11', 0.00, '2025-12-11 15:24:39'),
(258, NULL, 24, 9, '2025-11', 3150000.00, '2025-12-11 15:24:39'),
(259, NULL, 25, 9, '2025-11', 540000.00, '2025-12-11 15:24:39'),
(260, NULL, 26, 9, '2025-11', 180000.00, '2025-12-11 15:24:39'),
(261, NULL, 6, 9, '2025-11', -1890000.00, '2025-12-11 15:24:39'),
(262, 26, NULL, 10, '2025-11', 4400000.00, '2025-12-11 15:24:39'),
(263, 2, NULL, 10, '2025-11', 26.00, '2025-12-11 15:24:39'),
(264, 7, NULL, 10, '2025-11', 0.00, '2025-12-11 15:24:39'),
(265, 15, NULL, 10, '2025-11', 4960000.00, '2025-12-11 15:24:39'),
(266, 4, NULL, 10, '2025-11', 1460000.00, '2025-12-11 15:24:39'),
(267, 16, NULL, 10, '2025-11', 20.00, '2025-12-11 15:24:39'),
(268, 25, NULL, 10, '2025-11', 11000000.00, '2025-12-11 15:24:39'),
(269, 5, NULL, 10, '2025-11', 0.00, '2025-12-11 15:24:39'),
(270, 14, NULL, 10, '2025-11', 2340000.00, '2025-12-11 15:24:39'),
(271, 13, NULL, 10, '2025-11', 1.00, '2025-12-11 15:24:39'),
(272, 17, NULL, 10, '2025-11', 1000000.00, '2025-12-11 15:24:39'),
(273, 3, NULL, 10, '2025-11', 0.00, '2025-12-11 15:24:39'),
(274, 6, NULL, 10, '2025-11', 500000.00, '2025-12-11 15:24:39'),
(275, 8, NULL, 10, '2025-11', 9000000.00, '2025-12-11 15:24:39'),
(276, 10, NULL, 10, '2025-11', 1460000.00, '2025-12-11 15:24:39'),
(277, 1, NULL, 10, '2025-11', 9000000.00, '2025-12-11 15:24:39'),
(278, 11, NULL, 10, '2025-11', 15400000.00, '2025-12-11 15:24:39'),
(279, NULL, 1, 10, '2025-11', 0.00, '2025-12-11 15:24:39'),
(280, NULL, 3, 10, '2025-11', -500000.00, '2025-12-11 15:24:39'),
(281, NULL, 20, 10, '2025-11', 720000.00, '2025-12-11 15:24:39'),
(282, NULL, 21, 10, '2025-11', 135000.00, '2025-12-11 15:24:39'),
(283, NULL, 22, 10, '2025-11', 90000.00, '2025-12-11 15:24:39'),
(284, NULL, 4, 10, '2025-11', -18305000.00, '2025-12-11 15:24:39'),
(285, NULL, 5, 10, '2025-11', 0.00, '2025-12-11 15:24:39'),
(286, NULL, 19, 10, '2025-11', 0.00, '2025-12-11 15:24:39'),
(287, NULL, 24, 10, '2025-11', 1575000.00, '2025-12-11 15:24:39'),
(288, NULL, 25, 10, '2025-11', 270000.00, '2025-12-11 15:24:39'),
(289, NULL, 26, 10, '2025-11', 90000.00, '2025-12-11 15:24:39'),
(290, NULL, 6, 10, '2025-11', -1945000.00, '2025-12-11 15:24:39'),
(291, 26, NULL, 11, '2025-11', 4400000.00, '2025-12-11 15:24:39'),
(292, 2, NULL, 11, '2025-11', 26.00, '2025-12-11 15:24:39'),
(293, 7, NULL, 11, '2025-11', 0.00, '2025-12-11 15:24:39'),
(294, 15, NULL, 11, '2025-11', 4960000.00, '2025-12-11 15:24:39'),
(295, 4, NULL, 11, '2025-11', 4130000.00, '2025-12-11 15:24:39'),
(296, 16, NULL, 11, '2025-11', 20.00, '2025-12-11 15:24:39'),
(297, 25, NULL, 11, '2025-11', 11000000.00, '2025-12-11 15:24:39'),
(298, 5, NULL, 11, '2025-11', 0.00, '2025-12-11 15:24:39'),
(299, 14, NULL, 11, '2025-11', 2340000.00, '2025-12-11 15:24:39'),
(300, 13, NULL, 11, '2025-11', 1.00, '2025-12-11 15:24:39'),
(301, 17, NULL, 11, '2025-11', 1000000.00, '2025-12-11 15:24:39'),
(302, 3, NULL, 11, '2025-11', 0.00, '2025-12-11 15:24:39'),
(303, 6, NULL, 11, '2025-11', 0.00, '2025-12-11 15:24:39'),
(304, 8, NULL, 11, '2025-11', 17000000.00, '2025-12-11 15:24:39'),
(305, 10, NULL, 11, '2025-11', 730000.00, '2025-12-11 15:24:39'),
(306, 1, NULL, 11, '2025-11', 17000000.00, '2025-12-11 15:24:39'),
(307, 11, NULL, 11, '2025-11', 15400000.00, '2025-12-11 15:24:39'),
(308, NULL, 1, 11, '2025-11', 0.00, '2025-12-11 15:24:39'),
(309, NULL, 3, 11, '2025-11', 0.00, '2025-12-11 15:24:39'),
(310, NULL, 20, 11, '2025-11', 1360000.00, '2025-12-11 15:24:39'),
(311, NULL, 21, 11, '2025-11', 255000.00, '2025-12-11 15:24:39'),
(312, NULL, 22, 11, '2025-11', 170000.00, '2025-12-11 15:24:39'),
(313, NULL, 4, 11, '2025-11', -17915000.00, '2025-12-11 15:24:39'),
(314, NULL, 5, 11, '2025-11', 0.00, '2025-12-11 15:24:39'),
(315, NULL, 19, 11, '2025-11', 0.00, '2025-12-11 15:24:39'),
(316, NULL, 24, 11, '2025-11', 2975000.00, '2025-12-11 15:24:39'),
(317, NULL, 25, 11, '2025-11', 510000.00, '2025-12-11 15:24:39'),
(318, NULL, 26, 11, '2025-11', 170000.00, '2025-12-11 15:24:39'),
(319, NULL, 6, 11, '2025-11', -1785000.00, '2025-12-11 15:24:39'),
(320, 26, NULL, 12, '2025-11', 4400000.00, '2025-12-11 15:24:39'),
(321, 2, NULL, 12, '2025-11', 26.00, '2025-12-11 15:24:39'),
(322, 7, NULL, 12, '2025-11', 0.00, '2025-12-11 15:24:39'),
(323, 15, NULL, 12, '2025-11', 4960000.00, '2025-12-11 15:24:39'),
(324, 4, NULL, 12, '2025-11', 1130000.00, '2025-12-11 15:24:39'),
(325, 16, NULL, 12, '2025-11', 20.00, '2025-12-11 15:24:39'),
(326, 25, NULL, 12, '2025-11', 11000000.00, '2025-12-11 15:24:39'),
(327, 5, NULL, 12, '2025-11', 0.00, '2025-12-11 15:24:39'),
(328, 14, NULL, 12, '2025-11', 2340000.00, '2025-12-11 15:24:39'),
(329, 13, NULL, 12, '2025-11', 1.00, '2025-12-11 15:24:39'),
(330, 17, NULL, 12, '2025-11', 1000000.00, '2025-12-11 15:24:39'),
(331, 3, NULL, 12, '2025-11', 1.00, '2025-12-11 15:24:39'),
(332, 6, NULL, 12, '2025-11', 0.00, '2025-12-11 15:24:39'),
(333, 8, NULL, 12, '2025-11', 8000000.00, '2025-12-11 15:24:39'),
(334, 10, NULL, 12, '2025-11', 730000.00, '2025-12-11 15:24:39'),
(335, 1, NULL, 12, '2025-11', 8000000.00, '2025-12-11 15:24:39'),
(336, 11, NULL, 12, '2025-11', 15400000.00, '2025-12-11 15:24:39'),
(337, NULL, 1, 12, '2025-11', 351153.85, '2025-12-11 15:24:39'),
(338, NULL, 3, 12, '2025-11', 351153.85, '2025-12-11 15:24:39'),
(339, NULL, 20, 12, '2025-11', 640000.00, '2025-12-11 15:24:39'),
(340, NULL, 21, 12, '2025-11', 120000.00, '2025-12-11 15:24:39'),
(341, NULL, 22, 12, '2025-11', 80000.00, '2025-12-11 15:24:39'),
(342, NULL, 4, 12, '2025-11', -16618846.15, '2025-12-11 15:24:39'),
(343, NULL, 5, 12, '2025-11', 0.00, '2025-12-11 15:24:39'),
(344, NULL, 19, 12, '2025-11', 0.00, '2025-12-11 15:24:39'),
(345, NULL, 24, 12, '2025-11', 1400000.00, '2025-12-11 15:24:39'),
(346, NULL, 25, 12, '2025-11', 240000.00, '2025-12-11 15:24:39'),
(347, NULL, 26, 12, '2025-11', 80000.00, '2025-12-11 15:24:39'),
(348, NULL, 6, 12, '2025-11', -488846.15, '2025-12-11 15:24:39'),
(349, 26, NULL, 13, '2025-11', 4400000.00, '2025-12-11 15:24:39'),
(350, 2, NULL, 13, '2025-11', 26.00, '2025-12-11 15:24:39'),
(351, 7, NULL, 13, '2025-11', 0.00, '2025-12-11 15:24:39'),
(352, 15, NULL, 13, '2025-11', 4960000.00, '2025-12-11 15:24:39'),
(353, 4, NULL, 13, '2025-11', 1130000.00, '2025-12-11 15:24:39'),
(354, 16, NULL, 13, '2025-11', 20.00, '2025-12-11 15:24:39'),
(355, 25, NULL, 13, '2025-11', 11000000.00, '2025-12-11 15:24:39'),
(356, 5, NULL, 13, '2025-11', 0.00, '2025-12-11 15:24:39'),
(357, 14, NULL, 13, '2025-11', 2340000.00, '2025-12-11 15:24:39'),
(358, 13, NULL, 13, '2025-11', 1.00, '2025-12-11 15:24:39'),
(359, 17, NULL, 13, '2025-11', 1000000.00, '2025-12-11 15:24:39'),
(360, 3, NULL, 13, '2025-11', 0.00, '2025-12-11 15:24:39'),
(361, 6, NULL, 13, '2025-11', 0.00, '2025-12-11 15:24:39'),
(362, 8, NULL, 13, '2025-11', 14000000.00, '2025-12-11 15:24:39'),
(363, 10, NULL, 13, '2025-11', 730000.00, '2025-12-11 15:24:39'),
(364, 1, NULL, 13, '2025-11', 14000000.00, '2025-12-11 15:24:39'),
(365, 11, NULL, 13, '2025-11', 15400000.00, '2025-12-11 15:24:39'),
(366, NULL, 1, 13, '2025-11', 0.00, '2025-12-11 15:24:39'),
(367, NULL, 3, 13, '2025-11', 0.00, '2025-12-11 15:24:39'),
(368, NULL, 20, 13, '2025-11', 1120000.00, '2025-12-11 15:24:39'),
(369, NULL, 21, 13, '2025-11', 210000.00, '2025-12-11 15:24:39'),
(370, NULL, 22, 13, '2025-11', 140000.00, '2025-12-11 15:24:39'),
(371, NULL, 4, 13, '2025-11', -17600000.00, '2025-12-11 15:24:39'),
(372, NULL, 5, 13, '2025-11', 0.00, '2025-12-11 15:24:39'),
(373, NULL, 19, 13, '2025-11', 0.00, '2025-12-11 15:24:39'),
(374, NULL, 24, 13, '2025-11', 2450000.00, '2025-12-11 15:24:39'),
(375, NULL, 25, 13, '2025-11', 420000.00, '2025-12-11 15:24:39'),
(376, NULL, 26, 13, '2025-11', 140000.00, '2025-12-11 15:24:39'),
(377, NULL, 6, 13, '2025-11', -1470000.00, '2025-12-11 15:24:39'),
(378, 26, NULL, 14, '2025-11', 4400000.00, '2025-12-11 15:24:39'),
(379, 2, NULL, 14, '2025-11', 26.00, '2025-12-11 15:24:39'),
(380, 7, NULL, 14, '2025-11', 0.00, '2025-12-11 15:24:39'),
(381, 15, NULL, 14, '2025-11', 4960000.00, '2025-12-11 15:24:39'),
(382, 4, NULL, 14, '2025-11', 1130000.00, '2025-12-11 15:24:39'),
(383, 16, NULL, 14, '2025-11', 20.00, '2025-12-11 15:24:39'),
(384, 25, NULL, 14, '2025-11', 11000000.00, '2025-12-11 15:24:39'),
(385, 5, NULL, 14, '2025-11', 0.00, '2025-12-11 15:24:39'),
(386, 14, NULL, 14, '2025-11', 2340000.00, '2025-12-11 15:24:39'),
(387, 13, NULL, 14, '2025-11', 1.00, '2025-12-11 15:24:39'),
(388, 17, NULL, 14, '2025-11', 1000000.00, '2025-12-11 15:24:39'),
(389, 3, NULL, 14, '2025-11', 0.00, '2025-12-11 15:24:39'),
(390, 6, NULL, 14, '2025-11', 0.00, '2025-12-11 15:24:39'),
(391, 8, NULL, 14, '2025-11', 0.00, '2025-12-11 15:24:39'),
(392, 10, NULL, 14, '2025-11', 730000.00, '2025-12-11 15:24:39'),
(393, 1, NULL, 14, '2025-11', 5000000.00, '2025-12-11 15:24:39'),
(394, 11, NULL, 14, '2025-11', 15400000.00, '2025-12-11 15:24:39'),
(395, NULL, 1, 14, '2025-11', 0.00, '2025-12-11 15:24:39'),
(396, NULL, 3, 14, '2025-11', 0.00, '2025-12-11 15:24:39'),
(397, NULL, 20, 14, '2025-11', 0.00, '2025-12-11 15:24:39'),
(398, NULL, 21, 14, '2025-11', 0.00, '2025-12-11 15:24:39'),
(399, NULL, 22, 14, '2025-11', 0.00, '2025-12-11 15:24:39'),
(400, NULL, 4, 14, '2025-11', -16130000.00, '2025-12-11 15:24:39'),
(401, NULL, 5, 14, '2025-11', 0.00, '2025-12-11 15:24:39'),
(402, NULL, 19, 14, '2025-11', 0.00, '2025-12-11 15:24:39'),
(403, NULL, 24, 14, '2025-11', 0.00, '2025-12-11 15:24:39'),
(404, NULL, 25, 14, '2025-11', 0.00, '2025-12-11 15:24:39'),
(405, NULL, 26, 14, '2025-11', 0.00, '2025-12-11 15:24:39'),
(406, NULL, 6, 14, '2025-11', 0.00, '2025-12-11 15:24:39'),
(407, 26, NULL, 15, '2025-11', 4400000.00, '2025-12-11 15:24:39'),
(408, 2, NULL, 15, '2025-11', 26.00, '2025-12-11 15:24:39'),
(409, 7, NULL, 15, '2025-11', 0.00, '2025-12-11 15:24:39'),
(410, 15, NULL, 15, '2025-11', 4960000.00, '2025-12-11 15:24:39'),
(411, 4, NULL, 15, '2025-11', 730000.00, '2025-12-11 15:24:39'),
(412, 16, NULL, 15, '2025-11', 20.00, '2025-12-11 15:24:39'),
(413, 25, NULL, 15, '2025-11', 11000000.00, '2025-12-11 15:24:39'),
(414, 5, NULL, 15, '2025-11', 0.00, '2025-12-11 15:24:39'),
(415, 14, NULL, 15, '2025-11', 2340000.00, '2025-12-11 15:24:39'),
(416, 13, NULL, 15, '2025-11', 1.00, '2025-12-11 15:24:39'),
(417, 17, NULL, 15, '2025-11', 1000000.00, '2025-12-11 15:24:39'),
(418, 3, NULL, 15, '2025-11', 0.00, '2025-12-11 15:24:39'),
(419, 6, NULL, 15, '2025-11', 0.00, '2025-12-11 15:24:39'),
(420, 8, NULL, 15, '2025-11', 9500000.00, '2025-12-11 15:24:39'),
(421, 10, NULL, 15, '2025-11', 730000.00, '2025-12-11 15:24:39'),
(422, 1, NULL, 15, '2025-11', 9500000.00, '2025-12-11 15:24:39'),
(423, 11, NULL, 15, '2025-11', 15400000.00, '2025-12-11 15:24:39'),
(424, NULL, 1, 15, '2025-11', 0.00, '2025-12-11 15:24:39'),
(425, NULL, 3, 15, '2025-11', 0.00, '2025-12-11 15:24:39'),
(426, NULL, 20, 15, '2025-11', 760000.00, '2025-12-11 15:24:39'),
(427, NULL, 21, 15, '2025-11', 142500.00, '2025-12-11 15:24:39'),
(428, NULL, 22, 15, '2025-11', 95000.00, '2025-12-11 15:24:39'),
(429, NULL, 4, 15, '2025-11', -17127500.00, '2025-12-11 15:24:39'),
(430, NULL, 5, 15, '2025-11', 0.00, '2025-12-11 15:24:39'),
(431, NULL, 19, 15, '2025-11', 0.00, '2025-12-11 15:24:39'),
(432, NULL, 24, 15, '2025-11', 1662500.00, '2025-12-11 15:24:39'),
(433, NULL, 25, 15, '2025-11', 285000.00, '2025-12-11 15:24:39'),
(434, NULL, 26, 15, '2025-11', 95000.00, '2025-12-11 15:24:39'),
(435, NULL, 6, 15, '2025-11', -997500.00, '2025-12-11 15:24:39'),
(436, 26, NULL, 16, '2025-11', 4400000.00, '2025-12-11 15:24:39'),
(437, 2, NULL, 16, '2025-11', 26.00, '2025-12-11 15:24:39'),
(438, 7, NULL, 16, '2025-11', 0.00, '2025-12-11 15:24:39'),
(439, 15, NULL, 16, '2025-11', 4960000.00, '2025-12-11 15:24:39'),
(440, 4, NULL, 16, '2025-11', 730000.00, '2025-12-11 15:24:39'),
(441, 16, NULL, 16, '2025-11', 20.00, '2025-12-11 15:24:39'),
(442, 25, NULL, 16, '2025-11', 11000000.00, '2025-12-11 15:24:39'),
(443, 5, NULL, 16, '2025-11', 0.00, '2025-12-11 15:24:39'),
(444, 14, NULL, 16, '2025-11', 2340000.00, '2025-12-11 15:24:39'),
(445, 13, NULL, 16, '2025-11', 1.00, '2025-12-11 15:24:39'),
(446, 17, NULL, 16, '2025-11', 1000000.00, '2025-12-11 15:24:39'),
(447, 3, NULL, 16, '2025-11', 0.00, '2025-12-11 15:24:39'),
(448, 6, NULL, 16, '2025-11', 1000000.00, '2025-12-11 15:24:39'),
(449, 8, NULL, 16, '2025-11', 9200000.00, '2025-12-11 15:24:39'),
(450, 10, NULL, 16, '2025-11', 730000.00, '2025-12-11 15:24:39'),
(451, 1, NULL, 16, '2025-11', 9200000.00, '2025-12-11 15:24:39'),
(452, 11, NULL, 16, '2025-11', 15400000.00, '2025-12-11 15:24:39'),
(453, NULL, 1, 16, '2025-11', 0.00, '2025-12-11 15:24:39'),
(454, NULL, 3, 16, '2025-11', -1000000.00, '2025-12-11 15:24:39'),
(455, NULL, 20, 16, '2025-11', 736000.00, '2025-12-11 15:24:39'),
(456, NULL, 21, 16, '2025-11', 138000.00, '2025-12-11 15:24:39'),
(457, NULL, 22, 16, '2025-11', 92000.00, '2025-12-11 15:24:39'),
(458, NULL, 4, 16, '2025-11', -18096000.00, '2025-12-11 15:24:39'),
(459, NULL, 5, 16, '2025-11', 0.00, '2025-12-11 15:24:39'),
(460, NULL, 19, 16, '2025-11', 0.00, '2025-12-11 15:24:39'),
(461, NULL, 24, 16, '2025-11', 1610000.00, '2025-12-11 15:24:39'),
(462, NULL, 25, 16, '2025-11', 276000.00, '2025-12-11 15:24:39'),
(463, NULL, 26, 16, '2025-11', 92000.00, '2025-12-11 15:24:39'),
(464, NULL, 6, 16, '2025-11', -2966000.00, '2025-12-11 15:24:39'),
(465, 26, NULL, 17, '2025-11', 4400000.00, '2025-12-11 15:24:39'),
(466, 2, NULL, 17, '2025-11', 26.00, '2025-12-11 15:24:39'),
(467, 7, NULL, 17, '2025-11', 0.00, '2025-12-11 15:24:39'),
(468, 15, NULL, 17, '2025-11', 4960000.00, '2025-12-11 15:24:39'),
(469, 4, NULL, 17, '2025-11', 1230000.00, '2025-12-11 15:24:39'),
(470, 16, NULL, 17, '2025-11', 20.00, '2025-12-11 15:24:39'),
(471, 25, NULL, 17, '2025-11', 11000000.00, '2025-12-11 15:24:39'),
(472, 5, NULL, 17, '2025-11', 0.00, '2025-12-11 15:24:39'),
(473, 14, NULL, 17, '2025-11', 2340000.00, '2025-12-11 15:24:39'),
(474, 13, NULL, 17, '2025-11', 1.00, '2025-12-11 15:24:39'),
(475, 17, NULL, 17, '2025-11', 1000000.00, '2025-12-11 15:24:39'),
(476, 3, NULL, 17, '2025-11', 0.00, '2025-12-11 15:24:39'),
(477, 6, NULL, 17, '2025-11', 0.00, '2025-12-11 15:24:39'),
(478, 8, NULL, 17, '2025-11', 8800000.00, '2025-12-11 15:24:39'),
(479, 10, NULL, 17, '2025-11', 1230000.00, '2025-12-11 15:24:39'),
(480, 1, NULL, 17, '2025-11', 8800000.00, '2025-12-11 15:24:39'),
(481, 11, NULL, 17, '2025-11', 15400000.00, '2025-12-11 15:24:39'),
(482, NULL, 1, 17, '2025-11', 0.00, '2025-12-11 15:24:39'),
(483, NULL, 3, 17, '2025-11', 0.00, '2025-12-11 15:24:39'),
(484, NULL, 20, 17, '2025-11', 704000.00, '2025-12-11 15:24:39'),
(485, NULL, 21, 17, '2025-11', 132000.00, '2025-12-11 15:24:39'),
(486, NULL, 22, 17, '2025-11', 88000.00, '2025-12-11 15:24:39'),
(487, NULL, 4, 17, '2025-11', -17554000.00, '2025-12-11 15:24:39'),
(488, NULL, 5, 17, '2025-11', 0.00, '2025-12-11 15:24:39'),
(489, NULL, 19, 17, '2025-11', 0.00, '2025-12-11 15:24:39'),
(490, NULL, 24, 17, '2025-11', 1540000.00, '2025-12-11 15:24:39'),
(491, NULL, 25, 17, '2025-11', 264000.00, '2025-12-11 15:24:39'),
(492, NULL, 26, 17, '2025-11', 88000.00, '2025-12-11 15:24:39'),
(493, NULL, 6, 17, '2025-11', -924000.00, '2025-12-11 15:24:39'),
(494, 26, NULL, 18, '2025-11', 4400000.00, '2025-12-11 15:24:39'),
(495, 2, NULL, 18, '2025-11', 26.00, '2025-12-11 15:24:39'),
(496, 7, NULL, 18, '2025-11', 0.00, '2025-12-11 15:24:39'),
(497, 15, NULL, 18, '2025-11', 4960000.00, '2025-12-11 15:24:39'),
(498, 4, NULL, 18, '2025-11', 0.00, '2025-12-11 15:24:39'),
(499, 16, NULL, 18, '2025-11', 20.00, '2025-12-11 15:24:39'),
(500, 25, NULL, 18, '2025-11', 11000000.00, '2025-12-11 15:24:39'),
(501, 5, NULL, 18, '2025-11', 0.00, '2025-12-11 15:24:39'),
(502, 14, NULL, 18, '2025-11', 2340000.00, '2025-12-11 15:24:39'),
(503, 13, NULL, 18, '2025-11', 1.00, '2025-12-11 15:24:39'),
(504, 17, NULL, 18, '2025-11', 1000000.00, '2025-12-11 15:24:39'),
(505, 3, NULL, 18, '2025-11', 0.00, '2025-12-11 15:24:39'),
(506, 6, NULL, 18, '2025-11', 0.00, '2025-12-11 15:24:39'),
(507, 8, NULL, 18, '2025-11', 15000000.00, '2025-12-11 15:24:39'),
(508, 10, NULL, 18, '2025-11', 0.00, '2025-12-11 15:24:39'),
(509, 1, NULL, 18, '2025-11', 15000000.00, '2025-12-11 15:24:39'),
(510, 11, NULL, 18, '2025-11', 15400000.00, '2025-12-11 15:24:39'),
(511, NULL, 1, 18, '2025-11', 0.00, '2025-12-11 15:24:39'),
(512, NULL, 3, 18, '2025-11', 0.00, '2025-12-11 15:24:39'),
(513, NULL, 20, 18, '2025-11', 1200000.00, '2025-12-11 15:24:39'),
(514, NULL, 21, 18, '2025-11', 225000.00, '2025-12-11 15:24:39'),
(515, NULL, 22, 18, '2025-11', 150000.00, '2025-12-11 15:24:39'),
(516, NULL, 4, 18, '2025-11', -16975000.00, '2025-12-11 15:24:39'),
(517, NULL, 5, 18, '2025-11', 0.00, '2025-12-11 15:24:39'),
(518, NULL, 19, 18, '2025-11', 0.00, '2025-12-11 15:24:39'),
(519, NULL, 24, 18, '2025-11', 2625000.00, '2025-12-11 15:24:39'),
(520, NULL, 25, 18, '2025-11', 450000.00, '2025-12-11 15:24:39'),
(521, NULL, 26, 18, '2025-11', 150000.00, '2025-12-11 15:24:39'),
(522, NULL, 6, 18, '2025-11', -1575000.00, '2025-12-11 15:24:39'),
(523, 26, NULL, 19, '2025-11', 4400000.00, '2025-12-11 15:24:39'),
(524, 2, NULL, 19, '2025-11', 26.00, '2025-12-11 15:24:39'),
(525, 7, NULL, 19, '2025-11', 0.00, '2025-12-11 15:24:39'),
(526, 15, NULL, 19, '2025-11', 4960000.00, '2025-12-11 15:24:39'),
(527, 4, NULL, 19, '2025-11', 0.00, '2025-12-11 15:24:39'),
(528, 16, NULL, 19, '2025-11', 20.00, '2025-12-11 15:24:39'),
(529, 25, NULL, 19, '2025-11', 11000000.00, '2025-12-11 15:24:39'),
(530, 5, NULL, 19, '2025-11', 0.00, '2025-12-11 15:24:39'),
(531, 14, NULL, 19, '2025-11', 2340000.00, '2025-12-11 15:24:39'),
(532, 13, NULL, 19, '2025-11', 1.00, '2025-12-11 15:24:39'),
(533, 17, NULL, 19, '2025-11', 1000000.00, '2025-12-11 15:24:39'),
(534, 3, NULL, 19, '2025-11', 0.00, '2025-12-11 15:24:39'),
(535, 6, NULL, 19, '2025-11', 0.00, '2025-12-11 15:24:39'),
(536, 8, NULL, 19, '2025-11', 15000000.00, '2025-12-11 15:24:39'),
(537, 10, NULL, 19, '2025-11', 0.00, '2025-12-11 15:24:39'),
(538, 1, NULL, 19, '2025-11', 15000000.00, '2025-12-11 15:24:39'),
(539, 11, NULL, 19, '2025-11', 15400000.00, '2025-12-11 15:24:39'),
(540, NULL, 1, 19, '2025-11', 0.00, '2025-12-11 15:24:39'),
(541, NULL, 3, 19, '2025-11', 0.00, '2025-12-11 15:24:39'),
(542, NULL, 20, 19, '2025-11', 1200000.00, '2025-12-11 15:24:39'),
(543, NULL, 21, 19, '2025-11', 225000.00, '2025-12-11 15:24:39'),
(544, NULL, 22, 19, '2025-11', 150000.00, '2025-12-11 15:24:39'),
(545, NULL, 4, 19, '2025-11', -16975000.00, '2025-12-11 15:24:39'),
(546, NULL, 5, 19, '2025-11', 0.00, '2025-12-11 15:24:39'),
(547, NULL, 19, 19, '2025-11', 0.00, '2025-12-11 15:24:39'),
(548, NULL, 24, 19, '2025-11', 2625000.00, '2025-12-11 15:24:39'),
(549, NULL, 25, 19, '2025-11', 450000.00, '2025-12-11 15:24:39'),
(550, NULL, 26, 19, '2025-11', 150000.00, '2025-12-11 15:24:39'),
(551, NULL, 6, 19, '2025-11', -1575000.00, '2025-12-11 15:24:39'),
(552, 26, NULL, 20, '2025-11', 4400000.00, '2025-12-11 15:24:39'),
(553, 2, NULL, 20, '2025-11', 26.00, '2025-12-11 15:24:39'),
(554, 7, NULL, 20, '2025-11', 0.00, '2025-12-11 15:24:39'),
(555, 15, NULL, 20, '2025-11', 4960000.00, '2025-12-11 15:24:39'),
(556, 4, NULL, 20, '2025-11', 730000.00, '2025-12-11 15:24:39'),
(557, 16, NULL, 20, '2025-11', 20.00, '2025-12-11 15:24:39'),
(558, 25, NULL, 20, '2025-11', 11000000.00, '2025-12-11 15:24:39'),
(559, 5, NULL, 20, '2025-11', 0.00, '2025-12-11 15:24:39'),
(560, 14, NULL, 20, '2025-11', 2340000.00, '2025-12-11 15:24:39'),
(561, 13, NULL, 20, '2025-11', 0.00, '2025-12-11 15:24:39'),
(562, 17, NULL, 20, '2025-11', 1000000.00, '2025-12-11 15:24:39'),
(563, 3, NULL, 20, '2025-11', 0.00, '2025-12-11 15:24:39'),
(564, 6, NULL, 20, '2025-11', 0.00, '2025-12-11 15:24:39'),
(565, 8, NULL, 20, '2025-11', 0.00, '2025-12-11 15:24:39'),
(566, 10, NULL, 20, '2025-11', 730000.00, '2025-12-11 15:24:39'),
(567, 1, NULL, 20, '2025-11', 7500000.00, '2025-12-11 15:24:39'),
(568, 11, NULL, 20, '2025-11', 11000000.00, '2025-12-11 15:24:39'),
(569, NULL, 1, 20, '2025-11', 0.00, '2025-12-11 15:24:39'),
(570, NULL, 3, 20, '2025-11', 0.00, '2025-12-11 15:24:39'),
(571, NULL, 20, 20, '2025-11', 0.00, '2025-12-11 15:24:39'),
(572, NULL, 21, 20, '2025-11', 0.00, '2025-12-11 15:24:39'),
(573, NULL, 22, 20, '2025-11', 0.00, '2025-12-11 15:24:39'),
(574, NULL, 4, 20, '2025-11', -11730000.00, '2025-12-11 15:24:39'),
(575, NULL, 5, 20, '2025-11', 0.00, '2025-12-11 15:24:39'),
(576, NULL, 19, 20, '2025-11', 0.00, '2025-12-11 15:24:39'),
(577, NULL, 24, 20, '2025-11', 0.00, '2025-12-11 15:24:39'),
(578, NULL, 25, 20, '2025-11', 0.00, '2025-12-11 15:24:39'),
(579, NULL, 26, 20, '2025-11', 0.00, '2025-12-11 15:24:39'),
(580, NULL, 6, 20, '2025-11', 0.00, '2025-12-11 15:24:39'),
(581, 26, NULL, 21, '2025-11', 4400000.00, '2025-12-11 15:24:39'),
(582, 2, NULL, 21, '2025-11', 26.00, '2025-12-11 15:24:39'),
(583, 7, NULL, 21, '2025-11', 0.00, '2025-12-11 15:24:39'),
(584, 15, NULL, 21, '2025-11', 4960000.00, '2025-12-11 15:24:39'),
(585, 4, NULL, 21, '2025-11', 0.00, '2025-12-11 15:24:39'),
(586, 16, NULL, 21, '2025-11', 20.00, '2025-12-11 15:24:39'),
(587, 25, NULL, 21, '2025-11', 11000000.00, '2025-12-11 15:24:39'),
(588, 5, NULL, 21, '2025-11', 0.00, '2025-12-11 15:24:39'),
(589, 14, NULL, 21, '2025-11', 2340000.00, '2025-12-11 15:24:39'),
(590, 13, NULL, 21, '2025-11', 0.00, '2025-12-11 15:24:39'),
(591, 17, NULL, 21, '2025-11', 1000000.00, '2025-12-11 15:24:39'),
(592, 3, NULL, 21, '2025-11', 21.00, '2025-12-11 15:24:39'),
(593, 6, NULL, 21, '2025-11', 0.00, '2025-12-11 15:24:39'),
(594, 8, NULL, 21, '2025-11', 15000000.00, '2025-12-11 15:24:39'),
(595, 10, NULL, 21, '2025-11', 0.00, '2025-12-11 15:24:39'),
(596, 1, NULL, 21, '2025-11', 15000000.00, '2025-12-11 15:24:39'),
(597, 11, NULL, 21, '2025-11', 11000000.00, '2025-12-11 15:24:39'),
(598, NULL, 1, 21, '2025-11', 12115384.68, '2025-12-11 15:24:39'),
(599, NULL, 3, 21, '2025-11', 12115384.68, '2025-12-11 15:24:39'),
(600, NULL, 20, 21, '2025-11', 1200000.00, '2025-12-11 15:24:39'),
(601, NULL, 21, 21, '2025-11', 225000.00, '2025-12-11 15:24:39'),
(602, NULL, 22, 21, '2025-11', 150000.00, '2025-12-11 15:24:39'),
(603, NULL, 4, 21, '2025-11', -459615.32, '2025-12-11 15:24:39'),
(604, NULL, 5, 21, '2025-11', 0.00, '2025-12-11 15:24:39'),
(605, NULL, 19, 21, '2025-11', 0.00, '2025-12-11 15:24:40'),
(606, NULL, 24, 21, '2025-11', 2625000.00, '2025-12-11 15:24:40'),
(607, NULL, 25, 21, '2025-11', 450000.00, '2025-12-11 15:24:40'),
(608, NULL, 26, 21, '2025-11', 150000.00, '2025-12-11 15:24:40'),
(609, NULL, 6, 21, '2025-11', 10540384.68, '2025-12-11 15:24:40'),
(610, 26, NULL, 22, '2025-11', 4400000.00, '2025-12-11 15:24:40'),
(611, 2, NULL, 22, '2025-11', 26.00, '2025-12-11 15:24:40'),
(612, 7, NULL, 22, '2025-11', 0.00, '2025-12-11 15:24:40'),
(613, 15, NULL, 22, '2025-11', 4960000.00, '2025-12-11 15:24:40'),
(614, 4, NULL, 22, '2025-11', 0.00, '2025-12-11 15:24:40'),
(615, 16, NULL, 22, '2025-11', 20.00, '2025-12-11 15:24:40'),
(616, 25, NULL, 22, '2025-11', 11000000.00, '2025-12-11 15:24:40'),
(617, 5, NULL, 22, '2025-11', 0.00, '2025-12-11 15:24:40'),
(618, 14, NULL, 22, '2025-11', 2340000.00, '2025-12-11 15:24:40'),
(619, 13, NULL, 22, '2025-11', 0.00, '2025-12-11 15:24:40'),
(620, 17, NULL, 22, '2025-11', 1000000.00, '2025-12-11 15:24:40'),
(621, 3, NULL, 22, '2025-11', 0.00, '2025-12-11 15:24:40'),
(622, 6, NULL, 22, '2025-11', 0.00, '2025-12-11 15:24:40'),
(623, 8, NULL, 22, '2025-11', 0.00, '2025-12-11 15:24:40'),
(624, 10, NULL, 22, '2025-11', 0.00, '2025-12-11 15:24:40'),
(625, 1, NULL, 22, '2025-11', 0.00, '2025-12-11 15:24:40'),
(626, 11, NULL, 22, '2025-11', 11000000.00, '2025-12-11 15:24:40'),
(627, NULL, 1, 22, '2025-11', 0.00, '2025-12-11 15:24:40'),
(628, NULL, 3, 22, '2025-11', 0.00, '2025-12-11 15:24:40'),
(629, NULL, 20, 22, '2025-11', 0.00, '2025-12-11 15:24:40'),
(630, NULL, 21, 22, '2025-11', 0.00, '2025-12-11 15:24:40'),
(631, NULL, 22, 22, '2025-11', 0.00, '2025-12-11 15:24:40'),
(632, NULL, 4, 22, '2025-11', -11000000.00, '2025-12-11 15:24:40'),
(633, NULL, 5, 22, '2025-11', 0.00, '2025-12-11 15:24:40'),
(634, NULL, 19, 22, '2025-11', 0.00, '2025-12-11 15:24:40'),
(635, NULL, 24, 22, '2025-11', 0.00, '2025-12-11 15:24:40'),
(636, NULL, 25, 22, '2025-11', 0.00, '2025-12-11 15:24:40'),
(637, NULL, 26, 22, '2025-11', 0.00, '2025-12-11 15:24:40'),
(638, NULL, 6, 22, '2025-11', 0.00, '2025-12-11 15:24:40'),
(639, 26, NULL, 23, '2025-11', 4400000.00, '2025-12-11 15:24:40'),
(640, 2, NULL, 23, '2025-11', 26.00, '2025-12-11 15:24:40'),
(641, 7, NULL, 23, '2025-11', 0.00, '2025-12-11 15:24:40'),
(642, 15, NULL, 23, '2025-11', 4960000.00, '2025-12-11 15:24:40'),
(643, 4, NULL, 23, '2025-11', 0.00, '2025-12-11 15:24:40'),
(644, 16, NULL, 23, '2025-11', 20.00, '2025-12-11 15:24:40'),
(645, 25, NULL, 23, '2025-11', 11000000.00, '2025-12-11 15:24:40'),
(646, 5, NULL, 23, '2025-11', 0.00, '2025-12-11 15:24:40'),
(647, 14, NULL, 23, '2025-11', 2340000.00, '2025-12-11 15:24:40'),
(648, 13, NULL, 23, '2025-11', 0.00, '2025-12-11 15:24:40'),
(649, 17, NULL, 23, '2025-11', 1000000.00, '2025-12-11 15:24:40'),
(650, 3, NULL, 23, '2025-11', 0.00, '2025-12-11 15:24:40'),
(651, 6, NULL, 23, '2025-11', 0.00, '2025-12-11 15:24:40'),
(652, 8, NULL, 23, '2025-11', 0.00, '2025-12-11 15:24:40'),
(653, 10, NULL, 23, '2025-11', 0.00, '2025-12-11 15:24:40'),
(654, 1, NULL, 23, '2025-11', 0.00, '2025-12-11 15:24:40'),
(655, 11, NULL, 23, '2025-11', 11000000.00, '2025-12-11 15:24:40'),
(656, NULL, 1, 23, '2025-11', 0.00, '2025-12-11 15:24:40'),
(657, NULL, 3, 23, '2025-11', 0.00, '2025-12-11 15:24:40'),
(658, NULL, 20, 23, '2025-11', 0.00, '2025-12-11 15:24:40'),
(659, NULL, 21, 23, '2025-11', 0.00, '2025-12-11 15:24:40'),
(660, NULL, 22, 23, '2025-11', 0.00, '2025-12-11 15:24:40'),
(661, NULL, 4, 23, '2025-11', -11000000.00, '2025-12-11 15:24:40'),
(662, NULL, 5, 23, '2025-11', 0.00, '2025-12-11 15:24:40'),
(663, NULL, 19, 23, '2025-11', 0.00, '2025-12-11 15:24:40'),
(664, NULL, 24, 23, '2025-11', 0.00, '2025-12-11 15:24:40'),
(665, NULL, 25, 23, '2025-11', 0.00, '2025-12-11 15:24:40'),
(666, NULL, 26, 23, '2025-11', 0.00, '2025-12-11 15:24:40'),
(667, NULL, 6, 23, '2025-11', 0.00, '2025-12-11 15:24:40');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `schedulerequirements`
--

DROP TABLE IF EXISTS `schedulerequirements`;
CREATE TABLE IF NOT EXISTS `schedulerequirements` (
  `requirementID` int NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
  `departmentID` int NOT NULL COMMENT 'FK - Áp dụng cho phòng ban nào (ví dụ: In ấn)',
  `shiftID` int NOT NULL COMMENT 'FK - Áp dụng cho ca nào (ví dụ: C808)',
  `totalstaffneeded` int NOT NULL DEFAULT '0' COMMENT 'Tổng số nhân viên cần (ví dụ: 3)',
  PRIMARY KEY (`requirementID`),
  UNIQUE KEY `UQ_Dept_Shift` (`departmentID`,`shiftID`) COMMENT 'Mỗi phòng ban/ca chỉ có 1 yêu cầu',
  KEY `FK_schedule_req_shift` (`shiftID`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Nhu cầu nhân sự cơ bản cho ca làm việc của phòng ban';

--
-- Đang đổ dữ liệu cho bảng `schedulerequirements`
--

INSERT INTO `schedulerequirements` (`requirementID`, `departmentID`, `shiftID`, `totalstaffneeded`) VALUES
(2, 5, 36, 3),
(3, 5, 41, 3),
(6, 4, 41, 3),
(7, 4, 36, 1),
(12, 3, 36, 2),
(14, 3, 41, 1);

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
  `durationhours` int NOT NULL,
  PRIMARY KEY (`shiftID`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `shifts`
--

INSERT INTO `shifts` (`shiftID`, `shiftname`, `starttime`, `endtime`, `durationhours`) VALUES
(1, 'Hành chính', '08:00:00', '17:00:00', 0),
(2, 'Ca Sáng (SX)', '06:00:00', '14:00:00', 0),
(3, 'Ca Chiều (SX)', '14:00:00', '22:00:00', 0),
(4, 'Ca Đêm (SX)', '22:00:00', '06:00:00', 0),
(5, 'C601', '01:00:00', '07:00:00', 6),
(6, 'C602', '02:00:00', '08:00:00', 6),
(7, 'C603', '03:00:00', '09:00:00', 6),
(8, 'C604', '04:00:00', '10:00:00', 6),
(9, 'C605', '05:00:00', '11:00:00', 6),
(10, 'C606', '06:00:00', '12:00:00', 6),
(11, 'C607', '07:00:00', '13:00:00', 6),
(12, 'C608', '08:00:00', '14:00:00', 6),
(13, 'C609', '09:00:00', '15:00:00', 6),
(14, 'C610', '10:00:00', '16:00:00', 6),
(15, 'C611', '11:00:00', '17:00:00', 6),
(16, 'C612', '12:00:00', '18:00:00', 6),
(17, 'C613', '13:00:00', '19:00:00', 6),
(18, 'C614', '14:00:00', '20:00:00', 6),
(19, 'C615', '15:00:00', '21:00:00', 6),
(20, 'C616', '16:00:00', '22:00:00', 6),
(21, 'C617', '17:00:00', '23:00:00', 6),
(22, 'C618', '18:00:00', '00:00:00', 6),
(23, 'C619', '19:00:00', '01:00:00', 6),
(24, 'C620', '20:00:00', '02:00:00', 6),
(25, 'C621', '21:00:00', '03:00:00', 6),
(26, 'C622', '22:00:00', '04:00:00', 6),
(27, 'C623', '23:00:00', '05:00:00', 6),
(28, 'C624', '00:00:00', '06:00:00', 6),
(29, 'C801', '01:00:00', '10:00:00', 8),
(30, 'C802', '02:00:00', '11:00:00', 8),
(31, 'C803', '03:00:00', '12:00:00', 8),
(32, 'C804', '04:00:00', '13:00:00', 8),
(33, 'C805', '05:00:00', '14:00:00', 8),
(34, 'C806', '06:00:00', '15:00:00', 8),
(35, 'C807', '07:00:00', '16:00:00', 8),
(36, 'C808', '08:00:00', '17:00:00', 8),
(37, 'C809', '09:00:00', '18:00:00', 8),
(38, 'C810', '10:00:00', '19:00:00', 8),
(39, 'C811', '11:00:00', '20:00:00', 8),
(40, 'C812', '12:00:00', '21:00:00', 8),
(41, 'C813', '13:00:00', '22:00:00', 8),
(42, 'C814', '14:00:00', '23:00:00', 8),
(43, 'C815', '15:00:00', '00:00:00', 8),
(44, 'C816', '16:00:00', '01:00:00', 8),
(45, 'C817', '17:00:00', '02:00:00', 8),
(46, 'C818', '18:00:00', '03:00:00', 8),
(47, 'C819', '19:00:00', '04:00:00', 8),
(48, 'C820', '20:00:00', '05:00:00', 8),
(49, 'C821', '21:00:00', '06:00:00', 8),
(50, 'C822', '22:00:00', '07:00:00', 8),
(51, 'C823', '23:00:00', '08:00:00', 8),
(52, 'C824', '00:00:00', '09:00:00', 8),
(53, 'AL (Anually Leave)', '00:00:00', '00:00:00', 0),
(54, 'SL (Sick Leave)', '00:00:00', '00:00:00', 0),
(55, 'PL (Paternity Leave)', '00:00:00', '00:00:00', 0),
(56, 'ML (Maternity Leave)', '00:00:00', '00:00:00', 0),
(57, 'UL (Unpaid Leave)', '00:00:00', '00:00:00', 0);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `shift_change_requests`
--

DROP TABLE IF EXISTS `shift_change_requests`;
CREATE TABLE IF NOT EXISTS `shift_change_requests` (
  `requestID` int NOT NULL AUTO_INCREMENT,
  `employeeID` int NOT NULL COMMENT 'Người xin đổi',
  `date` date NOT NULL COMMENT 'Ngày muốn đổi',
  `currentShiftID` int DEFAULT NULL COMMENT 'Ca hiện tại (trước khi đổi)',
  `newShiftID` int DEFAULT NULL COMMENT 'Ca mong muốn (NULL nếu xin nghỉ)',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Lý do',
  `status` enum('PENDING','APPROVED','REJECTED','CANCELLED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING',
  `approverID` int DEFAULT NULL COMMENT 'Người duyệt',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`requestID`),
  KEY `FK_Request_Employee` (`employeeID`),
  KEY `FK_Request_Approver` (`approverID`),
  KEY `FK_Request_NewShift` (`newShiftID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `shift_change_requests`
--

INSERT INTO `shift_change_requests` (`requestID`, `employeeID`, `date`, `currentShiftID`, `newShiftID`, `reason`, `status`, `approverID`, `created_at`, `updated_at`) VALUES
(1, 9, '2025-12-03', 41, 41, 'Em bận việc gia đình buổi sáng nên xin đổi sang ca chiều', 'APPROVED', 9, '2025-12-11 09:54:13', NULL),
(2, 9, '2025-12-03', 41, 36, 'Em bận việc gia đình buổi sáng nên xin đổi sang ca sáng', 'APPROVED', 9, '2025-12-11 09:54:44', NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `taxsettings`
--

DROP TABLE IF EXISTS `taxsettings`;
CREATE TABLE IF NOT EXISTS `taxsettings` (
  `SettingID` int NOT NULL AUTO_INCREMENT,
  `SettingKey` enum('PERSONAL_DEDUCTION','DEPENDENT_DEDUCTION','BASIC_SALARY_STATE','REGION_MIN_SALARY','LUNCH_ALLOWANCE_LIMIT','INSURANCE_CAP_MULTIPLIER','HOURS_PER_WORKDAY') COLLATE utf8mb4_unicode_ci NOT NULL,
  `Value` decimal(15,2) NOT NULL,
  `EffectiveDate` date NOT NULL,
  `IsActive` tinyint(1) NOT NULL DEFAULT '1',
  `Description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Mô tả chi tiết về tham số',
  PRIMARY KEY (`SettingID`),
  UNIQUE KEY `uk_setting_date` (`SettingKey`,`EffectiveDate`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `taxsettings`
--

INSERT INTO `taxsettings` (`SettingID`, `SettingKey`, `Value`, `EffectiveDate`, `IsActive`, `Description`) VALUES
(1, 'PERSONAL_DEDUCTION', 11000000.00, '2024-01-01', 1, 'Giảm trừ bản thân (VN)'),
(2, 'DEPENDENT_DEDUCTION', 4400000.00, '2024-01-01', 1, 'Giảm trừ người phụ thuộc (VN)'),
(3, 'BASIC_SALARY_STATE', 2340000.00, '2024-01-01', 1, 'Lương cơ sở nhà nước'),
(4, 'REGION_MIN_SALARY', 4960000.00, '2024-01-01', 1, 'Lương tối thiểu vùng I'),
(5, 'LUNCH_ALLOWANCE_LIMIT', 1000000.00, '2024-01-01', 1, 'Tiền ăn ca tối đa không chịu thuế TNCN'),
(6, 'INSURANCE_CAP_MULTIPLIER', 20.00, '2024-01-01', 1, 'Số lần lương cơ sở để tính trần BHXH/BHYT'),
(7, 'HOURS_PER_WORKDAY', 8.00, '2024-01-01', 1, 'Số giờ làm việc chuẩn trong 1 ngày (dùng tính overtime)');

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

--
-- Đang đổ dữ liệu cho bảng `userpermissions`
--

INSERT INTO `userpermissions` (`userID`, `permissionID`, `activepermission`) VALUES
(4, 29, 0),
(4, 31, 0),
(6, 5, 1),
(6, 10, 1),
(7, 29, 0);

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
  `cccd` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phonenumber` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `birth` date DEFAULT NULL,
  `gender` enum('MALE','FEMALE') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bankaccount` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bankname` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hiredate` date DEFAULT NULL,
  `status` enum('active','inactive','on_leave') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active',
  `roleID` int DEFAULT NULL,
  `departmentID` int DEFAULT NULL,
  `skillGrade` int DEFAULT '1',
  `jobtype` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'NORMAL',
  PRIMARY KEY (`userID`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `roleID` (`roleID`),
  KEY `departmentID` (`departmentID`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`userID`, `username`, `password`, `fullname`, `cccd`, `email`, `phonenumber`, `birth`, `gender`, `address`, `bankaccount`, `bankname`, `hiredate`, `status`, `roleID`, `departmentID`, `skillGrade`, `jobtype`) VALUES
(1, 'admin', '$2a$10$2sQzJxjvMcMeSNOSsysqjOQZzWIpwvHKIdwdeZ.EqQDM6QKcufj0q', 'Phạm Minh Anh', '12345678987', 'admin@manaplastic.com', '0123456789', NULL, 'MALE', '123 Cao Lỗ ', NULL, NULL, '2023-01-01', 'active', 1, 2, 3, 'NORMAL'),
(2, 'it_support', '$2a$10$skyfJgN4n.Z2GMTP7GLnneUFL4cSm1DWoJdSsYGvF06flQTGz1GBC', 'Lê Hỗ Trợ IT', NULL, 'it.support@manaplastic.com', NULL, NULL, 'MALE', NULL, NULL, NULL, '2023-05-10', 'active', 1, 2, 3, 'NORMAL'),
(3, 'hr_manager', '$2a$10$yVs4Kv0e36Kcb8wesofM3enjSu/Kicj5TFJm6YavsG5TDd2kLtsqy', 'Nguyễn Thị Nhân Sự', '012345678906', 'hr.manager@manaplastic.com', '0123456789', '2006-03-02', 'FEMALE', '123 Cao Lỗ', '1028123123', 'vietcombank', '2023-02-15', 'active', 2, 1, 1, 'NORMAL'),
(4, 'hr_staff', '$2a$10$wnTMZHPSgAkKLfwNCY3cE.ufDKVPHrdWvaJ5oL.o0dKj7kxBMJNXG', 'Phạm Văn Tuyển Dụng', NULL, 'hr.staff@manaplastic.com', NULL, NULL, 'MALE', NULL, NULL, NULL, '2023-08-01', 'active', 2, 1, 1, 'NORMAL'),
(5, 'kythuat_lead', '$2a$10$QP/JCd6cnNgRkFr3BYzbKOSRMIbdI6Y1fE6D.w.p1xfbvQV9y5UJe', 'Võ Văn Kỹ Thuật', NULL, 'kythuat.lead@manaplastic.com', NULL, NULL, 'FEMALE', NULL, NULL, NULL, '2023-03-01', 'active', 3, 3, 3, 'NORMAL'),
(6, 'kythuat_staff', '$2a$10$TXgpVb3lT7f/1YQxy2i/he2wiepriI6XqcPO2WcxG2xBLt0V9T8HO', 'Hoàng Thị Máy Móc', '012345678911', 'kythuat.staff@manaplastic.com', '0123456789', NULL, 'MALE', NULL, NULL, NULL, '2023-09-10', 'active', 4, 3, 1, 'NORMAL'),
(7, 'sanxuat_lead', '$2a$10$4b25FwYYUWVDgrItKmyyJ.ntqK6S6SJFqLHabuDL.xsoP/yScAov.', 'Trịnh Hữu Sản Xuất', '01234568900', 'sanxuat.lead@manaplastic.com', NULL, NULL, 'MALE', NULL, NULL, NULL, '2023-03-02', 'active', 3, 4, 3, 'NORMAL'),
(8, 'sanxuat_staff', '$2a$10$TxHjoxC0uVKLIDznDhNWmuF73p7LTpVr1Lf1uGdn1lw2lo8gR0ApS', 'Đặng Văn Vận Hành', '09876543211', 'sanxuat.staff@manaplastic.com', NULL, NULL, 'FEMALE', NULL, NULL, NULL, '2023-09-15', 'active', 4, 4, 1, 'NORMAL'),
(9, 'inan_lead', '$2a$10$6v5RYvb1wRZZ333NkxGDLuVRAgXeG0xHcfsxiBQHV0tXRpvai2yYS', 'Bùi Văn Mực', '123456789876', 'inan.lead@manaplastic.com', NULL, NULL, 'FEMALE', '123 Cao Lỗ', NULL, 'Vietcombankkkkkkkkkk', '2023-04-01', 'active', 3, 5, 3, 'NORMAL'),
(10, 'inan_staff', '$2a$10$JN0oA3nYZmxvy4FXcXyMHuTBXs4xWWzcoFkXTgSbwgoqyHGmEG3Pm', 'Lý Thị In', '098765432111', 'inan.staff@manaplastic.com', NULL, NULL, 'MALE', NULL, NULL, NULL, '2023-10-01', 'active', 4, 5, 1, 'DANGER'),
(11, 'cskh_lead', '$2a$10$bm/qxbS1udqc01zrB/p/HuNdfz1kH9oFBVpfuLoTaGFLZKEuysMF.', 'Đỗ Thị Khách Hàng', NULL, 'cskh.lead@manaplastic.com', NULL, NULL, 'MALE', NULL, NULL, NULL, '2023-05-01', 'active', 3, 6, 3, 'NORMAL'),
(12, 'cskh_staff', '$2a$10$tHV/qqOG68rkmYyYL82LIe5v1hnm.lzY.SlZclzOZoV6j13l6LtD.', 'Mạc Văn Hài Lòng', NULL, 'cskh.staff@manaplastic.com', NULL, NULL, 'FEMALE', NULL, NULL, NULL, '2023-11-01', 'active', 4, 6, 1, 'NORMAL'),
(13, '57540101', '$2a$10$xPIdXJigdb91ZNCAFFtTu.4RLJpIYAaQGJ70VlaE2wSblfQznOlDi', 'Phạm Minh Anh HR', '123456789876', NULL, NULL, NULL, 'FEMALE', NULL, NULL, NULL, '2024-04-28', 'active', 2, 1, 3, 'NORMAL'),
(14, '52082901', '$2a$10$jeaJPcpV3IQMoxGHBLw2teRExwzuD0ZB9ubxat7yzrqgAgfWn.CvS', 'testAddAccountHR', '79203031165', NULL, NULL, NULL, 'MALE', NULL, NULL, NULL, '2023-02-09', 'active', 2, 1, 3, 'NORMAL'),
(15, '71939801', '$2a$10$U0qh1Bel43Hp2K6CbsOCXefQQRgRqPJMU5Sah3eIflWTGeSkZlrPG', 'testAddAccountNV', '79203031168', 'test@gmail.com', NULL, NULL, 'FEMALE', NULL, NULL, NULL, '2023-02-23', 'active', 4, 5, 3, 'NORMAL'),
(16, '83637905', '$2a$10$IHOeCVrioPATwf9X9s3wh.zNbCKAEHubkZxk8uPfqEWrmUb8mJCGm', 'Phạm Nhân Viên', '12345678922', NULL, '0123456789', NULL, 'FEMALE', NULL, NULL, NULL, '2024-10-13', 'active', 4, 5, 2, 'NORMAL'),
(17, '72001905', '$2a$10$eQmfti7ZY3a5TXk4xqfSaOc25bW6d1.bScjcFq5pabC7b6D5WTtSK', 'testAddAccountNVinan', '123456789000', NULL, NULL, NULL, 'FEMALE', NULL, NULL, NULL, '2023-08-03', 'active', 4, 5, 1, 'NORMAL'),
(18, '56885905', '$2a$10$4WtXYERXuVRu89Fh1KVm4uc0PuMsuHbwMg7/32dl9/qbrsFZvin3m', 'testAddAccountNVinan', '12345678900', 'pminhanh1106@gmail.com', NULL, NULL, 'FEMALE', NULL, NULL, NULL, '2024-02-17', 'active', 4, 5, 2, 'NORMAL'),
(19, '79753710', '$2a$10$vHtQqmnZ0POJYFnxD42L4O./uv5SHW5viV3DZdBCXAa8vpYdD1MQG', 'Phạm Minh Anh Test Pass', '12345678922', 'phamminhanh11623@gmail.com', '0395168006', '2003-06-11', 'MALE', NULL, '1023765488', NULL, '2024-11-27', 'active', 2, 1, 1, 'NORMAL'),
(20, '000020', '$2a$10$gFUhtqQKDR2Lxwp71TlleONgBBbuc7ND.rIf/lhNhksN1lfEEJrbS', 'testAddAccountNVinan logic mới', '012345678900', NULL, NULL, NULL, 'FEMALE', NULL, NULL, NULL, '2025-11-15', 'active', 4, 5, 1, 'NORMAL'),
(21, '000021', '$2a$10$hbZy3M1bMaLduAoQQ5g0eOEpoo6CtNZp5P1zi3GofCnBbMNlJnSjO', 'Phạm Minh Anh Kỹ Thuật', '012345678900', 'darkpic1106@gmail.com', '0395168006', '2003-06-11', 'MALE', '123 Cao Lỗ', '1028792003', 'Vietcombank', '2025-11-28', 'active', 3, 3, 1, 'NORMAL'),
(22, '000022', '$2a$10$u2IyQxrWOZ.Y6oRGTVEnBu.NieT5QReL9OBod7Jz2hRPfqoEAb/Ou', 'Phạm Minh Anh Nhân Sự', '012345678900', NULL, NULL, NULL, 'MALE', NULL, NULL, NULL, '2025-12-10', 'active', 2, 1, 1, 'NORMAL'),
(23, '000023', '$2a$10$KqJl/D63wJh4Qsd4eKbIM.H.NKNk13jQGHctYQyGUOXnTPs3rlXE.', 'Phạm Minh Anh Sản Xuất', '012345678900', NULL, NULL, NULL, 'MALE', NULL, NULL, NULL, '2025-12-10', 'active', 4, 4, 1, 'NORMAL');

-- --------------------------------------------------------

--
-- Cấu trúc đóng vai cho view `v_employee_profile_flat`
-- (See below for the actual view)
--
DROP VIEW IF EXISTS `v_employee_profile_flat`;
CREATE TABLE IF NOT EXISTS `v_employee_profile_flat` (
`AGE` bigint
,`BASE_SALARY` decimal(15,2)
,`CONTRACT_TYPE` varchar(100)
,`DEPENDENT_COUNT` bigint
,`DEPT_NAME` varchar(255)
,`GENDER` enum('MALE','FEMALE')
,`INSURANCE_BASE` decimal(15,2)
,`IS_OFFICE` bit(1)
,`JOB_TYPE` varchar(100)
,`ROLE_NAME` varchar(100)
,`SENIORITY_DAYS` int
,`SENIORITY_MONTHS` bigint
,`SKILL_GRADE` int
,`TOXIC_TYPE` enum('NONE','CASH','IN_KIND')
,`userID` int
);

-- --------------------------------------------------------

--
-- Cấu trúc cho view `v_employee_profile_flat`
--
DROP TABLE IF EXISTS `v_employee_profile_flat`;

DROP VIEW IF EXISTS `v_employee_profile_flat`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_employee_profile_flat`  AS SELECT `u`.`userID` AS `userID`, `u`.`gender` AS `GENDER`, `u`.`jobtype` AS `JOB_TYPE`, `u`.`skillGrade` AS `SKILL_GRADE`, timestampdiff(YEAR,`u`.`birth`,curdate()) AS `AGE`, (to_days(curdate()) - to_days(`u`.`hiredate`)) AS `SENIORITY_DAYS`, timestampdiff(MONTH,`u`.`hiredate`,curdate()) AS `SENIORITY_MONTHS`, `c`.`basesalary` AS `BASE_SALARY`, `c`.`type` AS `CONTRACT_TYPE`, `c`.`AllowanceToxicType` AS `TOXIC_TYPE`, `c`.`InsuranceSalary` AS `INSURANCE_BASE`, `d`.`departmentname` AS `DEPT_NAME`, `d`.`isoffice` AS `IS_OFFICE`, `r`.`rolename` AS `ROLE_NAME`, (select count(0) from `dependents` `dep` where ((`dep`.`userID` = `u`.`userID`) and (`dep`.`istaxdeductible` = 1))) AS `DEPENDENT_COUNT` FROM (((`users` `u` left join `contracts` `c` on(((`u`.`userID` = `c`.`userID`) and (`c`.`Status` = 'ACTIVE')))) left join `departments` `d` on((`u`.`departmentID` = `d`.`departmentID`))) left join `roles` `r` on((`u`.`roleID` = `r`.`roleID`))) ;

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
  ADD CONSTRAINT `attendances_ibfk_2` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_Attendance_CheckInLog` FOREIGN KEY (`checkInLogID`) REFERENCES `attendancelogs` (`attendanceLogID`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_Attendance_CheckOutLog` FOREIGN KEY (`checkOutLogID`) REFERENCES `attendancelogs` (`attendanceLogID`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `contractallowances`
--
ALTER TABLE `contractallowances`
  ADD CONSTRAINT `FK_Allowance_Contract` FOREIGN KEY (`ContractID`) REFERENCES `contracts` (`contractID`) ON DELETE CASCADE ON UPDATE CASCADE;

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
-- Các ràng buộc cho bảng `dependents`
--
ALTER TABLE `dependents`
  ADD CONSTRAINT `FK_dependent_User` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `employeedraftschedule`
--
ALTER TABLE `employeedraftschedule`
  ADD CONSTRAINT `FK_preferences_shift` FOREIGN KEY (`shiftID`) REFERENCES `shifts` (`shiftID`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_preferences_user` FOREIGN KEY (`employeeID`) REFERENCES `users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `employeeofficialschedule`
--
ALTER TABLE `employeeofficialschedule`
  ADD CONSTRAINT `FK_schedule_manager` FOREIGN KEY (`approvedbymanagerID`) REFERENCES `users` (`userID`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_schedule_shift` FOREIGN KEY (`shiftID`) REFERENCES `shifts` (`shiftID`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_schedule_user` FOREIGN KEY (`employeeID`) REFERENCES `users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `leavebalance`
--
ALTER TABLE `leavebalance`
  ADD CONSTRAINT `FK_leavebalance_Shift` FOREIGN KEY (`leavetypeid`) REFERENCES `shifts` (`shiftID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_leavebalance_User` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `leavepolicy`
--
ALTER TABLE `leavepolicy`
  ADD CONSTRAINT `FK_Policy_Shift` FOREIGN KEY (`leavetypeid`) REFERENCES `shifts` (`shiftID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `leaverequests`
--
ALTER TABLE `leaverequests`
  ADD CONSTRAINT `FK_Request_Shift` FOREIGN KEY (`shiftID`) REFERENCES `shifts` (`shiftID`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `leaverequests_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `overtime`
--
ALTER TABLE `overtime`
  ADD CONSTRAINT `fk_overtime_type` FOREIGN KEY (`overtimetypeid`) REFERENCES `overtimetypes` (`OvertimeTypeID`),
  ADD CONSTRAINT `fk_overtime_users` FOREIGN KEY (`userid`) REFERENCES `users` (`userID`);

--
-- Các ràng buộc cho bảng `payrolls`
--
ALTER TABLE `payrolls`
  ADD CONSTRAINT `payrolls_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `requirementrules`
--
ALTER TABLE `requirementrules`
  ADD CONSTRAINT `FK_rule_req` FOREIGN KEY (`requirementID`) REFERENCES `schedulerequirements` (`requirementID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `rewardpunishmentdecisions`
--
ALTER TABLE `rewardpunishmentdecisions`
  ADD CONSTRAINT `FK_Decision_User` FOREIGN KEY (`UserID`) REFERENCES `users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `rolespermissions`
--
ALTER TABLE `rolespermissions`
  ADD CONSTRAINT `rolespermissions_ibfk_1` FOREIGN KEY (`roleID`) REFERENCES `roles` (`roleID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `rolespermissions_ibfk_2` FOREIGN KEY (`permissionID`) REFERENCES `permissions` (`permissionID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `salaryvariables`
--
ALTER TABLE `salaryvariables`
  ADD CONSTRAINT `fk_salaryvariables_rule_fix` FOREIGN KEY (`rule_id`) REFERENCES `salary_rule` (`rule_id`);

--
-- Các ràng buộc cho bảng `schedulerequirements`
--
ALTER TABLE `schedulerequirements`
  ADD CONSTRAINT `FK_schedule_req_dept` FOREIGN KEY (`departmentID`) REFERENCES `departments` (`departmentID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_schedule_req_shift` FOREIGN KEY (`shiftID`) REFERENCES `shifts` (`shiftID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `shift_change_requests`
--
ALTER TABLE `shift_change_requests`
  ADD CONSTRAINT `FK_Request_Approver` FOREIGN KEY (`approverID`) REFERENCES `users` (`userID`) ON DELETE SET NULL,
  ADD CONSTRAINT `FK_Request_Employee` FOREIGN KEY (`employeeID`) REFERENCES `users` (`userID`) ON DELETE CASCADE,
  ADD CONSTRAINT `FK_Request_NewShift` FOREIGN KEY (`newShiftID`) REFERENCES `shifts` (`shiftID`) ON DELETE SET NULL;

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
