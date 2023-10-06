package kawal.target.system;

import static com.mongodb.client.model.Updates.set;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.ksb.qametrics.configreader.ReportFileReader;
import com.ksb.qametrics.cucumberplugin.FeatureDetails;
import com.ksb.qametrics.cucumberplugin.ScenarioDetails;
import com.ksb.qametrics.cucumberplugin.StepsDetails;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

public class MongoDBOperation {
	
	protected MongoDatabase db;
	  protected MongoDatabase testDb;
	  protected MongoCollection<Document> statusCollection;
	  Properties prop;
	  
	  
	  
	  public MongoDBOperation() {
		  
		  
		  try {
			  
			  
			

				
					
					prop=ReportFileReader.getPropertyFile("\\src\\test\\resources\\ReportDB.properties");
					
			
	
		      String mongoUri = prop.getProperty("mongodb.uri");
		      String databaseName = prop.getProperty("mongodb.dbname");
		      String collectionName=prop.getProperty("mongodb.collectionname");
		      db = MongoClients.create(mongoUri).getDatabase(databaseName);
		      statusCollection = db.getCollection(collectionName);
		   
		      
		      
		  //    testDb = MongoClients.create(mongoUri).getDatabase("testDb");

		    } catch (Exception e) {
		      this.db = null;
		    }
	  }
	  
	  public  Document addStepsDetails(String stName,String stStatus,String stStartTime,String stEndTime,String stDuration) {
		  
		  StepsDetails st=new StepsDetails();
			
			st.setStName(stName);
			st.setStStatus(stStatus);
			st.setStepStartTime(stStartTime);
			st.setStepEndTime(stEndTime);
			st.setStepDuration(stDuration);
			Document d=new Document();
			d.put("StepName", st.getStName());
			d.append("StepStatus", st.getStStatus());
			d.append("StepStartTime", st.getStepStartTime());
			d.append("StepEndTime", st.getStepEndTime());
			d.append("StepDuratione", st.getStepDuration());			
			return d;	
		  
	  }
	  
	  
	  public  Document addFailStepsDetails(String stName,String stStatus,String failReason,String stStartTime,String stEndTime,String stDuration) {
		  
		  StepsDetails st=new StepsDetails();
			
			st.setStName(stName);
			st.setStStatus(stStatus);
			st.setStepErrorDetails(failReason);
			st.setStepStartTime(stStartTime);
			st.setStepEndTime(stEndTime);
			st.setStepDuration(stDuration);
			Document d=new Document();
			d.put("StepName", st.getStName());
			d.append("StepStatus", st.getStStatus());
			d.append("StepFailReason", st.getStepErrorDetails());
			d.append("StepStartTime", st.getStepStartTime());
			d.append("StepEndTime", st.getStepEndTime());
			d.append("StepDuratione", st.getStepDuration());			
			return d;	
		  
	  }
	  
 public  Document addScenarioDetails(String scName,String scStatus,List<String> tags,String scStartTime,String scEndTime,String scDuration,List<Document> stepList) {
		  
		  ScenarioDetails sc=new ScenarioDetails();
			
			sc.setScName(scName);
			sc.setScStatus(scStatus);
			sc.setScenarioStartTime(scStartTime);
			sc.setScenarioEndTime(scEndTime);
			sc.setScenarioDuration(scDuration);
			sc.setTagList(tags);
			Document d=new Document();
			d.put("Scenario", sc.getScName());
			d.append("ScenarioStatus", sc.getScStatus());
			d.append("Tags", tags);
			d.append("SecnarioStartTime", sc.getScenarioStartTime());
			d.append("SecnarioEndTime", sc.getScenarioEndTime());
			d.append("SecnarioDuration", sc.getScenarioDuration());			
			d.append("Steps",stepList);
			return d;	
		  
	  }
 
 public  Document addFeatureDetails(String fID,String fName,String fStatus,String fstartTime,int runID,List<Document> scenarioList) {
	  
	  FeatureDetails f=new FeatureDetails();
		f.setFeatureId(fID);
		f.setfName(fName);
		f.setfStatus(fStatus);
		f.setFeatureStartTime(fstartTime);
		f.setScenarioList(scenarioList);
		f.setRunId(runID);
		
		Document d=new Document();
		d.put("FeatureId", f.getFeatureId());
		d.put("FeatureName", f.getfName());
		d.append("FeatureStatus", f.getfStatus());
		d.append("FeatureStartTime", f.getFeatureStartTime());
		d.put("RunID",f.getRunId());
		d.append("Scenarios",scenarioList);
		 return d;
	  
 }
 
