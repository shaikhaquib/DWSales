package dealwithusmailcom.dwsales.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dealwithusmailcom.dwsales.Config.APIUrls;
import dealwithusmailcom.dwsales.Config.AppController;
import dealwithusmailcom.dwsales.Global;
import dealwithusmailcom.dwsales.Model.LeadModel;
import dealwithusmailcom.dwsales.R;

public class MeetingList extends AppCompatActivity {

    String status;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    ArrayList<LeadModel> List =new ArrayList<>();
    String Url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_list);

        progressDialog = new ProgressDialog(MeetingList.this);
        setTitle("Scheduled Meetings");
        status = getIntent().getStringExtra("status");

        System.out.println(getIntent().getStringExtra("status"));

        recyclerView = findViewById(R.id.rvSchedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        if (getIntent().getIntExtra("mode",1)==1){
            getData();
        }else
            if (getIntent().getIntExtra("mode",1)==2){
            Url = APIUrls.Today_ScheduleMeeting_List;
                getsCHEData();
        }

    }

    private void getData() {
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest request = new StringRequest(StringRequest.Method.POST,APIUrls.ScheduleMeeting_List, new Response.Listener < String > () {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();


                try {
                  JSONObject  jsonObject = new JSONObject(response);


                    if (jsonObject.getBoolean("status")) {

                        if (!jsonObject.isNull("result")) {
                            // Note, not `getJSONArray` or any of that.
                            // This will give us whatever's at "URL", regardless of its type.
                            Object item = jsonObject.get("result");

                            // `instanceof` tells us whether the object can be cast to a specific type
                            if (item instanceof JSONArray) {
                                //  System.out.println("it's an array");
                                JSONArray jsonArray = jsonObject.getJSONArray("result");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    LeadModel leadModel = new LeadModel();

                                    leadModel.LeadMobile = object.getString("mobile");
                                    leadModel.LeadName = object.getString("name");
                                    leadModel.LeadLocation = object.getString("location");
                                    leadModel.LeadRemark = object.getString("remarks");
                                    leadModel.Leademail = object.getString("email");
                                    leadModel.LeadId = object.getString("id");
                                    leadModel.LeadRating = object.getString("rating");

                                    List.add(leadModel);
                                }
                                recyclerView.setAdapter(new RecyclerView.Adapter() {
                                    @NonNull
                                    @Override
                                    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                        View view = LayoutInflater.from(MeetingList.this).inflate(R.layout.meeting, parent, false);
                                        Holder holder = new Holder(view);
                                        return holder;
                                    }

                                    @Override
                                    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                                        final Holder hold = (Holder) holder;
                                        final LeadModel current = List.get(position);

                                        hold.name.setText(current.LeadName);
                                        hold.no.setText(current.LeadMobile);
                                        hold.location.setText(current.LeadLocation);
                                        hold.Remark.setText(current.LeadRemark);

                                        //hold.location.setText();

                                    }

                                    @Override
                                    public int getItemCount() {
                                        return List.size();
                                    }

                                    class Holder extends RecyclerView.ViewHolder {

                                        TextView name, no, location, Remark;

                                        public Holder(@NonNull View itemView) {
                                            super(itemView);

                                            name = itemView.findViewById(R.id.mtname);
                                            no = itemView.findViewById(R.id.mtPhone);
                                            location = itemView.findViewById(R.id.mtLocation);
                                            Remark = itemView.findViewById(R.id.mtRemark);

                                        }
                                    }

                                });
                            } else {
                                // if you know it's either an array or an object, then it's an object
                                System.out.println("it's an Object");

                                AlertDialog.Builder builder;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    builder = new AlertDialog.Builder(MeetingList.this, android.R.style.Theme_Material_Dialog_Alert);
                                } else {
                                    builder = new AlertDialog.Builder(MeetingList.this);
                                }
                                builder.setCancelable(false);
                                builder.setTitle("Error")
                                        .setMessage(jsonObject.getString("result"))
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                        }
                    }else {


                        Object json = new JSONTokener(response).nextValue();
                        if (json instanceof JSONObject){
                            Global.SessionExpired(MeetingList.this);
                        }else {

                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(MeetingList.this, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(MeetingList.this);
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
                param.put("employee_id", getIntent().getStringExtra("id"));
                param.put("status", status);

                return param;
            }
        };

        AppController.getInstance().addToRequestQueue(request);

    }
    private void getsCHEData() {
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest request = new StringRequest(StringRequest.Method.POST,Url, new Response.Listener < String > () {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();



                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response);


                        if (jsonObject.getBoolean("status")) {

                            if (!jsonObject.isNull("result")) {
                                // Note, not `getJSONArray` or any of that.
                                // This will give us whatever's at "URL", regardless of its type.
                                Object item = jsonObject.get("result");

                                // `instanceof` tells us whether the object can be cast to a specific type
                                if (item instanceof JSONArray) {
                                    //  System.out.println("it's an array");
                                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);

                                        LeadModel leadModel = new LeadModel();

                                        leadModel.LeadMobile = object.getString("mobile");
                                        leadModel.LeadName = object.getString("name");
                                        leadModel.LeadLocation = object.getString("location");
                                        leadModel.LeadRemark = object.getString("remarks");
                                        leadModel.Leademail = object.getString("email");
                                        leadModel.LeadId = object.getString("id");
                                        leadModel.LeadRating = object.getString("rating");

                                        List.add(leadModel);
                                    }
                                    recyclerView.setAdapter(new RecyclerView.Adapter() {
                                        @NonNull
                                        @Override
                                        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                            View view = LayoutInflater.from(MeetingList.this).inflate(R.layout.meeting, parent, false);
                                            Holder holder = new Holder(view);
                                            return holder;
                                        }

                                        @Override
                                        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                                            final Holder hold = (Holder) holder;
                                            final LeadModel current = List.get(position);

                                            hold.name.setText(current.LeadName);
                                            hold.no.setText(current.LeadMobile);
                                            hold.location.setText(current.LeadLocation);
                                            hold.Remark.setText(current.LeadRemark);

                                            //hold.location.setText();

                                        }

                                        @Override
                                        public int getItemCount() {
                                            return List.size();
                                        }

                                        class Holder extends RecyclerView.ViewHolder {

                                            TextView name, no, location, Remark;

                                            public Holder(@NonNull View itemView) {
                                                super(itemView);

                                                name = itemView.findViewById(R.id.mtname);
                                                no = itemView.findViewById(R.id.mtPhone);
                                                location = itemView.findViewById(R.id.mtLocation);
                                                Remark = itemView.findViewById(R.id.mtRemark);

                                            }
                                        }

                                    });
                                } else {
                                    // if you know it's either an array or an object, then it's an object
                                    System.out.println("it's an Object");

                                    AlertDialog.Builder builder;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        builder = new AlertDialog.Builder(MeetingList.this, android.R.style.Theme_Material_Dialog_Alert);
                                    } else {
                                        builder = new AlertDialog.Builder(MeetingList.this);
                                    }
                                    builder.setCancelable(false);
                                    builder.setTitle("Error")
                                            .setMessage(jsonObject.getString("result"))
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }
                                            })
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();
                                }
                            }
                        }else {


                        Object json = new JSONTokener(response).nextValue();
                        if (json instanceof JSONObject){
                            Global.SessionExpired(MeetingList.this);
                        }else {

                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(MeetingList.this, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(MeetingList.this);
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
                param.put("employee_id",getIntent().getStringExtra("id"));
                param.put("schedule_time",getIntent().getStringExtra("date"));
                param.put("status", status);

                return param;
            }
        };

        AppController.getInstance().addToRequestQueue(request);

    }

}
