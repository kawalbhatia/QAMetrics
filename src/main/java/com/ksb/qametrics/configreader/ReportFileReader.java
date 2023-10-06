package com.ksb.qametrics.configreader;

import java.io.FileInputStream;
import java.util.Properties;

public class ReportFileReader {
	
	private static Properties prop = new Properties();
	
	
	public static Properties getPropertyFile(String path) {
		
		try {
//			prop = new Properties();
			FileInputStream in = new FileInputStream(
			System.getProperty("user.dir") + path);
			prop.load(in);
			return prop;
		} catch (Exception e) {
			
			e.printStackTrace();
			return prop;
		
		
		
		
	}

}
}