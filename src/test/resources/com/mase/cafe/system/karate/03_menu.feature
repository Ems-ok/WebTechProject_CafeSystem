Feature: Menu Management API

  Background: Setup the Base path and login
    Given url baseUrl
    * def loginResult = call read('classpath:com/mase/cafe/system/karate/03_login_success.feature')
    * def jwtToken = loginResult.token
    * def testDate = '2026-05-20'

  Scenario: Create Menu - Successful and Unsuccessful (Duplicate)

    # 1. Create Menu - Successful
    # Assuming the endpoint is POST /manager/api/menus and it takes a date
    Given path 'manager/api/menus'
    And header Authorization = 'Bearer ' + jwtToken
    And header Content-Type = 'application/json'
    And request { menuDate: '#(testDate)' }
    When method post
    Then status 201
    And match response contains { id: '#number', menuDate: '#(testDate)' }
    * def createdMenuId = response.id


    # 2. Create Menu - Unsuccessful (Duplicate Date)
    # Attempting to create a menu for the exact same date should fail
    Given path 'manager/api/menus'
    And header Authorization = 'Bearer ' + jwtToken
    And header Content-Type = 'application/json'
    And request { menuDate: '#(testDate)' }
    When method post
    # Expecting 400 Bad Request or 409 Conflict depending on your API design
    Then status 400
    And match response contains "already exists"


    # 3. Get Menu by ID
    Given path 'manager/api/menus/' + createdMenuId
    And header Authorization = 'Bearer ' + jwtToken
    And header Accept = 'application/json'
    When method get
    Then status 200
    And match response.id == createdMenuId


    # 4. Get All Menus
    Given path 'manager/api/menus'
    And header Authorization = 'Bearer ' + jwtToken
    And header Accept = 'application/json'
    When method get
    Then status 200
    And match response == '#[]'
    And match response[*] contains { menuDate: '#(testDate)' }


  Scenario: Create Item and Add to Menu
    # Testing the specific endpoint: /create-and-add?date=yyyy-mm-dd
    # We use the date created in the background or a known existing one

    * def itemName = 'Caramel Macchiato ' + java.lang.System.currentTimeMillis()

    Given path 'manager/api/menus/create-and-add'
    And param date = '2026-03-15'
    And header Authorization = 'Bearer ' + jwtToken
    And header Content-Type = 'application/json'
    And request
    """
    {
      "name": "#(itemName)",
      "description": "Rich espresso with caramel drizzle",
      "price": 5.50,
      "category": "Beverage"
    }
    """
    When method post
    Then status 200
    And match response.menuDate == '2026-03-15'
    # Check if the items list contains our new item
    And match response.items[*].name contains itemName