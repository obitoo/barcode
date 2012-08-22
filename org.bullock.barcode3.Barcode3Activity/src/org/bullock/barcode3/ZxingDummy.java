package org.bullock.barcode3;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class ZxingDummy extends Activity {
	private static final String FILE = "ZxingDummy.java";

	   static int  count = 0;

	   protected void onCreate(Bundle savedInstanceState) {
 		        
		   		super.onCreate(savedInstanceState);
		      	Log.d(FILE, "onCreate");
		      	
		      	
		      	
		      	// dummy data
	    		count++;
 	    		getIntent().putExtra ("SCAN_RESULT_FORMAT", "EAN13");
 	    		int opt =(count %3);
 	    		switch (opt) {
 	    		case 0:  getIntent().putExtra ("SCAN_RESULT"       , "9781934356562");
 	    				 break;
 	    		case 1:  getIntent().putExtra ("SCAN_RESULT"       , "185723457X");
 						 break;
 	    		default: getIntent().putExtra ("SCAN_RESULT"       , "0963617885");
 	    				 break;
	    		}  

	    		// and return
	    		setResult(RESULT_OK, getIntent()); 
	    		finish();
	   }
}
