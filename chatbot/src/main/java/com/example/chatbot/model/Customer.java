package com.example.chatbot.model;


import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class Customer {

    private UUID id;
    private String name;
    private String surname;
    private String phoneNumber;
    private String email;
    private String address;
    private LocalDateTime createdAt;


    // Getters

}
