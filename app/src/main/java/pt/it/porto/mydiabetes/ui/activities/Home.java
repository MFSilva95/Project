package pt.it.porto.mydiabetes.ui.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

	private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
	private static final int REQUEST_PERMISSION_SETTING = 101;
	private boolean sentToSettings = false;
	private SharedPreferences permissionStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);

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
		if (savedInstanceState == null) {
			mViewPager.setCurrentItem(1);
			Log.e("ENTRA","ENTRA");
		}
		else{
			mViewPager.setCurrentItem(savedInstanceState.getInt("viewpager", 0));
		}


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
						checkPermissions();
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
		Log.e("VIEW PAGER", mViewPager.getCurrentItem()+"");
		bottomNavigationView.getMenu().getItem(mViewPager.getCurrentItem()).setChecked(true);
		setupBottomNavigationView();
	}

	public void checkPermissions(){
		if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
			} else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,false)) {
						sentToSettings = true;
						Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
						Uri uri = Uri.fromParts("package", getPackageName(), null);
						intent.setData(uri);
						startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
			} else {
				//just request the permission
				ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
			}

			SharedPreferences.Editor editor = permissionStatus.edit();
			editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,true);
			editor.commit();


		} else {
			//You already have the permission, just go ahead.
			proceedAfterPermission();
		}
	}

	private void proceedAfterPermission() {
		//We've got the permission, now we can proceed further
		Intent intent = new Intent(getApplicationContext(), SettingsImportExport.class);
		startActivity(intent);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				//The External Storage Write Permission is granted to you... Continue your left job...
				proceedAfterPermission();
			} else {
				if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
					ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
				} else {
					Toast.makeText(getBaseContext(),"Unable to get Permission",Toast.LENGTH_LONG).show();
				}
			}
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_PERMISSION_SETTING) {
			if (ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
				//Got Permission
				proceedAfterPermission();
			}
		}
	}


	@Override
	protected void onPostResume() {
		super.onPostResume();
		if (sentToSettings) {
			if (ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
				//Got Permission
				proceedAfterPermission();
			}
		}
	}


	public void ShowDialogAddData() {
		Intent intent = new Intent(this, WelcomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		finish();
	}
	private void setupBottomNavigationView() {
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
				updateNavigationBarState(item.getItemId());
				return true;
			}
		});
	}

	private void updateNavigationBarState(int actionId){
		Menu menu = bottomNavigationView.getMenu();
		for (int i = 0, size = menu.size(); i < size; i++) {
			MenuItem item = menu.getItem(i);
			item.setChecked(item.getItemId() == actionId);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("viewpager", mViewPager.getCurrentItem());
		// do this for each or your Spinner
		// You might consider using Bundle.putStringArray() instead
	}



}


