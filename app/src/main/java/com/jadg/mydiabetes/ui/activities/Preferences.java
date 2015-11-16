package com.jadg.mydiabetes.ui.activities;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import com.jadg.mydiabetes.R;
import com.jadg.mydiabetes.ui.fragments.Diseases;
import com.jadg.mydiabetes.ui.fragments.Exercises;
import com.jadg.mydiabetes.ui.fragments.Insulins;
import com.jadg.mydiabetes.ui.fragments.Tags;
import com.jadg.mydiabetes.ui.fragments.TargetBG;


public class Preferences extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);
		// Show the Up button in the action bar.
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		ActionBar.Tab tab = getSupportActionBar().newTab();
		Fragment tagsFragment = new Tags();
		tab.setTabListener(new MyTabsListener(tagsFragment));
		tab.setText(getString(R.string.preferences_day));
		getSupportActionBar().addTab(tab);
        
        tab = getSupportActionBar().newTab();
        Fragment targetsFragment = new TargetBG();
        tab.setTabListener(new MyTabsListener(targetsFragment));
        tab.setText(R.string.preferences_target);
		getSupportActionBar().addTab(tab);
        
        tab = getSupportActionBar().newTab();
        Fragment insulinsFragment = new Insulins();
        tab.setTabListener(new MyTabsListener(insulinsFragment));
        tab.setText(R.string.preferences_insulins);
		getSupportActionBar().addTab(tab);
        
        tab = getSupportActionBar().newTab();
        Fragment diseasesFragment = new Diseases();
        tab.setTabListener(new MyTabsListener(diseasesFragment));
        tab.setText(R.string.preferences_diseases);
		getSupportActionBar().addTab(tab);
        
        tab = getSupportActionBar().newTab();
        Fragment exercisesFragment = new Exercises();
        tab.setTabListener(new MyTabsListener(exercisesFragment));
        tab.setText(R.string.preferences_exercises);
		getSupportActionBar().addTab(tab);
        
        
        
        //tab = getSupportActionBar().newTab();
        //Fragment medicinesFragment = new Medicines();
        //tab.setTabListener(new MyTabsListener(medicinesFragment));
        //tab.setText("Medicamentos");
        //getSupportActionBar().addTab(tab);
		
        
        Bundle args = getIntent().getExtras();
        if(args!=null){
			getSupportActionBar().getTabAt(args.getInt("tabPosition")).select();
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
	
	
	public void EditTagRow(View v){
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

		}

		@Override
		public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

		}

		@Override
		public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

		}
	}

}
