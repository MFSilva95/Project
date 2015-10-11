package com.jadg.mydiabetes.middleHealth;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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

import es.libresoft.openhealth.android.AndroidHealthDevice;
import es.libresoft.openhealth.android.AndroidMeasure;
import es.libresoft.openhealth.android.IApplicationRegister;
import es.libresoft.openhealth.android.IDevice;
import es.libresoft.openhealth.android.IEventCallback;
import es.libresoft.openhealth.android.MiddleHealth;
import ieee_11073.part_10101.Nomenclature;

public class DevicesReader
{
	private static final String TAG = "DevicesReader";

	private Activity mActivity;
	private IApplicationRegister mAppRegister;
	private IDevice mDevice;
	private Messenger mMiddleHealthMessenger;

	public DevicesReader(Activity activity)
	{
		mActivity = activity;
	}

	public void initialize()
	{
		Intent middleHealthService = new Intent(mActivity, MiddleHealth.class);
		mActivity.startService(middleHealthService);
		mActivity.bindService(middleHealthService, mMiddleHealthConnection, Context.BIND_AUTO_CREATE);

		Intent applicationRegisterService = new Intent(mActivity, IApplicationRegister.class);
		mActivity.bindService(applicationRegisterService, mApplicationRegisterConnection, Context.BIND_AUTO_CREATE);

		Intent deviceService = new Intent(mActivity, IDevice.class);
		mActivity.bindService(deviceService, mDeviceConnection, Context.BIND_AUTO_CREATE);
	}

	private ServiceConnection mMiddleHealthConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			Message msg = Message.obtain(null, MiddleHealth.MSG_REG_CLIENT);
			msg.replyTo = mMessenger;
			mMiddleHealthMessenger = new Messenger(service);
			try
			{
				mMiddleHealthMessenger.send(msg);
			}
			catch (RemoteException e)
			{
				Log.w(TAG, "Unable to register client to service.");
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			mMiddleHealthMessenger = null;
		}
	};

	private ServiceConnection mApplicationRegisterConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			System.out.println("Service connected");

			mAppRegister = IApplicationRegister.Stub.asInterface(service);

			try
			{
				mAppRegister.registerApplication(mCallbacks);
			}
			catch (RemoteException e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			System.out.println("service disconnected");
		}
		
	};
	
	private ServiceConnection mDeviceConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			mDevice = IDevice.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
		}
	};

	/************************************************************
	 * Deal with callbacks
	 ************************************************************/
	private final IEventCallback.Stub mCallbacks = new IEventCallback.Stub()
	{
		@Override
		public void deviceConnected(String systemID) throws RemoteException
		{
			System.out.println("FROM ACTIVITY: DEVICE CONNECTED!!");
			System.out.println("MYCLIENT: now reading device...");
			AndroidHealthDevice dev = mDevice.getDeviceInfo(systemID);
			System.out.println("MYCLIENT: powerStatus -> " + dev.getPowerStatus());
			System.out.println("MYCLIENT: batteryLevel -> " + dev.getBatteryLever());
			System.out.println("MYCLIENT: macAddress -> " + dev.getMacAddress());
			System.out.println("MYCLIENT: manufacturer -> " + dev.getManufacturer());
			System.out.println("MYCLIENT: modelNumber -> " + dev.getModelNumber());
			System.out.println("MYCLIENT: is11073 -> " + dev.is11073());
			System.out.println("MYCLIENT: systemTypeIds -> ");
			for(int id : dev.getSystemTypeIds()){
				System.out.println("MYCLIENT: " + id);
			}
			System.out.println("MYCLIENT: System types -> ");
			for(String name : dev.getSystemTypes()){
				System.out.println("MYCLIENT: " + name);
			}
		}
		
		@Override
		public void deviceChangeStatus(String systemID, String prevState, String newState) throws RemoteException
		{
			System.out.println("FROM ACTIVITY: STATE CHANGE " + prevState + " -> " + newState);
		}
		
		@Override
		public void MeasureReceived(String systemID, AndroidMeasure m) throws RemoteException
		{
			System.out.println("MYCLIENT: systemID -> " + systemID);
			System.out.println("MYCLIENT: measureID -> " + m.getMeasureId());
			System.out.println("MYCLIENT: measureName -> " + m.getMeasureName());
			System.out.println("MYCLIENT: unitId -> " + m.getUnitId());
			System.out.println("MYCLIENT: unitName -> " + m.getUnitName());
			System.out.println("MYCLIENT: values -> ");
			for(double v : m.getValues()){
				System.out.println("MYCLIENT: " + v);
			};
			System.out.print("MYCLIENT: metricIds -> ");
			for(int v: m.getMetricIds()){
				System.out.println(v + " ");
			}
			System.out.println();
			System.out.print("MYCLIENT: metricNames -> ");
			for(String n: m.getMetricNames()){
				System.out.print("MYCLIENT: " + n );
			}
			System.out.println();
		}

		@Override
		public void deviceDisconnected(String systemID) throws RemoteException
		{
			System.out.println("FROM ACTIVITY: DEVICE DISCONNECTED");
		}
	};

	private Handler mIncomingHandler = new Handler()
	{
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
	};

	private List<AndroidMeasure> mMeasures = new ArrayList<>();
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

	private final Messenger mMessenger = new Messenger(mIncomingHandler);

	private void sendMessage(int what, int value)
	{
		if (mMiddleHealthMessenger == null)
		{
			Log.d(TAG, "Health Service not connected.");
			return;
		}

		try
		{
			mMiddleHealthMessenger.send(Message.obtain(null, what, value, 0));
		}
		catch (RemoteException e)
		{
			Log.w(TAG, "Unable to reach service.");
			e.printStackTrace();
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
