package pt.it.porto.mydiabetes.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.dataBinding.TargetDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.TargetAdapter;


public class SettingsInsulinTargets extends BaseOldActivity {

    private ListView targetList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_target_bg);

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
        ArrayList<TargetDataBinding> allTags = rdb.Target_GetAll();

        rdb.close();

        lv.setAdapter(new TargetAdapter(allTags, this));
    }
}
