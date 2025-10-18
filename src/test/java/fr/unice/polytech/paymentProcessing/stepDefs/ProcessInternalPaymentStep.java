package fr.unice.polytech.paymentProcessing.stepDefs;

import fr.unice.polytech.orderManagement.Order;
import fr.unice.polytech.orderManagement.OrderManager;
import fr.unice.polytech.orderManagement.OrderStatus;
import fr.unice.polytech.paymentProcessing.PaymentMethod;
import fr.unice.polytech.users.StudentAccount;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;


public class ProcessInternalPaymentStep {
    StudentAccount.Builder clientBuilder;
    StudentAccount client;
    Order.Builder orderBuilder;
    Order order;
    boolean paymentResult;
    OrderManager orderManager = new OrderManager();

    @Given("a client named {string}")
    public void a_client_named(String name) {
        clientBuilder = new StudentAccount.Builder(name, "Smith");
    }

    @Given("Jordan has an account balance of {double}")
    public void jordan_has_an_account_balance_of(double balance) {
        clientBuilder.balance(balance);
        client =  clientBuilder.build();
    }
    @Given("Jordan has an order of total amount {double}")
    public void jordan_has_an_order_of_total_amount(Double amount) {
        orderBuilder = new Order.Builder(client).amount(amount);
        order = orderBuilder.build();

    }

    @When("Jordan pays his order using student credit")
    public void jordan_pays_his_order_using_student_credit() {
        orderManager.initiatePayment(order, PaymentMethod.INTERNAL);
        paymentResult = order.getOrderStatus() == OrderStatus.VALIDATED;
    }

    @Then("the payment is approved")
    public void the_payment_is_approved() {
        Assertions.assertTrue(paymentResult);
    }

    @Then("the order is Validated")
    public void the_order_is_validated() {
        Assertions.assertEquals(OrderStatus.VALIDATED, order.getOrderStatus());
    }
    @Then("Jordan's account balance is {double}")
    public void jordans_account_balance_is(double balance) {
        Assertions.assertEquals(balance, client.getBalance());
    }


    @When("Jordan attempts to pay his order using student credit")
    public void jordan_attempts_to_pay_his_order_using_student_credit() {
        // Utiliser l'OrderManager pour la cohérence du test
        orderManager.initiatePayment(order, PaymentMethod.INTERNAL);
        paymentResult = order.getOrderStatus() == OrderStatus.VALIDATED;
    }

    @Then("the payment is unsuccessful")
    public void the_payment_is_unsuccessful() {
        Assertions.assertFalse(paymentResult);

    }







}
