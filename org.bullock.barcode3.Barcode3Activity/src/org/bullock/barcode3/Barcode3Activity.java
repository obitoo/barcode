package org.bullock.barcode3;

 

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

 

public class Barcode3Activity extends Activity implements OnClickListener, OnItemClickListener {
	
	private static final String FILE = "Barcode3Activity.java";
	private static final int REQUEST_SCAN  = 1;
	
	ArrayAdapter<Data> 	ar;
	Scanner 			scanner;
	DataList   			dataList;
	
	// threading
	private Handler    		 guiThread;
	private ExecutorService  workerThread;
	private Runnable		 updateTask;
	private Future           workerPending;
	
	// not sure if this shit is thread safe like this
    private String scanResult_contents;
    private String scanResult_format;
    private Data data;
    
	
	private int 		listItemSelected; 				// meh
	    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Set up click listeners for our buttons
        View button1 = findViewById(R.id.button1);
        button1.setOnClickListener(this);
 
        // setup the listview
  	    ListView lv =  (ListView) findViewById(R.id.listView1);
  	    
  	    // click listeners
  	    lv.setOnItemClickListener(this);
  	    lv.setOnItemLongClickListener(new OnItemLongClickListener() {
		    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			      // When clicked, show a toast with the TextView text
			      Toast.makeText(getApplicationContext(), "Long Click",Toast.LENGTH_SHORT).show();
			      // open dialog, for amazon or delete
			      showListDialog((int)id);
			      return true;
		    	}
  	    });
 	  
  	    // create helper objects
  	    scanner = new Scanner(this);
  	    dataList = new DataList(this);
  		ar = new CustomArrayAdapter(this, R.layout.list_item, dataList.getArrayListData());
  	    lv.setAdapter(ar);
  	    
  	    // threads
  	    initThreading();
  	   
    }
   
    
    
	    //
	    // Private methods
	    //

    // yuck. Both use the global variable 'listItemSelected'
    private void launchAmazon() {
	    // extract Url
    	String url = dataList.getItem(listItemSelected).url;
    	if ((null == url)|| (!url.startsWith("http://"))) {
    		showOkDialog ("No link to Amazon on this one, sorry!");
        	return;
    	}
    	// Mobile site:  replace offer-listing with aw/ol/offer-listing/
    	String mobileUrl = url.replaceFirst("offer", "aw\\/ol\\/offer");
    	
    	// Toast
    	Toast.makeText(getApplicationContext(), "launching Amazon Site",Toast.LENGTH_SHORT).show();
	    Log.d(FILE, "launchAmazon("+mobileUrl+")");

	    // open browser Intent
	    Uri uri = Uri.parse(mobileUrl);
	    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	    startActivity(intent);
    }

    private void showOkDialog(String str) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(str);
		builder.setCancelable(false);
	    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
			      // just do nothing
	        }
	    });
	    builder.show();
    }
    
    // listItemSelected is set in here
    private void showListDialog(int id) {
	    Log.d(FILE, "showListClickChoices("+id+")");

	    listItemSelected = id; // global, yuck
	    
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    // locate correct data item
	    Data data = dataList.getItem(id);
	    // build dialog
	    builder.setMessage("Go to Amazon site, or Delete from list?");
	    builder.setPositiveButton("Amazon", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
			      // launch amazon
			      Barcode3Activity.this.launchAmazon();
	        }
	    })
	    .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
	              dialog.cancel(); 			 
			      // call delete 
			      Barcode3Activity.this.deleteItem();
 	        }
	    });
	    // display it
	    AlertDialog alert = builder.create();
	    alert.setIcon(R.drawable.barcode); //TODO
	    alert.setTitle(data.title);
	    alert.show();
	    
    }

 
  
    
 
	//
	// Listener/Callback Methods
	//
		// ------------Button at the top
	// @Override
	public void onClick(View v) {
		    int id = v.getId();
		    Log.d(FILE, "onClick");
		      
		    switch (id) {
	        case R.id.button1:
		        	scanner.launch();
	           // the rest is done in the callback fn onActivityResult()
	
	        break;
		    }
	}
		//--------------------- List View
	public void onItemClick(AdapterView<?> parent, View view,
	  	                        int position, long id) {
 	       	Log.d(FILE, "onItemClick() id =" + id + "posn ="+position);

 	       	// Show info popup.  
	          Intent i = new Intent (this, Info.class);
	          Data data = dataList.getItem((int)id);
	          i.putExtra("string1", data.getFullDetailString());
	   	      startActivity(i);
	}

    
    //
	// for Scanner Callback
	//
	private void greenToast (String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_LONG);
		TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
		v.setTextColor(Color.GREEN);
		toast.show();
	}
	private void defaultToast (String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
		TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
		//v.setTextColor(Color.GREEN);
		toast.show();
	}
	
	
	
	
	//
    // callback from barcode scanner
	//
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
  	   Log.d(FILE, "onActivityResult(), resultCode is "+resultCode);

       if (requestCode == REQUEST_SCAN) {
            if (resultCode == RESULT_OK) {
               scanResult_contents = intent.getStringExtra("SCAN_RESULT");
               scanResult_format = intent.getStringExtra("SCAN_RESULT_FORMAT");
              
               // Create new object
               data = new Data();
               
               // Add to view, get key
               dataList.add(data);
               long id = data.dbId;
               Log.d (FILE, "dataList.add() returns dbId of "+id);
               
               
               // queue up a thread
               guiThread.post(updateTask);
               
               // launch again
               if (!scanner.testMode()) scanner.launch(); 
               
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel - don't launch again
            	// but refresh view
         	   	ar.notifyDataSetChanged();

            }
 	    }
 	}
    
    //
    // called from the worker thread
    //
    public void setUpdated(final Data data) {
    	guiThread.post(new Runnable() {
    		public void run() {
    	        //  show Alert in green if good price
    	        if (data.floatLowestUsedPrice() > 0.01) {
    	           greenToast ("OOOH!        Lowest Used Price: "+data.lowestUsedPrice );
    	       } else {
    	           defaultToast ("Lowest Used Price: "+data.lowestUsedPrice );
    	       }
    	 
    	       // refresh screen view
    		   ar.notifyDataSetChanged();
    	       
    			   // Save to persistent store
    	       dataList.update(data);
    		}
    	});
 
     }
    
    // todo - thread
    public void showError (String err) {
    	Log.d(FILE, err);
    }
    
    private void initThreading() {
    	guiThread = new Handler();
    	workerThread = Executors.newSingleThreadExecutor();
    	
    	// this task updates the screen.... mebbe
    	updateTask = new Runnable() {
    		public void run() {
    			// get data item. Now, does this need to be a copy?
    			   // global, for now
    			
    			
    			// let user know we're doing summat
    			data.title = ".....IN PROGRESS....";
         	    ar.notifyDataSetChanged();
         	    
    			// kick off network code 
    			try {
    				LookupTask lookupTask = new LookupTask(Barcode3Activity.this, scanner, data, scanResult_contents  );
    				workerPending = workerThread.submit(lookupTask);
    			} catch (RejectedExecutionException e) {
    				// unable to start new task
    				e.printStackTrace();
    			}
    		}
    	};
    	
    }

    // TODO - this is unlikely to work. 
    //hang on hang on - it does!
    private void deleteItem() {  // called back from dialog so uses global var not param
	      Toast.makeText(getApplicationContext(), "Item "+listItemSelected+" deleted", Toast.LENGTH_SHORT).show();
	      dataList.delete(listItemSelected);
	      // update view
	 	  ar.notifyDataSetChanged();
    }
 
    //
    // Menu-key: settings and About
    //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()) {
       case R.id.settings:
          startActivity(new Intent(this, Prefs.class));
          return true;
       case R.id.info:
          Intent i = new Intent (this, Info.class);
         //TODO i.putExtra("string1", R.string.about_string);
           i.putExtra("string1", "Helptext here / "+ dataList.size() + " items");
   	      startActivity(i);
	      return true;
       case R.id.clear:
   		  showClearDialog ("Delete all scanned books - are you sure?");
	      return true;
	   }
    
       return false;
    }    
    
    
    private void showClearDialog(String str) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(str);
	    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
	        	   // delete everything
	        	dataList.clear();
	    	       // refresh screen view
	    	    ar.notifyDataSetChanged();
 
	        }
	    });
	    builder.setNegativeButton("No way!", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
			      // just do nothing
	        }
	    });
	    builder.show();
    }
    
    
//EOF

}