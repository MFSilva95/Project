package com.jadg.mydiabetes.middleHealth.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.jadg.mydiabetes.Meal;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.database.GlycemiaDataBinding;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.AndroidMeasure;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.IEventCallback;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_10101.Nomenclature;
import com.jadg.mydiabetes.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventCallback extends IEventCallback.Stub
{
	private static final String TAG = "EventCallback";

	private Activity mActivity;
	private List<AndroidMeasure> mMeasures = new ArrayList<>();

	public EventCallback(Activity activity)
	{
		mActivity = activity;
	}

	@Override
	public void deviceConnected(String systemId) throws RemoteException
	{
		Log.d(TAG, "mCallbacks.deviceConnected()");
	}

	@Override
	public void deviceDisconnected(String systemId) throws RemoteException
	{
		Log.d(TAG, "mCallbacks.deviceDisconnected()");

		handleMeasures(mMeasures);

		mMeasures.clear();
	}

	@Override
	public void deviceChangeStatus(String systemId, String previous, String newState) throws RemoteException
	{
		Log.d(TAG, "mCallbacks.deviceChangeStatus() - State changed from " + previous + " to " + newState);
	}

	@Override
	public void MeasureReceived(String systemId, AndroidMeasure measure) throws RemoteException
	{
		Log.d(TAG, "mCallbacks.MeasureReceived()");

		mMeasures.add(measure);
	}

	private void handleMeasures(List<AndroidMeasure> measures)
	{
		if(measures.size() == 0)
			return;

		// If there was only a single measure:
		if(measures.size() == 1)
		{
			// Get that single measure:
			AndroidMeasure measure = measures.get(0);

			// Calculate how much time it passed since the measure was taken:
			Date now = new Date();
			long difference = now.getTime() - measure.getTimestamp();

			// If it was not taken longer than ten minutes ago:
			if(difference <= 600000)
			{
				if(measure.getMeasureId() == Nomenclature.MDC_CONC_GLU_GEN)
				{
					Intent intent = new Intent(mActivity, Meal.class);
					intent.putExtra("value", measure.getValues().get(0)); // TODO convert units
					intent.putExtra("timestamp", measure.getTimestamp());
					mActivity.startActivity(intent);
				}
			}
		}
		else
		{
			for (AndroidMeasure measure : measures)
				saveGlycemia(measure);

			final int numberOfMeasures = measures.size();
			mActivity.runOnUiThread(
					new Runnable()
					{
						@Override
						public void run()
						{
							Toast.makeText(mActivity, "Added " + numberOfMeasures + " measures to the database!", Toast.LENGTH_LONG).show();
						}
					}
			);
		}
	}

	private void saveGlycemia(AndroidMeasure measure)
	{
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
		glycemia.setValue(Double.parseDouble(measure.getValues().get(0).toString()));
		glycemia.setDate(dateString);
		glycemia.setTime(hourString);
		glycemia.setIdTag(idTag);
		writeDatabase.Glycemia_Save(glycemia);

		// Close database:
		writeDatabase.close();
	}
}