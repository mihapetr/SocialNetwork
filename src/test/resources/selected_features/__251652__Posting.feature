Feature: Posting

  Background:
    Given user is logged in : posting

  Scenario: New Post
    Given user navigates to Posts view
    And user selects new Post option
    And user fills the new post form
    When user submits the new post form
    Then the post is created on the platform
    And all users can see the post
    And user is post owner

  Scenario: User Posts
    When user navigates to their Profile view
    Then user can see all of their Posts

  Scenario: Comment on Post
    Given user navigates to Posts view
    And user selects details of one of the Posts
    When user submits the comment form
    Then comment is added to the Post
    And user details are visible in the post

