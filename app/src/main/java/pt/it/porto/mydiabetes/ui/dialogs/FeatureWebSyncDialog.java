package pt.it.porto.mydiabetes.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.EditText;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.activities.Preferences;

public class FeatureWebSyncDialog extends DialogFragment {

	ActivateFeatureDialogListener listener;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(inflater.inflate(R.layout.dialog_new_feature_websync, null))
				// Add action buttons
				.setPositiveButton(R.string.use, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						// activate feature
						getUserDataPopUp().show();
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

	public Dialog getUserDataPopUp() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = getActivity().getLayoutInflater();
		builder.setView(inflater.inflate(R.layout.dialog_account_input, null)).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String username = ((EditText) ((Dialog) dialog).findViewById(R.id.username)).getText().toString();
				String password = ((EditText) ((Dialog) dialog).findViewById(R.id.password)).getText().toString();
				pt.it.porto.mydiabetes.database.Preferences.saveCloudSyncCredentials(((Dialog) dialog).getContext(), username, password);
			}
		}).setNegativeButton(android.R.string.cancel, null);
		return builder.create();
	}


	public void setListener(ActivateFeatureDialogListener listener) {
		this.listener = listener;
	}

	public interface ActivateFeatureDialogListener {
		void useFeature();

		void notUseFeature();
	}
}
