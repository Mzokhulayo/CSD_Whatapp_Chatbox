package com.example.chatbot.repository;

import com.example.chatbot.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CustomerRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Customer save(Customer customer) {
        String sql = "INSERT INTO customers (id, name, phone_number, email, created_at) VALUES (?, ?, ?, ?, ?)";
        UUID customerId = UUID.randomUUID();
        jdbcTemplate.update(sql, customerId, customer.getName(), customer.getPhoneNumber(), customer.getEmail(), customer.getCreatedAt());
        customer.setId(customerId);
        return customer;
    }

    public Optional<Customer> findByPhoneNumber(String phoneNumber) {
        String sql = "SELECT * FROM customers WHERE phone_number = ?";
        return jdbcTemplate.query(sql, new Object[]{phoneNumber}, new CustomerRowMapper())
                .stream().findFirst();
    }

    private static class CustomerRowMapper implements RowMapper<Customer> {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Customer customer = new Customer();
            customer.setId(UUID.fromString(rs.getString("id")));
            customer.setName(rs.getString("name"));
            customer.setPhoneNumber(rs.getString("phone_number"));
            customer.setEmail(rs.getString("email"));
            customer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return customer;
        }
    }
}
