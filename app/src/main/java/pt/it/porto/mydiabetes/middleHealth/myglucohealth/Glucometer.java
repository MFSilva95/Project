package pt.it.porto.mydiabetes.middleHealth.myglucohealth;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.GregorianCalendar;
import java.util.UUID;

import pt.it.porto.mydiabetes.middleHealth.CustomDevices.Record;
import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.Measure;
import pt.it.porto.mydiabetes.middleHealth.ieee_11073.part_10101.Nomenclature;


public class Glucometer implements Runnable {
    private static final String TAG = Glucometer.class.getCanonicalName();

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
    private static final String NAME_INSECURE = "Glucometer";
    private static final UUID MY_UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final static byte STX = (byte) 0x80;
    private final static byte CMD_NUM_RECORDS = (byte) 0x00;
    private final static byte CMD_UNITS = (byte) 0x05;
    private final static byte CMD_RECORD = (byte) 0x01;
    private final static byte CMD_SERIAL = (byte) 0x09;
    private final static byte CMD_ERASE = (byte) 0x14;

    private final static byte[] REQUEST_ALL_DATA_MEMORY_ERASE =
            {
                    STX, 0x01, (byte) 0xFE, CMD_ERASE, (byte) 0x81, (byte) 0xEA
            };


    private static final String BLUETOOTH_NOT_SUPPORTED_ERROR_MESSAGE = "Bluetooth is not supported on this device!";

    private static Glucometer mInstance;

    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothServerSocket mBluetoothServerSocket;
    private BluetoothSocket mBluetoothSocket;
    private boolean mRepeat;
    private boolean mInitialized = false;
    private boolean mEraseAllDataOption = false;

    private Glucometer() {
    }

    public static Glucometer getInstance() {
        if (mInstance == null) {
            mInstance = new Glucometer();
        }

        return mInstance;
    }

    public boolean initialize() {
        if (mInitialized) {
            return true;
        }

        // Get default bluetooth adapter:
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.d(TAG, BLUETOOTH_NOT_SUPPORTED_ERROR_MESSAGE);
            return false;
        }

        mInitialized = true;

