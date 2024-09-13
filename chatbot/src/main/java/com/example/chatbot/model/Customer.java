package com.example.chatbot.model;


import java.time.LocalDateTime;
import java.util.UUID;


public class Customer {

    private UUID id;
    private String name;
    private String phoneNumber;
    private String email;
    private LocalDateTime createdAt;


    // Getters

    public String getEmail() {
        return email;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
