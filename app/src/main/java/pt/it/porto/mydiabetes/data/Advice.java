package pt.it.porto.mydiabetes.data;

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

    public String getTimer() {
        return timer;
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
    private String timer;


    public Advice(String summaryText, String expText, String adviceType, String[] adviceArgs, int urgency){
        super(Type.ADVICE);
        this.expandedText = expText;
        this.summaryText = summaryText;
        this.type = adviceType;
        this.urgency = urgency;
        parseAdvice(adviceArgs);
    }

    private void parseAdvice(String[] adviceAttr) {
        if(this.type.equals(AdviceTypes.ALERT.toString())){
            this.notificationText = adviceAttr[0];
            this.registryType = adviceAttr[1];
            this.timer = adviceAttr[2];
            //run alert
        }
        if(this.type.equals(AdviceTypes.QUESTION) && this.urgency >=7){
            this.notificationText = adviceAttr[0];
        }
        if(this.type.equals(AdviceTypes.SUGGESTION)){
            this.registryType = adviceAttr[0];
        }
    }

    public Calendar getTime(){
        Calendar calendar = YapDroid.string2Time(timer);
        return calendar;
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

        //return (this.getUrgency()-advice.getUrgency());
        return (advice.getUrgency()-this.getUrgency());
    }
}

