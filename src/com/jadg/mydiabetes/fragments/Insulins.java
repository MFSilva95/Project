package com.jadg.mydiabetes.fragments;

import java.util.ArrayList;
import java.util.HashMap;


import android.view.LayoutInflater;
import android.app.Fragment;
import android.app.AlertDialog;
import android.widget.ListView;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jadg.mydiabetes.InsulinsDetail;
import com.jadg.mydiabetes.R;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.database.InsulinAdapter;
import com.jadg.mydiabetes.database.InsulinDataBinding;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link Insulins.OnFragmentInteractionListener} interface to handle
 * interaction events. Use the {@link Insulins#newInstance} factory method to
 * create an instance of this fragment.
 * 
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class Insulins extends Fragment {
	
	ListView insulinList;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater diseasesmenu = getActivity().getMenuInflater();
        diseasesmenu.inflate(R.menu.insulins_menu, menu);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_insulins, null);
		
		insulinList = (ListView)v.findViewById(R.id.insulinsFragmentList);
		fillListView(insulinList);
		return v;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menuItem_InsulinsFragment_Add:
				Intent intent = new Intent(this.getActivity(), InsulinsDetail.class);
				startActivity(intent);
				//showInsulinDialog();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    insulinList = (ListView)super.getActivity().findViewById(R.id.insulinsFragmentList);
	    fillListView(insulinList);
	}
	
	public void showInsulinDialog(){
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View v = inflater.inflate(R.layout.dialog_new_insulin,null);

    	new AlertDialog.Builder(getActivity())
    	    .setView(v)
    	    .setPositiveButton("Gravar", new DialogInterface.OnClickListener() {
    	         public void onClick(DialogInterface dialog, int whichButton) {
    	             // deal with the editable
    	        	 EditText iname = (EditText)v.findViewById(R.id.et_dialog_new_insulin_name);
    	        	 EditText itype = (EditText)v.findViewById(R.id.et_dialog_new_insulin_type);
    	        	 EditText iaction = (EditText)v.findViewById(R.id.et_dialog_new_insulin_action);
    	        	 EditText iduration = (EditText)v.findViewById(R.id.et_dialog_new_insulin_duration);
    	        	 
    	        	 String[] insulin = new String[4];
    	        	 insulin[0] = iname.getText().toString();
    	        	 insulin[1] = itype.getText().toString();
    	        	 insulin[2] = iaction.getText().toString();
    	        	 insulin[3] = iduration.getText().toString();
    	        	 
    	        	 DB_Write wdb = new DB_Write(getActivity());
    	        	 wdb.Insulin_Add(insulin);
    	             wdb.close();
    	             fillListView(insulinList);
    	         }
    	    })
    	    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
    	         public void onClick(DialogInterface dialog, int whichButton) {
    	                // Do nothing.
    	         }
    	    }).show();
	}
	
	public void fillListView(ListView lv){

		ArrayList<InsulinDataBinding> allinsulins = new ArrayList<InsulinDataBinding>();
		
		DB_Read rdb = new DB_Read(getActivity());
		HashMap<Integer, String[]> val = rdb.Insulin_GetAll();
		rdb.close();
		InsulinDataBinding insulin;
		String[] row;
		if(val!=null){
			for (int i : val.keySet()){
				row = val.get(i);
				insulin = new InsulinDataBinding();
				insulin.setId(i);
				insulin.setName(row[0]);
				insulin.setType(row[1]);
				insulin.setAction(row[2]);
				insulin.setDuration(Double.parseDouble(row[3]));
				allinsulins.add(insulin);
			}
		}
		else{
			
		}
		
		lv.setAdapter(new InsulinAdapter(allinsulins, getActivity()));
	}
}
