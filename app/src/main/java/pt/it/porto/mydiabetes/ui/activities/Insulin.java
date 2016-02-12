package pt.it.porto.mydiabetes.ui.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.listAdapters.InsulinRegAdapter;
import pt.it.porto.mydiabetes.ui.listAdapters.InsulinRegDataBinding;
import pt.it.porto.mydiabetes.utils.DateUtils;


public class Insulin extends Activity {

	ListView insulinsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insulin);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		FillDates();

		EditText datefrom = (EditText) findViewById(R.id.et_Insulin_DataFrom);
		EditText dateto = (EditText) findViewById(R.id.et_Insulin_DataTo);
		datefrom.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				fillListView(insulinsList);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		dateto.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				fillListView(insulinsList);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		insulinsList = (ListView) findViewById(R.id.InsulinsActivityList);
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

	public void FillDates() {
		EditText dateago = (EditText) findViewById(R.id.et_Insulin_DataFrom);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -3);
		dateago.setText(DateUtils.getFormattedDate(calendar));

		EditText datenow = (EditText) findViewById(R.id.et_Insulin_DataTo);
		calendar = Calendar.getInstance();
		datenow.setText(DateUtils.getFormattedDate(calendar));
	}


	public void fillListView(ListView lv) {

		EditText datefrom = (EditText) findViewById(R.id.et_Insulin_DataFrom);
		EditText dateto = (EditText) findViewById(R.id.et_Insulin_DataTo);
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

	public void showDatePickerDialogFrom(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_Insulin_DataFrom,
				DateUtils.getDateCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void showDatePickerDialogTo(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_Insulin_DataTo,
				DateUtils.getDateCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}
}
