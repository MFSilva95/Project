package pt.it.porto.mydiabetes.utils;

import android.util.Log;

import java.text.ParseException;
import java.util.Calendar;

public class HomeElement {


    public enum Type{ADVICE, TASK, HEADER, SPACE, DAY};
    private Type displayType;
    private String name;

    public HomeElement(Type type){
        this.displayType = type;
    }
    public HomeElement(Type type, String name){
        this.displayType = type;
        this.name = name;
    }

    public Type getDisplayType() {
        return displayType;
    }

    public String getName() {
        return name;
    }

}
