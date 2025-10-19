package fr.unice.polytech.paymentProcessing;

import fr.unice.polytech.orderManagement.Order;
import fr.unice.polytech.orderManagement.OrderStatus;

public class PaymentProcessor implements IPaymentProcessor{

    private final Order order;
    private final IPaymentService paymentService;

    public PaymentProcessor(Order order) {
        this(order, new PaymentService());
    }

    public PaymentProcessor(Order order, IPaymentService paymentService) {
        this.order = order;
        this.paymentService = paymentService;
    }


    public OrderStatus processPayment() {
        return processPayment(order);
    }

    public OrderStatus processPayment(Order order){
        boolean paymentSuccessful = paymentService.processExternalPayment(order);
        return paymentSuccessful ? OrderStatus.VALIDATED : OrderStatus.CANCELED;
    }

    public OrderStatus updatePaymentStatus(Order order) {
        OrderStatus currentStatus = order.getOrderStatus();
        if (currentStatus == OrderStatus.VALIDATED) {
            return currentStatus;
        }

        OrderStatus status = processPayment(order);
        if (status == OrderStatus.VALIDATED) {
            return status;
        }
        int retries = 0;
        while (retries < 2 && status == OrderStatus.CANCELED) {
            status = processPayment(order);
            retries++;
        }
        return status;
    }

}
