-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1:3306
-- Thời gian đã tạo: Th10 19, 2025 lúc 11:17 AM
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
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
(20, '2025-11-19', '2025-11-19 11:00:12', '2025-11-19 11:02:27', '/uploads/47358d64-7cf1-4a33-8e1c-c5efbb7553d0.jpg', '/uploads/238acd2f-fd35-46f8-a36f-c4bfbe93b13d.jpg', 'LATE_AND_EARLY', 46, 20, 16, 17);

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
) ENGINE=InnoDB AUTO_INCREMENT=218 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `employeedraftschedule`
--

INSERT INTO `employeedraftschedule` (`draftID`, `employeeID`, `date`, `shiftID`, `isdayoff`, `monthyear`, `registrationdate`) VALUES
(1, 15, '2025-12-04', NULL, 1, '2025-12', '2025-11-16 13:10:08'),
(2, 15, '2025-12-03', 36, 0, '2025-12', '2025-11-16 13:10:08'),
(3, 9, '2025-12-01', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(4, 10, '2025-12-01', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(5, 15, '2025-12-01', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(6, 16, '2025-12-01', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(7, 17, '2025-12-01', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(8, 18, '2025-12-01', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(9, 20, '2025-12-01', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(10, 9, '2025-12-02', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(11, 20, '2025-12-02', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(12, 10, '2025-12-02', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(13, 15, '2025-12-02', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(14, 16, '2025-12-02', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(15, 17, '2025-12-02', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(16, 18, '2025-12-02', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(17, 9, '2025-12-03', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(18, 18, '2025-12-03', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(19, 20, '2025-12-03', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(20, 10, '2025-12-03', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(21, 16, '2025-12-03', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(22, 17, '2025-12-03', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(23, 9, '2025-12-04', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(24, 18, '2025-12-04', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(25, 20, '2025-12-04', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(26, 10, '2025-12-04', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(27, 16, '2025-12-04', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(28, 17, '2025-12-04', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(29, 15, '2025-12-05', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(30, 18, '2025-12-05', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(31, 20, '2025-12-05', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(32, 9, '2025-12-05', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(33, 10, '2025-12-05', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(34, 16, '2025-12-05', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(35, 17, '2025-12-05', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(36, 15, '2025-12-06', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(37, 17, '2025-12-06', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(38, 18, '2025-12-06', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(39, 9, '2025-12-06', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(40, 20, '2025-12-06', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(41, 10, '2025-12-06', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(42, 16, '2025-12-06', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(43, 15, '2025-12-07', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(44, 16, '2025-12-07', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(45, 17, '2025-12-07', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(46, 18, '2025-12-07', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(47, 20, '2025-12-07', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(48, 9, '2025-12-07', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(49, 10, '2025-12-07', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(50, 9, '2025-12-08', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(51, 16, '2025-12-08', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(52, 10, '2025-12-08', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(53, 15, '2025-12-08', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(54, 18, '2025-12-08', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(55, 17, '2025-12-08', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(56, 20, '2025-12-08', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(57, 9, '2025-12-09', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(58, 20, '2025-12-09', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(59, 16, '2025-12-09', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(60, 15, '2025-12-09', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(61, 10, '2025-12-09', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(62, 17, '2025-12-09', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(63, 18, '2025-12-09', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(64, 9, '2025-12-10', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(65, 18, '2025-12-10', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(66, 20, '2025-12-10', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(67, 15, '2025-12-10', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(68, 10, '2025-12-10', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(69, 16, '2025-12-10', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(70, 17, '2025-12-10', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(71, 9, '2025-12-11', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(72, 17, '2025-12-11', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(73, 18, '2025-12-11', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(74, 20, '2025-12-11', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(75, 10, '2025-12-11', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(76, 16, '2025-12-11', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(77, 15, '2025-12-11', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(78, 15, '2025-12-12', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(79, 17, '2025-12-12', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(80, 18, '2025-12-12', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(81, 9, '2025-12-12', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(82, 20, '2025-12-12', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(83, 10, '2025-12-12', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(84, 16, '2025-12-12', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(85, 15, '2025-12-13', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(86, 16, '2025-12-13', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(87, 17, '2025-12-13', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(88, 9, '2025-12-13', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(89, 18, '2025-12-13', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(90, 20, '2025-12-13', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(91, 10, '2025-12-13', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(92, 15, '2025-12-14', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(93, 10, '2025-12-14', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(94, 16, '2025-12-14', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(95, 18, '2025-12-14', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(96, 20, '2025-12-14', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(97, 17, '2025-12-14', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(98, 9, '2025-12-14', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(99, 9, '2025-12-15', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(100, 10, '2025-12-15', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(101, 16, '2025-12-15', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(102, 15, '2025-12-15', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(103, 18, '2025-12-15', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(104, 17, '2025-12-15', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(105, 20, '2025-12-15', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(106, 9, '2025-12-16', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(107, 20, '2025-12-16', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(108, 10, '2025-12-16', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(109, 15, '2025-12-16', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(110, 16, '2025-12-16', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(111, 17, '2025-12-16', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(112, 18, '2025-12-16', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(113, 9, '2025-12-17', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(114, 18, '2025-12-17', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(115, 20, '2025-12-17', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(116, 15, '2025-12-17', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(117, 10, '2025-12-17', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(118, 16, '2025-12-17', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(119, 17, '2025-12-17', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(120, 9, '2025-12-18', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(121, 17, '2025-12-18', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(122, 18, '2025-12-18', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(123, 20, '2025-12-18', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(124, 10, '2025-12-18', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(125, 16, '2025-12-18', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(126, 15, '2025-12-18', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(127, 15, '2025-12-19', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(128, 17, '2025-12-19', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(129, 18, '2025-12-19', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(130, 9, '2025-12-19', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(131, 20, '2025-12-19', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(132, 10, '2025-12-19', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(133, 16, '2025-12-19', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(134, 15, '2025-12-20', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(135, 16, '2025-12-20', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(136, 17, '2025-12-20', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(137, 9, '2025-12-20', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(138, 18, '2025-12-20', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(139, 20, '2025-12-20', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(140, 10, '2025-12-20', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(141, 15, '2025-12-21', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(142, 10, '2025-12-21', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(143, 16, '2025-12-21', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(144, 18, '2025-12-21', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(145, 17, '2025-12-21', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(146, 20, '2025-12-21', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(147, 9, '2025-12-21', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(148, 15, '2025-12-22', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(149, 10, '2025-12-22', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(150, 16, '2025-12-22', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(151, 9, '2025-12-22', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(152, 18, '2025-12-22', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(153, 17, '2025-12-22', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(154, 20, '2025-12-22', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(155, 15, '2025-12-23', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(156, 20, '2025-12-23', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(157, 10, '2025-12-23', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(158, 9, '2025-12-23', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(159, 16, '2025-12-23', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(160, 17, '2025-12-23', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(161, 18, '2025-12-23', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(162, 15, '2025-12-24', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(163, 20, '2025-12-24', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(164, 18, '2025-12-24', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(165, 9, '2025-12-24', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(166, 10, '2025-12-24', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(167, 16, '2025-12-24', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(168, 17, '2025-12-24', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(169, 17, '2025-12-25', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(170, 20, '2025-12-25', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(171, 18, '2025-12-25', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(172, 9, '2025-12-25', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(173, 10, '2025-12-25', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(174, 16, '2025-12-25', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(175, 15, '2025-12-25', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(176, 15, '2025-12-26', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(177, 17, '2025-12-26', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(178, 20, '2025-12-26', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(179, 9, '2025-12-26', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(180, 18, '2025-12-26', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(181, 10, '2025-12-26', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(182, 16, '2025-12-26', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(183, 16, '2025-12-27', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(184, 17, '2025-12-27', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(185, 20, '2025-12-27', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(186, 15, '2025-12-27', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(187, 18, '2025-12-27', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(188, 9, '2025-12-27', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(189, 10, '2025-12-27', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(190, 10, '2025-12-28', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(191, 16, '2025-12-28', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(192, 17, '2025-12-28', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(193, 15, '2025-12-28', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(194, 20, '2025-12-28', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(195, 18, '2025-12-28', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(196, 9, '2025-12-28', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(197, 9, '2025-12-29', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(198, 10, '2025-12-29', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(199, 16, '2025-12-29', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(200, 15, '2025-12-29', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(201, 17, '2025-12-29', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(202, 18, '2025-12-29', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(203, 20, '2025-12-29', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(204, 9, '2025-12-30', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(205, 20, '2025-12-30', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(206, 10, '2025-12-30', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(207, 15, '2025-12-30', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(208, 16, '2025-12-30', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(209, 17, '2025-12-30', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(210, 18, '2025-12-30', NULL, 1, '2025-12', '2025-11-16 13:10:23'),
(211, 9, '2025-12-31', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(212, 18, '2025-12-31', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(213, 20, '2025-12-31', 36, 0, '2025-12', '2025-11-16 13:10:23'),
(214, 15, '2025-12-31', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(215, 10, '2025-12-31', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(216, 16, '2025-12-31', 41, 0, '2025-12', '2025-11-16 13:10:23'),
(217, 17, '2025-12-31', NULL, 1, '2025-12', '2025-11-16 13:10:23');

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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `employeeofficialschedule`
--

INSERT INTO `employeeofficialschedule` (`officialID`, `employeeID`, `date`, `shiftID`, `isdayoff`, `monthyear`, `approvedbymanagerID`, `publisheddate`) VALUES
(7, 20, '2025-11-19', 46, 0, '2025-11', 1, '2025-11-19 17:43:29');

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
(18, 53, 2025, 12, 0, 0, '2025-11-14 22:14:46'),
(18, 54, 2025, 30, 0, 0, '2025-11-14 22:14:46'),
(18, 56, 2025, 180, 0, 0, '2025-11-14 22:14:46'),
(19, 53, 2025, 12, 0, 0, '2025-11-14 22:14:46'),
(19, 54, 2025, 30, 0, 0, '2025-11-14 22:14:46'),
(19, 55, 2025, 14, 0, 0, '2025-11-14 22:14:46');

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
  `days` int NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Mô tả (VD: "Nhân viên dưới 5 năm")',
  `leavetypeid` int DEFAULT NULL,
  PRIMARY KEY (`policyID`),
  KEY `FK_Policy_Shift` (`leavetypeid`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Chính sách phép năm theo thâm niên';

--
-- Đang đổ dữ liệu cho bảng `leavepolicy`
--

INSERT INTO `leavepolicy` (`policyID`, `leavetype`, `minyearsservice`, `maxyearsservice`, `jobtype`, `days`, `description`, `leavetypeid`) VALUES
(1, 'ANNUAL', 0, 5, NULL, 12, 'Nhân viên có thâm niên dưới 5 năm', 53),
(2, 'ANNUAL', 5, 10, NULL, 13, 'Nhân viên có thâm niên từ 5 đến dưới 10 năm', 53),
(3, 'ANNUAL', 10, NULL, NULL, 14, 'Nhân viên có thâm niên từ 10 năm trở lên', 53),
(7, 'SICK', 0, 15, 'NORMAL', 30, 'Phép ốm cho nhân viên đóng BHXH dưới 15 năm', 54),
(8, 'SICK', 15, 30, 'NORMAL', 40, 'Phép ốm cho nhân viên đóng BHXH từ 15 năm - 30 năm', 54),
(9, 'SICK', 30, NULL, 'NORMAL', 60, 'Phép ốm cho nhân viên đóng BHXH từ 30 năm trở lên', 54),
(10, 'MATERNITY', 0, NULL, NULL, 180, 'Nghỉ thai sản', 56),
(11, 'PATERNITY', 0, NULL, NULL, 14, 'Nhân viên có vợ sinh con', 55),
(12, 'SICK', 0, 15, 'DANGER', 40, 'Phép ốm (Độc hại) cho nhân viên đóng BHXH dưới 15 năm', 54),
(13, 'SICK', 15, 30, 'DANGER', 50, 'Phép ốm (Độc hại) cho nhân viên đóng BHXH từ 15 năm - 30 năm', 54),
(14, 'SICK', 30, NULL, 'DANGER', 70, 'Phép ốm (Độc hại) cho nhân viên đóng BHXH từ 30 năm trở lên', 54);

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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `leaverequests`
--

INSERT INTO `leaverequests` (`leaverequestID`, `leavetype`, `startdate`, `enddate`, `reason`, `status`, `requestdate`, `userID`, `shiftID`) VALUES
(1, 'SICK', '2025-10-29', '2025-10-29', 'Bị sốt', 'APPROVED', '2025-10-28', 6, 54),
(2, 'ANNUAL', '2025-11-05', '2025-11-06', 'Gia đình có việc', 'PENDING', '2025-10-28', 12, 53),
(3, 'ANNUAL', '2025-10-20', '2025-10-20', 'Việc cá nhân', 'REJECTED', '2025-10-19', 8, 53),
(5, 'SICK', '2025-12-01', '2025-12-02', 'Test API tạo đơn từ Postman', 'PENDING', '2025-11-13', 15, 54),
(6, 'SICK', '2025-12-06', '2025-12-06', 'Test API tạo đơn từ Postman', 'REJECTED', '2025-11-13', 18, 54);

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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Quy tắc kỹ năng chi tiết cho một yêu cầu nhân sự';

--
-- Đang đổ dữ liệu cho bảng `requirementrules`
--

INSERT INTO `requirementrules` (`ruleID`, `requirementID`, `requiredskillGrade`, `minstaffcount`) VALUES
(7, 2, 3, 1),
(9, 3, 3, 1);

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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Nhu cầu nhân sự cơ bản cho ca làm việc của phòng ban';

--
-- Đang đổ dữ liệu cho bảng `schedulerequirements`
--

INSERT INTO `schedulerequirements` (`requirementID`, `departmentID`, `shiftID`, `totalstaffneeded`) VALUES
(2, 5, 36, 3),
(3, 5, 41, 3);

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
  `cccd` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
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
  `skillGrade` int DEFAULT '1',
  `jobtype` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'NORMAL',
  PRIMARY KEY (`userID`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `roleID` (`roleID`),
  KEY `departmentID` (`departmentID`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`userID`, `username`, `password`, `fullname`, `cccd`, `email`, `phonenumber`, `birth`, `gender`, `address`, `bankaccount`, `bankname`, `hiredate`, `status`, `roleID`, `departmentID`, `skillGrade`, `jobtype`) VALUES
(1, 'admin', '$2a$10$2sQzJxjvMcMeSNOSsysqjOQZzWIpwvHKIdwdeZ.EqQDM6QKcufj0q', 'Phạm Minh Anh', '123456789874', 'admin@manaplastic.com', NULL, NULL, 1, NULL, NULL, NULL, '2023-01-01', 'active', 1, 2, 3, 'NORMAL'),
(2, 'it_support', '$2a$10$skyfJgN4n.Z2GMTP7GLnneUFL4cSm1DWoJdSsYGvF06flQTGz1GBC', 'Lê Hỗ Trợ IT', NULL, 'it.support@manaplastic.com', NULL, NULL, 1, NULL, NULL, NULL, '2023-05-10', 'active', 1, 2, 3, 'NORMAL'),
(3, 'hr_manager', '$2a$10$yVs4Kv0e36Kcb8wesofM3enjSu/Kicj5TFJm6YavsG5TDd2kLtsqy', 'Nguyễn Thị Nhân Sự', '1234567890', 'hr.manager@manaplastic.com', '0123456789', NULL, 0, NULL, NULL, NULL, '2023-02-15', 'active', 2, 1, 1, 'NORMAL'),
(4, 'hr_staff', '$2a$10$wnTMZHPSgAkKLfwNCY3cE.ufDKVPHrdWvaJ5oL.o0dKj7kxBMJNXG', 'Phạm Văn Tuyển Dụng', NULL, 'hr.staff@manaplastic.com', NULL, NULL, 1, NULL, NULL, NULL, '2023-08-01', 'active', 2, 1, 1, 'NORMAL'),
(5, 'kythuat_lead', '$2a$10$QP/JCd6cnNgRkFr3BYzbKOSRMIbdI6Y1fE6D.w.p1xfbvQV9y5UJe', 'Võ Văn Kỹ Thuật', NULL, 'kythuat.lead@manaplastic.com', NULL, NULL, 0, NULL, NULL, NULL, '2023-03-01', 'active', 3, 3, 3, 'NORMAL'),
(6, 'kythuat_staff', '$2a$10$TXgpVb3lT7f/1YQxy2i/he2wiepriI6XqcPO2WcxG2xBLt0V9T8HO', 'Hoàng Thị Máy Móc', NULL, 'kythuat.staff@manaplastic.com', NULL, NULL, 1, NULL, NULL, NULL, '2023-09-10', 'active', 4, 3, 1, 'NORMAL'),
(7, 'sanxuat_lead', '$2a$10$4b25FwYYUWVDgrItKmyyJ.ntqK6S6SJFqLHabuDL.xsoP/yScAov.', 'Trịnh Hữu Sản Xuất', NULL, 'sanxuat.lead@manaplastic.com', NULL, NULL, 1, NULL, NULL, NULL, '2023-03-02', 'active', 3, 4, 3, 'NORMAL'),
(8, 'sanxuat_staff', '$2a$10$TxHjoxC0uVKLIDznDhNWmuF73p7LTpVr1Lf1uGdn1lw2lo8gR0ApS', 'Đặng Văn Vận Hành', NULL, 'sanxuat.staff@manaplastic.com', NULL, NULL, 0, NULL, NULL, NULL, '2023-09-15', 'active', 4, 4, 1, 'NORMAL'),
(9, 'inan_lead', '$2a$10$6v5RYvb1wRZZ333NkxGDLuVRAgXeG0xHcfsxiBQHV0tXRpvai2yYS', 'Bùi Văn Mực', '123456789876', 'inan.lead@manaplastic.com', NULL, NULL, 0, NULL, NULL, 'Vietcombank', '2023-04-01', 'active', 3, 5, 3, 'NORMAL'),
(10, 'inan_staff', '$2a$10$JN0oA3nYZmxvy4FXcXyMHuTBXs4xWWzcoFkXTgSbwgoqyHGmEG3Pm', 'Lý Thị In', '098765432111', 'inan.staff@manaplastic.com', NULL, NULL, 1, NULL, NULL, NULL, '2023-10-01', 'active', 4, 5, 1, 'DANGER'),
(11, 'cskh_lead', '$2a$10$bm/qxbS1udqc01zrB/p/HuNdfz1kH9oFBVpfuLoTaGFLZKEuysMF.', 'Đỗ Thị Khách Hàng', NULL, 'cskh.lead@manaplastic.com', NULL, NULL, 1, NULL, NULL, NULL, '2023-05-01', 'active', 3, 6, 3, 'NORMAL'),
(12, 'cskh_staff', '$2a$10$tHV/qqOG68rkmYyYL82LIe5v1hnm.lzY.SlZclzOZoV6j13l6LtD.', 'Mạc Văn Hài Lòng', NULL, 'cskh.staff@manaplastic.com', NULL, NULL, 0, NULL, NULL, NULL, '2023-11-01', 'active', 4, 6, 1, 'NORMAL'),
(13, '57540101', '$2a$10$xPIdXJigdb91ZNCAFFtTu.4RLJpIYAaQGJ70VlaE2wSblfQznOlDi', 'Phạm Minh Anh HR', '123456789876', NULL, NULL, NULL, 0, NULL, NULL, NULL, '2024-04-28', 'active', 2, 1, 3, 'NORMAL'),
(14, '52082901', '$2a$10$jeaJPcpV3IQMoxGHBLw2teRExwzuD0ZB9ubxat7yzrqgAgfWn.CvS', 'testAddAccountHR', '79203031165', NULL, NULL, NULL, 1, NULL, NULL, NULL, '2023-02-09', 'active', 2, 1, 3, 'NORMAL'),
(15, '71939801', '$2a$10$U0qh1Bel43Hp2K6CbsOCXefQQRgRqPJMU5Sah3eIflWTGeSkZlrPG', 'testAddAccountNV', '79203031168', 'test@gmail.com', NULL, NULL, 0, NULL, NULL, NULL, '2023-02-23', 'active', 4, 5, 3, 'NORMAL'),
(16, '83637905', '$2a$10$IHOeCVrioPATwf9X9s3wh.zNbCKAEHubkZxk8uPfqEWrmUb8mJCGm', 'Phạm Nhân Viên', '12345678922', NULL, NULL, NULL, 0, NULL, NULL, NULL, '2024-10-13', 'active', 4, 5, 2, 'NORMAL'),
(17, '72001905', '$2a$10$eQmfti7ZY3a5TXk4xqfSaOc25bW6d1.bScjcFq5pabC7b6D5WTtSK', 'testAddAccountNVinan', '123456789000', NULL, NULL, NULL, 0, NULL, NULL, NULL, '2023-08-03', 'active', 4, 5, 1, 'NORMAL'),
(18, '56885905', '$2a$10$4WtXYERXuVRu89Fh1KVm4uc0PuMsuHbwMg7/32dl9/qbrsFZvin3m', 'testAddAccountNVinan', '12345678900', 'pminhanh1106@gmail.com', NULL, NULL, 0, NULL, NULL, NULL, '2024-02-17', 'active', 4, 5, 2, 'NORMAL'),
(19, '79753710', '$2a$10$vHtQqmnZ0POJYFnxD42L4O./uv5SHW5viV3DZdBCXAa8vpYdD1MQG', 'Phạm Minh Anh Test Pass', '12345678922', 'phamminhanh11623@gmail.com', '0395168006', '2003-06-11', 1, NULL, '1023765488', NULL, '2024-11-27', 'active', 2, 1, 1, 'NORMAL'),
(20, '000020', '$2a$10$gFUhtqQKDR2Lxwp71TlleONgBBbuc7ND.rIf/lhNhksN1lfEEJrbS', 'testAddAccountNVinan logic mới', '012345678900', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-11-15', 'active', 4, 5, 1, 'NORMAL');

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
-- Các ràng buộc cho bảng `requirementrules`
--
ALTER TABLE `requirementrules`
  ADD CONSTRAINT `FK_rule_req` FOREIGN KEY (`requirementID`) REFERENCES `schedulerequirements` (`requirementID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `rolespermissions`
--
ALTER TABLE `rolespermissions`
  ADD CONSTRAINT `rolespermissions_ibfk_1` FOREIGN KEY (`roleID`) REFERENCES `roles` (`roleID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `rolespermissions_ibfk_2` FOREIGN KEY (`permissionID`) REFERENCES `permissions` (`permissionID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `schedulerequirements`
--
ALTER TABLE `schedulerequirements`
  ADD CONSTRAINT `FK_schedule_req_dept` FOREIGN KEY (`departmentID`) REFERENCES `departments` (`departmentID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_schedule_req_shift` FOREIGN KEY (`shiftID`) REFERENCES `shifts` (`shiftID`) ON DELETE CASCADE ON UPDATE CASCADE;

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
