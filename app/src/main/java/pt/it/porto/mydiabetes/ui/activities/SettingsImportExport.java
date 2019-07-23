package pt.it.porto.mydiabetes.ui.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;


import com.cdev.achievementview.AchievementView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pt.it.porto.mydiabetes.BuildConfig;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Handler;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.sync.ServerSync;
import pt.it.porto.mydiabetes.ui.dialogs.RankWebSyncDialog;
import pt.it.porto.mydiabetes.utils.BadgeUtils;
import pt.it.porto.mydiabetes.utils.DbUtils;

public class SettingsImportExport extends BaseActivity {

	public static final String BACKUP_LOCATION = "/MyDiabetes/backup/DB_Diabetes";
	public static final String PROJECT_MANAGER_EMAIL = "mydiabetes@dcc.fc.up.pt";

	private AchievementView achievementView;
	private Context context;

	final private int WEBVIEW = 332;

	@Nullable
	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings_report);
		context = this;
		// Show the Up button in the action bar.
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		ActionBar actionBar=getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		if(!BuildConfig.SYNC_AVAILABLE){
			findViewById(R.id.sync).setVisibility(View.GONE);
		}
		String username = pt.it.porto.mydiabetes.database.Preferences.getUsername(this);
		if(username!=null && !username.equals("")){
		    findViewById(R.id.syncCloud).setVisibility(View.VISIBLE);
        }

		if(isSDWriteable()){
			File inputFile = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes/backup/DB_Diabetes");
			if (inputFile.exists()) {
				SQLiteDatabase db = SQLiteDatabase.openDatabase(inputFile.getPath(), null, 0);
				if(!isDeprecated(this, db)){
					db.close();
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(inputFile.lastModified());
					Date newDate = cal.getTime();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String dateString = formatter.format(newDate);

					TextView lastbackup = (TextView) findViewById(R.id.tv_lastBackup);
					lastbackup.setText(dateString);

					Button restore = (Button) findViewById(R.id.bt_Restore);
					restore.setEnabled(true);
					//findViewById(R.id.share).setEnabled(true);
					restore.setVisibility(View.VISIBLE);
					//findViewById(R.id.share).setVisibility(View.VISIBLE);

				}else{
					db.close();
					Button restore = (Button) findViewById(R.id.bt_Restore);
					restore.setEnabled(false);
					//findViewById(R.id.share).setEnabled(false);
					restore.setVisibility(View.GONE);
					//findViewById(R.id.share).setVisibility(View.GONE);
				}
			} else {
				Button restore = (Button) findViewById(R.id.bt_Restore);
				restore.setEnabled(false);
				//findViewById(R.id.share).setEnabled(false);
				restore.setVisibility(View.GONE);
				//findViewById(R.id.share).setVisibility(View.GONE);
			}
		}else{
			Button restore = (Button) findViewById(R.id.bt_Restore);
			restore.setEnabled(false);
			//findViewById(R.id.share).setEnabled(false);
			restore.setVisibility(View.GONE);
			//findViewById(R.id.share).setVisibility(View.GONE);
		}

		achievementView = findViewById(R.id.achievement_view);
	}


	public static boolean isSDWriteable(){
		boolean rc = false;

		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			rc = true;
		}
		return rc;
	}

	public static boolean hasBackup(Context c){
		if(isSDWriteable()){
			File inputFile = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes/backup/DB_Diabetes");
			if (inputFile.exists()) {
                SQLiteDatabase db;
			    try{
			        db = SQLiteDatabase.openDatabase(inputFile.getPath(), null, 0);
                    if (isDeprecated(c, db)) {
                    	db.close();
                        return false;
                    }else{
                    	db.close();
                    	return true;
					}
                }catch (Exception e) {
                    return false;
                }
			}
		}
		return false;
	}


	public static Boolean isDeprecated(Context c, SQLiteDatabase db){

		DB_Handler handler = new DB_Handler(c);
		Boolean deprecated = handler.isDBdeprecated(db);
		handler.close();
		return deprecated;
	}


	public static boolean restoreBackup(Context context){
		if (isSDWriteable()) {
			File inputFile = new File(Environment.getExternalStorageDirectory()
					+ "/MyDiabetes/backup/DB_Diabetes");
			if(inputFile.exists()){
				SQLiteDatabase db = SQLiteDatabase.openDatabase(inputFile.getPath(), null, 0);
				boolean isDeprecated = isDeprecated(context, db);
                db.close();
				if(!isDeprecated){
					try {
						File outputDir = new File(Environment.getDataDirectory() + "/data/" + context.getPackageName() + "/databases");
						outputDir.mkdirs();

						File fileBackup = new File(outputDir, "DB_Diabetes");
						if(fileBackup.exists()){//overwrite
                            copyFile(inputFile, fileBackup);
                            return true;
						}else{
                            fileBackup.createNewFile();
                            copyFile(inputFile, fileBackup);
                            return true;
                        }
					} catch (Exception e) {
						return false;
					}
				}else{
				    return false;
                }
			}else{
			    return false;
            }
		}
		return false;
	}

	private static void copyFile(File src, File dst) throws IOException {
		FileChannel inChannel = new FileInputStream(src).getChannel();
		FileOutputStream outStream = new FileOutputStream(dst);
		FileChannel outChannel = outStream.getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		}catch (Exception e){
            Log.i("COPY_FILE", "copyFile: ERROR!!!");
		    e.printStackTrace();
		}finally {
		    outStream.close();
			outStream.flush();
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
	}

	public static File backup(Context context, Boolean to_export)throws Exception {
		if (isSDWriteable()) {
			File inputFile = null;
			inputFile = DbUtils.get_database_file(context);
            //make backup dirs
            File outputDir = new File(Environment.getExternalStorageDirectory()
                    + "/MyDiabetes/backup");
            outputDir.mkdirs();
			if(inputFile!=null){
				if (inputFile.exists()) {

                    File fileBackup;
                    if(to_export){
                        fileBackup = new File(outputDir, "DB_Diabetes_to_export");
                    }else{
                        fileBackup = new File(outputDir, "DB_Diabetes");
                    }

					if(fileBackup.exists()){
                        copyFile(inputFile, fileBackup);
                    }else{
                        fileBackup.createNewFile();
                        copyFile(inputFile, fileBackup);
                    }

                    if(to_export){
                        fileBackup = get_anonymized_db(fileBackup);
                    }

                    return fileBackup;
				}
			}
		}
        throw new Exception();

	}


    public static File get_anonymized_db(File dbFile){
        SQLiteDatabase clean_db = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null, 0);

        Cursor cursor = clean_db.rawQuery("SELECT BDate FROM UserInfo", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String bdate = cursor.getString(0);
            cursor.close();

            String[] birthday = bdate.split("-");
            String byear = birthday[2];

            ContentValues toUpdate = new ContentValues();
            toUpdate.put("Name", "");
            toUpdate.put("Gender", -1);
            toUpdate.put("BDate", "00-00-"+byear);

            clean_db.update("UserInfo", toUpdate, null, null);
        }
        clean_db.close();
        return dbFile;
    }

	public void backupButton(View v) {
        File backup_file;
        try {
            backup_file = backup(this.getBaseContext(),false);
        } catch (Exception e) {
            ShowDialogMsg(getString(R.string.error_dbcopy));
            return;
        }
        if (backup_file != null) {
			ShowDialogMsg(getString(R.string.dbcopy_success));
			DB_Read rdb = new DB_Read(v.getContext());
			boolean winBadge = BadgeUtils.addExportBadge(getBaseContext(), rdb);
			// send notification in case of badge win
			rdb.close();
            //just created backup - make restore available
            Button restore = (Button) findViewById(R.id.bt_Restore);
            restore.setEnabled(true);
            //findViewById(R.id.share).setEnabled(true);
            restore.setVisibility(View.VISIBLE);
			if (winBadge) {
				achievementView.show(this.getString(R.string.congratsMessage1), this.getString(R.string.backupBadgeWon));
			}
		} else {
			ShowDialogMsg(getString(R.string.error_dbcopy));
		}
	}

	public void restore(View v) {

		Dialog dialog = new AlertDialog.Builder(this).setTitle(R.string.restore_backup)
				.setMessage(R.string.backup_restore_confirmation_dialog_text)
				.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							if (restoreBackup(getApplicationContext())) {
								ShowDialogMsg(getString(R.string.restore_backup_success));
								fillBackup(SettingsImportExport.this);
							} else {
								ShowDialogMsg(getString(R.string.restore_backup_error));
							}
						} catch (Exception e) {
							ShowDialogMsg(getString(R.string.restore_backup_deprecated_db_error));
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
		new android.app.AlertDialog.Builder(this).setTitle(R.string.information).setMessage(msg).show();
	}

	public boolean fillBackup(Context c) {
		if (isSDWriteable()) {
			File inputFile = new File(Environment.getExternalStorageDirectory() + BACKUP_LOCATION);
			if (inputFile.exists()) {
				SQLiteDatabase db = SQLiteDatabase.openDatabase(inputFile.getPath(), null, 0);
				if(!isDeprecated(c,db)){
				    db.close();
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(inputFile.lastModified());
					Date newDate = cal.getTime();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String dateString = formatter.format(newDate);

					TextView lastbackup = (TextView) findViewById(R.id.tv_lastBackup);
					lastbackup.setText(dateString);
					Button restore = (Button) findViewById(R.id.bt_Restore);
					restore.setEnabled(true);
					//findViewById(R.id.share).setEnabled(true);
					return true;
				}else{
				    db.close();
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

//	public void syncPC(View view) {
//		Intent intent = new Intent(this, ScanActivity.class);
//		startActivity(intent);
//	}


//	@Override
//	protected void onResume() {
//		super.onResume();
//		String username = pt.it.porto.mydiabetes.database.Preferences.getUsername(this);
//		if(username!=null && !username.equals("")){
//			findViewById(R.id.syncCloud).setVisibility(View.VISIBLE);
//		}
//	}

    public void showUpload(){
        findViewById(R.id.syncCloud).setVisibility(View.VISIBLE);
    }
	public void hideUpload(){
		findViewById(R.id.syncCloud).setVisibility(View.GONE);
	}

	public void syncCloud(View view){
		if(BuildConfig.SYNC_AVAILABLE){

			String username = pt.it.porto.mydiabetes.database.Preferences.getUsername(this);
			if(username==null){
				editAccount(null);
				return;
			}
			dialog = new ProgressDialog(this);
			dialog.show();
            try {
                ServerSync.getInstance(this).send(new ServerSync.ServerSyncListener() {
                    @Override
                    public void onSyncSuccessful() {
                        if (dialog != null) {
							dialog.dismiss();
                        }
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ShowDialogMsg(getBaseContext().getString(R.string.upload_successful));
							}
						});
                    }

                    @Override
                    public void onSyncUnSuccessful() {
                        if (dialog != null) {
							dialog.dismiss();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
								editErrorAccount(context);
                            }
                        });
                    }

                    @Override
                    public void noNetworkAvailable() {
                        onSyncUnSuccessful();
                    }
                });
            } catch (Exception e) {
                if (dialog != null) {
					dialog.dismiss();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
						editErrorAccount(context);
                    }
                });
            }


        }

	}
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == WEBVIEW && resultCode == RESULT_OK) {
			if (webSyncDialog != null) {
				webSyncDialog.uploadData();
			}
		}

        super.onActivityResult(requestCode, resultCode, data);
    }


	RankWebSyncDialog webSyncDialog;//FeatureWebSyncDialog webSyncDialog;
	public void editAccount(View view) {
		webSyncDialog = new RankWebSyncDialog();//new FeatureWebSyncDialog();
		webSyncDialog.show(getSupportFragmentManager(), "editAccount");
		//webSyncDialog.dismiss();
		//webSyncDialog.getUserDataPopUp(this, -1, -1);
	}

	public void editErrorAccount(Context c) {
		webSyncDialog = new RankWebSyncDialog();//new FeatureWebSyncDialog();
		webSyncDialog.showError(c);
		//webSyncDialog.show(getSupportFragmentManager(), "editAccount");
		//webSyncDialog.dismiss();
		//webSyncDialog.getUserDataPopUp(this, -1, -1);
	}



}
