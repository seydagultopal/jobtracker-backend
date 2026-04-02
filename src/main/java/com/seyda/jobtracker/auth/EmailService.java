package com.seyda.jobtracker.auth;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // KAFKA'YI SÜREKLİ DİNLİYOR. MESAJ GELDİĞİ AN ÇALIŞIR.
    @KafkaListener(topics = "password-reset-topic", groupId = "pebble-group")
    public void consumePasswordResetEvent(PasswordResetEvent event) {
        System.out.println("Kafka'dan mesaj alındı. Mail gönderiliyor: " + event.getEmail());
        
        String resetLink = "http://localhost:5173/reset-password?token=" + event.getToken();

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(event.getEmail());
            helper.setSubject("Pebble - Şifre Sıfırlama Talebi 🔒");

            String htmlMsg = "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; background-color: #f9f9f9; padding: 40px; border-radius: 20px;\">"
                    + "<div style=\"text-align: center; margin-bottom: 30px;\">"
                    + "<h1 style=\"color: #E9ACBB; font-size: 32px; font-weight: 900; letter-spacing: 2px;\">PEBBLE</h1>"
                    + "</div>"
                    + "<div style=\"background-color: #ffffff; padding: 40px; border-radius: 20px; box-shadow: 0 10px 25px rgba(0,0,0,0.05);\">"
                    + "<h2 style=\"color: #333333; font-size: 24px; margin-bottom: 20px;\">Şifre Sıfırlama Talebi 🥺</h2>"
                    + "<p style=\"color: #666666; font-size: 16px; line-height: 1.6;\">Merhaba,</p>"
                    + "<p style=\"color: #666666; font-size: 16px; line-height: 1.6;\">Pebble hesabının şifresini unuttuğunu duyduk. Hiç merak etme, aşağıdaki butona tıklayarak yeni şifreni hemen belirleyebilirsin:</p>"
                    + "<div style=\"text-align: center; margin: 40px 0;\">"
                    + "<a href=\"" + resetLink + "\" style=\"background-color: #7B92B3; color: #ffffff; padding: 15px 30px; text-decoration: none; border-radius: 12px; font-weight: bold; font-size: 16px; display: inline-block;\">Şifremi Yenile</a>"
                    + "</div>"
                    + "<p style=\"color: #999999; font-size: 14px; text-align: center;\">Bu talebi sen yapmadıysan, bu maili görmezden gelebilirsin. Güvendesin!</p>"
                    + "</div></div>";

            helper.setText(htmlMsg, true);
            mailSender.send(message);
            
            System.out.println("Mail BAŞARIYLA gönderildi: " + event.getEmail());

        } catch (Exception e) {
            System.err.println("Mail gönderirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}