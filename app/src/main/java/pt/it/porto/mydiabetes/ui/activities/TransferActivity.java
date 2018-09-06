package pt.it.porto.mydiabetes.ui.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.sync.transfer.FileInfo;
import pt.it.porto.mydiabetes.sync.transfer.Stream;
import pt.it.porto.mydiabetes.sync.transfer.Stream.StreamBinder;
import pt.it.porto.mydiabetes.sync.transfer.Transmission;

public class TransferActivity extends BaseActivity {
	private String host = "";
	private int port = 5444;
	private byte[] key;
	private byte[] iv;
	private Intent starter;
	private FileInfo fi;
	private Boolean onPC = null;
	private Hashtable<String, FileInfo> htFileInfo = new Hashtable<>();
	AlertDialog dialog1;

	// Bound Service
	private Stream mService;
	private boolean mBound = false;

	ImageButton lock;

	private Handler handler_lig = new Handler() {
		public void handleMessage(Message message) {
			if (message.arg1 == RESULT_OK) {
				showDialogTransf(true);
			} 
			else {
				showDialogTransf(false);
			}
		}
	};
	
	private Handler handler1 = new Handler() {
		public void handleMessage(Message message) {
			if (message.arg2 == 5) {
				showDialogProg();
			} 
			else {
				showDialogProg();
			}
		}
	};

