package com.kalantos.spitball.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Async task to conect server php.
 * TODO: REFACTOR APP, NOW ALL MESSAGES ARE POSTS, MAYBE GET WILL IMPROVE THE SPEED
 */
public class HTTPSocket extends AsyncTask<String,Void,String> {

    @Override
    protected String doInBackground(String... strings) {
    /*
    * Receive an URL a Method (post, get..), a Json or Plain text and make the Http request.
    * */
        String method, message;
        HttpURLConnection connection;
        BufferedReader reader ;
        method = strings[1];
        message = strings[2];

        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection)
                    url.openConnection();
            connection.setRequestMethod(method);
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);
            //connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            connection.connect();
            JSONObject json= new JSONObject(message);
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(json.toString());
            wr.flush();
            //next line receive data that server sends
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            String st=convertStreamToString(reader);
            return st;

        }catch (android.os.NetworkOnMainThreadException | IOException | JSONException e){
            Log.e("ONLINE CONNECTION", e.getMessage());
            return null;
        }
    }
    private String convertStreamToString(BufferedReader reader) {
    /*
    * return a string build from a data stream
    * */
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (Exception e) {
            Log.e("ONLINE CONNECTION", e.getMessage());
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                Log.e("ONLINE CONNECTION", e.getMessage());
            }
        }
        return sb.toString();
    }

}

