// ChatbotController.java
package com.example.chatbot.controller;

import com.example.chatbot.DataTransfareObject.ChatbotRequest;
import com.example.chatbot.Handler.*;
import com.example.chatbot.model.Customer;
import com.example.chatbot.model.RegistrationState;
import com.example.chatbot.service.CustomerService;
import com.example.chatbot.service.OtpService;
import com.example.chatbot.service.RegistrationStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Validated
public class ChatbotController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RegistrationStateService registrationStateService;

    @Autowired
    private OtpService otpService;

    private final Map<String, RegistrationStepHandler> stepHandlers = new HashMap<>();

    @PostConstruct
    public void init() {
        stepHandlers.put("name", new NameStepHandler(registrationStateService, customerService));
        stepHandlers.put("surname", new SurnameStepHandler(registrationStateService, customerService));
        stepHandlers.put("email", new EmailStepHandler(registrationStateService, customerService));
        stepHandlers.put("address", new AddressStepHandler(customerService, registrationStateService));
    }

    @PostMapping("/message")
    public Map<String, String> handleMessage(@Valid @RequestBody ChatbotRequest request) {
        String phone = request.getPhone();
        String message = request.getMessage();
        Map<String, String> response = new HashMap<>();

        Optional<Customer> customerOptional = customerService.findByPhoneNumber(phone);

        if (customerOptional.isEmpty()) {
            // Start registration and generate OTP
            UUID customerId = customerService.startRegistration(phone);
            String otp = otpService.generateOtp();
            otpService.saveOtp(customerId, otp, phone);

            response.put("message", "Welcome! An OTP has been sent to your phone. Please enter the OTP.");
            return response;
        }

        Customer customer = customerOptional.get();
        Optional<RegistrationState> registrationStateOpt = registrationStateService.findByCustomerId(customer.getId());

        registrationStateOpt.ifPresentOrElse(
                registrationState -> handleRegistrationSteps(customer, registrationState, message, response),
                () -> response.put("message", "You are already registered!")
        );

        return response;
    }

    private void handleRegistrationSteps(Customer customer, RegistrationState registrationState, String message, Map<String, String> response) {
        boolean otpVerified = registrationState.isOtpVerified();
        String step = registrationState.getStep();

        if (!otpVerified) {
            if (otpService.validateOtp(customer.getId(), message)) {
                // Update OTP verified status
                registrationStateService.updateOtpVerified(customer.getId(), true); // Mark OTP as verified
                registrationStateService.updateStep(customer.getId(), "name"); // Proceed to the next step

                response.put("message", "OTP verified! Please provide your Name.");
                return; // End here if OTP verification was just completed
            } else {
                response.put("message", "Invalid OTP. Please try again.");
                return;
            }
        }

        // Handle other registration steps based on the current step
        RegistrationStepHandler handler = stepHandlers.get(step);
        if (handler != null) {
            handler.handle(customer, message, response);
        } else {
            response.put("message", "We encountered an error.");
        }
    }
}

