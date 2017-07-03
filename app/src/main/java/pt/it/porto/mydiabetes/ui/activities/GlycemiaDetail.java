//package pt.it.porto.mydiabetes.ui.activities;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.support.v4.app.NavUtils;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.EditText;
//import android.widget.Spinner;
//import android.widget.SpinnerAdapter;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import pt.it.porto.mydiabetes.R;
//import pt.it.porto.mydiabetes.data.GlycemiaRec;
//import pt.it.porto.mydiabetes.data.Note;
//import pt.it.porto.mydiabetes.database.DB_Read;
//import pt.it.porto.mydiabetes.database.DB_Write;
//
//
//public class GlycemiaDetail extends FormActivity {
//
//	int idGlycemia = 0;
//	int idNote = 0;
//
//	public static void SelectSpinnerItemByValue(Spinner spnr, String value) {
//		SpinnerAdapter adapter = spnr.getAdapter();
//		for (int position = 0; position < adapter.getCount(); position++) {
//			if (adapter.getItem(position).equals(value)) {
//				spnr.setSelection(position);
//				return;
//			}
//		}
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		hideSection(FormActivity.SECTION_CARBS);
//		hideSection(FormActivity.SECTION_INSULIN);
//		hideSection(FormActivity.SECTION_TARGET_GLICEMIA);
//
//		TextView hora = (TextView) findViewById(R.id.time);
//
//		Bundle args = getIntent().getExtras();
//		if (args != null) {
//			DB_Read rdb = new DB_Read(this);
//			idGlycemia = args.getInt("Id");
//			GlycemiaRec toFill = rdb.Glycemia_GetById(idGlycemia);
//			if (toFill != null) {
//				TextView date = (TextView) findViewById(R.id.date);
//				if (date != null) {
//					date.setText(toFill.getFormattedDate());
//				}
//				if (hora != null) {
//					hora.setText(toFill.getFormattedTime());
//				}
//				EditText glycemia = (EditText) findViewById(R.id.glycemia);
//				if (glycemia != null) {
//					glycemia.setText(String.valueOf(toFill.getValue()));
//				}
//				Spinner spinner = (Spinner) findViewById(R.id.tag);
//				SelectSpinnerItemByValue(spinner, rdb.Tag_GetById(toFill.getIdTag()).getName());
//
//				EditText note = (EditText) findViewById(R.id.notes);
//				if (toFill.getIdNote() != -1) {
//					Note n = rdb.Note_GetById(toFill.getIdNote());
//					if (note != null && n != null) {
//						note.setText(n.getNote());
//						idNote = n.getId();
//					}
//				}
//			}
//
//			rdb.close();
//		}
//
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		MenuInflater inflater = getMenuInflater();
//		Bundle args = getIntent().getExtras();
//		if (args != null) {
//			inflater.inflate(R.menu.glycemia_detail_edit, menu);
//		} else {
//			inflater.inflate(R.menu.glycemia_detail, menu);
//		}
//
//		return super.onCreateOptionsMenu(menu);
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//			case android.R.id.home:
//				NavUtils.navigateUpFromSameTask(this);
//				return true;
//			case R.id.menuItem_GlycemiaDetail_Save:
//				AddGlycemiaRead();
//				//NavUtils.navigateUpFromSameTask(this);
//				return true;
//			case R.id.menuItem_GlycemiaDetail_Delete:
//				DeleteGlycemiaRead();
//				//NavUtils.navigateUpFromSameTask(this);
//				return true;
//			case R.id.menuItem_GlycemiaDetail_EditSave:
//				UpdateGlycemiaRead(idGlycemia);
//				//NavUtils.navigateUpFromSameTask(this);
//				return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//
//	public void AddGlycemiaRead() {
//		Spinner tagSpinner = (Spinner) findViewById(R.id.tag);
//		EditText glycemia = (EditText) findViewById(R.id.glycemia);
//		TextView date = (TextView) findViewById(R.id.date);
//		TextView time = (TextView) findViewById(R.id.time);
//		EditText note = (EditText) findViewById(R.id.notes);
//
//		if (glycemia != null && glycemia.getText().toString().equals("")) {
//			glycemia.requestFocus();
//			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.showSoftInput(glycemia, InputMethodManager.SHOW_IMPLICIT);
//			return;
//		}
//
//		//Get id of user
//		DB_Read rdb = new DB_Read(this);
//		int idUser = rdb.getId();
//
//		//Get id of selected tag
//		String tag = null;
//		if (tagSpinner != null) {
//			tag = tagSpinner.getSelectedItem().toString();
//		}
//		Log.d("selected Spinner", tag);
//		int idTag = rdb.Tag_GetIdByName(tag);
//		rdb.close();
//
//		DB_Write reg = new DB_Write(this);
//		GlycemiaRec gly = new GlycemiaRec();
//
//		if (note != null && !note.getText().toString().equals("")) {
//			Note n = new Note();
//			n.setNote(note.getText().toString());
//			gly.setIdNote(reg.Note_Add(n));
//		}
//
//
//		gly.setIdUser(idUser);
//		if (glycemia != null) {
//			gly.setValue(Integer.parseInt(glycemia.getText().toString()));
//		}
//		if (date != null && time != null) {
//			gly.setDateTime(date.getText().toString(), time.getText().toString());
//		}
//		gly.setIdTag(idTag);
//
//
//		reg.Glycemia_Save(gly);
//		reg.close();
//		goUp();
//	}
//
//	public void UpdateGlycemiaRead(int id) {
//		Spinner tagSpinner = (Spinner) findViewById(R.id.tag);
//		EditText glycemia = (EditText) findViewById(R.id.glycemia);
//		TextView date = (TextView) findViewById(R.id.date);
//		TextView time = (TextView) findViewById(R.id.time);
//		EditText note = (EditText) findViewById(R.id.notes);
//
//		if (glycemia != null && glycemia.getText().toString().equals("")) {
//			glycemia.requestFocus();
//			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.showSoftInput(glycemia, InputMethodManager.SHOW_IMPLICIT);
//			return;
//		}
//
//		//Get id of user
//		DB_Read rdb = new DB_Read(this);
//		int idUser = rdb.getId();
//
//		String tag = null;
//		if (tagSpinner != null) {
//			tag = tagSpinner.getSelectedItem().toString();
//		}
//		Log.d("selected Spinner", tag);
//		int idTag = rdb.Tag_GetIdByName(tag);
//		rdb.close();
//
//		DB_Write reg = new DB_Write(this);
//		GlycemiaRec gly = new GlycemiaRec();
//
//		if (note != null && !note.getText().toString().equals("") && idNote == 0) {
//			Note n = new Note();
//			n.setNote(note.getText().toString());
//			gly.setIdNote(reg.Note_Add(n));
//		}
//		if (idNote != 0 && note != null) {
//			Note n = new Note();
//			n.setNote(note.getText().toString());
//			n.setId(idNote);
//			reg.Note_Update(n);
//		}
//
//		gly.setId(id);
//		gly.setIdUser(idUser);
//		if (glycemia != null) {
//			gly.setValue(Integer.parseInt(glycemia.getText().toString()));
//		}
//		if (date != null && time != null) {
//			gly.setDateTime(date.getText().toString(), time.getText().toString());
//		}
//		gly.setIdTag(idTag);
//
//		reg.Glycemia_Update(gly);
//		reg.close();
//		goUp();
//	}
//
//	public void DeleteGlycemiaRead() {
//		final Context c = this;
//		new AlertDialog.Builder(this)
//				.setTitle(getString(R.string.deleteReading))
//				.setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						//Falta verificar se não está associada a nenhuma entrada da DB
//						DB_Write wdb = new DB_Write(c);
//						try {
//							wdb.Glycemia_Delete(idGlycemia);
//							goUp();
//						} catch (Exception e) {
//							Toast.makeText(c, getString(R.string.deleteException), Toast.LENGTH_LONG).show();
//						}
//						wdb.close();
//
//					}
//				})
//				.setNegativeButton(getString(R.string.negativeButton), new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						// Do nothing.
//					}
//				}).show();
//	}
//
//	public void goUp() {
//		NavUtils.navigateUpFromSameTask(this);
//	}
//
//
//	@Override
//	void insulinsNotFound() {
//
//	}
//
//	@Override
//	protected boolean shouldSetInsulin() {
//		return false;
//	}
//
//}
