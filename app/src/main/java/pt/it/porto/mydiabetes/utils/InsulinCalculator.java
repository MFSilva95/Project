package pt.it.porto.mydiabetes.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;

import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.Insulin;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.activities.NewHomeRegistry;

import static java.lang.String.valueOf;
import static pt.it.porto.mydiabetes.ui.activities.NewHomeRegistry.*;


public class InsulinCalculator  implements Cloneable {

	/** Indicates the linear decline per half an hour of the insulin on board
	 */

	public final float IOB_LINE_DECLINE = 0.125f;
	InsulinCalculatorListener listener;
	private float glycemiaRatio;
	private float carbsRatio;
	private int insulinTarget;
	private int carbs;
	private MealType type_of_meal;
	private float lipids;
	private float protein;
	private float icarbs;
	private int glycemia;
	@Nullable
	private Insulin typeInsulin;
	private int insuType;

	int INSULIN_TYPE_PUMP = 1;
	int INSULIN_TYPE_PEN = 0;

	private int time; // time of intake in minutes
	private float insulinOnBoard = 0.0f;
	private float insuladjvalue ;
	private Calendar date;
	private Context context;

	public void updateInsuType(int insuType){
		this.insuType = insuType;
	}

	public InsulinCalculator(Context context, Calendar c) {

		date = c;
		this.context = context;
		String d = DateUtils.formatTimeToDb(c);
		DB_Read rdb = new DB_Read(context);
		int defaultCarbsRatio = rdb.getCarbsRatio();
		int defaultInsuratio = rdb.getInsulinRatio();

		glycemiaRatio = rdb.Sensitivity_GetCurrent(d);
		carbsRatio = rdb.Ratio_GetCurrent(d);
		rdb.close();

		type_of_meal = MealType.NoMeal;



		if(glycemiaRatio==-1){glycemiaRatio = defaultInsuratio;}
		if(carbsRatio==-1){carbsRatio = defaultCarbsRatio;}

		time = (date.get(Calendar.HOUR_OF_DAY) * 60) + date.get(Calendar.MINUTE);

	}




	public void updateRatios(Calendar c){
		DB_Read rdb = new DB_Read(context);

		int defaultCarbsRatio = rdb.getCarbsRatio();
		int defaultInsuratio = rdb.getInsulinRatio();
		String d = DateUtils.formatTimeToDb(c);

		glycemiaRatio = rdb.Sensitivity_GetCurrent(d);
		carbsRatio = rdb.Ratio_GetCurrent(d);
		rdb.close();

		if(glycemiaRatio==-1){glycemiaRatio = defaultInsuratio;}
		if(carbsRatio==-1){carbsRatio = defaultCarbsRatio;}
	}


//	public float getInsulinTotalFloat(boolean withIOB, boolean round, boolean contain_LP) {
//		float insulinTotal = getInsulinTotalFloat(withIOB);
//
//		if(contain_LP){
//			float BN = 0;
//			float BS = 0;
//
//			float CU = carbs;
//			float FPU = (protein*4 + lipids*9)/100;
//			float IRFactor = carbsRatio;
//			float CU_FPU_correction = NBSBCorrection / 100;
//
//			float CU_perc = CU/(CU+FPU);
//
//			//if(CU_perc <0.2){BN = 0;}
//			if(CU_perc>=0.2 && CU_perc<=0.8){ BN = CU*IRFactor*(1-CU_FPU_correction);}
//			if(CU_perc>0.8){BN = CU*IRFactor;}
//
//			//if(FPU <1.0){BS = 0;}
//			if(CU_perc < 0.2){BS = FPU*IRFactor;}
//			if(CU_perc >= 0.2 && CU_perc <= 0.8){ BS = FPU*IRFactor*(1+CU_FPU_correction);}
//			//if(CU_perc >= 0.8){BS = 0;}
//		}
//
//		if (round) {
//			insulinTotal = (float) (0.5 * Math.round(insulinTotal / 0.5));
//		}
//		if(insulinTotal<0){
//			insulinTotal = 0.0f;
//		}
//		return insulinTotal;
//	}

	///



	public float getInsulinAdjustment(){ //valor de insulina a ser ajustado horas após
		float valuetocorr = 0.0f;
		if(getType_of_meal() == MealType.BigM){
			if(this.insuType==INSULIN_TYPE_PUMP){
					valuetocorr = getInsulinCarbs() * 0.35f + getInsulinTotalFloat(false, true);
			}
			else{
				valuetocorr = getInsulinCarbs() * 0.35f;
			}
		}
		return valuetocorr;
	}

