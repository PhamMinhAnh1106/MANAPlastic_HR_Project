package com.manaplastic.backend.service;

import com.manaplastic.backend.entity.UserEntity;
import com.manaplastic.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class ForgotPassService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private  EmailService emailService;
     @Autowired
     private UserRepository userRepository;
     @Autowired
     private PasswordEncoder passwordEncoder;
     @Autowired
     private JwtService jwtService;
     @Autowired
     private UserDetailsService userDetailsService;


    private static final String OTP_PREFIX = "otp:forgot_password:";
    private static final long OTP_EXPIRATION_MINUTES = 5;

    private static final long RESET_TOKEN_EXPIRATION_MINUTES = 15;


    public void forgotPassword(String email) {

         if (!userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email không tồn tại");
         }

        String otp = generateOtp();//  Tạo OTP ngẫu nhiên (6 chữ số)
        String redisKey = OTP_PREFIX + email;

        redisTemplate.opsForValue().set(redisKey, otp, OTP_EXPIRATION_MINUTES, TimeUnit.MINUTES);

        // sendOtpEmail(email, otp);
        emailService.sendOtpEmail(email, otp, OTP_EXPIRATION_MINUTES);
    }

    public String verifyOtp(String email, String otp) {
        String redisKey = OTP_PREFIX + email;
        String storedOtp = redisTemplate.opsForValue().get(redisKey);

        if (storedOtp == null) {
            throw new RuntimeException("OTP đã hết hạn hoặc không tồn tại!");
        }

        if (!storedOtp.equals(otp)) {
            throw new RuntimeException("OTP không chính xác!");
        }

        redisTemplate.delete(redisKey); // Xóa OTP sau khi xác thực thành công

        // Lấy UserDetails để tạo JWT
        UserEntity userDetails = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));

        //Tạo JWT
        String resetToken = jwtService.generateToken(userDetails);

        // Lưu JWT vào Redis
        String resetKey = "reset_token:" + resetToken;
        redisTemplate.opsForValue().set(
                resetKey,
                userDetails.getUsername(),
                RESET_TOKEN_EXPIRATION_MINUTES,
                TimeUnit.MINUTES
        );

        return resetToken;
    }


    public void resetPassword(String resetToken, String newPassword) {
        String username;
        try {
            username = jwtService.extractUsername(resetToken);
        } catch (Exception e) {
            throw new RuntimeException("Token không hợp lệ hoặc đã hết hạn!");
        }

        String resetKey = "reset_token:" + resetToken;
        String usernameFromRedis = redisTemplate.opsForValue().get(resetKey);

        if (usernameFromRedis == null) {
            throw new RuntimeException("Token đã được sử dụng hoặc không hợp lệ!");
        }

        if (!username.equals(usernameFromRedis)) {
            throw new RuntimeException("Token không khớp!");
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + username));


        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        System.out.println("Đã cập nhật mật khẩu cho: " + username);

        redisTemplate.delete(resetKey);
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    // Mang qua EmailService
//    private void sendOtpEmail(String toEmail, String otp) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(toEmail);
//        message.setSubject("[MANAPlastic] Yêu Cầu Đặt Lại Mật Khẩu");
//        message.setText("Mã OTP của bạn là: " + otp + "\n" +
//                "Mã này sẽ hết hạn sau " + OTP_EXPIRATION_MINUTES + " phút.");
//
//        try {
//            mailSender.send(message);
//        } catch (Exception e) {
//            throw new RuntimeException("Không thể gửi email OTP. Lỗi: " + e.getMessage());
//        }
//    }
}
