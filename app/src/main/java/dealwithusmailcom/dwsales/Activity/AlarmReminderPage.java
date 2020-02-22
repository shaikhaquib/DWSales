package dealwithusmailcom.dwsales.Activity;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import dealwithusmailcom.dwsales.R;

public class AlarmReminderPage extends AppCompatActivity {

    TextView remark ;
    ImageView pause;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmreminder);

        getSupportActionBar().hide();
        remark=findViewById(R.id.txtRemark);
        pause = findViewById(R.id.pause);
        System.out.println("AlarmReminderPage "+getIntent().getStringExtra("remark"));
        remark.setText(getIntent().getStringExtra("remark"));
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        try {
            AssetFileDescriptor afd = getAssets().openFd("jarvis_reminder.mp3");
            MediaPlayer player = new MediaPlayer();
            player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static float getFitTextSize(TextPaint paint, float width, String text) {
        float nowWidth = paint.measureText(text);
        float newSize = (float) width / nowWidth * paint.getTextSize();
        return newSize;
    }
}
