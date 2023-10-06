package com.ksb.qametrics.cucumberplugin;

import static io.cucumber.gherkin.Gherkin.makeSourceEnvelope;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.aventstack.extentreports.service.ExtentService;
import com.ksb.qametrics.configreader.ReportFileReader;

import io.cucumber.gherkin.Gherkin;
import io.cucumber.messages.Messages;
import io.cucumber.messages.Messages.GherkinDocument;
import io.cucumber.messages.Messages.GherkinDocument.Feature;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Background;
import io.cucumber.messages.Messages.GherkinDocument.Feature.FeatureChild;
import io.cucumber.messages.Messages.GherkinDocument.Feature.FeatureChild.RuleChild;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Scenario;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Scenario.Examples;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Step;
import io.cucumber.messages.Messages.GherkinDocument.Feature.TableRow;
import io.cucumber.messages.internal.com.google.protobuf.GeneratedMessageV3;
import io.cucumber.messages.internal.com.google.protobuf.Message;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestRunFinished;
import io.cucumber.plugin.event.TestRunStarted;
import io.cucumber.plugin.event.TestSourceRead;
import io.cucumber.plugin.event.TestStepFinished;
import io.cucumber.plugin.event.TestStepStarted;
import kawal.target.system.AdoOperation;
import kawal.target.system.MongoDBOperation;
import kawal.target.system.SQLServerOperation;
import io.cucumber.plugin.Plugin;

public class CustomReportListener implements ConcurrentEventListener,Plugin {

	MongoDBOperation mdbo;
	SQLServerOperation sqlo;
	AdoOperation adoo;
	List<Document> stepList;
	List<String> stepListadd;
	List<StepsDetails> stepListexec,stepListAdo;
	List<Document> scenarioList;
//	private final TestSourcesModel testSources = new TestSourcesModel();
	String stepName = " ";
	String keyword = "Triggered the hook :";
	String featureName = "";
	String featureStatus = "PASSED";
	String scenarioName = "";
	String scenarioStatus = "PASSED";
	int fNumber = 0;
	String featureId;
	boolean insertFeature,firstFeature;
	ObjectId id = null;

	private final Map<URI, TestSourceRead> pathToReadEventMap = new HashMap<>();
	private final Map<URI, GherkinDocument> pathToAstMap = new HashMap<>();
	private final Map<URI, Map<Integer, AstNode>> pathToNodeMap = new HashMap<>();
	List<URI> featureList;
	String prevfeatureStatus = "PASSED";
	int runId = 0;
	String featureStartTime, featureEndTime, featureDuration;
	String scenarioStartTime, scenarioEndTime, scenarioDuration;
	String stepStartTime, stepEndTime, stepDuration;
	List<String> tagList;
	Properties prop;
	String dbName,adoUpdate,adoCreateBug;
	boolean mongodb,mssql,ado,adobugflag=false;
	boolean adolaststep=true;
	String runStartTime,runEndTime,runDuration;
	List<String> sqlFeatureList,sqlScenarioList,sqlStepList;
	int featurefk;
	String featureFileName,sqlfeatureFileName;
	
	
	public CustomReportListener(String arg) {
//		ExtentService.getInstance();
		CustomReportListenersetup();
	}

	public void CustomReportListenersetup() {
		
		prop=ReportFileReader.getPropertyFile("\\src\\test\\resources\\ReportDB.properties");
		
		adoUpdate=prop.getProperty("ado.update");		
		System.out.println("Ado update flag is set as: "+adoUpdate);
		dbName=prop.getProperty("report.db");
		
		if (dbName.equalsIgnoreCase("MSSQL")) {
	    	   mssql=true;
	    	   mongodb=false;
	    	   sqlo=new SQLServerOperation();
	    	   sqlFeatureList=new ArrayList<String>();
	    	   sqlScenarioList=new ArrayList<String>();
	    	   sqlStepList=new ArrayList<String>();
	       }else if(dbName.equalsIgnoreCase("MONGODB")) {
	    	   mongodb=true;
	    	   mssql=false;
	    	   mdbo = new MongoDBOperation();
	       }else {
	    	   
	    	   mongodb=false;
	    	   mssql=false;
	    	   
	       }
		
		
		if(adoUpdate.equalsIgnoreCase("YES") || adoUpdate.equalsIgnoreCase("TRUE")){
			
			ado=true;
			adoo=new AdoOperation();
			
			
			adoCreateBug=prop.getProperty("ado.createbug");
			
			 if(adoCreateBug.equalsIgnoreCase("YES") || adoCreateBug.equalsIgnoreCase("TRUE"))
			 {				 
				 
				 adobugflag=true;
				 adoo.getOpenBugIds();
			 }else {
				 adobugflag=false;
			 }
			
		}else {
			ado=false;
		}
		
		

	};

