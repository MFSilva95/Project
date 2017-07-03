package pt.it.porto.mydiabetes.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.Tag;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.listAdapters.DayFaseAdapter;


public class SettingsDayFases extends BaseActivity {

    private ListView tagList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_day_fases);
        // Show the Up button in the action bar.
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), DayFaseDetail.class);
                startActivity(intent);
            }
        });


        tagList = (ListView) findViewById(R.id.tagsFragmentList);
        fillListView(tagList);
    }

    @Override
    public void onResume() {
        super.onResume();
        tagList = (ListView) findViewById(R.id.tagsFragmentList);
        fillListView(tagList);
    }

    public void fillListView(ListView lv) {
        DB_Read rdb = new DB_Read(this);
        ArrayList<Tag> allTags = rdb.Tag_GetAll();
        rdb.close();
        lv.setAdapter(new DayFaseAdapter(allTags, this));
    }
}
