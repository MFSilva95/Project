package pt.it.porto.mydiabetes.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.dataBinding.DiseaseDataBinding;

public class DiseasesDetail extends Activity {
	public static final String BUNDLE_DATA = "Data";

	int idDiseases = 0;
	String diseaseName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diseases_detail);
		// Show the Up button in the action bar.
		getActionBar();

		Bundle args = getIntent().getExtras();
		if (args != null) {
			if (args.containsKey(BUNDLE_DATA)) {
				DiseaseDataBinding data = args.getParcelable(BUNDLE_DATA);
				if (data != null) {
					idDiseases = data.getId();
					diseaseName = data.getName();
				}
			}
			if (diseaseName == null || diseaseName.isEmpty()) {
				String id = args.getString("Id");
				idDiseases = Integer.parseInt(id);
				diseaseName = args.getString("Name");
			}
			EditText name = (EditText) findViewById(R.id.et_Diseases_Nome);
			name.setText(diseaseName);

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();

		Bundle args = getIntent().getExtras();
		if (args != null) {
			inflater.inflate(R.menu.insulins_detail_edit, menu);
		} else {
			inflater.inflate(R.menu.insulins_detail, menu);
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
			case R.id.menuItem_InsulinsDetail_Save:
				AddNewDisease();
				return true;
			case R.id.menuItem_InsulinsDetail_EditSave:
				UpdateDisease();
				return true;
			case R.id.menuItem_InsulinsDetail_Delete:
				DeleteDisease();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void AddNewDisease() {
		// deal with the editable
		DB_Write wdb = new DB_Write(this);
		EditText diseasename = (EditText) findViewById(R.id.et_Diseases_Nome);
		//adicionado por zeornelas
		//obriga a colocar os valores
		if (diseasename.getText().toString().equals("")) {
			diseasename.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(diseasename, InputMethodManager.SHOW_IMPLICIT);
			return;
		}

		wdb.Disease_Add(diseasename.getText().toString());
		wdb.close();
		finish();
	}


	public void UpdateDisease() {
		EditText diseasename = (EditText) findViewById(R.id.et_Diseases_Nome);
		//adicionado por zeornelas
		//obriga a colocar os valores
		if (diseasename.getText().toString().equals("")) {
			diseasename.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(diseasename, InputMethodManager.SHOW_IMPLICIT);
			return;
		}

		DB_Write wdb = new DB_Write(this);

		DiseaseDataBinding disease = new DiseaseDataBinding();

		disease.setId(idDiseases);
		disease.setName(diseasename.getText().toString());


		wdb.Disease_Update(disease);
		wdb.close();
		finish();

	}


	public void DeleteDisease() {
		final Context c = this;
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.delete_Disease))
				.setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB

						DB_Write wdb = new DB_Write(c);
						wdb.Disease_Remove(idDiseases);
						wdb.close();
						Log.d("to delete diseases id: ", String.valueOf(idDiseases));
						finish();

					}
				})
				.setNegativeButton(getString(R.string.negativeButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Do nothing.
					}
				}).show();
	}

	public void goUp() {
		//NavUtils.navigateUpFromSameTask(this);
		finish();
	}

}
