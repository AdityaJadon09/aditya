/*************************************************************************
	 * ADOBE CONFIDENTIAL
	 * __________________
	 * <p/>
	 * Copyright 2012 Adobe Systems Incorporated
	 * All Rights Reserved.
	 * <p/>
	 * NOTICE:  All information contained herein is, and remains
	 * the property of Adobe Systems Incorporated and its suppliers,
	 * if any.  The intellectual and technical concepts contained
	 * herein are proprietary to Adobe Systems Incorporated and its
	 * suppliers and are protected by trade secret or copyright law.
	 * Dissemination of this information or reproduction of this material
	 * is strictly forbidden unless prior written permission is obtained
	 * from Adobe Systems Incorporated.
**************************************************************************/

package com.assignment.aem.demo.commerce;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import org.apache.commons.lang3.StringUtils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.commerce.pim.api.ProductImporter;
import com.adobe.cq.commerce.pim.common.AbstractProductImporter;
import com.assignment.aem.demo.Constants;
import com.day.cq.commons.jcr.JcrConstants;

/**
 * Imports products from a CSV file.
 * <p/>
 * Each row of the CSV describes a product or a variation of a product.
 * Variations must come after their parent product. (They are no longer required
 * to be immediately after.)
 * <p/>
 * Row structure: operation, type, sku, title, property=value, sizes, price,
 * image, description, tags, additional-property=value, ...
 * <p/>
 * Operations supported are 'add', 'update' . Add is supported for the type
 * 'product'. Update is only supported for 'product'. variations and/or sizes).
 */
@SuppressWarnings("deprecation")
@Component(metatype = true, label = "Demo Product Importer", description = "CSV-based product importer for Demo")
@Service(CSVProductImporter.class)
@Properties(value = { @Property(name = "service.description", value = "CSV-based product importer for Demo"),
		@Property(name = "commerceProvider", value = "Demo", propertyPrivate = true) })
