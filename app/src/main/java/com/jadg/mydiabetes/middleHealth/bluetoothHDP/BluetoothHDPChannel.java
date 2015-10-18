package com.jadg.mydiabetes.middleHealth.bluetoothHDP;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.ParcelFileDescriptor;



import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.channel.Channel;

public class BluetoothHDPChannel extends Channel {
	private int id;
	private ParcelFileDescriptor mFD; 
	 

	public BluetoothHDPChannel(ParcelFileDescriptor fd) throws Exception{
		super(new FileInputStream(fd.getFileDescriptor()),new FileOutputStream(fd.getFileDescriptor()));
		mFD = fd;
		id = 1;
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public void releaseChannel() {
		// TODO Auto-generated method stub
		try {
			mFD.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int getChannelId() {
		// TODO Auto-generated method stub
		return id;
	}

}
