package pt.it.porto.mydiabetes.ui.activities;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.fragments.register.PersonalDataFragment;
import pt.it.porto.mydiabetes.utils.DateUtils;


public class MyData extends BaseOldActivity {

	private Object[] myData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_data);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Spinner sp_MyData_Sex = (Spinner) findViewById(R.id.sp_MyData_Sex);
		ArrayAdapter<CharSequence> adapter_sp_MyData_Sex = ArrayAdapter.createFromResource(this, R.array.Sex, android.R.layout.simple_spinner_item);
		adapter_sp_MyData_Sex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_MyData_Sex.setAdapter(adapter_sp_MyData_Sex);

		//Read MyData From DB
		DB_Read db_read = new DB_Read(this);
		myData = db_read.MyData_Read();
		setMyDataFromDB(myData);
		db_read.close();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.my_data, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_MyData_Save:

				if (inputIsValid()) {

					DB_Write rdb = new DB_Write(this);
					rdb.MyData_Save(getMyDataFromActivity());
					rdb.close();
					Toast.makeText(this, getString(R.string.mydata_saved), Toast.LENGTH_LONG).show();
					NavUtils.navigateUpFromSameTask(this);

					return true;
				} else {
					//toast message
					Toast.makeText(this, getString(R.string.mydata_before_saving), Toast.LENGTH_LONG).show();
				}

		}
		return super.onOptionsItemSelected(item);
	}

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_MyData_BirthDate,
				DateUtils.getDateCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public Boolean inputIsValid() {
		EditText[] obj = new EditText[3];
		obj[0] = (EditText) findViewById(R.id.et_MyData_Name);
		obj[1] = (EditText) findViewById(R.id.et_MyData_BirthDate);
		obj[2] = (EditText) findViewById(R.id.et_MyData_Height);

		for (EditText aux : obj) {
			if (aux.getText().toString().trim().length() == 0) {
				aux.setError(getString(R.string.error_field_required));
				return false;
			}
		}

		if (!PersonalDataFragment.isHeightValid(obj[2].getText().toString())) {
			obj[2].setError(getString(R.string.error_invalid_height));
			return false;
		}
		return true;
	}

	public Object[] getMyDataFromActivity() {
		EditText name = (EditText) findViewById(R.id.et_MyData_Name);
		EditText bDate = (EditText) findViewById(R.id.et_MyData_BirthDate);
		Spinner gender = (Spinner) findViewById(R.id.sp_MyData_Sex);
		EditText height = (EditText) findViewById(R.id.et_MyData_Height);

		if (name.getTag() != null)
			myData[0] = Integer.parseInt(name.getTag().toString());
		else
			myData[0] = 0;
		myData[1] = name.getText().toString();
		myData[7] = bDate.getText().toString();
		myData[8] = gender.getSelectedItem().toString();
		myData[9] = Float.parseFloat(height.getText().toString());

		return myData;
	}

	public void setMyDataFromDB(Object[] obj) {
		if (obj != null) {
			EditText name = (EditText) findViewById(R.id.et_MyData_Name);
			EditText bDate = (EditText) findViewById(R.id.et_MyData_BirthDate);
			Spinner gender = (Spinner) findViewById(R.id.sp_MyData_Sex);
			EditText height = (EditText) findViewById(R.id.et_MyData_Height);

			name.setTag(obj[0].toString());
			name.setText(obj[1].toString());
			bDate.setText(obj[7].toString());

			if (!gender.getSelectedItem().toString().equalsIgnoreCase(obj[8].toString())) {
				if (gender.getSelectedItemId() == 0)
					gender.setSelection(1);
				else
					gender.setSelection(0);
			}
			height.setText(obj[9].toString());
		}
	}

}
