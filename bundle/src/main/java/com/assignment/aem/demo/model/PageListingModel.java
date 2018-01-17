package com.assignment.aem.demo.model;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.assignment.aem.demo.services.PageListing;
import com.day.cq.wcm.api.Page;

@Model(adaptables = SlingHttpServletRequest.class)
public class PageListingModel {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PageListingModel.class);
	
	/**
	 * SlingHttpServletRequest
	 */
	@Inject
	protected SlingHttpServletRequest request;
	
	@Inject
	protected Resource resource;
	
	@Inject
	protected Page currentPage;
	
	private List<String> pagepathlist;
	
	/**
	 * PageListing
	 */
	public PageListing PageListingService;
	
	
	/**
	 * @param sling
	 * @throws ServletException
	 */
	@Inject
	public PageListingModel(SlingScriptHelper sling) throws ServletException {
		PageListingService = sling.getService(PageListing.class);
	}
	
	@PostConstruct
	public void init() {
		String tag = resource.adaptTo(ValueMap.class).get("tag",String.class);
		this.pagepathlist=PageListingService.searchPage(request,tag);
	}
	

	public List<String> getPagepathlist() {
		return pagepathlist;
	}

	public void setPagepathlist(List<String> pagepathlist) {
		this.pagepathlist = pagepathlist;
	}

}
