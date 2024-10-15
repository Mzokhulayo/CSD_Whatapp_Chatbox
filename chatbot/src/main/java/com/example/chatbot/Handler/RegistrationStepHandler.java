package com.example.chatbot.Handler;



import com.example.chatbot.model.Customer;
import java.util.Map;

public interface RegistrationStepHandler {
    void handle(Customer customer, String message, Map<String, String> response);
}
