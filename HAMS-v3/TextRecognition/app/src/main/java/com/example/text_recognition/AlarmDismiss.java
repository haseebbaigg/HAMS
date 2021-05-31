package com.example.text_recognition;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class AlarmDismiss extends AppCompatActivity {

    Button b1;
    TextView textView1,textView2;

    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    Intent intent;
    int Hours ,Minutes;
    StringBuilder stringBuilder = new StringBuilder();
    StringBuilder med_name = new StringBuilder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_dismiss);

        b1 = findViewById(R.id.button2);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);


        intent = getIntent();
        Hours = intent.getIntExtra("hours",0);
        Minutes = intent.getIntExtra("minutes",0);

        Log.i("Hours_Dismiss_!!!", String.valueOf(Hours));
        Log.i("Minutes_Dismiss_!!!",String.valueOf(Minutes));

        sqLiteDatabase = this.openOrCreateDatabase("alarm",0,null);

        cursor = sqLiteDatabase.rawQuery("SELECT * FROM alarm WHERE hour='"+Hours+"' AND minutes='"+Minutes+"' ",null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            String hour = cursor.getString(1);
            int Converted_Hour = 0;
            if (Integer.parseInt(hour) == 12) {
                Log.i("HOUR == 12 (2222)", String.valueOf(cursor.getString(1)));
                stringBuilder.append(cursor.getString(1));
            }
            if (Integer.parseInt(hour) > 12) {
                Converted_Hour = Integer.parseInt(hour) - 12;
                Log.i("HOUR>12 (2222)", String.valueOf(Converted_Hour));
                stringBuilder.append(Converted_Hour);
            } else if (Integer.parseInt(hour) < 12) {
                if (Integer.parseInt(hour) == 0) {
                    Converted_Hour = Integer.parseInt(hour) + 12;
                    Log.i("HOUR==0 (2222)", String.valueOf(Converted_Hour));
                    stringBuilder.append(Converted_Hour);
                } else {
                    Log.i("HOUR<12 (2222)", String.valueOf(cursor.getString(1)));
                    stringBuilder.append(cursor.getString(1));
                }
            }
            //stringBuilder.append(cursor.getString(1));
            stringBuilder.append(" : ");
            Log.i("minutes-length (2222)", String.valueOf(cursor.getString(2).length()));
            if (cursor.getString(2).length() > 1) {
                Log.i("MINUTES>1 (2222)", String.valueOf(cursor.getString(2)));
                stringBuilder.append(cursor.getString(2));
            } else {
                String min = cursor.getString(2);
                String add_min = "0" + min;
                Log.i("MINUTES<1 (2222)", String.valueOf(add_min));
                stringBuilder.append(add_min);
            }
            stringBuilder.append(" ");
            if (Integer.parseInt(hour) < 12) {
                stringBuilder.append("AM");
            } else if (Integer.parseInt(hour) >= 12) {
                stringBuilder.append("PM");
            }
            stringBuilder.append("\n");
            med_name.append(cursor.getString(3));
            cursor.moveToNext();
        }
        if(Hours <12) {
            textView1.setText(String.valueOf(Hours) + ':' + String.valueOf(Minutes) + " Am");
        }
        else if(Hours == 0)
        {
            textView1.setText(String.valueOf(Hours) + ':' + String.valueOf(Minutes) + " Pm");
        }
        else if(Hours >12)
        {
            textView1.setText(String.valueOf(Hours) + ':' + String.valueOf(Minutes) + " Pm");
        }
        else if(Hours == 24)
        {
            textView1.setText(String.valueOf(Hours) + ':' + String.valueOf(Minutes) + " Am");
        }

        Log.i("MED_NAME:::::::::::::::",med_name.toString());
        textView2.setText(med_name.toString());

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AlarmManager dismiss = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                Intent intent = new Intent(Alarm.context, BroadcastReceiver.class);
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(Alarm.context, 1, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
//                dismiss.cancel(pendingIntent);

                BroadcastReceiver.ringtone.stop();
                MainActivity.gotAlarm = 1;

                //finish();
                Intent intent1 = new Intent(AlarmDismiss.this,AlarmList.class);
                intent1.putExtra("HOURS",Hours);
                intent1.putExtra("MINUTES",Minutes);
                startActivity(intent1);
            }
        });

    }
}