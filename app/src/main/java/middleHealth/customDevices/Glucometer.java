package middleHealth.customDevices;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import middleHealth.es.libresoft.openhealth.HealthDevice;
import middleHealth.es.libresoft.openhealth.Measure;
import middleHealth.es.libresoft.openhealth.events.EventManager;



public class Glucometer implements ICustomDevice{
	private static EventManager ieManager = null;
	private static HealthDevice dev  = null;
	
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
	
	private static final String System_ID = UUID.randomUUID().toString();
	private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";
	private static final UUID MY_UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	
    private int num_records;
    private final static byte STX = (byte) 0x80;
	private final static byte CMD_NUM_RECORDS = (byte)0x00;
	private final static byte CMD_UNITS = (byte)0x05;
	private final static byte CMD_RECORD = (byte)0x01;
	private final static byte CMD_SERIAL = (byte)0x09;
	
	
	private BluetoothAdapter mAdapter;
    private String mac;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    BluetoothServerSocket serverSocket;
    BluetoothSocket clientSocket;
	private boolean repeat = true;
	
	private synchronized void setRepeat(boolean rep){
		repeat = rep;
	}
	private synchronized boolean getRepeat(){
		return repeat;
	}
	
	
	@Override
	public void run() {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		dev = new HealthDevice();
		dev.setSystId(System_ID);
		dev.setManufacturer("Entra Health Systems");
		while(getRepeat()){
			waitForConnection();
			readDataFromMeter();
			closeConnection();
			
		}
	}

	@Override
	public void setEventHandler(EventManager ieManager) {
		this.ieManager = ieManager;		
	}

	@Override
	public String getSystemId() {
		// TODO Auto-generated method stub
		return System_ID;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		setRepeat(false);
	}

	@Override
	public List<String> invokeCommand(List<String> args) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	
	
	public void waitForConnection(){
		try {
			serverSocket = mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE, MY_UUID_INSECURE);
			System.out.println("Now waiting for a glucometer connection...");
			clientSocket = serverSocket.accept();
			System.out.println("Glucometer connection found!");
			dev.setAddress(clientSocket.getRemoteDevice().getAddress());
			ieManager.deviceConnected(System_ID, dev);
			mmInStream = clientSocket.getInputStream();
			mmOutStream = clientSocket.getOutputStream();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error initializing sockets");
		}
		
	}
	
	public void closeConnection(){
		if (mmInStream != null) {
            try {mmInStream.close();} catch (Exception e) {}
            mmInStream = null;
    }

    if (mmOutStream != null) {
            try {mmOutStream.close();} catch (Exception e) {}
            mmOutStream = null;
    }

    if (clientSocket != null) {
            try {clientSocket.close();} catch (Exception e) {}
            clientSocket = null;
    }
    if (serverSocket != null) {
        try {serverSocket.close();} catch (Exception e) {}
        serverSocket = null;
    }

    ieManager.deviceDisconnected(System_ID);
	}
	
	public boolean readDataFromMeter(){
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
		try{
			sc_receiveBuf1 = dialogWithMeter(sc_sendBuf1, 7);				
		}
		catch(Exception e)
		{
			
			return false;
		}
		num_records = sc_receiveBuf1[4];
		

		String currentSync = "";
		byte  sc_sendBuf2[] = new byte[7];
		byte  sc_receiveBuf2[] = new byte[13];
		while( true ) {
			
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
					System.out.println("ERROR: meter records not found");
				}
				/* No data in meter */
				break;
			} else {
				if ( record.equals(lastRecord) ) {
					lastRecord = record; /* the same record, try again */
				} else {
					lastRecord = record;
					Measure m = new Measure();
					m.setMeasureId(28948);
					m.setMeasureName("glucose");
					m.setUnitId(2130);
					m.setUnitName("mg/dl");
					m.add((double) record.getResult());
					
					GregorianCalendar c = new GregorianCalendar(2000 + record.getYear(), record.getMon(), record.getDay(), record.getHour(), record.getMin());
					m.setTimestamp(c.getTimeInMillis());
					System.out.println(record.getYear() + " " +  record.getMon() + " " +  record.getDay() + " " +  record.getHour() + " " +  record.getMin());
					
					ieManager.receivedMeasure(System_ID, m);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(record.getResult());
					System.out.println(record.getTemp());
					System.out.println(record.getAmpm());
					
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

	
	
	private byte[] dialogWithMeter(byte[] send, int noOfBytes) throws Exception{
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
	
	
	
	
	private boolean writeToMeter(byte[] send){			
		try {
			if((mmOutStream != null) && (mmInStream != null)){
//				if (mmInStream.available() > 0) return true;
				if (mmInStream.available() > 0) {
					//Log.d(TAG,"There are " + mmInStream.available() + " bytes in input stream, skip it");
					mmInStream.skip(mmInStream.available());
				}
				
				for (int j = 0; j < WRITE_RETRIES; j++) {
					mmOutStream.write(send);
					//Log.d(TAG,"Wrote " + send.length + " bytes");
//					mmOutStream.flush();
					for (int i = 0; i < WRITE_AVAILABLE_RETRIES; i++) {
						if (mmInStream.available() > 0) return true;
						Thread.sleep(WRITE_TIMEOUT);
					}
					//Log.d(TAG,"No answer, repeat...");
				}

				return false;
			}
			else {
				//Log.d(TAG,"Can not write to meter");
			}
		} catch (Exception e) {
			//Log.d(TAG,"Error while write to meter: " + e.getMessage());
        	//Logger.printException(e);
		}
		return false;
	}

	
	private byte[] readFromMeter(int paramInt) throws Exception
	{
		int i;
		byte[] arrayOfByte = new byte[paramInt];
		//Log.d(TAG,"Need to read " + paramInt + " bytes, available " + mmInStream.available() + " bytes");
		for (i = 0; i < paramInt; ++i)
		{
			for (int k = 0; k < READ_AVAILABLE_RETRIES; k++) {
				if (mmInStream.available() > 0)
					break;
				Thread.sleep(READ_TIMEOUT);
			}
			if (mmInStream.available() == 0)
				return null;
		  
		  int j = mmInStream.read();
		  if (j == -1)
		    throw new IOException();
		  arrayOfByte[i] = (byte)j;
		}

		return arrayOfByte;
	}
	

}
