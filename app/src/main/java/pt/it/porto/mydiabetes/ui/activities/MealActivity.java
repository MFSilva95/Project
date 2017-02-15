package pt.it.porto.mydiabetes.ui.activities;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.CarbsRec;
import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.data.Insulin;
import pt.it.porto.mydiabetes.data.InsulinRec;
import pt.it.porto.mydiabetes.data.Note;
import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;

public class MealActivity extends BaseMealActivity {


    @Override
    public String getRegType(){return "Meal";}


	public static final String BUNDLE_EXTRAS_GLYCEMIA_ID = "Bundle_extras_glycemia_id";

	private GlycemiaRec glycemiaData = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Bundle args = getIntent().getExtras();
		if (args != null) {
			if (args.containsKey(BUNDLE_EXTRAS_GLYCEMIA_ID)) {
				int glycemiaId = args.getInt(BUNDLE_EXTRAS_GLYCEMIA_ID);
				DB_Read db_read = new DB_Read(this);
				glycemiaData = db_read.Glycemia_GetById(glycemiaId);
				insulinCalculator = new InsulinCalculator(this);
			}
		}

		super.onCreate(savedInstanceState);
		if (glycemiaData != null) {
			fillDateHour(glycemiaData.getFormattedDate(), glycemiaData.getFormattedTime());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.meal, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean canSave() {
		if (insulinCalculator.getCarbs() != 0 || insulinCalculator.getGlycemia() != 0) {
			return true;
		} else {
			return super.canSave();
		}
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
	protected void dateChanged(TextView view, String text) {

	}

	@Override
	protected void timeChanged(TextView view, String text) {
		DB_Read rdb = new DB_Read(this);
		double d = rdb.Target_GetTargetByTime(text);
		rdb.close();
		setGlycemiaTarget((int) d);
		insulinCalculator.setGlycemiaTarget((int) d);

		// load correct insulin for Insulin On Board
		Calendar time = DateUtils.getTimeCalendar(text);
		if (time != null) {
			insulinCalculator.setTime(this, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), null);
		}
	}

	@Override
	void insulinsNotFound() {

	}

	@Override
	InsulinCalculator getInsulinCalculator() {
		DB_Read rdb = new DB_Read(this);
		UserInfo obj = rdb.MyData_Read();
		int iRatio = obj.getInsulinRatio();
		int cRatio = obj.getCarbsRatio();
		InsulinCalculator insulinCalculator = new InsulinCalculator(iRatio, cRatio);

		Calendar calendar = Calendar.getInstance();
		insulinCalculator.setTime(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), null);
		if (glycemiaData != null) {
			insulinCalculator.setGlycemia(glycemiaData.getValue());
			insulinCalculator.setTime(this, glycemiaData.getDateTime().get(Calendar.HOUR_OF_DAY), glycemiaData.getDateTime().get(Calendar.MINUTE), DateUtils.getFormattedDate(glycemiaData.getDateTime()));
		}
		double d = rdb.Target_GetTargetByTime(DateUtils.getFormattedTime(calendar));
		if (d != 0) {
			insulinCalculator.setGlycemiaTarget((int) d);
		}
		rdb.close();
		return insulinCalculator;
	}


	public void AddCarbsRead() {
		Spinner tagSpinner = (Spinner) findViewById(R.id.tag);
		Uri imgUri = getImgUri();

		if ((insulinCalculator.getCarbsRatio() == 0) && imgUri == null) {
			// nothing to save
			return;
		}


		//Get id of user
		DB_Read rdb = new DB_Read(this);
		int idUser = rdb.getId();

		//Get id of selected tag
		String tag = null;
		if (tagSpinner != null) {
			tag = tagSpinner.getSelectedItem().toString();
		}
		Log.d("selected Spinner", tag);
		int idTag = rdb.Tag_GetIdByName(tag);
		rdb.close();
		DB_Write reg = new DB_Write(this);
		CarbsRec carb = new CarbsRec();

		String note = getNote();
		if (note != null && !note.isEmpty()) {
			Note n = new Note();
			n.setNote(note);
			carb.setIdNote(reg.Note_Add(n));
		}


		carb.setIdUser(idUser);
		carb.setCarbsValue(insulinCalculator.getCarbs());
		carb.setIdTag(idTag);
		carb.setPhotoPath(imgUri != null ? imgUri.getPath() : null); // /data/MyDiabetes/yyyy-MM-dd HH.mm.ss.jpg
		carb.setDateTime(getDateTime());


		reg.Carbs_Save(carb);
		reg.close();
	}

	public void AddInsulinRead() {
		Spinner insulinSpinner = (Spinner) findViewById(R.id.sp_MealDetail_Insulin);
		EditText glycemia = (EditText) findViewById(R.id.glycemia);
		EditText target = (EditText) findViewById(R.id.glycemia_target);

		//Get id of user
		DB_Read rdb = new DB_Read(this);
		int idUser = rdb.getId();

		//Get id of selected phase of day
		int idTag = rdb.Tag_GetIdByName(getPhaseOfDay());

		//Get id of selected insulin
		String insulin = null;
		if (insulinSpinner != null) {
			insulin = insulinSpinner.getSelectedItem().toString();
		}
		Log.d("selected Spinner", insulin);
		Insulin insulin_ = rdb.Insulin_GetByName(insulin);
		int idInsulin = 0;
		if (insulin_ != null) {
			idInsulin = insulin_.getId();
		}


		int idGlycemia = 0;
		boolean hasGlycemia = false;
		DB_Write reg = new DB_Write(this);

		InsulinRec ins = new InsulinRec();

		int idnote = 0;
		String note = getNote();
		if (note!=null && !note.isEmpty()) {
			Note n = new Note();
			n.setNote(note);
			idnote = reg.Note_Add(n);
			ins.setIdNote(idnote);
		}

		GlycemiaRec gly = glycemiaData;
		if (gly == null) {
			gly = new GlycemiaRec();
		}
		if (glycemia!=null && !glycemia.getText().toString().equals("")) {
			gly.setIdUser(idUser);
			gly.setValue(Integer.parseInt(glycemia.getText().toString()));
			gly.setDateTime(getDateTime());
			gly.setIdTag(idTag);
			if (idnote > 0) {
				gly.setIdNote(idnote);
			}

			if (glycemiaData != null) {
				reg.Glycemia_Update(gly);
			} else {
				idGlycemia = reg.Glycemia_Save(gly);
			}
			Log.d("id glycemia", String.valueOf(idGlycemia));
			hasGlycemia = true;
		}

		ins.setIdUser(idUser);
		ins.setIdInsulin(idInsulin);
		ins.setIdBloodGlucose(hasGlycemia ? idGlycemia : -1);
		ins.setTargetGlycemia(target != null ? Integer.parseInt(target.getText().toString()) : 0);
		ins.setDateTime(getDateTime());
		ins.setInsulinUnits(getInsulinIntake());
		ins.setIdTag(idTag);


		reg.Insulin_Save(ins);

		reg.close();
		rdb.close();
	}
}