	@Override
	public void setEventPublisher(EventPublisher publisher) {
		// TODO Auto-generated method stub

		/*
		 * :: is method reference , so this::collecTag means collectTags method in
		 * 'this' instance. Here we says runStarted method accepts or listens to
		 * TestRunStarted event type
		 */
		publisher.registerHandlerFor(TestRunStarted.class, this::runStarted);
		publisher.registerHandlerFor(TestRunFinished.class, this::runFinished);
		publisher.registerHandlerFor(TestSourceRead.class, this::featureRead);
		publisher.registerHandlerFor(TestCaseStarted.class, this::ScenarioStarted);
		publisher.registerHandlerFor(TestCaseFinished.class, this::ScenarioFinished);
		publisher.registerHandlerFor(TestStepStarted.class, this::stepStarted);
		publisher.registerHandlerFor(TestStepFinished.class, this::stepFinished);

	};

	/*
	 * Here we set argument type as TestRunStarted if you set anything else then the
	 * corresponding register shows error as it doesn't have a listner method that
	 * accepts the type specified in TestRunStarted.class
	 */

	// Here we create the reporter
	private void runStarted(TestRunStarted event) {
		
		
	       
		featureList = new ArrayList<URI>();
		firstFeature=true;
		runStartTime=getTimeinESTTimeZone(event.getInstant());
		
		if(mssql) {
			runId=sqlo.getLastrunID()+1;
			sqlFeatureList=sqlo.getFeatureList();
		//	System.out.println("sql feature List=>");
			sqlFeatureList.forEach(System.out::println);
			sqlScenarioList=sqlo.getScenarioList();
			System.out.println("sql Scenario List=>");
			sqlScenarioList.forEach(System.out::println);
			sqlStepList=sqlo.getStepList();
			System.out.println("sql Step List=>");
			sqlStepList.forEach(System.out::println);
			try {
				sqlo.updateQuery("insert into [dbo].[RunDetails] values("+runId+",'"+runStartTime+"','','','Regression','UVS')");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Succesfully inserted Run id insert condition");
			 
		}else if(mongodb) {
			
			runId = mdbo.getLastrunID() + 1;
		}
		
         if(ado) {        	 
        	 		
			adoo.validateTestSuiteInADO();
			adoo.createTestRunInADO();
			
		      }
		
		
		
		
//		System.out.println("Run id in run staretd=>"+runId);
       
       
       
     
      
      
       
		
		

	};

	// TestRunFinished event is triggered when all feature file executions are
	// completed
	private void runFinished(TestRunFinished event) {
		
		featureEndTime=scenarioEndTime;			
		featureDuration=duration(featureStartTime,featureEndTime);
		runEndTime=getTimeinESTTimeZone(event.getInstant());
		runDuration=duration(runStartTime,runEndTime);
		
		if(ado && adolaststep) {
			adoo.completeTestRunInADO();
			
		}
		
		if(mongodb) {
			
			mdbo.insertFieldDocument(id,"FeatureEndTime",featureEndTime);
			mdbo.insertFieldDocument(id,"FeatureDuration",featureDuration);
						
		}else if(mssql) {
			
			
			try {
				sqlo.updateQuery("update [dbo].[FeatureExecution] set FeatureEndTime='"+featureEndTime+"',FeatureDuration='"+featureDuration+"' where RUNID="+runId+" and FeatureID="+featurefk+"");
				sqlo.updateQuery("update [dbo].[RunDetails] set RunEndTime='"+runEndTime+"',RunDuration='"+runDuration+"' where RUNID="+runId);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			sqlo.cleanUp();
			
//			 String sqlquery="insert into [dbo].[RunDetails] values("+runId+",'"+runstartTime+"','')";
//			  try {
//			      int result= sqlo.updateQuery(sqlquery);
//			      System.out.println("result after insert is"+result);
//			       }catch(Exception e) {System.out.println(e);}
		}
		
		
		
			
		
		
	

	};

	// This event is triggered when feature file is read
	// here we create the feature node
	private void featureRead(TestSourceRead event) {
		pathToReadEventMap.put(event.getUri(), event);		
		if(pathToReadEventMap.size()==0) {
			System.out.println("There is no feature file or tag to execute");
	    	  
	    	  adolaststep=false;
	      }
		
          

//		String featureSource = event.getUri().toString();
//		System.out.println("Feature URI :"+featureSource);
//		System.out.println("event.getclass"+event.getClass());
		
//	     for (Map.Entry<URI,TestSourceRead> entry : pathToReadEventMap.entrySet()) 
//	            System.out.println("Key = " + entry.getKey() +
//	                             ", Value = " + entry.getValue());

	};

	// This event is triggered when Test Case is started
	// here we create the scenario node
	private void ScenarioStarted(TestCaseStarted event) {
		
		
		Instant ins = event.getInstant();
		scenarioStartTime = getTimeinESTTimeZone(ins);
		scenarioList = new ArrayList<Document>();
		stepList = new ArrayList<Document>();
		URI path = event.getTestCase().getUri();
		tagList=new ArrayList<String>();
		tagList=event.getTestCase().getTags();
		scenarioName = event.getTestCase().getName();
		
		Feature f = getFeature(path);
		featureName = f.getName();
		stepListadd=new ArrayList<String>();
		stepListexec=new ArrayList<StepsDetails>();
		stepListAdo=new ArrayList<StepsDetails>();
		featureFileName=event.getTestCase().getUri().toString().split(".*/")[1];
//		stepListstatus=new ArrayList<String>();
		
		

		if (!featureList.contains(path)) {

			insertFeature = true;
			featureList.add(path);
			prevfeatureStatus = "PASSED";
			featureStatus = "PASSED";
			
			
				if(firstFeature) {
					
					firstFeature=false;
				}else {
					
					featureEndTime=scenarioEndTime;
					featureDuration=duration(featureStartTime,featureEndTime);
							
					// Feature end time is set as last scenario end time
					
					if(mongodb) {
								mdbo.insertFieldDocument(id,"FeatureEndTime",featureEndTime);
								mdbo.insertFieldDocument(id,"FeatureDuration",featureDuration);
					            }else if (mssql)
					            {
					            	System.out.println("In mssql feature update===========");
					            	
									try {
										
										sqlo.updateQuery("update [dbo].[FeatureExecution] set FeatureEndTime='"+featureEndTime+"',FeatureDuration='"+featureDuration+"' where RUNID="+runId+" and FeatureID="+featurefk+"");
										System.out.println("succesfully updated feature end time");
									} catch (SQLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
					          
					            }
				     }
				
			featureStartTime=scenarioStartTime;
//			featurefk = sqlo.selectQuery("SELECT Top (1) ID FROM [dbo].[Feature] where FeatureName='"+featureName+"'");
			
				
		} else {
			insertFeature = false;

		}
		
		

		/*
		 * 
		 * event.getsiurceclass io.cucumber.plugin.event.TestSourceRead
		 * event.getTestCase().getKeyword()Scenario
		 * event.getTestCase().getName()Validate the google search functionality
		 * event.getTestCase().getScenarioDesignation()featurefile/GoogleSearch.feature:
		 * 10 # Validate the google search functionality event.getTestCase().getLine()10
		 * event.getTestCase().getId()91cfa05d-405e-4447-ac8a-27d42a3eb851
		 * event.getTestCase().getLocation()io.cucumber.plugin.event.Location@4fb
		 * event.getTestCase().getTags()[@Featuretagtest, @RegressionUI, @GoogleSearch]
		 * event.getTestCase().getTestSteps()[io.cucumber.core.runner.HookTestStep@
		 * 4b6e1c0, io.cucumber.core.runner.PickleStepTestStep@561b61ed,
		 * io.cucumber.core.runner.PickleStepTestStep@654c7d2d,
		 * io.cucumber.core.runner.PickleStepTestStep@26cb5207,
		 * io.cucumber.core.runner.PickleStepTestStep@15400fff,
		 * io.cucumber.core.runner.HookTestStep@18d910b3]
		 * event.getTestCase().getUri()classpath:featurefile/GoogleSearch.feature
		 * 
		 */

		
//		feaure description=>
//		feaure keyword=>Feature
//		feaure name=>Google search functionality
//		feaure taglist=>[location {
//		  line: 1
//		  column: 1
//		}
//		name: "@Featuretagtest"
//		id: "a3361663-4763-44a0-b8d8-26d28c9a0297"
//		]
//		

//		System.out.println("feaure description=>"+f.getDescription());
//		System.out.println("feaure keyword=>"+f.getKeyword());
//		System.out.println("feaure name=>"+f.getName());
//		System.out.println("feaure taglist=>"+f.getTagsList());
//		System.out.println("feaure childrenlist=>"+f.getChildrenList().toString());
//		System.out.println("feaure string=>"+f.toString());
	
		
	};

	private void ScenarioFinished(TestCaseFinished event) {

		Instant ins = event.getInstant();
		scenarioEndTime = getTimeinESTTimeZone(ins);
		scenarioDuration = duration(scenarioStartTime, scenarioEndTime);
		
		System.out.println("=======================in scenario finished state==============");
		if(ado && adolaststep) {
			
			adoo.updateTestResultseinADO(scenarioName,event.getResult().getStatus().toString());
			
			if(event.getResult().getStatus().toString().equalsIgnoreCase("FAILED")) {
				
				 if(adobugflag)
				 {				 
					 
				     adoo.createBugInADO(scenarioName,stepListAdo);
				 }
			}
			
		}

		

		if (insertFeature) {
			
//			System.out.println("In insert Feature condition====");
			
					
			if(mongodb){
				fNumber = fNumber + 1;
				featureId = String.format("F%03d", fNumber);
			scenarioList.add(mdbo.addScenarioDetails(scenarioName, event.getResult().getStatus().toString(),tagList,scenarioStartTime,scenarioEndTime,scenarioDuration,stepList));
//            System.out.println("Feature STart Time"+featureStartTime);
			Document d = mdbo.addFeatureDetails(featureId, featureName, featureStatus,featureStartTime, runId, scenarioList);
			id = mdbo.insertDocumentDB(d);
			}else if(mssql) {
//            System.out.println("==========starting scenario finished for mssql========");
				
				if(!sqlFeatureList.contains(featureName)) {
					try {
						fNumber=sqlo.getLastFeatureNum()+1;
						featureId = String.format("F%03d", fNumber);
//						System.out.println("===in condition feature name doe snot exist===");
						sqlo.updateQuery("insert into [dbo].[Feature] values('"+featureName+"','"+featureId+"','"+featureFileName+"')");
						System.out.println("succesfully inserted feature ");
						sqlFeatureList.add(featureName);
						
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else {
					try {
					sqlfeatureFileName=sqlo.getFeatureFileName(featureName);
					System.out.println("SQL Feature Name is =>"+sqlfeatureFileName);
					if(sqlfeatureFileName==null || !sqlfeatureFileName.equals(featureFileName))
					{
						
							sqlo.updateQuery("update [dbo].[Feature] set FeatureFileName='"+featureFileName+"' where FeatureName='"+featureName+"'");
						
					}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					
				
					
					if(!sqlScenarioList.contains(scenarioName)) {
						try {
							System.out.println("===in scenario does not exists condition===");
							int featurepk=sqlo.selectQuery("SELECT Top (1) ID FROM [dbo].[Feature] where FeatureName='"+featureName+"'");
							System.out.println("succesfully retrieved featurepk");
							
							sqlo.updateQuery("insert into [dbo].[Scenario] values('"+scenarioName+"',"+featurepk+",'"+tagList.toString()+"')");
							System.out.println("succesfully inserted Scenario in scenario table in insert feature condition");
							sqlScenarioList.add(scenarioName);
							
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
					
//					System.out.println("=====stepListadd===");
//					stepListadd.forEach(System.out::println);
					
					for (String s:stepListadd ) {
						
						if(!sqlStepList.contains(s)) {
							try {
								System.out.println("===in step does not exists condition===");
								int scenariopk=sqlo.selectQuery("SELECT Top (1) ID FROM [dbo].[Scenario] where ScenarioName='"+scenarioName+"'");	
								System.out.println("succesfully retrieved scenariopk");
								sqlo.updateQuery("insert into [dbo].[Step] values('"+s+"','"+scenariopk+"')");
								System.out.println("succesfully inserted Step in Step table in insert feature condition");
								sqlStepList.add(s);
								
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					    }
						
												
					}
					
					try {
						featurefk=sqlo.selectQuery("SELECT Top (1) ID FROM [dbo].[Feature] where FeatureName='"+featureName+"'");
						System.out.println("Retrieved featurefk in feature insert condition");
						int scenariofk=sqlo.selectQuery("SELECT Top (1) ID FROM [dbo].[Scenario] where ScenarioName='"+scenarioName+"'");
						System.out.println("Retrieved scenariofk in feature insert condition");
						
						sqlo.updateQuery("insert into [dbo].[FeatureExecution] values("+runId+",'"+featureStatus+"',"+featurefk+",'"+featureStartTime+"','','')");
						System.out.println("Succesfully inserted FeatureExecution insert condition");
						sqlo.updateQuery("insert into [dbo].[ScenarioExecution] values("+runId+",'"+event.getResult().getStatus().toString()+"',"+scenariofk+",'"+scenarioStartTime+"','"+scenarioEndTime+"','"+scenarioDuration+"')");
						System.out.println("Succesfully inserted ScenarioExecution insert condition");
						for(StepsDetails st:stepListexec){
							
							int stepfk=sqlo.selectQuery("SELECT Top (1) ID FROM [dbo].[Step] where StepName='"+st.getStName()+"'");	
							System.out.println("Retrieved stepfk in feature insert condition");
							sqlo.updateQuery("insert into [dbo].[StepExecution] values("+runId+",'"+scenariofk+"','"+st.getStStatus()+"','"+stepfk+"','"+st.getStepStartTime()+"','"+st.getStepEndTime()+"','"+st.getStepDuration()+"')");	
							System.out.println("Succesfully inserted StepExecution insert condition");
							
						}
						
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					
			
		}

			insertFeature = false;

			if (featureStatus.equals("FAILED")) {

				prevfeatureStatus = "FAILED";
			}
		} else {
              if(mongodb) {
			mdbo.updateDocument(id,
					mdbo.addScenarioDetails(scenarioName, event.getResult().getStatus().toString(),tagList,scenarioStartTime,scenarioEndTime,scenarioDuration, stepList));

			if (prevfeatureStatus.equals("PASSED") & featureStatus.equals("FAILED")) {
				mdbo.updateDocumentFieldValue(id, featureStatus);
				prevfeatureStatus = "FAILED";
			}
		            }else if (mssql) {
		            	
		            	if(!sqlScenarioList.contains(scenarioName)) {
							try {
								int featurepk=sqlo.selectQuery("SELECT Top (1) ID FROM [dbo].[Feature] where FeatureName='"+featureName+"'");
								System.out.println("retrived featurepk in next scenario iteration");
								
								sqlo.updateQuery("insert into [dbo].[Scenario] values('"+scenarioName+"',"+featurepk+",'"+tagList.toString()+"')");
								System.out.println("succefsully inserted scenario in next scenario iteration");
								sqlScenarioList.add(scenarioName);
								
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					    }
						
						
						for (String s:stepListadd ) {
							
							if(!sqlStepList.contains(s)) {
								try {
									System.out.println("in step addition for next scenario iteration");
									int scenariopk=sqlo.selectQuery("SELECT Top (1) ID FROM [dbo].[Scenario] where ScenarioName='"+scenarioName+"'");											
									sqlo.updateQuery("insert into [dbo].[Step] values('"+s+"','"+scenariopk+"')");
									sqlStepList.add(s);
									
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
						    }
							
													
						}
						
						
						try {
							featurefk=sqlo.selectQuery("SELECT Top (1) ID FROM [dbo].[Feature] where FeatureName='"+featureName+"'");
							int scenariofk=sqlo.selectQuery("SELECT Top (1) ID FROM [dbo].[Scenario] where ScenarioName='"+scenarioName+"'");
							sqlo.updateQuery("insert into [dbo].[ScenarioExecution] values("+runId+",'"+event.getResult().getStatus().toString()+"','"+scenariofk+"','"+scenarioStartTime+"','"+scenarioEndTime+"','"+scenarioDuration+"')");
							for(StepsDetails st:stepListexec){
								
								int stepfk=sqlo.selectQuery("SELECT Top (1) ID FROM [dbo].[Step] where StepName='"+st.getStName()+"'");							
								sqlo.updateQuery("insert into [dbo].[StepExecution] values("+runId+",'"+scenariofk+"','"+st.getStStatus()+"','"+stepfk+"','"+st.getStepStartTime()+"','"+st.getStepEndTime()+"','"+st.getStepDuration()+"')");						
								
							}
							
							if (prevfeatureStatus.equals("PASSED") & featureStatus.equals("FAILED")) {								
								sqlo.updateQuery("update [dbo].[FeatureExecution] set FeatureStatus='"+featureStatus+"' where RUNID="+runId+" and FeatureID='"+featurefk+"'");
								prevfeatureStatus = "FAILED";
							}
							
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            	
		            	
		            }
		}

	};

	// step started event
	// here we creates the test node
	private void stepStarted(TestStepStarted event) {

		// We checks whether the event is from a hook or step
		if (event.getTestStep() instanceof PickleStepTestStep) {
			// TestStepStarted event implements PickleStepTestStep interface
			// WHich have additional methods to interact with the event object
			// So we have to cast TestCase object to get those methods
//			event.getTestStep().getCodeLocation()com.cucumber.proj.boeing.test.stepdefinition.GoogleSearch_steps.user_on_the_google_home_page()
//			event.getTestStep().getInstant()2022-06-18T06:54:21.717Z
//			System.out.println("event.getTestStep().getCodeLocation()"+event.getTestStep().getCodeLocation());
//			System.out.println("event.getTestStep().getInstant()"+event.getInstant());
			Instant ins = event.getInstant();

			stepStartTime = getTimeinESTTimeZone(ins);

			PickleStepTestStep steps = (PickleStepTestStep) event.getTestStep();
//			steps.getStep().getKeyword()Given 
//			steps.getStep().getArgument()null
//			System.out.println("steps.getStep().getKeyword()"+steps.getStep().getKeyword());
//			System.out.println("steps.getStep().getArgument()"+steps.getStep().getArgument());
			stepName = steps.getStep().getText();
			keyword = steps.getStep().getKeyword();
			if(mssql) {
				
				if (!sqlStepList.contains(keyword + "" + stepName))
			        stepListadd.add(keyword + "" + stepName);
			       }
			
			
			
			

		} else {
			// Same with HookTestStep
//			HookTestStep hoo = (HookTestStep) event.getTestStep();
//			stepName = hoo.getHookType().name();
		}

	};

	// This is triggered when TestStep is finished
	private void stepFinished(TestStepFinished event) {

		if (event.getTestStep() instanceof PickleStepTestStep) {
			Instant ins = event.getInstant();

			stepEndTime = getTimeinESTTimeZone(ins);
			stepDuration = duration(stepStartTime, stepEndTime);
//			System.out.println("status of step "+keyword + " "+stepName+" is "+event.getResult().getStatus().toString() );

			if (event.getResult().getStatus().toString() == "PASSED") {
                if(mongodb) {
				stepList.add(mdbo.addStepsDetails(keyword + "" + stepName, "PASSED",stepStartTime,stepEndTime,stepDuration));
                }else if(mssql) {
                	stepListexec.add(sqlo.addStepsDetails(keyword + "" + stepName, "PASSED",stepStartTime,stepEndTime,stepDuration));
                }else if(ado) {
                	stepListAdo.add(adoo.addStepsDetails(keyword + "" + stepName, "PASSED", ""));
                	
                }
				
				

			} else if (event.getResult().getStatus().toString() == "SKIPPED")

			{
				if(mongodb) {
				stepList.add(mdbo.addStepsDetails(keyword + "" + stepName, "SKIPPED",stepStartTime,stepEndTime,stepDuration));
				   }else if(mssql) {
	                	stepListexec.add(sqlo.addStepsDetails(keyword + "" + stepName, "SKIPPED",stepStartTime,stepEndTime,stepDuration));
	                }else if(ado) {
	                	stepListAdo.add(adoo.addStepsDetails(keyword + "" + stepName, "SKIPPED", ""));
	                	
	                }
			} else {

				
				scenarioStatus = "FAILED";
				featureStatus = "FAILED";
//				System.out.println("custom error");
				StringWriter errors = new StringWriter();
				event.getResult().getError().printStackTrace(new PrintWriter(errors));
//				System.out.println(errors.toString());
				
				if(mongodb) {
				stepList.add(mdbo.addFailStepsDetails(keyword + "" + stepName, "FAILED",errors.toString(),stepStartTime,stepEndTime,stepDuration));
				 }else if(mssql) {
	                	stepListexec.add(sqlo.addStepsDetails(keyword + "" + stepName, "FAILED",stepStartTime,stepEndTime,stepDuration));
	                }else if(ado) {
	                	stepListAdo.add(adoo.addStepsDetails(keyword + "" + stepName, "FAILED", errors.toString()));
	                	
	                }

			}
			;
		}

	}

	Feature getFeature(URI path) {
		if (!pathToAstMap.containsKey(path)) {
			parseGherkinSource(path);
			getFeature(path);
		}
		if (pathToAstMap.containsKey(path)) {

			// System.out.println("===ksbFeature==============="+pathToAstMap.get(path).getFeature());
			return pathToAstMap.get(path).getFeature();
		}
		return null;
	}

	private void parseGherkinSource(URI path) {
		if (!pathToReadEventMap.containsKey(path)) {
			return;
		}
		String source = pathToReadEventMap.get(path).getSource();

		List<Messages.Envelope> sources = singletonList(makeSourceEnvelope(source, path.toString()));

		List<Messages.Envelope> envelopes = Gherkin
				.fromSources(sources, true, true, true, () -> String.valueOf(UUID.randomUUID())).collect(toList());

		GherkinDocument gherkinDocument = envelopes.stream().filter(Messages.Envelope::hasGherkinDocument)
				.map(Messages.Envelope::getGherkinDocument).findFirst().orElse(null);

		pathToAstMap.put(path, gherkinDocument);
		Map<Integer, AstNode> nodeMap = new HashMap<>();

		AstNode currentParent = new AstNode(gherkinDocument.getFeature(), null);
		for (FeatureChild child : gherkinDocument.getFeature().getChildrenList()) {

			processFeatureDefinition(nodeMap, child, currentParent);

		}

	}

	private void processFeatureDefinition(Map<Integer, AstNode> nodeMap, FeatureChild child, AstNode currentParent) {
		if (child.hasBackground()) {
			processBackgroundDefinition(nodeMap, child.getBackground(), currentParent);
		} else if (child.hasScenario()) {
			processScenarioDefinition(nodeMap, child.getScenario(), currentParent);
		} else if (child.hasRule()) {
			AstNode childNode = new AstNode(child.getRule(), currentParent);
			nodeMap.put(child.getRule().getLocation().getLine(), childNode);
			for (RuleChild ruleChild : child.getRule().getChildrenList()) {
				processRuleDefinition(nodeMap, ruleChild, childNode);
			}
		}
	}

	private void processBackgroundDefinition(Map<Integer, AstNode> nodeMap, Background background,
			AstNode currentParent) {
		AstNode childNode = new AstNode(background, currentParent);
		nodeMap.put(background.getLocation().getLine(), childNode);
		for (Step step : background.getStepsList()) {
			nodeMap.put(step.getLocation().getLine(), new AstNode(step, childNode));
		}
	}

	private void processScenarioDefinition(Map<Integer, AstNode> nodeMap, Scenario child, AstNode currentParent) {
		AstNode childNode = new AstNode(child, currentParent);
		nodeMap.put(child.getLocation().getLine(), childNode);
		for (Step step : child.getStepsList()) {
			nodeMap.put(step.getLocation().getLine(), new AstNode(step, childNode));
		}
		if (child.getExamplesCount() > 0) {
			processScenarioOutlineExamples(nodeMap, child, childNode);
		}
	}

	private void processRuleDefinition(Map<Integer, AstNode> nodeMap, RuleChild child, AstNode currentParent) {
		if (child.hasBackground()) {
			processBackgroundDefinition(nodeMap, child.getBackground(), currentParent);
		} else if (child.hasScenario()) {
			processScenarioDefinition(nodeMap, child.getScenario(), currentParent);
		}
	}

	private void processScenarioOutlineExamples(Map<Integer, AstNode> nodeMap, Scenario scenarioOutline,
			AstNode parent) {
		for (Examples examples : scenarioOutline.getExamplesList()) {
			AstNode examplesNode = new AstNode(examples, parent);
			TableRow headerRow = examples.getTableHeader();
			AstNode headerNode = new AstNode(headerRow, examplesNode);
			nodeMap.put(headerRow.getLocation().getLine(), headerNode);
			for (int i = 0; i < examples.getTableBodyCount(); ++i) {
				TableRow examplesRow = examples.getTableBody(i);
				GeneratedMessageV3 rowNode = new ExamplesRowWrapperNode(examplesRow, i);
				AstNode expandedScenarioNode = new AstNode(rowNode, examplesNode);
				nodeMap.put(examplesRow.getLocation().getLine(), expandedScenarioNode);
			}
		}
	}

	AstNode getAstNode(URI path, int line) {
		if (!pathToNodeMap.containsKey(path)) {
			parseGherkinSource(path);
		}
		if (pathToNodeMap.containsKey(path)) {
			return pathToNodeMap.get(path).get(line);
		}
		return null;
	}

	boolean hasBackground(URI path, int line) {
		if (!pathToNodeMap.containsKey(path)) {
			parseGherkinSource(path);
		}
		if (pathToNodeMap.containsKey(path)) {
			AstNode astNode = pathToNodeMap.get(path).get(line);
			return getBackgroundForTestCase(astNode) != null;
		}
		return false;
	}

	static Background getBackgroundForTestCase(AstNode astNode) {
		Feature feature = getFeatureForTestCase(astNode);
		return feature.getChildrenList().stream().filter(FeatureChild::hasBackground).map(FeatureChild::getBackground)
				.findFirst().orElse(null);
	}

	private static Feature getFeatureForTestCase(AstNode astNode) {
		while (astNode.parent != null) {
			astNode = astNode.parent;
		}
		return (Feature) astNode.node;
	}

	static class ExamplesRowWrapperNode extends GeneratedMessageV3 {

		final int bodyRowIndex;

		ExamplesRowWrapperNode(GeneratedMessageV3 examplesRow, int bodyRowIndex) {
			this.bodyRowIndex = bodyRowIndex;
		}

		@Override
		protected FieldAccessorTable internalGetFieldAccessorTable() {
			throw new UnsupportedOperationException("not implemented");
		}

		@Override
		protected Message.Builder newBuilderForType(BuilderParent builderParent) {
			throw new UnsupportedOperationException("not implemented");
		}

		@Override
		public Message.Builder newBuilderForType() {
			throw new UnsupportedOperationException("not implemented");
		}

		@Override
		public Message.Builder toBuilder() {
			throw new UnsupportedOperationException("not implemented");
		}

		@Override
		public Message getDefaultInstanceForType() {
			throw new UnsupportedOperationException("not implemented");
		}

	}

	static class AstNode {

		final GeneratedMessageV3 node;
		final AstNode parent;

		AstNode(GeneratedMessageV3 node, AstNode parent) {
			this.node = node;
			this.parent = parent;
		}

	}

	public String getTimeinESTTimeZone(Instant ins)

	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss a");
		formatter = formatter.withZone(TimeZone.getTimeZone("EST").toZoneId());
	    ins=ins.plus(1, ChronoUnit.HOURS);
		return formatter.format(ins);
	}

	public String duration(String starttime, String endtime) {

		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		

		Date d1;
		try {
			d1 = formatter.parse(starttime);
			Date d2 = formatter.parse(endtime);
			long difference_In_Time = d2.getTime() - d1.getTime();

			// Calucalte time difference in
			// seconds, minutes, hours, years,
			// and days
			long difference_In_Seconds = (difference_In_Time / 1000) % 60;

			long difference_In_Minutes = (difference_In_Time / (1000 * 60)) % 60;

			long difference_In_Hours = (difference_In_Time / (1000 * 60 * 60)) % 24;

			if (difference_In_Hours == 0 && difference_In_Minutes > 0) {
				return difference_In_Minutes + " mins, " + difference_In_Seconds + " secs";
			}
			if (difference_In_Hours == 0 && difference_In_Minutes == 0) {
				return difference_In_Seconds + " secs";
			}
			return difference_In_Hours + " hrs " + difference_In_Minutes + " mins, " + difference_In_Seconds + "secs";
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "issue with finding the duration" + e;
		}

	}
}
//
//
//
//mdbo.addScenarioDetails("Scenario1","Fail",stepList);
//
//Document d=mdbo.addFeatureDetails("Sample Feature Name","Pass",scenarioList);
//mdbo.insertDocumentDB(d);


