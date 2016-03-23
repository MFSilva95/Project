package pt.it.porto.mydiabetes.middleHealth.myglucohealth;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.database.DeviceMeasureDb;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.Measure;
import pt.it.porto.mydiabetes.middleHealth.utils.IntentBuilder;
import pt.it.porto.mydiabetes.ui.activities.MealActivity;
import pt.it.porto.mydiabetes.utils.DateUtils;

public class BluetoothChangesReceiver extends BroadcastReceiver {
    static final String TAG = BluetoothChangesReceiver.class.getCanonicalName();

    Glucometer.OnMeasurementListener onMeasurementListener;
    Thread glucometer;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "-----------------");
        Log.d(TAG, intent.toString());
//		Log.d(TAG, intent.getExtras().toString());
        Log.d(TAG, "-----------------");
        if (onMeasurementListener == null) {
            onMeasurementListener = new MyMeasurementListener(context);
            Glucometer.getInstance().setOnMeasurementListener(onMeasurementListener);
        }
        if (glucometer == null) {
            glucometer = new Thread(Glucometer.getInstance());
            glucometer.start();
        } else {
            if (!glucometer.isAlive()) {
                glucometer = new Thread(Glucometer.getInstance());
                glucometer.start();
            }
        }
    }

    class MyMeasurementListener implements Glucometer.OnMeasurementListener {
        private Context context;

        public MyMeasurementListener(Context context) {
            this.context = context;
        }


        @Override
        public void onNewMeasure(Measure measure) {
            GlycemiaRec glycemiaRec = new GlycemiaRec();
            glycemiaRec.setValue((int) (double) measure.getValues().get(0));
            glycemiaRec.setDateTime(DateUtils.getCalendar(measure.getTimestamp()));

            DeviceMeasureDb deviceMeasureDb = new DeviceMeasureDb(MyDiabetesStorage.getInstance(context));
            int id = deviceMeasureDb.saveGlycemiaRec(glycemiaRec);

            if (id != -1) {
                // place notification
                Resources resources = context.getResources();
                String text = resources.getQuantityString(R.plurals.notification_text_new_glucose_measure, 1, 1);
                String bigText = text;

                bigText += " " + resources.getQuantityString(R.plurals.notification_text_new_meal, 1, 1);
                Intent intent = IntentBuilder.buildMealIntent(context, id);
                build(context, 0, MealActivity.class, text, bigText, intent);
            }
        }

        public void build(Context context, int id, Class activityToLaunch, String text, String bigText, Intent intent) {
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.bigText(bigText);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(text)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setStyle(bigTextStyle)
                    .setAutoCancel(true);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(activityToLaunch);
            stackBuilder.addNextIntent(intent);

            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // ID allows you to update the notification later on:
            notificationManager.notify(id, builder.build());
        }

    }
}
