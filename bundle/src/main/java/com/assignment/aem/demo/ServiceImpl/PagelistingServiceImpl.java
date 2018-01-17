package com.assignment.aem.demo.ServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;

import com.assignment.aem.demo.services.PageListing;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

@Component
@Service(value=PageListing.class)
public class PagelistingServiceImpl implements PageListing {
	
	@Reference
	QueryBuilder queryBuilder;

	@Override
	public List<String> searchPage(SlingHttpServletRequest request , String tag) {
		
		List<String> returnedpagespath= new ArrayList<String>();
		
		Session session=request.getResourceResolver().adaptTo(Session.class);
		
		Map queryMap = new HashMap<String,Object>();
		
		queryMap.put("path", "/content");
		queryMap.put("type","cq:page");
		queryMap.put("1_property", "cq:tags");
		queryMap.put("1_property.value",tag);
		queryMap.put("relativedaterange.property", "@jcr:content/cq:lastModified");
		queryMap.put("relativedaterange.lowerBound",-5d);
		queryMap.put("ordeyBy","@jcr:content/cq:lastModified");
		queryMap.put("ordeyBy.sort","desc");
		
		Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), session);
		
		SearchResult result = query.getResult();
		
		for (Hit hit : result.getHits())
		{
			String path=StringUtils.EMPTY;
			try {
				path = hit.getPath();
			} catch (RepositoryException e) {
				
				e.printStackTrace();
			}
			
			returnedpagespath.add(path);
		}
		
		return returnedpagespath;
		
	}
	
	
	

}
