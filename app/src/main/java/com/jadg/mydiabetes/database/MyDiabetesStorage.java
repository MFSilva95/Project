package com.jadg.mydiabetes.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

public class MyDiabetesStorage {

	private DB_Handler mHandler;
	private static MyDiabetesStorage instance;

	private MyDiabetesStorage(Context context) {
		mHandler = new DB_Handler(context);
	}


	public synchronized static MyDiabetesStorage getInstance(Context context) {
		if (instance == null) {
			instance = new MyDiabetesStorage(context);
		}
		return instance;
	}


	public Cursor getWeightRegist(int id) {
		SQLiteDatabase db = mHandler.getReadableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(MyDiabetesContract.Reg_Weight.TABLE_NAME + " INNER JOIN " + MyDiabetesContract.Note.TABLE_NAME
				+ "ON " + MyDiabetesContract.Reg_Weight.COLUMN_NAME_NOTE_ID + "=" + MyDiabetesContract.Note.COLUMN_NAME_ID);

		Cursor cursor = queryBuilder.query(db, new String[]{MyDiabetesContract.Reg_Weight.COLUMN_NAME_ID, MyDiabetesContract.Reg_Weight.COLUMN_NAME_DATETIME,
						MyDiabetesContract.Reg_Weight.COLUMN_NAME_VALUE, MyDiabetesContract.Note.COLUMN_NAME_Note},
				null, null, null, null, null, "1");
		return cursor;

	}

	public Cursor getAllWeights() {
		SQLiteDatabase db = mHandler.getReadableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(MyDiabetesContract.Reg_Weight.TABLE_NAME);

		Cursor cursor = queryBuilder.query(db, new String[]{MyDiabetesContract.Reg_Weight.COLUMN_NAME_ID,
						MyDiabetesContract.Reg_Weight.COLUMN_NAME_DATETIME, MyDiabetesContract.Reg_Weight.COLUMN_NAME_VALUE},
				null, null, null, null, null);
		return cursor;

	}
}
