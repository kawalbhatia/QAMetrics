package com.ksb.qametrics.helper.Logger;

//import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author QET Team
 *
 *02/07/2K19
 *
 */

@SuppressWarnings("rawtypes")
public class LoggerHelper {
	
	private static boolean root = false;
	
	public static Logger getLogger(Class clas) {
		if(root)
			return LogManager.getLogger(clas);
		
			
		root = true;
		return LogManager.getLogger(clas);
	}

}
