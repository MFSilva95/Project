package com.jadg.mydiabetes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.database.DiseaseDataBinding;
import com.jadg.mydiabetes.database.DiseaseRegDataBinding;
import com.jadg.mydiabetes.database.NoteDataBinding;
import com.jadg.mydiabetes.dialogs.DatePickerFragment;



import android.annotation.TargetApi;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")

public class DiseaseDetail extends Activity {

	int idDisease = 0;
	int idNote = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_disease_detail);
		// Show the Up button in the action bar.
		getActionBar();
		FillDiseaseAC();
		
		
		Bundle args = getIntent().getExtras();
		if(args!=null){
			DB_Read rdb = new DB_Read(this);
			String id = args.getString("Id");
			idDisease = Integer.parseInt(id);
			DiseaseRegDataBinding toFill = rdb.DiseaseReg_GetById(Integer.parseInt(id));
			
			AutoCompleteTextView diseaseSpinner = (AutoCompleteTextView)findViewById(R.id.ac_DiseaseRegDetail_Disease);
			EditText dataFrom = (EditText)findViewById(R.id.et_DiseaseRegDetail_DataFrom);
			EditText dataTo = (EditText)findViewById(R.id.et_DiseaseRegDetail_DataTo);
			EditText note = (EditText)findViewById(R.id.et_DiseaseRegDetail_Notes);
			diseaseSpinner.setText(toFill.getDisease());
			dataFrom.setText(toFill.getStartDate());
			dataTo.setText((toFill.getEndDate()!=null) ? toFill.getEndDate() : "");
			if(toFill.getIdNote()!=-1){
				NoteDataBinding n = new NoteDataBinding();
				n=rdb.Note_GetById(toFill.getIdNote());
				note.setText(n.getNote());
				idNote=n.getId();
			}
			rdb.close();
		}
		else{
			FillDateFrom();
		}
		
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		
		Bundle args = getIntent().getExtras();
		if(args!=null){
			inflater.inflate(R.menu.disease_detail_edit, menu);
		}else{
			inflater.inflate(R.menu.disease_detail, menu);
		}
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_DiseaseRegDetail_Save:
				AddDiseaseRead();
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_DiseaseRegDetail_Delete:
				DeleteDiseaseRead();
				return true;
			case R.id.menuItem_DiseaseRegDetail_EditSave:
				UpdateDiseaseRead();
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void showDatePickerDialogFrom(View v){
		DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_DiseaseRegDetail_DataFrom);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "DatePicker");
	}
	public void showDatePickerDialogTo(View v){
		DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_DiseaseRegDetail_DataTo);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "DatePicker");
	}
	
	public void FillDiseaseAC(){
		AutoCompleteTextView spinner = (AutoCompleteTextView) findViewById(R.id.ac_DiseaseRegDetail_Disease);
		ArrayList<String> allDiseases = new ArrayList<String>();
		DB_Read rdb = new DB_Read(this);
		ArrayList<DiseaseDataBinding> val = rdb.Disease_GetAll();
		rdb.close();
		
		if(val!=null){
			for (DiseaseDataBinding d : val){
				allDiseases.add(d.getName());
			}
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allDiseases);
		spinner.setAdapter(adapter);
	}
	
	@SuppressLint("SimpleDateFormat")
	public void FillDateFrom(){
		EditText date = (EditText)findViewById(R.id.et_DiseaseRegDetail_DataFrom);
		final Calendar c = Calendar.getInstance();
	    Date newDate = c.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    String dateString = formatter.format(newDate);
        date.setText(dateString);
	}
	
	public void AddDiseaseRead(){
		AutoCompleteTextView diseaseSpinner = (AutoCompleteTextView)findViewById(R.id.ac_DiseaseRegDetail_Disease);
		EditText dataFrom = (EditText)findViewById(R.id.et_DiseaseRegDetail_DataFrom);
		EditText dataTo = (EditText)findViewById(R.id.et_DiseaseRegDetail_DataTo);
		EditText note = (EditText)findViewById(R.id.et_DiseaseRegDetail_Notes);
		
		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());
		
		//Get id of selected exercise
		String disease = diseaseSpinner.getText().toString();
		Log.d("selected disease", disease);
		DB_Write reg = new DB_Write(this);
		if(!rdb.Disease_ExistName(diseaseSpinner.getText().toString())){
			reg.Disease_Add(diseaseSpinner.getText().toString());
		}
		
		DiseaseRegDataBinding dis = new DiseaseRegDataBinding();
		
		if(!note.getText().toString().equals("")){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			dis.setIdNote(reg.Note_Add(n));
		}
		
        dis.setIdUser(idUser);
        dis.setDisease(disease);
        dis.setStartDate(dataFrom.getText().toString());
		dis.setEndDate((!dataTo.getText().toString().equals("")) ? dataTo.getText().toString() : null);
        
		
		reg.DiseaseReg_Save(dis);
		reg.close();
		rdb.close();
	}
	
	public void DeleteDiseaseRead(){
		final Context c = this;
		new AlertDialog.Builder(this)
	    .setTitle("Eliminar leitura?")
	    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton) {
	             //Falta verificar se n�o est� associada a nenhuma entrada da DB
	        	 //Rever porque n�o elimina o registo de glicemia
	        	 DB_Write wdb = new DB_Write(c);
	        	 try {
	        		 wdb.DiseaseReg_Delete(idDisease);
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
	
	public void UpdateDiseaseRead(){
		AutoCompleteTextView diseaseSpinner = (AutoCompleteTextView)findViewById(R.id.ac_DiseaseRegDetail_Disease);
		EditText dataFrom = (EditText)findViewById(R.id.et_DiseaseRegDetail_DataFrom);
		EditText dataTo = (EditText)findViewById(R.id.et_DiseaseRegDetail_DataTo);
		EditText note = (EditText)findViewById(R.id.et_DiseaseRegDetail_Notes);
		
		
		DB_Write wdb = new DB_Write(this);
		
		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());
		
		DiseaseRegDataBinding dis = new DiseaseRegDataBinding();
		
		if(!note.getText().toString().equals("") && idNote==0){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			dis.setIdNote(wdb.Note_Add(n));
		}
		if(idNote!=0){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			n.setId(idNote);
			wdb.Note_Update(n);
		}
		
		//Get id of selected exercise
		String disease = diseaseSpinner.getText().toString();
		Log.d("selected disease", disease);
		if(!rdb.Disease_ExistName(diseaseSpinner.getText().toString())){
			wdb.Disease_Add(diseaseSpinner.getText().toString());
		}
		
		
		dis.setId(idDisease);
		dis.setIdUser(idUser);
		dis.setDisease(disease);
        dis.setStartDate(dataFrom.getText().toString());
		dis.setEndDate((!dataTo.getText().toString().equals("")) ? dataTo.getText().toString() : null);
		
		wdb.DiseaseReg_Update(dis);
		
		wdb.close();
		rdb.close();
	}
}
