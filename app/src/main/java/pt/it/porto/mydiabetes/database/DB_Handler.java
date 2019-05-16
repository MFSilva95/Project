package pt.it.porto.mydiabetes.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import pt.it.porto.mydiabetes.R;


public class DB_Handler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 19;//17
    private static final int DATABASE_VERSION_USERID_BADGES = 8;
    private static final int DATABASE_VERSION_TARGET_BG = 10;
    private static final int DATABASE_VERSION_TAG = 17;
    private static final int DATABASE_VERSION_V2 = 18;



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
    public void onCreate(SQLiteDatabase db) {

        if(db.getVersion()==0) {// empty or in need of update
            initDatabaseTables(db);//iniciar a nova bd
            initDayPhases(db); //iniciar as tags na nova bd
            insertDepricated(db,false);
        }
    }

    public void insertDepricated(SQLiteDatabase db, Boolean activated){
        Cursor result = db.query(MyDiabetesContract.Feature.TABLE_NAME, new String[]{MyDiabetesContract.Feature.COLUMN_NAME_NAME},
                MyDiabetesContract.Feature.COLUMN_NAME_NAME + "=?", new String[]{FeaturesDB.DEPRECATED}, null, null, null);
        if(result.getCount()==0){
            createFeature(db,FeaturesDB.DEPRECATED);
        }
        ContentValues toInsert = new ContentValues();
        toInsert.put(MyDiabetesContract.Feature.COLUMN_NAME_ACTIVATED, activated ? 1 : 0);
        db.update(MyDiabetesContract.Feature.TABLE_NAME, toInsert, MyDiabetesContract.Feature.COLUMN_NAME_NAME + "=?", new String[]{FeaturesDB.DEPRECATED});
    }

    private boolean old_backup_exists() {
        File file = new File(Environment.getExternalStorageDirectory()
                + "/MyDiabetes/backup/old_db_backup");
        return file.exists();
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
            File db_file = new File(Environment.getDataDirectory() + "/data/" + myContext.getPackageName()
                    + "/databases/"+ DB_Handler.getCurrentDbName());
            if(db_file.exists()) {
                File outputDir = new File(Environment.getExternalStorageDirectory()
                        + "/MyDiabetes/backup");
                outputDir.mkdirs();
                outputDir = new File(Environment.getExternalStorageDirectory()
                        + "/MyDiabetes/backup/old_db_backup");
                if(!outputDir.exists()){
                    try {
                        outputDir.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    copyFile(db_file, outputDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            resetDatabase(myDB);

            initDatabaseTables(myDB);//iniciar a nova bd
            initDayPhases(myDB); //iniciar as tags na nova bd
            if(old_backup_exists()){
                //there was an update to the database-the old database was deleted and this database being created needs to be marked as
                // deprecated to the home activity know that it needs to ask the user if it is their will to save the backup
                // checks is feature created
                Cursor result = myDB.query(MyDiabetesContract.Feature.TABLE_NAME, new String[]{MyDiabetesContract.Feature.COLUMN_NAME_NAME},
                        MyDiabetesContract.Feature.COLUMN_NAME_NAME + "=?", new String[]{FeaturesDB.DEPRECATED}, null, null, null, "1");
                if(result.getCount()==0){
                    createFeature(myDB, FeaturesDB.DEPRECATED);
                }
                ContentValues toInsert = new ContentValues();
                toInsert.put(MyDiabetesContract.Feature.COLUMN_NAME_ACTIVATED, 1);
                myDB.update(MyDiabetesContract.Feature.TABLE_NAME, toInsert, MyDiabetesContract.Feature.COLUMN_NAME_NAME + "=?", new String[]{FeaturesDB.DEPRECATED});
            }
        }else{
            ContentValues toInsert = new ContentValues();
            toInsert.put(MyDiabetesContract.Feature.COLUMN_NAME_ACTIVATED, 0);
            myDB.update(MyDiabetesContract.Feature.TABLE_NAME, toInsert, MyDiabetesContract.Feature.COLUMN_NAME_NAME + "=?", new String[]{FeaturesDB.DEPRECATED});
        }

    }



    public void resetDatabase(SQLiteDatabase db){
        String[] arrTblNames = {"Reg_Medication",     "Reg_Weight","Reg_A1c","Reg_Cholesterol",
                "Reg_Disease","Reg_BloodPressure","Reg_Exercise","Reg_Insulin","Reg_BloodGlucose",
                "Insulin", "Reg_CarboHydrate","BG_Target","Badges",
                "Points", "Activity_Log","Clicks_Log","Sensitivity_Reg","Ratio_Reg",
                          "Exercise","UserInfo","Note","Medicine","Disease",
                "Tag","Feature","Db_Info",
                "Sync_Images_Diff","Record"};

        for(String name: arrTblNames){
               // if(name.equals("sqlite_master")){continue;}
                db.execSQL("DROP TABLE IF EXISTS "+name);
            }

    }

    public void createFeature(SQLiteDatabase db, String feature) {
        ContentValues toInsert = new ContentValues();
        toInsert.put(MyDiabetesContract.Feature.COLUMN_NAME_NAME, feature);
        db.insert(MyDiabetesContract.Feature.TABLE_NAME,null, toInsert);
    }


    public Boolean isDBdeprecated(SQLiteDatabase db){
            Cursor result = db.query(MyDiabetesContract.Feature.TABLE_NAME, new String[]{MyDiabetesContract.Feature.COLUMN_NAME_ACTIVATED},
                    MyDiabetesContract.Feature.COLUMN_NAME_NAME + "=?", new String[]{FeaturesDB.DEPRECATED}, null, null, null, "1");
            if(result.getCount()==0){
                return true;
            }
            result.moveToFirst();
            return result.getInt(0) == 1;
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
        File backup = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes/backup/old_db_backup");
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

        //<item>Acordar</item>
        toInsert.put("Name", daytimes[0]);
        toInsert.put("TimeStart", "06:00");
        toInsert.put("TimeEnd", "07:30");
        db.insert("Tag", null, toInsert);

        //<item>Antes Peq. almoço</item>
        toInsert = new ContentValues();
        toInsert.put("Name", daytimes[1]);
        toInsert.put("TimeStart", "07:30");
        toInsert.put("TimeEnd", "09:00");
        db.insert("Tag", null, toInsert);

        //<item>Depois Peq. almoço</item>
        toInsert = new ContentValues();
        toInsert.put("Name", daytimes[2]);
        toInsert.put("TimeStart", "09:00");
        toInsert.put("TimeEnd", "10:30");
        db.insert("Tag", null, toInsert);

        //<item>Antes almoço</item>
        toInsert = new ContentValues();
        toInsert.put("Name", daytimes[3]);
        toInsert.put("TimeStart", "10:30");
        toInsert.put("TimeEnd", "13:00");
        db.insert("Tag", null, toInsert);

        //<item>Depois Almoço</item>
        toInsert = new ContentValues();
        toInsert.put("Name", daytimes[4]);
        toInsert.put("TimeStart", "13:00");
        toInsert.put("TimeEnd", "15:30");
        db.insert("Tag", null, toInsert);

        //<item>Lanche</item>
        toInsert = new ContentValues();
        toInsert.put("Name", daytimes[5]);
        toInsert.put("TimeStart", "15:30");
        toInsert.put("TimeEnd", "18:00");
        db.insert("Tag", null, toInsert);

        //<item>Antes jantar</item>
        toInsert = new ContentValues();
        toInsert.put("Name", daytimes[6]);
        toInsert.put("TimeStart", "18:00");
        toInsert.put("TimeEnd", "20:30");
        db.insert("Tag", null, toInsert);

        //<item>Depois jantar</item>
        toInsert = new ContentValues();
        toInsert.put("Name", daytimes[7]);
        toInsert.put("TimeStart", "20:30");
        toInsert.put("TimeEnd", "22:00");
        db.insert("Tag", null, toInsert);

        //<item>Deitar</item>
        toInsert = new ContentValues();
        toInsert.put("Name", daytimes[8]);
        toInsert.put("TimeStart", "22:00");
        toInsert.put("TimeEnd", "01:00");
        db.insert("Tag", null, toInsert);

        //<item>Noite</item>
        toInsert = new ContentValues();
        toInsert.put("Name", daytimes[9]);
        toInsert.put("TimeStart", "01:00");
        toInsert.put("TimeEnd", "06:00");
        db.insert("Tag", null, toInsert);


//        toInsert = new ContentValues();
//        toInsert.put("Name", daytimes[10]);
//        toInsert.put("TimeStart", "01:00");
//        toInsert.put("TimeEnd", "06:00");
//        db.insert("Tag", null, toInsert);
    }
}







