package pt.it.porto.mydiabetes.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.FeaturesDB;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;
import pt.it.porto.mydiabetes.utils.LocaleUtils;

public abstract class BaseMealActivity extends FormActivity {

	Calendar dateTime = null;
	private EditText insulinIntake;
	private EditText glycemia;
	private EditText carbs;
	private EditText target;
	private TextView time;
	private TextView date;
	private TextView advice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		insulinIntake = (EditText) findViewById(R.id.insulin_intake);
		target = (EditText) findViewById(R.id.glycemia_target);
		glycemia = (EditText) findViewById(R.id.glycemia);
		carbs = (EditText) findViewById(R.id.carbs);
		time = (TextView) findViewById(R.id.time);
		date = (TextView) findViewById(R.id.date);

		setUpdateListeners();
		setupInsulinCalculator();

		FeaturesDB featuresDB = new FeaturesDB(MyDiabetesStorage.getInstance(this));
		super.setUseIOB(featuresDB.isFeatureActive(FeaturesDB.FEATURE_INSULIN_ON_BOARD));
	}


	private void setUpdateListeners() {
		target.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = s.toString();
				int val = 0;
				if (!text.isEmpty()) {
					try {
						val = Integer.parseInt(s.toString());
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				insulinCalculator.setGlycemiaTarget(val);
				setInsulinIntake();
				glycemiaTargetChanged(target, text);
			}
		});

		glycemia.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = s.toString();
				int val = 0;
				if (text.isEmpty()) {
					val = 0;
				} else {
					try {
						val = Integer.parseInt(s.toString());
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				insulinCalculator.setGlycemia(val);
				setInsulinIntake();
				glycemiaChanged(glycemia, text);
			}
		});

		carbs.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = s.toString();
				int val = 0;
				if (!text.isEmpty()) {
					try {
						val = Integer.parseInt(s.toString());
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}

				insulinCalculator.setCarbs(val);
				setInsulinIntake();
				carbsChanged(carbs, text);
			}
		});


		time.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				setPhaseOfDayByValue(s.toString());
				timeChanged(time, s.toString());
			}
		});

		date.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				dateChanged(date, s.toString());
			}
		});

		insulinIntake.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = s.toString();
//				float val = 0;
				if (!text.isEmpty()) {
//					try {
//						val = Float.parseFloat(s.toString());
//					} catch (NumberFormatException e) {
//						e.printStackTrace();
//					}
				}
				insulinIntakeChanged(insulinIntake, text);
			}

		});
	}

	protected abstract void glycemiaTargetChanged(EditText view, String text);

	protected abstract void glycemiaChanged(EditText view, String text);

	protected abstract void carbsChanged(EditText view, String text);

	protected abstract void insulinIntakeChanged(EditText view, String text);

	protected abstract void dateChanged(TextView view, String text);

	protected abstract void timeChanged(TextView view, String text);

	void showUpdateIndicator(EditText view, boolean valueChanged) {
		if (valueChanged) {
			view.setBackgroundResource(R.drawable.edit_text_holo_dark_changed);
		} else {
			view.setBackgroundResource(R.drawable.default_edit_text_holo_dark);
		}
	}


	protected boolean shouldSetInsulin() {
		return true;
	}


	abstract InsulinCalculator getInsulinCalculator();

	public void setupInsulinCalculator() {
		this.insulinCalculator = getInsulinCalculator();
		if (insulinCalculator.getCarbs() > 0) {
			carbs.setText(String.format(LocaleUtils.MY_LOCALE, "%d", (int) insulinCalculator.getCarbs()));
		}
		if (insulinCalculator.getGlycemia() > 0) {
			glycemia.setText(String.format(LocaleUtils.MY_LOCALE, "%d", (int) insulinCalculator.getGlycemia()));
		}
		if (insulinCalculator.getInsulinTarget() > 0) {
			target.setText(String.format(LocaleUtils.MY_LOCALE, "%d", (int) insulinCalculator.getInsulinTarget()));
		}
		setGlycemiaTarget(insulinCalculator.getInsulinTarget());
		float insulinTotal = insulinCalculator.getInsulinTotal(useIOB, true);
		if (insulinTotal > 0) {
			insulinIntake.setText(String.format(LocaleUtils.MY_LOCALE, "%d", (int) insulinTotal));
		}
	}


	public Calendar getDateTime() {
		updateDateTime();
		return dateTime;
	}

	private void updateDateTime() {
		try {
			// only udpates dateTime if needed
			Calendar newDateTime = DateUtils.getDateTime(getDate(), getTime());
			if (DateUtils.isSameTime(dateTime, newDateTime)) {
				return;
			}
			dateTime = newDateTime;
			dateTime.set(Calendar.SECOND, Calendar.getInstance().get(Calendar.SECOND)); // we add seconds to be possible distinguish in logbook multiple references in same minute
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}


	public boolean canSave() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (carbs.getText().toString().isEmpty()) {
			carbs.requestFocus();
			imm.showSoftInput(carbs, InputMethodManager.SHOW_IMPLICIT);
		} else if (glycemia.getText().toString().isEmpty()) {
			glycemia.requestFocus();
			imm.showSoftInput(glycemia, InputMethodManager.SHOW_IMPLICIT);
		} else {
			return true;
		}
		return false;
	}


}
