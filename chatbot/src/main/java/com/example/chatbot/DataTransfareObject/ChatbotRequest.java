package com.example.chatbot.DataTransfareObject;

import jakarta.validation.constraints.NotBlank;

public class ChatbotRequest {
    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Message is required")
    private String message;

    // Getters and Setters

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
