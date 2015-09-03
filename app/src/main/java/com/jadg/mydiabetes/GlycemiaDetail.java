package com.jadg.mydiabetes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.ScrollView;
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
import android.view.inputmethod.InputMethodManager;

import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.database.GlycemiaDataBinding;
import com.jadg.mydiabetes.database.NoteDataBinding;
import com.jadg.mydiabetes.database.TagDataBinding;
import com.jadg.mydiabetes.dialogs.DatePickerFragment;
import com.jadg.mydiabetes.dialogs.TimePickerFragment;
import com.jadg.mydiabetes.usability.ActivityEvent;


public class GlycemiaDetail extends Activity {

	int idGlycemia = 0;
	int idNote = 0;

	// variavel que contem o nome da janela em que vai ser contado o tempo
	// contem o tempo de inicio ou abertura dessa janela
	// no fim de fechar a janela vai conter o tempo em que a janela foi fechada
	// e vai criar uma entrada na base de dados a registar os tempos
	ActivityEvent activityEvent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_glycemia_detail);
		// Show the Up button in the action bar.
		getActionBar();
		FillTagSpinner();

		EditText hora = (EditText)findViewById(R.id.et_GlycemiaDetail_Hora);

		// bloco de codigo que verifica se o utilizador carregou numa zona
		// "vazia" do ecra. Neste caso regista o click como um missed click
		ScrollView sv = (ScrollView)findViewById(R.id.glycemiaDetailScrollView);
		sv.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					DB_Write write = new DB_Write(GlycemiaDetail.this);    // gera uma nova instancia de escrita na base de dados
					write.newClick("GlycemiaDetail_Missed_Click");                // regista o clique na base de dados

					write.newMissed(event.getX(), event.getY(), "GlycemiaDetail");
					Log.d("test", event.toString());
				}
				return true;
			}
		});

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
		DB_Write write = new DB_Write(this);						// gera uma nova instancia de escrita na base de dados
		switch (item.getItemId()) {
			case android.R.id.home:
				write.newClick("menuItem_GlycemiaDetail_Home");	// regista o clique na base de dados

				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_GlycemiaDetail_Save:
				write.newClick("menuItem_GlycemiaDetail_Save");		// regista o clique na base de dados
				AddGlycemiaRead();
				//NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_GlycemiaDetail_Delete:
				write.newClick("menuItem_GlycemiaDetail_Delete");	// regista o clique na base de dados
				DeleteGlycemiaRead();
				//NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_GlycemiaDetail_EditSave:
				write.newClick("menuItem_GlycemiaDetail_EditSave");	// regista o clique na base de dados
				UpdateGlycemiaRead(idGlycemia);
				//NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Esta funcao e chamada sempre que a actividade atual passa a ser a GlycemiaDetail
	// ou seja, quando a janela a mostrar é a janela da GlycemiaDetail. Assim, é nesta
	// funcao que o timer inicia.
	@Override
	public void onResume(){
		activityEvent = new ActivityEvent(new DB_Write(this), "GlycemiaDetail");
		super.onPause();
	}

	// Esta funcao e chamada sempre que a actividade atual deixa de ser a GlycemiaDetail
	// ou seja, quando a janela a mostrar deixa de ser a janela da GlycemiaDetail. Assim,
	// é nesta funcao que o timer para e que guardamos a nova entrada na base de dados.
	@Override
	public void onPause(){
		activityEvent.stop();
		super.onPause();
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

		if(glycemia.getText().toString().equals("")){
			glycemia.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(glycemia, InputMethodManager.SHOW_IMPLICIT);
			return;
		}

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
		goUp();

	}
	public void UpdateGlycemiaRead(int id){
		Spinner TagSpinner = (Spinner)findViewById(R.id.sp_GlycemiaDetail_Tag);
		EditText glycemia = (EditText)findViewById(R.id.et_GlycemiaDetail_Glycemia);
		EditText data = (EditText)findViewById(R.id.et_GlycemiaDetail_Data);
		EditText hora = (EditText)findViewById(R.id.et_GlycemiaDetail_Hora);
		EditText note = (EditText)findViewById(R.id.et_GlycemiaDetail_Notes);

		if(glycemia.getText().toString().equals("")){
			glycemia.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(glycemia, InputMethodManager.SHOW_IMPLICIT);
			return;
		}

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
		goUp();
	}

	public void DeleteGlycemiaRead(){
		final Context c = this;
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.deleteReading))
				.setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB
						DB_Write wdb = new DB_Write(c);
						try {
							wdb.Glycemia_Delete(idGlycemia);
							goUp();
						} catch (Exception e) {
							Toast.makeText(c, getString(R.string.deleteException), Toast.LENGTH_LONG).show();
						}
						wdb.close();

					}
				})
				.setNegativeButton(getString(R.string.negativeButton), new DialogInterface.OnClickListener() {
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
