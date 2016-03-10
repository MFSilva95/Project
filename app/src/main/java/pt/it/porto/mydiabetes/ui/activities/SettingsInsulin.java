package pt.it.porto.mydiabetes.ui.activities;

import android.app.ActionBar;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.database.FeaturesDB;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.dialogs.FeatureIOBDialog;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.LocaleUtils;


public class SettingsInsulin extends BaseOldActivity {

	private CheckBox useActiveInsulin;
	private Object[] myData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_insulin_calc);
		// Show the Up button in the action bar.
		ActionBar actionbar = getActionBar();
		if (actionbar != null) {
			actionbar.setDisplayHomeAsUpEnabled(true);
		}

		Spinner sp_MyData_DiabetesType = (Spinner) findViewById(R.id.sp_MyData_DiabetesType);
		ArrayAdapter<CharSequence> adapter_sp_MyData_DiabetesType = ArrayAdapter.createFromResource(this, R.array.diabetes_Type, android.R.layout.simple_spinner_item);
		adapter_sp_MyData_DiabetesType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_MyData_DiabetesType.setAdapter(adapter_sp_MyData_DiabetesType);

		//Read MyData From DB
		DB_Read db = new DB_Read(this);
		myData = db.MyData_Read();
		setMyDataFromDB(myData);
		db.close();

		useActiveInsulin = (CheckBox) findViewById(R.id.use_IOB);
		FeaturesDB features = new FeaturesDB(MyDiabetesStorage.getInstance(this));
		useActiveInsulin.setChecked(features.isFeatureActive(FeaturesDB.FEATURE_INSULIN_ON_BOARD));
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

				if (inputIsValid()) {

					DB_Write rdb = new DB_Write(this);
					rdb.MyData_Save(getMyDataFromActivity());
					rdb.close();
					FeaturesDB db = new FeaturesDB(MyDiabetesStorage.getInstance(this));
					db.changeFeatureStatus(FeaturesDB.FEATURE_INSULIN_ON_BOARD, useActiveInsulin.isChecked());
					Toast.makeText(this, getString(R.string.mydata_saved), Toast.LENGTH_LONG).show();

					//mandar para a actividade das insulinas
					DB_Read read = new DB_Read(this);
					if (!read.Insulin_HasInsulins()) {
						read.close();
						ShowDialogAddInsulin();
					} else {
						NavUtils.navigateUpFromSameTask(this);
					}


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

	//corrige erro ao gravar
	// os spinners não são verificados porque incialmente têm sempre valor
	public boolean inputIsValid() {
		EditText[] obj = new EditText[4];
		obj[0] = (EditText) findViewById(R.id.et_MyData_InsulinRatio);
		obj[1] = (EditText) findViewById(R.id.et_MyData_CarbsRatio);
		obj[2] = (EditText) findViewById(R.id.et_MyData_LowerRange);
		obj[3] = (EditText) findViewById(R.id.et_MyData_HigherRange);

		for (EditText aux : obj) {
			if (aux.getText().toString().trim().length() == 0) {
				aux.setError(getString(R.string.error_field_required));
				return false;
			}
		}
		return true;
	}

	public Object[] getMyDataFromActivity() {
		Spinner dType = (Spinner) findViewById(R.id.sp_MyData_DiabetesType);
		EditText iRatio = (EditText) findViewById(R.id.et_MyData_InsulinRatio);
		EditText cRatio = (EditText) findViewById(R.id.et_MyData_CarbsRatio);
		EditText lRange = (EditText) findViewById(R.id.et_MyData_LowerRange);
		EditText hRange = (EditText) findViewById(R.id.et_MyData_HigherRange);

		myData[2] = dType.getSelectedItem().toString();
		myData[3] = Integer.parseInt(iRatio.getText().toString());
		myData[4] = Integer.parseInt(cRatio.getText().toString());
		myData[5] = Integer.parseInt(lRange.getText().toString());
		myData[6] = Integer.parseInt(hRange.getText().toString());

		return myData;
	}

	public void setMyDataFromDB(Object[] obj) {
		if (obj != null) {
			Spinner dType = (Spinner) findViewById(R.id.sp_MyData_DiabetesType);
			EditText iRatio = (EditText) findViewById(R.id.et_MyData_InsulinRatio);
			EditText cRatio = (EditText) findViewById(R.id.et_MyData_CarbsRatio);
			EditText lRange = (EditText) findViewById(R.id.et_MyData_LowerRange);
			EditText hRange = (EditText) findViewById(R.id.et_MyData_HigherRange);

			try {
				dType.setSelection(Integer.parseInt(obj[2].toString()));
			} catch (NumberFormatException e) {
				// old format, we should deprecate soon
				if (!dType.getSelectedItem().toString().equals(obj[2].toString())) {
					if (dType.getSelectedItemId() == 0)
						dType.setSelection(1);
					else
						dType.setSelection(0);
				}
			}
			iRatio.setText(String.format(LocaleUtils.MY_LOCALE, "%d", (int) ((double) obj[3])));
			cRatio.setText(String.format(LocaleUtils.MY_LOCALE, "%d", (int) ((double) obj[4])));
			lRange.setText(String.format(LocaleUtils.MY_LOCALE, "%d", (int) ((double) obj[5])));
			hRange.setText(String.format(LocaleUtils.MY_LOCALE, "%d", (int) ((double) obj[6])));
		}
	}

	public void ShowDialogAddInsulin() {
		final Context c = this;
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.title_activity_info))
				.setMessage(getString(R.string.mydata_next_add_insulin))
				.setPositiveButton(getString(R.string.okButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB
						//Rever porque não elimina o registo de glicemia
						Intent intent = new Intent(c, Preferences.class);
						intent.putExtra("tabPosition", 2);
						startActivity(intent);
						finish();
					}
				}).show();
	}


	public void showIOBDialog(View v) {
		FeatureIOBDialog dialog = new FeatureIOBDialog();
		dialog.setCancelable(true);
		dialog.setListener(new FeatureIOBDialog.ActivateFeatureDialogListener() {
			@Override
			public void useFeature() {
				useActiveInsulin.setChecked(true);
			}

			@Override
			public void notUseFeature() {
				useActiveInsulin.setChecked(false);
			}
		});
		dialog.show(getFragmentManager(), "iob");
	}

	public void editInsulins(View v) {
		Intent intent = new Intent(this, Insulins.class);
		startActivity(intent);
	}

	public void insulinTargets(View view) {
		Intent intent = new Intent(this, SettingsInsulinTargets.class);
		startActivity(intent);
	}
}
