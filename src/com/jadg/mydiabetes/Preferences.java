package com.jadg.mydiabetes;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jadg.mydiabetes.fragments.Diseases;
import com.jadg.mydiabetes.fragments.Exercises;
import com.jadg.mydiabetes.fragments.Insulins;
import com.jadg.mydiabetes.fragments.Tags;
import com.jadg.mydiabetes.fragments.TargetBG;


public class Preferences extends Activity {

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
		tab.setText("Fases do Dia");
        getSupportActionBar().addTab(tab);
        
        tab = getSupportActionBar().newTab();
        Fragment targetsFragment = new TargetBG();
        tab.setTabListener(new MyTabsListener(targetsFragment));
        tab.setText("Objetivos Glicemia");
        getSupportActionBar().addTab(tab);
        
        tab = getSupportActionBar().newTab();
        Fragment diseasesFragment = new Diseases();
        tab.setTabListener(new MyTabsListener(diseasesFragment));
        tab.setText("Doenças");
        getSupportActionBar().addTab(tab);
        
        tab = getSupportActionBar().newTab();
        Fragment exercisesFragment = new Exercises();
        tab.setTabListener(new MyTabsListener(exercisesFragment));
        tab.setText("Exercicios");
        getSupportActionBar().addTab(tab);
        
        tab = getSupportActionBar().newTab();
        Fragment insulinsFragment = new Insulins();
        tab.setTabListener(new MyTabsListener(insulinsFragment));
        tab.setText("Insulinas");
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
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			ft.replace(R.id.preferences_FragmentContainer, fragment);
			
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
