package com.system.bank.bankechosystem;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SpendAnalyzer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spend_analyzer);
        BarChart chart = (BarChart) findViewById(R.id.chart);

        List<BarEntry> entries1 = new ArrayList<>();
        BarEntry entry = new BarEntry(0f, 4F);
        entries1.add(entry);
        List<BarEntry> entries2 = new ArrayList<>();
        BarEntry entry2 = new BarEntry(1f, 6F);
        entries2.add(entry2);
        BarDataSet set1 = new BarDataSet(entries1, "FOOD");
        BarDataSet set2 = new BarDataSet(entries2, "ENTERTAINMENT");
        set1.setColors( Color.YELLOW);
        set2.setColors( Color.BLUE);

        BarEntry entry3 = new BarEntry(2f, 6F);
        List<BarEntry> entries3 = new ArrayList<>();
        entries3.add(entry3);
        BarDataSet set3 = new BarDataSet(entries3, "MISC");
        set3.setColors( Color.RED);

        BarData data = new BarData(set1, set2, set3);
        data.setBarWidth(0.6f); // set custom bar width
        chart.setData(data);
        chart.setDrawBorders(false);
        chart.setDrawGridBackground(false);
        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh




//        new PutData().execute();
//        new GetData().execute();

    }

    private class GetData extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... voids) {
            try {
                Log.e("TAG", "executing" );
                URL url = new URL("https://hackathonairtel.firebaseio.com/transaction.json");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();


                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String aVoid) {
            Log.e("TAG", "data: " + aVoid);
//            try {
                ArrayList<TransactionDto> list = new ArrayList<>();
                try {
                    JSONArray obj = new JSONArray(aVoid);
                    for(int i = 0 ; i< obj.length(); i++){
                        Object o = obj.get(i);
                        if ( o.equals("null")) {
                            Log.e("TAG", "null found");
                        }
                        else {
                            JSONObject data = obj.getJSONObject(i);
                            if (data != null) {
                                TransactionDto dto = new TransactionDto();
                                dto.setAmount(data.getString("amount"));
                                list.add(dto);
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.print(list);

//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            super.onPostExecute(aVoid);
        }
    }

    private class PutData extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Log.e("TAG", "Putting" );
                URL url = new URL("https://hackathonairtel.firebaseio.com/transaction/6.json");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("PUT");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");
                    OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
                    osw.write(String.format("{\"amount\":34,\"id\":2,\"merchant_category\":\"food\",\"merchant_name\":\"tesr\",\"phone_number\":899999999,\"timestamp\":2333232}"));
                    osw.flush();
                    osw.close();
                    Log.e("TAG",connection.getResponseCode() + "");


//                    urlConnection.setDoOutput(true);
//                    urlConnection.setRequestMethod("PUT");
//
//                    urlConnection.setRequestProperty(
//                            "Content-Type", "application/json");
//                    OutputStreamWriter out = new OutputStreamWriter(
//                            urlConnection.getOutputStream());
//                    out.write("{\"amount\":34,\"id\":2,\"merchant_category\":\"food\",\"merchant_name\":\"tesr\",\"phone_number\":899999999,\"timestamp\":2333232}");
//                    out.close();
//                    return null;
                }
                finally{
//                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.e("TAG", "data: " + aVoid);
            super.onPostExecute(aVoid);
        }
    }
}
