package pt.it.porto.mydiabetes.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import pt.it.porto.mydiabetes.R;

public class DB_Handler extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 4;

	// Database Name
	private static final String DATABASE_NAME = "DB_Diabetes";

	// Context to be accessible from any method
	private Context myContext;

	public DB_Handler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.myContext = context;
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

	/**
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < 3) {
			initDatabaseTables(db);
		}
		if (oldVersion < 4) {
			// convert timestamps to don't have seconds
			Cursor data = db.query(MyDiabetesContract.Tag.TABLE_NAME, new String[]{MyDiabetesContract.Tag.COLUMN_NAME_ID,
					MyDiabetesContract.Tag.COLUMN_NAME_TIME_START, MyDiabetesContract.Tag.COLUMN_NAME_TIME_END},
					MyDiabetesContract.Tag.COLUMN_NAME_TIME_START+" LIKE '__:__:00' OR "+
							MyDiabetesContract.Tag.COLUMN_NAME_TIME_END+" LIKE '__:__:00'", null, null, null, null);
			data.moveToFirst();
			ContentValues contentValues=new ContentValues();
			String[] tmp;
			while(!data.isAfterLast()){
				int id = data.getInt(0);
				String timeStart=data.getString(1);
				String timeEnd=data.getString(2);

				tmp = timeEnd.split(":");
				contentValues.put(MyDiabetesContract.Tag.COLUMN_NAME_TIME_END, tmp[0]+":"+tmp[1]);
				tmp = timeStart.split(":");
				contentValues.put(MyDiabetesContract.Tag.COLUMN_NAME_TIME_START, tmp[0]+":"+tmp[1]);
				db.update(MyDiabetesContract.Tag.TABLE_NAME, contentValues, MyDiabetesContract.Tag.COLUMN_NAME_ID+" = ?", new String[] {String.valueOf(id)});

				data.moveToNext();
			}
			data.close();
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
