package pt.it.porto.mydiabetes.ui.dialogs;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import pt.it.porto.mydiabetes.utils.DateUtils;


public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
	public static final String ARG_TIME = "time";
	public static final String ARG_TEXT_BOX = "textbox";

	TextView item;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker

		// Use the current time as the default values for the picker
		Calendar calendar;
		Bundle args = getArguments();
		if (args != null) {
			calendar = (Calendar) args.getSerializable(ARG_TIME);
			if (calendar == null) {
				calendar = Calendar.getInstance();
			}
			item = (TextView) getActivity().findViewById(args.getInt(ARG_TEXT_BOX));
		} else {
			calendar = Calendar.getInstance();
		}
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		// Create a new instance of DatePickerDialog and return it
		TimePickerDialog dpdialog = new TimePickerDialog(getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity()));
		dpdialog.setButton(DialogInterface.BUTTON_NEGATIVE,
				getString(android.R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (which == DialogInterface.BUTTON_NEGATIVE) {
							dismiss();
						}
					}
				}
		);

		return dpdialog;
	}

	@Override
	public void onTimeSet(TimePicker arg0, int hour, int min) {
		final Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, min);

		String timeString = DateUtils.getFormattedTime(c);
		//Log.d("time", timeString);
		if (item != null) {
			item.setText(timeString);
		}
		if (listener != null) {
			listener.onTimeSet(timeString);
		}
		/*
		item.setText(new StringBuilder()
		        // Month is 0 based so add 1
		        .append(hour).append(":")
		        .append(min).append(":00")
		        );*/
		dismiss();
	}

	public static DialogFragment getTimePickerFragment(int textbox, @Nullable Calendar time) {
		DialogFragment newFragment = new TimePickerFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_TEXT_BOX, textbox);
		if (time != null) {
			args.putSerializable(ARG_TIME, time);
		}
		newFragment.setArguments(args);
		return newFragment;
	}

	TimePickerChangeListener listener;

	public void setListener(TimePickerChangeListener listener) {
		this.listener = listener;
	}

	public interface TimePickerChangeListener {
		void onTimeSet(String time);
	}
}


