package pt.it.porto.mydiabetes.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.ui.listAdapters.TagDataBinding;


public class TagDetail extends Activity {

	public static final String DATA = "data";
	int idTag = 0;
	String id;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tag_detail);
		// Show the Up button in the action bar.
		getActionBar();

		Bundle args = getIntent().getExtras();
		if (args != null) {
			TagDataBinding toFill = null;
			if (args.containsKey(DATA)) {
				toFill = args.getParcelable(DATA);
				if (toFill != null) {
					idTag = toFill.getId();
				}

			}
			if (toFill == null) {
				DB_Read rdb = new DB_Read(this);
				id = args.getString("Id");
				idTag = Integer.parseInt(id);
				toFill = rdb.Tag_GetById(Integer.parseInt(id));


				rdb.close();
			}
			EditText name = (EditText) findViewById(R.id.et_FaseDia_Nome);
			name.setText(toFill.getName());
			EditText from = (EditText) findViewById(R.id.et_FaseDia_HourFrom);
			from.setText(toFill.getStart());
			EditText to = (EditText) findViewById(R.id.et_FaseDia_HourTo);
			to.setText(toFill.getEnd());
			if (idTag <= 9) {
				name.setEnabled(false);
			}
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();

		Bundle args = getIntent().getExtras();
		if (args != null) {
			inflater.inflate(R.menu.tag_detail_edit, menu);
			if (Integer.parseInt(args.getString("Id")) <= 9) {
				MenuItem m = menu.findItem(R.id.menuItem_FaseDiaDetail_Delete);
				m.setVisible(false);
			}

		} else {
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
				return true;
			case R.id.menuItem_FaseDiaDetail_EditSave:
				UpdateTag();
				return true;
			case R.id.menuItem_FaseDiaDetail_Delete:
				DeleteTag();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}


	public void showTimePickerDialogFrom(View v) {
		DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.et_FaseDia_HourFrom,
				TimePickerFragment.getCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "timePicker");
	}

	public void showTimePickerDialogTo(View v) {
		DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.et_FaseDia_HourTo,
				TimePickerFragment.getCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "timePicker");
	}

	public void AddNewTag() {
		EditText name = (EditText) findViewById(R.id.et_FaseDia_Nome);
		EditText hourFrom = (EditText) findViewById(R.id.et_FaseDia_HourFrom);
		EditText hourTo = (EditText) findViewById(R.id.et_FaseDia_HourTo);

		//adicionado por zeornelas
		//obriga a colocar os valores
		if (name.getText().toString().equals("")) {
			name.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if (hourFrom.getText().toString().equals("")) {
			hourFrom.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(hourFrom, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if (hourTo.getText().toString().equals("")) {
			hourTo.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(hourTo, InputMethodManager.SHOW_IMPLICIT);
			return;
		}


		DB_Write wdb = new DB_Write(this);

		TagDataBinding tag = new TagDataBinding();


		tag.setName(name.getText().toString());

		if (!hourFrom.getText().toString().equals("") && !hourTo.getText().toString().equals("")) {
			tag.setStart(hourFrom.getText().toString());
			tag.setEnd(hourTo.getText().toString());
		}

		wdb.Tag_Add(tag);
		wdb.close();
		NavUtils.navigateUpFromSameTask(this);
	}

	public void UpdateTag() {
		EditText name = (EditText) findViewById(R.id.et_FaseDia_Nome);
		EditText hourFrom = (EditText) findViewById(R.id.et_FaseDia_HourFrom);
		EditText hourTo = (EditText) findViewById(R.id.et_FaseDia_HourTo);

		//adicionado por zeornelas
		//obriga a colocar os valores
		if (name.getText().toString().equals("")) {
			name.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if (hourFrom.getText().toString().equals("")) {
			hourFrom.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(hourFrom, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if (hourTo.getText().toString().equals("")) {
			hourTo.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(hourTo, InputMethodManager.SHOW_IMPLICIT);
			return;
		}

		DB_Write wdb = new DB_Write(this);

		TagDataBinding tag = new TagDataBinding();

		tag.setId(idTag);
		tag.setName(name.getText().toString());

		if (!hourFrom.getText().toString().equals("") && !hourTo.getText().toString().equals("")) {
			tag.setStart(hourFrom.getText().toString());
			tag.setEnd(hourTo.getText().toString());
		}

		wdb.Tag_Update(tag);
		wdb.close();
		NavUtils.navigateUpFromSameTask(this);
	}

	public void DeleteTag() {
		final Context c = this;
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.tag_info))
				.setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						///Falta verificar se não está associada a nenhuma entrada da DB
						//Rever porque não elimina o registo de glicemia
						DB_Write wdb = new DB_Write(c);
						try {
							wdb.Tag_Remove(idTag);
							goUp();
						} catch (Exception e) {
							Toast.makeText(c, getString(R.string.tag_info_delete_exception), Toast.LENGTH_LONG).show();
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

	public void goUp() {
		NavUtils.navigateUpFromSameTask(this);
	}
}
