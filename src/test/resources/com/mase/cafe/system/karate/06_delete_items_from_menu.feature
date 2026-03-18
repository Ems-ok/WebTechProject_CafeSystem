Scenario: Successfully Delete an Item from Menu
* def deleteItemName = 'DeleteMe-' + java.lang.System.currentTimeMillis()
* def deleteDate = '2026-09-15'
* def payload =
"""
    {
      "name": "#(deleteItemName)",
      "description": "To be deleted",
      "price": 2.50,
      "category": "Food"
    }
    """

Given path 'manager/api/menus/create-and-add'
And param date = deleteDate
And header Authorization = 'Bearer ' + jwtToken
And request payload
When method post
Then status 200

* def items = response.items
* def targetItem = karate.jsonPath(items, "$.[?(@.name=='" + deleteItemName + "')]")[0]
* def itemId = targetItem.id

Given path 'manager/api/items', itemId
And header Authorization = 'Bearer ' + jwtToken
When method delete
Then status 204

Given path 'manager/api/menus'
And header Authorization = 'Bearer ' + jwtToken
When method get
Then status 200
And match response[*].items[*].name !contains deleteItemName

