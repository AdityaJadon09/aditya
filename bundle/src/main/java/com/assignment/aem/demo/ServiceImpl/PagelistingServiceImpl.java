package com.assignment.aem.demo.ServiceImpl;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyUnbounded;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;

import com.assignment.aem.demo.services.PageListing;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

@Component(immediate = true, label = "Page Listing Faq/Search Configuration Service", description = "Page listing banned words", metatype = true)
@Service(value=PageListing.class)
public class PagelistingServiceImpl implements PageListing {
	
	@Property(unbounded = PropertyUnbounded.ARRAY, label = "Multi String", description = "Words Banned config")
	private static final String BANNED_WORDS = "bannedWords";
	
	private String[] bannedwords;
	
	@Reference
	QueryBuilder queryBuilder;

	@Override
	public List<String> searchPage(SlingHttpServletRequest request , String[] tag) {
		
		List<String> returnedpagespath= new ArrayList<String>();
		
		Session session=request.getResourceResolver().adaptTo(Session.class);
		
		Map queryMap = new HashMap<String,Object>();
		
		queryMap.put("path", "/content");
		queryMap.put("type","cq:page");
		queryMap.put("tagid.property", "jcr:content/cq:tags");
		int index=1;
		for(String tagval : tag)
		{
			queryMap.put("tagid."+index+"_value",tag);
			index++;
		}
		
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

	@Override
	public String[] getBannedWords() {
	 return this.bannedwords;
	}
	
	
	@Activate
	public void activate(final ComponentContext context) {
		Dictionary<?, ?> properties = context.getProperties();
		this.bannedwords = PropertiesUtil.toStringArray(properties.get(BANNED_WORDS));
	}	

}
