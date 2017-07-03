package pt.it.porto.mydiabetes.adviceSystem.alerts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * 
 * @author esmetafinker
 * Its this class can receive an String and parsing it
 * generates an Advice
 */
public class AdviseATest extends SimpleAdvice {
	
	
	private String testType;

	public String getTestType() {
		return testType;
	}
	public void setTestType(String testType) {
		this.testType = testType;
	}


	public static AdviseATest newInstance(String my_advice, String occurrence_gravity, String testType, Context myContext) {

		AdviseATest result = new AdviseATest();
		result.setMyAdvice(my_advice);
		result.setOccurrenceGravity(occurrence_gravity);
		result.setTestType(testType);
		result.setVisibility(true);
		return result;

	}

	
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

		final Activity currentActivity = getActivity();

		if(currentActivity!=null){
			AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);

			builder.setTitle("Conselho")
					.setMessage(getMyAdvice())
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int id) {

							Class<?> wantedAct = null;

							try {
								wantedAct = Class.forName("com.jadg.mydiabetes."+getTestType());
							} catch (ClassNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							Intent intent = new Intent(currentActivity, wantedAct);
							getActivity().startActivity(intent);
							setVisibility(false);
							dismiss();
						}
					})
					.setNegativeButton("LATER", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							setVisibility(false); dismiss();
						}
					});
			return builder.create();
		}
    	return null;
    }

}