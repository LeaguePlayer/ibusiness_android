package ru.amobile_studio.ibusiness.speakers;

import ru.amobile_studio.ibusiness.R;
import ru.amobile_studio.ibusiness.SplashActivity;
import ru.amobile_studio.ibusiness.adapters.SpeakersCursorAdapter;
import ru.amobile_studio.ibusiness.managers.SpeakersManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SpeakersList extends ListFragment implements LoaderCallbacks<Cursor> {
	
	public final static String LOG_SPEAKERS = "speakers";
	private SpeakersCursorAdapter adapter;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Log.d(LOG_SPEAKERS, "list speakers");
		String[] from = new String[] {SpeakersManager.NAME_SPEAKER, SpeakersManager.NAME_TITLE_REPORT};
		int[] to = new int[] {R.id.fio_speaker, R.id.desc_speaker};
		
		Bundle args = getArguments();
		getLoaderManager().initLoader(0, args, this);
		//adapter = new SpeakersCursorAdapter(getActivity(), null, 0);
		adapter = new SpeakersCursorAdapter(getActivity().getApplicationContext(), 
				R.layout.speakers_list_item, null, from, to, 0);
		setListAdapter(adapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.speakers_listview, container, false);
		// TODO Auto-generated method stub
		return v;
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle args) {
		// TODO Auto-generated method stub
		int event_id = args.getInt("event_id");
		String uriStr = "content://ru.amobile_studio.ibusiness.providers/speakers";
		Uri uri = Uri.parse(uriStr);
				
		return new CursorLoader(getActivity(), uri, null, SpeakersManager.NAME_ID_EVENT + "=?", 
				new String[] {"" + event_id}, "data_sort");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		// TODO Auto-generated method stub
		adapter.swapCursor(cursor);
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		adapter.swapCursor(null);
		
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		//super.onListItemClick(l, v, position, id);
		Log.d(SplashActivity.LOG_TAG, "click " + id);
		Intent intent = new Intent(getActivity(), SpeakerInfoFragment.class);
		intent.putExtra("speaker_id", (int) id);
		getActivity().startActivity(intent);
	}
	
}
