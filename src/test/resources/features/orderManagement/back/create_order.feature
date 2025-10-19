Feature: Create an order (guest flow)
  As a hungry customer
  I want to place an order for my meal
  So that I can receive my food delivery

  Background:
    Given a customer named "Alex"
    And Alex has the following items in the cart:
      | item             | quantity | unit price |
      | Margherita pizza | 1        | 12.50      |
      | Tiramisu         | 2        | 4.00       |

  Scenario: Successfully create an order with a saved payment method
    When Alex selects the delivery address "25 rue de France, Nice"
    And Alex chooses the saved payment method "Visa"
    And Alex confirms the order
    Then the order should be created with status "CONFIRMED"
    And Alex should see the order total of "20.50"
    And Alex should receive an order confirmation notification

  Scenario Outline: Prevent order creation when required information is missing
    Given the cart belongs to "<customer>"
    When the customer tries to create the order without "<missing information>"
    Then the order should be rejected with the message "<error message>"
    And no payment should be captured

    Examples:
      | customer | missing information | error message                          |
      | Alex     | delivery address    | Delivery address is required           |
      | Alex     | payment method      | Payment method must be provided        |
      | Alex     | cart items          | Cannot create an order with empty cart |

  Scenario: Update totals when an item quantity changes before confirmation
    When Alex updates the quantity of "Tiramisu" to 1
    And Alex reviews the order summary
    Then the order total should be recalculated to "16.50"
    And the cart should reflect the updated quantity