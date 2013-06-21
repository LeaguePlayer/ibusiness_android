package ru.amobile_studio.ibusiness;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	final String LOG_TAG = "myLogs";
	public final static String DBNAME = "ibizDB";
	
	public DBHelper(Context context) {
		super(context, DBNAME, null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		Log.d(LOG_TAG, "--- onCreate databases ---");
	    // создаем таблицу partners
		db.execSQL("CREATE TABLE partners ("
			  + "_id INTEGER PRIMARY KEY AUTOINCREMENT," 
	          + "img TEXT,"
	          + "version INTEGER" + ");");
		// создаем таблицу events
		db.execSQL("CREATE TABLE events ("
	          + "_id INTEGER PRIMARY KEY AUTOINCREMENT," 
	          + "id_type INTEGER,"
	          + "title TEXT,"
	          + "short_desc TEXT,"
	          + "full_desc TEXT,"
	          + "date_start TEXT,"
	          + "date_finish TEXT,"
	          + "show INTEGER,"
	          + "version INTEGER" + ");");
		// создаем таблицу speakers
		db.execSQL("CREATE TABLE speakers ("
		      + "_id INTEGER PRIMARY KEY AUTOINCREMENT," 
		      + "id_event INTEGER,"
		      + "name_speaker TEXT,"
		      + "about_speaker TEXT,"
		      + "title_report TEXT,"
		      + "about_report TEXT,"
		      + "img TEXT,"
		      + "data_sort INTEGER,"
		      + "version INTEGER" + ");");
		// создаем таблицу schedule
		db.execSQL("CREATE TABLE schedule ("
			  + "_id INTEGER PRIMARY KEY AUTOINCREMENT," 
			  + "id_event INTEGER,"
		      + "title TEXT,"
		      + "subtitle TEXT,"
		      + "time_begin TEXT,"
		      + "time_finish TEXT,"
		      + "at_day TEXT,"
		      + "know_time_finish INTEGER,"
		      + "coffee_break INTEGER,"
		      + "data_sort INTEGER,"
		      + "version INTEGER" + ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS partners");
		onCreate(db);
	}
	
	public void clear(SQLiteDatabase db){
		Log.d(LOG_TAG, "Drop table");
		db.execSQL("DROP TABLE IF EXISTS partners");
		db.execSQL("DROP TABLE IF EXISTS events");
		db.execSQL("DROP TABLE IF EXISTS schedule");
		onCreate(db);
	}

}
