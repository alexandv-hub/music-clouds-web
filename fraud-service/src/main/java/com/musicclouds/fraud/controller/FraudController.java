package com.musicclouds.fraud.controller;

import com.musicclouds.clients.fraud.FraudCheckResponse;
import com.musicclouds.fraud.service.FraudCheckService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/fraud-check")
@AllArgsConstructor
@Slf4j
public class FraudController {

    private final FraudCheckService fraudCheckService;

    @GetMapping(path = "{userId}")
    public FraudCheckResponse isFraudster(
            @PathVariable("userId") Integer userId) {
        boolean isFraudulentUser = fraudCheckService.
                isFraudulentUser(userId);
        log.info("fraud check request for user {}", userId);

        return new FraudCheckResponse(isFraudulentUser);
    }
}
