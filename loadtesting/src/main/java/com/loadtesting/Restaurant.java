package com.loadtesting;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.sql.Timestamp;

public class Restaurant {
    private Long id;

    @NotBlank(message = "Restaurant name is required")
    @Size(max = 255, message = "Restaurant name must be at most 255 characters")
    private String name;

    @Size(max = 500, message = "Address must be at most 500 characters")
    private String address;

    @Size(max = 100, message = "Cuisine type must be at most 100 characters")
    private String cuisineType;

    @DecimalMin(value = "0.0", message = "Rating cannot be below 0")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5")
    private Double rating;

    @Size(max = 20, message = "Phone must be at most 20 characters")
    private String phone;

    private Boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Restaurant() {}

    public Restaurant(Long id, String name, String address, String cuisineType, Double rating, String phone, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.cuisineType = cuisineType;
        this.rating = rating;
        this.phone = phone;
        this.isActive = isActive;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCuisineType() { return cuisineType; }
    public void setCuisineType(String cuisineType) { this.cuisineType = cuisineType; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
