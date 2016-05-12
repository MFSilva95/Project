package pt.it.porto.mydiabetes.data;

/**
 * Created by Diogo on 12/05/2016.
 */
public class Advice implements Comparable<Advice> {

    private String notificationText;
    private String expandedText;
    private int urgency;

    public Advice(String notText, String expText, int urgency){
        this.expandedText = expText;
        this.notificationText = notText;
        this.urgency = urgency;
    }

    public String getExpandedText() {
        return expandedText;
    }

    public int getUrgency() {
        return urgency;
    }

    public String getNotificationText() {
        return notificationText;
    }

    @Override
    public int compareTo(Advice advice) {
        return (this.getUrgency()-advice.getUrgency());
    }
}
