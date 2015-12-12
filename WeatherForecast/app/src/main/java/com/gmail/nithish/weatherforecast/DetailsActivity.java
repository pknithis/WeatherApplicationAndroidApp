package com.gmail.nithish.weatherforecast;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class DetailsActivity extends AppCompatActivity {
    JSONObject jsonObj ;
    String jsonStringReceived;
    String tempType;

    private void preProcessJsonData(String jsonData)
    {
        try {
            jsonObj = new JSONObject(jsonData.substring(jsonData.indexOf("{")));
            JSONObject currently = jsonObj.getJSONObject("currently");
            JSONObject daily = jsonObj.getJSONObject("daily");
            JSONArray dailyData = daily.getJSONArray("data");

            String locationData = jsonData.substring(0, jsonData.indexOf("{"));
            Log.d("Location Json ", locationData);
            String location[] = locationData.split(";");

            TextView locationInd = (TextView)findViewById(R.id.placeIndicator);
            locationInd.setText("More Details for "+location[0]+","+location[1]);

            if(location[2].equals("Fahrenheit"))
            {
                tempType= Html.fromHtml("&deg")+"F";
            }
            else
            {
                tempType=Html.fromHtml("&deg")+"C";
            }

            next24OnClickListener(null);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent it = getIntent();
        if(it!=null)
        {
            String jsonData = it.getStringExtra("JSONDATA");
            jsonStringReceived = jsonData;
            preProcessJsonData(jsonData);
            Log.d("JSONDATA DETAILS", jsonData);
        }
        else
        {
            Log.d("Error Intent DETAILS","itent received is null");
        }
        Button next24Button = (Button)findViewById(R.id.hourDetails);
        Utilities.setDefaultButtonColor(next24Button.getDrawingCacheBackgroundColor());
        next24Button.setBackgroundColor(Color.parseColor("#C8F1FF"));

    }
    private void populateRow(final TableLayout tableLayout,final TableRow.LayoutParams lp ,int start , int end) throws JSONException {
        JSONObject hourly = jsonObj.getJSONObject("hourly");
        final JSONArray hourlyData = hourly.getJSONArray("data");

        for (int i = start; i < end; i++) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(lp);

            if(i%2!=0)
                row.setBackgroundColor(Color.parseColor("#CBCBCB"));

            String unixTimeVal=hourlyData.getJSONObject(i).getString("time");
            Log.d("24Time ",unixTimeVal);
            TextView timeValue = new TextView(this);
            timeValue.setTypeface(null,Typeface.BOLD);
            timeValue.setText(Utilities.getFormattedTime(Long.parseLong(unixTimeVal)));
            row.addView(timeValue);

            String iconVal = hourlyData.getJSONObject(i).getString("icon");
            ImageView imgView = new ImageView(this);
            TableRow.LayoutParams tlp= new TableRow.LayoutParams(100, 100);
            tlp.setMargins(0,0,100,0);
            imgView.setLayoutParams(tlp);
            imgView.setImageResource(Utilities.getImageId(iconVal));

            row.addView(imgView);

            String tempVal = hourlyData.getJSONObject(i).getString("temperature");
            TextView tempText = new TextView(this);
            tempText.setText(Math.round(Float.parseFloat(tempVal))+"");

            row.addView(tempText);

            tableLayout.addView(row);
        }
    }

    public void next24OnClickListener(View view) {
        Button hourButton = (Button)findViewById(R.id.hourDetails);
        Button dayButton = (Button)findViewById(R.id.dayDetails);

        hourButton.setBackgroundColor(Color.parseColor("#C8F1FF"));
        dayButton.setBackgroundColor(Utilities.getDefaultButtonColor());

        final TableLayout tableLayout = (TableLayout)findViewById(R.id.weatherDetailedTable);
        final TableRow.LayoutParams lp = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tableLayout.removeAllViews();

        TableRow junkRow = new TableRow(this);
        junkRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ImageView junkImage = new ImageView(this);
        TableRow.LayoutParams jtlp= new TableRow.LayoutParams(25, 25);
        junkImage.setLayoutParams(jtlp);
        junkRow.addView(junkImage);
        tableLayout.addView(junkRow);


        TableRow rowMain = new TableRow(this);
        rowMain.setLayoutParams(lp);

        TextView time = new TextView(this);
        time.setText("Time");
        time.setTypeface(null, Typeface.BOLD);

        TextView summary = new TextView(this);
        summary.setText("Summary");
        summary.setTypeface(null, Typeface.BOLD);

        TextView temp = new TextView(this);
        temp.setText("Temp" + tempType);
        temp.setTypeface(null, Typeface.BOLD);

        rowMain.addView(time);
        rowMain.addView(summary);
        rowMain.addView(temp);
        rowMain.setBackgroundColor(Color.parseColor("#84ECE6"));
        tableLayout.addView(rowMain);


        try {
            populateRow(tableLayout, lp, 1, 25);

            TextView junk1 = new TextView(this);
            junk1.setText("    ");

            TextView junk2 = new TextView(this);
            junk2.setText("    ");

            final TableRow buttonRow = new TableRow(this);
            buttonRow.setLayoutParams(lp);

            Button expandBt = new Button(this);
            expandBt.setText(Html.fromHtml("<b>+<b>"));
            expandBt.setBackgroundColor(Color.parseColor("#50A1DE"));
            expandBt.setLayoutParams(new TableRow.LayoutParams(100, 100));
            buttonRow.addView(junk1);
            buttonRow.addView(expandBt);
            buttonRow.addView(junk2);
            tableLayout.addView(buttonRow);


            expandBt.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    for(int i =0;i<tableLayout.getChildCount();i++)
                    {
                        if(tableLayout.getChildAt(i) == buttonRow)
                        {
                            tableLayout.removeViewAt(i);
                            Log.d("VIEWREMOVED","ix"+i);
                            break;
                        }
                    }

                    try {
                        populateRow(tableLayout,lp,25,48);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void next7DaysOnClickListener(View view) {
        Button hourButton = (Button)findViewById(R.id.hourDetails);
        Button dayButton = (Button)findViewById(R.id.dayDetails);

        dayButton.setBackgroundColor(Color.parseColor("#C8F1FF"));
        hourButton.setBackgroundColor(Utilities.getDefaultButtonColor());


        final TableLayout tableLayout = (TableLayout)findViewById(R.id.weatherDetailedTable);
        tableLayout.removeAllViews();
        final TableRow.LayoutParams lp = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,100,0,0);
        try {
            JSONObject daily = jsonObj.getJSONObject("daily");
            final JSONArray dailyData = daily.getJSONArray("data");
            String colorValues[]={"","#FEDA69","#9FE6FE","#FEC3E9","#C4FFA5","#FFBDB7","#EFFFB5","#BCBEFF"};
            for (int i = 1; i <= 7; i++) {

                String time = dailyData.getJSONObject(i).getString("time");
                Utilities.prepareCalendar(time);

                String minTemp = Math.round(Float.parseFloat(dailyData.getJSONObject(i).getString("temperatureMin")))+Utilities.getUnit("tp");
                String maxTemp = Math.round(Float.parseFloat(dailyData.getJSONObject(i).getString("temperatureMax")))+Utilities.getUnit("tp");
                String minMaxTemp = "\n\nMin: "+minTemp+"|"+"Max: "+maxTemp+"\n";

                TableRow row = new TableRow(this);

                row.setLayoutParams(lp);
                TableRow junkRow = new TableRow(this);
                junkRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                TextView dateVal = new TextView(this);
                dateVal.setText(Utilities.getDayOfWeek() + "," + Utilities.getMonth() + " " + Utilities.getDayOfMonth()+minMaxTemp);
                dateVal.setTypeface(null,Typeface.BOLD);

                String iconVal = dailyData.getJSONObject(i).getString("icon");
                ImageView imgView = new ImageView(this);
                TableRow.LayoutParams tlp= new TableRow.LayoutParams(100, 100);
                tlp.setMargins(0, 0, 100, 0);
                imgView.setLayoutParams(tlp);
                imgView.setImageResource(Utilities.getImageId(iconVal));

                ImageView junkImage = new ImageView(this);
                TableRow.LayoutParams jtlp= new TableRow.LayoutParams(25,25);
                junkImage.setLayoutParams(jtlp);


                row.addView(dateVal);
                row.addView(imgView);

                junkRow.addView(junkImage);

                row.setBackgroundColor(Color.parseColor(colorValues[i]));
                tableLayout.addView(junkRow);
                tableLayout.addView(row);
            }


        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
