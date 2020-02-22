package dealwithusmailcom.dwsales.Activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dealwithusmailcom.dwsales.Config.APIUrls;
import dealwithusmailcom.dwsales.ServiceReciver.AlarmReceiver;
import dealwithusmailcom.dwsales.Config.AppController;
import dealwithusmailcom.dwsales.Global;
import dealwithusmailcom.dwsales.R;

public class LeadInfoFrom extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText edtAssign, edtDetail, edtRating, edtRemark;
    Button btnSubmit;
    ProgressDialog pdLoading;
    String [] status;
    Spinner spnStatus;
    int stts = 0;
    final static int RQS_1 = 1;
    ArrayAdapter aa;
    String DateAndTime="";
    String CurrentTimeStamp="";
    SimpleDateFormat Sdf =new SimpleDateFormat("yyyy-MM-dd");

    private Button mButton;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_leadinfo);
        getStatus();

        pdLoading = new ProgressDialog(LeadInfoFrom.this);
        edtAssign = findViewById(R.id.AssignPerson);
        edtDetail = findViewById(R.id.ldetails);
        edtRating = findViewById(R.id.lratting);
        edtRemark = findViewById(R.id.lRemark);
        btnSubmit = findViewById(R.id.LeadSubmit);
        spnStatus = findViewById(R.id.status);

        edtAssign.setVisibility(View.GONE);

        spnStatus.setOnItemSelectedListener(this);
        getIntent().getStringExtra("lid");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            validation();
            }
        });

    }

    private void getStatus() {
        final ProgressDialog dialog =new ProgressDialog(LeadInfoFrom.this);
        dialog.setMessage("Loading...");
        dialog.show();
        StringRequest stringRequest =new StringRequest(Request.Method.POST, APIUrls.leadStatus, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject jsonObject =new JSONObject(response);
                    if(jsonObject.getBoolean("status")){
                       // JSONObject object =jsonObject.getJSONObject("result");
                        String s = jsonObject.getString("result").replace("[", "").replace("]", "").replace("\"", "");
                        String[] stst = s.split("\\s*,\\s*");
                       aa = new ArrayAdapter(LeadInfoFrom.this, android.R.layout.simple_spinner_item, stst);
                        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnStatus.setAdapter(aa);
                        System.out.println("status" + Arrays.toString(stst));
                    }else if (jsonObject.has("error")){

                        JSONArray array = jsonObject.getJSONArray("error");
                        AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());
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
                    } else if (jsonObject.has("result")){
                        //  JSONObject jsonObject = object.getJSONObject("result");
                        Global.SessionExpired(getApplicationContext());
                    }else {
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(LeadInfoFrom.this);
                        builderInner.setMessage(jsonObject.getString("message"));
                        builderInner.setTitle("Data loading Error");
                        builderInner.setIcon(android.R.drawable.ic_dialog_alert);
                        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int which) {
                                dialog.dismiss();
                            }
                        });
                        builderInner.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();

            }
        })
        {
            @Override
            protected Map<String, String> getParams(){

                Map<String,String>  params =new HashMap<>();
                params.put("token", Global.token);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    private void validation() {


        String regx = "^[\\p{L} .'-]+$";
        Pattern paname = Pattern.compile(regx);
        Matcher matchname = paname.matcher(edtAssign.getText().toString());

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(edtDetail.getText().toString())) {
            edtDetail.setError(getString(R.string.error_field_required));
            focusView = edtDetail;
            cancel = true;
        } else if (TextUtils.isEmpty(edtRemark.getText().toString())) {
            edtRemark.setError(getString(R.string.error_field_required));
            focusView = edtRemark;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {

                if (stts == 3 || stts == 5){
                    DateAndTime = Sdf.format(new Date());
                }

            if (stts == 4 || stts == 2) {

                final Dialog Adialog = new Dialog(LeadInfoFrom.this);
                Adialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                Adialog.setCancelable(false);
                Adialog.setContentView(R.layout.activity_alarmact);

                final DatePicker pickerDate;
                final TimePicker pickerTime;
                final Button buttonSetAlarm , selectDate;

                pickerDate = Adialog.findViewById(R.id.pickerdate);
                pickerTime = Adialog.findViewById(R.id.pickertime);
                buttonSetAlarm = Adialog.findViewById(R.id.setalarm);
                selectDate = Adialog.findViewById(R.id.selectadate);

                Calendar now = Calendar.getInstance();


                selectDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pickerDate.setVisibility(View.GONE);
                        selectDate.setVisibility(View.GONE);
                        pickerTime.setVisibility(View.VISIBLE);
                        buttonSetAlarm.setVisibility(View.VISIBLE);
                    }
                });

                pickerDate.init(
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH),
                        null);

                pickerTime.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));
                pickerTime.setCurrentMinute(now.get(Calendar.MINUTE));

                buttonSetAlarm.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View arg0) {
                        Calendar current = Calendar.getInstance();



                        Calendar cal = Calendar.getInstance();
                        cal.set(pickerDate.getYear(),
                                pickerDate.getMonth(),
                                pickerDate.getDayOfMonth(),
                                pickerTime.getCurrentHour(),
                                pickerTime.getCurrentMinute(),
                                00);

                        Calendar newDate = Calendar.getInstance();
                        newDate.set(pickerDate.getYear(), pickerDate.getMonth(), pickerDate.getDayOfMonth());


                        DateAndTime =new SimpleDateFormat("yyyy-MM-dd").format(newDate.getTime());
                        if(cal.compareTo(current) <= 0){
                            //The set Date/Time already passed
                            Toast.makeText(getApplicationContext(),"Invalid Date/Time", Toast.LENGTH_LONG).show();
                        }else{
                            setAlarm(cal);
                            Adialog.dismiss();

                            UserAddTask(
                                    Global.token,
                                    getIntent().getStringExtra("lid"),
                                    edtDetail.getText().toString(),
                                    edtRemark.getText().toString(),
                                    String.valueOf(stts),
                                    DateAndTime,
                                    CurrentTimeStamp,
                                    Global.customer_id
                            );

                        }

                    }});
                Adialog.show();
            }else {
                 UserAddTask(
                        Global.token,
                        getIntent().getStringExtra("lid"),
                        edtDetail.getText().toString(),
                        edtRemark.getText().toString(),
                        String.valueOf(stts),
                        DateAndTime,
                        CurrentTimeStamp,
                        Global.customer_id
                );

            }


        }

    }

    private void UserAddTask(final String token, final String lid, final String edtDetail, final String edtRemark, final String stts, final String dateAndTime, final String currentTimeStamp, final String customer_id) {
       final ProgressDialog progressBar =new ProgressDialog(LeadInfoFrom.this);
        progressBar.setMessage("Submitting...");
        progressBar.show();
        StringRequest request=new StringRequest(Request.Method.POST, APIUrls.LeadInfoIncert, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {

                    AlertDialog.Builder builderInner = new AlertDialog.Builder(LeadInfoFrom.this);
                    builderInner.setMessage(object.getString("result"));
                    builderInner.setTitle("SUCCESS");
                    builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,int which) {
                            Intent in = new Intent(getApplicationContext(), ActivityMain.class);
                            startActivity(in);
                            finish();
                        }
                    });
                    builderInner.show();




                } else if (object.has("error")){

                    JSONArray array = object.getJSONArray("error");
                    AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());
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
                    Global.SessionExpired(LeadInfoFrom.this);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.dismiss();
                //  refreshLayout.setRefreshing(false);
            }
        }){
            @Override
            protected Map<String, String> getParams(){

                Map<String,String>  params =new HashMap<>();

                params.put("token", token);
                params.put("lead_id", lid);
                params.put("details", edtDetail);
                params.put("remarks", edtRemark);
                params.put("status", stts);
                params.put("schedule_time",dateAndTime );
                params.put("meeting", "");
                params.put("employee_id", customer_id);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int postion, long l) {
        stts = postion;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    @Override
    public void onPause() {
        super.onPause();
        if (pdLoading != null)
            pdLoading.dismiss();
    }

    private void setAlarm(Calendar targetCal){
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        System.out.println(edtRemark.getText().toString());
        intent.putExtra("remark",edtRemark.getText().toString());
        final int _id = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(),_id , intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);


    }
}