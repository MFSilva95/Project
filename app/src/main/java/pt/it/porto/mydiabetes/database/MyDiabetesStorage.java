package pt.it.porto.mydiabetes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.format.Time;

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
		queryBuilder.setTables(MyDiabetesContract.Regist.Weight.TABLE_NAME + " INNER JOIN " + MyDiabetesContract.Note.TABLE_NAME
				+ "ON " + MyDiabetesContract.Regist.Weight.COLUMN_NAME_NOTE_ID + "=" + MyDiabetesContract.Note.COLUMN_NAME_ID);

		Cursor cursor = queryBuilder.query(db, new String[]{MyDiabetesContract.Regist.Weight.COLUMN_NAME_ID, MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME,
						MyDiabetesContract.Regist.Weight.COLUMN_NAME_VALUE, MyDiabetesContract.Note.COLUMN_NAME_Note},
				null, null, null, null, null, "1");
		return cursor;

	}

	public Cursor getAllWeights(QueryOptions options) {
		SQLiteDatabase db = mHandler.getReadableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(MyDiabetesContract.Regist.Weight.TABLE_NAME);

		Cursor cursor = queryBuilder.query(db, new String[]{MyDiabetesContract.Regist.Weight.COLUMN_NAME_ID,
						MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME, MyDiabetesContract.Regist.Weight.COLUMN_NAME_VALUE},
				null, null, null, null,
				MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME + " " + (options != null ? options.getSortOrder() : QueryOptions.ORDER_DESC));
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

	public boolean addUserData(String name, String diabetesType, int insulinRatio, int carbsRatio, float lowerRange, float higherRange, String birthday, String gender, float height) {
		ContentValues toInsert = new ContentValues();
		toInsert.put(MyDiabetesContract.UserInfo.COLUMN_NAME_NAME, name);
		toInsert.put(MyDiabetesContract.UserInfo.COLUMN_NAME_DIABETES_TYPE, diabetesType);
		toInsert.put(MyDiabetesContract.UserInfo.COLUMN_NAME_RATIO_INSULIN, insulinRatio);
		toInsert.put(MyDiabetesContract.UserInfo.COLUMN_NAME_RATIO_CARBS, carbsRatio);
		toInsert.put(MyDiabetesContract.UserInfo.COLUMN_NAME_RANGE_LOWER, lowerRange);
		toInsert.put(MyDiabetesContract.UserInfo.COLUMN_NAME_RANGE_HIGHER, higherRange);
		toInsert.put(MyDiabetesContract.UserInfo.COLUMN_NAME_BIRTHDATE, birthday);
		toInsert.put(MyDiabetesContract.UserInfo.COLUMN_NAME_GENDER, gender);
		toInsert.put(MyDiabetesContract.UserInfo.COLUMN_NAME_HEIGHT, height);
		Time now = new Time(Time.getCurrentTimezone());
		now.setToNow();
		toInsert.put(MyDiabetesContract.UserInfo.COLUMN_NAME_LAST_UPDATE, now.format("%Y-%m-%d %H:%M:%S"));
		return insertNewData(MyDiabetesContract.UserInfo.TABLE_NAME, toInsert) != -1;
	}

	public boolean editUserData(int id, ContentValues newData) {
		SQLiteDatabase db = mHandler.getWritableDatabase();
		return db.update(MyDiabetesContract.UserInfo.TABLE_NAME, newData, MyDiabetesContract.UserInfo.COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(id)}) == 1;
	}

	public boolean addNote(String note) {
		ContentValues values = new ContentValues();
		values.put(MyDiabetesContract.Note.COLUMN_NAME_Note, note);
		return insertNewData(MyDiabetesContract.Note.TABLE_NAME, values) != -1;
	}


	public Cursor getAllInsulins(QueryOptions options) {
		SQLiteDatabase db = mHandler.getReadableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		// TODO untested!
		queryBuilder.setTables(MyDiabetesContract.Regist.Insulin.TABLE_NAME + " INNER JOIN " + MyDiabetesContract.Regist.BloodGlucose.TABLE_NAME
				+ " ON " + MyDiabetesContract.Regist.Insulin.COLUMN_NAME_BLOODGLUCOSE_ID + "=" + MyDiabetesContract.Regist.BloodGlucose.COLUMN_NAME_ID
				+ " INNER JOIN " + MyDiabetesContract.Insulin.TABLE_NAME + " ON "
				+ MyDiabetesContract.Regist.Insulin.COLUMN_NAME_INSULIN_ID + "=" + MyDiabetesContract.Insulin.COLUMN_NAME_ID
				+ "INNER JOIN" + MyDiabetesContract.Tag.TABLE_NAME + " ON "
				+ MyDiabetesContract.Regist.Insulin.COLUMN_NAME_TAG_ID + "=" + MyDiabetesContract.Tag.COLUMN_NAME_ID);

		Cursor cursor = queryBuilder.query(db, new String[]{MyDiabetesContract.Regist.Insulin.COLUMN_NAME_ID,
						MyDiabetesContract.Regist.Insulin.COLUMN_NAME_VALUE, MyDiabetesContract.Insulin.COLUMN_NAME_NAME,
						MyDiabetesContract.Regist.Insulin.COLUMN_NAME_DATETIME, MyDiabetesContract.Regist.Insulin.COLUMN_NAME_DURATION,
						MyDiabetesContract.Regist.BloodGlucose.COLUMN_NAME_VALUE, MyDiabetesContract.Tag.COLUMN_NAME_NAME},
				null, null, null, null,
				MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME + " " + (options != null ? options.getSortOrder() : QueryOptions.ORDER_DESC));
		return cursor;
	}

	public long insertNewData(String table, ContentValues data) {
		SQLiteDatabase db = mHandler.getWritableDatabase();
		return db.insert(table, null, data);
	}

	Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderby) {
		SQLiteDatabase db = mHandler.getReadableDatabase();
		return db.query(table, columns, selection, selectionArgs, groupBy, having, orderby);
	}

	Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderby, int limit) {
		SQLiteDatabase db = mHandler.getReadableDatabase();
		return db.query(table, columns, selection, selectionArgs, groupBy, having, orderby, String.valueOf(limit));
	}

	Cursor rawQuert(String query) {
		SQLiteDatabase db = mHandler.getReadableDatabase();
		return db.rawQuery(query, null);
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
