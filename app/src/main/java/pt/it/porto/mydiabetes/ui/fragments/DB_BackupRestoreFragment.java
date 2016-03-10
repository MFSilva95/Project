package pt.it.porto.mydiabetes.ui.fragments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.TextView;
import android.annotation.SuppressLint;


import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.utils.DbUtils;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */


public class DB_BackupRestoreFragment extends Fragment {

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
			v.findViewById(R.id.share).setEnabled(false);
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
			} else {
				return false;
			}
		}else{
			return false;
		}
	}

	public static boolean isSDWriteable(){
	    boolean rc = false;
	
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	      rc = true;
	    }
	
	    return rc;
	}

	public static boolean hasBackup(){
		if(isSDWriteable()){
			File inputFile = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes/backup/DB_Diabetes");
			if (inputFile.exists()) {
				return true;
			}
		}
		return false;
	}

	public static boolean restoreBackup(Context context) {
		if (isSDWriteable()) {
			File inputFile = new File(Environment.getExternalStorageDirectory()
					+ "/MyDiabetes/backup/DB_Diabetes");

			File outputDir = new File(Environment.getDataDirectory() + "/data/" + context.getPackageName() + "/databases");
			outputDir.mkdirs();
			if (inputFile.exists()) {
				File fileBackup = new File(outputDir, "DB_Diabetes");
				try {
					fileBackup.createNewFile();
					copyFile(inputFile, fileBackup);
					return true;
				} catch (IOException ioException) {
					return false;
				} catch (Exception exception) {
					return false;
				}
			}
		}
		return false;
	}

	private static void copyFile(File src, File dst) throws IOException {
		FileChannel inChannel = new FileInputStream(src).getChannel();
		FileChannel outChannel = new FileOutputStream(dst).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} finally {
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
	}

	public static boolean backup(Context context) {
		if (DB_BackupRestoreFragment.isSDWriteable()) {
			File inputFile = DbUtils.exportDb(context);

			File outputDir = new File(Environment.getExternalStorageDirectory()
					+ "/MyDiabetes/backup");
			outputDir.mkdirs();
			if (inputFile.exists()) {
				File fileBackup = new File(outputDir, "DB_Diabetes");
				try {
					fileBackup.createNewFile();
					copyFile(inputFile, fileBackup);
					return true;
				} catch (Exception exception) {
					return false;
				}
			}
		} else {
			return false;
		}
		return false;
	}
}
