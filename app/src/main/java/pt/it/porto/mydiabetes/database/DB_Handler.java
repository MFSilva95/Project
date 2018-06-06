package pt.it.porto.mydiabetes.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.BadgeRec;
import pt.it.porto.mydiabetes.data.BloodPressureRec;
import pt.it.porto.mydiabetes.data.CarbsRec;
import pt.it.porto.mydiabetes.data.CholesterolRec;
import pt.it.porto.mydiabetes.data.DiseaseRec;
import pt.it.porto.mydiabetes.data.ExerciseRec;
import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.data.Insulin;
import pt.it.porto.mydiabetes.data.InsulinRec;
import pt.it.porto.mydiabetes.data.PointsRec;
import pt.it.porto.mydiabetes.data.Tag;
import pt.it.porto.mydiabetes.data.TargetBGRec;
import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.data.WeightRec;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;

import static pt.it.porto.mydiabetes.ui.activities.Home.setOld_db;

public class DB_Handler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 19;//17
    private static final int DATABASE_VERSION_USERID_BADGES = 8;
    private static final int DATABASE_VERSION_TARGET_BG = 10;
    private static final int DATABASE_VERSION_TAG = 17;
    private static final int DATABASE_VERSION_V2 = 18;


    // Database Name
    private static final String DATABASE_NAME = "DB_Diabetes";
    private static final String DATABASE_NAME_V2 = "DB_Diabetes_v2";
    private static String currentDB_name;

    // Context to be accessible from any method
    private Context myContext;
    private SQLiteDatabase old_db;


    public DB_Handler(Context context) {
//        super(context, DATABASE_VERSION <= DATABASE_VERSION_V2 ? DATABASE_NAME_V2 : DATABASE_NAME, null, DATABASE_VERSION);
        super(context, DATABASE_NAME_V2, null, DATABASE_VERSION);

        currentDB_name = DATABASE_NAME_V2;
        this.myContext = context;
        //Log.i(TAG, "-------------DB_HANDLE!: NAME: "+getDatabaseName());
    }

    public static String getCurrentDbName(){
        return currentDB_name;
    }
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if(db.getVersion()==0){// empty or in need of update
            old_db = myContext.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
            DB_Read db_read = new DB_Read(old_db);
            if(db_read.isEmpty()){
                initDatabaseTables(db);
            }else{
                onUpgrade(db,db.getVersion(),DATABASE_VERSION);
            }
            db_read.close();
            old_db.close();
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
            //change flag
            setOld_db(true);
//            FeaturesDB db = new FeaturesDB(MyDiabetesStorage.getInstance(myContext));
//            db.changeFeatureStatus(FeaturesDB.OLD_DB_VERSION, true);
        }
    }

    private void insertIntoDB(SQLiteDatabase myDB, SQLiteDatabase old) throws Exception{
        String TAG = "cenas";
        if(old==null){
            Log.i(TAG, "onUpgrade: -> old is null");
            old = myContext.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null); //old database
        }


        DB_Read oldReads = new DB_Read(old);
        UserInfo basic_info = oldReads.MyData_Read();
        ArrayList<Insulin> old_insulins = oldReads.Insulins_GetAll();
        ArrayList<InsulinRec> old_insu_recs = oldReads.InsulinRec_GetAll();
        ArrayList<CarbsRec> old_carbs_recs = oldReads.CarbsRec_GetAll();
        ArrayList<GlycemiaRec> old_gly_recs = oldReads.GlycemiaRec_GetAll();
        ArrayList<TargetBGRec> old_targetBG_recs = oldReads.TargetBG_GetAll();
        ArrayList<BadgeRec> old_medals_recs = oldReads.getAllMedals();

        ArrayList<PointsRec> old_points_recs = oldReads.PointsReg_GetAll();

        ArrayList<WeightRec> old_weight_recs = oldReads.Weight_GetAll();
        ArrayList<ExerciseRec> old_exercise_recs = oldReads.ExerciseReg_GetAll();
        ArrayList<BloodPressureRec> old_BP_recs = oldReads.BloodPressure_GetAll();
        ArrayList<CholesterolRec> old_chol_recs = oldReads.Cholesterol_GetAll();
        ArrayList<DiseaseRec> old_disease_recs = oldReads.DiseaseReg_GetAll();



        oldReads.close();
        old.close();



        DB_Read db_read = new DB_Read(myDB);
        if(db_read.isEmpty()){
            Log.i(TAG, "DATABASE EMPTY");
            initDatabaseTables(myDB);//initialize new database
        }


        DB_Write newWrites = new DB_Write(myDB);
        if (basic_info != null) {
            newWrites.MyData_Save(basic_info);
        }

        if (old_points_recs != null) {
            for (PointsRec rec : old_points_recs) {
                newWrites.Point_Save(rec);
            }
        }

        //            verify if points were initialized
        if( db_read.Points_get_num_reg()<=0){
            LevelsPointsUtils.addPoints(myContext,0,"first", db_read);
        }

        if (old_insulins != null) {
            for (Insulin rec : old_insulins) {
                newWrites.Insulin_Add(rec);
            }
        }
        if (old_targetBG_recs != null) {
            for (TargetBGRec rec : old_targetBG_recs) {
                newWrites.TargetBG_Add(rec);
            }
        }
        if (old_gly_recs != null) {
            for (GlycemiaRec rec : old_gly_recs) {
                newWrites.Glycemia_Save(rec);
            }
        }
        if (old_insu_recs != null) {
            for (InsulinRec rec : old_insu_recs) {
                newWrites.Insulin_Save(rec);
            }
        }

        if (old_carbs_recs != null) {
            for (CarbsRec rec : old_carbs_recs) {
                newWrites.Carbs_Save(rec);
            }
        }
        if (old_medals_recs != null) {
            for (BadgeRec rec : old_medals_recs) {
                newWrites.Badge_Save(rec);
            }
        }
        if (old_BP_recs != null) {
            for (BloodPressureRec rec : old_BP_recs) {
                newWrites.BloodPressure_Save(rec);
            }
        }
        if (old_weight_recs != null) {
            for (WeightRec rec : old_weight_recs) {
                newWrites.Weight_Save(rec);
            }
        }
        if (old_exercise_recs != null) {
            for (ExerciseRec rec : old_exercise_recs) {
                newWrites.Exercise_Save(rec);
            }
        }
        if (old_chol_recs != null) {
            for (CholesterolRec rec : old_chol_recs) {
                newWrites.Cholesterol_Save(rec);
            }
        }
        if (old_disease_recs != null) {
            for (DiseaseRec rec : old_disease_recs) {
                newWrites.DiseaseReg_Save(rec);
            }
        }


        ContentValues toInsert = new ContentValues();
        toInsert.put("Name", FeaturesDB.INITIAL_REG_DONE);
        toInsert.put("Activated", 1);
        newWrites.addFeature(toInsert);
        newWrites.close();
        myContext.deleteDatabase(DATABASE_NAME);
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


    public void insertIntoDB(File inputFile) throws Exception{
        SQLiteDatabase db = SQLiteDatabase.openDatabase(inputFile.getPath(), null, 0);
        DB_Handler dbwrite = new DB_Handler(this.myContext);
        insertIntoDB(dbwrite.getWritableDatabase(),db);
        Log.i("cenas", "insertIntoDB: DONE");
    }
}
