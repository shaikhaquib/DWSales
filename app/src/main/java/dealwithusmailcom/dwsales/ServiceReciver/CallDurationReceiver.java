package dealwithusmailcom.dwsales.ServiceReciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class CallDurationReceiver extends BroadcastReceiver {

    static boolean flag = false;
    static long start_time, end_time;

    @Override
    public void onReceive(Context arg0, Intent intent) {
        String action = intent.getAction();
        if (action.equalsIgnoreCase("android.intent.action.PHONE_STATE")) {
            if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
                    TelephonyManager.EXTRA_STATE_RINGING)) {
                start_time = System.currentTimeMillis();
            }
            if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
                    TelephonyManager.EXTRA_STATE_IDLE)) {
                end_time = System.currentTimeMillis();
                //Total time talked =
                long total_time = end_time - start_time;
                //Store total_time somewhere or pass it to an Activity using intent

                System.out.println("total_time ="+ end_time + start_time +"="+total_time);
            }
        }
    }
}