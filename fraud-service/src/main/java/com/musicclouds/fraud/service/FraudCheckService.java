package com.musicclouds.fraud.service;

import com.musicclouds.fraud.domain.FraudCheckHistory;
import com.musicclouds.fraud.repository.FraudCheckHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class FraudCheckService {

    private final FraudCheckHistoryRepository fraudCheckHistoryRepository;

    public boolean isFraudulentUser(Integer userId) {
        fraudCheckHistoryRepository.save(
                FraudCheckHistory.builder()
                        .userId(userId)
                        .isFraudster(false)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        return false;
    }

}
