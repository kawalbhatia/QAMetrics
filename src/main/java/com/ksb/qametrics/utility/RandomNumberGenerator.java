package com.ksb.qametrics.utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

public class RandomNumberGenerator {

	public String digit6RandomNumer() {
		Random number = new Random();
		int random = number.nextInt(999999);
		return String.valueOf(random);

	}

	public String digit6RandomNumerUsingTime() {
		LocalDateTime now = LocalDateTime.now();
		String format = now.format(DateTimeFormatter.ofPattern("HHmmss", Locale.ENGLISH));
		return format;

	}
	public String gen_6digit_randon_with_non_Leading_Zero()
	{
		
			LocalDateTime now = LocalDateTime.now();
			String format = now.format(DateTimeFormatter.ofPattern("HHmmss", Locale.ENGLISH));
			
			// Convert str into StringBuffer as Strings are immutable. 
			StringBuffer sb = new StringBuffer(format); 
			
			for(int i =0;i<sb.length(); i++)
			{
				if(sb.charAt(i)=='0')
				{
					// The  StringBuffer replace function removes i characters from given index (0 here) 
					sb.replace(0, i, "1"); 
				}
				break;
			}
	         
			return sb.toString();
	}
	
	public String gen_6digit_randon_with_Leading_Zero()
	{
		
			LocalDateTime now = LocalDateTime.now();
			String format = now.format(DateTimeFormatter.ofPattern("HHmmss", Locale.ENGLISH));
			
			// Convert str into StringBuffer as Strings are immutable. 
			StringBuffer sb = new StringBuffer(format); 
			
			for(int i =0;i<sb.length(); i++)
			{
				if(sb.charAt(i)!='0')
				{
					// The  StringBuffer replace function removes i characters from given index (0 here) 
					sb.replace(0, i, "0"); 
					sb.delete(sb.length()-1, sb.length());
					
				}
				break;
			}
	         
			return sb.toString();
	}
}
