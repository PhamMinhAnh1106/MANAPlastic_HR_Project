////
///
/// Đây chỉ là file mã hóa pass ( hash pass cho dữ liệu mẫu ) <3
///
///
//package com.manaplastic.backend.config;
//
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//
//public class PasswordEncryptionScript {
//
//    public static void main(String[] args) {
//        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        try {
//            String url = "jdbc:mysql://localhost:3306/manaplastic_hr?useSSL=false";
//            String username = "root";
//            String password = "";
//
//            Connection connection = DriverManager.getConnection(url, username, password);
//            System.out.println("Kết nối CSDL thành công!");
//
//            String selectQuery = "SELECT userID, password FROM users";
//            PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
//
//            ResultSet rs = selectStmt.executeQuery();
//
//            while (rs.next()) {
//                int userId = rs.getInt("userID");
//                String plainTextPassword = rs.getString("password");
//
//                // Mã hóa mật khẩu
//                String encodedPassword = passwordEncoder.encode(plainTextPassword);
//
//                // Cập nhật mật khẩu mã hóa vào cơ sở dữ liệu
//                String updateQuery = "UPDATE users SET password = ? WHERE userID = ?";
//                PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
//                updateStmt.setString(1, encodedPassword);
//                updateStmt.setInt(2, userId);
//
//                updateStmt.executeUpdate();
//                System.out.println("Đã cập nhật mật khẩu cho user ID: " + userId);
//            }
//
//            System.out.println("Hoàn tất mã hóa mật khẩu cho tất cả user.");
//
//            rs.close();
//            selectStmt.close();
//            connection.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}