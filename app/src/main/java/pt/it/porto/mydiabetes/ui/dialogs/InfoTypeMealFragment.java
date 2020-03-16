package pt.it.porto.mydiabetes.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import pt.it.porto.mydiabetes.R;

public class InfoTypeMealFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(getActivity().getSupportFragmentManager().findFragmentByTag("InfoMeal")!=null) {
            builder.setTitle(R.string.infomealtitle)
                    .setMessage(R.string.infomealmessage)
                    .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //dismiss and close the dialog
                        }
                    });
            return builder.create();
        }
          // return super.onCreateDialog(savedInstanceState);
        return null;
    }
}
