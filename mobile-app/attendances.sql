-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1:3306
-- Thời gian đã tạo: Th10 02, 2025 lúc 01:02 PM
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
-- Cơ sở dữ liệu: `attendace_system`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `attendances`
--

DROP TABLE IF EXISTS `attendances`;
CREATE TABLE IF NOT EXISTS `attendances` (
  `attendance_id` int NOT NULL AUTO_INCREMENT,
  `employee_id` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `check_in_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `check_in_image_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`attendance_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `attendances`
--

INSERT INTO `attendances` (`attendance_id`, `employee_id`, `check_in_time`, `check_in_image_url`) VALUES
(1, 'NV001', '2025-10-13 16:49:29', 'uploads/68eccb291fbe3_1760348969.jpg'),
(2, 'NV001', '2025-10-13 16:51:30', 'uploads/68eccba26bcf6_1760349090.jpg'),
(3, 'NV001', '2025-10-13 17:02:38', 'uploads/68ecce3e6596d_1760349758.jpg'),
(4, 'NV001', '2025-10-13 17:04:53', 'uploads/68eccec5c706f_1760349893.jpg'),
(5, 'NV001', '2025-10-13 17:06:11', 'uploads/68eccf13e6789_1760349971.jpg'),
(6, 'NV001', '2025-10-13 17:06:50', 'uploads/68eccf3a2d91e_1760350010.jpg'),
(7, 'NV001', '2025-10-13 17:11:48', 'uploads/68ecd064e7481_1760350308.jpg'),
(8, 'NV001', '2025-10-13 17:26:47', 'uploads/68ecd3e71fbb8_1760351207.jpg'),
(9, 'NV001', '2025-10-13 17:27:23', 'uploads/68ecd40b6d56e_1760351243.jpg');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
