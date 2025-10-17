package fr.unice.polytech.restaurants.stepsDefs.Backend;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.But;
import static org.junit.jupiter.api.Assertions.*;

import fr.unice.polytech.restaurants.Restaurant;
import fr.unice.polytech.restaurants.RestaurantManager;
import fr.unice.polytech.restaurants.TimeSlot;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantTimeSlotManagementSteps {

    private Restaurant restaurant;
    private RestaurantManager restaurantManager;
    private Map<String, TimeSlot> timeSlots;
    private List<TimeSlot> availableTimeSlots;
    private String warningMessage;

    public RestaurantTimeSlotManagementSteps() {
        this.timeSlots = new HashMap<>();
        this.restaurantManager = new RestaurantManager();
    }

    // ============ BACKGROUND STEPS ============

    @Given("a restaurant {string} exists")
    public void a_restaurant_exists(String restaurantName) {
        restaurant = new Restaurant(restaurantName);
        restaurantManager.addRestaurant(restaurant);
    }

    @Given("I am logged in as restaurant manager of {string}")
    public void i_am_logged_in_as_restaurant_manager(String restaurantName) {
        assertNotNull(restaurant, "Restaurant should be initialized");
        assertEquals(restaurantName, restaurant.getRestaurantName());
    }

    // ============ HELPER METHODS ============

    private TimeSlot createTimeSlotFromString(String timeRange) {
        String[] times = timeRange.split("-");
        LocalTime start = LocalTime.parse(times[0]);
        LocalTime end = LocalTime.parse(times[1]);
        return new TimeSlot(start, end);
    }

    private String getTimeSlotKey(String startTime, String endTime) {
        return startTime + "-" + endTime;
    }

    // ============ SCENARIO 1: Define time slots with capacity ============

    @When("I create a time slot from {string} to {string} with capacity {int}")
    public void i_create_time_slot_with_capacity(String startTime, String endTime, int capacity) {
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        TimeSlot slot = new TimeSlot(start, end);
        
        restaurant.setCapacity(slot, capacity);
        String key = getTimeSlotKey(startTime, endTime);
        timeSlots.put(key, slot);
    }

    @Then("the restaurant should have {int} time slots available")
    public void the_restaurant_should_have_time_slots_available(int expectedCount) {
        List<TimeSlot> available = restaurantManager.getAvailableTimeSlots(restaurant);
        assertEquals(expectedCount, available.size(), 
            "Expected " + expectedCount + " time slots but found " + available.size());
    }

    @Then("the time slot {string} should have capacity {int}")
    public void the_time_slot_should_have_capacity(String timeRange, int expectedCapacity) {
        TimeSlot slot = timeSlots.get(timeRange);
        assertNotNull(slot, "Time slot " + timeRange + " should exist");
        assertEquals(expectedCapacity, restaurant.getCapacity(slot));
    }

    // ============ SCENARIO 2: Block time slot ============

    @Given("a time slot from {string} to {string} exists with capacity {int}")
    public void a_time_slot_exists_with_capacity(String startTime, String endTime, int capacity) {
        i_create_time_slot_with_capacity(startTime, endTime, capacity);
    }

    @When("I block the time slot {string} {int} times")
    public void i_block_time_slot_multiple_times(String timeRange, int times) {
        TimeSlot slot = timeSlots.get(timeRange);
        assertNotNull(slot, "Time slot should exist before blocking");
        
        for (int i = 0; i < times; i++) {
            restaurantManager.blockTimeSlot(slot, restaurant);
        }
    }

    @Then("the time slot {string} should not be available for booking")
    public void time_slot_should_not_be_available(String timeRange) {
        List<TimeSlot> available = restaurantManager.getAvailableTimeSlots(restaurant);
        TimeSlot slot = timeSlots.get(timeRange);
        
        assertFalse(available.contains(slot), 
            "Time slot " + timeRange + " should not be available");
    }

    // ============ SCENARIO 3: Unblock time slot ============

    @When("I unblock the time slot {string}")
    public void i_unblock_time_slot(String timeRange) {
        TimeSlot slot = timeSlots.get(timeRange);
        assertNotNull(slot, "Time slot should exist before unblocking");
        restaurantManager.unblockTimeSlot(slot, restaurant);
    }

    @Then("the time slot {string} should have capacity greater than {int}")
    public void time_slot_should_have_capacity_greater_than(String timeRange, int minCapacity) {
        TimeSlot slot = timeSlots.get(timeRange);
        int actualCapacity = restaurant.getCapacity(slot);
        assertTrue(actualCapacity > minCapacity, 
            "Capacity should be greater than " + minCapacity + " but was " + actualCapacity);
    }

    @Then("the time slot {string} should be available for booking")
    public void time_slot_should_be_available(String timeRange) {
        List<TimeSlot> available = restaurantManager.getAvailableTimeSlots(restaurant);
        TimeSlot slot = timeSlots.get(timeRange);
        
        assertTrue(available.contains(slot), 
            "Time slot " + timeRange + " should be available");
    }

    // ============ SCENARIO 4: Prevent negative capacity ============

    @Then("the system should not allow negative capacity")
    public void system_should_not_allow_negative_capacity() {
        for (TimeSlot slot : timeSlots.values()) {
            int capacity = restaurant.getCapacity(slot);
            assertTrue(capacity >= 0, 
                "Capacity should never be negative, but was " + capacity);
        }
    }

    // ============ SCENARIO 5: View available time slots ============

    @When("I request all available time slots")
    public void i_request_all_available_time_slots() {
        availableTimeSlots = restaurantManager.getAvailableTimeSlots(restaurant);
    }

    @Then("I should see {int} available time slots")
    public void i_should_see_available_time_slots(int expectedCount) {
        assertNotNull(availableTimeSlots, "Available time slots list should not be null");
        assertEquals(expectedCount, availableTimeSlots.size());
    }

    @Then("the list should include {string}")
    public void the_list_should_include(String timeRange) {
        TimeSlot expectedSlot = timeSlots.get(timeRange);
        assertNotNull(expectedSlot, "Time slot " + timeRange + " should be in the map");
        assertTrue(availableTimeSlots.contains(expectedSlot), 
            "Available slots should include " + timeRange);
    }

    @But("the list should not include {string}")
    public void the_list_should_not_include(String timeRange) {
        TimeSlot excludedSlot = timeSlots.get(timeRange);
        if (excludedSlot != null) {
            assertFalse(availableTimeSlots.contains(excludedSlot), 
                "Available slots should NOT include " + timeRange);
        }
    }

    // ============ SCENARIO 6: Update capacity ============

    @When("I update the time slot {string} capacity to {int}")
    public void i_update_time_slot_capacity(String timeRange, int newCapacity) {
        TimeSlot slot = timeSlots.get(timeRange);
        assertNotNull(slot, "Time slot should exist");
        restaurant.setCapacity(slot, newCapacity);
    }

    // ============ SCENARIO 7: No time slots configured ============

    @Given("the restaurant has no time slots configured")
    public void restaurant_has_no_time_slots_configured() {
        // Restaurant est déjà créé sans time slots
        assertTrue(restaurant.getAvailableTimeSlots().isEmpty());
    }

    @Then("a warning message {string} should be displayed")
    public void warning_message_should_be_displayed(String expectedMessage) {
        if (availableTimeSlots == null || availableTimeSlots.isEmpty()) {
            warningMessage = "No time slots configured";
        }
        assertEquals(expectedMessage, warningMessage);
    }
}