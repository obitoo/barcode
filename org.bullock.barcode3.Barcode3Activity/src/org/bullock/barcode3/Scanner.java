package org.bullock.barcode3;


import java.net.UnknownHostException;

import org.bullock.barcode3.NetCode;
import org.bullock.barcode3.Data;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
 

public class Scanner {//extends Activity {
	private static final String FILE = "Scanner.java";
	private static final int REQUEST_SCAN  = 1;
	private final Barcode3Activity parent;

	//
	// constructors
	//
	
	public Scanner(Context context) {
		this.parent=(Barcode3Activity)context;
	}
	
    //returns extra ("SCAN_RESULT");
    //        extra ("SCAN_RESULT_FORMAT");
    public void launch() {
    	
    	// launch ZXing activity
    	if (!Prefs.inDebugMode(parent)) { 
	        try {
		           Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		           intent.setPackage("com.google.zxing.client.android");
		           intent.putExtra("SCAN_MODE", "ONE_D_MODE");
		           intent.addCategory(Intent.CATEGORY_DEFAULT); //setting the category - necessary??. 
	
		           parent.startActivityForResult(intent, REQUEST_SCAN);
		       } catch (ActivityNotFoundException e) {
		    	   Toast toast = Toast.makeText(parent, "This requires the ZXing barcode reader app.  Install it and try again, mofo", 
	 				                    Toast.LENGTH_LONG);
		    	   toast.setGravity(Gravity.CENTER, 0, 0);
		    	   toast.show();
		       }
    	} else { 
	        try {
	           Intent intent = new Intent(parent, ZxingDummy.class);
	           parent.startActivityForResult(intent, REQUEST_SCAN);
	           
	    	   	// fill dummy values 
	    		intent.putExtra ("SCAN_RESULT"       , "9781934356562");
	    		intent.putExtra ("SCAN_RESULT_FORMAT", "EAN13");
		    } catch (ActivityNotFoundException e) {
		    	Toast toast = Toast.makeText(parent, "ZxingDummy try again, mofo", Toast.LENGTH_LONG);
		    	toast.setGravity(Gravity.CENTER, 0, 0);
		    	toast.show();
		    }

    	}
    }
    
    public boolean testMode() {
    	return Prefs.inDebugMode(parent);
    }

    
    //
    // Make 2 separate Net queries to my server
    //
    public boolean  lookupPrices (Data singleData, String ain_number) {
    	  Log.d(FILE, "lookupPrices()"+ain_number);
    	  boolean error = false;
 

	      // run the query
		  NetCode net = new NetCode(parent);
		  try {
			  Data prices = net.marketQuery(ain_number);
			  
			  singleData.salesRank              = prices.salesRank;
			  singleData.lowestNewPrice         = prices.lowestNewPrice;
			  singleData.lowestUsedPrice        = prices.lowestUsedPrice;
			  singleData.lowestCollectiblePrice = prices.lowestCollectiblePrice;
	 		  singleData.url                    = prices.url;
	 		  
 		   } catch (UnknownHostException e) {
			     Log.d(FILE,"HEY! High catch: UnknownHostException:"+e.getMessage());
			     error = true;
 		   }
 
 		   return error;
    }
    public boolean lookupTitle (Data singleData, String ain_number) {
	  	  Log.d(FILE, "lookupTitle()"+ain_number);
		  boolean error = false;

	      // run the query
		  NetCode net = new NetCode(parent);
		  try {
	
			  Data title = net.titleQuery(ain_number);
	 
			  singleData.title                  = title.title    ;
			  singleData.author                 = title.author;
			  singleData.publisher              = title.publisher;
			  
		   } catch (UnknownHostException e) {
			     Log.d(FILE,"HEY! High catch: UnknownHostException:"+e.getMessage());
			     error = true;
		   }

 		  return error;
  }

    
}
