package com.allits.BusAdmin;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends Activity {

	TextView usertitleET;
    ListView lv;
    String eMailId;



    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

        // Intent Message sent from Broadcast Receiver


		// Get Email ID from Shared preferences
		SharedPreferences prefs = getSharedPreferences("UserDetails",
				Context.MODE_PRIVATE);
		eMailId = prefs.getString("eMailId", "");
		// Set Title
		usertitleET = (TextView) findViewById(R.id.usertitle);
        lv = (ListView) findViewById(R.id.lvtouren);

        if (!checkPlayServices()) {
			Toast.makeText(
					getApplicationContext(),
					"This device doesn't support Play services, App will not work normally",
					Toast.LENGTH_LONG).show();
		}
		
		usertitleET.setText("Hello " + eMailId + " !");
		// When Message sent from Broadcase Receiver is not empty

        Button logout = (Button) findViewById(R.id.btnlogout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendLogout(HomeActivity.this, eMailId).execute(ApplicationConstants.APP_SERVER_URL+"logout.php");

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
        startTask();
        checkPlayServices();

	}

    protected void onDestroy(){
        super.onDestroy();

    }

    public void startTask(){

        new LoadTours(eMailId).execute();

    }

    private JSONArray mTours = null;
    public ArrayList<HashMap<String, String>> mToursList;

    ListAdapter adapter;
    private ProgressDialog pDialog;

    private static final String TAG_POSTS = "posts";
    private static final String TAG_TOUR = "tour";
    private static final String TAG_DATE = "datum";
    private static final String TAG_TIME = "time";
    private static final String TAG_KTIME = "ktime";



    public class LoadTours extends AsyncTask<Void, Void, Boolean> {



        String Email;


        public LoadTours(String Email) {
            this.Email = Email;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HomeActivity.this);
            pDialog.setMessage("Loading Tours...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {

            updateJSONdata(Email);

            return null;

        }


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            updateList();
            pDialog.dismiss();
        }


        }



        public void updateJSONdata(String email) {


            List<NameValuePair> params;

            mToursList = new ArrayList<>();
            JSONParser jParser = new JSONParser();
            JSONObject json;

            params = new ArrayList<>();
            params.add(new BasicNameValuePair("user", email));

            json = jParser.makeHttpRequest("http://allits.de/gcm/getTours.php", "POST", params);



            try {


                mTours = json.getJSONArray(TAG_POSTS);

                // looping through all posts according to the json object returned
                for (int i = 0; i < mTours.length(); i++) {
                    JSONObject jsonObject = mTours.getJSONObject(i);

                    //gets the content of each tag
                    String tour = jsonObject.getString(TAG_TOUR);
                    String time = jsonObject.getString(TAG_TIME);
                    String date = jsonObject.getString(TAG_DATE);
                    String ktime = jsonObject.getString(TAG_KTIME);


                    long diffMin = TimeFunction.getTimeDiff(time, ktime);




                    HashMap<String, String> map = new HashMap<>();
                    map.put(TAG_TOUR, tour);
                    map.put(TAG_DATE, date);
                    map.put(TAG_KTIME, diffMin+":00");

                    mToursList.add(map);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void updateList() {


            pDialog.dismiss();

            adapter = new SimpleAdapter(HomeActivity.this,
                    mToursList,
                    R.layout.single_item,
                    new String[]{TAG_TOUR, TAG_DATE, TAG_KTIME},
                    new int[]{R.id.txttour, R.id.txtdate, R.id.txtktime
                    });

            lv = (ListView) findViewById(R.id.lvtouren);

            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    String ktime = mToursList.get((int)id).get(TAG_KTIME).toString();
                    String[] pktime = ktime.split(":");
                    Intent i = new Intent(HomeActivity.this, AlertActivity.class);

                    i.putExtra("tour", mToursList.get((int)id).get(TAG_TOUR).toString());
                    i.putExtra("datum", mToursList.get((int)id).get(TAG_DATE).toString());
                    i.putExtra("ktime", pktime[0]);

                    startActivity(i);


                }


            });


        }
}






