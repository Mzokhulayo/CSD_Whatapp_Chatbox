package com.example.chatbot.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
public class Otp {

    private UUID customerId;
    private String otp;
    private LocalDateTime expirationTime;
}