	public float getInsulinCorr(){ //Valor de insulina de correcção a ser adicionado
		float Carbsratio = getInsulinCarbs();
		insuladjvalue = 0.0f;
		if(getType_of_meal()!=null){
			switch (getType_of_meal()) {
				case BigM:  // meal with high concentration of fat
					insuladjvalue = 0.0f; // só é aplicado correcção h após a refeição
					break;
				case StandardM:  //meal with protein
					insuladjvalue = 0.2f*Carbsratio;
					break;
				case SmallM:  //meal with HGI
					insuladjvalue = 0.0f;
					break;
			}
		}
		return insuladjvalue;
	}


	public MealType getType_of_meal() {
		return type_of_meal;
	}

	public void setType_of_meal(MealType type_of_meal) {
		this.type_of_meal = type_of_meal;
	}

	///
	public float getInsulinTotalFloat(boolean withIOB, boolean round) {
		float insulinTotal = getInsulinTotalFloat(withIOB);
		if (round) {
			insulinTotal = (float) (0.5 * Math.round(insulinTotal / 0.5));
		}
		if(insulinTotal<0){
			insulinTotal = 0.0f;
		}
		return insulinTotal;
	}

	public float getInsulinTotalFloat(boolean withIOB) {
		return getInsulinCarbs() + getInsulinCorr() + getInsulinGlycemia() - (withIOB ? insulinOnBoard : 0);
	}


	public String getInsulinTotal(){
		String result = valueOf(getInsulinTotalFloat(false, true));
		if(result==null || result.equals("0.0")){return "---";}
		else{
			return result;
		}
	}

	public float getInsulinCarbs() {
		if(carbs<=0){return 0;}
		return ((float)carbs / carbsRatio);
	}

	public float getInsulinGlycemia() {
		if(glycemia<=0){return 0;}
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

	public float getCarbsRatio() {
		return carbsRatio;
	}
	public float getInsulinRatio() {
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
	public void setLipids(int l) {
		this.lipids = l;
	}
	public void setProtein(int p) {
		this.protein = p;
	}

	public void setTime(int time) {
		this.time = time;
	}

	//public void setTime(Context context, int hour, int minute, String date) {
	public void setTime(Context context, Calendar c){
		this.date = c;

		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		this.time = (hour *60) + minute;//hour * 60 + minute;
		// load last insulin from database

		DB_Read read = new DB_Read(context);
		int[] lastInsulin =
				read.InsulinReg_GetLastHourAndQuantity((hour < 10 ? "0" : "") + valueOf(hour) + ":" + (minute < 10 ? "0" : "") + valueOf(minute), DateUtils.getFormattedTime(date));
		read.close();

		// this will be fun if it was in the day before at 23:XX and doing a Meal at 01:XX :)

		int minuteOriginal = lastInsulin[1] * 60 + lastInsulin[2];
		int insulinType = lastInsulin[0];
		int insulinDose = lastInsulin[3];

		setLastInsulin(insulinDose, minuteOriginal, insulinType);
	}
	public int setExtraInsulinVisibility(){
		int visibility=0;
		if (getType_of_meal() == MealType.BigM && this.insuType == INSULIN_TYPE_PEN) {
			visibility = 0;
		}else if (getType_of_meal() == MealType.BigM && this.insuType == INSULIN_TYPE_PUMP) {
			visibility = 8;
		} else{
			visibility=8;
		}
		return visibility;
	}
	public int setNoExtraInsulinVisibility() {
		int visibility = 0;
		if (getType_of_meal() == MealType.BigM){
			visibility = 8;
		} else if (getType_of_meal()== MealType.SmallM){
			visibility = 0;
		} else{
			visibility=0;
		}
		return visibility;
	}
	public int setExtraInsulinVisibility_Bomb(){
		int visibility=0;
		if (getType_of_meal() == MealType.BigM && this.insuType == INSULIN_TYPE_PEN) {
			visibility = 8;
		}else if (getType_of_meal() == MealType.BigM && this.insuType == INSULIN_TYPE_PUMP) {
			visibility = 0;
		} else{
			visibility=8;
		}
		return visibility;
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
		newCalculator.setType_of_meal(type_of_meal);
		return newCalculator;
	}




	public interface InsulinCalculatorListener {
		void insulinOnBoardChanged(InsulinCalculator calculator);
	}
}
