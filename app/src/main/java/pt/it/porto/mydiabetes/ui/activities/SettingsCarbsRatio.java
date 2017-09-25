package pt.it.porto.mydiabetes.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.CarbsRatioData;
import pt.it.porto.mydiabetes.data.Tag;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.listAdapters.CarbsRatioListAdapter;


public class SettingsCarbsRatio extends BaseActivity {

    private ListView targetList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ratios);
        // Show the Up button in the action bar.
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getBaseContext(), Ratio_detail.class);
//                startActivity(intent);
//            }
//        });


        targetList = (ListView) findViewById(R.id.targetsRatiosFragmentList);
        fillListView(targetList);
    }

    @Override
    public void onResume() {
        super.onResume();
        targetList = (ListView) findViewById(R.id.targetsRatiosFragmentList);
        fillListView(targetList);
    }

    public void fillListView(ListView lv) {
        DB_Read rdb = new DB_Read(this);
        int baseCarbsRatio = rdb.getCarbsRatio();
        ArrayList<CarbsRatioData> allTags = rdb.Ratio_GetAll();

        if(allTags==null){
            MyDiabetesStorage storage = MyDiabetesStorage.getInstance(this);
            storage.initRacioSens(baseCarbsRatio, "Ratio_Reg");
            //rdb = new DB_Read(this);
            allTags = rdb.Ratio_GetAll();
        }
        rdb.close();
        lv.setAdapter(new CarbsRatioListAdapter(allTags, this));
    }
}
