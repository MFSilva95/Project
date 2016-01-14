package pt.it.porto.mydiabetes.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.fragments.MealFragment;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;

public class Test_meal_fragment extends Activity implements MealFragment.MealFragmentListener{

	private InsulinCalculator insulinCalculator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_meal_fragment);
		insulinCalculator=getLastInsulin();
		getFragment().setInsulinCalculator(insulinCalculator);
	}


	protected InsulinCalculator getLastInsulin() {
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		rdb.close();
		double iRatio = Double.valueOf(obj[3].toString());
		double cRatio = Double.valueOf(obj[4].toString());
		InsulinCalculator insulinCalculator = new InsulinCalculator((float) iRatio, (float) cRatio);

		DB_Read read = new DB_Read(this);
		int[] lastInsulin = read.InsulinReg_GetLastHourAndQuantity();
		read.close();

		int minuteOriginal = lastInsulin[1] * 60 + lastInsulin[2];
		int insulinType = lastInsulin[0];
		int insulinDose = lastInsulin[3];

		insulinCalculator.setLastInsulin(insulinDose, minuteOriginal, insulinType);

		return insulinCalculator;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_CANCELED && requestCode == MealFragment.IMAGE_CAPTURE) {
			getFragment().setImageUri(data.getData());
		} else if (requestCode == MealFragment.IMAGE_VIEW) {
			Log.d("Result:", resultCode + "");
			//se tivermos apagado a foto dá result code -1
			//se voltarmos por um return por exemplo o resultcode é 0
			if (resultCode == -1) {
				getFragment().setImageUri(null);
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}

	}

	public void ShowDialogAddInsulin() {
		final Context c = this;
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.title_activity_info))
				.setMessage(getString(R.string.meal_alert_insulin))
				.setPositiveButton(getString(R.string.okButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB
						//Rever porque não elimina o registo de glicemia
						Intent intent = new Intent(c, Preferences.class);
						intent.putExtra("tabPosition", 4);
						startActivity(intent);
					}
				}).show();
	}

	public void ShowDialogAddTarget() {
		final Context c = this;
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.title_activity_info))
				.setMessage(getString(R.string.meal_alert_target))
				.setPositiveButton(getString(R.string.okButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB
						//Rever porque não elimina o registo de glicemia
						Intent intent = new Intent(c, Preferences.class);
						intent.putExtra("tabPosition", 1);
						startActivity(intent);
					}
				}).show();
	}


	private MealFragment getFragment(){
		return ((MealFragment) getFragmentManager().findFragmentById(R.id.meal_fragment));
	}

	@Override
	public void saveData() {

	}

	@Override
	public void exit() {

	}
}
