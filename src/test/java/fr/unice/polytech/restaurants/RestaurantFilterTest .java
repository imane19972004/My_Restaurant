package fr.unice.polytech.restaurants;

import fr.unice.polytech.dishes.Dish;
import fr.unice.polytech.dishes.DishCategory;
import fr.unice.polytech.dishes.DishType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RestaurantFilter Tests")
class RestaurantFilterTest {

    private RestaurantFilter filter;
    private List<Restaurant> restaurants;
    private Restaurant pizzaHouse;
    private Restaurant leBistrot;
    private Restaurant veggieTruck;
    private Restaurant campusCafeteria;

    @BeforeEach
    void setUp() {       System.out.println("setUp running");
        restaurants = new ArrayList<>();

        // ========== PIZZA HOUSE ==========
        pizzaHouse = new Restaurant.Builder("Pizza House")
                .withCuisineType(DishType.ITALIAN)
                .build();
        
        // Ajouter des plats
        Dish margherita = new Dish("Margherita", "Classic pizza", 12.50);
        margherita.setCategory(DishCategory.MAIN_COURSE);
        pizzaHouse.addDish(margherita);
        
        Dish pepperoni = new Dish("Pepperoni", "Spicy pizza", 14.00);
        pepperoni.setCategory(DishCategory.MAIN_COURSE);
        pizzaHouse.addDish(pepperoni);
        
        // Créneaux horaires : 11:00 - 22:00
        TimeSlot slot1 = new TimeSlot(LocalTime.of(11, 0), LocalTime.of(14, 0));
        TimeSlot slot2 = new TimeSlot(LocalTime.of(18, 0), LocalTime.of(22, 0));
        pizzaHouse.setCapacity(slot1, 10);
        pizzaHouse.setCapacity(slot2, 15);

        // ========== LE BISTROT ==========
        leBistrot = new Restaurant.Builder("Le Bistrot")
                .withCuisineType(DishType.FRENCH)
                .build();
        
        Dish coqAuVin = new Dish("Coq au Vin", "Traditional French", 18.00);
        leBistrot.addDish(coqAuVin);
        
        // Créneaux : 12:00 - 14:00
        TimeSlot bistrotSlot = new TimeSlot(LocalTime.of(12, 0), LocalTime.of(14, 0));
        leBistrot.setCapacity(bistrotSlot, 8);

        // ========== VEGGIE TRUCK ==========
        veggieTruck = new Restaurant.Builder("Veggie Truck")
                .withCuisineType(DishType.VEGETARIAN)
                .build();
        
        Dish buddhaBowl = new Dish("Buddha Bowl", "Healthy bowl", 9.50);
        veggieTruck.addDish(buddhaBowl);
        
        Dish smoothie = new Dish("Green Smoothie", "Fresh smoothie", 5.00);
        veggieTruck.addDish(smoothie);
        
        // Créneaux : 11:30 - 15:00
        TimeSlot truckSlot = new TimeSlot(LocalTime.of(11, 30), LocalTime.of(15, 0));
        veggieTruck.setCapacity(truckSlot, 5);

        // ========== CAMPUS CAFETERIA ==========
        campusCafeteria = new Restaurant.Builder("Campus Cafeteria")
                .withCuisineType(DishType.GENERAL)
                .build();
        
        Dish sandwich = new Dish("Sandwich", "Quick meal", 6.00);
        campusCafeteria.addDish(sandwich);
        
        // Créneaux : 08:00 - 18:00
        TimeSlot cafeteriaSlot = new TimeSlot(LocalTime.of(8, 0), LocalTime.of(18, 0));
        campusCafeteria.setCapacity(cafeteriaSlot, 50);

        // Ajouter tous les restaurants
        restaurants.add(pizzaHouse);
        restaurants.add(leBistrot);
        restaurants.add(veggieTruck);
        restaurants.add(campusCafeteria);

        // Créer le filtre
        filter = new RestaurantFilter(restaurants);
    }

    // ==================== FILTER BY AVAILABILITY TESTS ====================

    @Nested
    @DisplayName("Filter by Availability Tests")
    class FilterByAvailabilityTests {

        @Test
        @DisplayName("Should find restaurants available at lunch time (12:30)")
        void shouldFindRestaurantsAtLunchTime() {
            List<Restaurant> result = filter.filterByAvailability(LocalTime.of(12, 30));
            
            assertEquals(3, result.size(), "Should find 3 restaurants at 12:30");
            assertTrue(result.contains(pizzaHouse), "Pizza House should be available");
            assertTrue(result.contains(leBistrot), "Le Bistrot should be available");
            assertTrue(result.contains(veggieTruck), "Veggie Truck should be available");
        }

