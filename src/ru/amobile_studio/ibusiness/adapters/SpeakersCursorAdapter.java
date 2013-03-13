package ru.amobile_studio.ibusiness.adapters;

import ru.amobile_studio.ibusiness.R;
import ru.amobile_studio.ibusiness.SplashActivity;
import ru.amobile_studio.ibusiness.managers.ImageLoader;
import ru.amobile_studio.ibusiness.managers.SpeakersManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SpeakersCursorAdapter extends SimpleCursorAdapter {

	private final Context context;
	private final LayoutInflater inflater;
	private final Bitmap plaha;
	
	private static class ViewHolder {
		public TextView fio_speaker;
		public TextView desc_speaker;
		public ImageView image_speaker;
		public ImageView plaha;
		public int id;
		
	}
	
	public SpeakersCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		Bitmap tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.black_plaha);
		Log.d(SplashActivity.LOG_TAG, tmp.getWidth() + " width");
		this.plaha = getRoundedBitmap(tmp, 7);
	}

	@Override
	public void bindView(View view, Context context, Cursor c) {
		ViewHolder vh = new ViewHolder();
		
		Typeface tf = Typeface.createFromAsset(context.getAssets(),
    			"fonts/PFDinDisplayPro-Bold.ttf");
		
		vh.fio_speaker = (TextView) view.findViewById(R.id.fio_speaker);
		vh.fio_speaker.setTypeface(tf);
		vh.fio_speaker.setText(c.getString(c.getColumnIndex(SpeakersManager.NAME_SPEAKER)));
		
		vh.desc_speaker = (TextView) view.findViewById(R.id.desc_speaker);
		vh.desc_speaker.setTypeface(tf);
		vh.desc_speaker.setText(c.getString(c.getColumnIndex(SpeakersManager.NAME_TITLE_REPORT)));
		
		ImageLoader il = new ImageLoader(context);
		
		Bitmap image = il.getImage(c.getString(c.getColumnIndex(SpeakersManager.NAME_IMG)));
		if(image != null){
			image = getRoundedBitmap(image, 6);

			vh.image_speaker = (ImageView) view.findViewById(R.id.image_speaker);
			vh.image_speaker.setImageBitmap(image);
			
			vh.plaha = (ImageView) view.findViewById(R.id.black_plaha);
			vh.plaha.setImageBitmap(plaha);
		}else{
			vh.image_speaker = (ImageView) view.findViewById(R.id.image_speaker);
			vh.plaha = (ImageView) view.findViewById(R.id.black_plaha);
			
			vh.image_speaker.setVisibility(View.INVISIBLE);
			vh.plaha.setVisibility(View.INVISIBLE);
		}
		
		vh.id = c.getInt(c.getColumnIndex(SpeakersManager.NAME_ID));
		view.setId(vh.id);
		view.setTag(vh);
	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		View v = inflater.inflate(R.layout.speakers_list_item, parent, false);
		return v;
	}

	public static Bitmap getRoundedBitmap(Bitmap bitmap, int r) {

		Log.d(SplashActivity.LOG_TAG, "" + bitmap);
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
			    bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			final RectF rectF = new RectF(rect);
			final float roundPx = r;

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);

			return output;
	}

}
