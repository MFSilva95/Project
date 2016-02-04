package pt.it.porto.mydiabetes.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;

import pt.it.porto.mydiabetes.R;

public class NewFeatureDialog extends DialogFragment {

	ActivateFeatureDialogListener listener;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(inflater.inflate(R.layout.dialog_new_feature_iob, null))
				// Add action buttons
				.setPositiveButton(R.string.use, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						// activate feature
						if(listener!=null){
							listener.useFeature();
						}
						dismiss();
					}
				})
				.setNegativeButton(R.string.use_not, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if(listener!=null){
							listener.notUseFeature();
						}
						dismiss();
					}
				});
		setCancelable(false);
//		builder.setTitle("Nova funcionalidade");
		return builder.create();
	}

	public void setListener(ActivateFeatureDialogListener listener) {
		this.listener = listener;
	}

	public interface ActivateFeatureDialogListener{
		void useFeature();

		void notUseFeature();
	}
}
