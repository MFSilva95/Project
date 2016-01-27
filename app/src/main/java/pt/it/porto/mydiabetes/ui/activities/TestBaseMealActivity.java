package pt.it.porto.mydiabetes.ui.activities;

import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.ui.listAdapters.CarbsDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.GlycemiaDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.InsulinRegDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.NoteDataBinding;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;

public class TestBaseMealActivity extends BaseMealActivity {


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.meal, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menuItem_MealDetail_Save) {
			if (canSave()) {
				AddCarbsRead();
				AddInsulinRead();
				finish();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	protected void glycemiaTargetChanged(EditText view, String text) {

	}

	@Override
	protected void glycemiaChanged(EditText view, String text) {

	}

	@Override
	protected void carbsChanged(EditText view, String text) {

	}

	@Override
	protected void insulinIntakeChanged(EditText view, String text) {

	}

	@Override
	protected void dateChanged(EditText view, String text) {

	}

	@Override
	protected void timeChanged(EditText view, String text) {
		DB_Read rdb = new DB_Read(this);
		double d = rdb.Target_GetTargetByTime(text);
		rdb.close();
		setGlycemiaTarget((int) d);

		// load correct insulin for Insulin On Board
		Calendar time = TimePickerFragment.getCalendar(text);
		if(time!=null) {
			insulinCalculator.setTime(this, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE));
		}
	}

	@Override
	void insulinsNotFound() {

	}

	@Override
	InsulinCalculator getInsulinCalculator() {
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		rdb.close();
		double iRatio = Double.valueOf(obj[3].toString());
		double cRatio = Double.valueOf(obj[4].toString());
		InsulinCalculator insulinCalculator = new InsulinCalculator((int) iRatio, (int) cRatio);

		Calendar calendar=Calendar.getInstance();
		insulinCalculator.setTime(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));


		return insulinCalculator;
	}


	public void AddCarbsRead() {
		Spinner tagSpinner = (Spinner) findViewById(R.id.sp_MealDetail_Tag);
		Uri imgUri = getImgUri();

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

		String note = getNote();
		if (!note.isEmpty()) {
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note);
			carb.setId_Note(reg.Note_Add(n));
		}


		carb.setId_User(idUser);
		carb.setCarbsValue(insulinCalculator.getCarbs());
		carb.setId_Tag(idTag);
		carb.setPhotoPath(imgUri != null ? imgUri.getPath() : null); // /data/MyDiabetes/yyyy-MM-dd HH.mm.ss.jpg
		carb.setDate(getDate());
		carb.setTime(getTime());


		reg.Carbs_Save(carb);
		reg.close();
	}

	public void AddInsulinRead() {
		Spinner insulinSpinner = (Spinner) findViewById(R.id.sp_MealDetail_Insulin);
		EditText glycemia = (EditText) findViewById(R.id.et_MealDetail_Glycemia);
		EditText target = (EditText) findViewById(R.id.et_MealDetail_TargetGlycemia);

		//Get id of user
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());

		//Get id of selected phase of day
		int idTag = rdb.Tag_GetIdByName(getPhaseOfDay());

		//Get id of selected insulin
		String insulin = insulinSpinner.getSelectedItem().toString();
		Log.d("selected Spinner", insulin);
		int idInsulin = rdb.Insulin_GetByName(insulin).getId();


		int idGlycemia = 0;
		boolean hasGlycemia = false;
		DB_Write reg = new DB_Write(this);

		InsulinRegDataBinding ins = new InsulinRegDataBinding();

		int idnote = 0;
		String note = getNote();
		if (!note.isEmpty()) {
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note);
			idnote = reg.Note_Add(n);
			ins.setIdNote(idnote);
		}

		if (!glycemia.getText().toString().equals("")) {
			GlycemiaDataBinding gly = new GlycemiaDataBinding();

			gly.setIdUser(idUser);
			gly.setValue(Integer.parseInt(glycemia.getText().toString()));
			gly.setDate(getDate());
			gly.setTime(getTime());
			gly.setIdTag(idTag);
			if (idnote > 0) {
				gly.setIdNote(idnote);
			}

			idGlycemia = reg.Glycemia_Save(gly);
			Log.d("id glycemia", String.valueOf(idGlycemia));
			hasGlycemia = true;
		}

		ins.setIdUser(idUser);
		ins.setIdInsulin(idInsulin);
		ins.setIdBloodGlucose(hasGlycemia ? idGlycemia : -1);
		ins.setDate(getDate());
		ins.setTime(getTime());
		ins.setTargetGlycemia(Integer.parseInt(target.getText().toString()));
		ins.setInsulinUnits(getInsulinIntake());
		ins.setIdTag(idTag);


		reg.Insulin_Save(ins);

		reg.close();
		rdb.close();
	}
}
