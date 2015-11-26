package pt.it.porto.mydiabetes.ui.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DialogFragment;
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

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.listAdapters.CholesterolAdapter;
import pt.it.porto.mydiabetes.ui.listAdapters.CholesterolDataBinding;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;








public class Cholesterol extends Activity {

	ListView choList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cholesterol);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		FillDates();
		choList = (ListView)findViewById(R.id.CholesterolActivityList);
		fillListView(choList);
		
		EditText datefrom = (EditText)findViewById(R.id.et_Cholesterol_DataFrom);
		EditText dateto = (EditText)findViewById(R.id.et_Cholesterol_DataTo);
		datefrom.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				fillListView(choList); }
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override
			public void afterTextChanged(Editable s) { }
		});
		dateto.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				fillListView(choList); }
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override
			public void afterTextChanged(Editable s) { }
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cholesterol, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_Cholesterol:
				Intent intent = new Intent(this, CholesterolDetail.class);
				startActivity(intent);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void showDatePickerDialogFrom(View v){
		DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_Cholesterol_DataFrom);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "DatePicker");
	}
	public void showDatePickerDialogTo(View v){
		DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_Cholesterol_DataTo);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "DatePicker");
	}
	@SuppressLint("SimpleDateFormat")
	public void FillDates(){
		EditText dateago = (EditText)findViewById(R.id.et_Cholesterol_DataFrom);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, -180);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        
        Calendar cal = Calendar.getInstance();
	    cal.set(year, month, day);
	    Date newDate = cal.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    String dateString = formatter.format(newDate);
        
	    dateago.setText(dateString);
        
        EditText datenow = (EditText)findViewById(R.id.et_Cholesterol_DataTo);
        c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        cal.set(year, month, day);
	    newDate = cal.getTime();
	    dateString = formatter.format(newDate);
        datenow.setText(dateString);
	}
	
	public void fillListView(ListView lv){
		EditText datefrom = (EditText)findViewById(R.id.et_Cholesterol_DataFrom);
		EditText dateto = (EditText)findViewById(R.id.et_Cholesterol_DataTo);
		DB_Read rdb = new DB_Read(this);
		ArrayList<CholesterolDataBinding> allcho = rdb.Cholesterol_GetBtDate(datefrom.getText().toString(), dateto.getText().toString());
		rdb.close();
		lv.setAdapter(new CholesterolAdapter(allcho, this));
	}
}