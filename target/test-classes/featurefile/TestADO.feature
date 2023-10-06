@Featuretagtest
Feature: Validate ADO update with test results

  @RegressionUI @ADOSearch
  Scenario: Validate update of valid accepted date in VMD and to Elastic search through rhub pub sub interface
    Given user connects to VMD database
    When user validates the "vmd" table
    Then user should find the fields "adevrtised_location_code" in the "Ryder_Combo" table
    And user should be able to validate the valid data in the field

  @RegressionUI @ADOSearch
  Scenario: Validate accepted date of invalid format is not updated in VMD and to Elastic search through rhub pub sub interface
    Given user connects to VMD database
    When user validates the "vmd" table
    Then user should find the fields "image_electronic_location_code" in the "Ryder_Combo" table
    And user should be able to validate the valid data in the field
    
      @RegressionUI @ADOSearch11
  Scenario: Validate the view for Vehicle Line of Business value table1
    Given user connects to VMD database
    When user validates the "vmd" table
    Then user should find the fields "image_electronic_location_code" in the "Ryder_Combo" table
    And user should be able to validate the valid data in the field
    
   