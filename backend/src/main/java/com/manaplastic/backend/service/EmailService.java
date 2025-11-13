package com.manaplastic.backend.service;

import com.manaplastic.backend.entity.LeaverequestEntity;
import com.manaplastic.backend.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Luồng DUYỆTdđơn
    @Async
    public void sendApprovalEmail(String userEmail, String userFullname, LeaverequestEntity request) {
        if (userEmail == null || userEmail.isEmpty()) {
            System.out.println("WARN: User không có email. Không thể gửi mail.");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userEmail);
            message.setSubject("[Thông báo] Đơn nghỉ phép của bạn đã được duyệt");

            String text = String.format(
                    "Xin chào %s,\n\n" +
                            "Đơn nghỉ phép của bạn đã được DUYỆT.\n\n" +
                            "Chi tiết:\n" +
                            "- Loại đơn: %s\n" +
                            "- Từ ngày: %s\n" +
                            "- Đến ngày: %s\n\n" +
                            "MANAPlastic trân trọng.",
                   userFullname,
                    request.getLeavetype(),
                    request.getStartdate().format(DATE_FORMATTER),
                    request.getEnddate().format(DATE_FORMATTER)
            );

            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi email duyệt đơn: " + e.getMessage());
        }
    }

   // Luồng TỪ CHỐI đơn
    @Async
    public void sendRejectionEmail(String userEmail, String userFullname, LeaverequestEntity request){
        if (userEmail == null || userEmail.isEmpty()) {
            System.out.println("WARN: User không có email. Không thể gửi mail.");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userEmail);
            message.setSubject("[Thông báo] Đơn nghỉ phép của bạn đã bị từ chối");

            // Bạn đã nói là không cần lý do, nên mail sẽ đơn giản
            String text = String.format(
                    "Xin chào %s,\n\n" +
                            "Chúng tôi rất tiếc phải thông báo Đơn nghỉ phép của bạn đã bị TỪ CHỐI.\n\n" +
                            "Chi tiết đơn:\n" +
                            "- Loại đơn: %s\n" +
                            "- Từ ngày: %s\n" +
                            "- Đến ngày: %s\n\n" +
                            "Vui lòng liên hệ quản lý để biết thêm chi tiết.\n\n" +
                            "MANAPlastic trân trọng.",
                   userFullname,
                    request.getLeavetype(),
                    request.getStartdate().format(DATE_FORMATTER),
                    request.getEnddate().format(DATE_FORMATTER)
            );

            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi email từ chối: " + e.getMessage());
        }
    }

    // Luồng gửi mã OTP chức năng quên pass
    @Async
    public void sendOtpEmail(String toEmail, String otp, long expirationMinutes) {
        if (toEmail == null || toEmail.isEmpty()) {
            System.out.println("WARN: Email rỗng. Không thể gửi OTP.");
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[MANAPlastic] Yêu Cầu Đặt Lại Mật Khẩu");
        message.setText("Mã OTP của bạn là: " + otp + "\n" +
                "Mã này sẽ hết hạn sau " + expirationMinutes + " phút.");

        try {
            mailSender.send(message);
        } catch (Exception e) {
            // Ghi log lỗi, không ném ra để làm sập luồng chính
            System.err.println("Lỗi khi gửi email OTP: " + e.getMessage());
        }
    }
}
