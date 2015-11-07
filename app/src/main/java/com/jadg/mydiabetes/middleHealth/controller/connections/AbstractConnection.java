package com.jadg.mydiabetes.middleHealth.controller.connections;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

public abstract class AbstractConnection implements ServiceConnection
{
	private Context mContext;

	public void shutdown()
	{
		unbindService(mContext);
	}

	protected boolean initialize(Context context, Class<?> classType, String intentAction)
	{
		mContext = context;

		if(!bindService(context, classType,intentAction))
			return false;

		return true;
	}

	protected boolean bindService(Context context, Class<?> classType, String intentAction)
	{
		Intent intent = new Intent(context, classType);
		intent.setAction(intentAction);
		if(!context.bindService(intent, this, Context.BIND_AUTO_CREATE))
			return false;

		return true;
	}

	protected void unbindService(Context context)
	{
		context.unbindService(this);
	}
}