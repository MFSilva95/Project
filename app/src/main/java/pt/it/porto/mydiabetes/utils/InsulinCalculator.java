package pt.it.porto.mydiabetes.utils;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import pt.it.porto.mydiabetes.data.CarbsRatioData;
import pt.it.porto.mydiabetes.data.Sensitivity;
import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.database.DB_Read;

public class InsulinCalculator  implements Cloneable {

	/** Indicates the linear decline per half an hour of the insulin on board
	 */
	public final float IOB_LINE_DECLINE = 0.125f;
	InsulinCalculatorListener listener;
	private int glycemiaRatio;
	private int carbsRatio;
	private int insulinTarget;
	private int carbs;
	private int glycemia;
	private int time; // time of intake in minutes
	private float insulinOnBoard = 0.0f;
	private Calendar date;
	private Context context;

	public InsulinCalculator(Context context, Calendar c) {

		date = c;
		this.context = context;
		DB_Read rdb = new DB_Read(context);

		int defaultCarbsRatio = rdb.getCarbsRatio();
		int defaultInsuratio = rdb.getInsulinRatio();

		String d = DateUtils.formatTimeToDb(c);
		Log.i("cenas", "InsulinCalculator: !!!! "+d);

		glycemiaRatio = rdb.Sensitivity_GetCurrent(d);
		carbsRatio = rdb.Ratio_GetCurrent(d);

		//ArrayList<CarbsRatioData> allCarbs =  rdb.Ratio_GetAll();
		//ArrayList<Sensitivity> allSens =  rdb.Sensitivity_GetAll();
		rdb.close();

//		this.glycemiaRatio = getCurrentRatioInsulinElement(allSens);
//		this.carbsRatio = getCurrentCarbsRatioElement(allCarbs);
		if(glycemiaRatio==-1){glycemiaRatio = defaultInsuratio;}
		if(carbsRatio==-1){carbsRatio = defaultCarbsRatio;}

		time = (date.get(Calendar.HOUR_OF_DAY) * 60) + date.get(Calendar.MINUTE);
	}

	public void updateRatios(Calendar c){
		DB_Read rdb = new DB_Read(context);

		int defaultCarbsRatio = rdb.getCarbsRatio();
		int defaultInsuratio = rdb.getInsulinRatio();

		String d = DateUtils.formatTimeToDb(c);

		glycemiaRatio = rdb.Ratio_GetCurrent(d);
		carbsRatio = rdb.Sensitivity_GetCurrent(d);

//		glycemiaRatio = rdb.Ratio_GetCurrent(DateUtils.formatToDb(c));
//		carbsRatio = rdb.Sensitivity_GetCurrent(DateUtils.formatToDb(c));

		rdb.close();

		if(glycemiaRatio==-1){glycemiaRatio = defaultInsuratio;}
		if(carbsRatio==-1){carbsRatio = defaultCarbsRatio;}
	}

	private int getCurrentRatioInsulinElement(ArrayList<Sensitivity> list) {
		if(list==null){return -1;}
		for (Sensitivity sens:list){
			int currHour = date.get(Calendar.HOUR_OF_DAY);
			int currMinute = date.get(Calendar.MINUTE);
			int currTime = currHour*60 +currMinute;
			if (sens.getStartInMinutes() >= currTime && sens.getEndInMinutes() <= currTime){
				return (int) sens.getSensitivity();
			}
		}
		return -1;
	}
	private int getCurrentCarbsRatioElement(ArrayList<CarbsRatioData> list) {
		if(list==null){return -1;}
		for (CarbsRatioData sens:list){
			int currHour = date.get(Calendar.HOUR_OF_DAY);
			int currMinute = date.get(Calendar.MINUTE);
			int currTime = currHour*60 +currMinute;
			if (sens.getStartInMinutes() >= currTime && sens.getEndInMinutes() <= currTime){
				return (int) sens.getValue();
			}
		}
		return -1;
	}

	public float getInsulinTotalFloat(boolean withIOB, boolean round) {
		float insulinTotal = getInsulinTotalFloat(withIOB);
		if (round) {
			insulinTotal = (float) (0.5 * Math.round(insulinTotal / 0.5));
		}
		if(insulinTotal<0){
			insulinTotal = 0.0f;
		}
		Log.i("cenas", "  -> getInsulinTotal: "+insulinTotal);
		return insulinTotal;
	}

	public float getInsulinTotalFloat(boolean withIOB) {
		return getInsulinCarbs() + getInsulinGlycemia() - (withIOB ? insulinOnBoard : 0);
	}


	public String getInsulinTotal(){
		String result = String.valueOf(getInsulinTotalFloat(false, true));
		if(result==null || result.equals("0.0")){return "---";}
		else{
			return result;
		}
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
				insulinOnBoard = (dose - dose * (minuteDiff / 30 * IOB_LINE_DECLINE));
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

	public int getCarbsRatio() {
		return carbsRatio;
	}
	public int getInsulinRatio() {
		return glycemiaRatio;
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

	//public void setTime(Context context, int hour, int minute, String date) {
	public void setTime(Context context, Calendar c){
		this.date = c;
//		if(date!=null){
//			this.date = date;
//		}
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		this.time = (hour *60) + minute;//hour * 60 + minute;
		// load last insulin from database

		DB_Read read = new DB_Read(context);
		int[] lastInsulin =
				read.InsulinReg_GetLastHourAndQuantity((hour < 10 ? "0" : "") + String.valueOf(hour) + ":" + (minute < 10 ? "0" : "") + String.valueOf(minute), DateUtils.getFormattedTime(date));
		read.close();

		// this will be fun if it was in the day before at 23:XX and doing a Meal at 01:XX :)

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
		InsulinCalculator newCalculator = new InsulinCalculator(context, date);
		newCalculator.setCarbs(carbs);
		newCalculator.setGlycemia(glycemia);
		newCalculator.setGlycemiaTarget(insulinTarget);
		newCalculator.insulinOnBoard = insulinOnBoard;
		return newCalculator;
	}

//	public void setTime(Context context, String time, String date) {
//		Calendar calendar = DateUtils.getTimeCalendar(time);
//		if (calendar != null) {
//			setTime(context, calendar);
//		}
//	}




	public interface InsulinCalculatorListener {
		void insulinOnBoardChanged(InsulinCalculator calculator);
	}
}
