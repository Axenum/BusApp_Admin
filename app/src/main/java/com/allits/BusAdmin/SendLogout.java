package com.allits.BusAdmin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan on 03.06.2015.
 */

public class SendLogout extends AsyncTask<String, Void, String> {

    ProgressDialog pDialog;
    String username;
    Activity A;

    public SendLogout(Activity A, String username){
        this.username = username;

        this.A = A;
    }



    @Override
    protected void onPreExecute() {

        pDialog = new ProgressDialog(A);
        pDialog.setMessage("Logging out. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();


    }

    @Override
    protected String doInBackground(String... url) {

       return logout(url[0], username);

    }

    @Override
    protected void onPostExecute(String result) {
        pDialog.dismiss();

        Log.d("HTTP", result);

        if(result.contains("\"success\":1")){
            Intent i = new Intent(A,MainActivity.class );
            A.getSharedPreferences("UserDetails",0).edit().clear().commit();
            A.startActivity(i);


        }else{
            Toast.makeText(A, "Fehler beim Ausloggen", Toast.LENGTH_SHORT).show();
        }
    }


    public String logout(String url, String username){

        InputStream inputStream = null;
        String result = "";


        try {


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("username", username));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse httpResponse = httpclient.execute(httpPost);

            result = EntityUtils.toString(httpResponse.getEntity());

        } catch (ClientProtocolException e) {
            Log.d("InputStream", e.getLocalizedMessage());
        } catch (IOException e){

        }

        return result+"";

    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}