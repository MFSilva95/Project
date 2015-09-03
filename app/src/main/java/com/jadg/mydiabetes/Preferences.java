package com.jadg.mydiabetes;

import android.app.Activity;
import android.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.view.Menu;
import android.view.MenuItem;
import com.jadg.mydiabetes.fragments.Diseases;
import com.jadg.mydiabetes.fragments.Exercises;
import com.jadg.mydiabetes.fragments.Insulins;
import com.jadg.mydiabetes.fragments.Tags;
import com.jadg.mydiabetes.fragments.TargetBG;

import android.annotation.SuppressLint;



public class Preferences extends Activity {

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
        if(args!=null){
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
		public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			ft.replace(R.id.preferences_FragmentContainer, fragment);
		}

		@Override
		public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
