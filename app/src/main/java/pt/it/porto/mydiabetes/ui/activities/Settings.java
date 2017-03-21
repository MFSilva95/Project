package pt.it.porto.mydiabetes.ui.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import pt.it.porto.mydiabetes.BuildConfig;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.database.FeaturesDB;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.dialogs.FeatureIOBDialog;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.LocaleUtils;


public class Settings extends BaseActivity {

	private CheckBox useActiveInsulin;
	private UserInfo myData;
	private FloatingActionButton fab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		// Show the Up button in the action bar.
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		android.support.v7.app.ActionBar actionBar=getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (inputIsValid()) {

					DB_Write rdb = new DB_Write(getBaseContext());
					rdb.MyData_Save(getMyDataFromActivity());
					rdb.close();
					FeaturesDB db = new FeaturesDB(MyDiabetesStorage.getInstance(getBaseContext()));
					db.changeFeatureStatus(FeaturesDB.FEATURE_INSULIN_ON_BOARD, useActiveInsulin.isChecked());
					Toast.makeText(getBaseContext(), getString(R.string.mydata_saved), Toast.LENGTH_LONG).show();
					finish();
				} else {
					//toast message
					Toast.makeText(getBaseContext(), getString(R.string.mydata_before_saving), Toast.LENGTH_LONG).show();
				}

			}
		});

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

		if(!BuildConfig.IOB_AVAILABLE){
			findViewById(R.id.block_iob).setVisibility(View.GONE);
		}
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

	public UserInfo getMyDataFromActivity() {
		Spinner dType = (Spinner) findViewById(R.id.sp_MyData_DiabetesType);
		EditText iRatio = (EditText) findViewById(R.id.et_MyData_InsulinRatio);
		EditText cRatio = (EditText) findViewById(R.id.et_MyData_CarbsRatio);
		EditText lRange = (EditText) findViewById(R.id.et_MyData_LowerRange);
		EditText hRange = (EditText) findViewById(R.id.et_MyData_HigherRange);

		myData.setDiabetesType(dType.getSelectedItem().toString());
		myData.setInsulinRatio(Integer.parseInt(iRatio.getText().toString()));
		myData.setCarbsRatio(Integer.parseInt(cRatio.getText().toString()));
		myData.setLowerRange(Integer.parseInt(lRange.getText().toString()));
		myData.setHigherRange(Integer.parseInt(hRange.getText().toString()));

		return myData;
	}

	public void setMyDataFromDB(UserInfo obj) {
		if (obj != null) {
			Spinner dType = (Spinner) findViewById(R.id.sp_MyData_DiabetesType);
			EditText iRatio = (EditText) findViewById(R.id.et_MyData_InsulinRatio);
			EditText cRatio = (EditText) findViewById(R.id.et_MyData_CarbsRatio);
			EditText lRange = (EditText) findViewById(R.id.et_MyData_LowerRange);
			EditText hRange = (EditText) findViewById(R.id.et_MyData_HigherRange);

			String diabetesType=obj.getDiabetesType().getValue(this);
			try {
				dType.setSelection(Integer.parseInt(diabetesType));
			} catch (NumberFormatException e) {
				for (int i = 0; i < dType.getAdapter().getCount(); i++) {
					if (dType.getAdapter().getItem(i).toString().equalsIgnoreCase(diabetesType)) {
						dType.setSelection(i);
						break;
					}
				}
			}
			iRatio.setText(String.format(LocaleUtils.MY_LOCALE, "%d", obj.getInsulinRatio()));
			cRatio.setText(String.format(LocaleUtils.MY_LOCALE, "%d", obj.getCarbsRatio()));
			lRange.setText(String.format(LocaleUtils.MY_LOCALE, "%d", obj.getLowerRange()));
			hRange.setText(String.format(LocaleUtils.MY_LOCALE, "%d", obj.getHigherRange()));
		}
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
		Intent intent = new Intent(this, SettingsInsulins.class);
		startActivity(intent);
	}

	public void editGlycemia(View view) {
		Intent intent = new Intent(this, SettingsGlycemia.class);
		startActivity(intent);
	}

	public void editDayFases(View view) {
		Intent intent = new Intent(this, SettingsDayFases.class);
		startActivity(intent);
	}
}
