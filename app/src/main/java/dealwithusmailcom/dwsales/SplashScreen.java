package dealwithusmailcom.dwsales;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import dealwithusmailcom.dwsales.Activity.LoginActivity;
import dealwithusmailcom.dwsales.Config.MyConnectivityChecker;

public class SplashScreen extends AppCompatActivity {

    ProgressBar splprogressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        getSupportActionBar().hide();

        MyConnectivityChecker.isConnected(this);
        splprogressBar = findViewById(R.id.splprogressBar);




        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CheckConnection();
            }
        }, 6000 );


    }

    private void CheckConnection() {


        if(MyConnectivityChecker.isConnected(this)){
            //connectivity available
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
        else{
            //no connectivity
            splprogressBar.setVisibility(View.GONE);


            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(SplashScreen.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(SplashScreen.this);

            }
            builder.setCancelable(false);
            builder.setTitle("Connection Problem !!")
                    .setMessage("No Internet connection. Make sure that WI-FI or Cellular data is turned on, and then try again.")
                    .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            CheckConnection();
                            splprogressBar.setVisibility(View.VISIBLE);
                        }
                    })

                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })

                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }


    }
}
