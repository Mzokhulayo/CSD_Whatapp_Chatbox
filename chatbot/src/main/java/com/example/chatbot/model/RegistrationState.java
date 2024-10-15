package com.example.chatbot.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
public class RegistrationState {
    private UUID customerId;
    @Getter
    private boolean otpVerified;
    private String step;

    // Existing default constructor
    public RegistrationState() {}

    // New constructor
    public RegistrationState(UUID customerId, boolean otpVerified, String step) {
        this.customerId = customerId;
        this.otpVerified = otpVerified;
        this.step = step;
    }

    // Getters and setters...
}
