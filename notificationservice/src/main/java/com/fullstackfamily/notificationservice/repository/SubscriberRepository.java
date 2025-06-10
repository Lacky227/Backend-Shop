package com.fullstackfamily.notificationservice.repository;

import com.fullstackfamily.notificationservice.entity.Subscriber;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriberRepository extends MongoRepository<Subscriber, String> {
    Optional<Subscriber> findByEmail(String email);
    List<Subscriber> findAllBySubscribedIsTrue();
}
