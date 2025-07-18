package com.fullstackfamily.notificationservice.service;

import com.fullstackfamily.notificationservice.dto.ApiResponse;
import com.fullstackfamily.notificationservice.dto.EmailRequest;
import com.fullstackfamily.notificationservice.dto.MallingRequest;
import com.fullstackfamily.notificationservice.dto.TokenRequest;
import com.fullstackfamily.notificationservice.entity.Subscriber;
import com.fullstackfamily.notificationservice.repository.SubscriberRepository;
import com.fullstackfamily.notificationservice.validation.ValidationUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final SubscriberRepository subscriberRepository;
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;
    private final JwtService jwtService;

    @Value("${notification.token.secret}")
    private  String secretKey;
    @Value("${notification.email.base.unsub-url}")
    private String baseUnsubUrl;
    @Value("${notification.email.base.forgotpassword-url}")
    private String baseForgotPasswordUrl;

    /**
     * Підписує користувача на розсилку, генеруючи JWT токен.
     *
     * @param emailRequest Запит із email користувача.
     * @return ResponseEntity з відповіддю про статус операції.
     */
    public ResponseEntity<ApiResponse> subscribe(EmailRequest emailRequest) {
        if (ValidationUtils.emailInvalid(emailRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Email недійсний."));
        }

        Optional<Subscriber> subscriberOpt = subscriberRepository.findByEmail(emailRequest.getEmail());
        String token = jwtService.generateToken(emailRequest.getEmail(), false);

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
                sendWelcomeEmail(subscriber.getEmail(), "Дякую, що повернулись до нас!", token);
            } catch (MessagingException e) {
                throw new RuntimeException("Помилка надсилання листа: " + e.getMessage(), e);
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
            sendWelcomeEmail(newSubscriber.getEmail(), "Дякую за підписку!", token);
        } catch (MessagingException e) {
            throw new RuntimeException("Помилка надсилання листа: " + e.getMessage(), e);
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ApiResponse("Успішно підписано."));
    }

    /**
     * Відписує користувача від розсилки за JWT токеном.
     *
     * @param tokenRequest Запит із токеном.
     * @return ResponseEntity з відповіддю про статус операції.
     */
    public ResponseEntity<ApiResponse> unsubscribe(TokenRequest tokenRequest) {
        String token = tokenRequest.getToken();
        try {
            if (!jwtService.isTokenValid(token)) {
                return ResponseEntity.badRequest().body(new ApiResponse("Недійсний або прострочений токен."));
            }

            String email = jwtService.extractEmail(token);
            Optional<Subscriber> subscriberOpt = subscriberRepository.findByEmail(email);

            if (subscriberOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse("Користувача з таким токеном не знайдено."));
            }

            Subscriber subscriber = subscriberOpt.get();
            if (!subscriber.isSubscribed()) {
                return ResponseEntity.ok(new ApiResponse("Ви вже відписані."));
            }

            subscriber.setSubscribed(false);
            subscriberRepository.save(subscriber);

            return ResponseEntity.ok(new ApiResponse("Успішно відписано від розсилки."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Помилка обробки токена: " + e.getMessage()));
        }
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

    public void forgotPassword(String email) {
        sendForgotPassword(email, jwtService.generateTokenForgotPassword(email));
    }

    private void sendWelcomeEmail(String email, String subject, String token) throws MessagingException {
        MallingRequest request = new MallingRequest();
        request.setEmail(email);
        request.setSubject(subject);
        request.setLink(baseUnsubUrl + "?token=" + token);
        request.setTemplateName("welcome");
        sendEmail(request);
    }

    private void sendForgotPassword(String email, String token) {
        try {
            MallingRequest request = new MallingRequest();
            request.setEmail(email);
            request.setSubject("Відновлення паролю");
            request.setLink(baseForgotPasswordUrl + "?token=" + token);
            request.setTemplateName("forgot-password");
            sendEmail(request);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendEmail(MallingRequest request) throws MessagingException {
        Context context = new Context();
        context.setVariable("link", request.getLink());
        String html = templateEngine.process(request.getTemplateName(), context);
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
