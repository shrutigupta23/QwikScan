package com.ucd.android.qwikscan.map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResultJSONParser {

    /** Receives a JSONObject and returns a list */
    public List<HashMap<String,String>> parse(JSONObject result){

        JSONArray array = null;
        try {
            array = result.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getStores(array);
    }
    /** Method that generates a list of store objects **/
    private List<HashMap<String, String>> getStores(JSONArray array){
        int storesCount = array.length();
        List<HashMap<String, String>> storeList = new ArrayList<HashMap<String,String>>();
        HashMap<String, String> store = null;

        for(int i=0; i<storesCount;i++){
            try {
                store = getStore((JSONObject)array.get(i));
                storeList.add(store);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return storeList;
    }

    /**
     * Parses the JSON results, to create a hashmap of store objects.
     * @param object
     * @return
     */
    private HashMap<String, String> getStore(JSONObject object){

        HashMap<String, String> store = new HashMap<String, String>();
        String storeName = "-NA-";
        String vicinity="-NA-";
        String latitude="";
        String longitude="";

        try {
            if(!object.isNull("name")){
                storeName = object.getString("name");
            }

            if(!object.isNull("vicinity")){
                vicinity = object.getString("vicinity");
            }

            latitude = object.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = object.getJSONObject("geometry").getJSONObject("location").getString("lng");

            store.put("store_name", storeName);
            store.put("vicinity", vicinity);
            store.put("lat", latitude);
            store.put("lng", longitude);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return store;
    }

}
