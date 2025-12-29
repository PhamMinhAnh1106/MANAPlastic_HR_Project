-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1:3306
-- Thời gian đã tạo: Th12 23, 2025 lúc 05:52 PM
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
  `username` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `userID` int DEFAULT NULL,
  PRIMARY KEY (`logID`),
  KEY `userID` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `activitylogs`
--

INSERT INTO `activitylogs` (`logID`, `action`, `logType`, `details`, `actiontime`, `username`, `userID`) VALUES
(1, 'CREATE_LEAVE_POLICY', 'INFO', 'Desc: Thêm mới chính sách nghỉ phép | Data: {\"headers\":{},\"body\":{\"gendertarget\":null,\"id\":17,\"days\":15,\"description\":\"Nhân viên thâm niên trên 20 năm\",\"leavetype\":\"ANNUAL\",\"minyearsservice\":20,\"maxyearsservice\":null,\"jobtype\":null,\"leavetypeid\":{\"id\":53,\"shiftname\":\"AL (Anually Leave)\",\"starttime\":\"00:00:00\",\"endtime\":\"00:00:00\",\"durationHours\":0,\"shiftnameAsEnum\":\"ANNUAL\"}},\"statusCode\":\"OK\",\"statusCodeValue\":200}', '2025-12-20 14:21:53', '000022', NULL),
(2, 'UPDATE_ACCOUNT', 'INFO', 'Desc: Sửa thông tin tài khoản | Data: {\"headers\":{},\"body\":\"Cập nhật tài khoản thành công!\",\"statusCode\":\"OK\",\"statusCodeValue\":200}', '2025-12-22 01:00:10', 'hr_manager', 3),
(3, 'UPDATE_ROLE_PERMISSION', 'WARNING', 'Desc: Mở quyền/Ngăn chặn khẩn cấp quyền mặc đinh của role | Data: {\"headers\":{},\"body\":\"Cập nhật quyền cho Role thành công!\",\"statusCode\":\"OK\",\"statusCodeValue\":200}', '2025-12-22 01:26:39', 'admin', 1),
(4, 'UPDATE_OVERTIME_TYPE', 'WARNING', 'Desc: Sửa chính sách loại tăng ca | Data: {\"headers\":{},\"body\":{\"id\":7,\"otCode\":\"OT_NIGHT_HOLIDAY\",\"otName\":\"Tăng ca đêm Lễ Tết (390%)\",\"rate\":3.8,\"isTaxExemptPart\":true,\"calculationType\":\"MULTIPLIER\",\"taxExemptFormula\":\"EXCESS_ONLY\",\"taxExemptPercentage\":0,\"description\":\"Làm thêm giờ vào ban đêm ngày Lễ/Tết\"},\"statusCode\":\"OK\",\"statusCodeValue\":200}', '2025-12-22 01:27:19', 'admin', 1),
(5, 'ADD_ACCOUNT_PERMISSION', 'INFO', 'Desc: Cấp / Chặn quyền của tài khoản | Data: {\"headers\":{},\"body\":\"Đã CHẶN (Blacklist) quyền thành công cho user.\",\"statusCode\":\"OK\",\"statusCodeValue\":200}', '2025-12-22 01:32:10', 'admin', 1),
(6, 'ADD_ACCOUNT_PERMISSION', 'INFO', 'Desc: Cấp / Chặn quyền của tài khoản | Data: {\"headers\":{},\"body\":\"Đã CẤP (Whitelist) quyền thành công cho user.\",\"statusCode\":\"OK\",\"statusCodeValue\":200}', '2025-12-22 01:32:22', 'admin', 1),
(7, 'RESET_ACCOUNT_PERMISSION', 'DANGER', 'Desc: Trả về quyền mặc định của tài khoản | Data: {\"headers\":{},\"body\":\"Đã reset quyền (ID: 1) của user [000021] về mặc định.\",\"statusCode\":\"OK\",\"statusCodeValue\":200}', '2025-12-22 01:32:27', 'admin', 1),
(8, 'UPDATE_ROLE_PERMISSION', 'WARNING', 'Desc: Mở quyền/Ngăn chặn khẩn cấp quyền mặc đinh của role | Data: {\"headers\":{},\"body\":\"Cập nhật quyền cho Role thành công!\",\"statusCode\":\"OK\",\"statusCodeValue\":200}', '2025-12-23 22:47:26', 'admin', 1),
(9, 'UPDATE_ROLE_PERMISSION', 'WARNING', 'Desc: Mở quyền/Ngăn chặn khẩn cấp quyền mặc đinh của role | Data: {\"headers\":{},\"body\":\"Cập nhật quyền cho Role thành công!\",\"statusCode\":\"OK\",\"statusCodeValue\":200}', '2025-12-23 22:47:31', 'admin', 1),
(10, 'UPDATE_ROLE_PERMISSION', 'WARNING', 'Desc: Mở quyền/Ngăn chặn khẩn cấp quyền mặc đinh của role | Data: {\"headers\":{},\"body\":\"Cập nhật quyền cho Role thành công!\",\"statusCode\":\"OK\",\"statusCodeValue\":200}', '2025-12-23 22:48:48', 'admin', 1),
(11, 'CREATE_ATTENDANCE_REQ', 'INFO', 'Desc: Gửi yêu cầu bổ sung công | Data: {\"headers\":{},\"body\":\"Gửi yêu cầu thành công! Vui lòng chờ quản lý duyệt.\",\"statusCode\":\"OK\",\"statusCodeValue\":200}', '2025-12-23 23:55:16', '000020', 20),
(12, 'APPROVE_ATTENDANCE_REQ', 'WARNING', 'Desc: Duyệt yêu cầu công | Data: {\"headers\":{},\"body\":\"Đã duyệt yêu cầu và cập nhật dữ liệu chấm công thành công.\",\"statusCodeValue\":200,\"statusCode\":\"OK\"}', '2025-12-24 00:11:52', '000022', 22);

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
-- Cấu trúc bảng cho bảng `attendancerequests`
--

