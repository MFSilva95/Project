package pt.it.porto.mydiabetes.ui.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ScrollView;

import java.text.ParseException;
import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.database.Usage;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.SyncAlarm;


public class Home extends BaseOldActivity {

	private static final String TAG = "Home";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		getActionBar();

		DB_Read read = new DB_Read(this);
		if (!read.MyData_HasData()) {
			ShowDialogAddData();
		}
		read.close();

		final GestureDetector gestureDetector;
		gestureDetector = new GestureDetector(this, new MyGestureDetector());
		ScrollView sv = (ScrollView) findViewById(R.id.homeScrollView);
		sv.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				return !gestureDetector.onTouchEvent(event);
			}
		});
		setupSyncAlarm();
	}

	private void setupSyncAlarm() {
		SharedPreferences preferences = pt.it.porto.mydiabetes.database.Preferences.getPreferences(this);
		if (!preferences.contains(SyncAlarm.SYNC_ALARM_PREFERENCE)) { // only sets it if needed
			AlarmManager alm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(this, SyncAlarm.class);
			PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

			Calendar calendar = Calendar.getInstance();
			if (preferences.contains(SyncAlarm.SYNC_ALARM_LAST_SYNC)) {
				calendar.setTimeInMillis(preferences.getLong(SyncAlarm.SYNC_ALARM_LAST_SYNC, System.currentTimeMillis()));
			} else {
				Usage usage = new Usage(MyDiabetesStorage.getInstance(this));
				String date = usage.getOldestRegist();

				try {
					calendar.setTime(DateUtils.iso8601Format.parse(date));
				} catch (ParseException e) {
					e.printStackTrace();
					return;
				}
			}
			calendar.roll(Calendar.DAY_OF_YEAR, 7);
			calendar.set(Calendar.HOUR_OF_DAY, 21); // Maybe change later?

			alm.set(AlarmManager.RTC, calendar.getTimeInMillis(), alarmIntent);

			preferences.edit().putInt(SyncAlarm.SYNC_ALARM_PREFERENCE, 1).apply();
		}
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
			case R.id.menuItem_Home:
				Call_DataTools();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}


	public void ShowDialogAddData() {
		Intent intent = new Intent(this, WelcomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		finish();
	}

	public void Call_DataTools() {
		Intent intent = new Intent(this, DataTools.class);
		startActivity(intent);
	}

	public void Call_OutrasLeituras(View view) {
		Intent intent = new Intent(this, ExBPChoDisWei.class);
		startActivity(intent);
	}

	public void Call_Meal(View view) {
		Intent intent = new Intent(this, Meal.class);
		startActivity(intent);
	}

	public void Call_Glycemia(View view) {
		Intent intent = new Intent(this, Glycemia.class);
		startActivity(intent);
	}

	public void Call_Exercise(View view) {
		Intent intent = new Intent(this, Exercise.class);
		startActivity(intent);
	}

	public void Call_Insulin(View view) {
		Intent intent = new Intent(this, Insulin.class);
		startActivity(intent);
	}

	public void Call_Carbs(View view) {
		Intent intent = new Intent(this, CarboHydrate.class);
		startActivity(intent);
	}

	//ADDED BY ZE ORNELAS
	public void Call_Logbook(View view) {
		Intent intent = new Intent(this, Logbook.class);
		startActivity(intent);
	}

	private class MyGestureDetector extends SimpleOnGestureListener {

		private static final int SWIPE_MIN_DISTANCE = 120;
		private static final int SWIPE_MAX_OFF_PATH = 250;
		private static final int SWIPE_THRESHOLD_VELOCITY = 200;


		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
							   float velocityY) {
			if (e1 != null && e2 != null) {
				System.out.println(" in onFling() :: ");
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

					//Right
					Call_OutrasLeituras(getCurrentFocus());

				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					//Left
				}
			}
			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}
}


