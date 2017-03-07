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
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.data.InsulinTarget;
import pt.it.porto.mydiabetes.ui.listAdapters.GlycemiaAdapter;


public class SettingsGlycemia extends BaseActivity {

    private ListView targetList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_glycemia);
        // Show the Up button in the action bar.
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        targetList = (ListView) findViewById(R.id.targetsFragmentList);
        fillListView(targetList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.targets_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItem_TargetsFragment_Add:
                Intent intent = new Intent(this, TargetBG_detail.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        targetList = (ListView) findViewById(R.id.targetsFragmentList);
        fillListView(targetList);
    }

    public void fillListView(ListView lv) {
        DB_Read rdb = new DB_Read(this);
        ArrayList<InsulinTarget> allTags = rdb.Target_GetAll();

        rdb.close();

        lv.setAdapter(new GlycemiaAdapter(allTags, this));
    }
}
