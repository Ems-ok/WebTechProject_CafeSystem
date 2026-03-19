Feature: Order Management API

  Background:
    * url baseUrl
    * header Authorization = 'Bearer ' + authToken

  Scenario: Create a new order and verify it exists
    Given path 'orders'
    And request { ordername: 'Table 10 - Breakfast', totalAmount: 45.50 }
    When method post
    Then status 201
    And match response.ordername == 'Table 10 - Breakfast'
    * def orderId = response.id

    Given path 'orders', orderId
    When method get
    Then status 200
    And match response.id == orderId
    And match response.totalAmount == 45.50

  Scenario: Update an existing order

    Given path 'orders'
    And request { ordername: 'Temporary Order', totalAmount: 10.00 }
    When method post
    Then status 201
    * def tempId = response.id

    Given path 'orders', tempId
    And request { ordername: 'Updated Table Name', totalAmount: 99.99 }
    When method put
    Then status 200

    Given path 'orders', tempId
    When method get
    Then status 200
    And match response.ordername == 'Updated Table Name'

  Scenario: Delete an order

    Given path 'orders'
    And request { ordername: 'To Be Deleted', totalAmount: 5.00 }
    When method post
    * def toDeleteId = response.id

    Given path 'orders', toDeleteId
    When method delete
    Then status 200

    Given path 'orders', toDeleteId
    When method get
    Then status 404

  Scenario: Fetch all orders
    Given path 'orders'
    When method get
    Then status 200
    And match response == '#[]'
    And match each response contains { id: '#number', ordername: '#string' }

  Scenario: Fail to create order with short name
    Given path 'orders'

    And request { ordername: 'Tea', totalAmount: 2.50 }
    When method post
    Then status 400