 public  ObjectId insertDocumentDB(Document d) {
	 statusCollection.insertOne(d);
	 System.out.println("inside insert function,succesfull");
	 ObjectId id = d.getObjectId( "_id" );
	 System.out.println("id is ==>"+id.toString());
	 return id;
	 	  
}
	
 
 public boolean featureExists(String featureId) {
	 
	 long count = statusCollection.count(new BsonDocument("FeatureId", new BsonString(featureId)));
	 
	 if(count>0) {
		 return true;
	 }else {
		 
		 return false;
	 }
 }
 
 public void updateDocument (ObjectId objectID,Document scenarioDocument) {
	 
	 Bson filter = Filters.eq("_id", objectID);
	 Bson update = Updates.push("Scenarios", scenarioDocument);
	 FindOneAndUpdateOptions options = new FindOneAndUpdateOptions()
	                                     .returnDocument(ReturnDocument.AFTER);
	 Document result = statusCollection.findOneAndUpdate(filter, update, options);
	 System.out.println("Result of update query:"+result.toJson());
 }
 
public void insertFieldDocument (ObjectId objectID,String key,String value) {
	
	    BasicDBObject setNewFieldQuery = new BasicDBObject().append("$set", new BasicDBObject().append(key, value));
	    UpdateResult result = statusCollection.updateOne(new BasicDBObject().append("_id", objectID), setNewFieldQuery);
	    
	    System.out.println("Succesfully updated feature field :"+result.wasAcknowledged());
	
	 
//	 Bson filter = Filters.eq("_id", objectID);
//	 Bson update=Updates.set(field, value);
//	 
//	 UpdateResult result =statusCollection.updateOne(filter, update);
//	
//	 System.out.println("Succesfully updated feature field :"+result.wasAcknowledged());
 }
 
 public void updateDocumentFieldValue (ObjectId objectID,String fStatus) {
	 
	 Bson filter = Filters.eq("_id", objectID);
	 statusCollection.updateOne(filter, set("FeatureStatus", fStatus));
//	 Document result = statusCollection.findOneAndUpdate(filter, update, options);
//	 System.out.println("Result of update query:"+result.toJson());
 }
 
 public int getLastrunID() {
	 try {
	 FindIterable<Document> it= statusCollection.find().sort(new BasicDBObject( "RunID" , -1 )).limit(1);
	 
	 int runid=(int) it.first().get("RunID");
	 
	 System.out.println("runid is "+runid);
	 
	 return runid;
	 }catch(Exception e) {
		 return 0;
	 }
	 

 }
 
 

	public static void main(String[] args) {
		// TODO Auto-generated method stub\
//		MongoDBOperation caw=new MongoDBOperation();
//		caw.addStepsDetails("Step1","Pass");
//		caw.addStepsDetails("Step2","Fail");
//		
//		caw.addScenarioDetails("Scenario1","Fail",stepList);
//		
//		Document d=caw.addFeatureDetails("F001","Sample Feature Name","Pass",scenarioList);
//		caw.insertDocumentDB(d);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
//		
//		
//		
//		
//		
//		
//		Document testObject = new Document();
//	    testObject.put("FeatureName", "HomePageUI");
//	    testObject.put("status", "Passed");
//	    testObject.put("Tags", "SmokeGUI,RegressionGUI");
//	    
//	       
//	      
//		Document scenarioname = new Document();
//	    scenarioname.put("ScenarioName","Validate promo element");
//	    scenarioname.put("ScenarioStatus","Passed");
//	    
//	 
//	    
//	    Document stepName = new Document();
//	    stepName.put("StepName", "Validate promo step");
//	    stepName.put("StepStatus", "Passed");
//
//	    List<Document> stepList = new ArrayList<>();
//	    stepList.add(stepName);    
//	    scenarioname.append("steps", stepList);
//	    
//	    List<Document> scenarioList = new ArrayList<>();
//	    scenarioList.add(scenarioname);
//	    testObject.append("Scenarios", scenarioList);
//	  
//
//
//
//	      caw.statusCollection.insertOne(d);
		
				
		

	}

}
