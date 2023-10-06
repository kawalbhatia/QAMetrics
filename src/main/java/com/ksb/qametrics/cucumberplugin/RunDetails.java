package com.ksb.qametrics.cucumberplugin;

public class RunDetails {
	int runId;
	String runStartTime,runEndTime,runDuration;
	public String getRunEndTime() {
		return runEndTime;
	}
	public void setRunEndTime(String runEndTime) {
		this.runEndTime = runEndTime;
	}
	public String getRunDuration() {
		return runDuration;
	}
	public void setRunDuration(String runDuration) {
		this.runDuration = runDuration;
	}
	String appName;
	String TestType;
	public int getRunId() {
		return runId;
	}
	public void setRunId(int runId) {
		this.runId = runId;
	}
	public String getRunStartTime() {
		return runStartTime;
	}
	public void setRunStartTime(String runStartTime) {
		this.runStartTime = runStartTime;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getTestType() {
		return TestType;
	}
	public void setTestType(String testType) {
		TestType = testType;
	}

}
