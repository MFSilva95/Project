package com.jadg.mydiabetes;

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

import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.InsulinRegAdapter;
import com.jadg.mydiabetes.database.InsulinRegDataBinding;
import com.jadg.mydiabetes.dialogs.DatePickerFragment;

import android.annotation.TargetApi;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")

public class Insulin extends Activity {

	ListView insulinsList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insulin);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		FillDates();
		
		EditText datefrom = (EditText)findViewById(R.id.et_Insulin_DataFrom);
		EditText dateto = (EditText)findViewById(R.id.et_Insulin_DataTo);
		datefrom.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				fillListView(insulinsList); }
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override
			public void afterTextChanged(Editable s) { }
		});
		dateto.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				fillListView(insulinsList); }
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override
			public void afterTextChanged(Editable s) { }
		});
		
		insulinsList = (ListView)findViewById(R.id.InsulinsActivityList);
		fillListView(insulinsList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.insulin, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_Insulin:
				Intent intent = new Intent(this, InsulinDetail.class);
				startActivity(intent);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@SuppressLint("SimpleDateFormat")
	public void FillDates(){
		EditText dateago = (EditText)findViewById(R.id.et_Insulin_DataFrom);
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
        
        EditText datenow = (EditText)findViewById(R.id.et_Insulin_DataTo);
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
		
		EditText datefrom = (EditText)findViewById(R.id.et_Insulin_DataFrom);
		EditText dateto = (EditText)findViewById(R.id.et_Insulin_DataTo);
		DB_Read rdb = new DB_Read(this);
		ArrayList<InsulinRegDataBinding> allInsulins = rdb.InsulinReg_GetByDate(datefrom.getText().toString(), dateto.getText().toString());
		//HashMap<Integer, String[]> val = 
		//HashMap<Integer, String[]> val = rdb.Glycemia_GetAll();
		/*InsulinRegDataBinding insulin;
		String[] row;
		String[] datetime;
		if(val!=null){
			for (int i : val.keySet()){
				insulin = new InsulinRegDataBinding();
				row = val.get(i);
				insulin.setId(i);
				insulin.setInsulin(rdb.Insulin_GetNameById(Integer.parseInt(row[0])));
				insulin.setInsulinUnits(Double.parseDouble(row[3]));
				datetime = row[1].split(" ");
				insulin.setDate(datetime[0]);
				insulin.setTime(datetime[1]);
				insulin.setTag(rdb.Tag_GetNameById(Integer.parseInt(row[5])));
				if(row[4]!=null){
					insulin.setGlycemia(Double.parseDouble(row[4]));}
				insulin.setTargetGlycemia(row[4]);
				allInsulins.add(insulin);
			}
		}
		else{
			
		}*/
		rdb.close();
		lv.setAdapter(new InsulinRegAdapter(allInsulins, this));
	}
	
	@SuppressWarnings("deprecation")
	public void showDatePickerDialogFrom(View v){
		DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_Insulin_DataFrom);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "DatePicker");
	}
	@SuppressWarnings("deprecation")
	public void showDatePickerDialogTo(View v){
		DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_Insulin_DataTo);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "DatePicker");
	}
}
