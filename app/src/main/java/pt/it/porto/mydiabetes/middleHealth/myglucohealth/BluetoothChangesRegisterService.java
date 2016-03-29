package pt.it.porto.mydiabetes.middleHealth.myglucohealth;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import pt.it.porto.mydiabetes.BuildConfig;

public class BluetoothChangesRegisterService extends Service {
    BluetoothChangesReceiver receiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(BluetoothChangesReceiver.TAG, "starting");
        if (receiver == null) {
            receiver = new BluetoothChangesReceiver();
        }
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(receiver, filter1);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        Log.d(BluetoothChangesReceiver.TAG, "They kill kenny :c");
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static void startService(Context context){
        if(BuildConfig.GLUCOMETER_AVAILABLE) {
            Intent intentS = new Intent(context, BluetoothChangesRegisterService.class);
            context.getApplicationContext().startService(intentS);
        }
    }
}
