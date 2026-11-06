package com.loadtesting;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class RestaurantRepository {

    private final JdbcTemplate jdbcTemplate;

    public RestaurantRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Restaurant> rowMapper = (rs, rowNum) -> {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(rs.getLong("id"));
        restaurant.setName(rs.getString("name"));
        restaurant.setAddress(rs.getString("address"));
        restaurant.setCuisineType(rs.getString("cuisine_type"));
        restaurant.setRating(rs.getDouble("rating"));
        restaurant.setPhone(rs.getString("phone"));
        restaurant.setIsActive(rs.getBoolean("is_active"));
        restaurant.setCreatedAt(rs.getTimestamp("created_at"));
        restaurant.setUpdatedAt(rs.getTimestamp("updated_at"));
        return restaurant;
    };

    public List<Restaurant> findAll(int limit, int offset) {
        return jdbcTemplate.query(
                "SELECT * FROM restaurant ORDER BY id LIMIT ? OFFSET ?",
                rowMapper,
                limit,
                offset
        );
    }

    public long countAll() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM restaurant", Long.class);
        return count == null ? 0L : count;
    }

    public Restaurant findById(Long id) {
        List<Restaurant> results = jdbcTemplate.query(
                "SELECT * FROM restaurant WHERE id = ?",
                rowMapper,
                id
        );
        return results.isEmpty() ? null : results.get(0);
    }

    public Restaurant save(Restaurant restaurant) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO restaurant (name, address, cuisine_type, rating, phone, is_active) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, restaurant.getName());
            statement.setString(2, restaurant.getAddress());
            statement.setString(3, restaurant.getCuisineType());
            statement.setObject(4, restaurant.getRating());
            statement.setString(5, restaurant.getPhone());
            statement.setBoolean(6, Boolean.TRUE.equals(restaurant.getIsActive()));
            return statement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            restaurant.setId(generatedId.longValue());
        }
        return restaurant;
    }

    public int update(Long id, Restaurant restaurant) {
        return jdbcTemplate.update(
                "UPDATE restaurant SET name = ?, address = ?, cuisine_type = ?, rating = ?, phone = ?, is_active = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?",
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getCuisineType(),
                restaurant.getRating(),
                restaurant.getPhone(),
                Boolean.TRUE.equals(restaurant.getIsActive()),
                id
        );
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM restaurant WHERE id = ?", id);
    }

    public List<Restaurant> findByName(String name) {
        return jdbcTemplate.query(
                "SELECT * FROM restaurant WHERE name LIKE ?",
                rowMapper,
                "%" + name + "%"
        );
    }
}
