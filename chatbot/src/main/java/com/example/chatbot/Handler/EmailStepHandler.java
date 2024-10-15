package com.example.chatbot.Handler;

// EmailStepHandler.java


import com.example.chatbot.Handler.RegistrationStepHandler;
import com.example.chatbot.model.Customer;
import com.example.chatbot.service.CustomerService;
import com.example.chatbot.service.RegistrationStateService;

import java.util.Map;

public class EmailStepHandler implements RegistrationStepHandler {
    private final RegistrationStateService registrationStateService;
    private final CustomerService customerService;


    public EmailStepHandler(RegistrationStateService registrationStateService, CustomerService customerService) {
        this.registrationStateService = registrationStateService;
        this.customerService = customerService;

    }

    @Override
    public void handle(Customer customer, String message, Map<String, String> response) {
        if (isValidEmail(message)) {
            customer.setEmail(message);
            customerService.save(customer);
            registrationStateService.updateStep(customer.getId(), "address");
            response.put("message", "Thanks. Provide your Address.");
        } else {
            response.put("message", "Invalid email format. Please provide a valid email.");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}
