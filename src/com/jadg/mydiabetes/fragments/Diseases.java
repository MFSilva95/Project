package com.jadg.mydiabetes.fragments;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.app.AlertDialog;
import android.app.Fragment;
import android.widget.ListView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.jadg.mydiabetes.R;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.database.DiseaseAdapter;
import com.jadg.mydiabetes.database.DiseaseDataBinding;

import android.annotation.SuppressLint;



/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link Diseases.OnFragmentInteractionListener} interface to handle
 * interaction events. Use the {@link Diseases#newInstance} factory method to
 * create an instance of this fragment.
 * 
 */



public class Diseases extends Fragment {
	
	ListView diseaseList;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater diseasesmenu = getActivity().getMenuInflater();
        diseasesmenu.inflate(R.menu.diseases_menu, menu);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_diseases, null);
		
		diseaseList = (ListView)v.findViewById(R.id.diseasesFragmentList);
		
		fillListView(diseaseList);
		
		return v;
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menuItem_DiseasesFragment_Add:
				showDiseaseDialog();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void showDiseaseDialog(){
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View v = inflater.inflate(R.layout.dialog_new_disease, null);

    	new AlertDialog.Builder(getActivity())
    	    .setView(v)
    	    .setPositiveButton("Gravar", new DialogInterface.OnClickListener() {
    	         public void onClick(DialogInterface dialog, int whichButton) {
    	             // deal with the editable
    	        	 DB_Write wdb = new DB_Write(getActivity());
    	             EditText diseasename = (EditText)v.findViewById(R.id.et_dialog_new_disease_Name);
    	             wdb.Disease_Add(diseasename.getText().toString());
    	             wdb.close();
    	             fillListView(diseaseList);
    	         }
    	    })
    	    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
    	         public void onClick(DialogInterface dialog, int whichButton) {
    	                // Do nothing.
    	         }
    	    }).show();
	}

	public void fillListView(ListView lv){
		
		DB_Read rdb = new DB_Read(getActivity());
		ArrayList<DiseaseDataBinding> allDiseases = rdb.Disease_GetAll();
		rdb.close();
		
		
		lv.setAdapter(new DiseaseAdapter(allDiseases, getActivity()));
	}
	
}
