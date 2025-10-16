package fr.unice.polytech.orderManagement;


import fr.unice.polytech.dishes.Dish;
import fr.unice.polytech.restaurants.Restaurant;
import fr.unice.polytech.users.DeliveryLocation;
import fr.unice.polytech.users.StudentAccount;
import fr.unice.polytech.users.UserAccount;

import java.util.List;

public class Order {
    private UserAccount studentAccount;
    private Restaurant restaurant;
    private double amount;
    private OrderStatus orderStatus;
    private List<Dish> dishes;
    private DeliveryLocation deliveryLocation;


    private Order(Builder builder) {
        this.studentAccount = builder.studentAccount;
        this.amount = builder.amount;
        this.dishes = builder.dishes;
        this.deliveryLocation = builder.deliveryLocation;
        this.orderStatus = builder.orderStatus != null ? builder.orderStatus : OrderStatus.PENDING;
        this.restaurant = builder.restaurant;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public UserAccount getStudentAccount() {
        return studentAccount;
    }

    public double getAmount() {
        return amount;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public DeliveryLocation getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(DeliveryLocation deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    public static class Builder {
        private StudentAccount studentAccount;
        private double amount;
        private Restaurant restaurant;
        private List<Dish> dishes;
        private DeliveryLocation deliveryLocation;
        private OrderStatus orderStatus;

        public Builder(StudentAccount studentAccount) {
            this.studentAccount = studentAccount;
        }

        public Builder deliveryLocation(DeliveryLocation deliveryLocation) {
            this.deliveryLocation = deliveryLocation;
            return this;
        }

        public Builder orderStatus(OrderStatus orderStatus) {
            this.orderStatus = orderStatus;
            return this;
        }

        public Builder amount(double amount) {
            this.amount = amount;
            return this;
        }
        public Builder dishes(List<Dish> dishes) {
            this.dishes = dishes;
            return this;
        }
        public Builder restaurant(Restaurant restaurant) {
            this.restaurant = restaurant;
            return this;
        }


        public Order build() {
            return new Order(this);
        }
    }
}