DROP TABLE IF EXISTS `attendancerequests`;
CREATE TABLE IF NOT EXISTS `attendancerequests` (
  `requestid` int NOT NULL AUTO_INCREMENT,
  `userid` int NOT NULL COMMENT 'Nhân viên gửi yêu cầu',
  `date` date NOT NULL COMMENT 'Ngày cần bổ sung công',
  `shiftid` int DEFAULT NULL COMMENT 'Ca làm việc liên quan',
  `requesttype` enum('CHECK_IN','CHECK_OUT','FULL_SHIFT') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `checkintime` datetime DEFAULT NULL,
  `checkouttime` datetime DEFAULT NULL,
  `imgproof` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `status` enum('PENDING','APPROVED','REJECTED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING',
  `approverid` int DEFAULT NULL COMMENT 'Người duyệt (HR/Manager)',
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `createdat` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedat` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`requestid`),
  KEY `fk_attrequest_user` (`userid`),
  KEY `fk_attrequest_approver` (`approverid`),
  KEY `fk_attrequest_shift` (`shiftid`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Yêu cầu bổ sung dữ liệu chấm công';

--
-- Đang đổ dữ liệu cho bảng `attendancerequests`
--

INSERT INTO `attendancerequests` (`requestid`, `userid`, `date`, `shiftid`, `requesttype`, `checkintime`, `checkouttime`, `imgproof`, `reason`, `status`, `approverid`, `comment`, `createdat`, `updatedat`) VALUES
(1, 6, '2025-11-02', 1, 'CHECK_OUT', NULL, '2025-11-02 17:00:00', '/uploads/proofs/checkout_fix_6.jpg', 'Quên check-out khi về', 'PENDING', NULL, NULL, '2025-12-23 21:51:15', '2025-12-23 21:51:15'),
(2, 8, '2025-11-04', 2, 'FULL_SHIFT', '2025-11-04 06:00:00', '2025-11-04 14:00:00', '/uploads/proofs/full_fix_8.jpg', 'Máy chấm công bị lỗi không nhận diện được', 'PENDING', NULL, NULL, '2025-12-23 21:51:15', '2025-12-23 21:51:15'),
(3, 20, '2025-12-03', 36, 'CHECK_IN', '2025-12-03 08:00:00', NULL, '/uploads/proofs/a07b9472-6f8c-49e7-b5d9-a1939248dca0_Gemini_Generated_Image_1iwfj11iwfj11iwf (1).png', 'Chấm công hư máy', 'APPROVED', 22, NULL, '2025-12-23 23:55:15', '2025-12-24 00:11:52');

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
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
(46, '2025-11-30', '2025-11-30 07:55:00', '2025-11-30 17:05:00', NULL, NULL, 'PRESENT', 36, 21, NULL, NULL),
(47, '2025-12-01', '2025-12-01 07:55:00', '2025-12-01 17:00:00', NULL, NULL, 'PRESENT', 36, 20, NULL, NULL),
(48, '2025-12-02', '2025-12-02 21:50:00', '2025-12-03 06:10:00', NULL, NULL, 'PRESENT', 50, 20, NULL, NULL),
(49, '2025-12-03', '2025-12-03 08:00:00', '2025-12-03 17:05:00', NULL, NULL, 'PRESENT', 36, 20, NULL, NULL);

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
  `WorkType` enum('FULLTIME','PARTTIME') COLLATE utf8mb4_unicode_ci DEFAULT 'FULLTIME' COMMENT 'Loại hình làm việc: FULLTIME=8h, PARTTIME=6h',
  PRIMARY KEY (`contractID`),
  KEY `IDX_Contract_User` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `contracts`
--

INSERT INTO `contracts` (`contractID`, `contractname`, `type`, `basesalary`, `InsuranceSalary`, `AllowanceToxicType`, `fileurl`, `signdate`, `startdate`, `enddate`, `Status`, `userID`, `WorkType`) VALUES
(1, 'HĐLĐ Admin', 'INDEFINITE', 30000000.00, 30000000.00, 'NONE', NULL, '2023-01-01', '2023-01-01', NULL, 'ACTIVE', 1, 'FULLTIME'),
(2, 'HĐLĐ IT', 'FIXED_TERM', 15000000.00, 15000000.00, 'NONE', NULL, '2023-05-10', '2023-05-10', '2026-05-10', 'ACTIVE', 2, 'FULLTIME'),
(3, 'HĐLĐ Trưởng phòng HR', 'INDEFINITE', 25000000.00, 25000000.00, 'NONE', NULL, '2023-02-15', '2023-02-15', NULL, 'ACTIVE', 3, 'FULLTIME'),
(4, 'HĐLĐ Nhân viên HR', 'FIXED_TERM', 12000000.00, 12000000.00, 'NONE', NULL, '2023-08-01', '2023-08-01', '2026-08-01', 'ACTIVE', 4, 'FULLTIME'),
(5, 'HĐLĐ Trưởng nhóm Kỹ thuật', 'FIXED_TERM', 22000000.00, 22000000.00, 'IN_KIND', NULL, '2023-03-01', '2023-03-01', '2026-03-01', 'ACTIVE', 5, 'FULLTIME'),
(6, 'HĐLĐ Nhân viên Kỹ thuật', 'FIXED_TERM', 10000000.00, 10000000.00, 'CASH', NULL, '2023-09-10', '2023-09-10', '2026-09-10', 'ACTIVE', 6, 'FULLTIME'),
(7, 'HĐLĐ Trưởng ca Sản xuất', 'INDEFINITE', 20000000.00, 20000000.00, 'IN_KIND', NULL, '2023-03-02', '2023-03-02', NULL, 'ACTIVE', 7, 'FULLTIME'),
(8, 'HĐLĐ Công nhân Sản xuất', 'FIXED_TERM', 9000000.00, 9000000.00, 'CASH', NULL, '2023-09-15', '2023-09-15', '2026-09-15', 'ACTIVE', 8, 'FULLTIME'),
(9, 'HĐLĐ Trưởng nhóm In ấn', 'FIXED_TERM', 18000000.00, 18000000.00, 'IN_KIND', NULL, '2023-04-01', '2023-04-01', '2026-04-01', 'ACTIVE', 9, 'FULLTIME'),
(10, 'HĐLĐ Công nhân In ấn', 'FIXED_TERM', 9000000.00, 9000000.00, 'IN_KIND', NULL, '2023-10-01', '2023-10-01', '2026-10-01', 'ACTIVE', 10, 'FULLTIME'),
(11, 'HĐLĐ Trưởng nhóm CSKH', 'FIXED_TERM', 17000000.00, 17000000.00, 'NONE', NULL, '2023-05-01', '2023-05-01', '2026-05-01', 'ACTIVE', 11, 'FULLTIME'),
(12, 'HĐLĐ Nhân viên CSKH', 'FIXED_TERM', 8000000.00, 8000000.00, 'NONE', NULL, '2023-11-01', '2023-11-01', '2026-11-01', 'ACTIVE', 12, 'FULLTIME'),
(13, 'HĐLĐ Nhân viên HR', 'FIXED_TERM', 14000000.00, 14000000.00, 'NONE', NULL, '2024-04-28', '2024-04-28', '2026-04-28', 'ACTIVE', 13, 'FULLTIME'),
(14, 'HĐLĐ Thực tập sinh HR', 'FIXED_TERM', 5000000.00, 0.00, 'NONE', NULL, '2023-02-09', '2023-02-09', '2026-02-09', 'ACTIVE', 14, 'FULLTIME'),
(15, 'HĐLĐ Nhân viên In ấn', 'FIXED_TERM', 9500000.00, 9500000.00, 'IN_KIND', NULL, '2023-02-23', '2023-02-23', '2026-02-23', 'ACTIVE', 15, 'FULLTIME'),
(16, 'HĐLĐ Nhân viên In ấn', 'FIXED_TERM', 9200000.00, 9200000.00, 'IN_KIND', NULL, '2024-10-13', '2024-10-13', '2026-10-13', 'ACTIVE', 16, 'FULLTIME'),
(17, 'HĐLĐ Nhân viên In ấn', 'FIXED_TERM', 8800000.00, 8800000.00, 'CASH', NULL, '2023-08-03', '2023-08-03', '2026-08-03', 'ACTIVE', 17, 'FULLTIME'),
(18, 'HĐLĐ Nhân viên In ấn', 'FIXED_TERM', 9000000.00, 9000000.00, 'CASH', NULL, '2024-02-17', '2024-02-17', '2026-02-17', 'HISTORY', 18, 'FULLTIME'),
(19, 'HĐLĐ Chuyên viên Tuyển dụng', 'FIXED_TERM', 12000000.00, 12000000.00, 'NONE', NULL, '2024-11-27', '2024-11-27', '2025-11-27', 'HISTORY', 19, 'FULLTIME'),
(20, 'HĐLĐ Nhân viên In ấn (Thử việc)', 'PROBATION', 7500000.00, 0.00, 'IN_KIND', NULL, '2025-11-15', '2025-11-15', '2026-01-15', 'ACTIVE', 20, 'FULLTIME'),
(21, 'Hợp đồng Tái ký 2026 - test add hdld', 'FIXED_TERM', 15000000.00, 15000000.00, 'NONE', '/uploads/contracts/be665f42-7761-447b-985e-fa5afc98cf90.pdf', '2025-11-21', '2025-11-28', '2026-11-28', 'ACTIVE', 19, 'FULLTIME'),
(22, 'Hợp đồng Tái ký 2026 - test add hdld', 'FIXED_TERM', 15000000.00, 15000000.00, 'NONE', '/uploads/contracts/ed354bd8-4ef6-411e-a987-f461080a20c0.pdf', '2025-11-21', '2025-11-28', '2026-11-28', 'ACTIVE', 18, 'FULLTIME'),
(25, 'HDLD chính thức 12 tháng', 'FIXED_TERM', 15000000.00, 15000000.00, 'NONE', '/uploads/contracts/54ee9112-26eb-4675-91e2-be668549c24f.pdf', '2025-11-27', '2025-11-28', '2026-11-28', 'ACTIVE', 21, 'FULLTIME');

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
) ENGINE=InnoDB AUTO_INCREMENT=375 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
(373, 8, '2026-01-01', 36, 0, '2026-01', '2025-12-15 11:57:50'),
(374, 8, '2026-01-02', 36, 0, '2026-01', '2025-12-15 12:00:51');

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
) ENGINE=InnoDB AUTO_INCREMENT=326 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
(26, 20, '2025-12-02', 50, 0, '2025-12', 9, '2025-12-11 09:45:58'),
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
(130, 20, '2025-12-17', 41, 0, '2025-12', 9, '2025-12-15 09:14:05'),
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
(232, 17, '2025-12-31', NULL, 1, '2025-12', 9, '2025-12-11 09:45:58'),
(233, 5, '2025-12-01', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(234, 6, '2025-12-01', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(235, 21, '2025-12-01', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(236, 5, '2025-12-02', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(237, 6, '2025-12-02', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(238, 21, '2025-12-02', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(239, 5, '2025-12-03', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(240, 6, '2025-12-03', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(241, 21, '2025-12-03', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(242, 5, '2025-12-04', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(243, 6, '2025-12-04', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(244, 21, '2025-12-04', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(245, 5, '2025-12-05', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(246, 6, '2025-12-05', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(247, 21, '2025-12-05', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(248, 5, '2025-12-06', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(249, 6, '2025-12-06', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(250, 21, '2025-12-06', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(251, 5, '2025-12-07', NULL, 1, '2025-12', 21, '2025-12-15 08:45:41'),
(252, 6, '2025-12-07', NULL, 1, '2025-12', 21, '2025-12-15 08:45:41'),
(253, 21, '2025-12-07', NULL, 1, '2025-12', 21, '2025-12-15 08:45:41'),
(254, 5, '2025-12-08', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(255, 21, '2025-12-08', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(256, 6, '2025-12-08', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(257, 5, '2025-12-09', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(258, 21, '2025-12-09', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(259, 6, '2025-12-09', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(260, 5, '2025-12-10', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(261, 21, '2025-12-10', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(262, 6, '2025-12-10', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(263, 5, '2025-12-11', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(264, 21, '2025-12-11', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(265, 6, '2025-12-11', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(266, 5, '2025-12-12', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(267, 21, '2025-12-12', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(268, 6, '2025-12-12', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(269, 5, '2025-12-13', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(270, 21, '2025-12-13', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(271, 6, '2025-12-13', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(272, 5, '2025-12-14', NULL, 1, '2025-12', 21, '2025-12-15 08:45:41'),
(273, 6, '2025-12-14', NULL, 1, '2025-12', 21, '2025-12-15 08:45:41'),
(274, 21, '2025-12-14', NULL, 1, '2025-12', 21, '2025-12-15 08:45:41'),
(275, 5, '2025-12-15', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(276, 6, '2025-12-15', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(277, 21, '2025-12-15', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(278, 5, '2025-12-16', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(279, 6, '2025-12-16', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(280, 21, '2025-12-16', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(281, 5, '2025-12-17', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(282, 6, '2025-12-17', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(283, 21, '2025-12-17', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(284, 6, '2025-12-18', 40, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(285, 5, '2025-12-18', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(286, 21, '2025-12-18', NULL, 1, '2025-12', 21, '2025-12-15 08:45:41'),
(287, 21, '2025-12-19', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(288, 6, '2025-12-19', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(289, 5, '2025-12-19', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(290, 21, '2025-12-20', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(291, 6, '2025-12-20', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(292, 5, '2025-12-20', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(293, 21, '2025-12-21', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(294, 5, '2025-12-21', NULL, 1, '2025-12', 21, '2025-12-15 08:45:41'),
(295, 6, '2025-12-21', NULL, 1, '2025-12', 21, '2025-12-15 08:45:41'),
(296, 21, '2025-12-22', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(297, 6, '2025-12-22', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(298, 5, '2025-12-22', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(299, 21, '2025-12-23', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(300, 6, '2025-12-23', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(301, 5, '2025-12-23', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(302, 21, '2025-12-24', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(303, 6, '2025-12-24', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(304, 5, '2025-12-24', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(305, 5, '2025-12-25', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(306, 6, '2025-12-25', NULL, 1, '2025-12', 21, '2025-12-15 08:45:41'),
(307, 21, '2025-12-25', NULL, 1, '2025-12', 21, '2025-12-15 08:45:41'),
(308, 21, '2025-12-26', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(309, 6, '2025-12-26', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(310, 5, '2025-12-26', NULL, 1, '2025-12', 21, '2025-12-15 08:45:41'),
(311, 21, '2025-12-27', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(312, 5, '2025-12-27', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(313, 6, '2025-12-27', NULL, 1, '2025-12', 21, '2025-12-15 08:45:41'),
(314, 21, '2025-12-28', 36, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(315, 6, '2025-12-28', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(316, 5, '2025-12-28', NULL, 1, '2025-12', 21, '2025-12-15 08:45:41'),
(317, 5, '2025-12-29', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(318, 6, '2025-12-29', NULL, 1, '2025-12', 21, '2025-12-15 08:45:41'),
(319, 21, '2025-12-29', NULL, 1, '2025-12', 21, '2025-12-15 08:45:41'),
(320, 6, '2025-12-30', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(321, 5, '2025-12-30', NULL, 1, '2025-12', 21, '2025-12-15 08:45:41'),
(322, 21, '2025-12-30', NULL, 1, '2025-12', 21, '2025-12-15 08:45:41'),
(323, 5, '2025-12-31', 41, 0, '2025-12', 21, '2025-12-15 08:45:41'),
(324, 6, '2025-12-31', NULL, 1, '2025-12', 21, '2025-12-15 08:45:41'),
(325, 21, '2025-12-31', NULL, 1, '2025-12', 21, '2025-12-15 08:45:41');

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
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Chính sách phép năm theo thâm niên';

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
(14, 'SICK', 30, NULL, 'DANGER', NULL, 70, 'Phép ốm (Độc hại) cho nhân viên đóng BHXH từ 30 năm trở lên', 54),
(17, 'ANNUAL', 20, NULL, NULL, NULL, 15, 'Nhân viên thâm niên trên 20 năm', 53);

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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
(9, 'ANNUAL', '2025-12-25', '2025-12-26', 'Nghỉ phép test chức năng', 'PENDING', NULL, 6, NULL),
(10, 'ANNUAL', '2025-12-23', '2025-12-23', 'test', 'PENDING', '2025-12-21', 8, 53);

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
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
(12, 12, 2025, '2025-11-26', '2025-12-25', 26.00),
(13, 1, 2026, '2025-12-26', '2026-01-25', 26.00),
(14, 2, 2026, '2026-01-26', '2026-02-25', 26.00),
(15, 3, 2026, '2026-02-26', '2026-03-25', 22.00),
(16, 4, 2026, '2026-03-26', '2026-04-25', 26.00),
(17, 5, 2026, '2026-04-26', '2026-05-25', 26.00),
(18, 6, 2026, '2026-05-26', '2026-06-25', 26.00),
(19, 7, 2026, '2026-06-26', '2026-07-25', 26.00),
(20, 8, 2026, '2026-07-26', '2026-08-25', 26.00),
(21, 9, 2026, '2026-08-26', '2026-09-25', 26.00),
(22, 10, 2026, '2026-09-26', '2026-10-25', 26.00),
(23, 11, 2026, '2026-10-26', '2026-11-25', 26.00),
(24, 12, 2026, '2026-11-26', '2026-12-25', 26.00);

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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `overtime`
--

INSERT INTO `overtime` (`id`, `userid`, `overtimetypeid`, `date`, `hours`, `status`, `reason`) VALUES
(1, 1, 1, '2025-10-15', 3, 'APPROVED', 'Chạy deadline dự án'),
(2, 1, 2, '2025-10-19', 4, 'APPROVED', 'Bảo trì hệ thống cuối tuần'),
(3, 2, 1, '2025-10-20', 2, 'PENDING', 'Hỗ trợ khách hàng muộn'),
(4, 20, 1, '2025-12-01', 2, 'APPROVED', 'Làm cố nốt dự án'),
(5, 20, 5, '2025-12-03', 2, 'APPROVED', 'Hỗ trợ khách hàng nước ngoài'),
(6, 20, 2, '2025-12-07', 4, 'APPROVED', 'Bảo trì máy móc cuối tuần');

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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `overtimetypes`
--

INSERT INTO `overtimetypes` (`OvertimeTypeID`, `OtCode`, `OtName`, `Rate`, `CalculationType`, `IsTaxExemptPart`, `TaxExemptFormula`, `TaxExemptPercentage`, `Description`) VALUES
(1, 'OT_WEEKDAY', 'Làm thêm ngày thường', 1.50, 'MULTIPLIER', 1, 'EXCESS_ONLY', 0.00, 'Làm thêm giờ ngày thường: 150% lương giờ. Phần vượt 100% được miễn thuế.'),
(2, 'OT_WEEKEND', 'Làm thêm ngày nghỉ tuần', 2.00, 'MULTIPLIER', 1, 'EXCESS_ONLY', 0.00, 'Làm thêm giờ ngày nghỉ tuần: 200% lương giờ. Phần vượt 100% được miễn thuế.'),
(3, 'OT_HOLIDAY', 'Làm thêm ngày Lễ/Tết', 3.00, 'MULTIPLIER', 1, 'EXCESS_ONLY', 0.00, 'Làm thêm giờ ngày Lễ/Tết: 300% lương giờ. Phần vượt 100% được miễn thuế.'),
(4, 'NIGHT_SHIFT', 'Phụ cấp ca đêm (30%)', 0.30, 'ADDITIVE', 1, 'EXCESS_ONLY', 0.00, 'Phụ cấp dành cho giờ làm việc chính thức rơi vào khung 22h-06h'),
(5, 'OT_NIGHT_WEEKDAY', 'Tăng ca đêm ngày thường (200%)', 2.00, 'MULTIPLIER', 1, 'EXCESS_ONLY', 0.00, 'Làm thêm giờ vào ban đêm ngày thường'),
(6, 'OT_NIGHT_WEEKEND', 'Tăng ca đêm ngày nghỉ (260%)', 2.60, 'MULTIPLIER', 1, 'EXCESS_ONLY', 0.00, 'Làm thêm giờ vào ban đêm ngày nghỉ tuần'),
(7, 'OT_NIGHT_HOLIDAY', 'Tăng ca đêm Lễ Tết (390%)', 3.80, 'MULTIPLIER', 1, 'EXCESS_ONLY', 0.00, 'Làm thêm giờ vào ban đêm ngày Lễ/Tết');

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
) ENGINE=InnoDB AUTO_INCREMENT=1211 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `payrolls`
--

INSERT INTO `payrolls` (`payID`, `netsalary`, `payperiod`, `userID`, `totalincome`, `status`, `pit`, `bhxh_emp`, `bhyt_emp`, `bhtn_emp`, `basesalarysnapshot`, `insurancesalarysnapshot`, `standardworkdays`, `actualworkdays`, `othours`, `dependentcount`, `bhxh_comp`, `bhyt_comp`, `bhtn_comp`) VALUES
(1, 10500000.00, '2025-09', 6, 0.00, 'DRAFT', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 26.00, 0.00, 0.00, 0, 0.00, 0.00, 0.00),
(2, 9100000.00, '2025-09', 8, 0.00, 'DRAFT', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 26.00, 0.00, 0.00, 0, 0.00, 0.00, 0.00),
(981, -3300000.00, '2025-11', 1, 0.00, 'DRAFT', 0.00, 2400000.00, 600000.00, 300000.00, 30000000.00, 30000000.00, 26.00, 0.00, 0.00, 2, 5400000.00, 900000.00, 300000.00),
(982, -1650000.00, '2025-11', 2, 0.00, 'DRAFT', 0.00, 1200000.00, 300000.00, 150000.00, 15000000.00, 15000000.00, 26.00, 0.00, 0.00, 1, 2700000.00, 450000.00, 150000.00),
(983, -2750000.00, '2025-11', 3, 0.00, 'DRAFT', 0.00, 2000000.00, 500000.00, 250000.00, 25000000.00, 25000000.00, 26.00, 0.00, 0.00, 1, 4500000.00, 750000.00, 250000.00),
(984, -1320000.00, '2025-11', 4, 0.00, 'DRAFT', 0.00, 960000.00, 240000.00, 120000.00, 12000000.00, 12000000.00, 26.00, 0.00, 0.00, 1, 2160000.00, 360000.00, 120000.00),
(985, -1430384.62, '2025-11', 5, 989615.38, 'DRAFT', 0.00, 1760000.00, 440000.00, 220000.00, 22000000.00, 22000000.00, 26.00, 1.00, 0.00, 2, 3960000.00, 660000.00, 220000.00),
(986, -636153.84, '2025-11', 6, 663846.16, 'DRAFT', 0.00, 800000.00, 200000.00, 100000.00, 10000000.00, 10000000.00, 26.00, 2.00, 0.00, 1, 1800000.00, 300000.00, 100000.00),
(987, -2200000.00, '2025-11', 7, 0.00, 'DRAFT', 0.00, 1600000.00, 400000.00, 200000.00, 20000000.00, 20000000.00, 26.00, 0.00, 0.00, 1, 3600000.00, 600000.00, 200000.00),
(988, 1403461.54, '2025-11', 8, 2393461.54, 'DRAFT', 0.00, 720000.00, 180000.00, 90000.00, 9000000.00, 9000000.00, 26.00, 1.00, 0.00, 1, 1620000.00, 270000.00, 90000.00),
(989, -1980000.00, '2025-11', 9, 0.00, 'DRAFT', 0.00, 1440000.00, 360000.00, 180000.00, 18000000.00, 18000000.00, 26.00, 0.00, 0.00, 2, 3240000.00, 540000.00, 180000.00),
(990, -1990000.00, '2025-11', 10, -500000.00, 'DRAFT', 0.00, 720000.00, 180000.00, 90000.00, 9000000.00, 9000000.00, 26.00, 0.00, 0.00, 1, 1620000.00, 270000.00, 90000.00),
(991, -1870000.00, '2025-11', 11, 0.00, 'DRAFT', 0.00, 1360000.00, 340000.00, 170000.00, 17000000.00, 17000000.00, 26.00, 0.00, 0.00, 1, 3060000.00, 510000.00, 170000.00),
(992, -528846.15, '2025-11', 12, 351153.85, 'DRAFT', 0.00, 640000.00, 160000.00, 80000.00, 8000000.00, 8000000.00, 26.00, 1.00, 0.00, 1, 1440000.00, 240000.00, 80000.00),
(993, -1540000.00, '2025-11', 13, 0.00, 'DRAFT', 0.00, 1120000.00, 280000.00, 140000.00, 14000000.00, 14000000.00, 26.00, 0.00, 0.00, 1, 2520000.00, 420000.00, 140000.00),
(994, 0.00, '2025-11', 14, 0.00, 'DRAFT', 0.00, 0.00, 0.00, 0.00, 5000000.00, 0.00, 26.00, 0.00, 0.00, 1, 0.00, 0.00, 0.00),
(995, -1045000.00, '2025-11', 15, 0.00, 'DRAFT', 0.00, 760000.00, 190000.00, 95000.00, 9500000.00, 9500000.00, 26.00, 0.00, 0.00, 1, 1710000.00, 285000.00, 95000.00),
(996, -3012000.00, '2025-11', 16, -1000000.00, 'DRAFT', 0.00, 736000.00, 184000.00, 92000.00, 9200000.00, 9200000.00, 26.00, 0.00, 0.00, 1, 1656000.00, 276000.00, 92000.00),
(997, -968000.00, '2025-11', 17, 0.00, 'DRAFT', 0.00, 704000.00, 176000.00, 88000.00, 8800000.00, 8800000.00, 26.00, 0.00, 0.00, 1, 1584000.00, 264000.00, 88000.00),
(998, -1650000.00, '2025-11', 18, 0.00, 'DRAFT', 0.00, 1200000.00, 300000.00, 150000.00, 15000000.00, 15000000.00, 26.00, 0.00, 0.00, 1, 2700000.00, 450000.00, 150000.00),
(999, -1650000.00, '2025-11', 19, 0.00, 'DRAFT', 0.00, 1200000.00, 300000.00, 150000.00, 15000000.00, 15000000.00, 26.00, 0.00, 0.00, 1, 2700000.00, 450000.00, 150000.00),
(1000, 0.00, '2025-11', 20, 0.00, 'DRAFT', 0.00, 0.00, 0.00, 0.00, 7500000.00, 0.00, 26.00, 0.00, 0.00, 0, 0.00, 0.00, 0.00),
(1001, 10465384.68, '2025-11', 21, 12115384.68, 'DRAFT', 0.00, 1200000.00, 300000.00, 150000.00, 15000000.00, 15000000.00, 26.00, 21.00, 0.00, 0, 2700000.00, 450000.00, 150000.00),
(1002, 0.00, '2025-11', 22, 0.00, 'DRAFT', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 26.00, 0.00, 0.00, 0, 0.00, 0.00, 0.00),
(1003, 0.00, '2025-11', 23, 0.00, 'DRAFT', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 26.00, 0.00, 0.00, 0, 0.00, 0.00, 0.00),
(1119, -3150000.00, '2025-12', 1, 0.00, 'FINAL', 0.00, 2400000.00, 450000.00, 300000.00, 30000000.00, 30000000.00, 26.00, 0.00, 0.00, 2, 5250000.00, 900000.00, 300000.00),
(1120, -1575000.00, '2025-12', 2, 0.00, 'FINAL', 0.00, 1200000.00, 225000.00, 150000.00, 15000000.00, 15000000.00, 26.00, 0.00, 0.00, 1, 2625000.00, 450000.00, 150000.00),
(1121, -2625000.00, '2025-12', 3, 0.00, 'FINAL', 0.00, 2000000.00, 375000.00, 250000.00, 25000000.00, 25000000.00, 26.00, 0.00, 0.00, 1, 4375000.00, 750000.00, 250000.00),
(1122, -1260000.00, '2025-12', 4, 0.00, 'FINAL', 0.00, 960000.00, 180000.00, 120000.00, 12000000.00, 12000000.00, 26.00, 0.00, 0.00, 1, 2100000.00, 360000.00, 120000.00),
(1123, 690000.00, '2025-12', 5, 3000000.00, 'FINAL', 0.00, 1760000.00, 330000.00, 220000.00, 22000000.00, 22000000.00, 26.00, 0.00, 0.00, 2, 3850000.00, 660000.00, 220000.00),
(1124, -1050000.00, '2025-12', 6, 0.00, 'FINAL', 0.00, 800000.00, 150000.00, 100000.00, 10000000.00, 10000000.00, 26.00, 0.00, 0.00, 1, 1750000.00, 300000.00, 100000.00),
(1125, -2100000.00, '2025-12', 7, 0.00, 'FINAL', 0.00, 1600000.00, 300000.00, 200000.00, 20000000.00, 20000000.00, 26.00, 0.00, 0.00, 1, 3500000.00, 600000.00, 200000.00),
(1126, -945000.00, '2025-12', 8, 0.00, 'FINAL', 0.00, 720000.00, 135000.00, 90000.00, 9000000.00, 9000000.00, 26.00, 0.00, 0.00, 1, 1575000.00, 270000.00, 90000.00),
(1127, -1890000.00, '2025-12', 9, 0.00, 'FINAL', 0.00, 1440000.00, 270000.00, 180000.00, 18000000.00, 18000000.00, 26.00, 0.00, 0.00, 2, 3150000.00, 540000.00, 180000.00),
(1128, 55000.00, '2025-12', 10, 1000000.00, 'FINAL', 0.00, 720000.00, 135000.00, 90000.00, 9000000.00, 9000000.00, 26.00, 0.00, 0.00, 1, 1575000.00, 270000.00, 90000.00),
(1129, -1785000.00, '2025-12', 11, 0.00, 'FINAL', 0.00, 1360000.00, 255000.00, 170000.00, 17000000.00, 17000000.00, 26.00, 0.00, 0.00, 1, 2975000.00, 510000.00, 170000.00),
(1130, -340000.00, '2025-12', 12, 500000.00, 'FINAL', 0.00, 640000.00, 120000.00, 80000.00, 8000000.00, 8000000.00, 26.00, 0.00, 0.00, 1, 1400000.00, 240000.00, 80000.00),
(1131, -1470000.00, '2025-12', 13, 0.00, 'FINAL', 0.00, 1120000.00, 210000.00, 140000.00, 14000000.00, 14000000.00, 26.00, 0.00, 0.00, 1, 2450000.00, 420000.00, 140000.00),
(1132, 0.00, '2025-12', 14, 0.00, 'FINAL', 0.00, 0.00, 0.00, 0.00, 5000000.00, 0.00, 26.00, 0.00, 0.00, 1, 0.00, 0.00, 0.00),
(1133, -997500.00, '2025-12', 15, 0.00, 'FINAL', 0.00, 760000.00, 142500.00, 95000.00, 9500000.00, 9500000.00, 26.00, 0.00, 0.00, 1, 1662500.00, 285000.00, 95000.00),
(1134, -966000.00, '2025-12', 16, 0.00, 'FINAL', 0.00, 736000.00, 138000.00, 92000.00, 9200000.00, 9200000.00, 26.00, 0.00, 0.00, 1, 1610000.00, 276000.00, 92000.00),
(1135, -924000.00, '2025-12', 17, 0.00, 'FINAL', 0.00, 704000.00, 132000.00, 88000.00, 8800000.00, 8800000.00, 26.00, 0.00, 0.00, 1, 1540000.00, 264000.00, 88000.00),
(1136, -1575000.00, '2025-12', 18, 0.00, 'FINAL', 0.00, 1200000.00, 225000.00, 150000.00, 15000000.00, 15000000.00, 26.00, 0.00, 0.00, 1, 2625000.00, 450000.00, 150000.00),
(1137, -1575000.00, '2025-12', 19, 0.00, 'FINAL', 0.00, 1200000.00, 225000.00, 150000.00, 15000000.00, 15000000.00, 26.00, 0.00, 0.00, 1, 2625000.00, 450000.00, 150000.00),
(1138, 1577019.23, '2025-12', 20, 1577019.23, 'FINAL', 0.00, 0.00, 0.00, 0.00, 7500000.00, 0.00, 26.00, 3.00, 0.00, 0, 0.00, 0.00, 0.00),
(1139, 1309615.40, '2025-12', 21, 2884615.40, 'FINAL', 0.00, 1200000.00, 225000.00, 150000.00, 15000000.00, 15000000.00, 26.00, 5.00, 0.00, 0, 2625000.00, 450000.00, 150000.00),
(1140, 0.00, '2025-12', 22, 0.00, 'FINAL', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 26.00, 0.00, 0.00, 0, 0.00, 0.00, 0.00),
(1141, 0.00, '2025-12', 23, 0.00, 'FINAL', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 26.00, 0.00, 0.00, 0, 0.00, 0.00, 0.00);

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
  `active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`roleID`,`permissionID`),
  KEY `permissionID` (`permissionID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `rolespermissions`
--

INSERT INTO `rolespermissions` (`roleID`, `permissionID`, `active`) VALUES
(1, 1, 1),
(1, 2, 1),
(1, 3, 1),
(1, 4, 1),
(1, 5, 1),
(1, 6, 1),
(1, 7, 1),
(1, 8, 1),
(1, 9, 1),
(1, 10, 1),
(1, 11, 1),
(1, 12, 1),
(1, 13, 1),
(1, 31, 1),
(2, 1, 1),
(2, 2, 1),
(2, 3, 1),
(2, 4, 1),
(2, 5, 1),
(2, 6, 1),
(2, 7, 1),
(2, 8, 1),
(2, 14, 1),
(2, 15, 1),
(2, 16, 1),
(2, 17, 1),
(2, 18, 1),
(2, 19, 1),
(2, 20, 1),
(2, 28, 1),
(2, 29, 1),
(2, 30, 1),
(2, 31, 1),
(2, 32, 1),
(2, 33, 1),
(2, 34, 1),
(2, 35, 1),
(2, 36, 1),
(2, 37, 1),
(2, 38, 1),
(3, 1, 1),
(3, 2, 1),
(3, 3, 1),
(3, 4, 1),
(3, 5, 1),
(3, 6, 1),
(3, 14, 1),
(3, 15, 1),
(3, 16, 1),
(3, 19, 1),
(3, 21, 1),
(3, 22, 1),
(3, 24, 1),
(3, 25, 1),
(3, 27, 1),
(3, 29, 1),
(3, 30, 1),
(3, 39, 1),
(3, 40, 1),
(4, 1, 1),
(4, 2, 1),
(4, 3, 1),
(4, 4, 1),
(4, 5, 0),
(4, 14, 1),
(4, 15, 1),
(4, 21, 1),
(4, 23, 1),
(4, 24, 1),
(4, 25, 1),
(4, 26, 1),
(4, 30, 1);

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
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
(10, 'Tổng thu nhập miễn thuế', 'TAX_EXEMPT_INCOME', 'Tổng TaxFreeAmount + Chênh lệch OT', '\nSELECT\n    (SELECT COALESCE(SUM(TaxFreeAmount), 0)\n     FROM contractallowances ca\n     JOIN contracts c ON ca.ContractID = c.contractID\n     WHERE c.userID = :userId AND c.Status = \'ACTIVE\')\n    +\n    (SELECT COALESCE(SUM(Amount), 0)\n     FROM rewardpunishmentdecisions\n     WHERE UserID = :userId AND Type = \'REWARD\' AND Status IN (\'APPROVED\',\'PROCESSED\') AND IsTaxExempt = 1 AND DecisionDate BETWEEN :startDate AND :endDate)\n    +\n    (SELECT COALESCE(SUM(\n        CASE\n           \n            WHEN ot.TaxExemptFormula = \'EXCESS_ONLY\' THEN\n                o.hours * (SELECT (c.basesalary / 26 / 8) FROM contracts c WHERE c.userID = :userId AND c.Status = \'ACTIVE\' LIMIT 1) * (ot.Rate - 1)\n            WHEN ot.TaxExemptFormula = \'FULL_AMOUNT\' THEN\n                o.hours * (SELECT (c.basesalary / 26 / 8) FROM contracts c WHERE c.userID = :userId AND c.Status = \'ACTIVE\' LIMIT 1) * ot.Rate\n            WHEN ot.TaxExemptFormula = \'PERCENTAGE\' THEN\n                o.hours * (SELECT (c.basesalary / 26 / 8) FROM contracts c WHERE c.userID = :userId AND c.Status = \'ACTIVE\' LIMIT 1) * ot.Rate * (ot.TaxExemptPercentage / 100)\n            ELSE 0\n        END\n    ), 0)\n    FROM overtime o\n    JOIN overtimetypes ot ON o.overtimetypeid = ot.OvertimeTypeID\n    WHERE o.userid = :userId\n      AND o.status = \'APPROVED\'\n      AND o.date BETWEEN :startDate AND :endDate)\n', NULL, NULL, '{\"code\": \"TAX_EXEMPT_INCOME\", \"mode\": \"RAW_SQL\", \"name\": \"Tổng thu nhập miễn thuế\", \"sqlQuery\": \"\\nSELECT \\n    (SELECT COALESCE(SUM(TaxFreeAmount), 0) FROM contractallowances ca JOIN contracts c ON ca.ContractID = c.contractID WHERE c.userID = :userId AND c.Status = \'ACTIVE\')\\n    +\\n    (SELECT COALESCE(SUM(Amount), 0) FROM rewardpunishmentdecisions WHERE UserID = :userId AND Type = \'REWARD\' AND Status = \'APPROVED\' AND IsTaxExempt = 1 AND DecisionDate BETWEEN :startDate AND :endDate)\\n\", \"trueValue\": 0, \"falseValue\": 0, \"description\": \"Tổng TaxFreeAmount + Chênh lệch OT\"}'),
(11, 'Giảm trừ gia cảnh', 'FAMILY_DEDUCTION', '11tr + (Người phụ thuộc * 4.4tr)', '\nSELECT \n    (SELECT Value FROM taxsettings WHERE SettingKey = \'PERSONAL_DEDUCTION\' ORDER BY EffectiveDate DESC LIMIT 1) \n    + \n    (\n      (SELECT COALESCE(COUNT(*), 0) FROM dependents WHERE userID = :userId AND istaxdeductible = 1) \n      * (SELECT Value FROM taxsettings WHERE SettingKey = \'DEPENDENT_DEDUCTION\' ORDER BY EffectiveDate DESC LIMIT 1)\n    )\n', NULL, NULL, '{\"code\": \"FAMILY_DEDUCTION\", \"mode\": \"RAW_SQL\", \"name\": \"Giảm trừ gia cảnh\", \"sqlQuery\": \"\\nSELECT \\n    (SELECT Value FROM taxsettings WHERE SettingKey = \'PERSONAL_DEDUCTION\' ORDER BY EffectiveDate DESC LIMIT 1) \\n    + \\n    (\\n      (SELECT COALESCE(COUNT(*), 0) FROM dependents WHERE userID = :userId AND istaxdeductible = 1) \\n      * (SELECT Value FROM taxsettings WHERE SettingKey = \'DEPENDENT_DEDUCTION\' ORDER BY EffectiveDate DESC LIMIT 1)\\n    )\\n\", \"trueValue\": 0, \"falseValue\": 0, \"description\": \"11tr + (Người phụ thuộc * 4.4tr)\"}'),
(12, 'Thu nhập tính thuế', 'ASSESSABLE_INCOME', 'Thu nhập chịu thuế - Bảo hiểm - Giảm trừ gia cảnh', NULL, NULL, NULL, '{\"code\": \"ASSESSABLE_INCOME\", \"mode\": \"CALCULATED\", \"name\": \"Thu nhập tính thuế\", \"description\": \"Biến được tính toán tự động bởi hệ thống (Computed Variable)\"}'),
(13, 'Số người phụ thuộc', 'DEPENDENT_COUNT', 'Lấy từ bảng dependents', 'SELECT COALESCE(COUNT(*), 0) FROM dependents WHERE userID = :userId AND istaxdeductible = 1', NULL, NULL, '{\"code\": \"DEPENDENT_COUNT\", \"mode\": \"RAW_SQL\", \"name\": \"Số người phụ thuộc\", \"sqlQuery\": \"SELECT COALESCE(COUNT(*), 0) FROM dependents WHERE userID = :userId AND istaxdeductible = 1\", \"trueValue\": 0, \"falseValue\": 0, \"description\": \"Lấy từ bảng dependents\"}'),
(14, 'Lương cơ sở nhà nước', 'BASIC_SALARY_STATE', 'Lấy từ bảng TaxSettings', 'SELECT Value FROM taxsettings WHERE SettingKey = \'BASIC_SALARY_STATE\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1', NULL, NULL, '{\"code\": \"BASIC_SALARY_STATE\", \"mode\": \"RAW_SQL\", \"name\": \"Lương cơ sở nhà nước\", \"sqlQuery\": \"SELECT Value FROM taxsettings WHERE SettingKey = \'BASIC_SALARY_STATE\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1\", \"trueValue\": 0, \"falseValue\": 0, \"description\": \"Lấy từ bảng TaxSettings\"}'),
(15, 'Lương tối thiểu vùng', 'REGION_MIN_SALARY', 'Lấy từ bảng TaxSettings', 'SELECT Value FROM taxsettings WHERE SettingKey = \'REGION_MIN_SALARY\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1', NULL, NULL, '{\"code\": \"REGION_MIN_SALARY\", \"mode\": \"RAW_SQL\", \"name\": \"Lương tối thiểu vùng\", \"sqlQuery\": \"SELECT Value FROM taxsettings WHERE SettingKey = \'REGION_MIN_SALARY\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1\", \"trueValue\": 0, \"falseValue\": 0, \"description\": \"Lấy từ bảng TaxSettings\"}'),
(16, 'Hệ số trần bảo hiểm', 'INSURANCE_CAP_MULTIPLIER', 'Lấy từ bảng TaxSettings (thường là 20 lần)', 'SELECT Value FROM taxsettings WHERE SettingKey = \'INSURANCE_CAP_MULTIPLIER\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1', NULL, NULL, '{\"code\": \"INSURANCE_CAP_MULTIPLIER\", \"mode\": \"RAW_SQL\", \"name\": \"Hệ số trần bảo hiểm\", \"sqlQuery\": \"SELECT Value FROM taxsettings WHERE SettingKey = \'INSURANCE_CAP_MULTIPLIER\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1\", \"trueValue\": 0, \"falseValue\": 0, \"description\": \"Lấy từ bảng TaxSettings (thường là 20 lần)\"}'),
(17, 'Giới hạn ăn ca miễn thuế', 'LUNCH_ALLOWANCE_LIMIT', 'Lấy từ bảng TaxSettings', 'SELECT Value FROM taxsettings WHERE SettingKey = \'LUNCH_ALLOWANCE_LIMIT\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1', NULL, NULL, '{\"code\": \"LUNCH_ALLOWANCE_LIMIT\", \"mode\": \"RAW_SQL\", \"name\": \"Giới hạn ăn ca miễn thuế\", \"sqlQuery\": \"SELECT Value FROM taxsettings WHERE SettingKey = \'LUNCH_ALLOWANCE_LIMIT\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1\", \"trueValue\": 0, \"falseValue\": 0, \"description\": \"Lấy từ bảng TaxSettings\"}'),
(25, 'Mức giảm trừ bản thân', 'PERSONAL_DEDUCTION', 'Lấy từ bảng TaxSettings', 'SELECT Value FROM taxsettings WHERE SettingKey = \'PERSONAL_DEDUCTION\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1', NULL, NULL, NULL),
(26, 'Mức giảm trừ người phụ thuộc', 'DEPENDENT_DEDUCTION', 'Lấy từ bảng TaxSettings', 'SELECT Value FROM taxsettings WHERE SettingKey = \'DEPENDENT_DEDUCTION\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1', NULL, NULL, NULL),
(27, 'Lương 1 giờ làm việc (Quy đổi)', 'HOURLY_MONEY', 'Tính toán: (Lương cơ bản + PC chịu thuế) / Ngày công chuẩn / Giờ làm việc (8 hoặc 6)', 'SELECT \n        (\n            c.basesalary + \n            COALESCE((SELECT SUM(Amount) FROM contractallowances ca WHERE ca.ContractID = c.contractID AND ca.IsTaxable = 1), 0)\n        ) \n        / \n        (\n            (SELECT StandardWorkDays FROM monthlypayrollconfigs WHERE Month = :month AND Year = :year LIMIT 1) \n            * (CASE WHEN c.WorkType = \'PARTTIME\' THEN 6.0 ELSE 8.0 END)\n        )\n    FROM contracts c \n    WHERE c.userID = :userId AND c.Status = \'ACTIVE\' \n    LIMIT 1', NULL, NULL, '{\"code\": \"HOURLY_MONEY\", \"mode\": \"RAW_SQL\", \"name\": \"Lương 1 giờ làm việc (Quy đổi)\", \"sqlQuery\": \"SELECT (c.basesalary + COALESCE((SELECT SUM(Amount) FROM contractallowances ca WHERE ca.ContractID = c.contractID AND ca.IsTaxable = 1), 0)) / ((SELECT StandardWorkDays FROM monthlypayrollconfigs WHERE Month = :month AND Year = :year LIMIT 1) * (CASE WHEN c.WorkType = \'PARTTIME\' THEN 6.0 ELSE 8.0 END)) FROM contracts c WHERE c.userID = :userId AND c.Status = \'ACTIVE\' LIMIT 1\", \"description\": \"Tự động tính dựa trên WorkType và PC chịu thuế\"}'),
(29, 'Tổng giờ làm đêm (22h-06h)', 'TOTAL_NIGHT_HOURS', 'Tổng số giờ làm việc thực tế rơi vào khung 22h đêm đến 06h sáng', 'SELECT \n        COALESCE(SUM(\n            CASE \n                WHEN s.endtime < s.starttime THEN \n                    (GREATEST(0, 24 - GREATEST(HOUR(s.starttime), 22)) + LEAST(HOUR(s.endtime), 6))\n                WHEN s.starttime < \'06:00:00\' AND s.starttime >= \'00:00:00\' THEN\n                    GREATEST(0, LEAST(HOUR(s.endtime), 6) - HOUR(s.starttime))\n                WHEN s.endtime > \'22:00:00\' AND s.endtime > s.starttime THEN\n                    GREATEST(0, HOUR(s.endtime) - 22)\n                ELSE 0 \n            END\n        ), 0)\n    FROM attendances a\n    JOIN shifts s ON a.shiftID = s.shiftID\n    WHERE a.userID = :userId \n      AND a.status = \'PRESENT\'\n      AND a.date BETWEEN :startDate AND :endDate', NULL, NULL, '{\"code\": \"TOTAL_NIGHT_HOURS\", \"mode\": \"RAW_SQL\", \"name\": \"Tổng giờ làm đêm (22h-06h)\", \"sqlQuery\": \"SELECT COALESCE(SUM(CASE WHEN s.endtime < s.starttime THEN (GREATEST(0, 24 - GREATEST(HOUR(s.starttime), 22)) + LEAST(HOUR(s.endtime), 6)) WHEN s.starttime < \'06:00:00\' AND s.starttime >= \'00:00:00\' THEN GREATEST(0, LEAST(HOUR(s.endtime), 6) - HOUR(s.starttime)) WHEN s.endtime > \'22:00:00\' AND s.endtime > s.starttime THEN GREATEST(0, HOUR(s.endtime) - 22) ELSE 0 END), 0) FROM attendances a JOIN shifts s ON a.shiftID = s.shiftID WHERE a.userID = :userId AND a.status = \'PRESENT\' AND a.date BETWEEN :startDate AND :endDate\", \"description\": \"Tự động tách giờ đêm từ bảng chấm công\"}'),
(33, 'Tỷ lệ BHXH (NV)', 'RATE_BHXH_EMP', 'Lấy từ TaxSettings', 'SELECT Value FROM taxsettings WHERE SettingKey = \'BHXH_RATE_EMP\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1', NULL, NULL, '{\"code\": \"RATE_BHXH_EMP\", \"mode\": \"RAW_SQL\", \"name\": \"Tỷ lệ BHXH (NV)\", \"description\": \"Lấy từ bảng TaxSettings\"}'),
(34, 'Tỷ lệ BHYT (NV)', 'RATE_BHYT_EMP', 'Lấy từ TaxSettings', 'SELECT Value FROM taxsettings WHERE SettingKey = \'BHYT_RATE_EMP\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1', NULL, NULL, '{\"code\": \"RATE_BHYT_EMP\", \"mode\": \"RAW_SQL\", \"name\": \"Tỷ lệ BHYT (NV)\", \"description\": \"Lấy từ bảng TaxSettings\"}'),
(35, 'Tỷ lệ BHTN (NV)', 'RATE_BHTN_EMP', 'Lấy từ TaxSettings', 'SELECT Value FROM taxsettings WHERE SettingKey = \'BHTN_RATE_EMP\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1', NULL, NULL, '{\"code\": \"RATE_BHTN_EMP\", \"mode\": \"RAW_SQL\", \"name\": \"Tỷ lệ BHTN (NV)\", \"description\": \"Lấy từ bảng TaxSettings\"}'),
(36, 'Tỷ lệ BHXH (CT)', 'RATE_BHXH_COMP', 'Lấy từ TaxSettings', 'SELECT Value FROM taxsettings WHERE SettingKey = \'BHXH_RATE_COMP\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1', NULL, NULL, '{\"code\": \"RATE_BHXH_COMP\", \"mode\": \"RAW_SQL\", \"name\": \"Tỷ lệ BHXH (CT)\", \"description\": \"Lấy từ bảng TaxSettings\"}'),
(37, 'Tỷ lệ BHYT (CT)', 'RATE_BHYT_COMP', 'Lấy từ TaxSettings', 'SELECT Value FROM taxsettings WHERE SettingKey = \'BHYT_RATE_COMP\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1', NULL, NULL, '{\"code\": \"RATE_BHYT_COMP\", \"mode\": \"RAW_SQL\", \"name\": \"Tỷ lệ BHYT (CT)\", \"description\": \"Lấy từ bảng TaxSettings\"}'),
(38, 'Tỷ lệ BHTN (CT)', 'RATE_BHTN_COMP', 'Lấy từ TaxSettings', 'SELECT Value FROM taxsettings WHERE SettingKey = \'BHTN_RATE_COMP\' AND EffectiveDate <= :endDate ORDER BY EffectiveDate DESC LIMIT 1', NULL, NULL, '{\"code\": \"RATE_BHTN_COMP\", \"mode\": \"RAW_SQL\", \"name\": \"Tỷ lệ BHTN (CT)\", \"description\": \"Lấy từ bảng TaxSettings\"}'),
(39, 'Hệ số phụ cấp ca đêm', 'RATE_NIGHT_SHIFT', 'Lấy từ bảng Overtimetypes (Mã NIGHT_SHIFT)', 'SELECT Rate FROM overtimetypes WHERE OtCode = \'NIGHT_SHIFT\' LIMIT 1', NULL, NULL, '{\"code\": \"RATE_NIGHT_SHIFT\", \"mode\": \"RAW_SQL\", \"name\": \"Hệ số phụ cấp ca đêm\", \"description\": \"Lấy từ bảng overtimetypes\"}');

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
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
(20, 'BHXH_EMP', 'BHXH Nhân viên (8%)', NULL, 'APPROVED', 21, '2025-12-02 21:00:44', 4),
(21, 'BHYT_EMP', 'BHYT Nhân viên (1.5%)', NULL, 'APPROVED', 22, '2025-12-02 21:00:44', 4),
(22, 'BHTN_EMP', 'BHTN Nhân viên (1%)', NULL, 'APPROVED', 23, '2025-12-02 21:00:44', 4),
(24, 'BHXH_COMP', 'BHXH Công ty chi trả (17.5%)', 'Phần BHXH doanh nghiệp đóng', 'APPROVED', 24, '2025-12-11 14:48:14', 90),
(25, 'BHYT_COMP', 'BHYT Công ty chi trả (3%)', 'Phần BHYT doanh nghiệp đóng', 'APPROVED', 25, '2025-12-11 14:48:14', 90),
(26, 'BHTN_COMP', 'BHTN Công ty chi trả (1%)', 'Phần BHTN doanh nghiệp đóng', 'APPROVED', 26, '2025-12-11 14:48:14', 90),
(27, 'NIGHT_MONEY', 'Tiền phụ cấp ca đêm', NULL, 'APPROVED', 27, '2025-12-14 18:36:00', 2);

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
  KEY `FK_RuleVersion_Rule` (`rule_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `salary_rule_version`
--

INSERT INTO `salary_rule_version` (`version_id`, `rule_id`, `version_number`, `dsl_json`, `created_at`) VALUES
(1, 1, 1, '{\"left\": {\"left\": {\"left\": {\"name\": \"BASE_SALARY\", \"type\": \"VARIABLE\"}, \"type\": \"ADD\", \"right\": {\"name\": \"TOTAL_ALLOWANCE\", \"type\": \"VARIABLE\"}}, \"type\": \"DIV\", \"right\": {\"name\": \"STD_DAYS\", \"type\": \"VARIABLE\"}}, \"type\": \"MUL\", \"right\": {\"name\": \"REAL_WORK_DAYS\", \"type\": \"VARIABLE\"}}', '2025-12-01 01:47:36'),
(2, 2, 1, '{\"left\": {\"name\": \"INSURANCE_SALARY\", \"type\": \"VARIABLE\"}, \"type\": \"MUL\", \"right\": {\"type\": \"CONSTANT\", \"value\": 0.105}}', '2025-12-01 01:47:36'),
(3, 3, 1, '{\"left\": {\"left\": {\"left\": {\"left\": {\"name\": \"WORK_SALARY\", \"type\": \"REFERENCE\"}, \"type\": \"ADD\", \"right\": {\"name\": \"TOTAL_REWARD\", \"type\": \"VARIABLE\"}}, \"type\": \"ADD\", \"right\": {\"left\": {\"name\": \"TOTAL_OT_CONVERTED\", \"type\": \"VARIABLE\"}, \"type\": \"MUL\", \"right\": {\"name\": \"HOURLY_MONEY\", \"type\": \"VARIABLE\"}}}, \"type\": \"ADD\", \"right\": {\"name\": \"NIGHT_MONEY\", \"type\": \"REFERENCE\"}}, \"type\": \"SUB\", \"right\": {\"name\": \"TOTAL_PENALTY\", \"type\": \"VARIABLE\"}}', '2025-12-01 01:47:36'),
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
(20, 26, 1, '{\"left\": {\"name\": \"INSURANCE_SALARY\", \"type\": \"VARIABLE\"}, \"type\": \"MUL\", \"right\": {\"type\": \"CONSTANT\", \"value\": 0.01}}', '2025-12-11 14:48:43'),
(21, 20, 2, '{\"left\": {\"name\": \"INSURANCE_SALARY\", \"type\": \"VARIABLE\"}, \"type\": \"MUL\", \"right\": {\"name\": \"RATE_BHXH_EMP\", \"type\": \"VARIABLE\"}}', '2025-12-14 18:18:49'),
(22, 21, 2, '{\"left\": {\"name\": \"INSURANCE_SALARY\", \"type\": \"VARIABLE\"}, \"type\": \"MUL\", \"right\": {\"name\": \"RATE_BHYT_EMP\", \"type\": \"VARIABLE\"}}', '2025-12-14 18:18:49'),
(23, 22, 2, '{\"left\": {\"name\": \"INSURANCE_SALARY\", \"type\": \"VARIABLE\"}, \"type\": \"MUL\", \"right\": {\"name\": \"RATE_BHTN_EMP\", \"type\": \"VARIABLE\"}}', '2025-12-14 18:18:49'),
(24, 24, 2, '{\"left\": {\"name\": \"INSURANCE_SALARY\", \"type\": \"VARIABLE\"}, \"type\": \"MUL\", \"right\": {\"name\": \"RATE_BHXH_COMP\", \"type\": \"VARIABLE\"}}', '2025-12-14 18:18:49'),
(25, 25, 2, '{\"left\": {\"name\": \"INSURANCE_SALARY\", \"type\": \"VARIABLE\"}, \"type\": \"MUL\", \"right\": {\"name\": \"RATE_BHYT_COMP\", \"type\": \"VARIABLE\"}}', '2025-12-14 18:18:49'),
(26, 26, 2, '{\"left\": {\"name\": \"INSURANCE_SALARY\", \"type\": \"VARIABLE\"}, \"type\": \"MUL\", \"right\": {\"name\": \"RATE_BHTN_COMP\", \"type\": \"VARIABLE\"}}', '2025-12-14 18:18:49'),
(27, 27, 1, '{\"left\": {\"left\": {\"name\": \"TOTAL_NIGHT_HOURS\", \"type\": \"VARIABLE\"}, \"type\": \"MUL\", \"right\": {\"name\": \"HOURLY_MONEY\", \"type\": \"VARIABLE\"}}, \"type\": \"MUL\", \"right\": {\"name\": \"RATE_NIGHT_SHIFT\", \"type\": \"VARIABLE\"}}', '2025-12-14 18:36:00');

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
  `value` decimal(20,4) DEFAULT NULL,
  `evaluated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`cache_id`),
  UNIQUE KEY `UQ_Cache_Var` (`variable_id`,`employee_id`,`payperiod`),
  UNIQUE KEY `UQ_Cache_Rule` (`rule_id`,`employee_id`,`payperiod`),
  KEY `FK_Cache_Employee` (`employee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2692 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `salary_variable_cache`
--

INSERT INTO `salary_variable_cache` (`cache_id`, `variable_id`, `rule_id`, `employee_id`, `payperiod`, `value`, `evaluated_at`) VALUES
(1, 39, NULL, 1, '2025-12', 0.3000, '2025-12-18 15:07:58'),
(2, 26, NULL, 1, '2025-12', 4400000.0000, '2025-12-18 15:07:58'),
(3, 35, NULL, 1, '2025-12', 0.0100, '2025-12-18 15:07:58'),
(4, 25, NULL, 1, '2025-12', 11000000.0000, '2025-12-18 15:07:58'),
(5, 36, NULL, 1, '2025-12', 0.1750, '2025-12-18 15:07:58'),
(6, 34, NULL, 1, '2025-12', 0.0150, '2025-12-18 15:07:58'),
(7, 14, NULL, 1, '2025-12', 2340000.0000, '2025-12-18 15:07:58'),
(8, 13, NULL, 1, '2025-12', 2.0000, '2025-12-18 15:07:58'),
(9, 17, NULL, 1, '2025-12', 1000000.0000, '2025-12-18 15:07:58'),
(10, 3, NULL, 1, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(11, 29, NULL, 1, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(12, 10, NULL, 1, '2025-12', 730000.0000, '2025-12-18 15:07:58'),
(13, 2, NULL, 1, '2025-12', 26.0000, '2025-12-18 15:07:58'),
(14, 7, NULL, 1, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(15, 15, NULL, 1, '2025-12', 4960000.0000, '2025-12-18 15:07:58'),
(16, 4, NULL, 1, '2025-12', 3730000.0000, '2025-12-18 15:07:58'),
(17, 16, NULL, 1, '2025-12', 20.0000, '2025-12-18 15:07:58'),
(18, 5, NULL, 1, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(19, 27, NULL, 1, '2025-12', 158653.8462, '2025-12-18 15:07:58'),
(20, 6, NULL, 1, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(21, 8, NULL, 1, '2025-12', 30000000.0000, '2025-12-18 15:07:58'),
(22, 33, NULL, 1, '2025-12', 0.0800, '2025-12-18 15:07:58'),
(23, 38, NULL, 1, '2025-12', 0.0100, '2025-12-18 15:07:58'),
(24, 37, NULL, 1, '2025-12', 0.0300, '2025-12-18 15:07:58'),
(25, 1, NULL, 1, '2025-12', 30000000.0000, '2025-12-18 15:07:58'),
(26, 11, NULL, 1, '2025-12', 19800000.0000, '2025-12-18 15:07:58'),
(27, NULL, 1, 1, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(28, NULL, 27, 1, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(29, NULL, 3, 1, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(30, NULL, 20, 1, '2025-12', 2400000.0000, '2025-12-18 15:07:58'),
(31, NULL, 21, 1, '2025-12', 450000.0000, '2025-12-18 15:07:58'),
(32, NULL, 22, 1, '2025-12', 300000.0000, '2025-12-18 15:07:58'),
(33, NULL, 4, 1, '2025-12', -23680000.0000, '2025-12-18 15:07:58'),
(34, NULL, 5, 1, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(35, NULL, 19, 1, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(36, NULL, 24, 1, '2025-12', 5250000.0000, '2025-12-18 15:07:58'),
(37, NULL, 25, 1, '2025-12', 900000.0000, '2025-12-18 15:07:58'),
(38, NULL, 26, 1, '2025-12', 300000.0000, '2025-12-18 15:07:58'),
(39, NULL, 6, 1, '2025-12', -3150000.0000, '2025-12-18 15:07:58'),
(40, 39, NULL, 2, '2025-12', 0.3000, '2025-12-18 15:07:58'),
(41, 26, NULL, 2, '2025-12', 4400000.0000, '2025-12-18 15:07:58'),
(42, 35, NULL, 2, '2025-12', 0.0100, '2025-12-18 15:07:58'),
(43, 25, NULL, 2, '2025-12', 11000000.0000, '2025-12-18 15:07:58'),
(44, 36, NULL, 2, '2025-12', 0.1750, '2025-12-18 15:07:58'),
(45, 34, NULL, 2, '2025-12', 0.0150, '2025-12-18 15:07:58'),
(46, 14, NULL, 2, '2025-12', 2340000.0000, '2025-12-18 15:07:58'),
(47, 13, NULL, 2, '2025-12', 1.0000, '2025-12-18 15:07:58'),
(48, 17, NULL, 2, '2025-12', 1000000.0000, '2025-12-18 15:07:58'),
(49, 3, NULL, 2, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(50, 29, NULL, 2, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(51, 10, NULL, 2, '2025-12', 730000.0000, '2025-12-18 15:07:58'),
(52, 2, NULL, 2, '2025-12', 26.0000, '2025-12-18 15:07:58'),
(53, 7, NULL, 2, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(54, 15, NULL, 2, '2025-12', 4960000.0000, '2025-12-18 15:07:58'),
(55, 4, NULL, 2, '2025-12', 730000.0000, '2025-12-18 15:07:58'),
(56, 16, NULL, 2, '2025-12', 20.0000, '2025-12-18 15:07:58'),
(57, 5, NULL, 2, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(58, 27, NULL, 2, '2025-12', 72115.3846, '2025-12-18 15:07:58'),
(59, 6, NULL, 2, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(60, 8, NULL, 2, '2025-12', 15000000.0000, '2025-12-18 15:07:58'),
(61, 33, NULL, 2, '2025-12', 0.0800, '2025-12-18 15:07:58'),
(62, 38, NULL, 2, '2025-12', 0.0100, '2025-12-18 15:07:58'),
(63, 37, NULL, 2, '2025-12', 0.0300, '2025-12-18 15:07:58'),
(64, 1, NULL, 2, '2025-12', 15000000.0000, '2025-12-18 15:07:58'),
(65, 11, NULL, 2, '2025-12', 15400000.0000, '2025-12-18 15:07:58'),
(66, NULL, 1, 2, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(67, NULL, 27, 2, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(68, NULL, 3, 2, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(69, NULL, 20, 2, '2025-12', 1200000.0000, '2025-12-18 15:07:58'),
(70, NULL, 21, 2, '2025-12', 225000.0000, '2025-12-18 15:07:58'),
(71, NULL, 22, 2, '2025-12', 150000.0000, '2025-12-18 15:07:58'),
(72, NULL, 4, 2, '2025-12', -17705000.0000, '2025-12-18 15:07:58'),
(73, NULL, 5, 2, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(74, NULL, 19, 2, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(75, NULL, 24, 2, '2025-12', 2625000.0000, '2025-12-18 15:07:58'),
(76, NULL, 25, 2, '2025-12', 450000.0000, '2025-12-18 15:07:58'),
(77, NULL, 26, 2, '2025-12', 150000.0000, '2025-12-18 15:07:58'),
(78, NULL, 6, 2, '2025-12', -1575000.0000, '2025-12-18 15:07:58'),
(79, 39, NULL, 3, '2025-12', 0.3000, '2025-12-18 15:07:58'),
(80, 26, NULL, 3, '2025-12', 4400000.0000, '2025-12-18 15:07:58'),
(81, 35, NULL, 3, '2025-12', 0.0100, '2025-12-18 15:07:58'),
(82, 25, NULL, 3, '2025-12', 11000000.0000, '2025-12-18 15:07:58'),
(83, 36, NULL, 3, '2025-12', 0.1750, '2025-12-18 15:07:58'),
(84, 34, NULL, 3, '2025-12', 0.0150, '2025-12-18 15:07:58'),
(85, 14, NULL, 3, '2025-12', 2340000.0000, '2025-12-18 15:07:58'),
(86, 13, NULL, 3, '2025-12', 1.0000, '2025-12-18 15:07:58'),
(87, 17, NULL, 3, '2025-12', 1000000.0000, '2025-12-18 15:07:58'),
(88, 3, NULL, 3, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(89, 29, NULL, 3, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(90, 10, NULL, 3, '2025-12', 730000.0000, '2025-12-18 15:07:58'),
(91, 2, NULL, 3, '2025-12', 26.0000, '2025-12-18 15:07:58'),
(92, 7, NULL, 3, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(93, 15, NULL, 3, '2025-12', 4960000.0000, '2025-12-18 15:07:58'),
(94, 4, NULL, 3, '2025-12', 3730000.0000, '2025-12-18 15:07:58'),
(95, 16, NULL, 3, '2025-12', 20.0000, '2025-12-18 15:07:58'),
(96, 5, NULL, 3, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(97, 27, NULL, 3, '2025-12', 134615.3846, '2025-12-18 15:07:58'),
(98, 6, NULL, 3, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(99, 8, NULL, 3, '2025-12', 25000000.0000, '2025-12-18 15:07:58'),
(100, 33, NULL, 3, '2025-12', 0.0800, '2025-12-18 15:07:58'),
(101, 38, NULL, 3, '2025-12', 0.0100, '2025-12-18 15:07:58'),
(102, 37, NULL, 3, '2025-12', 0.0300, '2025-12-18 15:07:58'),
(103, 1, NULL, 3, '2025-12', 25000000.0000, '2025-12-18 15:07:58'),
(104, 11, NULL, 3, '2025-12', 15400000.0000, '2025-12-18 15:07:58'),
(105, NULL, 1, 3, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(106, NULL, 27, 3, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(107, NULL, 3, 3, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(108, NULL, 20, 3, '2025-12', 2000000.0000, '2025-12-18 15:07:58'),
(109, NULL, 21, 3, '2025-12', 375000.0000, '2025-12-18 15:07:58'),
(110, NULL, 22, 3, '2025-12', 250000.0000, '2025-12-18 15:07:58'),
(111, NULL, 4, 3, '2025-12', -18755000.0000, '2025-12-18 15:07:58'),
(112, NULL, 5, 3, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(113, NULL, 19, 3, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(114, NULL, 24, 3, '2025-12', 4375000.0000, '2025-12-18 15:07:58'),
(115, NULL, 25, 3, '2025-12', 750000.0000, '2025-12-18 15:07:58'),
(116, NULL, 26, 3, '2025-12', 250000.0000, '2025-12-18 15:07:58'),
(117, NULL, 6, 3, '2025-12', -2625000.0000, '2025-12-18 15:07:58'),
(118, 39, NULL, 4, '2025-12', 0.3000, '2025-12-18 15:07:58'),
(119, 26, NULL, 4, '2025-12', 4400000.0000, '2025-12-18 15:07:58'),
(120, 35, NULL, 4, '2025-12', 0.0100, '2025-12-18 15:07:58'),
(121, 25, NULL, 4, '2025-12', 11000000.0000, '2025-12-18 15:07:58'),
(122, 36, NULL, 4, '2025-12', 0.1750, '2025-12-18 15:07:58'),
(123, 34, NULL, 4, '2025-12', 0.0150, '2025-12-18 15:07:58'),
(124, 14, NULL, 4, '2025-12', 2340000.0000, '2025-12-18 15:07:58'),
(125, 13, NULL, 4, '2025-12', 1.0000, '2025-12-18 15:07:58'),
(126, 17, NULL, 4, '2025-12', 1000000.0000, '2025-12-18 15:07:58'),
(127, 3, NULL, 4, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(128, 29, NULL, 4, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(129, 10, NULL, 4, '2025-12', 730000.0000, '2025-12-18 15:07:58'),
(130, 2, NULL, 4, '2025-12', 26.0000, '2025-12-18 15:07:58'),
(131, 7, NULL, 4, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(132, 15, NULL, 4, '2025-12', 4960000.0000, '2025-12-18 15:07:58'),
(133, 4, NULL, 4, '2025-12', 1130000.0000, '2025-12-18 15:07:58'),
(134, 16, NULL, 4, '2025-12', 20.0000, '2025-12-18 15:07:58'),
(135, 5, NULL, 4, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(136, 27, NULL, 4, '2025-12', 59615.3846, '2025-12-18 15:07:58'),
(137, 6, NULL, 4, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(138, 8, NULL, 4, '2025-12', 12000000.0000, '2025-12-18 15:07:58'),
(139, 33, NULL, 4, '2025-12', 0.0800, '2025-12-18 15:07:58'),
(140, 38, NULL, 4, '2025-12', 0.0100, '2025-12-18 15:07:58'),
(141, 37, NULL, 4, '2025-12', 0.0300, '2025-12-18 15:07:58'),
(142, 1, NULL, 4, '2025-12', 12000000.0000, '2025-12-18 15:07:58'),
(143, 11, NULL, 4, '2025-12', 15400000.0000, '2025-12-18 15:07:58'),
(144, NULL, 1, 4, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(145, NULL, 27, 4, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(146, NULL, 3, 4, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(147, NULL, 20, 4, '2025-12', 960000.0000, '2025-12-18 15:07:58'),
(148, NULL, 21, 4, '2025-12', 180000.0000, '2025-12-18 15:07:58'),
(149, NULL, 22, 4, '2025-12', 120000.0000, '2025-12-18 15:07:58'),
(150, NULL, 4, 4, '2025-12', -17390000.0000, '2025-12-18 15:07:58'),
(151, NULL, 5, 4, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(152, NULL, 19, 4, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(153, NULL, 24, 4, '2025-12', 2100000.0000, '2025-12-18 15:07:58'),
(154, NULL, 25, 4, '2025-12', 360000.0000, '2025-12-18 15:07:58'),
(155, NULL, 26, 4, '2025-12', 120000.0000, '2025-12-18 15:07:58'),
(156, NULL, 6, 4, '2025-12', -1260000.0000, '2025-12-18 15:07:58'),
(157, 39, NULL, 5, '2025-12', 0.3000, '2025-12-18 15:07:58'),
(158, 26, NULL, 5, '2025-12', 4400000.0000, '2025-12-18 15:07:58'),
(159, 35, NULL, 5, '2025-12', 0.0100, '2025-12-18 15:07:58'),
(160, 25, NULL, 5, '2025-12', 11000000.0000, '2025-12-18 15:07:58'),
(161, 36, NULL, 5, '2025-12', 0.1750, '2025-12-18 15:07:58'),
(162, 34, NULL, 5, '2025-12', 0.0150, '2025-12-18 15:07:58'),
(163, 14, NULL, 5, '2025-12', 2340000.0000, '2025-12-18 15:07:58'),
(164, 13, NULL, 5, '2025-12', 2.0000, '2025-12-18 15:07:58'),
(165, 17, NULL, 5, '2025-12', 1000000.0000, '2025-12-18 15:07:58'),
(166, 3, NULL, 5, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(167, 29, NULL, 5, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(168, 10, NULL, 5, '2025-12', 3730000.0000, '2025-12-18 15:07:58'),
(169, 2, NULL, 5, '2025-12', 26.0000, '2025-12-18 15:07:58'),
(170, 7, NULL, 5, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(171, 15, NULL, 5, '2025-12', 4960000.0000, '2025-12-18 15:07:58'),
(172, 4, NULL, 5, '2025-12', 3730000.0000, '2025-12-18 15:07:58'),
(173, 16, NULL, 5, '2025-12', 20.0000, '2025-12-18 15:07:58'),
(174, 5, NULL, 5, '2025-12', 3000000.0000, '2025-12-18 15:07:58'),
(175, 27, NULL, 5, '2025-12', 120192.3077, '2025-12-18 15:07:58'),
(176, 6, NULL, 5, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(177, 8, NULL, 5, '2025-12', 22000000.0000, '2025-12-18 15:07:58'),
(178, 33, NULL, 5, '2025-12', 0.0800, '2025-12-18 15:07:58'),
(179, 38, NULL, 5, '2025-12', 0.0100, '2025-12-18 15:07:58'),
(180, 37, NULL, 5, '2025-12', 0.0300, '2025-12-18 15:07:58'),
(181, 1, NULL, 5, '2025-12', 22000000.0000, '2025-12-18 15:07:58'),
(182, 11, NULL, 5, '2025-12', 19800000.0000, '2025-12-18 15:07:58'),
(183, NULL, 1, 5, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(184, NULL, 27, 5, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(185, NULL, 3, 5, '2025-12', 3000000.0000, '2025-12-18 15:07:58'),
(186, NULL, 20, 5, '2025-12', 1760000.0000, '2025-12-18 15:07:58'),
(187, NULL, 21, 5, '2025-12', 330000.0000, '2025-12-18 15:07:58'),
(188, NULL, 22, 5, '2025-12', 220000.0000, '2025-12-18 15:07:58'),
(189, NULL, 4, 5, '2025-12', -22840000.0000, '2025-12-18 15:07:58'),
(190, NULL, 5, 5, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(191, NULL, 19, 5, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(192, NULL, 24, 5, '2025-12', 3850000.0000, '2025-12-18 15:07:58'),
(193, NULL, 25, 5, '2025-12', 660000.0000, '2025-12-18 15:07:58'),
(194, NULL, 26, 5, '2025-12', 220000.0000, '2025-12-18 15:07:58'),
(195, NULL, 6, 5, '2025-12', 690000.0000, '2025-12-18 15:07:58'),
(196, 39, NULL, 6, '2025-12', 0.3000, '2025-12-18 15:07:58'),
(197, 26, NULL, 6, '2025-12', 4400000.0000, '2025-12-18 15:07:58'),
(198, 35, NULL, 6, '2025-12', 0.0100, '2025-12-18 15:07:58'),
(199, 25, NULL, 6, '2025-12', 11000000.0000, '2025-12-18 15:07:58'),
(200, 36, NULL, 6, '2025-12', 0.1750, '2025-12-18 15:07:58'),
(201, 34, NULL, 6, '2025-12', 0.0150, '2025-12-18 15:07:58'),
(202, 14, NULL, 6, '2025-12', 2340000.0000, '2025-12-18 15:07:58'),
(203, 13, NULL, 6, '2025-12', 1.0000, '2025-12-18 15:07:58'),
(204, 17, NULL, 6, '2025-12', 1000000.0000, '2025-12-18 15:07:58'),
(205, 3, NULL, 6, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(206, 29, NULL, 6, '2025-12', 0.0000, '2025-12-18 15:07:58'),
(207, 10, NULL, 6, '2025-12', 1230000.0000, '2025-12-18 15:07:59'),
(208, 2, NULL, 6, '2025-12', 26.0000, '2025-12-18 15:07:59'),
(209, 7, NULL, 6, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(210, 15, NULL, 6, '2025-12', 4960000.0000, '2025-12-18 15:07:59'),
(211, 4, NULL, 6, '2025-12', 1230000.0000, '2025-12-18 15:07:59'),
(212, 16, NULL, 6, '2025-12', 20.0000, '2025-12-18 15:07:59'),
(213, 5, NULL, 6, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(214, 27, NULL, 6, '2025-12', 50480.7692, '2025-12-18 15:07:59'),
(215, 6, NULL, 6, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(216, 8, NULL, 6, '2025-12', 10000000.0000, '2025-12-18 15:07:59'),
(217, 33, NULL, 6, '2025-12', 0.0800, '2025-12-18 15:07:59'),
(218, 38, NULL, 6, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(219, 37, NULL, 6, '2025-12', 0.0300, '2025-12-18 15:07:59'),
(220, 1, NULL, 6, '2025-12', 10000000.0000, '2025-12-18 15:07:59'),
(221, 11, NULL, 6, '2025-12', 15400000.0000, '2025-12-18 15:07:59'),
(222, NULL, 1, 6, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(223, NULL, 27, 6, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(224, NULL, 3, 6, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(225, NULL, 20, 6, '2025-12', 800000.0000, '2025-12-18 15:07:59'),
(226, NULL, 21, 6, '2025-12', 150000.0000, '2025-12-18 15:07:59'),
(227, NULL, 22, 6, '2025-12', 100000.0000, '2025-12-18 15:07:59'),
(228, NULL, 4, 6, '2025-12', -17680000.0000, '2025-12-18 15:07:59'),
(229, NULL, 5, 6, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(230, NULL, 19, 6, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(231, NULL, 24, 6, '2025-12', 1750000.0000, '2025-12-18 15:07:59'),
(232, NULL, 25, 6, '2025-12', 300000.0000, '2025-12-18 15:07:59'),
(233, NULL, 26, 6, '2025-12', 100000.0000, '2025-12-18 15:07:59'),
(234, NULL, 6, 6, '2025-12', -1050000.0000, '2025-12-18 15:07:59'),
(235, 39, NULL, 7, '2025-12', 0.3000, '2025-12-18 15:07:59'),
(236, 26, NULL, 7, '2025-12', 4400000.0000, '2025-12-18 15:07:59'),
(237, 35, NULL, 7, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(238, 25, NULL, 7, '2025-12', 11000000.0000, '2025-12-18 15:07:59'),
(239, 36, NULL, 7, '2025-12', 0.1750, '2025-12-18 15:07:59'),
(240, 34, NULL, 7, '2025-12', 0.0150, '2025-12-18 15:07:59'),
(241, 14, NULL, 7, '2025-12', 2340000.0000, '2025-12-18 15:07:59'),
(242, 13, NULL, 7, '2025-12', 1.0000, '2025-12-18 15:07:59'),
(243, 17, NULL, 7, '2025-12', 1000000.0000, '2025-12-18 15:07:59'),
(244, 3, NULL, 7, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(245, 29, NULL, 7, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(246, 10, NULL, 7, '2025-12', 730000.0000, '2025-12-18 15:07:59'),
(247, 2, NULL, 7, '2025-12', 26.0000, '2025-12-18 15:07:59'),
(248, 7, NULL, 7, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(249, 15, NULL, 7, '2025-12', 4960000.0000, '2025-12-18 15:07:59'),
(250, 4, NULL, 7, '2025-12', 3730000.0000, '2025-12-18 15:07:59'),
(251, 16, NULL, 7, '2025-12', 20.0000, '2025-12-18 15:07:59'),
(252, 5, NULL, 7, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(253, 27, NULL, 7, '2025-12', 110576.9231, '2025-12-18 15:07:59'),
(254, 6, NULL, 7, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(255, 8, NULL, 7, '2025-12', 20000000.0000, '2025-12-18 15:07:59'),
(256, 33, NULL, 7, '2025-12', 0.0800, '2025-12-18 15:07:59'),
(257, 38, NULL, 7, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(258, 37, NULL, 7, '2025-12', 0.0300, '2025-12-18 15:07:59'),
(259, 1, NULL, 7, '2025-12', 20000000.0000, '2025-12-18 15:07:59'),
(260, 11, NULL, 7, '2025-12', 15400000.0000, '2025-12-18 15:07:59'),
(261, NULL, 1, 7, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(262, NULL, 27, 7, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(263, NULL, 3, 7, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(264, NULL, 20, 7, '2025-12', 1600000.0000, '2025-12-18 15:07:59'),
(265, NULL, 21, 7, '2025-12', 300000.0000, '2025-12-18 15:07:59'),
(266, NULL, 22, 7, '2025-12', 200000.0000, '2025-12-18 15:07:59'),
(267, NULL, 4, 7, '2025-12', -18230000.0000, '2025-12-18 15:07:59'),
(268, NULL, 5, 7, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(269, NULL, 19, 7, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(270, NULL, 24, 7, '2025-12', 3500000.0000, '2025-12-18 15:07:59'),
(271, NULL, 25, 7, '2025-12', 600000.0000, '2025-12-18 15:07:59'),
(272, NULL, 26, 7, '2025-12', 200000.0000, '2025-12-18 15:07:59'),
(273, NULL, 6, 7, '2025-12', -2100000.0000, '2025-12-18 15:07:59'),
(274, 39, NULL, 8, '2025-12', 0.3000, '2025-12-18 15:07:59'),
(275, 26, NULL, 8, '2025-12', 4400000.0000, '2025-12-18 15:07:59'),
(276, 35, NULL, 8, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(277, 25, NULL, 8, '2025-12', 11000000.0000, '2025-12-18 15:07:59'),
(278, 36, NULL, 8, '2025-12', 0.1750, '2025-12-18 15:07:59'),
(279, 34, NULL, 8, '2025-12', 0.0150, '2025-12-18 15:07:59'),
(280, 14, NULL, 8, '2025-12', 2340000.0000, '2025-12-18 15:07:59'),
(281, 13, NULL, 8, '2025-12', 1.0000, '2025-12-18 15:07:59'),
(282, 17, NULL, 8, '2025-12', 1000000.0000, '2025-12-18 15:07:59'),
(283, 3, NULL, 8, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(284, 29, NULL, 8, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(285, 10, NULL, 8, '2025-12', 1230000.0000, '2025-12-18 15:07:59'),
(286, 2, NULL, 8, '2025-12', 26.0000, '2025-12-18 15:07:59'),
(287, 7, NULL, 8, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(288, 15, NULL, 8, '2025-12', 4960000.0000, '2025-12-18 15:07:59'),
(289, 4, NULL, 8, '2025-12', 1230000.0000, '2025-12-18 15:07:59'),
(290, 16, NULL, 8, '2025-12', 20.0000, '2025-12-18 15:07:59'),
(291, 5, NULL, 8, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(292, 27, NULL, 8, '2025-12', 45673.0769, '2025-12-18 15:07:59'),
(293, 6, NULL, 8, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(294, 8, NULL, 8, '2025-12', 9000000.0000, '2025-12-18 15:07:59'),
(295, 33, NULL, 8, '2025-12', 0.0800, '2025-12-18 15:07:59'),
(296, 38, NULL, 8, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(297, 37, NULL, 8, '2025-12', 0.0300, '2025-12-18 15:07:59'),
(298, 1, NULL, 8, '2025-12', 9000000.0000, '2025-12-18 15:07:59'),
(299, 11, NULL, 8, '2025-12', 15400000.0000, '2025-12-18 15:07:59'),
(300, NULL, 1, 8, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(301, NULL, 27, 8, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(302, NULL, 3, 8, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(303, NULL, 20, 8, '2025-12', 720000.0000, '2025-12-18 15:07:59'),
(304, NULL, 21, 8, '2025-12', 135000.0000, '2025-12-18 15:07:59'),
(305, NULL, 22, 8, '2025-12', 90000.0000, '2025-12-18 15:07:59'),
(306, NULL, 4, 8, '2025-12', -17575000.0000, '2025-12-18 15:07:59'),
(307, NULL, 5, 8, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(308, NULL, 19, 8, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(309, NULL, 24, 8, '2025-12', 1575000.0000, '2025-12-18 15:07:59'),
(310, NULL, 25, 8, '2025-12', 270000.0000, '2025-12-18 15:07:59'),
(311, NULL, 26, 8, '2025-12', 90000.0000, '2025-12-18 15:07:59'),
(312, NULL, 6, 8, '2025-12', -945000.0000, '2025-12-18 15:07:59'),
(313, 39, NULL, 9, '2025-12', 0.3000, '2025-12-18 15:07:59'),
(314, 26, NULL, 9, '2025-12', 4400000.0000, '2025-12-18 15:07:59'),
(315, 35, NULL, 9, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(316, 25, NULL, 9, '2025-12', 11000000.0000, '2025-12-18 15:07:59'),
(317, 36, NULL, 9, '2025-12', 0.1750, '2025-12-18 15:07:59'),
(318, 34, NULL, 9, '2025-12', 0.0150, '2025-12-18 15:07:59'),
(319, 14, NULL, 9, '2025-12', 2340000.0000, '2025-12-18 15:07:59'),
(320, 13, NULL, 9, '2025-12', 2.0000, '2025-12-18 15:07:59'),
(321, 17, NULL, 9, '2025-12', 1000000.0000, '2025-12-18 15:07:59'),
(322, 3, NULL, 9, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(323, 29, NULL, 9, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(324, 10, NULL, 9, '2025-12', 1960000.0000, '2025-12-18 15:07:59'),
(325, 2, NULL, 9, '2025-12', 26.0000, '2025-12-18 15:07:59'),
(326, 7, NULL, 9, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(327, 15, NULL, 9, '2025-12', 4960000.0000, '2025-12-18 15:07:59'),
(328, 4, NULL, 9, '2025-12', 6960000.0000, '2025-12-18 15:07:59'),
(329, 16, NULL, 9, '2025-12', 20.0000, '2025-12-18 15:07:59'),
(330, 5, NULL, 9, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(331, 27, NULL, 9, '2025-12', 112980.7692, '2025-12-18 15:07:59'),
(332, 6, NULL, 9, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(333, 8, NULL, 9, '2025-12', 18000000.0000, '2025-12-18 15:07:59'),
(334, 33, NULL, 9, '2025-12', 0.0800, '2025-12-18 15:07:59'),
(335, 38, NULL, 9, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(336, 37, NULL, 9, '2025-12', 0.0300, '2025-12-18 15:07:59'),
(337, 1, NULL, 9, '2025-12', 18000000.0000, '2025-12-18 15:07:59'),
(338, 11, NULL, 9, '2025-12', 19800000.0000, '2025-12-18 15:07:59'),
(339, NULL, 1, 9, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(340, NULL, 27, 9, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(341, NULL, 3, 9, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(342, NULL, 20, 9, '2025-12', 1440000.0000, '2025-12-18 15:07:59'),
(343, NULL, 21, 9, '2025-12', 270000.0000, '2025-12-18 15:07:59'),
(344, NULL, 22, 9, '2025-12', 180000.0000, '2025-12-18 15:07:59'),
(345, NULL, 4, 9, '2025-12', -23650000.0000, '2025-12-18 15:07:59'),
(346, NULL, 5, 9, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(347, NULL, 19, 9, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(348, NULL, 24, 9, '2025-12', 3150000.0000, '2025-12-18 15:07:59'),
(349, NULL, 25, 9, '2025-12', 540000.0000, '2025-12-18 15:07:59'),
(350, NULL, 26, 9, '2025-12', 180000.0000, '2025-12-18 15:07:59'),
(351, NULL, 6, 9, '2025-12', -1890000.0000, '2025-12-18 15:07:59'),
(352, 39, NULL, 10, '2025-12', 0.3000, '2025-12-18 15:07:59'),
(353, 26, NULL, 10, '2025-12', 4400000.0000, '2025-12-18 15:07:59'),
(354, 35, NULL, 10, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(355, 25, NULL, 10, '2025-12', 11000000.0000, '2025-12-18 15:07:59'),
(356, 36, NULL, 10, '2025-12', 0.1750, '2025-12-18 15:07:59'),
(357, 34, NULL, 10, '2025-12', 0.0150, '2025-12-18 15:07:59'),
(358, 14, NULL, 10, '2025-12', 2340000.0000, '2025-12-18 15:07:59'),
(359, 13, NULL, 10, '2025-12', 1.0000, '2025-12-18 15:07:59'),
(360, 17, NULL, 10, '2025-12', 1000000.0000, '2025-12-18 15:07:59'),
(361, 3, NULL, 10, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(362, 29, NULL, 10, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(363, 10, NULL, 10, '2025-12', 2460000.0000, '2025-12-18 15:07:59'),
(364, 2, NULL, 10, '2025-12', 26.0000, '2025-12-18 15:07:59'),
(365, 7, NULL, 10, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(366, 15, NULL, 10, '2025-12', 4960000.0000, '2025-12-18 15:07:59'),
(367, 4, NULL, 10, '2025-12', 1460000.0000, '2025-12-18 15:07:59'),
(368, 16, NULL, 10, '2025-12', 20.0000, '2025-12-18 15:07:59'),
(369, 5, NULL, 10, '2025-12', 1000000.0000, '2025-12-18 15:07:59'),
(370, 27, NULL, 10, '2025-12', 43269.2308, '2025-12-18 15:07:59'),
(371, 6, NULL, 10, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(372, 8, NULL, 10, '2025-12', 9000000.0000, '2025-12-18 15:07:59'),
(373, 33, NULL, 10, '2025-12', 0.0800, '2025-12-18 15:07:59'),
(374, 38, NULL, 10, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(375, 37, NULL, 10, '2025-12', 0.0300, '2025-12-18 15:07:59'),
(376, 1, NULL, 10, '2025-12', 9000000.0000, '2025-12-18 15:07:59'),
(377, 11, NULL, 10, '2025-12', 15400000.0000, '2025-12-18 15:07:59'),
(378, NULL, 1, 10, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(379, NULL, 27, 10, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(380, NULL, 3, 10, '2025-12', 1000000.0000, '2025-12-18 15:07:59'),
(381, NULL, 20, 10, '2025-12', 720000.0000, '2025-12-18 15:07:59'),
(382, NULL, 21, 10, '2025-12', 135000.0000, '2025-12-18 15:07:59'),
(383, NULL, 22, 10, '2025-12', 90000.0000, '2025-12-18 15:07:59'),
(384, NULL, 4, 10, '2025-12', -17805000.0000, '2025-12-18 15:07:59'),
(385, NULL, 5, 10, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(386, NULL, 19, 10, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(387, NULL, 24, 10, '2025-12', 1575000.0000, '2025-12-18 15:07:59'),
(388, NULL, 25, 10, '2025-12', 270000.0000, '2025-12-18 15:07:59'),
(389, NULL, 26, 10, '2025-12', 90000.0000, '2025-12-18 15:07:59'),
(390, NULL, 6, 10, '2025-12', 55000.0000, '2025-12-18 15:07:59'),
(391, 39, NULL, 11, '2025-12', 0.3000, '2025-12-18 15:07:59'),
(392, 26, NULL, 11, '2025-12', 4400000.0000, '2025-12-18 15:07:59'),
(393, 35, NULL, 11, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(394, 25, NULL, 11, '2025-12', 11000000.0000, '2025-12-18 15:07:59'),
(395, 36, NULL, 11, '2025-12', 0.1750, '2025-12-18 15:07:59'),
(396, 34, NULL, 11, '2025-12', 0.0150, '2025-12-18 15:07:59'),
(397, 14, NULL, 11, '2025-12', 2340000.0000, '2025-12-18 15:07:59'),
(398, 13, NULL, 11, '2025-12', 1.0000, '2025-12-18 15:07:59'),
(399, 17, NULL, 11, '2025-12', 1000000.0000, '2025-12-18 15:07:59'),
(400, 3, NULL, 11, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(401, 29, NULL, 11, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(402, 10, NULL, 11, '2025-12', 730000.0000, '2025-12-18 15:07:59'),
(403, 2, NULL, 11, '2025-12', 26.0000, '2025-12-18 15:07:59'),
(404, 7, NULL, 11, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(405, 15, NULL, 11, '2025-12', 4960000.0000, '2025-12-18 15:07:59'),
(406, 4, NULL, 11, '2025-12', 4130000.0000, '2025-12-18 15:07:59'),
(407, 16, NULL, 11, '2025-12', 20.0000, '2025-12-18 15:07:59'),
(408, 5, NULL, 11, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(409, 27, NULL, 11, '2025-12', 98076.9231, '2025-12-18 15:07:59'),
(410, 6, NULL, 11, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(411, 8, NULL, 11, '2025-12', 17000000.0000, '2025-12-18 15:07:59'),
(412, 33, NULL, 11, '2025-12', 0.0800, '2025-12-18 15:07:59'),
(413, 38, NULL, 11, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(414, 37, NULL, 11, '2025-12', 0.0300, '2025-12-18 15:07:59'),
(415, 1, NULL, 11, '2025-12', 17000000.0000, '2025-12-18 15:07:59'),
(416, 11, NULL, 11, '2025-12', 15400000.0000, '2025-12-18 15:07:59'),
(417, NULL, 1, 11, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(418, NULL, 27, 11, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(419, NULL, 3, 11, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(420, NULL, 20, 11, '2025-12', 1360000.0000, '2025-12-18 15:07:59'),
(421, NULL, 21, 11, '2025-12', 255000.0000, '2025-12-18 15:07:59'),
(422, NULL, 22, 11, '2025-12', 170000.0000, '2025-12-18 15:07:59'),
(423, NULL, 4, 11, '2025-12', -17915000.0000, '2025-12-18 15:07:59'),
(424, NULL, 5, 11, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(425, NULL, 19, 11, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(426, NULL, 24, 11, '2025-12', 2975000.0000, '2025-12-18 15:07:59'),
(427, NULL, 25, 11, '2025-12', 510000.0000, '2025-12-18 15:07:59'),
(428, NULL, 26, 11, '2025-12', 170000.0000, '2025-12-18 15:07:59'),
(429, NULL, 6, 11, '2025-12', -1785000.0000, '2025-12-18 15:07:59'),
(430, 39, NULL, 12, '2025-12', 0.3000, '2025-12-18 15:07:59'),
(431, 26, NULL, 12, '2025-12', 4400000.0000, '2025-12-18 15:07:59'),
(432, 35, NULL, 12, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(433, 25, NULL, 12, '2025-12', 11000000.0000, '2025-12-18 15:07:59'),
(434, 36, NULL, 12, '2025-12', 0.1750, '2025-12-18 15:07:59'),
(435, 34, NULL, 12, '2025-12', 0.0150, '2025-12-18 15:07:59'),
(436, 14, NULL, 12, '2025-12', 2340000.0000, '2025-12-18 15:07:59'),
(437, 13, NULL, 12, '2025-12', 1.0000, '2025-12-18 15:07:59'),
(438, 17, NULL, 12, '2025-12', 1000000.0000, '2025-12-18 15:07:59'),
(439, 3, NULL, 12, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(440, 29, NULL, 12, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(441, 10, NULL, 12, '2025-12', 1230000.0000, '2025-12-18 15:07:59'),
(442, 2, NULL, 12, '2025-12', 26.0000, '2025-12-18 15:07:59'),
(443, 7, NULL, 12, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(444, 15, NULL, 12, '2025-12', 4960000.0000, '2025-12-18 15:07:59'),
(445, 4, NULL, 12, '2025-12', 1130000.0000, '2025-12-18 15:07:59'),
(446, 16, NULL, 12, '2025-12', 20.0000, '2025-12-18 15:07:59'),
(447, 5, NULL, 12, '2025-12', 500000.0000, '2025-12-18 15:07:59'),
(448, 27, NULL, 12, '2025-12', 40384.6154, '2025-12-18 15:07:59'),
(449, 6, NULL, 12, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(450, 8, NULL, 12, '2025-12', 8000000.0000, '2025-12-18 15:07:59'),
(451, 33, NULL, 12, '2025-12', 0.0800, '2025-12-18 15:07:59'),
(452, 38, NULL, 12, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(453, 37, NULL, 12, '2025-12', 0.0300, '2025-12-18 15:07:59'),
(454, 1, NULL, 12, '2025-12', 8000000.0000, '2025-12-18 15:07:59'),
(455, 11, NULL, 12, '2025-12', 15400000.0000, '2025-12-18 15:07:59'),
(456, NULL, 1, 12, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(457, NULL, 27, 12, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(458, NULL, 3, 12, '2025-12', 500000.0000, '2025-12-18 15:07:59'),
(459, NULL, 20, 12, '2025-12', 640000.0000, '2025-12-18 15:07:59'),
(460, NULL, 21, 12, '2025-12', 120000.0000, '2025-12-18 15:07:59'),
(461, NULL, 22, 12, '2025-12', 80000.0000, '2025-12-18 15:07:59'),
(462, NULL, 4, 12, '2025-12', -16970000.0000, '2025-12-18 15:07:59'),
(463, NULL, 5, 12, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(464, NULL, 19, 12, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(465, NULL, 24, 12, '2025-12', 1400000.0000, '2025-12-18 15:07:59'),
(466, NULL, 25, 12, '2025-12', 240000.0000, '2025-12-18 15:07:59'),
(467, NULL, 26, 12, '2025-12', 80000.0000, '2025-12-18 15:07:59'),
(468, NULL, 6, 12, '2025-12', -340000.0000, '2025-12-18 15:07:59'),
(469, 39, NULL, 13, '2025-12', 0.3000, '2025-12-18 15:07:59'),
(470, 26, NULL, 13, '2025-12', 4400000.0000, '2025-12-18 15:07:59'),
(471, 35, NULL, 13, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(472, 25, NULL, 13, '2025-12', 11000000.0000, '2025-12-18 15:07:59'),
(473, 36, NULL, 13, '2025-12', 0.1750, '2025-12-18 15:07:59'),
(474, 34, NULL, 13, '2025-12', 0.0150, '2025-12-18 15:07:59'),
(475, 14, NULL, 13, '2025-12', 2340000.0000, '2025-12-18 15:07:59'),
(476, 13, NULL, 13, '2025-12', 1.0000, '2025-12-18 15:07:59'),
(477, 17, NULL, 13, '2025-12', 1000000.0000, '2025-12-18 15:07:59'),
(478, 3, NULL, 13, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(479, 29, NULL, 13, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(480, 10, NULL, 13, '2025-12', 730000.0000, '2025-12-18 15:07:59'),
(481, 2, NULL, 13, '2025-12', 26.0000, '2025-12-18 15:07:59'),
(482, 7, NULL, 13, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(483, 15, NULL, 13, '2025-12', 4960000.0000, '2025-12-18 15:07:59'),
(484, 4, NULL, 13, '2025-12', 1130000.0000, '2025-12-18 15:07:59'),
(485, 16, NULL, 13, '2025-12', 20.0000, '2025-12-18 15:07:59'),
(486, 5, NULL, 13, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(487, 27, NULL, 13, '2025-12', 69230.7692, '2025-12-18 15:07:59'),
(488, 6, NULL, 13, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(489, 8, NULL, 13, '2025-12', 14000000.0000, '2025-12-18 15:07:59'),
(490, 33, NULL, 13, '2025-12', 0.0800, '2025-12-18 15:07:59'),
(491, 38, NULL, 13, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(492, 37, NULL, 13, '2025-12', 0.0300, '2025-12-18 15:07:59'),
(493, 1, NULL, 13, '2025-12', 14000000.0000, '2025-12-18 15:07:59'),
(494, 11, NULL, 13, '2025-12', 15400000.0000, '2025-12-18 15:07:59'),
(495, NULL, 1, 13, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(496, NULL, 27, 13, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(497, NULL, 3, 13, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(498, NULL, 20, 13, '2025-12', 1120000.0000, '2025-12-18 15:07:59'),
(499, NULL, 21, 13, '2025-12', 210000.0000, '2025-12-18 15:07:59'),
(500, NULL, 22, 13, '2025-12', 140000.0000, '2025-12-18 15:07:59'),
(501, NULL, 4, 13, '2025-12', -17600000.0000, '2025-12-18 15:07:59'),
(502, NULL, 5, 13, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(503, NULL, 19, 13, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(504, NULL, 24, 13, '2025-12', 2450000.0000, '2025-12-18 15:07:59'),
(505, NULL, 25, 13, '2025-12', 420000.0000, '2025-12-18 15:07:59'),
(506, NULL, 26, 13, '2025-12', 140000.0000, '2025-12-18 15:07:59'),
(507, NULL, 6, 13, '2025-12', -1470000.0000, '2025-12-18 15:07:59'),
(508, 39, NULL, 14, '2025-12', 0.3000, '2025-12-18 15:07:59'),
(509, 26, NULL, 14, '2025-12', 4400000.0000, '2025-12-18 15:07:59'),
(510, 35, NULL, 14, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(511, 25, NULL, 14, '2025-12', 11000000.0000, '2025-12-18 15:07:59'),
(512, 36, NULL, 14, '2025-12', 0.1750, '2025-12-18 15:07:59'),
(513, 34, NULL, 14, '2025-12', 0.0150, '2025-12-18 15:07:59'),
(514, 14, NULL, 14, '2025-12', 2340000.0000, '2025-12-18 15:07:59'),
(515, 13, NULL, 14, '2025-12', 1.0000, '2025-12-18 15:07:59'),
(516, 17, NULL, 14, '2025-12', 1000000.0000, '2025-12-18 15:07:59'),
(517, 3, NULL, 14, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(518, 29, NULL, 14, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(519, 10, NULL, 14, '2025-12', 730000.0000, '2025-12-18 15:07:59'),
(520, 2, NULL, 14, '2025-12', 26.0000, '2025-12-18 15:07:59'),
(521, 7, NULL, 14, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(522, 15, NULL, 14, '2025-12', 4960000.0000, '2025-12-18 15:07:59'),
(523, 4, NULL, 14, '2025-12', 1130000.0000, '2025-12-18 15:07:59'),
(524, 16, NULL, 14, '2025-12', 20.0000, '2025-12-18 15:07:59'),
(525, 5, NULL, 14, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(526, 27, NULL, 14, '2025-12', 25961.5385, '2025-12-18 15:07:59'),
(527, 6, NULL, 14, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(528, 8, NULL, 14, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(529, 33, NULL, 14, '2025-12', 0.0800, '2025-12-18 15:07:59'),
(530, 38, NULL, 14, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(531, 37, NULL, 14, '2025-12', 0.0300, '2025-12-18 15:07:59'),
(532, 1, NULL, 14, '2025-12', 5000000.0000, '2025-12-18 15:07:59'),
(533, 11, NULL, 14, '2025-12', 15400000.0000, '2025-12-18 15:07:59'),
(534, NULL, 1, 14, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(535, NULL, 27, 14, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(536, NULL, 3, 14, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(537, NULL, 20, 14, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(538, NULL, 21, 14, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(539, NULL, 22, 14, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(540, NULL, 4, 14, '2025-12', -16130000.0000, '2025-12-18 15:07:59'),
(541, NULL, 5, 14, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(542, NULL, 19, 14, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(543, NULL, 24, 14, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(544, NULL, 25, 14, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(545, NULL, 26, 14, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(546, NULL, 6, 14, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(547, 39, NULL, 15, '2025-12', 0.3000, '2025-12-18 15:07:59'),
(548, 26, NULL, 15, '2025-12', 4400000.0000, '2025-12-18 15:07:59'),
(549, 35, NULL, 15, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(550, 25, NULL, 15, '2025-12', 11000000.0000, '2025-12-18 15:07:59'),
(551, 36, NULL, 15, '2025-12', 0.1750, '2025-12-18 15:07:59'),
(552, 34, NULL, 15, '2025-12', 0.0150, '2025-12-18 15:07:59'),
(553, 14, NULL, 15, '2025-12', 2340000.0000, '2025-12-18 15:07:59'),
(554, 13, NULL, 15, '2025-12', 1.0000, '2025-12-18 15:07:59'),
(555, 17, NULL, 15, '2025-12', 1000000.0000, '2025-12-18 15:07:59'),
(556, 3, NULL, 15, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(557, 29, NULL, 15, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(558, 10, NULL, 15, '2025-12', 730000.0000, '2025-12-18 15:07:59'),
(559, 2, NULL, 15, '2025-12', 26.0000, '2025-12-18 15:07:59'),
(560, 7, NULL, 15, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(561, 15, NULL, 15, '2025-12', 4960000.0000, '2025-12-18 15:07:59'),
(562, 4, NULL, 15, '2025-12', 730000.0000, '2025-12-18 15:07:59'),
(563, 16, NULL, 15, '2025-12', 20.0000, '2025-12-18 15:07:59'),
(564, 5, NULL, 15, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(565, 27, NULL, 15, '2025-12', 45673.0769, '2025-12-18 15:07:59'),
(566, 6, NULL, 15, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(567, 8, NULL, 15, '2025-12', 9500000.0000, '2025-12-18 15:07:59'),
(568, 33, NULL, 15, '2025-12', 0.0800, '2025-12-18 15:07:59'),
(569, 38, NULL, 15, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(570, 37, NULL, 15, '2025-12', 0.0300, '2025-12-18 15:07:59'),
(571, 1, NULL, 15, '2025-12', 9500000.0000, '2025-12-18 15:07:59'),
(572, 11, NULL, 15, '2025-12', 15400000.0000, '2025-12-18 15:07:59'),
(573, NULL, 1, 15, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(574, NULL, 27, 15, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(575, NULL, 3, 15, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(576, NULL, 20, 15, '2025-12', 760000.0000, '2025-12-18 15:07:59'),
(577, NULL, 21, 15, '2025-12', 142500.0000, '2025-12-18 15:07:59'),
(578, NULL, 22, 15, '2025-12', 95000.0000, '2025-12-18 15:07:59'),
(579, NULL, 4, 15, '2025-12', -17127500.0000, '2025-12-18 15:07:59'),
(580, NULL, 5, 15, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(581, NULL, 19, 15, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(582, NULL, 24, 15, '2025-12', 1662500.0000, '2025-12-18 15:07:59'),
(583, NULL, 25, 15, '2025-12', 285000.0000, '2025-12-18 15:07:59'),
(584, NULL, 26, 15, '2025-12', 95000.0000, '2025-12-18 15:07:59'),
(585, NULL, 6, 15, '2025-12', -997500.0000, '2025-12-18 15:07:59'),
(586, 39, NULL, 16, '2025-12', 0.3000, '2025-12-18 15:07:59'),
(587, 26, NULL, 16, '2025-12', 4400000.0000, '2025-12-18 15:07:59'),
(588, 35, NULL, 16, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(589, 25, NULL, 16, '2025-12', 11000000.0000, '2025-12-18 15:07:59'),
(590, 36, NULL, 16, '2025-12', 0.1750, '2025-12-18 15:07:59'),
(591, 34, NULL, 16, '2025-12', 0.0150, '2025-12-18 15:07:59'),
(592, 14, NULL, 16, '2025-12', 2340000.0000, '2025-12-18 15:07:59'),
(593, 13, NULL, 16, '2025-12', 1.0000, '2025-12-18 15:07:59'),
(594, 17, NULL, 16, '2025-12', 1000000.0000, '2025-12-18 15:07:59'),
(595, 3, NULL, 16, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(596, 29, NULL, 16, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(597, 10, NULL, 16, '2025-12', 730000.0000, '2025-12-18 15:07:59'),
(598, 2, NULL, 16, '2025-12', 26.0000, '2025-12-18 15:07:59'),
(599, 7, NULL, 16, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(600, 15, NULL, 16, '2025-12', 4960000.0000, '2025-12-18 15:07:59'),
(601, 4, NULL, 16, '2025-12', 730000.0000, '2025-12-18 15:07:59'),
(602, 16, NULL, 16, '2025-12', 20.0000, '2025-12-18 15:07:59'),
(603, 5, NULL, 16, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(604, 27, NULL, 16, '2025-12', 44230.7692, '2025-12-18 15:07:59'),
(605, 6, NULL, 16, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(606, 8, NULL, 16, '2025-12', 9200000.0000, '2025-12-18 15:07:59'),
(607, 33, NULL, 16, '2025-12', 0.0800, '2025-12-18 15:07:59'),
(608, 38, NULL, 16, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(609, 37, NULL, 16, '2025-12', 0.0300, '2025-12-18 15:07:59'),
(610, 1, NULL, 16, '2025-12', 9200000.0000, '2025-12-18 15:07:59'),
(611, 11, NULL, 16, '2025-12', 15400000.0000, '2025-12-18 15:07:59'),
(612, NULL, 1, 16, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(613, NULL, 27, 16, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(614, NULL, 3, 16, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(615, NULL, 20, 16, '2025-12', 736000.0000, '2025-12-18 15:07:59'),
(616, NULL, 21, 16, '2025-12', 138000.0000, '2025-12-18 15:07:59'),
(617, NULL, 22, 16, '2025-12', 92000.0000, '2025-12-18 15:07:59'),
(618, NULL, 4, 16, '2025-12', -17096000.0000, '2025-12-18 15:07:59'),
(619, NULL, 5, 16, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(620, NULL, 19, 16, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(621, NULL, 24, 16, '2025-12', 1610000.0000, '2025-12-18 15:07:59'),
(622, NULL, 25, 16, '2025-12', 276000.0000, '2025-12-18 15:07:59'),
(623, NULL, 26, 16, '2025-12', 92000.0000, '2025-12-18 15:07:59'),
(624, NULL, 6, 16, '2025-12', -966000.0000, '2025-12-18 15:07:59'),
(625, 39, NULL, 17, '2025-12', 0.3000, '2025-12-18 15:07:59'),
(626, 26, NULL, 17, '2025-12', 4400000.0000, '2025-12-18 15:07:59'),
(627, 35, NULL, 17, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(628, 25, NULL, 17, '2025-12', 11000000.0000, '2025-12-18 15:07:59'),
(629, 36, NULL, 17, '2025-12', 0.1750, '2025-12-18 15:07:59'),
(630, 34, NULL, 17, '2025-12', 0.0150, '2025-12-18 15:07:59'),
(631, 14, NULL, 17, '2025-12', 2340000.0000, '2025-12-18 15:07:59'),
(632, 13, NULL, 17, '2025-12', 1.0000, '2025-12-18 15:07:59'),
(633, 17, NULL, 17, '2025-12', 1000000.0000, '2025-12-18 15:07:59'),
(634, 3, NULL, 17, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(635, 29, NULL, 17, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(636, 10, NULL, 17, '2025-12', 1230000.0000, '2025-12-18 15:07:59'),
(637, 2, NULL, 17, '2025-12', 26.0000, '2025-12-18 15:07:59'),
(638, 7, NULL, 17, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(639, 15, NULL, 17, '2025-12', 4960000.0000, '2025-12-18 15:07:59'),
(640, 4, NULL, 17, '2025-12', 1230000.0000, '2025-12-18 15:07:59'),
(641, 16, NULL, 17, '2025-12', 20.0000, '2025-12-18 15:07:59'),
(642, 5, NULL, 17, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(643, 27, NULL, 17, '2025-12', 44711.5385, '2025-12-18 15:07:59'),
(644, 6, NULL, 17, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(645, 8, NULL, 17, '2025-12', 8800000.0000, '2025-12-18 15:07:59'),
(646, 33, NULL, 17, '2025-12', 0.0800, '2025-12-18 15:07:59'),
(647, 38, NULL, 17, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(648, 37, NULL, 17, '2025-12', 0.0300, '2025-12-18 15:07:59'),
(649, 1, NULL, 17, '2025-12', 8800000.0000, '2025-12-18 15:07:59'),
(650, 11, NULL, 17, '2025-12', 15400000.0000, '2025-12-18 15:07:59'),
(651, NULL, 1, 17, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(652, NULL, 27, 17, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(653, NULL, 3, 17, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(654, NULL, 20, 17, '2025-12', 704000.0000, '2025-12-18 15:07:59'),
(655, NULL, 21, 17, '2025-12', 132000.0000, '2025-12-18 15:07:59'),
(656, NULL, 22, 17, '2025-12', 88000.0000, '2025-12-18 15:07:59'),
(657, NULL, 4, 17, '2025-12', -17554000.0000, '2025-12-18 15:07:59'),
(658, NULL, 5, 17, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(659, NULL, 19, 17, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(660, NULL, 24, 17, '2025-12', 1540000.0000, '2025-12-18 15:07:59'),
(661, NULL, 25, 17, '2025-12', 264000.0000, '2025-12-18 15:07:59'),
(662, NULL, 26, 17, '2025-12', 88000.0000, '2025-12-18 15:07:59'),
(663, NULL, 6, 17, '2025-12', -924000.0000, '2025-12-18 15:07:59'),
(664, 39, NULL, 18, '2025-12', 0.3000, '2025-12-18 15:07:59'),
(665, 26, NULL, 18, '2025-12', 4400000.0000, '2025-12-18 15:07:59'),
(666, 35, NULL, 18, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(667, 25, NULL, 18, '2025-12', 11000000.0000, '2025-12-18 15:07:59'),
(668, 36, NULL, 18, '2025-12', 0.1750, '2025-12-18 15:07:59'),
(669, 34, NULL, 18, '2025-12', 0.0150, '2025-12-18 15:07:59'),
(670, 14, NULL, 18, '2025-12', 2340000.0000, '2025-12-18 15:07:59'),
(671, 13, NULL, 18, '2025-12', 1.0000, '2025-12-18 15:07:59'),
(672, 17, NULL, 18, '2025-12', 1000000.0000, '2025-12-18 15:07:59'),
(673, 3, NULL, 18, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(674, 29, NULL, 18, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(675, 10, NULL, 18, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(676, 2, NULL, 18, '2025-12', 26.0000, '2025-12-18 15:07:59'),
(677, 7, NULL, 18, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(678, 15, NULL, 18, '2025-12', 4960000.0000, '2025-12-18 15:07:59'),
(679, 4, NULL, 18, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(680, 16, NULL, 18, '2025-12', 20.0000, '2025-12-18 15:07:59'),
(681, 5, NULL, 18, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(682, 27, NULL, 18, '2025-12', 72115.3846, '2025-12-18 15:07:59'),
(683, 6, NULL, 18, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(684, 8, NULL, 18, '2025-12', 15000000.0000, '2025-12-18 15:07:59'),
(685, 33, NULL, 18, '2025-12', 0.0800, '2025-12-18 15:07:59'),
(686, 38, NULL, 18, '2025-12', 0.0100, '2025-12-18 15:07:59'),
(687, 37, NULL, 18, '2025-12', 0.0300, '2025-12-18 15:07:59'),
(688, 1, NULL, 18, '2025-12', 15000000.0000, '2025-12-18 15:07:59'),
(689, 11, NULL, 18, '2025-12', 15400000.0000, '2025-12-18 15:07:59'),
(690, NULL, 1, 18, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(691, NULL, 27, 18, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(692, NULL, 3, 18, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(693, NULL, 20, 18, '2025-12', 1200000.0000, '2025-12-18 15:07:59'),
(694, NULL, 21, 18, '2025-12', 225000.0000, '2025-12-18 15:07:59'),
(695, NULL, 22, 18, '2025-12', 150000.0000, '2025-12-18 15:07:59'),
(696, NULL, 4, 18, '2025-12', -16975000.0000, '2025-12-18 15:07:59'),
(697, NULL, 5, 18, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(698, NULL, 19, 18, '2025-12', 0.0000, '2025-12-18 15:07:59'),
(699, NULL, 24, 18, '2025-12', 2625000.0000, '2025-12-18 15:07:59'),
(700, NULL, 25, 18, '2025-12', 450000.0000, '2025-12-18 15:07:59'),
(701, NULL, 26, 18, '2025-12', 150000.0000, '2025-12-18 15:07:59'),
(702, NULL, 6, 18, '2025-12', -1575000.0000, '2025-12-18 15:07:59'),
(703, 39, NULL, 19, '2025-12', 0.3000, '2025-12-18 15:08:00'),
(704, 26, NULL, 19, '2025-12', 4400000.0000, '2025-12-18 15:08:00'),
(705, 35, NULL, 19, '2025-12', 0.0100, '2025-12-18 15:08:00'),
(706, 25, NULL, 19, '2025-12', 11000000.0000, '2025-12-18 15:08:00'),
(707, 36, NULL, 19, '2025-12', 0.1750, '2025-12-18 15:08:00'),
(708, 34, NULL, 19, '2025-12', 0.0150, '2025-12-18 15:08:00'),
(709, 14, NULL, 19, '2025-12', 2340000.0000, '2025-12-18 15:08:00'),
(710, 13, NULL, 19, '2025-12', 1.0000, '2025-12-18 15:08:00'),
(711, 17, NULL, 19, '2025-12', 1000000.0000, '2025-12-18 15:08:00'),
(712, 3, NULL, 19, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(713, 29, NULL, 19, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(714, 10, NULL, 19, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(715, 2, NULL, 19, '2025-12', 26.0000, '2025-12-18 15:08:00'),
(716, 7, NULL, 19, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(717, 15, NULL, 19, '2025-12', 4960000.0000, '2025-12-18 15:08:00'),
(718, 4, NULL, 19, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(719, 16, NULL, 19, '2025-12', 20.0000, '2025-12-18 15:08:00'),
(720, 5, NULL, 19, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(721, 27, NULL, 19, '2025-12', 72115.3846, '2025-12-18 15:08:00'),
(722, 6, NULL, 19, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(723, 8, NULL, 19, '2025-12', 15000000.0000, '2025-12-18 15:08:00'),
(724, 33, NULL, 19, '2025-12', 0.0800, '2025-12-18 15:08:00'),
(725, 38, NULL, 19, '2025-12', 0.0100, '2025-12-18 15:08:00'),
(726, 37, NULL, 19, '2025-12', 0.0300, '2025-12-18 15:08:00'),
(727, 1, NULL, 19, '2025-12', 15000000.0000, '2025-12-18 15:08:00'),
(728, 11, NULL, 19, '2025-12', 15400000.0000, '2025-12-18 15:08:00'),
(729, NULL, 1, 19, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(730, NULL, 27, 19, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(731, NULL, 3, 19, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(732, NULL, 20, 19, '2025-12', 1200000.0000, '2025-12-18 15:08:00'),
(733, NULL, 21, 19, '2025-12', 225000.0000, '2025-12-18 15:08:00'),
(734, NULL, 22, 19, '2025-12', 150000.0000, '2025-12-18 15:08:00'),
(735, NULL, 4, 19, '2025-12', -16975000.0000, '2025-12-18 15:08:00'),
(736, NULL, 5, 19, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(737, NULL, 19, 19, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(738, NULL, 24, 19, '2025-12', 2625000.0000, '2025-12-18 15:08:00'),
(739, NULL, 25, 19, '2025-12', 450000.0000, '2025-12-18 15:08:00'),
(740, NULL, 26, 19, '2025-12', 150000.0000, '2025-12-18 15:08:00'),
(741, NULL, 6, 19, '2025-12', -1575000.0000, '2025-12-18 15:08:00'),
(742, 39, NULL, 20, '2025-12', 0.3000, '2025-12-18 15:08:00'),
(743, 26, NULL, 20, '2025-12', 4400000.0000, '2025-12-18 15:08:00'),
(744, 35, NULL, 20, '2025-12', 0.0100, '2025-12-18 15:08:00'),
(745, 25, NULL, 20, '2025-12', 11000000.0000, '2025-12-18 15:08:00'),
(746, 36, NULL, 20, '2025-12', 0.1750, '2025-12-18 15:08:00'),
(747, 34, NULL, 20, '2025-12', 0.0150, '2025-12-18 15:08:00'),
(748, 14, NULL, 20, '2025-12', 2340000.0000, '2025-12-18 15:08:00'),
(749, 13, NULL, 20, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(750, 17, NULL, 20, '2025-12', 1000000.0000, '2025-12-18 15:08:00'),
(751, 3, NULL, 20, '2025-12', 3.0000, '2025-12-18 15:08:00'),
(752, 29, NULL, 20, '2025-12', 8.0000, '2025-12-18 15:08:00'),
(753, 10, NULL, 20, '2025-12', 982403.8462, '2025-12-18 15:08:00'),
(754, 2, NULL, 20, '2025-12', 26.0000, '2025-12-18 15:08:00'),
(755, 7, NULL, 20, '2025-12', 15.0000, '2025-12-18 15:08:00'),
(756, 15, NULL, 20, '2025-12', 4960000.0000, '2025-12-18 15:08:00'),
(757, 4, NULL, 20, '2025-12', 730000.0000, '2025-12-18 15:08:00'),
(758, 16, NULL, 20, '2025-12', 20.0000, '2025-12-18 15:08:00'),
(759, 5, NULL, 20, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(760, 27, NULL, 20, '2025-12', 36057.6923, '2025-12-18 15:08:00'),
(761, 6, NULL, 20, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(762, 8, NULL, 20, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(763, 33, NULL, 20, '2025-12', 0.0800, '2025-12-18 15:08:00'),
(764, 38, NULL, 20, '2025-12', 0.0100, '2025-12-18 15:08:00'),
(765, 37, NULL, 20, '2025-12', 0.0300, '2025-12-18 15:08:00'),
(766, 1, NULL, 20, '2025-12', 7500000.0000, '2025-12-18 15:08:00'),
(767, 11, NULL, 20, '2025-12', 11000000.0000, '2025-12-18 15:08:00'),
(768, NULL, 1, 20, '2025-12', 949615.3800, '2025-12-18 15:08:00'),
(769, NULL, 27, 20, '2025-12', 86538.4615, '2025-12-18 15:08:00'),
(770, NULL, 3, 20, '2025-12', 1577019.2260, '2025-12-18 15:08:00'),
(771, NULL, 20, 20, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(772, NULL, 21, 20, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(773, NULL, 22, 20, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(774, NULL, 4, 20, '2025-12', -10405384.6202, '2025-12-18 15:08:00'),
(775, NULL, 5, 20, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(776, NULL, 19, 20, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(777, NULL, 24, 20, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(778, NULL, 25, 20, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(779, NULL, 26, 20, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(780, NULL, 6, 20, '2025-12', 1577019.2260, '2025-12-18 15:08:00'),
(781, 39, NULL, 21, '2025-12', 0.3000, '2025-12-18 15:08:00'),
(782, 26, NULL, 21, '2025-12', 4400000.0000, '2025-12-18 15:08:00'),
(783, 35, NULL, 21, '2025-12', 0.0100, '2025-12-18 15:08:00'),
(784, 25, NULL, 21, '2025-12', 11000000.0000, '2025-12-18 15:08:00'),
(785, 36, NULL, 21, '2025-12', 0.1750, '2025-12-18 15:08:00'),
(786, 34, NULL, 21, '2025-12', 0.0150, '2025-12-18 15:08:00'),
(787, 14, NULL, 21, '2025-12', 2340000.0000, '2025-12-18 15:08:00'),
(788, 13, NULL, 21, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(789, 17, NULL, 21, '2025-12', 1000000.0000, '2025-12-18 15:08:00'),
(790, 3, NULL, 21, '2025-12', 5.0000, '2025-12-18 15:08:00'),
(791, 29, NULL, 21, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(792, 10, NULL, 21, '2025-12', 0.0000, '2025-12-18 15:08:00');
INSERT INTO `salary_variable_cache` (`cache_id`, `variable_id`, `rule_id`, `employee_id`, `payperiod`, `value`, `evaluated_at`) VALUES
(793, 2, NULL, 21, '2025-12', 26.0000, '2025-12-18 15:08:00'),
(794, 7, NULL, 21, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(795, 15, NULL, 21, '2025-12', 4960000.0000, '2025-12-18 15:08:00'),
(796, 4, NULL, 21, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(797, 16, NULL, 21, '2025-12', 20.0000, '2025-12-18 15:08:00'),
(798, 5, NULL, 21, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(799, 27, NULL, 21, '2025-12', 72115.3846, '2025-12-18 15:08:00'),
(800, 6, NULL, 21, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(801, 8, NULL, 21, '2025-12', 15000000.0000, '2025-12-18 15:08:00'),
(802, 33, NULL, 21, '2025-12', 0.0800, '2025-12-18 15:08:00'),
(803, 38, NULL, 21, '2025-12', 0.0100, '2025-12-18 15:08:00'),
(804, 37, NULL, 21, '2025-12', 0.0300, '2025-12-18 15:08:00'),
(805, 1, NULL, 21, '2025-12', 15000000.0000, '2025-12-18 15:08:00'),
(806, 11, NULL, 21, '2025-12', 11000000.0000, '2025-12-18 15:08:00'),
(807, NULL, 1, 21, '2025-12', 2884615.4000, '2025-12-18 15:08:00'),
(808, NULL, 27, 21, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(809, NULL, 3, 21, '2025-12', 2884615.4000, '2025-12-18 15:08:00'),
(810, NULL, 20, 21, '2025-12', 1200000.0000, '2025-12-18 15:08:00'),
(811, NULL, 21, 21, '2025-12', 225000.0000, '2025-12-18 15:08:00'),
(812, NULL, 22, 21, '2025-12', 150000.0000, '2025-12-18 15:08:00'),
(813, NULL, 4, 21, '2025-12', -9690384.6000, '2025-12-18 15:08:00'),
(814, NULL, 5, 21, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(815, NULL, 19, 21, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(816, NULL, 24, 21, '2025-12', 2625000.0000, '2025-12-18 15:08:00'),
(817, NULL, 25, 21, '2025-12', 450000.0000, '2025-12-18 15:08:00'),
(818, NULL, 26, 21, '2025-12', 150000.0000, '2025-12-18 15:08:00'),
(819, NULL, 6, 21, '2025-12', 1309615.4000, '2025-12-18 15:08:00'),
(820, 39, NULL, 22, '2025-12', 0.3000, '2025-12-18 15:08:00'),
(821, 26, NULL, 22, '2025-12', 4400000.0000, '2025-12-18 15:08:00'),
(822, 35, NULL, 22, '2025-12', 0.0100, '2025-12-18 15:08:00'),
(823, 25, NULL, 22, '2025-12', 11000000.0000, '2025-12-18 15:08:00'),
(824, 36, NULL, 22, '2025-12', 0.1750, '2025-12-18 15:08:00'),
(825, 34, NULL, 22, '2025-12', 0.0150, '2025-12-18 15:08:00'),
(826, 14, NULL, 22, '2025-12', 2340000.0000, '2025-12-18 15:08:00'),
(827, 13, NULL, 22, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(828, 17, NULL, 22, '2025-12', 1000000.0000, '2025-12-18 15:08:00'),
(829, 3, NULL, 22, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(830, 29, NULL, 22, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(831, 10, NULL, 22, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(832, 2, NULL, 22, '2025-12', 26.0000, '2025-12-18 15:08:00'),
(833, 7, NULL, 22, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(834, 15, NULL, 22, '2025-12', 4960000.0000, '2025-12-18 15:08:00'),
(835, 4, NULL, 22, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(836, 16, NULL, 22, '2025-12', 20.0000, '2025-12-18 15:08:00'),
(837, 5, NULL, 22, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(838, 27, NULL, 22, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(839, 6, NULL, 22, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(840, 8, NULL, 22, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(841, 33, NULL, 22, '2025-12', 0.0800, '2025-12-18 15:08:00'),
(842, 38, NULL, 22, '2025-12', 0.0100, '2025-12-18 15:08:00'),
(843, 37, NULL, 22, '2025-12', 0.0300, '2025-12-18 15:08:00'),
(844, 1, NULL, 22, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(845, 11, NULL, 22, '2025-12', 11000000.0000, '2025-12-18 15:08:00'),
(846, NULL, 1, 22, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(847, NULL, 27, 22, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(848, NULL, 3, 22, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(849, NULL, 20, 22, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(850, NULL, 21, 22, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(851, NULL, 22, 22, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(852, NULL, 4, 22, '2025-12', -11000000.0000, '2025-12-18 15:08:00'),
(853, NULL, 5, 22, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(854, NULL, 19, 22, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(855, NULL, 24, 22, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(856, NULL, 25, 22, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(857, NULL, 26, 22, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(858, NULL, 6, 22, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(859, 39, NULL, 23, '2025-12', 0.3000, '2025-12-18 15:08:00'),
(860, 26, NULL, 23, '2025-12', 4400000.0000, '2025-12-18 15:08:00'),
(861, 35, NULL, 23, '2025-12', 0.0100, '2025-12-18 15:08:00'),
(862, 25, NULL, 23, '2025-12', 11000000.0000, '2025-12-18 15:08:00'),
(863, 36, NULL, 23, '2025-12', 0.1750, '2025-12-18 15:08:00'),
(864, 34, NULL, 23, '2025-12', 0.0150, '2025-12-18 15:08:00'),
(865, 14, NULL, 23, '2025-12', 2340000.0000, '2025-12-18 15:08:00'),
(866, 13, NULL, 23, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(867, 17, NULL, 23, '2025-12', 1000000.0000, '2025-12-18 15:08:00'),
(868, 3, NULL, 23, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(869, 29, NULL, 23, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(870, 10, NULL, 23, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(871, 2, NULL, 23, '2025-12', 26.0000, '2025-12-18 15:08:00'),
(872, 7, NULL, 23, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(873, 15, NULL, 23, '2025-12', 4960000.0000, '2025-12-18 15:08:00'),
(874, 4, NULL, 23, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(875, 16, NULL, 23, '2025-12', 20.0000, '2025-12-18 15:08:00'),
(876, 5, NULL, 23, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(877, 27, NULL, 23, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(878, 6, NULL, 23, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(879, 8, NULL, 23, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(880, 33, NULL, 23, '2025-12', 0.0800, '2025-12-18 15:08:00'),
(881, 38, NULL, 23, '2025-12', 0.0100, '2025-12-18 15:08:00'),
(882, 37, NULL, 23, '2025-12', 0.0300, '2025-12-18 15:08:00'),
(883, 1, NULL, 23, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(884, 11, NULL, 23, '2025-12', 11000000.0000, '2025-12-18 15:08:00'),
(885, NULL, 1, 23, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(886, NULL, 27, 23, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(887, NULL, 3, 23, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(888, NULL, 20, 23, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(889, NULL, 21, 23, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(890, NULL, 22, 23, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(891, NULL, 4, 23, '2025-12', -11000000.0000, '2025-12-18 15:08:00'),
(892, NULL, 5, 23, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(893, NULL, 19, 23, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(894, NULL, 24, 23, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(895, NULL, 25, 23, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(896, NULL, 26, 23, '2025-12', 0.0000, '2025-12-18 15:08:00'),
(897, NULL, 6, 23, '2025-12', 0.0000, '2025-12-18 15:08:00');

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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `shift_change_requests`
--

INSERT INTO `shift_change_requests` (`requestID`, `employeeID`, `date`, `currentShiftID`, `newShiftID`, `reason`, `status`, `approverID`, `created_at`, `updated_at`) VALUES
(1, 9, '2025-12-03', 41, 41, 'Em bận việc gia đình buổi sáng nên xin đổi sang ca chiều', 'APPROVED', 9, '2025-12-11 09:54:13', NULL),
(2, 9, '2025-12-03', 41, 36, 'Em bận việc gia đình buổi sáng nên xin đổi sang ca sáng', 'APPROVED', 9, '2025-12-11 09:54:44', NULL),
(3, 20, '2025-12-17', 36, 41, 'test YC gửi xin đổi ca', 'APPROVED', 9, '2025-12-15 09:05:25', NULL),
(4, 9, '2025-12-23', 41, 36, 'Test YC đổi ca lần 2', 'PENDING', NULL, '2025-12-21 19:07:48', NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `taxsettings`
--

DROP TABLE IF EXISTS `taxsettings`;
CREATE TABLE IF NOT EXISTS `taxsettings` (
  `SettingID` int NOT NULL AUTO_INCREMENT,
  `SettingKey` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Value` decimal(15,4) NOT NULL,
  `EffectiveDate` date NOT NULL,
  `IsActive` tinyint(1) NOT NULL DEFAULT '1',
  `Description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Mô tả chi tiết về tham số',
  PRIMARY KEY (`SettingID`),
  UNIQUE KEY `uk_setting_key_date` (`SettingKey`,`EffectiveDate`)
) ENGINE=MyISAM AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `taxsettings`
--

INSERT INTO `taxsettings` (`SettingID`, `SettingKey`, `Value`, `EffectiveDate`, `IsActive`, `Description`) VALUES
(1, 'PERSONAL_DEDUCTION', 11000000.0000, '2024-01-01', 1, 'Giảm trừ bản thân (VN)'),
(2, 'DEPENDENT_DEDUCTION', 4400000.0000, '2024-01-01', 1, 'Giảm trừ người phụ thuộc (VN)'),
(3, 'BASIC_SALARY_STATE', 2340000.0000, '2024-01-01', 1, 'Lương cơ sở nhà nước'),
(4, 'REGION_MIN_SALARY', 4960000.0000, '2024-01-01', 1, 'Lương tối thiểu vùng I'),
(5, 'LUNCH_ALLOWANCE_LIMIT', 1000000.0000, '2024-01-01', 1, 'Tiền ăn ca tối đa không chịu thuế TNCN'),
(6, 'INSURANCE_CAP_MULTIPLIER', 20.0000, '2024-01-01', 1, 'Số lần lương cơ sở để tính trần BHXH/BHYT'),
(7, 'HOURS_PER_WORKDAY', 8.0000, '2024-01-01', 1, 'Số giờ làm việc chuẩn trong 1 ngày (dùng tính overtime)'),
(11, 'BHXH_RATE_EMP', 0.0800, '2025-01-01', 1, 'Tỷ lệ đóng BHXH của nhân viên (8%)'),
(12, 'BHYT_RATE_EMP', 0.0150, '2025-01-01', 1, 'Tỷ lệ đóng BHYT của nhân viên (1.5%)'),
(13, 'BHTN_RATE_EMP', 0.0100, '2025-01-01', 1, 'Tỷ lệ đóng BHTN của nhân viên (1%)'),
(14, 'BHXH_RATE_COMP', 0.1750, '2025-01-01', 1, 'Tỷ lệ đóng BHXH của công ty (17.5%)'),
(15, 'BHYT_RATE_COMP', 0.0300, '2025-01-01', 1, 'Tỷ lệ đóng BHYT của công ty (3%)'),
(16, 'BHTN_RATE_COMP', 0.0100, '2025-01-01', 1, 'Tỷ lệ đóng BHTN của công ty (1%)'),
(17, 'REGION_MIN_SALARY', 5500000.0000, '2026-01-01', 1, 'Đính chính: Theo nghị định 123/CP');

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
-- Các ràng buộc cho bảng `attendancerequests`
--
ALTER TABLE `attendancerequests`
  ADD CONSTRAINT `fk_attrequest_approver` FOREIGN KEY (`approverid`) REFERENCES `users` (`userID`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_attrequest_shift` FOREIGN KEY (`shiftid`) REFERENCES `shifts` (`shiftID`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_attrequest_user` FOREIGN KEY (`userid`) REFERENCES `users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

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
-- Các ràng buộc cho bảng `salary_rule_version`
--
ALTER TABLE `salary_rule_version`
  ADD CONSTRAINT `FK_RuleVersion_Rule` FOREIGN KEY (`rule_id`) REFERENCES `salary_rule` (`rule_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `salary_variable_cache`
--
ALTER TABLE `salary_variable_cache`
  ADD CONSTRAINT `FK_Cache_Employee` FOREIGN KEY (`employee_id`) REFERENCES `users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_Cache_Rule` FOREIGN KEY (`rule_id`) REFERENCES `salary_rule` (`rule_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_Cache_Variable` FOREIGN KEY (`variable_id`) REFERENCES `salaryvariables` (`VariableID`) ON DELETE CASCADE ON UPDATE CASCADE;

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
