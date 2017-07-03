//package pt.it.porto.mydiabetes.middleHealth.myglucohealth;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//
//import pt.it.porto.mydiabetes.BuildConfig;
//
//public class BootCompleteReceiver extends BroadcastReceiver {
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if(BuildConfig.GLUCOMETER_AVAILABLE) {
//            Log.d(BluetoothChangesReceiver.TAG, "bootcompleteReceived receive");
//            BluetoothChangesRegisterService.startService(context);
//        }
//    }
//}
