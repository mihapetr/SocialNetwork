Feature: Account

  Scenario: Successful registration
    Given user does not have account
    When user submits registration form
    Then user profile is created
    And user is notified about success

  Scenario: Successful login
    Given credentials are valid
    When user submits login form
    Then user is successfully logged in

  Scenario: Unsuccessful login
    Given credentials are invalid
    When user submits login form
    Then login is rejected
    Then user is notified about failure
