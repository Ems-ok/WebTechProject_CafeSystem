Feature: Items Management API

    Background: Setup the Base path and login
        * url baseUrl
        * def loginResult = call read('classpath:com/mase/cafe/system/karate/03_login_success.feature')
        * def jwtToken = loginResult.token
        * def testDate = '2026-03-15'

    Scenario: Update Menu Item - Successful
        * def initialName = 'Original Latte ' + java.lang.System.currentTimeMillis()
        Given path 'manager/api/menus/create-and-add'
        And param date = testDate
        And header Authorization = 'Bearer ' + jwtToken
        And request { name: '#(initialName)', description: 'Old desc', price: 4.00, category: 'Beverage' }
        When method post
        Then status 200

        * def createdItem = response.items.find(x => x.name == initialName)
        * def itemId = createdItem.id

        Given path 'manager/api/items', itemId
        And header Authorization = 'Bearer ' + jwtToken
        And request
        """
        {
          "name": "Updated Latte",
          "description": "Silkier foam",
          "price": 4.50,
          "category": "Beverage"
        }
        """
        When method put
        Then status 200
        And match response.name == 'Updated Latte'
        And match response.price == 4.50

        Given path 'manager/api/menus/date'
        And param date = testDate
        And header Authorization = 'Bearer ' + jwtToken
        When method get
        Then status 200
        And match response.items[*].name contains 'Updated Latte'

    Scenario: Update Menu Item - Unsuccessful (Invalid Data)

        * def failItemName = 'Fail Item ' + java.lang.System.currentTimeMillis()
        Given path 'manager/api/menus/create-and-add'
        And param date = testDate
        And header Authorization = 'Bearer ' + jwtToken
        And request { name: '#(failItemName)', description: 'Test', price: 1.00, category: 'Beverage' }
        When method post
        Then status 200
        * def failItemId = response.items.find(x => x.name == failItemName).id

        Given path 'manager/api/items', failItemId
        And header Authorization = 'Bearer ' + jwtToken
        And request
        """
        {
          "name": "",
          "description": "Invalid update",
          "price": -1.00,
          "category": "Beverage"
        }
        """
        When method put

        Then status 400
        And match response.error == "#notnull"

    Scenario: Prevent Adding Duplicate Item Name to the Same Menu
        * def duplicateCheckDate = '2026-08-10'
        * def uniqueItemName = 'Double Espresso ' + java.lang.System.currentTimeMillis()
        * def itemPayload = { "name": "#(uniqueItemName)", "description": "Strong", "price": 3.50, "category": "Beverage" }

        Given path 'manager/api/menus/create-and-add'
        And param date = duplicateCheckDate
        And header Authorization = 'Bearer ' + jwtToken
        And request itemPayload
        When method post
        Then status 200

        Given path 'manager/api/menus/create-and-add'
        And param date = duplicateCheckDate
        And header Authorization = 'Bearer ' + jwtToken
        And request itemPayload
        When method post
        Then status 400
        And match response.error contains "already on the menu"