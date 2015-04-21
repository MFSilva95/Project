package com.jadg.mydiabetes;

import java.io.File;
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
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.inputmethod.InputMethodManager;

import com.jadg.mydiabetes.database.CarbsDataBinding;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.database.NoteDataBinding;
import com.jadg.mydiabetes.database.TagDataBinding;
import com.jadg.mydiabetes.dialogs.DatePickerFragment;
import com.jadg.mydiabetes.dialogs.TimePickerFragment;




public class CarboHydrateDetail extends Activity {

	private static int TAKE_PICTURE = 1;
	private Uri outputFileUri;
	private String now;
	int idNote = 0;
	private String _id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_carbohydrate_detail);
		// Show the Up button in the action bar.
		getActionBar();
		FillTagSpinner();
		EditText hora = (EditText)findViewById(R.id.et_CarboHydrateDetail_Hora);
		Bundle args = getIntent().getExtras();
		if(args!=null){
			DB_Read rdb = new DB_Read(this);
			String id = args.getString("Id");
			_id = id;
			CarbsDataBinding toFill = rdb.CarboHydrate_GetById(Integer.parseInt(id));
			
			Spinner tagSpinner = (Spinner)findViewById(R.id.sp_CarboHydrateDetail_Tag);
			SelectSpinnerItemByValue(tagSpinner, rdb.Tag_GetById(toFill.getId_Tag()).getName());
			EditText carbs = (EditText)findViewById(R.id.et_CarboHydrateDetail_Value);
			carbs.setText(toFill.getCarbsValue().toString());
			EditText data = (EditText)findViewById(R.id.et_CarboHydrateDetail_Data);
			data.setText(toFill.getDate());
			Log.d("data reg carb", toFill.getDate());
			hora.setText(toFill.getTime());
			
			EditText note = (EditText)findViewById(R.id.et_CarboHydrateDetail_Notes);
			if(toFill.getId_Note()!=-1){
				NoteDataBinding n = new NoteDataBinding();
				n=rdb.Note_GetById(toFill.getId_Note());
				note.setText(n.getNote());
				idNote=n.getId();
			}
			
			EditText photopath = (EditText)findViewById(R.id.et_CarboHydrateDetail_Photo);
			if(!toFill.getPhotoPath().equals("")){
				photopath.setText(toFill.getPhotoPath());
				Log.d("foto path", "foto: " + toFill.getPhotoPath());
				File dir = new File(Environment.getExternalStorageDirectory() + toFill.getPhotoPath());
				ImageView img = (ImageView)findViewById(R.id.iv_CarboHydrateDetail_Photo);
				img.setImageURI(Uri.fromFile(dir));
			}
				
			Log.d("texto no photopath", "texto:" + toFill.getPhotoPath() + ":");
			
			rdb.close();
		}else{
			FillDateHour();
			SetTagByTime();
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
		
		
		
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		Bundle args = getIntent().getExtras();
		if(args!=null){
			inflater.inflate(R.menu.carbo_hydrate_detail_edit, menu);
		}else{
			inflater.inflate(R.menu.carbo_hydrate_detail, menu);
		}
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Bundle args = getIntent().getExtras();
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_CarboHydrateDetail_Save:
				AddCarbsRead();
				//NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_CarboHydrateDetail_Delete:
				DeleteCarbsRead(Integer.parseInt(args.getString("Id")));
				//NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_CarboHydrateDetail_EditSave:
				UpdateCarbsRead(Integer.parseInt(args.getString("Id")));
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void SetTagByTime(){
		Spinner tagSpinner = (Spinner)findViewById(R.id.sp_CarboHydrateDetail_Tag);
		EditText hora = (EditText)findViewById(R.id.et_CarboHydrateDetail_Hora);
		DB_Read rdb = new DB_Read(this);
		String name = rdb.Tag_GetByTime(hora.getText().toString()).getName();
		rdb.close();
		SelectSpinnerItemByValue(tagSpinner, name);
	}
	
	@SuppressLint("SimpleDateFormat")
	public void FillDateHour(){
		EditText date = (EditText)findViewById(R.id.et_CarboHydrateDetail_Data);
		final Calendar c = Calendar.getInstance();
	    Date newDate = c.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    String dateString = formatter.format(newDate);
        date.setText(dateString);
        
        EditText hour = (EditText)findViewById(R.id.et_CarboHydrateDetail_Hora);
        formatter = new SimpleDateFormat("HH:mm:ss");
        String timeString = formatter.format(newDate);
        hour.setText(timeString);
	}
	public void showDatePickerDialog(View v){
		DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_CarboHydrateDetail_Data);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "DatePicker");
	}
	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_CarboHydrateDetail_Hora);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "timePicker");
	    
	}
	
	public void FillTagSpinner(){
		Spinner spinner = (Spinner) findViewById(R.id.sp_CarboHydrateDetail_Tag);
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
	
	public void AddCarbsRead(){
		Spinner tagSpinner = (Spinner)findViewById(R.id.sp_CarboHydrateDetail_Tag);
		EditText carbs = (EditText)findViewById(R.id.et_CarboHydrateDetail_Value);
		EditText data = (EditText)findViewById(R.id.et_CarboHydrateDetail_Data);
		EditText hora = (EditText)findViewById(R.id.et_CarboHydrateDetail_Hora);
		EditText photopath = (EditText)findViewById(R.id.et_CarboHydrateDetail_Photo);
		EditText note = (EditText)findViewById(R.id.et_CarboHydrateDetail_Notes);
		
		//adicionado por zeornelas
		//para obrigar a colocar o valor dos hidratos e nao crashar
		if(carbs.getText().toString().equals("")){
			carbs.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(carbs, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		
		
		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());
		
		//Get id of selected tag
		String tag = tagSpinner.getSelectedItem().toString();
		Log.d("selected Spinner", tag);
		int idTag = rdb.Tag_GetIdByName(tag);
		rdb.close();
		DB_Write reg = new DB_Write(this);
        CarbsDataBinding carb = new CarbsDataBinding();
        
        if(!note.getText().toString().equals("")){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			carb.setId_Note(reg.Note_Add(n));
		}
        
        
        carb.setId_User(idUser);
        carb.setCarbsValue(Double.parseDouble(carbs.getText().toString()));
        carb.setId_Tag(idTag);
        carb.setPhotoPath(photopath.getText().toString()); // /data/MyDiabetes/yyyy-MM-dd HH.mm.ss.jpg
        carb.setDate(data.getText().toString());
        carb.setTime(hora.getText().toString());

	
		
		reg.Carbs_Save(carb);
		reg.close();
		NavUtils.navigateUpFromSameTask(this);
	}
	
	public void TakePhoto(View v) {
		EditText photopath = (EditText)findViewById(R.id.et_CarboHydrateDetail_Photo);
		if(!photopath.getText().toString().equals("")){
			Intent intent = new Intent(v.getContext(), ViewPhoto.class);
			
			Bundle args = getIntent().getExtras();
			Bundle argsToPhoto = new Bundle();
			argsToPhoto.putString("Path", photopath.getText().toString());
			argsToPhoto.putInt("Id", Integer.parseInt(args.getString("Id")));
			intent.putExtras(argsToPhoto);
			//v.getContext().startActivity(intent);
			startActivityForResult(intent, 101010);
		}
		else{
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File dir = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes");
			dir.mkdirs();
			
			final Calendar c = Calendar.getInstance();
	        int year = c.get(Calendar.YEAR);
	        int month = c.get(Calendar.MONTH);
	        int day = c.get(Calendar.DAY_OF_MONTH);
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			int sec = c.get(Calendar.SECOND);
			now = year + "-" + (month+1) + "-" + day + " " + hour + "." + minute + "." + sec;
			File file = new File(dir, now + ".jpg");
			
			outputFileUri = Uri.fromFile(file);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			
			
			startActivityForResult(intent, TAKE_PICTURE);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode == TAKE_PICTURE && resultCode!= Activity.RESULT_CANCELED){
			Toast.makeText(getApplicationContext(), outputFileUri.toString(), Toast.LENGTH_LONG).show();
			EditText c = (EditText)findViewById(R.id.et_CarboHydrateDetail_Photo);
			c.setText("/MyDiabetes/" + now + ".jpg");
			ImageView img = (ImageView)findViewById(R.id.iv_CarboHydrateDetail_Photo);
			img.setImageURI(outputFileUri);
			deleteLastCapturedImage();
		}
		if (requestCode == 101010){
			DB_Read rdb = new DB_Read(this);
			CarbsDataBinding toFill = rdb.CarboHydrate_GetById(Integer.parseInt(_id));
			if (toFill.getPhotoPath().equals("")){
				EditText photopath = (EditText)findViewById(R.id.et_CarboHydrateDetail_Photo);
				photopath.setText(toFill.getPhotoPath());
				ImageView img = (ImageView)findViewById(R.id.iv_CarboHydrateDetail_Photo);
				img.setImageDrawable(getResources().getDrawable(R.drawable.newphoto));
			}
		}
		
 
	}

	@SuppressWarnings("deprecation")
	public void deleteLastCapturedImage() {
	    String[] projection = { 
	            MediaStore.Images.ImageColumns.SIZE,
	            MediaStore.Images.ImageColumns.DISPLAY_NAME,
	            MediaStore.Images.ImageColumns.DATA, 
	            BaseColumns._ID };
	    Cursor c = null;
	    Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	    try {
	        if (u != null) {
	            c = managedQuery(u, projection, null, null, null); }
	        if ((c != null) && (c.moveToLast())) {
	            ContentResolver cr = getContentResolver();
	            int i = cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseColumns._ID + "=" + c.getString(c.getColumnIndex(BaseColumns._ID)), null);
	            Log.v("delete dup foto", "Number of column deleted : " + i);
	        }
	    } finally {
	        if (c != null) {
	            c.close(); }
	    }
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
	
	public void DeleteCarbsRead(final int id){
		final Context c = this;
		new AlertDialog.Builder(this)
	    .setTitle("Eliminar leitura?")
	    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton) {
	             //Falta verificar se n�o est� associada a nenhuma entrada da DB
	        	 DB_Write wdb = new DB_Write(c);
	        	 try {
	        		 wdb.Carbs_Delete(id);
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
	
	public void UpdateCarbsRead(int id){
		Spinner tagSpinner = (Spinner)findViewById(R.id.sp_CarboHydrateDetail_Tag);
		EditText carbs = (EditText)findViewById(R.id.et_CarboHydrateDetail_Value);
		EditText data = (EditText)findViewById(R.id.et_CarboHydrateDetail_Data);
		EditText hora = (EditText)findViewById(R.id.et_CarboHydrateDetail_Hora);
		EditText photopath = (EditText)findViewById(R.id.et_CarboHydrateDetail_Photo);
		EditText note = (EditText)findViewById(R.id.et_CarboHydrateDetail_Notes);
		
		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());
		
		//Get id of selected tag
		String tag = tagSpinner.getSelectedItem().toString();
		Log.d("selected Spinner", tag);
		int idTag = rdb.Tag_GetIdByName(tag);
		rdb.close();
		DB_Write reg = new DB_Write(this);
        CarbsDataBinding carb = new CarbsDataBinding();
        
        if(!note.getText().toString().equals("") && idNote==0){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			carb.setId_Note(reg.Note_Add(n));
		}
		if(idNote!=0){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			n.setId(idNote);
			reg.Note_Update(n);
		}
        
        carb.setId(id);
        carb.setId_User(idUser);
        carb.setCarbsValue(Double.parseDouble(carbs.getText().toString()));
        carb.setId_Tag(idTag);
        carb.setPhotoPath(photopath.getText().toString()); // /data/MyDiabetes/yyyy-MM-dd HH.mm.ss.jpg
        carb.setDate(data.getText().toString());
        carb.setTime(hora.getText().toString());

	
		reg.Carbs_Update(carb);
		reg.close();
	}
}
