package fr.unice.polytech.orderManagement;

import fr.unice.polytech.dishes.Dish;
import fr.unice.polytech.paymentProcessing.*;
import fr.unice.polytech.restaurants.Restaurant;
import fr.unice.polytech.users.DeliveryLocation;
import fr.unice.polytech.users.StudentAccount;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderManager {

    private List<Order> registeredOrders;
    private List<Order> pendingOrders;
    private Map<Order, Long> orderCreationTimes;
    private static final long TIMEOUT_MILLIS = 3 * 60 * 1000; // 3 minutes

    public OrderManager(){
        registeredOrders = new java.util.ArrayList<>();
        pendingOrders = new java.util.ArrayList<>();
        orderCreationTimes = new HashMap<>();

    }

    public void createOrder(List<Dish> dishes, StudentAccount studentAccount, DeliveryLocation deliveryLocation, Restaurant restaurant) {
        Order order = new Order.Builder(studentAccount)
                .dishes(dishes)
                .amount(calculateTotalAmount(dishes))
                .deliveryLocation(deliveryLocation)
                .restaurant(restaurant)
                .build();

        restaurant.addOrder(order);
        pendingOrders.add(order);
        orderCreationTimes.put(order, System.currentTimeMillis());

    }


    public void initiatePayment(Order order, PaymentMethod paymentMethod) {
        if (isOrderTimedOut(order)) {
            dropOrder(order);
            return;
        }
        // Le processeur dépendra du type de paiement
        IPaymentProcessor processor;

        if (paymentMethod == PaymentMethod.EXTERNAL) {
                processor = new PaymentProcessor(order);
        }
        else if (paymentMethod == PaymentMethod.INTERNAL) {
            processor = new InternalPaymentProcessor(order);
        }
        else {
            throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
        }

        // Traitement du paiement (réutilisé pour les deux types)
        OrderStatus status = processor.processPayment(order);
        order.setOrderStatus(status);
    }

    private void dropOrder(Order order) {
        order.setOrderStatus(OrderStatus.CANCELED);
        pendingOrders.remove(order);
    }

    private boolean isOrderTimedOut(Order order) {
        Long creationTime = orderCreationTimes.get(order);
        if (creationTime == null) {
            orderCreationTimes.put(order, System.currentTimeMillis()); // first seen now
            pendingOrders.add(order);                                   // ensure tracked
            return false;
        }
        return System.currentTimeMillis() - creationTime > TIMEOUT_MILLIS;
    }


    public boolean registerOrder(Order order) {
        if (order.getOrderStatus() == OrderStatus.VALIDATED) {
            registeredOrders.add(order);
            pendingOrders.remove(order);
            orderCreationTimes.remove(order);
            return true;
        } else {
            return false;
        }
    }


    private double calculateTotalAmount(List<Dish> dishes) {
        return dishes.stream().mapToDouble(Dish::getPrice).sum();
    }

    public List<Order> getRegisteredOrders() {
        return registeredOrders;
    }
    public List<Order> getPendingOrders() {
        return pendingOrders;
    }




}
