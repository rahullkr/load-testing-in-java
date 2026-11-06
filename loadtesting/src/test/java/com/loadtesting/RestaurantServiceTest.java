package com.loadtesting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant(
                1L,
                "Spice Garden",
                "Pune",
                "Indian",
                4.5,
                "9876543210",
                true
        );
    }

    @Test
    void getRestaurantByIdReturnsRestaurantWhenFound() {
        when(restaurantRepository.findById(1L)).thenReturn(restaurant);

        Restaurant result = restaurantService.getRestaurantById(1L);

        assertNotNull(result);
        assertEquals("Spice Garden", result.getName());
        verify(restaurantRepository).findById(1L);
    }

    @Test
    void getRestaurantByIdReturnsNullWhenNotFound() {
        when(restaurantRepository.findById(99L)).thenReturn(null);

        Restaurant result = restaurantService.getRestaurantById(99L);

        assertNull(result);
        verify(restaurantRepository).findById(99L);
    }

    @Test
    void createRestaurantDelegatesToRepository() {
        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);

        Restaurant result = restaurantService.createRestaurant(restaurant);

        assertSame(restaurant, result);
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void updateRestaurantReturnsUpdatedRestaurantWhenRowExists() {
        when(restaurantRepository.update(1L, restaurant)).thenReturn(1);

        Restaurant result = restaurantService.updateRestaurant(1L, restaurant);

        assertSame(restaurant, result);
        verify(restaurantRepository).update(1L, restaurant);
        verify(restaurantRepository, never()).findById(anyLong());
    }

    @Test
    void updateRestaurantReturnsNullWhenNoRowWasUpdated() {
        when(restaurantRepository.update(99L, restaurant)).thenReturn(0);

        Restaurant result = restaurantService.updateRestaurant(99L, restaurant);

        assertNull(result);
        verify(restaurantRepository).update(99L, restaurant);
    }

    @Test
    void deleteRestaurantReturnsTrueWhenRowWasDeleted() {
        when(restaurantRepository.deleteById(1L)).thenReturn(1);

        assertTrue(restaurantService.deleteRestaurant(1L));

        verify(restaurantRepository).deleteById(1L);
        verify(restaurantRepository, never()).findById(anyLong());
    }

    @Test
    void deleteRestaurantReturnsFalseWhenNoRowWasDeleted() {
        when(restaurantRepository.deleteById(99L)).thenReturn(0);

        assertFalse(restaurantService.deleteRestaurant(99L));

        verify(restaurantRepository).deleteById(99L);
    }

    @Test
    void searchByNameReturnsMatchingRestaurants() {
        when(restaurantRepository.findByName("Spice")).thenReturn(List.of(restaurant));

        List<Restaurant> results = restaurantService.searchByName("Spice");

        assertEquals(1, results.size());
        assertEquals("Spice Garden", results.get(0).getName());
        verify(restaurantRepository).findByName("Spice");
    }

    @Test
    void getAllRestaurantsReturnsPageMetadataAndRequestsCorrectOffset() {
        when(restaurantRepository.countAll()).thenReturn(45L);
        when(restaurantRepository.findAll(20, 20L)).thenReturn(List.of(restaurant));

        PageResponse<Restaurant> result = restaurantService.getAllRestaurants(1, 20);

        assertEquals(1, result.page());
        assertEquals(20, result.size());
        assertEquals(45L, result.totalElements());
        assertEquals(3, result.totalPages());
        assertFalse(result.first());
        assertFalse(result.last());
        assertEquals(1, result.content().size());

        verify(restaurantRepository).findAll(20, 20L);
    }
}
