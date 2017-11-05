package com.ucd.android.qwikscan.map;

import android.os.AsyncTask;
import android.util.Log;

import com.ucd.android.qwikscan.screens.MapFragmentView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class BackgroundTasks{

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }

    public class NearbyStoresTask extends AsyncTask<String, Integer, String> {

        String data = null;
        MapFragmentView mapFragment;

        public NearbyStoresTask(MapFragmentView mapFragment){
            this.mapFragment = mapFragment;
        }

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            MarkStoresTask markStores = new MarkStoresTask(mapFragment);


            markStores.execute(result);
        }
    }

    public class MarkStoresTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

        JSONObject jObject;
        MapFragmentView mapFragment;

        public MarkStoresTask(MapFragmentView mapFragment){
            this.mapFragment = mapFragment;
        }

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String,String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> stores = null;
            ResultJSONParser resultsParser = new ResultJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                stores = resultsParser.parse(jObject);

            }catch(Exception e){
            }
            return stores;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String,String>> list){
            mapFragment.asyncTaskDone(list);
        }
    }
}

