package ru.amobile_studio.ibusiness;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import ru.amobile_studio.ibusiness.managers.JsonManager;
import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.overlay.location.MyLocationItem;
import ru.yandex.yandexmapkit.overlay.location.MyLocationOverlay;
import ru.yandex.yandexmapkit.overlay.location.OnMyLocationListener;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

public class PoleznoeActivity extends Activity implements OnTouchListener, OnMyLocationListener {
    MapController mMapController;
    OverlayManager mOverlayManager;
    MyLocationOverlay myLocation;
    
    private Button b1,b2,b3;
    
    private Overlay foods = null;
    private Overlay hotels = null;
    private Overlay banks = null;
    
    private Thread t;
    private boolean flagOk = false;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poleznoe);
        
        Log.d(SplashActivity.LOG_TAG, "oncreate map");
        
        DisplayMetrics metrics = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
        b1 = (Button) findViewById(R.id.but1);
        b2 = (Button) findViewById(R.id.but2);
        b3 = (Button) findViewById(R.id.but3);
        
        b1.setOnTouchListener(this);
        b2.setOnTouchListener(this);
        b3.setOnTouchListener(this);
        
        //65.55841199999992, 57.18262858092965
        
        final MapView mapView = (MapView) findViewById(R.id.map);
        mapView.showBuiltInScreenButtons(true);
        mapView.showFindMeButton(false);
        mapView.showScaleView(false);
        mapView.showZoomButtons(false);
        mapView.showJamsButton(false);

        mMapController = mapView.getMapController();
        mOverlayManager = mMapController.getOverlayManager();
        myLocation = mOverlayManager.getMyLocation();
        
        if(metrics.density >= 1.5f){
        	mMapController.setHDMode(true);
        }
        
        mMapController.setZoomCurrent(10);
        
        GeoPoint tyumen = new GeoPoint(57.18262858092965d, 65.55841199999992d);
        mMapController.setPositionNoAnimationTo(tyumen);
        myLocation.addMyLocationListener(this);

        t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				flagOk = getGeoPointsFromJson();
				b1.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(flagOk){
							b1.setPressed(true);
							mOverlayManager.addOverlay(foods);
							mMapController.hideBalloon();
							mMapController.notifyRepaint();
							//setZoomSpan(foods);
						}else{
							showMessage();
						}
					}
				});
			}
		});
        t.start();
	}
	
	@Override
	public void onMyLocationChange(MyLocationItem locationItem) {
		// TODO Auto-generated method stub
		mMapController.setPositionAnimationTo(locationItem.getGeoPoint(), 15f);
		myLocation.setEnabled(false);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() != MotionEvent.ACTION_DOWN)
			return true;
		b1.setPressed(false);
		b2.setPressed(false);
		b3.setPressed(false);
		
		if(!flagOk){
			showMessage();
			Log.d(SplashActivity.LOG_TAG, " --- ");
			return true;
		}
		
		Button b = (Button) v;
		
		clearOverlay();
		
		switch(v.getId()){
			case R.id.but1:{
				b.setPressed(true);
				mOverlayManager.addOverlay(foods);
				mMapController.hideBalloon();
				mMapController.notifyRepaint();
				//setZoomSpan(foods);
				break;
			}
			case R.id.but2:{
				b.setPressed(true);
				mOverlayManager.addOverlay(hotels);
				mMapController.hideBalloon();
				mMapController.notifyRepaint();
				//setZoomSpan(hotels);
				break;
			}
			case R.id.but3:{
				b.setPressed(true);
				mOverlayManager.addOverlay(banks);
				mMapController.hideBalloon();
				mMapController.notifyRepaint();
				//setZoomSpan(banks);
				break;
			}
		}
		
		return true;
	}

	private boolean getGeoPointsFromJson(){
		final JsonManager jm0 = new JsonManager(SplashActivity.DOMEN + "/ypoints/getjson/0");
		String res0 = jm0.getJsonResult();
		final JsonManager jm1 = new JsonManager(SplashActivity.DOMEN + "/ypoints/getjson/2");
		String res1 = jm1.getJsonResult();
		final JsonManager jm2 = new JsonManager(SplashActivity.DOMEN + "/ypoints/getjson/1");
		String res2 = jm2.getJsonResult();
		if(res0 == "" || res1 == "" || res2 == ""){
			return false;
		}
		try {
			foods = setBalloonsInOverlayFromJson(foods, res0);
			hotels = setBalloonsInOverlayFromJson(hotels, res1);
			banks = setBalloonsInOverlayFromJson(banks, res2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private void clearOverlay(){
		try {
			t.join();
			if(flagOk){
				mOverlayManager.removeOverlay(foods);
				mOverlayManager.removeOverlay(hotels);
				mOverlayManager.removeOverlay(banks);
			}else{
				showMessage();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void showMessage(){
		Toast.makeText(getApplicationContext(), "Необходимо подключение к сети.", 
				Toast.LENGTH_LONG).show();
	}
	
	private Overlay setBalloonsInOverlayFromJson(Overlay overlay, String jsonStr) throws JSONException{
		JSONObject jsonObject = new JSONObject(jsonStr);
		Resources res = getResources();
		
		Iterator<String> itr = jsonObject.keys();
		JSONObject jo = null;
		
		if(overlay == null){
			overlay = new Overlay(mMapController);
		}else{
			return overlay;
		}
		
		while(itr.hasNext()){
			String key = itr.next();
			jo = jsonObject.getJSONObject(key);
			OverlayItem oItem = new OverlayItem(new GeoPoint(jo.getDouble("cord_Y"), 
					jo.getDouble("cord_X")), res.getDrawable(R.drawable.shop));
			BalloonItem balloon = new BalloonItem(this, oItem.getGeoPoint());
			balloon.setText(Html.fromHtml("<strong>" + jo.getString("title")+ 
					"</strong><br/>" +  jo.getString("subtitle")));
			oItem.setBalloonItem(balloon);
			overlay.addOverlayItem(oItem);
		}
		
		return overlay;
	}
	
	/*private void setZoomSpan(Overlay mOverlay){
        List<OverlayItem> list = mOverlay.getOverlayItems();
        double maxLat, minLat, maxLon, minLon;
        maxLat = maxLon = Double.MIN_VALUE;
        minLat = minLon = Double.MAX_VALUE;
        for (int i = 0; i < list.size(); i++){
            GeoPoint geoPoint = list.get(i).getGeoPoint();
            double lat = geoPoint.getLat();
            double lon = geoPoint.getLon();

            maxLat = Math.max(lat, maxLat);
            minLat = Math.min(lat, minLat);
            maxLon = Math.max(lon, maxLon);
            minLon = Math.min(lon, minLon);
        }
        mMapController.setZoomToSpan(maxLat - minLat, maxLon - minLon);
        mMapController.setPositionAnimationTo(new GeoPoint((maxLat + minLat)/2, (maxLon + minLon)/2));
    }*/
	
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