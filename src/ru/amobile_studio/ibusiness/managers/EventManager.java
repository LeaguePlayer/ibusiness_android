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

public class EventManager {
	//-----------------------------Для работы с БД---------------------------------
	//Константы имена полей
	public static final String NAME_ID = "_id";
	public static final String NAME_ID_TYPE = "id_type";
	public static final String NAME_TITLE = "title";
	public static final String NAME_SHORTD = "short_desc";
	public static final String NAME_FULLD = "full_desc";
	public static final String NAME_DATE_S = "date_start";
	public static final String NAME_DATE_F = "date_finish";
	public static final String NAME_SHOW = "show";
	public static final String NAME_VERS = "version";
	
	//Сущность событие
	public class Event{
		public int id;
		public int id_type;
		public String title;
		public String short_desc;
		public String full_desc;
		public String date_start;
		public String date_finish;
		public int show;
		public int version;
		
		public Event(int id, int id_type, String title, String short_desc, String full_desc,
				String date_start, String date_finish, int show, int version){
			this.id = id;
			this.id_type = id_type;
			this.title = title;
			this.short_desc = short_desc;
			this.full_desc = full_desc;
			this.date_start = date_start;
			this.date_finish = date_finish;
			this.show = show;
			this.version = version;
		}
		
	}
	//-----------------------------Для работы с БД---------------------------------
	
	final String LOG_TAG = "myLogs";
	private ArrayList<Event> fromJson;
	private ArrayList<Event> fromDb;
	
	private final ContentResolver contentResolver;
	private String reqAll = SplashActivity.DOMEN + "/events/getjson/all"; //API request
	
	//Конструктор
	public EventManager(Context context){
		this.contentResolver = context.getContentResolver();
	}
	
	//Методы
	
	public ArrayList<Event> getDataJson(){
		return fromJson;
	}
	
	public ArrayList<Event> getDataDb(){
		return fromDb;
	}
	
	public void loadFromJson() throws InterruptedException, ExecutionException, JSONException{
		JsonManager jm = new JsonManager(reqAll);
		String result = jm.getJsonResult();
		
		JSONObject jsonObject = new JSONObject(result);
    	
		fromJson = new ArrayList<Event>(jsonObject.length());
		Iterator<String> itr = jsonObject.keys();
		JSONObject jo = null;
		
		Event item = null;
		
		while(itr.hasNext()){
			String key = itr.next();
			jo = jsonObject.getJSONObject(key);
			item = new Event(jo.getInt("id"), jo.getInt("id_type"), jo.getString("title"),
					jo.getString("short_desc"), jo.getString("full_desc"), jo.getString("date_start"), 
					jo.getString("date_finish"), jo.getInt("public"), jo.getInt("version"));
			fromJson.add(item);
		}
	}
	
	public void loadFromDb(){
		Cursor c = contentResolver.query(CProvider.CONTENT_URI_EVENTS, null, null, null, null);
		//Достаем данные
		if(c.getCount() != 0){
			if(c.moveToFirst()){
				fromDb = new ArrayList<Event>(c.getCount());
				String [] arrayNames = c.getColumnNames();
				Event tmp = null;
				do{
					tmp = new Event(
						c.getInt(c.getColumnIndex(arrayNames[0])), 
						c.getInt(c.getColumnIndex(arrayNames[1])), 
						c.getString(c.getColumnIndex(arrayNames[2])),
						c.getString(c.getColumnIndex(arrayNames[3])),
						c.getString(c.getColumnIndex(arrayNames[4])),
						c.getString(c.getColumnIndex(arrayNames[5])),
						c.getString(c.getColumnIndex(arrayNames[6])),
						c.getInt(c.getColumnIndex(arrayNames[7])),
						c.getInt(c.getColumnIndex(arrayNames[8])));
					fromDb.add(tmp);
				}while(c.moveToNext());
			}
		}else{
			fromDb = new ArrayList<Event>(0);
		}
		c.close();
	}
	
