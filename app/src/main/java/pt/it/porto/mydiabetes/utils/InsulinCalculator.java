package pt.it.porto.mydiabetes.utils;

import java.util.Calendar;

public class InsulinCalculator implements Cloneable {

	private float glycemiaRatio;
	private float carbsRatio;
	private float insulinTarget;
	private float carbs;
	private float glycemia;

	private float insulinOnBoard = 0.0f;

	public InsulinCalculator(float glycemiaRatio, float carbsRatio) {
		this.glycemiaRatio = glycemiaRatio;
		this.carbsRatio = carbsRatio;
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
		return (carbs / carbsRatio);
	}

	public float getInsulinGlycemia() {
		return ((glycemia - insulinTarget) / glycemiaRatio);
	}

	public void setLastInsulin(int dose, int minute, int type) {
		if (type != 0) {
			return;
		}
		Calendar currentDateTime = Calendar.getInstance();
		int currentTime = currentDateTime.get(Calendar.HOUR_OF_DAY) * 60 + currentDateTime.get(Calendar.MINUTE);
		int minuteDiff = currentTime - minute;
		insulinOnBoard = (float) (dose - dose * ((int) (minuteDiff / 30) * 0.1));
	}

	public float getInsulinOnBoard() {
		return insulinOnBoard;
	}

	public float getGlycemiaRatio() {
		return glycemiaRatio;
	}

	public void setGlycemiaRatio(float glycemiaRatio) {
		this.glycemiaRatio = glycemiaRatio;
	}

	public float getCarbsRatio() {
		return carbsRatio;
	}

	public void setCarbsRatio(float carbsRatio) {
		this.carbsRatio = carbsRatio;
	}

	public float getInsulinTarget() {
		return insulinTarget;
	}

	public void setInsulinTarget(float insulinTarget) {
		this.insulinTarget = insulinTarget;
	}

	public float getCarbs() {
		return carbs;
	}

	public void setCarbs(float carbs) {
		this.carbs = carbs;
	}

	public float getGlycemia() {
		return glycemia;
	}

	public void setGlycemia(float glycemia) {
		this.glycemia = glycemia;
	}

	@Override
	public InsulinCalculator clone() {
		InsulinCalculator newCalculator = new InsulinCalculator(glycemiaRatio, carbsRatio);
		newCalculator.setCarbs(carbs);
		newCalculator.setGlycemia(glycemia);
		newCalculator.setInsulinTarget(insulinTarget);
		newCalculator.insulinOnBoard = insulinOnBoard;
		return newCalculator;
	}
}
