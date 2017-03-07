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
import pt.it.porto.mydiabetes.ui.activities.Home;

/**
 * Created by Diogo on 19/04/2016.
 */
public class TaskAlertReceiver extends BroadcastReceiver {

    public Class getClassFromString(String testType){
        try{
            String path = "pt.it.porto.mydiabetes.ui.activities."+testType;
            //System.out.println("Le path is: "+path);
            return Class.forName(path);
        }catch (Exception e){
            System.err.println(e.toString());
        }
        return null;
    }


    @Override
    public void onReceive(Context context, Intent intent){

        String className;
        String notificationText = "";
        Bundle extras = intent.getExtras();


        if(extras == null) {
             className = null;
        } else {
            className = extras.getString("RegistryClassName");
            notificationText = extras.getString("NotificationText");
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.gota_white);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_v2));
        builder.setContentTitle(context.getString(R.string.app_name));
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        builder.setContentText(notificationText);

        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setStyle(bigTextStyle);
        builder.setAutoCancel(true);

        Intent resultIntent = new Intent(context, getClassFromString(className));

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(Home.class);
        taskStackBuilder.addNextIntent(resultIntent);
        builder.setContentIntent(taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT));

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());

    }
}
