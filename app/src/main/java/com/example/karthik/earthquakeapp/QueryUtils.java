package com.example.karthik.earthquakeapp;

import android.text.TextUtils;
import android.util.Log;

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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by karthik on 11/3/17.
 */

public class QueryUtils {

    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the USGS dataset and return a list of {@link Earthquake} objects.
     */
    public static List<Earthquake> fetchEarthquakeData2(String requestUrl) {
        // An empty ArrayList that we can start adding earthquakes to
        List<Earthquake> earthquakes = new ArrayList<>();
        //  URL object to store the url for a given string
        URL url = null;
        try{
            url = new URL(requestUrl);
        } catch (MalformedURLException e){
            Log.e(LOG_TAG, "Cannot create Url from String", e);
        }
        // A string to store the response obtained from rest call in the form of string
        String jsonResponse = "";
        try {
                // check TODO: 1. Create a URL from the requestUrl string and make a GET request to it
                // check TODO: 2. Read from the Url Connection and store it as a string(jsonResponse)
                /* check TODO: 3. Parse the jsonResponse string obtained in step 2 above into JSONObject to extract the values of
                        "mag","place","time","url"for every earth quake and create corresponding Earthquake objects with them
                        Add each earthquake object to the list(earthquakes) and return it.
                */

                jsonResponse = request(url);
                //extract
                try{
                    JSONObject baseJson = new JSONObject(jsonResponse);

                    JSONArray earthquakeArr = baseJson.getJSONArray("features");

                    for (int i =0; i < earthquakeArr.length(); i++){

                        JSONObject earthquake = earthquakeArr.getJSONObject(i);

                        JSONObject properties = earthquake.getJSONObject("properties");

                        double mag = properties.getDouble("mag");
                        String place = properties.getString("place");
                        long time = properties.getLong("time");
                        String quakeUrl = properties.getString("url");

                        Earthquake NewEarthquake = new Earthquake(mag,place, time, quakeUrl);

                        earthquakes.add(NewEarthquake);
                    }
                } catch (Exception e ){
                    Log.e(LOG_TAG, "Error retrieving earthquakes list");
                }


            // Return the list of earthquakes

        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception:  ", e);
        }
        // Return the list of earthquakes
        return earthquakes;
    }

    public static String request(URL url) throws IOException{
        String response = "";

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                response = readStream(inputStream);
            } else{
                Log.e(LOG_TAG, "Error getting response" );
            }
        }catch (IOException e){
            Log.e(LOG_TAG, "Error getting response", e);
        }finally{
            if (urlConnection != null){
                urlConnection.disconnect();
            }

            //if inputstream is not null
            inputStream.close();
        }
        return response;
    }

    public static String readStream(InputStream inputStream) throws IOException{
        StringBuilder response = new StringBuilder();

        if (inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader in = new BufferedReader(inputStreamReader);
            String line = in.readLine();
            while (line!= null){
                response.append(line);
                line = in.readLine();
            }
            in.close();
        }
        return response.toString();
    }


}
