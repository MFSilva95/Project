package pt.it.porto.mydiabetes.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.ui.fragments.MealFragment;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;

public class Test_meal_fragment extends Activity implements MealFragment.MealFragmentListener {

	private InsulinCalculator insulinCalculator;
	private MealFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_meal_fragment);
		insulinCalculator = getLastInsulin();
		fragment = getFragment();

		Calendar c = Calendar.getInstance();
		String hour=TimePickerFragment.getFormatedDate(c);
		insulinCalculator.setGlycemiaTarget(getTargetByHour(hour));
		fragment.setInsulinCalculator(insulinCalculator);
		fragment.setShowUpdateIndicator(true);
		fragment.setPhaseOfDayByValue(getTagByHour(hour));

		setListeners();
	}

	private void setListeners() {
		fragment.setTimeChangeListener(new MealFragment.OnTextChange() {
			@Override
			public void onTextChange(View view, String text) {
				fragment.setGlycemiaTarget(getTargetByHour(text));
				fragment.setPhaseOfDayByValue(getTagByHour(text));
			}
		});
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

	private float getTargetByHour(String time) {
		DB_Read rdb = new DB_Read(this);
		double d = rdb.Target_GetTargetByTime(time);
		rdb.close();
		return (float) d;
	}

	private String getTagByHour(String time) {
		DB_Read rdb = new DB_Read(this);
		String result = rdb.Tag_GetByTime(time).getName();
		rdb.close();
		return result;
	}


	private MealFragment getFragment() {
		return ((MealFragment) getFragmentManager().findFragmentById(R.id.meal_fragment));
	}

	@Override
	public void saveData() {


	}

	@Override
	public void exit() {

	}

	@Override
	public void insulinsNotFound() {
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
}
