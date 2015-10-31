package com.jadg.mydiabetes.middleHealth.customDevices;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.HealthDevice;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.Measure;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.events.EventManager;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_10101.Nomenclature;

public class Glucometer implements ICustomDevice
{
	private static final String TAG = "Glucometer";
	
	// Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    private final static int WRITE_RETRIES = 5;
	private final static int WRITE_AVAILABLE_RETRIES = 50;
	private final static int WRITE_TIMEOUT = 50;
	private final static int READ_AVAILABLE_RETRIES = 50;
	private final static int READ_TIMEOUT = 50;
	private final static int DIALOG_RETRIES_IN_CASE_OF_WRONG_RESPONCE = 3;
    public static final int DIALOG_PROGRESS = 2;
	
	private static final String SYSTEM_ID = UUID.randomUUID().toString();
	private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";
	private static final UUID MY_UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final static byte STX = (byte) 0x80;
	private final static byte CMD_NUM_RECORDS = (byte)0x00;
	private final static byte CMD_UNITS = (byte)0x05;
	private final static byte CMD_RECORD = (byte)0x01;
	private final static byte CMD_SERIAL = (byte)0x09;


	private static EventManager sEventManager = null;
	private static HealthDevice sDevice = null;

	private BluetoothAdapter mAdapter;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private BluetoothServerSocket mBluetoothServerSocket;
    private BluetoothSocket mBluetoothSocket;
	private boolean mRepeat = true;

	@Override
	public void run()
	{
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		sDevice = new HealthDevice();
		sDevice.setSystId(SYSTEM_ID);
		sDevice.setManufacturer("Entra Health Systems");
		while(getRepeat())
		{
			waitForConnection();
			readDataFromMeter();
			closeConnection();
		}
	}

	@Override
	public void setEventHandler(EventManager eventManager)
	{
		this.sEventManager = eventManager;
	}

	@Override
	public String getSystemId()
	{
		return SYSTEM_ID;
	}

	@Override
	public void stop()
	{
		setRepeat(false);
	}

	@Override
	public List<String> invokeCommand(List<String> args)
	{
		return null;
	}

