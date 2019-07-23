package pt.it.porto.mydiabetes.utils;

/**
 * Created by Diogo on 12/07/2016.
 */
public class RawRecord {

    private String dateTime;
    private int tag;
    private int id_carbs;
    private int id_insulin;
    private int id;
    private int id_bloodglucose;
    private int id_note;


    public int get_tag() {
        return tag;
    }

    public int getId_carbs() {
        return id_carbs;
    }

    public int getId_insulin() {
        return id_insulin;
    }

    public int getId_bloodglucose() {
        return id_bloodglucose;
    }

    public int getId_note() {
        return id_note;
    }
    public int getId(){return id;}




    public RawRecord(int id, String dateTime, int tag, int id_carbs, int id_insulin, int id_bloodglucose, int id_note) {

        this.dateTime = dateTime;
        this.id = id;
        this.tag = tag;
        this.id_carbs = id_carbs;
        this.id_insulin = id_insulin;
        this.id_bloodglucose = id_bloodglucose;
        this.id_note = id_note;
    }

    public String getFormattedDate() {
        return dateTime;
    }

    public String getFormattedTime() {
        //return DateUtils.getFormattedTime(dateTime);
        return dateTime;
    }

    @Override
    public String toString() {
        return "RawRecord{" +
                //"dateTime=" + dateTime.toString() +
                ", tag='" + tag + '\'' +
                ", id_carbs=" + id_carbs +
                ", id_insulin=" + id_insulin +
                ", id=" + id +
                ", id_bloodglucose=" + id_bloodglucose +
                ", id_note=" + id_note +
                '}';
    }
}
