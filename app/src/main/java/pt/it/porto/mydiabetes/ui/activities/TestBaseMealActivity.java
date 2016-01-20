package pt.it.porto.mydiabetes.ui.activities;

import android.widget.EditText;

import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;

public class TestBaseMealActivity extends BaseMealActivity {
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

		DB_Read read = new DB_Read(this);
		int[] lastInsulin = read.InsulinReg_GetLastHourAndQuantity();
		read.close();

		int minuteOriginal = lastInsulin[1] * 60 + lastInsulin[2];
		int insulinType = lastInsulin[0];
		int insulinDose = lastInsulin[3];

		insulinCalculator.setLastInsulin(insulinDose, minuteOriginal, insulinType);

		return insulinCalculator;
	}
}