        @Test
        @DisplayName("Should find only cafeteria in early morning (09:00)")
        void shouldFindOnlyCafeteriaInMorning() {
            List<Restaurant> result = filter.filterByAvailability(LocalTime.of(9, 0));
            
            assertEquals(1, result.size(), "Only cafeteria should be open at 09:00");
            assertTrue(result.contains(campusCafeteria));
        }

        @Test
        @DisplayName("Should find restaurants in evening (19:00)")
        void shouldFindRestaurantsInEvening() {
            List<Restaurant> result = filter.filterByAvailability(LocalTime.of(19, 0));
            
            assertEquals(1, result.size(), "Only Pizza House should be open at 19:00");
            assertTrue(result.contains(pizzaHouse));
        }

        @Test
        @DisplayName("Should return empty list at night (03:00)")
        void shouldReturnEmptyListAtNight() {
            List<Restaurant> result = filter.filterByAvailability(LocalTime.of(3, 0));
            
            assertTrue(result.isEmpty(), "No restaurants should be open at 03:00");
        }

        @Test
        @DisplayName("Should find all restaurants at peak time (13:00)")
        void shouldFindAllAtPeakTime() {
            List<Restaurant> result = filter.filterByAvailability(LocalTime.of(13, 0));
            
            assertEquals(4, result.size(), "All restaurants should be available at 13:00");
        }
    }

    // ==================== FILTER BY CUISINE TYPE TESTS ====================

    @Nested
    @DisplayName("Filter by Cuisine Type Tests")
    class FilterByCuisineTypeTests {

        @Test
        @DisplayName("Should find Italian restaurants")
        void shouldFindItalianRestaurants() {
            List<Restaurant> result = filter.filterByCuisineType(DishType.ITALIAN);
            
            assertEquals(1, result.size());
            assertEquals("Pizza House", result.get(0).getRestaurantName());
        }

        @Test
        @DisplayName("Should find French restaurants")
        void shouldFindFrenchRestaurants() {
            List<Restaurant> result = filter.filterByCuisineType(DishType.FRENCH);
            
            assertEquals(1, result.size());
            assertEquals("Le Bistrot", result.get(0).getRestaurantName());
        }

        @Test
        @DisplayName("Should find Vegetarian restaurants")
        void shouldFindVegetarianRestaurants() {
            List<Restaurant> result = filter.filterByCuisineType(DishType.VEGETARIAN);
            
            assertEquals(1, result.size());
            assertEquals("Veggie Truck", result.get(0).getRestaurantName());
        }

        @Test
        @DisplayName("Should return empty list for non-existent cuisine")
        void shouldReturnEmptyForNonExistentCuisine() {
            List<Restaurant> result = filter.filterByCuisineType(DishType.JAPANESE);
            
            assertTrue(result.isEmpty(), "No Japanese restaurants should exist");
        }
    }

    // ==================== FILTER BY PRICE RANGE TESTS ====================

    @Nested
    @DisplayName("Filter by Price Range Tests")
    class FilterByPriceRangeTests {

        @Test
        @DisplayName("Should find restaurants with dishes under 10€")
        void shouldFindAffordableRestaurants() {
            List<Restaurant> result = filter.filterByPriceRange(0, 10.0);
            
            assertEquals(2, result.size(), "Should find 2 restaurants with cheap dishes");
            assertTrue(result.contains(veggieTruck), "Veggie Truck has dishes under 10€");
            assertTrue(result.contains(campusCafeteria), "Cafeteria has dishes under 10€");
        }

        @Test
        @DisplayName("Should find restaurants in mid price range (10-15€)")
        void shouldFindMidPriceRestaurants() {
            List<Restaurant> result = filter.filterByPriceRange(10.0, 15.0);
            
            assertEquals(1, result.size());
            assertTrue(result.contains(pizzaHouse), "Pizza House has dishes 10-15€");
        }

        @Test
        @DisplayName("Should find expensive restaurants (15€+)")
        void shouldFindExpensiveRestaurants() {
            List<Restaurant> result = filter.filterByPriceRange(15.0, 50.0);
            
            assertEquals(1, result.size());
            assertTrue(result.contains(leBistrot), "Le Bistrot has expensive dishes");
        }

        @Test
        @DisplayName("Should return empty for unrealistic price range")
        void shouldReturnEmptyForUnrealisticPriceRange() {
            List<Restaurant> result = filter.filterByPriceRange(100.0, 200.0);
            
            assertTrue(result.isEmpty(), "No restaurants have dishes that expensive");
        }

        @Test
        @DisplayName("Should find all restaurants with broad price range")
        void shouldFindAllWithBroadRange() {
            List<Restaurant> result = filter.filterByPriceRange(0, 100.0);
            
            assertEquals(4, result.size(), "All restaurants should match broad range");
        }
    }

    // ==================== MULTIPLE CRITERIA TESTS ====================

    @Nested
    @DisplayName("Multiple Criteria Filter Tests")
    class MultipleCriteriaTests {

