package com.loadtesting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class RestaurantRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final RowMapper<Restaurant> rowMapper = (rs, rowNum) -> {
        Restaurant r = new Restaurant();
        r.setId(rs.getLong("id"));
        r.setName(rs.getString("name"));
        r.setAddress(rs.getString("address"));
        r.setCuisineType(rs.getString("cuisine_type"));
        r.setRating(rs.getDouble("rating"));
        r.setPhone(rs.getString("phone"));
        r.setIsActive(rs.getBoolean("is_active"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        r.setUpdatedAt(rs.getTimestamp("updated_at"));
        return r;
    };

    public List<Restaurant> findAll() {
        return jdbcTemplate.query("SELECT * FROM restaurant", rowMapper);
    }

    public Restaurant findById(Long id) {
        List<Restaurant> results = jdbcTemplate.query("SELECT * FROM restaurant WHERE id = ?", rowMapper, id);
        return results.isEmpty() ? null : results.get(0);
    }

    public Restaurant save(Restaurant restaurant) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO restaurant (name, address, cuisine_type, rating, phone, is_active) VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, restaurant.getName());
            ps.setString(2, restaurant.getAddress());
            ps.setString(3, restaurant.getCuisineType());
            ps.setDouble(4, restaurant.getRating());
            ps.setString(5, restaurant.getPhone());
            ps.setBoolean(6, restaurant.getIsActive());
            return ps;
        }, keyHolder);
        restaurant.setId(keyHolder.getKey().longValue());
        return restaurant;
    }

    public Restaurant update(Long id, Restaurant restaurant) {
        jdbcTemplate.update(
            "UPDATE restaurant SET name=?, address=?, cuisine_type=?, rating=?, phone=?, is_active=?, updated_at=? WHERE id=?",
            restaurant.getName(), restaurant.getAddress(), restaurant.getCuisineType(),
            restaurant.getRating(), restaurant.getPhone(), restaurant.getIsActive(),
            new Timestamp(System.currentTimeMillis()), id
        );
        restaurant.setId(id);
        return restaurant;
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM restaurant WHERE id = ?", id);
    }

    public List<Restaurant> findByName(String name) {
        return jdbcTemplate.query("SELECT * FROM restaurant WHERE name LIKE ?", rowMapper, "%" + name + "%");
    }
}