public class CSVProductImporter extends AbstractProductImporter implements ProductImporter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CSVProductImporter.class);

	private InputStream is;

	@Override
	protected boolean validateInput(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws IOException {
		ResourceResolver resourceResolver = request.getResourceResolver();

		String provider = request.getParameter("provider");
		if (StringUtils.isEmpty(provider)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No commerce provider specified.");
			return false;
		}

		String csvPath = request.getParameter("csvPath");

		try {
			Resource csvResource = resourceResolver.getResource(csvPath);
			Node source = csvResource.adaptTo(Node.class);
			is = source.getProperty(JcrConstants.JCR_CONTENT + Constants.FORWARD_SLASH + JcrConstants.JCR_DATA)
					.getBinary().getStream();
		} catch (Exception e) {
			LOGGER.error("CSVProductImporter.validateInput(): Exception - {} ", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Product CSV [" + csvPath + "] not found.");
			return false;
		}

		return true;
	}

	@Override
	protected void doImport(ResourceResolver resourceResolver, Node productPath, boolean arg2)
			throws RepositoryException, IOException {
		String path = productPath.getPath();
		String line = null;
		String csvSplitBy = ",";
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] dataArray = line.split(csvSplitBy);
				if (isValidDataInLine(dataArray)) {
					processProduct(createProductModel(resourceResolver, dataArray), path, resourceResolver);
				}
			}

		} catch (Exception e) {
			LOGGER.error("CSV product Importer .importProductsfromCSV: IOException", e);
		}

	}

	/**
	 * 
	 * @param dataArray
	 * @return
	 */
	private boolean isValidDataInLine(final String[] dataArray) {
		boolean validData = false;
		if (dataArray != null && dataArray.length <= 9 && (StringUtils.isNotEmpty(dataArray[0])
				&& dataArray[0].length() == 14 && StringUtils.isNumeric(dataArray[0]))) {
			validData = true;
		}
		return validData;
	}

	/**
	 * Update product method
	 *
	 * @param product
	 * @param path
	 * @param resourceResolver
	 */
	private void updateProduct(ProductModel product, String path, ResourceResolver resourceResolver) {

		try {
			Node productNode = resourceResolver.getResource(path).adaptTo(Node.class);
			setProperties(productNode, product, false);
			productUpdated(productNode);
		} catch (RepositoryException e) {
			LOGGER.error("CSVProductImporter.updateProduct(): RepositoryException - {}", e);
		}
	}

	/**
	 * Create Product method
	 *
	 * @param product
	 * @param path
	 * @param resourceResolver
	 */
	private void createProduct(ProductModel product, String path, ResourceResolver resourceResolver) {

		try {

			Node productNode = createProduct(path, resourceResolver.adaptTo(Session.class));
			setProperties(productNode, product, true);
		} catch (RepositoryException e) {
			LOGGER.error("CSVProductImporter.createProduct(): RepositoryException - {}", e);
		}
	}

	/**
	 * ProcessProduct
	 *
	 * @param ResourceResolver
	 * @param storePath
	 * @param product
	 */
	private void processProduct(ProductModel product, String storePath, ResourceResolver resourceResolver) {
		String path = storePath + Constants.FORWARD_SLASH + product.getSKU();

		if (resourceResolver.getResource(path) == null) {
			createProduct(product, path, resourceResolver);
		} else {
			updateProduct(product, path, resourceResolver);
		}
	}

	/**
	 * Create ProductModel
	 *
	 * @param ResourceResolver
	 * @param productdata
	 */

	private ProductModel createProductModel(ResourceResolver resourceResolver, String[] productData) {

		ProductModel product = new ProductModel();
		product.setSKU(getValueForSpecificIndex(productData, 0));
		product.setProductNameImported(getValueForSpecificIndex(productData, 1));
		product.setProductDescriptionImported(getValueForSpecificIndex(productData, 2));
		product.setProductHeaderImageImported(getValueForSpecificIndex(productData, 3));
		product.setProductHeaderInnerImageImported(getValueForSpecificIndex(productData, 4));
		product.setProductHeadlineImported(getValueForSpecificIndex(productData, 5));
		product.setProductLinkImported(getValueForSpecificIndex(productData, 6));
		product.setProductTextImported(getValueForSpecificIndex(productData, 7));
		product.setFileReference(getValueForSpecificIndex(productData, 8));
		return product;
	}

	/**
	 * 
	 * @param productData
	 * @param index
	 * @return
	 */
	private String getValueForSpecificIndex(String[] productData, int index) {
		String data = null;
		try {
			if (StringUtils.isNotEmpty(productData[index])) {
				data = productData[index];
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
			LOGGER.error("CSVProductImporter.setProperties(): getValueForSpecificIndex - {}", ex);
		}
		return data;
	}

	/**
	 * Set properties of a node
	 *
	 * @param productNode
	 * @param product
	 */
	private void setProperties(Node productNode, ProductModel product, boolean isNew) {

		try {
			productNode.setProperty("cq:scaffolding", "/etc/scaffolding/Demo/product");
			productNode.setProperty(Constants.PRODUCT_IDENTIFIER, product.getSKU());
			productNode.setProperty(Constants.PRODUCT_NAME, product.getProductNameImported());
			productNode.setProperty(Constants.PRODUCT_DESCRIPTION, product.getProductDescriptionImported());
			productNode.setProperty(Constants.PRODUCT_HEADLINE, product.getProductHeadlineImported());
			productNode.setProperty(Constants.PRODUCT_LINK, product.getProductLinkImported());
			productNode.setProperty(Constants.PRODUCT_TEXT, product.getProductTextImported());

			if ((null != product.getFileReference() && product.getFileReference().length() > 0)) {

				if (isNew) {
					Node producPackshotImageNode = createImage(productNode);
					producPackshotImageNode.setProperty(Constants.PRODUCT_IMAGE, product.getFileReference());
					producPackshotImageNode.setProperty(Constants.PRODUCT_TAGGING, Constants.TV_PACKSHOT);
				} else {
					if (!productNode.hasNode(Constants.IMAGE)) {
						Node producPackshotImageNode = createImage(productNode);
						producPackshotImageNode.setProperty(Constants.PRODUCT_IMAGE, product.getFileReference());
						producPackshotImageNode.setProperty(Constants.PRODUCT_TAGGING, Constants.TV_PACKSHOT);
					} else {
						Node productNodeImageasset = productNode.getNode("image");
						productNodeImageasset.setProperty(Constants.PRODUCT_IMAGE, product.getFileReference());
					}
				}
			} else {
				if (productNode.hasNode(Constants.IMAGE)) {
					productNode.getNode(Constants.IMAGE).remove();
				}
			}

			if ((null != product.getProductHeaderInnerImageImported()
					&& product.getProductHeaderInnerImageImported().length() > 0)
					|| (null != product.getProductHeaderImageImported()
							&& product.getProductHeaderImageImported().length() > 0)) {
				if (isNew) {
					Node productNodeassets = productNode.addNode("assets", Constants.NT_UNSTRUCTURED);

					if (null != product.getProductHeaderInnerImageImported()
							&& product.getProductHeaderInnerImageImported().length() > 0) {
						Node productNodeassetFileReference = productNodeassets.addNode("asset",
								Constants.NT_UNSTRUCTURED);
						productNodeassetFileReference.setProperty(Constants.PRODUCT_IMAGE,
								product.getProductHeaderInnerImageImported());
						productNodeassetFileReference.setProperty(Constants.PRODUCT_IMAGE_RESOURCETYPE,
								"commerce/components/product/image");
						productNodeassetFileReference.setProperty(Constants.PRODUCT_TAGGING,
								Constants.HEADER_INNER_IMAGE);
					}

					if (null != product.getProductHeaderImageImported()
							&& product.getProductHeaderImageImported().length() > 0) {

						Node productHeaderImageAssetNode = productNodeassets.addNode("asset0",
								Constants.NT_UNSTRUCTURED);
						productHeaderImageAssetNode.setProperty(Constants.PRODUCT_IMAGE,
								product.getProductHeaderImageImported());
						productHeaderImageAssetNode.setProperty(Constants.PRODUCT_IMAGE_RESOURCETYPE,
								"commerce/components/product/image");
						productHeaderImageAssetNode.setProperty(Constants.PRODUCT_TAGGING,
								Constants.HEADER_PRODUCT_IMAGE_TAG);
					}
				} else {
					if (!productNode.hasNode("assets")) {

						Node productNodeassets = productNode.addNode("assets", Constants.NT_UNSTRUCTURED);

						if (null != product.getProductHeaderInnerImageImported()
								&& product.getProductHeaderInnerImageImported().length() > 0) {
							Node productNodeassetFileReference = productNodeassets.addNode("asset",
									Constants.NT_UNSTRUCTURED);
							productNodeassetFileReference.setProperty(Constants.PRODUCT_IMAGE,
									product.getProductHeaderInnerImageImported());
							productNodeassetFileReference.setProperty(Constants.PRODUCT_IMAGE_RESOURCETYPE,
									"commerce/components/product/image");
							productNodeassetFileReference.setProperty(Constants.PRODUCT_TAGGING,
									Constants.HEADER_INNER_IMAGE);
						}

						if (null != product.getProductHeaderImageImported()
								&& product.getProductHeaderImageImported().length() > 0) {

							Node productHeaderImageAssetNode = productNodeassets.addNode("asset0",
									Constants.NT_UNSTRUCTURED);
							productNodeassets.orderBefore("asset0", null);
							productHeaderImageAssetNode.setProperty(Constants.PRODUCT_IMAGE,
									product.getProductHeaderImageImported());
							productHeaderImageAssetNode.setProperty(Constants.PRODUCT_IMAGE_RESOURCETYPE,
									"commerce/components/product/image");
							productHeaderImageAssetNode.setProperty(Constants.PRODUCT_TAGGING,
									Constants.HEADER_PRODUCT_IMAGE_TAG);
						}
					} else {
						if (productNode.hasNode("assets/asset")) {
							Node ProductNodeImageasset = productNode.getNode("assets/asset");
							if (null != product.getProductHeaderInnerImageImported()
									&& product.getProductHeaderInnerImageImported().length() > 0) {
								ProductNodeImageasset.setProperty(Constants.PRODUCT_IMAGE,
										product.getProductHeaderInnerImageImported());
							}

						} else {
							if (null != product.getProductHeaderInnerImageImported()
									&& product.getProductHeaderInnerImageImported().length() > 0) {
								Node productNodeassets = productNode.getNode("assets");
								Node productNodeassetFileReference = productNodeassets.addNode("asset");
								productNodeassetFileReference.setProperty(Constants.PRODUCT_IMAGE,
										product.getProductHeaderInnerImageImported());
								productNodeassetFileReference.setProperty(Constants.PRODUCT_IMAGE_RESOURCETYPE,
										"commerce/components/product/image");
								productNodeassetFileReference.setProperty(Constants.PRODUCT_TAGGING,
										Constants.TV_PACKSHOT);
							}
						}
						if (productNode.hasNode("assets/asset0")) {
							Node ProductNodeHeaderImageImageasset = productNode.getNode("assets/asset0");
							if (null != product.getProductHeaderImageImported()
									&& product.getProductHeaderImageImported().length() > 0) {
								ProductNodeHeaderImageImageasset.setProperty(Constants.PRODUCT_IMAGE,
										product.getProductHeaderImageImported());
							}

						} else {
							if (null != product.getProductHeaderImageImported()
									&& product.getProductHeaderImageImported().length() > 0) {
								Node productNodeassets = productNode.getNode("assets");
								Node productNodeassetHeaderImageFileReference = productNodeassets.addNode("asset0");
								productNodeassets.orderBefore("asset0", null);
								productNodeassetHeaderImageFileReference.setProperty(Constants.PRODUCT_IMAGE,
										product.getProductHeaderImageImported());
								productNodeassetHeaderImageFileReference.setProperty(
										Constants.PRODUCT_IMAGE_RESOURCETYPE, "commerce/components/product/image");
								productNodeassetHeaderImageFileReference.setProperty(Constants.PRODUCT_TAGGING,
										Constants.HEADER_PRODUCT_IMAGE_TAG);

							}
						}
					}

				}
			} else {
				if (productNode.hasNode("assets")) {
					productNode.getNode("assets").remove();
				}
			}

		} catch (RepositoryException e) {
			LOGGER.error("CSVProductImporter.setProperties(): RepositoryException - {}", e);
		}
	}

}