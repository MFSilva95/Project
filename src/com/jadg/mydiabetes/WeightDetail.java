package com.jadg.mydiabetes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.Toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.database.NoteDataBinding;
import com.jadg.mydiabetes.database.WeightDataBinding;
import com.jadg.mydiabetes.dialogs.DatePickerFragment;
import com.jadg.mydiabetes.dialogs.TimePickerFragment;

public class WeightDetail extends Activity {

	int idWeight = 0;
	int idNote = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weight_detail);
		// Show the Up button in the action bar.
		getSupportActionBar();
		
		Bundle args = getIntent().getExtras();
		if(args!=null){
			DB_Read rdb = new DB_Read(this);
			String id = args.getString("Id");
			idWeight = Integer.parseInt(id);
			WeightDataBinding toFill = rdb.Weight_GetById(Integer.parseInt(id));
			
			EditText value = (EditText)findViewById(R.id.et_WeightDetail_Value);
			value.setText(String.valueOf(toFill.getValue()));
			EditText data = (EditText)findViewById(R.id.et_WeightDetail_Data);
			data.setText(toFill.getDate());
			EditText hora = (EditText)findViewById(R.id.et_WeightDetail_Hora);
			hora.setText(toFill.getTime());
			EditText note = (EditText)findViewById(R.id.et_WeightDetail_Notes);
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
		MenuInflater inflater = getSupportMenuInflater();
		
		Bundle args = getIntent().getExtras();
		if(args!=null){
			inflater.inflate(R.menu.weight_detail_edit, menu);
		}else{
			inflater.inflate(R.menu.weight_detail, menu);
		}
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_WeightDetail_Save:
				AddWeightRead();
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_WeightDetail_Delete:
				DeleteWeightRead();
				return true;
			case R.id.menuItem_WeightDetail_EditSave:
				UpdateWeightRead();
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("SimpleDateFormat")
	public void FillDateHour(){
		EditText date = (EditText)findViewById(R.id.et_WeightDetail_Data);
		final Calendar c = Calendar.getInstance();
	    Date newDate = c.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    String dateString = formatter.format(newDate);
        date.setText(dateString);
        
        EditText hour = (EditText)findViewById(R.id.et_WeightDetail_Hora);
        formatter = new SimpleDateFormat("HH:mm:ss");
        String timeString = formatter.format(newDate);
        hour.setText(timeString);
	}
	
	@SuppressWarnings("deprecation")
	public void showDatePickerDialog(View v){
		DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_WeightDetail_Data);
	    newFragment.setArguments(args);
	    newFragment.show(getSupportFragmentManager(), "DatePicker");
	}
	
	@SuppressWarnings("deprecation")
	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_WeightDetail_Hora);
	    newFragment.setArguments(args);
	    newFragment.show(getSupportFragmentManager(), "timePicker");
	    
	}
	
	public void AddWeightRead(){
		EditText value = (EditText)findViewById(R.id.et_WeightDetail_Value);
		EditText data = (EditText)findViewById(R.id.et_WeightDetail_Data);
		EditText hora = (EditText)findViewById(R.id.et_WeightDetail_Hora);
		EditText note = (EditText)findViewById(R.id.et_WeightDetail_Notes);
		
		
		DB_Write wdb = new DB_Write(this);
		
		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());
		
		WeightDataBinding weight = new WeightDataBinding();
		
		if(!note.getText().toString().equals("")){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			weight.setIdNote(wdb.Note_Add(n));
		}
		
		
		weight.setIdUser(idUser);
		weight.setValue(Double.parseDouble(value.getText().toString()));
		weight.setDate(data.getText().toString());
		weight.setTime(hora.getText().toString());
		
		wdb.Weight_Save(weight);
		
		wdb.close();
		rdb.close();
		
		
	}

	public void UpdateWeightRead(){
		EditText value = (EditText)findViewById(R.id.et_WeightDetail_Value);
		EditText data = (EditText)findViewById(R.id.et_WeightDetail_Data);
		EditText hora = (EditText)findViewById(R.id.et_WeightDetail_Hora);
		EditText note = (EditText)findViewById(R.id.et_WeightDetail_Notes);
		
		
		DB_Write wdb = new DB_Write(this);
		
		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());
		
		WeightDataBinding cho = new WeightDataBinding();
		
		if(!note.getText().toString().equals("") && idNote==0){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			cho.setIdNote(wdb.Note_Add(n));
		}
		if(idNote!=0){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			n.setId(idNote);
			wdb.Note_Update(n);
		}
		
		cho.setId(idWeight);
		cho.setIdUser(idUser);
		cho.setValue(Double.parseDouble(value.getText().toString()));
		cho.setDate(data.getText().toString());
		cho.setTime(hora.getText().toString());
		
		wdb.Weight_Update(cho);
		
		wdb.close();
		rdb.close();
	}
	
	public void DeleteWeightRead(){
		final Context c = this;
		new AlertDialog.Builder(this)
	    .setTitle("Eliminar leitura?")
	    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton) {
	             //Falta verificar se não está associada a nenhuma entrada da DB
	        	 //Rever porque não elimina o registo de glicemia
	        	 DB_Write wdb = new DB_Write(c);
	        	 try {
	        		 wdb.Weight_Delete(idWeight);
	        		 goUp();
	        	 }catch (Exception e) {
	        		 Toast.makeText(c, "Não pode eliminar esta leitura!", Toast.LENGTH_LONG).show();
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
