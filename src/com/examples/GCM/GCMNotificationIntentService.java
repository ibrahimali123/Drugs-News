package com.examples.GCM;

import java.util.ArrayList;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.examples.antibiotic.R;
import com.examples.antibiotic.R.drawable;
import com.examples.model.DataBase;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMNotificationIntentService extends IntentService {
	// Sets an ID for the notification, so it can be updated
	public static final int notifyID = 9001;
	NotificationCompat.Builder builder;

	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString(), "" , "");
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification(
						"Deleted messages on server: " + extras.toString(), "" , "");
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				sendNotification(
						"This is new news: \n\n\n"
								+ extras.get(ApplicationConstants.MSG_KEY),
						"This is new news "
								+ extras.get(ApplicationConstants.MSG_KEY) ,(String)extras.get(ApplicationConstants.MSG_KEY));
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg, String type, String dbMsg) {
		
		
		Intent resultIntent = new Intent(this,
				NotificationMessageActivity.class);
		
		System.out.println("msg : " + msg);
		if (type.contains("null")) {
			msg = ("This is our first news:\n\n" + "Welcome :) ");
			dbMsg = "Welcome :) ";
		}
		
		DataBase db = new DataBase(this); 
		db.insertNews(dbMsg);
		System.out.println("inserted");
		
		resultIntent.putExtra("msg", msg);

		PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
				resultIntent, PendingIntent.FLAG_ONE_SHOT);

		NotificationCompat.Builder mNotifyBuilder;
		NotificationManager mNotificationManager;

		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		mNotifyBuilder = new NotificationCompat.Builder(this)
				.setContentTitle("New News")
				.setContentText("You've received new message.")
				.setSmallIcon(R.drawable.ic_noty);
		// Set pending intent
		mNotifyBuilder.setContentIntent(resultPendingIntent);

		// Set Vibrate, Sound and Light
		int defaults = 0;
		defaults = defaults | Notification.DEFAULT_LIGHTS;
		defaults = defaults | Notification.DEFAULT_VIBRATE;
		defaults = defaults | Notification.DEFAULT_SOUND;

		mNotifyBuilder.setDefaults(defaults);
		// Set the content for Notification
		mNotifyBuilder.setContentText("You get a new message from Drugs News");
		// Set autocancel
		mNotifyBuilder.setAutoCancel(true);
		// Post a notification
		mNotificationManager.notify(notifyID, mNotifyBuilder.build());
	}
}