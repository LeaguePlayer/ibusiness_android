package ru.amobile_studio.ibusiness.providers;

import ru.amobile_studio.ibusiness.SplashActivity;
import ru.amobile_studio.ibusiness.managers.DbManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class CProvider extends ContentProvider {

	public static final String PROVIDER_NAME = "ru.amobile_studio.ibusiness.providers";
	
	public static final Uri CONTENT_URI_SPEAKERS = 
			Uri.parse("content://ru.amobile_studio.ibusiness.providers/speakers");
	public static final Uri CONTENT_URI_EVENTS = 
			Uri.parse("content://ru.amobile_studio.ibusiness.providers/events");
	public static final Uri CONTENT_URI_PARTNERS = 
			Uri.parse("content://ru.amobile_studio.ibusiness.providers/partners");
	public static final Uri CONTENT_URI_SCHEDULE = 
			Uri.parse("content://ru.amobile_studio.ibusiness.providers/schedule");
	
	public static final int URI_CODE1 = 1;
	public static final int URI_CODE1_ID = 2;
	public static final int URI_CODE2 = 3;
	public static final int URI_CODE2_ID = 4;
	public static final int URI_CODE3 = 5;
	public static final int URI_CODE3_ID = 6;
	public static final int URI_CODE4 = 7;
	public static final int URI_CODE4_ID = 8;
	
	public static final String TABLE_NAME_SPEAKERS = "speakers";
	public static final String TABLE_NAME_EVENTS = "events";
	public static final String TABLE_NAME_PARTNERS = "partners";
	public static final String TABLE_NAME_SCHEDULE = "schedule";
	
	private static final UriMatcher uriMatcher;
	
	private SQLiteDatabase db;
	
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, TABLE_NAME_SPEAKERS, URI_CODE1);
		uriMatcher.addURI(PROVIDER_NAME, TABLE_NAME_SPEAKERS + "/#", URI_CODE1_ID);
		
		uriMatcher.addURI(PROVIDER_NAME, TABLE_NAME_EVENTS, URI_CODE2);
		uriMatcher.addURI(PROVIDER_NAME, TABLE_NAME_EVENTS + "/#", URI_CODE2_ID);
		
		uriMatcher.addURI(PROVIDER_NAME, TABLE_NAME_PARTNERS, URI_CODE3);
		uriMatcher.addURI(PROVIDER_NAME, TABLE_NAME_PARTNERS + "/#", URI_CODE3_ID);
		
		uriMatcher.addURI(PROVIDER_NAME, TABLE_NAME_SCHEDULE, URI_CODE4);
		uriMatcher.addURI(PROVIDER_NAME, TABLE_NAME_SCHEDULE + "/#", URI_CODE4_ID);
	}
	
	@Override
	public boolean onCreate() {
		Log.d(SplashActivity.LOG_TAG, "onCreate provider");
		       
		DbManager dbm = new DbManager(getContext());
		db = dbm.connectToDb();
		return (db == null) ? false : true;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] args, String sort) {
		Cursor c = null;
		switch(uriMatcher.match(uri)){
			case URI_CODE1:{
				c = db.query(TABLE_NAME_SPEAKERS, projection, selection, args, null, null, sort);
				c.setNotificationUri(getContext().getContentResolver(), uri);
				break;
			}
			case URI_CODE2:{
				c = db.query(TABLE_NAME_EVENTS, projection, selection, args, null, null, sort);
				c.setNotificationUri(getContext().getContentResolver(), uri);
				break;
			}
			case URI_CODE3:{
				c = db.query(TABLE_NAME_PARTNERS, projection, selection, args, null, null, sort);
				c.setNotificationUri(getContext().getContentResolver(), uri);
				break;
			}
			case URI_CODE4:{
				c = db.query(TABLE_NAME_SCHEDULE, projection, selection, args, null, null, sort);
				c.setNotificationUri(getContext().getContentResolver(), uri);
				break;
			}
		}
		
		return c;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] args) {
		
		int retVal = 0;
		
		switch(uriMatcher.match(uri)){
			case URI_CODE1:{
				retVal = db.delete(TABLE_NAME_SPEAKERS, selection, args);
				getContext().getContentResolver().notifyChange(uri, null);
				break;
			}
			case URI_CODE2:{
				retVal = db.delete(TABLE_NAME_EVENTS, selection, args);
				getContext().getContentResolver().notifyChange(uri, null);
				break;
			}
			case URI_CODE3:{
				retVal = db.delete(TABLE_NAME_PARTNERS, selection, args);
				getContext().getContentResolver().notifyChange(uri, null);
				break;
			}
			case URI_CODE4:{
				retVal = db.delete(TABLE_NAME_SCHEDULE, selection, args);
				getContext().getContentResolver().notifyChange(uri, null);
				break;
			}
		}

		return retVal;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri url, ContentValues cv) {
		
		long row_id = 0;
		Uri uri = null;
		
		switch(uriMatcher.match(url)){
			case URI_CODE1:{
				row_id = db.insert(TABLE_NAME_SPEAKERS, null, cv);
				
				if(row_id > 0){
					uri = ContentUris.withAppendedId(CONTENT_URI_SPEAKERS, row_id);
					getContext().getContentResolver().notifyChange(uri, null);
				}else{
					throw new SQLException("Провал вставки " + url);
				}
				break;
			}
			case URI_CODE2:{
				row_id = db.insert(TABLE_NAME_EVENTS, null, cv);
				
				if(row_id > 0){
					uri = ContentUris.withAppendedId(CONTENT_URI_EVENTS, row_id);
					getContext().getContentResolver().notifyChange(uri, null);
				}else{
					throw new SQLException("Провал вставки " + url);
				}
				break;
			}
			case URI_CODE3:{
				row_id = db.insert(TABLE_NAME_PARTNERS, null, cv);
				
				if(row_id > 0){
					uri = ContentUris.withAppendedId(CONTENT_URI_PARTNERS, row_id);
					getContext().getContentResolver().notifyChange(uri, null);
				}else{
					throw new SQLException("Провал вставки " + url);
				}
				break;
			}
			case URI_CODE4:{
				row_id = db.insert(TABLE_NAME_SCHEDULE, null, cv);
				
				if(row_id > 0){
					uri = ContentUris.withAppendedId(CONTENT_URI_SCHEDULE, row_id);
					getContext().getContentResolver().notifyChange(uri, null);
				}else{
					throw new SQLException("Провал вставки " + url);
				}
				break;
			}
		}
		Log.d(SplashActivity.LOG_TAG, "insert - " + url);
		return uri;
	}

	@Override
	public int update(Uri uri, ContentValues cv, String selection, String[] args) {

		int retVal = 0;
		
		switch(uriMatcher.match(uri)){
			case URI_CODE1:{
				retVal = db.update(TABLE_NAME_SPEAKERS, cv, selection, args);
				getContext().getContentResolver().notifyChange(uri, null);
				break;
			}
			case URI_CODE2:{
				retVal = db.update(TABLE_NAME_EVENTS, cv, selection, args);
				getContext().getContentResolver().notifyChange(uri, null);
				break;
			}
			case URI_CODE3:{
				retVal = db.update(TABLE_NAME_PARTNERS, cv, selection, args);
				getContext().getContentResolver().notifyChange(uri, null);
				break;
			}
			case URI_CODE4:{
				retVal = db.update(TABLE_NAME_SCHEDULE, cv, selection, args);
				getContext().getContentResolver().notifyChange(uri, null);
				break;
			}
		}
		
		return retVal;
	}

}
