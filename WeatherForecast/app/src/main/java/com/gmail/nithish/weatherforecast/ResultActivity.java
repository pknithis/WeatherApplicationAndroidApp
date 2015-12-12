package com.gmail.nithish.weatherforecast;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

class ImageDownloader extends AsyncTask<String,Void,Bitmap> {

    private ImageView imageIcon;

    ImageDownloader(ImageView view)
    {
        this.imageIcon = view;
    }
    @Override
    protected Bitmap doInBackground(String... params) {

       try{
           Log.d("JSON image background ", params[0]);
           URL imageUrl=new URL(params[0]);
           return  BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());

       } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    protected void onPostExecute(Bitmap icon) {
        Log.d("JSON image postExecute ", "Entered");
       imageIcon.setImageBitmap(icon);
    }


}
public class ResultActivity extends AppCompatActivity {

    JSONObject jsonObj ;
    String temperatureType;
    String receivedIntentData;
    String location_details;
    String imageUriFb ;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    String fbDescriptionData;

    private String matchPrecipeValue(float precipVAl)
    {
        String precipIntensity="none";

        if(precipVAl >= 0 && precipVAl<0.002)
            precipIntensity="none";
        if(precipVAl >= 0.002 && precipVAl<0.017)
            precipIntensity="Very Light";
        if(precipVAl >= 0.017 && precipVAl<0.1)
            precipIntensity="Light";
        if(precipVAl >= 0.1 && precipVAl<0.4)
            precipIntensity="Moderate";
        if(precipVAl >= 0.4)
            precipIntensity="Heavy";

        return precipIntensity;
    }


    private String getWeatherTime( String timeType) throws JSONException {

        JSONObject daily= jsonObj.getJSONObject("daily");
        JSONArray dailyData=daily.getJSONArray("data");
        String timeZone = jsonObj.getString("timezone");
        Utilities.setTimeZone(timeZone);

        switch (timeType)
        {
            case "Sunrise":
                int sunriseTime = Integer.parseInt(dailyData.getJSONObject(0).getString("sunriseTime"));
                return Utilities.getFormattedTime(sunriseTime);

            case "Sunset":
                int sunsetTime = Integer.parseInt(dailyData.getJSONObject(0).getString("sunsetTime"));
                return Utilities.getFormattedTime(sunsetTime);

        }
        return "";
    }

    private String getWeatherData(String dataType,JSONObject jsonOb) {
        String returnData = "";
        try {
            switch (dataType) {
                case "Precipitation":
                    Float precipValue = Float.parseFloat(jsonOb.getString("precipIntensity"));
                    returnData = matchPrecipeValue(precipValue);
                    break;

                case "Chance Of Rain":
                    returnData = Math.round(Float.parseFloat(jsonOb.getString("precipProbability")) * 100) + "%";
                    break;

                case "Wind Speed":
                    returnData = Math.round(Float.parseFloat(jsonOb.getString("windSpeed"))) + Utilities.getUnit("ws");
                    break;

                case "Dew Point":
                    returnData = Math.round(Float.parseFloat(jsonOb.getString("dewPoint"))) + Utilities.getUnit("dp");
                    break;

                case "Humidity":
                    returnData = Math.round(Float.parseFloat(jsonOb.getString("humidity")) * 100) + "%";
                    break;

                case "Visibility":
                    returnData = Float.parseFloat(jsonOb.getString("visibility")) + Utilities.getUnit("vs");
                    break;

                case "Sunrise":
                    returnData = getWeatherTime( "Sunrise");
                    break;

                case "Sunset":
                    returnData = getWeatherTime( "Sunset");
                    break;
            }


        } catch (JSONException e) {
            e.printStackTrace();

        }
        return  returnData;
    }

