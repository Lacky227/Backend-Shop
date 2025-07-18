package com.fullstackfamily.authservice.repository;

import com.fullstackfamily.authservice.entity.BlackListToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListTokenRepository extends JpaRepository<BlackListToken, String> {
    boolean existsByToken(String token);
}
