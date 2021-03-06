package pt.it.porto.mydiabetes.data;

import android.content.Context;

import java.util.Calendar;

//import pt.it.porto.mydiabetes.adviceSystem.yapDroid.YapDroid;
import pt.it.porto.mydiabetes.utils.HomeElement;

/**
 * Created by Diogo on 12/05/2016.
 */
public class Task extends HomeElement implements Comparable<Task> {

    public String getRegistryType() {
        return registryType;
    }

    public String getTimer() {
        return timer;
    }

    public String getSummaryText() {
        return summaryText;
    }

    private String summaryText;
    private String expandedText;
    private int urgency;

    private String notificationText;
    private String registryType;
    private String timer;


    public Task(){
        super(Type.TASK);
    }

    public Task(String summaryText, String expText, String[] taskArgs, int urgency){
        super(Type.TASK);
        this.expandedText = expText;
        this.summaryText = summaryText;
        this.urgency = urgency;
        parseTask(taskArgs);
    }
    public void setSummaryText(String summaryText){
        this.summaryText = summaryText;
    }
    public void setExpText(String expandedText){
        this.expandedText = expandedText;
    }
    public void setTaskArgs(String[] taskArgs){
        parseTask(taskArgs);
    }
    public void setUrg(int urg){
        this.urgency = urg;
    }

    private void parseTask(String[] adviceAttr) {
        if(adviceAttr!=null){
            this.notificationText = adviceAttr[0];
            this.registryType = adviceAttr[1];
            this.timer = adviceAttr[2];
        }
    }

    public Calendar getTime(){
        String[] timings = timer.split(":");
        int timeValue = Integer.parseInt(timings[0]);
        //Calendar calendar = YapDroid.string2Time(timeValue, timings[1]);
        //return calendar;
        return null;
    }

    public String getExpandedText() {return expandedText;}

    public int getUrgency() {return urgency;}

    public String getNotificationText() {return notificationText;}

    public void setupAlarm(Context c) {

        /*AlarmManager alm = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(c, AdviceAlertReceiver.class);

        Bundle extras = new Bundle();
        extras.putString("NotificationText", getNotificationText());
        intent.putExtras(extras);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(c, 0, intent, 0);
        alm.set(AlarmManager.RTC_WAKEUP, getTime().getTimeInMillis(), alarmIntent);*/

        //long timeTest = System.currentTimeMillis() + 5 * 1000;
        //alm.set(AlarmManager.RTC_WAKEUP, timeTest, alarmIntent);
    }


    @Override
    public String toString() {
        return "Task{" +
                "summaryText='" + summaryText + '\'' +
                ", expandedText='" + expandedText + '\'' +
                ", urgency=" + urgency +
                ", notificationText='" + notificationText + '\'' +
                ", registryType='" + registryType + '\'' +
                ", timer='" + timer + '\'' +
                '}';
    }

    @Override
    public int compareTo(Task task) {
        return (task.getUrgency()-this.getUrgency());
    }

}

