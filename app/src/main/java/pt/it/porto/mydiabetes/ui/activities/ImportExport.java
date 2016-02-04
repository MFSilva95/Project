package pt.it.porto.mydiabetes.ui.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;

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

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.sync.crypt.KeyGenerator;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.fragments.DB_BackupRestore;
import pt.it.porto.mydiabetes.ui.fragments.PdfExport;
import pt.it.porto.mydiabetes.ui.fragments.Sync;
import pt.it.porto.mydiabetes.ui.listAdapters.BloodPressureDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.CarbsDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.CholesterolDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.DiseaseRegDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.ExerciseRegDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.GlycemiaDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.HbA1cDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.InsulinRegDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.WeightDataBinding;

public class ImportExport extends BaseOldActivity {

	public static final String BACKUP_LOCATION = "/MyDiabetes/backup/DB_Diabetes";
	public static final String PROJECT_MANAGER_EMAIL = "mydiabetes@dcc.fc.up.pt";
	public static final String EXTRAS_TAB = "tab";
	public static final int EXTRAS_TAB_SYNC = 0;
	public static final int EXTRAS_TAB_IMPORT_EXPORT = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import_export);
		// Show the Up button in the action bar.
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.Tab tab = actionBar.newTab();
		Fragment syncFragment = new Sync();
		tab.setTabListener(new MyTabsListener(syncFragment));
		tab.setText(R.string.sync);
		actionBar.addTab(tab);

		tab = actionBar.newTab();
		Fragment bacuprestoreFragment = new DB_BackupRestore();
		tab.setTabListener(new MyTabsListener(bacuprestoreFragment));
		tab.setText(R.string.backup);
		actionBar.addTab(tab);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			int tabToFocus = extras.getInt(EXTRAS_TAB, EXTRAS_TAB_SYNC);
			actionBar.selectTab(actionBar.getTabAt(tabToFocus));
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.import_export, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				Intent upIntent = NavUtils.getParentActivityIntent(this);
				if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
					// This activity is NOT part of this app's task, so create a new task
					// when navigating up, with a synthesized back stack.
					TaskStackBuilder.create(this)
							// Add all of this activity's parents to the back stack
							.addNextIntentWithParentStack(upIntent)
							// Navigate up to the closest parent
							.startActivities();
				} else {
					// This activity is part of this app's task, so simply
					// navigate up to the logical parent activity.
					NavUtils.navigateUpTo(this, upIntent);
				}
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void backup(View v) {
		if (isSDWriteable()) {
			File inputFile = new File(Environment.getDataDirectory() + "/data/"
					+ this.getPackageName() + "/databases/DB_Diabetes");

			File outputDir = new File(Environment.getExternalStorageDirectory()
					+ "/MyDiabetes/backup");
			outputDir.mkdirs();
			if (inputFile.exists()) {
				File fileBackup = new File(outputDir, "DB_Diabetes");
				try {
					fileBackup.createNewFile();
					copyFile(inputFile, fileBackup);
					ShowDialogMsg(getString(R.string.dbcopy_success));
				} catch (IOException ioException) {
					ShowDialogMsg(getString(R.string.dbcopy_error));
				} catch (Exception exception) {
					ShowDialogMsg(getString(R.string.dbcopy_error));
				}
			}
		}
	}

	public void restore(View v) {
		if (isSDWriteable()) {
			File inputFile = new File(Environment.getExternalStorageDirectory()
					+ "/MyDiabetes/backup/DB_Diabetes");

			File outputDir = new File(Environment.getDataDirectory() + "/data/"
					+ this.getPackageName() + "/databases");
			outputDir.mkdirs();
			if (inputFile.exists()) {
				File fileBackup = new File(outputDir, "DB_Diabetes");
				try {
					fileBackup.createNewFile();
					copyFile(inputFile, fileBackup);
					ShowDialogMsg("Restauro efectuado com sucesso!");
				} catch (IOException ioException) {
					ShowDialogMsg("Ocurreu um erro durante o restauro, verifique se a memória externa está disponivel!");
				} catch (Exception exception) {
					ShowDialogMsg("Ocurreu um erro durante o restauro, verifique se a memória externa está disponivel!");
				}
			}
		}
	}

	private boolean isSDWriteable() {
		boolean rc = false;

		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			rc = true;
		}

		return rc;
	}

	@SuppressWarnings("resource")
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

	public void ShowDialogMsg(String msg) {
		// final Context c = this;
		new AlertDialog.Builder(this).setTitle("Informação").setMessage(msg)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB
						// Rever porque não elimina o registo de glicemia
						fillBackup();
					}
				}).show();
	}

	@SuppressLint("SimpleDateFormat")
	public boolean fillBackup() {
		if (isSDWriteable()) {
			File inputFile = new File(Environment.getExternalStorageDirectory() + BACKUP_LOCATION);
			if (inputFile.exists()) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(inputFile.lastModified());
				Date newDate = cal.getTime();
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
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
		Intent intent = new Intent(this, TransferActivity.class);
		intent.putExtra("host", "192.168.1.44");
		byte[] key = new KeyGenerator().generateKey();
		byte[] iv = new KeyGenerator().generateKey();
		intent.putExtra("key", key);
		intent.putExtra("iv", iv);
		intent.putExtra("onPC", false);
		startActivity(intent);
	}

	public void share(View view) {
		File backupFile = new File(Environment.getExternalStorageDirectory() + BACKUP_LOCATION);
		if (backupFile.exists()) {
			backupFile.setReadable(true, false); // making sure that other apps can read the file
		} else {
			return;
		}
		DB_Read read = new DB_Read(this);
		String patientName = (String) read.MyData_Read()[1];
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

	@SuppressWarnings("deprecation")
	class MyTabsListener implements ActionBar.TabListener {
		public Fragment fragment;

		public MyTabsListener(Fragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
			ft.replace(R.id.importexport_FragmentContainer, fragment);
		}

		@Override
		public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
		}
	}
}
