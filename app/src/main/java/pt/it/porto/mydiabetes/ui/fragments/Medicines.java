package pt.it.porto.mydiabetes.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.listAdapters.MedicineAdapter;
import pt.it.porto.mydiabetes.ui.listAdapters.MedicineDataBinding;

import java.util.ArrayList;
import java.util.HashMap;


public class Medicines extends Fragment {

	ListView medicineList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		MenuInflater diseasesmenu = getActivity().getMenuInflater();
		diseasesmenu.inflate(R.menu.medicines_menu, menu);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_medicines, null);

		medicineList = (ListView) v.findViewById(R.id.medicinesFragmentList);
		fillListView(medicineList);
		return v;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menuItem_MedicinesFragment_Add:
				showMedicineDialog();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void showMedicineDialog() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View v = inflater.inflate(R.layout.dialog_new_medicine, null);

		new AlertDialog.Builder(getActivity())
				.setView(v)
				.setPositiveButton(getString(R.string.saveButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// deal with the editable
						EditText mname = (EditText) v.findViewById(R.id.et_dialog_new_medicine_name);
						EditText munits = (EditText) v.findViewById(R.id.et_dialog_new_medicine_units);

						String[] medicine = new String[2];
						medicine[0] = mname.getText().toString();
						medicine[1] = munits.getText().toString();

						DB_Write wdb = new DB_Write(getActivity());
						wdb.Medicine_Add(medicine);
						wdb.close();
						fillListView(medicineList);
					}
				})
				.setNegativeButton(getString(R.string.negativeButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Do nothing.
					}
				}).show();
	}

	public void fillListView(ListView lv) {

		ArrayList<MedicineDataBinding> allmedicines = new ArrayList<MedicineDataBinding>();

		DB_Read rdb = new DB_Read(getActivity());
		HashMap<Integer, String[]> val = rdb.Medicine_GetAll();
		rdb.close();
		MedicineDataBinding medicine;
		String[] row;
		if (val != null) {
			for (int i : val.keySet()) {
				row = val.get(i);
				medicine = new MedicineDataBinding();
				medicine.setId(i);
				medicine.setName(row[0]);
				medicine.setUnits(row[1]);
				allmedicines.add(medicine);
			}
		} else {

		}

		lv.setAdapter(new MedicineAdapter(allmedicines, getActivity()));
	}
}
