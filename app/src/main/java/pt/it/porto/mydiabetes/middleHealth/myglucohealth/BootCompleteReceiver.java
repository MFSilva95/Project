package pt.it.porto.mydiabetes.middleHealth.myglucohealth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(BluetoothChangesReceiver.TAG, "bootcompleteReceived receive");
        BluetoothChangesRegisterService.startService(context);
    }
}
