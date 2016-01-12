package pt.it.porto.mydiabetes.database;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

	public static final String PREFERENCES = "MyPreferences";

	public static SharedPreferences getPreferences(Context context) {
		return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
	}

	public static boolean showFeatureForFirstTime(Context context, String featureName) {
		SharedPreferences prefs = getPreferences(context);
		if (prefs.contains(featureName)) {
			return false;
		} else {
			prefs.edit().putBoolean(featureName, false).apply();// set it as seen
			return true;
		}
	}
}
