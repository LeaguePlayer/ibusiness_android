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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class SpeakersManager{
	//-----------------------------Для работы с БД---------------------------------
	//Константы имена полей
	public static final String NAME_ID = "_id";
	public static final String NAME_ID_EVENT = "id_event";
	public static final String NAME_SPEAKER = "name_speaker";
	public static final String NAME_ABOUT_SPEAKER = "about_speaker";
	public static final String NAME_TITLE_REPORT = "title_report";
	public static final String NAME_ABOUT_REPORT = "about_report";
	public static final String NAME_IMG = "img";
	public static final String NAME_SORT = "data_sort";
	public static final String NAME_VERS = "version";
	
	//Сущность событие
	public class Speaker{
		public int id;
		public int id_event;
		public String name_speaker;
		public String about_speaker;
		public String title_report;
		public String about_report;
		public String img;
		public int data_sort;
		public int version;
		
		public Speaker(int id, int id_event, String name_speaker, String about_speaker, 
				String title_report, String about_report, String img, int data_sort, int version){
			this.id = id;
			this.id_event = id_event;
			this.name_speaker = name_speaker;
			this.about_speaker = about_speaker;
			this.title_report = title_report;
			this.about_report = about_report;
			this.img = img;
			this.data_sort = data_sort;
			this.version = version;
		}
		
	}
	//-----------------------------Для работы с БД---------------------------------
	
	final String LOG_TAG = "myLogs";
	private ArrayList<Speaker> fromJson;
	private ArrayList<Speaker> fromDb;
	private Context context;
	
	private final float LDPI = 0.75f;
	private final float MDPI = 1f;
	private final float HDPI = 1.5f;
	private final float XHDPI = 2f;
	private final float d;
	
	private String reqAll = SplashActivity.DOMEN + "/speakers/getjson/all"; //API request
	public final static String URL_IMAGES = SplashActivity.DOMEN + "/uploads/speakers/";
	
	private final ContentResolver contentResolver;
	private final ImageLoader il;
	//Конструктор
	public SpeakersManager(Context context){
		this.context = context;
		this.contentResolver = context.getContentResolver();
		this.il = new ImageLoader(context);
		DisplayMetrics metrics = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
		this.d = metrics.density;
	}
	
	//Методы
	
	public void loadFromJson() throws InterruptedException, ExecutionException, JSONException{
		JsonManager jm = new JsonManager(reqAll);
		String result = jm.getJsonResult();
		
		JSONObject jsonObject = new JSONObject(result);

		fromJson = new ArrayList<Speaker>(jsonObject.length());
		Iterator<String> itr = jsonObject.keys();
		JSONObject jo = null;
		
		Speaker item = null;
		
		while(itr.hasNext()){
			String key = itr.next();
			jo = jsonObject.getJSONObject(key);
			item = new Speaker(jo.getInt("id"), jo.getInt("id_event"), jo.getString("name_speaker"),
					jo.getString("about_speaker"), jo.getString("title_report"), 
					jo.getString("about_report"), jo.getString("img"), jo.getInt("data_sort"), 
					jo.getInt("version"));
			fromJson.add(item);
		}
	}
	
	public void loadFromDb(){
		Cursor c = contentResolver.query(CProvider.CONTENT_URI_SPEAKERS, null, null, null, null);
		//Достаем данные
		if(c.getCount() != 0){
			if(c.moveToFirst()){
				fromDb = new ArrayList<Speaker>(c.getCount());
				String [] arrayNames = c.getColumnNames();
				Speaker tmp = null;
				do{
					tmp = new Speaker(
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
			fromDb = new ArrayList<Speaker>(0);
		}
		c.close();
	}
	
	public Cursor getCursor(String selection, String[] args, String orderby){
		return contentResolver.query(CProvider.CONTENT_URI_SPEAKERS, null, selection, args, orderby);
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
				//Delete not find image
				Cursor c = contentResolver.query(CProvider.CONTENT_URI_SPEAKERS, 
						new String[]{"img"}, NAME_ID + " NOT IN (" + sb.toString() + ")", hold, null);
				DeleteImages(c);
				c.close();
				//Удаляем старые
				contentResolver.delete(CProvider.CONTENT_URI_SPEAKERS, 
						NAME_ID + " NOT IN (" + sb.toString() + ")", hold);
				//printDbToLog();				
			}else if(fromDb.size() == 0){//Если в базе пусто
				for(int i = 0; i < fromJson.size(); i++){
					Speaker tmp = fromJson.get(i);
					contentResolver.insert(CProvider.CONTENT_URI_SPEAKERS, getCv(tmp));
					il.saveImageFromUrl(URL_IMAGES + getPrefixDensity(d) + tmp.img, tmp.img);
				}
			}
		}
	}
	
	private String getPrefixDensity(float d){
		if(d == LDPI){
			return "mdpi_";
		}
		if(d == MDPI){
			return "mdpi_";
		}
		if(d == HDPI){
			return "hdpi_";
		}
		if(d == XHDPI){
			return "xhdpi_";
		}
		return "mdpi_";
	}
	
	private void DeleteImages(Cursor c){
		if(c.getCount() != 0){
			if(c.moveToFirst()){
				do{
					il.deleteImage(c.getString(c.getColumnIndex("img")));
				}while(c.moveToNext());
			}
		}
	}
	
	public String updateVersion(Speaker item){
		int oldVers = getVersionFromDb(item.id);
		if(oldVers != -1){ //Запись существует
			if(oldVers < item.version){ //старая запись, обновляем
				//Update images<--
				Cursor c = contentResolver.query(CProvider.CONTENT_URI_SPEAKERS, 
						new String[]{"img"}, NAME_ID + "=?", new String[]{item.id + ""}, null);
				if(c.getCount() != 0){
					if(c.moveToFirst()){
						String oldImage = c.getString(c.getColumnIndex("img"));
						il.updateImage(oldImage, item.img, URL_IMAGES + getPrefixDensity(d) + item.img);
					}
				}
				c.close();
				//Update images-->
				String[] args = {item.id + ""};
				contentResolver.update(CProvider.CONTENT_URI_SPEAKERS, getCv(item), NAME_ID + "=?", args);
				return  item.id + "";
			}
		}else{//Запись нет, доабавляем
			contentResolver.insert(CProvider.CONTENT_URI_SPEAKERS, getCv(item));
			//save image
			il.saveImageFromUrl(URL_IMAGES + getPrefixDensity(d) + item.img, item.img);
		}
		return item.id + "";
	}

	private ContentValues getCv(Speaker item){
		ContentValues cv = new ContentValues();
		cv.put(NAME_ID, item.id);
		cv.put(NAME_ID_EVENT, item.id_event);
		cv.put(NAME_SPEAKER, item.name_speaker);
		cv.put(NAME_ABOUT_SPEAKER, item.about_speaker);
		cv.put(NAME_TITLE_REPORT, item.title_report);
		cv.put(NAME_ABOUT_REPORT, item.about_report);
		cv.put(NAME_IMG, item.img);
		cv.put(NAME_SORT, item.data_sort);
		cv.put(NAME_VERS, item.version);
		return cv;
	}
	
	public int getVersionFromDb(int id){
		String [] column = {"version"};
		String [] target = {"" + id};
		Cursor c = contentResolver.query(CProvider.CONTENT_URI_SPEAKERS, column, 
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
		Cursor c = contentResolver.query(CProvider.CONTENT_URI_SPEAKERS, null, null, null, null);
		if(c.getCount() != 0){
			if(c.moveToFirst()){
				String [] arrayNames = c.getColumnNames();
				do{
					Log.d(LOG_TAG, "item: " 
							+ "id = " + c.getInt(c.getColumnIndex(arrayNames[0])) + ","
							+ " id_event = " + c.getString(c.getColumnIndex(arrayNames[1])) + ","
							+ " name = " + c.getString(c.getColumnIndex(arrayNames[2])) + ","
							+ " about_speaker = " + c.getString(c.getColumnIndex(arrayNames[3])) + ","
							+ " title_report = " + c.getString(c.getColumnIndex(arrayNames[4])) + ","
							+ " about_report = " + c.getString(c.getColumnIndex(arrayNames[5])) + ","
							+ " img = " + c.getString(c.getColumnIndex(arrayNames[6])) + ","
							+ " sort = " + c.getString(c.getColumnIndex(arrayNames[7])) + ","
							+ " version = " + c.getInt(c.getColumnIndex(arrayNames[8])));
				}while(c.moveToNext());
				Log.d(LOG_TAG, "--------------------------------------");
			}
		}
		c.close();
	}
	
}
