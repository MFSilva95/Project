package middleHealth.bluetoothHDP;


import middleHealth.es.libresoft.openhealth.Agent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHealth;
import android.bluetooth.BluetoothHealthAppConfiguration;
import android.bluetooth.BluetoothHealthCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.ParcelFileDescriptor;

public class HDPManager {
	private BluetoothAdapter mbluetoothAdapter;
	private BluetoothHealthAppConfiguration mAppconfig;
	private BluetoothHealth mbluetoothhealth;
	private Context mAppContext;
	private final int SCALE = 0x100f;
	private final int BLOOD_MONITOR = 0x1007;
	 
	 public HDPManager(Context context){
		 mbluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		 mAppContext = context;
	     boolean result = mbluetoothAdapter.getProfileProxy(context,mBluetoothServiceListener, BluetoothProfile.HEALTH);
	     if(result == true){
	        System.out.println("fetching profile: OK");
	     }
		 
	 }
	 //######################################################################################
	 
	 private final BluetoothProfile.ServiceListener mBluetoothServiceListener = new BluetoothProfile.ServiceListener() {
			public void onServiceConnected(int profile, BluetoothProfile proxy) {
				if (profile == BluetoothProfile.HEALTH) {
					mbluetoothhealth = (BluetoothHealth) proxy;
					//List<BluetoothDevice> connectedDevs = mbluetoothhealth.getConnectedDevices();
					;
					boolean resultreg = mbluetoothhealth.registerSinkAppConfiguration("Scale", SCALE, mHealthCallback);
					mbluetoothhealth.registerSinkAppConfiguration("blood_monitor", BLOOD_MONITOR, mHealthCallback);
					if(resultreg == true){
						System.out.println("registering app: OK");
					}
				}
			}

			@Override
			public void onServiceDisconnected(int profile) {
				// TODO Auto-generated method stub
				mbluetoothhealth = null;
			}
	    };
	    
	    //#########################################################################################
	    
	    private final BluetoothHealthCallback mHealthCallback = new BluetoothHealthCallback() {
			// Callback to handle application registration and unregistration events. The service
			// passes the status back to the UI client.
			public void onHealthAppConfigurationStatusChange(BluetoothHealthAppConfiguration config, int status) {
				//super.onHealthAppConfigurationStatusChange(config,status);

			}
			// Callback to handle channel connection state changes.
			// Note that the logic of the state machine may need to be modified based on the HDP device.
			// When the HDP device is connected, the received file descriptor is passed to the
			// ReadThread to read the content.
			public void onHealthChannelStateChange(BluetoothHealthAppConfiguration config, BluetoothDevice device, int prevState, int newState, ParcelFileDescriptor fd, int channelId) {
				//super.onHealthChannelStateChange(config, device, prevState, newState, fd, channelId);
				//outs.setText("did an agent just connected?");
				if(prevState == BluetoothHealth.STATE_CHANNEL_DISCONNECTED && newState == BluetoothHealth.STATE_CHANNEL_CONNECTED){
					//outs.setText("aww ysss we're getting somewhere");
					BluetoothHDPChannel cHDP = null; 
					try {
						cHDP = new BluetoothHDPChannel(fd);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(cHDP != null){
						Agent a = new Agent();
						a.addChannel(cHDP);
						a.setAddress(device.getAddress());
						System.out.println("added a new Agent!");
					}
					
					
				}
				
				

			}
		};
	 
	 
	 
	 
	 
	 
}
