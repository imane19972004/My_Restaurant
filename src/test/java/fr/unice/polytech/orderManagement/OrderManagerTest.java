package fr.unice.polytech.orderManagement;

import fr.unice.polytech.dishes.Dish;
import fr.unice.polytech.paymentProcessing.BankInfo;
import fr.unice.polytech.paymentProcessing.PaymentMethod;
import fr.unice.polytech.restaurants.Restaurant;
import fr.unice.polytech.users.DeliveryLocation;
import fr.unice.polytech.users.StudentAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderManagerTest {

    private OrderManager orderManager;
    private StudentAccount mockStudentAccount;
    private Restaurant mockRestaurant;
    private DeliveryLocation mockDeliveryLocation;
    private List<Dish> mockDishes;
    private Dish mockDish1;
    private Dish mockDish2;
    private BankInfo mockBankInfo;

    @BeforeEach
    void setUp() {
        orderManager = new OrderManager();
        mockStudentAccount = mock(StudentAccount.class);
        mockRestaurant = mock(Restaurant.class);
        mockDeliveryLocation = mock(DeliveryLocation.class);
        mockBankInfo = mock(BankInfo.class);

        mockDish1 = mock(Dish.class);
        mockDish2 = mock(Dish.class);
        when(mockDish1.getPrice()).thenReturn(15.50);
        when(mockDish2.getPrice()).thenReturn(12.00);

        when(mockBankInfo.getExpirationDate()).thenReturn(YearMonth.from(LocalDate.now().plusYears(2)));
        when(mockBankInfo.getCardNumber()).thenReturn("1234567890123456");
        when(mockBankInfo.getCVV()).thenReturn(123);


        when(mockStudentAccount.getBankInfo()).thenReturn(mockBankInfo);
        mockDishes = Arrays.asList(mockDish1, mockDish2);
    }

    @Test
    void testCreateOrder() throws Exception {
        orderManager.createOrder(mockDishes, mockStudentAccount, mockDeliveryLocation, mockRestaurant);

        verify(mockRestaurant).addOrder(any(Order.class));

        Field pendingOrdersField = OrderManager.class.getDeclaredField("pendingOrders");
        pendingOrdersField.setAccessible(true);
        List<Order> pendingOrders = (List<Order>) pendingOrdersField.get(orderManager);

        assertEquals(1, pendingOrders.size());
        Order createdOrder = pendingOrders.get(0);
        assertEquals(mockStudentAccount, createdOrder.getStudentAccount());
        assertEquals(27.50, createdOrder.getAmount());
        assertEquals(mockDishes, createdOrder.getDishes());
        assertEquals(mockDeliveryLocation, createdOrder.getDeliveryLocation());
        assertEquals(mockRestaurant, createdOrder.getRestaurant());
    }

    @Test
    void testCreateOrderTimerStart() throws Exception {
        orderManager.createOrder(mockDishes, mockStudentAccount, mockDeliveryLocation, mockRestaurant);

        Field orderCreationTimesField = OrderManager.class.getDeclaredField("orderCreationTimes");
        orderCreationTimesField.setAccessible(true);
        Map<Order, Long> orderCreationTimes = (Map<Order, Long>) orderCreationTimesField.get(orderManager);

        assertEquals(1, orderCreationTimes.size());
        Long creationTime = orderCreationTimes.values().iterator().next();
        assertTrue(Math.abs(System.currentTimeMillis() - creationTime) < 1000);
    }

    @Test
    void testInitiatePaymentBeforeTimeout() throws Exception {
        orderManager.createOrder(mockDishes, mockStudentAccount, mockDeliveryLocation, mockRestaurant);

        Field pendingOrdersField = OrderManager.class.getDeclaredField("pendingOrders");
        pendingOrdersField.setAccessible(true);
        List<Order> pendingOrders = (List<Order>) pendingOrdersField.get(orderManager);
        Order order = pendingOrders.get(0);

        orderManager.initiatePayment(order, PaymentMethod.EXTERNAL);


        assertEquals(1, pendingOrders.size());
        assertNotEquals(OrderStatus.CANCELED, order.getOrderStatus());
    }

    @Test
    void testInitiatePaymentAfterTimeout() throws Exception {
        orderManager.createOrder(mockDishes, mockStudentAccount, mockDeliveryLocation, mockRestaurant);

        Field pendingOrdersField = OrderManager.class.getDeclaredField("pendingOrders");
        pendingOrdersField.setAccessible(true);
        List<Order> pendingOrders = (List<Order>) pendingOrdersField.get(orderManager);
        Order order = pendingOrders.get(0);


        Field orderCreationTimesField = OrderManager.class.getDeclaredField("orderCreationTimes");
        orderCreationTimesField.setAccessible(true);
        Map<Order, Long> orderCreationTimes = (Map<Order, Long>) orderCreationTimesField.get(orderManager);
        orderCreationTimes.put(order, System.currentTimeMillis() - (4 * 60 * 1000));

        orderManager.initiatePayment(order, PaymentMethod.EXTERNAL);

        assertEquals(OrderStatus.CANCELED, order.getOrderStatus());
        assertEquals(0, pendingOrders.size());
    }

    @Test
    void testRegisterValidatedOrder() throws Exception {
        Order order = new Order.Builder(mockStudentAccount)
                .orderStatus(OrderStatus.VALIDATED)
                .build();

        Field pendingOrdersField = OrderManager.class.getDeclaredField("pendingOrders");
        pendingOrdersField.setAccessible(true);
        List<Order> pendingOrders = (List<Order>) pendingOrdersField.get(orderManager);
        pendingOrders.add(order);

        Field orderCreationTimesField = OrderManager.class.getDeclaredField("orderCreationTimes");
        orderCreationTimesField.setAccessible(true);
        Map<Order, Long> orderCreationTimes = (Map<Order, Long>) orderCreationTimesField.get(orderManager);
        orderCreationTimes.put(order, System.currentTimeMillis());

        boolean result = orderManager.registerOrder(order);

        assertTrue(result);
        assertEquals(0, pendingOrders.size());
        assertFalse(orderCreationTimes.containsKey(order));

        Field registeredOrdersField = OrderManager.class.getDeclaredField("registeredOrders");
        registeredOrdersField.setAccessible(true);
        List<Order> registeredOrders = (List<Order>) registeredOrdersField.get(orderManager);
        assertEquals(1, registeredOrders.size());
        assertTrue(registeredOrders.contains(order));
    }

    @Test
    void testRegisterNonValidatedOrder() {
        Order order = new Order.Builder(mockStudentAccount)
                .orderStatus(OrderStatus.PENDING)
                .build();

        boolean result = orderManager.registerOrder(order);

        assertFalse(result);
    }

    @Test
    void testCalculateTotalAmount() {
        orderManager.createOrder(mockDishes, mockStudentAccount, mockDeliveryLocation, mockRestaurant);


        verify(mockDish1).getPrice();
        verify(mockDish2).getPrice();
    }
}