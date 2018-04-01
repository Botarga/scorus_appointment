package com.projects.bordarga.scorusappointment;

/**
 * Created by Mario Vivas
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetWebService extends AsyncTask<String,Void,String> {
    public enum ConnectionType {UPDATE_FREE_HOURS, DOWNLOAD_BUSY_HOURS, DOWNLOAD_APPOINTMENTS};

    private ProgressDialog progressDialog;
    private Context context;
    private ConnectionType type;
    private boolean remember;
    private ListView productsList;

    public GetWebService(ProgressDialog progressDialog, Context context, ConnectionType type, boolean remember){
        this.progressDialog = progressDialog;
        this.context = context;
        this.type = type;
        this.remember = remember;
    }

    @Override
    protected String doInBackground(String... params) {
        String urlContent = params[0];
        URL url = null;
        String result ="";

        try {
            url = new URL(urlContent);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                    " (Linux; Android 1.5; es-ES) Ejemplo HTTP");

            int respuesta = connection.getResponseCode();
            StringBuilder sbResult = new StringBuilder();

            if (respuesta == HttpURLConnection.HTTP_OK){
                InputStream in = new BufferedInputStream(connection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    sbResult.append(line);        // Paso toda la entrada al StringBuilder
                }

                result = sbResult.toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onCancelled(String s) {
        super.onCancelled(s);
    }

    @Override
    protected void onPostExecute(String s) {
        switch (type){
            case UPDATE_FREE_HOURS:
                progressDialog.dismiss();

                break;

            case DOWNLOAD_BUSY_HOURS:
                String result = s;
                if(result != null && !result.isEmpty()){
                    String[] allHours = result.split(";");
                    for(int i = 0; i < allHours.length; ++i){
                        String[] dateTime = allHours[i].split(" ");
                        String[] dateData = dateTime[0].split("-");
                        MyDay auxMyDay = new MyDay(Integer.parseInt(dateData[2]),
                                Integer.parseInt(dateData[1]), Integer.parseInt(dateData[0]));

                        // Search current task in global tasks
                        String[] hourData = dateTime[1].split(":");

                        int calculatedIndex = (Integer.parseInt(hourData[0]) - 8) + (hourData[1].compareTo("30") == 0? 1 : 0) * 2;
                        MainActivity.allTasks.get(auxMyDay).get(calculatedIndex).setFree(false);
                    }
                }

                progressDialog.dismiss();


                break;

            case DOWNLOAD_APPOINTMENTS:
                result = s;
                String[] allAppointments = s.split(";");

                for(String ap : allAppointments){
                    String[] singleAppointment = ap.split("@");
                    String[] dateFullData = singleAppointment[1].split(" ");
                    String[] dateData = dateFullData[0].split("-");
                    String[] hourData = dateFullData[1].split(":");

                    MyDay auxMyDay = new MyDay(Integer.parseInt(dateData[2]), Integer.parseInt(dateData[1]), Integer.parseInt(dateData[0]));
                    int calculatedIndex = (Integer.parseInt(hourData[0]) - 8) + (hourData[1].compareTo("30") == 0? 1 : 0) * 2;


                    if(singleAppointment[2].compareTo("1") == 0)
                        MainActivity.allTasks.get(auxMyDay).get(calculatedIndex).set(singleAppointment[0], false, true, true);
                    else
                        MainActivity.allTasks.get(auxMyDay).get(calculatedIndex).set(singleAppointment[0], false, false, true);
                }


                break;
        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}