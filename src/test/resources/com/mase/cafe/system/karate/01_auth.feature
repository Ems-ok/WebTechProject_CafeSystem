Feature: Authentication API - login failure

    Background:
        * def loginResult = call read('classpath:com/mase/cafe/system/karate/03_login_success.feature')
        * def jwtToken = loginResult.jwtToken
        Given url baseUrl

    Scenario: Login with invalid credentials
        Given path '/auth/login'
        And header Content-Type = 'application/json'
        And header Accept = 'application/json'
        And request
    """
    {
      "username": "manager",
      "password": "wrongpassword"
    }
    """
        When method post
        Then status 401
        And match response.error == 'Unauthorized'