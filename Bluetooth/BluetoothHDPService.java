package bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHealth;
import android.bluetooth.BluetoothHealthAppConfiguration;
import android.bluetooth.BluetoothHealthCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

import java.io.FileInputStream;
import java.io.IOException;

public class BluetoothHDPService extends Service
{
    public static final int sRESULT_OK = 0;
    public static final int sRESULT_FAIL = -1;

    public static final int sSTATUS_HEALTH_APP_REGISTER = 100;
    public static final int sSTATUS_HEALTH_APP_UNREGISTER = 101;
    public static final int sSTATUS_CREATE_CHANNEL = 102;
    public static final int sSTATUS_DESTROY_CHANNEL = 103;
    public static final int sSTATUS_READ_DATA = 104;
    public static final int sSTATUS_READ_DATA_DONE = 105;

    public static final int sMSG_REGISTER_CLIENT = 200;
    public static final int sMSG_UNREGISTER_CLIENT = 201;
    public static final int sMSG_REGISTER_HEALTH_APP = 300;
    public static final int sMSG_UNREGISTER_HEALTH_APP = 301;
    public static final int sMSG_CONNECT_CHANNEL = 400;
    public static final int sMSG_DISCONNECT_CHANNEL = 401;

    private static final String sTAG = "BluetoothHDPService";

    private class IncomingHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case sMSG_REGISTER_CLIENT:
                    log("handleMessage() - Activity client registered.");
                    mClient = msg.replyTo;
                    break;

