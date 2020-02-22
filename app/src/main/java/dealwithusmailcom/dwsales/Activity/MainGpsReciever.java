package dealwithusmailcom.dwsales.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.util.Log;

class MainGpsReciever  extends BroadcastReceiver {

    private double GPSLatitude;
    private double GPSLongitude;
    private double GPSSpeed;

    public void onReceive(Context arg0, Intent calledIntent) {

       GPSLatitude = calledIntent.getDoubleExtra("latitude", -1);
       GPSLongitude = calledIntent.getDoubleExtra("longitude", -1);
       GPSSpeed = calledIntent.getFloatExtra("speed", -1);

        Log.i("GPS Demo", "Lat & Long --- "+GPSLatitude+"--"+GPSLongitude);
        Log.i("GPS Demo", "Speed --- "+GPSSpeed);
//

    }
}

