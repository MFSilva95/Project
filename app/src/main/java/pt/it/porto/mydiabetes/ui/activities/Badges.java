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
        mViewPager.blockSwipeRight(true);
        mViewPager.blockSwipeLeft(true);

//        DB_Read read = new DB_Read(this);
//        BadgeRec lastDaily = read.getLastDailyMedal();
//        read.close();

//        TextView daily_bronze_text = (TextView) findViewById(R.id.daily_bronze_text);
//        TextView daily_silver_text = (TextView) findViewById(R.id.daily_silver_text);
//        TextView daily_gold_text = (TextView) findViewById(R.id.daily_gold_text);
//        if(lastDaily != null){
//
//            ImageView daily_bronze_background = (ImageView) findViewById(R.id.daily_bronze_background);
//            ImageView daily_bronze_symbol = (ImageView) findViewById(R.id.daily_bronze_symbol);
//
//            ImageView daily_silver_background = (ImageView) findViewById(R.id.daily_silver_background);
//            ImageView daily_silver_symbol = (ImageView) findViewById(R.id.daily_silver_symbol);
//
//            ImageView daily_gold_background = (ImageView) findViewById(R.id.daily_gold_background);
//            ImageView daily_gold_symbol = (ImageView) findViewById(R.id.daily_gold_symbol);
//
//
//            switch (lastDaily.getMedal()){
//                case "bronze":
//                    daily_bronze_background.setImageResource(R.drawable.medal_bronze_daily);
//                    daily_bronze_symbol.setImageResource(R.drawable.log);
//                    break;
//                case "silver":
//                    daily_silver_background.setImageResource(R.drawable.medal_silver_daily);
//                    daily_silver_symbol.setImageResource(R.drawable.log);
//                    break;
//                case "gold":
//                    daily_gold_background.setImageResource(R.drawable.medal_gold_daily);
//                    daily_gold_symbol.setImageResource(R.drawable.log);
//                    break;
//            }
//        }
//            daily_bronze_text.setText(R.string.daily_bronze_all);
//            daily_silver_text.setText(R.string.daily_silver_all);
//            daily_gold_text.setText(R.string.daily_gold_all);


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


