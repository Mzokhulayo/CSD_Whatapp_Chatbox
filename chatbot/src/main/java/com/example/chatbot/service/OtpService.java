package com.example.chatbot.service;

import com.example.chatbot.model.Otp;
import com.example.chatbot.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class OtpService {

    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final RegistrationStateService registrationStateService;

    @Autowired
    public OtpService(OtpRepository otpRepository, RegistrationStateService registrationStateService) {
        this.otpRepository = otpRepository;
        this.registrationStateService = registrationStateService;
    }


public void saveOtp(UUID customerId, String otp, String phoneNumber) {
    LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);
    String hashedOtp = hashOtp(otp);

    // Print the OTP and the phone number
    System.out.println("OTP: " + otp + " is sent to: " + phoneNumber);

    Otp otpRecord = new Otp();
    otpRecord.setCustomerId(customerId);
    otpRecord.setOtp(hashedOtp);
    otpRecord.setExpirationTime(expirationTime);
    otpRepository.saveOtp(otpRecord);
}

    // Hash OTP before saving it to the database
    private String hashOtp(String otp) {
        return passwordEncoder.encode(otp);
    }

    public boolean validateOtp(UUID customerId, String otp) {
        Optional<Otp> otpRecord = otpRepository.findByCustomerId(customerId);

        if (otpRecord.isPresent()) {
            Otp storedOtp = otpRecord.get();
            boolean isOtpValid = passwordEncoder.matches(otp, storedOtp.getOtp()) &&
                    LocalDateTime.now().isBefore(storedOtp.getExpirationTime());

            if (isOtpValid) {
                // OTP is verified, update registration state to mark OTP as verified
                registrationStateService.updateOtpVerified(customerId, true); // Mark OTP as verified

                // Optionally, move to the next step (e.g., asking for name)
//                registrationStateService.updateStep(customerId, "name");

                return true;
            }
        }
        return false;
    }


    // Generate a random OTP
    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(100000);
        return String.valueOf(otp);
    }
}
