package com.jadg.mydiabetes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import com.jadg.mydiabetes.database.BloodPressureDataBinding;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.database.NoteDataBinding;
import com.jadg.mydiabetes.database.TagDataBinding;
import com.jadg.mydiabetes.dialogs.DatePickerFragment;
import com.jadg.mydiabetes.dialogs.TimePickerFragment;



import android.annotation.TargetApi;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class BloodPressureDetail extends Activity {

	int idBP = 0;
	int idNote = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blood_pressure_detail);
		// Show the Up button in the action bar.
		getActionBar();
		
		FillTagSpinner();
		
		Bundle args = getIntent().getExtras();
		if(args!=null){
			DB_Read rdb = new DB_Read(this);
			String id = args.getString("Id");
			idBP = Integer.parseInt(id);
			
			BloodPressureDataBinding toFill = rdb.BloodPressure_GetById(Integer.parseInt(id));
			
			Spinner tagSpinner = (Spinner)findViewById(R.id.sp_BloodPressureDetail_Tag);
			SelectSpinnerItemByValue(tagSpinner, rdb.Tag_GetById(toFill.getIdTag()).getName());
			EditText systolic = (EditText)findViewById(R.id.et_BloodPressureDetail_Systolic);
			systolic.setText(String.valueOf(toFill.getSystolic()));
			EditText diastolic = (EditText)findViewById(R.id.et_BloodPressureDetail_Diastolic);
			diastolic.setText(String.valueOf(toFill.getDiastolic()));
			EditText data = (EditText)findViewById(R.id.et_BloodPressureDetail_Data);
			data.setText(toFill.getDate());
			EditText hora = (EditText)findViewById(R.id.et_BloodPressureDetail_Hora);
			hora.setText(toFill.getTime());
			EditText note = (EditText)findViewById(R.id.et_BloodPressureDetail_Notes);
			if(toFill.getIdNote()!=-1){
				NoteDataBinding n = new NoteDataBinding();
				n=rdb.Note_GetById(toFill.getIdNote());
				note.setText(n.getNote());
				idNote=n.getId();
			}
			rdb.close();
		}else{
			FillDateHour();
			SetTagByTime();
		}
		
		
		EditText hora = (EditText)findViewById(R.id.et_BloodPressureDetail_Hora);
		hora.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				SetTagByTime(); }
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override
			public void afterTextChanged(Editable s) { }
		});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		
		Bundle args = getIntent().getExtras();
		if(args!=null){
			inflater.inflate(R.menu.blood_pressure_detail_edit, menu);
		}else{
			inflater.inflate(R.menu.blood_pressure_detail, menu);
		}
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_BloodPressureDetail_Save:
				AddBloodPressureRead();
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_BloodPressureDetail_Delete:
				DeleteBloodPressureRead();
				return true;
			case R.id.menuItem_BloodPressureDetail_EditSave:
				UpdateBloodPressureRead();
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	
	@SuppressLint("SimpleDateFormat")
	public void FillDateHour(){
		EditText date = (EditText)findViewById(R.id.et_BloodPressureDetail_Data);
		final Calendar c = Calendar.getInstance();
	    Date newDate = c.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    String dateString = formatter.format(newDate);
        date.setText(dateString);
        
        EditText hour = (EditText)findViewById(R.id.et_BloodPressureDetail_Hora);
        formatter = new SimpleDateFormat("HH:mm:ss");
        String timeString = formatter.format(newDate);
        hour.setText(timeString);
	}
	
	public void showDatePickerDialog(View v){
		DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_BloodPressureDetail_Data);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "DatePicker");
	}
	
	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_BloodPressureDetail_Hora);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "timePicker");
	    
	}
	
	public void FillTagSpinner(){
		Spinner spinner = (Spinner) findViewById(R.id.sp_BloodPressureDetail_Tag);
		ArrayList<String> allTags = new ArrayList<String>();
		DB_Read rdb = new DB_Read(this);
		ArrayList<TagDataBinding> t = rdb.Tag_GetAll();
		rdb.close();
		
		
		if(t!=null){
			for (TagDataBinding i : t){
				allTags.add(i.getName());
			}
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, allTags);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
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
	
	public void SetTagByTime(){
		Spinner tagSpinner = (Spinner)findViewById(R.id.sp_BloodPressureDetail_Tag);
		EditText hora = (EditText)findViewById(R.id.et_BloodPressureDetail_Hora);
		DB_Read rdb = new DB_Read(this);
		String name = rdb.Tag_GetByTime(hora.getText().toString()).getName();
		rdb.close();
		SelectSpinnerItemByValue(tagSpinner, name);
	}

	public void AddBloodPressureRead(){
		Spinner tagSpinner = (Spinner)findViewById(R.id.sp_BloodPressureDetail_Tag);
		EditText systolic = (EditText)findViewById(R.id.et_BloodPressureDetail_Systolic);
		EditText diastolic = (EditText)findViewById(R.id.et_BloodPressureDetail_Diastolic);
		EditText data = (EditText)findViewById(R.id.et_BloodPressureDetail_Data);
		EditText hora = (EditText)findViewById(R.id.et_BloodPressureDetail_Hora);
		EditText note = (EditText)findViewById(R.id.et_BloodPressureDetail_Notes);
		
		
		DB_Write wdb = new DB_Write(this);
		
		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());
		
		//Get id of selected tag
		String tag = tagSpinner.getSelectedItem().toString();
		Log.d("selected Spinner", tag);
		int idTag = rdb.Tag_GetIdByName(tag);
		
		BloodPressureDataBinding bp = new BloodPressureDataBinding();
		
		if(!note.getText().toString().equals("")){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			bp.setIdNote(wdb.Note_Add(n));
		}
		
		
		bp.setIdUser(idUser);
		bp.setSystolic(Integer.parseInt(systolic.getText().toString()));
		bp.setDiastolic(Integer.parseInt(diastolic.getText().toString()));
		bp.setDate(data.getText().toString());
		bp.setTime(hora.getText().toString());
		bp.setIdTag(idTag);
		
		wdb.BloodPressure_Save(bp);
		
		wdb.close();
		rdb.close();
		
		
	}

	public void UpdateBloodPressureRead(){
		Spinner tagSpinner = (Spinner)findViewById(R.id.sp_BloodPressureDetail_Tag);
		EditText systolic = (EditText)findViewById(R.id.et_BloodPressureDetail_Systolic);
		EditText diastolic = (EditText)findViewById(R.id.et_BloodPressureDetail_Diastolic);
		EditText data = (EditText)findViewById(R.id.et_BloodPressureDetail_Data);
		EditText hora = (EditText)findViewById(R.id.et_BloodPressureDetail_Hora);
		EditText note = (EditText)findViewById(R.id.et_BloodPressureDetail_Notes);
		
		DB_Write wdb = new DB_Write(this);
		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());
		//Get id of selected tag
		String tag = tagSpinner.getSelectedItem().toString();
		Log.d("selected Spinner", tag);
		int idTag = rdb.Tag_GetIdByName(tag);
		
		BloodPressureDataBinding bp = new BloodPressureDataBinding();
		
		if(!note.getText().toString().equals("") && idNote==0){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			bp.setIdNote(wdb.Note_Add(n));
		}
		if(idNote!=0){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			n.setId(idNote);
			wdb.Note_Update(n);
		}
		
		bp.setId(idBP);
		bp.setIdUser(idUser);
		bp.setSystolic(Integer.parseInt(systolic.getText().toString()));
		bp.setDiastolic(Integer.parseInt(diastolic.getText().toString()));
		bp.setDate(data.getText().toString());
		bp.setTime(hora.getText().toString());
		bp.setIdTag(idTag);
		
		wdb.BloodPressure_Update(bp);
		
		wdb.close();
		rdb.close();
	}

	public void DeleteBloodPressureRead(){
		final Context c = this;
		new AlertDialog.Builder(this)
	    .setTitle("Eliminar leitura?")
	    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton) {
	             //Falta verificar se n�o est� associada a nenhuma entrada da DB
	        	 //Rever porque n�o elimina o registo de glicemia
	        	 DB_Write wdb = new DB_Write(c);
	        	 try {
	        		 wdb.BloodPressure_Delete(idBP);
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
}
