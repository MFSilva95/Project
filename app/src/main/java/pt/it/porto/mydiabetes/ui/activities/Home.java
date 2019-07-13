package pt.it.porto.mydiabetes.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.cdev.achievementview.AchievementView;

import java.util.Calendar;
import java.util.List;

import pt.it.porto.mydiabetes.BuildConfig;
import pt.it.porto.mydiabetes.R;

import pt.it.porto.mydiabetes.database.DB_Handler;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.database.FeaturesDB;
import pt.it.porto.mydiabetes.database.MyDiabetesContract;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.database.Preferences;
import pt.it.porto.mydiabetes.database.Usage;
import pt.it.porto.mydiabetes.sync.ServerSync;
import pt.it.porto.mydiabetes.ui.charts.data.Logbook;
import pt.it.porto.mydiabetes.ui.dialogs.RankWebSyncDialog;
import pt.it.porto.mydiabetes.ui.listAdapters.homePageAdapter;
import pt.it.porto.mydiabetes.utils.CustomViewPager;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.SyncAlarm;

import static pt.it.porto.mydiabetes.ui.activities.SettingsImportExport.PROJECT_MANAGER_EMAIL;


public class Home extends BaseActivity {

	public static final int CHANGES_OCCURRED = 1;
	private static Boolean old_db = false;

	private NavigationView navigationView;
	private DrawerLayout drawerLayout;

	private CustomViewPager mViewPager;
	private homePageAdapter adapter;
	private BottomNavigationView bottomNavigationView;

	private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
	private static final int DB_OLD_PERMISSION_CONSTANT = 102;
    private static final int INIT_PERMISSION_REQUEST = 103;

    private static final String FROM_WIDGET = "FROM_WIDGET";
    private static final String FROM_WIDGET_TO_LOGBOOK = "FROM_WIDGET_TO_LOGBOOK";


    private static int idUser;

	private SharedPreferences permissionStatus;
    FeaturesDB db_features;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

        /**
         * WIDGET CALL
         */

