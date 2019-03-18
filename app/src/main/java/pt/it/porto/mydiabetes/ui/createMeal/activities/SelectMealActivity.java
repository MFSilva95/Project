package pt.it.porto.mydiabetes.ui.createMeal.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.activities.BaseActivity;
import pt.it.porto.mydiabetes.ui.createMeal.adapters.MealItemListAdapter;
import pt.it.porto.mydiabetes.ui.createMeal.adapters.ViewPagerAdapter;
import pt.it.porto.mydiabetes.ui.createMeal.db.DataBaseHelper;
import pt.it.porto.mydiabetes.ui.createMeal.fragments.FoodListFragment;
import pt.it.porto.mydiabetes.ui.createMeal.fragments.LoggedMealListFragment;


public class SelectMealActivity extends BaseActivity {
    private static final String TAG = "SelectMealActivity";

//    private RecyclerView recyclerView;
//    private MealItemListAdapter mAdapter;
//    private SearchView searchView;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LoggedMealListFragment mLoggedMealFragment;

    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_meal);

        dbHelper = new DataBaseHelper(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("firstTime", true)) {
            //Load CSV data to the Simple Meal Database when this activity is first created
            Log.d(TAG, "Load food list data");
            try {
                dbHelper.loadInitialData(this);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("firstTime", false);
                editor.commit();
            }
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(onTabSelectedListener(viewPager));
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new FoodListFragment(), getString(R.string.tab1_title));
        mLoggedMealFragment = new LoggedMealListFragment();
        adapter.addFragment(mLoggedMealFragment, getString(R.string.tab2_title));
        viewPager.setAdapter(adapter);
    }

    private TabLayout.OnTabSelectedListener onTabSelectedListener(final ViewPager viewPager) {
        return new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if(viewPager.getCurrentItem() == 0) {
                    mLoggedMealFragment.loadFragmentData();
                    logSave("SelectMealActivity:Food");
                }


                if(viewPager.getCurrentItem() == 1) {
                    mLoggedMealFragment.loadFragmentData();
                    logSave("SelectMealActivity:Meal");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };
    }

//    private void resetData(){
//        deleteDatabase(dbHelper.DATABASE_NAME);
//        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
//        editor.putBoolean("firstTime", true);
//        editor.commit();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.select_meal_tittle));
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLoggedMealFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        if (viewPager.getCurrentItem() == 0) logSave("SelectMealActivity:Food");
        else if (viewPager.getCurrentItem() == 1) logSave("SelectMealActivity:Meal");
    }

}
