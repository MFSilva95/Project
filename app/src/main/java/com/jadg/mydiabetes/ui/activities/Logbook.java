package com.jadg.mydiabetes.ui.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.jadg.mydiabetes.R;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.ui.listAdapters.LogbookAdapter;
import com.jadg.mydiabetes.ui.listAdapters.LogbookDataBinding;
import com.jadg.mydiabetes.ui.dialogs.DatePickerFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

public class Logbook extends Activity {
	
	ListView logbookList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logbook);

		FillDates();
		
		EditText datefrom = (EditText)findViewById(R.id.et_Logbook_DataFrom);
		EditText dateto = (EditText)findViewById(R.id.et_Logbook_DataTo);
		datefrom.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				fillListView(logbookList); 
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override
			public void afterTextChanged(Editable s) { }
		});
		dateto.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				fillListView(logbookList); 
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override
			public void afterTextChanged(Editable s) { }
		});
		
		
		logbookList = (ListView)findViewById(R.id.LogbookActivityList);
		fillListView(logbookList);
		
	}
		
	@SuppressLint("SimpleDateFormat")
	public void FillDates(){
		EditText dateago = (EditText)findViewById(R.id.et_Logbook_DataFrom);
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
        
        EditText datenow = (EditText)findViewById(R.id.et_Logbook_DataTo);
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
		
		EditText datefrom = (EditText)findViewById(R.id.et_Logbook_DataFrom);
		EditText dateto = (EditText)findViewById(R.id.et_Logbook_DataTo);
		DB_Read rdb = new DB_Read(this);
		ArrayList<LogbookDataBinding> lb = rdb.getLogbook(datefrom.getText().toString(), dateto.getText().toString());
		rdb.close();
		lv.setAdapter(new LogbookAdapter(lb, this));
	}
	
	public void showDatePickerDialogFrom(View v){
		DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_Logbook_DataFrom);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void showDatePickerDialogTo(View v){
		DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_Logbook_DataTo);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "DatePicker");
	}

}
