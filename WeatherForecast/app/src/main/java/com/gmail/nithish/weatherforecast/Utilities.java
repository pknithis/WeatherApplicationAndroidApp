package com.gmail.nithish.weatherforecast;

import android.text.Html;
import android.view.View;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by nithishkp on 12/6/2015.
 */
public class Utilities {
    private static String timeZone;
    private static String tempType;
    private static Calendar calendar;
    private static int defaultButtonColor;

    public static void setDefaultButtonColor(int c)
    {
        defaultButtonColor = c;
    }
    public static int getDefaultButtonColor()
    {
        return defaultButtonColor;
    }
    public static void setTimeZone(String val)
    {
        timeZone = val;
    }
    public static void setTmperatureType(String temp)
    {
        tempType=temp;
    }
    public static String getTimeZone()
    {
        return timeZone;
    }

    public static void prepareCalendar(String time)
    {
        Date date = new Date(Long.parseLong(time)*1000);
        calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(Utilities.getTimeZone()));
        calendar.setTime(date);
    }
    public static String getMonth()
    {
        switch (calendar.get(Calendar.MONTH))
        {
            case Calendar.JANUARY:
                return "Jan";
            case Calendar.FEBRUARY:
                return "Feb";
            case Calendar.MARCH:
                return "Mar";
            case Calendar.APRIL:
                return "Apr";
            case Calendar.MAY:
                return "May";
            case Calendar.JUNE:
                return "Jun";
            case Calendar.JULY:
                return "Jul";
            case Calendar.AUGUST:
                return "Augt";
            case Calendar.SEPTEMBER:
                return "Sep";
            case Calendar.OCTOBER:
                return "Oct";
            case Calendar.NOVEMBER:
                return "Nov";
            case Calendar.DECEMBER:
                return "Dec";
            default:return "Jan";
        }
    }

    public static String getDayOfMonth()
    {
        return calendar.get(Calendar.DAY_OF_MONTH)+"";
    }

    public static String getDayOfWeek()
    {
        switch (calendar.get(Calendar.DAY_OF_WEEK))
        {
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            case Calendar.SUNDAY:
                return "Sunday";
            default:return "Sunday";
        }
    }
    public static String getFormattedTime(long unixTime)
    {
        Date date = new Date(unixTime*1000);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        return sdf.format(date);
    }
    public static  String getUnit(String type)
    {
        if(tempType.equals("Fahrenheit"))
        {
            switch(type)
            {
                case "ws":return "mph";
                case "tp":
                case "dp" : return Html.fromHtml("&#8457")+"";
                case "vs": return "mi";
                case "ps": return "mb";
            }
        }
        else
        {
            switch(type)
            {
                case "ws":return "m/s";
                case "tp":
                case "dp" : return Html.fromHtml("&#8451")+"";
                case "vs": return "km";
                case "ps": return "hPa";
            }
        }
        return "";
    }
    public static int getImageId(String iconVal)
    {
        switch (iconVal)
        {
            case "clear-day": return R.drawable.clear;

            case "clear-night": return  R.drawable.clear_night;

            case "rain": return R.drawable.rain;

            case "snow": return R.drawable.snow;

            case "sleet": return R.drawable.sleet;

            case "wind": return R.drawable.wind;

            case "fog": return R.drawable.fog;

            case "cloudy": return R.drawable.cloudy;

            case "partly-cloudy-day": return R.drawable.cloud_day;

            case "partly-cloudy-night": return R.drawable.cloud_night;

        }
        return R.drawable.clear;
    }

    public static  String imageURI(String iconValue)
    {
        String icon_image="";
        switch(iconValue)
        {
            case "clear-day":icon_image ="clear.png";break;
            case "clear-night":icon_image ="clear_night.png";break;
            case "rain":icon_image ="rain.png";break;
            case "snow":icon_image ="snow.png";break;
            case "sleet":icon_image="sleet.png";break;
            case "wind":icon_image ="wind.png";break;
            case "fog":icon_image ="fog.png";break;
            case "cloudy":icon_image ="cloudy.png";break;
            case "partly-cloudy-day":icon_image ="cloud_day.png";break;
            case "partly-cloudy-night":icon_image ="cloud_night.png";break;
            default : break;
        }
        return "http://cs-server.usc.edu:45678/hw/hw8/images/"+icon_image;
    }


    public static String getStateAb(String stateName)
    {
        switch(stateName)
        {
            case "Alabama" : return "AL";
            case "Alaska": return"AK";
            case "Arizona": return"AZ";
            case "Arkansas": return "AR";
            case "California": return "CA";
            case "Colorado": return "CO";
            case "Connecticut": return "CT";
            case "Delaware": return "DE";
            case "District Of Columbia": return "DC";
            case "Florida": return "FL";
            case "Georgia": return "GA";
            case "Hawaii": return "HI";
            case "Idaho": return "ID";
            case "Illinois": return "IL";
            case "Indiana": return "IN";
            case "Iowa": return "IA";
            case "Kansas": return "KS";
            case "Kentucky": return "KY";
            case "Louisiana": return "LA";
            case "Maine": return "ME";
            case "Maryland": return "MD";
            case "Massachusetts": return "MA";
            case "Michigan": return "MI";
            case "Minnesota": return "MN";
            case "Mississippi": return "MS";
            case "Missouri": return "MO";
            case "Montana": return "MT";
            case "Nebraska": return "NE";
            case "Nevada": return "NV";
            case "New Hampshire": return "NH";
            case "New Jersey": return "NJ";
            case "New Mexico": return "NM";
            case "New York": return "NY";
            case "North Carolina": return "NC";
            case "North Dakota": return "ND";
            case "Ohio": return "OH";
            case "Oklahoma": return "OK";
            case "Oregon": return "OR";
            case "Pennsylvania": return "PA";
            case "Rhode Island": return "RI";
            case "South Carolina": return "SC";
            case "South Dakota": return "TN";
            case "Texas": return "TX";
            case "Utah": return "UT";
            case "Vermont": return "VT";
            case "Virginia": return "VA";
            case "Washington": return "WA";
            case "West Virginia": return "WV";
            case "Wisconsin": return "WI";
            case "Wyoming</opti": return "WY";
            default : return "";
        }
    }
}
