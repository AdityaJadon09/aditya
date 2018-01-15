package com.assignment.aem.demo.commerce;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.commerce.api.Product;
import com.adobe.cq.commerce.common.AbstractJcrProduct;
import com.assignment.aem.demo.Constants;


/**
 * Class for getting Product Information
 */
public class ProductImpl extends AbstractJcrProduct implements Product {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductImpl.class);

	/**
	 * Reference Manager
	 */
	ResourceResolver resourceResolver;

	/**

	/**
	 * Constructor
	 *
	 * @param resource
	 */

	public ProductImpl(ResourceResolver resourceResolver, Resource resource) {
		super(resource);
		this.resourceResolver = resourceResolver;
	}

	/**
	 * Getter SKU
	 *
	 * @return
	 */
	@Override
	public String getSKU() {
		return getProperty(Constants.PRODUCT_IDENTIFIER, String.class);
	}

	/**
	 * Getter Product Name Imported
	 *
	 * @return
	 */
	public String getProductNameImported() {
		return getProperty(Constants.PRODUCT_NAME, String.class);
	}

	/**
	 * Getter Product Description
	 *
	 * @return
	 */
	public String getProductDescriptionImported() {
		return getProperty(Constants.PRODUCT_DESCRIPTION, String.class);
	}

	/**
	 * Getter Product Headline
	 *
	 * @return
	 */
	public String getProductHeadlineImported() {
		return getProperty(Constants.PRODUCT_HEADLINE, String.class);
	}

	/**
	 * Getter Product link
	 *
	 * @return
	 */
	public String getProductLinkImported() {
		return getProperty(Constants.PRODUCT_LINK, String.class);
	}

	/**
	 * Getter Product link
	 *
	 * @return
	 */
	public String getProductTextImported() {
		return getProperty(Constants.PRODUCT_TEXT, String.class);
	}

	/**
	 * return asset for a specific tag
	 *
	 * @param tag
	 * @return String
	 */
	private String getProductImageByTag(String tag) {
		List<Resource> imageList = this.getAssets();
		
		String path = null;

		if (imageList != null) {
			for (Resource res : imageList) {
				String[] tags = res.getValueMap().get(Constants.PRODUCT_TAGGING, String[].class);
				if (tags != null && ArrayUtils.contains(tags, tag)) {
					path = res.getPath();
				}
			}
		}
		if (null != path) {
			try {
				if (resourceResolver.getResource(path).adaptTo(Node.class).hasProperty(Constants.PRODUCT_IMAGE))
					path = resourceResolver.getResource(path).getValueMap().get(Constants.PRODUCT_IMAGE, String.class);
			} catch (RepositoryException ex) {
				ex.printStackTrace();
			}

		}
		return (path != null) ? path : "";

	}

	/**
	 * return header Image asset path
	 *
	 * @return String
	 */
	public String getProductHeaderImage() {
		return this.getProductImageByTag(Constants.HEADER_PRODUCT_IMAGE_TAG);
	}

	/**
	 * return header Inner Image Image asset path
	 *
	 * @return String
	 */
	public String getProductHeaderInnerImage() {
		return this.getProductImageByTag(Constants.HEADER_INNER_IMAGE);
	}

	/**
	 * return header Image asset path
	 *
	 * @return String
	 */
	public String getProductImage() {
		return this.getProductImageByTag(Constants.TV_PACKSHOT);
	}

	/**
	 * Get the property information
	 *
	 * @param property
	 * @param type
	 * @param <T>
	 * @return
	 */
	@Override
	public <T> T getProperty(String property, Class<T> type) {

		T value = super.getProperty(property, type);

		if (value == null) {

			switch (property) {
			case Constants.PRODUCT_IDENTIFIER:
				property = Constants.PRODUCT_IDENTIFIER;
				break;
			case Constants.PRODUCT_NAME:
				property = Constants.PRODUCT_NAME;
				break;
			case Constants.PRODUCT_DESCRIPTION:
				property = Constants.PRODUCT_DESCRIPTION;
				break;
			case Constants.PRODUCT_HEADLINE:
				property = Constants.PRODUCT_HEADLINE;
				break;
			case Constants.PRODUCT_LINK:
				property = Constants.PRODUCT_LINK;
				break;
			case Constants.PRODUCT_TEXT:
				property = Constants.PRODUCT_TEXT;
				break;
			default:
				property = property;
				break;
			}

			value = super.getProperty(property, type);
		}

		return value;
	}
}