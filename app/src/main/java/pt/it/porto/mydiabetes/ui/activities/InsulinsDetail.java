package pt.it.porto.mydiabetes.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.listAdapters.InsulinDataBinding;


public class InsulinsDetail extends Activity {

	int idInsulin = 0;
	String originalName;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insulins_detail);
		// Show the Up button in the action bar.
		getActionBar();

		Bundle args = getIntent().getExtras();
		if(args!=null){
			DB_Read rdb = new DB_Read(this);
			String id = args.getString("Id");
			idInsulin = Integer.parseInt(id);
			InsulinDataBinding toFill = rdb.Insulin_GetById(Integer.parseInt(id));

			EditText name = (EditText)findViewById(R.id.et_Insulins_Nome);
			name.setText(toFill.getName());
			originalName = toFill.getName();
			EditText type = (EditText)findViewById(R.id.et_Insulins_Tipo);
			type.setText(toFill.getType());

			RadioGroup myRadioGroup = (RadioGroup)findViewById(R.id.et_insulin_action);
			int index = 0;
			try {
				index = Integer.parseInt(toFill.getAction());
			} catch (NumberFormatException nfe) {
				// index will get 0
				Log.e ("onCreate", "Read a text that was not a number from action"+ nfe);
			}
			((RadioButton)myRadioGroup.getChildAt(index)).setChecked(true);




//			EditText acao = (EditText)findViewById(R.id.et_Insulins_Acao);
//			acao.setText(toFill.getAction());
			//EditText value = (EditText)findViewById(R.id.et_TargetBG_Glycemia);
			//value.setText(String.valueOf(toFill.get));

			rdb.close();

		}
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();

		Bundle args = getIntent().getExtras();
		if(args!=null){
			inflater.inflate(R.menu.insulins_detail_edit, menu);
		}else{
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
				AddNewInsulin();
				//Intent data = new Intent();
				//data.putExtra("tabPosition", 2);
				//setResult(RESULT_OK, data);
				//NavUtils.navigateUpFromSameTask(this);

				return true;
			case R.id.menuItem_InsulinsDetail_EditSave:
				UpdateInsulin();
				return true;
			case R.id.menuItem_InsulinsDetail_Delete:
				DeleteInsulin();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void ShowDialogAddTarget(){
		final Context c = this;
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.title_activity_info))
				.setMessage(getString(R.string.how_to_add_insulins))
				.setPositiveButton(getString(R.string.okButton), new DialogInterface.OnClickListener() {
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

	public void AddNewInsulin(){
		EditText name = (EditText)findViewById(R.id.et_Insulins_Nome);
		EditText type = (EditText)findViewById(R.id.et_Insulins_Tipo);
//		EditText action = (EditText)findViewById(R.id.et_Insulins_Acao);
		RadioGroup myRadioGroup = (RadioGroup)findViewById(R.id.et_insulin_action);
		int index = myRadioGroup.indexOfChild(findViewById(myRadioGroup.getCheckedRadioButtonId()));


		//EditText value = (EditText)findViewById(R.id.et_TargetBG_Glycemia);

		//adicionado por zeornelas
		//para obrigar a colocar o valor dos hidratos e nao crashar
		if(name.getText().toString().equals("")){
			name.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if(type.getText().toString().equals("")){
			type.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(type, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if(index==-1){
			myRadioGroup.requestFocus();
			Toast.makeText(this, getString(R.string.insulin_message_action), Toast.LENGTH_SHORT).show();
			return;
		}

		DB_Read read = new DB_Read(this);
		boolean name_exists = read.Insulin_NameExists(name.getText().toString());
		if(name_exists){
			name.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
			Toast.makeText(this, getString(R.string.insulin_message_name_exists), Toast.LENGTH_SHORT).show();
			return;
		}

		read.close();




		DB_Write wdb = new DB_Write(this);

		InsulinDataBinding insulin = new InsulinDataBinding();


		insulin.setName(name.getText().toString());

		insulin.setType(type.getText().toString());
		insulin.setAction(index + "");


		wdb.Insulin_Add(insulin);
		wdb.close();

		read = new DB_Read(this);
		if(!read.Target_HasTargets()){
			read.close();
			ShowDialogAddTarget();
		}else{
			finish();
		}
	}


	public void UpdateInsulin(){
		EditText name = (EditText)findViewById(R.id.et_Insulins_Nome);
		EditText type = (EditText)findViewById(R.id.et_Insulins_Tipo);

		RadioGroup myRadioGroup = (RadioGroup)findViewById(R.id.et_insulin_action);
		int index = myRadioGroup.indexOfChild(findViewById(myRadioGroup.getCheckedRadioButtonId()));

//		EditText action = (EditText)findViewById(R.id.et_Insulins_Acao);
		//EditText value = (EditText)findViewById(R.id.et_TargetBG_Glycemia);

		//adicionado por zeornelas
		//para obrigar a colocar o valor dos hidratos e nao crashar
		if(name.getText().toString().equals("")){
			name.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if(type.getText().toString().equals("")){
			type.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(type, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if(index==-1){
			myRadioGroup.requestFocus();
			Toast.makeText(this,getString(R.string.insulin_message_action),Toast.LENGTH_SHORT).show();

			return;
		}


		DB_Read read = new DB_Read(this);
		boolean name_exists = read.Insulin_NameExists(name.getText().toString());
		if(name_exists && !originalName.equals(name.getText().toString())){
			name.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
			Toast.makeText(this, getString(R.string.insulin_message_name_exists), Toast.LENGTH_SHORT).show();
			return;
		}

		read.close();

		DB_Write wdb = new DB_Write(this);

		InsulinDataBinding insulin = new InsulinDataBinding();

		insulin.setId(idInsulin);
		insulin.setName(name.getText().toString());

		insulin.setType(type.getText().toString());
		insulin.setAction(index + "");


		wdb.Insulin_Update(insulin);
		wdb.close();

		read = new DB_Read(this);
		if(!read.Target_HasTargets()){
			read.close();
			ShowDialogAddTarget();
		}else{
			finish();
		}
	}



	public void DeleteInsulin(){
		final Context c = this;
		new AlertDialog.Builder(this)
				.setTitle(R.string.delete_insulin)
				.setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB
						//Rever porque não elimina o registo de glicemia
						DB_Write wdb = new DB_Write(c);
						try {
							wdb.Insulin_Remove(idInsulin);
							goUp();
						}catch (Exception e) {
							Toast.makeText(c, getString(R.string.delete_insulin_exception), Toast.LENGTH_LONG).show();
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

	public void goUp(){
		//NavUtils.navigateUpFromSameTask(this);
		finish();
	}
}
