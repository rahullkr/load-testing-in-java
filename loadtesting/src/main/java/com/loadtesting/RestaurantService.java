package com.loadtesting;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public PageResponse<Restaurant> getAllRestaurants(int page, int size) {
        long totalElements = restaurantRepository.countAll();
        long offset = (long) page * size;
        List<Restaurant> content = restaurantRepository.findAll(size, offset);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new PageResponse<>(
                content,
                page,
                size,
                totalElements,
                totalPages,
                page == 0,
                page >= totalPages - 1
        );
    }

    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id);
    }

    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public Restaurant updateRestaurant(Long id, Restaurant restaurant) {
        int updatedRows = restaurantRepository.update(id, restaurant);
        return updatedRows == 0 ? null : restaurant;
    }

    public boolean deleteRestaurant(Long id) {
        return restaurantRepository.deleteById(id) > 0;
    }

    public List<Restaurant> searchByName(String name) {
        return restaurantRepository.findByName(name);
    }
}
