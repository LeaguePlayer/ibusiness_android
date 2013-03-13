package ru.amobile_studio.ibusiness.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.amobile_studio.ibusiness.R;
import ru.amobile_studio.ibusiness.managers.ScheduleManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emilsjolander.components.stickylistheaders.StickyListHeadersCursorAdapter;

public class ScheduleCursorAdapter extends StickyListHeadersCursorAdapter {

	private LayoutInflater inflater;
	
	private final static SimpleDateFormat inDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private final static SimpleDateFormat outDateFormat = new SimpleDateFormat("EEEE dd MMMM");

	private final static SimpleDateFormat inFormat = new SimpleDateFormat("H:m:s");
	private final static SimpleDateFormat outFormat = new SimpleDateFormat("H:mm");
	
	private Typeface tf;

	public ScheduleCursorAdapter(Context context, Cursor c) {
		super(context, c, false);
		inflater = LayoutInflater.from(context);
		tf = Typeface.createFromAsset(context.getAssets(),	"fonts/PFDinDisplayPro-Bold.ttf");
	}

	@Override
	protected View newHeaderView(Context context, Cursor cursor) {
		HeaderViewHolder holder = new HeaderViewHolder();
		View v = inflater.inflate(R.layout.header_shedule, null);
		holder.sectionDate = (TextView) v.findViewById(R.id.section_text);
		holder.sectionDate.setTypeface(tf);
		v.setTag(holder);
		return v;
	}

	class HeaderViewHolder{
		TextView sectionDate;
	}
	
	class ViewHolder{
		TextView time;
		TextView title;
		TextView desc;
		ImageView coffee;
	}

	@Override
	protected void bindHeaderView(View view, Context context, Cursor cursor) {
		try {
			String headerText = outDateFormat.format(inDateFormat.parse(
					cursor.getString(cursor.getColumnIndex(ScheduleManager.NAME_AT_DAY))));
			((HeaderViewHolder)view.getTag()).sectionDate.setText(headerText);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected long getHeaderId(Context context, Cursor cursor) {

		try {
			Date dateHeader = inDateFormat.parse(
					cursor.getString(cursor.getColumnIndex(ScheduleManager.NAME_AT_DAY)));
			return (long) dateHeader.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public void bindView(View view, Context context, Cursor c) {

		String timeStart = "", timeFinish = "";
		try {
			timeStart = outFormat.format(inFormat.parse(
					c.getString(c.getColumnIndex(ScheduleManager.NAME_TIME_BEGIN))));
			timeFinish = outFormat.format(inFormat.parse(
					c.getString(c.getColumnIndex(ScheduleManager.NAME_TIME_FINISH))));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(c.getInt(c.getColumnIndex(ScheduleManager.NAME_COFFEE)) == 1){
			((ViewHolder)view.getTag()).time.setText(timeStart + " - " + timeFinish);
			((ViewHolder)view.getTag()).title.setText(c.getString(c.getColumnIndex(ScheduleManager.NAME_TITLE)));
			((ViewHolder)view.getTag()).coffee.setVisibility(View.VISIBLE);
			((ViewHolder)view.getTag()).desc.setVisibility(View.GONE);
		}else{
			((ViewHolder)view.getTag()).time.setText(timeStart + " - " + timeFinish);
			((ViewHolder)view.getTag()).title.setText(c.getString(c.getColumnIndex(ScheduleManager.NAME_TITLE)));
			((ViewHolder)view.getTag()).coffee.setVisibility(View.GONE);

			String desc_text = c.getString(c.getColumnIndex(ScheduleManager.NAME_SUBTITLE));
			if(desc_text.length() != 0){
				((ViewHolder)view.getTag()).desc.setText(desc_text);
				((ViewHolder)view.getTag()).desc.setVisibility(View.VISIBLE);
			}else{
				((ViewHolder)view.getTag()).desc.setVisibility(View.GONE);
			}
			
		}
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		View v = inflater.inflate(R.layout.event_schedule_list_item, null);
		holder.title = (TextView) v.findViewById(R.id.s_title);
		holder.title.setTypeface(tf);
		holder.time = (TextView) v.findViewById(R.id.item_time);
		holder.time.setTypeface(tf);
		holder.desc = (TextView) v.findViewById(R.id.s_desc);
		holder.desc.setTypeface(tf);
		holder.coffee = (ImageView) v.findViewById(R.id.coffee);
		v.setTag(holder);
		return v;
	}

}
