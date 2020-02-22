package dealwithusmailcom.dwsales.ServiceReciver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by Shaikh Aquib on 14-May-18.
 */

public class LocationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent("com.gps.LocationReceiveService");
        i.setClass(context,GpsService.class);
        context.startService(i);
    }
}
