package com.jadg.mydiabetes;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

import com.jadg.mydiabetes.database.CarbsDataBinding;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.database.GlycemiaDataBinding;
import com.jadg.mydiabetes.database.InsulinRegDataBinding;
import com.jadg.mydiabetes.database.NoteDataBinding;
import com.jadg.mydiabetes.database.TagDataBinding;
import com.jadg.mydiabetes.dialogs.DatePickerFragment;
import com.jadg.mydiabetes.dialogs.TimePickerFragment;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;



public class LogbookDetail extends Activity {

	private static int TAKE_PICTURE = 1;
	private Uri outputFileUri;
	private String now;
	ArrayList<String> allInsulins;
	
	private int id_ch = -1;
	private int id_ins = -1;
	private int id_bg = -1;
	private CarbsDataBinding ch = null;
	private InsulinRegDataBinding ins = null;
	private GlycemiaDataBinding bg = null;
	int tagId;
	int insulinId;
	int noteId;
	
	EditText data;
	EditText hora;
	Spinner TagSpinner;
	EditText glycemia;
	ImageView img;
	EditText carbs;
	Spinner InsulinSpinner;
	EditText target;
	EditText insulin;
	EditText note;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meal);
		// Show the Up button in the action bar.
		getActionBar();
		
		
		
		FillDateHour();
		FillTagSpinner();
		SetTagByTime();
		SetTargetByHour();
		FillInsulinSpinner();
		
		EditText hora = (EditText)findViewById(R.id.et_MealDetail_Hora);
		hora.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				SetTagByTime(); }
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override
			public void afterTextChanged(Editable s) { }
		});
		
		
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		final double iRatio = Double.valueOf(obj[3].toString());
		final double cRatio = Double.valueOf(obj[4].toString());
		rdb.close();
		
		final EditText insulinunits = (EditText)findViewById(R.id.et_MealDetail_InsulinUnits);
		final EditText target = (EditText)findViewById(R.id.et_MealDetail_TargetGlycemia);
		final EditText glycemia = (EditText)findViewById(R.id.et_MealDetail_Glycemia);
		final EditText carbs = (EditText)findViewById(R.id.et_MealDetail_Carbs);
		
		
		target.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(!target.getText().toString().equals("") && !glycemia.getText().toString().equals("") && !carbs.getText().toString().equals("")){
					Double gli = Double.parseDouble(glycemia.getText().toString());
					Double tar = Double.parseDouble(target.getText().toString());
					Double car = Double.parseDouble(carbs.getText().toString());
					Double result = ((gli-tar)/ iRatio) + (car/cRatio);
					Log.d("resultado", result.toString());
					insulinunits.setText(String.valueOf(0.5 * Math.round(result/0.5)));
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override
			public void afterTextChanged(Editable s) { }
		});
		
		glycemia.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(!target.getText().toString().equals("") && !glycemia.getText().toString().equals("") && !carbs.getText().toString().equals("")){
					Double gli = Double.parseDouble(glycemia.getText().toString());
					Double tar = Double.parseDouble(target.getText().toString());
					Double car = Double.parseDouble(carbs.getText().toString());
					Double result = ((gli-tar)/ iRatio) + (car/cRatio);
					Log.d("resultado", result.toString());
					insulinunits.setText(String.valueOf(0.5 * Math.round(result/0.5)));
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override
			public void afterTextChanged(Editable s) { }
		});
		
		carbs.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(!target.getText().toString().equals("") && !glycemia.getText().toString().equals("") && !carbs.getText().toString().equals("")){
					Double gli = Double.parseDouble(glycemia.getText().toString());
					Double tar = Double.parseDouble(target.getText().toString());
					Double car = Double.parseDouble(carbs.getText().toString());
					Double result = ((gli-tar)/ iRatio) + (car/cRatio);
					Log.d("resultado", result.toString());
					insulinunits.setText(String.valueOf(0.5 * Math.round(result/0.5)));
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override
			public void afterTextChanged(Editable s) { }
		});
		
		
		
		Bundle args = getIntent().getExtras();
		if(args!=null){
			String arg_ch = args.getString("ch");
			String arg_ins = args.getString("ins");
			String arg_bg = args.getString("bg");
			
			rdb = new DB_Read(this);
			
			if (!arg_ch.isEmpty()){
				id_ch = Integer.parseInt(arg_ch);
				ch = rdb.CarboHydrate_GetById(id_ch);
			}
			if (!arg_ins.isEmpty()){
				id_ins = Integer.parseInt(arg_ins);
				ins = rdb.InsulinReg_GetById(id_ins);
			}
			if (!arg_bg.isEmpty()){
				id_bg = Integer.parseInt(arg_bg);
				bg = rdb.Glycemia_GetById(id_bg);
			}
			rdb.close();
			fillAllValues();
		}
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		
		inflater.inflate(R.menu.meal, menu);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_MealDetail_Save:
				
				//VerifySaveReads();
				//Toast.makeText(this, "Clicado em gravar", Toast.LENGTH_LONG).show();
				//NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//created by zeornelas
	public void fillAllValues(){
		DB_Read rdb = new DB_Read(this);
		//File file;  
		
		data = (EditText)findViewById(R.id.et_MealDetail_Data);
		hora = (EditText)findViewById(R.id.et_MealDetail_Hora);
		
		TagSpinner = (Spinner)findViewById(R.id.sp_MealDetail_Tag);
		glycemia = (EditText)findViewById(R.id.et_MealDetail_Glycemia);
		img = (ImageView)findViewById(R.id.iv_MealDetail_Photo);
		carbs = (EditText)findViewById(R.id.et_MealDetail_Carbs);
		InsulinSpinner = (Spinner)findViewById(R.id.sp_MealDetail_Insulin);
		target = (EditText)findViewById(R.id.et_MealDetail_TargetGlycemia);
		insulin = (EditText)findViewById(R.id.et_MealDetail_InsulinUnits);
		note = (EditText)findViewById(R.id.et_MealDetail_Notes);
		
		//coloca a photo
		if(ch!=null){
			EditText photopath = (EditText)findViewById(R.id.et_MealDetail_Photo);
			if(!ch.getPhotoPath().equals("")){
				photopath.setText(ch.getPhotoPath());
				Log.d("foto path", "foto: " + ch.getPhotoPath());
				File dir = new File(Environment.getExternalStorageDirectory() + ch.getPhotoPath());
				ImageView img = (ImageView)findViewById(R.id.iv_MealDetail_Photo);
				img.setImageURI(Uri.fromFile(dir));
			}
		}
		
		
		if(ch!=null && ins!=null && bg!=null){
			data.setText(ins.getDate());
			hora.setText(ins.getTime());;
			tagId = ins.getIdTag();
			String aux = rdb.Tag_GetById(tagId).getName();
			SelectSpinnerItemByValue(TagSpinner, aux);
			glycemia.setText(bg.getValue().toString());
			
			/*
			if(!ch.getPhotoPath().isEmpty()){
				file = new File(Environment.getExternalStorageDirectory() + ch.getPhotoPath());
				img.setImageURI(Uri.fromFile(file));
			}
			*/
			carbs.setText(ch.getCarbsValue().toString());
			insulinId = ins.getIdInsulin();
			String aux1 = rdb.Insulin_GetById(insulinId).getName();
			SelectSpinnerItemByValue(InsulinSpinner, aux1);
            target.setText(ins.getTargetGlycemia().toString());
			insulin.setText(ins.getInsulinUnits().toString());
			noteId = ins.getIdNote();
			if(noteId!=-1){
				note.setText(rdb.Note_GetById(noteId).getNote());
			}
		}else if(ch!=null && ins== null && bg==null){//so hidratos carbono
        	data.setText(ch.getDate());
            hora.setText(ch.getTime());
            tagId = ch.getId_Tag();
			String aux = rdb.Tag_GetById(tagId).getName();
			SelectSpinnerItemByValue(TagSpinner, aux);
            glycemia.setText("");
			/*
            img.setImageURI(Uri.parse(ch.getPhotoPath()));
			Log.d("Imagem", "link:" + ch.getPhotoPath());
			if(!ch.getPhotoPath().isEmpty()){
				file = new File(Environment.getExternalStorageDirectory() + ch.getPhotoPath());
				img.setImageURI(Uri.fromFile(file));
			}
			*/
			carbs.setText(ch.getCarbsValue().toString());
            target.setText("");
            insulin.setText("");
            noteId = ch.getId_Note();
            if(noteId!=-1){
				note.setText(rdb.Note_GetById(noteId).getNote());
			}
			
        }else if(ch==null && ins!= null && bg!=null){//insulina com parametro da glicemia
        	data.setText(ins.getDate());
            hora.setText(ins.getTime());
            tagId = ins.getIdTag();
			String aux = rdb.Tag_GetById(tagId).getName();
			SelectSpinnerItemByValue(TagSpinner, aux);
            glycemia.setText(bg.getValue().toString());
            carbs.setText("");
            insulinId = ins.getIdInsulin();
			String aux1 = rdb.Insulin_GetById(insulinId).getName();
			SelectSpinnerItemByValue(InsulinSpinner, aux1);
            target.setText(ins.getTargetGlycemia().toString());
            insulin.setText(ins.getInsulinUnits().toString());
            noteId = ins.getIdNote();
            if(noteId!=-1){
				note.setText(rdb.Note_GetById(noteId).getNote());
			}
        }else if(ch==null && ins== null && bg!=null){//so glicemia
        	data.setText(bg.getDate());
            hora.setText(bg.getTime());
            tagId = bg.getIdTag();
			String aux = rdb.Tag_GetById(tagId).getName();
			SelectSpinnerItemByValue(TagSpinner, aux);
            glycemia.setText(bg.getValue().toString());
            carbs.setText("");
            target.setText("");
            insulin.setText("");
            noteId = bg.getIdNote();
            if(noteId!=-1){
				note.setText(rdb.Note_GetById(noteId).getNote());
			}
          
        }else if(ch==null && ins!= null && bg==null){//so insulina
        	data.setText(ins.getDate());
            hora.setText(ins.getTime());
            tagId = ins.getIdTag();
			String aux = rdb.Tag_GetById(tagId).getName();
			SelectSpinnerItemByValue(TagSpinner, aux);
            glycemia.setText("");
            carbs.setText("");
            insulinId = ins.getIdInsulin();
			String aux1 = rdb.Insulin_GetById(insulinId).getName();
			SelectSpinnerItemByValue(InsulinSpinner, aux1);
            target.setText(ins.getTargetGlycemia().toString());
            insulin.setText(ins.getInsulinUnits().toString());
            noteId = ins.getIdNote();
            if(noteId!=-1){
				note.setText(rdb.Note_GetById(noteId).getNote());
			}
        }
		rdb.close();
	}
	
	
	
	@SuppressLint("SimpleDateFormat")
	public void FillDateHour(){
		EditText date = (EditText)findViewById(R.id.et_MealDetail_Data);
		final Calendar c = Calendar.getInstance();
	    Date newDate = c.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    String dateString = formatter.format(newDate);
        date.setText(dateString);
        
        EditText hour = (EditText)findViewById(R.id.et_MealDetail_Hora);
        formatter = new SimpleDateFormat("HH:mm:ss");
        String timeString = formatter.format(newDate);
        hour.setText(timeString);
	}
	
	public void FillTagSpinner(){
		Spinner spinner = (Spinner) findViewById(R.id.sp_MealDetail_Tag);
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

	public void showDatePickerDialog(View v){
		DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_MealDetail_Data);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "DatePicker");
	}
	
	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_MealDetail_Hora);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "timePicker");
	    
	}

	public void SetTagByTime(){
		Spinner tagSpinner = (Spinner)findViewById(R.id.sp_MealDetail_Tag);
		EditText hora = (EditText)findViewById(R.id.et_MealDetail_Hora);
		DB_Read rdb = new DB_Read(this);
		String name = rdb.Tag_GetByTime(hora.getText().toString()).getName();
		rdb.close();
		SelectSpinnerItemByValue(tagSpinner, name);
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
	

	public void ShowDialogAddInsulin(){
		final Context c = this;
		new AlertDialog.Builder(this)
	    .setTitle("Informa��o")
	    .setMessage("Antes de Adicionar registos de insulina deve adicionar a insulina a administrar!")
	    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton) {
	             //Falta verificar se n�o est� associada a nenhuma entrada da DB
	        	 //Rever porque n�o elimina o registo de glicemia
	        	 Intent intent = new Intent(c, Preferences.class);
	        	 intent.putExtra("tabPosition", 4);
	        	 startActivity(intent);
	        	 end();
	         }
	    }).show();
	}
	
	public void end(){
		finish();
	}

	public void SetTargetByHour(){
		EditText target = (EditText)findViewById(R.id.et_MealDetail_TargetGlycemia);
		EditText hora = (EditText)findViewById(R.id.et_MealDetail_Hora);
		DB_Read rdb = new DB_Read(this);
		double d = rdb.Target_GetTargetByTime(hora.getText().toString());
		if(d!=0){
			target.setText(String.valueOf(d));
		}
		rdb.close();
	}
	
	public void FillInsulinSpinner(){
		Spinner spinner = (Spinner) findViewById(R.id.sp_MealDetail_Insulin);
		allInsulins = new ArrayList<String>();
		DB_Read rdb = new DB_Read(this);
		HashMap<Integer, String> val = rdb.Insulin_GetAllNames();
		rdb.close();
		
		if(val!=null){
			for (int i : val.keySet()){
				allInsulins.add(val.get(i));
			}
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, allInsulins);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}
	
	
	
	//-------------------------------------------
	public void AddGlycemiaRead(){
		Spinner TagSpinner = (Spinner)findViewById(R.id.sp_MealDetail_Tag);
		EditText glycemia = (EditText)findViewById(R.id.et_MealDetail_Glycemia);
		EditText data = (EditText)findViewById(R.id.et_MealDetail_Data);
		EditText hora = (EditText)findViewById(R.id.et_MealDetail_Hora);
		EditText note = (EditText)findViewById(R.id.et_MealDetail_Notes);
		
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

	
	
	public void AddCarbsRead(){
		Spinner tagSpinner = (Spinner)findViewById(R.id.sp_MealDetail_Tag);
		EditText carbs = (EditText)findViewById(R.id.et_MealDetail_Carbs);
		EditText data = (EditText)findViewById(R.id.et_MealDetail_Data);
		EditText hora = (EditText)findViewById(R.id.et_MealDetail_Hora);
		EditText photopath = (EditText)findViewById(R.id.et_MealDetail_Photo);
		EditText note = (EditText)findViewById(R.id.et_MealDetail_Notes);
		
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
	}
	
	public void TakePhoto(View v) {
		EditText photopath = (EditText)findViewById(R.id.et_MealDetail_Photo);
		if(!photopath.getText().toString().equals("")){
			Intent intent = new Intent(v.getContext(), ViewPhoto.class);
			
			Bundle argsToPhoto = new Bundle();
			argsToPhoto.putString("Path", photopath.getText().toString());
			argsToPhoto.putInt("Id", id_ch);
			intent.putExtras(argsToPhoto);
			//v.getContext().startActivity(intent);
			startActivityForResult(intent, 101010);
		}else{
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
			now = year + "" + (month+1) + "" + day + "" + hour + "" + minute + "" + sec;
			File file = new File(dir, now + ".jpg");
			
			outputFileUri = Uri.fromFile(file);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			startActivityForResult(intent, TAKE_PICTURE);
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		EditText photopath = (EditText)findViewById(R.id.et_MealDetail_Photo);
		ImageView img = (ImageView)findViewById(R.id.iv_MealDetail_Photo);
		if (requestCode == TAKE_PICTURE && resultCode!= Activity.RESULT_CANCELED){
			Toast.makeText(getApplicationContext(), outputFileUri.toString(), Toast.LENGTH_LONG).show();
			photopath.setText("/MyDiabetes/" + now + ".jpg");
			img.setImageURI(outputFileUri);
			deleteLastCapturedImage();
		}
		if (requestCode == 101010){
			//se tivermos apagado a foto dá result code -1
			//se voltarmos por um return por exemplo o resultcode é 0
			if(resultCode==-1){
				photopath.setText("");
				img.setImageDrawable(getResources().getDrawable(R.drawable.newphoto));
			}
		}
	}
	

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
	        	//c = managedQuery(u, projection, null, null, null);
	            c = getContentResolver().query(u, projection, null, null, null);
	        }
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
	
	
	
	public void AddInsulinRead(){
		Spinner tagSpinner = (Spinner)findViewById(R.id.sp_MealDetail_Tag);
		Spinner insulinSpinner = (Spinner)findViewById(R.id.sp_MealDetail_Insulin);
		EditText glycemia = (EditText)findViewById(R.id.et_MealDetail_Glycemia);
		EditText data = (EditText)findViewById(R.id.et_MealDetail_Data);
		EditText hora = (EditText)findViewById(R.id.et_MealDetail_Hora);
		EditText target = (EditText)findViewById(R.id.et_MealDetail_TargetGlycemia);
		EditText insulinunits = (EditText)findViewById(R.id.et_MealDetail_InsulinUnits);
		EditText note = (EditText)findViewById(R.id.et_MealDetail_Notes);
		
		
		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());
		
		//Get id of selected tag
		String tag = tagSpinner.getSelectedItem().toString();
		Log.d("selected Spinner", tag);
		int idTag = rdb.Tag_GetIdByName(tag);
		
		//Get id of selected insulin
		String insulin = insulinSpinner.getSelectedItem().toString();
		Log.d("selected Spinner", insulin);
		int idInsulin = rdb.Insulin_GetByName(insulin).getId();
		
		
        int idGlycemia = 0;
        boolean hasGlycemia = false;
        DB_Write reg = new DB_Write(this);
        
        InsulinRegDataBinding ins = new InsulinRegDataBinding();
        
        int idnote = 0;
        
        if(!note.getText().toString().equals("")){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			idnote = reg.Note_Add(n);
			ins.setIdNote(idnote);
		}
        
        if(!glycemia.getText().toString().equals("")){
        	GlycemiaDataBinding gly = new GlycemiaDataBinding();
        	
        	gly.setIdUser(idUser);
            gly.setValue(Double.parseDouble(glycemia.getText().toString()));
            gly.setDate(data.getText().toString());
            gly.setTime(hora.getText().toString());
            gly.setIdTag(idTag);
    		if(idnote > 0){
    			gly.setIdNote(idnote);
    		}
            
    		idGlycemia = reg.Glycemia_Save(gly);
    		Log.d("id glycemia", String.valueOf(idGlycemia));
    		hasGlycemia = true;
        }
        
        ins.setIdUser(idUser);
        ins.setIdInsulin(idInsulin);
        ins.setIdBloodGlucose(hasGlycemia ? idGlycemia : -1);
        ins.setDate(data.getText().toString());
        ins.setTime(hora.getText().toString());
        ins.setTargetGlycemia(Double.parseDouble(target.getText().toString()));
        ins.setInsulinUnits(Double.parseDouble(insulinunits.getText().toString()));
        ins.setIdTag(idTag);
        
		
		reg.Insulin_Save(ins);
		
		reg.close();
		rdb.close();
	}
	
	//-----------------------------------------
	
	public void VerifySaveReads(){
		EditText insulinunits = (EditText)findViewById(R.id.et_MealDetail_InsulinUnits);
		EditText target = (EditText)findViewById(R.id.et_MealDetail_TargetGlycemia);
		EditText glycemia = (EditText)findViewById(R.id.et_MealDetail_Glycemia);
		EditText carbs = (EditText)findViewById(R.id.et_MealDetail_Carbs);
		
		if(glycemia.getText().toString().equals("")){
			glycemia.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(glycemia, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if(carbs.getText().toString().equals("")){
			carbs.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(carbs, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if(target.getText().toString().equals("")){
			target.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(target, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if(insulinunits.getText().toString().equals("")){
			insulinunits.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(insulinunits, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		//spinner das insulinas tem de ter valores
		if(allInsulins.isEmpty()){
					ShowDialogAddInsulin();
					return;
		}
		
		//AddGlycemiaRead();
		AddCarbsRead();
		AddInsulinRead();
		NavUtils.navigateUpFromSameTask(this);
	}
	/*
	//created by zeornelas
	public void saveButton(){
		
		//bg
		
		//carbs
		if(ch==null){
			if(!carbs.getText().toString().equals("")){
				
			}
		}
		
		//ins
	}
	
	public void saveBg(){
		
	}
	
	public void updateBg(){
		
	}
	
	
	public void saveCarbs(){
		
	}
	
	public void updateCarbs(){
		
	}
	
	public void saveIns(){
		
	}
	
	public void updateIns(){
		
	}
	*/
	
}
