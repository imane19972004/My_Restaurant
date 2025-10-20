package fr.unice.polytech.restaurants.stepsDefs.back;

import fr.unice.polytech.restaurants.Restaurant;
import io.cucumber.java.en.Given;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CommonSteps {
    private final ScenarioContext ctx;

    public CommonSteps(ScenarioContext ctx) { this.ctx = ctx; }

    @Given("a restaurant {string} exists")
    public void a_restaurant_exists(String restaurantName) {
        ctx.restaurant = new Restaurant(restaurantName);
    }

    @Given("I am logged in as restaurant manager of {string}")
    public void i_am_logged_in_as_restaurant_manager(String restaurantName) {
        assertNotNull(ctx.restaurant, "Restaurant should be initialized");
        assertEquals(restaurantName, ctx.restaurant.getRestaurantName());
        ctx.managerLoggedIn = true;
    }
}

