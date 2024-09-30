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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.WebRequest;

@RestController
@RequestMapping("/api")
@SessionAttributes({"conversation", "otpVerified","lastResponse"})
@Validated
public class ChatbotController {

    @Autowired
    private CustomerService customerService;

    private final Map<String, Customer> customerDataMap = new HashMap<>();
    private final Map<String, Boolean> otpVerifiedMap = new HashMap<>();

    @PostMapping("/message")
    public Map<String, String> handleMessage(@Valid @RequestBody ChatbotRequest request, WebRequest webRequest) {
        String phone = request.getPhone();
        String message = request.getMessage();





        // Retrieve the session data for conversation
        Map<String, String> conversation = (Map<String, String>) webRequest.getAttribute("conversation", WebRequest.SCOPE_SESSION);
        Boolean otpVerified = (Boolean) webRequest.getAttribute("otpVerified", WebRequest.SCOPE_SESSION);
        String lastResponse = (String) webRequest.getAttribute("lastResponse", WebRequest.SCOPE_SESSION);


        if (conversation == null) {
            conversation = new HashMap<>();
        }

        Map<String, String> response = new HashMap<>();

        Optional<Customer> customerOptional = customerService.findCustomerByPhoneNumber(phone);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            response.put("message", "Welcome back, " + customer.getName() + "! You said: " + message + ". How can I assist you today?");
        } else {
            if (!customerDataMap.containsKey(phone)) {
                customerService.sendOtp(phone);
                response.put("message", "Your phone number was not found. To register, please provide the OTP sent to your number.");
                customerDataMap.put(phone, new Customer());
            } else {
                if (!otpVerifiedMap.getOrDefault(phone, false)) {
                    if (customerService.validateOtp(phone, message)) {
                        otpVerifiedMap.put(phone, true);
                        response.put("message", "OTP verified! Please provide your details in the format: Name, Surname, Email, Physical Address.");
                    } else {
                        response.put("message", "Invalid OTP or OTP expired. Please try again.");
                    }
                } else {
                    Customer newCustomer = customerDataMap.get(phone);
                    String[] userInformation = message.split(", ");
                    if (userInformation.length == 4) {
                        newCustomer.setName(userInformation[0]);
                        newCustomer.setSurname(userInformation[1]);
                        newCustomer.setEmail(userInformation[2]);
                        newCustomer.setAddress(userInformation[3]);
                        newCustomer.setPhoneNumber(phone);
                        customerService.registerCustomer(newCustomer);
                        customerDataMap.remove(phone);
                        otpVerifiedMap.remove(phone);
                        response.put("message", "Registration successful! Welcome to The CSD App.");
                    } else {
                        response.put("message", "Please provide the details in the correct format: Name, Surname, Email, Physical Address.");
                    }
                }
            }
        }

        // Store the conversation state in the session
        webRequest.setAttribute("conversation", conversation, WebRequest.SCOPE_SESSION);
        return response;
    }
}


