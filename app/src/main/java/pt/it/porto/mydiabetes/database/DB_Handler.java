package pt.it.porto.mydiabetes.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import pt.it.porto.mydiabetes.R;

public class DB_Handler extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 15;
    private static final int DATABASE_VERSION_USERID_BADGES = 8;
	private static final int DATABASE_VERSION_TARGET_BG = 10;


    // Database Name
	private static final String DATABASE_NAME = "DB_Diabetes";

	// Context to be accessible from any method
	private Context myContext;

	public DB_Handler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.myContext = context;
	}

	@Override
	public void onConfigure(SQLiteDatabase db) {
		super.onConfigure(db);
		db.setForeignKeyConstraintsEnabled(true);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		initDatabaseTables(db);

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
		toInsert.put("TimeStart", "18:00");
		toInsert.put("TimeEnd", "20:30");
		db.insert("Tag", null, toInsert);

		toInsert = new ContentValues();
		toInsert.put("Name", daytimes[6]);
		toInsert.put("TimeStart", "20:30");
		toInsert.put("TimeEnd", "22:30");
		db.insert("Tag", null, toInsert);

		toInsert = new ContentValues();
		toInsert.put("Name", daytimes[7]);
		toInsert.put("TimeStart", "22:30");
		toInsert.put("TimeEnd", "01:00");
		db.insert("Tag", null, toInsert);

		toInsert = new ContentValues();
		toInsert.put("Name", daytimes[8]);
		db.insert("Tag", null, toInsert);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("DB Downgrade","Nothing to downgrade");
		return;
	}

	/**
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i("DB Upgrade", "Upgrading DB");
		if(oldVersion>0){

			String updateDATABASE_NOTE_BG_DEPENDENCY = "ALTER TABLE Reg_BloodGlucose rename to Reg_BloodGlucose_backup;\n" + //rename old table
					"CREATE TABLE IF NOT EXISTS Reg_BloodGlucose (Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, Value REAL NOT NULL, DateTime DATETIME NOT NULL, Id_Tag INTEGER, Id_Note INTEGER, Target_BG REAL, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id), FOREIGN KEY(Id_Tag) REFERENCES Tag(Id), FOREIGN KEY (Id_Note) REFERENCES Note(Id) ON DELETE SET NULL);\n" +
					"INSERT INTO Reg_BloodGlucose(id, id_user, value, datetime, id_tag, id_note, target_bg) SELECT b.*,u.id FROM Reg_BloodGlucose_backup as b, userinfo as u;\n" +//recover the old ones to the one table
					"DROP TABLE Reg_BloodGlucose_backup;";
			String updateDATABASE_NOTE_CARBS_DEPENDENCY = "ALTER TABLE Reg_CarboHydrate rename to Reg_CarboHydrate_backup;\n" + //rename old table
					"CREATE TABLE IF NOT EXISTS Reg_CarboHydrate (Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, Value REAL NOT NULL, DateTime DATETIME NOT NULL, Id_Tag INTEGER, Id_Note INTEGER, Target_BG REAL, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id), FOREIGN KEY(Id_Tag) REFERENCES Tag(Id), FOREIGN KEY (Id_Note) REFERENCES Note(Id) ON DELETE SET NULL);\n" +
					"INSERT INTO Reg_CarboHydrate(id, id_user, value, datetime, id_tag, id_note, target_bg) SELECT b.*,u.id FROM Reg_CarboHydrate_backup as b, userinfo as u;\n" +//recover the old ones to the one table
					"DROP TABLE Reg_CarboHydrate_backup;";
			String updateDATABASE_NOTE_INSU_DEPENDENCY = "ALTER TABLE Reg_Insulin rename to Reg_Insulin_backup;\n" + //rename old table
					"CREATE TABLE IF NOT EXISTS Reg_Insulin (Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, Id_Insulin INTEGER NOT NULL, Id_BloodGlucose INTEGER, DateTime DATETIME NOT NULL, Target_BG REAL, Value REAL NOT NULL, Id_Tag INTEGER, Id_Note INTEGER, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id), FOREIGN KEY(Id_Insulin) REFERENCES Insulin(Id), FOREIGN KEY(Id_Tag) REFERENCES Tag(Id), FOREIGN KEY(Id_BloodGlucose) REFERENCES Reg_BloodGlucose(Id), FOREIGN KEY (Id_Note) REFERENCES Note(Id) ON DELETE SET NULL);\n" +
					"INSERT INTO Reg_Insulin(id, id_user, value, datetime, id_tag, id_note, target_bg) SELECT b.*,u.id FROM Reg_Insulin_backup as b, userinfo as u;\n" +//recover the old ones to the one table
					"DROP TABLE Reg_Insulin_backup;";

			try {
				db.execSQL(updateDATABASE_NOTE_BG_DEPENDENCY);
				db.execSQL(updateDATABASE_NOTE_CARBS_DEPENDENCY);
				db.execSQL(updateDATABASE_NOTE_INSU_DEPENDENCY);
				Log.i("DB Upgrade", "Finished altering DB");
			} catch (SQLException sqlE) {
				Log.e("DB Update adding column", sqlE.getLocalizedMessage());
			}

		}
		if (oldVersion < DB_Handler.DATABASE_VERSION) {
			initDatabaseTables(db); // creates new feature table and Db_Info and Badges and points
			if (oldVersion == DB_Handler.DATABASE_VERSION_USERID_BADGES) { //need to update BADGES
				//Log.i("DB Upgrade","Altering DB");
				String updateBadges = "ALTER TABLE Badges rename to badges_backup;\n" + //rename old table
						"CREATE TABLE IF NOT EXISTS Badges (Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, DateTime DATETIME NOT NULL, Type TEXT NOT NULL, Name TEXT NOT NULL, Medal TEXT NOT NULL, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id));\n" + //create new one
						"INSERT INTO badges(id,datetime,type, name,medal,id_user) SELECT b.*,u.id FROM badges_backup as b, userinfo as u;\n" +//recover the old ones to the one table
						"DROP TABLE badges_backup;";//drop backup
				try {
					db.execSQL(updateBadges);
					Log.i("DB Upgrade", "Finished altering DB");
				} catch (SQLException sqlE) {
					Log.e("DB Update adding column", sqlE.getLocalizedMessage());
				}
			}
			if (oldVersion < DATABASE_VERSION_TARGET_BG) {
				String updateGlicemia = "ALTER TABLE Reg_BloodGlucose add column Target_BG REAL;";
				try {
					db.execSQL(updateGlicemia);
					Log.i("DB Upgrade", "Finished altering DB");
				} catch (SQLException sqlE) {
					Log.e("DB Update adding column", sqlE.getLocalizedMessage());
				}
			}
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
}
