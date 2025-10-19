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
    private final PaymentProcessorFactory paymentProcessorFactory;

    public OrderManager(){
        this(new PaymentProcessorFactory());

    }
    public OrderManager(PaymentProcessorFactory paymentProcessorFactory) {
        this.paymentProcessorFactory = paymentProcessorFactory;
        registeredOrders = new java.util.ArrayList<>();
        pendingOrders = new java.util.ArrayList<>();
        orderCreationTimes = new HashMap<>();
    }

    public void createOrder(List<Dish> dishes, StudentAccount studentAccount, DeliveryLocation deliveryLocation, Restaurant restaurant) {
        if (!studentAccount.hasDeliveryLocation(deliveryLocation)) {
            throw new IllegalArgumentException("Order creation failed: Delivery location is not among the student's saved locations.");
        }
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
        if (paymentMethod == null) {
            // The payment method comes from the user selection in the order confirmation flow and can be
            // missing when the client submits an incomplete request. Fail fast with an explicit error
            // instead of letting the processor selection crash on a null value.
            throw new IllegalArgumentException("Payment method must be provided");
        }
        if (isOrderTimedOut(order)) {
            dropOrder(order);
            return;
        }
        //Creattion du processeur de paiement via la factory
        IPaymentProcessor processor = paymentProcessorFactory.createProcessor(order, paymentMethod);

        // Traitement du paiement (réutilisé pour les deux types)
        OrderStatus status = processor.processPayment(order);
        order.setOrderStatus(status);

    }

    private void dropOrder(Order order) {
        order.setOrderStatus(OrderStatus.CANCELED);
        pendingOrders.remove(order);
        orderCreationTimes.remove(order); // Important: remove the timer
    }

    private boolean isOrderTimedOut(Order order) {
        Long creationTime = orderCreationTimes.get(order);
        return (System.currentTimeMillis() - creationTime) > TIMEOUT_MILLIS;
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
