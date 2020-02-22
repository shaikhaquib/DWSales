package dealwithusmailcom.dwsales.Adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dealwithusmailcom.dwsales.Activity.ActivityMain;
import dealwithusmailcom.dwsales.Activity.DeviceAdminDemo;
import dealwithusmailcom.dwsales.Activity.LeadInfoFrom;
import dealwithusmailcom.dwsales.Activity.Main2Activity;
import dealwithusmailcom.dwsales.Activity.TService;
import dealwithusmailcom.dwsales.Config.APIUrls;
import dealwithusmailcom.dwsales.Config.AppController;
import dealwithusmailcom.dwsales.Model.EmpModel;
import dealwithusmailcom.dwsales.ServiceReciver.CallDurationReceiver;
import dealwithusmailcom.dwsales.Global;
import dealwithusmailcom.dwsales.Model.LeadModel;
import dealwithusmailcom.dwsales.Model.LeadModelData;
import dealwithusmailcom.dwsales.R;

import static android.app.Activity.RESULT_OK;

public class ExpandableListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback {

    private Context context;
    private ArrayList<LeadModel> models; // header titles
    ProgressDialog progressBar;
    AlertDialog alertDialog;
    ArrayList<LeadModelData> arrayList =new ArrayList<LeadModelData>();
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;
    private int lastPosition = -1;
    int fragment;



    private static final int REQUEST_CODE = 0;
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;

    public ExpandableListAdapter(Context context, ArrayList<LeadModel> listDataHeader, int i) {
        this.context = context;
        this.models = listDataHeader;
        progressBar = new ProgressDialog(context);
        this.fragment = i ;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_parent, parent, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Holder holder1 = (Holder) holder;
        final LeadModel current = models.get(position);
        holder1.lblListHeader.setText(current.LeadName);

        String[] splited = current.LeadDate.split("\\s+");

        holder1.date.setText(splited[0]);
        holder1.mno.setText(current.LeadMobile);
        holder1.email.setText(current.Leademail);
        holder1.location.setText(current.LeadLocation);
        holder1.remark.setText(current.LeadRemark);
        holder1.menu.setTag(current);
        holder1.ratingBar.setTag(current);
        holder1.recyclerView.setTag(current);
        holder1.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder1.addLeadData.setTag(current);
        holder1.call.setTag(current);
        holder1.mail.setTag(current);
        holder1.error.setTag(current);
        Global.slideDown(holder1.addLeadData);
        holder1.addLeadData.setVisibility(View.GONE);
        holder1.subLayout.setTag(current);



        if (!Global.empStatus.equals("1") && fragment == 1){
     //       menuItem.setVisible(true);
            holder1.menu.setVisibility(View.GONE);  }
        holder1.mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String []{current.Leademail});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,"");
                emailIntent.setType("message/rfc822");

                try {
                    ((Activity) context).startActivityForResult(Intent.createChooser(emailIntent, "Send E-Mail"), 123);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(context,
                            "No email clients installed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder1.call.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT < 23) {
                    //Do not need to check the permission
                } else {
                    if (checkAndRequestPermissions()) {
                      /* Intent intent = new Intent(context, Main2Activity.class);
                       intent.putExtra("mobielno",current.LeadMobile);
                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                       context.startActivity(intent);*/

                       /* Uri packageURI = Uri.parse("package:"+"dealwithusmailcom.dwsales");
                        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
                        context.startActivity(uninstallIntent);*/

                        try {
                            // Initiate DevicePolicyManager.
                            mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                            mAdminName = new ComponentName(context, DeviceAdminDemo.class);
                         //   getSupportActionBar().hide();
                            if (!mDPM.isAdminActive(mAdminName)) {
                                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
                                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.");
                                ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
                            } else {
                                Intent intent = new Intent(context, TService.class);
                                context.startService(intent);
                                Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + current.LeadMobile));
                                context.startActivity(intent1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }


            }
        });

        holder1.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popup = new PopupMenu(context,view);

