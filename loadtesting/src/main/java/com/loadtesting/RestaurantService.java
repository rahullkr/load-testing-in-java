package com.loadtesting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id);
    }

    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public Restaurant updateRestaurant(Long id, Restaurant restaurant) {
        Restaurant existing = restaurantRepository.findById(id);
        if (existing == null) {
            return null;
        }
        return restaurantRepository.update(id, restaurant);
    }

    public boolean deleteRestaurant(Long id) {
        Restaurant existing = restaurantRepository.findById(id);
        if (existing == null) {
            return false;
        }
        restaurantRepository.deleteById(id);
        return true;
    }

    public List<Restaurant> searchByName(String name) {
        return restaurantRepository.findByName(name);
    }
}
