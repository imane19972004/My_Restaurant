Feature: Internal payment

  Background:
    Given a client named "Jordan"
    And Jordan has an account balance of 30.00

  Scenario: Successfully pay with internal balance
    Given Jordan has an order of total amount 25.00
    When Jordan pays his order using student credit
    Then the payment is approved
    And the order is Validated
    And Jordan's account balance is 5.00

  Scenario: Reject when funds are insufficient
    Given Jordan has an order of total amount 35.00
    When Jordan attempts to pay his order using student credit
    Then the payment is unsuccessful
    And Jordan's account balance is 30.00

