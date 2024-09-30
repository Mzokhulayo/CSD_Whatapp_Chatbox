package com.example.chatbot.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class Otp {

    private String phoneNumber;
    private String otp;
    private LocalDateTime expirationTime;

}
