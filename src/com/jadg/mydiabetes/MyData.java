package com.jadg.mydiabetes;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.Toast;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.EditText;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.dialogs.DatePickerFragment;

public class MyData extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_data);
		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		Spinner sp_MyData_Sex = (Spinner) findViewById(R.id.sp_MyData_Sex);
		ArrayAdapter<CharSequence> adapter_sp_MyData_Sex = ArrayAdapter.createFromResource(this, R.array.Sex, org.holoeverywhere.R.layout.simple_spinner_item);
		adapter_sp_MyData_Sex.setDropDownViewResource(org.holoeverywhere.R.layout.simple_spinner_dropdown_item);
		sp_MyData_Sex.setAdapter(adapter_sp_MyData_Sex);
		
		Spinner sp_MyData_DiabetesType = (Spinner) findViewById(R.id.sp_MyData_DiabetesType);
		ArrayAdapter<CharSequence> adapter_sp_MyData_DiabetesType = ArrayAdapter.createFromResource(this, R.array.diabetes_Type, org.holoeverywhere.R.layout.simple_spinner_item);
		adapter_sp_MyData_DiabetesType.setDropDownViewResource(org.holoeverywhere.R.layout.simple_spinner_dropdown_item);
		sp_MyData_DiabetesType.setAdapter(adapter_sp_MyData_DiabetesType);
		
		
		//Read MyData From DB
		DB_Read db = new DB_Read(this);
		Object[] obj = db.MyData_Read();
		setMyDataFromDB(obj);
		db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
       MenuInflater inflater = getSupportMenuInflater();
       inflater.inflate(R.menu.my_data, menu);
       return super.onCreateOptionsMenu(menu);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				// This ID represents the Home or Up button. In the case of this
				// activity, the Up button is shown. Use NavUtils to allow users
				// to navigate up one level in the application structure. For
				// more details, see the Navigation pattern on Android Design:
				//
				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
				//
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_MyData_Save:
				DB_Write rdb = new DB_Write(this);
				rdb.MyData_Save(getMyDataFromActivity());
				rdb.close();
				Toast.makeText(this, "Guardado", Toast.LENGTH_LONG).show();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@SuppressWarnings("deprecation")
	public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_MyData_BirthDate);
	    newFragment.setArguments(args);
	    newFragment.show(getSupportFragmentManager(), "DatePicker");
	    
	}
	
public Object[] getMyDataFromActivity(){
		
		Object[] obj = new Object[10];
		EditText name = (EditText)findViewById(R.id.et_MyData_Name);
		Spinner dType = (Spinner)findViewById(R.id.sp_MyData_DiabetesType);
		EditText iRatio = (EditText)findViewById(R.id.et_MyData_InsulinRatio);
		EditText cRatio = (EditText)findViewById(R.id.et_MyData_CarbsRatio);
		EditText lRange = (EditText)findViewById(R.id.et_MyData_LowerRange);
		EditText hRange = (EditText)findViewById(R.id.et_MyData_HigherRange);
		EditText bDate = (EditText)findViewById(R.id.et_MyData_BirthDate);
		Spinner gender = (Spinner)findViewById(R.id.sp_MyData_Sex);
		EditText height = (EditText)findViewById(R.id.et_MyData_Height);
		
		if(name.getTag() != null)
			obj[0] = Integer.parseInt(name.getTag().toString());
		else
			obj[0] = 0;
		obj[1] = name.getText().toString();
		obj[2] = dType.getSelectedItem().toString();
		obj[3] = Integer.parseInt(iRatio.getText().toString());
		obj[4] = Integer.parseInt(cRatio.getText().toString());
		obj[5] = Double.parseDouble(lRange.getText().toString());
		obj[6] = Double.parseDouble(hRange.getText().toString());
		obj[7] = bDate.getText().toString();
		obj[8] = gender.getSelectedItem().toString();
		obj[9] = Float.parseFloat(height.getText().toString());
		
		return obj;
	}

	public void setMyDataFromDB(Object[] obj) {
		if(obj!=null){
			EditText name = (EditText)findViewById(R.id.et_MyData_Name);
			Spinner dType = (Spinner)findViewById(R.id.sp_MyData_DiabetesType);
			EditText iRatio = (EditText)findViewById(R.id.et_MyData_InsulinRatio);
			EditText cRatio = (EditText)findViewById(R.id.et_MyData_CarbsRatio);
			EditText lRange = (EditText)findViewById(R.id.et_MyData_LowerRange);
			EditText hRange = (EditText)findViewById(R.id.et_MyData_HigherRange);
			EditText bDate = (EditText)findViewById(R.id.et_MyData_BirthDate);
			Spinner gender = (Spinner)findViewById(R.id.sp_MyData_Sex);
			EditText height = (EditText)findViewById(R.id.et_MyData_Height);
			
			name.setTag(obj[0].toString());
			name.setText(obj[1].toString());
			
			if(!dType.getSelectedItem().toString().equals(obj[2].toString())){
				if(dType.getSelectedItemId() == 0)
					dType.setSelection(1);
				else
					dType.setSelection(0);
			}
				
			iRatio.setText(obj[3].toString());
			cRatio.setText(obj[4].toString());
			lRange.setText(obj[5].toString());
			hRange.setText(obj[6].toString());
			bDate.setText(obj[7].toString());
			
			if(!gender.getSelectedItem().toString().equals(obj[8].toString())){
				if(gender.getSelectedItemId() == 0)
					gender.setSelection(1);
				else
					gender.setSelection(0);
			}
			height.setText(obj[9].toString());
		}
}
	
	
}
