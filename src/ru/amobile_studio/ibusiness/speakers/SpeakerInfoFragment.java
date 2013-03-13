package ru.amobile_studio.ibusiness.speakers;

import ru.amobile_studio.ibusiness.R;
import ru.amobile_studio.ibusiness.SplashActivity;
import ru.amobile_studio.ibusiness.managers.SpeakersManager;
import ru.amobile_studio.ibusiness.providers.CProvider;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

public class SpeakerInfoFragment extends FragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.speaker_info);
		
		Typeface tf = Typeface.createFromAsset(getAssets(),
    			"fonts/PFDinDisplayPro-Bold.ttf");
		
		Intent intent = getIntent();
		int id = intent.getIntExtra("speaker_id", 0);
		Log.d(SplashActivity.LOG_TAG, "s id " + id);
		
		Cursor c = getContentResolver().query(CProvider.CONTENT_URI_SPEAKERS, null, 
				SpeakersManager.NAME_ID + "=?", new String[]{id+""}, null);
		if(c.getCount() > 0){
			if(c.moveToFirst()){
				TextView name = (TextView) findViewById(R.id.name);
				name.setTypeface(tf);
				name.setText(c.getString(c.getColumnIndex(SpeakersManager.NAME_SPEAKER)));
				
				TextView info = (TextView) findViewById(R.id.info);
				info.setTypeface(tf);
				info.setText(Html.fromHtml(
						c.getString(c.getColumnIndex(SpeakersManager.NAME_ABOUT_SPEAKER))));
				
				TextView title_report = (TextView) findViewById(R.id.title_report);
				title_report.setTypeface(tf);
				title_report.setText(c.getString(c.getColumnIndex(SpeakersManager.NAME_TITLE_REPORT)));
				
				TextView desc_report = (TextView) findViewById(R.id.desc_report);
				desc_report.setTypeface(tf);
				desc_report.setText(Html.fromHtml(
						c.getString(c.getColumnIndex(SpeakersManager.NAME_ABOUT_REPORT))));
			}
		}
		c.close();
	}
	
}
