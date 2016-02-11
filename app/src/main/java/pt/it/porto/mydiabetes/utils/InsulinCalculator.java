package pt.it.porto.mydiabetes.utils;

import android.content.Context;

import java.util.Calendar;

import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;

public class InsulinCalculator implements Cloneable {

	InsulinCalculatorListener listener;
	private int glycemiaRatio;
	private int carbsRatio;
	private int insulinTarget;
	private int carbs;
	private int glycemia;
	private int time; // time of intake in minutes
	private float insulinOnBoard = 0.0f;
	private String date;

	public InsulinCalculator(int glycemiaRatio, int carbsRatio) {
		this.glycemiaRatio = glycemiaRatio;
		this.carbsRatio = carbsRatio;
		Calendar currentDateTime = Calendar.getInstance();
		time = currentDateTime.get(Calendar.HOUR_OF_DAY) * 60 + currentDateTime.get(Calendar.MINUTE);
	}

	public InsulinCalculator(Context context) {
		this(0, 0);
		DB_Read rdb = new DB_Read(context);
		Object[] obj = rdb.MyData_Read();
		rdb.close();
		this.glycemiaRatio = (int) (double) Double.valueOf(obj[3].toString());
		this.carbsRatio = (int) (double) Double.valueOf(obj[4].toString());
	}

	public float getInsulinTotal(boolean withIOB, boolean round) {
		float insulinTotal = getInsulinTotal(withIOB);
		if (round) {
			insulinTotal = (float) (0.5 * Math.round(insulinTotal / 0.5));
		}
		return insulinTotal;
	}

	public float getInsulinTotal(boolean withIOB) {
		return getInsulinCarbs() + getInsulinGlycemia() - (withIOB ? insulinOnBoard : 0);
	}

	public float getInsulinCarbs() {
		return ((float)carbs / carbsRatio);
	}

	public float getInsulinGlycemia() {
		return ((float)(glycemia - insulinTarget) / glycemiaRatio);
	}

	public void setLastInsulin(int dose, int minute, int type) {
		float prevIOB = insulinOnBoard;
		insulinOnBoard = 0;
		if (type == 0) {
			int minuteDiff = time - minute;
			if (minuteDiff < 0) {
				insulinOnBoard = 0;
			} else {
				insulinOnBoard = (float) (dose - dose * (minuteDiff / 30 * 0.1));
				if (insulinOnBoard < 0) {
					insulinOnBoard = 0;
				}
			}
		}
		if (Float.compare(prevIOB, insulinOnBoard) != 0 && listener != null) {
			listener.insulinOnBoardChanged(this);
		}
	}

	public float getInsulinOnBoard() {
		return insulinOnBoard;
	}

	public float getGlycemiaRatio() {
		return glycemiaRatio;
	}

	public void setGlycemiaRatio(int glycemiaRatio) {
		this.glycemiaRatio = glycemiaRatio;
	}

	public float getCarbsRatio() {
		return carbsRatio;
	}

	public void setCarbsRatio(int carbsRatio) {
		this.carbsRatio = carbsRatio;
	}

	public int getInsulinTarget() {
		return insulinTarget;
	}

	public void setGlycemiaTarget(int insulinTarget) {
		this.insulinTarget = insulinTarget;
	}

	public int getCarbs() {
		return carbs;
	}

	public void setCarbs(int carbs) {
		this.carbs = carbs;
	}

	public int getGlycemia() {
		return glycemia;
	}

	public void setGlycemia(int glycemia) {
		this.glycemia = glycemia;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public void setTime(Context context, int hour, int minute, String date) {
		if(date!=null){
			this.date = date;
		}
		this.time = hour * 60 + minute;
		// load last insulin from database

		DB_Read read = new DB_Read(context);
		int[] lastInsulin = read.InsulinReg_GetLastHourAndQuantity((hour < 10 ? "0" : "") + String.valueOf(hour) + ":" + (minute < 10 ? "0" : "") + String.valueOf(minute), this.date);
		read.close();

		// this will be fun if it was in the day before at 23:XX and doing a Meal at 01:XX :)
		// todo // FIXME: 03/02/16 take in consideration date
		int minuteOriginal = lastInsulin[1] * 60 + lastInsulin[2];
		int insulinType = lastInsulin[0];
		int insulinDose = lastInsulin[3];

		setLastInsulin(insulinDose, minuteOriginal, insulinType);
	}

	public void setListener(InsulinCalculatorListener listener) {
		this.listener = listener;
	}

	@Override
	public InsulinCalculator clone() {
		InsulinCalculator newCalculator = new InsulinCalculator(glycemiaRatio, carbsRatio);
		newCalculator.setCarbs(carbs);
		newCalculator.setGlycemia(glycemia);
		newCalculator.setGlycemiaTarget(insulinTarget);
		newCalculator.insulinOnBoard = insulinOnBoard;
		return newCalculator;
	}

	public void setTime(Context context, String time, String date) {
		Calendar calendar = TimePickerFragment.getCalendar(time);
		if (calendar != null) {
			setTime(context, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), date);
		}
	}

	public interface InsulinCalculatorListener {
		void insulinOnBoardChanged(InsulinCalculator calculator);
	}
}
