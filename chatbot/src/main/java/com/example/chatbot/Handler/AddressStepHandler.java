package com.example.chatbot.Handler;

import com.example.chatbot.model.Customer;
import com.example.chatbot.service.CustomerService;
import com.example.chatbot.service.RegistrationStateService;
import java.util.Map;

public class AddressStepHandler implements RegistrationStepHandler {
    private final CustomerService customerService;
    private final RegistrationStateService registrationStateService;

    public AddressStepHandler(CustomerService customerService, RegistrationStateService registrationStateService) {
        this.customerService = customerService;
        this.registrationStateService = registrationStateService;
    }

    @Override
    public void handle(Customer customer, String message, Map<String, String> response) {
        customer.setAddress(message);
        customerService.save(customer); // Save the fully registered customer
        registrationStateService.deleteRegistrationState(customer.getId()); // Remove registration state
        response.put("message", "Thank Your Registration is complete! Welcome To JHB CSD App chat.");
    }
}
