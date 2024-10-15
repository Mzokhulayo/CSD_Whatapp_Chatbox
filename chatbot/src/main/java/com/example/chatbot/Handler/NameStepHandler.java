package com.example.chatbot.Handler;

import com.example.chatbot.model.Customer;
import com.example.chatbot.service.CustomerService;
import com.example.chatbot.service.RegistrationStateService;
import java.util.Map;

public class NameStepHandler implements RegistrationStepHandler {
    private final RegistrationStateService registrationStateService;
    private final CustomerService customerService;

    public NameStepHandler(RegistrationStateService registrationStateService, CustomerService customerService) {
        this.registrationStateService = registrationStateService;
        this.customerService = customerService;
    }

    @Override
    public void handle(Customer customer, String message, Map<String, String> response) {
        customer.setName(message);
        customerService.save(customer);
        registrationStateService.updateStep(customer.getId(), "surname");
        response.put("message", "Thanks, " + message + ". Please provide your Surname.");
    }
}
