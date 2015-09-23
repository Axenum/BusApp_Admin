package com.allits.BusAdmin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Created by Stefan on 12.05.2015.
 */
public class AlertActivity extends Activity {


    Boolean running;
    public static CountDownTimer Count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent i = getIntent();

        Bundle extras = i.getExtras();

        final String tour = extras.getString("tour");
        final String datum = extras.getString("datum");
        final String ktime = extras.getString("ktime");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert);

        final TextView txttime = (TextView) findViewById(R.id.txtalert);
        Button submit = (Button) findViewById(R.id.btnsubmit);

        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        final String eMailId = prefs.getString("eMailId", "");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("email", eMailId);
                Log.d("tour", tour);
                Log.d("datum", datum);
                new SendSubmit(AlertActivity.this, eMailId, tour, datum).execute(ApplicationConstants.APP_SERVER_URL + ApplicationConstants.APP_SUBMIT_URL);
            }
        });


        int time = (Integer.parseInt(ktime)*60)*1000 ;


        Count = new CountDownTimer(time, 10000) {
            @Override
            public void onTick(long millisUntilFinished) {
                running = true;
                txttime.setText(String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                //txttime.setText(""+millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                getParent().finish();

            }
        };

        Count.start();



    }

    public boolean getRunning(){
        return false;
    }

    public static void cancelCount(){
        Count.cancel();
    }
}
