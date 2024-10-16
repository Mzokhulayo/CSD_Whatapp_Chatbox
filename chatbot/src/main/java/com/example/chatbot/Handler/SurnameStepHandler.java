package com.example.chatbot.Handler;

import com.example.chatbot.model.Customer;
import com.example.chatbot.service.CustomerService;
import com.example.chatbot.service.RegistrationStateService;
import java.util.Map;

public class SurnameStepHandler implements RegistrationStepHandler {
    private final RegistrationStateService registrationStateService;
    private final CustomerService customerService;

    public SurnameStepHandler(RegistrationStateService registrationStateService, CustomerService customerService) {
        this.registrationStateService = registrationStateService;
        this.customerService = customerService;
    }

    @Override
    public void handle(Customer customer, String message, Map<String, String> response) {
        if (!message.matches("[a-zA-Z]+")) {
            response.put("message", "Invalid surname format. Please enter a valid surname (letters only).");
            return;
        }

        customer.setSurname(message);
        customerService.save(customer);
        registrationStateService.updateStep(customer.getId(), "email");
        response.put("message", "Got it. Now, provide your Email.");
    }
}
