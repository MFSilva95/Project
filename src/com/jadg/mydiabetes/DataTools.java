package com.jadg.mydiabetes;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")

public class DataTools extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_datatools);
		// Show the Up button in the action bar.
		getActionBar();
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.data_tools, menu);
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
		}
		return super.onOptionsItemSelected(item);
	}

	
	public void Call_MyData(View view){
		Intent intent = new Intent(this, MyData.class);
		startActivity(intent);
	}
	
	public void Call_Preferences(View view){
		Intent intent = new Intent(this, Preferences.class);
		startActivity(intent);
	}
	
	public void Call_ImpExp(View view){
		Intent intent = new Intent(this, ImportExport.class);
		startActivity(intent);
	}
	
	public void Call_Info(View view){
		Intent intent = new Intent(this, Info.class);
		startActivity(intent);
	}
}