	public void waitForConnection()
	{
		try
		{
			mBluetoothServerSocket = mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE, MY_UUID_INSECURE);
			Log.d(TAG, "Now waiting for a glucometer connection...");
			mBluetoothSocket = mBluetoothServerSocket.accept();
			Log.d(TAG, "Glucometer connection found!");
			sDevice.setAddress(mBluetoothSocket.getRemoteDevice().getAddress());
			sEventManager.deviceConnected(SYSTEM_ID, sDevice);
			mInputStream = mBluetoothSocket.getInputStream();
			mOutputStream = mBluetoothSocket.getOutputStream();
		}
		catch (IOException e)
		{
			Log.d(TAG, "Error initializing sockets");
		}
	}
	
	public void closeConnection()
	{
		try
		{
			if (mInputStream != null)
				mInputStream.close();

			if(mOutputStream != null)
				mOutputStream.close();

			if(mBluetoothSocket != null)
				mBluetoothSocket.close();

			if(mBluetoothServerSocket != null)
				mBluetoothServerSocket.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

    	sEventManager.deviceDisconnected(SYSTEM_ID);
	}
	
	public boolean readDataFromMeter()
	{
		Record record;
		Record lastRecord = null;
		
		int recordsCount = 0;

		byte  sc_sendBuf1[] = new byte[6];
		byte  sc_receiveBuf1[] = new byte[7];
		
		// get the number of records
		sc_sendBuf1[0] = STX;//stx
		sc_sendBuf1[1] = (byte)0x01;//size
		sc_sendBuf1[2] = (byte) ~ ( sc_sendBuf1[1] );//~size
		sc_sendBuf1[3] = CMD_NUM_RECORDS;//command
		sc_sendBuf1[4] = (byte) ~(sc_sendBuf1[0] ^ sc_sendBuf1[2]); //checksum L
		sc_sendBuf1[5] = (byte) ~(sc_sendBuf1[1] ^ sc_sendBuf1[3]); // checksum H
		try
		{
			sc_receiveBuf1 = dialogWithMeter(sc_sendBuf1, 7);				
		}
		catch(Exception e)
		{

			return false;
		}
		int numberRecords = sc_receiveBuf1[4];

		String currentSync = "";
		byte  sc_sendBuf2[] = new byte[7];
		byte  sc_receiveBuf2[] = new byte[13];
		while(true)
		{
			/**
			 * one record request, 0x01
			 */
			sc_sendBuf2[0] = STX;//stx
			sc_sendBuf2[1] = (byte)0x02;//size
			sc_sendBuf2[2] = (byte) ~ ( sc_sendBuf2[1] );//~size
			sc_sendBuf2[3] = CMD_RECORD;//command
			sc_sendBuf2[4] = (byte)recordsCount;//data
			sc_sendBuf2[5] = (byte) ~((sc_sendBuf2[0] ^ sc_sendBuf2[2]) ^ sc_sendBuf2[4]); //checksum L
			sc_sendBuf2[6] = (byte) ~(sc_sendBuf2[1] ^ sc_sendBuf2[3]); // checksum H

			try{
				sc_receiveBuf2 = dialogWithMeter(sc_sendBuf2, 13);				
			}
			catch(Exception e)
			{ 
				return false;
			}

			record = new Record(sc_receiveBuf2, 5);

			if ( record.getResult() == 0 ) {
				if ( recordsCount == 0 ) {
					Log.d(TAG, "ERROR: meter records not found");
				}
				/* No data in meter */
				break;
			} else {
				if ( record.equals(lastRecord) ) {
					lastRecord = record; /* the same record, try again */
				} else {
					lastRecord = record;
					Measure m = new Measure();
					m.setMeasureId(Nomenclature.MDC_CONC_GLU_GEN);
					m.setMeasureName("glucose");
					m.setUnitId(Nomenclature.MDC_DIM_MILLI_G_PER_DL);
					m.setUnitName("mg/dl");
					m.add((double) record.getResult());
					
					GregorianCalendar c = new GregorianCalendar(2000 + record.getYear(), record.getMon() - 1, record.getDay(), record.getHour(), record.getMin());
					m.setTimestamp(c.getTime().getTime());
					
					sEventManager.receivedMeasure(SYSTEM_ID, m);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					recordsCount++;
					
					currentSync = record.convertToLastSync();
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					return false;
				} /* Required -- ori. 55 */
			
			}
			
			
		} 

		if ( recordsCount == 0 ) {
			return false;
		}
		
		
		return true;
	}

	private byte[] dialogWithMeter(byte[] send, int noOfBytes) throws Exception
	{
		byte receive[];
		for (int i = 0; i < DIALOG_RETRIES_IN_CASE_OF_WRONG_RESPONCE; i++) {
			boolean isWritten = writeToMeter(send);
			if(!isWritten)
				throw new IOException("Unable to write into device"); 
			
		
			receive = readFromMeter(noOfBytes);

			/* Corrupted or no data */
			if (receive == null) {
    			/* Try again */
    			Thread.sleep(100);
    			continue;
			}	
			
			if(receive[3] == send[3]){
				return receive;
			}
			
			/* Try again */
			Thread.sleep(100);
		}
		throw new IOException("Wrong response from device"); 
	}

	private boolean writeToMeter(byte[] send)
	{
		try
		{
			if((mOutputStream != null) && (mInputStream != null))
			{
				if (mInputStream.available() > 0)
				{
					//Log.d(TAG,"There are " + mInputStream.available() + " bytes in input stream, skip it");
					mInputStream.skip(mInputStream.available());
				}
				
				for (int j = 0; j < WRITE_RETRIES; j++)
				{
					mOutputStream.write(send);
					//Log.d(TAG,"Wrote " + send.length + " bytes");
//					mOutputStream.flush();
					for (int i = 0; i < WRITE_AVAILABLE_RETRIES; i++)
					{
						if (mInputStream.available() > 0)
							return true;

						Thread.sleep(WRITE_TIMEOUT);
					}
					//Log.d(TAG,"No answer, repeat...");
				}

				return false;
			}
			else
			{
				//Log.d(TAG,"Can not write to meter");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return false;
	}

	
	private byte[] readFromMeter(int paramInt) throws Exception
	{
		int i;
		byte[] arrayOfByte = new byte[paramInt];
		//Log.d(TAG,"Need to read " + paramInt + " bytes, available " + mInputStream.available() + " bytes");
		for (i = 0; i < paramInt; ++i)
		{
			for (int k = 0; k < READ_AVAILABLE_RETRIES; k++)
			{
				if (mInputStream.available() > 0)
					break;
				Thread.sleep(READ_TIMEOUT);
			}
			if (mInputStream.available() == 0)
				return null;

			int j = mInputStream.read();
			if (j == -1)
				throw new IOException();

			arrayOfByte[i] = (byte) j;
		}

		return arrayOfByte;
	}

	private void eraseDateFromGlucometer()
	{

	}

	private synchronized boolean getRepeat()
	{
		return mRepeat;
	}
	private synchronized void setRepeat(boolean rep)
	{
		mRepeat = rep;
	}
}