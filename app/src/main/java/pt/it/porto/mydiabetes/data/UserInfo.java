package pt.it.porto.mydiabetes.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import pt.it.porto.mydiabetes.R;

public class UserInfo {

	private int id;
	private String username;
	private DiabetesType diabetesType;
	private int insulinRatio;
	private int carbsRatio;
	private int lowerRange;
	private int higherRange;
	private String birthday;
	private Gender gender;
	private double height;
	private String lastUpdate;

	public UserInfo(Cursor cursor) {
		if (cursor.getCount() == 0) {
			throw new IllegalArgumentException();
		}
		//Id
		id = Integer.parseInt(cursor.getString(0));
		//Name
		username = cursor.getString(1);
		//Diabetes Type
		diabetesType = DiabetesType.getEnum(cursor.getString(2));
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
		gender = Gender.getEnum(cursor.getString(8));
		//Height
		height = Double.parseDouble(cursor.getString(9));
		//DateTimeUpdate
		lastUpdate = cursor.getString(10);
	}

	public UserInfo(int id, String username, String diabetesType, int insulinRatio, int carbsRatio, int lowerRange, int higherRange, String birthday, String gender, double height, String lastedit) {
		this.id = id;
		this.username = username;
		this.diabetesType = DiabetesType.getEnum(diabetesType);
		this.insulinRatio = insulinRatio;
		this.carbsRatio = carbsRatio;
		this.lowerRange = lowerRange;
		this.higherRange = higherRange;
		this.birthday = birthday;
		this.gender = Gender.getEnum(gender);
		this.height = height;
		this.lastUpdate = lastedit;
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

	public DiabetesType getDiabetesType() {
		return diabetesType;
	}

	public void setDiabetesType(String diabetesType) {
		this.diabetesType = DiabetesType.getEnum(diabetesType);
	}

	public void setDiabetesType(DiabetesType diabetesType) {
		this.diabetesType = diabetesType;
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

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public void setGender(String gender, Context context) {
		this.gender = Gender.getEnum(gender, context);
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
		if (gender != userInfo.gender) {
			return false;
		}
		return lastUpdate.equals(userInfo.lastUpdate);

	}

	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put("Name", getUsername());
		contentValues.put("DType", getDiabetesType().toString());
		contentValues.put("InsulinRatio", getInsulinRatio());
		contentValues.put("CarbsRatio", getCarbsRatio());
		contentValues.put("LowerRange", getLowerRange());
		contentValues.put("HigherRange", getHigherRange());
		contentValues.put("BDate", getBirthday());
		contentValues.put("Gender", getGender().toString());
		contentValues.put("Height", getHeight());
		SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		contentValues.put("DateTimeUpdate", now.format(Calendar.getInstance().getTime()));
		//contentValues.put("DateTimeUpdate", now.format("%Y-%m-%d %H:%M:%S"));
		return contentValues;
	}

	public enum Gender {
		WOMAN("Feminino", R.string.gender_female), MAN("Masculino", R.string.gender_male);

		String value;
		private int resource;

		Gender(String value, int resource) {
			this.value = value;
			this.resource = resource;
		}

		public static Gender getEnum(String o) {
			for (Gender v : values()) {
				if (v.getValue().equalsIgnoreCase(o)) {
					return v;
				}
			}
			throw new IllegalArgumentException();
		}


		public static Gender getEnum(String o, Context context) {
			for (Gender v : values()) {
				if (context.getString(v.resource).equalsIgnoreCase(o)) {
					return v;
				}
			}
			throw new IllegalArgumentException();
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	public enum DiabetesType {
		TYPE_1("Tipo 1", R.string.diabetes_type_1),
		TYPE_2("Tipo 2", R.string.diabetes_type_2),
		GESTATIONAL("Gestacional", R.string.diabetes_type_gestational);

		String value;
		int resource;

		DiabetesType(String value, int resource) {
			this.value = value;
			this.resource = resource;
		}

		public static DiabetesType getEnum(String o) {
			if ((o.endsWith("1") && o.length()>1) || o.equals("0")) {
				return TYPE_1;
			} else if ((o.endsWith("2") && o.length()>1) || o.equals("1")) {
				return TYPE_2;
			} else {
				return GESTATIONAL;
			}
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return value;
		}

		public String getValue(Context context) {
			return context.getString(resource);
		}
	}
}
