package pt.it.porto.mydiabetes.database;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

	public static final String PREFERENCES = "MyPreferences";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";

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

	public static boolean saveCloudSyncCredentials(Context context, String username, String password) {
		SharedPreferences preferences = getPreferences(context);
		preferences.edit().putString(USERNAME, username).putString(PASSWORD, password).apply();
		return true;
	}

	public static String getUsername(Context context) {
		return getPreferences(context).getString(USERNAME, null);
	}

	public static String getPassword(Context context) {
		return getPreferences(context).getString(PASSWORD, null);
	}
}
