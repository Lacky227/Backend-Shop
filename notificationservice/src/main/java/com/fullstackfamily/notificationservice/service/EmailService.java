package com.fullstackfamily.notificationservice.service;

import com.fullstackfamily.notificationservice.dto.EmailRequest;
import com.fullstackfamily.notificationservice.dto.MallingRequest;
import com.fullstackfamily.notificationservice.entity.Subscriber;
import com.fullstackfamily.notificationservice.repository.SubscriberRepository;
import com.fullstackfamily.notificationservice.validation.ValidationUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class EmailService {

    private final SubscriberRepository subscriberRepository;
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    public ResponseEntity<String> subscribe(EmailRequest emailRequest) {
        if (ValidationUtils.emailInvalid(emailRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email недійсний.");
        }

        Optional<Subscriber> subscriberOpt = subscriberRepository.findByEmail(emailRequest.getEmail());

        if (subscriberOpt.isPresent()) {
            Subscriber subscriber = subscriberOpt.get();
            if (subscriber.isSubscribed()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Цей email вже підписано.");
            }
            subscriber.setSubscribed(true);
            subscriberRepository.save(subscriber);
            MallingRequest mallingRequest = new MallingRequest();
            mallingRequest.setEmail(emailRequest.getEmail());
            mallingRequest.setSubject("Дякую що ви повернулись до нас");
            try {
                log.info("Sending malling request to subscriber '{}'", subscriber.getEmail());
                sendEmail(mallingRequest);
                log.info("Sent malling request to subscriber '{}'", subscriber.getEmail());
            } catch (MessagingException e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body("Успішно повторно підписано.");
        }

        Subscriber newSubscriber = new Subscriber();
        newSubscriber.setEmail(emailRequest.getEmail());
        newSubscriber.setSubscribed(true);
        subscriberRepository.save(newSubscriber);
        MallingRequest mallingRequest = new MallingRequest();
        mallingRequest.setEmail(emailRequest.getEmail());
        mallingRequest.setSubject("Дякую за підписку");
        try {
            log.info("Sending malling request to subscriber '{}'", newSubscriber.getEmail());
            sendEmail(mallingRequest);
            log.info("Sent malling request to subscriber '{}'", newSubscriber.getEmail());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body("Успішно підписано.");
    }

    public ResponseEntity<String> unsubscribe(EmailRequest emailRequest) {
        if (ValidationUtils.emailInvalid(emailRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email недійсний.");
        }

        Optional<Subscriber> subscriberOpt = subscriberRepository.findByEmail(emailRequest.getEmail());

        if (subscriberOpt.isPresent()) {
            Subscriber subscriber = subscriberOpt.get();
            if (!subscriber.isSubscribed()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Цей email вже відписано.");
            }
            subscriber.setSubscribed(false);
            subscriberRepository.save(subscriber);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body("Успішно відписано.");
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body("Цей email не був підписаний.");
    }

    private void sendEmail(MallingRequest request) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", request.getEmail());

        String html = templateEngine.process("malling-template", context);
        String plainText = Jsoup.parse(html).text();

        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime, true);

        String FROM_EMAIL = "Lucky Shop <notification.veedev@gmail.com>";
        helper.setFrom(FROM_EMAIL);
        helper.setTo(request.getEmail());
        helper.setSubject(request.getSubject());
        helper.setText(plainText, html);

        mailSender.send(mime);
    }
}
