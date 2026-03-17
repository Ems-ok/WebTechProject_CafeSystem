Feature: Authentication API - login success

  Background:
    Given url baseUrl

  Scenario: Login with valid credentials
    Given path '/auth/login'
    And header Content-Type = 'application/json'
    And header Accept = 'application/json'
    And request
    """
    {
      "username": "manager",
      "password": "manager"
    }
    """
    When method post
    Then status 200
    And match response.token == '#notnull'

    * def token = response.token
    * print 'JWT token:', token