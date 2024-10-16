package com.example.chatbot.repository;

import com.example.chatbot.model.Otp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OtpRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OtpRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveOtp(Otp otp) {
        String sql = "INSERT INTO otp (customer_id, otp, expiration_time) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, otp.getCustomerId(), otp.getOtp(), otp.getExpirationTime());
    }

    public Optional<Otp> findByCustomerId(UUID customerId) {
        String sql = "SELECT * FROM otp WHERE customer_id = ?";
        return jdbcTemplate.query(sql, new Object[]{customerId}, new OtpRowMapper()).stream().findFirst();
    }

    private static class OtpRowMapper implements RowMapper<Otp> {
        @Override
        public Otp mapRow(ResultSet rs, int rowNum) throws SQLException {
            Otp otp = new Otp();
            otp.setCustomerId((UUID) rs.getObject("customer_id")); // Map customer ID
            otp.setOtp(rs.getString("otp"));
            otp.setExpirationTime(rs.getTimestamp("expiration_time").toLocalDateTime());
            return otp;
        }
    }
}


