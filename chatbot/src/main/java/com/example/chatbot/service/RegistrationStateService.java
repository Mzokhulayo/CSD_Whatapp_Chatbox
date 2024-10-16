package com.example.chatbot.service;

import com.example.chatbot.model.RegistrationState;
import com.example.chatbot.repository.RegistrationStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class RegistrationStateService {

    @Autowired
    private RegistrationStateRepository registrationStateRepository;


    public void save(RegistrationState registrationState) {
        // Save or update the registration state
        registrationStateRepository.save(registrationState);
    }

    public Optional<RegistrationState> findByCustomerId(UUID customerId) {
        return registrationStateRepository.findByCustomerId(customerId);
    }


    public void updateOtpVerified(UUID customerId, boolean otpVerified) {
        Optional<RegistrationState> registrationStateOpt = findByCustomerId(customerId);
        if (registrationStateOpt.isPresent()) {
            RegistrationState registrationState = registrationStateOpt.get();
            registrationState.setOtpVerified(otpVerified);
            registrationStateRepository.save(registrationState); // This should update the existing record
        }
    }

    public void updateStep(UUID customerId, String step) {
        Optional<RegistrationState> registrationStateOpt = findByCustomerId(customerId);
        if (registrationStateOpt.isPresent()) {
            RegistrationState registrationState = registrationStateOpt.get();
            registrationState.setStep(step);
            registrationStateRepository.save(registrationState); // Update the step
        }
    }

    public void deleteRegistrationState(UUID customerId) {
        registrationStateRepository.deleteByCustomerId(customerId); // Ensure this method exists in the repository
    }
}
