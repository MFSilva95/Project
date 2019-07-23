package pt.it.porto.mydiabetes.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.database.FeaturesDB;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;


public class SettingsAPP extends BaseActivity {

	private CheckBox useAutoUpdates;
	private UserInfo myData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_app);
		// Show the Up button in the action bar.
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		android.support.v7.app.ActionBar actionBar=getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		useAutoUpdates = findViewById(R.id.use_auto_updates);

		useAutoUpdates.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				FeaturesDB db = new FeaturesDB(MyDiabetesStorage.getInstance(getBaseContext()));
				db.changeFeatureStatus(FeaturesDB.ACCEPTED_NEW_TERMS, useAutoUpdates.isChecked());
			}
		});

		FeaturesDB features = new FeaturesDB(MyDiabetesStorage.getInstance(this));
		useAutoUpdates.setChecked(features.isFeatureActive(FeaturesDB.ACCEPTED_NEW_TERMS));
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

}
