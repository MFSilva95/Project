package pt.it.porto.mydiabetes.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import pt.it.porto.mydiabetes.BuildConfig;
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
			DB_Read db = new DB_Read(this);//getBaseContext());
			idUser = db.getUserId();
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
				if(!this.getComponentName().getClassName().equals("pt.it.porto.mydiabetes.ui.activities.WelcomeActivity")){//welcome activity can crash because it may start with no DB
					DB_Write dbwrite = new DB_Write(this);//getBaseContext());
					dbwrite.Clicks_Save(idUser,this.getComponentName().getClassName(),ev.getX(),ev.getY());
					dbwrite.close();
				}
			}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	protected void onPause() {
		super.onPause();
		//Log.d("BaseActivity", "Exit activity: " + this.getComponentName().getClassName());
		if(!this.getComponentName().getClassName().equals("pt.it.porto.mydiabetes.ui.activities.WelcomeActivity")) {
			if (idUser != -1) {
				DB_Write dbwrite = new DB_Write(this);//getBaseContext());
				dbwrite.Log_Save(idUser, "EXIT ACTIVITY:" + this.getComponentName().getClassName().substring(37));
				dbwrite.close();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(!this.getComponentName().getClassName().equals("pt.it.porto.mydiabetes.ui.activities.WelcomeActivity")){
			if(idUser != -1){
				DB_Write dbwrite = new DB_Write(this);//getBaseContext());
				//dbwrite.Log_Save(idUser,this.getComponentName().getClassName());
				dbwrite.Log_Save(idUser,"ENTER ACTIVITY:"+this.getComponentName().getClassName().substring(37));
				dbwrite.close();
			}
		}
		//Log.d("BaseActivity", "Enter activity: " + this.getComponentName().getClassName());
	}
}