	    if (this.getIntent() != null) {
            if (this.getIntent().getStringExtra(FROM_WIDGET) != null) {
                logSave("Widget:NewHomeRegistry");
                Intent intent = new Intent(this, NewHomeRegistry.class);
                startActivity(intent);
            }
            if (this.getIntent().getStringExtra(FROM_WIDGET_TO_LOGBOOK) != null) {
                logSave("Widget:LogbookChartList");
                Intent intent = new Intent(this, LogbookChartList.class);
                startActivity(intent);
            }
        }



        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
        permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);

        if(BuildConfig.SYNC_AVAILABLE) {
            try {
                setupAlarm();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }

        DB_Read db = new DB_Read(this);//getBaseContext());
        idUser = db.getUserId();
        db.close();

        db_features = new FeaturesDB(MyDiabetesStorage.getInstance(getBaseContext()));
        boolean accepted_terms = db_features.isFeatureActive(FeaturesDB.ACCEPTED_TERMS);
        boolean initial_reg_done = db_features.isFeatureActive(FeaturesDB.INITIAL_REG_DONE);
        //MyDiabetesStorage.getInstance(getBaseContext()).close_handler();

        if(!accepted_terms){
            showTermsOfService();
        }else{
            if(!initial_reg_done){
                if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, INIT_PERMISSION_REQUEST);
                }else{
                    if(ShouldBackupDB()){
                        showBackupDialog();
                    }else{
                        ShowDialogAddData();
                        return;
                    }
                }
            }else{
                if(ShouldBackupDB()){
                    showBackupDialog();
                }else{
                    setMainView(savedInstanceState);
                }
            }
        }
	}

    private void setupSyncAlarm() {
        SharedPreferences preferences = pt.it.porto.mydiabetes.database.Preferences.getPreferences(this);
        if (!preferences.contains(SyncAlarm.SYNC_ALARM_PREFERENCE)) { // only sets it if needed
            AlarmManager alm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, SyncAlarm.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            Calendar calendar = Calendar.getInstance();
            if (preferences.contains(SyncAlarm.SYNC_ALARM_LAST_SYNC)) {
                calendar.setTimeInMillis(preferences.getLong(SyncAlarm.SYNC_ALARM_LAST_SYNC, System.currentTimeMillis()));
            } else {
                Usage usage = new Usage(MyDiabetesStorage.getInstance(this));
                String date = usage.getOldestRegist();
                try {
                    try {
                        calendar.setTime(DateUtils.parseDate(date).getTime());
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    return;
                }
            }
            calendar.roll(Calendar.DAY_OF_YEAR, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 21); // Maybe change later?

            alm.set(AlarmManager.RTC, calendar.getTimeInMillis(), alarmIntent);

            preferences.edit().putInt(SyncAlarm.SYNC_ALARM_PREFERENCE, 1).apply();
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

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //Called when a drawer's position changes.
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //Called when a drawer has settled in a completely open state.
                //The drawer is interactive at this point.
                // If you have 2 drawers (left and right) you can distinguish
                // them by using id of the drawerView. int id = drawerView.getUserId();
                // id will be your layout's id: for example R.id.left_drawer
                if(idUser!=-1){
                    DB_Write dbwrite = new DB_Write(drawerView.getContext());//getBaseContext());
                    dbwrite.Log_Save(idUser,"Drawer_opened");
                    dbwrite.close();
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                // Called when a drawer has settled in a completely closed state.
                if(idUser!=-1){
                    DB_Write dbwrite = new DB_Write(drawerView.getContext());//getBaseContext());
                    dbwrite.Log_Save(idUser,"Drawer_closed");
                    dbwrite.close();
                }
                //Log.i("test", "onDrawerClosed: ");
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Called when the drawer motion state changes. The new state will be one of STATE_IDLE, STATE_DRAGGING or STATE_SETTLING.
            }
        });










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
		}else{
            if(permission_need == EXTERNAL_STORAGE_PERMISSION_CONSTANT){
                goToImportExportActivity();
            }
        }
	}


    public void ShowDialogMsg(String msg) {
        new android.app.AlertDialog.Builder(this).setTitle(R.string.information).setMessage(msg).show();
    }

    final private int WEBVIEW = 332;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Log.i("cenas", "onActivityResult: TESTE TESTE TESTE");

        if (requestCode == WEBVIEW && resultCode == RESULT_OK) {
            try {
                ServerSync.getInstance(this).testCredentials(new ServerSync.ServerSyncListener() {
                    @Override
                    public void onSyncSuccessful() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ShowDialogMsg(getBaseContext().getString(R.string.upload_successful));
                            }
                        });
                    }

                    @Override
                    public void onSyncUnSuccessful() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ShowDialogMsg(getBaseContext().getString(R.string.upload_failed));
                            }
                        });
                    }

                    @Override
                    public void noNetworkAvailable() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ShowDialogMsg(getBaseContext().getString(R.string.upload_failed));
                            }
                        });
                    }
                });
            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShowDialogMsg(getBaseContext().getString(R.string.upload_failed));
                    }
                });
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
                    Toast.makeText(getBaseContext(),R.string.all_permissions,Toast.LENGTH_LONG).show();
				}
				break;
			case DB_OLD_PERMISSION_CONSTANT:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					//backup_old_db(Home.this);
                    if(!db_features.isFeatureActive(FeaturesDB.INITIAL_REG_DONE)){
                        ShowDialogAddData();
                        return;
                    }else{
                        setMainView(null);
                    }
				} else {
					Toast.makeText(getBaseContext(),R.string.all_permissions,Toast.LENGTH_LONG).show();
				}
				break;
            case INIT_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(ShouldBackupDB()){
                        showBackupDialog();

                    }else{
                        if(!db_features.isFeatureActive(FeaturesDB.INITIAL_REG_DONE)){
                            ShowDialogAddData();
                            return;
                        }else{
                            setMainView(null);
                        }
                    }
                } else {
                    Toast.makeText(getBaseContext(),R.string.all_permissions,Toast.LENGTH_LONG).show();
                    if(!db_features.isFeatureActive(FeaturesDB.INITIAL_REG_DONE)){
                        ShowDialogAddData();
                        return;
                    }else{
                        setMainView(null);
                    }
                }
                break;
		}
	}

	public Boolean ShouldBackupDB(){

	    DB_Handler handler = new DB_Handler(this);
        Boolean deprecated = handler.isDBdeprecated(handler.getReadableDatabase());
        handler.close();
        return deprecated;
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
                DB_Write dbwrite = new DB_Write(getBaseContext());//getBaseContext());
				switch (item.getItemId()) {
					case R.id.action_health:
                        dbwrite.Clicks_Save(idUser,"leftHomePage",-1,-1);
                        dbwrite.close();
                        //Log.i("cenas", "getItem: 1");
						mViewPager.setCurrentItem(0);
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        logSave("Home:homeLeftFragment");
						break;
					case R.id.action_register:
                        dbwrite.Clicks_Save(idUser,"middleHomePage",-1,-1);
                        dbwrite.close();
                        //Log.i("cenas", "getItem: 2");
						mViewPager.setCurrentItem(1);
                        bottomNavigationView.getMenu().getItem(1).setChecked(true);
                        logSave("Home:homeMiddleFragment");
						break;
					case R.id.action_person:
                        dbwrite.Clicks_Save(idUser,"rightHomePage",-1,-1);
                        dbwrite.close();
                        //Log.i("cenas", "getItem: 3");
						mViewPager.setCurrentItem(2);
                        bottomNavigationView.getMenu().getItem(2).setChecked(true);
                        logSave("Home:homeRightFragment");
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
            Toast.makeText(getApplicationContext(), R.string.error_email, Toast.LENGTH_SHORT).show();
		}
	}

	private void removeDepricate(){
	    DB_Handler h = new DB_Handler(this);
	    SQLiteDatabase db = h.getWritableDatabase();
        Cursor result = db.query(MyDiabetesContract.Feature.TABLE_NAME, new String[]{MyDiabetesContract.Feature.COLUMN_NAME_NAME},
                MyDiabetesContract.Feature.COLUMN_NAME_NAME + "=?", new String[]{"db_deprecated"}, null, null, null, "1");
        if(result.getCount()==0){
            return;
        }
        ContentValues toInsert = new ContentValues();
        toInsert.put(MyDiabetesContract.Feature.COLUMN_NAME_ACTIVATED, 0);
        db.update(MyDiabetesContract.Feature.TABLE_NAME, toInsert, MyDiabetesContract.Feature.COLUMN_NAME_NAME + "=?", new String[]{"db_deprecated"});
        db.close();
        h.close();
    }

	private void showBackupDialog(){
        removeDepricate();
        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(this);
        builder1.setTitle(getString(R.string.db_depricated_dialog_title));
        builder1.setMessage(getString(R.string.db_depricated_dialog_description));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                getString(R.string.positiveButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(!db_features.isFeatureActive(FeaturesDB.INITIAL_REG_DONE)){
                            ShowDialogAddData();
                            return;
                        }else{
                            setMainView(null);
                        }
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
                                        DB_Handler h = new DB_Handler(Home.this);
                                        if(!db_features.isFeatureActive(FeaturesDB.INITIAL_REG_DONE)){
                                            h.deleteOldBackup();
                                            h.close();
                                            ShowDialogAddData();
                                            return;
                                        }else{
                                            h.deleteOldBackup();
                                            h.close();
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



    private void showTermsOfService() {

        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(this);
        builder1.setTitle(getString(R.string.terms_of_service_title));
        builder1.setMessage(getText(R.string.terms_of_service_description));
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                getString(R.string.positiveButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        db_features.changeFeatureStatus(FeaturesDB.ACCEPTED_TERMS, true);

                        if (!db_features.isFeatureActive(FeaturesDB.INITIAL_REG_DONE)) {
                            if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, INIT_PERMISSION_REQUEST);
                            } else {
                                if (ShouldBackupDB()) {
                                    showBackupDialog();
                                } else {
                                    ShowDialogAddData();
                                    return;
                                }
                            }
                        } else {
                            if (ShouldBackupDB()) {
                                showBackupDialog();
                            } else {
                                setMainView(null);
                            }
                        }
                        dialog.cancel();
                    }
                });

        android.app.AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void logSave (String activity) {
        DB_Read db = new DB_Read(getBaseContext());
        int idUser = db.getUserId();
        db.close();

        if(idUser != -1){
            DB_Write dbwrite = new DB_Write(getBaseContext());
            dbwrite.Log_Save(idUser,activity);
            dbwrite.close();
        }
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        if(mViewPager!=null){
            if (mViewPager.getCurrentItem()==0) logSave("Home:homeLeftFragment");
            else if (mViewPager.getCurrentItem()==1) logSave("Home:homeMiddleFragment");
            else if (mViewPager.getCurrentItem()==2) logSave("Home:homeRightFragment");
        }
    }



    private void setupAlarm() throws java.text.ParseException {

        SharedPreferences preferences = pt.it.porto.mydiabetes.database.Preferences.getPreferences(this);

        Calendar calendar = Calendar.getInstance();
        AlarmManager alm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, SyncAlarm.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        if (!preferences.contains(SyncAlarm.SYNC_ALARM_LAST_SYNC)) { // only sets it if needed
            Usage usage = new Usage(MyDiabetesStorage.getInstance(this));
            String date = usage.getOldestRegist();
            try {
                calendar.setTime(DateUtils.iso8601Format.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }
            calendar.roll(Calendar.DAY_OF_YEAR, 7);
            calendar.set(Calendar.HOUR_OF_DAY, 21); // Maybe change later?
            alm.set(AlarmManager.RTC, calendar.getTimeInMillis(), alarmIntent);
            preferences.edit().putInt(SyncAlarm.SYNC_ALARM_PREFERENCE, 1).apply();
        } else {
            calendar.setTimeInMillis(preferences.getLong(SyncAlarm.SYNC_ALARM_LAST_SYNC, System.currentTimeMillis()));
            calendar.roll(Calendar.DAY_OF_YEAR, 7);
            calendar.set(Calendar.HOUR_OF_DAY, 21); // Maybe change later?
            if (calendar.before(Calendar.getInstance())) {
                alm.set(AlarmManager.RTC, calendar.getTimeInMillis(), alarmIntent);
            } else if (!preferences.contains(SyncAlarm.SYNC_ALARM_PREFERENCE)) {
                preferences.edit().putInt(SyncAlarm.SYNC_ALARM_PREFERENCE, 1).apply();
                alm.set(AlarmManager.RTC, calendar.getTimeInMillis(), alarmIntent);
            }
        }
    }

    public void notifyPageAdapter() {
	    adapter.notifyDataSetChanged();
    }
}


