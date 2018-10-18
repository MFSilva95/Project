package pt.it.porto.mydiabetes.ui.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.it.porto.mydiabetes.BuildConfig;
import pt.it.porto.mydiabetes.R;

import pt.it.porto.mydiabetes.data.CarbsRatioData;
import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.data.Sensitivity;
import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.database.DB_Handler;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.database.FeaturesDB;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.listAdapters.homePageAdapter;
import pt.it.porto.mydiabetes.utils.CustomViewPager;

import static pt.it.porto.mydiabetes.ui.activities.SettingsImportExport.PROJECT_MANAGER_EMAIL;
import static pt.it.porto.mydiabetes.ui.activities.SettingsImportExport.backup;
import static pt.it.porto.mydiabetes.ui.activities.SettingsImportExport.backup_old_db;


public class Home extends BaseActivity {

	public static final int CHANGES_OCCURRED = 1;
	private static Boolean old_db = false;

	private NavigationView navigationView;
	private DrawerLayout drawerLayout;

	private CustomViewPager mViewPager;
	private PagerAdapter adapter;
	private BottomNavigationView bottomNavigationView;

	private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
	private static final int DB_OLD_PERMISSION_CONSTANT = 102;
	private SharedPreferences permissionStatus;
    FeaturesDB db;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);

		db = new FeaturesDB(MyDiabetesStorage.getInstance(getBaseContext()));
		if(ShouldBackupDB()){
            showBackupDialog();

        }else{
			if(!db.isFeatureActive(FeaturesDB.INITIAL_REG_DONE)){
				ShowDialogAddData();
				return;
			}else{
                setMainView(savedInstanceState);
            }
		}
	}

	public void setMainView(Bundle savedInstanceState){

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
        //mViewPager.blockSwipeRight(true);
        //mViewPager.blockSwipeLeft(true);
        if (savedInstanceState == null) {
            mViewPager.setCurrentItem(1);
        }
        else{
            mViewPager.setCurrentItem(savedInstanceState.getInt("viewpager", 0));
        }


        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
//			mViewPager.setBotNav(bottomNavigationView);
        //----------------------nav
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();
                Intent intent;

                switch (menuItem.getItemId()) {
                    case R.id.userLogbook:
                        intent = new Intent(getApplicationContext(), LogbookChartList.class);
                        startActivity(intent);
                        return true;
                    case R.id.diabetesData:
                        intent = new Intent(getApplicationContext(), Settings.class);
                        startActivity(intent);
                        return true;
                    case R.id.importAndExport:
                        checkPermissions(EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                        return true;
                    case R.id.info:
                        intent = new Intent(getApplicationContext(), Info.class);
                        startActivity(intent);
                        return true;
                    case R.id.feedback:
                        feedback();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });
        bottomNavigationView.getMenu().getItem(mViewPager.getCurrentItem()).setChecked(true);
        setupBottomNavigationView();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }
        });
    }

	public void checkPermissions(int permission_need){
		if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, permission_need);
		} else {
		    if(permission_need == EXTERNAL_STORAGE_PERMISSION_CONSTANT){
                goToImportExportActivity();
            }else{
                backup_old_db(Home.this);
                if(!db.isFeatureActive(FeaturesDB.INITIAL_REG_DONE)){
                    ShowDialogAddData();
                    return;
                }else{
                    setMainView(null);
                }
            }
		}
	}

	private void goToImportExportActivity() {
		//We've got the permission, now we can proceed further
		Intent intent = new Intent(getApplicationContext(), SettingsImportExport.class);
		startActivity(intent);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		switch (requestCode){
			case EXTERNAL_STORAGE_PERMISSION_CONSTANT:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					goToImportExportActivity();
				} else {
					Toast.makeText(getBaseContext(),"Unable to get Permission",Toast.LENGTH_LONG).show();
				}
				break;
			case DB_OLD_PERMISSION_CONSTANT:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					backup_old_db(Home.this);
                    if(!db.isFeatureActive(FeaturesDB.INITIAL_REG_DONE)){
                        ShowDialogAddData();
                        return;
                    }else{
                        setMainView(null);
                    }
				} else {
					Toast.makeText(getBaseContext(),"Unable to get Permission",Toast.LENGTH_LONG).show();
				}
				break;
		}
	}

	public Boolean ShouldBackupDB(){

        DB_Handler handler = new DB_Handler(this);
        if(handler.hasDepricatedDb()){
            return true;
        }
        return false;
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
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
						break;
					case R.id.action_register:
						mViewPager.setCurrentItem(1);
                        bottomNavigationView.getMenu().getItem(1).setChecked(true);
						break;
					case R.id.action_person:
						mViewPager.setCurrentItem(2);
                        bottomNavigationView.getMenu().getItem(2).setChecked(true);
						break;
				}
				return true;
			}
		});
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(mViewPager != null){
		    if(mViewPager.getCurrentItem()!=-1){
                outState.putInt("viewpager", mViewPager.getCurrentItem());
            }
        }
	}

	public void feedback() {
		DB_Read read = new DB_Read(this);
		Intent intent = ShareCompat.IntentBuilder.from(this)
				.setType("message/rfc822")
				.addEmailTo(PROJECT_MANAGER_EMAIL)
				.setSubject(getResources().getString(R.string.email_feedback_subject))
				.setText(getResources().getString(R.string.share_text))
				.getIntent();

		// get apps that resolve email
		Intent justEmailAppsIntent = new Intent(Intent.ACTION_SENDTO);
		justEmailAppsIntent.setType("text/plain");
		justEmailAppsIntent.setData(Uri.parse("mailto:"));
		List<ResolveInfo> activities = getPackageManager().queryIntentActivities(justEmailAppsIntent, 0);

		Intent[] extraIntents = new Intent[activities.size() - 1];
		for (int i = 0; i < activities.size() - 1; i++) {
			extraIntents[i] = (Intent) intent.clone();
			extraIntents[i].setClassName(activities.get(i).activityInfo.packageName, activities.get(i).activityInfo.name);
		}
		Intent one = (Intent) intent.clone();
		one.setClassName(activities.get(activities.size() - 1).activityInfo.packageName, activities.get(activities.size() - 1).activityInfo.name);

		Intent openInChooser = Intent.createChooser(one, null);
		openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);

		ComponentName activityResolved = openInChooser.resolveActivity(getPackageManager());
		if (activityResolved != null) {
			if (intent.resolveActivity(getPackageManager()) != null) {
				startActivity(openInChooser);
			}
		} else {
			Log.e("Share", "No email client found!");
			//TODO do something to show the error
		}
	}

	private void showBackupDialog(){
        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(this);
        builder1.setTitle(getString(R.string.db_depricated_dialog_title));
        builder1.setMessage(getString(R.string.db_depricated_dialog_description));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                getString(R.string.positiveButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        checkPermissions(DB_OLD_PERMISSION_CONSTANT);
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(


                getString(R.string.negativeButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(Home.this);
                        builder1.setTitle(getString(R.string.db_depricated_dialog_title));
                        builder1.setMessage(getString(R.string.db_depricated_backup_cancel));
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                getString(R.string.positiveButton),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if(!db.isFeatureActive(FeaturesDB.INITIAL_REG_DONE)){
                                            ShowDialogAddData();
                                            return;
                                        }else{
                                            setMainView(null);
                                        }
                                        dialog.cancel();
                                        dialog.dismiss();
                                    }
                                });

                        builder1.setNegativeButton(
                                getString(R.string.negativeButton),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        showBackupDialog();
                                        dialog.cancel();
                                        dialog.dismiss();
                                    }
                                });

                        android.app.AlertDialog alert12 = builder1.create();
                        alert12.show();

                    }
                });

        android.app.AlertDialog alert11 = builder1.create();
        alert11.show();
    }



}


