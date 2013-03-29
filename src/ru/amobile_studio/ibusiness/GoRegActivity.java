package ru.amobile_studio.ibusiness;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import ru.amobile_studio.ibusiness.managers.EventManager;
import ru.amobile_studio.ibusiness.managers.EventManager.Event;
import ru.amobile_studio.ibusiness.managers.JsonManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GoRegActivity extends Activity implements OnFocusChangeListener, OnClickListener {

	private static final String NAME_TEXT = "Как вас зовут?";
	private static final String PHONE_TEXT = "Номер телефона";
	private static final String EMAIL_TEXT = "email";
	
	public final Pattern EMAIL_ADDRESS_PATTERN = 
			Pattern.compile("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$");
	
	private static final String URL = SplashActivity.DOMEN + "/prices/getjson/";
	
	private final Thread t = new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Intent intent = getIntent();
			Bundle b = intent.getExtras();
			JsonManager jm = new JsonManager(URL + b.getInt("event_id"));
			String json = jm.getJsonResult();
			if(json.length() == 0){
				priceTextView.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						priceTextView.setText("");
					}
				});
				return;
			}	
			try {
				JSONObject jo = new JSONObject(json);
				Iterator<String> itr = jo.keys();
				
				while(itr.hasNext()){
					String key = itr.next();
					jo = jo.getJSONObject(key);
					final int price = jo.getInt("price");
					priceTextView.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							NumberFormat nf = NumberFormat.getInstance(Locale.FRENCH);
							if(price == 0){
								priceTextView.setText("Участие - бесплатно.");
								
							}else{
								priceTextView.setText(nf.format(price) + " руб.");
							}
						}
					});
					break;
				}	
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
	});
	
	private final Thread calendarThread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(Build.VERSION.SDK_INT < 14){
				addInCalendarLatestVersion();
			}else{
				addInCalendar();
			}
			
		}
	});
	
	private LinearLayout llmain;
	private TextView priceTextView;
	private EditText name;
	private EditText phone;
	private EditText email;
	private AlertDialog ad;
	
	private Event event;
	private int prevInput = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.event_reg);
		setContentView(R.layout.registration);
		ad = getDialog("Ошибка", "Заполнены не все поля");
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/PFDinDisplayPro-Bold.ttf");
		
		llmain = (LinearLayout) findViewById(R.id.regform);
		llmain.setOnTouchListener(new OnTouchListener() 
	    {
	        @Override
	        public boolean onTouch(View v, MotionEvent event) 
	        {
	        	name.clearFocus();
	        	phone.clearFocus();
	        	email.clearFocus();
	        	hideKeyBoard();
	            return false;
	        }
	    });
		
		TextView tv = (TextView) findViewById(R.id.t1);
		tv.setTypeface(tf);
		tv = (TextView) findViewById(R.id.event_title);
		tv.setTypeface(tf);
		tv = (TextView) findViewById(R.id.event_price);
		tv.setTypeface(tf);
		tv = (TextView) findViewById(R.id.infotext);
		tv.setTypeface(tf);
		tv = (TextView) findViewById(R.id.or);
		tv.setTypeface(tf);
		
		
		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		
		EventManager em = new EventManager(getApplicationContext());
    	event = em.getEvents(EventManager.NAME_ID + "=?", 
    			new String[]{"" + b.getInt("event_id")}, null).get(0);
    	
    	TextView event_title = (TextView) findViewById(R.id.event_title);
    	event_title.setText(event.title);
		
		name = (EditText) findViewById(R.id.name);
		name.setTypeface(tf);
		phone = (EditText) findViewById(R.id.phone);
		phone.setTypeface(tf);
		email = (EditText) findViewById(R.id.email);
		email.setTypeface(tf);
		
		Button go = (Button) findViewById(R.id.go);
		go.setTypeface(tf);
		Button cancel = (Button) findViewById(R.id.cancel);
		cancel.setTypeface(tf);
		
		priceTextView = (TextView) findViewById(R.id.event_price);
		priceTextView.setText("");
		t.start();
		name.clearFocus();
		phone.clearFocus();
		email.clearFocus();
		
		name.setOnFocusChangeListener(this);
		phone.setOnFocusChangeListener(this);
		email.setOnFocusChangeListener(this);	
		
		go.setOnClickListener(this);
		cancel.setOnClickListener(this);
		//calendarThread.start();
	}

	private void hideKeyBoard(){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
	}
	
	private AlertDialog getDialog(String title, String text) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(text)
			.setTitle(title)
	       .setCancelable(false)
	       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                //do things
	           }
	       });
		return builder.create();
	}
	
	private AlertDialog goodDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Заявка отправлена.")
			.setTitle("Спасибо!")
			.setCancelable(false)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	    GoRegActivity.this.finish();
	           }
	       });
		return builder.create();
	}
	
	private boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

	@Override
	public void onFocusChange(View v, boolean state) {
		EditText et = (EditText) v;
		if(state){
			if(et.getText().toString().equals(NAME_TEXT)  || et.getText().toString().equals(PHONE_TEXT)
					|| et.getText().toString().equals(EMAIL_TEXT) ){
				et.setText("");
			}
			if(prevInput == R.id.phone){
				if((name.getText().toString().length() > 0 && phone.getText().toString().length() > 0) && 
						!name.getText().toString().equals(NAME_TEXT) && !phone.getText().toString().equals(PHONE_TEXT)){
					hideKeyBoard();
					llmain.setFocusableInTouchMode(true);
					llmain.requestFocus();
				}
			}
			prevInput = v.getId();
		}else{
			if(et.getText().toString().equals("")){
				switch(et.getId()){
					case R.id.name:{
						et.setText(NAME_TEXT);
						break;
					}
					case R.id.phone:{
						et.setText(PHONE_TEXT);
						break;
					}
					case R.id.email:{
						et.setText(EMAIL_TEXT);
						break;
					}
				}
			}
		}	
	}
	

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.go:{
				Log.d(SplashActivity.LOG_TAG, "go");
				String nameInput = name.getText().toString();
				String phoneInput = phone.getText().toString();
				String emailInput = email.getText().toString();

				String android_id = Installation.id(this);
				//final String url = "http://test2.amobile-studio.ru" + "/mail/";
				final String url = SplashActivity.DOMEN + "/mail/";
				
				if(CheckInput(nameInput, NAME_TEXT) && CheckInput(phoneInput, PHONE_TEXT) && 
						CheckInput(emailInput, EMAIL_TEXT)){ 
					if(!checkEmail(emailInput)){
						AlertDialog alertEmail = getDialog("Ошибка ввода", "Некорректно введен email.");
						alertEmail.show();
						return;
					}
					//Заполнены все					
					final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
					nameValuePairs.add(new BasicNameValuePair("name", nameInput));
					nameValuePairs.add(new BasicNameValuePair("phone", phoneInput));
					nameValuePairs.add(new BasicNameValuePair("email", emailInput));
					nameValuePairs.add(new BasicNameValuePair("id_event", ""+event.id));
					nameValuePairs.add(new BasicNameValuePair("device", ""+android_id));
					
					Thread sender = new Thread(new Runnable() {
						
						@Override
						public void run() {
							boolean f = sendData(url, nameValuePairs);
							if(!f){
								final AlertDialog alert = getDialog("Ошибка подключения", 
										"Отсутствует подключение");
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										alert.show();
									}
								});
							}else{
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										//addInCalendar();
										final AlertDialog alert = goodDialog();
										alert.show();
									}
								});
							}
						}
					});
					sender.start();
					calendarThread.start();
					
				}else if(CheckInput(nameInput, NAME_TEXT) && CheckInput(phoneInput, PHONE_TEXT)){
					//Кроме мыла
					final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
					nameValuePairs.add(new BasicNameValuePair("name", nameInput));
					nameValuePairs.add(new BasicNameValuePair("phone", phoneInput));
					nameValuePairs.add(new BasicNameValuePair("email", ""));
					nameValuePairs.add(new BasicNameValuePair("id_event", ""+event.id));
					nameValuePairs.add(new BasicNameValuePair("device", ""+android_id));
					Thread sender = new Thread(new Runnable() {
						
						@Override
						public void run() {
							boolean f = sendData(url, nameValuePairs);
							if(!f){
								final AlertDialog alert = getDialog("Ошибка подключения", 
										"Отсутствует подключение");
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										alert.show();
									}
								});
							}else{
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										//addInCalendar();
										final AlertDialog alert = goodDialog();
										alert.show();
									}
								});
							}
						}
					});
					sender.start();
					calendarThread.start();
				}else if(CheckInput(nameInput, NAME_TEXT) && CheckInput(emailInput, EMAIL_TEXT)){ 
					//Кроме телефона
					if(!checkEmail(emailInput)){
						AlertDialog alertEmail = getDialog("Ошибка ввода", "Некорректно введен email.");
						alertEmail.show();
						return;
					}
					final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
					nameValuePairs.add(new BasicNameValuePair("name", nameInput));
					nameValuePairs.add(new BasicNameValuePair("phone", ""));
					nameValuePairs.add(new BasicNameValuePair("email", emailInput));
					nameValuePairs.add(new BasicNameValuePair("id_event", ""+event.id));
					nameValuePairs.add(new BasicNameValuePair("device", ""+android_id));
					Thread sender = new Thread(new Runnable() {
						
						@Override
						public void run() {
							boolean f = sendData(url, nameValuePairs);
							if(!f){
								
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										AlertDialog alert = getDialog("Ошибка подключения", 
												"Отсутствует подключение");
										alert.show();
									}
								});
							}else{
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										//addInCalendar();
										final AlertDialog alert = goodDialog();
										alert.show();
									}
								});
							}
						}
					});
					sender.start();
					calendarThread.start();
				}else{
					ad.show();
				}
				
				break;
			}
			case R.id.cancel:{
				Log.d(SplashActivity.LOG_TAG, "cancel");
				finish();
				break;
			}
		}
	}
	
	private boolean CheckInput(String text, String con){
		if(text.length() == 0)
			return false;
		if(text.equals(con))
			return false;
		return true;
	}
	
	private boolean sendData(String url, List<NameValuePair> data){
		
		StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(data, "UTF-8"));
			HttpResponse response = client.execute(httpPost);
			StatusLine statusLine = response.getStatusLine();
	        int statusCode = statusLine.getStatusCode();
	        
	        if (statusCode == 200) {
	            
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(
	        			response.getEntity().getContent(), "UTF-8"),8);
	            String line;
	            while((line = reader.readLine()) != null){
	            	builder.append(line).append("\n");;
	            }
	            Log.d(SplashActivity.LOG_TAG, "post " + builder.toString());  
	            return true;
	        }else {
	        	return false;
	        }
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return false;
	}

	//Working with calendar
	
	public static final String[] EVENT_PROJECTION = new String[] {
	    Calendars._ID,                           // 0
	    Calendars.ACCOUNT_NAME,                  // 1
	    Calendars.CALENDAR_DISPLAY_NAME,         // 2
	    Calendars.OWNER_ACCOUNT                  // 3
	};
	  
	// The indices for the projection array above.
	private static final int PROJECTION_ID_INDEX = 0;
	//private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
	//private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
	//private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
	
	private void addInCalendarLatestVersion(){

		Cursor cur = null;
		ContentResolver cr = getContentResolver();
		Uri uri = null;
		Uri uriEvents = null;
		Uri reminder = null;
		SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s");
		
		if (Build.VERSION.SDK_INT >= 8) {
			uri = Uri.parse("content://com.android.calendar/calendars");
			uriEvents = Uri.parse("content://com.android.calendar/events");
			reminder = Uri.parse("content://com.android.calendar/reminders");
		} else if (Build.VERSION.SDK_INT < 8){
			uri = Uri.parse("content://calendar/calendars");
			uriEvents = Uri.parse("content://calendar/events");
			reminder = Uri.parse("content://calendar/reminders");
		}
		
		String selection = "name" + " LIKE '%gmail.com'";

		cur = cr.query(uri, null, selection, null, null);
		Log.d(SplashActivity.LOG_TAG, "cur len " + cur.getColumnCount());
		if(cur.moveToFirst()){
		    int id = 0;
		    String name = "";

		    do{
				id = cur.getInt(cur.getColumnIndex(cur.getColumnName(0)));
			}while(cur.moveToNext());

			Log.d(SplashActivity.LOG_TAG, "id " + id);
			if(id != 0){
				long startMillis = 0; 
				long endMillis = 0;     
				boolean durition = false;
				Calendar beginTime = Calendar.getInstance();
				Calendar endTime = Calendar.getInstance();
				
				try {
					Date dbegin = inFormat.parse(event.date_start);
					beginTime.setTime(dbegin);
					startMillis = beginTime.getTimeInMillis();
					
					Date dEnd = inFormat.parse(event.date_finish);
					endTime.setTime(dEnd);
					if(dbegin.compareTo(dEnd) > 0){
						durition = true;
					}
					endMillis = endTime.getTimeInMillis();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//TimeZone timeZone = TimeZone.getDefault();
				
				ContentValues values = new ContentValues();
				values.put("dtstart", startMillis);
				if(durition){
					values.put("duration", "PT1H");
				}else{
					values.put("dtend", endMillis);
				}
				values.put("title", event.title);
				values.put("visibility", 3);
				values.put("hasAlarm", 1);
				values.put("description", event.short_desc);
				values.put("calendar_id", id);
				//values.put(Events.EVENT_TIMEZONE, timeZone.getID());
				Uri uriInsert = cr.insert(uriEvents, values);
				
				long eventID = Long.parseLong(uriInsert.getLastPathSegment());
				//set reminders
				if(eventID > 0){
					values = new ContentValues();
					values.put("minutes", 30);
					values.put("event_id", eventID);
					values.put("method", Reminders.METHOD_ALERT);
					cr.insert(reminder, values);
				}
			}
		}
		cur.close();
	}
	
	private void addInCalendar(){
		Cursor cur = null;
		ContentResolver cr = getContentResolver();
		Uri uri = null;
		Uri uriEvents = null;
		Uri reminder = null;
		SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s");
		
		uri = Uri.parse("content://com.android.calendar/calendars");
		uriEvents = Uri.parse("content://com.android.calendar/events");
		reminder = Uri.parse("content://com.android.calendar/reminders");
		
		String selection = Calendars.OWNER_ACCOUNT + " LIKE '%gmail.com'";

		cur = cr.query(uri, EVENT_PROJECTION, selection, null, null);
		if(cur.moveToFirst()){
		    long id = 0;
		    
			do{
				id = cur.getLong(PROJECTION_ID_INDEX);
				break;
				//String acc = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
				//String name = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
				//Log.d(SplashActivity.LOG_TAG, "id " + id + " acc "+acc+" name"+name);
			}while(cur.moveToNext());

			if(id != 0){
				long startMillis = 0; 
				long endMillis = 0;     
				boolean durition = false;
				Calendar beginTime = Calendar.getInstance();
				Calendar endTime = Calendar.getInstance();
				
				try {
					Date dbegin = inFormat.parse(event.date_start);
					beginTime.setTime(dbegin);
					startMillis = beginTime.getTimeInMillis();
					
					Date dEnd = inFormat.parse(event.date_finish);
					endTime.setTime(dEnd);
					if(dbegin.compareTo(dEnd) > 0){
						durition = true;
					}
					endMillis = endTime.getTimeInMillis();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				TimeZone timeZone = TimeZone.getDefault();
				
				ContentValues values = new ContentValues();
				values.put(Events.DTSTART, startMillis);
				if(durition){
					values.put(Events.DURATION, "PT1H");
				}else{
					values.put(Events.DTEND, endMillis);
				}
				values.put(Events.TITLE, event.title);
				values.put(Events.DESCRIPTION, event.short_desc);
				values.put(Events.CALENDAR_ID, id);
				values.put(Events.EVENT_TIMEZONE, timeZone.getID());
				Uri uriInsert = cr.insert(uriEvents, values);
				
				long eventID = Long.parseLong(uriInsert.getLastPathSegment());
				//set reminders
				if(eventID > 0){
					values = new ContentValues();
					values.put(Reminders.MINUTES, 30);
					values.put(Reminders.EVENT_ID, eventID);
					values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
					cr.insert(reminder, values);
				}
			}
		}
		cur.close();
	}
}
