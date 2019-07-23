package pt.it.porto.mydiabetes.adviceSystem.alerts;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;




public class AlertWithTimer extends AdviseATest {

	private Calendar timeToTest;
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	static int timeUnit;
	private Class testClass;
	private String testType;

	public void setTestType(String testType){
		this.testType = testType;
	}

	/*public AlertWithTimer() {
		super(myAdvice, occurrence_gravity, testType,context);
		this.timeUnit=timeU;
		this.timeToTest=timeToTest;
		
	}*/

	public static AlertWithTimer newInstance(String my_advice, String occurrence_gravity, String testType, Context context, Calendar calendar) {
		AlertWithTimer result = new AlertWithTimer();
		result.setMyAdvice(my_advice);
		result.setOccurrenceGravity(occurrence_gravity);
		result.setTestType(testType);
		result.setCalendar(calendar);
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
							setupAlarm(currentActivity);
							dismiss();
						}
					});
			return builder.create();
		}
		return null;
    }

	private void setupAlarm(Activity act) {

			AlarmManager alm = (AlarmManager) act.getSystemService(Context.ALARM_SERVICE);
			//Intent intent = new Intent(getActivity(), AdviceAlertReceiver.class);

			Bundle extras = new Bundle();
			extras.putString("ClassName", testType);
			//intent.putExtras(extras);




			//PendingIntent alarmIntent = PendingIntent.getBroadcast(act, 0, intent, Intent.FILL_IN_DATA);
			//alm.set(AlarmManager.RTC_WAKEUP, timeToTest.getTimeInMillis(), alarmIntent);
	}

	public void setCalendar(Calendar calendar){
		this.timeToTest = calendar;
	}
}



