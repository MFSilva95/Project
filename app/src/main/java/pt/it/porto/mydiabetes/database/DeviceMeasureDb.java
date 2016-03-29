package pt.it.porto.mydiabetes.database;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Calendar;

import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.utils.DateUtils;

public class DeviceMeasureDb {


    private MyDiabetesStorage storage;

    public DeviceMeasureDb(MyDiabetesStorage storage) {
        this.storage = storage;
    }


    public int saveGlycemiaRec(GlycemiaRec record) {
        record.setIdTag(getTagId(record.getDateTime()));

        // check if already exists
        Calendar datetime = record.getDateTime();
        Cursor cursor = storage.rawQuery("SELECT * FROM Reg_BloodGlucose WHERE DateTime == '" + DateUtils.formatToDb(datetime) + "';");
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            // value exists, check if value is the same
            int storedValue = cursor.getInt(2);
            if (storedValue == record.getValue()) {
                // record already in the database, just exit
                cursor.close();
                return -1;
            }
        }
        cursor.close();

        // storage record in database
        ContentValues toInsert = new ContentValues();
        toInsert.put("Id_User", record.getIdUser());
        toInsert.put("Value", record.getValue());
        toInsert.put("DateTime", DateUtils.formatToDb(record.getDateTime()));
        toInsert.put("Id_Tag", record.getIdTag());
        return (int) storage.insertNewData(MyDiabetesContract.Regist.BloodGlucose.TABLE_NAME, toInsert);
    }

    private int getTagId(Calendar calendar) {
        String dateTime = DateUtils.formatToDb(calendar);
        Cursor cursor = storage.rawQuery("SELECT * FROM Tag WHERE  " + "(TimeStart < TimeEnd AND '" + dateTime + "' >= TimeStart AND '" + dateTime + "' <= TimeEnd)" +
                "OR " + "(TimeStart > TimeEnd AND('" + dateTime + "' >= TimeStart OR '" + dateTime + "' <= TimeEnd ));");
        int id;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(0);
        } else {
            cursor = storage.rawQuery("SELECT * FROM Tag WHERE  " + "TimeStart IS NULL AND TimeEnd IS NULL ;");
            cursor.moveToFirst();
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }
}
