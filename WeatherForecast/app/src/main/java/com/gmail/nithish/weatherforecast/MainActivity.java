package com.gmail.nithish.weatherforecast;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

class JsonDataDownloader extends AsyncTask<String,Void,Void> {

    private Activity parentActivity;
    JsonDataDownloader(Activity activity)
    {
        parentActivity = activity;
    }
    @Override
    protected Void doInBackground(String... params) {

        Log.d("JsonData","Async entered");
        String urlDetails = params[0];
        Log.d("JsonData url",urlDetails);
        String urlParams[] = urlDetails.split(":");
        String streetAddress= urlParams[0];
        String city=urlParams[1];
        String state=urlParams[2];
        String temp=urlParams[3];
        String url="http://nithishkp.elasticbeanstalk.com/index/processhw8.php?Address="+URLEncoder.encode(streetAddress)+"&City="+URLEncoder.encode(city)+"&State="+URLEncoder.encode(state)+"&Temperature="+temp;


        Log.d("JsonData URL string",url);
        URL urlOb= null;
        try {
            urlOb = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JSONObject job=null;
        StringBuilder jsonData=new StringBuilder();
        BufferedInputStream bin = null;
        try {
            HttpURLConnection httpconnection = (HttpURLConnection)urlOb.openConnection();
            httpconnection.connect();
            int response = httpconnection.getResponseCode();
            Log.d("Http Response",response+"");
            InputStream ip = httpconnection.getInputStream();
            bin = new BufferedInputStream(ip);
            int  data = bin.read();

            while (data!=-1)
            {
                jsonData.append((char)data);
                data = bin.read();
            }
            String locationDetails=city+";"+state+";"+temp;

            Intent toResultActivity = new Intent(this.parentActivity, ResultActivity.class);
            toResultActivity.putExtra("JSONDATA",locationDetails+jsonData.toString());
            parentActivity.startActivity(toResultActivity);


            Log.d("JSON DATA",jsonData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }



        return null;
    }


}
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void aboutClickHandler(View view) {
        startActivity(new Intent(this, AboutActivity.class));
    }
    public void forecastIoListener(View view)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://forecast.io/"));
        startActivity(browserIntent);
    }
    public void radioButtonClickHandler(View view)
    {
        Log.d("RB Clicked","Success1");
        RadioButton clickedButton = (RadioButton)view;
        int name=clickedButton.getId();
        Log.d("RB Clicked","Success2" );
        if(name == R.id.fah)
        {
            RadioButton otherButtonCel = (RadioButton)findViewById(R.id.cel);
            otherButtonCel.setChecked(false);
        }
        else
        {
            RadioButton otherButtonFah = (RadioButton)findViewById(R.id.fah);
            otherButtonFah.setChecked(false);
        }
        Log.d("RB Clicked", "Success3");
    }

    public void searchClickHandler(View view) {
        String streetAddress = ((EditText)findViewById(R.id.edtAddress)).getText()+"";
        String city = ((EditText)findViewById(R.id.edtCity)).getText()+"";
        String state = ((Spinner)findViewById(R.id.spState)).getSelectedItem().toString();
        String temp="";

        if(((RadioButton)findViewById(R.id.fah)).isChecked())
        {
           temp="Fahrenheit";
        }
        else
        {
            temp="Celsius";
        }
        TextView errorText = (TextView)findViewById(R.id.txtError);
        if(streetAddress.length() == 0)
        {
            errorText.setText("Please enter Street Address");
            return;
        }
        else if(city.length() == 0)
        {
            errorText.setText("Please enter City");
            return;
        }
        else if(state.equals("Select"))
        {
            errorText.setText("Please select State");
            return;
        }else
        {
            errorText.setText("");
        }

        String finalUrl = streetAddress+":"+city+":"+state+":"+temp;
        //Toast.makeText(getApplicationContext(), finalUrl,Toast.LENGTH_LONG).show();
        new JsonDataDownloader(this).execute(finalUrl);
    }

    public void clearButtonListener(View view) {
        EditText street = (EditText)findViewById(R.id.edtAddress);
        street.setText("");
        EditText city = (EditText)findViewById(R.id.edtCity);
        city.setText("");
        Spinner states = (Spinner)findViewById(R.id.spState);
        states.setSelection(0);
        RadioButton frb = (RadioButton)findViewById(R.id.fah);
        frb.setChecked(true);
        RadioButton crb = (RadioButton)findViewById(R.id.cel);
        crb.setChecked(false);
        TextView errorText = (TextView)findViewById(R.id.txtError);
        errorText.setText("");

    }
}
