package pt.it.porto.mydiabetes.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;

import com.crashlytics.android.Crashlytics;

import java.util.LinkedList;

import io.fabric.sdk.android.Fabric;
import pt.it.porto.mydiabetes.BuildConfig;
import pt.it.porto.mydiabetes.data.BadgeRec;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;


public abstract class BaseActivity extends AppCompatActivity {

	//Advice activityAdvice = null;
	private int idUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (BuildConfig.USE_FABRIC) {
			Fabric.with(this, new Crashlytics());
		}
		if(!this.getComponentName().getClassName().equals("pt.it.porto.mydiabetes.ui.activities.WelcomeActivity")){
			DB_Read db = new DB_Read(getBaseContext());
			idUser = db.getId();
			db.close();
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
			if(!this.getComponentName().getClassName().equals("pt.it.porto.mydiabetes.ui.activities.WelcomeActivity")){
				DB_Write dbwrite = new DB_Write(getBaseContext());
				dbwrite.Clicks_Save(idUser,this.getComponentName().getClassName(),ev.getX(),ev.getY());
				dbwrite.close();
			}
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
		if(!this.getComponentName().getClassName().equals("pt.it.porto.mydiabetes.ui.activities.WelcomeActivity")){
			DB_Write dbwrite = new DB_Write(getBaseContext());
			dbwrite.Log_Save(idUser,this.getComponentName().getClassName());
			dbwrite.close();
		}
		Log.d("BaseActivity", "Enter activity: " + this.getComponentName().getClassName());
	}
}
