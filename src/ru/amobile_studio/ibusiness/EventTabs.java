package ru.amobile_studio.ibusiness;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import ru.amobile_studio.ibusiness.adapters.PagerAdapter;
import ru.amobile_studio.ibusiness.managers.EventManager;
import ru.amobile_studio.ibusiness.managers.EventManager.Event;
import ru.amobile_studio.ibusiness.speakers.SpeakersList;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

public class EventTabs extends FragmentActivity implements 
						TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener, OnClickListener {
	private TabHost mTabHost;
	private ViewPager mViewPager;
	private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, EventTabs.TabInfo>();
	private PagerAdapter mPagerAdapter;
	private Button goreg;
	
	private class TabInfo {
		private String tag;
		private Class<?> clss;
		private Bundle args;
		private Fragment fragment;
		
		TabInfo(String tag, Class<?> clss, Bundle args){
			this.tag = tag;
			this.clss = clss;
			this.args = args;
		}
	}
	
	class TabFactory implements TabContentFactory{
		private final Context context;
		
		public TabFactory(Context context) {
			this.context = context;
		}
		
		@Override
		public View createTabContent(String tag) {
			View v = new View(context);
			v.setMinimumHeight(0);
			v.setMinimumWidth(0);
			return v;
		}
		
	}
	
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.event_tabs);
    	RelativeLayout plaha = (RelativeLayout) findViewById(R.id.plaha_reg);
    	//plaha.setBackgroundResource(R.drawable.bg_reg_plaha);
    	
    	Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/PFDinDisplayPro-Med.ttf");
    	TextView interes = (TextView) findViewById(R.id.interes);
    	interes.setTypeface(tf);
    	
    	EventManager em = new EventManager(getApplicationContext());
    	Event event = em.getEvents(EventManager.NAME_ID + "=?", 
    			new String[]{"" + getIntent().getIntExtra("event_id", 0)}, null).get(0);
    	((TextView) findViewById(R.id.title_event)).setTypeface(tf);
    	((TextView) findViewById(R.id.title_event)).setText(event.title);
    	/*if(event.title.length() > 60){
    		((TextView) findViewById(R.id.title_event)).setText(event.title.substring(0, 60) + "...");
    	}else{
    		
    	}*/
    	
    	this.initialiseTabHost(savedInstanceState);
    	
		if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); //set the tab as per the saved state
        }
		
		goreg = (Button) findViewById(R.id.goreg);
		goreg.setTypeface(tf);
		goreg.setOnClickListener(this);
		
		// Intialise ViewPager
		this.intialiseViewPager();
    }

    private void intialiseViewPager() {

		List<Fragment> fragments = new Vector<Fragment>();
		fragments.add(Fragment.instantiate(this, EventFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, SpeakersList.class.getName()));
		fragments.add(Fragment.instantiate(this, StickyHeadersSchedule.class.getName()));
		Intent intent = getIntent();
		fragments.get(0).setArguments(intent.getExtras());
		fragments.get(1).setArguments(intent.getExtras());
		fragments.get(2).setArguments(intent.getExtras());
		goreg.setTag(intent.getExtras());
		
		this.mPagerAdapter  = new PagerAdapter(super.getSupportFragmentManager(), fragments);
		//
		this.mViewPager = (ViewPager)super.findViewById(R.id.pager);
		this.mViewPager.setAdapter(this.mPagerAdapter);
		this.mViewPager.setOnPageChangeListener(this);
    }
    
    private void initialiseTabHost(Bundle args) {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
        
        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/PFDinDisplayPro-Bold.ttf");
        
        mTabHost.getTabWidget().setStripEnabled(false);
        TabInfo tabInfo = null;
        
        //one tab
        View tab1 = LayoutInflater.from(mTabHost.getContext()).inflate(R.layout.event_tab, null);
        TextView tv = (TextView) tab1.findViewById(R.id.title_tab);
        tv.setTypeface(tf);
        tv.setText("Инфо");
        AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab1").setIndicator(tab1), 
        		( tabInfo = new TabInfo("Tab1", EventFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        
        //two tab
        View tab2 = LayoutInflater.from(mTabHost.getContext()).inflate(R.layout.event_tab, null);
        tv = (TextView) tab2.findViewById(R.id.title_tab);
        tv.setTypeface(tf);
        tv.setText("Спикеры");
        AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab2").setIndicator(tab2), 
        		( tabInfo = new TabInfo("Tab2", SpeakersList.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        
        //three tab
        View tab3 = LayoutInflater.from(mTabHost.getContext()).inflate(R.layout.event_tab, null);
        tv = (TextView) tab3.findViewById(R.id.title_tab);
        tv.setTypeface(tf);
        tv.setText("Расписание");
        AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab3").setIndicator(tab3), 
        		( tabInfo = new TabInfo("Tab3", StickyHeadersSchedule.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);

        mTabHost.setOnTabChangedListener(this);
	}
    
    private static void AddTab(EventTabs activity, TabHost tabHost, 
    		TabHost.TabSpec tabSpec, TabInfo tabInfo) {
		// Attach a Tab view factory to the spec
		tabSpec.setContent(activity.new TabFactory(activity));
        tabHost.addTab(tabSpec);
	}
    
    protected void onSaveInstanceState(Bundle outState) {
    	outState.putString("tab", mTabHost.getCurrentTabTag()); //save the tab selected
    	super.onSaveInstanceState(outState);
    }

    
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
		this.mTabHost.setCurrentTab(position);
	}

	@Override
	public void onTabChanged(String arg0) {
		// TODO Auto-generated method stub
		int pos = this.mTabHost.getCurrentTab();
		this.mViewPager.setCurrentItem(pos);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.goreg){
			Intent intent = new Intent(this, GoRegActivity.class);
			intent.putExtras(getIntent());
			startActivity(intent);
		}
	}
}
