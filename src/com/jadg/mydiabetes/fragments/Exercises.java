package com.jadg.mydiabetes.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.ListView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jadg.mydiabetes.R;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.database.ExerciseAdapter;
import com.jadg.mydiabetes.database.ExerciseDataBinding;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link Exercises.OnFragmentInteractionListener} interface to handle
 * interaction events. Use the {@link Exercises#newInstance} factory method to
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
        MenuInflater exercisessmenu = getSupportActivity().getSupportMenuInflater();
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
				showExerciseDialog();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void showExerciseDialog(){
		LayoutInflater inflater = getLayoutInflater();
		final View v = inflater.inflate(R.layout.dialog_new_exercise);

    	new AlertDialog.Builder(getActivity())
    	    .setView(v)
    	    .setPositiveButton("Gravar", new DialogInterface.OnClickListener() {
    	         public void onClick(DialogInterface dialog, int whichButton) {
    	             // deal with the editable
    	        	 DB_Write wdb = new DB_Write(getActivity());
    	             EditText exercisename = (EditText)v.findViewById(R.id.et_dialog_new_exercise_Name);
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
		else{
			
		}
		
		lv.setAdapter(new ExerciseAdapter(allExercises, getActivity()));
	}
}
