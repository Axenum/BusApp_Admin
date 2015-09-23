package com.allits.BusAdmin;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Date;

public class GCMIntentService extends IntentService {
	// Sets an ID for the notification, so it can be updated
	int notifyID = 0;
	NotificationCompat.Builder builder;

	public GCMIntentService() {
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
				//sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				//sendNotification("Deleted messages on server: "
				//		+ extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

                String[] data = {extras.getString("tour"),extras.getString("datum"),extras.getString("time"),extras.getString("ktime")};

                sendNotification(data);
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String[] msg) {
	        Intent resultIntent = new Intent(this, AlertActivity.class);
            resultIntent.putExtra("tour", msg[0]);
            resultIntent.putExtra("datum", msg[1]);

            long timediff = TimeFunction.getTimeDiff(msg[2],msg[3]);
            resultIntent.putExtra("ktime", timediff+"");


	        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
	                resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);


            NotificationCompat.Builder mNotifyBuilder;
	        NotificationManager mNotificationManager;

	        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

	        mNotifyBuilder = new NotificationCompat.Builder(this)
	                .setContentTitle("Alert")
	                .setContentText("You've received new message.")
	                .setSmallIcon(R.drawable.ic_launcher);
	        // Set pending intent
	        mNotifyBuilder.setContentIntent(resultPendingIntent);

	        // Set Vibrate, Sound and Light
	        int defaults = 0;
	        //defaults = defaults | Notification.DEFAULT_LIGHTS;
	        //defaults = defaults | Notification.DEFAULT_VIBRATE;
	        // defaults = defaults | Notification.DEFAULT_SOUND;

            Notification notification = mNotifyBuilder.build();

            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm);
            notification.defaults |= Notification.DEFAULT_VIBRATE;

	        mNotifyBuilder.setDefaults(defaults);
	        // Set the content for Notification
	        mNotifyBuilder.setContentText("New message from Server");
	        // Set autocancel
	        mNotifyBuilder.setAutoCancel(true);
	        // Post a notification
            Date date = new Date();
            notifyID = (int) date.getTime();
	        mNotificationManager.notify(notifyID, notification);

	}
}
