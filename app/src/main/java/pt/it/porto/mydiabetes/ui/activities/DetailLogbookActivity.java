package pt.it.porto.mydiabetes.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.listAdapters.CarbsDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.GlycemiaDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.InsulinRegDataBinding;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;

public class DetailLogbookActivity extends BaseMealActivity {
	public static final String ARG_CARBS = "ARG_CARBS";
	public static final String ARG_INSULIN = "ARG_INSULIN";
	public static final String ARG_BLOOD_GLUCOSE = "ARG_BLOOD_GLUCOSE";

	private GlycemiaDataBinding glycemiaData;
	private CarbsDataBinding carbsData;
	private InsulinRegDataBinding insulinData;

	private InsulinCalculator insulinCalculator;
	private int noteId;
	private String date = "";
	private String time = "";

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
			if (carbsData != null) {
				noteId = carbsData.getId_Note();
				date = carbsData.getDate();
				time = carbsData.getTime();
			} else if (glycemiaData != null) {
				noteId = glycemiaData.getIdNote();
				date = glycemiaData.getDate();
				time = glycemiaData.getTime();
			} else if (insulinData != null) {
				noteId = insulinData.getIdNote();
				date = insulinData.getDate();
				time = insulinData.getTime();
			}


			DB_Read rdb = new DB_Read(this);
			Object[] obj = rdb.MyData_Read();
			rdb.close();
			double iRatio = Double.valueOf(obj[3].toString());
			double cRatio = Double.valueOf(obj[4].toString());
			insulinCalculator = new InsulinCalculator((int) iRatio, (int) cRatio);

			insulinCalculator.setCarbs(carbsData != null ? carbsData.getCarbsValue() : 0);
			insulinCalculator.setGlycemia(glycemiaData != null ? glycemiaData.getValue() : 0);
			insulinCalculator.setGlycemiaTarget(insulinData != null ? insulinData.getTargetGlycemia() : 0);
			// get insulin before this one
			String time = null;
			if (carbsData != null) {
				time = carbsData.getTime();
			} else if (glycemiaData != null) {
				time = glycemiaData.getTime();
			}

			DB_Read read = new DB_Read(this);
			int[] lastInsulin = read.InsulinReg_GetLastHourAndQuantity(time);
			read.close();

			int minuteOriginal = lastInsulin[1] * 60 + lastInsulin[2];
			int insulinType = lastInsulin[0];
			int insulinDose = lastInsulin[3];

			insulinCalculator.setLastInsulin(insulinDose, minuteOriginal, insulinType);

		}
		super.onCreate(savedInstanceState);

		// set insulin
		if (insulinData != null) {
			DB_Read rdb = new DB_Read(this);
			String insulinName = rdb.Insulin_GetById(insulinData.getIdInsulin()).getName();
			rdb.close();
			setInsulin(insulinName, insulinData.getInsulinUnits());
		}

		// set note


		if (noteId != -1) {
			DB_Read db_read = new DB_Read(this);
			String note = db_read.Note_GetById(noteId).getNote();
			db_read.close();
			setNote(note);
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
			new AlertDialog.Builder(this)
					.setTitle(getString(R.string.deleteReading))
					.setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							DB_Write wdb = new DB_Write(c);
							try {
								wdb.Logbook_Delete(carbsData.getId(), insulinData.getId(), glycemiaData.getId(), noteId);
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
					}).show();
			return true;
		} else if (item.getItemId() == R.id.menuItem_LogbookDetail_EditSave) {

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void glycemiaTargetChanged(EditText view, String text) {
		int newVal = !text.isEmpty() ? Integer.parseInt(text) : 0;
		boolean changed = insulinData.getTargetGlycemia() != newVal;
		updateIndicator(view, changed);
		setInconsistentInsulin(changed);
	}

	@Override
	protected void glycemiaChanged(EditText view, String text) {
		int newVal = !text.isEmpty() ? Integer.parseInt(text) : 0;
		boolean changed = glycemiaData.getValue() != newVal;
		updateIndicator(view, changed);
		setInconsistentInsulin(changed);
	}

	@Override
	protected void carbsChanged(EditText view, String text) {
		int newVal = !text.isEmpty() ? Integer.parseInt(text) : 0;
		boolean changed = carbsData.getCarbsValue() != newVal;
		updateIndicator(view, changed);
		setInconsistentInsulin(changed);
	}

	@Override
	protected void insulinIntakeChanged(EditText view, String text) {
		float newVal = !text.isEmpty() ? Float.parseFloat(text) : 0;
		boolean changed = Float.compare(insulinData.getInsulinUnits(), newVal) != 0;
		updateIndicator(view, changed);
		setInconsistentInsulin(changed);
	}

	@Override
	protected void dateChanged(EditText view, String text) {
		boolean changed = !date.equals(text);
		updateIndicator(view, changed);
	}

	@Override
	protected void timeChanged(EditText view, String text) {
		updateIndicator(view, !time.equals(text));
		DB_Read rdb = new DB_Read(this);
		int d = (int) rdb.Target_GetTargetByTime(text);
		rdb.close();
		setGlycemiaTarget(d);
	}

	private void updateIndicator(EditText view, boolean valueChanged) {
		if (valueChanged) {
			view.setBackgroundResource(R.drawable.edit_text_holo_dark_changed);
		} else {
			view.setBackgroundResource(R.drawable.default_edit_text_holo_dark);
		}
	}

	boolean showingError=true;

	private void setInconsistentInsulin(boolean inconsistent) {
		ToggleButton button = (ToggleButton) findViewById(R.id.bt_insulin_calc_info);
		button.setCompoundDrawables(null, null, null, null);
		ImageSpan imageSpan;
		showingError=inconsistent;
		if (inconsistent) {
			imageSpan = new ImageSpan(this, R.drawable.ic_report_grey_400_24dp);
//			((ToggleButton) findViewById(R.id.bt_insulin_calc_info)).setCompoundDrawables(null, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_report_problem_grey_500_18dp, null), null, null);
		} else {
			imageSpan = new ImageSpan(this, android.R.drawable.ic_menu_info_details);
//			((ToggleButton) findViewById(R.id.bt_insulin_calc_info)).setCompoundDrawables(null, ResourcesCompat.getDrawable(getResources(), android.R.drawable.ic_menu_info_details, null), null, null);
//			((ToggleButton) findViewById(R.id.bt_insulin_calc_info)).setButtonDrawable(android.R.drawable.ic_menu_info_details);
		}
		SpannableString content = new SpannableString("X");
		content.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		button.setText(content);
		button.setTextOn(content);
		button.setTextOff(content);
	}

	@Override
	boolean shouldSetInsulin() {
		return !showingError;
	}

	@Override
	void insulinsNotFound() {

	}

	@Override
	InsulinCalculator getInsulinCalculator() {
		return insulinCalculator;
	}
}
