Feature: User Management API

  Background: Setup the Base path and login
    Given url baseUrl
    * def loginResult = call read('classpath:com/mase/cafe/system/karate/03_login_success.feature')
    * def jwtToken = loginResult.token
    * print 'JWT token:', jwtToken

  Scenario: Manage users (CRUD)

    #Get all users
    Given path 'manager/api/users'
    And header Authorization = 'Bearer ' + jwtToken
    And header Accept = 'application/json'
    When method get
    Then status 200

    * def objectCount = karate.sizeOf(response)
    * print 'Number of users:', objectCount

    And match each response contains { id: '#number', username: '#string', role: '#string' }


    #Create a new user
    Given path 'manager/api/users'
    And header Authorization = 'Bearer ' + jwtToken
    And header Content-Type = 'application/json'
    And request
    """
    {
      "username": "newuser",
      "password": "password123",
      "role": "USER"
    }
    """
    When method post
    Then status 201

    * def createdUserId = response.id
    * print 'Created user ID:', createdUserId

    And match response contains { id: '#number', username: 'newuser', role: 'USER' }


 # Get the created user by ID
    Given path 'manager/api/users/' + createdUserId
    And header Authorization = 'Bearer ' + jwtToken
    And header Accept = 'application/json'
    When method get
    Then status 200
    And match response contains { id: '#(createdUserId)', username: 'newuser', role: 'USER' }


# Update the created user
    Given path 'manager/api/users/' + createdUserId
    And header Authorization = 'Bearer ' + jwtToken
    And header Content-Type = 'application/json'
    And request
        """
        {
          "username": "updateduser",
          "password": "newpassword",
          "role": "ADMIN"
        }
        """
    When method put
    Then status 200
    And match response contains { id: '#(createdUserId)', username: 'updateduser', role: 'ADMIN' }


    # Delete the created user
    Given path 'manager/api/users/' + createdUserId
    And header Authorization = 'Bearer ' + jwtToken
    When method delete
    Then status 204