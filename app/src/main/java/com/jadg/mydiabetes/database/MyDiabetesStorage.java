package com.jadg.mydiabetes.database;

import android.content.ContentValues;
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

	public Cursor getAllWeights(QueryOptions options) {
		SQLiteDatabase db = mHandler.getReadableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(MyDiabetesContract.Reg_Weight.TABLE_NAME);

		Cursor cursor = queryBuilder.query(db, new String[]{MyDiabetesContract.Reg_Weight.COLUMN_NAME_ID,
						MyDiabetesContract.Reg_Weight.COLUMN_NAME_DATETIME, MyDiabetesContract.Reg_Weight.COLUMN_NAME_VALUE},
				null, null, null, null,
				MyDiabetesContract.Reg_Weight.COLUMN_NAME_DATETIME + " " + (options != null ? options.getSortOrder() : QueryOptions.ORDER_DESC));
		return cursor;

	}

	/**
	 * Adds a insulin to the database and returns of sucessfull
	 *
	 * @param name
	 * @param type
	 * @param action
	 * @return
	 */
	public boolean addInsulin(String name, String type, int action) {
		if (insulinExists(name)) {
			return false;
		}
		SQLiteDatabase db = mHandler.getWritableDatabase();
		ContentValues toInsert = new ContentValues();
		toInsert.put(MyDiabetesContract.Insulin.COLUMN_NAME_NAME, name);
		toInsert.put(MyDiabetesContract.Insulin.COLUMN_NAME_TYPE, type);
		toInsert.put(MyDiabetesContract.Insulin.COLUMN_NAME_ACTION, action);

		return db.insert(MyDiabetesContract.Insulin.TABLE_NAME, null, toInsert) != -1;
	}

	public boolean insulinExists(String name) {
		SQLiteDatabase db = mHandler.getReadableDatabase();
		Cursor cursor = db.query(MyDiabetesContract.Insulin.TABLE_NAME, new String[]{MyDiabetesContract.Insulin.COLUMN_NAME_NAME},
				MyDiabetesContract.Insulin.COLUMN_NAME_NAME + "==?", new String[]{name}, null, null, null, null);
		return cursor.getCount() != 0;
	}

	public boolean addGlycemiaObjective(String description, String timeStart, String timeEnd, int objective) {
		if (glycemiaObjectiveExists(description)) {
			return false;
		}
		SQLiteDatabase db = mHandler.getWritableDatabase();
		ContentValues toInsert = new ContentValues();
		toInsert.put(MyDiabetesContract.BG_Target.COLUMN_NAME_NAME, description);
		toInsert.put(MyDiabetesContract.BG_Target.COLUMN_NAME_TIME_START, timeStart);
		toInsert.put(MyDiabetesContract.BG_Target.COLUMN_NAME_TIME_END, timeEnd);
		toInsert.put(MyDiabetesContract.BG_Target.COLUMN_NAME_VALUE, objective);

		return db.insert(MyDiabetesContract.BG_Target.TABLE_NAME, null, toInsert) != -1;
	}

	public boolean glycemiaObjectiveExists(String description) {
		SQLiteDatabase db = mHandler.getReadableDatabase();
		Cursor cursor = db.query(MyDiabetesContract.BG_Target.TABLE_NAME, new String[]{MyDiabetesContract.BG_Target.COLUMN_NAME_NAME},
				MyDiabetesContract.BG_Target.COLUMN_NAME_NAME + "==?", new String[]{description}, null, null, null, null);
		return cursor.getCount() != 0;
	}

	public static class QueryOptions {
		public static final String ORDER_ASC = "ASC";
		public static final String ORDER_DESC = "DESC";

		private String sortOrder;

		public QueryOptions setSortOrder(String sortOrder) {
			this.sortOrder = sortOrder;
			return this;
		}

		public String getSortOrder() {
			return sortOrder;
		}
	}
}
