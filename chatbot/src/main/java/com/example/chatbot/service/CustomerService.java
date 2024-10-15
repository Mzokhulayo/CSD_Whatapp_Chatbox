package com.example.chatbot.service;

import com.example.chatbot.model.Customer;
import com.example.chatbot.model.Otp;
import com.example.chatbot.model.RegistrationState;
import com.example.chatbot.repository.CustomerRepository;
import com.example.chatbot.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class CustomerService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private RegistrationStateService registrationStateService; // Change here

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Optional<Customer> findByPhoneNumber(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber);
    }

    // Hash OTP before saving it to the database
    public String hashOtp(String otp) {
        return passwordEncoder.encode(otp);
    }

    public void sendOtp(UUID customerId) {
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);
        String otp = generateOtp();
        String hashedOtp = hashOtp(otp);

        Otp otpRecord = new Otp();
        otpRecord.setCustomerId(customerId); // Reference customer ID
        otpRecord.setOtp(hashedOtp);
        otpRecord.setExpirationTime(expirationTime);
        otpRepository.saveOtp(otpRecord);

        System.out.println("Sending unhashed OTP: " + otp + " to customer ID: " + customerId);
    }

    public boolean validateOtp(UUID customerId, String otp) {
        Optional<Otp> otpRecord = otpRepository.findByCustomerId(customerId);

        if (otpRecord.isPresent()) {
            Otp storedOtp = otpRecord.get();
            if (passwordEncoder.matches(otp, storedOtp.getOtp())
                    && LocalDateTime.now().isBefore(storedOtp.getExpirationTime())) {

                // Proceed to the next registration step after OTP is verified
                registrationStateService.updateRegistrationState(customerId, true, "name"); // Change here
                return true;
            }
        }
        return false;
    }

    // Generate a random OTP
    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    public void save(Customer customer) {
        String sql = "INSERT INTO customers (id, phone_number, name, surname, email, address, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (phone_number) DO UPDATE SET name = EXCLUDED.name, surname = EXCLUDED.surname, email = EXCLUDED.email, address = EXCLUDED.address";
        jdbcTemplate.update(sql, customer.getId(), customer.getPhoneNumber(), customer.getName(),
                customer.getSurname(), customer.getEmail(), customer.getAddress(), customer.getCreatedAt());
    }

    public UUID startRegistration(String phoneNumber) {
        // Check if the customer already exists
        Optional<Customer> existingCustomer = customerRepository.findByPhoneNumber(phoneNumber);
        UUID customerId;

        if (existingCustomer.isPresent()) {
            customerId = existingCustomer.get().getId();
        } else {
            // Create a new Customer instance and assign a UUID
            Customer customer = new Customer();
            customer.setId(UUID.randomUUID());
            customer.setPhoneNumber(phoneNumber);
            customer.setCreatedAt(LocalDateTime.now());

            // Save the customer to persist the phone number
            customerRepository.save(customer);
            customerId = customer.getId(); // Use the newly generated customer ID
        }

        // Instead of saving the registration state here, delegate to RegistrationStateService
        registrationStateService.save(new RegistrationState(customerId, false, "otp")); // Change here

        return customerId; // Return the generated or existing customer ID
    }

}
