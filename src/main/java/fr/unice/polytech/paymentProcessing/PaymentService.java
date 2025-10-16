package fr.unice.polytech.paymentProcessing;

import fr.unice.polytech.orderManagement.Order;

public class PaymentService implements IPaymentService{
    @Override
    public boolean processExternalPayment(Order order) {
        return new MockedExternalPaymentSystem(order).mockedCheckingInformation();
    }
}
