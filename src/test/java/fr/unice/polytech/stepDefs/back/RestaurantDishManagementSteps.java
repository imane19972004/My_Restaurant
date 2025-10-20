package fr.unice.polytech.stepDefs.back;

import fr.unice.polytech.restaurants.ScenarioContext;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.datatable.DataTable;

import static org.junit.jupiter.api.Assertions.*;

import fr.unice.polytech.dishes.*;

import java.util.*;

public class RestaurantDishManagementSteps {
    private final ScenarioContext ctx;
    public RestaurantDishManagementSteps(ScenarioContext ctx) { this.ctx = ctx; }

    private Dish currentDish;
    private final List<String> currentDishTags = new ArrayList<>();
    private String currentAllergenInfo;

    // ============ SCENARIO 1: Add a new dish ============

    @When("I add a new dish with the following details:")
    public void i_add_a_new_dish_with_details(DataTable dataTable) {
        Map<String, String> dishData = dataTable.asMap(String.class, String.class);

        String name = dishData.get("name");
        String description = dishData.get("description");
        double price = Double.parseDouble(dishData.get("price"));

        currentDish = new Dish(name, description, price);

        if (dishData.containsKey("category")) {
            String category = dishData.get("category").replace(" ", "_").toUpperCase();
            currentDish.setCategory(DishCategory.valueOf(category));
        }

        ctx.restaurant.addDish(currentDish);
    }

    @Then("the dish {string} should be added to the menu")
    public void the_dish_should_be_added_to_menu(String dishName) {
        Dish dish = ctx.restaurant.findDishByName(dishName);
        assertNotNull(dish, "Dish should be found in the menu");
        assertEquals(dishName, dish.getName());
    }

    @Then("the dish should have price {double} euros")
    public void the_dish_should_have_price_euros(double expectedPrice) {
        assertNotNull(currentDish, "Current dish should not be null");
        assertEquals(expectedPrice, currentDish.getPrice(), 0.01);
    }

    // ============ SCENARIO 2: Dietary tags ============

    @When("I tag the dish as {string} and {string}")
    public void i_tag_the_dish_as(String tag1, String tag2) {
        assertNotNull(currentDish, "Current dish should not be null");
        currentDishTags.clear();
        currentDishTags.add(tag1);
        currentDishTags.add(tag2);
    }

    @Then("the dish {string} should have tag {string}")
    public void the_dish_should_have_tag(String dishName, String expectedTag) {
        Dish dish = ctx.restaurant.findDishByName(dishName);
        assertNotNull(dish, "Dish should exist");
        assertTrue(currentDishTags.contains(expectedTag),
                "Tag '" + expectedTag + "' should be present");
    }

    // ============ SCENARIO 3: Toppings ============

    @io.cucumber.java.en.Given("a dish {string} exists with price {double}")
    public void a_dish_exists_with_price(String dishName, double price) {
        currentDish = new Dish(dishName, "Test dish", price);
        ctx.restaurant.addDish(currentDish);
    }

    @When("I add a topping {string} with price {double}")
    public void i_add_a_topping_with_price(String toppingName, double price) {
        assertNotNull(currentDish, "Current dish should not be null");
        Topping topping = new Topping(toppingName, price);
        currentDish.addTopping(topping);
    }

    @Then("the dish should have {int} toppings available")
    public void the_dish_should_have_toppings_available(int expectedCount) {
        assertNotNull(currentDish, "Current dish should not be null");
        assertEquals(expectedCount, currentDish.getToppings().size());
    }

    @Then("topping {string} should cost {double} euros")
    public void topping_should_cost_euros(String toppingName, double expectedPrice) {
        assertNotNull(currentDish, "Current dish should not be null");
        Topping topping = currentDish.getToppings().stream()
                .filter(t -> t.getName().equals(toppingName))
                .findFirst()
                .orElse(null);
        assertNotNull(topping, "Topping '" + toppingName + "' should exist");
        assertEquals(expectedPrice, topping.getPrice(), 0.01);
    }

    // ============ SCENARIO 4: Update dish ============

    @When("I update the dish price to {double}")
    public void i_update_the_dish_price_to(double newPrice) {
        assertNotNull(currentDish, "Current dish should not be null");
        currentDish.setPrice(newPrice);
    }

    @When("I update the description to {string}")
    public void i_update_the_description_to(String newDescription) {
        assertNotNull(currentDish, "Current dish should not be null");
        currentDish.setDescription(newDescription);
    }

    @Then("the dish {string} should have price {double}")
    public void the_dish_should_have_price(String dishName, double expectedPrice) {
        Dish dish = ctx.restaurant.findDishByName(dishName);
        assertNotNull(dish, "Dish should be found");
        assertEquals(expectedPrice, dish.getPrice(), 0.01);
    }

    @Then("the dish description should be {string}")
    public void the_dish_description_should_be(String expectedDescription) {
        assertNotNull(currentDish, "Current dish should not be null");
        assertEquals(expectedDescription, currentDish.getDescription());
    }

    // ============ SCENARIO 5: Remove dish ============

    @io.cucumber.java.en.Given("a dish {string} exists in the menu")
    public void a_dish_exists_in_the_menu(String dishName) {
        Dish dish = new Dish(dishName, "Test dish", 10.00);
        ctx.restaurant.addDish(dish);
    }

    @When("I remove the dish {string} from the menu")
    public void i_remove_the_dish_from_menu(String dishName) {
        ctx.restaurant.removeDish(dishName);
    }

    @Then("the dish {string} should not be available")
    public void the_dish_should_not_be_available(String dishName) {
        Dish dish = ctx.restaurant.findDishByName(dishName);
        assertNull(dish, "Dish should not be found in menu");
    }

    @Then("customers should not see {string} in the menu")
    public void customers_should_not_see_in_menu(String dishName) {
        List<Dish> allDishes = ctx.restaurant.getDishes();
        boolean found = allDishes.stream().anyMatch(d -> d.getName().equals(dishName));
        assertFalse(found, "Dish should not be visible in menu");
    }

    // ============ SCENARIO 6: Extra options (fake storage) ============

    private final Map<String, Double> extraOptions = new HashMap<>();

    @When("I define an extra option {string} with price {double}")
    public void i_define_an_extra_option_with_price(String extraName, double price) {
        extraOptions.put(extraName, price);
    }

    @Then("the restaurant should have {int} extra options available")
    public void the_restaurant_should_have_extra_options_available(int expectedCount) {
        assertEquals(expectedCount, extraOptions.size());
    }

    @Then("option {string} should cost {double} euros")
    public void option_should_cost_euros(String optionName, double expectedPrice) {
        assertTrue(extraOptions.containsKey(optionName),
                "Extra option '" + optionName + "' should exist");
        assertEquals(expectedPrice, extraOptions.get(optionName), 0.01);
    }

    // ============ SCENARIO 7: Allergen information ============

    @When("I add allergen information {string}")
    public void i_add_allergen_information(String allergenInfo) {
        assertNotNull(currentDish, "Current dish should not be null");
        currentAllergenInfo = allergenInfo;
    }

    @Then("the dish should display allergen warning")
    public void the_dish_should_display_allergen_warning() {
        assertNotNull(currentDish, "Current dish should not be null");
        assertNotNull(currentAllergenInfo, "Allergen information should be set");
    }

    @Then("the warning should mention {string}")
    public void the_warning_should_mention(String allergen) {
        assertNotNull(currentAllergenInfo, "Allergen information should exist");
        assertTrue(currentAllergenInfo.toLowerCase().contains(allergen.toLowerCase()),
                "Allergen warning should mention '" + allergen + "'");
    }
}
