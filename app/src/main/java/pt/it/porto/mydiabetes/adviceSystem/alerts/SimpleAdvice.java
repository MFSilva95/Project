package pt.it.porto.mydiabetes.adviceSystem.alerts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class SimpleAdvice extends DialogFragment {
	
	private static String myAdvice;
	private static String occurrenceGravity;
	private static Boolean isVisible;
	//private boolean sound_efect = false;

	/*public SimpleAdvice(String my_advice, String occurrence_gravity){
		myAdvice = my_advice;
		occurrenceGravity = occurrence_gravity;
	}*/

	public static SimpleAdvice newInstance(String my_advice, String occurrence_gravity) {
		SimpleAdvice result = new SimpleAdvice();
		result.setMyAdvice(my_advice);
		result.setOccurrenceGravity(occurrence_gravity);
		result.setVisibility(true);
		return result;
	}

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        builder.setTitle("Conselho")
        	   .setMessage(myAdvice)
               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int id) {
					   setVisibility(false);
					   dismiss();
				   }
			   });
        return builder.create();
    }

	public Boolean isDialogVisible(){
		return isVisible;
	}
	public String getMyAdvice() {
		return myAdvice;
	}
	public void setMyAdvice(String myAdvice) {
		this.myAdvice = myAdvice;
	}
	public static String getOccurrenceGravity() {
		return occurrenceGravity;
	}
	public static void setOccurrenceGravity(String occurrenceGravity) {
		SimpleAdvice.occurrenceGravity = occurrenceGravity;
	}
	public void setVisibility(Boolean bool){
		isVisible = bool;
	}
}