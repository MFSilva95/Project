package com.jadg.mydiabetes;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.Toast;

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
import com.jadg.mydiabetes.database.TargetDataBinding;
import com.jadg.mydiabetes.dialogs.TimePickerFragment;

public class TargetBG_detail extends Activity {

	
	int idTarget = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_target_bg_detail);
		// Show the Up button in the action bar.
		getSupportActionBar();
		
		
		Bundle args = getIntent().getExtras();
		if(args!=null){
			DB_Read rdb = new DB_Read(this);
			String id = args.getString("Id");
			idTarget = Integer.parseInt(id);
			TargetDataBinding toFill = rdb.Target_GetById(Integer.parseInt(id));
			
			EditText name = (EditText)findViewById(R.id.et_TargetBG_Nome);
			name.setText(toFill.getName());
			EditText from = (EditText)findViewById(R.id.et_TargetBG_HourFrom);
			from.setText(toFill.getStart());
			EditText to = (EditText)findViewById(R.id.et_TargetBG_HourTo);
			to.setText(toFill.getEnd());
			EditText value = (EditText)findViewById(R.id.et_TargetBG_Glycemia);
			value.setText(String.valueOf(toFill.getTarget()));
			
			rdb.close();
			
		}
		
	}

	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getSupportMenuInflater();

		Bundle args = getIntent().getExtras();
		if(args!=null){
			inflater.inflate(R.menu.target_bg_detail_edit, menu);
		}else{
			inflater.inflate(R.menu.target_bg_detail, menu);
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
			case R.id.menuItem_TargetBGDetail_Save:
				AddNewTarget();
				//Intent data = new Intent();
				//data.putExtra("tabPosition", 2);
				//setResult(RESULT_OK, data);
				//NavUtils.navigateUpFromSameTask(this);
				finish();
				return true;
			case R.id.menuItem_TargetBGDetail_EditSave:
				UpdateTarget();
				finish();
				return true;
			case R.id.menuItem_TargetBGDetail_Delete:
				DeleteTarget();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	@SuppressWarnings("deprecation")
	public void showTimePickerDialogFrom(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_TargetBG_HourFrom);
	    newFragment.setArguments(args);
	    newFragment.show(getSupportFragmentManager(), "timePicker");
	}
	@SuppressWarnings("deprecation")
	public void showTimePickerDialogTo(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_TargetBG_HourTo);
	    newFragment.setArguments(args);
	    newFragment.show(getSupportFragmentManager(), "timePicker");
	}
	
	public void AddNewTarget(){
		EditText name = (EditText)findViewById(R.id.et_TargetBG_Nome);
		EditText hourFrom = (EditText)findViewById(R.id.et_TargetBG_HourFrom);
		EditText hourTo = (EditText)findViewById(R.id.et_TargetBG_HourTo);
		EditText value = (EditText)findViewById(R.id.et_TargetBG_Glycemia);
		
		DB_Write wdb = new DB_Write(this);
		
		TargetDataBinding target = new TargetDataBinding();
		
		
		target.setName(name.getText().toString());
		
		if(!hourFrom.getText().toString().equals("") && !hourTo.getText().toString().equals("")){
			target.setStart(hourFrom.getText().toString());
			target.setEnd(hourTo.getText().toString());
		}
		
		target.setTarget(Double.valueOf(value.getText().toString()));
		
		wdb.Target_Add(target);
		wdb.close();
	}
	
	public void UpdateTarget(){
		EditText name = (EditText)findViewById(R.id.et_TargetBG_Nome);
		EditText hourFrom = (EditText)findViewById(R.id.et_TargetBG_HourFrom);
		EditText hourTo = (EditText)findViewById(R.id.et_TargetBG_HourTo);
		EditText value = (EditText)findViewById(R.id.et_TargetBG_Glycemia);
		
		DB_Write wdb = new DB_Write(this);
		
		TargetDataBinding target = new TargetDataBinding();
		
		target.setId(idTarget);
		target.setName(name.getText().toString());
		
		if(!hourFrom.getText().toString().equals("") && !hourTo.getText().toString().equals("")){
			target.setStart(hourFrom.getText().toString());
			target.setEnd(hourTo.getText().toString());
		}
		
		target.setTarget(Double.valueOf(value.getText().toString()));
		
		wdb.Target_Update(target);
		wdb.close();
	}
	
	
	public void DeleteTarget(){
		final Context c = this;
		new AlertDialog.Builder(this)
	    .setTitle("Eliminar Objetivo de Glicemia?")
	    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton) {
	             //Falta verificar se não está associada a nenhuma entrada da DB
	        	 //Rever porque não elimina o registo de glicemia
	        	 DB_Write wdb = new DB_Write(c);
	        	 try {
	        		 wdb.Target_Remove(idTarget);
	        		 goUp();
	        	 }catch (Exception e) {
	        		 Toast.makeText(c, "Não pode eliminar este objetivo, associado a outros registos!", Toast.LENGTH_LONG).show();
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
		//NavUtils.navigateUpFromSameTask(this);
		finish();
	}
}
