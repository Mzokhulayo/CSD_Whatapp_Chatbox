package com.example.chatbot.controller;

import com.example.chatbot.model.Customer;
import com.example.chatbot.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ChatbotController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/message")
    public Map<String, String> handleMessage(@RequestBody Map<String, String> payload) {
        String phone = payload.get("phone");
        String message = payload.get("message");
        String name = payload.get("name");
        String email = payload.get("email");

        Map<String, String> response = new HashMap<>();

        // Check if the customer is already registered
        // Using Optional helps in clearly expressing that a value might be absent and encourages handling both cases
        // (value present or not) explicitly. This makes the code safer and more readable.
        //Checking Customer Existence: customerService.findCustomerByPhoneNumber(phone) returns an Optional<Customer>
        // which may contain a Customer object if one exists with the given phone number.
        //
        Optional<Customer> customerOptional = customerService.findCustomerByPhoneNumber(phone);

        if (customerOptional.isPresent()) {
            // Customer exists, return a personalized welcome message
            Customer customer = customerOptional.get();
            response.put("message", "Welcome back, " + customer.getName() + "!");
        } else {
            // Customer does not exist, check if registration details are provided
            if (name != null && email != null) {
                // Register the new customer
                Customer newCustomer = new Customer();
                newCustomer.setName(name);
                newCustomer.setPhoneNumber(phone);
                newCustomer.setEmail(email);
                customerService.registerCustomer(newCustomer);

                // Respond with a registration success message
                response.put("message", "Registration successful, Welcome " + name + "!");
            } else {
                // Prompt for registration details
                response.put("message", "Hello! To proceed, please register by providing your name, email, and phone number.");
            }
        }
        return response;
    }


}
