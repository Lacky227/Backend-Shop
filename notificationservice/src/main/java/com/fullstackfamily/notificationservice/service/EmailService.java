package com.fullstackfamily.notificationservice.service;

import com.fullstackfamily.notificationservice.dto.EmailRequest;
import com.fullstackfamily.notificationservice.entity.Subscriber;
import com.fullstackfamily.notificationservice.repository.SubscriberRepository;
import com.fullstackfamily.notificationservice.validation.ValidationUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class EmailService {

    private final SubscriberRepository subscriberRepository;

    public ResponseEntity<String> subscribe(EmailRequest emailRequest) {
        if (ValidationUtils.validateEmail(emailRequest.getEmail())) {
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
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body("Успішно повторно підписано.");
        }

        Subscriber newSubscriber = new Subscriber();
        newSubscriber.setEmail(emailRequest.getEmail());
        newSubscriber.setSubscribed(true);
        subscriberRepository.save(newSubscriber);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body("Успішно підписано.");
    }

    public ResponseEntity<String> unsubscribe(EmailRequest emailRequest) {
        if (ValidationUtils.validateEmail(emailRequest.getEmail())) {
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
}
