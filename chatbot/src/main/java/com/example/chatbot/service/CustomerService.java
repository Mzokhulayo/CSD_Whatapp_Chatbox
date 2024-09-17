package com.example.chatbot.service;

import com.example.chatbot.model.Customer;
import com.example.chatbot.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Customer registerCustomer(Customer customer) {
        customer.setCreatedAt(java.time.LocalDateTime.now());
        return customerRepository.save(customer);
    }

    public Optional<Customer> findCustomerByPhoneNumber(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber);
    }

    // Simulate OTP sending (in real applications, integrate with an SMS service)
    public void sendOtp(String phoneNumber, String otp) {
        System.out.println("Sending OTP " + otp + " to phone number: " + phoneNumber);
        // Here, integrate with an actual SMS API to send OTP
    }
}


//package com.example.chatbot.service;
//
//import com.example.chatbot.model.Customer;
//import com.example.chatbot.repository.CustomerRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class CustomerService {
//
//    @Autowired
//    private CustomerRepository customerRepository;
//
//    public Customer registerCustomer(Customer customer) {
//        customer.setCreatedAt(java.time.LocalDateTime.now());
//        return customerRepository.save(customer);
//    }
//
//    public Optional<Customer> findCustomerByPhoneNumber(String phoneNumber) {
//        return customerRepository.findByPhoneNumber(phoneNumber);
//    }
//}
