package ru.amobile_studio.ibusiness.gsm;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {
	public static final String SERVER_URL = "http://push.amobile-studio.ru/gcm/";

    public static final String SENDER_ID = "3714253922";

    public static final String TAG = "GCM";

    public static final String DISPLAY_MESSAGE_ACTION = "ru.amobile.gcmtest.DISPLAY_MESSAGE";
    public static final String ACTION_ON_REGISTERED   = "ru.amobile.gcmtest.ON_REGISTERED";
    public static final String FIELD_REGISTRATION_ID = "registration_id";

    public static final String EXTRA_MESSAGE = "message";

    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
