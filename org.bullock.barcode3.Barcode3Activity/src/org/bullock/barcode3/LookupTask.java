package org.bullock.barcode3;

import android.util.Log;

public class LookupTask implements Runnable{
	private static final String FILE = "LookupTask.java";
	
	private Barcode3Activity parent;
	private Data             data;
	private Scanner			 scanner;
	private String			 scanResult_contents;
	
 	LookupTask(Barcode3Activity p, Scanner s, Data d, String contents){
		// init stuff
		parent 				= p;
		data   				= d;
		scanner 			= s;
		scanResult_contents = contents;
	}
	
	public void run() {
 
        // Start the clock
        long startTime = System.currentTimeMillis();


        
        // server Lookup 1  
        if (scanner.lookupPrices(data, scanResult_contents)) {
     	   //error 
     	   parent.showError("Error connecting to Server");
     	   return;
        }

        
        // server Lookup 2  
        if (scanner.lookupTitle(data, scanResult_contents)){
     	   //error 
        	parent.showError ("Error connecting to Server");
     	   return;
        }


        // stop the clock! 
        long endTime = System.currentTimeMillis();
        Log.i(FILE, "That took " + (endTime - startTime) + " milliseconds");

        // save extra details from Zxing
        data.scan_result            = scanResult_contents;
   // TODO     data.scan_result_format     = scanResult_format;
		  
        
        
		// call gui thread to update view + persistent store
		parent.setUpdated(data);
	}

}
