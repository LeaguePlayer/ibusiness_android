package ru.amobile_studio.ibusiness;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Review extends Activity implements OnClickListener {
	
	private int [] items = {R.id.r_tx1, R.id.r_tx2, R.id.r_tx3, R.id.r_tx4, R.id.r_tx5};
	private int [] lls = {R.id.r_bt1, R.id.r_bt2, R.id.r_bt3, R.id.r_bt4, R.id.r_bt5};
	private int current = -1;
	private static final String URL = SplashActivity.DOMEN + "/reviews/create";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.review);
		
//		SharedPreferences sPref = getPreferences(MODE_PRIVATE);
//	    Boolean vote = sPref.getBoolean("vote", false);
//	    if(vote){
//	    	final AlertDialog alert = goodDialog("", "Вы уже голосовали.");
//			alert.show();
//	    }
		
		Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/PFDinDisplayPro-Bold.ttf");
		String [] r_array = getResources().getStringArray(R.array.reviews_marks);
		
		Button submit = (Button) findViewById(R.id.r_submit);
		submit.setOnClickListener(this);
		
		//title
		TextView tv = (TextView) findViewById(R.id.reviewTitle);
		LinearLayout ll;
		tv.setTypeface(tf);
		
		for(int i = 0; i < items.length; i++){
			tv = (TextView) findViewById(items[i]);
			tv.setText(r_array[i]);
			tv.setTypeface(tf);
			
			ll = (LinearLayout) findViewById(lls[i]);
			ll.setOnClickListener(this);
			ll.setTag(i);
		}
	}
	
	private void backBackground(){
		LinearLayout ll;
		for(int i = 0; i < lls.length; i++){
			ll = (LinearLayout) findViewById(lls[i]);
			ll.setBackgroundResource(R.drawable.event_button_selector);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.r_bt1: {
				backBackground();
				v.setBackgroundResource(R.drawable.event_button_click);
				current = (Integer) v.getTag();
				break;
			}
			case R.id.r_bt2: {
				backBackground();
				v.setBackgroundResource(R.drawable.event_button_click);
				current = (Integer) v.getTag();
				break;
			}
			case R.id.r_bt3: {
				backBackground();
				v.setBackgroundResource(R.drawable.event_button_click);
				current = (Integer) v.getTag();
				break;
			}
			case R.id.r_bt4: {
				backBackground();
				v.setBackgroundResource(R.drawable.event_button_click);
				current = (Integer) v.getTag();
				break;
			}
			case R.id.r_bt5: {
				backBackground();
				v.setBackgroundResource(R.drawable.event_button_click);
				current = (Integer) v.getTag();
				break;
			}
			case R.id.r_submit: {
				if(current >= 0){
					//Проверяем голосовал или нет
					SharedPreferences sPref = getPreferences(MODE_PRIVATE);
				    Boolean vote = sPref.getBoolean("vote", false);
				    if(vote){
				    	final AlertDialog alert = goodDialog("", "Вы уже голосовали.");
						alert.show();
				    }
				    
					final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
					Intent intent = getIntent();
					Bundle b = intent.getExtras();
					
					int event_id = b.getInt("event_id");
					
					nameValuePairs.add(new BasicNameValuePair("Reviews[raiting]", current + ""));
					nameValuePairs.add(new BasicNameValuePair("Reviews[type_device]", "1"));
					nameValuePairs.add(new BasicNameValuePair("Reviews[id_event]", event_id + ""));
					Log.d(SplashActivity.LOG_TAG, nameValuePairs.toString() + "");
					
					Thread sender = new Thread(new Runnable() {
						
						@Override
						public void run() {
							Boolean f = sendData(URL, nameValuePairs);
							if(f){
								
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										final AlertDialog alert = goodDialog("Спасибо!", "Ваш голос для нас очень важен!");
										saveVoteFlag();
										alert.show();
									}
								});
							}else{
								
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										final AlertDialog alert = getDialog("Ошибка.", 	"Отсутствует подключение");
										alert.show();
									}
								});
							}
							Log.d(SplashActivity.LOG_TAG, f + "");
						}
					});
					sender.start();
				}else{
					final AlertDialog alert = getDialog("", "Необходимо выбрать оценку!");
					alert.show();
				}
				
				break;
			}
		}
		
		
		
		Log.d(SplashActivity.LOG_TAG, current + "");
	}
	
	private AlertDialog goodDialog(String title, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg)
			.setTitle(title)
			.setCancelable(false)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	    Review.this.finish();
	           }
	       });
		return builder.create();
	}
	
	private void saveVoteFlag(){
		SharedPreferences sPref = getPreferences(MODE_PRIVATE);
	    Editor ed = sPref.edit();
	    ed.putBoolean("vote", true);
	    ed.commit();
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
}
