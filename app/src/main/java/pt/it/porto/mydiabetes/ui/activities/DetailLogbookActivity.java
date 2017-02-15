package pt.it.porto.mydiabetes.ui.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.CarbsRec;
import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.data.InsulinRec;
import pt.it.porto.mydiabetes.data.Note;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;

public class DetailLogbookActivity extends BaseMealActivity {

	@Override
	public String getRegType(){return "Meal";}

	public static final String ARG_CARBS = "ARG_CARBS";
	public static final String ARG_INSULIN = "ARG_INSULIN";
	public static final String ARG_BLOOD_GLUCOSE = "ARG_BLOOD_GLUCOSE";

	public static final String SAVE_SHOWING_ERROR = "SAVE_SHOWING_ERROR";
	public static final String SAVE_AUTO_UPDATE = "SAVE_AUTO_UPDATE";
	public static final int RESULT_SAVED_CHANGES = 3;

	final int MODE_REFRESH = 1;
	final int MODE_REVERT = 2;
	final int MODE_INFO = 3;
	boolean showingError = false;
	boolean autoUpdate = false;
	boolean undo = false;
	int mode = MODE_INFO;
	@Nullable
	private GlycemiaRec glycemiaData;
	@Nullable
	private CarbsRec carbsData;
	@Nullable
	private InsulinRec insulinData;
	private InsulinCalculator insulinCalculator;
	private int noteId;
	private String date = "";
	private String time = "";
	private String originalNoteContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Bundle args = getIntent().getExtras();
		if (args != null) {
			if (args.containsKey(ARG_CARBS)) {
				carbsData = args.getParcelable(ARG_CARBS);
			}
			if (args.containsKey(ARG_BLOOD_GLUCOSE)) {
				glycemiaData = args.getParcelable(ARG_BLOOD_GLUCOSE);
			}

			if (args.containsKey(ARG_INSULIN)) {
				insulinData = args.getParcelable(ARG_INSULIN);
			}

			noteId = -1;
			DB_Read db_read = new DB_Read(this);
			if (carbsData != null) {
				carbsData = db_read.CarboHydrate_GetById(carbsData.getId());
				if (carbsData != null) {
					noteId = carbsData.getIdNote();
					date = carbsData.getFormattedDate();
					time = carbsData.getFormattedTime();
				}
			}
			if (glycemiaData != null) {
				glycemiaData = db_read.Glycemia_GetById(glycemiaData.getId());
				if (glycemiaData != null) {
					noteId = glycemiaData.getIdNote();
					date = glycemiaData.getFormattedDate();
					time = glycemiaData.getFormattedTime();
				}
			}
			if (insulinData != null) {
				insulinData = db_read.InsulinReg_GetById(insulinData.getId());
				if (insulinData != null) {
					noteId = insulinData.getIdNote();
					date = insulinData.getFormattedDate();
					time = insulinData.getFormattedTime();
				}
			}
			db_read.close();

			insulinCalculator = new InsulinCalculator(this);

			insulinCalculator.setCarbs(carbsData != null ? carbsData.getCarbsValue() : 0);
			insulinCalculator.setGlycemia(glycemiaData != null ? glycemiaData.getValue() : 0);
			insulinCalculator.setGlycemiaTarget(insulinData != null ? insulinData.getTargetGlycemia() : 0);
			// get insulin before this one
			Calendar timeCalendar = DateUtils.getTimeCalendar(time);
			if (timeCalendar != null) {
				insulinCalculator.setTime(this, timeCalendar.get(Calendar.HOUR_OF_DAY), timeCalendar.get(Calendar.MINUTE), date);
			}
		}
		super.onCreate(savedInstanceState);

		// set date
		fillDateHour(date, time);
		insulinCalculator.setGlycemiaTarget(insulinData != null ? insulinData.getTargetGlycemia() : 0);
		setupInsulinCalculator();

		// set insulin
		DB_Read db_read = new DB_Read(this);
		if (insulinData != null) {
			String insulinName = db_read.Insulin_GetById(insulinData.getIdInsulin()).getName();
			setInsulin(insulinName, insulinData.getInsulinUnits());
		} else {
			setInsulin(null, 0);
		}

		// set note
		if (noteId != -1) {
			originalNoteContent = db_read.Note_GetById(noteId).getNote();
			setNote(originalNoteContent);
		}
		db_read.close();

