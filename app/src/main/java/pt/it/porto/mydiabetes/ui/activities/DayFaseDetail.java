package pt.it.porto.mydiabetes.ui.activities;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.data.Tag;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.utils.DateUtils;


public class DayFaseDetail extends BaseActivity {

	public static final String DATA = "data";
	int idTag = 0;
	String id;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tag_detail);
		// Show the Up button in the action bar.
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		ActionBar actionBar=getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		Bundle args = getIntent().getExtras();
		if (args != null) {
			Tag toFill = null;
			if (args.containsKey(DATA)) {
				toFill = args.getParcelable(DATA);
				if (toFill != null) {
					idTag = toFill.getId();
				}

			}
			if (toFill == null) {
				DB_Read rdb = new DB_Read(this);
				id = args.getString("Id");
				idTag = Integer.parseInt(id);
				toFill = rdb.Tag_GetById(Integer.parseInt(id));


				rdb.close();
			}
			EditText name = (EditText) findViewById(R.id.et_FaseDia_Nome);
			name.setText(toFill.getName());
			EditText from = (EditText) findViewById(R.id.et_FaseDia_HourFrom);
			from.setText(toFill.getStart());
			//EditText to = (EditText) findViewById(R.id.et_FaseDia_HourTo);
			//to.setText(toFill.getEnd());
			if (idTag <= 9) {
				name.setEnabled(false);
			}
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();

		Bundle args = getIntent().getExtras();
		if (args != null) {
			inflater.inflate(R.menu.tag_detail_delete, menu);
			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					UpdateTag();
				}
			});
			if (Integer.parseInt(args.getString("Id")) <= 9) {
				MenuItem m = menu.findItem(R.id.menuItem_FaseDiaDetail_Delete);
				m.setVisible(false);
			}

		} else {
			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					AddNewTag();
				}
			});
		}

		//getSupportMenuInflater().inflate(R.menu.tag_detail, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.menuItem_FaseDiaDetail_Delete:
				DeleteTag();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}


	public void showTimePickerDialogFrom(View v) {

		String currentTime = "";
		if(((EditText) v).getText().toString().equals("")){
			Calendar registerDate = Calendar.getInstance();
			currentTime = DateUtils.getFormattedTimeSec(registerDate);
		}
		else{
			currentTime = ((EditText) v).getText().toString();
		}
		android.support.v4.app.DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.et_FaseDia_HourFrom,
				DateUtils.getTimeCalendar(currentTime));
		newFragment.show(getSupportFragmentManager(), "timePicker");
		TextView errorLabel = (TextView) findViewById(R.id.day_phase_error);
		errorLabel.setText("");
		errorLabel.setVisibility(View.GONE);
	}

//	public void showTimePickerDialogTo(View v) {
//		DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.et_FaseDia_HourTo,
//				DateUtils.getTimeCalendar(((EditText) v).getText().toString()));
//		newFragment.show(getFragmentManager(), "timePicker");
//	}

	public void AddNewTag() {

		//TODO verificar se valores derp
		EditText name = (EditText) findViewById(R.id.et_FaseDia_Nome);
		EditText hourFrom = (EditText) findViewById(R.id.et_FaseDia_HourFrom);
		//EditText hourTo = (EditText) findViewById(R.id.et_FaseDia_HourTo);

		//adicionado por zeornelas
		//obriga a colocar os valores
		if (name.getText().toString().equals("")) {
			name.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if (hourFrom.getText().toString().equals("")) {
			hourFrom.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(hourFrom, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
//		if (hourTo.getText().toString().equals("")) {
//			hourTo.requestFocus();
//			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.showSoftInput(hourTo, InputMethodManager.SHOW_IMPLICIT);
//			return;
//		}


		if(tag_time_exists(hourFrom.getText().toString(), idTag+"")){
			TextView errorLabel = (TextView) findViewById(R.id.day_phase_error);
			errorLabel.setText(R.string.error_time_overlaps);
			errorLabel.setVisibility(View.VISIBLE);
			return;
		}

		//save tag
		DB_Write wdb = new DB_Write(this);
		Tag tag = new Tag();
		tag.setName(name.getText().toString());
		//if (!hourFrom.getText().toString().equals("") && !hourTo.getText().toString().equals("")) {

		tag.setStart(hourFrom.getText().toString());
		//tag.setEnd(hourTo.getText().toString());

		wdb.Tag_Add(tag);
		wdb.close();
		finish();
	}

	public boolean tag_time_exists(String st, String id){

		DB_Read read = new DB_Read(this);
		Boolean overlaps = read.tagTimeStartExists(st, id);
		read.close();
		return overlaps;
	}


	public void UpdateTag() {
		EditText name = (EditText) findViewById(R.id.et_FaseDia_Nome);
		EditText hourFrom = (EditText) findViewById(R.id.et_FaseDia_HourFrom);
		//EditText hourTo = (EditText) findViewById(R.id.et_FaseDia_HourTo);

		//adicionado por zeornelas
		//obriga a colocar os valores
		if (name.getText().toString().equals("")) {
			name.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if (hourFrom.getText().toString().equals("")) {
			hourFrom.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(hourFrom, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
//		if (hourTo.getText().toString().equals("")) {
//			hourTo.requestFocus();
//			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.showSoftInput(hourTo, InputMethodManager.SHOW_IMPLICIT);
//			return;
//		}

		if(tag_time_exists(hourFrom.getText().toString(), idTag+"")){
			TextView errorLabel = (TextView) findViewById(R.id.day_phase_error);
			errorLabel.setText(R.string.error_time_overlaps);
			errorLabel.setVisibility(View.VISIBLE);
			return;
		}

		DB_Write wdb = new DB_Write(this);

		Tag tag = new Tag();

		tag.setId(idTag);
		tag.setName(name.getText().toString());

//		if (!hourFrom.getText().toString().equals("") && !hourTo.getText().toString().equals("")) {
		if (!hourFrom.getText().toString().equals("")) {
			tag.setStart(hourFrom.getText().toString());
//			tag.setEnd(hourTo.getText().toString());
		}

		wdb.Tag_Update(tag);
		wdb.close();
		finish();
	}

	public void DeleteTag() {
		final Context c = this;
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.tag_info))
				.setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						///Falta verificar se não está associada a nenhuma entrada da DB
						//Rever porque não elimina o registo de glicemia
						DB_Write wdb = new DB_Write(c);
						try {
							wdb.Tag_Remove(idTag);
							finish();
						} catch (Exception e) {
							Toast.makeText(c, getString(R.string.tag_info_delete_exception), Toast.LENGTH_LONG).show();
						}
						wdb.close();

					}
				})
				.setNegativeButton(getString(R.string.negativeButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Do nothing.
					}
				}).show();
	}

}
