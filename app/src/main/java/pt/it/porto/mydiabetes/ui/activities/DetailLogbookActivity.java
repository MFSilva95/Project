package pt.it.porto.mydiabetes.ui.activities;

import android.os.Bundle;
import android.widget.EditText;

import pt.it.porto.mydiabetes.database.DB_Read;
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
			String insulinName = rdb.Insulin_GetById(insulinData.getId()).getName();
			rdb.close();
			setInsulin(insulinName, insulinData.getInsulinUnits());
		}

		// set note
		int noteId=-1;
		if (carbsData != null) {
			noteId = carbsData.getId_Note();
		} else if (glycemiaData != null) {
			noteId = glycemiaData.getIdNote();
		} else if (insulinData!=null){
			noteId=insulinData.getIdNote();
		}

		if(noteId!=-1){
			DB_Read db_read=new DB_Read(this);
			String note=db_read.Note_GetById(noteId).getNote();
			db_read.close();
			setNote(note);
		}

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

	}

	@Override
	void insulinsNotFound() {

	}

	@Override
	InsulinCalculator getInsulinCalculator() {
		return insulinCalculator;
	}
}
