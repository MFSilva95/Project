package pt.it.porto.mydiabetes.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.sync.ServerSync;

public class FeatureWebSyncDialog extends DialogFragment {

	ActivateFeatureDialogListener listener;
	Context context;
	Dialog currentShowingDialog;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		this.context = getActivity();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = LayoutInflater.from(context);

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(inflater.inflate(R.layout.dialog_new_feature_websync, null))
			   // Add action buttons
			   .setPositiveButton(R.string.use, new DialogInterface.OnClickListener() {
				   @Override
				   public void onClick(DialogInterface dialog, int id) {
					   // activate feature
					   getUserDataPopUp(context).show();
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

	public Dialog getUserDataPopUp(final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		LayoutInflater inflater = LayoutInflater.from(context);
		builder.setView(inflater.inflate(R.layout.dialog_account_input, null))
			   .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				   @Override
				   public void onClick(DialogInterface dialog, int which) {
					   String username = ((EditText) ((Dialog) dialog).findViewById(R.id.username)).getText().toString();
					   String password = ((EditText) ((Dialog) dialog).findViewById(R.id.password)).getText().toString();
					   pt.it.porto.mydiabetes.database.Preferences.saveCloudSyncCredentials(((Dialog) dialog).getContext(), username, password);
					   testCredentials(context).show();
				   }
			   })
			   .setNegativeButton(android.R.string.cancel, null);
		return builder.create();
	}

	public Dialog testCredentials(final Context context) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		ServerSync.getInstance(context).testCredentials(new ServerSync.ServerSyncListener() {
			@Override
			public void onSyncSuccessful() {
				currentShowingDialog.dismiss();
			}

			@Override
			public void onSyncUnSuccessful() {
				((Activity) context).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						currentShowingDialog.dismiss();
						showLoginError();
					}
				});
			}
		});
		progressDialog.setCancelable(false);
		currentShowingDialog = progressDialog;
		return progressDialog;
	}

	private void showLoginError() {
		Dialog userDataPopUp = getUserDataPopUp(context);
		userDataPopUp.show();
		TextView errorText = ((TextView) userDataPopUp.findViewById(R.id.error));
		errorText.setVisibility(View.VISIBLE);
		currentShowingDialog = userDataPopUp;
	}


	public void setListener(ActivateFeatureDialogListener listener) {
		this.listener = listener;
	}

	public interface ActivateFeatureDialogListener {
		void useFeature();

		void notUseFeature();
	}
}
