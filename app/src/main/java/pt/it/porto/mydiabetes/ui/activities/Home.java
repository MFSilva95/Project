package pt.it.porto.mydiabetes.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ScrollView;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;


public class Home extends BaseActivity {

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
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.title_activity_info))
				.setMessage(getString(R.string.homeInfo))
				.setPositiveButton(getString(R.string.okButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB
						//Rever porque não elimina o registo de glicemia
						Intent intent = new Intent(Home.this, MyData.class);
						intent.putExtra("tabPosition", 2);
						startActivity(intent);

					}
				}).show();
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
			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}
}


