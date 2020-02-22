package dealwithusmailcom.dwsales.ServiceReciver;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import dealwithusmailcom.dwsales.Config.APIUrls;
import dealwithusmailcom.dwsales.Config.AppController;
import dealwithusmailcom.dwsales.Global;
import dealwithusmailcom.dwsales.SQLiteHandler;

public class GpsService extends Service {
    private Timer timer;
    private  long UPDATE_INTERVAL ;
    public static final String Stub = null;
    LocationManager mlocmag;
    LocationListener mlocList ;
    private double lat,longn;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private SQLiteHandler db;
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    private FusedLocationProviderClient client;


    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        //    webService = new WebService();
            mlocmag = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location loc = mlocmag.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (loc == null) {
                loc = mlocmag.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        timer  = new Timer();       // location.
        UpdateWithNewLocation(loc); // This method is used to get updated
        //mlocmag.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,mlocList);

        db = new SQLiteHandler(getApplicationContext());

        HashMap<String, String> user;
        user = db.getUserDetails();
        Global.customer_id = user.get("uid");
        Global.userId = user.get("uid");
    }

    @Override
    public IBinder onBind(Intent arg0) { return null; }



    @SuppressLint("MissingPermission")
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
           // timer.cancel();
            Log.d("Destroy","Service has Destroy");
        }
        //mlocmag.removeUpdates(mlocList);
    }

    @Override
    public boolean stopService(Intent name) {
        Log.d("Destroy","Service has Destroy");
        return super.stopService(name);
    }

    private void UpdateWithNewLocation(final Location loc) {
        UPDATE_INTERVAL = 60000 * 10;

        timer.scheduleAtFixedRate(new TimerTask() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                if (loc != null) {

                    client = LocationServices.getFusedLocationProviderClient(GpsService.this);
                    client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            System.out.println("MyLocation :"+location);
                            incertLocation(location.getLatitude() , location.getLongitude());
                        }
                    });

                }

                else {
                    String latLongStr = "No lat and longitude found";
                }

            }
        }, 0, UPDATE_INTERVAL);
    }

    private void incertLocation(final double latitude, final double longitude) {
        StringRequest request = new StringRequest(Request.Method.POST, APIUrls.LocationIncert, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject =new JSONObject(response);

                    if (jsonObject.getBoolean("status")){
                        Log.d("Success",jsonObject.getString("result"));
                    }else {
                        Log.d("Error",jsonObject.getString("result"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams(){

                Map<String,String>  params =new HashMap<>();
//                params.put("token", Global.token);
                params.put("employee_id", Global.userId);
                params.put("status", "1");
                params.put("date",Global.date);
                params.put("current_location", latitude +","+longitude);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    /** Checks whether two providers are the same */

}