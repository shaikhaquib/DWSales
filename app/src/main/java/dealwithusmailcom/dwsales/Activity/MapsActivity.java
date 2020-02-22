package dealwithusmailcom.dwsales.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dealwithusmailcom.dwsales.Config.APIUrls;
import dealwithusmailcom.dwsales.Config.AppController;
import dealwithusmailcom.dwsales.Global;
import dealwithusmailcom.dwsales.Model.LocationModel;
import dealwithusmailcom.dwsales.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,DatePickerDialog.OnDateSetListener{

    private GoogleMap mMap;
    ArrayList< LocationModel> LocationList = new ArrayList<>();
    FloatingActionButton calender;
    private int mYear;
    private int mMonth;
    private int mDay;
    String date="";
    ProgressDialog progressDialog;
    DatePickerDialog datePickerDialog;
    List<LatLng> polygon = new ArrayList<>();
    String id ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        calender=findViewById(R.id.datepicker);
        mapFragment.getMapAsync(this);


        id = getIntent().getStringExtra("id");
        progressDialog = new ProgressDialog(MapsActivity.this);
        Calendar call =Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(MapsActivity.this, MapsActivity.this, call.get(Calendar.YEAR), call.get(Calendar.MONTH), call.get(Calendar.DAY_OF_MONTH));
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getLocation(mMap , APIUrls.getLocationByID);

        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
    }

       void getLocation(final GoogleMap mMap, String Url) {

        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {

                    LocationList.clear();
                    mMap.clear();

                    JSONObject jsonObject =new JSONObject(response);
                    if (jsonObject.getBoolean("status")){
                        Log.d("Success",jsonObject.getString("result"));

                        JSONArray jsonArray =jsonObject.getJSONArray("result");

                        for (int i = 0 ; i < jsonArray.length() ; i++){

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            String Latt ,Lang;


                            LocationModel  locationModel = new LocationModel();

                            locationModel.id = jsonObject1.getString("id");
                            locationModel.employee_id = jsonObject1.getString("employee_id");
                            locationModel.current_location = jsonObject1.getString("current_location");
                            locationModel.date = jsonObject1.getString("date");
                            locationModel.status = jsonObject1.getString("status");
                            locationModel.created_at = jsonObject1.getString("created_at");

                            String[] location = locationModel.current_location.split(",");

                            Double lat = Double.valueOf(location[0]);
                            Double lang = Double.valueOf(location[1]);

                            LatLng cc = new LatLng(lat,lang);
                            polygon.add(cc);

                            mMap.addMarker(new MarkerOptions().position(cc).title(getIntent().getStringExtra("name")));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(cc));
                            Marker marker = mMap.addMarker(new MarkerOptions().position(cc).title("Last seen at : " +  locationModel.created_at));
                            marker.showInfoWindow();


                            LocationList.add(locationModel);
                        }

/*
                        mMap.addPolygon(new PolygonOptions()
                                .addAll(polygon)
                                .strokeColor(Color.parseColor("#03A9F4"))
                                .strokeWidth(15)
                                .fillColor(Color.parseColor("#B3E5FC"))
                        );
*/
                    }else {
                        Log.d("Error",jsonObject.getString("result"));


                        if (jsonObject.getString("result").equals("No Data Available")){

                            AlertDialog.Builder builder;

                            builder = new AlertDialog.Builder(MapsActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                            builder.setTitle("Sorry !!")
                                    .setMessage("No data available for this day...")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }else {

                            AlertDialog.Builder builder;

                            builder = new AlertDialog.Builder(MapsActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                            builder.setTitle("Sorry !!")
                                    .setMessage(jsonObject.getString("result"))
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams(){

                Map<String,String>  params =new HashMap<>();
                params.put("token", Global.token);
                params.put("employee_id",  id);
                params.put("status", "1");
                params.put("date", date);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(i, i1, i2);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        date = format.format(calendar.getTime());
        System.out.println(date);

        LocationList.clear();
        getLocation(mMap , APIUrls.getLocationBydate);


    }
}
