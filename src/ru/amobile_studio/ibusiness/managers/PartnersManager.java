package ru.amobile_studio.ibusiness.managers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import ru.amobile_studio.ibusiness.SplashActivity;
import ru.amobile_studio.ibusiness.providers.CProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class PartnersManager {
	//-----------------------------Для работы с БД---------------------------------
	//Константы имена полей
	private final String NAME_ID = "_id";
	private final String NAME_IMG = "img";
	private final String NAME_VERS = "version";
	
	private final float LDPI = 0.75f;
	private final float MDPI = 1f;
	private final float HDPI = 1.5f;
	private final float XHDPI = 2f;
	
	public class Partner{
		private String img;
		private int version;
		private int id;
		
		public Partner(int _id, String _img, int _version){
			this.id = _id;
			this.setImg(_img);
			this.version = _version;
		}

		public String getImg() {
			return img;
		}

		public void setImg(String img) {
			this.img = img;
		}
	}
	//-----------------------------Для работы с БД---------------------------------
	
	//Место на сервере
	final String LOG_TAG = "myLogs";
	public final static String URL_IMAGES = SplashActivity.DOMEN + "/uploads/partners/";
	
	private ArrayList<Partner> fromJson;
	private ArrayList<Partner> fromDb;

	private String reqAll = SplashActivity.DOMEN + "/partners/getjson/all";
	private final ContentResolver contentResolver;
	private final ImageLoader il;
	private final float d;
	
	//Конструктор
	public PartnersManager(Context context){
		this.contentResolver = context.getContentResolver();
		this.il = new ImageLoader(context);
		DisplayMetrics metrics = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
		this.d = metrics.density;
		//Log.d(SplashActivity.LOG_TAG, getPrefixDensity(d));
	}
	
	public ArrayList<Partner> getDataJson(){
		return fromJson;
	}
	
	public ArrayList<Partner> getDataDb(){
		return fromDb;
	}
	
	public void loadFromJson() throws InterruptedException, ExecutionException, JSONException{
		JsonManager jm = new JsonManager(reqAll);
		String result = jm.getJsonResult();
		
		JSONObject jsonObject = new JSONObject(result);
		
		fromJson = new ArrayList<Partner>(jsonObject.length());
		Iterator<String> itr = jsonObject.keys();
		JSONObject jo = null;
		
		Partner item = null;
		
		while(itr.hasNext()){
			String key = itr.next();
			jo = jsonObject.getJSONObject(key);
			item = new Partner(jo.getInt("id"), jo.getString("img"), jo.getInt("version"));
			fromJson.add(item);
		}
	}
	
	public void loadFromDb(){
		
		Cursor c = contentResolver.query(CProvider.CONTENT_URI_PARTNERS, null, null, null, null);
		//Достаем данные
		if(c.getCount() != 0){
			if(c.moveToFirst()){
				fromDb = new ArrayList<Partner>(c.getCount());
				String [] arrayNames = c.getColumnNames();
				Partner tmp = null;
				do{
					tmp = new Partner(
							c.getInt(c.getColumnIndex(arrayNames[0])), 
							c.getString(c.getColumnIndex(arrayNames[1])), 
							c.getInt(c.getColumnIndex(arrayNames[2])));
					fromDb.add(tmp);
				}while(c.moveToNext());
			}
		}else{
			fromDb = new ArrayList<Partner>(0);
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
				//Delete not find image
				Cursor c = contentResolver.query(CProvider.CONTENT_URI_PARTNERS, 
						new String[]{"img"}, NAME_ID + " NOT IN (" + sb.toString() + ")", hold, null);
				DeleteImages(c);
				c.close();
				//Удаляем старые
				contentResolver.delete(CProvider.CONTENT_URI_PARTNERS, 
						NAME_ID + " NOT IN (" + sb.toString() + ")", hold);
				//printDbToLog();				
			}else if(fromDb.size() == 0){//Если в базе пусто
				for(int i = 0; i < fromJson.size(); i++){
					Partner tmp = fromJson.get(i);
					contentResolver.insert(CProvider.CONTENT_URI_PARTNERS, getCv(tmp));
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
	
	public String updateVersion(Partner item){
		int oldVers = getVersionFromDb(item.id);
		if(oldVers != -1){ //Запись существует
			if(oldVers < item.version){ //старая запись, обновляем
				//Update images<--
				Cursor c = contentResolver.query(CProvider.CONTENT_URI_PARTNERS, 
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
				contentResolver.update(CProvider.CONTENT_URI_PARTNERS, getCv(item), NAME_ID + "=?", args);
				return  item.id + "";
			}
		}else{//Запись нет, доабавляем
			contentResolver.insert(CProvider.CONTENT_URI_PARTNERS, getCv(item));
			//save image
			il.saveImageFromUrl(URL_IMAGES + getPrefixDensity(d) + item.img, item.img);
		}
		return item.id + "";
	}

	public int getVersionFromDb(int id){
		String [] column = {"version"};
		String [] target = {"" + id};
		Cursor c = contentResolver.query(CProvider.CONTENT_URI_PARTNERS, column, 
				NAME_ID + " = ?", target, null);
		if(c.moveToFirst()){
			int vers = c.getInt(0);
			c.close();
			return vers;
		}
		c.close();
		return -1;
	}
	
	private ContentValues getCv(Partner item){
		ContentValues cv = new ContentValues();
		cv.put(NAME_ID, item.id);
		cv.put(NAME_IMG, item.img);
		cv.put(NAME_VERS, item.version);
		return cv;
	}
	
	public Bitmap getBunner(){
		Cursor c = contentResolver.query(CProvider.CONTENT_URI_PARTNERS, 
				new String[]{"img"}, null, null, NAME_ID);
		if(c.getCount() != 0){
			if(c.moveToFirst()){
				Bitmap b = null;
				Random random = new Random();
				int d = random.nextInt(c.getCount());
				int count = 0;
				do{
					if(d == count){
						b = il.getImage(c.getString(c.getColumnIndex("img")));
					}
					count++;
				}while(c.moveToNext());
				
				c.close();
				return b;
			}
		}
		c.close();
		return null;
	}
	
	public void printDbToLog(){
		Cursor c = contentResolver.query(CProvider.CONTENT_URI_PARTNERS, null, null, null, null);
		if(c.getCount() != 0){
			if(c.moveToFirst()){
				String [] arrayNames = c.getColumnNames();
				do{
					Log.d(SplashActivity.LOG_TAG, "item: id = " + c.getInt(c.getColumnIndex(arrayNames[0])) + ","
							+ " img = " + c.getString(c.getColumnIndex(arrayNames[1])) + ","
							+ " version = " + c.getInt(c.getColumnIndex(arrayNames[2])));
				}while(c.moveToNext());
				Log.d(SplashActivity.LOG_TAG, "--------------------------------------");
			}
		}
		c.close();
	}

	public boolean isEmpty() {
		Cursor c = contentResolver.query(CProvider.CONTENT_URI_PARTNERS, null, null, null, null);
		if(c.getCount() > 0){
			c.close();
			return false;
		}
		c.close();
		return true;
	}

}
