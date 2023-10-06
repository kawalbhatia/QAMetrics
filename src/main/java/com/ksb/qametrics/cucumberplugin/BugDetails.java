package com.ksb.qametrics.cucumberplugin;

public class BugDetails {
	
	private int id;
    private String title;
    private String description;
    
    
    
	public BugDetails(int id, String title, String description) {
		
		this.id = id;
		this.title = title;
		this.description = description;
	}

	
	   public String getTitle() {
	        return title;
	    }

	    public String getDescription() {
	        return description;
	    }
	    
	    public int getId() {
	        return id;
	    }

}
