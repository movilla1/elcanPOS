package com.elcansoftware.elcanpos;



public class elcanPosOrder {
  protected String sku;
  protected String name;
  protected Double price;
  protected Integer qty;
  protected Double subtotal;

  //Constructor should only clear the order map
  public elcanPosOrder() {
	  name=sku="";
	  qty=0;
	  price=subtotal=(double)0;
  }
  public elcanPosOrder(String nam,double pric,int qty1,double subtot,String skU) {
	  name=nam;
	  price=pric;
	  qty=qty1;
	  subtotal=subtot;
	  sku=skU;
  }
  /**
   * This function adds an sku to the order.
   * @param sku2 is a string with the sku to add.
   * @param nam is the article name
   * @param pric is the article price
   */
  public void setItem(String sku2,String nam,Double pric) {
	  sku=sku2;
	  qty=1;
	  subtotal=(float)qty*price;
	  price=pric;
	  name=nam;
  }
  
  /**
   * This function increments the qty of an article
   */
  public void incItem() {
	  qty++;
  }
  
  public String getName() {
	return name;
  }
  public Double getPrice() {
	return price;
  }
	
  public Double getSubTotal() {
	double ret;
	ret=qty*price;
	subtotal=ret;
	return ret;
  }
	
  public Integer getQty() {
	return qty;
  }
  public String getSku() {
	  return sku;
  }
  /**
   * This function returns the items in a comma separated list, one sku and it's qty
   * on each array position, will be used for the data representation in the listview
   * @return a string array with "sku, qty" on each item.
   */
  public String getItem() {
	 String ret=name+","+price.toString()+","+qty.toString()+","+subtotal.toString();
	 return ret;
  }
  
  public String[] getArray() {
	  String ret[]=new String[4];
	  ret[0]=name;
	  ret[1]=price.toString();
	  ret[2]=qty.toString();
	  ret[3]=subtotal.toString();
	  return ret;
  }
}
