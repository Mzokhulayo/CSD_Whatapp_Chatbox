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



//package com.example.chatbot.repository;
//
//import com.example.chatbot.model.Otp;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.stereotype.Repository;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.Optional;
//import java.util.UUID;
//
//@Repository
//public class OtpRepository {
//
//    private final JdbcTemplate jdbcTemplate;
//
//    @Autowired
//    public OtpRepository(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    public void saveOtp(Otp otp) {
//        String sql = "INSERT INTO otp (customer_id, otp, expiration_time) VALUES (?, ?, ?)";
//        jdbcTemplate.update(sql, otp.getCustomerId(), otp.getOtp(), otp.getExpirationTime());
//    }
//
//    public Optional<Otp> findByCustomerId(UUID customerId) {
//        String sql = "SELECT * FROM otp WHERE customer_id = ?";
//        return jdbcTemplate.query(sql, new Object[]{customerId}, new OtpRowMapper()).stream().findFirst();
//    }
//
//    private static class OtpRowMapper implements RowMapper<Otp> {
//        @Override
//        public Otp mapRow(ResultSet rs, int rowNum) throws SQLException {
//            Otp otp = new Otp();
//            otp.setCustomerId((UUID) rs.getObject("customer_id")); // Map customer ID
//            otp.setOtp(rs.getString("otp"));
//            otp.setExpirationTime(rs.getTimestamp("expiration_time").toLocalDateTime());
//            return otp;
//        }
//    }
//}
//
//
//
////package com.example.chatbot.repository;
////
////import com.example.chatbot.model.Otp;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.jdbc.core.JdbcTemplate;
////import org.springframework.jdbc.core.RowMapper;
////import org.springframework.stereotype.Repository;
////import java.sql.ResultSet;
////import java.sql.SQLException;
////import java.util.Optional;
////import java.util.UUID;
////
////@Repository
////public class OtpRepository {
////
////    JdbcTemplate jdbcTemplate;
////    CustomerRepository customerRepository;
////
////    @Autowired
////    public OtpRepository(JdbcTemplate jdbcTemplate, CustomerRepository customerRepository) {
////        this.jdbcTemplate = jdbcTemplate;
////        this.customerRepository = customerRepository;
////    }
////
////    public void saveOtp(Otp otp) {
////        String sql = "insert into otp (customer_id, otp, expiration_time,phone_number) values (?, ?, ?, ?)";
////        jdbcTemplate.update(sql, otp.getCustomerId(), otp.getOtp(), otp.getExpirationTime(), otp.getPhoneNumber());
////    }
////
////
////    public Optional<Otp> findByPhoneNumber(String phoneNumber) {
////        String sql = "SELECT * FROM otp WHERE phone_number = ?"; // Assuming phone_number is a column in the otp table
////        return jdbcTemplate.query(sql, new Object[]{phoneNumber}, new OtpRowMapper()).stream().findFirst();
////    }
////    private static class OtpRowMapper implements RowMapper<Otp> {
////        @Override
////        public Otp mapRow(ResultSet rs, int rowNum) throws SQLException {
////            Otp otp = new Otp();
////            otp.setPhoneNumber(rs.getString("phone_number")); // Mapping phone number
////            otp.setOtp(rs.getString("otp")); // Assuming the OTP column is named "otp"
////            otp.setExpirationTime(rs.getTimestamp("expiration_time").toLocalDateTime());
////            return otp;
////        }
////    }
////
////    // New method to find OTP by customer ID
////    public Optional<Otp> findByCustomerId(UUID customerId) {
////        String sql = "SELECT * FROM otp WHERE customer_id = ?";
////        return jdbcTemplate.query(sql, new Object[]{customerId}, new OtpRowMapper()).stream().findFirst();
////    }
////
////}
