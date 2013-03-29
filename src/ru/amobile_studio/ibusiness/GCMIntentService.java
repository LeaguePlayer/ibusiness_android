package ru.amobile_studio.ibusiness;

import static ru.amobile_studio.ibusiness.gsm.CommonUtilities.ACTION_ON_REGISTERED;
import static ru.amobile_studio.ibusiness.gsm.CommonUtilities.FIELD_REGISTRATION_ID;
import static ru.amobile_studio.ibusiness.gsm.CommonUtilities.SENDER_ID;
import ru.amobile_studio.ibusiness.R;
import ru.amobile_studio.ibusiness.gsm.ServerUtilities;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;


public class GCMIntentService extends GCMBaseIntentService {

	public final String TAG = "testgsm";
	
	public GCMIntentService(){
		super(SENDER_ID);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received message");
        //String message = getString(R.string.gcm_message);
        //displayMessage(context, message);
        // notifies user
        generateNotification(context, intent.getStringExtra("message"));
	}
	
	@Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        //String message = getString(R.string.gcm_deleted, total);
        //displayMessage(context, message);
        // notifies user
        //generateNotification(context, message);
    }

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.d(TAG, "Device registered: regId = " + registrationId);
		Intent intent = new Intent(ACTION_ON_REGISTERED);
	    intent.putExtra(FIELD_REGISTRATION_ID, registrationId);
	    context.sendBroadcast(intent);
        ServerUtilities.register(context, registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
        //displayMessage(context, getString(R.string.gcm_unregistered));
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            ServerUtilities.unregister(context, registrationId);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
        }
	}
	
	@Override
	protected void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
        //displayMessage(context, getString(R.string.gcm_error, errorId));
	}
	
	@Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        //displayMessage(context, getString(R.string.gcm_recoverable_error, errorId));
        return super.onRecoverableError(context, errorId);
    }
	
	private static void generateNotification(Context context, String massage){
		
		long[] vibraPattern = {0, 500, 250, 500 };
		
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.icon)
		        .setContentTitle("iБизнес")
		        .setAutoCancel(true)
		        .setVibrate(vibraPattern)
		        .setContentText(massage);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, FrontActivity.class);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(FrontActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		
		mNotificationManager.notify(0, mBuilder.build());
	}

}
