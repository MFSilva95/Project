package pt.it.porto.mydiabetes.utils;

import java.text.ParseException;
import java.util.Calendar;

/**
 * Created by Diogo on 12/07/2016.
 */
public class HomeElement {


    public enum Type{ADVICE, TASK, HEADER, SPACE, LOGITEM}
    private Type displayType;
    private String name;

    private Calendar dateTime;
    private String tag_name;
    private int tag_id;
    private int recordID;
    private int carbs;
    private float insulinVal;
    private String insulinName;
    private int glycemia;
    private int carbsId;
    private int insulinId;
    private int glycemiaId;
    private int note_id;

    private boolean isPressed = false;

    public boolean isPressed(){
        return isPressed;
    }
    public int getNote_id(){return note_id;}
    public void setPressed(boolean pressed){
        isPressed = pressed;
    }
    public void setTag_id(int tag){
        this.tag_id = tag;
    }
    public void setTag_name(String tag){
        this.tag_name = tag;
    }

    public HomeElement(Type type){
        this.displayType = type;
    }
    public HomeElement(Type type, String name){
        this.displayType = type;
        this.name = name;
    }
    public HomeElement( int recordId, String dateTime, int tag, int carbs, float insulinVal, String insulinName, int glycemia, int carbsId, int insulinId, int glycemiaId, int note_id) {
        this.displayType = Type.LOGITEM;

        try {
            this.dateTime = DateUtils.parseDateTime(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.recordID = recordId;
        this.tag_id = tag;
        this.carbs = carbs;
        this.insulinVal = insulinVal;
        this.insulinName = insulinName;
        this.glycemia = glycemia;
        this.carbsId = carbsId;
        this.insulinId = insulinId;
        this.glycemiaId = glycemiaId;
        this.note_id = note_id;
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

    public int getRecordID(){return recordID;}
    public int getTag_id() {
        return tag_id;
    }
    public String getTag_name() {
        return tag_name;
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
