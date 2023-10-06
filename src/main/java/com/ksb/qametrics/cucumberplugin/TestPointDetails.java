package com.ksb.qametrics.cucumberplugin;


import java.util.ArrayList;
import java.util.List;


public class TestPointDetails {
	
	int testPlanId;
	int testSuiteId;
	String testSuiteName;
	int tcId;
	String testCaseName;
	List<Integer> testCasePoints ;
	String testPointProcessed;
	int parentId;
	
	
	
	
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public String getTestPointProcessed() {
		return testPointProcessed;
	}
	public void setTestPointProcessed(String testPointProcessed) {
		this.testPointProcessed = testPointProcessed;
	}
	public TestPointDetails() {
	
		testCasePoints = new ArrayList<Integer>();
	}
	public int getTestPlanId() {
		return testPlanId;
	}
	public void setTestPlanId(int testPlanId) {
		this.testPlanId = testPlanId;
	}
	public int getTestSuiteId() {
		return testSuiteId;
	}
	public void setTestSuiteId(int testSuiteId) {
		this.testSuiteId = testSuiteId;
	}
	public String getTestSuiteName() {
		return testSuiteName;
	}
	public void setTestSuiteName(String testSuiteName) {
		this.testSuiteName = testSuiteName;
	}
	public int getTcId() {
		return tcId;
	}
	public void setTcId(int tcId) {
		this.tcId = tcId;
	}
	public String getTestCaseName() {
		return testCaseName;
	}
	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}
	public List<Integer> getTestCasePoints() {
		return testCasePoints;
	}
	public void setTestCasePoints(List<Integer> testCasePoints) {
		this.testCasePoints = testCasePoints;
	}
	
}
