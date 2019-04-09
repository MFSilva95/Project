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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

		Spinner sp_MyData_DiabetesType = (Spinner) findViewById(R.id.sp_MyData_DiabetesType);
		ArrayAdapter<CharSequence> adapter_sp_MyData_DiabetesType = ArrayAdapter.createFromResource(this, R.array.diabetes_Type, android.R.layout.simple_spinner_item);
		adapter_sp_MyData_DiabetesType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_MyData_DiabetesType.setAdapter(adapter_sp_MyData_DiabetesType);

		sp_MyData_DiabetesType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				DB_Write rdb = new DB_Write(getBaseContext());
				rdb.MyData_Save(getMyDataFromActivity());
				rdb.close();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});

		//Read MyData From DB
		DB_Read db = new DB_Read(this);
		myData = db.MyData_Read();
		setMyDataFromDB(myData);
		db.close();

		useActiveInsulin = (CheckBox) findViewById(R.id.use_IOB);

		useActiveInsulin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				FeaturesDB db = new FeaturesDB(MyDiabetesStorage.getInstance(getBaseContext()));
				db.changeFeatureStatus(FeaturesDB.FEATURE_INSULIN_ON_BOARD, useActiveInsulin.isChecked());
				//MyDiabetesStorage.getInstance(getBaseContext()).close_handler();
			}
		});

		FeaturesDB features = new FeaturesDB(MyDiabetesStorage.getInstance(this));
		useActiveInsulin.setChecked(features.isFeatureActive(FeaturesDB.FEATURE_INSULIN_ON_BOARD));
		//MyDiabetesStorage.getInstance(getBaseContext()).close_handler();

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


	public UserInfo getMyDataFromActivity() {
		Spinner dType = (Spinner) findViewById(R.id.sp_MyData_DiabetesType);
		myData.setDiabetesType(dType.getSelectedItemPosition());

		return myData;
	}

	public void setMyDataFromDB(UserInfo obj) {
		if (obj != null) {
			Spinner dType = (Spinner) findViewById(R.id.sp_MyData_DiabetesType);

			int diabetesType=obj.getDiabetesType();
			dType.setSelection(diabetesType);
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

	public void editFactors(View v) {
		Intent intent = new Intent(this, SettingsFactors.class);
		startActivity(intent);
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
