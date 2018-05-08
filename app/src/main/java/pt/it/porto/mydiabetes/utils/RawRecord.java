package pt.it.porto.mydiabetes.utils;

import java.text.ParseException;
import java.util.Calendar;

/**
 * Created by Diogo on 12/07/2016.
 */
public class RawRecord {

    private Calendar dateTime;
    private String tag;
    private int id_carbs;
    private int id_insulin;

    public String get_tag() {
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

    private int id_bloodglucose;
    private int id_note;


    public RawRecord(String dateTime, String tag, int id_carbs, int id_insulin, int id_bloodglucose, int id_note) {
        try {
            this.dateTime = DateUtils.parseDateTime(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.tag = tag;
        this.id_carbs = id_carbs;
        this.id_insulin = id_insulin;
        this.id_bloodglucose = id_bloodglucose;
        this.id_note = id_note;
    }

    public String getFormattedDate() {
        return DateUtils.getFormattedDate(dateTime);
    }

    public String getFormattedTime() {
        return DateUtils.getFormattedTime(dateTime);
    }

}
