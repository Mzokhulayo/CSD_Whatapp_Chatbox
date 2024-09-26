package com.example.chatbot.controller;

import com.example.chatbot.DataTransfareObject.ChatbotRequest;
import com.example.chatbot.model.Customer;
import com.example.chatbot.repository.CustomerRepository;
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
    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping("/message")
    public Map<String, String> handleMessage(@Valid @RequestBody ChatbotRequest request) {
        String phone = request.getPhone();
        String message = request.getMessage();

        Map<String, String> response = new HashMap<>();

        // Check if the phone number exists in the database
        Optional<Customer> customerOptional = customerService.findCustomerByPhoneNumber(phone);

        if (customerOptional.isPresent()) {
            // Customer found, handle any message accordingly
            Customer customer = customerOptional.get();
            response.put("message", "Welcome back, " + customer.getName() + "! You said: " + message + ". How can I assist you today?");
        } else {
            // Customer not found, start registration process
            if (!otpVerifiedMap.containsKey(phone)) {
                otpVerifiedMap.put(phone, false);
                customerService.sendOtp(phone, "123456"); // Dummy OTP sending
                response.put("message", "Your phone number was not found. To register, please provide the OTP sent to your number.");
            } else if (!otpVerifiedMap.get(phone)) {
                if (isValidOTP(message)) {
                    otpVerifiedMap.put(phone, true);
                    // Phone number already provided in request, now will skip asking
                    Customer newCustomer = new Customer();
                    newCustomer.setPhoneNumber(phone); // AutoSet Phone Number
                    customerDataMap.put(phone, newCustomer);
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
                    response.put("message", "Got it! Please provide your email address.");
                } else if (newCustomer.getEmail() == null) {
                    newCustomer.setEmail(message);
                    response.put("message", "Thanks! Please enter your Residential address?");
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

    // Dummy method for validation
    private boolean isValidOTP(String otp) {
        return "123456".equals(otp);
    }
}
