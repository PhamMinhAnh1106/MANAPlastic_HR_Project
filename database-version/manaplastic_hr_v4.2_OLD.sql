-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1:3306
-- Thời gian đã tạo: Th10 14, 2025 lúc 05:46 PM
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
  `id_card_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'CCCD hoặc Giấy khai sinh',
  `phonenumber` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_tax_deductible` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1=Có đăng ký giảm trừ gia cảnh, 0=Không',
  PRIMARY KEY (`dependentID`),
  KEY `FK_Dependent_User` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Người phụ thuộc của nhân viên';

--
-- Đang đổ dữ liệu cho bảng `dependents`
--

INSERT INTO `dependents` (`dependentID`, `userID`, `fullname`, `relationship`, `birth`, `gender`, `id_card_number`, `phonenumber`, `is_tax_deductible`) VALUES
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
  `is_day_off` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1 = muốn xin nghỉ, 0 = đi làm',
  `month_year` varchar(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Tháng/Năm áp dụng lịch (ví dụ: 2025-12)',
  `registration_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian gửi đăng ký',
  PRIMARY KEY (`draftID`),
  UNIQUE KEY `UQ_Employee_Date_Preference` (`employeeID`,`date`) COMMENT 'Mỗi nhân viên chỉ có 1 đăng ký/ngày',
  KEY `IX_preference_month_year` (`month_year`),
  KEY `FK_preferences_shift` (`shiftID`)
) ENGINE=InnoDB AUTO_INCREMENT=291 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `employeedraftschedule`
--

INSERT INTO `employeedraftschedule` (`draftID`, `employeeID`, `date`, `shiftID`, `is_day_off`, `month_year`, `registration_date`) VALUES
(164, 16, '2025-12-01', NULL, 1, '2025-12', '2025-11-09 16:24:04'),
(165, 16, '2025-12-03', 41, 0, '2025-12', '2025-11-09 16:24:04'),
(166, 15, '2025-12-04', NULL, 1, '2025-12', '2025-11-09 16:24:51'),
(167, 15, '2025-12-03', 36, 0, '2025-12', '2025-11-09 16:24:51'),
(168, 9, '2025-12-01', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(169, 10, '2025-12-01', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(170, 15, '2025-12-01', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(171, 18, '2025-12-01', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(172, 9, '2025-12-02', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(173, 10, '2025-12-02', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(174, 15, '2025-12-02', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(175, 16, '2025-12-02', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(176, 9, '2025-12-03', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(177, 10, '2025-12-03', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(178, 18, '2025-12-03', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(179, 9, '2025-12-04', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(180, 10, '2025-12-04', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(181, 16, '2025-12-04', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(182, 18, '2025-12-04', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(183, 9, '2025-12-05', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(184, 10, '2025-12-05', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(185, 15, '2025-12-05', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(186, 16, '2025-12-05', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(187, 9, '2025-12-06', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(188, 10, '2025-12-06', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(189, 15, '2025-12-06', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(190, 16, '2025-12-06', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(191, 9, '2025-12-07', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(192, 10, '2025-12-07', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(193, 15, '2025-12-07', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(194, 16, '2025-12-07', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(195, 9, '2025-12-08', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(196, 10, '2025-12-08', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(197, 15, '2025-12-08', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(198, 16, '2025-12-08', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(199, 9, '2025-12-09', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(200, 10, '2025-12-09', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(201, 15, '2025-12-09', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(202, 16, '2025-12-09', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(203, 9, '2025-12-10', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(204, 10, '2025-12-10', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(205, 15, '2025-12-10', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(206, 16, '2025-12-10', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(207, 9, '2025-12-11', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(208, 10, '2025-12-11', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(209, 15, '2025-12-11', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(210, 16, '2025-12-11', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(211, 9, '2025-12-12', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(212, 10, '2025-12-12', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(213, 15, '2025-12-12', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(214, 16, '2025-12-12', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(215, 9, '2025-12-13', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(216, 10, '2025-12-13', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(217, 15, '2025-12-13', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(218, 16, '2025-12-13', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(219, 9, '2025-12-14', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(220, 10, '2025-12-14', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(221, 15, '2025-12-14', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(222, 16, '2025-12-14', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(223, 9, '2025-12-15', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(224, 10, '2025-12-15', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(225, 15, '2025-12-15', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(226, 16, '2025-12-15', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(227, 9, '2025-12-16', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(228, 10, '2025-12-16', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(229, 15, '2025-12-16', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(230, 16, '2025-12-16', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(231, 9, '2025-12-17', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(232, 10, '2025-12-17', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(233, 15, '2025-12-17', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(234, 16, '2025-12-17', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(235, 9, '2025-12-18', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(236, 10, '2025-12-18', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(237, 15, '2025-12-18', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(238, 16, '2025-12-18', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(239, 9, '2025-12-19', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(240, 10, '2025-12-19', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(241, 15, '2025-12-19', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(242, 16, '2025-12-19', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(243, 9, '2025-12-20', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(244, 10, '2025-12-20', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(245, 15, '2025-12-20', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(246, 16, '2025-12-20', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(247, 9, '2025-12-21', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(248, 10, '2025-12-21', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(249, 15, '2025-12-21', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(250, 16, '2025-12-21', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(251, 9, '2025-12-22', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(252, 10, '2025-12-22', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(253, 15, '2025-12-22', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(254, 16, '2025-12-22', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(255, 9, '2025-12-23', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(256, 10, '2025-12-23', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(257, 15, '2025-12-23', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(258, 16, '2025-12-23', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(259, 9, '2025-12-24', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(260, 10, '2025-12-24', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(261, 15, '2025-12-24', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(262, 16, '2025-12-24', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(263, 9, '2025-12-25', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(264, 10, '2025-12-25', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(265, 15, '2025-12-25', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(266, 16, '2025-12-25', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(267, 9, '2025-12-26', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(268, 10, '2025-12-26', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(269, 15, '2025-12-26', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(270, 16, '2025-12-26', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(271, 9, '2025-12-27', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(272, 10, '2025-12-27', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(273, 15, '2025-12-27', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(274, 16, '2025-12-27', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(275, 9, '2025-12-28', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(276, 10, '2025-12-28', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(277, 15, '2025-12-28', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(278, 16, '2025-12-28', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(279, 9, '2025-12-29', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(280, 10, '2025-12-29', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(281, 15, '2025-12-29', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(282, 16, '2025-12-29', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(283, 9, '2025-12-30', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(284, 10, '2025-12-30', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(285, 15, '2025-12-30', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(286, 16, '2025-12-30', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(287, 9, '2025-12-31', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(288, 10, '2025-12-31', 36, 0, '2025-12', '2025-11-09 16:25:26'),
(289, 15, '2025-12-31', 41, 0, '2025-12', '2025-11-09 16:25:26'),
(290, 16, '2025-12-31', 41, 0, '2025-12', '2025-11-09 16:25:26');

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
  `is_day_off` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1 = ngày nghỉ, 0 = đi làm',
  `month_year` varchar(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Tháng/Năm áp dụng lịch (ví dụ: 2025-12)',
  `approved_by_managerID` int DEFAULT NULL COMMENT 'Manager đã duyệt (FK từ bảng users)',
  `published_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Ngày hoàn tất/công bố',
  PRIMARY KEY (`officialID`),
  UNIQUE KEY `UQ_Employee_Date_Schedule` (`employeeID`,`date`) COMMENT 'Mỗi nhân viên chỉ có 1 lịch/ngày',
  KEY `IX_schedule_month_year` (`month_year`),
  KEY `FK_schedule_shift` (`shiftID`),
  KEY `FK_schedule_manager` (`approved_by_managerID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `leavebalance`
--

DROP TABLE IF EXISTS `leavebalance`;
CREATE TABLE IF NOT EXISTS `leavebalance` (
  `userID` int NOT NULL COMMENT 'FK: Bảng users',
  `leave_type_id` int NOT NULL COMMENT 'FK: Bảng shifts (tham chiếu ID của AL, SL, PL...)',
  `year` int NOT NULL COMMENT 'Năm áp dụng (VD: 2025)',
  `total_granted` int NOT NULL DEFAULT '0',
  `carried_over` int NOT NULL DEFAULT '0',
  `days_used` int NOT NULL DEFAULT '0',
  `last_updated` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`userID`,`leave_type_id`,`year`) COMMENT 'Mỗi NV chỉ có 1 số dư cho 1 loại phép/năm',
  KEY `FK_leavebalance_Shift` (`leave_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Số dư phép của từng nhân viên';

--
-- Đang đổ dữ liệu cho bảng `leavebalance`
--

INSERT INTO `leavebalance` (`userID`, `leave_type_id`, `year`, `total_granted`, `carried_over`, `days_used`, `last_updated`) VALUES
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
  `leave_type` enum('ANNUAL','SICK','MATERNITY','PATERNITY') COLLATE utf8mb4_unicode_ci NOT NULL,
  `min_years_service` int NOT NULL COMMENT 'Thâm niên tối thiểu (năm)',
  `max_years_service` int DEFAULT NULL,
  `job_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `days` int NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Mô tả (VD: "Nhân viên dưới 5 năm")',
  PRIMARY KEY (`policyID`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Chính sách phép năm theo thâm niên';

--
-- Đang đổ dữ liệu cho bảng `leavepolicy`
--

INSERT INTO `leavepolicy` (`policyID`, `leave_type`, `min_years_service`, `max_years_service`, `job_type`, `days`, `description`) VALUES
(1, 'ANNUAL', 0, 5, NULL, 12, 'Nhân viên có thâm niên dưới 5 năm'),
(2, 'ANNUAL', 5, 10, NULL, 13, 'Nhân viên có thâm niên từ 5 đến dưới 10 năm'),
(3, 'ANNUAL', 10, NULL, NULL, 14, 'Nhân viên có thâm niên từ 10 năm trở lên'),
(7, 'SICK', 0, 15, 'NORMAL', 30, 'Phép ốm cho nhân viên đóng BHXH dưới 15 năm'),
(8, 'SICK', 15, 30, 'NORMAL', 40, 'Phép ốm cho nhân viên đóng BHXH từ 15 năm - 30 năm'),
(9, 'SICK', 30, NULL, 'NORMAL', 60, 'Phép ốm cho nhân viên đóng BHXH từ 30 năm trở lên'),
(10, 'MATERNITY', 0, NULL, NULL, 180, 'Nghỉ thai sản'),
(11, 'PATERNITY', 0, NULL, NULL, 14, 'Nhân viên có vợ sinh con'),
(12, 'SICK', 0, 15, 'DANGER', 40, 'Phép ốm (Độc hại) cho nhân viên đóng BHXH dưới 15 năm'),
(13, 'SICK', 15, 30, 'DANGER', 50, 'Phép ốm (Độc hại) cho nhân viên đóng BHXH từ 15 năm - 30 năm'),
(14, 'SICK', 30, NULL, 'DANGER', 70, 'Phép ốm (Độc hại) cho nhân viên đóng BHXH từ 30 năm trở lên');

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
  `status` enum('PENDING','APPROVED','REJECTED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `requestdate` date DEFAULT NULL,
  `userID` int DEFAULT NULL,
  PRIMARY KEY (`leaverequestID`),
  KEY `userID` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `leaverequests`
--

INSERT INTO `leaverequests` (`leaverequestID`, `leavetype`, `startdate`, `enddate`, `reason`, `status`, `requestdate`, `userID`) VALUES
(1, 'Nghỉ ốm', '2025-10-29', '2025-10-29', 'Bị sốt', 'APPROVED', '2025-10-28', 6),
(2, 'Nghỉ phép năm', '2025-11-05', '2025-11-06', 'Gia đình có việc', 'PENDING', '2025-10-28', 12),
(3, 'Nghỉ phép năm', '2025-10-20', '2025-10-20', 'Việc cá nhân', 'REJECTED', '2025-10-19', 8),
(5, 'Nghỉ ốm (Test)', '2025-12-01', '2025-12-02', 'Test API tạo đơn từ Postman', 'PENDING', '2025-11-13', 15),
(6, 'Nghỉ ốm (Test mail)', '2025-12-06', '2025-12-06', 'Test API tạo đơn từ Postman', 'REJECTED', '2025-11-13', 18);

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
  `required_skillGrade` int NOT NULL COMMENT 'Cấp kỹ năng yêu cầu (ví dụ: 3)',
  `min_staff_count` int NOT NULL DEFAULT '1' COMMENT 'Số lượng nhân viên TỐI THIỂU phải có kỹ năng này (ví dụ: 1)',
  PRIMARY KEY (`ruleID`),
  KEY `FK_rule_req` (`requirementID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Quy tắc kỹ năng chi tiết cho một yêu cầu nhân sự';

--
-- Đang đổ dữ liệu cho bảng `requirementrules`
--

INSERT INTO `requirementrules` (`ruleID`, `requirementID`, `required_skillGrade`, `min_staff_count`) VALUES
(1, 2, 3, 1),
(2, 3, 3, 1);

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
  `total_staff_needed` int NOT NULL DEFAULT '0' COMMENT 'Tổng số nhân viên cần (ví dụ: 3)',
  PRIMARY KEY (`requirementID`),
  UNIQUE KEY `UQ_Dept_Shift` (`departmentID`,`shiftID`) COMMENT 'Mỗi phòng ban/ca chỉ có 1 yêu cầu',
  KEY `FK_schedule_req_shift` (`shiftID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Nhu cầu nhân sự cơ bản cho ca làm việc của phòng ban';

--
-- Đang đổ dữ liệu cho bảng `schedulerequirements`
--

INSERT INTO `schedulerequirements` (`requirementID`, `departmentID`, `shiftID`, `total_staff_needed`) VALUES
(2, 5, 36, 2),
(3, 5, 41, 2);

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
  `duration_hours` int NOT NULL,
  PRIMARY KEY (`shiftID`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `shifts`
--

INSERT INTO `shifts` (`shiftID`, `shiftname`, `starttime`, `endtime`, `duration_hours`) VALUES
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
  `job_type` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'NORMAL',
  PRIMARY KEY (`userID`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `roleID` (`roleID`),
  KEY `departmentID` (`departmentID`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`userID`, `username`, `password`, `fullname`, `cccd`, `email`, `phonenumber`, `birth`, `gender`, `address`, `bankaccount`, `bankname`, `hiredate`, `status`, `roleID`, `departmentID`, `skillGrade`, `job_type`) VALUES
(1, 'admin', '$2a$10$2sQzJxjvMcMeSNOSsysqjOQZzWIpwvHKIdwdeZ.EqQDM6QKcufj0q', 'Phạm Minh Anh', '123456789874', 'admin@manaplastic.com', NULL, NULL, 1, NULL, NULL, NULL, '2023-01-01', 'active', 1, 2, 3, 'NORMAL'),
(2, 'it_support', '$2a$10$skyfJgN4n.Z2GMTP7GLnneUFL4cSm1DWoJdSsYGvF06flQTGz1GBC', 'Lê Hỗ Trợ IT', NULL, 'it.support@manaplastic.com', NULL, NULL, 1, NULL, NULL, NULL, '2023-05-10', 'active', 1, 2, 3, 'NORMAL'),
(3, 'hr_manager', '$2a$10$yVs4Kv0e36Kcb8wesofM3enjSu/Kicj5TFJm6YavsG5TDd2kLtsqy', 'Nguyễn Thị Nhân Sự', '1234567890', 'hr.manager@manaplastic.com', '0123456789', NULL, 0, NULL, NULL, NULL, '2023-02-15', 'active', 2, 1, 1, 'NORMAL'),
(4, 'hr_staff', '$2a$10$wnTMZHPSgAkKLfwNCY3cE.ufDKVPHrdWvaJ5oL.o0dKj7kxBMJNXG', 'Phạm Văn Tuyển Dụng', NULL, 'hr.staff@manaplastic.com', NULL, NULL, 1, NULL, NULL, NULL, '2023-08-01', 'active', 2, 1, 1, 'NORMAL'),
(5, 'kythuat_lead', '$2a$10$QP/JCd6cnNgRkFr3BYzbKOSRMIbdI6Y1fE6D.w.p1xfbvQV9y5UJe', 'Võ Văn Kỹ Thuật', NULL, 'kythuat.lead@manaplastic.com', NULL, NULL, 0, NULL, NULL, NULL, '2023-03-01', 'active', 3, 3, 3, 'NORMAL'),
(6, 'kythuat_staff', '$2a$10$TXgpVb3lT7f/1YQxy2i/he2wiepriI6XqcPO2WcxG2xBLt0V9T8HO', 'Hoàng Thị Máy Móc', NULL, 'kythuat.staff@manaplastic.com', NULL, NULL, 1, NULL, NULL, NULL, '2023-09-10', 'active', 4, 3, 1, 'NORMAL'),
(7, 'sanxuat_lead', '$2a$10$4b25FwYYUWVDgrItKmyyJ.ntqK6S6SJFqLHabuDL.xsoP/yScAov.', 'Trịnh Hữu Sản Xuất', NULL, 'sanxuat.lead@manaplastic.com', NULL, NULL, 1, NULL, NULL, NULL, '2023-03-02', 'active', 3, 4, 3, 'NORMAL'),
(8, 'sanxuat_staff', '$2a$10$TxHjoxC0uVKLIDznDhNWmuF73p7LTpVr1Lf1uGdn1lw2lo8gR0ApS', 'Đặng Văn Vận Hành', NULL, 'sanxuat.staff@manaplastic.com', NULL, NULL, 0, NULL, NULL, NULL, '2023-09-15', 'active', 4, 4, 1, 'NORMAL'),
(9, 'inan_lead', '$2a$10$6v5RYvb1wRZZ333NkxGDLuVRAgXeG0xHcfsxiBQHV0tXRpvai2yYS', 'Bùi Văn Mực', '123456789876', 'inan.lead@manaplastic.com', NULL, NULL, 0, NULL, NULL, 'Vietcombank', '2023-04-01', 'active', 3, 5, 3, 'NORMAL'),
(10, 'inan_staff', '$2a$10$JN0oA3nYZmxvy4FXcXyMHuTBXs4xWWzcoFkXTgSbwgoqyHGmEG3Pm', 'Lý Thị In', NULL, 'inan.staff@manaplastic.com', NULL, NULL, 1, NULL, NULL, NULL, '2023-10-01', 'active', 4, 5, 1, 'NORMAL'),
(11, 'cskh_lead', '$2a$10$bm/qxbS1udqc01zrB/p/HuNdfz1kH9oFBVpfuLoTaGFLZKEuysMF.', 'Đỗ Thị Khách Hàng', NULL, 'cskh.lead@manaplastic.com', NULL, NULL, 1, NULL, NULL, NULL, '2023-05-01', 'active', 3, 6, 3, 'NORMAL'),
(12, 'cskh_staff', '$2a$10$tHV/qqOG68rkmYyYL82LIe5v1hnm.lzY.SlZclzOZoV6j13l6LtD.', 'Mạc Văn Hài Lòng', NULL, 'cskh.staff@manaplastic.com', NULL, NULL, 0, NULL, NULL, NULL, '2023-11-01', 'active', 4, 6, 1, 'NORMAL'),
(13, '57540101', '$2a$10$xPIdXJigdb91ZNCAFFtTu.4RLJpIYAaQGJ70VlaE2wSblfQznOlDi', 'Phạm Minh Anh HR', '123456789876', NULL, NULL, NULL, 0, NULL, NULL, NULL, '2024-04-28', 'active', 2, 1, 3, 'NORMAL'),
(14, '52082901', '$2a$10$jeaJPcpV3IQMoxGHBLw2teRExwzuD0ZB9ubxat7yzrqgAgfWn.CvS', 'testAddAccountHR', '79203031165', NULL, NULL, NULL, 1, NULL, NULL, NULL, '2023-02-09', 'active', 2, 1, 3, 'NORMAL'),
(15, '71939801', '$2a$10$U0qh1Bel43Hp2K6CbsOCXefQQRgRqPJMU5Sah3eIflWTGeSkZlrPG', 'testAddAccountNV', '079203031168', NULL, NULL, NULL, 0, NULL, NULL, NULL, '2023-02-23', 'active', 4, 5, 3, 'NORMAL'),
(16, '83637905', '$2a$10$IHOeCVrioPATwf9X9s3wh.zNbCKAEHubkZxk8uPfqEWrmUb8mJCGm', 'Phạm Nhân Viên', '12345678922', NULL, NULL, NULL, 0, NULL, NULL, NULL, '2024-10-13', 'active', 4, 5, 2, 'NORMAL'),
(17, '72001905', '$2a$10$eQmfti7ZY3a5TXk4xqfSaOc25bW6d1.bScjcFq5pabC7b6D5WTtSK', 'testAddAccountNVinan', '123456789000', NULL, NULL, NULL, 0, NULL, NULL, NULL, '2023-08-03', 'active', 4, NULL, 1, 'NORMAL'),
(18, '56885905', '$2a$10$4WtXYERXuVRu89Fh1KVm4uc0PuMsuHbwMg7/32dl9/qbrsFZvin3m', 'testAddAccountNVinan', '123456789000', 'pminhanh1106@gmail.com', NULL, NULL, 0, NULL, NULL, NULL, '2024-02-17', 'active', 4, 5, 2, 'NORMAL'),
(19, '79753710', '$2a$10$vHtQqmnZ0POJYFnxD42L4O./uv5SHW5viV3DZdBCXAa8vpYdD1MQG', 'Phạm Minh Anh Test Pass', '12345678922', 'phamminhanh11623@gmail.com', '0395168006', '2003-06-11', 1, NULL, '1023765488', NULL, '2024-11-27', 'active', 2, 1, 1, 'NORMAL');

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
  ADD CONSTRAINT `FK_schedule_manager` FOREIGN KEY (`approved_by_managerID`) REFERENCES `users` (`userID`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_schedule_shift` FOREIGN KEY (`shiftID`) REFERENCES `shifts` (`shiftID`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_schedule_user` FOREIGN KEY (`employeeID`) REFERENCES `users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `leavebalance`
--
ALTER TABLE `leavebalance`
  ADD CONSTRAINT `FK_leavebalance_Shift` FOREIGN KEY (`leave_type_id`) REFERENCES `shifts` (`shiftID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_leavebalance_User` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

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
