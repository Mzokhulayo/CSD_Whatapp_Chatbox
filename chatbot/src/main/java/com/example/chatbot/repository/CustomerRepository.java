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

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Customer customer) {
        String sql = "INSERT INTO customers (id, phone_number, name, surname, email, address, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (phone_number) DO UPDATE SET name = ?, surname = ?, email = ?, address = ?";
        jdbcTemplate.update(sql, customer.getId(), customer.getPhoneNumber(), customer.getName(),
                customer.getSurname(), customer.getEmail(), customer.getAddress(), customer.getCreatedAt(),
                customer.getName(), customer.getSurname(), customer.getEmail(), customer.getAddress());
    }

    public Optional<Customer> findByPhoneNumber(String phoneNumber) {
        String sql = "SELECT * FROM customers WHERE phone_number = ?";
        return jdbcTemplate.query(sql, new Object[]{phoneNumber}, new CustomerRowMapper()).stream().findFirst();
    }

    private static class CustomerRowMapper implements RowMapper<Customer> {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Customer customer = new Customer();
            customer.setId((UUID) rs.getObject("id")); // Map UUID
            customer.setPhoneNumber(rs.getString("phone_number"));
            customer.setName(rs.getString("name"));
            customer.setSurname(rs.getString("surname"));
            customer.setEmail(rs.getString("email"));
            customer.setAddress(rs.getString("address"));
            customer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return customer;
        }
    }
}
