package com.jadg.mydiabetes.dialogs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.widget.EditText;
import android.widget.TimePicker;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;

import android.annotation.TargetApi;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")


public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
	EditText item;
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        
		// Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

       
        
        Bundle args = getArguments();
        item=(EditText)getActivity().findViewById(args.getInt("textbox"));
        
        
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

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onTimeSet(TimePicker arg0, int hour, int min) {
		// TODO Auto-generated method stub
		final Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, min);
		Date cal = c.getTime();
		
		
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		String timeString = formatter.format(cal);
		Log.d("time", timeString);
		item.setText(timeString);
		/*
		item.setText(new StringBuilder()
		        // Month is 0 based so add 1
		        .append(hour).append(":")
		        .append(min).append(":00")
		        );*/
		dismiss();
	}
}


