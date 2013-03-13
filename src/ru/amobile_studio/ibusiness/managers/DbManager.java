package ru.amobile_studio.ibusiness.managers;

import ru.amobile_studio.ibusiness.DBHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DbManager {
	
	private DBHelper dbHelper;
	private SQLiteDatabase db = null;
	
	public DbManager(Context context){
		dbHelper = new DBHelper(context);
	}
	
	public SQLiteDatabase connectToDb(){
		if(db == null){
			db = dbHelper.getWritableDatabase();
			return db;
		}
		return db;
	}
	
	public void disconnect(){
		if(db != null){
			db.close();
		}
	}
	
}