		// set meal image
		if (carbsData != null && carbsData.hasPhotoPath()) {
			setImageUri(Uri.parse(carbsData.getPhotoPath()));
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			ActionBar actionBar = getActionBar();
			if (actionBar != null) {
				actionBar.setLogo(R.drawable.ic_close_grey_100_48dp);
				actionBar.setHomeAsUpIndicator(R.drawable.actionbar_empty_space);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(SAVE_AUTO_UPDATE, autoUpdate);
		outState.putBoolean(SAVE_SHOWING_ERROR, showingError);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.containsKey(SAVE_AUTO_UPDATE)) {
			autoUpdate = savedInstanceState.getBoolean(SAVE_AUTO_UPDATE);
		}
		if (savedInstanceState != null && savedInstanceState.containsKey(SAVE_SHOWING_ERROR)) {
			showingError = savedInstanceState.getBoolean(SAVE_SHOWING_ERROR);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.logbook_detail_edit, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menuItem_LogbookDetail_Delete) {
			final Context c = this;
			new AlertDialog.Builder(this).setTitle(getString(R.string.deleteReading))
					.setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							DB_Write wdb = new DB_Write(c);
							try {
								wdb.Logbook_Delete(carbsData != null ? carbsData.getId() : -1, insulinData != null ? insulinData.getId() : -1, glycemiaData != null ? glycemiaData.getId() : -1, noteId);
								setResult(DetailLogbookActivity.RESULT_SAVED_CHANGES);
								finish();
							} catch (Exception e) {
								Toast.makeText(c, getString(R.string.deleteException), Toast.LENGTH_LONG).show();
							}
							wdb.close();
						}
					})
					.setNegativeButton(getString(R.string.negativeButton), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// Do nothing.
						}
					})
					.show();
			return true;
		} else if (item.getItemId() == R.id.menuItem_LogbookDetail_EditSave) {
			saveData();
			return true;
		} else if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (needsToSave()) {
			final Context c = this;
			new AlertDialog.Builder(this).setTitle("Dados foram alterados, deseja sair sem gravar as alterações?")
					.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							dialog.dismiss();
							finish();
						}
					})
					.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							saveData();
						}
					})
					.show();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void glycemiaTargetChanged(EditText view, String text) {
		int newVal = !text.isEmpty() ? (int) Float.parseFloat(text) : 0;
		boolean changed;
		if (insulinData != null) {
			changed = insulinData.getTargetGlycemia() != newVal;
		} else {
			changed = newVal != 0;
		}
		updateIndicator(view, changed);
		setInconsistentInsulin(changed);
	}

	@Override
	protected void glycemiaChanged(EditText view, String text) {
		int newVal = !text.isEmpty() ? Integer.parseInt(text) : 0;
		boolean changed;
		if (glycemiaData != null) {
			changed = glycemiaData.getValue() != newVal;
		} else {
			changed = newVal != 0;
		}
		updateIndicator(view, changed);
		setInconsistentInsulin(changed);
	}

	@Override
	protected void carbsChanged(EditText view, String text) {
		int newVal = !text.isEmpty() ? Integer.parseInt(text) : 0;
		boolean changed;
		if (carbsData != null) {
			changed = carbsData.getCarbsValue() != newVal;
		} else {
			changed = newVal != 0;
		}
		updateIndicator(view, changed);
		setInconsistentInsulin(changed);
	}

	@Override
	protected void insulinIntakeChanged(EditText view, String text) {
		float newVal = !text.isEmpty() ? Float.parseFloat(text) : 0;
		boolean changed;
		if (insulinData != null) {
			changed = Float.compare(insulinData.getInsulinUnits(), newVal) != 0;
		} else {
			changed = newVal != 0;
		}
		//		updateIndicator(view, changed);
		//		setInconsistentInsulin(changed);
		if (mode == MODE_INFO && changed) {
			setModeRevert();
		} else if (mode == MODE_REVERT && !changed) {
			setModeInfo();
		}
	}

	@Override
	protected void dateChanged(TextView view, String text) {
		boolean changed = !date.equals(text);
		updateIndicator(view, changed);
	}

	@Override
	protected void timeChanged(TextView view, String text) {
		updateIndicator(view, !time.equals(text));
		DB_Read rdb = new DB_Read(this);
		int d = (int) rdb.Target_GetTargetByTime(text);
		rdb.close();
		setGlycemiaTarget(d);
		// set time and load correct insulin for Insulin On Board
		Calendar time = DateUtils.getTimeCalendar(text);
		if (time != null) {
			insulinCalculator.setTime(this, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), null);
		}
	}

	private void updateIndicator(TextView view, boolean valueChanged) {
		if (valueChanged) {
			view.setBackgroundResource(R.drawable.edit_text_holo_dark_changed);
		} else {
			view.setBackgroundResource(R.drawable.default_edit_text_holo_dark);
		}
	}

	private void setInconsistentInsulin(boolean changed) {
		boolean inconsistent = Float.compare(getInsulinCalculator().getInsulinTotal(useIOB, true), insulinData != null ? insulinData.getInsulinUnits() : 0) != 0;

		if (inconsistent && !showingError) {
			showingError = inconsistent;
			setModeRefresh();
		} else if (!inconsistent && showingError) {
			showingError = false;
			setModeInfo();
		} else if (autoUpdate) {
			setModeRevert();
			setInsulinIntake();
		}
	}

	void setModeRefresh() {
		findViewById(R.id.insulin_intake).setBackgroundResource(R.drawable.edit_text_holo_dark_error);
		setToggleIconImage(R.drawable.ic_cached_grey_400_24dp);
		undo = true;
		mode = MODE_REFRESH;
	}

	void setModeRevert() {
		findViewById(R.id.insulin_intake).setBackgroundResource(R.drawable.edit_text_holo_dark_changed);
		setToggleIconImage(R.drawable.ic_redo_flip_grey_400_24dp);
		mode = MODE_REVERT;
		autoUpdate = true;
	}

	void setModeInfo() {
		undo = false;
		autoUpdate = false;
		findViewById(R.id.insulin_intake).setBackgroundResource(R.drawable.default_edit_text_holo_dark);
		setToggleIconImage(android.R.drawable.ic_menu_info_details);
		if (fragmentInsulinCalcsFragment != null) {
			((ToggleButton) findViewById(R.id.bt_insulin_calc_info)).setChecked(true);
		}
		mode = MODE_INFO;
	}

	void setToggleIconImage(int resource) {
		ImageButton button = (ImageButton) findViewById(R.id.bt_insulin_calc_info);
		button.setImageDrawable(ContextCompat.getDrawable(this, resource));
	}

	@Override
	public void toggleInsulinCalcDetails(View view) {
		switch (mode) {
			case MODE_INFO:
				super.toggleInsulinCalcDetails(view);
				break;
			case MODE_REFRESH:
				setModeRevert();
				showCalcs();
				setInsulinIntake();
				break;
			case MODE_REVERT:
				setModeRefresh();
				showCalcs();
				setInsulin(null, insulinData != null ? insulinData.getInsulinUnits() : 0);
				break;
		}
	}

	@Override
	protected boolean shouldSetInsulin() {
		return autoUpdate;
	}

	@Override
	void insulinsNotFound() {

	}

	@Override
	InsulinCalculator getInsulinCalculator() {
		return insulinCalculator;
	}

	public boolean needsToSave() {
		String date = getDate();
		String time = getTime();
		String note = getNote();

		// note content was changed
		if (originalNoteContent != null && !originalNoteContent.equals(note)) {
			return true;
		}

		if (carbsData != null) {
			if (!carbsData.getFormattedDate().equals(date) || !carbsData.getFormattedTime().equals(time)) {
				return true;
			}

			if (carbsData.getPhotoPath() != null && !carbsData.getPhotoPath().equals(getImgUri() != null ? getImgUri().getPath() : "")) {
				return true;
			}
			// if photo was added
			if (carbsData.getPhotoPath() == null && getImgUri() != null) {
				return true;
			}
			if (carbsData.getCarbsValue() != insulinCalculator.getCarbs()) {
				return true;
			}
			if (carbsData.getIdNote() != -1 && note.isEmpty()) {
				// note removed
				return true;
			} else if (carbsData.getIdNote() == -1 && !note.isEmpty()) {
				return true;
			}
		}
		DB_Read rdb = new DB_Read(this);
		int insulinId = rdb.Insulin_GetByName(getInsulin()).getId();
		rdb.close();
		if (insulinData != null) {
			if (!insulinData.getFormattedDate().equals(date) || !insulinData.getFormattedTime().equals(time)) {
				return true;
			}
			if (Float.compare(insulinData.getInsulinUnits(), getInsulinIntake()) != 0) {
				return true;
			}
			if (insulinData.getIdInsulin() != insulinId) {
				return true;
			}
			if (insulinData.getTargetGlycemia() != insulinCalculator.getInsulinTarget()) {
				return true;
			}
			if (insulinData.getIdNote() != -1 && note.isEmpty()) {
				// note removed
				return true;
			} else if (insulinData.getIdNote() == -1 && !note.isEmpty()) {
				return true;
			}

		}
		if (glycemiaData != null) {
			if (!glycemiaData.getFormattedDate().equals(date) || !glycemiaData.getFormattedTime().equals(time)) {
				return true;
			}
			if (glycemiaData.getValue() != insulinCalculator.getGlycemia()) {
				return true;
			}
			if (glycemiaData.getIdNote() != -1 && note.isEmpty()) {
				// note removed
				return true;
			} else if (glycemiaData.getIdNote() == -1 && !note.isEmpty()) {
				return true;
			}
		}

		return false;
	}

	public void saveData() {
		DB_Write reg = new DB_Write(this);

		int user_id = -1;
		Calendar originalDateTime = null;
		if (carbsData != null) {
			user_id = carbsData.getIdUser();
			originalDateTime = carbsData.getDateTime();
		} else if (glycemiaData != null) {
			user_id = glycemiaData.getIdUser();
			originalDateTime = glycemiaData.getDateTime();
		} else if (insulinData != null) {
			user_id = insulinData.getIdUser();
			originalDateTime = insulinData.getDateTime();
		} else {
			// go to database and get it!
			// it shouldn't be necessary, if we are where some of this values are defined
			Log.e("DetailLogbookActivity", "carbsData, glycemiaData and insulinData are all null");
		}


		String note = getNote();
		Uri imgUri = getImgUri();

		// IDs
		int carbsRegId = 0;
		int glycemiaRegId = glycemiaData != null ? glycemiaData.getId() : -1;
		int insulinRegId = 0;


		//nota id nao existe e tem de ser criado
		if (noteId == -1 && !note.isEmpty()) {
			Note n = new Note();
			n.setNote(note);
			noteId = reg.Note_Add(n);
		} else if (noteId != -1) {        //nota id existe e o texto foi apagado/alterado
			Note n = new Note();
			n.setNote(note);
			n.setId(noteId);
			reg.Note_Update(n);
		}

		String tagSelected = getPhaseOfDay();
		DB_Read rdb = new DB_Read(this);
		int tagId = rdb.Tag_GetIdByName(tagSelected);
		rdb.close();

		Calendar newDateTime=getDateTime();
		if (originalDateTime != null) {
			newDateTime.set(Calendar.SECOND, originalDateTime.get(Calendar.SECOND));
		}
		// save carbs
		CarbsRec newCarbs = new CarbsRec(carbsData);
		newCarbs.setIdUser(user_id);
		newCarbs.setCarbsValue(insulinCalculator.getCarbs());
		newCarbs.setIdTag(tagId);
		newCarbs.setPhotoPath(imgUri != null ? imgUri.getPath() : null); // /data/MyDiabetes/yyyy-MM-dd HH.mm.ss.jpg
		newCarbs.setDateTime(newDateTime);
		newCarbs.setIdNote(noteId);

		if (carbsData == null && !newCarbs.equals(carbsData)) {
			// needs to save it
			reg.Carbs_Save(newCarbs);
		} else if (carbsData != null && !newCarbs.equals(carbsData)) {
			reg.Carbs_Update(newCarbs);
		}
		carbsData = newCarbs;

		// save glicemia
		GlycemiaRec newGlicemia = new GlycemiaRec(glycemiaData);
		newGlicemia.setIdUser(user_id);
		newGlicemia.setValue(insulinCalculator.getGlycemia());
		newGlicemia.setDateTime(newDateTime);
		newGlicemia.setIdTag(tagId);
		newGlicemia.setIdNote(noteId);

		if (glycemiaData == null && !newGlicemia.equals(glycemiaData)) {
			glycemiaRegId = reg.Glycemia_Save(newGlicemia);
		} else if (glycemiaData != null && !newGlicemia.equals(glycemiaData)) {
			reg.Glycemia_Update(newGlicemia);
		}
		glycemiaData = newGlicemia;

		// save insulin
		rdb = new DB_Read(this);
		int insulinId = rdb.Insulin_GetByName(getInsulin()).getId();
		rdb.close();
		float insulinIntake = getInsulinIntake();

		InsulinRec newInsulin = new InsulinRec(insulinData);
		newInsulin.setIdUser(user_id);
		newInsulin.setIdInsulin(insulinId);
		newInsulin.setIdBloodGlucose(glycemiaRegId != -1 ? glycemiaRegId : -1);
		newInsulin.setDateTime(newDateTime);
		newInsulin.setTargetGlycemia(insulinCalculator.getInsulinTarget());
		newInsulin.setInsulinUnits(insulinIntake);
		newInsulin.setIdTag(tagId);
		newInsulin.setIdNote(noteId);

		if (insulinData == null && !newInsulin.equals(insulinData)) {
			insulinRegId = reg.Insulin_Save(newInsulin);
			newInsulin.setId(insulinRegId);
		} else if (insulinData != null && !newInsulin.equals(insulinData)) {
			reg.Insulin_Update(newInsulin);
		}
		insulinData = newInsulin;

		setResult(DetailLogbookActivity.RESULT_SAVED_CHANGES);
		finish();
	}


	/**
	 * We catch this event and immediately save the change
	 */
	@Override
	void imageRemoved() {
		super.imageRemoved();
		if (carbsData != null) {
			carbsData.setPhotoPath(null);
			DB_Write db_write = new DB_Write(this);
			db_write.Carbs_Update(carbsData);
		}
	}
}