    protected  void processJson(String jsonData) {
        try {
            jsonObj = new JSONObject(jsonData.substring(jsonData.indexOf("{")));
            JSONObject currently= jsonObj.getJSONObject("currently");
            JSONObject daily= jsonObj.getJSONObject("daily");
            JSONArray dailyData=daily.getJSONArray("data");

            String summary = currently.getString("summary");
            Log.d("Summary Json ", summary);

            String locationData = jsonData.substring(0, jsonData.indexOf("{"));
            Log.d("Location Json ", locationData);

            String location[] = locationData.split(";");
            location_details = location[0]+" "+location[1];
            Utilities.setTmperatureType(location[2]);

            TextView weatherDetail = (TextView) findViewById(R.id.weatberDetail);
            weatherDetail.setText(summary + " in " + location[0] + "," + location[1]);
            imageUriFb = currently.getString("icon");

            ImageView weatherIcon = (ImageView)findViewById(R.id.weatberImage);
            //weatherIcon.setImageResource(Utilities.getImageId(currently.getString("icon")));
            new ImageDownloader(weatherIcon).execute(Utilities.imageURI(currently.getString("icon")));

            TextView tempField = (TextView) findViewById(R.id.tempField);
            String temp =(Math.round(Float.parseFloat(currently.getString("temperature"))))+"";

            tempField.setText(Html.fromHtml(String.valueOf(Math.round(Float.parseFloat(currently.getString("temperature")))) + "<sup><small>" + Utilities.getUnit("tp") + "<small></sup>"));
            fbDescriptionData =  summary +","+temp;

            TextView lowHightempField = (TextView) findViewById(R.id.lowHighTemp);
            String degSymbol = Html.fromHtml("<span>&deg<span>")+"";

            String lowTemp = Math.round(Float.parseFloat(dailyData.getJSONObject(0).getString("temperatureMin")))+degSymbol;
            String highTemp = Math.round(Float.parseFloat(dailyData.getJSONObject(0).getString("temperatureMin")))+degSymbol;
            String lowHighString ="L:"+lowTemp+" | H:"+highTemp;
            lowHightempField.setText(lowHighString);


            String weatherParams[]={"Precipitation","Chance Of Rain","Wind Speed","Dew Point","Humidity","Visibility","Sunrise","Sunset"};

            TableLayout tableLayout = (TableLayout)findViewById(R.id.weatherDetailedTable);
            for(int i = 0;i<weatherParams.length;i++)
            {
                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                TextView dataType = new TextView(this);
                dataType.setText(weatherParams[i]);
                dataType.setPadding(10,10,10,10);

                TextView dataParam = new TextView(this);
                dataParam.setText(getWeatherData(weatherParams[i],currently));
                dataParam.setPadding(10,10,10,10);

                row.addView(dataType);
                row.addView(dataParam);

                tableLayout.addView(row);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_result);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(getApplicationContext(), "Facebook Post Successful", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Post Cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Posting Error", Toast.LENGTH_LONG).show();
            }
        });

        Intent it = getIntent();
        if(it!=null)
        {
            String jsonData = it.getStringExtra("JSONDATA");
            receivedIntentData = jsonData;
            Log.d("JSONDATA RECEIVED",jsonData);
            processJson(jsonData);
        }
        else
        {
            Log.d("Error Intent ","itent received is null");
        }

    }

    public void moreDetailsClickListener(View view) {
        Intent toDetailsActivity = new Intent(this, DetailsActivity.class);
        toDetailsActivity.putExtra("JSONDATA",receivedIntentData);
        this.startActivity(toDetailsActivity);
    }

    public void viewMapClickListener(View view) {
        Intent toMapActivity = new Intent(this, MapActivity.class);
        toMapActivity.putExtra("LOCATION",location_details);
        this.startActivity(toMapActivity);

    }

    public void fbIconListener(View view) {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Current Weather in " + location_details)
                    .setContentDescription(
                            fbDescriptionData)
                    .setContentUrl(Uri.parse("http://forecast.io/")).setImageUrl(Uri.parse(Utilities.imageURI(imageUriFb)))
                    .build();

            shareDialog.show(linkContent);
        }



    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
