package dealwithusmailcom.dwsales.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import dealwithusmailcom.dwsales.Adapter.ExpandableListAdapter;
import dealwithusmailcom.dwsales.Adapter.MeetingAdapter;
import dealwithusmailcom.dwsales.Config.APIUrls;
import dealwithusmailcom.dwsales.Config.AppController;
import dealwithusmailcom.dwsales.Global;
import dealwithusmailcom.dwsales.Model.LeadModel;
import dealwithusmailcom.dwsales.R;

public class Executive_dashboard extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    RecyclerView recyclerView, todayList;
    ExpandableListAdapter listAdapter;
    Button dashleadbydate ,tdmlist;
    LinearLayout leadbyidlayout , tdmlayout ;
    ProgressDialog progressDialog;
    TextView notdmList ;
    ArrayList<LeadModel> List =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_executive_dashboard);
        setTitle(getIntent().getStringExtra("name")+" "+ "Dashbaord");
        progressDialog =new ProgressDialog(Executive_dashboard.this);
        recyclerView= findViewById(R.id.dashAssign);
        todayList=findViewById(R.id.rvtdMeeting);
        dashleadbydate = findViewById(R.id.dashleadbydate);
        recyclerView.setLayoutManager(new LinearLayoutManager(Executive_dashboard.this));
        todayList.setLayoutManager(new LinearLayoutManager(Executive_dashboard.this));
        notdmList = findViewById(R.id.notdmList);
        leadbyidlayout = findViewById(R.id.dashleadbydatelayout);
        tdmlayout = findViewById(R.id.tdmlistlayout);
        tdmlist = findViewById(R.id.tdmlist);

        Calendar call =Calendar.getInstance();
        final DatePickerDialog datePickerDialog = new DatePickerDialog(Executive_dashboard.this, Executive_dashboard.this, call.get(Calendar.YEAR), call.get(Calendar.MONTH), call.get(Calendar.DAY_OF_MONTH));

        dashleadbydate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        AssignLeads();

        tdmlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMeetingData();
            }
        });

    }

    private void AssignLeads() {


        progressDialog.setMessage("Loading...");
        progressDialog.show();
        final ArrayList<LeadModel> listDataHeader = new ArrayList<>();
        StringRequest request = new StringRequest(StringRequest.Method.POST, APIUrls.Assignlist, new Response.Listener < String > () {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();

                try {


                    JSONObject object =new JSONObject(response);

                    if (object.getBoolean("status")){

                        JSONArray array=object.getJSONArray("result");

                        for (int i = 0 ; i < array.length() ; i++ ){

                            JSONObject jsonObject=array.getJSONObject(i);
                            LeadModel model=new LeadModel();

                            model.LeadDate = jsonObject.getString("created_at");
                            model.LeadId = jsonObject.getString("lead_id");
                            model.LeadLocation = jsonObject.getString("location");
                            model.LeadName = jsonObject.getString("name");
                            model.LeadMobile = jsonObject.getString("mobile");
                            model.Leademail =jsonObject.getString("email");
                            model.LeadRemark =jsonObject.getString("remarks");
                            model.LeadRating =jsonObject.getString("rating");
                            model.LeadStatus =jsonObject.getString("status");

                            listDataHeader.add(model);
                        }}else if (object.has("error")){

                        JSONArray array = object.getJSONArray("error");
                        AlertDialog.Builder alert = new AlertDialog.Builder(Executive_dashboard.this);
                        alert.setCancelable(false);
                        alert.setMessage(String.valueOf(array.get(0)));
                        alert.setTitle("Error!");
                        alert.setIcon(android.R.drawable.ic_dialog_alert);
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                        alert.show();
                    } else if (object.has("result")){
                        //  JSONObject jsonObject = object.getJSONObject("result");
                        Global.SessionExpired(Executive_dashboard.this);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listAdapter = new ExpandableListAdapter(Executive_dashboard.this, listDataHeader , 3);
                recyclerView.setAdapter(listAdapter);

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
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(Executive_dashboard.this);
        builderSingle.setTitle("Select Meeting Type");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Executive_dashboard.this, android.R.layout.simple_spinner_dropdown_item);


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
                intent.putExtra("id",getIntent().getStringExtra("id") );
                intent.putExtra("mode",2 );
                startActivity(intent);


            }
        });
        builderSingle.show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    private void getMeetingData() {
        tdmlist.setVisibility(View.GONE);
        leadbyidlayout.setVisibility(View.GONE);
        tdmlayout.setVisibility(View.VISIBLE);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest request = new StringRequest(StringRequest.Method.POST,APIUrls.Today_ScheduleMeeting_List, new Response.Listener < String > () {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();



                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);


                    if (jsonObject.getBoolean("status")) {

                        if (!jsonObject.isNull("result")) {

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
                                        View view = LayoutInflater.from(Executive_dashboard.this).inflate(R.layout.meeting, parent, false);
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
                                    builder = new AlertDialog.Builder(Executive_dashboard.this, android.R.style.Theme_Material_Dialog_Alert);
                                } else {
                                    builder = new AlertDialog.Builder(Executive_dashboard.this);
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
                            Global.SessionExpired(Executive_dashboard.this);
                        }else {

                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(Executive_dashboard.this, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(Executive_dashboard.this);
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
                param.put("schedule_time",new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));

                param.put("status", "1");

                return param;
            }
        };

        AppController.getInstance().addToRequestQueue(request);

    }

}