        return true;
    }

    @Override
    public void run() {
        Log.i(TAG, "running");
        if (!initialize()) {
            return;
        }
//        setRepeat(true);
//        while (getRepeat()) {
        // Wait for connection:
        if (!waitForConnection()) {
//                continue;
            return;
        }

        // Read data from glucometer:
        if (readDataFromMeter()) {
            // Erase all data if configured to do so:
            if (mEraseAllDataOption) {
                eraseDateFromGlucometer();
            }
        }

        // Close connection:
        closeConnection();
//        }
    }

    private OnMeasurementListener onMeasurementListener;

    public void setOnMeasurementListener(OnMeasurementListener onMeasurementListener) {
        this.onMeasurementListener = onMeasurementListener;
    }

    public void onNewMeasure(Measure measure) {
        if (onMeasurementListener != null) {
            onMeasurementListener.onNewMeasure(measure);
        }
    }

    public void stop() {
        setRepeat(false);
    }

    public String getSystemId() {
        return SYSTEM_ID;
    }

    public boolean getEraseAllDataOption() {
        return mEraseAllDataOption;
    }

    public void setEraseAllDataOption(boolean value) {
        mEraseAllDataOption = value;
    }

    private boolean waitForConnection() {
        try {

            // Create bluetooth server socket:
            mBluetoothServerSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE, MY_UUID_INSECURE);
//            mBluetoothSocket.connect();
//            Log.d(TAG, "Now waiting for a glucometer connection...");
            mBluetoothSocket = mBluetoothServerSocket.accept();
            Log.d(TAG, "Glucometer connection found!");
//            mDevice.setAddress(mBluetoothSocket.getRemoteDevice().getAddress());
//            mEventManager.deviceConnected(SYSTEM_ID, mDevice);
            mInputStream = mBluetoothSocket.getInputStream();
            mOutputStream = mBluetoothSocket.getOutputStream();
        } catch (IOException e) {
            Log.d(TAG, "Error initializing sockets");
            return false;
        }
        return true;
    }

    private void closeConnection() {
        try {
            if (mInputStream != null) {
                mInputStream.close();
            }

            if (mOutputStream != null) {
                mOutputStream.close();
            }

            if (mBluetoothSocket != null) {
                mBluetoothSocket.close();
            }

            if (mBluetoothServerSocket != null) {
                mBluetoothServerSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setRepeat(false);
    }

    private boolean readDataFromMeter() {
        try {
            // Read number of records:
            int numberOfRecords = getNumberOfRecords();

            int recordIndex;
            for (recordIndex = 0; recordIndex < numberOfRecords; recordIndex++) {
                // Read record:
                Record record = getRecord(recordIndex);

                // Create measure from record and notify event manager:
                Measure measure = createMeasure(record);
                onNewMeasure(measure);

//                Log.i(TAG, "Measure: "+measure.toString());
//                mEventManager.receivedMeasure(SYSTEM_ID, measure);

                Thread.sleep(100); // Required -- ori. 55
            }

            return recordIndex != 0;
        } catch (Exception e) {
            Log.d(TAG, "readDataFromMeter() - exception occurred!");
            e.printStackTrace();
            return false;
        }
    }

    private byte[] dialogWithMeter(byte[] send, int noOfBytes) throws Exception {
        byte receive[];
        for (int i = 0; i < DIALOG_RETRIES_IN_CASE_OF_WRONG_RESPONCE; i++) {
            boolean isWritten = writeToMeter(send);
            if (!isWritten) {
                throw new IOException("Unable to write into device");
            }


            receive = readFromMeter(noOfBytes);

			/* Corrupted or no data */
            if (receive == null) {
                /* Try again */
                Thread.sleep(100);
                continue;
            }

            if (receive[3] == send[3]) {
                return receive;
            }

			/* Try again */
            Thread.sleep(100);
        }
        throw new IOException("Wrong response from device");
    }

    private boolean writeToMeter(byte[] send) {
        try {
            if ((mOutputStream != null) && (mInputStream != null)) {
                if (mInputStream.available() > 0) {
                    //Log.d(TAG,"There are " + mInputStream.available() + " bytes in input stream, skip it");
                    mInputStream.skip(mInputStream.available());
                }

                for (int j = 0; j < WRITE_RETRIES; j++) {
                    mOutputStream.write(send);
                    //Log.d(TAG,"Wrote " + send.length + " bytes");
//					mOutputStream.flush();
                    for (int i = 0; i < WRITE_AVAILABLE_RETRIES; i++) {
                        if (mInputStream.available() > 0) {
                            return true;
                        }

                        Thread.sleep(WRITE_TIMEOUT);
                    }
                    //Log.d(TAG,"No answer, repeat...");
                }

                return false;
            } else {
                //Log.d(TAG,"Can not write to meter");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private byte[] readFromMeter(int paramInt) throws Exception {
        int i;
        byte[] arrayOfByte = new byte[paramInt];
        //Log.d(TAG,"Need to read " + paramInt + " bytes, available " + mInputStream.available() + " bytes");
        for (i = 0; i < paramInt; ++i) {
            for (int k = 0; k < READ_AVAILABLE_RETRIES; k++) {
                if (mInputStream.available() > 0) {
                    break;
                }
                Thread.sleep(READ_TIMEOUT);
            }
            if (mInputStream.available() == 0) {
                return null;
            }

            int j = mInputStream.read();
            if (j == -1) {
                throw new IOException();
            }

            arrayOfByte[i] = (byte) j;
        }

        return arrayOfByte;
    }

    private boolean eraseDateFromGlucometer() {
        // Send a request to erase all the data from memory:
        if (!writeToMeter(REQUEST_ALL_DATA_MEMORY_ERASE)) {
            Log.d(TAG, "eraseDateFromGlucometer() - Couldn't send erase memory request to glucometer.");
            return false;
        }

        try {
            // Wait for response:
            byte[] receiveBuffer = readFromMeter(1);
            if (receiveBuffer != null && receiveBuffer.length > 0 && receiveBuffer[0] == CMD_ERASE) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "eraseDateFromGlucometer() - Bad response from glucometer.");
        return false;
    }

    private synchronized boolean getRepeat() {
        return mRepeat;
    }

    private synchronized void setRepeat(boolean rep) {
        mRepeat = rep;
    }

    private int getNumberOfRecords() throws Exception {
        // Create get number of records request:
        byte[] requestBuffer = createGetNumberOfRecordsRequestBuffer();

        // Send request:
        byte[] responseBuffer = dialogWithMeter(requestBuffer, 7);

        return responseBuffer[4];
    }


    private Record getRecord(int recordIndex) throws Exception {
        // Create read one data request:
        byte[] requestBuffer = createReadOneDataRequestBuffer(recordIndex);

        // Send request:
        byte[] responseBuffer = dialogWithMeter(requestBuffer, 13);

        return new Record(responseBuffer, 5);
    }

    private byte[] createGetNumberOfRecordsRequestBuffer() {
        byte[] buffer = new byte[6];

        buffer[0] = STX; // stx
        buffer[1] = (byte) 0x01; // size
        buffer[2] = (byte) ~(buffer[1]); // ~size
        buffer[3] = CMD_NUM_RECORDS; // command
        buffer[4] = (byte) ~(buffer[0] ^ buffer[2]); // checksum L
        buffer[5] = (byte) ~(buffer[1] ^ buffer[3]); // checksum H

        return buffer;
    }

    private byte[] createReadOneDataRequestBuffer(int recordIndex) {
        byte[] buffer = new byte[7];

        buffer[0] = STX; // stx
        buffer[1] = (byte) 0x02; // size
        buffer[2] = (byte) ~(buffer[1]); // ~size
        buffer[3] = CMD_RECORD; // command
        buffer[4] = (byte) recordIndex; // data
        buffer[5] = (byte) ~((buffer[0] ^ buffer[2]) ^ buffer[4]); // checksum L
        buffer[6] = (byte) ~(buffer[1] ^ buffer[3]); // checksum H

        return buffer;
    }

    private Measure createMeasure(Record record) {
        Measure measure = new Measure();

        measure.setMeasureId(Nomenclature.MDC_CONC_GLU_GEN);
        measure.setMeasureName("glucose");
        measure.setUnitId(Nomenclature.MDC_DIM_MILLI_G_PER_DL);
        measure.setUnitName("mg/dl");
        measure.add((double) record.getResult());

        GregorianCalendar c = new GregorianCalendar(2000 + record.getYear(), record.getMon() - 1, record.getDay(), record.getHour(), record.getMin());
        measure.setTimestamp(c.getTime().getTime());

        return measure;
    }

    interface OnMeasurementListener {
        void onNewMeasure(Measure measure);
    }
}