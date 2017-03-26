package pt.it.porto.mydiabetes.data;

import java.text.ParseException;
import java.util.Calendar;

import pt.it.porto.mydiabetes.utils.DateUtils;

/**
 * Created by parra on 26/03/2017.
 */

public class LogBookEntry {
    private Calendar dateTime;
    private String tag;
    private float carbs;
    private float insulinVal;
    private String insulinName;
    private int glycemia;
    private int carbsId;
    private int insulinId;
    private int glycemiaId;

    public LogBookEntry(String dateTime, String tag, float carbs, float insulinVal, String insulinName, int glycemia, int carbsId, int insulinId, int glycemiaId) {

        try {
            this.dateTime = DateUtils.parseDateTime(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.tag = tag;
        this.carbs = carbs;
        this.insulinVal = insulinVal;
        this.insulinName = insulinName;
        this.glycemia = glycemia;
        this.carbsId = carbsId;
        this.insulinId = insulinId;
        this.glycemiaId = glycemiaId;
    }

    public String getFormattedDate() {
        return DateUtils.getFormattedDate(dateTime);
    }

    public String getFormattedTime() {
        return DateUtils.getFormattedTime(dateTime);
    }

    public String getTag() {
        return tag;
    }

    public float getCarbs() {
        return carbs;
    }

    public float getInsulinVal() {
        return insulinVal;
    }

    public String getInsulinName() {
        return insulinName;
    }

    public int getGlycemia() {
        return glycemia;
    }

    public int getCarbsId() {
        return carbsId;
    }

    public int getInsulinId() {
        return insulinId;
    }

    public int getGlycemiaId() {
        return glycemiaId;
    }



}
