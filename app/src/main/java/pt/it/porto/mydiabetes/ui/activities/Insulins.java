package pt.it.porto.mydiabetes.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.activities.BaseOldActivity;
import pt.it.porto.mydiabetes.ui.activities.InsulinsDetail;
import pt.it.porto.mydiabetes.ui.listAdapters.InsulinAdapter;
import pt.it.porto.mydiabetes.ui.listAdapters.InsulinDataBinding;


public class Insulins extends BaseOldActivity {

	private ListView insulinList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_insulins);

		insulinList = (ListView) findViewById(R.id.insulinsFragmentList);
		fillListView(insulinList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.insulins_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menuItem_InsulinsFragment_Add:
				Intent intent = new Intent(this, InsulinsDetail.class);
				startActivity(intent);
				//showInsulinDialog();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();
		insulinList = (ListView) findViewById(R.id.insulinsFragmentList);
		fillListView(insulinList);
	}

	public void showInsulinDialog() {
		LayoutInflater inflater = getLayoutInflater();
		final View v = inflater.inflate(R.layout.dialog_new_insulin, null);

		new AlertDialog.Builder(this)
				.setView(v)
				.setPositiveButton(getString(R.string.saveButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// deal with the editable
						EditText iname = (EditText) v.findViewById(R.id.et_dialog_new_insulin_name);
						EditText itype = (EditText) v.findViewById(R.id.et_dialog_new_insulin_type);
						EditText iaction = (EditText) v.findViewById(R.id.et_dialog_new_insulin_action);
						EditText iduration = (EditText) v.findViewById(R.id.et_dialog_new_insulin_duration);

						String[] insulin = new String[4];
						insulin[0] = iname.getText().toString();
						insulin[1] = itype.getText().toString();
						insulin[2] = iaction.getText().toString();
						insulin[3] = iduration.getText().toString();

						DB_Write wdb = new DB_Write(getApplicationContext());
						wdb.Insulin_Add(insulin);
						wdb.close();
						fillListView(insulinList);
					}
				})
				.setNegativeButton(getString(R.string.negativeButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Do nothing.
					}
				}).show();
	}

	public void fillListView(ListView lv) {

		ArrayList<InsulinDataBinding> allinsulins = new ArrayList<InsulinDataBinding>();

		DB_Read rdb = new DB_Read(this);
		HashMap<Integer, String[]> val = rdb.Insulin_GetAll();
		rdb.close();
		InsulinDataBinding insulin;
		String[] row;
		if (val != null) {
			for (int i : val.keySet()) {
				row = val.get(i);
				insulin = new InsulinDataBinding();
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
