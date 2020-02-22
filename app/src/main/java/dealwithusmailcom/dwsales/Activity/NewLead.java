package dealwithusmailcom.dwsales.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dealwithusmailcom.dwsales.Config.APIUrls;
import dealwithusmailcom.dwsales.Global;
import dealwithusmailcom.dwsales.R;
import dealwithusmailcom.dwsales.SQLiteHandler;

public class NewLead extends AppCompatActivity {

    EditText ldName, ldEmail ,ldMobile ,ldLocation ,ldRemark ;
    Button   btnSubmit;
    int typeoflead = 1;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_lead);

        ldName =findViewById(R.id.leadName);
        ldEmail =findViewById(R.id.leadEmail);
        ldMobile= findViewById(R.id.leadMobile);
        ldLocation = findViewById(R.id.leadLocation);
        ldRemark = findViewById(R.id.leadRemark) ;
        btnSubmit = findViewById(R.id.ldSubmit);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation();
            }
        });
    }

    private void validation() {

        String name = ldName.getText().toString().trim();
        String email = ldEmail.getText().toString().trim();
        String phone = ldMobile.getText().toString().trim();
        String location = ldLocation.getText().toString().trim();
        String Remark = ldRemark.getText().toString().trim();

        Pattern pattern1=Pattern.compile("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher matcher1=pattern1.matcher(email);
        String regx = "^[\\p{L} .'-]+$";
        Pattern paname =Pattern.compile(regx);
        Matcher matchname=paname.matcher(name);

        boolean cancel= false;
        View focusView = null;

        if (TextUtils.isEmpty(name)) {
            ldName.setError(getString(R.string.error_field_required));
            focusView = ldName;
            cancel = true;
        }else if (!matchname.matches()) {
            ldName.setError("Enter Name");
            focusView = ldName;
            cancel = true;
        }else if (TextUtils.isEmpty(email)) {
            ldEmail.setError(getString(R.string.error_field_required));
            focusView = ldEmail;
            cancel = true;
        }else if (!matcher1.matches() && !ldEmail.getText().toString().isEmpty()) {
            ldEmail.setError("Enter Valid Email");
            focusView = ldEmail;
            cancel = true;
        }else if (TextUtils.isEmpty(phone)) {
            ldMobile.setError(getString(R.string.error_field_required));
            focusView = ldMobile;
            cancel = true;
        }else if (phone.length() < 10) {
            ldMobile.setError("Enter valid mobile no.");
            focusView = ldMobile;
            cancel = true;
        }
        else if (TextUtils.isEmpty(location)) {
            ldLocation.setError(getString(R.string.error_field_required));
            focusView = ldLocation;
            cancel = true;
        }else if (TextUtils.isEmpty(Remark)) {
            ldRemark.setError(getString(R.string.error_field_required));
            focusView = ldRemark;
            cancel = true;
        }if (cancel) {
            focusView.requestFocus();
        }
        else {
                new UserAddTask().execute(Global.token,Global.userId,name, email, phone, location, Remark, String.valueOf(typeoflead));
        }


    }
    private class UserAddTask extends AsyncTask<String , String ,String> {
        ProgressDialog pdLoading = new ProgressDialog(NewLead.this);
        HttpURLConnection conn;
        URL url = null;
        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;


        @Override
        protected void onPreExecute() {
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... param) {
            try {

                url = new URL(APIUrls.new_lead);

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder =new Uri.Builder()
                        .appendQueryParameter("token",param[0])
                        .appendQueryParameter("employee_id",param[1])
                        .appendQueryParameter("name",param[2])
                        .appendQueryParameter("email",param[3])
                        .appendQueryParameter("mobile",param[4])
                        .appendQueryParameter("location",param[5])
                        .appendQueryParameter("remarks",param[6])
                        .appendQueryParameter("typeoflead",param[7]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }

        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pdLoading.dismiss();
            Boolean aBoolean = false;
            try {
                JSONObject object =new JSONObject(s);
                aBoolean=object.getBoolean("status");
                if (aBoolean){
                    Toast.makeText(getApplicationContext(),object.getString("result"), Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(NewLead.this, ActivityMain.class);
                    startActivity(in);
                    finish();
                }else if (object.has("error")){

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
                    Global.SessionExpired(NewLead.this);
                }            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
