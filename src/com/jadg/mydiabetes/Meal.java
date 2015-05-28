package com.jadg.mydiabetes;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.jadg.mydiabetes.database.CarbsDataBinding;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.database.GlycemiaDataBinding;
import com.jadg.mydiabetes.database.InsulinRegDataBinding;
import com.jadg.mydiabetes.database.NoteDataBinding;
import com.jadg.mydiabetes.database.TagDataBinding;
import com.jadg.mydiabetes.dialogs.DatePickerFragment;
import com.jadg.mydiabetes.dialogs.TimePickerFragment;




public class Meal extends Activity {

	
	ArrayList<String> allInsulins;
	//photo variables - start
	final private int CAPTURE_IMAGE = 2;
	Uri imgUri;
	Bitmap b;
	//photo variables - end
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meal);
		// Show the Up button in the action bar.
		getActionBar();
		
		DB_Read read = new DB_Read(this);
		if(!read.Insulin_HasInsulins()){
			ShowDialogAddInsulin();
		}
		read.close();
		
		
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
					result = 0.5 * Math.round(result/0.5);
					if(result<0){
						result = 0.0;
					}
					Log.d("resultado", result.toString());
					insulinunits.setText(String.valueOf(result));
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
					result = 0.5 * Math.round(result/0.5);
					if(result<0){
						result = 0.0;
					}
					Log.d("resultado", result.toString());
					insulinunits.setText(String.valueOf(result));
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
					result = 0.5 * Math.round(result/0.5);
					if(result<0){
						result = 0.0;
					}
					Log.d("resultado", result.toString());
					insulinunits.setText(String.valueOf(result));
				}
			}
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
				
				VerifySaveReads();
				//Toast.makeText(this, "Clicado em gravar", Toast.LENGTH_LONG).show();
				//NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
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

	//@SuppressWarnings("deprecation")
	public void showDatePickerDialog(View v){
		DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_MealDetail_Data);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "DatePicker");
	}
	
	//@SuppressWarnings("deprecation")
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
	    .setTitle("Informação")
	    .setMessage("Antes de adicionar uma refeição deve adicionar a insulina a administrar!")
	    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton) {
	             //Falta verificar se não está associada a nenhuma entrada da DB
	        	 //Rever porque não elimina o registo de glicemia
	        	 Intent intent = new Intent(c, Preferences.class);
	        	 intent.putExtra("tabPosition", 4);
	        	 startActivity(intent);
	        	 end();
	         }
	    }).show();
	}
	
	public void ShowDialogAddTarget(){
		final Context c = this;
		new AlertDialog.Builder(this)
	    .setTitle("Informação")
	    .setMessage("Antes de adicionar uma refeição deve adicionar os seus objetivos da glicemia!")
	    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton) {
	        	 //Falta verificar se não está associada a nenhuma entrada da DB
	        	 //Rever porque não elimina o registo de glicemia
	        	 Intent intent = new Intent(c, Preferences.class);
	        	 intent.putExtra("tabPosition", 1);
	        	 startActivity(intent);
	        	 finish();
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
		
		//tem de ter um target inserido
		DB_Read read = new DB_Read(this);
		if(!read.Target_HasTargets()){
			read.close();
			ShowDialogAddTarget();
			return;
		}
			
		
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
	
	
	
	//PHOTO - START
	
	public Uri setImageUri() {
        // Store image in /MyDiabetes
        File file = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes", new Date().getTime() + ".jpg");
        File dir = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes");
        if(!dir.exists()){
        	dir.mkdir();
        }
        imgUri = Uri.fromFile(file);
        return imgUri;
	}
	
		
	public void TakePhoto(View v) {
		EditText photopath = (EditText)findViewById(R.id.et_MealDetail_Photo);
		if(!photopath.getText().toString().equals("")){
			final Intent intent = new Intent(this, ViewPhoto.class);
			Bundle argsToPhoto = new Bundle();
			argsToPhoto.putString("Path", photopath.getText().toString());
			argsToPhoto.putInt("Id", -1);
			intent.putExtras(argsToPhoto);
			startActivityForResult(intent, 101010);
		}else{
			final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
			startActivityForResult(intent, CAPTURE_IMAGE);
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		EditText photopath = (EditText)findViewById(R.id.et_MealDetail_Photo);
		ImageView img = (ImageView)findViewById(R.id.iv_MealDetail_Photo);
		if (resultCode != Activity.RESULT_CANCELED) {
			if (requestCode == CAPTURE_IMAGE) {
				Toast.makeText(getApplicationContext(), getString(R.string.photoSaved) +" " + imgUri.getPath(), Toast.LENGTH_LONG).show();
				DisplayMetrics displaymetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
				int height = (int)(displaymetrics.heightPixels * 0.1);
				int width = (int)(displaymetrics.widthPixels * 0.1);
				b = decodeSampledBitmapFromPath(imgUri.getPath(),width,height );
				img.setImageBitmap(b);
				photopath.setText(imgUri.getPath());
			}else if (requestCode == 101010){
				Log.d("Result:", resultCode+"");
				//se tivermos apagado a foto dá result code -1
				//se voltarmos por um return por exemplo o resultcode é 0
				if(resultCode==-1){
					photopath.setText("");
					img.setImageDrawable(getResources().getDrawable(R.drawable.newphoto));
				}
			}else {
				super.onActivityResult(requestCode, resultCode, data);
			}
		}

	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    if (imgUri != null) {
	        outState.putString("cameraImageUri", imgUri.toString());
	    }
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
	    super.onRestoreInstanceState(savedInstanceState);
	    if (savedInstanceState.containsKey("cameraImageUri")) {
	        imgUri = Uri.parse(savedInstanceState.getString("cameraImageUri"));
	        EditText photopath = (EditText)findViewById(R.id.et_MealDetail_Photo);
			ImageView img = (ImageView)findViewById(R.id.iv_MealDetail_Photo);
			
			DisplayMetrics displaymetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int height = (int)(displaymetrics.heightPixels * 0.1);
			int width = (int)(displaymetrics.widthPixels * 0.1);
			b = decodeSampledBitmapFromPath(imgUri.getPath(),width,height );
			img.setImageBitmap(b);
			photopath.setText(imgUri.getPath());
	    }
	}
	
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {
	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }
	
	    return inSampleSize;
	}
	
	
	public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    //BitmapFactory.decodeResource(res, resId, options);
	    BitmapFactory.decodeFile(path, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return adjustImageOrientation(BitmapFactory.decodeFile(path, options),path);
	}
	
	
	private static Bitmap adjustImageOrientation(Bitmap image, String picturePath ) {
        ExifInterface exif;
        try {
            exif = new ExifInterface(picturePath);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            int rotate = 0;
            switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            }

            if (rotate != 0) {
                int w = image.getWidth();
                int h = image.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap & convert to ARGB_8888, required by tess
                image = Bitmap.createBitmap(image, 0, 0, w, h, mtx, false);

            }
        } catch (IOException e) {
                 return null;
        }
        return image.copy(Bitmap.Config.ARGB_8888, true);
    }
	//PHOTO - END
	

}
