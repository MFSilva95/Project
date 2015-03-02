package com.jadg.mydiabetes;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ScrollView;

import android.view.Menu;
import android.view.MenuItem;
import com.jadg.mydiabetes.database.DB_Read;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")

public class Home extends Activity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		getActionBar();
		
		DB_Read read = new DB_Read(this);
		if(!read.MyData_HasData()){
			ShowDialogAddData();
		}
		read.close();
		
		final GestureDetector gestureDetector;
        gestureDetector = new GestureDetector(new MyGestureDetector());
		ScrollView sv = (ScrollView)findViewById(R.id.homeScrollView);
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
			case R.id.menuItem_Home:
				Call_DataTools();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	public void ShowDialogAddData(){
		final Context c = this;
		new AlertDialog.Builder(this)
	    .setTitle("Informa��o")
	    .setMessage("Antes de adicionar qualquer registo deve adicionar a sua informa��o!")
	    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton) {
	             //Falta verificar se n�o est� associada a nenhuma entrada da DB
	        	 //Rever porque n�o elimina o registo de glicemia
	        	 Intent intent = new Intent(c, MyData.class);
	        	 intent.putExtra("tabPosition", 4);
	        	 startActivity(intent);
	        	 
	         }
	    }).show();
	}
	
	public void Call_DataTools(){
		Intent intent = new Intent(this, DataTools.class);
		startActivity(intent);
	}
	
	public void Call_Leituras(View view){
		Intent intent = new Intent(this, GlyInsCarbs.class);
		startActivity(intent);
	}

	public void Call_OutrasLeituras(View view){
		Intent intent = new Intent(this, ExBPChoDisWei.class);
		startActivity(intent);
	}

	
	public void Call_Meal(View view){
		Intent intent = new Intent(this, Meal.class);
		startActivity(intent);
	}
	
	public void Call_Glycemia(View view){
		Intent intent = new Intent(this, Glycemia.class);
		startActivity(intent);
	}
	
	public void Call_Exercise(View view){
		Intent intent = new Intent(this, Exercise.class);
		startActivity(intent);
	}

	public void Call_Insulin(View view){
		Intent intent = new Intent(this, Insulin.class);
		startActivity(intent);
	}
	
	public void Call_Carbs(View view){
		Intent intent = new Intent(this, CarboHydrate.class);
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


