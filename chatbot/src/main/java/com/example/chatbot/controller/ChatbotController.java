package com.example.chatbot.controller;

import com.example.chatbot.DataTransfareObject.ChatbotRequest;
import com.example.chatbot.model.Customer;
import com.example.chatbot.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.StringHttpMessageConverter;
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

    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    @Autowired
    private CustomerService customerService;
    private StringHttpMessageConverter stringHttpMessageConverter;

    @PostMapping("/message")
    public Map<String, String> handleMessage(@Valid @RequestBody ChatbotRequest request) {
        String phone = request.getPhone();
        String message = request.getMessage();
        Map<String, String> response = new HashMap<>();

        // Step 1: Check if the user is already registered
        Optional<Customer> customerOptional = customerService.findCustomerByPhoneNumber(phone);

        if (customerOptional.isPresent()) {
            // If the user is already registered, handle their request as a returning customer
            Customer customer = customerOptional.get();
            response.put("message", "Welcome back, " + customer.getName() + "! You said: " + message + ". How can I assist you today?");
        } else {
            // Step 2: Check the user's registration state from the database
            Map<String, Object> registrationState = customerService.getRegistrationState(phone);

            if (registrationState != null) {
                boolean otpVerified = (Boolean) registrationState.get("otp_verified");

                String step     = (String) registrationState.get("step");
                String name     = (String) registrationState.get("name");
                String surname  = (String) registrationState.get("surname");
                String email    = (String) registrationState.get("email");
                String address  = (String) registrationState.get("address");


                // Step 3: OTP not verified yet, ask for OTP input or resend OTP
                if (!otpVerified) {
                    if (customerService.validateOtp(phone, message)) {
                        // OTP verified, update the registration state
                        customerService.updateRegistrationState(phone, true, null, null, null, null, null);
                        response.put("message", "OTP verified! Please provide your Name.");
                    } else {
                        response.put("message", "Invalid OTP or OTP expired. Please enter the correct OTP.");
                    }
                }
                // collecting registration data step-by-step
                else {
                    switch (step) {
                        case "name":
                            customerService.updateRegistrationState(phone, true, "surname", message, null, null, null);
                            response.put("message", "Thanks, " + message + ". Now, please provide your Surname.");
                            break;
                        case "surname":
                            customerService.updateRegistrationState(phone, true, "email", name, message, null, null);
                            response.put("message", "Got it, " + message + ". Next, please provide your Email.");
                            break;
                        case "email":
                            if (isValidEmail(message)){
                                customerService.updateRegistrationState(phone, true, "address", name, surname, message, null);
                                response.put("message", "Thanks! Now, please provide your Physical Address.");
                            }  else {
                                response.put("message", "Invalid email format. Please provide a valid email address.");
                            }
                            break;
                        case "address":
                            Customer newCustomer = new Customer();
                            newCustomer.setName(name);
                            newCustomer.setSurname(surname);
                            newCustomer.setEmail(email);
                            newCustomer.setAddress(message);
                            newCustomer.setPhoneNumber(phone);

                            customerService.saveRegistrationData(phone, newCustomer);
                            customerService.registerCustomer(newCustomer);

                            response.put("message", "Registration successful! Welcome to The CSD App.");
                            customerService.removeRegistrationState(phone);  // Clear the session once registration is done
                            break;
                        default:
                            response.put("message", "We encountered an error. Please try again.");
                    }
                }
            } else {
                // Step 5: start new registration and send otp
                customerService.sendOtp(phone);
                response.put("message", "Your phone number was not found on our Records. To register as a new customer, please provide " +
                        " the OTP sent to your phone number.");
                customerService.updateRegistrationState(phone, false, "otp", null, null, null, null);
            }
        }

        return response;
    }
}



