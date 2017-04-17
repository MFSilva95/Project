package pt.it.porto.mydiabetes.ui.activities;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import info.abdolahi.CircularMusicProgressBar;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.fragments.register.PersonalDataFragment;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;
import pt.it.porto.mydiabetes.utils.LocaleUtils;


public class MyData extends BaseActivity {

	private UserInfo myData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_data);
		// Show the Up button in the action bar.
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		ActionBar actionBar=getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}


		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (inputIsValid()) {
					DB_Write rdb = new DB_Write(getBaseContext());
					rdb.MyData_Save(getMyDataFromActivity());
					rdb.close();
					Toast.makeText(getBaseContext(), getString(R.string.mydata_saved), Toast.LENGTH_LONG).show();
					finish();
				} else {
					//toast message
					Toast.makeText(getBaseContext(), getString(R.string.mydata_before_saving), Toast.LENGTH_LONG).show();
				}
			}
		});

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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
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

	public UserInfo getMyDataFromActivity() {
		EditText name = (EditText) findViewById(R.id.et_MyData_Name);
		EditText bDate = (EditText) findViewById(R.id.et_MyData_BirthDate);
		Spinner gender = (Spinner) findViewById(R.id.sp_MyData_Sex);
		EditText height = (EditText) findViewById(R.id.et_MyData_Height);

		if (name.getTag() != null)
			myData.setId(Integer.parseInt(name.getTag().toString()));
		else
			myData.setId(0);
		myData.setUsername(name.getText().toString());
		myData.setBirthday(bDate.getText().toString());
		myData.setGender(gender.getSelectedItem().toString(), this);
		myData.setHeight(Float.parseFloat(height.getText().toString()));

		return myData;
	}

	public void setMyDataFromDB(UserInfo obj) {
		if (obj != null) {
			EditText name = (EditText) findViewById(R.id.et_MyData_Name);
			EditText bDate = (EditText) findViewById(R.id.et_MyData_BirthDate);
			Spinner gender = (Spinner) findViewById(R.id.sp_MyData_Sex);
			EditText height = (EditText) findViewById(R.id.et_MyData_Height);

			name.setTag(obj.getId());
			name.setText(obj.getUsername());
			bDate.setText(obj.getBirthday());

			if (!gender.getSelectedItem().toString().equalsIgnoreCase(String.valueOf(obj.getGender()))) {
				if (gender.getSelectedItemId() == 0) {
					gender.setSelection(1);
				} else {
					gender.setSelection(0);
				}
			}
			height.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.2f", obj.getHeight()));
		}
	}

}
