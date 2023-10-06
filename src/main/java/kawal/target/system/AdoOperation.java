package kawal.target.system;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ksb.qametrics.configreader.ReportFileReader;
import com.ksb.qametrics.cucumberplugin.BugDetails;
import com.ksb.qametrics.cucumberplugin.StepsDetails;
import com.ksb.qametrics.cucumberplugin.TestPointDetails;
import com.ksb.qametrics.utility.StringOperation;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class AdoOperation {
	
	public static  RequestSpecification httpRequest=null;
	public static Response response=null;
	Properties prop;
	private  HashSet<Integer> allSuitewtc;
	private JSONArray  testCasesArr = null;
	private JSONObject results=null;
	private List<TestPointDetails> testAutoSuitetestCases;
	private static int runId=0;
	private  int planId,suiteId=0;
	private static  String USER_STORY_ID="0" ;
	private String org_name;
	private String proj_name;
	private String auth_key;
	private String ado_baseuri;
	int parent_story_id=0;
	List <Integer>openBugIds;
	boolean bugcapture=false;
	List<BugDetails> bugDetailsList;
	int numbugs=0;
	
	 

	
	public AdoOperation() {
		
		
		
		
  try {
			  
			  
			  prop = new Properties();

				try {
					
					prop=ReportFileReader.getPropertyFile("\\src\\test\\resources\\ReportDB.properties");
					allSuitewtc=new HashSet<Integer>();
					testAutoSuitetestCases=new ArrayList<TestPointDetails>();
					openBugIds=new ArrayList<Integer>();
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
	
		    	      
		      
		  //    testDb = MongoClients.create(mongoUri).getDatabase("testDb");

		    } catch (Exception e) {
		      prop = null;
		    }
		
	}
	
	
	public RequestSpecification get_httpRequest_Ref()
	{
		

		httpRequest=null;

		if(httpRequest==null)
		{
		
			System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2"); 
             org_name=prop.getProperty("ado.org.name");
             proj_name=prop.getProperty("ado.proj.name");
             auth_key="Basic "+prop.getProperty("ado.auth.key");
			 ado_baseuri = "https://dev.azure.com/"+org_name+"/"+proj_name+"/_apis/";
			RestAssured.baseURI = ado_baseuri;
			httpRequest = RestAssured.given();
			httpRequest.urlEncodingEnabled(false);
			httpRequest = RestAssured.given().relaxedHTTPSValidation();
			
			httpRequest.header("Authorization", auth_key);
//			httpRequest.log().all();
		}
		else
		{
			//System.out.println(" HttpRequest is not null");
		}

		return httpRequest;
	}
	
	public  Response getHttpResponse(String endpoint_path)
	{   
		Response resp = get_httpRequest_Ref().get(endpoint_path);
//		System.out.println("Orig Resp==>"+resp.asPrettyString());
		return resp;
	}
	
	public boolean validateTestSuiteInADO() {
		
		try {
			
			planId=Integer.parseInt(prop.getProperty("ado.planid"));
			suiteId=Integer.parseInt(prop.getProperty("ado.suiteid"));
//			System.out.println("Suite id is "+suiteId);
//			System.out.println("Plan id is "+planId);
			
			if(planId>0 && suiteId>0) {
				
				getAllSuiteWithTestCases(suiteId);
				
			//	System.out.println("Suite with test cases under regression");				
				for (int sid:allSuitewtc) {
                 
				}
				
				for (int sid:allSuitewtc) {
					
					
					getTestCasesDetailsUsingPlanAndSuiteID(planId,sid );	
				}
				
				
				
				return true;
				
			}else {
				
				return false;
			}
			
			
			
			
			
		}catch(Exception e) {return false;}
	}
	
	
		
	
	public boolean createTestRunInADO() {
		try {
			
			String runBody="{\"name\": \"Automated Regression Run\",\"plan\": {\"id\": \""+planId+"\"},\"isAutomated\": true}";
		response=get_httpRequest_Ref().body(runBody).header("Content-Type","application/json").post("test/runs?api-version=7.1-preview.3");	
		
		//System.out.println("Response code is==>"+response.statusCode());
		if (response.getStatusCode()==200) {
			runId=response.jsonPath().getInt("id");
			
		//	System.out.println("Run started with run id : "+runId);
			
			if(runId!=0) {
			return true;}
			else {
				return false;
			}
		}else
		{return false;}
		
		}catch(Exception e) {
			return false;
			}
	}
	
	
	
	public boolean addTestResultsToRunIdInADO(int runId,String testCaseTitle,int testCasePoint,String testStatus) {
		try {
			
			String runBody="[{ \"testCaseTitle\": \""+testCaseTitle+"\", \"testPoint\": { \"id\": "+testCasePoint +"}, \"outcome\": \""+testStatus+"\", \"state\":\"Completed\" } ]";
		response=get_httpRequest_Ref().body(runBody).header("Content-Type","application/json").post("test/Runs/"+runId+"/results?api-version=5.0-preview.5");	
		
		//System.out.println("Response code is==>"+response.statusCode());
		if (response.getStatusCode()==200) {
			//System.out.println("response is==>"+response.prettyPrint());
			return true;
		}else
		{return false;}
		
		}catch(Exception e) {
			return false;
			}
	}
	public boolean completeTestRunInADO() {
		try {
			
			String runBody="{\r\n"
					+ "  \"state\": \"Completed\"\r\n"
					+ " \r\n"
					+ "}";
		response=get_httpRequest_Ref().body(runBody).header("Content-Type","application/json").patch("test/runs/"+runId+"?api-version=5.1");	
		
		//System.out.println("Response code is==>"+response.statusCode());
		if (response.getStatusCode()==200) {
			//System.out.println("response is==>"+response.prettyPrint());
			return true;
		}else
		{return false;}
		
		}catch(Exception e) {
			return false;
			}
	}
	
	
	public boolean getTestDetailsSuite() {
		
		try {
			response=getHttpResponse("/testplan/Plans/389503/Suites/391684/TestCase");	
			
	
			if (response.statusCode()==200) {
		      
				return true;
			}else
			{return false;}
			
			}catch(Exception e) {
				return false;
				}		
		
	}
	
	
public boolean getAllSuiteWithTestCases(int suiteId) {
		
		try {
			
//			int suiteId=389511;
			response=getHttpResponse("/testplan/suiteentry/"+suiteId+"?api-version=5.0");
			
			
			List<Integer> testIdsList=new ArrayList<Integer>();
			List<Integer> tempSuiteIds=new ArrayList<Integer>();
			
			int count=response.jsonPath().getInt("count");
			//System.out.println("COunt is "+count);
			
			
	
			if (!allSuitewtc.contains(suiteId)) {
				
				
				testIdsList.addAll(response.path("value.findAll { it.suiteEntryType== 'testCase' }.id"));				
			
				if(testIdsList.size()>0) {
					
					allSuitewtc.add(suiteId);
				}
				
				tempSuiteIds.addAll(response.path("value.findAll { it.suiteEntryType== 'suite' }.id"));
				
				
				
				for (int sid:tempSuiteIds) {
					getAllSuiteWithTestCases(sid);
				}
				
				
				
			}
			
//			for (int sid:allSuitewtc) {
//				System.out.println(sid);
//			}
			
			
			
			return true;
			}catch(Exception e) {
				return false;
				}		
		
	}
	

public boolean getTestCasesDetailsUsingPlanAndSuiteID(int planId,int suiteId ) {
	
	try {
		
//		 suiteId=389512;
//		 planId=389503;
		response=getHttpResponse("testplan/Plans/"+planId+"/Suites/"+suiteId+"/TestCase");
	//	System.out.println("Response is =="+response.asString());
		
		
		
		int count=response.jsonPath().getInt("count");
	//	System.out.println("Count is "+count);
		
		results = new JSONObject(response.asString());
		
		if(count>0) {
			
			testCasesArr=results.getJSONArray("value");
		}
		
		

		for (int i=0;i<testCasesArr.length();i++) {
			
			TestPointDetails tpd=new TestPointDetails();
			
			tpd.setTestPlanId(planId);
			tpd.setTestSuiteId(suiteId);
			tpd.setTestSuiteName(testCasesArr.getJSONObject(i).getJSONObject("testSuite").getString("name"));
			tpd.setTcId(testCasesArr.getJSONObject(i).getJSONObject("workItem").getInt("id"));			
			tpd.setParentId(gettestCaseParentUserStoryId(testCasesArr.getJSONObject(i).getJSONObject("workItem").getInt("id")));
			tpd.setTestCaseName(testCasesArr.getJSONObject(i).getJSONObject("workItem").getString("name"));
			tpd.setTestPointProcessed("NO");
			JSONArray testPoints=testCasesArr.getJSONObject(i).getJSONArray("pointAssignments");
			List<Integer> testPointIds=new ArrayList<Integer>();
			
			for(int j=0;j<testPoints.length();j++) {
				
				testPointIds.add(testPoints.getJSONObject(j).getInt("id"));				
				
			}
			
			tpd.setTestCasePoints(testPointIds);
			testAutoSuitetestCases.add(tpd);
		}
		
//		System.out.println(testAutoSuitetestCases.get(0).getTestCasePoints());
//		System.out.println(testAutoSuitetestCases.get(0).getTestCaseName());
		
		return true;
		}catch(Exception e) {
			return false;
			}		
	
}


public Integer getTestCasepointForScenario(String scenarioName) {
	
  int tcPoint=0;
	
	
	try {
		 for (int i=0;i<testAutoSuitetestCases.size();i++) {
			 
			 if(testAutoSuitetestCases.get(i).getTestCaseName().trim().equals(scenarioName) && testAutoSuitetestCases.get(i).getTestPointProcessed().equals("NO") )
			 {
				 tcPoint=testAutoSuitetestCases.get(i).getTestCasePoints().get(0);
				 parent_story_id=testAutoSuitetestCases.get(i).getParentId();
				 testAutoSuitetestCases.get(i).setTestPointProcessed("YES");
				 break;
			 }
		 }

		return tcPoint;
		
		
	}catch(Exception e) {return 0;}
	
	
}


public boolean updateTestResultseinADO(String scenarioName,String scenarioStatus) {
	
	boolean result=true;
	int testCasePoint=getTestCasepointForScenario(scenarioName);
//	System.out.println("============================Test Case point ====================="+testCasePoint);
	
	try {
		result=addTestResultsToRunIdInADO(runId,scenarioName,testCasePoint,scenarioStatus);
		return result;
		
	}catch(Exception e) {return false;}
	
	
}

public int createBugInADO(String scenarioName,List<StepsDetails> stepDetailsList) {
	int bugId=0;
	
	int maxbug=0;
	String maxbugprop=prop.getProperty("ado.max.run.bugs");
	if (!StringUtils.isBlank(maxbugprop)) { 
		maxbug=Integer.valueOf(maxbugprop);
		
	}
	
	
	if(!bugcapture) {
		
		openBugIds=getOpenBugIds();
		
		bugcapture=true;
		bugDetailsList=new ArrayList<BugDetails>();
		
		
		for (int id:openBugIds) {
			
			
			bugDetailsList.add(getBugDetails(id));			
			
		}
		
	}
	
	boolean bugExists = false;
	
    for (BugDetails bug : bugDetailsList) {
        if (StringOperation.containsConsecutiveWords(bug.getTitle(), scenarioName, 5)) {
        	bugId=bug.getId();
            bugExists = true;
            break;
        }
    }

    // Print the result
    if (bugExists) {
        System.out.println("A similar bug with id " + bugId + " was found.Hence no new bug is raised for the failed scenario");
        return bugId;
    } else {
    	
    	if(maxbug>numbugs) {     
    
	
	String bugTitle="[Test Automation] "+scenarioName+ " failed";
	String bugDescription="While executing  scenario,getting error at the step : ";
	
	String startbugbody="[ ";
	String endbugbody=" ]";
	String failedStep="";
	String stepDetails="";
	String error="";
	String stepstoReproduce="";
	String expectedBehaviour;
	String actualBehaviour;
	String tags="Automation";
	String newline = System.lineSeparator(); 
//	String assignedToEmailID="kawaljeet_singhbhatia@ryder.com";
	String areapath,iteration,assignedToEmailID = null;
	String requestBody;
	
	
	
	for(StepsDetails sd:stepDetailsList) {
		
		stepDetails=stepDetails+sd.getStName()+" ";
		stepstoReproduce=stepstoReproduce+sd.getStName()+"--"+sd.getStStatus()+"<br>";
//		System.out.println("StepDetails="+stepDetails);
		
		if(sd.getStStatus().equalsIgnoreCase("Failed")) {
			failedStep=sd.getStName() +"--Failed";
//			System.out.println("failestep="+failedStep);
			error=sd.getStepErrorDetails();
//			System.out.println("Error ="+error);
			
		}
		
		
	}
	expectedBehaviour=failedStep +" should pass successfully";
	actualBehaviour="Getting error while executing step: "+failedStep+" with error as :"+"<br>"+error;
	
	bugDescription=bugDescription+ failedStep+"<br>"+error;
	bugDescription=bugDescription.replaceAll("\"", "'");
	stepstoReproduce=stepstoReproduce.replaceAll("\"", "'");
	expectedBehaviour=expectedBehaviour.replaceAll("\"", "'");
	actualBehaviour=actualBehaviour.replaceAll("\"", "'");

	
	requestBody=startbugbody
			        + " { \"op\": \"add\", \"path\": \"/fields/System.Title\", \"value\": \""+bugTitle+"\" },"
					+ " { \"op\": \"add\", \"path\": \"/fields/System.Description\", \"value\": \""+bugDescription+"\" },"
					+ " { \"op\": \"add\", \"path\": \"/fields/Microsoft.VSTS.TCM.ReproSteps\", \"value\": \""+stepstoReproduce+"\" },"
					+ " { \"op\": \"add\", \"path\": \"/fields/Custom.ExpectedBehavior\", \"value\": \""+expectedBehaviour+"\" },"
					+ " { \"op\": \"add\", \"path\": \"/fields/Custom.ActualBehavior\", \"value\": \""+actualBehaviour+"\" },"
					+ " { \"op\": \"add\", \"path\": \"/fields/System.Tags\", \"value\": \""+tags+"\" },";
	
	assignedToEmailID=prop.getProperty("ado.assignedto.email");
	if(assignedToEmailID.length()>0) {
		
		requestBody=requestBody
				+ " { \"op\": \"add\", \"path\": \"/fields/System.AssignedTo\", \"value\": \""+assignedToEmailID+"\" },";		
	}
	
	areapath=prop.getProperty("ado.bug.areapath");
//	System.out.println("Area path ==>"+areapath);
if(areapath.length()>0) {
			areapath=areapath.replaceAll("\\\\", "\\\\\\\\");
	
		requestBody=requestBody
				+ " { \"op\": \"add\", \"path\": \"/fields/System.AreaPath\", \"value\": \""+areapath+"\" },";		
	}
	

iteration=prop.getProperty("ado.bug.iteration");
//System.out.println("Iteration is "+iteration);

if(iteration.length()>0) {
	
	if(iteration.toLowerCase().contains("@current")) {
		String team_name=prop.getProperty("ado.teams.name");
		team_name=team_name.replaceAll(" ", "%20");
		
		if(team_name.length()>0) {
			iteration=getCurrentIteration(team_name);
			iteration=iteration.replaceAll("\\\\", "\\\\\\\\");
			
			requestBody=requestBody
					+ " { \"op\": \"add\", \"path\": \"/fields/System.IterationPath\", \"value\": \""+iteration+"\" },";			
		                         }		
		
	                                                }	else {
	                                                	
	                                                	iteration=iteration.replaceAll("\\\\", "\\\\\\\\");
	                                        		  			requestBody=requestBody
	                                        					+ " { \"op\": \"add\", \"path\": \"/fields/System.IterationPath\", \"value\": \""+iteration+"\"},";	
	                                                	
	                                                	
	                                                }
		
	                      }

	USER_STORY_ID=prop.getProperty("ado.regression.userstoryid");
//	System.out.println("USER_STORY_ID="+USER_STORY_ID);
	
	if(USER_STORY_ID.length()>0 && USER_STORY_ID!="0") {
		
		
		if(USER_STORY_ID.toLowerCase().contains("link") || USER_STORY_ID.toLowerCase().contains("parent") || USER_STORY_ID.toLowerCase().contains("story") ) {
			
			requestBody=requestBody
					+ " { \"op\": \"add\", \"path\": \"/relations/-\", \"value\": { \"rel\": \"System.LinkTypes.Hierarchy-Reverse\", \"url\": \""+ado_baseuri+"wit/workItems/"+parent_story_id+"\", \"attributes\": { \"comment\": \"Linking bug to user story\" } } }";
			
		}else if(validateWorkItemExistsinADO(USER_STORY_ID)) {
			requestBody=requestBody
					+ " { \"op\": \"add\", \"path\": \"/relations/-\", \"value\": { \"rel\": \"System.LinkTypes.Hierarchy-Reverse\", \"url\": \""+ado_baseuri+"wit/workItems/"+USER_STORY_ID+"\", \"attributes\": { \"comment\": \"Linking bug to user story\" } } }";
		
			
		                             }
		}
	
	requestBody=requestBody+endbugbody;
	
		response=get_httpRequest_Ref()
        		.contentType("application/json-patch+json")
                .body(requestBody)
//                .log().all()
                .post("wit/workItems/$Bug?api-version=6.0");
		
//		System.out.println("response is==>"+response.asPrettyString());
				
				
		try {
			bugId=response.path("id");
			bugDetailsList.add(new BugDetails(bugId,bugTitle,bugDescription));
			numbugs=numbugs+1;
			System.out.println("Bug raised for failed scenario - "+scenarioName+" and bugid is:"+bugId);
			
		}catch(Exception e) {
			System.out.println("There was an issue creating bug for failed scenario: "+scenarioName+" and error is: "+e);
		}

    	}
	
	return bugId;
    }
    
}


public  StepsDetails addStepsDetails(String stName,String stStatus,String error) {
	  
	  StepsDetails st=new StepsDetails();
		
		st.setStName(stName);
		st.setStStatus(stStatus);
		st.setStepErrorDetails(error);
				
		return st;	
	  
}

public String getCurrentIteration(String teamName) {
	
	String baseuri= "https://dev.azure.com/"+org_name+"/"+proj_name+"/"+teamName+"/_apis";
	String currentSprint="";
	
	RestAssured.baseURI = baseuri;
	httpRequest = RestAssured.given();
	httpRequest.urlEncodingEnabled(true);
	httpRequest = RestAssured.given().relaxedHTTPSValidation();	
	httpRequest.header("Authorization", auth_key);
//	httpRequest.log().all();
	
	
	Response resp=httpRequest.get("/work/teamsettings/iterations?$timeframe=current&api-version=6.0");

	
	currentSprint=resp.path("value[0].path");	
	return currentSprint;
	
	
	
}

public boolean validateWorkItemExistsinADO(String userstoryid) {
	
	Response resp=get_httpRequest_Ref().get("/wit/workitems/"+userstoryid);
	if(resp.statusCode()==200) {
		return true;
	}else {
		return false;
	}
}

public int gettestCaseParentUserStoryId(int tcid) {
	
	int storyid=0;
	
	Response resp=get_httpRequest_Ref().get("wit/workitems/"+tcid+"?$expand=all&api-version=6.0");
	
	if(resp.statusCode()!=200) {
		storyid=0;
	}
	
	try {
		String storyUrl=resp.path("relations[0].url");	
		
		storyid=Integer.parseInt(storyUrl.split("workItems/")[1]);
		
		
	}
	catch(Exception e) {
		storyid=0;
	}
	
	return storyid;
	
	
	
}

public List<Integer> getOpenBugIds() {
	
	List <Integer>bugIds=new ArrayList<Integer>();
	String areaPath=prop.getProperty("ado.bug.areapath");
	areaPath=areaPath.replaceAll("\\\\", "\\\\\\\\");
	
	String reqBody="{ \"query\": \" Select [System.Id], [System.Title], [System.State] From WorkItems Where [System.WorkItemType] = 'Bug' and [System.State]!='Done' and   [System.State] !='Removed' and [System.AreaPath]='"+areaPath+"' \"}";
	try {
	Response resp=get_httpRequest_Ref()
    		.contentType("application/json")
            .body(reqBody)
//            .log().all()
            .post("wit/wiql?api-version=5.1");
	
	
		
	bugIds.addAll(resp.path("workItems.id"));
	
	
	}catch(Exception e) {
		
	}
	
	return bugIds;
	
	
	
	
}


public BugDetails getBugDetails(int bugid) {
	
	Response resp=get_httpRequest_Ref().get("wit/workitems/"+bugid+"?$expand=all&api-version=6.0");
	
	if(resp.statusCode()==200) {
		
		String bugTitle=resp.path("fields.'System.Title'");
	
		String bugDescription=resp.path("fields.'System.Description'");

		return new BugDetails(bugid,bugTitle,bugDescription);
	  	
		
	}else {
		return null;
	}
	
	
	
	
}

	
}
