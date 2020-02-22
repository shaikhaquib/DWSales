package dealwithusmailcom.dwsales.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dealwithusmailcom.dwsales.Adapter.FragmentViewPagerAdapter;

import dealwithusmailcom.dwsales.Config.APIUrls;
import dealwithusmailcom.dwsales.Config.AppController;
import dealwithusmailcom.dwsales.Fragment.AssignLeadFragment;
import dealwithusmailcom.dwsales.Fragment.LeadFragment;
import dealwithusmailcom.dwsales.Fragment.MyLeadFragment;
import dealwithusmailcom.dwsales.Global;
import dealwithusmailcom.dwsales.Model.EmpModel;
import dealwithusmailcom.dwsales.R;
import dealwithusmailcom.dwsales.SQLiteHandler;
import dealwithusmailcom.dwsales.ServiceReciver.GpsService;
import dealwithusmailcom.dwsales.SessionManager;

public class ActivityMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private List < Fragment > fragments = new ArrayList < > ();
    private List < String > titles = new ArrayList < > ();
    private ViewPager viewPager;
    private FragmentViewPagerAdapter adapter;
    private TabLayout tabLayout;
    private int[] tabIcons = {
            R.drawable.ic_lead,
            R.drawable.ic_mylead,
            R.drawable.ic_assignlead
    };


    private SQLiteHandler db;
    private SessionManager session;
    HashMap < String, String > user;
    ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        //  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        /*
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
        */

        dialog = new ProgressDialog(ActivityMain.this);

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        user = db.getUserDetails();

        final String name = user.get("name");
        Global.empemail = user.get("email");
        Global.customer_id = user.get("uid");
        Global.userId = user.get("uid");
        Global.name = name;
        Global.token = user.get("token");
        Global.empStatus = user.get("status");
        Global.empDesig = user.get("token");

        new GetEmplist().execute(Global.token);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewPager = (ViewPager) findViewById(R.id.container);

        if (Global.empStatus.equals(1)) {
            viewPager.setOffscreenPageLimit(3);
        } else {
            viewPager.setOffscreenPageLimit(2);
        }
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        prepareDataResource();
        adapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), fragments, titles);

        // Bind Adapter to ViewPager.
        viewPager.setAdapter(adapter);

        // Link ViewPager and TabLayout

        tabLayout.setupWithViewPager(viewPager);
        setTabIcons();




        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.navUserName);
        navUsername.setText(Global.name);
        TextView NavEmail = headerView.findViewById(R.id.navEmail);
        NavEmail.setText(Global.empemail);

        initGpsListeners();
        //  test();

        Menu nav_Menu = navigationView.getMenu();
        if (!Global.empStatus.equals("1")) {

            nav_Menu.findItem(R.id.nav_location).setVisible(false);
            nav_Menu.findItem(R.id.nav_add).setVisible(false);
            nav_Menu.findItem(R.id.nav_exe_Dash).setVisible(false);
        }
    }


    private void initGpsListeners() {

        Intent myGpsService = new Intent(this, GpsService.class);
        startService(myGpsService);
        String GPS_FILTER = "GPS Filter";
        IntentFilter mainFilter = new IntentFilter(GPS_FILTER);


    }

    public void logoutUser() {
        logoutWebservice();
        session.setLogin(false);
        db.deleteUsers();
        Intent intent = new Intent(ActivityMain.this, LoginActivity.class);
        startActivity(intent);
    }

    private void logoutWebservice() {
        dialog.setMessage("Logout...");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, APIUrls.loGOUT, new Response.Listener < String > () {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {

                        AlertDialog.Builder builderInner = new AlertDialog.Builder(ActivityMain.this);
                        builderInner.setMessage(jsonObject.getString("result"));
                        builderInner.setTitle("SUCCESS");
                        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                session.setLogin(false);
                                db.deleteUsers();
                                Intent intent = new Intent(ActivityMain.this, LoginActivity.class);
                                ActivityMain.this.startActivity(intent);
                                dialog.dismiss();
                                finish();
                            }
                        });
                        builderInner.show();
                    } else {
                        // JSONArray jsonArray = jsonObject.getJSONArray("error");
                        Toast.makeText(ActivityMain.this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //  if ()
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                //  refreshLayout.setRefreshing(false);
            }
        }) {
            @Override
            protected Map < String, String > getParams() {

                Map < String, String > params = new HashMap < > ();

                params.put("token", Global.token);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    private void prepareDataResource() {

        fragments = new ArrayList < > ();

        if (Global.empStatus.equals("1")) {
            fragments.add(new LeadFragment());
            fragments.add(new MyLeadFragment());
            fragments.add(new AssignLeadFragment());
        } else {
            fragments.add(new MyLeadFragment());
            fragments.add(new AssignLeadFragment());
        }



        if (Global.empStatus.equals("1")) {
            titles.add("Lead");
            titles.add("My Lead");
            titles.add("Assign");
        } else {
            titles.add("MyLead");
            titles.add("Assign");
        }


        FragmentViewPagerAdapter adapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);
        viewPager.setCurrentItem(0);

    }

    private void setTabIcons() {



        if (Global.empStatus.equals("1")) {
            tabLayout.getTabAt(0).setCustomView(getTabView(0));
            tabLayout.getTabAt(1).setCustomView(getTabView(1));
            tabLayout.getTabAt(2).setCustomView(getTabView(2));
        } else {
            tabLayout.getTabAt(0).setCustomView(getTabView(0));
            tabLayout.getTabAt(1).setCustomView(getTabView(1));
        }
    }


    public View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.custom_layout, null);
        TextView txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_title.setText(titles.get(position));
        ImageView img_title = (ImageView) view.findViewById(R.id.img_title);
        img_title.setImageResource(tabIcons[position]);

        return view;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_location) {
            //  startActivity(new Intent(ActivityMain.this,MapsActivity.class));
            SelectEMp();
        } else if (id == R.id.nav_Dash) {
            startActivity(new Intent(ActivityMain.this, Dashboard.class));
        } else if (id == R.id.nav_Logout) {
            logoutUser();
        } else if (id == R.id.nav_add) {
            //  logoutUser();
            startActivity(new Intent(getApplicationContext(), Add_user.class));
        }else if (id == R.id.nav_exe_Dash) {
            //  logoutUser();
            SelectEMpDeshboard();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        return true;
    }

    private void SelectEMp() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ActivityMain.this);
        builderSingle.setTitle("Select Lead Status");

        final ArrayAdapter < String > arrayAdapter = new ArrayAdapter < String > (ActivityMain.this, android.R.layout.simple_spinner_dropdown_item, Global.eList);
        /*

                                    EmpModel empModel =new EmpModel();

                                   for (int i = 0 ; i < Global.ListEMP.size() ; i++){
                                       arrayAdapter.add(empModel.name);
                                   }
        */

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final EmpModel EmpData = Global.ListEMP.get(which);
                String strName = arrayAdapter.getItem(which);


                Intent intent = new Intent(ActivityMain.this, MapsActivity.class);
                intent.putExtra("name", EmpData.name);
                intent.putExtra("id", EmpData.employee_id);
                startActivity(intent);


            }
        });
        builderSingle.show();

    }
    private void SelectEMpDeshboard() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ActivityMain.this);
        builderSingle.setTitle("Select Lead Status");

        final ArrayAdapter < String > arrayAdapter = new ArrayAdapter < String > (ActivityMain.this, android.R.layout.simple_spinner_dropdown_item, Global.eList);

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final EmpModel EmpData = Global.ListEMP.get(which);
                String strName = arrayAdapter.getItem(which);


                Intent intent = new Intent(ActivityMain.this, Executive_dashboard.class);
                intent.putExtra("name", EmpData.name);
                intent.putExtra("id", EmpData.employee_id);
                startActivity(intent);


            }
        });
        builderSingle.show();

    }

    public void newLead(View view) {
        startActivity(new Intent(ActivityMain.this, NewLead.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        // Check if image is captured successfully

        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment: getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class GetEmplist extends AsyncTask < String, String, String > {

        HttpURLConnection conn;
        URL url = null;
        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;


        @Override
        protected void onPreExecute() {
            dialog.setMessage("\tLoading...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String...param) {
            try {

                url = new URL(APIUrls.emp_list);

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
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("token", param[0]);
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
            dialog.dismiss();
            Boolean aBoolean = false;
            try {
                dialog.dismiss();

                Global.eList.clear();
                Global.ListEMP.clear();


                JSONObject object = new JSONObject(s);

                if (object.getBoolean("status")) {

                    JSONArray array = object.getJSONArray("result");
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject jsonObject = array.getJSONObject(i);
                        EmpModel model = new EmpModel();

                        model.id = jsonObject.getString("id");
                        model.employee_id = jsonObject.getString("employee_id");
                        model.name = jsonObject.getString("name");
                        model.last_name = jsonObject.getString("last_name");
                        model.mobile = jsonObject.getString("mobile");
                        model.designation = jsonObject.getString("designation");
                        model.email = jsonObject.getString("email");
                        model.status = jsonObject.getString("status");
                        model.created_at = jsonObject.getString("created_at");

                        Global.eList.add(jsonObject.getString("name") + " " + jsonObject.getString("last_name"));
                        Global.ListEMP.add(model);
                    }
                } else if (object.has("error")) {

                    JSONArray array = object.getJSONArray("error");
                    AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());
                    alert.setCancelable(false);
                    alert.setMessage(String.valueOf(array.get(0)));
                    alert.setTitle("Error!");
                    alert.setIcon(android.R.drawable.ic_dialog_alert);
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });

                    alert.show();
                } else if (object.has("result")) {
                    //  JSONObject jsonObject = object.getJSONObject("result");
                    Global.SessionExpired(ActivityMain.this);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}