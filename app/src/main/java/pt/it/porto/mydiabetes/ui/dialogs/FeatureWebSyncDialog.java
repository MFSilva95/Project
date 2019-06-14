package pt.it.porto.mydiabetes.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.Preferences;
import pt.it.porto.mydiabetes.sync.ServerSync;
import pt.it.porto.mydiabetes.ui.activities.MyDiabetesWebViewActivity;
import pt.it.porto.mydiabetes.ui.fragments.home.homeMiddleFragment;
import pt.it.porto.mydiabetes.ui.fragments.home.homeRightFragment;

public class FeatureWebSyncDialog extends DialogFragment {

	ActivateFeatureDialogListener listener;
	Context context;
	Dialog currentShowingDialog;
	String username;
	String password;
	final private int WEBVIEW = 332;
	View v;


	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		System.out.println("THIS CONTEXT: "+getActivity());
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = LayoutInflater.from(context);

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		v = inflater.inflate(R.layout.dialog_new_feature_websync, null);
		builder.setView(v)
			   // Add action buttons
			   .setPositiveButton(R.string.use, new DialogInterface.OnClickListener() {
				   @Override
				   public void onClick(DialogInterface dialog, int id) {
					   // activate feature
					   getUserDataPopUp(context, -1, -1).show();
				   }
			   }).setNegativeButton(R.string.use_not, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				if (listener != null) {
					listener.notUseFeature();
				}
				dismiss();
			}
		});
		setCancelable(false);
		return builder.create();
	}


	/**
	 * LOGIN ONE
	 * @param context
	 * @param okButtonMessage
	 * @param cancelButtonMessage
	 * @return
	 */
	public Dialog getUserDataPopUp(final Context context, int okButtonMessage, int cancelButtonMessage) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		password = Preferences.getPassword(context);
		username = Preferences.getUsername(context);

		LayoutInflater inflater = LayoutInflater.from(context);
		builder.setView(inflater.inflate(R.layout.dialog_account_input, null))
				.setPositiveButton(okButtonMessage > 0 ? okButtonMessage : android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					    username = ((EditText) ((Dialog) dialog).findViewById(R.id.username)).getText().toString();
					    password = ((EditText) ((Dialog) dialog).findViewById(R.id.password)).getText().toString();

					   AsyncTask<Void, Void, Void> saveTask = new AsyncTask<Void, Void, Void>() {
						   @Override
						   protected Void doInBackground(Void... params) {
							   pt.it.porto.mydiabetes.database.Preferences.saveCloudSyncCredentials(context, username, password);
							   return null;
						   }

						   @Override
						   protected void onPostExecute(Void aVoid) {
							   super.onPostExecute(aVoid);
							   testCredentials(FeatureWebSyncDialog.this.context).show();
						   }
					   };
					   saveTask.execute();
				   }
			   })
				.setNegativeButton(cancelButtonMessage > 0 ? cancelButtonMessage : android.R.string.cancel, null);

		Dialog dialog = builder.create();
		currentShowingDialog = dialog;
		dialog.show();

		dialog.findViewById(R.id.webViewButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//Intent intent = new Intent(view.getContext(), MyDiabetesWebViewActivity.class);
				Intent intent = new Intent(context, MyDiabetesWebViewActivity.class);
				//((Activity) view.getContext()).startActivityForResult(intent, WEBVIEW);
				((Activity) context).startActivityForResult(intent, WEBVIEW);
				//((Activity) FeatureWebSyncDialog.this.context).startActivityForResult(intent, WEBVIEW);
			}
		});
		((EditText) dialog.findViewById(R.id.username)).setText(username);
		((EditText) dialog.findViewById(R.id.password)).setText(password);
		return dialog;
	}

    public Dialog getRankUserDataPopUp(final Context context, int okButtonMessage, int cancelButtonMessage) {
		this.context = context;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        password = Preferences.getPassword(context);
        username = Preferences.getUsername(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        builder.setView(inflater.inflate(R.layout.dialog_account_input, null))
                .setPositiveButton(okButtonMessage > 0 ? okButtonMessage : android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        username = ((EditText) ((Dialog) dialog).findViewById(R.id.username)).getText().toString();
                        password = ((EditText) ((Dialog) dialog).findViewById(R.id.password)).getText().toString();

                        AsyncTask<Void, Void, Void> saveTask = new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                pt.it.porto.mydiabetes.database.Preferences.saveCloudSyncCredentials(context, username, password);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                testCredentials(FeatureWebSyncDialog.this.context).show();
                            }
                        };
                        saveTask.execute();
                    }
                })
                .setNegativeButton(cancelButtonMessage > 0 ? cancelButtonMessage : android.R.string.cancel, null);

        Dialog dialog = builder.create();
        currentShowingDialog = dialog;
        dialog.show();

        dialog.findViewById(R.id.webViewButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(view.getContext(), MyDiabetesWebViewActivity.class);
                Intent intent = new Intent(context, MyDiabetesWebViewActivity.class);
                //((Activity) view.getContext()).startActivityForResult(intent, WEBVIEW);
                ((Activity) context).startActivityForResult(intent, WEBVIEW);
                //((Activity) FeatureWebSyncDialog.this.context).startActivityForResult(intent, WEBVIEW);
            }
        });

        ((EditText) dialog.findViewById(R.id.username)).setText(username);
        ((EditText) dialog.findViewById(R.id.password)).setText(password);
        return dialog;
    }

	public Dialog testCredentials(final Context context){
		ProgressDialog progressDialog = new ProgressDialog(context);
		try {
			ServerSync.getInstance(context).testCredentials(new ServerSync.ServerSyncListener() {
				@Override
				public void onSyncSuccessful() {
					currentShowingDialog.dismiss();
				}

				@Override
				public void onSyncUnSuccessful() {
					((Activity) FeatureWebSyncDialog.this.context).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							currentShowingDialog.dismiss();
							showLoginError();
						}
					});
				}

				@Override
				public void noNetworkAvailable() {
					((Activity) FeatureWebSyncDialog.this.context).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							currentShowingDialog.dismiss();
							showNoNetwork();
						}
					});
				}
			});
		} catch (Exception e) {
			showError();
		}
		progressDialog.setCancelable(false);
		currentShowingDialog = progressDialog;
		return progressDialog;
	}

	private void showLoginError() {
		Dialog userDataPopUp = getUserDataPopUp(context, -1, -1);
		userDataPopUp.show();
		TextView errorText = ((TextView) userDataPopUp.findViewById(R.id.error));
		errorText.setVisibility(View.VISIBLE);
		currentShowingDialog = userDataPopUp;
	}

	private void showError() {
		Dialog userDataPopUp = getUserDataPopUp(context, R.string.login_try_again, R.string.login_try_later);
		userDataPopUp.show();
		TextView errorText = ((TextView) userDataPopUp.findViewById(R.id.error));
		errorText.setVisibility(View.VISIBLE);
		errorText.setText(R.string.error_could_not_send_data);
		currentShowingDialog = userDataPopUp;
		currentShowingDialog.setCancelable(true);
	}

	private void showNoNetwork() {
		Dialog userDataPopUp = getUserDataPopUp(context, R.string.login_try_again, R.string.login_try_later);
		userDataPopUp.show();
		TextView errorText = ((TextView) userDataPopUp.findViewById(R.id.error));
		errorText.setVisibility(View.VISIBLE);
		errorText.setText(R.string.error_login_no_internet);
		currentShowingDialog = userDataPopUp;
		currentShowingDialog.setCancelable(true);
	}


	public void setListener(ActivateFeatureDialogListener listener) {
		this.listener = listener;
	}

	public interface ActivateFeatureDialogListener {
		void useFeature();

		void notUseFeature();
	}
}
