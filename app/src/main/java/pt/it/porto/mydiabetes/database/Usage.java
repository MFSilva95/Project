package pt.it.porto.mydiabetes.database;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Calendar;

import pt.it.porto.mydiabetes.utils.DateUtils;

public class Usage {


	private MyDiabetesStorage storage;

	public Usage(MyDiabetesStorage storage) {
		this.storage = storage;
	}

	public String getOldestRegist() {
		Cursor cursor = storage.query(MyDiabetesContract.Regist.Insulin.TABLE_NAME, new String[]{MyDiabetesContract.Regist.Insulin.COLUMN_NAME_DATETIME}, null, null, null, null, MyDiabetesContract.Regist.Insulin.COLUMN_NAME_DATETIME + " ASC", 1);
		cursor.moveToFirst();
		String datetime;
		if (cursor.getCount() == 0) {
			Calendar calendar = Calendar.getInstance();
			datetime = DateUtils.formatToDb(calendar);
		} else {
			datetime = cursor.getString(cursor.getColumnIndex(MyDiabetesContract.Regist.Insulin.COLUMN_NAME_DATETIME));
		}
		cursor.close();
		return datetime;
	}

	public void setAppVersion(String appVersion) {
		ContentValues values = new ContentValues();
		values.put(MyDiabetesContract.DbInfo.COLUMN_NAME_VERSION, appVersion);
		values.put(MyDiabetesContract.DbInfo.COLUMN_NAME_DATETIME, DateUtils.formatToDb(Calendar.getInstance()));
		values.put("NotDeprecated", true);
		storage.insertNewData(MyDiabetesContract.DbInfo.TABLE_NAME, values);
	}

}
