package com.jadg.mydiabetes;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ScrollView;

import android.view.Menu;
import android.view.MenuItem;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.usability.ActivityEvent;


public class Home extends Activity {

	// variavel que contem o nome da janela em que vai ser contado o tempo
	// contem o tempo de inicio ou abertura dessa janela
	// no fim de fechar a janela vai conter o tempo em que a janela foi fechada
	// e vai criar uma entrada na base de dados a registar os tempos
	ActivityEvent activityEvent;

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
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					DB_Write write = new DB_Write(Home.this);    // gera uma nova instancia de escrita na base de dados
					write.newClick("Home_Missed_Click");                // regista o clique na base de dados

					write.newMissed(event.getX(), event.getY(), "Home");
					Log.d("test", event.toString());
				}

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

	// Esta funcao e chamada sempre que a actividade atual passa a ser a Home
	// ou seja, quando a janela a mostrar é a janela Home. Assim, é nesta
	// funcao que o timer inicia.
	@Override
	public void onResume(){
		activityEvent = new ActivityEvent(new DB_Write(this), "Home");
		super.onPause();
	}

	// Esta funcao e chamada sempre que a actividade atual deixa de ser a Home
	// ou seja, quando a janela a mostrar deixa de ser a janela Home. Assim,
	// é nesta funcao que o timer para e que guardamos a nova entrada na base de dados.
	@Override
	public void onPause(){
		activityEvent.stop();
		super.onPause();
	}
	
	
	public void ShowDialogAddData(){
		final Context c = this;
		new AlertDialog.Builder(this)
	    .setTitle(getString(R.string.title_activity_info))
	    .setMessage(getString(R.string.homeInfo))
	    .setPositiveButton(getString(R.string.okButton), new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton) {
	        	 //Falta verificar se não está associada a nenhuma entrada da DB
	        	 //Rever porque não elimina o registo de glicemia
	        	 Intent intent = new Intent(c, MyData.class);
	        	 intent.putExtra("tabPosition", 2);
	        	 startActivity(intent);
	        	 
	         }
	    }).show();
	}
	
	public void Call_DataTools(){
		DB_Write write = new DB_Write(this);	// gera uma nova instancia de escrita na base de dados
		write.newClick("Call_DataTools");		// regista o clique na base de dados

		Intent intent = new Intent(this, DataTools.class);
		startActivity(intent);
	}

	public void Call_OutrasLeituras(View view){
		DB_Write write = new DB_Write(this);	// gera uma nova instancia de escrita na base de dados
		write.newClick("Call_OutrasLeituras");	// regista o clique na base de dados

		Intent intent = new Intent(this, ExBPChoDisWei.class);
		startActivity(intent);
	}

	public void Call_Meal(View view){
		DB_Write write = new DB_Write(this);	// gera uma nova instancia de escrita na base de dados
		write.newClick("Call_Meal");			// regista o clique na base de dados

		Intent intent = new Intent(this, Meal.class);
		startActivity(intent);
	}

	public void Call_Glycemia(View view){
		DB_Write write = new DB_Write(this);	// gera uma nova instancia de escrita na base de dados
		write.newClick("Call_Glycemia");		// regista o clique na base de dados

		Intent intent = new Intent(this, Glycemia.class);
		startActivity(intent);
	}

	public void Call_Exercise(View view){
		DB_Write write = new DB_Write(this);	// gera uma nova instancia de escrita na base de dados
		write.newClick("Call_Exercise");		// regista o clique na base de dados

		Intent intent = new Intent(this, Exercise.class);
		startActivity(intent);
	}

	public void Call_Insulin(View view){
		DB_Write write = new DB_Write(this);	// gera uma nova instancia de escrita na base de dados
		write.newClick("Call_Insulin");			// regista o clique na base de dados

		Intent intent = new Intent(this, Insulin.class);
		startActivity(intent);
	}

	public void Call_Carbs(View view){
		DB_Write write = new DB_Write(this);	// gera uma nova instancia de escrita na base de dados
		write.newClick("Call_Carbs");			// regista o clique na base de dados

		Intent intent = new Intent(this, CarboHydrate.class);
		startActivity(intent);
	}
	
	//ADDED BY ZE ORNELAS
	public void Call_Logbook(View view){
		DB_Write write = new DB_Write(this);	// gera uma nova instancia de escrita na base de dados
		write.newClick("Call_Logbook");			// regista o clique na base de dados

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


