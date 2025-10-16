package fr.unice.polytech.paymentProcessing;

import fr.unice.polytech.orderManagement.Order;
import fr.unice.polytech.orderManagement.OrderStatus;
import fr.unice.polytech.users.StudentAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


class PaymentProcessorTest {

    private final String NAME = "Alice";
    private final String SURNAME = "Smith";
    private final String EMAIL = "alice.smith@etu.unice.fr";
    private final String ID = "22400632";

    Order order;

    @BeforeEach
    void setUp() {
        StudentAccount student = new StudentAccount.Builder(NAME, SURNAME)
                .email(EMAIL)
                .studentId(ID)
                .bankInfo("3151 2136 8946 4151", 401, 5,28)
                .build();

        order = new Order.Builder(student).build();
    }


    @Test
    void processPaymentWithSuccessfulExternalPaymentValidatesOrder() {
        IPaymentService paymentService = mock(IPaymentService.class);
        when(paymentService.processExternalPayment(order)).thenReturn(true);
        PaymentProcessor processor = new PaymentProcessor(order, paymentService);

        OrderStatus status = processor.processPayment();

        assertEquals(OrderStatus.VALIDATED, status);
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());
        verify(paymentService).processExternalPayment(order);
    }

    @Test
    void processPaymentWithFailedExternalPaymentCancelsOrder() {
        IPaymentService paymentService = mock(IPaymentService.class);
        when(paymentService.processExternalPayment(order)).thenReturn(false);
        PaymentProcessor processor = new PaymentProcessor(order, paymentService);

        OrderStatus status = processor.processPayment();

        assertEquals(OrderStatus.CANCELED, status);
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());
        verify(paymentService).processExternalPayment(order);
    }

    @Test
    void processPaymentWithCustomOrderUsesProvidedOrderInstance() {
        IPaymentService paymentService = mock(IPaymentService.class);
        PaymentProcessor processor = new PaymentProcessor(order, paymentService);

        StudentAccount otherStudent = new StudentAccount.Builder(NAME, SURNAME)
                .email("other." + EMAIL)
                .studentId(ID + "1")
                .bankInfo("4321 6789 4321 6789", 402, 6, 29)
                .build();
        Order otherOrder = new Order.Builder(otherStudent).build();

        when(paymentService.processExternalPayment(otherOrder)).thenReturn(false);

        OrderStatus status = processor.processPayment(otherOrder);

        assertEquals(OrderStatus.CANCELED, status);
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());
        verify(paymentService).processExternalPayment(otherOrder);
        verify(paymentService, never()).processExternalPayment(order);
    }

    @Test
    void updatePaymentStatus_ShouldValidate_OnSuccessAfterFirstFailure() {
        IPaymentService paymentService = mock(IPaymentService.class);
        when(paymentService.processExternalPayment(order))
                .thenReturn(false)
                .thenReturn(true);

        PaymentProcessor processor = new PaymentProcessor(order, paymentService);

        OrderStatus status = processor.updatePaymentStatus(order);

        assertEquals(OrderStatus.VALIDATED, status);
        verify(paymentService, times(2)).processExternalPayment(order);
    }

    @Test
    void updatePaymentStatus_ShouldCancel_AfterThreeConsecutiveFailures() {
        IPaymentService paymentService = mock(IPaymentService.class);
        when(paymentService.processExternalPayment(order))
                .thenReturn(false) // 1ère tentative
                .thenReturn(false) // 2ème tentative (Relance 1)
                .thenReturn(false); // 3ème tentative (Relance 2)

        PaymentProcessor processor = new PaymentProcessor(order, paymentService);

        OrderStatus status = processor.updatePaymentStatus(order);

        assertEquals(OrderStatus.CANCELED, status);
        verify(paymentService, times(3)).processExternalPayment(order);
    }

    @Test
    void updatePaymentStatus_ShouldValidate_OnImmediateSuccess() {
        IPaymentService paymentService = mock(IPaymentService.class);
        when(paymentService.processExternalPayment(order))
                .thenReturn(true);

        PaymentProcessor processor = new PaymentProcessor(order, paymentService);

        OrderStatus status = processor.updatePaymentStatus(order);

        assertEquals(OrderStatus.VALIDATED, status);
        verify(paymentService, times(1)).processExternalPayment(order);
    }
    @Test
    void updatePaymentStatus_ShouldValidate_OnSuccessAtLastRetry() {
        IPaymentService paymentService = mock(IPaymentService.class);
        when(paymentService.processExternalPayment(order))
                .thenReturn(false)
                .thenReturn(false)
                .thenReturn(true);

        PaymentProcessor processor = new PaymentProcessor(order, paymentService);

        OrderStatus status = processor.updatePaymentStatus(order);

        assertEquals(OrderStatus.VALIDATED, status);
        verify(paymentService, times(3)).processExternalPayment(order);
    }
}