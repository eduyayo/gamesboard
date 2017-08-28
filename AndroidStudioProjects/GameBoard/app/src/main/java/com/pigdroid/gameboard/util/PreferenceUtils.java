package com.pigdroid.gameboard.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.pigdroid.gameboard.R;

public final class PreferenceUtils {

	private static final String USER_EMAIL = "USER_EMAIL";
	private static final String USER_PASSWORD = "USER_PASSWORD";

	private PreferenceUtils() {
		
	}
	
	public static final String getUserEmail(final Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(USER_EMAIL, null);
	}
	
	public static final String getUserPassword(final Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(USER_PASSWORD, null);
	}
	
	public static final void setUserEmail(final Context context, final String email) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(USER_EMAIL, email).commit();
	}
	
	public static final void setUserPassword(final Context context, final String password) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(USER_PASSWORD, password).commit();
	}

    public static final Uri getNotificationSound(final Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Uri.parse(prefs.getString("notifications_new_message_ringtone", "android.resource://com.pigdroid.gameboard/" + R.raw.notification));
    }

    public static final boolean getNotificationVibrate(final Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("notifications_new_message_vibrate", Boolean.TRUE);
    }


	
}
