package org.bullock.barcode3;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.provider.BaseColumns;
 

public class DataList extends SQLiteOpenHelper{
	private static final String FILE = "DataList.java";
	private static final String DBNAME = "data.db";
	private static final int    DBVERSION = 1;
	
	

	private ArrayList<Data>   items       = new ArrayList<Data>();

	private Activity parentActivity;
		
	   //
	   // constructor 
	   //
	   public DataList(Context ctx) {
		   super (ctx, DBNAME, null, DBVERSION );
		   Log.d(FILE, "DataList constructor()");
		   parentActivity = (Activity)ctx;
		   initialise();
	   }
	 
	   //
	   // class methods
	   //
	   @Override
	   public void onCreate(SQLiteDatabase db) {
		   db.execSQL ("CREATE TABLE  data ( "+BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
				   							 "title                TEXT ,"+
				   							 "author               TEXT ,"+
				   							 "publisher            TEXT ,"+
				   							 "salesRank            TEXT ,"+
				   							 "lowestNewPrice       TEXT ,"+
				   							 "lowestUsedPrice      TEXT ,"+
				   							 "url                  TEXT ,"+
				   							 "scan_result          TEXT ,"+
				   							 "scan_result_format   TEXT )");
	   }
	   @Override
	   public void onUpgrade(SQLiteDatabase db, int newversion, int oldversion) {
		   db.execSQL("DROP TABLE IF EXISTS data");
		   onCreate(db);
	   }

	   
	   
	   
	   //
	   // public methods
	   //
	   public ArrayList<Data> getArrayListData() {
		    Log.d(FILE,"getArrayListData()");
	        return items;
	   }
	   
	   public Data getItem(int id) {
		    Log.d(FILE,"getItem("+id+")");
		    return items.get(id);
	   }
	   
	   public void add (Data data) {
		   // add to top
		   items.add(0, data);
		   Log.d(FILE, "DataList add() title = "+data.title);

	 	   	
	 	   //write to Db
	 	   SQLiteDatabase db = this.getWritableDatabase();
	 	   ContentValues values = new ContentValues();
 
	 	   values.put("title"              , data.title );
		   values.put("author"             , data.author);
 		   values.put("publisher"          , data.publisher);
		   values.put("salesRank"          , data.salesRank);
		   values.put("lowestNewPrice"     , data.lowestNewPrice);
		   values.put("lowestUsedPrice"    , data.lowestUsedPrice);
  		   values.put("url"                , data.url);
		   values.put("scan_result"        , data.scan_result);
		   values.put("scan_result_format" , data.scan_result_format);
		   
		   //todo - handle exception
		   data.dbId =  db.insertOrThrow("data", null, values);
		   		// pointer? - yes!
 			
	   }
	   
	   // here, id is the position in the array
	   public void delete (int id) {
		   // lookup in memory
		   long dbId = items.get(id).dbId;
		   Log.d(FILE, "DataList delete() id = "+id+",dbId="+dbId);

		   //delete from memory
		   items.remove(id);
		   //delete from Db
	 	   SQLiteDatabase db = this.getWritableDatabase();
	 	   db.delete("data",BaseColumns._ID +" =  "+ dbId , null);

	   }
	   
	   //relys on data.dbId - the identity value
       public void update(Data data) {
	 	   SQLiteDatabase db = this.getWritableDatabase();

	 	   ContentValues updateValues = new ContentValues();
	 	  
		   updateValues.put("title"              , data.title );
		   updateValues.put("author"             , data.author);
		   updateValues.put("publisher"          , data.publisher);
		   updateValues.put("salesRank"          , data.salesRank);
		   updateValues.put("lowestNewPrice"     , data.lowestNewPrice);
		   updateValues.put("lowestUsedPrice"    , data.lowestUsedPrice);
		   updateValues.put("url"                , data.url);
		   updateValues.put("scan_result"        , data.scan_result);
		   updateValues.put("scan_result_format" , data.scan_result_format);
		   
	 	  db.update("data", updateValues, BaseColumns._ID + "=" + data.dbId, null);
       }
       
       public void clear() {

		   //delete from memory
		   items.clear();
		   //delete from Db
	 	   SQLiteDatabase db = this.getWritableDatabase();
	 	   db.delete("data",null, null);		   
       }
       public int size() {
    	   return items.size();
       }
	   
	   
	   
	   
	   
	   //
	   // private methods
	   //
	   private void initialise() {		   
		   // Database load
		   Cursor curs = prepareQuery();
		   execQuery(curs);
 
	   }
	   
	   private static String[] fromString = { BaseColumns._ID, "title", "author", "publisher", "salesRank","lowestNewPrice",
               "lowestUsedPrice", "url", "scan_result", "scan_result_format" };   
	   
	   private Cursor prepareQuery() {
		   // Managed Query
	 	   SQLiteDatabase db = this.getReadableDatabase(); 
	 	   Cursor cursor = db.query("data", fromString, null, null, null, null, BaseColumns._ID);
	 	   
	 	  // deprecated, TODO
	 	   parentActivity.startManagingCursor (cursor);
	 	  return cursor;

	   }
	   
	   private void execQuery (Cursor cursor) {

		   while (cursor.moveToNext()) {
			    StringBuilder builder = new StringBuilder("Saved:\n");
			    Data data = new Data();
			    data.dbId                = cursor.getLong(0);
		    	data.title               = cursor.getString(1);
		    	data.author              = cursor.getString(2);
		    	data.publisher           = cursor.getString(3);
		    	data.salesRank           = cursor.getString(4);
		    	data.lowestNewPrice      = cursor.getString(5);
		    	data.lowestUsedPrice     = cursor.getString(6);
		    	data.url                 = cursor.getString(7);
		    	data.scan_result         = cursor.getString(8);
		    	data.scan_result_format  = cursor.getString(9);


				items.add(0, data);
		   }
 

	   }
	   
}
