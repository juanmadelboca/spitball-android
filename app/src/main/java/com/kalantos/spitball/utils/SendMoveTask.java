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
 * Created by Juanma on 24/3/2017.
 */

public class SendMoveTask extends AsyncTask<String,Void,String> {
    //conecta con una url y envia los datos que sean escritos dentro del output stream
    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(strings[0]);

            connection = (HttpURLConnection)
                    url.openConnection();
            connection.setRequestMethod("POST");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);
            connection.connect();
            //la linea siguiente manda un Json
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
            //la linea siguiente recibe lo que el server devuelve
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            String st=convertStreamToString(reader);
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
        //devuelve un string a partir del stream que recibio
        StringBuilder sb = new StringBuilder();

        String line = null;
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