/*
                popup.getMenuInflater().inflate(R.menu.popopmenu,popup.getMenu());

                Menu mItem=popup.getMenu();
                MenuItem menuItem= mItem.findItem(R.id.assign);

                if (!Global.empStatus.equals("1")){
                    menuItem.setVisible(false);
                }else {
                    menuItem.setVisible(true);
                }*/




                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if (menuItem.getItemId() == R.id.addInfo){
                            Intent intent =  new Intent(context,LeadInfoFrom.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("lid",current.LeadId);
                            context.startActivity(intent);

                        }else if (menuItem.getItemId() == R.id.addleadratings){

                            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
                            View mView = layoutInflaterAndroid.inflate(R.layout.rating_dialog, null);
                            AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(context);
                            alertDialogBuilderUserInput.setView(mView);
                            final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                            TextView cancel = mView.findViewById(R.id.rtCancel);
                            final RatingBar rating = mView.findViewById(R.id.rtRating);
                            holder1.progress.setTag(current);
                            cancel.setOnClickListener(new View.OnClickListener() {@Override
                            public void onClick(View v) {
                                alertDialogAndroid.dismiss();
                            }
                            });

                            final TextView submit = mView.findViewById(R.id.rtSubmit);
                            submit.setOnClickListener(new View.OnClickListener() {@Override
                            public void onClick(View v) {
                                alertDialogAndroid.dismiss();

                                if (rating.getProgress() > 0){
                                submitRating(current.LeadId, String.valueOf(rating.getProgress()));}
                                else {
                                    Snackbar snackbar = Snackbar.make(holder1.view , "Invalid Rating",Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }

                            }
                            });

                            alertDialogAndroid.show();

                        }else if (menuItem.getItemId() == R.id.addstatus){

                            AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
                            builderSingle.setTitle("Select Lead Status");

                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item);

                            arrayAdapter.add("Initiated");
                            arrayAdapter.add("Follow Up");
                            arrayAdapter.add("In Process");
                            arrayAdapter.add("Reviewed");
                            arrayAdapter.add("Scheduled Call");
                            arrayAdapter.add("Calling Done");
                            arrayAdapter.add("Scheduled Meeting");
                            arrayAdapter.add("Meeting Done");
                            arrayAdapter.add("Dropped");
                            arrayAdapter.add("Closed");


                            builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {



                                   /* String strName = arrayAdapter.getItem(which);
                                    AlertDialog.Builder builderInner = new AlertDialog.Builder(context);
                                    builderInner.setMessage(strName);
                                    builderInner.setTitle("Your Selected Item is");
                                    builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builderInner.show();*/


                                    updateStatus(current.LeadId , which);

                                }
                            });
                            builderSingle.show();
                        }else if (menuItem.getItemId() == R.id.assign){

                            AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
                            builderSingle.setTitle("Select Employee");

                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item ,Global.eList);
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
                                    AlertDialog.Builder builderInner = new AlertDialog.Builder(context);
                                    builderInner.setMessage("Assign "+current.LeadName +" to "+ strName + " Click OK to Confirm !");
                                    builderInner.setTitle("Confirm");
                                    builderInner.setIcon(R.drawable.ic_confirm);
                                    builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,int which) {
                                            dialog.dismiss();

                                            Assign(current.LeadId , EmpData.employee_id);
                                        }
                                    });
                                    builderInner.show();

                                }
                            });
                            builderSingle.show();
                        }
                        return false;
                    }
                } );

                popup.getMenuInflater().inflate(R.menu.popopmenu,popup.getMenu());
                MenuItem menuItem =popup.getMenu().findItem(R.id.assign);
                MenuItem addinfo  =popup.getMenu().findItem(R.id.addInfo);
                MenuItem addrating  =popup.getMenu().findItem(R.id.addleadratings);
                MenuItem addStatus  =popup.getMenu().findItem(R.id.addstatus);

                if (Global.empStatus.equals("1") && fragment == 1){
                    menuItem.setVisible(true);
                }
                if (fragment == 1){
                    addinfo.setVisible(false);
                    addrating.setVisible(false);
                    addStatus.setVisible(false);
                }
                popup.show();




            }
        });


        if (current.LeadStatus.equals("1")){
            holder1.substatus.setImageResource(R.drawable.ic_followup);
            holder1.txtStatus.setText("Follow Up");
            holder1.txtStatus.setTextColor(Color.parseColor("#e65100"));
        }else if (current.LeadStatus.equals("2")){
            holder1.substatus.setImageResource(R.drawable.ic_process);
            holder1.txtStatus.setText("In Process");
            holder1.txtStatus.setTextColor(Color.parseColor("#22356b"));
        }else if (current.LeadStatus.equals("3")){
            holder1.substatus.setImageResource(R.drawable.ic_review);
            holder1.txtStatus.setText("Reviewed");
            holder1.txtStatus.setTextColor(Color.parseColor("#D69C42"));
        } else if (current.LeadStatus.equals("4")){
            holder1.substatus.setImageResource(R.drawable.ic_24_hours);
            holder1.txtStatus.setText("Scheduled Call");
            holder1.txtStatus.setTextColor(Color.parseColor("#22356b"));
        }else if (current.LeadStatus.equals("5")){
            holder1.substatus.setImageResource(R.drawable.ic_phone_call);
            holder1.txtStatus.setText("Calling Done");
            holder1.txtStatus.setTextColor(Color.parseColor("#8b2a57"));
        }else if (current.LeadStatus.equals("6")){
            holder1.substatus.setImageResource(R.drawable.ic_meeting_deadlines);
            holder1.txtStatus.setText("Scheduled Meeting");
            holder1.txtStatus.setTextColor(Color.parseColor("#009925"));
        }else if (current.LeadStatus.equals("7")){
            holder1.substatus.setImageResource(R.drawable.ic_meeting);
            holder1.txtStatus.setText("Meeting Done");
            holder1.txtStatus.setTextColor(Color.parseColor("#009925"));
        }else if (current.LeadStatus.equals("8")){
            holder1.substatus.setImageResource(R.drawable.ic_dropped);
            holder1.txtStatus.setText("Dropped");
            holder1.txtStatus.setTextColor(Color.parseColor("#f57c00"));
        }else if (current.LeadStatus.equals("9")){
            holder1.substatus.setImageResource(R.drawable.ic_close);
            holder1.txtStatus.setText("Closed");
            holder1.txtStatus.setTextColor(Color.parseColor("#33691e"));
        }
        int Ratings = Integer.parseInt(current.LeadRating);

        if (Ratings > 0){
            holder1.ratingBar.setProgress(Ratings);
            holder1.ratingBar.setVisibility(View.VISIBLE);
            // holder1.addRating.setVisibility(View.GONE);
        }


        holder1.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayList.clear();
                if (!current.tapIn){
                    current.tapIn = true ;
                    holder1.progress.setVisibility(View.VISIBLE);
                    LeadDataservice(holder1.recyclerView , current.LeadId ,position ,holder1.progress ,holder1.error ,holder1.addLeadData);
                    //  Global.slideDown(holder1.subLayout);
                }else {

                    current.tapIn = false ;

                    TranslateAnimation animate = new TranslateAnimation(0,holder1.recyclerView.getWidth(),0,0);
                    animate.setDuration(500);
                    animate.setFillAfter(true);
                    holder1.recyclerView.startAnimation(animate);
                    animate.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            holder1.recyclerView.clearAnimation();
                            arrayList.clear(); //clear list
                            holder1.recyclerView.getAdapter().notifyDataSetChanged();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    holder1.error.startAnimation(animate);
                    animate.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            holder1.recyclerView.clearAnimation();
                            arrayList.clear(); //clear list
                            holder1.recyclerView.getAdapter().notifyDataSetChanged();
                            holder1.error.clearAnimation();
                            holder1.error.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }

            }
        });

        for (int i = 0; i < getItemCount(); i++) {

            animate(holder1.view, i);

        }


    }

    private void updateStatus(final String leadId, final int status)         {
        progressBar.setMessage("Submitting...");
        progressBar.show();
        StringRequest request=new StringRequest(Request.Method.POST, APIUrls.LeadStts, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.dismiss();
                try {
                    JSONObject jsonObject =new JSONObject(response);
                    if(jsonObject.getBoolean("status")){

                        AlertDialog.Builder builderInner = new AlertDialog.Builder(context);
                        builderInner.setMessage("You have Successfully changed the lead Status.");
                        builderInner.setTitle("SUCCESS");
                        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int which) {
                                Intent intent =new Intent(context ,ActivityMain.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                                dialog.dismiss();
                                ((Activity)context).finish();
                            }
                        });
                        builderInner.show();
                    }else if (jsonObject.has("error")){

                        JSONArray array = jsonObject.getJSONArray("error");
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
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
                        Global.SessionExpired(context);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //  if ()
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
                params.put("token", Global.token);
                params.put("lead_id", leadId);
                params.put("status", String.valueOf(status));

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    private void Assign(final String leadId, final String empID)         {
        progressBar.setMessage("Submitting...");
        progressBar.show();
        StringRequest request=new StringRequest(Request.Method.POST, APIUrls.assign, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.dismiss();
                try {
                    JSONObject jsonObject =new JSONObject(response);
                    if(jsonObject.getBoolean("status")){

                        AlertDialog.Builder builderInner = new AlertDialog.Builder(context);
                        builderInner.setMessage(jsonObject.getString("result"));
                        builderInner.setTitle("SUCCESS");
                        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int which) {
                                Intent intent =new Intent(context ,ActivityMain.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                                dialog.dismiss();
                                ((Activity)context).finish();
                            }
                        });
                        builderInner.show();
                    }else if (jsonObject.has("error")){

                        JSONArray array = jsonObject.getJSONArray("error");
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
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
                        Global.SessionExpired(context);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //  if ()
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

                params.put("token", Global.token);
                params.put("lead_id", leadId);
                params.put("assign_person", empID);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return models.size();
    }


    class Holder extends RecyclerView.ViewHolder {

        TextView lblListHeader, date, mno, email, location, remark  ,error,txtStatus;
        ImageView substatus;
        RatingBar ratingBar;
        LinearLayout subLayout;
        ProgressBar progress;
        ImageView call ,mail;
        ImageView menu ;
        CheckBox addLeadData;
        RecyclerView recyclerView;
        View view;
        private int lastPosition = -1;


        public Holder(View convertView) {
            super(convertView);

            lblListHeader = convertView.findViewById(R.id.idTitle);
            date =  convertView.findViewById(R.id.idDate);
            mno = convertView.findViewById(R.id.idNo);
            email =convertView.findViewById(R.id.idMail);
            location = convertView.findViewById(R.id.idLocation);
            remark =convertView.findViewById(R.id.idRemark);
            menu =convertView.findViewById(R.id.popopMenu);
            //addRating = convertView.findViewById(R.id.giveRating);
            ratingBar = convertView.findViewById(R.id.leadRating);
            progress = convertView.findViewById(R.id.progress);
            recyclerView = convertView.findViewById(R.id.subReCycler);
            addLeadData = convertView.findViewById(R.id.addData);
            call = convertView.findViewById(R.id.call);
            mail = convertView.findViewById(R.id.mail);
            error = convertView.findViewById(R.id.error);
            subLayout = convertView.findViewById(R.id.subLayout);
            substatus=itemView.findViewById(R.id.leadstatus);
            txtStatus=itemView.findViewById(R.id.txtleadStatus);

            view=convertView;

        }
    }

    private void submitRating(final String id, final String rating) {
        {
            progressBar.setMessage("Submitting...");
            progressBar.show();
            StringRequest request=new StringRequest(Request.Method.POST, APIUrls.leadRating, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressBar.dismiss();
                    try {
                        JSONObject jsonObject =new JSONObject(response);
                        if(jsonObject.getBoolean("status")){

                            AlertDialog.Builder builderInner = new AlertDialog.Builder(context);
                            builderInner.setMessage("Thanks For Rating This Lead.");
                            builderInner.setTitle("SUCCESS");
                            builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,int which) {
                                    Intent intent =new Intent(context ,ActivityMain.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                    dialog.dismiss();
                                    ((Activity)context).finish();
                                }
                            });
                            builderInner.show();
                        }else if (jsonObject.has("error")){

                            JSONArray array = jsonObject.getJSONArray("error");
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
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
                            Global.SessionExpired(context);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //  if ()
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
                    params.put("token", Global.token);
                    params.put("lead_id", id);
                    params.put("rating",rating);

                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(request);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCOUNTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {
                    //You did not accept the request can not use the functionality.

                    alertDialog.setMessage("You did not accept the request can not use the functionality");

                    // Setting OK Button
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after mDialog closed
                            alertDialog.dismiss();
                        }
                    });

                    // Showing Alert Message
                    alertDialog.show();

                }
                break;
        }
    }

    private boolean checkAndRequestPermissions() {
        int CALLPERMITION = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
        int WRITEStorage = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int READStorage = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int RECORDAUDIO = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (CALLPERMITION != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        } if (WRITEStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } if (READStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        } if (RECORDAUDIO != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) context,listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }



        return true;
    }



    private void LeadDataservice(final RecyclerView recyclerView, final String current, int position, final ProgressBar progress, final TextView error, final CheckBox addLeadData) {
        //  progressBar.show();
        StringRequest request=new StringRequest(Request.Method.POST, APIUrls.LeadData, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.setVisibility(View.GONE);
                try {
                    //  Global.slideUp(addLeadData);
                    // addLeadData.setVisibility(View.VISIBLE);
                    JSONObject object=new JSONObject(response);

                    if (object.getBoolean("status")){

                    JSONArray array=object.getJSONArray("result");
                    for (int i = 0 ; i < array.length() ; i++){
                        JSONObject object1 =array.getJSONObject(i);
                        LeadModelData model =new LeadModelData();

                        model.id = object1.getString("id");
                        model.lead_id = object1.getString("lead_id");
                        model.details = object1.getString("details");
                        model.remarks = object1.getString("remarks");
                        model.rating = object1.getString("rating");
                        model.date=object1.getString("created_at");
                        model.status=object1.getString("status");

                        arrayList.add(model);
                    }

                    if (arrayList.isEmpty()){
                        error.setVisibility(View.VISIBLE);
                    }

                    recyclerView.setVisibility(View.VISIBLE);

                    //  Global.slideUp(recyclerView);

                    recyclerView.setAdapter(new RecyclerView.Adapter() {
                                                @Override
                                                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                                    View view = LayoutInflater.from(context).inflate(R.layout.subleadetail,parent,false);
                                                    Holder holder = new Holder(view);
                                                    return holder;
                                                }

                                                @Override
                                                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                                                    final Holder LDHolder=(Holder)holder;
                                                    final LeadModelData current = arrayList.get(position);

                                                    Global.slideUp(recyclerView);


                                                    LDHolder.subDate.setText(current.date);
                                                    LDHolder.subDetail.setText(current.details);
                                                    LDHolder.subRemark.setText(current.remarks);
                                                    LDHolder.subAssign.setText(current.assign_person);
                                                    LDHolder.txtStatus.setTag(current);
                                                    LDHolder.substatus.setTag(current);
                                                    LDHolder.rating.setProgress(Integer.parseInt(current.rating));

                                                    if ( Integer.parseInt(current.rating) <= 0 ){
                                                        LDHolder.rating.setVisibility(View.GONE);
                                                    }
                                                    for (int i = 0; i < getItemCount(); i++) {

                                                        animate(LDHolder.view, i);

                                                    }

                                                    if (current.status.equals("1")){
                                                        LDHolder.substatus.setImageResource(R.drawable.ic_review);
                                                        LDHolder.txtStatus.setText("Reviewed");
                                                        LDHolder.txtStatus.setTextColor(Color.parseColor("#D69C42"));
                                                    }else if (current.status.equals("2")){
                                                        LDHolder.substatus.setImageResource(R.drawable.ic_24_hours);
                                                        LDHolder.txtStatus.setText("Scheduled Call");
                                                        LDHolder.txtStatus.setTextColor(Color.parseColor("#f67c00"));
                                                    }else if (current.status.equals("3")){
                                                        LDHolder.substatus.setImageResource(R.drawable.ic_phone_call);
                                                        LDHolder.txtStatus.setText("Call");
                                                        LDHolder.txtStatus.setTextColor(Color.parseColor("#22356b"));
                                                    }else if (current.status.equals("4")){
                                                        LDHolder.substatus.setImageResource(R.drawable.ic_meeting_deadlines);
                                                        LDHolder.txtStatus.setText("Scheduled Meeting");
                                                        LDHolder.txtStatus.setTextColor(Color.parseColor("#8b2a57"));
                                                    }else if (current.status.equals("5")){
                                                        LDHolder.substatus.setImageResource(R.drawable.ic_meeting);
                                                        LDHolder.txtStatus.setText("Meeting Done");
                                                        LDHolder.txtStatus.setTextColor(Color.parseColor("#009925"));
                                                    }

                                                }

                                                @Override
                                                public int getItemCount() {
                                                    return arrayList.size();
                                                }

                                                class Holder extends RecyclerView.ViewHolder{
                                                    TextView subDate , subDetail ,subRemark ,subAssign ,txtStatus;
                                                    ImageView substatus;
                                                    View view;
                                                    RatingBar rating;
                                                    public Holder(View itemView) {
                                                        super(itemView);
                                                        view = itemView;
                                                        subDate=itemView.findViewById(R.id.subDate);
                                                        subDetail=itemView.findViewById(R.id.subDetail);
                                                        subRemark=itemView.findViewById(R.id.subRemark);
                                                        rating=itemView.findViewById(R.id.subRating);
                                                        subAssign=itemView.findViewById(R.id.subAssign);
                                                        substatus=itemView.findViewById(R.id.substatus);
                                                        txtStatus=itemView.findViewById(R.id.txtStatus);
                                                    }
                                                }

                                            }


                    );}else if (object.has("error")){

                        JSONArray array = object.getJSONArray("error");
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
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
                        Global.SessionExpired(context);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.setVisibility(View.GONE);
                //  refreshLayout.setRefreshing(false);
            }
        }){
            @Override
            protected Map<String, String> getParams(){

                Map<String,String>  params =new HashMap<>();
                params.put("token", Global.token);
                params.put("lead_id", current);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }
    private void animate(final View view, final int position){

        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        view.startAnimation(animation);
        lastPosition = position;

    }


    public  void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MyAdapter", "onActivityResult");

        if (REQUEST_CODE == requestCode) {
            Intent intent = new Intent(context, TService.class);
            context.startService(intent);
        }
        // finish();


    }
}

