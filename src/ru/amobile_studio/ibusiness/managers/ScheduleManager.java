package ru.amobile_studio.ibusiness.managers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import ru.amobile_studio.ibusiness.SplashActivity;
import ru.amobile_studio.ibusiness.providers.CProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class ScheduleManager {
	//-----------------------------Для работы с БД---------------------------------
	//Константы имена полей
	public static final String NAME_ID = "_id";
	public static final String NAME_ID_EVENT = "id_event";
	public static final String NAME_TITLE = "title";
	public static final String NAME_SUBTITLE = "subtitle";
	public static final String NAME_TIME_BEGIN = "time_begin";
	public static final String NAME_TIME_FINISH = "time_finish";
	public static final String NAME_AT_DAY = "at_day";
	public static final String NAME_KNOW_TIME_FINISH = "know_time_finish";
	public static final String NAME_COFFEE = "coffee_break";
	public static final String NAME_SORT = "data_sort";
	public static final String NAME_VERS = "version";
	
	//Сущность событие
	public class Schedule{
		public int id;
		public int id_event;
		public String title;
		public String subtitle;
		public String time_begin;
		public String time_finish;
		public String at_day;
		public int know_time_finish;
		public int coffee_break;
		public int data_sort;
		public int version;
		
		public Schedule(int id, int id_event, String title, String subtitle,	String time_begin, 
				String time_finish, String at_day, int know_time_finish, int coffee_break, int data_sort, 
				int version){
			this.id = id;
			this.id_event = id_event;
			this.title = title;
			this.subtitle = subtitle;
			this.time_begin = time_begin;
			this.time_finish = time_finish;
			this.at_day = at_day;
			this.know_time_finish = know_time_finish;
			this.coffee_break = coffee_break;
			this.data_sort = data_sort;
			this.version = version;
		}
		
	}
	//-----------------------------Для работы с БД---------------------------------
	
	final String LOG_TAG = "myLogs";
	private ArrayList<Schedule> fromJson;
	private ArrayList<Schedule> fromDb;
	
	private String reqAll = SplashActivity.DOMEN + "/schedule/getjson/all"; //API request
	private final ContentResolver contentResolver;
	
	//Конструктор
	public ScheduleManager(Context context){
		this.contentResolver = context.getContentResolver();
	}
	
	//Методы
	
	public void loadFromJson() throws InterruptedException, ExecutionException, JSONException{
		JsonManager jm = new JsonManager(reqAll);
		String result = jm.getJsonResult();
		
		JSONObject jsonObject = new JSONObject(result);
    	
		fromJson = new ArrayList<Schedule>(jsonObject.length());
		Iterator<String> itr = jsonObject.keys();
		JSONObject jo = null;
		
		Schedule item = null;
		
		while(itr.hasNext()){
			String key = itr.next();
			jo = jsonObject.getJSONObject(key);
			item = new Schedule(jo.getInt("id"), jo.getInt("id_event"), jo.getString("title"),
					jo.getString("subtitle"), jo.getString("time_begin"), jo.getString("time_finish"), 
					jo.getString("at_day"), jo.getInt("know_time_finish"),jo.getInt("coffee_break"), 
					jo.getInt("data_sort"), jo.getInt("version"));
			fromJson.add(item);
		}
	}
	
	public void loadFromDb(){
		
		Cursor c = contentResolver.query(CProvider.CONTENT_URI_SCHEDULE, null, null, null, null);
		//Достаем данные
		if(c.getCount() != 0){
			if(c.moveToFirst()){
				fromDb = new ArrayList<Schedule>(c.getCount());
				String [] arrayNames = c.getColumnNames();
				Schedule tmp = null;
				do{
					tmp = new Schedule(
						c.getInt(c.getColumnIndex(arrayNames[0])), 
						c.getInt(c.getColumnIndex(arrayNames[1])), 
						c.getString(c.getColumnIndex(arrayNames[2])),
						c.getString(c.getColumnIndex(arrayNames[3])),
						c.getString(c.getColumnIndex(arrayNames[4])),
						c.getString(c.getColumnIndex(arrayNames[5])),
						c.getString(c.getColumnIndex(arrayNames[6])),
						c.getInt(c.getColumnIndex(arrayNames[7])),
						c.getInt(c.getColumnIndex(arrayNames[8])),
						c.getInt(c.getColumnIndex(arrayNames[9])),
						c.getInt(c.getColumnIndex(arrayNames[10])));
					fromDb.add(tmp);
				}while(c.moveToNext());
			}
		}else{
			fromDb = new ArrayList<Schedule>(0);
		}
		c.close();
	}
	
	public void startSync() throws InterruptedException, ExecutionException, JSONException{
		loadFromJson();
		loadFromDb();
		
		String[] hold = new String[fromJson.size()];
		
		if(fromJson.size() > 0){
			if(fromDb.size() > 0){//Если в базе есть чтото
				StringBuilder sb  = new StringBuilder();
				sb.append("?");
				hold[0] = updateVersion(fromJson.get(0));
				
				for(int i = 1; i < fromJson.size(); i++){
					hold[i] = updateVersion(fromJson.get(i));
					sb.append(",").append("?");
				}
				//Удаляем старые
				contentResolver.delete(CProvider.CONTENT_URI_SCHEDULE, 
						NAME_ID + " NOT IN (" + sb.toString() + ")", hold);
				//printDbToLog();				
			}else if(fromDb.size() == 0){//Если в базе пусто
				for(int i = 0; i < fromJson.size(); i++){
					Schedule tmp = fromJson.get(i);
					contentResolver.insert(CProvider.CONTENT_URI_SCHEDULE, getCv(tmp));
				}
			}
		}
	}
	
	public String updateVersion(Schedule item){
		int oldVers = getVersionFromDb(item.id);
		if(oldVers != -1){ //Запись существует
			if(oldVers < item.version){ //старая запись, обновляем
				String[] args = {item.id + ""};
				contentResolver.update(CProvider.CONTENT_URI_SCHEDULE, getCv(item), NAME_ID + "=?", args);
				return  item.id + "";
			}
		}else{//Запись нет, доабавляем
			contentResolver.insert(CProvider.CONTENT_URI_SCHEDULE, getCv(item));
		}
		return item.id + "";
	}

	private ContentValues getCv(Schedule item){
		ContentValues cv = new ContentValues();
		cv.put(NAME_ID, item.id);
		cv.put(NAME_ID_EVENT, item.id_event);
		cv.put(NAME_TITLE, item.title);
		cv.put(NAME_SUBTITLE, item.subtitle);
		cv.put(NAME_TIME_BEGIN, item.time_begin);
		cv.put(NAME_TIME_FINISH, item.time_finish);
		cv.put(NAME_AT_DAY, item.at_day);
		cv.put(NAME_KNOW_TIME_FINISH, item.know_time_finish);
		cv.put(NAME_COFFEE, item.coffee_break);
		cv.put(NAME_SORT, item.data_sort);
		cv.put(NAME_VERS, item.version);
		return cv;
	}
	
	public int getVersionFromDb(int id){
		String [] column = {"version"};
		String [] target = {"" + id};
		Cursor c = contentResolver.query(CProvider.CONTENT_URI_SCHEDULE, column, 
				NAME_ID + " = ?", target, null);
		if(c.moveToFirst()){
			int vers = c.getInt(0);
			c.close();
			return vers;
		}
		c.close();
		return -1;
	}
	
	public void printDbToLog(){
		Cursor c = contentResolver.query(CProvider.CONTENT_URI_SCHEDULE, null, null, null, null);
		if(c.getCount() != 0){
			if(c.moveToFirst()){
				String [] arrayNames = c.getColumnNames();
				do{
					Log.d(LOG_TAG, "item: " 
							+ "id = " + c.getInt(c.getColumnIndex(arrayNames[0])) + ","
							+ " id_event = " + c.getString(c.getColumnIndex(arrayNames[1])) + ","
							+ " title = " + c.getString(c.getColumnIndex(arrayNames[2])) + ","
							+ " subtitle = " + c.getString(c.getColumnIndex(arrayNames[3])) + ","
							+ " time_begin = " + c.getString(c.getColumnIndex(arrayNames[4])) + ","
							+ " time_finish = " + c.getString(c.getColumnIndex(arrayNames[5])) + ","
							+ " at_day = " + c.getString(c.getColumnIndex(arrayNames[6])) + ","
							+ " know_time_finish = " + c.getInt(c.getColumnIndex(arrayNames[7])) + ","
							+ " coffee_break = " + c.getInt(c.getColumnIndex(arrayNames[8])) + ","
							+ " sort = " + c.getInt(c.getColumnIndex(arrayNames[9])) + ","
							+ " version = " + c.getInt(c.getColumnIndex(arrayNames[10])));
				}while(c.moveToNext());
				Log.d(LOG_TAG, "--------------------------------------");
			}
		}
		c.close();
	}
}
