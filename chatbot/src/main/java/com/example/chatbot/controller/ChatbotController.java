package com.example.chatbot.controller;

import com.example.chatbot.DataTransfareObject.ChatbotRequest;
import com.example.chatbot.model.Customer;
import com.example.chatbot.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Validated
public class ChatbotController {

    @Autowired
    private CustomerService customerService;

    private final Map<String, Customer> customerDataMap = new HashMap<>();
    private final Map<String, Boolean> otpVerifiedMap = new HashMap<>();

    @PostMapping("/message")
    public Map<String, String> handleMessage(@Valid @RequestBody ChatbotRequest request) {
        String phone = request.getPhone();
        String message = request.getMessage();

        Map<String, String> response = new HashMap<>();

        // Check if the customer is already registered
        Optional<Customer> customerOptional = customerService.findCustomerByPhoneNumber(phone);

        if (customerOptional.isPresent()) {
            // Customer exists, handle based on input message
            if (message.equalsIgnoreCase("hi") || message.equalsIgnoreCase("hello")) {
                response.put("message", "Welcome back to JHB CSD App, " + customerOptional.get().getName() + "! What would you like to do today?");
            } else {
                // Handle other user-specific commands or messages here
                response.put("message", "How can I assist you today?");
            }
        } else {
            // Customer does not exist, start OTP verification first
            if (!otpVerifiedMap.containsKey(phone)) {
                // Start OTP verification
                otpVerifiedMap.put(phone, false);
                response.put("message", "To register, please provide the OTP sent to your number.");
            } else if (!otpVerifiedMap.get(phone)) {
                // Verify OTP
                if (isValidOTP(message)) { // Assuming you have a method to validate the OTP
                    otpVerifiedMap.put(phone, true);
                    customerDataMap.put(phone, new Customer());
                    response.put("message", "OTP verified! What is your name?");
                } else {
                    response.put("message", "Invalid OTP. Please try again.");
                }
            } else {
                Customer newCustomer = customerDataMap.get(phone);

                if (newCustomer.getName() == null) {
                    newCustomer.setName(message);
                    response.put("message", "Thank you! Now, please provide your surname.");
                } else if (newCustomer.getSurname() == null) {
                    newCustomer.setSurname(message);
                    response.put("message", "Got it! Please provide your contact number.");
                } else if (newCustomer.getPhoneNumber() == null) {
                    newCustomer.setPhoneNumber(message);
                    response.put("message", "Thanks! What is your email address?");
                } else if (newCustomer.getEmail() == null) {
                    newCustomer.setEmail(message);
                    response.put("message", "Got it! Finally, please provide your address.");
                } else if (newCustomer.getAddress() == null) {
                    newCustomer.setAddress(message);
                    customerService.registerCustomer(newCustomer);
                    customerDataMap.remove(phone);
                    otpVerifiedMap.remove(phone);
                    response.put("message", "Registration successful! Welcome to JHB CSD App.");

                }
            }
        }

        return response;
    }

    // Dummy method to validate OTP - Replace with your OTP validation logic
    private boolean isValidOTP(String otp) {
        return "123456".equals(otp); // For demonstration purposes, assume "123456" is the valid OTP
    }
}
