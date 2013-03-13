package ru.amobile_studio.ibusiness;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

public class NewsItemActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_web);
		
		Intent intent = getIntent();
		String url = intent.getStringExtra("url");
		
		WebView web = (WebView) findViewById(R.id.web_news);
		
		web.getSettings().setLoadWithOverviewMode(true);
		web.getSettings().setUseWideViewPort(true);
		web.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		web.getSettings().setBuiltInZoomControls(true);
		web.getSettings().setSupportZoom(true); 
		web.setInitialScale(1);
		
		//web.getSettings().setLoadWithOverviewMode(true);
		//web.getSettings().setUseWideViewPort(true);
		//web.getSettings().setSupportZoom(true);
		
		//исправляем url
		String res = url.replace("http://", "");
		String good = res.replace("//", "/");
		Log.d(SplashActivity.LOG_TAG, res);
		
		web.loadUrl("http://"+good);

		Log.d(SplashActivity.LOG_TAG, good);
	}

}
