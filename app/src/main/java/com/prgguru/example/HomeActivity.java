package com.prgguru.example;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Activity {
	TextView msgET, usertitleET;
    ListView lv;
    String str = "";

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		// Intent Message sent from Broadcast Receiver
		str = getIntent().getStringExtra("msg");

		// Get Email ID from Shared preferences
		SharedPreferences prefs = getSharedPreferences("UserDetails",
				Context.MODE_PRIVATE);
		String eMailId = prefs.getString("eMailId", "");
		// Set Title
		usertitleET = (TextView) findViewById(R.id.usertitle);
        msgET = (TextView)findViewById(R.id.message);

        List list = new ArrayList<String>();
        lv = (ListView)findViewById(R.id.listView);




        if (!checkPlayServices()) {
			Toast.makeText(
					getApplicationContext(),
					"This device doesn't support Play services, App will not work normally",
					Toast.LENGTH_LONG).show();
		}
		
		usertitleET.setText("Hello " + eMailId + " !");
		// When Message sent from Broadcase Receiver is not empty
		if (str != null) {

            list.add(str);
            msgET.setText(str);
		}

        ListAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Clicked on Item " + position, Toast.LENGTH_SHORT).show();
            }
        });
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Toast.makeText(
						getApplicationContext(),
						"This device doesn't support Play services, App will not work normally",
						Toast.LENGTH_LONG).show();
				finish();
			}
			return false;
		} else {
			Toast.makeText(
					getApplicationContext(),
					"This device supports Play services, App will work normally",
					Toast.LENGTH_LONG).show();
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkPlayServices();
        msgET.setText(str+"test");
	}

    protected void onDestroy(){
        super.onDestroy();

        str = msgET.getText().toString();


    }
}
