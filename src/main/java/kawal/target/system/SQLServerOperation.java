package kawal.target.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.bson.Document;

import com.ksb.qametrics.configreader.ReportFileReader;
import com.ksb.qametrics.cucumberplugin.StepsDetails;

public class SQLServerOperation {
	
	 Connection conn = null;
	 Properties prop;
	 

	
	public SQLServerOperation() {
		
		
		
		
  try {
			  
			  
			  prop = new Properties();

				try {
					
					prop=ReportFileReader.getPropertyFile("\\src\\test\\resources\\ReportDB.properties");
					
				} catch (Exception e) {
					e.printStackTrace();
				}
	
		    	      
		      
		  //    testDb = MongoClients.create(mongoUri).getDatabase("testDb");

		    } catch (Exception e) {
		      prop = null;
		    }
		
	}
	
	public  Connection getSQLConnection()
	
	{
		
		  
		   
	        try {
	 
	            String dbURL = "jdbc:sqlserver://reportmetrics.database.windows.net:1433;database=selenium_reports;user=kawalb@reportmetrics;password=Test@123;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
	          
	            if(conn==null) {
	            	conn = DriverManager.getConnection(dbURL);
	        	   
	           }
	            
				            
	 
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
			return conn; 
	}
	
	 
//	public ResultSet selectQuery(String sql) throws SQLException {
//		
//		Statement state = getSQLConnection().createStatement();
//		ResultSet rs = state.executeQuery(sql);
//     	return rs;
//
//	}
	
public int updateQuery(String sql) throws SQLException {
		
	Statement stmt = getSQLConnection().createStatement();
  
	int result=stmt.executeUpdate(sql);
	return result;

	}
	

public int getLastrunID() {
	 try {
		 Statement stmt = getSQLConnection().createStatement();
		 ResultSet rs = stmt.executeQuery("select top 1 RUNID from [dbo].[RunDetails] order by RUNID desc");
		 int runid=0;
		 
		 if(rs.next())
		 {
			 runid= rs.getInt("RUNID");
		 }
	 
	 return runid;
	 }catch(Exception e) {
		 return 0;
	 }
	 

}

public String getFeatureFileName(String fname) {
	 try {
		 Statement stmt = getSQLConnection().createStatement();
		 ResultSet rs = stmt.executeQuery("select top 1 FeatureFileName from [dbo].[Feature] where FeatureName='"+fname+"'");
		 String filename = null;
		 
		 if(rs.next())
		 {
			 filename= rs.getString(1);
		 }
	 
	 return filename;
	 }catch(Exception e) {
		 return null;
	 }
	 

}

public int getLastFeatureNum() {
	 try {
		 Statement stmt = getSQLConnection().createStatement();
		 ResultSet rs = stmt.executeQuery("select top 1 FeatureId from [dbo].[Feature] order by FeatureId desc");
		 String featureId = null;
		 
		 if(rs.next())
		 {
			 featureId= rs.getString("FeatureId");
		 }
		 
	 
	 return Integer.valueOf(featureId.substring(1));
	 }catch(Exception e) {
		 return 0;
	 }
	 

}


public List<String> getFeatureList() {
	 try {
		 Statement stmt = getSQLConnection().createStatement();
		 ResultSet rs = stmt.executeQuery("SELECT FeatureName FROM [dbo].[Feature]");
		 List<String> featureList=new ArrayList<String>();
		 System.out.println("Feature size=>"+rs.getFetchSize());
		 while(rs.next())
		 {
			 featureList.add(rs.getString(1));
		 }
	 
	 return featureList;
	 }catch(Exception e) {
		 return null;}
	 }
	 
	 public List<String> getScenarioList() {
		 try {
			 Statement stmt = getSQLConnection().createStatement();
			 ResultSet rs = stmt.executeQuery("SELECT ScenarioName FROM [dbo].[Scenario]");
			 List<String> scenarioList=new ArrayList<String>();
			 
			 while(rs.next())
			 {
				 scenarioList.add(rs.getString(1));
			 }
		 
		 return scenarioList;
		 }catch(Exception e) {
			 return null;}
		 }
		 
		 
		 public List<String> getStepList() {
			 try {
				 Statement stmt = getSQLConnection().createStatement();
				 ResultSet rs = stmt.executeQuery("SELECT StepName FROM [dbo].[Step]");
				 List<String> stepList=new ArrayList<String>();
				 
				 while(rs.next())
				 {
					 stepList.add(rs.getString(1));
				 }
			 
			 return stepList;
			 }catch(Exception e) {
				 return null;
			 }
	 

}

		 
		 public int selectQuery(String sql) throws SQLException {
				
				Statement state = getSQLConnection().createStatement();
				ResultSet rs = state.executeQuery(sql);
				int featurepk = 0;
				 
				 if(rs.next())
				 {
					 featurepk= rs.getInt("ID");
				 }
				 return featurepk;

			}
		 
		 public  StepsDetails addStepsDetails(String stName,String stStatus,String stStartTime,String stEndTime,String stDuration) {
			  
			  StepsDetails st=new StepsDetails();
				
				st.setStName(stName);
				st.setStStatus(stStatus);
				st.setStepStartTime(stStartTime);
				st.setStepEndTime(stEndTime);
				st.setStepDuration(stDuration);
						
				return st;	
			  
		  }
	
	
	public void cleanUp() {
		
		try {			
			
			if (conn != null && !conn.isClosed())
				conn.close();
		} catch (SQLException e) {
			e.getMessage();
		}
		 finally {
	            try {
	                if (conn != null && !conn.isClosed()) {
	                    conn.close();
	                }
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	                
	            }
	        }
	}
//    public static void main(String[] args) throws SQLException {
//    	SQLServerOperation sqs=new SQLServerOperation();
//    	int result=sqs.updateQuery("Insert into dbo.Feature values('dummy feature name11','F005')");
//     System.out.println("result is =>"+result);
//     sqs.cleanUp();
//    }
}
