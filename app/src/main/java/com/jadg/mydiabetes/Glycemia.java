package com.jadg.mydiabetes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DialogFragment;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.ListView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.database.GlycemiaAdapter;
import com.jadg.mydiabetes.database.GlycemiaDataBinding;
import com.jadg.mydiabetes.dialogs.DatePickerFragment;
import com.jadg.mydiabetes.usability.ActivityEvent;


public class Glycemia extends Activity {

	ListView glycemiasList;

	// variavel que contem o nome da janela em que vai ser contado o tempo
	// contem o tempo de inicio ou abertura dessa janela
	// no fim de fechar a janela vai conter o tempo em que a janela foi fechada
	// e vai criar uma entrada na base de dados a registar os tempos
	ActivityEvent activityEvent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_glycemia);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		FillDates();

		// bloco de codigo que verifica se o utilizador carregou numa zona
		// "vazia" do ecra. Neste caso regista o click como um missed click
		LinearLayout sv = (LinearLayout)findViewById(R.id.glycemiaScrollView);
		sv.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					DB_Write write = new DB_Write(Glycemia.this);    // gera uma nova instancia de escrita na base de dados
					write.newClick("Glycemia_Missed_Click");                // regista o clique na base de dados

					write.newMissed(event.getX(), event.getY(), "Glycemia");
					Log.d("test", event.toString());
				}
				return true;
			}
		});
		ListView sv2 = (ListView)findViewById(R.id.GlycemiasActivityList);
		sv2.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					DB_Write write = new DB_Write(Glycemia.this);    // gera uma nova instancia de escrita na base de dados
					write.newClick("Glycemia_Missed_Click");                // regista o clique na base de dados

					write.newMissed(event.getX(), event.getY(), "Glycemia");
					Log.d("test", event.toString());
				}
				return true;
			}
		});

		EditText datefrom = (EditText)findViewById(R.id.et_Glycemia_DataFrom);
		EditText dateto = (EditText)findViewById(R.id.et_Glycemia_DataTo);
		datefrom.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				fillListView(glycemiasList); }
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override
			public void afterTextChanged(Editable s) { }
		});
		dateto.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				fillListView(glycemiasList); }
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override
			public void afterTextChanged(Editable s) { }
		});
		glycemiasList = (ListView)findViewById(R.id.GlycemiasActivityList);

		fillListView(glycemiasList);

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.glycemia, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		DB_Write write = new DB_Write(this);			// gera uma nova instancia de escrita na base de dados
		switch (item.getItemId()) {
			case android.R.id.home:
				write.newClick("menuItem_Glycemia_Home");	// regista o clique na base de dados

				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_Glycemia:
				write.newClick("menuItem_Glycemia");	// regista o clique na base de dados

				Intent intent = new Intent(this, GlycemiaDetail.class);
				startActivity(intent);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Esta funcao e chamada sempre que a actividade atual passa a ser a Glycemia
	// ou seja, quando a janela a mostrar � a janela da Glycemia. Assim, � nesta
	// funcao que o timer inicia.
	@Override
	public void onResume(){
		activityEvent = new ActivityEvent(new DB_Write(this), "Glycemia");
		super.onPause();
	}

	// Esta funcao e chamada sempre que a actividade atual deixa de ser a Glycemia
	// ou seja, quando a janela a mostrar deixa de ser a janela da Glycemia. Assim,
	// � nesta funcao que o timer para e que guardamos a nova entrada na base de dados.
	@Override
	public void onPause(){
		activityEvent.stop();
		super.onPause();
	}

	public void fillListView(ListView lv){

		EditText datefrom = (EditText)findViewById(R.id.et_Glycemia_DataFrom);
		EditText dateto = (EditText)findViewById(R.id.et_Glycemia_DataTo);
		DB_Read rdb = new DB_Read(this);
		ArrayList<GlycemiaDataBinding> allGlycemias = rdb.Glycemia_GetByDate(datefrom.getText().toString(), dateto.getText().toString());
		rdb.close();
		lv.setAdapter(new GlycemiaAdapter(allGlycemias, this));
	}

	public void showDatePickerDialogFrom(View v){
		DialogFragment newFragment = new DatePickerFragment();
		Bundle args = new Bundle();
		args.putInt("textbox",R.id.et_Glycemia_DataFrom);
		newFragment.setArguments(args);
		newFragment.show(getFragmentManager(), "DatePicker");
	}
	public void showDatePickerDialogTo(View v){
		DialogFragment newFragment = new DatePickerFragment();
		Bundle args = new Bundle();
		args.putInt("textbox",R.id.et_Glycemia_DataTo);
		newFragment.setArguments(args);
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	@SuppressLint("SimpleDateFormat")
	public void FillDates(){
		EditText dateago = (EditText)findViewById(R.id.et_Glycemia_DataFrom);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, -3);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		Date newDate = cal.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(newDate);

		dateago.setText(dateString);

		EditText datenow = (EditText)findViewById(R.id.et_Glycemia_DataTo);
		c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		cal.set(year, month, day);
		newDate = cal.getTime();
		dateString = formatter.format(newDate);
		datenow.setText(dateString);
	}
}