package com.jadg.mydiabetes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


import android.widget.AutoCompleteTextView;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.database.ExerciseRegDataBinding;
import com.jadg.mydiabetes.database.NoteDataBinding;
import com.jadg.mydiabetes.dialogs.DatePickerFragment;
import com.jadg.mydiabetes.dialogs.TimePickerFragment;







public class ExerciseDetail extends Activity {

	int idNote = 0;
	int idExercise = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exercise_detail);
		// Show the Up button in the action bar.
		getActionBar();
		FillExerciseSpinner();
		FillEffortSpinner();
		
		Bundle args = getIntent().getExtras();
		if(args!=null){
			DB_Read rdb = new DB_Read(this);
			String id = args.getString("Id");
			idExercise = Integer.parseInt(id);
			ExerciseRegDataBinding toFill = rdb.ExerciseReg_GetById(Integer.parseInt(id));
			
			AutoCompleteTextView exerciseSpinner = (AutoCompleteTextView)findViewById(R.id.ac_ExerciseDetail_Exercise);
			exerciseSpinner.setText(toFill.getExercise());
			Spinner effortSpinner = (Spinner)findViewById(R.id.sp_ExerciseDetail_Effort);
			SelectSpinnerItemByValue(effortSpinner, toFill.getEffort());
			EditText duration = (EditText)findViewById(R.id.et_ExerciseDetail_Duration);
			duration.setText(String.valueOf(toFill.getDuration()));
			EditText data = (EditText)findViewById(R.id.et_ExerciseDetail_Data);
			data.setText(toFill.getDate());
			EditText hora = (EditText)findViewById(R.id.et_ExerciseDetail_Hora);
			hora.setText(toFill.getTime());
			
			EditText note = (EditText)findViewById(R.id.et_ExerciseDetail_Notes);
			if(toFill.getIdNote()!=-1){
				NoteDataBinding n = new NoteDataBinding();
				n=rdb.Note_GetById(toFill.getIdNote());
				note.setText(n.getNote());
				idNote=n.getId();
			}
			
			rdb.close();
		}else{
			FillDateHour();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		
		Bundle args = getIntent().getExtras();
		if(args!=null){
			inflater.inflate(R.menu.exercise_detail_edit, menu);
		}else{
			inflater.inflate(R.menu.exercise_detail, menu);
		}
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_ExerciseDetail_Save:
				AddExerciseRead();
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_ExerciseDetail_Delete:
				DeleteExerciseRead(idExercise);
				return true;
			case R.id.menuItem_ExerciseDetail_EditSave:
				UpdateExerciseRead(idExercise);
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@SuppressLint("SimpleDateFormat")
	public void FillDateHour(){
		EditText date = (EditText)findViewById(R.id.et_ExerciseDetail_Data);
		final Calendar c = Calendar.getInstance();
	    Date newDate = c.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    String dateString = formatter.format(newDate);
        date.setText(dateString);
        
        EditText hour = (EditText)findViewById(R.id.et_ExerciseDetail_Hora);
        formatter = new SimpleDateFormat("HH:mm:ss");
        String timeString = formatter.format(newDate);
        hour.setText(timeString);
	}

	public void showDatePickerDialog(View v){
		DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_ExerciseDetail_Data);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "DatePicker");
	}
	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_ExerciseDetail_Hora);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "timePicker");
	    
	}
	
	public void FillExerciseSpinner(){
		AutoCompleteTextView spinner = (AutoCompleteTextView) findViewById(R.id.ac_ExerciseDetail_Exercise);
		ArrayList<String> allExercises = new ArrayList<String>();
		DB_Read rdb = new DB_Read(this);
		HashMap<Integer, String> val = rdb.Exercise_GetAll();
		rdb.close();
		
		if(val!=null){
			for (int i : val.keySet()){
				allExercises.add(val.get(i));
			}
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allExercises);
		spinner.setAdapter(adapter);
	}
	
	public void FillEffortSpinner(){
		Spinner sp_Exercise_Detail = (Spinner) findViewById(R.id.sp_ExerciseDetail_Effort);
		ArrayAdapter<CharSequence> adapter_sp_Exercise_Detail = ArrayAdapter.createFromResource(this, R.array.Effort, android.R.layout.simple_spinner_item);
		adapter_sp_Exercise_Detail.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_Exercise_Detail.setAdapter(adapter_sp_Exercise_Detail);
	}
	
	public void AddExerciseRead(){
		AutoCompleteTextView exerciseSpinner = (AutoCompleteTextView)findViewById(R.id.ac_ExerciseDetail_Exercise);
		Spinner effortSpinner = (Spinner)findViewById(R.id.sp_ExerciseDetail_Effort);
		EditText duration = (EditText)findViewById(R.id.et_ExerciseDetail_Duration);
		EditText data = (EditText)findViewById(R.id.et_ExerciseDetail_Data);
		EditText hora = (EditText)findViewById(R.id.et_ExerciseDetail_Hora);
		EditText note = (EditText)findViewById(R.id.et_ExerciseDetail_Notes);
		
		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());
		
		DB_Write reg = new DB_Write(this);
		ExerciseRegDataBinding ex = new ExerciseRegDataBinding();
		
		
		
		//Get id of selected exercise
		String exercise = exerciseSpinner.getText().toString();
		Log.d("selected exercise", exercise);
		
		if(!rdb.Exercise_ExistName(exerciseSpinner.getText().toString())){
			reg.Exercise_Add(exerciseSpinner.getText().toString());
		}
		
		
		if(!note.getText().toString().equals("")){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			ex.setIdNote(reg.Note_Add(n));
		}
		
		String effort = effortSpinner.getSelectedItem().toString();
        
        ex.setId_User(idUser);
        ex.setExercise(exerciseSpinner.getText().toString());
        ex.setDuration(Integer.parseInt(duration.getText().toString()));
        ex.setEffort(effort);
        ex.setDate(data.getText().toString());
        ex.setTime(hora.getText().toString());
        
		
		reg.Exercise_Save(ex);
		reg.close();
		rdb.close();
	}
	public static void SelectSpinnerItemByValue(Spinner spnr, String value){
	    SpinnerAdapter adapter = (SpinnerAdapter) spnr.getAdapter();
	    for (int position = 0; position < adapter.getCount(); position++){
	        if(adapter.getItem(position).equals(value)){
	            spnr.setSelection(position);
	            return;
	        }
	    }
	}
	
	public void DeleteExerciseRead(final int id){
		final Context c = this;
		new AlertDialog.Builder(this)
	    .setTitle("Eliminar leitura?")
	    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton) {
	             //Falta verificar se n�o est� associada a nenhuma entrada da DB
	        	 //Rever porque n�o elimina o registo de glicemia
	        	 DB_Write wdb = new DB_Write(c);
	        	 try {
	        		 wdb.Exercise_Delete(id);
	        		 goUp();
	        	 }catch (Exception e) {
	        		 Toast.makeText(c, "N�o pode eliminar esta leitura!", Toast.LENGTH_LONG).show();
	     		 }
	             wdb.close();
	             
	         }
	    })
	    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton) {
	                // Do nothing.
	         }
	    }).show();
	}
	
	public void goUp(){
		NavUtils.navigateUpFromSameTask(this);
	}
	
	public void UpdateExerciseRead(int id){
		AutoCompleteTextView exerciseSpinner = (AutoCompleteTextView)findViewById(R.id.ac_ExerciseDetail_Exercise);
		Spinner effortSpinner = (Spinner)findViewById(R.id.sp_ExerciseDetail_Effort);
		EditText duration = (EditText)findViewById(R.id.et_ExerciseDetail_Duration);
		EditText data = (EditText)findViewById(R.id.et_ExerciseDetail_Data);
		EditText hora = (EditText)findViewById(R.id.et_ExerciseDetail_Hora);
		EditText note = (EditText)findViewById(R.id.et_ExerciseDetail_Notes);
		
		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		DB_Write wdb = new DB_Write(this);
		Object[] obj = rdb.MyData_Read();
		
		int idUser = Integer.valueOf(obj[0].toString());
		if(!rdb.Exercise_ExistName(exerciseSpinner.getText().toString())){
			wdb.Exercise_Add(exerciseSpinner.getText().toString());
		}
		
		ExerciseRegDataBinding toUpdate = new ExerciseRegDataBinding();
		
		if(!note.getText().toString().equals("") && idNote==0){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			toUpdate.setIdNote(wdb.Note_Add(n));
		}
		if(idNote!=0){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			n.setId(idNote);
			wdb.Note_Update(n);
		}
		
		toUpdate.setId(id);
		toUpdate.setId_User(idUser);
		toUpdate.setDate(data.getText().toString());
		toUpdate.setTime(hora.getText().toString());
		toUpdate.setDuration(Integer.parseInt(duration.getText().toString()));
		toUpdate.setEffort(effortSpinner.getSelectedItem().toString());
		toUpdate.setExercise(exerciseSpinner.getText().toString());
		
		wdb.Exercise_Update(toUpdate);
		wdb.close();
		rdb.close();
	}
}
