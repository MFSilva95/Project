package pt.it.porto.mydiabetes.utils;

import android.util.Log;

import java.text.ParseException;
import java.util.Calendar;

/**
 * Created by Diogo on 12/07/2016.
 */
public class HomeElement {


    public enum Type{ADVICE, TASK, HEADER, SPACE, LOGITEM};
    private Type displayType;
    private String name;

    private Calendar dateTime;
    private String tag;
    private int recordID;
    private int carbs;
    private float insulinVal;
    private String insulinName;
    private int glycemia;
    private int carbsId;
    private int insulinId;
    private int glycemiaId;
    private boolean isPressed = false;

    public boolean isPressed(){
        return isPressed;
    }
    public void setPressed(boolean pressed){
        isPressed = pressed;
    }

    public HomeElement(Type type){
        this.displayType = type;
    }
    public HomeElement(Type type, String name){
        this.displayType = type;
        this.name = name;
    }
    public HomeElement(String dateTime, int recordId, String tag, int carbs, float insulinVal, String insulinName, int glycemia, int carbsId, int insulinId, int glycemiaId) {
        this.displayType = Type.LOGITEM;

        try {
            this.dateTime = DateUtils.parseDateTime(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.recordID = recordId;
        this.tag = tag;
        this.carbs = carbs;
        this.insulinVal = insulinVal;
        this.insulinName = insulinName;
        this.glycemia = glycemia;
        this.carbsId = carbsId;
        this.insulinId = insulinId;
        this.glycemiaId = glycemiaId;
    }

    public Type getDisplayType() {
        return displayType;
    }

    public String getName() {
        return name;
    }

    public String getFormattedDate() {
        return DateUtils.getFormattedDate(dateTime);
    }

    public String getFormattedTime() {
        return DateUtils.getFormattedTime(dateTime);
        //return DateUtils.getFormattedTimeSec(dateTime);
    }

    public String getTag() {
        return tag;
    }

    public int getCarbs() {
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
