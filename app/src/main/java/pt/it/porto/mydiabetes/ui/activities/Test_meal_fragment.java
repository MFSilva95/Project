package pt.it.porto.mydiabetes.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.fragments.MealFragment;
import pt.it.porto.mydiabetes.ui.listAdapters.CarbsDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.GlycemiaDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.InsulinRegDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.NoteDataBinding;
import pt.it.porto.mydiabetes.utils.DateUtils;
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
		String hour = DateUtils.getFormattedTime(c);
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
		InsulinCalculator insulinCalculator = new InsulinCalculator((int) iRatio, (int) cRatio);

		DB_Read read = new DB_Read(this);
		int[] lastInsulin = read.InsulinReg_GetLastHourAndQuantity();
		read.close();

		int minuteOriginal = lastInsulin[1] * 60 + lastInsulin[2];
		int insulinType = lastInsulin[0];
		int insulinDose = lastInsulin[3];

		insulinCalculator.setLastInsulin(insulinDose, minuteOriginal, insulinType);

		return insulinCalculator;
	}

	private int getTargetByHour(String time) {
		DB_Read rdb = new DB_Read(this);
		double d = rdb.Target_GetTargetByTime(time);
		rdb.close();
		return (int) d;
	}

	private String getTagByHour(String time) {
		DB_Read rdb = new DB_Read(this);
		String result = rdb.Tag_GetByTime(time).getName();
		rdb.close();
		return result;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.meal, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menuItem_MealDetail_Save:
				saveData();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private MealFragment getFragment() {
		return ((MealFragment) getFragmentManager().findFragmentById(R.id.meal_fragment));
	}

	@Override
	public void saveData() {
		if (!fragment.canSave()) {
			// todo add feedback
		} else {
			Uri imgUri = fragment.getImgUri();
			float insulinIntake = fragment.getInsulinIntake();
			InsulinCalculator insulinCalculator = fragment.getInsulinCalculator();
			String date = fragment.getDate();
			String time = fragment.getTime();
			String phaseOfDay = fragment.getPhaseOfDay();
			String note = fragment.getNote();

			DB_Read rdb = new DB_Read(this);
			Object[] obj = rdb.MyData_Read();
			int idUser = Integer.valueOf(obj[0].toString());
			//Get id of selected tag
			int idTag = rdb.Tag_GetIdByName(phaseOfDay);
			int insulinId = rdb.Insulin_GetByName(fragment.getInsulin()).getId();

			rdb.close();
			int noteId = -1;


			// save meal
			DB_Write reg = new DB_Write(this);
			CarbsDataBinding carb = new CarbsDataBinding();

			if (!note.isEmpty()) {
				NoteDataBinding n = new NoteDataBinding();
				n.setNote(note);
				noteId = reg.Note_Add(n);
				carb.setId_Note(noteId);
			}


			carb.setId_User(idUser);
			carb.setCarbsValue( insulinCalculator.getCarbs());
			carb.setId_Tag(idTag);
			if (imgUri != null) {
				carb.setPhotoPath(imgUri.getPath()); // /data/MyDiabetes/yyyy-MM-dd HH.mm.ss.jpg
			}
			carb.setDate(date);
			carb.setTime(time);
			reg.Carbs_Save(carb);

			//save insulin
			InsulinRegDataBinding ins = new InsulinRegDataBinding();

			int glycemiaId = -1;

			if (noteId != -1) {
				ins.setIdNote(noteId);
			}
			if (insulinCalculator.getGlycemia() > 0) {
				// save glycemia read
				GlycemiaDataBinding gly = new GlycemiaDataBinding();

				gly.setIdUser(idUser);
				gly.setValue( insulinCalculator.getGlycemia());
				gly.setDate(date);
				gly.setTime(time);
				gly.setIdTag(idTag);
				if (noteId > 0) {
					gly.setIdNote(noteId);
				}

				glycemiaId = reg.Glycemia_Save(gly);
				ins.setIdBloodGlucose(glycemiaId);
			}

			ins.setIdUser(idUser);
			ins.setIdInsulin(insulinId);
			ins.setDate(date);
			ins.setTime(time);
			ins.setTargetGlycemia( insulinCalculator.getInsulinTarget());
			ins.setInsulinUnits( insulinIntake);
			ins.setIdTag(idTag);

			reg.Insulin_Save(ins);
			reg.close();
			finish();
		}
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
