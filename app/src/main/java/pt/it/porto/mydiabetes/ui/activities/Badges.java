package pt.it.porto.mydiabetes.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;



import pt.it.porto.mydiabetes.BuildConfig;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.adviceSystem.yapDroid.YapDroid;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.listAdapters.badgePageAdapter;
import pt.it.porto.mydiabetes.ui.listAdapters.homePageAdapter;
import pt.it.porto.mydiabetes.utils.CustomViewPager;


public class Badges extends BaseActivity {

    private CustomViewPager mViewPager;
    private PagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badges);

        // Show the Up button in the action bar.
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mViewPager = (CustomViewPager) super.findViewById(R.id.content_fragment);
        adapter = new badgePageAdapter(super.getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.blockSwipeRight(true);
        mViewPager.blockSwipeLeft(true);

       setupNavigationView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupNavigationView() {
        final ImageView buttonGrid = (ImageView) findViewById(R.id.button_grid);
        final ImageView buttonList = (ImageView) findViewById(R.id.button_list);

        mViewPager.setCurrentItem(0);
        buttonGrid.setColorFilter(ContextCompat.getColor(getBaseContext(),R.color.primary_dark));

        buttonGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
                buttonGrid.setColorFilter(ContextCompat.getColor(getBaseContext(),R.color.primary_dark));
                buttonList.setColorFilter(ContextCompat.getColor(getBaseContext(),R.color.ef_grey));
            }
        });

        buttonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(1);
                buttonGrid.setColorFilter(ContextCompat.getColor(getBaseContext(),R.color.ef_grey));
                buttonList.setColorFilter(ContextCompat.getColor(getBaseContext(),R.color.primary_dark));
            }
        });
    }

}


