package com.jadg.mydiabetes.fragments;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.view.LayoutInflater;
import android.app.Fragment;
import android.widget.Button;
import android.widget.TextView;
import android.annotation.SuppressLint;


import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import com.jadg.mydiabetes.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link DB_BackupRestore.OnFragmentInteractionListener} interface to handle
 * interaction events. Use the {@link DB_BackupRestore#newInstance} factory
 * method to create an instance of this fragment.
 * 
 */


public class DB_BackupRestore extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_db__backup_restore, null);
		
		if(!fillBackup(v)){
			Button restore = (Button)v.findViewById(R.id.bt_Restore);
			restore.setEnabled(false);
		}
		
		return v;
	}

	
	@SuppressLint("SimpleDateFormat")
	public boolean fillBackup(View v){
		if(isSDWriteable()){
			File inputFile = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes/backup/DB_Diabetes");
			if (inputFile.exists()) {
				Calendar cal = Calendar.getInstance();
			    cal.setTimeInMillis(inputFile.lastModified());
			    Date newDate = cal.getTime();
			    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    String dateString = formatter.format(newDate);
				
			    TextView lastbackup = (TextView)v.findViewById(R.id.tv_lastBackup);
			    lastbackup.setText(dateString);
			    return true;
			}
			else{
				return false;
			}
		}else{
			return false;
		}
	}

	private boolean isSDWriteable(){
	    boolean rc = false;
	
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	      rc = true;
	    }
	
	    return rc;
	}
}