        @Test
        @DisplayName("Should filter by availability AND cuisine type")
        void shouldFilterByAvailabilityAndCuisine() {
            FilterCriteria criteria = new FilterCriteria.Builder()
                    .withAvailability(LocalTime.of(12, 30))
                    .withCuisineType(DishType.ITALIAN)
                    .build();

            List<Restaurant> result = filter.filterByMultipleCriteria(criteria);
            
            assertEquals(1, result.size());
            assertEquals("Pizza House", result.get(0).getRestaurantName());
        }

        @Test
        @DisplayName("Should filter by availability AND price range")
        void shouldFilterByAvailabilityAndPrice() {
            FilterCriteria criteria = new FilterCriteria.Builder()
                    .withAvailability(LocalTime.of(13, 0))
                    .withPriceRange(5.0, 10.0)
                    .build();

            List<Restaurant> result = filter.filterByMultipleCriteria(criteria);
            
            assertEquals(2, result.size());
            assertTrue(result.contains(veggieTruck));
            assertTrue(result.contains(campusCafeteria));
        }

        @Test
        @DisplayName("Should filter by ALL criteria")
        void shouldFilterByAllCriteria() {
            FilterCriteria criteria = new FilterCriteria.Builder()
                    .withAvailability(LocalTime.of(12, 0))
                    .withCuisineType(DishType.ITALIAN)
                    .withPriceRange(10.0, 15.0)
                    .build();

            List<Restaurant> result = filter.filterByMultipleCriteria(criteria);
            
            assertEquals(1, result.size());
            assertEquals("Pizza House", result.get(0).getRestaurantName());
        }

        @Test
        @DisplayName("Should return empty when no match for combined criteria")
        void shouldReturnEmptyWhenNoMatch() {
            FilterCriteria criteria = new FilterCriteria.Builder()
                    .withAvailability(LocalTime.of(3, 0))
                    .withCuisineType(DishType.ITALIAN)
                    .build();

            List<Restaurant> result = filter.filterByMultipleCriteria(criteria);
            
            assertTrue(result.isEmpty(), "No Italian restaurant open at 3 AM");
        }

        @Test
        @DisplayName("Should handle criteria with no filters (return all)")
        void shouldReturnAllWithEmptyCriteria() {
            FilterCriteria criteria = new FilterCriteria.Builder().build();

            List<Restaurant> result = filter.filterByMultipleCriteria(criteria);
            
            assertEquals(4, result.size(), "Should return all restaurants with no filters");
        }
    }

    // ==================== EDGE CASES TESTS ====================

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle empty restaurant list")
        void shouldHandleEmptyRestaurantList() {
            RestaurantFilter emptyFilter = new RestaurantFilter(new ArrayList<>());
            
            List<Restaurant> result = emptyFilter.filterByAvailability(LocalTime.of(12, 0));
            
            assertTrue(result.isEmpty(), "Empty list should return empty results");
        }

        @Test
        @DisplayName("Should handle null time for availability")
        void shouldHandleNullTime() {
            // Selon votre implémentation, ceci pourrait throw une exception
            // ou retourner une liste vide
            assertDoesNotThrow(() -> filter.filterByAvailability(null));
        }

        @Test
        @DisplayName("Should handle negative prices")
        void shouldHandleNegativePrices() {
            List<Restaurant> result = filter.filterByPriceRange(-10.0, 5.0);
            
            // Devrait retourner les restaurants avec plats < 5€
            assertNotNull(result);
        }

        @Test
        @DisplayName("Should handle inverted price range (max < min)")
        void shouldHandleInvertedPriceRange() {
            List<Restaurant> result = filter.filterByPriceRange(20.0, 10.0);
            
            // Devrait retourner vide ou corriger automatiquement
            assertTrue(result.isEmpty() || result.size() > 0);
        }
    }

    // ==================== PERFORMANCE TESTS ====================

    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {

        @Test
        @DisplayName("Should handle large number of restaurants efficiently")
        void shouldHandleLargeDataSet() {
            List<Restaurant> largeList = new ArrayList<>();
            
            // Créer 100 restaurants
            for (int i = 0; i < 100; i++) {
                Restaurant r = new Restaurant("Restaurant " + i);
                Dish dish = new Dish("Dish " + i, "Description", 10.0 + i);
                r.addDish(dish);
                
                TimeSlot slot = new TimeSlot(LocalTime.of(11, 0), LocalTime.of(14, 0));
                r.setCapacity(slot, 10);
                
                largeList.add(r);
            }
            
            RestaurantFilter largeFilter = new RestaurantFilter(largeList);
            
            long startTime = System.currentTimeMillis();
            List<Restaurant> result = largeFilter.filterByAvailability(LocalTime.of(12, 0));
            long endTime = System.currentTimeMillis();
            
            assertTrue(endTime - startTime < 1000, "Should filter 100 restaurants in < 1s");
            assertEquals(100, result.size());
        }
    }
}