	public ArrayList<Event> getEvents(String selection, String[] args, String orderby){
		
		Cursor c = contentResolver.query(CProvider.CONTENT_URI_EVENTS, 
				null, selection, args, orderby);

		ArrayList<Event> result = null;
		
		if(c.getCount() != 0){
			if(c.moveToFirst()){
				result = new ArrayList<Event>(c.getCount());
				String [] arrayNames = c.getColumnNames();
				Event tmp = null;
				do{
					tmp = new Event(
						c.getInt(c.getColumnIndex(arrayNames[0])), 
						c.getInt(c.getColumnIndex(arrayNames[1])), 
						c.getString(c.getColumnIndex(arrayNames[2])),
						c.getString(c.getColumnIndex(arrayNames[3])),
						c.getString(c.getColumnIndex(arrayNames[4])),
						c.getString(c.getColumnIndex(arrayNames[5])),
						c.getString(c.getColumnIndex(arrayNames[6])),
						c.getInt(c.getColumnIndex(arrayNames[7])),
						c.getInt(c.getColumnIndex(arrayNames[8])));
					result.add(tmp);
				}while(c.moveToNext());
			}
		}
		c.close();
		return result;
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
				contentResolver.delete(CProvider.CONTENT_URI_EVENTS, 
						NAME_ID + " NOT IN (" + sb.toString() + ")", hold);				
			}else if(fromDb.size() == 0){//Если в базе пусто
				for(int i = 0; i < fromJson.size(); i++){
					Event tmp = fromJson.get(i);
					contentResolver.insert(CProvider.CONTENT_URI_EVENTS, getCv(tmp));
				}
			}
		}
	}
	
	public String updateVersion(Event item){
		int oldVers = getVersionFromDb(item.id);
		if(oldVers != -1){ //Запись существует
			if(oldVers < item.version){ //старая запись, обновляем
				String[] args = {item.id + ""};
				contentResolver.update(CProvider.CONTENT_URI_EVENTS, getCv(item), NAME_ID + "=?", args);
				return  item.id + "";
			}
		}else{//Запись нет, доабавляем
			contentResolver.insert(CProvider.CONTENT_URI_EVENTS, getCv(item));
		}
		return item.id + "";
	}

	private ContentValues getCv(Event item){
		ContentValues cv = new ContentValues();
		cv.put(NAME_ID, item.id);
		cv.put(NAME_ID_TYPE, item.id_type);
		cv.put(NAME_TITLE, item.title);
		cv.put(NAME_SHORTD, item.short_desc);
		cv.put(NAME_FULLD, item.full_desc);
		cv.put(NAME_DATE_S, item.date_start);
		cv.put(NAME_DATE_F, item.date_finish);
		cv.put(NAME_SHOW, item.show);
		cv.put(NAME_VERS, item.version);
		return cv;
	}
	
	public int getVersionFromDb(int id){
		String [] column = {"version"};
		String [] target = {"" + id};
		Cursor c = contentResolver.query(CProvider.CONTENT_URI_EVENTS, column, 
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
		Cursor c = contentResolver.query(CProvider.CONTENT_URI_EVENTS, null, null, null, null);
		if(c.getCount() != 0){
			if(c.moveToFirst()){
				String [] arrayNames = c.getColumnNames();
				do{
					Log.d(LOG_TAG, "item: " 
							+ "id = " + c.getInt(c.getColumnIndex(arrayNames[0])) + ","
							+ " id_type = " + c.getString(c.getColumnIndex(arrayNames[1])) + ","
							+ " title = " + c.getString(c.getColumnIndex(arrayNames[2])) + ","
							//+ " short_d = " + c.getString(c.getColumnIndex(arrayNames[3])) + ","
							//+ " full_d = " + c.getString(c.getColumnIndex(arrayNames[4])) + ","
							+ " date_s = " + c.getString(c.getColumnIndex(arrayNames[5])) + ","
							+ " date_f = " + c.getString(c.getColumnIndex(arrayNames[6])) + ","
							+ " show = " + c.getString(c.getColumnIndex(arrayNames[7])) + ","
							+ " version = " + c.getInt(c.getColumnIndex(arrayNames[8])));
				}while(c.moveToNext());
				Log.d(LOG_TAG, "--------------------------------------");
			}
		}
		c.close();
	}

}
