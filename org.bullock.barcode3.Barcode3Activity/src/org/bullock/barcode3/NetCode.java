package org.bullock.barcode3;


 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import org.bullock.barcode3.Data;


public class NetCode   {
	   private static final String FILE = "NetCode";
	 //  private static final String SERVERIP   = "82.69.65.202";// "192.168.7.11";
	   private static final int    SERVERPORT =  3499;
	   private  Barcode3Activity parent;
	   
	   //
	   // Constructor
	   //
	   public NetCode (Barcode3Activity act) {
		   parent = act;
	   }
	   
	   
	   //
	   // Public methods
	   //

	   public boolean networkPresent(Context context) {
		   // check for network availability
		   Log.d(FILE, "NetCode() - checking network availability");
		   
		   ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		   NetworkInfo ni = cm.getActiveNetworkInfo();
		   if(ni == null)
			   return false;
		   
		   return true;
	   }
	   public Data titleQuery(String queryString) throws UnknownHostException{
 	      Log.d(FILE, "titleQuery()");
        
 	      // run the query
	      String retstring = doQuery(3, queryString);  // 3 title, author
	      
	      // parse query. tells us if it was good or bad
	      Data data = parseTitleQuery(retstring);

	      // finished
	      return data;
	   }
	   public Data marketQuery(String queryString)throws UnknownHostException{
	 	      Log.d(FILE, "marketQuery()");
	        
	 	      // run the query
		      String retstring = doQuery(4, queryString);  // 4- marketplace
		      
		      // parse query. tells us if it was good or bad
		      Data data = parseMarketQuery(retstring);

		      // finished
		      return data;
		   }
	   
	   
	   
	   //
	   // Private - talk to the Server
	   //
	   private String doQuery(int queryType, String isbn) throws UnknownHostException {
		   String  ret        = null;
		   String  serverAddr = Prefs.ipAddr(parent);
		   int     serverPort = SERVERPORT;
		   
		   String query = Integer.toString(queryType)+isbn; 
		   Log.d(FILE,"doQuery("+queryType+"):"+query);
		   
		   try {
			   //connect
			   Socket sock = new Socket(serverAddr, serverPort);
			   Log.d(FILE, " connected to "+serverAddr+":"+serverPort);
			   //setup in/out streams
 		   	   BufferedReader in = new BufferedReader   (new InputStreamReader(sock.getInputStream()));
			   PrintWriter out = new PrintWriter  (sock.getOutputStream(), true /* autoFlush */);
			 
			   //send
			   out.print(query);
			   out.flush();
			   //receive
			   ret = (String)in.readLine();
			   Log.d(FILE,"server>" + ret);
  		   } 
		   catch (UnknownHostException e) {
  			     Log.d(FILE,"Caught UnknownHostException:"+e.getMessage());
  			     throw e; 
		   } 
		   catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		   }

		   return ret;
	   }

	   //
	   //  Parsing  
	   //
	   private Data parseTitleQuery(String string) {
		   Data retdata = new Data();
		   
		   //check for error code from my server or AWD
		   if (string.startsWith("BAD")){
		   		retdata.status = 1;
		   		retdata.error = string.substring(4);
		   		return retdata;
		   }
		   // so good so far
		   
		   // obviously this tag= data method is somewhat flawed. TODO - find a book with "Title=" in the title
		   retdata.status = 0;
		   int lastIndex = 0;
 		   retdata.title     = findTagData(string, "Title=",lastIndex);
		   retdata.author    = findTagData(string, "Author=",lastIndex);;
		   retdata.publisher = findTagData(string, "Publisher=",lastIndex);;
		   retdata.salesRank = findTagData(string, "SalesRank=",lastIndex);;
		   
		   
		   return retdata;
	   }
	   
	   private Data parseMarketQuery(String string) {
		   Data retdata = new Data();
		   
		   //check for error code from my server or AWD
		   if (string.startsWith("BAD")){
		   		retdata.status = 1;
		   		retdata.error = string.substring(4);
		   		return retdata;
		   }
		   // so good so far
		   
		   // obviously this tag= data method is somewhat flawed. TODO - find a book with "Title=" in the title
		   retdata.status = 0;
		   int lastIndex = 0;
 		   retdata.lowestNewPrice     = findTagData(string, "LowestNewPrice=",lastIndex);
		   retdata.lowestUsedPrice    = findTagData(string, "LowestUsedPrice=",lastIndex);;
		   retdata.lowestCollectiblePrice = findTagData(string, "LowestCollectiblePrice=",lastIndex);
		   retdata.url                    = findTagData(string, "Url=",lastIndex);;

	   
		   return retdata;
	   }
	   
 	   // in format  [tag=][data][\] without the brackets 
	   private String findTagData (String input, String tag, int lastIndex) {
//		   Log.d (FILE, "find ("+tag+":"+input);

		   String ret = null;
		   String tmp;
		   int index = input.indexOf(tag, lastIndex-1);
		   if (index != -1) {
			   tmp=input.substring(index+tag.length());
			   index=tmp.indexOf("\\",0);
			   if (index != -1) 
				   ret=tmp.substring(0,index);
		   }
		   	    
//		   Log.d (FILE, "    ..returns:"+ret);
		   return ret;
	   }
	  

}
