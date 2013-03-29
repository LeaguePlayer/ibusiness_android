package ru.amobile_studio.ibusiness;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class FrontActivity extends TabActivity {
	private TabHost tabHost;
	private Typeface tf;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.front);
	    
	    //ImageLoader il = new ImageLoader(this);
        //il.clearDir();
        //this.deleteDatabase(DBHelper.DBNAME);
	    
	    //After delete
//	    Log.d(SplashActivity.LOG_TAG, "class - " + Review.class);
//	    Intent in = new Intent(this, "ru.amobile_studio.ibusiness.Review");
//	    startActivity(in);
//	    
//	    return;
	    
	    tf = Typeface.createFromAsset(getAssets(),"fonts/PFDinDisplayPro-Bold.ttf");
	    
	    DisplayMetrics metrics = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(metrics);
	    /*TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
	    tabHost.setup();*/
	    tabHost = (TabHost) getTabHost();
	    tabHost.getTabWidget().setStripEnabled(false);
	    
	    float density = metrics.density;
	    
	    TabWidget tabWidget = tabHost.getTabWidget();
	    tabWidget.setBackgroundResource(R.drawable.tabs_background);
	    
	    FrameLayout frameL = (FrameLayout) findViewById(android.R.id.tabcontent);
	    frameL.setBackgroundColor(Color.WHITE);
	    
	    //tabWidjet.getLayoutParams().height = (int) (66 * metrics.scaledDensity);
	    tabHost.addTab(createTab(EventsActivity.class, 
                "topEvents", getString(R.string.events_tab), R.drawable.events));
	    tabHost.addTab(createTab(NewsActivity.class, 
                "topNews", getString(R.string.news_tab), R.drawable.news));
	    tabHost.addTab(createTab(PoleznoeActivity.class, 
                "topCab", getString(R.string.polez_tab), R.drawable.usefull));
	    
	    int countTabs = tabWidget.getChildCount();
	    
	    int leftRightMargin = (int) (20*density);
	    int middle = (int) (10*density);
	    
	    //������ �������
	    for(int i = 0; i < countTabs; i++){
	    	View currentView = tabWidget.getChildAt(i);
	    	LinearLayout.LayoutParams currentLayout = (LinearLayout.LayoutParams) currentView.getLayoutParams();
	    	if(i==0){
	    		currentLayout.setMargins(leftRightMargin, 0, middle, 0);
	    	}else if(i == (countTabs - 1)) {
	    		currentLayout.setMargins(middle, 0, leftRightMargin, 0);
	    	}else{
	    		currentLayout.setMargins(middle, 0, middle, 0);
	    	}
	    }
	    tabWidget.requestLayout();
	    tabHost.setCurrentTab(0);
	    
	}
	
	public void switchTab(int tab) {
        tabHost.setCurrentTab(tab);
    }
	
	private TabSpec createTab(final Class<?> intentClass, final String tag, 
            final String title, final int drawable)
    {
        final Intent intent = new Intent().setClass(this, intentClass);

        final View tab = LayoutInflater.from(getTabHost().getContext()).
            inflate(R.layout.tab, null);
        ((TextView)tab.findViewById(R.id.tab_text)).setText(title);
        ((TextView)tab.findViewById(R.id.tab_text)).setTypeface(tf);
        ((ImageView)tab.findViewById(R.id.tab_icon)).setImageResource(drawable);

        return getTabHost().newTabSpec(tag).setIndicator(tab).setContent(intent);
    }

}
