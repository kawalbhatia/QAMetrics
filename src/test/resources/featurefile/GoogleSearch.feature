@Featuretagtest
Feature: Google search functionality

Background:
Given user on the google home page


    
    @RegressionUI @GoogleSearch @screenshot
   Scenario: Validate the google search functionality  
    Given user on the google home page  
    When User enters the search keyword in the search box
    And User submits the search operation
    Then User should be presented with the search result
    #And last test step
    
       @RegressionUI @GoogleSearch
   Scenario Outline: Validate the google search functionality through scenario outline
    When User enters the search keyword as "<search_keyword>" in the search box
    And User submits the search operation
    Then User should be presented with the search result
    And last test step
    
    Examples:    
    |search_keyword|
    |javascript|
 
    
    
 

 