package com.assignment.aem.demo.model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.assignment.aem.demo.Constants;
import com.assignment.aem.demo.commerce.ProductImpl;
import com.day.cq.wcm.api.Page;

@Model(adaptables = SlingHttpServletRequest.class)
public class ProductComponentModel {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductComponentModel.class);
	/**
	 * SlingHttpServletRequest
	 */
	@Inject
	protected SlingHttpServletRequest request;
	/**
	 * Current Page
	 */
	@Inject
	protected Page currentPage;
	
	@Inject
	protected Resource resource;
	/**
	 * Response
	 */
	@Inject
	protected SlingHttpServletResponse response;

	/**
	 * Resource Resolver
	 */
	@Inject
	protected ResourceResolver resourceResolver;

	/**
	 * product Object
	 */
	public ProductImpl product;

	/**
	 * Status of product
	 */
	public String status;

	/**
	 * pagepath
	 */
	private String pagepath;
	
	/**
	 * Initialize the component.
	 */
	@PostConstruct
	public void init() {
		
		String skuNumber = getSkuNumber();		
		try {
			getValuesProductDatabase(getProductIdentifierFromSkuNumber(skuNumber));
			
		} catch (Exception ex) {
			LOGGER.error("ProductComponentModel.init(): Exception - {} ", ex);
		}

	}	

	/**
	 * @param skuNumber
	 * @return skuNumber for the Skunumber
	 */
	private String getProductIdentifierFromSkuNumber(String skuNumber) {
		if (skuNumber.length() >= 14) {
			return skuNumber.substring(0, 14);
		} else {
			return skuNumber;
		}
	}	
	/**
	 * @param valuemap
	 * @param property
	 * @return
	 */
	private String getValueOfProperty(ValueMap valuemap, String property) {
		return valuemap.get(property, String.class);
	}

	
	/**
	 * This method is used for getting Skunumber
	 * 
	 * @return
	 */
	private String getSkuNumber() {
		String skuNumber = null;
		if (null != request.getQueryString()) {
			if (null != request.getParameter(Constants.SKU)) {
				skuNumber = request.getParameter(Constants.SKU);
			}
		}
		return skuNumber;
	}

	/**                                                        
	 * This method is used to return values from product database
	 * 
	 * @param skuNumber
	 */
	private void getValuesProductDatabase(String skuNumber) {
		product = null;
		String pathofproducts = getPath(skuNumber);
		if (null != pathofproducts) {
			if ((null != resourceResolver.getResource(pathofproducts))) {
				Resource productresource = resourceResolver.getResource(pathofproducts);
				product = new ProductImpl(resourceResolver, productresource);
			}
		}
	}

	/**
	 * this method returns path to the product
	 * 
	 * @param skuNumber
	 * @return
	 */
	private String getPath(String skuNumber) {
		String path = Constants.PATH_PRODUCTS;
		if (null != path) {
			if ((null != resourceResolver.getResource(path))) {
				if (null != resourceResolver.getResource(path).getChild(skuNumber)) {
					path = resourceResolver.getResource(path).getChild(skuNumber).getPath();
				} else {
					path = null;
				}
			}
		}
		return path;
	}

	/**
	 * this method returns product name from product database
	 * 
	 * @return
	 */
	public String getProductName() {
		return product.getProductNameImported();
	}

	/**
	 * this method returns product description from product database
	 * 
	 * @return
	 */
	public String getProductDescriptionImported() {
		return product.getProductDescriptionImported();
	}

/**
	 * This method returns product header image from product database
	 * 
	 * @return
	 */
	public String getHeaderImage() {
		String headerImage = null;
		String type=getValueOfProperty(resource.getValueMap(),Constants.TYPE);
		if (null != type) {
			if (type.equalsIgnoreCase(Constants.HEADER_INNER_IMAGE_TYPE)) {
				headerImage = product.getProductHeaderInnerImage();
			} else {
				headerImage = product.getProductHeaderImage();
			}
		}

		return headerImage;
	}
	
	
	/**
	 * this method returns product headline from product database
	 * 
	 * @return
	 */
	public String getProductHeadline() {
		return product.getProductHeadlineImported();
	}

	/**
	 * this method returns product Image from product database
	 * 
	 * @return
	 */
	public String getProductImage() {
		return product.getProductImage();
	}

	/**
	 * this method returns product text from product database
	 * 
	 * @return
	 */
	public String getProductText() {
		return product.getProductTextImported();
	}

	/**
	 * this method returns product Description from product database
	 * 
	 * @return
	 */
	public String getProductDescription() {
		return product.getProductDescriptionImported();
	}

}
