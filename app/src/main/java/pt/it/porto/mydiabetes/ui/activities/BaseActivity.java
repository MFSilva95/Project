package pt.it.porto.mydiabetes.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import pt.it.porto.mydiabetes.BuildConfig;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (BuildConfig.USE_FABRIC) {
			Fabric.with(this, new Crashlytics());
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			Log.d("BaseActivity", ev.toString());
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d("BaseActivity", "Exit activity: " + this.getComponentName().getClassName());
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("BaseActivity", "Enter activity: " + this.getComponentName().getClassName());
	}
}
