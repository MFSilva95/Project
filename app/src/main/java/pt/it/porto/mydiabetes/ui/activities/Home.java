package pt.it.porto.mydiabetes.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;



import pt.it.porto.mydiabetes.BuildConfig;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.adviceSystem.yapDroid.YapDroid;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.listAdapters.homePageAdapter;
import pt.it.porto.mydiabetes.utils.CustomViewPager;


public class Home extends BaseActivity {

	public static final int CHANGES_OCCURRED = 1;

	private NavigationView navigationView;
	private DrawerLayout drawerLayout;



	private YapDroid yapDroid;

	private CustomViewPager mViewPager;
	private PagerAdapter adapter;
	private BottomNavigationView bottomNavigationView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		DB_Read read = new DB_Read(this);
		if (!read.MyData_HasData()) {
			ShowDialogAddData();
			read.close();
			return;
		}
		read.close();

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

		ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
				this, drawerLayout, toolbar,
				R.string.navigation_drawer_open, R.string.navigation_drawer_close
		);

		drawerLayout.addDrawerListener(mDrawerToggle);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		mDrawerToggle.syncState();


		mViewPager = (CustomViewPager) super.findViewById(R.id.content_home_fragment);
		adapter = new homePageAdapter(super.getSupportFragmentManager());
		mViewPager.setAdapter(adapter);
		mViewPager.setOffscreenPageLimit(1);
		mViewPager.blockSwipeRight(true);
		mViewPager.blockSwipeLeft(true);
		mViewPager.setCurrentItem(1);

		bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

		//----------------------nav
		navigationView = (NavigationView) findViewById(R.id.navigation_view);

        if(!BuildConfig.TASKS_AVAILABLE){
			navigationView.getMenu().findItem(R.id.userTasks).setVisible(false);
        }

		navigationView.setItemIconTintList(null);
		navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			// This method will trigger on item Click of navigation menu
			@Override
			public boolean onNavigationItemSelected(MenuItem menuItem) {
				drawerLayout.closeDrawers();
				Intent intent;

				switch (menuItem.getItemId()) {
					case R.id.userTasks:
						intent = new Intent(getApplicationContext(), TaskListActivity.class);
						startActivity(intent);
						return true;
					case R.id.userLogbook:
						intent = new Intent(getApplicationContext(), LogbookChartList.class);
						startActivity(intent);
						return true;
					case R.id.personalData:
						intent = new Intent(getApplicationContext(), MyData.class);
						startActivity(intent);
						return true;
					case R.id.diabetesData:
						intent = new Intent(getApplicationContext(), Settings.class);
						startActivity(intent);
						return true;
					case R.id.importAndExport:
						intent = new Intent(getApplicationContext(), SettingsImportExport.class);
						startActivity(intent);
						return true;
					case R.id.info:
						intent = new Intent(getApplicationContext(), Info.class);
						startActivity(intent);
						return true;
					default:
						Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
						return true;
				}
			}
		});

		setupBottomNavigationView();
	}

	public void ShowDialogAddData() {
		Intent intent = new Intent(this, WelcomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		finish();
	}
	private void setupBottomNavigationView() {
		Menu bottomNavigationViewMenu = bottomNavigationView.getMenu();
		bottomNavigationViewMenu.findItem(R.id.action_register).setChecked(true);

		bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {


				switch (item.getItemId()) {
					case R.id.action_health:
						mViewPager.setCurrentItem(0);
						break;
					case R.id.action_register:
						mViewPager.setCurrentItem(1);
						break;
					case R.id.action_person:
						mViewPager.setCurrentItem(2);
						break;
				}
				return true;
			}
		});
	}

}


