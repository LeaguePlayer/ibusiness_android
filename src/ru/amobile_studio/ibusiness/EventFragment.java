package ru.amobile_studio.ibusiness;

import ru.amobile_studio.ibusiness.managers.EventManager;
import ru.amobile_studio.ibusiness.managers.EventManager.Event;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class EventFragment extends Fragment {

    public static EventFragment newInstance(int position) {
    	EventFragment fragment = new EventFragment();       
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	Bundle args = getArguments();
 	
    	EventManager em = new EventManager(getActivity());
    	Event event = em.getEvents(EventManager.NAME_ID + "=?", 
    			new String[]{"" + args.getInt("event_id")}, null).get(0);
    	
    	Log.d("events", args.getInt("event_id") + "");
    	
    	View v = inflater.inflate(R.layout.web, null);
		WebView web = (WebView) v.findViewById(R.id.webview);
		web.loadDataWithBaseURL(null, "<html><body>" + event.full_desc + "</body></html>", 
				"text/html", "UTF-8", null);
    	
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putString(KEY_CONTENT, mContent);
    }
}
