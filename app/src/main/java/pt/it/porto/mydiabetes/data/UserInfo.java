package pt.it.porto.mydiabetes.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import pt.it.porto.mydiabetes.R;

public class UserInfo {

	private int id;
	private String username;
	private int diabetesType;
	private int insulinRatio;
	private int carbsRatio;
	private int lowerRange;
	private int higherRange;
	private String birthday;
	private int gender_index;
	private double height;
	private String lastUpdate;
	private int bg_target_Factor;

	public UserInfo(Cursor cursor) {
		if (cursor.getCount() == 0) {
			throw new IllegalArgumentException();
		}
		//Id
		id = Integer.parseInt(cursor.getString(0));
		//Name
		username = cursor.getString(1);
		//Diabetes Type
		diabetesType = cursor.getInt(2);
		//Insulin Ratio
		insulinRatio = (int) Double.parseDouble(cursor.getString(3));
		//Carbs Ratio
		carbsRatio = (int) Double.parseDouble(cursor.getString(4));
		//Lower Range
		lowerRange = (int) Double.parseDouble(cursor.getString(5));
		//Higher Range
		higherRange = (int) Double.parseDouble(cursor.getString(6));
		//Birth Date
		birthday = cursor.getString(7);
		//Gender
		gender_index = cursor.getInt(8);
		//Height
		height = Double.parseDouble(cursor.getString(9));
		//DateTimeUpdate
		lastUpdate = cursor.getString(10);
		//Glicaemia target
		bg_target_Factor = cursor.getInt(11);
	}

	public UserInfo(int id, String username, int diabetesType, int insulinRatio, int carbsRatio, int lowerRange, int higherRange, String birthday, int gender, double height, String lastedit, int bg_t) {
		this.id = id;
		this.username = username;
		this.diabetesType = diabetesType;
		this.insulinRatio = insulinRatio;
		this.carbsRatio = carbsRatio;
		this.lowerRange = lowerRange;
		this.higherRange = higherRange;
		this.birthday = birthday;
		this.gender_index = gender;
		this.height = height;
		this.lastUpdate = lastedit;
		this.bg_target_Factor = bg_t;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

//	public String getDiabetesType(Context c) {
//	    switch (diabetesType){
//            case 0:
//                return c.getString(R.string.diabetes_type_1);
//            case 1:
//                return c.getString(R.string.diabetes_type_2);
//            case 2:
//                return c.getString(R.string.diabetes_type_gestational);
//        }
//		return null;
//	}
    public int getDiabetesType() {
        return diabetesType;
    }

	public void setDiabetesType(int diabetesType) {
		this.diabetesType = diabetesType;
	}

	public int getTG() {
		return bg_target_Factor;
	}

	public void setTG(int tg) {
		this.bg_target_Factor = tg;
	}

	public int getInsulinRatio() {
		return insulinRatio;
	}

	public void setInsulinRatio(int insulinRatio) {
		this.insulinRatio = insulinRatio;
	}

	public int getCarbsRatio() {
		return carbsRatio;
	}

	public void setCarbsRatio(int carbsRatio) {
		this.carbsRatio = carbsRatio;
	}

	public int getLowerRange() {
		return lowerRange;
	}

	public void setLowerRange(int lowerRange) {
		this.lowerRange = lowerRange;
	}

	public int getHigherRange() {
		return higherRange;
	}

	public void setHigherRange(int higherRange) {
		this.higherRange = higherRange;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

//	public String getGender(Context c) {
//	    switch (this.gender_index){
//            case 0:
//                return c.getString(R.string.gender_female);
//            case 1:
//                return c.getString(R.string.gender_male);
//        }
//		return c.getString(R.string.gender_unknown);
//	}

    public int getGender() {
        return this.gender_index;
    }

	public void setGender(int g) {
		this.gender_index = g;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		UserInfo userInfo = (UserInfo) o;

		if (id != userInfo.id) {
			return false;
		}
		if (insulinRatio != userInfo.insulinRatio) {
			return false;
		}
		if (carbsRatio != userInfo.carbsRatio) {
			return false;
		}
		if (lowerRange != userInfo.lowerRange) {
			return false;
		}
		if (higherRange != userInfo.higherRange) {
			return false;
		}
		if (Double.compare(userInfo.height, height) != 0) {
			return false;
		}
		if (!username.equals(userInfo.username)) {
			return false;
		}
		if (diabetesType != userInfo.diabetesType) {
			return false;
		}
		if (!birthday.equals(userInfo.birthday)) {
			return false;
		}
		if (gender_index != userInfo.gender_index) {
			return false;
		}
		if(bg_target_Factor != userInfo.bg_target_Factor){
			return false;
		}
		return lastUpdate.equals(userInfo.lastUpdate);

	}

	public ContentValues getContentValues(Context c) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("Name", getUsername());
		contentValues.put("DType", diabetesType);
		contentValues.put("InsulinRatio", getInsulinRatio());
		contentValues.put("CarbsRatio", getCarbsRatio());
		contentValues.put("LowerRange", getLowerRange());
		contentValues.put("HigherRange", getHigherRange());
		contentValues.put("BDate", getBirthday());
		contentValues.put("Gender", gender_index);
		contentValues.put("Height", getHeight());
		SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		contentValues.put("DateTimeUpdate", now.format(Calendar.getInstance().getTime()));
		contentValues.put("BG_Target", getTG());
		//contentValues.put("DateTimeUpdate", now.format("%Y-%m-%d %H:%M:%S"));
		return contentValues;
	}

}
