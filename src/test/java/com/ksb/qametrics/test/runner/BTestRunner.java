package com.ksb.qametrics.test.runner;

import org.testng.annotations.AfterClass;



import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;


@CucumberOptions(features = { "classpath:featurefile" }, glue = { "classpath:com.ksb.qametrics.test.stepdefinition" }, plugin = { "pretty", "json:target/DemoProj.json",
		"com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:","com.ksb.qametrics.cucumberplugin.CustomReportListener:"}, monochrome = true, tags = "@ADOSearch")


//"com.ksb.qametrics.cucumberplugin.CustomReportListener",

public class BTestRunner extends AbstractTestNGCucumberTests {
	
	@AfterClass(alwaysRun = true)
	public static void writeExtentReport() {
//		ReporterUtil.archiveReport();
	}

}
