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
 * TODO: DUPLICATE!.
 */

public class SendMoveTask extends AsyncTask<String,Void,String> {

    @Override
    protected String doInBackground(String... strings) {
    /*
    * conect with an url and send the data received by parameters.
    * */
        HttpURLConnection connection;
        BufferedReader reader ;

        try {
            URL url = new URL(strings[0]);

            connection = (HttpURLConnection)
                    url.openConnection();
            connection.setRequestMethod("POST");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);
            connection.connect();
            JSONObject json= new JSONObject();
            json.put("METHODTYPE", strings[1]);
            json.put("XINIT",Integer.parseInt(strings[2]));
            json.put("YINIT",Integer.parseInt(strings[3]));
            json.put("XLAST",Integer.parseInt(strings[4]));
            json.put("YLAST",Integer.parseInt(strings[5]));
            json.put("SPLIT",Integer.parseInt(strings[6]));
            json.put("GAMEID",Integer.parseInt(strings[7]));
            json.put("TURN",Integer.parseInt(strings[8]));
            ////////////////////
            Log.d("JSONSEND",json.toString());
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(json.toString());
            wr.flush();
            //next line receive data that server sends
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            String st=convertStreamToString(reader);
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

