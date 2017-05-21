package com.system.bank.bankechosystem;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

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

        List<BarEntry> entries = new ArrayList<>();
        BarEntry entry = new BarEntry(4f, 4F);
        entries.add(new BarEntry(0f, 3f));
        entries.add(new BarEntry(1f, 8f));
        entries.add(new BarEntry(2f, 6f));
        entries.add(new BarEntry(3f, 5f));




        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);


        String[] str = {"FOOd", "Entertainment", "FOOd", "Entertainment", "FOOd"};
        BarDataSet set = new BarDataSet(entries, "BarDataSet");
        set.setColors( Color.YELLOW, Color.BLUE, Color.RED, Color.CYAN);
        set.setStackLabels(str);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        BarData data = new BarData(set);
        data.setBarWidth(0.6f); // set custom bar width
        chart.setData(data);
        chart.setDrawGridBackground(false);
        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh

//        List<BarEntry> entries = new ArrayList<>();
//
//        for (float data : dataObjects) {
//
//            // turn your data into Entry objects
//            entries.add(new BarEntry(data, data));
//        }
//        BarDataSet dataSet = new BarDataSet(entries, "Label");
//        BarData lineData = new BarData(dataSet);
//        chart.setData(lineData);
//        chart.invalidate(); // refresh

        new PutData().execute();
        new GetData().execute();


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
                    return null;
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
        protected void onPostExecute(Void aVoid) {
            Log.e("TAG", "data: " + aVoid);
            super.onPostExecute(aVoid);
        }
    }
}
