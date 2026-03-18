Feature: End to End Tests for ToolsQA's Book Store API
Description: The purpose of these tests are to cover End to End hppy flows for customer

Background: User generates token for Authentication
Given The user is authorized

Scenario: Authorized user is able to Add and Remove a book
Given A list of book is available
When User adds a book to his reading list
Then The book is added
When User removes a book from his reading list
Then The book is removed 

