package com.jadg.mydiabetes.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.jadg.mydiabetes.ExercisesDetail;
import com.jadg.mydiabetes.R;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.database.ExerciseAdapter;
import com.jadg.mydiabetes.database.ExerciseDataBinding;

import java.util.ArrayList;
import java.util.HashMap;





/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * @link Exercises.OnFragmentInteractionListener interface to handle
 * interaction events. Use the @link Exercises#newInstance factory method to
 * create an instance of this fragment.
 * 
 */
public class Exercises extends Fragment {
	
	ListView exerciseList;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater exercisessmenu = getActivity().getMenuInflater();
        exercisessmenu.inflate(R.menu.exercises_menu, menu);
        
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_exercises, null);
		
		exerciseList = (ListView)v.findViewById(R.id.exercisesFragmentList);
		
		fillListView(exerciseList);
		
		return v;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menuItem_ExercisesFragment_Add:
				Intent intent = new Intent(this.getActivity(), ExercisesDetail.class);
				startActivity(intent);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();
		exerciseList = (ListView)super.getActivity().findViewById(R.id.exercisesFragmentList);
		fillListView(exerciseList);
	}
	
	public void showExerciseDialog(){
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View v = inflater.inflate(R.layout.dialog_new_exercise, null);

    	new AlertDialog.Builder(getActivity())
    	    .setView(v)
    	    .setPositiveButton("Gravar", new DialogInterface.OnClickListener() {
    	         public void onClick(DialogInterface dialog, int whichButton) {
    	        	EditText exercisename = (EditText)v.findViewById(R.id.et_dialog_new_exercise_Name);
    	        	//adicionado por zeornelas
     	     		//obriga a colocar os valores
     	     		if(exercisename.getText().toString().equals("")){
     	     			Toast.makeText(v.getContext(), getString(R.string.exerciseInputAlert), Toast.LENGTH_LONG).show();
     	     			return;
     	     		}
    	             // deal with the editable
    	        	 DB_Write wdb = new DB_Write(getActivity());
    	             
    	             wdb.Exercise_Add(exercisename.getText().toString());
    	             wdb.close();
    	             fillListView(exerciseList);
    	         }
    	    })
    	    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
    	         public void onClick(DialogInterface dialog, int whichButton) {
    	                // Do nothing.
    	         }
    	    }).show();
	}
	
	public void fillListView(ListView lv){

		ArrayList<ExerciseDataBinding> allExercises = new ArrayList<ExerciseDataBinding>();
		
		DB_Read rdb = new DB_Read(getActivity());
		HashMap<Integer, String> val = rdb.Exercise_GetAll();
		rdb.close();
		ExerciseDataBinding exercise;
		
		if(val!=null){
			for (int i : val.keySet()){
				exercise = new ExerciseDataBinding();
				exercise.setId(i);
				exercise.setName(val.get(i));
				allExercises.add(exercise);
			}
		}
		
		lv.setAdapter(new ExerciseAdapter(allExercises, getActivity()));
	}
}
