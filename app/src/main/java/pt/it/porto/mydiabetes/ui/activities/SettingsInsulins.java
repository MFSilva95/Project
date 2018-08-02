package pt.it.porto.mydiabetes.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.data.Insulin;
import pt.it.porto.mydiabetes.database.FeaturesDB;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.listAdapters.InsulinAdapter;


public class SettingsInsulins extends BaseActivity {

	private ListView insulinList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_insulins);

		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		ActionBar actionBar=getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getBaseContext(), InsulinsDetail.class);
				startActivity(intent);
			}
		});

		insulinList = (ListView) findViewById(R.id.insulinsFragmentList);
		fillListView(insulinList);
	}

	@Override
	public void onResume() {
		super.onResume();
		insulinList = (ListView) findViewById(R.id.insulinsFragmentList);
		fillListView(insulinList);
	}


	public void fillListView(ListView lv) {

		ArrayList<Insulin> allinsulins = new ArrayList<>();

		DB_Read rdb = new DB_Read(this);
		HashMap<Integer, String[]> val = rdb.Insulin_GetAll();
		rdb.close();
		Insulin insulin;
		String[] row;
		if (val != null) {
			for (int i : val.keySet()) {
				row = val.get(i);
				insulin = new Insulin();
				insulin.setId(i);
				insulin.setName(row[0]);
				insulin.setType(row[1]);
				insulin.setAction(row[2]);
				insulin.setDuration(Double.parseDouble(row[3]));
				allinsulins.add(insulin);
			}
		} else {

		}

		lv.setAdapter(new InsulinAdapter(allinsulins, this));
	}
}
