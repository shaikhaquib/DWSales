package dealwithusmailcom.dwsales.ServiceReciver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import dealwithusmailcom.dwsales.Activity.AlarmReminderPage;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.d("Receiver", "Broadcast received: ");

        String state = intent.getStringExtra("remark");
        System.out.println("Receiver "+state);

        Intent intent1 = new Intent(context , AlarmReminderPage.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra("remark",state);
        context.startActivity(intent1);
    }

}