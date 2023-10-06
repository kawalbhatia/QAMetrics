package com.ksb.qametrics.test.stepdefinition;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;

public class GoogleSearch_steps {
	WebDriver driver;

	
	
@Before("not (@ADOSearch)")

public void setup() {
	 WebDriverManager.chromedriver().setup();
	  driver=new ChromeDriver();
	  driver.manage().window().maximize();
	
}

@After("not (@ADOSearch)")

public void after(Scenario scenario) throws Exception {
    tearDownDriver(scenario);}

public void tearDownDriver(Scenario scenario) throws Exception {

    try {

        System.out.println("=====in tear down function==============");

        if (driver != null) {
			
		/*
			if (scenario.isFailed()) {
				String imagePath = new GenericHelper(ObjectRepo.driver).takeScreenShot(scenario.getName());
				scenario.write(imagePath);
				
				Reporter.addScreenCaptureFromPath(imagePath);
			}
			*/
            takeScreenshot(scenario,driver);

            driver.close();
           driver.quit();
           System.out.println("shutting Down the driver");
            
            
            

            //Assert.assertTrue(false,"Testing failure");
        }


    } catch (Exception e) {
        
        throw e;
    }
}

public void takeScreenshot(Scenario scenario,WebDriver driver) throws Exception {

	try {

		if (scenario.isFailed()) {			
			
			String screenshotName = scenario.getName().replaceAll(" ", "_");
			//String imagePath = new GenericHelper(ObjectRepo.driver).takeScreenShot(scenario.getName());
			byte[] sourcePath = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
			generateScreenshot(sourcePath);
		//	scenario.attach(extractBytes(imagePath), "image/png",screenshotName);
			scenario.attach(sourcePath, "image/png", screenshotName);
			

		}
	}catch(Exception e) {}
}

public void generateScreenshot(byte[] source) throws IOException {
	
	System.out.println("=======in  png file creation ===========");
	   InputStream in = new ByteArrayInputStream(source);

       // Read image from input stream
       BufferedImage image = ImageIO.read(in);

       // Write image to file in PNG format
       ImageIO.write(image, "PNG", new FileOutputStream("image.png"));
       System.out.println("=======generated png file===========");
	
	
}
	
	
	 @Given("^user on the google home page$")
	    public void user_on_the_google_home_page() throws Throwable {
		 
		
		 driver.get("https://www.google.com/");
		 		 
		System.out.println("Google home page");
	    }

	    @When("^User enters the search keyword in the search box$")
	    public void user_enters_the_search_keyword_in_the_search_box() throws Throwable {
	    	
	     driver.findElement(By.xpath("//input[@name='q']")).sendKeys("Ryder Systems");
	    	System.out.println("User enters the search keyword in the search box");
	    	
	    }
	    
	    @And("^User submits the search operation$")
	    public void user_submits_the_search_operation() throws Throwable {
	    	
	    	driver.findElement(By.xpath("(//input[@value='Google Search'])[2]")).click();
	    	
	    	System.out.println("User submits the search operation");
	        
	    }

	    @Then("^User should be presented with the search result$")
	    public void user_should_be_presented_with_the_search_result() throws Throwable {
	    	System.out.println("User should be presented with the search result");
	    	Assert.assertTrue("failed to get the results",false);
	    	
	    }
	    
	    
	    @When("User enters the search keyword as {string} in the search box")
	    public void user_enters_the_search_keyword_as_in_the_search_box(String string) {
	        // Write code here that turns the phrase above into concrete actions
	    	
	    	System.out.println("User enters the search keyword ");
	        
	    }
	    
	    @Then("last test step")
	    public void last_test_step() {
	        // Write code here that turns the phrase above into concrete actions
	        System.out.println("Last step to test");
	    }


		@Given("^user connects to VMD database$")
		public void userConnectsToVMDDatabase() throws Throwable {
			System.out.println("user connects to VMD database");
			
		}


		@When("^user validates the \"([^\"]*)\" table$")
		public void userValidatesTheTable(String arg1) throws Throwable {
			System.out.println("User validates the table");
		
		}


		@Then("^user should find the fields \"([^\"]*)\" in the \"([^\"]*)\" table$")
		public void userShouldFindTheFieldsInTheTable(String arg1, String arg2) throws Throwable {

        if ( arg1.equals("image_electronic_location_code")) {
        	Thread.sleep(20000);
        	Assert.assertTrue(false);
        }
		}


		@And("^user should be able to validate the valid data in the field$")
		public void userShouldBeAbleToValidateTheValidDataInTheField() throws Throwable {
			
			System.out.println("user should be able to validate the valid data in the field");
		}
	   


}
