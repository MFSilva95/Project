package pt.it.porto.mydiabetes.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static final String ISO8601_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String ISO8601_FORMAT_SECONDS = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FORMAT_DB = "dd-MM-yyyy";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String TIME_FORMAT_SECONDS = "HH:mm:ss";
    public static final SimpleDateFormat iso8601Format = new SimpleDateFormat(ISO8601_FORMAT, LocaleUtils.ENGLISH_LOCALE);
    public static final SimpleDateFormat iso8601FormatSeconds = new SimpleDateFormat(ISO8601_FORMAT_SECONDS, LocaleUtils.ENGLISH_LOCALE);
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, LocaleUtils.ENGLISH_LOCALE);
    public static final SimpleDateFormat dateFormatDb = new SimpleDateFormat(DATE_FORMAT_DB, LocaleUtils.ENGLISH_LOCALE);
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, LocaleUtils.ENGLISH_LOCALE);
    public static final SimpleDateFormat timeFormatSeconds = new SimpleDateFormat(TIME_FORMAT_SECONDS, LocaleUtils.ENGLISH_LOCALE);

    public static Calendar parseDateTime(String dateTime) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        Date date;
        if (dateTime.length() == ISO8601_FORMAT.length()) {
            date = iso8601Format.parse(dateTime);
        } else {
            date = iso8601FormatSeconds.parse(dateTime);
        }
        calendar.setTime(date);
        return calendar;
    }

    public static String formatToDb(Calendar calendar) {
        return iso8601FormatSeconds.format(calendar.getTime());
    }


    public static String getFormattedDate(Calendar calendar) {
        return dateFormat.format(calendar.getTime());
    }

    /**
     * //TODO change to throw exception
     *
     * @param date
     * @return null if unable to parse
     */
    public static Calendar getDateCalendar(String date) {
        Calendar calendar = Calendar.getInstance();
        try {
            if(date.indexOf('-')==4) {
                calendar.setTime(dateFormat.parse(date));
            } else {
                calendar.setTime(dateFormatDb.parse(date));
            }
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * //TODO change to throw exception
     *
     * @param date
     * @return null if unable to parse
     */
    public static Calendar getTimeCalendar(String date) {
        Calendar calendar = Calendar.getInstance();
        try {
            if (date.length() == TIME_FORMAT.length()) {
                calendar.setTime(timeFormat.parse(date));
            } else {
                calendar.setTime(timeFormatSeconds.parse(date));
            }
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFormattedTime(Calendar calendar) {
        return timeFormat.format(calendar.getTime());
    }

    public static String getFormattedTimeSec(Calendar calendar) {
        return timeFormatSeconds.format(calendar.getTime());
    }

    public static Calendar getDateTime(String date, String time) throws ParseException {
        return parseDateTime(date + " " + time);
    }

    /**
     * Compares two calendars and returns if they are the same ignoring the seconds
     *
     * @return
     */
    public static boolean isSameTime(Calendar current, Calendar next) {
        if (current == null && next != null || current != null && next == null) {
            return false;
        }
        if (current == null && next == null) {
            return true;
        }
        return current.get(Calendar.YEAR) == next.get(Calendar.YEAR) &&
                current.get(Calendar.MONTH) == next.get(Calendar.MONTH) &&
                current.get(Calendar.DAY_OF_MONTH) == next.get(Calendar.DAY_OF_MONTH) &&
                current.get(Calendar.HOUR_OF_DAY) == next.get(Calendar.HOUR_OF_DAY) &&
                current.get(Calendar.MINUTE) == next.get(Calendar.MINUTE);
    }

    public static Calendar getCalendar(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return calendar;
    }

    public static String getFormatedDateTime(Calendar calendar) {
        return iso8601Format.format(calendar.getTime());
    }

    public static Calendar parseDate(String string) throws ParseException {
        Calendar result=Calendar.getInstance();
        result.setTime(dateFormat.parse(string));
        return result;
    }

    public static int getAge(Calendar dob){
        Calendar today = Calendar.getInstance();


        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = age;

        return ageInt;
    }
}
