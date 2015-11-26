package pt.it.porto.mydiabetes.ui.activities;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.fragments.Diseases;
import pt.it.porto.mydiabetes.ui.fragments.Exercises;
import pt.it.porto.mydiabetes.ui.fragments.Insulins;
import pt.it.porto.mydiabetes.ui.fragments.Tags;
import pt.it.porto.mydiabetes.ui.fragments.TargetBG;


public class Preferences extends BaseOldActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);
		// Show the Up button in the action bar.
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getActionBar().setDisplayHomeAsUpEnabled(true);


		ActionBar.Tab tab = getActionBar().newTab();
		Fragment tagsFragment = new Tags();
		tab.setTabListener(new MyTabsListener(tagsFragment));
		tab.setText(getString(R.string.preferences_day));
		getActionBar().addTab(tab);

		tab = getActionBar().newTab();
		Fragment targetsFragment = new TargetBG();
		tab.setTabListener(new MyTabsListener(targetsFragment));
		tab.setText(R.string.preferences_target);
		getActionBar().addTab(tab);

		tab = getActionBar().newTab();
		Fragment insulinsFragment = new Insulins();
		tab.setTabListener(new MyTabsListener(insulinsFragment));
		tab.setText(R.string.preferences_insulins);
		getActionBar().addTab(tab);

		tab = getActionBar().newTab();
		Fragment diseasesFragment = new Diseases();
		tab.setTabListener(new MyTabsListener(diseasesFragment));
		tab.setText(R.string.preferences_diseases);
		getActionBar().addTab(tab);

		tab = getActionBar().newTab();
		Fragment exercisesFragment = new Exercises();
		tab.setTabListener(new MyTabsListener(exercisesFragment));
		tab.setText(R.string.preferences_exercises);
		getActionBar().addTab(tab);


		//tab = getSupportActionBar().newTab();
		//Fragment medicinesFragment = new Medicines();
		//tab.setTabListener(new MyTabsListener(medicinesFragment));
		//tab.setText("Medicamentos");
		//getSupportActionBar().addTab(tab);


		Bundle args = getIntent().getExtras();
		if (args != null) {
			getActionBar().getTabAt(args.getInt("tabPosition")).select();
		}

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}


	public void EditTagRow(View v) {
		Intent intent = new Intent(this, TagDetail.class);
		startActivity(intent);
	}

	class MyTabsListener implements ActionBar.TabListener {
		public Fragment fragment;

		public MyTabsListener(Fragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
			ft.replace(R.id.preferences_FragmentContainer, fragment);
		}

		@Override
		public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

		}

		@Override
		public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

		}
	}

}
