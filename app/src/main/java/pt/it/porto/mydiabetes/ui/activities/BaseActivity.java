package pt.it.porto.mydiabetes.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import lecho.lib.hellocharts.model.Line;
import pt.it.porto.mydiabetes.BuildConfig;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.adviceSystem.yapDroid.YapDroid;
import pt.it.porto.mydiabetes.data.Advice;


public abstract class BaseActivity extends AppCompatActivity {

	//Advice activityAdvice = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (BuildConfig.USE_FABRIC) {
			Fabric.with(this, new Crashlytics());
		}
		//if(getRegType()!=null){activityAdvice = YapDroid.newInstance(getApplicationContext()).getSingleAdvice("start", getRegType(), this.getApplicationContext());}
	}

	public String getRegType() {
		return null;
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
