package pt.it.porto.mydiabetes.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

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


	public static File get_database_file(Context context) throws IOException {
		try {
			String version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
			new Usage(MyDiabetesStorage.getInstance(context)).setAppVersion(version);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace(); // this shouldn't happen :(
		}
		File db_file;
		db_file = new File(Environment.getDataDirectory() + "/data/" + context.getPackageName() + "/databases/"+ DB_Handler.getCurrentDbName());
		if(db_file.exists()){
			return db_file;
		}else{
			db_file.createNewFile();
			return db_file;
		}
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
