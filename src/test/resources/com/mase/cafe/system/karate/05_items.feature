Feature: Items Management API

    Background: Setup the Base path and login
        * url baseUrl
        * def loginResult = call read('classpath:com/mase/cafe/system/karate/03_login_success.feature')
        * def jwtToken = loginResult.token

    # Generate a unique date based on current time to avoid collisions in the pipeline
        * def now = java.lang.System.currentTimeMillis()
        * def testDate = java.time.LocalDate.now().plusDays(new java.util.Random().nextInt(1000) + 1).toString()


Scenario: Prevent Adding Duplicate Item Name to the Same Menu
    * def duplicateCheckDate = '2026-08-10'
    * def uniqueItemName = 'Double Espresso ' + java.lang.System.currentTimeMillis()
    * def itemPayload =
    """
    {
      "name": "#(uniqueItemName)",
      "description": "Strong and bold",
      "price": 3.50,
      "category": "Beverage"
    }
    """

    # 1. First Add: Should succeed and auto-create the menu
    Given path 'manager/api/menus/create-and-add'
    And param date = duplicateCheckDate
    And header Authorization = 'Bearer ' + jwtToken
    And request itemPayload
    When method post
    Then status 200
    And match response.items[*].name contains uniqueItemName

    # 2. Second Add: Should fail because the name already exists for this date
    # Even if we change the price or description, the name collision triggers the 400
    Given path 'manager/api/menus/create-and-add'
    And param date = duplicateCheckDate
    And header Authorization = 'Bearer ' + jwtToken
    # We use the same payload name
    And request itemPayload
    When method post
    Then status 400
    # Match the specific error message from your Service/GlobalExceptionHandler
    And match response.error contains "already on the menu"