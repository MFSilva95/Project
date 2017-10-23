package pt.it.porto.mydiabetes.ui.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

import pt.it.porto.mydiabetes.utils.DateUtils;


public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	public static final String ARG_DATE = "date";
	public static final String ARG_TEXT_BOX = "textbox";
	private DatePickerDialog.OnDateSetListener listener;

	TextView item;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();
		Calendar calendar;
		if (args != null) {
			// Use the current date as the default date in the picker
			calendar = (Calendar) args.getSerializable(ARG_DATE);
			if (calendar == null) {
				calendar = Calendar.getInstance();
			}
			item = (TextView) getActivity().findViewById(args.getInt(ARG_TEXT_BOX));
		} else {
			calendar = Calendar.getInstance();
		}

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		DatePickerDialog dpdialog;

		if(listener==null){
			// Create a new instance of DatePickerDialog and return it
			dpdialog = new DatePickerDialog(getActivity(), this, year, month, day);
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
		}else{
			// Create a new instance of DatePickerDialog and return it
			dpdialog = new DatePickerDialog(getActivity(), listener, year, month, day);
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
		}


		return dpdialog;
	}

	public void setListener(DatePickerDialog.OnDateSetListener listener) {
		this.listener = listener;
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		// Do something with the date chosen by the user
		//et_BDate
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		String dateString = DateUtils.getFormattedDate(cal);
		if (item != null) {
			item.setText(dateString);
		}
		dismiss();
	}

	public static DialogFragment getDatePickerFragment(int textbox, @Nullable Calendar date) {
		DialogFragment newFragment = new DatePickerFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_TEXT_BOX, textbox);
		if (date != null) {
			args.putSerializable(ARG_DATE, date);
		}
		newFragment.setArguments(args);
		return newFragment;
	}

	public static DialogFragment getDatePickerFragment(int textbox, DatePickerDialog.OnDateSetListener lis, @Nullable Calendar date) {
		DatePickerFragment newFragment = new DatePickerFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_TEXT_BOX, textbox);
		if (date != null) {
			args.putSerializable(ARG_DATE, date);
		}
		newFragment.setArguments(args);
		newFragment.setListener(lis);
		return newFragment;
	}

}


