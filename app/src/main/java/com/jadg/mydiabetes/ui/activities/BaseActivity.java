package com.jadg.mydiabetes.ui.activities;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;

public class BaseActivity extends AppCompatActivity {

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
