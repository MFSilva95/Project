package pt.it.porto.mydiabetes.middleHealth.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android.AndroidMeasure;
import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android.IEventCallback;
import pt.it.porto.mydiabetes.middleHealth.ieee_11073.part_10101.Nomenclature;
import pt.it.porto.mydiabetes.middleHealth.utils.Utils;
import pt.it.porto.mydiabetes.ui.activities.Meal;
import pt.it.porto.mydiabetes.ui.dataBinding.GlycemiaDataBinding;

public class EventCallback extends IEventCallback.Stub {
	private static final String TAG = "EventCallback";
	private static final int TIME_INTERVAL_TO_START_MEAL = 300000; // 5 minutes = 300 s = 300.000 ms

	private Activity mActivity;
	private List<AndroidMeasure> mMeasures = new ArrayList<>();

	public EventCallback(Activity activity) {
		mActivity = activity;
	}

	@Override
	public void deviceConnected(String systemId) throws RemoteException {
		Log.d(TAG, "deviceConnected()");
	}

	@Override
	public void deviceDisconnected(String systemId) throws RemoteException {
		Log.d(TAG, "deviceDisconnected()");

		handleMeasures(mMeasures);

		mMeasures.clear();
	}

	@Override
	public void deviceChangeStatus(String systemId, String previous, String newState) throws RemoteException {
		Log.d(TAG, "deviceChangeStatus() - State changed from " + previous + " to " + newState);
	}

	@Override
	public void measureReceived(String systemId, AndroidMeasure measure) throws RemoteException {
		Log.d(TAG, "measureReceived()");

		mMeasures.add(measure);
	}

	private void handleMeasures(List<AndroidMeasure> measures) {
		if (measures.size() == 0)
			return;

		// Calculate the max and min timestamps:
		Long minTimestamp = Long.MAX_VALUE;
		Long maxTimestamp = 0L;
		for (AndroidMeasure measure : measures) {
			long timestamp = measure.getTimestamp();
			if (timestamp < minTimestamp)
				minTimestamp = timestamp;

			if (timestamp > maxTimestamp)
				maxTimestamp = timestamp;
		}

		// Remove duplicates:
		removeDuplicates(measures, minTimestamp, maxTimestamp);
		if (measures.isEmpty()) {
			showMessage(0);
			return;
		}

		// If not older than 5 minutes, remove from the "measures" list and start Meal activity:
		processMostRecentMeasure(measures);

		// Save the measures:
		for (AndroidMeasure measure : measures)
			saveGlycemia(measure);

		// Show a message on the user interface:
		showMessage(measures.size());
	}

	private void removeDuplicates(List<AndroidMeasure> measures, Long minTimestamp, Long maxTimestamp) {
		// Create strings for the initial and final dates, in the correct format:
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String initialDate = dateFormat.format(Utils.getDateFromTimestamp(minTimestamp));
		String finalDate = dateFormat.format(Utils.getDateFromTimestamp(maxTimestamp));

		// Read glycemia values:
		DB_Read readDatabase = new DB_Read(mActivity);
		ArrayList<GlycemiaDataBinding> glycemiaValues = readDatabase.Glycemia_GetByDate(initialDate, finalDate);
		readDatabase.close();

		// Remove measures with equal timestamps:
		List<Long> timestamps = new ArrayList<>(Utils.getTimestampsFromDateFormat(glycemiaValues, "yyyy-MM-dd HH:mm:ss"));
		for (Long timestamp : timestamps) {
			if (measures.size() == 0)
				break;

			for (int i = 0; i < measures.size(); i++) {
				AndroidMeasure measure = measures.get(i);
				if (measure.getTimestamp() == timestamp) {
					measures.remove(i);
					break;
				}
			}
		}
	}

	private AndroidMeasure findMostRecentMeasure(List<AndroidMeasure> measures) {
		// Calculate the max and min timestamps:
		Long minTimestamp = Long.MAX_VALUE;
		Long maxTimestamp = 0L;
		AndroidMeasure mostRecentMeasure = null;
		for (AndroidMeasure measure : measures) {
			long timestamp = measure.getTimestamp();
			if (timestamp < minTimestamp)
				minTimestamp = timestamp;

			if (timestamp > maxTimestamp) {
				maxTimestamp = timestamp;
				mostRecentMeasure = measure;
			}
		}

		return mostRecentMeasure;
	}

	private void processMostRecentMeasure(List<AndroidMeasure> measures) {
		if (measures.size() == 0)
			return;

		// Find the most recent measure:
		AndroidMeasure mostRecentMeasure = findMostRecentMeasure(measures);

		// Calculate how much time it passed since the measure was taken:
		Date now = new Date();
		long difference = now.getTime() - mostRecentMeasure.getTimestamp();

		// If it was not taken longer than five minutes ago:
		if (difference <= TIME_INTERVAL_TO_START_MEAL) {
			if (mostRecentMeasure.getMeasureId() == Nomenclature.MDC_CONC_GLU_GEN) {
				Intent intent = new Intent(mActivity, Meal.class);
				intent.putExtra("value", mostRecentMeasure.getValues().get(0));
				intent.putExtra("timestamp", mostRecentMeasure.getTimestamp());
				mActivity.startActivity(intent);

				// Remove the most recent measure from the list, if the Meal activity is started:
				measures.remove(mostRecentMeasure);
			}
		}
	}

	private void saveGlycemia(AndroidMeasure measure) {
		// Build a date object from measure timestamp:
		Date measureDate = Utils.getDateFromTimestamp(measure.getTimestamp());

		// Format date:
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = dateFormat.format(measureDate);

		// Format date to output hour in the correct format:
		SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
		String hourString = hourFormat.format(measureDate);


		// Open database to read:
		DB_Read readDatabase = new DB_Read(mActivity);

		// Get ID of user:
		Object[] obj = readDatabase.MyData_Read();
		int userId = Integer.valueOf(obj[0].toString());

		// Get id of tag calculated using the measure time:
		String tag = readDatabase.Tag_GetByTime(hourString).getName();
		int idTag = readDatabase.Tag_GetIdByName(tag);

		// Close database:
		readDatabase.close();

		// Open database to write:
		DB_Write writeDatabase = new DB_Write(mActivity);

		// Save glycemia value:
		GlycemiaDataBinding glycemia = new GlycemiaDataBinding();
		glycemia.setIdUser(userId);
		glycemia.setValue((int) Double.parseDouble(measure.getValues().get(0).toString())); // TODO check if the cast is ok
		glycemia.setDateTime(dateString, hourString);
		glycemia.setIdTag(idTag);
		writeDatabase.Glycemia_Save(glycemia);

		// Close database:
		writeDatabase.close();
	}

	private void showMessage(final int numberOfMeasuresAdded) {
		final String message;
		if (numberOfMeasuresAdded == 0)
			message = mActivity.getString(R.string.no_measures_added_to_database);
		else
			message = mActivity.getString(R.string.measures_added_to_database, numberOfMeasuresAdded);

		mActivity.runOnUiThread(
				new Runnable() {
					@Override
					public void run() {
						Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
					}
				}
		);
	}
}