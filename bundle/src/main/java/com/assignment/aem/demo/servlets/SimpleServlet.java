package com.assignment.aem.demo.servlets;

import java.io.IOException;

import javax.jcr.Repository;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;   
@SuppressWarnings("serial") 
@SlingServlet(resourceTypes = "sling/servlet/default", selectors = "data", extensions = "html", methods = "GET", metatype =true) 
public class SimpleServlet extends SlingSafeMethodsServlet {
	
	@Reference     
	private Repository repository;
	
	@Override     
	protected void doGet(final SlingHttpServletRequest req,final SlingHttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		String keys[] = repository.getDescriptorKeys();
		JSONObject jsonobject = new JSONObject(); 
		
		for(int i=0;i<keys.length;i++){
            try {
                jsonobject.put(keys[i], repository.getDescriptor(keys[i]));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        resp.getWriter().println(jsonobject.toString());
         
    }
}
				