                case sMSG_UNREGISTER_CLIENT:
                    log("handleMessage() - Activity client unregistered.");
                    mClient = null;
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }
    private class ReadThread extends Thread
    {
        private ParcelFileDescriptor mFileDescriptor;

        public ReadThread(ParcelFileDescriptor fileDescriptor)
        {
            super();
            mFileDescriptor = fileDescriptor;
        }

        @Override
        public void run()
        {
            FileInputStream inputStream = new FileInputStream(mFileDescriptor.getFileDescriptor());

            final byte data[] = new byte[8192];
            try
            {
                while(inputStream.read(data) > -1)
                {
                    // At this point, the application can pass the raw data to a parser that
                    // has implemented the IEEE 11073-xxxxx specifications.  Instead, this sample
                    // simply indicates that some data has been received.
                    sendMessage(sSTATUS_READ_DATA, 0);

                    // TODO

                    /*BluetoothHDPChannel cHDP = new BluetoothHDPChannel(fd);

                    Agent a = new Agent();
                    a.addChannel(cHDP);
                    a.setAddress(device.getAddress());
                    System.out.println("added a new Agent!");*/
                }
            }
            catch(IOException e)
            {
            }

            if (mFileDescriptor != null)
            {
                try
                {
                    mFileDescriptor.close();
                }
                catch (IOException e)
                {
                }
            }

            sendMessage(sSTATUS_READ_DATA_DONE, 0);
        }
    }

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothHealthAppConfiguration mHealthAppConfig;
    private BluetoothHealth mBluetoothHealth;
    private BluetoothDevice mDevice;
    private int mChannelId;
    private Messenger mClient;
    private final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public void onCreate()
    {
        // Get bluetooth adapter:
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
        {
            error("onCreate() - Failed getting bluetooth default adapter");
            stopSelf();
            return;
        }

        // Get bluetooth health profile:
        if(!mBluetoothAdapter.getProfileProxy(this, mBluetoothServiceListener, BluetoothProfile.HEALTH))
        {
            error("onCreate() - Bluetooth health profile is not available");
            stopSelf();
            return;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        log("onStartCommand() - BluetoothHDPService is running.");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return mMessenger.getBinder();
    }

    private void registerApplication(int dataType)
    {
        // Register health application through the Bluetooth Health API:
        mBluetoothHealth.registerSinkAppConfiguration(sTAG, dataType, mHealthCallback);
    }

    private void unregisterApplication()
    {
        // Unregister health application through the Bluetooth Health API:
        mBluetoothHealth.unregisterAppConfiguration(mHealthAppConfig);
    }

    private void connectChannel(BluetoothDevice device)
    {
        log("connectChannel()");

        mDevice = device;

        // Connect channel through the Bluetooth Health API:
        mBluetoothHealth.connectChannelToSource(device, mHealthAppConfig);
    }

    private void disconnectChannel(BluetoothDevice device, int channelId)
    {
        log("disconnectChannel()");

        mBluetoothHealth.disconnectChannel(device, mHealthAppConfig, channelId);
    }

    private final BluetoothProfile.ServiceListener mBluetoothServiceListener = new BluetoothProfile.ServiceListener()
    {
        public void onServiceConnected(int profile, BluetoothProfile proxy)
        {
            if (profile == BluetoothProfile.HEALTH)
            {
                mBluetoothHealth = (BluetoothHealth) proxy;
                log("onServiceConnected() - profile: " + profile);
            }
        }

        @Override
        public void onServiceDisconnected(int profile)
        {
            if(profile == BluetoothProfile.HEALTH)
            {
                mBluetoothHealth = null;
                log("onServiceDisconnected() - profile: " + profile);
            }
        }
    };

    private final BluetoothHealthCallback mHealthCallback = new BluetoothHealthCallback()
    {
        public void onHealthAppConfigurationStatusChange(BluetoothHealthAppConfiguration config, int status)
        {
            if (status == BluetoothHealth.APP_CONFIG_REGISTRATION_FAILURE)
            {
                mHealthAppConfig = null;
                sendMessage(sSTATUS_HEALTH_APP_REGISTER, sRESULT_FAIL);
                error("onHealthAppConfigurationStatusChange() - App config registration failure!");
            }
            else if (status == BluetoothHealth.APP_CONFIG_REGISTRATION_SUCCESS)
            {
                mHealthAppConfig = config;
                sendMessage(sSTATUS_HEALTH_APP_REGISTER, sRESULT_OK);
                log("onHealthAppConfigurationStatusChange() - App config registration ok!");
            }
            else if (status == BluetoothHealth.APP_CONFIG_UNREGISTRATION_FAILURE)
            {
                sendMessage(sSTATUS_HEALTH_APP_UNREGISTER, sRESULT_FAIL);
                error("onHealthAppConfigurationStatusChange() - App config unregistration failure!");
            }
            else if(status == BluetoothHealth.APP_CONFIG_UNREGISTRATION_SUCCESS)
            {
                sendMessage(sSTATUS_HEALTH_APP_UNREGISTER, sRESULT_OK);
                log("onHealthAppConfigurationStatusChange() - App config unregistration ok!");
            }
        }


        public void onHealthChannelStateChange(BluetoothHealthAppConfiguration config, BluetoothDevice device, int prevState, int newState, ParcelFileDescriptor fd, int channelId)
        {
            log("onHealthChannelStateChange() - Previous state: " + prevState + " | New state: " + newState);

            if (prevState == BluetoothHealth.STATE_CHANNEL_DISCONNECTED &&
                    newState == BluetoothHealth.STATE_CHANNEL_CONNECTED)
            {
                if (config.equals(mHealthAppConfig))
                {
                    mChannelId = channelId;
                    sendMessage(sSTATUS_CREATE_CHANNEL, sRESULT_OK);

                    // Read data from health device using a new thread:
                    (new ReadThread(fd)).start();
                }
                else
                {
                    sendMessage(sSTATUS_CREATE_CHANNEL, sRESULT_FAIL);
                }
            }
            else if (prevState == BluetoothHealth.STATE_CHANNEL_CONNECTING &&
                    newState == BluetoothHealth.STATE_CHANNEL_DISCONNECTED)
            {
                sendMessage(sSTATUS_CREATE_CHANNEL, sRESULT_FAIL);
            }
            else if (newState == BluetoothHealth.STATE_CHANNEL_DISCONNECTED)
            {
                if (config.equals(mHealthAppConfig))
                {
                    sendMessage(sSTATUS_DESTROY_CHANNEL, sRESULT_OK);
                }
                else
                {
                    sendMessage(sSTATUS_DESTROY_CHANNEL, sRESULT_FAIL);
                }
            }
        }
    };

    private void sendMessage(int what, int value)
    {
        if(mClient == null)
            return;

        try
        {
            mClient.send(Message.obtain(null, what, value, 0));
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    private static void log(String message)
    {
        System.out.println(message);
    }

    private static void error(String message)
    {
        System.err.println(message);
    }
}
