package com.jadg.mydiabetes.middleHealth.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.jadg.mydiabetes.Meal;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.database.GlycemiaDataBinding;
import com.jadg.mydiabetes.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.AndroidMeasure;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.MiddleHealth;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_10101.Nomenclature;

public class MessageReceiver extends Handler
{
	private static final String TAG = "MessageReceiver";

	private List<AndroidMeasure> mMeasures = new ArrayList<>();

	private Activity mActivity;

	public MessageReceiver(Activity activity)
	{
		mActivity = activity;
	}

	@Override
	public void handleMessage(Message msg)
	{
		switch (msg.what)
		{
			case MiddleHealth.MSG_DEVICE_CONNECTED:
				onDeviceConnected();
				break;

			case MiddleHealth.MSG_DEVICE_DISCONNECTED:
				onDeviceDisconnected();
				break;

			case MiddleHealth.MSG_MEASURE_RECEIVED:
				onMeasureReceived(msg);
				break;

			default:
				super.handleMessage(msg);
		}
	}

	private void onDeviceConnected()
	{
	}
	private void onMeasureReceived(Message message)
	{
		Log.d(TAG, "Measure Received!");
		final Bundle bundle = message.getData();
		bundle.setClassLoader(mActivity.getClassLoader());

		AndroidMeasure measure = bundle.getParcelable("data");
		mMeasures.add(measure);
	}
	private void onDeviceDisconnected()
	{
		if(mMeasures.size() == 0)
			return;

		// If there was only a single measure:
		if(mMeasures.size() == 1)
		{
			// Get that single measure:
			AndroidMeasure measure = mMeasures.get(0);

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
			for (AndroidMeasure measure : mMeasures)
				saveGlycemia(measure);

			Toast.makeText(mActivity, "Added " + mMeasures.size() + " measures to the database!", Toast.LENGTH_LONG).show();
		}

		mMeasures.clear();
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
