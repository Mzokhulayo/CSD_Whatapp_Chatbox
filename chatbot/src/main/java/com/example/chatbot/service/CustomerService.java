package com.example.chatbot.service;

import com.example.chatbot.controller.ChatbotController;
import com.example.chatbot.model.Customer;
import com.example.chatbot.model.Otp;
import com.example.chatbot.repository.CustomerRepository;
import com.example.chatbot.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;



@Service
public class CustomerService {


    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OtpRepository otpRepository;

    // Generate and store OTP in the database
    public void sendOtp(String phoneNumber) {
        String otp = generateOtp();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

        Optional<Otp> existingOtp = otpRepository.findByPhoneNumber(phoneNumber);

        Otp otpRecord = new Otp();
        otpRecord.setPhoneNumber(phoneNumber);
        otpRecord.setOtp(otp);
        otpRecord.setExpirationTime(expirationTime);

        otpRepository.saveOtp(otpRecord);



        System.out.println("Sending OTP" + otp + " to phone number: " + phoneNumber);
        // Here, integrate with an actual SMS API to send OTP

    }

    // Validating OTP and checking for Correctness
    public boolean validateOtp(String phoneNumber, String otp) {
        Optional<Otp> otpRecord = otpRepository.findByPhoneNumber(phoneNumber);
        if (otpRecord.isPresent()) {
            Otp storedOtp = otpRecord.get();
            if (storedOtp.getOtp().equals(otp) && LocalDateTime.now().isBefore(storedOtp.getExpirationTime())) {
                return true;
            }
        }
        return false;
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(100000);
        return String.valueOf(otp);
    }

    public Customer registerCustomer(Customer customer) {
        customer.setCreatedAt(java.time.LocalDateTime.now());
        return customerRepository.save(customer);
    }

    public Optional<Customer> findCustomerByPhoneNumber(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber);
    }

    public Optional<Otp> findOtpByPhoneNumber(String phoneNumber) {
        return otpRepository.findByPhoneNumber(phoneNumber);
    }


}

