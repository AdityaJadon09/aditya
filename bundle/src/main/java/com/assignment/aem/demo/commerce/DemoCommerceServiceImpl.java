package com.assignment.aem.demo.commerce;

import com.adobe.cq.commerce.api.*;
import com.adobe.cq.commerce.common.AbstractJcrCommerceService;
import com.adobe.cq.commerce.common.ServiceContext;
import com.assignment.aem.demo.commerce.ProductImpl;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.List;

public class DemoCommerceServiceImpl extends AbstractJcrCommerceService implements CommerceService{
	
    /**
     * Constructor
     * @param serviceContext
     * @param resource
     */
    public DemoCommerceServiceImpl(ServiceContext serviceContext, Resource resource) {
        super(serviceContext, resource);
    }

    /**
     * Login
     * @param request
     * @param response
     * @return
     * @throws CommerceException
     */
    @Override
    public CommerceSession login(SlingHttpServletRequest request, SlingHttpServletResponse response) throws CommerceException {
        return null;
    }

    /**
     * Is available?
     * @param serviceType
     * @return
     */
    @Override
    public boolean isAvailable(String serviceType) {
        return CommerceConstants.SERVICE_COMMERCE.equals(serviceType);
    }

    /**
     * Get the product
     * @param path
     * @return
     * @throws CommerceException
     */
    @Override
    public Product getProduct(final String path) throws CommerceException {
        Resource resource = resolver.getResource(path);
        if (resource != null && ProductImpl.isAProductOrVariant(resource)) {
            return new ProductImpl(resolver, resource);
        }
        return null;
    }

    /**
     * catalogRolloutHook
     * @param blueprint
     * @param catalog
     */
    @Override
    public void catalogRolloutHook(Page blueprint, Page catalog) {
        
    }

    /**
     * sectionRolloutHook
     * @param blueprint
     * @param section
     */
    @Override
    public void sectionRolloutHook(Page blueprint, Page section) {
       
    }

    /**
     * productRolloutHook
     * @param productData
     * @param productPage
     * @param product
     * @throws CommerceException
     */
    @Override
    public void productRolloutHook(Product productData, Page productPage, Product product) throws CommerceException {
        
    }

    /**
     * getAvailableShippingMethods
     * @return
     * @throws CommerceException
     */
    @Override
    public List<ShippingMethod> getAvailableShippingMethods() throws CommerceException {
        return new ArrayList<>();
    }

    /**
     * getAvailablePaymentMethods
     * @return
     * @throws CommerceException
     */
    @Override
    public List<PaymentMethod> getAvailablePaymentMethods() throws CommerceException {
        return new ArrayList<>();
    }

    /**
     * getCountries
     * @return
     * @throws CommerceException
     */
    @Override
    public List<String> getCountries() throws CommerceException {
        return new ArrayList<>();
    }

    /**
     * getCreditCardTypes
     * @return
     * @throws CommerceException
     */
    @Override
    public List<String> getCreditCardTypes() throws CommerceException {
        return new ArrayList<>();
    }

    /**
     * getOrderPredicates
     * @return
     * @throws CommerceException
     */
    @Override
    public List<String> getOrderPredicates() throws CommerceException {
        return new ArrayList<>();
    }

}
