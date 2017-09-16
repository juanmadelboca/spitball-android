package com.kalantos.spitball.utils;

import android.os.AsyncTask;
import android.util.Log;
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
 */

public class ConnectionTask extends AsyncTask<String,Void,String> {

    @Override
    protected String doInBackground(String... strings) {
    /*
    * conect with an url and send the data received by parameters.
    * */
        HttpURLConnection connection;
        BufferedReader reader;

        try {
            URL url = new URL(strings[0]);

            connection = (HttpURLConnection)
                    url.openConnection();
            connection.setRequestMethod("POST");
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            connection.connect();
            JSONObject json= new JSONObject();
            json.put("METHOD", strings[1]);
            ////////////////////
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(json.toString());
            wr.flush();
            //next line receive data that server sends
            Log.d("TEST","CONNECTED TO PHP SERVER");
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            String st = convertStreamToString(reader);
            Log.d("TEST",st);
            return st;

        }catch (MalformedURLException e){
            e.printStackTrace();
            return "MALA URL";
        }catch (android.os.NetworkOnMainThreadException e){
            e.printStackTrace();
            return "LO DE INTERNET";
        } catch (IOException e){
            e.printStackTrace();
            return "IO ERROR";
        }catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
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
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}

