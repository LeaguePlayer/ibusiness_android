package ru.amobile_studio.ibusiness;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.amobile_studio.ibusiness.managers.JsonManager;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class NewsActivity extends ListActivity {
	
	private PullToRefreshListView mPullRefreshListView;
	private NewsAdapter mAdapter;
	ListView actualListView;
	ArrayList<News> listArray;
	ProgressDialog pg;
	
	private final Thread t = new Thread(new Runnable() {
		
		@Override
		public void run() {
		
			listArray = loadJson();
			actualListView = mPullRefreshListView.getRefreshableView();
			actualListView.post(new Runnable() {
				
				@Override
				public void run() {
					mAdapter = new NewsAdapter(getApplicationContext(), R.layout.news_item, listArray);
					actualListView.setAdapter(mAdapter);
					
					if(pg!= null && pg.isShowing())
						pg.dismiss();
				}
			});
		}
	});
	
	private final static String TWEET_URL = "https://api.twitter.com/1/statuses/user_timeline.json?include_entities=true&include_rts=true&screen_name=gmk_ru";
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		News n = (News) v.getTag();
		
		Intent intent = new Intent(this, NewsItemActivity.class);
		intent.putExtra("url", n.url);
		
		startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.news_list);
		
		pg = ProgressDialog.show(this, null, getString(R.string.progress_text), true);
		
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		t.start();
		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// Do work to refresh the list here.
				new GetDataTask().execute();
			}
		});	
	}
	
	private class News{
		String title;
		String url;
		
		public News(String title, String url){
			this.title = title;
			this.url = url;
		}
	}
	
	private class GetDataTask extends AsyncTask<Void, Void, ArrayList<News>> {

		@Override
		protected ArrayList<News> doInBackground(Void... params) {
			// Simulates a background job.
			listArray = loadJson();
			Log.d(SplashActivity.LOG_TAG, "list - " + listArray.size());
			return listArray;
		}

		@Override
		protected void onPostExecute(ArrayList<News> result) {
			mAdapter = new NewsAdapter(getApplicationContext(), R.layout.news_item, result);
			actualListView.setAdapter(mAdapter);
			
			mAdapter.notifyDataSetChanged();
			
			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}
	
	private class NewsAdapter extends ArrayAdapter<News>{

		private LayoutInflater lInflater;
		private ArrayList<News> objects;
		
		public NewsAdapter(Context context, int textViewResourceId,	ArrayList<News> objects) {
			super(context, textViewResourceId, objects);
			this.objects = objects;
			this.lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view = convertView;
		    if (view == null) {
		      view = lInflater.inflate(R.layout.news_item, parent, false);
		    }
		    
		    News n = objects.get(position);
		    TextView tv = (TextView) view.findViewById(android.R.id.text1);
		    tv.setText(n.title);
		    view.setTag(n);
		    
		    return view;
		}
	}
	
	private ArrayList<News> loadJson(){
		JsonManager jm = new JsonManager(TWEET_URL);
		JSONArray jsonArray = null;
		ArrayList<News> res = null;
		try {
			String r = jm.getJsonResult();
			if(r.equals("")){
				News n = new News("��� �����������", "");
				ArrayList<News> one = new ArrayList<News>(1);
				one.add(n);
				return one;
			}
			jsonArray = new JSONArray(r);
			res = new ArrayList<News>(jsonArray.length());
			
			for(int i = 0; i< jsonArray.length(); i++){
				JSONObject tmp = jsonArray.getJSONObject(i);
				
				JSONObject entites_url = tmp.getJSONObject("entities");
				JSONArray urls = entites_url.getJSONArray("urls");
				if(urls.length() > 0){
					entites_url = urls.getJSONObject(0);
					News n = new News(tmp.getString("text"), entites_url.getString("expanded_url"));
					res.add(n);
				}
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return res;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
	        FrontActivity parentActivity;
	        parentActivity = (FrontActivity) this.getParent();
	        parentActivity.switchTab(0);
	        return true;
	    }
		return super.onKeyDown(keyCode, event);
	}
	
}
