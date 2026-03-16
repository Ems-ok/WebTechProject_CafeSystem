Feature: Menu Management API

  Background: Setup the Base path and login
    * url baseUrl
    * def loginResult = call read('classpath:com/mase/cafe/system/karate/03_login_success.feature')
    * def jwtToken = loginResult.token

    # Generate a unique date based on current time to avoid collisions in the pipeline
    * def now = java.lang.System.currentTimeMillis()
    * def testDate = java.time.LocalDate.now().plusDays(new java.util.Random().nextInt(1000) + 1).toString()

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
    And match response.error contains "already exists"


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
    And match response[*].menuDate contains testDate


  Scenario: Create Item and Add to Menu
    * def itemName = 'Caramel Macchiato ' + java.lang.System.currentTimeMillis()
    * def itemDate = '2026-03-15'

    #Create the menu first so the next call doesn't fail
    Given path 'manager/api/menus'
    And header Authorization = 'Bearer ' + jwtToken
    And request { menuDate: '#(itemDate)' }
    When method post
    Then status 201
