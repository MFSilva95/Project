package pt.it.porto.mydiabetes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import java.util.ArrayList;
import java.util.Calendar;

import pt.it.porto.mydiabetes.data.Tag;
import pt.it.porto.mydiabetes.ui.views.GlycemiaObjectivesData;
import pt.it.porto.mydiabetes.utils.DateUtils;

public class MyDiabetesStorage {

	private static MyDiabetesStorage instance;
	private DB_Handler mHandler;

	private MyDiabetesStorage(Context context) {
		mHandler = new DB_Handler(context);
	}


	public synchronized static MyDiabetesStorage getInstance(Context context) {
		if (instance == null) {
			instance = new MyDiabetesStorage(context);
		}
		return instance;
	}

//	public void close_handler(){
//		this.mHandler.close();
//		this.mHandler = null;
//		MyDiabetesStorage.instance = null;
//	}


	public Cursor getWeightRegist(int id) {
		SQLiteDatabase db = mHandler.getReadableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(MyDiabetesContract.Regist.Weight.TABLE_NAME + " INNER JOIN " + MyDiabetesContract.Note.TABLE_NAME + "ON " + MyDiabetesContract.Regist.Weight.COLUMN_NAME_NOTE_ID + "=" + MyDiabetesContract.Note.COLUMN_NAME_ID);

		Cursor cursor = queryBuilder.query(db, new String[]{MyDiabetesContract.Regist.Weight.COLUMN_NAME_ID, MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME, MyDiabetesContract.Regist.Weight.COLUMN_NAME_VALUE, MyDiabetesContract.Note.COLUMN_NAME_Note}, null, null, null, null, null, "1");
		return cursor;

	}

	public Cursor getAllWeights(QueryOptions options) {
		SQLiteDatabase db = mHandler.getReadableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(MyDiabetesContract.Regist.Weight.TABLE_NAME);

		Cursor cursor = queryBuilder.query(db, new String[]{MyDiabetesContract.Regist.Weight.COLUMN_NAME_ID, MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME, MyDiabetesContract.Regist.Weight.COLUMN_NAME_VALUE}, null, null, null, null, MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME + " " + (options != null ? options.getSortOrder() : QueryOptions.ORDER_DESC));
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
		Cursor cursor = db.query(MyDiabetesContract.Insulin.TABLE_NAME, new String[]{MyDiabetesContract.Insulin.COLUMN_NAME_NAME}, MyDiabetesContract.Insulin.COLUMN_NAME_NAME + "==?", new String[]{name}, null, null, null, null);
		return cursor.getCount() != 0;
	}
	public boolean addGlycemiaObjective(String description, String timeStart, /*String timeEnd,*/ int objective) {
		if (glycemiaObjectiveExists(description)) {
			return false;
		}
		SQLiteDatabase db = mHandler.getWritableDatabase();
		ContentValues toInsert = new ContentValues();
		toInsert.put(MyDiabetesContract.BG_Target.COLUMN_NAME_NAME, description);
		toInsert.put(MyDiabetesContract.BG_Target.COLUMN_NAME_TIME_START, timeStart);
		//toInsert.put(MyDiabetesContract.BG_Target.COLUMN_NAME_TIME_END, timeEnd);
		toInsert.put(MyDiabetesContract.BG_Target.COLUMN_NAME_VALUE, objective);

		return db.insert(MyDiabetesContract.BG_Target.TABLE_NAME, null, toInsert) != -1;
	}

	public void initTarget_bg(int value){

		SQLiteDatabase db = mHandler.getWritableDatabase();
		DB_Read dbRead = new DB_Read(db);
		ArrayList<Tag> tags = dbRead.Tag_GetAll();
		int id_user = dbRead.getUserId();
		for(int index=0;index<tags.size()-1;index++){

			ContentValues toInsert = new ContentValues();
			toInsert.put("Name", tags.get(index).getName());
			toInsert.put("Value", value);
			toInsert.put("TimeStart", tags.get(index).getStart());

			db.insert(MyDiabetesContract.BG_Target.TABLE_NAME, null, toInsert);
		}
	}

	public void initRacioSens(int value, String table){

		SQLiteDatabase db = mHandler.getWritableDatabase();
		DB_Read dbRead = new DB_Read(db);
		ArrayList<Tag> tags = dbRead.Tag_GetAll();
		int id_user = dbRead.getUserId();
//		String TAG = "cenas";
//		Log.i(TAG, "initRacioSens: USER_ID= "+id_user);
//		Log.i(TAG, " TABLE : "+table);

		for(int index=0;index<tags.size()-1;index++){
//			Log.i(TAG, "- NEW TAG -");

			ContentValues toInsert = new ContentValues();
			toInsert.put("Id_User", id_user);
//			toInsert.put("Id_Tag", tags.get(index).getUserId());
			toInsert.put("Value", value);
			toInsert.put("Name", tags.get(index).getName());
			toInsert.put("TimeStart", tags.get(index).getStart());
			toInsert.put("TimeEnd", tags.get(index).getEnd());

//			Log.i(TAG, " TAG ID: "+tags.get(index).getUserId());
//			Log.i(TAG, " TAG VALUE: "+value);
//			Log.i(TAG, " TAG NAME: "+tags.get(index).getName());
//			Log.i(TAG, " TAG : START"+tags.get(index).getStart());
//			Log.i(TAG, " TAG END: "+tags.get(index).getEnd());

			db.insert(table, null, toInsert);
		}
	}

