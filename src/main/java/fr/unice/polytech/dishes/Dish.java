package fr.unice.polytech.dishes;
import java.util.ArrayList;
import java.util.List;

public class Dish extends PriceableItem {
    private String description;
    private DishType cuisineType = DishType.GENERAL;

    public Dish(String name, double price) {
        super(name, price);
    }

    public Dish(String name, double price, String description) {
        super(name, price);
        this.description = description;
    }

    public DishType getCuisineType() {
        return cuisineType;
    }

    private DishCategory category; 
    private List<Topping> toppings;


    public Dish(String name, String description, double price) {
        super(name, price);
        this.description = description;
        this.toppings = new ArrayList<>();
    }


    public String getDescription() {
        return description;
    }

    public DishCategory getCategory() {
        return category;
    }

    public List<Topping> getToppings() {
        return toppings;
    }
    
    // Setter to allow category modification
    public void setCategory(DishCategory category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Single Update Method (for administrative bulk updates)
    public void updateDetails(String newName, String newDescription, double newPrice) {
        this.setName(newName);
        this.setDescription(newDescription);
        this.setPrice(newPrice);
    }

    public void addTopping(Topping topping) {
        this.toppings.add(topping);
    }


    @Override
    public String toString() {
        String categoryStr = (category == null) ? "N/A" : category.toString();
        return "Dish [name=" + getName() + ", price=" + getPrice() + ", category=" + categoryStr + "]";
    }


}