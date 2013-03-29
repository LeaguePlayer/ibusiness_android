package ru.amobile_studio.ibusiness;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.amobile_studio.ibusiness.managers.EventManager;
import ru.amobile_studio.ibusiness.managers.EventManager.Event;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RekListActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rek_list);
		
		EventManager em = new EventManager(this);
		
		ArrayList<Event> data = em.getEvents(EventManager.NAME_ID_TYPE + " = 0 ", 
				null, "date(" +EventManager.NAME_DATE_S+ ") ASC");
		setListAdapter(new NewsAdapter(getApplicationContext(), R.layout.news_item, data));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		Event e = (Event) v.getTag();
		Intent intent = new Intent(this, EventTabs.class);
		intent.putExtra("event_id", e.id);
		startActivity(intent);
	}
	
	private class NewsAdapter extends ArrayAdapter<Event>{

		private LayoutInflater lInflater;
		private ArrayList<Event> objects;
		
		public NewsAdapter(Context context, int textViewResourceId,	ArrayList<Event> objects) {
			super(context, textViewResourceId, objects);
			this.objects = objects;
			this.lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view = convertView;
		    if (view == null) {
		      view = lInflater.inflate(R.layout.rek_item, parent, false);
		    }
		    
		    Event n = objects.get(position);
		    Log.d(SplashActivity.LOG_TAG, "start " + n.date_start + " end " + n.date_finish);
		    
		    SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s");
		    SimpleDateFormat timeFormat = new SimpleDateFormat("H:mm");
    		SimpleDateFormat outDateFormat = new SimpleDateFormat("EEE. dd MMMM");
		    
		    String inText = "";
		    
		    try {
				Date start = inFormat.parse(n.date_start);
				Date finish = inFormat.parse(n.date_finish);
				if(equalsDate(start, finish)){
					inText = outDateFormat.format(start) + " " +
							timeFormat.format(start) + " - " + timeFormat.format(finish);
				}else if(start.compareTo(finish) < 0){
					inText = outDateFormat.format(start) + " - " + outDateFormat.format(finish) +
							" " + timeFormat.format(finish);
				}else if(start.compareTo(finish) > 0){
					inText = outDateFormat.format(start);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		    Typeface tf = Typeface.createFromAsset(getAssets(),
	    			"fonts/PFDinDisplayPro-Bold.ttf");
		    
		    TextView tv = (TextView) view.findViewById(R.id.date);
		    tv.setTypeface(tf);
		    tv.setText(inText);
		    tv = (TextView) view.findViewById(R.id.rek_title);
		    tv.setTypeface(tf);
		    tv.setText(n.title);
		    tv = (TextView) view.findViewById(R.id.rek_desc);
		    if(n.short_desc.length() == 0){
		    	tv.setVisibility(View.GONE);
		    }else{
		    	tv.setVisibility(View.VISIBLE);
		    }
		    tv.setTypeface(tf);
		    tv.setText(n.short_desc);
		    view.setTag(n);
		    
		    return view;
		}
		
		private boolean equalsDate(Date d1, Date d2){
			SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd");
			String s1 = outFormat.format(d1);
			String s2 = outFormat.format(d2);
			
			return s1.equals(s2);
		}
	}
}
