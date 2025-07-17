package com.fullstackfamily.notificationservice.service;

import com.fullstackfamily.notificationservice.dto.ApiResponse;
import com.fullstackfamily.notificationservice.dto.EmailRequest;
import com.fullstackfamily.notificationservice.dto.MallingRequest;
import com.fullstackfamily.notificationservice.entity.Subscriber;
import com.fullstackfamily.notificationservice.repository.SubscriberRepository;
import com.fullstackfamily.notificationservice.validation.ValidationUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import java.util.Base64;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final SubscriberRepository subscriberRepository;
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;
    @Value("${notification.token.secret}")
    private String secretKey;

    public ResponseEntity<ApiResponse> subscribe(EmailRequest emailRequest) {
        if (ValidationUtils.emailInvalid(emailRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Email недійсний."));
        }

        Optional<Subscriber> subscriberOpt = subscriberRepository.findByEmail(emailRequest.getEmail());

        String token = generateToken(emailRequest.getEmail(), secretKey);

        if (subscriberOpt.isPresent()) {
            Subscriber subscriber = subscriberOpt.get();
            if (subscriber.isSubscribed()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse("Цей email вже підписано."));
            }

            subscriber.setToken(token);
            subscriber.setSubscribed(true);

            subscriberRepository.save(subscriber);

            try {
                sendWelcomeEmail(subscriber.getEmail(), "Дякую що ви повернулись до нас", token);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }

            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(new ApiResponse("Успішно повторно підписано."));
        }

        Subscriber newSubscriber = new Subscriber();

        newSubscriber.setToken(token);
        newSubscriber.setEmail(emailRequest.getEmail());
        newSubscriber.setSubscribed(true);
        subscriberRepository.save(newSubscriber);

        try {
            sendWelcomeEmail(newSubscriber.getEmail(), "Дякую за підписку", token);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ApiResponse("Успішно підписано."));
    }

    private void sendWelcomeEmail(String email, String subject, String token) throws MessagingException {
        MallingRequest request = new MallingRequest();
        request.setEmail(email);
        request.setSubject(subject);
        request.setUnsubscribeLink("https://yourdomain.com/api/notify/unsubscribe?token=" + token);
        sendEmail(request);
    }

    public String generateToken(String email, String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        byte[] hmacSha256 = new HmacUtils("HmacSHA256", keyBytes).hmac(email);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hmacSha256);
    }

    public ResponseEntity<ApiResponse> unsubscribe(String token) {
        Optional<Subscriber> subscriberOpt = subscriberRepository.findByToken(token);

        if (subscriberOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("Неіснуючий токен"));
        }

        Subscriber subscriber = subscriberOpt.get();
        if (!subscriber.isSubscribed()) {
            return ResponseEntity.ok(new ApiResponse("Ви вже були відписані"));
        }

        subscriber.setSubscribed(false);
        subscriberRepository.save(subscriber);

        return ResponseEntity.ok(new ApiResponse("Вас успішно відписано до розсилки"));
    }

//    public ResponseEntity<ApiResponse> unsubscribe(EmailRequest emailRequest) {
//        if (ValidationUtils.emailInvalid(emailRequest.getEmail())) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse("Email недійсний."));
//        }
//
//        Optional<Subscriber> subscriberOpt = subscriberRepository.findByEmail(emailRequest.getEmail());
//
//        if (subscriberOpt.isPresent()) {
//            Subscriber subscriber = subscriberOpt.get();
//            if (!subscriber.isSubscribed()) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body(new ApiResponse("Цей email вже відписано."));
//            }
//
//            subscriber.setSubscribed(false);
//            subscriberRepository.save(subscriber);
//
//            return ResponseEntity.status(HttpStatus.ACCEPTED)
//                    .body(new ApiResponse("Успішно відписано."));
//        }
//
//        return ResponseEntity.status(HttpStatus.ACCEPTED)
//                .body(new ApiResponse("Цей email не був підписаний."));
//    }

    private void sendEmail(MallingRequest request) throws MessagingException {
        Context context = new Context();
        context.setVariable("unsubscribeLink", request.getUnsubscribeLink());
        String html = templateEngine.process("welcome", context);
        String plainText = Jsoup.parse(html).text();

        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime, true);

        String FROM_EMAIL = "Lucky Shop <noreply@lucky.com>";
        helper.setFrom(FROM_EMAIL);
        helper.setTo(request.getEmail());
        helper.setSubject(request.getSubject());
        helper.setText(plainText, html);

        mailSender.send(mime);
    }
}
