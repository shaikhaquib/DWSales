package dealwithusmailcom.dwsales.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dealwithusmailcom.dwsales.Config.APIUrls;
import dealwithusmailcom.dwsales.Config.AppController;
import dealwithusmailcom.dwsales.R;
import dealwithusmailcom.dwsales.SQLiteHandler;
import dealwithusmailcom.dwsales.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    AlertDialog alertDialog;
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        pDialog =new ProgressDialog(LoginActivity.this);
        session = new SessionManager(this);
        db = new SQLiteHandler(this);
        initialize();
        checkAndRequestPermissions();
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, ActivityMain.class);
            startActivity(intent);
            finish();
        }

    }

    private void initialize() {

        inputEmail      = findViewById(R.id.lgEmail);
        inputPassword   = findViewById(R.id.lgPassword);

    }

    private void autoLaunchActivity(Context context) {


        if (Build.MANUFACTURER.equalsIgnoreCase("oppo")) {
            try {

                Toast.makeText(context, "For Better experiance in Background Please Enable yChat app Auto Start mode !", Toast.LENGTH_LONG).show();

                Intent intent = new Intent();
                intent.setClassName("com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity");
                startActivity(intent);
            } catch (Exception e) {
                try {
                    Intent intent = new Intent();
                    intent.setClassName("com.oppo.safe",
                            "com.oppo.safe.permission.startup.StartupAppListActivity");
                    startActivity(intent);

                } catch (Exception ex) {
                    try {
                        Intent intent = new Intent();
                        intent.setClassName("com.coloros.safecenter",
                                "com.coloros.safecenter.startupapp.StartupAppListActivity");
                        startActivity(intent);
                    } catch (Exception exx) {

                    }
                }
            }

        } else if (Build.MANUFACTURER.equalsIgnoreCase("vivo")){

            Toast.makeText(context, "For Better experiance in Background Please Enable  app Auto Start mode !", Toast.LENGTH_LONG).show();

            try {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.iqoo.secure",
                        "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"));
                startActivity(intent);
            } catch (Exception e) {
                try {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.vivo.permissionmanager",
                            "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                    startActivity(intent);
                } catch (Exception ex) {
                    try {
                        Intent intent = new Intent();
                        intent.setClassName("com.iqoo.secure",
                                "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager");
                        startActivity(intent);
                    } catch (Exception exx) {
                        ex.printStackTrace();
                    }
                }
            }


        }else if (Build.BRAND.equalsIgnoreCase("xiaomi")) {
            Intent intent = new Intent();
            Toast.makeText(context, "For Better experiance in Background Please Enable  app Auto Start mode !", Toast.LENGTH_LONG).show();
            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            startActivity(intent);
        }else if(Build.BRAND.equalsIgnoreCase("Letv")){
            Toast.makeText(context, "For Better experiance in Background Please Enable  app Auto Start mode !", Toast.LENGTH_LONG).show();

            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            startActivity(intent);

        }
        else if(Build.BRAND.equalsIgnoreCase("Honor")){
            Toast.makeText(context, "For Better experiance in Background Please Enable  app Auto Start mode !", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            startActivity(intent);

        }

    }

    // Permission check

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkAndRequestPermissions() {
        int READEXTRSTORAGEPERMISSION = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        int WRITEEXTRSTORAGEPERMISSION = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);


        int ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        int ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        int PROCESS_OUTGOING_CALLS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.PROCESS_OUTGOING_CALLS);

        int RECORD_AUDIO = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        int READ_PHONE_STATE = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);

        int CALL_PHONE = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE);





        List<String> listPermissionsNeeded = new ArrayList<>();
        if (READEXTRSTORAGEPERMISSION != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (WRITEEXTRSTORAGEPERMISSION != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (PROCESS_OUTGOING_CALLS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.PROCESS_OUTGOING_CALLS);
        }
        if (RECORD_AUDIO != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (READ_PHONE_STATE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (CALL_PHONE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }



        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCOUNTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    // Setting Dialog Message
                    alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage("Allow TargetSels to Autostart?");

                    // Setting OK Button
                    alertDialog.setButton("Allow", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after mDialog closed
                            autoLaunchActivity(getApplicationContext());
                            alertDialog.dismiss();
                        }
                    });

                    // Showing Alert Message
                    alertDialog.show();
                } else {
                    //You did not accept the request can not use the functionality.
                    autoLaunchActivity(this);

                }
                break;
        }
    }


    public void loginService(View view) {

        boolean cancel= false;
        View focusView = null;

        if (inputEmail.getText().toString().equals("")){
            inputEmail.setError("Please Enter Email !");
            focusView = inputEmail;
            cancel = true;
        }else
        if (inputPassword.getText().toString().equals("")){
            inputPassword.setError("Please Enter Password !");
            focusView = inputPassword;
            cancel = true;
        }if (cancel) {
            focusView.requestFocus();
        }else {
            checkLogin(inputEmail.getText().toString(), inputPassword.getText().toString());

        }
    }

    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, APIUrls.login, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();

                try {


//                    boolean error = jObj.getBoolean("error");

                    JSONObject obj = new JSONObject(response);
                    // Check for error node in json
//                    if (!error) {
                    // user successfully logged i

                    if (obj.getBoolean("status")){

                        JSONObject jsonObject = obj.getJSONObject("result");

                        String uid = jsonObject.getString("employee_id");
//                        JSONObject user = jObj.getJSONObject("user");
                        String name = jsonObject.getString("name") + jsonObject.getString("last_name");
                         String email = jsonObject.getString("email");
                        String created_at = jsonObject.getString("created_at");
                        String token = obj.getString("token");
                        System.out.println(token);
                        session.setLogin(true);

                        // Inserting row in users table

                        db.addUser(name, email, uid,jsonObject.getString("status"),jsonObject.getString("designation"), created_at,token);

                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this, ActivityMain.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = obj.getString("msg");
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(LoginActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(LoginActivity.this);
                        }
                        builder.setTitle("Login Error")
                                .setMessage(errorMsg)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    //   Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
               /* Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();*/
               hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
