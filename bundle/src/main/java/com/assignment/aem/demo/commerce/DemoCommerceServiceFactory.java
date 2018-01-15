package com.assignment.aem.demo.commerce;

import com.adobe.cq.commerce.api.CommerceService;
import com.adobe.cq.commerce.api.CommerceServiceFactory;
import com.adobe.cq.commerce.common.AbstractJcrCommerceServiceFactory;


import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;


@Component
@Service
@Properties(value = {
        @Property(name = "service.description", value = "Factory for reference implementation commerce service"),
        @Property(name = "commerceProvider", value = "Demo", propertyPrivate = true)
})
public class DemoCommerceServiceFactory extends AbstractJcrCommerceServiceFactory implements CommerceServiceFactory{
	 /**
     * Create a new <code>DemoCommerceServiceImpl</code>.
     */
    @Override
    public CommerceService getCommerceService(Resource res) {
        return new DemoCommerceServiceImpl(getServiceContext(), res);
    }
}
