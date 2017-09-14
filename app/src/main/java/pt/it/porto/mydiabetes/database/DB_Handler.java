package pt.it.porto.mydiabetes.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.BadgeRec;
import pt.it.porto.mydiabetes.data.CarbsRec;
import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.data.Insulin;
import pt.it.porto.mydiabetes.data.InsulinRec;
import pt.it.porto.mydiabetes.data.Tag;
import pt.it.porto.mydiabetes.data.TargetBGRec;
import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.ui.views.GlycemiaObjetivesElement;
import pt.it.porto.mydiabetes.utils.DateUtils;

public class DB_Handler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 18;//17
    private static final int DATABASE_VERSION_USERID_BADGES = 8;
    private static final int DATABASE_VERSION_TARGET_BG = 10;
    private static final int DATABASE_VERSION_TAG = 17;
    private static final int DATABASE_VERSION_V2 = 18;


    // Database Name
    private static final String DATABASE_NAME = "DB_Diabetes";
    private static final String DATABASE_NAME_V2 = "DB_Diabetes_v2";

    // Context to be accessible from any method
    private Context myContext;
    private SQLiteDatabase old_db;


    public DB_Handler(Context context) {
//        super(context, DATABASE_VERSION <= DATABASE_VERSION_V2 ? DATABASE_NAME_V2 : DATABASE_NAME, null, DATABASE_VERSION);
        super(context, DATABASE_NAME_V2, null, DATABASE_VERSION);
        this.myContext = context;
        //Log.i(TAG, "-------------DB_HANDLE!: NAME: "+getDatabaseName());
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Log.i(TAG, "onCreate: PASSOU AKI lol...");
        //Log.i(TAG, "onCreate: lvl: "+DATABASE_VERSION+" -> "+db.getVersion());
        if(db.getVersion()==0){// empty or in need of update
            old_db = myContext.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
            DB_Read db_read = new DB_Read(old_db);
            if(db_read.isEmpty()){
                //Log.i(TAG, "onCreate: EMPTY BOTH");
                initDatabaseTables(db);//initialize new database
                initDayPhases(db);
            }else{
                //Log.i(TAG, "onCreate: EMPTY UPGRADE FROM EXISTING");
                onUpgrade(db,db.getVersion(),DATABASE_VERSION);
            }
            db_read.close();
            old_db.close();
        }
//        if(DATABASE_VERSION > DATABASE_VERSION_V2){
//            onUpgrade(db,db.getVersion(),DATABASE_VERSION);
//        }else{
//            initDatabaseTables(db);
//            initDayPhases(db);
//        }
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
        toInsert.put("TimeEnd", "22:30");
        db.insert("Tag", null, toInsert);

        toInsert = new ContentValues();
        toInsert.put("Name", daytimes[8]);
        toInsert.put("TimeStart", "22:30");
        toInsert.put("TimeEnd", "01:00");
        db.insert("Tag", null, toInsert);

        toInsert = new ContentValues();
        toInsert.put("Name", daytimes[9]);
        toInsert.put("TimeStart", "1:00");
        toInsert.put("TimeEnd", "06:00");
        db.insert("Tag", null, toInsert);

        toInsert = new ContentValues();
        toInsert.put("Name", daytimes[10]);
        db.insert("Tag", null, toInsert);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("DB Downgrade", "Nothing to downgrade");
        return;
    }

    /**
     * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
     */
    //String TAG = "TAG!";

    @Override
    public void onUpgrade(SQLiteDatabase myDB, int oldVersion, int newVersion) {
        //Log.i(TAG, "onUpgrade: IS USING: "+getDatabaseName());
        //Log.i(TAG, "onUpgrade: myDB name: "+myDB.getPath());
        insertIntoDB(myDB, null);
    }

    private void insertIntoDB(SQLiteDatabase myDB, SQLiteDatabase old){
        try {
            if(old==null){
                old = myContext.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null); //old database
            }

            //Log.i(TAG, "onUpgrade: -> "+(old != null));
            DB_Read oldReads = new DB_Read(old);
            UserInfo basic_info = oldReads.MyData_Read();
            ArrayList<Insulin> old_insulins = oldReads.Insulins_GetAll();
            ArrayList<InsulinRec> old_insu_recs = oldReads.InsulinRec_GetAll();
            ArrayList<CarbsRec> old_carbs_recs = oldReads.CarbsRec_GetAll();
            ArrayList<GlycemiaRec> old_gly_recs = oldReads.GlycemiaRec_GetAll();
            ArrayList<TargetBGRec> old_targetBG_recs = oldReads.TargetBG_GetAll();
            ArrayList<BadgeRec> old_medals_recs = oldReads.getAllMedals();
            oldReads.close();
            old.close();

            //personal info...
//			ArrayList<GlycemiaRec> old_weight_recs = oldReads.GlycemiaRec_GetAll();
//			ArrayList<GlycemiaRec> old_exercise_recs = oldReads.GlycemiaRec_GetAll();
//			ArrayList<GlycemiaRec> old_BP_recs = oldReads.GlycemiaRec_GetAll();
//			ArrayList<GlycemiaRec> old_chol_recs = oldReads.GlycemiaRec_GetAll();
//			ArrayList<GlycemiaRec> old_disease_recs = oldReads.GlycemiaRec_GetAll();


//            myDB = myContext.openOrCreateDatabase(DATABASE_NAME_V2, Context.MODE_PRIVATE, null); //old database
            DB_Read db_read = new DB_Read(myDB);
            if(db_read.isEmpty()){
                initDatabaseTables(myDB);//initialize new database
                initDayPhases(myDB);
            }
            //Log.d(TAG, "onUpgrade2: "+ DatabaseUtils.dumpCursorToString(myDB.rawQuery("SELECT * from  sqlite_sequence", null)));

            DB_Write newWrites = new DB_Write(myDB);
            if (basic_info != null) {
                newWrites.MyData_Save(basic_info);
                //Log.i(TAG, "USER ADD");
            }
            if (old_insulins != null) {
                for (Insulin rec : old_insulins) {
                    newWrites.Insulin_Add(rec);
                    //Log.i(TAG, "INSU ADDED: "+rec.toString());
                }
            }
            if (old_targetBG_recs != null) {
                for (TargetBGRec rec : old_targetBG_recs) {
                    newWrites.TargetBG_Add(rec);
                    //Log.i(TAG, "TARGET ADDED");
                }
            }
            if (old_gly_recs != null) {
                for (GlycemiaRec rec : old_gly_recs) {
                    newWrites.Glycemia_Save(rec);
                    //Log.i(TAG, "GLY_REC ADDED");
                }
            }
            if (old_insu_recs != null) {
                for (InsulinRec rec : old_insu_recs) {
                    newWrites.Insulin_Save(rec);
                    //Log.i(TAG, "INSU_REC ADDED");
                }
            }

            if (old_carbs_recs != null) {
                for (CarbsRec rec : old_carbs_recs) {
                    newWrites.Carbs_Save(rec);
                    //Log.i(TAG, "CARBS_REC ADDED");
                }
            }
            if (old_medals_recs != null) {
                for (BadgeRec rec : old_medals_recs) {
                    newWrites.Badge_Save(rec);
                    //Log.i(TAG, "BADGE_REC ADDED");
                }
            }

            ContentValues toInsert = new ContentValues();
            toInsert.put("Name", FeaturesDB.INITIAL_REG_DONE);
            toInsert.put("Activated", 1);
            newWrites.addFeature(toInsert);

            myContext.deleteDatabase(DATABASE_NAME);
            //Log.d(TAG, "onUpgrade3: "+ DatabaseUtils.dumpCursorToString(myDB.rawQuery("SELECT * from  sqlite_sequence", null)));
            //newWrites.close();

            Log.i("cenas", "onUpgrade: END");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("cenas", "onUpgrade: deu poopoo");
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

    public void insertIntoDB(File inputFile) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(inputFile.getPath(), null, 0);
        DB_Handler dbwrite = new DB_Handler(this.myContext);
        insertIntoDB(dbwrite.getWritableDatabase(),db);
    }
}


