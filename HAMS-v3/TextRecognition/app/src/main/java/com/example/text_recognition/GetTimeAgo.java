package com.example.text_recognition;

import android.app.Application;
import android.content.Context;

public class GetTimeAgo extends Application {

    public static final int SEC_MILLIS = 1000;
    public static final int MIN_MILLIS = 60 * SEC_MILLIS;
    public static final int HOUR_MILLIS = 60 * MIN_MILLIS;
    public static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String GetTimeAgo(long time, Context context)
    {
        if(time < 1000000000000L)
        {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if(time>now || time <= 0)
        {
            return null;
        }
        final long diff = now-time;
        if(diff < MIN_MILLIS)
        {
            return "just now";
        }
        else if(diff < 2 * MIN_MILLIS)
        {
            return "a minute ago";
        }
        else if(diff < 50* MIN_MILLIS)
        {
            return diff / MIN_MILLIS + " minute ago";
        }
        else if(diff < 90*MIN_MILLIS)
        {
            return "an hour ago";
        }
        else if(diff < 24 * HOUR_MILLIS)
        {
            return diff / HOUR_MILLIS + " hour ago";
        }
        else if(diff < 48 * HOUR_MILLIS)
        {
            return "yesterday";
        }
        else
        {
            return diff / DAY_MILLIS + "days ago";
        }

    }

}
