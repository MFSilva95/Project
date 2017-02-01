package pt.it.porto.mydiabetes.data;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.Calendar;

import pt.it.porto.mydiabetes.adviceSystem.yapDroid.YapDroid;
import pt.it.porto.mydiabetes.utils.HomeElement;

/**
 * Created by Diogo on 12/05/2016.
 */
public class Advice extends HomeElement implements Comparable<Advice> {

    public String getRegistryType() {
        return registryType;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public enum AdviceTypes{NORMAL, SUGGESTION, QUESTION, ALERT};

    private String summaryText;
    private String expandedText;
    private int urgency;
    private String type;

    private String notificationText;
    private String registryType;
    private int time;
    private String timeUnit;

    public void setSummaryText(String summaryText){
        this.summaryText = summaryText;
    }
    public void setExpandedText(String expandedText){
        this.expandedText = expandedText;
    }
    public void setUrgency(int urgency){
        this.urgency = urgency;
    }
    public void setType(String type){
        this.type = type;
    }
    public String getAdviceType(){
        return this.type;
    }
    public void setRegistryType(String regType){
        this.registryType = regType;
    }

    public void setNotification(String[] notificationParams, Context context){
        notificationText = notificationParams[0];
        registryType = notificationParams[1];
        time = Integer.parseInt(notificationParams[2]);
        timeUnit = notificationParams[3];
        setupAlarm(context);

    }

    public Advice(){
        super(Type.ADVICE);
    }

    public Advice(Context currentContext, String summaryText, String expText, String adviceType, String[] adviceArgs, int urgency){
        super(Type.ADVICE);
        this.expandedText = expText;
        this.summaryText = summaryText;
        this.type = adviceType;
        this.urgency = urgency;
        if(this.type.equals(AdviceTypes.ALERT.toString())){
            parseArgs(adviceArgs);
            setupAlarm(currentContext);
        }
    }

    private void parseArgs(String[] adviceAttr) {
        if(this.type.equals(AdviceTypes.ALERT.toString())){
            this.notificationText = adviceAttr[0];
            this.registryType = adviceAttr[1];
            time = Integer.parseInt(adviceAttr[2]);
            timeUnit = adviceAttr[3];
        }
        if(this.type.equals(AdviceTypes.QUESTION)){
            //is something present?
            //if yes assert id:
            this.registryType = adviceAttr[0]; //saves id to assert
            //id to assert missing.
        }
        if(this.type.equals(AdviceTypes.SUGGESTION)){
            //makes suggestion on what to do:
            //ex: you should registry something
            //needs normal text: expText with question: registry type
            this.registryType = adviceAttr[0];
        }
    }

    public Calendar getTime(){
        Calendar calendar = YapDroid.string2Time(time, timeUnit);
        return calendar;
    }
    private void setupAlarm(Context ctxt) {

        /*AlarmManager alm = (AlarmManager) ctxt.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctxt, AdviceAlertReceiver.class);

        Bundle extras = new Bundle();
        extras.putString("RegistryClassName", this.getRegistryType());
        extras.putString("NotificationText", this.getNotificationText());
        intent.putExtras(extras);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(ctxt, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long timeTest = getTime().getTimeInMillis();
        alm.set(AlarmManager.RTC_WAKEUP, timeTest, alarmIntent);*/
    }

    public String getExpandedText() {return expandedText;}
    public String getType() {return type;}

    public int getUrgency() {
        return urgency;
    }

    public String getNotificationText() {
        return notificationText;
    }

    @Override
    public int compareTo(Advice advice) {
        return (advice.getUrgency()-this.getUrgency());
    }
}

