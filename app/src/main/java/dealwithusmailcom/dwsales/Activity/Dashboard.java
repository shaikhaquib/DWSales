package dealwithusmailcom.dwsales.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.CircularPropagation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dealwithusmailcom.dwsales.Adapter.MeetingAdapter;
import dealwithusmailcom.dwsales.Config.APIUrls;
import dealwithusmailcom.dwsales.Config.AppController;
import dealwithusmailcom.dwsales.Global;
import dealwithusmailcom.dwsales.Model.LeadModel;
import dealwithusmailcom.dwsales.R;

public class Dashboard extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    RecyclerView Callinglist , meetinglist ;
    ProgressDialog progressDialog;
    CircularProgressBar circularProgressBar;
    TextView sheCall ,sheMeeting ,txterrorcall , txtmeeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        progressDialog = new ProgressDialog(this);
        Callinglist = findViewById(R.id.rvCall);

        Callinglist.setLayoutManager(new LinearLayoutManager(Dashboard.this));

        meetinglist = findViewById(R.id.rvMeeting);

        meetinglist.setLayoutManager(new LinearLayoutManager(Dashboard.this));

        circularProgressBar =findViewById(R.id.consumprogrss);
        sheCall=findViewById(R.id.scheCall);
        sheMeeting=findViewById(R.id.scheMeeting);
        txtmeeting=findViewById(R.id.txterror);
        txterrorcall=findViewById(R.id.txterr);


        sheCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getApplicationContext(),MeetingList.class);
                intent.putExtra("date","" );
                intent.putExtra("mode",1 );
                intent.putExtra("id",Global.userId);
                intent.putExtra("status","2");

                startActivity(intent);
            }
        });

        sheMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getApplicationContext(),MeetingList.class);
                intent.putExtra("date","" );
                intent.putExtra("mode",1 );
                intent.putExtra("id",Global.userId);
                intent.putExtra("status","4");

                startActivity(intent);
            }
        });


        getCallingData();
        getMeetingData();
        getCompletedmeetinglist();

        Calendar call =Calendar.getInstance();
        final DatePickerDialog datePickerDialog = new DatePickerDialog(Dashboard.this, Dashboard.this, call.get(Calendar.YEAR), call.get(Calendar.MONTH), call.get(Calendar.DAY_OF_MONTH));

        final FloatingActionButton floatingActionButton = findViewById(R.id.dashdatepicker);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
    }

    private void getCallingData() {
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest request = new StringRequest(StringRequest.Method.POST, APIUrls.monthlylist, new Response.Listener < String > () {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();

                ArrayList<LeadModel> CList =new ArrayList<>();

                try {
                    JSONObject  jsonObject = new JSONObject(response);


                    if (jsonObject.getBoolean("status")) {

                        if (!jsonObject.isNull("result")) {
                            // Note, not `getJSONArray` or any of that.
                            // This will give us whatever's at "URL", regardless of its type.
                            Object item = jsonObject.get("result");

                            // `instanceof` tells us whether the object can be cast to a specific type
                            if (item instanceof JSONArray) {

                                JSONArray jsonArray =  jsonObject.getJSONArray("result");

                                for (int i = 0 ; i < jsonArray.length() ; i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    LeadModel leadModel =new LeadModel();

                                    leadModel.LeadMobile = object.getString("mobile");
                                    leadModel.LeadName = object.getString("name");
                                    leadModel.LeadLocation = object.getString("location");
                                    leadModel.LeadRemark = object.getString("remarks");
                                    leadModel.Leademail = object.getString("email");
                                    leadModel.LeadId = object.getString("id");
                                    leadModel.LeadRating = object.getString("rating");

                                    CList.add(leadModel);
                                }
                                System.out.println(CList.size());
                                MeetingAdapter listAdapter =new MeetingAdapter(Dashboard.this,CList);
                                Callinglist.setAdapter(listAdapter);
                            } else {
                                // if you know it's either an array or an object, then it's an object
                                System.out.println("it's an Object");

                                txterrorcall.setVisibility(View.VISIBLE);
                                txterrorcall.setText(jsonObject.getString("result"));

                            }
                        }
                    }else {


                        Object json = new JSONTokener(response).nextValue();
                        if (json instanceof JSONObject){
                            Global.SessionExpired(Dashboard.this);
                        }else {

                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(Dashboard.this, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(Dashboard.this);
                            }
                            builder.setCancelable(false);
                            builder.setTitle("Error")
                                    .setMessage(jsonObject.getString("result"))
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    }


                }

                catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map <String,String> param = new HashMap<>();
                param.put("token", Global.token);
                param.put("employee_id", Global.userId);
                param.put("status", "2");

                return param;
            }
        };

        AppController.getInstance().addToRequestQueue(request);

    }

    private void getMeetingData() {
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest request = new StringRequest(StringRequest.Method.POST, APIUrls.monthlylist, new Response.Listener < String > () {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();

                ArrayList<LeadModel> MList =new ArrayList<>();



                try {
                    JSONObject  jsonObject = new JSONObject(response);


                    if (jsonObject.getBoolean("status")) {

                        if (!jsonObject.isNull("result")) {
                            // Note, not `getJSONArray` or any of that.
                            // This will give us whatever's at "URL", regardless of its type.
                            Object item = jsonObject.get("result");

                            // `instanceof` tells us whether the object can be cast to a specific type
                            if (item instanceof JSONArray) {

                                JSONArray jsonArray =  jsonObject.getJSONArray("result");

                                for (int i = 0 ; i < jsonArray.length() ; i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    LeadModel leadModel =new LeadModel();

                                    leadModel.LeadMobile = object.getString("mobile");
                                    leadModel.LeadName = object.getString("name");
                                    leadModel.LeadLocation = object.getString("location");
                                    leadModel.LeadRemark = object.getString("remarks");
                                    leadModel.Leademail = object.getString("email");
                                    leadModel.LeadId = object.getString("id");
                                    leadModel.LeadRating = object.getString("rating");

                                    MList.add(leadModel);
                                }
                                System.out.println(MList.size());
                                MeetingAdapter listAdapter =new MeetingAdapter(Dashboard.this,MList);
                                meetinglist.setAdapter(listAdapter);
                            } else {
                                // if you know it's either an array or an object, then it's an object
                                System.out.println("it's an Object");

                                txtmeeting.setVisibility(View.VISIBLE);
                                txtmeeting.setText(jsonObject.getString("result"));

                            }
                        }
                    }else {


                        Object json = new JSONTokener(response).nextValue();
                        if (json instanceof JSONObject){
                            Global.SessionExpired(Dashboard.this);
                        }else {

                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(Dashboard.this, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(Dashboard.this);
                            }
                            builder.setCancelable(false);
                            builder.setTitle("Error")
                                    .setMessage(jsonObject.getString("result"))
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    }


                }

                catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map <String,String> param = new HashMap<>();
                param.put("token", Global.token);
                param.put("employee_id", Global.userId);
                param.put("status", "4");

                return param;
            }
        };

        AppController.getInstance().addToRequestQueue(request);

    }

    private void getCompletedmeetinglist() {
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest request = new StringRequest(StringRequest.Method.POST, APIUrls.ScheduleMeeting_List, new Response.Listener < String > () {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();


                try {

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getBoolean("status")) {

                        JSONArray jsonArray =  jsonObject.getJSONArray("result");

                        for (int i = 0 ; i < jsonArray.length() ; i++){
                            JSONObject object = jsonArray.getJSONObject(i);

                            double progress =((double) i/125)*100;

                            circularProgressBar.setProgress((float) progress);

                        }

                    }else {
                        Object json = new JSONTokener(response).nextValue();
                        if (json instanceof JSONObject){
                            Global.SessionExpired(Dashboard.this);
                        }else {

                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(Dashboard.this, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(Dashboard.this);
                            }
                            builder.setCancelable(false);
                            builder.setTitle("Error")
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
            protected Map<String, String> getParams()  {
                Map <String,String> param = new HashMap<>();
                param.put("token", Global.token);
                param.put("employee_id", Global.userId);
                param.put("status", "5");

                return param;
            }
        };

        AppController.getInstance().addToRequestQueue(request);

    }


    @Override
    public void onDateSet(DatePicker datePicker, final int i, int i1, int i2) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(i, i1, i2);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
         final String date = format.format(calendar.getTime());
        System.out.println(date);
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(Dashboard.this);
        builderSingle.setTitle("Select Meeting Type");
        
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Dashboard.this, android.R.layout.simple_spinner_dropdown_item);

       
        arrayAdapter.add("Scheduled Call");
        arrayAdapter.add("Scheduled Meeting");
       

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                
                String stts = null;

                String strName = arrayAdapter.getItem(which);
                
                if (strName.equals("Scheduled Call")){
                    stts ="2";
                }else if (strName.equals("Scheduled Meeting")){
                    stts ="4";
                }
                
                Intent intent =new Intent(getApplicationContext(),MeetingList.class);
                intent.putExtra("status",stts);
                intent.putExtra("date",date );
                intent.putExtra("mode",2 );
                intent.putExtra("id",Global.userId);
                startActivity(intent);
                

            }
        });
        builderSingle.show();
    }
}