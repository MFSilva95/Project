package pt.it.porto.mydiabetes.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import pt.it.porto.mydiabetes.data.Tag;
import pt.it.porto.mydiabetes.database.DB_Handler;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.database.Usage;

public class DbUtils {
	public static String toString(String[] elements) {
		StringBuilder result = new StringBuilder(elements.length * 7); // assuming that 7 is the normal size of a column name in the database
		for (int i = 0; i < elements.length; i++) {
			result.append(elements[i]);
			if (i != elements.length - 1) {
				result.append(',');
			}
		}
		return result.toString();
	}


	public static File exportDb(Context context) {
		try {
			String version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
			new Usage(MyDiabetesStorage.getInstance(context)).setAppVersion(version);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace(); // this shouldn't happen :(
		}
		File db = new File(Environment.getDataDirectory() + "/data/" + context.getPackageName() + "/databases/"+ DB_Handler.getCurrentDbName());
		//Log.i("RAWR", "exportDb: "+db.getAbsolutePath());
		if(db.exists()){
			//"clean" database

			SQLiteDatabase clean_db = SQLiteDatabase.openDatabase(db.getAbsolutePath(), null, 0);

            ContentValues toUpdate = new ContentValues();
            toUpdate.put("Name", "");
            toUpdate.put("Gender", -1);

            clean_db.update("UserInfo", toUpdate, null, null);
            clean_db.close();
            return db;
		}else{
			return null;
		}//DB_Diabetes");
	}

//	public static File export_old_Db(Context context) {
//		try {
//			String version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
//			new Usage(MyDiabetesStorage.getInstance(context)).setAppVersion(version);
//		} catch (PackageManager.NameNotFoundException e) {
//			e.printStackTrace(); // this shouldn't happen :(
//		}
//
//		return new File(Environment.getDataDirectory() + "/data/" + context.getPackageName() + "/databases/"+ DB_Handler.getOldDbName());//DB_Diabetes");
//	}
}
