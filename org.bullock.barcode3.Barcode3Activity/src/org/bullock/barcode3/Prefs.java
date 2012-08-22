package org.bullock.barcode3;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;



public class Prefs extends PreferenceActivity{
	  private static final String FILE = "Prefs";

      @Override
	  protected void onCreate(Bundle savedInstanceState) {
		      super.onCreate(savedInstanceState);
		      addPreferencesFromResource(R.xml.prefs);
		      
		      
		   }
	   
 
	   public static boolean inDebugMode(Context context) {
	   		boolean val = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("debug", false);
	   		return val;
 	   }
	   public static String ipAddr(Context context) {
 	   		String val = PreferenceManager.getDefaultSharedPreferences(context).getString("ipaddr","0.0.0.0");
	   		return val;
	   }
	   
}
