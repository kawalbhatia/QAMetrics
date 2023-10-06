@Featuretagtest
Feature: Binge search functionality another feature

Background:
Given user on the google home page


    
    @RegressionUI @GoogleSearchtest
   Scenario: Validate the binge search functionality another feature   
    When User enters the search keyword in the search box
    And User submits the search operation
    Then User should be presented with the search result
    #And last test step
    
       @RegressionUI @GoogleSearch
   Scenario Outline: Validate the binge search functionality through scenario outline another feature
    When User enters the search keyword as "<search_keyword>" in the search box
    And User submits the search operation
    Then User should be presented with the search result
    And last test step
    
    Examples:    
    |search_keyword|
    |javascript|
 
    
    
 

 