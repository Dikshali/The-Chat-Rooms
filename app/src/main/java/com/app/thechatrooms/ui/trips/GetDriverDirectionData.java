package com.app.thechatrooms.ui.trips;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GetDriverDirectionData extends AsyncTask<Object, String, String > {

    GoogleMap mMap;
    String url;
    LatLng start, end, driversLocation;
    HttpURLConnection httpURLConnection = null;
    String data="";
    InputStream inputStream =null;
    Context context;

    public GetDriverDirectionData(Context c){
        this.context = c;
    }


    @Override
    protected String doInBackground(Object... objects) {

        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        start = (LatLng) objects[2];
        end = (LatLng) objects[3];

        try {
            URL myUrl = new URL(url);
            httpURLConnection = (HttpURLConnection) myUrl.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();
            String lines ="";

            while((lines = bufferedReader.readLine())!=null){
                sb.append(lines);
            }
            data = sb.toString();
            bufferedReader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return data;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
            JSONObject jsonBound = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONObject("bounds");
            JSONObject jsonSouthWest = jsonBound.getJSONObject("southwest");
            JSONObject jsonNorthEast = jsonBound.getJSONObject("northeast");
            LatLng boundSouthWest = new LatLng(jsonSouthWest.getDouble("lat"),jsonSouthWest.getDouble("lng"));
            LatLng boundNorthEast = new LatLng(jsonNorthEast.getDouble("lat"),jsonNorthEast.getDouble("lng"));
            ArrayList<LatLng> bounds = new ArrayList<LatLng>();
            bounds.add(boundNorthEast);
            bounds.add(boundSouthWest);

            int count = jsonArray.length();
            String[] polyline_array = new String[count];

            JSONObject jsonObject1;

            for(int i=0;i<count;i++){
                jsonObject1 = jsonArray.getJSONObject(i);
                String polygon = jsonObject1.getJSONObject("polyline").getString("points");
                polyline_array[i] = polygon;
            }
            int count2 = polyline_array.length;

            for(int i=0;i<count2;i++){
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.color(Color.BLUE);
                polylineOptions.width(10);
                polylineOptions.addAll(PolyUtil.decode(polyline_array[i]));
                mMap.addPolyline(polylineOptions);
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(bounds.get(0));
            builder.include(bounds.get(1));

            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 200));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
