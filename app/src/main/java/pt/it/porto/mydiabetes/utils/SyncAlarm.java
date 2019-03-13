package pt.it.porto.mydiabetes.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.Preferences;
import pt.it.porto.mydiabetes.ui.activities.SettingsImportExport;

public class SyncAlarm extends BroadcastReceiver {

	public static final String SYNC_ALARM_PREFERENCE = "sync_alarm";
	public static final String SYNC_ALARM_LAST_SYNC = "sync_alarm_last";
	public static final String CHANNEL_ID="sync_notification_id";

	public SyncAlarm() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID);
		builder.setSmallIcon(R.drawable.gota_white);
		builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_v2));
		builder.setContentTitle(context.getString(R.string.app_name));
		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		builder.setContentText(context.getString(R.string.notification_sync_text));
		if (Preferences.getPreferences(context).contains(SYNC_ALARM_LAST_SYNC)) {
			bigTextStyle.bigText(context.getString(R.string.notification_sync_recurrent));
		} else {
			bigTextStyle.bigText(context.getString(R.string.notification_sync_first_time));
		}

		builder.setPriority(NotificationCompat.PRIORITY_MIN);
		builder.setStyle(bigTextStyle);
		builder.setAutoCancel(true);

		Intent resultIntent = new Intent(context, SettingsImportExport.class);
		Bundle extras = new Bundle();
		//extras.putInt(ImportExport.EXTRAS_TAB, ImportExport.EXTRAS_TAB_IMPORT_EXPORT);
		resultIntent.putExtras(extras);

		TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
		taskStackBuilder.addParentStack(SettingsImportExport.class);
		taskStackBuilder.addNextIntent(resultIntent);
		builder.setContentIntent(taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT));

		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, context.getString(R.string.notification_channel_sync_description), NotificationManager.IMPORTANCE_LOW);
			mNotificationManager.createNotificationChannel(channel);
			builder.setChannelId(CHANNEL_ID);
		}

		mNotificationManager.notify(0, builder.build());

		// alarm fired, remove setting and sets time of the sync
		Preferences.getPreferences(context).edit().remove(SYNC_ALARM_PREFERENCE)
				.putLong(SYNC_ALARM_LAST_SYNC, System.currentTimeMillis()).commit();
	}
}
