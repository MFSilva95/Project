package pt.it.porto.mydiabetes.ui.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.BadgeRec;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.listAdapters.BadgePageAdapter;
import pt.it.porto.mydiabetes.utils.CustomViewPager;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;


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
        adapter = new BadgePageAdapter(super.getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(1);
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
                logSave("Badges:BadgesBoard");
            }
        });

        buttonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(1);
                buttonGrid.setColorFilter(ContextCompat.getColor(getBaseContext(),R.color.ef_grey));
                buttonList.setColorFilter(ContextCompat.getColor(getBaseContext(),R.color.primary_dark));
                logSave("Badges:BadgesList");
            }
        });
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
        if (mViewPager.getCurrentItem()==0) logSave("Badges:BadgeBoard");
        else if (mViewPager.getCurrentItem()==1) logSave("Badges:BadgeList");
    }
}


