package com.ksb.qametrics.cucumberplugin;


public class StepsDetails {

	
	String stName;
	String stStatus;	
	String stepStartTime,stepEndTime,stepDuration;
	String stepErrorDetails;
	public String getStepErrorDetails() {
		return stepErrorDetails;
	}
	public void setStepErrorDetails(String stepErrorDetails) {
		this.stepErrorDetails = stepErrorDetails;
	}
	public String getStepStartTime() {
		return stepStartTime;
	}
	public void setStepStartTime(String stepStartTime) {
		this.stepStartTime = stepStartTime;
	}
	public String getStepEndTime() {
		return stepEndTime;
	}
	public void setStepEndTime(String stepEndTime) {
		this.stepEndTime = stepEndTime;
	}
	public String getStepDuration() {
		return stepDuration;
	}
	public void setStepDuration(String stepDuration) {
		this.stepDuration = stepDuration;
	}
	public String getStName() {
		return stName;
	}
	public void setStName(String stName) {
		this.stName = stName;
	}
	public String getStStatus() {
		return stStatus;
	}
	public void setStStatus(String stStatus) {
		this.stStatus = stStatus;
	}
	

	
}
