package pt.it.porto.mydiabetes.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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


        tagList = (ListView) findViewById(R.id.tagsFragmentList);
        fillListView(tagList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tags_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItem_TagsFragment_Add:
                Intent intent = new Intent(this, DayFaseDetail.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
