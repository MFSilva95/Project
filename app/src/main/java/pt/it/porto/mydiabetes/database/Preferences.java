package pt.it.porto.mydiabetes.database;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

	public static final String PREFERENCES="MyPreferences";

	public static SharedPreferences getPreferences(Context context) {
		return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
	}
}
