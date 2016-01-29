package pt.it.porto.mydiabetes.database;

import android.content.ContentValues;
import android.database.Cursor;

public class FeaturesDB {
	public static final String FEATURE_INSULIN_ON_BOARD = "feature_insulin_on_board";

	private MyDiabetesStorage storage;

	public FeaturesDB(MyDiabetesStorage storage) {
		this.storage = storage;
	}

	public boolean isFeatureActive(String feature) {
		Cursor result = storage.query(MyDiabetesContract.Feature.TABLE_NAME, new String[]{MyDiabetesContract.Feature.COLUMN_NAME_ACTIVATED},
				MyDiabetesContract.Feature.COLUMN_NAME_NAME + "=?", new String[]{feature}, null, null, null, 1);
		return result.getInt(0) != 0;
	}

	public void changeFeatureStatus(String feature, boolean active) {
		// checks is feature created
		Cursor result = storage.query(MyDiabetesContract.Feature.TABLE_NAME, new String[]{MyDiabetesContract.Feature.COLUMN_NAME_NAME},
				MyDiabetesContract.Feature.COLUMN_NAME_NAME + "=?", new String[]{feature}, null, null, null, 1);
		if(result.getCount()==0){
			createFeature(feature);
		}
		ContentValues toInsert = new ContentValues();
		toInsert.put(MyDiabetesContract.Feature.COLUMN_NAME_ACTIVATED, active ? 1 : 0);
		storage.updateData(MyDiabetesContract.Feature.TABLE_NAME, toInsert, MyDiabetesContract.Feature.COLUMN_NAME_NAME + "=?", new String[]{feature});
	}

	public void createFeature(String feature) {
		ContentValues toInsert = new ContentValues();
		toInsert.put(MyDiabetesContract.Feature.COLUMN_NAME_NAME, feature);
		storage.insertNewData(MyDiabetesContract.Feature.TABLE_NAME, toInsert);
	}
}
