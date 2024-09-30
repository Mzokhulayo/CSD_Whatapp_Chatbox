package com.example.chatbot.DataTransfareObject;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatbotRequest {
    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Message is required")
    private String message;

    // Getters and Setters

}
