package com.jadg.mydiabetes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.widget.EditText;
import android.widget.Toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import com.jadg.mydiabetes.database.CholesterolDataBinding;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.database.NoteDataBinding;
import com.jadg.mydiabetes.dialogs.DatePickerFragment;
import com.jadg.mydiabetes.dialogs.TimePickerFragment;







public class CholesterolDetail extends Activity {

	int idCho = 0;
	int idNote = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cholesterol_detail);
		// Show the Up button in the action bar.
		getActionBar();
		
		Bundle args = getIntent().getExtras();
		if(args!=null){
			DB_Read rdb = new DB_Read(this);
			String id = args.getString("Id");
			idCho = Integer.parseInt(id);
			CholesterolDataBinding toFill = rdb.Cholesterol_GetById(Integer.parseInt(id));
			
			EditText value = (EditText)findViewById(R.id.et_CholesterolDetail_Value);
			value.setText(String.valueOf(toFill.getValue()));
			EditText data = (EditText)findViewById(R.id.et_CholesterolDetail_Data);
			data.setText(toFill.getDate());
			EditText hora = (EditText)findViewById(R.id.et_CholesterolDetail_Hora);
			hora.setText(toFill.getTime());
			EditText note = (EditText)findViewById(R.id.et_CholesterolDetail_Notes);
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
		MenuInflater inflater = getMenuInflater();
		
		Bundle args = getIntent().getExtras();
		if(args!=null){
			inflater.inflate(R.menu.cholesterol_detail_edit, menu);
		}else{
			inflater.inflate(R.menu.cholesterol_detail, menu);
		}
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_CholesterolDetail_Save:
				AddCholesterolRead();
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_CholesterolDetail_Delete:
				DeleteCholesterolRead();
				return true;
			case R.id.menuItem_CholesterolDetail_EditSave:
				UpdateCholesterolRead();
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("SimpleDateFormat")
	public void FillDateHour(){
		EditText date = (EditText)findViewById(R.id.et_CholesterolDetail_Data);
		final Calendar c = Calendar.getInstance();
	    Date newDate = c.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    String dateString = formatter.format(newDate);
        date.setText(dateString);
        
        EditText hour = (EditText)findViewById(R.id.et_CholesterolDetail_Hora);
        formatter = new SimpleDateFormat("HH:mm:ss");
        String timeString = formatter.format(newDate);
        hour.setText(timeString);
	}
	
	public void showDatePickerDialog(View v){
		DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_CholesterolDetail_Data);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "DatePicker");
	}
	
	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_CholesterolDetail_Hora);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "timePicker");
	    
	}
	
	public void AddCholesterolRead(){
		EditText value = (EditText)findViewById(R.id.et_CholesterolDetail_Value);
		EditText data = (EditText)findViewById(R.id.et_CholesterolDetail_Data);
		EditText hora = (EditText)findViewById(R.id.et_CholesterolDetail_Hora);
		EditText note = (EditText)findViewById(R.id.et_CholesterolDetail_Notes);
		
		
		DB_Write wdb = new DB_Write(this);
		
		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());
		
		CholesterolDataBinding cho = new CholesterolDataBinding();
		
		if(!note.getText().toString().equals("")){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			cho.setIdNote(wdb.Note_Add(n));
		}
		
		
		cho.setIdUser(idUser);
		cho.setValue(Double.parseDouble(value.getText().toString()));
		cho.setDate(data.getText().toString());
		cho.setTime(hora.getText().toString());
		
		wdb.Cholesterol_Save(cho);
		
		wdb.close();
		rdb.close();
		
		
	}

	public void UpdateCholesterolRead(){
		EditText value = (EditText)findViewById(R.id.et_CholesterolDetail_Value);
		EditText data = (EditText)findViewById(R.id.et_CholesterolDetail_Data);
		EditText hora = (EditText)findViewById(R.id.et_CholesterolDetail_Hora);
		EditText note = (EditText)findViewById(R.id.et_CholesterolDetail_Notes);
		
		
		DB_Write wdb = new DB_Write(this);
		
		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());
		
		CholesterolDataBinding cho = new CholesterolDataBinding();
		
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
		
		cho.setId(idCho);
		cho.setIdUser(idUser);
		cho.setValue(Double.parseDouble(value.getText().toString()));
		cho.setDate(data.getText().toString());
		cho.setTime(hora.getText().toString());
		
		wdb.Cholesterol_Update(cho);
		
		wdb.close();
		rdb.close();
	}
	
	public void DeleteCholesterolRead(){
		final Context c = this;
		new AlertDialog.Builder(this)
	    .setTitle("Eliminar leitura?")
	    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton) {
	             //Falta verificar se n�o est� associada a nenhuma entrada da DB
	        	 //Rever porque n�o elimina o registo de glicemia
	        	 DB_Write wdb = new DB_Write(c);
	        	 try {
	        		 wdb.Cholesterol_Delete(idCho);
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