//
//
//
//	@Override
//	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		Log.i("DB Upgrade", "Upgrading DB: "+oldVersion+" to-> "+newVersion);
//
//		if(oldVersion>0){
//
//			String updateDATABASE_SENSITIVITY_DEPENDENCY = "CREATE TABLE IF NOT EXISTS Sensitivity_Reg(" +
//					"Id INTEGER PRIMARY KEY AUTOINCREMENT, "+
//					"Id_User INTEGER NOT NULL, " +
//					"Id_Tag INTEGER NOT NULL, "+
//					"Value REAL NOT NULL, " +
//					"Name TEXT NOT NULL, " +
//					"TimeStart DATETIME NOT NULL, " +
//					"TimeEnd DATETIME NOT NULL, " +
//					"FOREIGN KEY(Id_User) REFERENCES UserInfo(Id), " +
//					"FOREIGN KEY(Id_Tag) REFERENCES Tag(Id) );";
//
//			String updateDATABASE_RATIO_DEPENDENCY = "CREATE TABLE IF NOT EXISTS Ratio_Reg(" +
//					"Id INTEGER PRIMARY KEY AUTOINCREMENT, "+
//					"Id_User INTEGER NOT NULL, " +
//					"Id_Tag INTEGER NOT NULL, "+
//					"Value REAL NOT NULL, " +
//					"Name TEXT NOT NULL, " +
//					"TimeStart DATETIME NOT NULL, " +
//					"TimeEnd DATETIME NOT NULL, " +
//					"FOREIGN KEY(Id_User) REFERENCES UserInfo(Id), " +
//					"FOREIGN KEY(Id_Tag) REFERENCES Tag(Id) );";
//
//			String updateDATABASE_NOTE_BG_DEPENDENCY = "ALTER TABLE Reg_BloodGlucose rename to Reg_BloodGlucose_backup;\n" + //rename old table
//					"CREATE TABLE IF NOT EXISTS Reg_BloodGlucose (Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, Value REAL NOT NULL, DateTime DATETIME NOT NULL, Id_Tag INTEGER, Id_Note INTEGER, Target_BG REAL, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id), FOREIGN KEY(Id_Tag) REFERENCES Tag(Id), FOREIGN KEY (Id_Note) REFERENCES Note(Id) ON DELETE SET NULL);\n" +
//					"INSERT INTO Reg_BloodGlucose(id, id_user, value, datetime, id_tag, id_note, target_bg) SELECT b.*,u.id FROM Reg_BloodGlucose_backup as b, userinfo as u;\n" +//recover the old ones to the one table
//					"DROP TABLE 'Reg_BloodGlucose_backup';";
//			String updateDATABASE_NOTE_CARBS_DEPENDENCY = "ALTER TABLE Reg_CarboHydrate rename to Reg_CarboHydrate_backup;\n" + //rename old table
//					"CREATE TABLE IF NOT EXISTS Reg_CarboHydrate (Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, Value REAL NOT NULL, DateTime DATETIME NOT NULL, Id_Tag INTEGER, Id_Note INTEGER, Target_BG REAL, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id), FOREIGN KEY(Id_Tag) REFERENCES Tag(Id), FOREIGN KEY (Id_Note) REFERENCES Note(Id) ON DELETE SET NULL);\n" +
//					"INSERT INTO Reg_CarboHydrate(id, id_user, value, datetime, id_tag, id_note, target_bg) SELECT b.*,u.id FROM Reg_CarboHydrate_backup as b, userinfo as u;\n" +//recover the old ones to the one table
//					"DROP TABLE 'Reg_CarboHydrate_backup';";
//			String updateDATABASE_NOTE_INSU_DEPENDENCY = "ALTER TABLE Reg_Insulin rename to Reg_Insulin_backup;\n" + //rename old table
//					"CREATE TABLE IF NOT EXISTS Reg_Insulin (Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, Id_Insulin INTEGER NOT NULL, Id_BloodGlucose INTEGER, DateTime DATETIME NOT NULL, Target_BG REAL, Value REAL NOT NULL, Id_Tag INTEGER, Id_Note INTEGER, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id), FOREIGN KEY(Id_Insulin) REFERENCES Insulin(Id), FOREIGN KEY(Id_Tag) REFERENCES Tag(Id), FOREIGN KEY(Id_BloodGlucose) REFERENCES Reg_BloodGlucose(Id), FOREIGN KEY (Id_Note) REFERENCES Note(Id) ON DELETE SET NULL);\n" +
//					"INSERT INTO Reg_Insulin(id, id_user, value, datetime, id_tag, id_note, target_bg) SELECT b.*,u.id FROM Reg_Insulin_backup as b, userinfo as u;\n" +//recover the old ones to the one table
//					"DROP TABLE 'Reg_Insulin_backup';";
//
//			String updateDATABASE_TAG = "ALTER TABLE Tag rename to Tag_backup;\n" + //rename old table
//					"CREATE TABLE IF NOT EXISTS Tag (Id INTEGER PRIMARY KEY AUTOINCREMENT, Name Text NOT NULL, TimeStart DATETIME, TimeEnd DATETIME);\n" +
//					"DROP TABLE 'Tag_backup';";
//
//			String updateUSABILITY = "DROP TABLE IF EXISTS 'Usability_Clicks';\n" +
//					"DROP TABLE IF EXISTS 'Usability_Events';\n" +
//					"DROP TABLE IF EXISTS 'Usability_Flux';\n"+
//					"DROP TABLE IF EXISTS 'badges_backup';\n"+
//					"DROP TABLE IF EXISTS 'Reg_BloodGlucose_backup';\n"+
//					"DROP TABLE IF EXISTS 'Reg_CarboHydrate_backup';\n"+
//					"DROP TABLE IF EXISTS 'Reg_Insulin_backup';\n"+
//					"DROP TABLE IF EXISTS 'Tag_backup';\n";
//
//			try {
//				db.execSQL(updateUSABILITY);
//				Log.i("cenas", "onUpgrade: -> APAGOU?");
//			} catch (SQLException sqlE) {
//				Log.e("DB Update adding column", sqlE.getLocalizedMessage());
//			}
//			try {
//				db.execSQL(updateDATABASE_TAG);
//				initDayPhases(db);
//				Log.i("DB Upgrade", "Finished altering DB_ TAG");
//			} catch (SQLException sqlE) {
//				Log.e("DB Update adding column", sqlE.getLocalizedMessage());
//			}
//			try {
//				db.execSQL(updateDATABASE_RATIO_DEPENDENCY);
//				Log.i("DB Upgrade", "Finished altering DB_RATIO");
//			} catch (SQLException sqlE) {
//				Log.e("DB Update adding column", sqlE.getLocalizedMessage());
//			}
//			try {
//				db.execSQL(updateDATABASE_SENSITIVITY_DEPENDENCY);
//				Log.i("DB Upgrade", "Finished altering DB_SENSE");
//			} catch (SQLException sqlE) {
//				Log.e("DB Update adding column", sqlE.getLocalizedMessage());
//			}
//			try {
//				db.execSQL(updateDATABASE_NOTE_BG_DEPENDENCY);
//				Log.i("DB Upgrade", "Finished altering DB_BG");
//			} catch (SQLException sqlE) {
//				Log.e("DB Update adding column", sqlE.getLocalizedMessage());
//			}
//			try {
//				db.execSQL(updateDATABASE_NOTE_CARBS_DEPENDENCY);
//				Log.i("DB Upgrade", "Finished altering DB_CARBS");
//			} catch (SQLException sqlE) {
//				Log.e("DB Update adding column", sqlE.getLocalizedMessage());
//			}
//			try {
//				db.execSQL(updateDATABASE_NOTE_INSU_DEPENDENCY);
//				Log.i("DB Upgrade", "Finished altering DB_NOTE INSU");
//			} catch (SQLException sqlE) {
//				Log.e("DB Update adding column", sqlE.getLocalizedMessage());
//			}
//			try {
//				db.execSQL(updateUSABILITY);
//				Log.i("DB Upgrade", "Finished altering DELETE OLD");
//			} catch (SQLException sqlE) {
//				Log.e("DB Update adding column", sqlE.getLocalizedMessage());
//			}
//
//		}
//		if (oldVersion < DB_Handler.DATABASE_VERSION) {
//			initDatabaseTables(db); // creates new feature table and Db_Info and Badges and points
//			if (oldVersion == DB_Handler.DATABASE_VERSION_USERID_BADGES) { //need to update BADGES
//				//Log.i("DB Upgrade","Altering DB");
//				String updateBadges = "ALTER TABLE Badges rename to badges_backup;\n" + //rename old table
//						"CREATE TABLE IF NOT EXISTS Badges (Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, DateTime DATETIME NOT NULL, Type TEXT NOT NULL, Name TEXT NOT NULL, Medal TEXT NOT NULL, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id));\n" + //create new one
//						"INSERT INTO badges(id,datetime,type, name,medal,id_user) SELECT b.*,u.id FROM badges_backup as b, userinfo as u;\n" +//recover the old ones to the one table
//						"DROP TABLE badges_backup;";//drop backup
//				try {
//					db.execSQL(updateBadges);
//					Log.i("DB Upgrade", "Finished altering DB");
//				} catch (SQLException sqlE) {
//					Log.e("DB Update adding column", sqlE.getLocalizedMessage());
//				}
//			}
//			if (oldVersion < DATABASE_VERSION_TARGET_BG) {
//				String updateGlicemia = "ALTER TABLE Reg_BloodGlucose add column Target_BG REAL;";
//				try {
//					db.execSQL(updateGlicemia);
//					Log.i("DB Upgrade", "Finished altering DB");
//				} catch (SQLException sqlE) {
//					Log.e("DB Update adding column", sqlE.getLocalizedMessage());
//				}
//			}
//		}
//		if(oldVersion<=DATABASE_VERSION_TAG){
//			//updateTags(db);
//			initDayPhases(db);
//		}
//	}
