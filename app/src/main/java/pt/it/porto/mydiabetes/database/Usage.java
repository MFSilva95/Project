package pt.it.porto.mydiabetes.database;

import android.database.Cursor;

public class Usage {


	private MyDiabetesStorage storage;

	public Usage(MyDiabetesStorage storage) {
		this.storage = storage;
	}

	public String getOldestRegist() {
		Cursor cursor = storage.query(MyDiabetesContract.Regist.Insulin.TABLE_NAME, new String[]{MyDiabetesContract.Regist.Insulin.COLUMN_NAME_DATETIME},
				null, null, null, null, MyDiabetesContract.Regist.Insulin.COLUMN_NAME_DATETIME + " ASC", 1);
		cursor.moveToFirst();
		String datetime = cursor.getString(cursor.getColumnIndex(MyDiabetesContract.Regist.Insulin.COLUMN_NAME_DATETIME));
		cursor.close();
		return datetime;
	}

}
