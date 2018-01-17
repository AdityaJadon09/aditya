package com.assignment.aem.demo.services;

import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;

public interface PageListing {
	
	String[] getBannedWords();
	
	public List<String> searchPage(SlingHttpServletRequest request,String[] tag);

}
