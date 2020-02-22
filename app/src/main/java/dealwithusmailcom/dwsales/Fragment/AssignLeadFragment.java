package dealwithusmailcom.dwsales.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dealwithusmailcom.dwsales.Adapter.ExpandableListAdapter;
import dealwithusmailcom.dwsales.Config.APIUrls;
import dealwithusmailcom.dwsales.Config.AppController;
import dealwithusmailcom.dwsales.Global;
import dealwithusmailcom.dwsales.Model.LeadModel;
import dealwithusmailcom.dwsales.R;

@SuppressLint("ValidFragment")
public class AssignLeadFragment extends Fragment {

    ExpandableListAdapter listAdapter;
    RecyclerView expListView;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.assignlead_fragment, container, false);

        // get the listview
        expListView = rootView.findViewById(R.id.idrvassign);
        expListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // preparing list listDataHeader
        MYlEAD();
        // setting list adapter

        return rootView;

    }


    private void MYlEAD() {

        final ProgressDialog dialog =new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.show();
        final ArrayList<LeadModel> listDataHeader = new ArrayList<>();
        StringRequest request = new StringRequest(StringRequest.Method.POST, APIUrls.Assignlist, new Response.Listener < String > () {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();

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
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
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
                        Global.SessionExpired(getActivity());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listAdapter = new ExpandableListAdapter(getContext(), listDataHeader , 3);
                expListView.setAdapter(listAdapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                }
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map <String,String> param = new HashMap<>();
                param.put("token", Global.token);
                param.put("employee_id", Global.userId);
                return param;
            }
        };

        AppController.getInstance().addToRequestQueue(request);
    }

}