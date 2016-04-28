package pt.it.porto.mydiabetes.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import pt.it.porto.mydiabetes.R;

public class FormActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_form);

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

}
