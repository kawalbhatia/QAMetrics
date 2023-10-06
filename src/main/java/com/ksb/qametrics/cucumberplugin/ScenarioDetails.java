package com.ksb.qametrics.cucumberplugin;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

public class ScenarioDetails {
	
	String scName;
	String scStatus;
	List<Document> stepList ;
	List<String> tagList;
	public List<String> getTagList() {
		return tagList;
	}


	public void setTagList(List<String> tagList) {
		this.tagList = tagList;
	}
	String scenarioStartTime,scenarioEndTime,scenarioDuration;
	
	
	public String getScenarioStartTime() {
		return scenarioStartTime;
	}


	public void setScenarioStartTime(String scenarioStartTime) {
		this.scenarioStartTime = scenarioStartTime;
	}


	public String getScenarioEndTime() {
		return scenarioEndTime;
	}


	public void setScenarioEndTime(String scenarioEndTime) {
		this.scenarioEndTime = scenarioEndTime;
	}


	public String getScenarioDuration() {
		return scenarioDuration;
	}


	public void setScenarioDuration(String scenarioDuration) {
		this.scenarioDuration = scenarioDuration;
	}


	public ScenarioDetails() {
		
		stepList = new ArrayList<Document>() ;
	}
	
	
	public String getScName() {
		return scName;
	}
	public void setScName(String scName) {
		this.scName = scName;
	}
	public String getScStatus() {
		return scStatus;
	}
	public void setScStatus(String scStatus) {
		this.scStatus = scStatus;
	}
	public List<Document> getStepList() {
		return stepList;
	}
	public void setStepList(List<Document> stepList) {
		this.stepList = stepList;
	}

}
