package ru.amobile_studio.ibusiness;

import static ru.amobile_studio.ibusiness.gsm.CommonUtilities.SENDER_ID;
import static ru.amobile_studio.ibusiness.gsm.CommonUtilities.SERVER_URL;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import com.google.android.gcm.GCMRegistrar;

import ru.amobile_studio.ibusiness.gsm.ServerUtilities;
import ru.amobile_studio.ibusiness.managers.EventManager;
import ru.amobile_studio.ibusiness.managers.PartnersManager;
import ru.amobile_studio.ibusiness.managers.ScheduleManager;
import ru.amobile_studio.ibusiness.managers.SpeakersManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SplashActivity extends Activity {

	public static final String DOMEN = "http://test.amobile-studio.ru";
	public static final String LOG_TAG = "myLogs";
	
	public static final int CONN_WIFI = ConnectivityManager.TYPE_WIFI;
	public static final int CONN_ANY = ConnectivityManager.TYPE_MOBILE;
	
	public static boolean connect_flag = false;
	
	final int ASYNC_OK = 1;
	private ImageView iv;

	private AsyncTask<Void, Void, Void> mRegisterTask;

	public Handler h;
	public BroadcastReceiver br = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d(LOG_TAG, "-------------GCM------------");
        
        //add for GCM android
        checkNotNull(SERVER_URL, "SERVER_URL");
        checkNotNull(SENDER_ID, "SENDER_ID");
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        
        final String regId = GCMRegistrar.getRegistrationId(this);
        Log.d(LOG_TAG, "reg 1 - " + regId);
        if (regId.equals("")) {
        	GCMRegistrar.register(this, SENDER_ID);
        }else{
        	if (GCMRegistrar.isRegisteredOnServer(this)) {
        		
        	}else{
        		final Context context = this;
            	mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        boolean registered = ServerUtilities.register(context, regId);
                        if (!registered) {
                            GCMRegistrar.unregister(context);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }
                };
                mRegisterTask.execute(null, null, null);
        	}
        }
        //-----
        
        setContentView(R.layout.main);
        
        final AlphaAnimation down = new AlphaAnimation(1.0f, 0.0f);
        down.setDuration(500);
        final AlphaAnimation up = new AlphaAnimation(0.0f, 1.0f);
        up.setDuration(500);
        
        iv = (ImageView) findViewById(R.id.banner);

        final Runnable downAnimation = new Runnable() {
    		@Override
    		public void run() {
    			// TODO Auto-generated method stub
    			iv.startAnimation(down);
    		}
    	};
        
        //������� ������� ��� �������� ���������� � ����
        br = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				ConnectivityManager conn =  (ConnectivityManager)
					context.getSystemService(Context.CONNECTIVITY_SERVICE);
				
				NetworkInfo networkInfo = conn.getActiveNetworkInfo();
				
				//Net detected
				if (networkInfo != null && 
					 (networkInfo.getType() == CONN_WIFI || networkInfo.getType() == CONN_ANY)) {
					
					Thread t = new Thread(new Runnable() {
						public void run() {
							try {
								syncBanners();
								Thread.sleep(2000);
								iv.post(downAnimation);
								Thread.sleep(500);
								iv.post(new Runnable() {
									@Override
									public void run() {
										showBanner();
										ProgressBar pb = (ProgressBar) findViewById(R.id.loader);
										pb.setVisibility(View.VISIBLE);
									}
								});
								loadData();
								h.sendEmptyMessage(ASYNC_OK);
							} catch (InterruptedException e) {
								e.printStackTrace();
							} catch (ExecutionException e) {
								e.printStackTrace();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
					t.start();
				} else { //Net undetected
					PartnersManager pm = new PartnersManager(getApplicationContext());
					if(pm.isEmpty()){
						Toast.makeText(getApplicationContext(), getString(R.string.need_net), Toast.LENGTH_LONG).show();
					}else{
						Thread tWait = new Thread(new Runnable() {
							
							@Override
							public void run() {
								try {
									Thread.sleep(3000);
									iv.post(downAnimation);
									Thread.sleep(500);
									iv.post(new Runnable() {
										
										@Override
										public void run() {
											// TODO Auto-generated method stub
											showBanner();
										}
									});
									Thread.sleep(3000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								h.sendEmptyMessage(ASYNC_OK);
							}
						});
						tWait.start();
					}
				}
			}
		};
		
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(br, filter);
        
        h = new Handler(){
        	@Override
        	public void handleMessage(Message msg) {
        		if(msg.what == ASYNC_OK){
        			Log.d(LOG_TAG, "Async ok");
        			AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        	        anim.setDuration(500);
        			iv.startAnimation(anim);
        			Intent intent = new Intent(getApplicationContext(), FrontActivity.class);
        			startActivity(intent);
        		}
        	}
        }; 
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    protected void onRestart() {
    	// TODO Auto-generated method stub
    	super.onRestart();
    	finish();
    }

   @Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (br != null){
			this.unregisterReceiver(br);
		}
		
	}
   
   @Override
	protected void onPause() {
		super.onPause();
		
		if (br != null){
			Log.d(LOG_TAG, "unreg receiver");
			this.unregisterReceiver(br);
			br = null;
		}
	}
   

   private boolean syncBanners() throws InterruptedException, ExecutionException, JSONException{
	   PartnersManager pm = new PartnersManager(this);
	   pm.startSync();
	   Log.d(LOG_TAG, "empty" + pm.isEmpty());
	   return pm.isEmpty() ? false : true;
   }
   
   private void checkNotNull(Object reference, String name) {
       if (reference == null) {
           throw new NullPointerException();
       }
   }
    
   public void showBanner(){
	   PartnersManager pm = new PartnersManager(this);
	   final Bitmap banner = pm.getBunner();
	   iv = (ImageView) findViewById(R.id.banner);
	   iv.setImageBitmap(banner);
   }
   
   public void loadData() throws InterruptedException, ExecutionException, JSONException{
    	    	    	
		EventManager em = new EventManager(this);
		em.startSync();
		Log.d(LOG_TAG, "events sync");

		SpeakersManager sm = new SpeakersManager(this);
		sm.startSync();
		Log.d(LOG_TAG, "speakers sync");
		
		ScheduleManager schm = new ScheduleManager(this);
		schm.startSync();
		Log.d(LOG_TAG, "schedule sync");
    }
}
