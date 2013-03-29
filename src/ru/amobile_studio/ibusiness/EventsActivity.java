package ru.amobile_studio.ibusiness;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ru.amobile_studio.ibusiness.managers.EventManager;
import ru.amobile_studio.ibusiness.managers.EventManager.Event;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EventsActivity extends Activity implements OnClickListener {

	private int current_butt = 1;
	private Typeface tf;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events);
    
	    EventManager em = new EventManager(this);

	    tf = Typeface.createFromAsset(getAssets(),"fonts/PFDinDisplayPro-Bold.ttf");
	    
	    ArrayList<Event> dataAfterNow = em.getEvents("date('now') <= datetime(" + EventManager.NAME_DATE_S + ")" + 
	    		"AND " + EventManager.NAME_ID_TYPE + " <> 0 ", null, 
	    		"date(" +EventManager.NAME_DATE_S+ ") ASC");
	    
	    if(dataAfterNow != null){
	    	for(int i = 0; i < dataAfterNow.size(); i++){
	    		Log.d(SplashActivity.LOG_TAG, "after " + current_butt);
	    		View event = null;
	    		Event e = dataAfterNow.get(i);
	    		if(current_butt == 1){
	    			event = findViewById(R.id.ll_but1);
	    			RelativeLayout rl = (RelativeLayout) event.findViewById(R.id.event_active);
	    			rl.setOnClickListener(this);
	    			rl.setTag(e);
	    			setEventInfo(e, rl);
	    		}else if(current_butt == 2){
	    			event = findViewById(R.id.ll_but2);
	    			RelativeLayout rl = (RelativeLayout) event.findViewById(R.id.event_item1);
	    			rl.setOnClickListener(this);
	    			rl.setTag(e);
	    			setEventInfo(e, rl);
	    		}else if(current_butt == 3){
	    			event = findViewById(R.id.ll_but3);
	    			RelativeLayout rl = (RelativeLayout) event.findViewById(R.id.event_item2);
	    			rl.setOnClickListener(this);
	    			rl.setTag(e);
	    			setEventInfo(e, rl);
	    		}
	    		current_butt++;
	    	}
	    }
	    
	   
	    ArrayList<Event> dataBeforeNow = em.getEvents("date('now') > datetime(" + EventManager.NAME_DATE_S + ")" + 
	    		"AND " + EventManager.NAME_ID_TYPE + " <> 0 ", null, 
	    		"date(" +EventManager.NAME_DATE_S+ ") ASC");
	    
	    if(dataBeforeNow != null){
	    	for(int i = 0; i < dataBeforeNow.size(); i++){
	    		Log.d(SplashActivity.LOG_TAG, "befor " + current_butt);
	    		View event = null;
	    		Event e = dataBeforeNow.get(i);
	    		if(current_butt == 1){
	    			event = findViewById(R.id.ll_but1);
	    			RelativeLayout rl = (RelativeLayout) event.findViewById(R.id.event_active);
	    			rl.setOnClickListener(this);
	    			rl.setTag(e);
	    			setEventInfo(e, rl);
	    		}else if(current_butt == 2){
	    			event = findViewById(R.id.ll_but2);
	    			RelativeLayout rl = (RelativeLayout) event.findViewById(R.id.event_item1);
	    			rl.setOnClickListener(this);
	    			rl.setTag(e);
	    			setEventInfo(e, rl);
	    		}else if(current_butt == 3){
	    			event = findViewById(R.id.ll_but3);
	    			RelativeLayout rl = (RelativeLayout) event.findViewById(R.id.event_item2);
	    			rl.setOnClickListener(this);
	    			rl.setTag(e);
	    			setEventInfo(e, rl);
	    		}
	    		current_butt++;
	    	}
	    }
	    //рекомендованные
	    
	    //View rek =  lf.inflate(R.layout.rekomend, linLayout, true);
	    View rek =  findViewById(R.id.ll_but4);
	    RelativeLayout rl = (RelativeLayout) rek.findViewById(R.id.rek);
	    rl.setOnClickListener(this);
	}
	
	//Устанавливаем активное лого в первую кнопку
	private void setEventInfo(Event e, View event){
		TextView tv = null;
		if(current_butt == 1){
			switch(e.id_type){
				case 1:{
					ImageView iv = (ImageView) event.findViewById(R.id.logo_active);
					iv.setImageResource(R.drawable.imar_active);
					break;
				}
				case 2:{
					ImageView iv = (ImageView) event.findViewById(R.id.logo_active);
					iv.setImageResource(R.drawable.prod_active);
					break;
				}
				case 3:{
					ImageView iv = (ImageView) event.findViewById(R.id.logo_active);
					iv.setImageResource(R.drawable.mkad_active);
					break;
				}
			}
			tv = (TextView) event.findViewById(R.id.date_active);
		}else{
			switch(e.id_type){
				case 1:{
					if(current_butt == 2){
						ImageView iv = (ImageView) event.findViewById(R.id.logo);
						iv.setImageResource(R.drawable.imar);
						tv = (TextView) event.findViewById(R.id.date);
					}else if(current_butt == 3){
						ImageView iv = (ImageView) event.findViewById(R.id.logo2);
						iv.setImageResource(R.drawable.imar);
						tv = (TextView) event.findViewById(R.id.date2);
					}
					break;
				}
				case 2:{
					if(current_butt == 2){
						ImageView iv = (ImageView) event.findViewById(R.id.logo);
						iv.setImageResource(R.drawable.prod);
						tv = (TextView) event.findViewById(R.id.date);
					}else if(current_butt == 3){
						ImageView iv = (ImageView) event.findViewById(R.id.logo2);
						iv.setImageResource(R.drawable.prod);
						tv = (TextView) event.findViewById(R.id.date2);
					}
					break;
				}
				case 3:{
					if(current_butt == 2){
						ImageView iv = (ImageView) event.findViewById(R.id.logo);
						iv.setImageResource(R.drawable.mkad);
						tv = (TextView) event.findViewById(R.id.date);
					}else if(current_butt == 3){
						ImageView iv = (ImageView) event.findViewById(R.id.logo2);
						iv.setImageResource(R.drawable.mkad);
						tv = (TextView) event.findViewById(R.id.date2);
					}
					break;
				}
			}
		}
		SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s");
		SimpleDateFormat outFormat = new SimpleDateFormat("dd.MM.yy");
		try {
			String date = outFormat.format(inFormat.parse(e.date_start));
			tv.setTypeface(tf);
			tv.setText(date);
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		Event e = null;
		Intent intent = null;
		
		switch (v.getId()) {
			case R.id.event_active:
				e = (Event) v.getTag();
				intent = new Intent(this, EventTabs.class);
				intent.putExtra("event_id", e.id);
				startActivity(intent);
				break;
			case R.id.event_item1:
				e = (Event) v.getTag();
				intent = new Intent(this, EventTabs.class);
				intent.putExtra("event_id", e.id);
				startActivity(intent);
				break;
			case R.id.event_item2:
				e = (Event) v.getTag();
				intent = new Intent(this, EventTabs.class);
				intent.putExtra("event_id", e.id);
				startActivity(intent);
				break;
			case R.id.rek:
				intent = new Intent(this, RekListActivity.class);
				startActivity(intent);
				break;

		}
	}
}
