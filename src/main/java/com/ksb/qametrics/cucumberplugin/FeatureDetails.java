package com.ksb.qametrics.cucumberplugin;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

public class FeatureDetails {

	
	 String fName;
	 String fStatus;
	 String fTags;
	 String featureId;
	 String featureStartTime,featureEndTime,featureDuration;
	 public String getFeatureStartTime() {
		return featureStartTime;
	}



	public void setFeatureStartTime(String featureStartTime) {
		this.featureStartTime = featureStartTime;
	}



	public String getFeatureEndTime() {
		return featureEndTime;
	}



	public void setFeatureEndTime(String featureEndTime) {
		this.featureEndTime = featureEndTime;
	}



	public String getFeatureDuration() {
		return featureDuration;
	}



	public void setFeatureDuration(String featureDuration) {
		this.featureDuration = featureDuration;
	}
	int runId;
	 
	 public int getRunId() {
		return runId;
	}



	public void setRunId(int runId) {
		this.runId = runId;
	}



	public String getFeatureId() {
		return featureId;
	}



	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}
	List <Document> scenarioList;
	 
	 
	 public List<Document> getScenarioList() {
		return scenarioList;
	}



	public void setScenarioList(List<Document> scenarioList) {
		this.scenarioList = scenarioList;
	}



	public FeatureDetails() {
		 
		 scenarioList=new ArrayList<Document>(); 
	 }
	 
	 
	 
	public String getfName() {
		return fName;
	}
	public void setfName(String fName) {
		this.fName = fName;
	}
	public String getfStatus() {
		return fStatus;
	}
	public void setfStatus(String fStatus) {
		this.fStatus = fStatus;
	}
	public String getfTags() {
		return fTags;
	}
	public void setfTags(String fTags) {
		this.fTags = fTags;
	}
}
