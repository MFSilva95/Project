package com.jadg.mydiabetes.dialogs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.widget.DatePicker;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;







public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	EditText item;
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Bundle args = getArguments();
        
        item=(EditText)getActivity().findViewById(args.getInt("textbox"));
        
        
        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dpdialog = new DatePickerDialog(getActivity(), this, year, month, day);
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
	public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
    	//et_BDate
		Calendar cal = Calendar.getInstance();
	    cal.set(year, month, day);
	    Date newDate = cal.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    String dateString = formatter.format(newDate);
    	item.setText(dateString);
    	dismiss();
    }
}


