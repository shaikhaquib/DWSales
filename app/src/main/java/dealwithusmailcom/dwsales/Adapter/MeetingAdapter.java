package dealwithusmailcom.dwsales.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import dealwithusmailcom.dwsales.Activity.Dashboard;
import dealwithusmailcom.dwsales.Model.LeadModel;
import dealwithusmailcom.dwsales.R;

public class MeetingAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<LeadModel> list ;

    public MeetingAdapter(Context context, ArrayList<LeadModel> mList) {

        this.context = context;
        this.list =mList;

    }

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.meeting, parent, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Holder hold = (Holder) holder;
        final LeadModel current = list.get(position);

        hold.name.setText(current.LeadName);
        hold.no.setText(current.LeadMobile);
        hold.location.setText(current.LeadLocation);
        hold.Remark.setText(current.LeadRemark);


        Log.d(String.valueOf(position),current.LeadName);
        Log.d(String.valueOf(position),current.LeadMobile);
        Log.d(String.valueOf(position),current.LeadLocation);
        Log.d(String.valueOf(position),current.LeadRemark);
        Log.d(String.valueOf(position),current.Leademail);


        //hold.location.setText();

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder{

        TextView name , no , location , Remark;

        public Holder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.mtname);
            no = itemView.findViewById(R.id.mtPhone);
            location = itemView.findViewById(R.id.mtLocation);
            Remark = itemView.findViewById(R.id.mtRemark);

        }
    }


}
