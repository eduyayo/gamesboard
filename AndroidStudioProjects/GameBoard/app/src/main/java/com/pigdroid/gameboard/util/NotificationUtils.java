package com.pigdroid.gameboard.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.pigdroid.gameboard.R;

public class NotificationUtils {

	private static long[] VIV_PATTERN = {0, 100, 100, 100, 200, 100};
	
	private NotificationUtils() {
	}
	
	public static void createNotification(Context context, String id, Intent resultIntent, String title, String content, Object ... params) {
		NotificationCompat.Builder builder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.ic_stat_notif_trans);
		builder.setContentTitle(String.format(title, params));
		if (content != null) {
	        builder.setContentText(String.format(content, params));
		}
		builder.setContentIntent(PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)).setAutoCancel(true);
		builder.setSound(PreferenceUtils.getNotificationSound(context));
        if (PreferenceUtils.getNotificationVibrate(context)) {
            builder.setVibrate(VIV_PATTERN);
        }
		NotificationManager notificationManager =
			    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(id, 0, builder.build());
	}

	public static void cancelNotification(Context context, String id) {
		NotificationManager notificationManager =
			    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(id, 0);
	}

}
