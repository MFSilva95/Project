package middleHealth.es.libresoft.openhealth.android;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

public class MyActivity2 extends Activity{
	
	IApplicationRegister mAppRegister;
	IDevice mDevice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		Intent intent = new Intent(IApplicationRegister.class.getName());
		Intent intent2 = new Intent(IDevice.class.getName());
		bindService(intent,this.connection,Context.BIND_AUTO_CREATE);
		bindService(intent2,this.connection2 , Context.BIND_AUTO_CREATE);
		startService(new Intent(this,MiddleHealthService.class));
	}	
	
	
	protected void onStart(){
		super.onStart();
		
	}
	
	
	
	private ServiceConnection connection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mAppRegister = IApplicationRegister.Stub.asInterface(service);
			try {
				mAppRegister.registerApplication(mCallbacks);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		@Override
		public void onServiceDisconnected(ComponentName name){
			System.out.println("service disconnected");
			
		}
		
	};
	
	private ServiceConnection connection2 = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mDevice = IDevice.Stub.asInterface(service);
			
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	
	
	/************************************************************
	 * Deal with callbacks
	 ************************************************************/
	
	private final IEventCallback.Stub mCallbacks = new IEventCallback.Stub() {
		
		@Override
		public void deviceDisconnected(String systemID) throws RemoteException {
			// TODO Auto-generated method stub
			System.out.println("FROM ACTIVITY: DEVICE DISCONNECTED");
			
		}
		
		@Override
		public void deviceConnected(String systemID) throws RemoteException {
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
		public void deviceChangeStatus(String systemID, String prevState,
				String newState) throws RemoteException {
			System.out.println("FROM ACTIVITY: STATE CHANGE " + prevState + " -> " + newState);
			// TODO Auto-generated method stub
			
			
		}
		
		@Override
		public void MeasureReceived(String systemID, AndroidMeasure m)
				throws RemoteException {
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
	};
	
}
