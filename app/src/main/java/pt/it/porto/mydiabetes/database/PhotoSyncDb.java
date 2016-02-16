package pt.it.porto.mydiabetes.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

public class PhotoSyncDb {


	private MyDiabetesStorage storage;

	public PhotoSyncDb(MyDiabetesStorage storage) {
		this.storage = storage;
	}

	public void removePhoto(String photoPath) {
		storage.delete(MyDiabetesContract.SyncImagesDiff.TABLE_NAME, MyDiabetesContract.SyncImagesDiff.COLUMN_NAME_FILE_NAME + "=?", new String[]{photoPath});
	}

	public void removePhoto(int carbsId) {
		Cursor cursor = storage.query(MyDiabetesContract.Regist.CarboHydrate.TABLE_NAME, new String[]{MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_PHOTO_PATH}, MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_ID + "=?", new String[]{String.valueOf(carbsId)}, null, null, null);
		if (cursor.moveToFirst()) {
			removePhoto(cursor.getString(0));
		} else {
			Log.d("PhotoSyncDb", "Photo not found when tried to delete");
		}
	}

	public void addPhoto(String photoPath) {
		ContentValues photo = new ContentValues(1);
		photo.put(MyDiabetesContract.SyncImagesDiff.COLUMN_NAME_FILE_NAME, photoPath);
		storage.insertNewData(MyDiabetesContract.SyncImagesDiff.TABLE_NAME, photo);
	}

}
