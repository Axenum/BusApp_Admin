package com.allits.BusAdmin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Stefan on 12.05.2015.
 */
public class AlertActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent i = getIntent();

        Bundle extras = i.getExtras();

        final String tour = extras.getString("tour");
        final String datum = extras.getString("datum");
        final String ktime = extras.getString("ktime");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.latetours);


        TextView txttour = (TextView) findViewById(R.id.txttour);
        TextView txtfahrer = (TextView) findViewById(R.id.txtfahrer);
        TextView txtzeit = (TextView) findViewById(R.id.txtzeit);
        TextView txtkzeit = (TextView) findViewById(R.id.txtkzeit);

        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        final String eMailId = prefs.getString("eMailId", "");

        new LoadDetails(tour, datum).execute();


    }

    private static final String TAG_POSTS = "posts";
    private static final String TAG_TOUR = "tour";
    private static final String TAG_DATE = "datum";
    private static final String TAG_TIME = "time";
    private static final String TAG_KTIME = "ktime";
    private static final String TAG_FAHRER = "username";



    private JSONArray mTours = null;
    public ArrayList<HashMap<String, String>> mToursList = new ArrayList<HashMap<String, String>>();

    private ProgressDialog pDialog;


    public class LoadDetails extends AsyncTask<Void, Void, Boolean> {


        String tour;
        String datum;


        public LoadDetails(String tour, String datum) {
            this.tour = tour;
            this.datum = datum;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AlertActivity.this);
            pDialog.setMessage("Loading Details...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {

            updateJSONdata(tour, datum);

            return null;

        }


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            updateList();
            pDialog.dismiss();
        }

        public void updateJSONdata(String tour, String datum) {


            List<NameValuePair> params;

            JSONParser jParser = new JSONParser();
            JSONObject json = null;

            params = new ArrayList<>();
            params.add(new BasicNameValuePair("admin", "yes"));
            params.add(new BasicNameValuePair("tour", tour));
            params.add(new BasicNameValuePair("datum", datum));

            json = jParser.makeHttpRequest("http://allits.de/gcm/getDetails.php", "POST", params);



            try {


                mTours = json.getJSONArray(TAG_POSTS);

                // looping through all posts according to the json object returned
                for (int i = 0; i < mTours.length(); i++) {
                    JSONObject jsonObject = mTours.getJSONObject(i);

                    //gets the content of each tag
                    String fahrt = jsonObject.getString(TAG_TOUR);
                    String fahrer = jsonObject.getString(TAG_FAHRER);
                    String date = jsonObject.getString(TAG_DATE);
                    String time = jsonObject.getString(TAG_TIME);
                    String ktime = jsonObject.getString(TAG_KTIME);



                    HashMap<String, String> map = new HashMap<>();
                    map.put(TAG_TOUR, fahrt);
                    map.put(TAG_FAHRER, fahrer);
                    map.put(TAG_DATE, date);
                    map.put(TAG_TIME, time);
                    map.put(TAG_KTIME, ktime);

                    mToursList.add(map);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void updateList() {



            pDialog.dismiss();


            TextView txttour = (TextView) findViewById(R.id.txttour);
            TextView txtfahrer = (TextView) findViewById(R.id.txtfahrer);
            TextView txtdate = (TextView) findViewById(R.id.txtdatum);
            TextView txtzeit = (TextView) findViewById(R.id.txtzeit);
            TextView txtkzeit = (TextView) findViewById(R.id.txtkzeit);

            txttour.setText(mToursList.get(0).get(TAG_TOUR));
            txtfahrer.setText(mToursList.get(0).get(TAG_FAHRER));
            txtdate.setText(mToursList.get(0).get(TAG_DATE));
            txtzeit.setText(mToursList.get(0).get(TAG_TIME));
            txtkzeit.setText(mToursList.get(0).get(TAG_KTIME));





        }


    }


}
