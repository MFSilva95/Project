package pt.it.porto.mydiabetes.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.esafirm.imagepicker.view.SquareFrameLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.utils.DateUtils;


public class DB_Handler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 19;//17
    private static final int DATABASE_VERSION_USERID_BADGES = 8;
    private static final int DATABASE_VERSION_TARGET_BG = 10;
    private static final int DATABASE_VERSION_TAG = 17;
    private static final int DATABASE_VERSION_V2 = 18;
    private static boolean could_not_backup = false;


    // Database Name
    private static final String DATABASE_NAME = "DB_Diabetes";


    // Context to be accessible from any method
    private Context myContext;


    public DB_Handler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }

    public static String getCurrentDbName(){
        return DATABASE_NAME;
    }
    public static int getCurrentDbVersion(){
        return DATABASE_VERSION;
    }

    public static String getOldDbName(){
        return DATABASE_NAME;
    }
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if(db.getVersion()==0){// empty or in need of update
            initDatabaseTables(db);//iniciar a nova bd
            initDayPhases(db); //iniciar as tags na nova bd


            try {
                Cursor cursor = db.rawQuery("SELECT NotDeprecated FROM Db_Info ", null);
                cursor.moveToFirst();
                ContentValues toInsert;
                String version;
                switch (cursor.getColumnIndex("NotDeprecated")) {
                    case -1: //column does not exist
                        db.execSQL("ALTER TABLE Db_Info ADD COLUMN NotDeprecated INTEGER");
                        version = myContext.getPackageManager().getPackageInfo(myContext.getPackageName(), 0).versionName;
                        toInsert = new ContentValues();
                        toInsert.put("NotDeprecated", 1);
                        toInsert.put(MyDiabetesContract.DbInfo.COLUMN_NAME_VERSION, version);
                        toInsert.put(MyDiabetesContract.DbInfo.COLUMN_NAME_DATETIME, DateUtils.formatToDb(Calendar.getInstance()));
                        db.insert("Db_Info", null, toInsert);
                        break;
                    case 0: //column exists but has no values
                        switch (cursor.getCount()){
                            case 0:
                                version = myContext.getPackageManager().getPackageInfo(myContext.getPackageName(), 0).versionName;
                                toInsert = new ContentValues();
                                toInsert.put("NotDeprecated", 1);
                                toInsert.put(MyDiabetesContract.DbInfo.COLUMN_NAME_VERSION, version);
                                toInsert.put(MyDiabetesContract.DbInfo.COLUMN_NAME_DATETIME, DateUtils.formatToDb(Calendar.getInstance()));
                                db.insert("Db_Info", null, toInsert);
                                break;
                            case 1:
                                ContentValues toUpdate = new ContentValues();
                                toUpdate.put("NotDeprecated", 1);
                                db.update("Db_Info", toUpdate, null, null);
                                break;
                        }
                        break;
                }
            }catch (Exception e){
                Log.i("rawr", "onCreate: BOOM!");
            }
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("DB Downgrade", "Nothing to downgrade");
        return;
    }

    /**
     * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
     */

    @Override
    public void onUpgrade(SQLiteDatabase myDB, int oldVersion, int newVersion) {
        if(oldVersion<=18){
            //old database
            //get database
            File db_file = new File(Environment.getDataDirectory() + "/data/" + myContext.getPackageName() + "/databases/"+ DB_Handler.getCurrentDbName());
            if(db_file.exists()) {

                try {
                    Cursor cursor = myDB.rawQuery("SELECT NotDeprecated FROM Db_Info ", null);
                    cursor.moveToFirst();
                    ContentValues toInsert;
                    String version;
                    switch (cursor.getColumnIndex("NotDeprecated")) {
                        case -1: //column does not exist
                            myDB.execSQL("ALTER TABLE Db_Info ADD COLUMN NotDeprecated INTEGER");
                            version = myContext.getPackageManager().getPackageInfo(myContext.getPackageName(), 0).versionName;
                            toInsert = new ContentValues();
                            toInsert.put("NotDeprecated", 0);
                            toInsert.put(MyDiabetesContract.DbInfo.COLUMN_NAME_VERSION, version);
                            toInsert.put(MyDiabetesContract.DbInfo.COLUMN_NAME_DATETIME, DateUtils.formatToDb(Calendar.getInstance()));
                            myDB.insert("Db_Info", null, toInsert);
                            break;
                        case 0: //column exists but has no values
                            switch (cursor.getCount()){
                                case 0:
                                    version = myContext.getPackageManager().getPackageInfo(myContext.getPackageName(), 0).versionName;
                                    toInsert = new ContentValues();
                                    toInsert.put("NotDeprecated", 0);
                                    toInsert.put(MyDiabetesContract.DbInfo.COLUMN_NAME_VERSION, version);
                                    toInsert.put(MyDiabetesContract.DbInfo.COLUMN_NAME_DATETIME, DateUtils.formatToDb(Calendar.getInstance()));
                                    myDB.insert("Db_Info", null, toInsert);
                                    break;
                                case 1:
                                    ContentValues toUpdate = new ContentValues();
                                    toUpdate.put("NotDeprecated", 0);
                                    myDB.update("Db_Info", toUpdate, null, null);
                                    break;
                            }
                            break;
                    }
                    File outputDir = new File(Environment.getDataDirectory() + "/data/" + myContext.getPackageName() + "/databases/old_db_backup");
                    outputDir.mkdirs();
                    try {
                        copyFile(db_file, outputDir);
                    } catch (IOException e) {
                        could_not_backup = true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }else{
            could_not_backup = true;
        }
        myContext.deleteDatabase(DATABASE_NAME);
    }

    public Boolean notDBdeprecated(SQLiteDatabase db){

        try{
            boolean deprecated;
            Cursor cursor = db.rawQuery("SELECT NotDeprecated FROM Db_Info", null);
            cursor.moveToFirst();
            ContentValues toInsert;
            String version;
            switch (cursor.getColumnIndex("NotDeprecated")) {
                case -1: //column does not exist
                    db.execSQL("ALTER TABLE Db_Info ADD COLUMN NotDeprecated INTEGER");
                    version = myContext.getPackageManager().getPackageInfo(myContext.getPackageName(), 0).versionName;
                    toInsert = new ContentValues();
                    toInsert.put("NotDeprecated", 0);
                    toInsert.put(MyDiabetesContract.DbInfo.COLUMN_NAME_VERSION, version);
                    toInsert.put(MyDiabetesContract.DbInfo.COLUMN_NAME_DATETIME, DateUtils.formatToDb(Calendar.getInstance()));
                    db.insert("Db_Info", null, toInsert);
                    return false;
                case 0: //column exists but has no values
                    if(cursor.getCount()==0){
                        version = myContext.getPackageManager().getPackageInfo(myContext.getPackageName(), 0).versionName;
                        toInsert = new ContentValues();
                        toInsert.put("NotDeprecated", 0);
                        toInsert.put(MyDiabetesContract.DbInfo.COLUMN_NAME_VERSION, version);
                        toInsert.put(MyDiabetesContract.DbInfo.COLUMN_NAME_DATETIME, DateUtils.formatToDb(Calendar.getInstance()));
                        db.insert("Db_Info", null, toInsert);
                        return false;
                    }else{
                        deprecated = (cursor.getInt(cursor.getColumnIndex("NotDeprecated")) == 1);
                        cursor.close();
                        return deprecated;
                    }
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

    private static void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }
    public void deleteOldBackup(){
        File backup = new File(Environment.getDataDirectory() + "/data/" + myContext.getPackageName() + "/databases/old_db_backup");
        Log.i("RAWR", "deleteOldBackup_PATH: "+ Environment.getDataDirectory() + "/data/" + myContext.getPackageName() + "/databases/old_db_backup");
        if(backup.exists()){
            backup.delete();
        }
    }



    private void initDatabaseTables(SQLiteDatabase db) {
        try {
            BufferedReader localBufferedReader =
                    new BufferedReader(new InputStreamReader(this.myContext.getAssets().open("DataBase.sql"), "UTF-8"));
            String str;
            while ((str = localBufferedReader.readLine()) != null) {
                try {
                    db.execSQL(str);
                } catch (Exception localException) {
                    Log.i("sql", str);
                    Log.e("Error", localException.getMessage());
                }
            }
        } catch (Exception e) {
            Log.d("Erro", e.toString());
            e.printStackTrace();
        }
    }
    private void initDayPhases(SQLiteDatabase db) {
        Resources res = this.myContext.getResources();
        String[] daytimes = res.getStringArray(R.array.daytimes);

        ContentValues toInsert = new ContentValues();
        toInsert.put("Name", daytimes[0]);
        toInsert.put("TimeStart", "06:00");
        toInsert.put("TimeEnd", "07:30");
        db.insert("Tag", null, toInsert);

        toInsert = new ContentValues();
        toInsert.put("Name", daytimes[1]);
        toInsert.put("TimeStart", "07:30");
        toInsert.put("TimeEnd", "09:00");
        db.insert("Tag", null, toInsert);

        toInsert = new ContentValues();
        toInsert.put("Name", daytimes[2]);
        toInsert.put("TimeStart", "09:00");
        toInsert.put("TimeEnd", "10:30");
        db.insert("Tag", null, toInsert);

        toInsert = new ContentValues();
        toInsert.put("Name", daytimes[3]);
        toInsert.put("TimeStart", "10:30");
        toInsert.put("TimeEnd", "13:00");
        db.insert("Tag", null, toInsert);

        toInsert = new ContentValues();
        toInsert.put("Name", daytimes[4]);
        toInsert.put("TimeStart", "13:00");
        toInsert.put("TimeEnd", "15:30");
        db.insert("Tag", null, toInsert);

        toInsert = new ContentValues();
        toInsert.put("Name", daytimes[5]);
        toInsert.put("TimeStart", "15:30");
        toInsert.put("TimeEnd", "18:00");
        db.insert("Tag", null, toInsert);

        toInsert = new ContentValues();
        toInsert.put("Name", daytimes[6]);
        toInsert.put("TimeStart", "18:00");
        toInsert.put("TimeEnd", "20:30");
        db.insert("Tag", null, toInsert);

        toInsert = new ContentValues();
        toInsert.put("Name", daytimes[7]);
        toInsert.put("TimeStart", "20:30");
        toInsert.put("TimeEnd", "22:00");
        db.insert("Tag", null, toInsert);

        toInsert = new ContentValues();
        toInsert.put("Name", daytimes[8]);
        toInsert.put("TimeStart", "22:00");
        toInsert.put("TimeEnd", "00:00");
        db.insert("Tag", null, toInsert);

        toInsert = new ContentValues();
        toInsert.put("Name", daytimes[9]);
        toInsert.put("TimeStart", "00:00");
        toInsert.put("TimeEnd", "06:00");
        db.insert("Tag", null, toInsert);

//        toInsert = new ContentValues();
//        toInsert.put("Name", daytimes[10]);
//        db.insert("Tag", null, toInsert);
    }
}
