package com.manaplastic.backend.service;

import com.manaplastic.backend.entity.LeaverequestEntity;
import com.manaplastic.backend.entity.UserEntity;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
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
            System.err.println("Lỗi khi gửi email OTP: " + e.getMessage());
        }
    }

    // Luồng gửi phiếu lương
    @Async
    public void sendPayslipEmail(String userEmail, String userFullname, String payPeriod, byte[] pdfBytes) {
        if (userEmail == null || userEmail.isEmpty()) {
            System.out.println("WARN: User " + userFullname + " không có email. Bỏ qua gửi payslip.");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            // multipart = true để hỗ trợ đính kèm
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(userEmail);
            helper.setSubject("[MANAPlastic] Phiếu lương tháng " + payPeriod);

            String text = String.format(
                    "Xin chào %s,\n\n" +
                            "Phòng Nhân sự xin gửi bạn phiếu lương tháng %s.\n" +
                            "Vui lòng xem file PDF đính kèm để biết chi tiết.\n\n" +
                            "Mọi thắc mắc vui lòng liên hệ bộ phận HR.\n\n" +
                            "MANAPlastic trân trọng.",
                    userFullname, payPeriod
            );
            helper.setText(text);

            // Đính kèm file PDF
            String fileName = "Phieu_luong_" + payPeriod + ".pdf";
            helper.addAttachment(fileName, new ByteArrayResource(pdfBytes));

            mailSender.send(message);
            System.out.println("-> Đã gửi mail lương cho: " + userEmail);

        } catch (Exception e) {
            System.err.println("Lỗi khi gửi email lương cho " + userEmail + ": " + e.getMessage());
        }
    }

    //Luồng gửi pass và username
    public void sendAccountInfo(String toEmail, String username, String rawPassword,String fullname) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@manaplastic.com");
            helper.setTo(toEmail);
            helper.setSubject("Thông tin tài khoản MANAPlastic của nhân viên - " + fullname);

            String htmlContent = """
            <div style="font-family: Arial, sans-serif; line-height: 1.6;">
                <div style="text-align: center;">
                    <img src='cid:companyLogo' width='180' />
                </div>

                <h3>Chào mừng bạn gia nhập MANAPlastic</h3>

                <p>
                    Bên dưới là thông tin tài khoản của bạn để đăng nhập vào
                    hệ thống quản lý nhân sự của công ty <b>MANAPlastic</b>.
                </p>

                <p style="color: red; font-weight: bold;">
                    ⚠ TUYỆT ĐỐI KHÔNG CHIA SẺ THÔNG TIN NÀY
                </p>

                <p>
                    <b>Username:</b> %s <br/>
                    <b>Password:</b> %s
                </p>

                <p>
                    Vui lòng đổi mật khẩu sau khi đăng nhập.
                </p>

                <hr/>
                <small>MANAPlastic HR System</small>
            </div>
            """.formatted(username, rawPassword);

            helper.setText(htmlContent, true);

            FileSystemResource logo =
                    new FileSystemResource(new File("src/main/resources/static/assets/logo.png"));

            helper.addInline("companyLogo", logo);

            mailSender.send(message);
            System.out.println("Email đã gửi tới: " + toEmail + " (Nhân viên: " + fullname + " )" );

        } catch (Exception e) {
            System.err.println("Lỗi gửi email: " + e.getMessage());
        }
    }

}
