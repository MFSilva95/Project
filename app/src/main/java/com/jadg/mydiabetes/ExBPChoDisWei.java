package com.jadg.mydiabetes;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ScrollView;

import android.view.Menu;
import android.view.MenuItem;


import android.annotation.SuppressLint;






public class ExBPChoDisWei extends Activity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exbpchodiswei);
		// Show the Up button in the action bar.
		getActionBar();
		
		final GestureDetector gestureDetector;
        gestureDetector = new GestureDetector(new MyGestureDetector());
		ScrollView sv = (ScrollView)findViewById(R.id.exbpchodisweiScrollView);
		sv.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return false;
                } else {
                    return true;
                }
            }
        });
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				// This ID represents the Home or Up button. In the case of this
				// activity, the Up button is shown. Use NavUtils to allow users
				// to navigate up one level in the application structure. For
				// more details, see the Navigation pattern on Android Design:
				//
				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
				//
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_Home:
				Call_DataTools();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	public void Call_DataTools(){
		Intent intent = new Intent(this, DataTools.class);
		startActivity(intent);
	}
	
	public void Call_Exercise(View view){
		Intent intent = new Intent(this, Exercise.class);
		startActivity(intent);
	}
	
	public void Call_BloodPressure(View view){
		Intent intent = new Intent(this, BloodPressure.class);
		startActivity(intent);
	}

	public void Call_Cholesterol(View view){
		Intent intent = new Intent(this, Cholesterol.class);
		startActivity(intent);
	}
	
	public void Call_Disease(View view){
		Intent intent = new Intent(this, Disease.class);
		startActivity(intent);
	}

	
	public void Call_Weight(View view){
		Intent intent = new Intent(this, Weight.class);
		startActivity(intent);
	}
	
	public void Call_H1A1c(View view){
		Intent intent = new Intent(this, HbA1c.class);
		startActivity(intent);
	}
	
	public void Call_Middle(View view){
		finish();
		//Intent intent = new Intent(this, CarboHydrate.class);
		//startActivity(intent);
	}

	private class MyGestureDetector extends SimpleOnGestureListener {

	    private static final int SWIPE_MIN_DISTANCE = 120;
	    private static final int SWIPE_MAX_OFF_PATH = 250;
	    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
	            float velocityY) {
	        System.out.println(" in onFling() :: ");
	        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
	            return false;
	        if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
	                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	        	
	        	//Right
        		
	        	
	        } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
	                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	            //Left
	        	finish();
	        }
	        return super.onFling(e1, e2, velocityX, velocityY);
	    }
	}
}