	private Handler handler_transf = new Handler() {
		public void handleMessage(Message message) {
			if (message.arg1 == RESULT_OK) {
				showDialogFinal(true);
			} else {
				showDialogFinal(false);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transfer);
		// Show the Up button in the action bar.
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		ActionBar actionBar=getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		findViewById(R.id.checkedTextView1).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((CheckedTextView)v).toggle();
			}
		});
		starter = getIntent();
		Bundle extras = starter.getExtras();
		host = extras.getString("host");
		key = extras.getByteArray("key").clone();
		iv = extras.getByteArray("iv").clone();
		onPC = extras.getBoolean("onPC");
		showDialogLig();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.transfer, menu);
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		syncFileStructure(null);

	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}


	/**
	 * Button transferir fetches the DB_Diabetes from the data directory
	 * an gets the images selected
	 * @param view
	 */
	public void transfer(View view) {
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}

		Intent intent = new Intent(this, Stream.class);
		Messenger messenger = new Messenger(handler_transf);
		
		Messenger progress = new Messenger(handler1);
		intent.putExtra("PROGRESS", progress);
		
		intent.putExtra("MESSENGER", messenger);
		intent.putExtra("host", host);
		intent.putExtra("port", port);
		intent.putExtra("key", key);
		intent.putExtra("iv", iv);
		intent.putExtra("cmd", Transmission.PUT_CONTENTS);
		ArrayList<FileInfo> alfi = getSelectFiles((LinearLayout) findViewById(R.id.llFiles));
		FileInfo fi = new FileInfo(getApplicationInfo().dataDir + "/databases",
				"/DB_Diabetes");
		alfi.add(0, fi); // ensure the database is the first file to send
		intent.putExtra("files", alfi);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		//Messenger messenger1 = new Messenger(handler3);


	}





	private ArrayList<FileInfo> getSelectFiles(LinearLayout ll) {
		ArrayList<FileInfo> fil = new ArrayList<>();
		LinearLayout ll2;
		CheckedTextView cb;
		TextView tv;
		FileInfo fi;
		for (int i = 1; i < ll.getChildCount(); i++) {
			if (ll.getChildAt(i) instanceof LinearLayout) {
				fil.addAll(getSelectFiles((LinearLayout) ll.getChildAt(i)));
			} else {
				cb = (CheckedTextView) ll.getChildAt(i);
				if (cb == null)
					System.out.println("CheckBox is null");
				else if (cb.isChecked()) {
					fi = htFileInfo.get(cb.getText().toString());
					if (fi != null && !fi.isDir() && fi.checkFileExists()) {
						fil.add(fi);
					}
				}
			}
		}
		return fil;
	}


	/**
	 * Button refresh, call method addFiles to put images from FileInfo in ll
	 */
	public void button2Click() {
		if (mBound) {
			if ((this.fi = mService.getFIServer()) == null) {
				System.out.println("Ainda não obteve fi do Servidor");
			} else {
				LinearLayout ll = (LinearLayout) findViewById(R.id.llFiles);
				if (ll != null && ll.getChildCount() > 1) {
					try {
						System.out.println("Entrou if button2Click --->"+ll.getChildCount());
						ll.removeViews(1, ll.getChildCount());
						htFileInfo.clear();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				System.out.println("FI antes do addFiles -->"+mService.getFIServer().getAbsolutePath());
				addFiles(ll, mService.getFIServer());

			}
			unbindService(mConnection);
			mBound = false;
		} else {
			System.out.println("Serviço não ligado");
		}

	}


	/**
	 * Usado por button2click
	 * @param ll
	 * @param fi
	 */
	public void addFiles(LinearLayout ll, FileInfo fi) {
		FileInfo fi2 = new FileInfo(fi.getRelativePath());
		fi2.update();
		if (fi2.exists() && fi2.getFileList() != null) {
			addFiles2(ll, fi2);
		}
	}

	public void addFiles2(LinearLayout ll, FileInfo fi) {
		LinearLayout ll2;
		String fileName;

		for (FileInfo fiChild : fi.getFileList()) {

			if (!htFileInfo.containsKey(fiChild.getName())) {

				if (fiChild.getName().contains(".jpg")){
					CheckedTextView layout2= (CheckedTextView) getLayoutInflater().inflate(R.layout.list_item_tranfere_activity, ll, false);
					layout2.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							((CheckedTextView)v).toggle();
						}
					});
					fileName = fiChild.getName();
					System.out.println("Nome do ficheiro --->"+fiChild.getName());
					htFileInfo.put(fileName, fiChild);
					layout2.setText(fileName);
					ll.addView(layout2);
				}
			}

			if (fiChild.isDir()) {
				ll2 = new LinearLayout(this);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.setMargins(30, 0, 0, 0);
				ll2.setLayoutParams(lp);

				addFiles2(ll2, fiChild);
				ll.addView(ll2);
			}
		}
	}

	public void selectAll(View view) {
		selectAll2((LinearLayout) findViewById(R.id.llFiles));
	}

	public void selectAll2(LinearLayout ll) {
		try {
			for (int i = 1; i < ll.getChildCount(); i++) {
				if (ll.getChildAt(i) instanceof LinearLayout) {
					selectAll2((LinearLayout) ll.getChildAt(i));
				} else if (ll.getChildAt(i) instanceof CheckedTextView) {
					((CheckedTextView) ll.getChildAt(i)).setChecked(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void syncServer() {
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
		// TODO PASSAR esta funcao para SyncServerActivity
		byte b = 15;
		Intent intent = new Intent(this, Stream.class);
		Messenger messenger = new Messenger(handler_transf);
		intent.putExtra("MESSENGER", messenger);
		intent.putExtra("host", host);
		intent.putExtra("port", 5445);
		intent.putExtra("key", key);
		intent.putExtra("iv", iv);
		intent.putExtra("cmd", b);
		intent.putExtra("file", Environment.getExternalStorageDirectory()
				+ "/MyDiabetes");
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	public void syncFileStructure(View view) {
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}

		Intent intent = new Intent(this, Stream.class);
		Messenger messenger = new Messenger(handler_lig);
		
		
		intent.putExtra("MESSENGER", messenger);
		intent.putExtra("host", host);
		intent.putExtra("port", port);
		intent.putExtra("key", key);
		intent.putExtra("iv", iv);
		intent.putExtra("cmd", Transmission.GET_INFO);
		intent.putExtra("file", Environment.getExternalStorageDirectory()
				+ "/MyDiabetes");
		//		if (mBound) {
		//			unbindService(mConnection);
		//			mBound = false;
		//		}
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			StreamBinder binder = (StreamBinder) service;
			mService = binder.getService();
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

	/**
	 * Dialogo de espera
	 */
	public void showDialogLig(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		builder.setTitle(R.string.transf_WaitTitle).
		setMessage(this.getString(R.string.transf_WaitMesg));
		dialog1 = builder.create();
		dialog1.show();
		dialog1.setCanceledOnTouchOutside(false);
	}


	/**
	 * Diálogo para a ligação
	 * @param success
	 */
	public void showDialogTransf(boolean success){
		if(isFinishing()){
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(this.getString(R.string.transf_InfoTitle));
		
		if (success){
			builder.setMessage(this.getString(R.string.transf_ConnOK));
			builder.setPositiveButton(getString(R.string.okButton), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog1.dismiss();
					button2Click();
				}
			}).show();
		} else {
			builder.setMessage(getString(R.string.transfer_fail_connection )+ "\n"
					+ getString(R.string.transfer_check_wifi ) + "\n"
					+ getString(R.string.transfer_correct_network));
			builder.setPositiveButton(getString(R.string.okButton), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Intent intent = new Intent(TransferActivity.this, SettingsImportExport.class);
					startActivity(intent);
				}
			}).show();
		}

	}
	
	/**
	 * Dialogo progresso transfeencia
	 */
	public void showDialogProg(){
		if(isFinishing()){
			return;
		}
		ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage(getString(R.string.transfer_transfer));
		pd.setCanceledOnTouchOutside(false);
		pd.show();
	}
	
	public void showDialogFinal(boolean d){
		final Context c = this;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(this.getString(R.string.transf_InfoTitle));
		if (d){
		builder.setMessage(getString(R.string.transfer_success))
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				Intent intent = new Intent(c, SettingsImportExport.class);
				NavUtils.navigateUpTo(TransferActivity.this, intent);
				finish();
			}
		}).show();
		}
		else {
			//TODO set in string
			builder.setMessage(getString(R.string.error_transfer)+"\n"
					+getString(R.string.transfer_try_again) )
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(c, SettingsImportExport.class);
					startActivity(intent);
					
				}
			}).show();
		}
		
	}
	
	
}
