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
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.database.GlycemiaDataBinding;
import com.jadg.mydiabetes.database.NoteDataBinding;
import com.jadg.mydiabetes.database.TagDataBinding;
import com.jadg.mydiabetes.dialogs.DatePickerFragment;
import com.jadg.mydiabetes.dialogs.TimePickerFragment;


import android.annotation.TargetApi;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")

public class GlycemiaDetail extends Activity {

	int idGlycemia = 0;
	int idNote = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_glycemia_detail);
		// Show the Up button in the action bar.
		getActionBar();
		FillTagSpinner();
		
		EditText hora = (EditText)findViewById(R.id.et_GlycemiaDetail_Hora);
		
		Bundle args = getIntent().getExtras();
		if(args!=null){
			DB_Read rdb = new DB_Read(this);
			String id = args.getString("Id");
			idGlycemia = Integer.parseInt(id);
			GlycemiaDataBinding toFill = rdb.Glycemia_GetById(Integer.parseInt(id));
			
			EditText date = (EditText)findViewById(R.id.et_GlycemiaDetail_Data);
			date.setText(toFill.getDate());
			hora.setText(toFill.getTime());
			EditText glycemia = (EditText)findViewById(R.id.et_GlycemiaDetail_Glycemia);
			glycemia.setText(toFill.getValue().toString());
			Spinner spinner = (Spinner) findViewById(R.id.sp_GlycemiaDetail_Tag);
			SelectSpinnerItemByValue(spinner, rdb.Tag_GetById(toFill.getIdTag()).getName());
			
			EditText note = (EditText)findViewById(R.id.et_GlycemiaDetail_Notes);
			if(toFill.getIdNote()!=-1){
				NoteDataBinding n = new NoteDataBinding();
				n=rdb.Note_GetById(toFill.getIdNote());
				note.setText(n.getNote());
				idNote=n.getId();
			}
			
			rdb.close();
		}else
		{
			FillDateHour();
			SetTagByTime();
		}
		
		
		
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
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		Bundle args = getIntent().getExtras();
		if(args!=null){
			inflater.inflate(R.menu.glycemia_detail_edit, menu);
		}else{
			inflater.inflate(R.menu.glycemia_detail, menu);
		}
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_GlycemiaDetail_Save:
				AddGlycemiaRead();
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_GlycemiaDetail_Delete:
				DeleteGlycemiaRead();
				//NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_GlycemiaDetail_EditSave:
				UpdateGlycemiaRead(idGlycemia);
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@SuppressLint("SimpleDateFormat")
	public void FillDateHour(){
		EditText date = (EditText)findViewById(R.id.et_GlycemiaDetail_Data);
		final Calendar c = Calendar.getInstance();
	    Date newDate = c.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    String dateString = formatter.format(newDate);
        date.setText(dateString);
        
        EditText hour = (EditText)findViewById(R.id.et_GlycemiaDetail_Hora);
        formatter = new SimpleDateFormat("HH:mm:ss");
        String timeString = formatter.format(newDate);
        hour.setText(timeString);
	}

	public void FillTagSpinner(){
		Spinner spinner = (Spinner) findViewById(R.id.sp_GlycemiaDetail_Tag);
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
	
	public void AddGlycemiaRead(){
		Spinner TagSpinner = (Spinner)findViewById(R.id.sp_GlycemiaDetail_Tag);
		EditText glycemia = (EditText)findViewById(R.id.et_GlycemiaDetail_Glycemia);
		EditText data = (EditText)findViewById(R.id.et_GlycemiaDetail_Data);
		EditText hora = (EditText)findViewById(R.id.et_GlycemiaDetail_Hora);
		EditText note = (EditText)findViewById(R.id.et_GlycemiaDetail_Notes);
		
		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());
		
		//Get id of selected tag
		String tag = TagSpinner.getSelectedItem().toString();
		Log.d("selected Spinner", tag);
		int idTag = rdb.Tag_GetIdByName(tag);
		rdb.close();
		
		DB_Write reg = new DB_Write(this);
		GlycemiaDataBinding gly = new GlycemiaDataBinding();
		
		if(!note.getText().toString().equals("")){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			gly.setIdNote(reg.Note_Add(n));
		}
		
        
        gly.setIdUser(idUser);
        gly.setValue(Double.parseDouble(glycemia.getText().toString()));
        gly.setDate(data.getText().toString());
        gly.setTime(hora.getText().toString());
        gly.setIdTag(idTag);
        
		
		reg.Glycemia_Save(gly);
		reg.close();
		
	}
	public void UpdateGlycemiaRead(int id){
		Spinner TagSpinner = (Spinner)findViewById(R.id.sp_GlycemiaDetail_Tag);
		EditText glycemia = (EditText)findViewById(R.id.et_GlycemiaDetail_Glycemia);
		EditText data = (EditText)findViewById(R.id.et_GlycemiaDetail_Data);
		EditText hora = (EditText)findViewById(R.id.et_GlycemiaDetail_Hora);
		EditText note = (EditText)findViewById(R.id.et_GlycemiaDetail_Notes);
		
		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());
		
		String tag = TagSpinner.getSelectedItem().toString();
		Log.d("selected Spinner", tag);
		int idTag = rdb.Tag_GetIdByName(tag);
		rdb.close();
		
		DB_Write reg = new DB_Write(this);
		GlycemiaDataBinding gly = new GlycemiaDataBinding();
		
		if(!note.getText().toString().equals("") && idNote==0){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			gly.setIdNote(reg.Note_Add(n));
		}
		if(idNote!=0){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			n.setId(idNote);
			reg.Note_Update(n);
		}
		
		gly.setId(id);
		gly.setIdUser(idUser);
        gly.setValue(Double.parseDouble(glycemia.getText().toString()));
        gly.setDate(data.getText().toString());
        gly.setTime(hora.getText().toString());
        gly.setIdTag(idTag);
		
		reg.Glycemia_Update(gly);
		reg.close();
		
	}
	
	public void DeleteGlycemiaRead(){
		final Context c = this;
		new AlertDialog.Builder(this)
	    .setTitle("Eliminar leitura?")
	    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton) {
	             //Falta verificar se n�o est� associada a nenhuma entrada da DB
	        	 DB_Write wdb = new DB_Write(c);
	        	 try {
	        		 wdb.Glycemia_Delete(idGlycemia);
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
	
	public static void SelectSpinnerItemByValue(Spinner spnr, String value)
	{
	    SpinnerAdapter adapter = (SpinnerAdapter) spnr.getAdapter();
	    for (int position = 0; position < adapter.getCount(); position++)
	    {
	        if(adapter.getItem(position).equals(value))
	        {
	            spnr.setSelection(position);
	            return;
	        }
	    }
	}
	
	public void goUp(){
		NavUtils.navigateUpFromSameTask(this);
	}
	
	public void showDatePickerDialog(View v){
		DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_GlycemiaDetail_Data);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "DatePicker");
	}
	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_GlycemiaDetail_Hora);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "timePicker");
	    
	}
	
	public void SetTagByTime(){
		Spinner tagSpinner = (Spinner)findViewById(R.id.sp_GlycemiaDetail_Tag);
		EditText hora = (EditText)findViewById(R.id.et_GlycemiaDetail_Hora);
		DB_Read rdb = new DB_Read(this);
		String name = rdb.Tag_GetByTime(hora.getText().toString()).getName();
		rdb.close();
		SelectSpinnerItemByValue(tagSpinner, name);
	}
}
