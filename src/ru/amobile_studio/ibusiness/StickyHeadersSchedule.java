package ru.amobile_studio.ibusiness;

import ru.amobile_studio.ibusiness.adapters.ScheduleCursorAdapter;
import ru.amobile_studio.ibusiness.managers.ScheduleManager;
import ru.amobile_studio.ibusiness.providers.CProvider;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView;

public class StickyHeadersSchedule extends Fragment implements OnScrollListener {
	
	private static final String KEY_LIST_POSITION = "KEY_LIST_POSITION";
	private int firstVisible;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Bundle args = getArguments();
		
		View v = inflater.inflate(R.layout.stickylist, null);

		StickyListHeadersListView stickyList = (StickyListHeadersListView) v.findViewById(R.id.stickylist);
		stickyList.setOnScrollListener(this);

		if (savedInstanceState != null) {
			firstVisible = savedInstanceState.getInt(KEY_LIST_POSITION);
		}
		
		Cursor c = getActivity().getContentResolver().query(CProvider.CONTENT_URI_SCHEDULE, null, 
				ScheduleManager.NAME_ID_EVENT + "=?", 
				new String[] {"" + args.getInt("event_id")}, 
				"date(" +ScheduleManager.NAME_AT_DAY + ") ASC, " + 
				"time(" + ScheduleManager.NAME_TIME_BEGIN + ")");
		
		stickyList.setAdapter(new ScheduleCursorAdapter(getActivity(), c));
		stickyList.setSelection(firstVisible);
		return v;
	}

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}
