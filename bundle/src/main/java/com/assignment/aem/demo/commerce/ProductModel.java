package com.assignment.aem.demo.commerce;

/**
 * 
 * Model for Product
 *
 */
public class ProductModel {
    
	private String identifier;
	private String productNameImported;
    private String productDescriptionImported;
    private String productHeaderImageImported;
    private String productHeaderInnerImageImported;
    private String productHeadlineImported;
    private String productLinkImported;
    private String productTextImported;
    private String fileReference;
    
    /**
     * Getter ProductModel  SKU
     * 
     * @return
     */
   public String getSKU() {
       return identifier;
   }

   /**
    * Setter ProductModel  title
    * 
    * @return
    */
   public void setSKU(String identifier) {
       this.identifier = identifier;
   }
   
   
	public String getProductNameImported() {
		return productNameImported;
	}
	public void setProductNameImported(String productNameImported) {
		this.productNameImported = productNameImported;
	}
	public String getProductDescriptionImported() {
		return productDescriptionImported;
	}
	public void setProductDescriptionImported(String productDescriptionImported) {
		this.productDescriptionImported = productDescriptionImported;
	}
	public String getProductHeaderImageImported() {
		return productHeaderImageImported;
	}
	public void setProductHeaderImageImported(String productHeaderImageImported) {
		this.productHeaderImageImported = productHeaderImageImported;
	}
	public String getProductHeadlineImported() {
		return productHeadlineImported;
	}
	public void setProductHeadlineImported(String productHeadlineImported) {
		this.productHeadlineImported = productHeadlineImported;
	}
	public String getProductLinkImported() {
		return productLinkImported;
	}
	public void setProductLinkImported(String productLinkImported) {
		this.productLinkImported = productLinkImported;
	}
	public String getProductTextImported() {
		return productTextImported;
	}
	public void setProductTextImported(String productTextImported) {
		this.productTextImported = productTextImported;
	}
	public String getFileReference() {
		return fileReference;
	}
	public void setFileReference(String fileReference) {
		this.fileReference = fileReference;
	}

	public void setProductHeaderInnerImageImported(String hederInnerImageImported) {
		this.productHeaderInnerImageImported=hederInnerImageImported;		
	}
	
	public String getProductHeaderInnerImageImported() {
		return productHeaderInnerImageImported;
	}

    
 }