	public ArrayList<GlycemiaObjectivesData> getGlycemiaObjectives() throws Exception{
		SQLiteDatabase db = mHandler.getReadableDatabase();
		Cursor cursor = db.query(
				MyDiabetesContract.BG_Target.TABLE_NAME,
				new String[]{MyDiabetesContract.BG_Target.COLUMN_NAME_VALUE, MyDiabetesContract.BG_Target.COLUMN_NAME_TIME_START, MyDiabetesContract.BG_Target.COLUMN_NAME_TIME_END},
				null,
                null,
                null,
                null,
                null,
                null
		);
		cursor.moveToFirst();
        ArrayList<GlycemiaObjectivesData> objList = new ArrayList<>();
        for(int result = 0; result<cursor.getCount(); result++){
            GlycemiaObjectivesData gly = new GlycemiaObjectivesData((int) cursor.getFloat(0),cursor.getString(1),cursor.getString(2));
            objList.add(gly);
            cursor.moveToNext();
        }
		return objList;
	}

	public boolean glycemiaObjectiveExists(String description) {
		SQLiteDatabase db = mHandler.getReadableDatabase();
		Cursor cursor = db.query(MyDiabetesContract.BG_Target.TABLE_NAME, new String[]{MyDiabetesContract.BG_Target.COLUMN_NAME_NAME}, MyDiabetesContract.BG_Target.COLUMN_NAME_NAME + "==?", new String[]{description}, null, null, null, null);
		return cursor.getCount() != 0;
	}

	public boolean addUserData(String name, int diabetesType, int insulinRatio, int carbsRatio, float lowerRange, float higherRange, String birthday, int gender, float height, int bg_t) {
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
		toInsert.put(MyDiabetesContract.UserInfo.COLUMN_NAME_LAST_UPDATE, DateUtils.formatToDb(Calendar.getInstance()));
		toInsert.put(MyDiabetesContract.UserInfo.COLUMN_GLUCOSE_TARGET, bg_t);

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
		queryBuilder.setTables(MyDiabetesContract.Regist.Insulin.TABLE_NAME + " INNER JOIN " + MyDiabetesContract.Regist.BloodGlucose.TABLE_NAME + " ON " + MyDiabetesContract.Regist.Insulin.COLUMN_NAME_BLOODGLUCOSE_ID + "=" + MyDiabetesContract.Regist.BloodGlucose.COLUMN_NAME_ID + " INNER JOIN " + MyDiabetesContract.Insulin.TABLE_NAME + " ON " + MyDiabetesContract.Regist.Insulin.COLUMN_NAME_INSULIN_ID + "=" + MyDiabetesContract.Insulin.COLUMN_NAME_ID + "INNER JOIN" + MyDiabetesContract.Tag.TABLE_NAME + " ON " + MyDiabetesContract.Regist.Insulin.COLUMN_NAME_TAG_ID + "=" + MyDiabetesContract.Tag.COLUMN_NAME_ID);

		Cursor cursor = queryBuilder.query(db, new String[]{MyDiabetesContract.Regist.Insulin.COLUMN_NAME_ID, MyDiabetesContract.Regist.Insulin.COLUMN_NAME_VALUE, MyDiabetesContract.Insulin.COLUMN_NAME_NAME, MyDiabetesContract.Regist.Insulin.COLUMN_NAME_DATETIME, MyDiabetesContract.Regist.Insulin.COLUMN_NAME_DURATION, MyDiabetesContract.Regist.BloodGlucose.COLUMN_NAME_VALUE, MyDiabetesContract.Tag.COLUMN_NAME_NAME}, null, null, null, null, MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME + " " + (options != null ? options.getSortOrder() : QueryOptions.ORDER_DESC));
		return cursor;
	}

	public long insertNewData(String table, ContentValues data) {
		SQLiteDatabase db = mHandler.getWritableDatabase();
		return db.insert(table, null, data);
	}

	public long updateData(String table, ContentValues data, String where, String[] args) {
		SQLiteDatabase db = mHandler.getWritableDatabase();
		return db.update(table, data, where, args);
	}

	Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderby) {
		SQLiteDatabase db = mHandler.getReadableDatabase();
		return db.query(table, columns, selection, selectionArgs, groupBy, having, orderby);
	}

	Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderby, int limit) {
		SQLiteDatabase db = mHandler.getReadableDatabase();
		return db.query(table, columns, selection, selectionArgs, groupBy, having, orderby, String.valueOf(limit));
	}

	Cursor rawQuery(String query) {
		SQLiteDatabase db = mHandler.getReadableDatabase();
		return db.rawQuery(query, null);
	}

	int delete(String table, String where, String[] whereArgs) {
		SQLiteDatabase db = mHandler.getReadableDatabase();
		return db.delete(table, where, whereArgs);
	}

	public static class QueryOptions {
		public static final String ORDER_ASC = "ASC";
		public static final String ORDER_DESC = "DESC";

		private String sortOrder;

		public String getSortOrder() {
			return sortOrder;
		}

		public QueryOptions setSortOrder(String sortOrder) {
			this.sortOrder = sortOrder;
			return this;
		}
	}
}
