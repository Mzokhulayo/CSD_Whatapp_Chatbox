package com.example.chatbot.repository;

import com.example.chatbot.model.RegistrationState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RegistrationStateRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RegistrationStateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(RegistrationState registrationState) {
        String sql = "INSERT INTO registration_state (customer_id, otp_verified, step) " +
                "VALUES (?, ?, ?) " +
                "ON CONFLICT (customer_id) " +
                "DO UPDATE SET otp_verified = EXCLUDED.otp_verified, step = EXCLUDED.step";

        jdbcTemplate.update(sql, registrationState.getCustomerId(), registrationState.isOtpVerified(), registrationState.getStep());
    }

    public Optional<RegistrationState> findByCustomerId(UUID customerId) {
        String sql = "SELECT * FROM registration_state WHERE customer_id = ?";
        return jdbcTemplate.query(sql, new Object[]{customerId}, new RegistrationStateRowMapper()).stream().findFirst();
    }

    private static class RegistrationStateRowMapper implements RowMapper<RegistrationState> {
        @Override
        public RegistrationState mapRow(ResultSet rs, int rowNum) throws SQLException {
            RegistrationState state = new RegistrationState();
            state.setCustomerId((UUID) rs.getObject("customer_id")); // Map customer ID
            state.setOtpVerified(rs.getBoolean("otp_verified"));
            state.setStep(rs.getString("step"));
            return state;
        }
    }

    // Delete registration state by customer ID
    public void deleteByCustomerId(UUID customerId) {
        String sql = "DELETE FROM registration_state WHERE customer_id = ?";
        jdbcTemplate.update(sql, customerId);
    }
}

