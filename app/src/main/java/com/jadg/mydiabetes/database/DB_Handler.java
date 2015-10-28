package com.jadg.mydiabetes.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jadg.mydiabetes.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DB_Handler extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 2;

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
		try {
			BufferedReader localBufferedReader =
					new BufferedReader(new InputStreamReader(this.myContext.getAssets().open("DataBase.txt"), "UTF-8"));
			while (true) {
				String str = localBufferedReader.readLine();
				if (str == null)
					return;
				try {
					db.execSQL(str);
					Log.d("DB:", str);
				} catch (Exception localException) {
					Log.i("sql", str);
					Log.e("Error", localException.getMessage());
				}
			}

			//pre fill DB


		} catch (Exception e) {
			Log.d("Erro", e.toString());
			e.printStackTrace();
			return;
		} finally {
			//db.close();
			Log.d("DB:", "Sai do finally");
			Resources res = this.myContext.getResources();
			String[] daytimes = res.getStringArray(R.array.daytimes);

			ContentValues toInsert = new ContentValues();
			toInsert.put("Name", daytimes[0]);
			toInsert.put("TimeStart", "06:00:00");
			toInsert.put("TimeEnd", "07:30:00");
			db.insert("Tag", null, toInsert);

			toInsert = new ContentValues();
			toInsert.put("Name", daytimes[1]);
			toInsert.put("TimeStart", "07:30:00");
			toInsert.put("TimeEnd", "09:00:00");
			db.insert("Tag", null, toInsert);

			toInsert = new ContentValues();
			toInsert.put("Name", daytimes[2]);
			toInsert.put("TimeStart", "09:00:00");
			toInsert.put("TimeEnd", "10:30:00");
			db.insert("Tag", null, toInsert);

			toInsert = new ContentValues();
			toInsert.put("Name", daytimes[3]);
			toInsert.put("TimeStart", "10:30:00");
			toInsert.put("TimeEnd", "13:00:00");
			db.insert("Tag", null, toInsert);

			toInsert = new ContentValues();
			toInsert.put("Name", daytimes[4]);
			toInsert.put("TimeStart", "13:00:00");
			toInsert.put("TimeEnd", "15:30:00");
			db.insert("Tag", null, toInsert);

			toInsert = new ContentValues();
			toInsert.put("Name", daytimes[5]);
			toInsert.put("TimeStart", "18:00:00");
			toInsert.put("TimeEnd", "20:30:00");
			db.insert("Tag", null, toInsert);

			toInsert = new ContentValues();
			toInsert.put("Name", daytimes[6]);
			toInsert.put("TimeStart", "20:30:00");
			toInsert.put("TimeEnd", "22:30:00");
			db.insert("Tag", null, toInsert);

			toInsert = new ContentValues();
			toInsert.put("Name", daytimes[7]);
			toInsert.put("TimeStart", "22:30:00");
			toInsert.put("TimeEnd", "01:00:00");
			db.insert("Tag", null, toInsert);

			toInsert = new ContentValues();
			toInsert.put("Name", daytimes[8]);
			db.insert("Tag", null, toInsert);

		}


	}

	/**
	 * TODO: update from version where clicks do not exist and action (see below) is just a text
	 *
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == 1) {
			//for version 2
			//need to create the new tables for usability
			//need to change the action type in Insulin as the DB_Read::Insulin_GetActionTypeByName expects action to be a parseable int
		}
	}

}
