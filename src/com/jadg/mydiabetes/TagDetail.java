package com.jadg.mydiabetes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.widget.EditText;
import android.widget.Toast;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.database.TagDataBinding;
import com.jadg.mydiabetes.dialogs.TimePickerFragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")

public class TagDetail extends Activity {

	int idTag = 0;
	String id;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tag_detail);
		// Show the Up button in the action bar.
		getActionBar();
		
		Bundle args = getIntent().getExtras();
		if(args!=null){
			DB_Read rdb = new DB_Read(this);
			id = args.getString("Id");
			idTag = Integer.parseInt(id);
			TagDataBinding toFill = rdb.Tag_GetById(Integer.parseInt(id));
			
			EditText name = (EditText)findViewById(R.id.et_FaseDia_Nome);
			name.setText(toFill.getName());
			EditText from = (EditText)findViewById(R.id.et_FaseDia_HourFrom);
			from.setText(toFill.getStart());
			EditText to = (EditText)findViewById(R.id.et_FaseDia_HourTo);
			to.setText(toFill.getEnd());
			
			rdb.close();
			if(Integer.parseInt(id)<=9){
				
				name.setEnabled(false);
			}
		}
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		
		Bundle args = getIntent().getExtras();
		if(args!=null){
			inflater.inflate(R.menu.tag_detail_edit, menu);
			if(Integer.parseInt(args.getString("Id"))<=9){
				MenuItem m = menu.findItem(R.id.menuItem_FaseDiaDetail_Delete);
				m.setVisible(false);
			}
				
		}else{
			inflater.inflate(R.menu.tag_detail, menu);
		}
		
		//getSupportMenuInflater().inflate(R.menu.tag_detail, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_FaseDiaDetail_Save:
				AddNewTag();
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_FaseDiaDetail_EditSave:
				UpdateTag();
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_FaseDiaDetail_Delete:
				DeleteTag();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	//@SuppressWarnings("deprecation")
	public void showTimePickerDialogFrom(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_FaseDia_HourFrom);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "timePicker");
	}
	//@SuppressWarnings("deprecation")
	public void showTimePickerDialogTo(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_FaseDia_HourTo);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "timePicker");
	}
	
	public void AddNewTag(){
		EditText name = (EditText)findViewById(R.id.et_FaseDia_Nome);
		EditText hourFrom = (EditText)findViewById(R.id.et_FaseDia_HourFrom);
		EditText hourTo = (EditText)findViewById(R.id.et_FaseDia_HourTo);
		
		DB_Write wdb = new DB_Write(this);
		
		TagDataBinding tag = new TagDataBinding();
		
		
		tag.setName(name.getText().toString());
		
		if(!hourFrom.getText().toString().equals("") && !hourTo.getText().toString().equals("")){
			tag.setStart(hourFrom.getText().toString());
			tag.setEnd(hourTo.getText().toString());
		}
		
		wdb.Tag_Add(tag);
		wdb.close();
	}
	
	public void UpdateTag(){
		EditText name = (EditText)findViewById(R.id.et_FaseDia_Nome);
		EditText hourFrom = (EditText)findViewById(R.id.et_FaseDia_HourFrom);
		EditText hourTo = (EditText)findViewById(R.id.et_FaseDia_HourTo);
		
		DB_Write wdb = new DB_Write(this);
		
		TagDataBinding tag = new TagDataBinding();
		
		tag.setId(idTag);
		tag.setName(name.getText().toString());
		
		if(!hourFrom.getText().toString().equals("") && !hourTo.getText().toString().equals("")){
			tag.setStart(hourFrom.getText().toString());
			tag.setEnd(hourTo.getText().toString());
		}
		
		wdb.Tag_Update(tag);
		wdb.close();
	}
	
	public void DeleteTag(){
		final Context c = this;
		new AlertDialog.Builder(this)
	    .setTitle("Eliminar Fase do dia?")
	    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton) {
	             //Falta verificar se não está associada a nenhuma entrada da DB
	        	 //Rever porque não elimina o registo de glicemia
	        	 DB_Write wdb = new DB_Write(c);
	        	 try {
	        		 wdb.Tag_Remove(idTag);
	        		 goUp();
	        	 }catch (Exception e) {
	        		 Toast.makeText(c, "Não pode eliminar esta leitura, associada a outros registos!", Toast.LENGTH_LONG).show();
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
