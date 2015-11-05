package com.jadg.mydiabetes;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.database.InsulinDataBinding;

import android.annotation.SuppressLint;






public class InsulinsDetail extends Activity {

	int idInsulin = 0;


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
			EditText type = (EditText)findViewById(R.id.et_Insulins_Tipo);
			type.setText(toFill.getType());
			EditText acao = (EditText)findViewById(R.id.et_Insulins_Acao);
			acao.setText(toFill.getAction());
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
				.setTitle("Informação")
				.setMessage("Para adicionar mais insulinas deve carregar no ícone superior direito do menu inicial, de seguida entrar em Configurações e depois seleccionar a aba Insulinas. Para finalizar deve adicionar os seus objetivos da glicemia!")
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

	public void AddNewInsulin(){
		EditText name = (EditText)findViewById(R.id.et_Insulins_Nome);
		EditText type = (EditText)findViewById(R.id.et_Insulins_Tipo);
		EditText action = (EditText)findViewById(R.id.et_Insulins_Acao);
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
		if(action.getText().toString().equals("")){
			action.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(action, InputMethodManager.SHOW_IMPLICIT);
			return;
		}

		DB_Write wdb = new DB_Write(this);

		InsulinDataBinding insulin = new InsulinDataBinding();


		insulin.setName(name.getText().toString());

		insulin.setType(type.getText().toString());
		insulin.setAction(action.getText().toString());


		wdb.Insulin_Add(insulin);
		wdb.close();

		DB_Read read = new DB_Read(this);
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
		EditText action = (EditText)findViewById(R.id.et_Insulins_Acao);
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
		if(action.getText().toString().equals("")){
			action.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(action, InputMethodManager.SHOW_IMPLICIT);
			return;
		}

		DB_Write wdb = new DB_Write(this);

		InsulinDataBinding insulin = new InsulinDataBinding();

		insulin.setId(idInsulin);
		insulin.setName(name.getText().toString());

		insulin.setType(type.getText().toString());
		insulin.setAction(action.getText().toString());


		wdb.Insulin_Update(insulin);
		wdb.close();

		DB_Read read = new DB_Read(this);
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
				.setTitle("Eliminar Insulina?")
				.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB
						//Rever porque não elimina o registo de glicemia
						DB_Write wdb = new DB_Write(c);
						try {
							wdb.Insulin_Remove(idInsulin);
							goUp();
						}catch (Exception e) {
							Toast.makeText(c, "Não pode eliminar esta insulina, associado a outros registos!", Toast.LENGTH_LONG).show();
						}
						wdb.close();

					}
				})
				.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
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
