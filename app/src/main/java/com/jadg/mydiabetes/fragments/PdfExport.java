package com.jadg.mydiabetes.fragments;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.view.LayoutInflater;
import android.app.Fragment;
import android.widget.EditText;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.jadg.mydiabetes.R;






public class PdfExport extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_pdf_export, null);
		FillDates(v);


		return v;
	}

	
	
	
	@SuppressLint("SimpleDateFormat")
	public void FillDates(View v){
		EditText dateago = (EditText)v.findViewById(R.id.et_pdfexport_DataFrom);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        
        Calendar cal = Calendar.getInstance();
	    cal.set(year, month, day);
	    Date newDate = cal.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    String dateString = formatter.format(newDate);
        
	    dateago.setText(dateString);
        
        EditText datenow = (EditText)v.findViewById(R.id.et_pdfexport_DataTo);
        c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        cal.set(year, month, day);
	    newDate = cal.getTime();
	    dateString = formatter.format(newDate);
        datenow.setText(dateString);
	}
	
	
}
