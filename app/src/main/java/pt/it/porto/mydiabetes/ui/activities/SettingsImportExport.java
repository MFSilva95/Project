package pt.it.porto.mydiabetes.ui.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pt.it.porto.mydiabetes.BuildConfig;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Handler;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.sync.ServerSync;
import pt.it.porto.mydiabetes.ui.dialogs.FeatureWebSyncDialog;
import pt.it.porto.mydiabetes.utils.BadgeUtils;
import pt.it.porto.mydiabetes.utils.DbUtils;

public class SettingsImportExport extends BaseActivity {

	public static final String BACKUP_LOCATION = "/MyDiabetes/backup/DB_Diabetes";
	public static final String PROJECT_MANAGER_EMAIL = "mydiabetes@dcc.fc.up.pt";

	@Nullable
	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_report);
		// Show the Up button in the action bar.
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		ActionBar actionBar=getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		if(!BuildConfig.SYNC_AVAILABLE){
			findViewById(R.id.sync).setVisibility(View.GONE);
		}

		if(isSDWriteable()){
			File inputFile = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes/backup/DB_Diabetes");
			if (inputFile.exists()) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(inputFile.lastModified());
				Date newDate = cal.getTime();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String dateString = formatter.format(newDate);

				TextView lastbackup = (TextView) findViewById(R.id.tv_lastBackup);
				lastbackup.setText(dateString);
			} else {
				Button restore = (Button) findViewById(R.id.bt_Restore);
				restore.setEnabled(false);
				findViewById(R.id.share).setEnabled(false);
			}
		}else{
			Button restore = (Button) findViewById(R.id.bt_Restore);
			restore.setEnabled(false);
			findViewById(R.id.share).setEnabled(false);
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
			if(inputFile.exists()){
				DB_Handler handler = new DB_Handler(context);
				handler.insertIntoDB(inputFile);
				return true;
			}
//
//			File outputDir = new File(Environment.getDataDirectory() + "/data/" + context.getPackageName() + "/databases");
//			outputDir.mkdirs();
//			if (inputFile.exists()) {
//				File fileBackup = new File(outputDir, "DB_Diabetes");
//				try {
//					fileBackup.createNewFile();
//					copyFile(inputFile, fileBackup);
//					return true;
//				} catch (IOException ioException) {
//					return false;
//				} catch (Exception exception) {
//					return false;
//				}
//			}
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
		if (isSDWriteable()) {
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

	public void backupButton(View v) {
		if (backup(getApplicationContext())) {
			ShowDialogMsg(getString(R.string.dbcopy_success));
			DB_Read rdb = new DB_Read(v.getContext());
			BadgeUtils.addExportBadge(getBaseContext(), rdb);
			rdb.close();
		} else {
			ShowDialogMsg(getString(R.string.dbcopy_error));
		}
	}

	public void restore(View v) {
		Dialog dialog = new AlertDialog.Builder(this).setTitle(R.string.restore_backup)
				.setMessage(R.string.backup_restore_confirmation_dialog_text)
				.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (restoreBackup(getApplicationContext())) {
							ShowDialogMsg(getString(R.string.restore_backup_success));
						} else {
							ShowDialogMsg(getString(R.string.restore_backup_error));
						}
					}
				})
				.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.create();
		dialog.show();
	}

	public void ShowDialogMsg(String msg) {
		// final Context c = this;
		new AlertDialog.Builder(this).setTitle("Informação").setMessage(msg).setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//Falta verificar se não está associada a nenhuma entrada da DB
				// Rever porque não elimina o registo de glicemia
				fillBackup();
			}
		}).show();
	}

	public boolean fillBackup() {
		if (isSDWriteable()) {
			File inputFile = new File(Environment.getExternalStorageDirectory() + BACKUP_LOCATION);
			if (inputFile.exists()) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(inputFile.lastModified());
				Date newDate = cal.getTime();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String dateString = formatter.format(newDate);

				TextView lastbackup = (TextView) findViewById(R.id.tv_lastBackup);
				lastbackup.setText(dateString);
				Button restore = (Button) findViewById(R.id.bt_Restore);
				restore.setEnabled(true);
				findViewById(R.id.share).setEnabled(true);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void syncPC(View view) {
		Intent intent = new Intent(this, ScanActivity.class);
		startActivity(intent);
	}

	public void syncCloud(View view) {
		String username = pt.it.porto.mydiabetes.database.Preferences.getUsername(this);
		if(username==null){
			editAccount(null);
			return;
		}
		dialog = new ProgressDialog(this);
		dialog.show();
		ServerSync.getInstance(this).send(new ServerSync.ServerSyncListener() {
			@Override
			public void onSyncSuccessful() {
				if (dialog != null) {
					dialog.hide();
				}
			}

			@Override
			public void onSyncUnSuccessful() {
				if (dialog != null) {
					dialog.hide();
				}
				Toast.makeText(getApplicationContext(), "Infelizmente falhou o envio, tente mais tarde.", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void noNetworkAvailable() {
				onSyncUnSuccessful();
			}
		});


	}

	public void share(View view) {
		backup(getApplicationContext());
		File backupFile = new File(Environment.getExternalStorageDirectory() + BACKUP_LOCATION);
		if (backupFile.exists()) {
			backupFile.setReadable(true, false); // making sure that other apps can read the file
		} else {
			return;
		}
		DB_Read read = new DB_Read(this);
		String patientName = read.MyData_Read().getUsername();
		Intent intent = ShareCompat.IntentBuilder.from(this)
				.setType("message/rfc822")
				.addEmailTo(PROJECT_MANAGER_EMAIL)
				.setSubject(String.format(getResources().getString(R.string.share_subject), patientName))
				.setText(getResources().getString(R.string.share_text))
				.setStream(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + BACKUP_LOCATION)))
				.getIntent();

		// get apps that resolve email
		Intent justEmailAppsIntent = new Intent(Intent.ACTION_SENDTO);
		justEmailAppsIntent.setType("text/plain");
		justEmailAppsIntent.setData(Uri.parse("mailto:"));
		List<ResolveInfo> activities = getPackageManager().queryIntentActivities(justEmailAppsIntent, 0);

		Intent[] extraIntents = new Intent[activities.size() - 1];
		for (int i = 0; i < activities.size() - 1; i++) {
			extraIntents[i] = (Intent) intent.clone();
			extraIntents[i].setClassName(activities.get(i).activityInfo.packageName, activities.get(i).activityInfo.name);
		}
		Intent one = (Intent) intent.clone();
		one.setClassName(activities.get(activities.size() - 1).activityInfo.packageName, activities.get(activities.size() - 1).activityInfo.name);

		Intent openInChooser = Intent.createChooser(one, null);
		openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);

		ComponentName activityResolved = openInChooser.resolveActivity(getPackageManager());
		if (activityResolved != null) {
			if (intent.resolveActivity(getPackageManager()) != null) {
				startActivity(openInChooser);
			}
		} else {
			Log.e("Share", "No email client found!");
			//TODO do something to show the error
		}
	}

	public void editAccount(View view) {
		FeatureWebSyncDialog webSyncDialog = new FeatureWebSyncDialog();
		webSyncDialog.show(getFragmentManager(), "editAccount");
		webSyncDialog.dismiss();
		webSyncDialog.getUserDataPopUp(this, -1, -1);
	}



}
