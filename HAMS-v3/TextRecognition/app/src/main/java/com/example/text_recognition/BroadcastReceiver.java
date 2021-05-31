package com.example.text_recognition;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.Calendar;
import java.util.Date;

public class BroadcastReceiver extends android.content.BroadcastReceiver {
    Calendar calendar;
    public static Ringtone ringtone;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("BROADCAST","BROADCAST RECEIVER !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        vibrator.vibrate(2000);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//
        ringtone = RingtoneManager.getRingtone(context,uri);
        ringtone.play();

        AlarmList.triggered = 1;

        calendar = Calendar.getInstance();

        //Log.i("DATE_!!!!!!!!!!!!",new Date().toString());
        calendar.setTime(new Date());
        //Log.i("CALENDAR",calendar.toString());
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        Intent intent1 = new Intent(context,AlarmDismiss.class);
        intent1.putExtra("hours",hours);
        intent1.putExtra("minutes",minutes);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
    }
}
