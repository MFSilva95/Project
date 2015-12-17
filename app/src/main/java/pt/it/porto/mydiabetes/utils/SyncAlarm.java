package pt.it.porto.mydiabetes.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.Preferences;
import pt.it.porto.mydiabetes.ui.activities.ImportExport;

public class SyncAlarm extends BroadcastReceiver {

	public static final String SYNC_ALARM_PREFERENCE = "sync_alarm";
	public static final String SYNC_ALARM_LAST_SYNC = "sync_alarm_last";


	public SyncAlarm() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setSmallIcon(R.drawable.gota_white);
		builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
		builder.setContentTitle(context.getString(R.string.app_name));
		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		builder.setContentText("Envie os dados ao gestor do projecto");
		if (Preferences.getPreferences(context).contains(SYNC_ALARM_LAST_SYNC)) {
			bigTextStyle.bigText("Já passaram 7 dias desde a última vez que enviou dados ao projecto. Quer enviar?");
		} else {
			bigTextStyle.bigText("Já passaram 7 dias desde que começou a usar a aplicação, deseja enviar os dados ao projecto?");
		}

		builder.setPriority(NotificationCompat.PRIORITY_LOW);
		builder.setStyle(bigTextStyle);
		builder.setAutoCancel(true);

		Intent resultIntent = new Intent(context, ImportExport.class);
		Bundle extras = new Bundle();
		extras.putInt(ImportExport.EXTRAS_TAB, ImportExport.EXTRAS_TAB_IMPORT_EXPORT);
		resultIntent.putExtras(extras);

		TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
		taskStackBuilder.addParentStack(ImportExport.class);
		taskStackBuilder.addNextIntent(resultIntent);
		builder.setContentIntent(taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT));

		NotificationManager mNotificationManager =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, builder.build());

		// alarm fired, remove setting and sets time of the sync
		Preferences.getPreferences(context).edit().remove(SYNC_ALARM_PREFERENCE)
				.putLong(SYNC_ALARM_LAST_SYNC, System.currentTimeMillis()).commit();
	}
}
