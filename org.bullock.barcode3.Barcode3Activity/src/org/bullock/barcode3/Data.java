package org.bullock.barcode3;



import android.util.Log;

public class Data {
 
 
	   int status;
	   long     dbId;
	   String   error;
	   String	title;
	   String	author;
	   String 	publisher;
	   String   salesRank;
	   
	   String   lowestNewPrice;
	   String   lowestUsedPrice;
	   String   lowestCollectiblePrice;
	   
	   String   url;
	   String   scan_result;
	   String   scan_result_format;
	   
	
	   public Data (String str) {
		   title = str;
	   }
	   public Data () {
	   }
	   
	   public String getDetailString() {
			String  details =  
					   title +"/"+
					   author +"/"+
					   publisher +"/"+
					   salesRank +"/"+
					   lowestNewPrice +"/"+
					   lowestUsedPrice +"(" +
					//TODO   url +"/"+
					   scan_result +"/"+
					   scan_result_format + ")";
			return details;
	   }
	   	   
	   public String getFullDetailString() {
			String  details =  
					   title +"\n"+
					   author +"\n"+
					   publisher +"\n"+
					   salesRank +"\n"+
					   lowestNewPrice +"\n"+
					   lowestUsedPrice +"\n" +
					   url +"\n"+
					   scan_result +"\n"+
					   scan_result_format + "\n";
			return details;
	   }
 
	   public String getTitlePriceString() {
		   String  titlePrice=
				   author          +"/"+
				   title           +"/"+
				   lowestUsedPrice ;
		   return titlePrice;
	   }
	   //
	   // strip off leading pound sign and return as Float value in pounds
	   // TODO - catch exceptions
	   public float floatLowestUsedPrice () {
		    if (lowestUsedPrice == null) return -1;
		   
			String tmp = lowestUsedPrice.replaceAll("£", "");
			return new Float(tmp); 
	   }
 
 

}
