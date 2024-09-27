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

                    response.put("message", "OTP verified! Please provide your details in the format: Name, Surname, Email, Physical Address");
                } else {
                    response.put("message", "Invalid OTP. Please try again.");
                }
            } else {
                // Process Information provided by user for registration
                Customer newCustomer = customerDataMap.get(phone);
                String[] userInformation =message.split(", ");

                if(userInformation.length == 4) {
                    newCustomer.setName(userInformation[0]);
                    newCustomer.setSurname(userInformation[1]);
                    newCustomer.setEmail(userInformation[2]);
                    newCustomer.setAddress(userInformation[3]);
                    newCustomer.setPhoneNumber(phone);

                    // Save Customer to database.
                    customerService.registerCustomer(newCustomer);
//
//                    may also add validation of correctness for user details like cellphone number and email
//                    also might be a good idea to check this before registering user.
//                    Error Handling for Missing Fields: You can also handle cases where users might provide fewer details
//                    than expected by checking the length of the userData array before saving.

//                    if (!userData[2].matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
//                        response.put("message", "Invalid email format. Please provide your details again in the format: Name, Surname, Email, Address.");
//                        return response;
//                    }

                    // clear the Maps
                    customerDataMap.remove(phone);
                    otpVerifiedMap.remove(phone);
                    response.put("message", "Registration successful! Welcome to The CSD App.");
                } else {
                    response.put("message", "Please provide the details in the correct format: \nName, \nSurname, \nEmail, \nPhysical Address");
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
