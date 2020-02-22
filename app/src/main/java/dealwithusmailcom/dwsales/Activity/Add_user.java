package dealwithusmailcom.dwsales.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dealwithusmailcom.dwsales.Config.APIUrls;
import dealwithusmailcom.dwsales.Config.AppController;
import dealwithusmailcom.dwsales.Global;
import dealwithusmailcom.dwsales.R;

public class Add_user extends AppCompatActivity {

    EditText fname ,lname ,phone,empid ,email , desig ,password ;
    Button btnSubmit;
    ProgressDialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        getSupportActionBar().hide();


        progressBar =new ProgressDialog(Add_user.this);


        fname =findViewById(R.id.fname);
        lname=findViewById(R.id.lname);
        email=findViewById(R.id.edtemail);
        phone=findViewById(R.id.edtmobile);
        desig=findViewById(R.id.edtDesig);
        password=findViewById(R.id.edtPassword);
        empid=findViewById(R.id.edtempid);
        btnSubmit=findViewById(R.id.btnSubmit);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation();
            }
        });
    }


    private void validation() {

        String strfname = fname.getText().toString().trim();
        String strlname = lname.getText().toString().trim();
        String stremail = email.getText().toString().trim();
        String strphone = phone.getText().toString().trim();
        String strdesig = desig.getText().toString().trim();
        String strpass = password.getText().toString().trim();
        String strempid = empid.getText().toString().trim();

        Pattern pattern1=Pattern.compile("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher matcher1=pattern1.matcher(stremail);
        String regx = "^[\\p{L} .'-]+$";
        Pattern paname =Pattern.compile(regx);
        Matcher matchfname=paname.matcher(strfname);
        Matcher matchlname=paname.matcher(strlname);

        boolean cancel= false;
        View focusView = null;

        if (TextUtils.isEmpty(strfname)) {
            fname.setError(getString(R.string.error_field_required));
            focusView = fname;
            cancel = true;
        }else if (!matchfname.matches()) {
            fname.setError("Enter First Name");
            focusView = fname;
            cancel = true;
        }else if (TextUtils.isEmpty(strlname)) {
            lname.setError(getString(R.string.error_field_required));
            focusView = lname;
            cancel = true;
        }else if (!matchlname.matches()) {
            lname.setError("Enter Last Name");
            focusView = lname;
            cancel = true;
        }else if (TextUtils.isEmpty(strempid)) {
            empid.setError(getString(R.string.error_field_required));
            focusView = empid;
            cancel = true;
        }else if (TextUtils.isEmpty(stremail)) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        }else if (!matcher1.matches() && !email.getText().toString().isEmpty()) {
            email.setError("Enter Valid Email");
            focusView = email;
            cancel = true;
        }else if (TextUtils.isEmpty(strphone)) {
            phone.setError(getString(R.string.error_field_required));
            focusView = phone;
            cancel = true;
        }else if (strphone.length() < 10) {
            phone.setError("Enter valid mobile no.");
            focusView = phone;
            cancel = true;
        }
        else if (TextUtils.isEmpty(strdesig)) {
            desig.setError(getString(R.string.error_field_required));
            focusView = desig;
            cancel = true;
        }else if (TextUtils.isEmpty(strpass)) {
            password.setError(getString(R.string.error_field_required));
            focusView = password;
            cancel = true;
        }if (cancel) {
            focusView.requestFocus();
        }
        else {
          UserAddTask(Global.token,strempid,strfname,strlname,stremail,strphone,strdesig,strpass);
        }


    }

    private void UserAddTask(final String token, final String userId, final String strfname, final String strlname, final String stremail, final String strphone, final String strdesig, final String strpass) {
        progressBar.setMessage("Submitting...");
        progressBar.show();
        StringRequest request=new StringRequest(Request.Method.POST, APIUrls.signup, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.dismiss();

                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("status")) {

                        AlertDialog.Builder builderInner = new AlertDialog.Builder(Add_user.this);
                        builderInner.setMessage("You have created new user "+strfname + " "+ strlname +" with \n Email:" +stremail+ "\n" +"Password:" +strpass);
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

                params.put("employee_id", userId);
                params.put("name", strfname);
                params.put("last_name", strlname);
                params.put("email", stremail);
                params.put("mobile",strphone );
                params.put("designation", strdesig);
                params.put("password", strpass);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }


}
