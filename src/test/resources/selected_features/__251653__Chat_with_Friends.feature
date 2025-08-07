Feature: Chat with Friends

  Background:
    Given user is logged in

  Scenario: Requesting friendship
    Given user navigates to Profiles view
    And user selects Profile details
    And user is not other profile's friend already
    When user selects request friendhip option
    Then chat with request is created with the other profile

  Scenario: Accepting friendship
    Given user navigates to Chats view
    And user has a friend request message from other profile
    And user opens the chat with request
    When user selects accept option
    Then user and other profile are friends

  Scenario: Sending a message
    Given user navigates to Chats view
    And user opens a Chat with a recipient
    And user is recipient's friend
    When user submits Message content
    Then message is created in the chat
    And both users can see the message
