package ru.amobile_studio.ibusiness.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import ru.amobile_studio.ibusiness.SplashActivity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class ImageLoader {
	
	public final static String IMAGE_DIR = "iBiz";
	
	private boolean mExternalStorageAvailable = false;
	private boolean mExternalStorageWriteable = false;
	private String state = Environment.getExternalStorageState();
	private Context context;
	
	public ImageLoader(Context context){
		this.context = context;
		if(CheckSdCard()){
			//clearDir();
		}
		Log.d(SplashActivity.LOG_TAG, "check sdCard = " + mExternalStorageAvailable);
	}
	
	private boolean CheckSdCard(){
		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		
		return mExternalStorageAvailable && mExternalStorageWriteable;
	}
	
	private void saveToInternalStorage(Bitmap bitmapImage,String filename){
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir(IMAGE_DIR, Context.MODE_PRIVATE);
        File mypath = new File(directory, filename);

        FileOutputStream fos = null;
        try {
           // fos = openFileOutput(filename, Context.MODE_PRIVATE);
            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
	
	private Bitmap loadInternalImage(String filename) throws FileNotFoundException{
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir(IMAGE_DIR, Context.MODE_PRIVATE);
        File file = new File(directory, filename);
        if(file.exists()){
        	Bitmap b = BitmapFactory.decodeStream(new FileInputStream(file));
        	return b;
        }
        return null;
    }
	
	public void saveImageFromUrl(String urlImage, String fileName){
		//if(CheckSdCard()){
			Bitmap b;
			try {
				b = loadInternalImage(fileName);
				if(b == null){
					URL url = new URL(urlImage);
					b = BitmapFactory.decodeStream(url.openConnection().getInputStream());
					saveToInternalStorage(b, fileName);
					Log.d(SplashActivity.LOG_TAG, "save file - " + fileName);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//}
	}
	
	public Bitmap getImage(String filename) {
		Bitmap bmp = null;
		try {
			bmp = loadInternalImage(filename);
			Log.d(SplashActivity.LOG_TAG, "load file - " + filename);		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bmp;
	}
	
	public void updateImage(String oldFile, String newFile, String urlNewFile){
		if((oldFile != newFile) && urlNewFile != "" ){
			deleteImage(oldFile);
			saveImageFromUrl(urlNewFile, newFile);
		}
	}
	
	protected boolean deleteImage(String filename){
		ContextWrapper cw = new ContextWrapper(context);
		File directory = cw.getDir(IMAGE_DIR, Context.MODE_PRIVATE);
        File file = new File(directory, filename);
        if(file.exists()){
        	return file.delete();
        }
        return false;
	}
	
	public void clearDir(){
		ContextWrapper cw = new ContextWrapper(context);
		File directory = cw.getDir(IMAGE_DIR, Context.MODE_PRIVATE);
		String[] files = directory.list();
		for(int i = 0; i < files.length; i++){
			new File(directory, files[i]).delete();
			Log.d(SplashActivity.LOG_TAG, "delete file - " + files[i]);
		}
		
	}
}
