Feature: Menu Management API

  Background: Setup the Base path and login
    * url baseUrl
    * def loginResult = call read('classpath:com/mase/cafe/system/karate/03_login_success.feature')
    * def jwtToken = loginResult.token

    * def now = java.lang.System.currentTimeMillis()
    * def testDate = java.time.LocalDate.now().plusDays(new java.util.Random().nextInt(1000) + 1).toString()

  Scenario: Create Menu - Successful and Unsuccessful (Duplicate)

    Given path 'manager/api/menus'
    And header Authorization = 'Bearer ' + jwtToken
    And header Content-Type = 'application/json'
    And request { menuDate: '#(testDate)' }
    When method post
    Then status 201
    And match response contains { id: '#number', menuDate: '#(testDate)' }
    * def createdMenuId = response.id


    Given path 'manager/api/menus'
    And header Authorization = 'Bearer ' + jwtToken
    And header Content-Type = 'application/json'
    And request { menuDate: '#(testDate)' }
    When method post

    Then status 400
    And match response.error contains "already exists"

    Given path 'manager/api/menus/' + createdMenuId
    And header Authorization = 'Bearer ' + jwtToken
    And header Accept = 'application/json'
    When method get
    Then status 200
    And match response.id == createdMenuId


    Given path 'manager/api/menus'
    And header Authorization = 'Bearer ' + jwtToken
    And header Accept = 'application/json'
    When method get
    Then status 200
    And match response[*].menuDate contains testDate


  Scenario: Create Item and Add to Menu
    * def itemName = 'Caramel Macchiato ' + java.lang.System.currentTimeMillis()
    * def itemDate = '2026-03-15'

    Given path 'manager/api/menus'
    And header Authorization = 'Bearer ' + jwtToken
    And request { menuDate: '#(itemDate)' }
    When method post
    Then status 201

    Given path 'manager/api/menus/create-and-add'
    And param date = itemDate
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

    And match response.items[*].name contains itemName