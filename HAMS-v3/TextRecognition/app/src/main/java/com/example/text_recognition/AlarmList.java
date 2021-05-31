package com.example.text_recognition;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmList extends AppCompatActivity {
        ListView listView;
        ArrayList<String> arrayList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase,medicineDatabase;

        String ID[], hour[], min[], title[], mon[], tues[], wed[], thurs[], fri[], sat[], sun[], status[];

        String Idd[], hourr[], minn[], titlee[], monn[], tuess[], wedd[], thurss[], frii[], satt[], sunn[], statuss[];


        Calendar cal_alarm;

        public static int alarmlist;
        public static int alarmHandler;

        static int triggered = 0;

        int H,M;

        Timer timer = new Timer();

        DrawerLayout drawerLayout;

        int hours;
        int mins;

    @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_alarm_list);


            drawerLayout = findViewById(R.id.drawer_layout);

            alarmHandler = 1;
            Log.i("ALARM HANDLER_@@@@@@@@@", String.valueOf(alarmHandler));

            listView = findViewById(R.id.listView);
            sqLiteDatabase = this.openOrCreateDatabase("alarm", 0, null);
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS alarm (id INTEGER PRIMARY KEY AUTOINCREMENT" +
                ",hour VARCHAR , minutes VARCHAR , title VARCHAR,status VARCHAR," +
                "mon BOOLEAN,tues BOOLEAN,wed BOOLEAN,thurs BOOLEAN, fri BOOLEAN,sat BOOLEAN,sun BOOLEAN)");

            medicineDatabase = this.openOrCreateDatabase("medicine", 0, null);
            medicineDatabase.execSQL("CREATE TABLE IF NOT EXISTS medicine (id INTEGER PRIMARY KEY AUTOINCREMENT" +
                ",name VARCHAR , info VARCHAR , description VARCHAR , qrcode VARCHAR)");

            cal_alarm = Calendar.getInstance();

            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * from alarm ORDER BY id", null);

            ID = new String[cursor.getCount()];
            hour = new String[cursor.getCount()];
            min = new String[cursor.getCount()];
            title = new String[cursor.getCount()];
            status = new String[cursor.getCount()];
            mon = new String[cursor.getCount()];
            tues = new String[cursor.getCount()];
            wed = new String[cursor.getCount()];
            thurs = new String[cursor.getCount()];
            fri = new String[cursor.getCount()];
            sat = new String[cursor.getCount()];
            sun = new String[cursor.getCount()];


            Log.i("LENGTH", String.valueOf(hour.length));
            cursor.moveToFirst();
            final StringBuilder stringBuilder = new StringBuilder();
            while (!cursor.isAfterLast()) {
                stringBuilder.setLength(0);
//                Log.i("ID", String.valueOf(cursor.getString(0)));
//                Log.i("Hour", String.valueOf(cursor.getString(1)));
//                if (cursor.getString(2).length() > 1) {
//                    Log.i("Min", String.valueOf(cursor.getString(2)));
//                } else {
//                    Log.i("Min", 0 + cursor.getString(2));
//                }
//                Log.i("Title", String.valueOf(cursor.getString(3)));
//                Log.i("M", String.valueOf(cursor.getString(5)));
//                Log.i("T", String.valueOf(cursor.getString(6)));
//                Log.i("W", String.valueOf(cursor.getString(7)));
//                Log.i("TH", String.valueOf(cursor.getString(8)));
//                Log.i("F", String.valueOf(cursor.getString(9)));
//                Log.i("S", String.valueOf(cursor.getString(10)));
//                Log.i("SUN", String.valueOf(cursor.getString(11)));
//                Log.i("SWITCH", String.valueOf(cursor.getString(4)));


                String hour = cursor.getString(1);
                Log.i("SIMPLE_HOUR", hour);
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
                stringBuilder.append(cursor.getString(3));
                arrayList.add(stringBuilder.toString());

                cursor.moveToNext();
            }

            cursor.moveToFirst();
            for (int i = 0; i < hour.length; i++) {
                ID[i] = String.valueOf(cursor.getString(0));
                hour[i] = String.valueOf(cursor.getString(1));
                min[i] = String.valueOf(cursor.getString(2));
                title[i] = String.valueOf(cursor.getString(3));
                status[i] = String.valueOf(cursor.getString(4));
                mon[i] = String.valueOf(cursor.getString(5));
                tues[i] = String.valueOf(cursor.getString(6));
                wed[i] = String.valueOf(cursor.getString(7));
                thurs[i] = String.valueOf(cursor.getString(8));
                fri[i] = String.valueOf(cursor.getString(9));
                sat[i] = String.valueOf(cursor.getString(10));
                sun[i] = String.valueOf(cursor.getString(11));
                cursor.moveToNext();
            }

            ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
            listView.setAdapter(arrayAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Intent intent = new Intent(AlarmList.this,MainActivity.class);
//                    Log.i("ID", String.valueOf(id));
//                    Log.i("POSITION", String.valueOf(position));
//                    Log.i("View Id", String.valueOf(view.getId()));
//                    Log.i("HOUR-###########3",hour[position]);
//                    Log.i("MINUTE-#########3",min[position]);
//                    Log.i("TITLE-##########3",title[position]);

                    //Toast.makeText(getApplicationContext(), "Hour :" + hour[position] + " Min :" + min[position] + " FRI : " + fri[position], Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AlarmList.this, Alarm.class);
                    intent.putExtra("id", ID[position]);
                    intent.putExtra("hour", hour[position]);
                    intent.putExtra("min", min[position]);
                    intent.putExtra("title", title[position]);
                    intent.putExtra("mon", mon[position]);
                    intent.putExtra("tues", tues[position]);
                    intent.putExtra("wed", wed[position]);
                    intent.putExtra("thurs", thurs[position]);
                    intent.putExtra("fri", fri[position]);
                    intent.putExtra("sat", sat[position]);
                    intent.putExtra("sun", sun[position]);
                    intent.putExtra("status", status[position]);

                    //alarmlist = 1;
                    Alarm.alarm = 1;
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    //return false;
                    //Toast.makeText(getApplicationContext(),"Hour :"+hour[position] + " Min :"+min[position] + " Id : "+ID[position],Toast.LENGTH_LONG).show();
                    final String Hour = hour[position];
                    final String Minute = min[position];
                    final String Id = ID[position];
                    final String Title = title[position];
                    Log.i("TITLE OF MEDICINE ",Title);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(AlarmList.this);
                    builder.setMessage("Are You Sure To Delete Medicine Alarm ?");
                    builder.setTitle("Delete Alarm");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sqLiteDatabase.execSQL("DELETE FROM alarm WHERE id= '" + Id + "'");
                            Toast.makeText(getApplicationContext(), "Hour :" + Hour + " Min :" + Minute + " is Deleted", Toast.LENGTH_LONG).show();
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(AlarmList.this);
                            if (!Title.isEmpty()) {
                                builder1.setMessage("Do You Want to Delete Medicine As Well ?");
                                builder1.setTitle("Delete Medicine");
                                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        medicineDatabase = openOrCreateDatabase("medicine", 0, null);
                                        medicineDatabase.execSQL("DELETE FROM medicine Where name='" + Title.toLowerCase() + "'");
                                        Toast.makeText(getApplicationContext(), "Medicine name " + Title + " is Deleted", Toast.LENGTH_LONG).show();
                                        sqLiteDatabase.execSQL("DELETE FROM alarm where title = '" + Title + "'");
                                        finish();
                                        overridePendingTransition(0, 0);
                                        startActivity(getIntent());
                                        overridePendingTransition(0, 0);

                                    }
                                });
                                builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                        overridePendingTransition(0, 0);
                                        startActivity(getIntent());
                                        overridePendingTransition(0, 0);
                                    }
                                });
                                AlertDialog alertDialog1 = builder1.create();
                                alertDialog1.show();
                            }
                            else {
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);
                            }
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return true;
                }
            });


            Intent intent = getIntent();
            try {
                H = intent.getIntExtra("HOURS", 0);
                M = intent.getIntExtra("MINUTES", 0);
            }catch (Exception e)
            {
                Log.i("EXCEPTION E",e.getMessage());
            }
            Log.i("HOUR GET INTENT", String.valueOf(H));
            Log.i("MINUTES GET INTENT",String.valueOf(M));

//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Boolean monn = false, tuess = false, wedd = false, thurss = false, frii = false, satt = false, sunn = false;
//                        Calendar calendar = Calendar.getInstance();
//                        Log.i("INSIDE_HANDLER", "HANDLER_@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@22");
//                        Log.i("HOURS-#$#$#$#$#$#",String.valueOf(H));
//                        Log.i("MINUTES-#$#$#$#$#",String.valueOf(M));
//                        Log.i("HOUR OF DAY-(((((: ", String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
//                        Log.i("MINUTE OF DAY-)))): ", String.valueOf(calendar.get(Calendar.MINUTE)));
//                        if (H != calendar.get(Calendar.HOUR_OF_DAY) | M != calendar.get(Calendar.MINUTE)) {
//                            sqLiteDatabase = openOrCreateDatabase("alarm", 0, null);
//                            try {
//                                Cursor cursor1 = sqLiteDatabase.rawQuery("SELECT * from alarm WHERE hour='" + calendar.get(Calendar.HOUR_OF_DAY) + "'" +
//                                        "AND minutes='" + calendar.get(Calendar.MINUTE) + "'", null);
//
//
//
//                                if (cursor1.getCount() > 0) {
//                                    Log.i("HAVE SOMETHING CURSOR", "CURSOR IS GREATER THAN 0");
//                                    if (cursor1.moveToFirst()) {
//                                        cursor1.moveToFirst();
//                                        Log.i("HOUR_OF_CUROSR_%%%%%%%%", "HOUR_!!!!!!!!!!!!!!!!!!!!!!!!11111111");
//                                        while (!cursor1.isAfterLast()) {
//                                            Log.i("GET_CURSOR_HOUR_!!!!!!", cursor1.getString(1));
//                                            Log.i("GET_CURSOR-MINUTE-!!!!", cursor1.getString(2));
//                                            cal_alarm.set(Calendar.HOUR_OF_DAY, Integer.parseInt(cursor1.getString(1)));
//                                            cal_alarm.set(Calendar.MINUTE, Integer.parseInt(cursor1.getString(2)));
//                                            cal_alarm.set(Calendar.SECOND, 0);
//
//                                            monn = Boolean.parseBoolean(cursor1.getString(5));
//                                            tuess = Boolean.parseBoolean(cursor1.getString(6));
//                                            wedd = Boolean.parseBoolean(cursor1.getString(7));
//                                            thurss = Boolean.parseBoolean(cursor1.getString(8));
//                                            frii = Boolean.parseBoolean(cursor1.getString(9));
//                                            satt = Boolean.parseBoolean(cursor1.getString(10));
//                                            sunn = Boolean.parseBoolean(cursor1.getString(11));
//
//                                            cursor1.moveToNext();
//                                        }
//                                        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                                        if (satt == true) {
//                                            if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 7) {
//                                                Log.i("SAT_ALARM_LISt-222222", "TRUE");
//                                                Intent intent = new Intent(AlarmList.this, BroadcastReceiver.class);
//                                                PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmList.this, 0, intent, 0);
//
//                                                am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                                            }
//                                        }
//                                        if (frii == true) {
//                                            if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 6) {
//                                                Log.i("ALARM-TRIGGERED_2222222", "FRIDAY_222222");
//                                                Intent intent = new Intent(AlarmList.this, BroadcastReceiver.class);
//                                                //Log.i("ALARM-TRIGGERED_22222","FRIDAY_22222");
//                                                PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmList.this, 0, intent, 0);
//                                                //Log.i("ALARM-TRIGGERED","FRIDAY");
//                                                am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                                            }
//                                        }
//                                        if (thurss == true) {
//                                            if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 5) {
//                                                Log.i("THURS_ALARM_LISt-2222", "TRUE");
//                                                Intent intent = new Intent(AlarmList.this, BroadcastReceiver.class);
//                                                PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmList.this, 0, intent, 0);
//
//                                                Log.i("ALARM-GETTIME-MILLIS: ", String.valueOf(cal_alarm.getTimeInMillis()));
//                                                Date date = new Date(cal_alarm.getTimeInMillis());
//                                                Log.i("DATE_@@@@@@", String.valueOf(date));
//                                                am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                                            }
//                                        }
//                                        if (wedd == true) {
//                                            if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 4) {
//                                                Log.i("WED_222", "TRUE");
//                                                Intent intent = new Intent(AlarmList.this, BroadcastReceiver.class);
//                                                PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmList.this, 0, intent, 0);
//                                                am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                                            }
//                                        }
//                                        if (tuess == true) {
//                                            if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 3) {
//                                                Log.i("TUES_ALARM_LISt-22222", "TRUE");
//                                                Intent intent = new Intent(AlarmList.this, BroadcastReceiver.class);
//                                                PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmList.this, 0, intent, 0);
//                                                am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                                            }
//                                        }
//                                        if (monn == true) {
//                                            if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 2) {
//                                                Log.i("MON_2222", "TRUE");
//                                                Intent intent = new Intent(AlarmList.this, BroadcastReceiver.class);
//                                                PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmList.this, 0, intent, 0);
//
//                                                am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                                            }
//                                        }
//                                        if (sunn == true) {
//                                            if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 1) {
//                                                Log.i("SUN_ALARM_LISt-22222", "TRUE");
//                                                Intent intent = new Intent(AlarmList.this, BroadcastReceiver.class);
//                                                PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmList.this, 0, intent, 0);
//
//                                                am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                                            }
//                                        }
//
//                                    }
//                                    new Handler().postDelayed(this, 60000);
//                                }
//                                if (cursor1.getCount() <= 0) {
//                                    new Handler().postDelayed(this, 30000);
//                                }
//                            } catch (Exception e) {
//                                Log.i("GOT_EXCEPTION", e.getMessage());
//                            }
//
//
//                            Log.i("DAY-OF-WEEK: ", String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)));
//                            Log.i("HOUR OF DAY: ", String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
//                            Log.i("MINUTE OF DAY: ", String.valueOf(calendar.get(Calendar.MINUTE)));
//
//                            Log.i("GETTIME-MILLIS@@@@@ : ", String.valueOf(cal_alarm.getTimeInMillis()));
//                            Date date = new Date(cal_alarm.getTimeInMillis());
//                            Log.i("GETTING_DATE_#########", String.valueOf(date));
//
//                        }
//                        new Handler().postDelayed(this,20000);
//                    }
//                }, 10000);
            }


    public void ClickMenu(View view)
    {
        openDrawer(drawerLayout);
    }
    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }
    public static void closeDrawer(DrawerLayout drawerLayout)
    {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
    public void ClickAlarmList(View view)
    {
        recreate();
    }
    public void ClickAddAlarm(View view)
    {
        redirectActivity(this,Alarm.class);
    }
    public void ClickAddMedicine(View view)
    {
        redirectActivity(this,MainActivity.class);
    }

    public void ClickScanMedicine(View view)
    {
        redirectActivity(this,ScanMedicine.class);
    }
    public void ClickQRScan(View view)
    {
        redirectActivity(this,ScanQR.class);
    }

    public void ClickMap(View view)
    {
        redirectActivity(this,NearByPlaces.class);
    }

    public void ClickChat(View view)
    {
        redirectActivity(this,ChatScreen.class);
    }

    public static void redirectActivity(Activity activity, Class aclass) {
        Intent intent = new Intent(activity,aclass);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("lifecycle","onStart invoked");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Boolean monn = false, tuess = false, wedd = false, thurss = false, frii = false, satt = false, sunn = false;
//                Log.i("INSIDE_HANDLER", "HANDLER_@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@22");
//                Log.i("HOURS-#$#$#$#$#$#", String.valueOf(H));
//                Log.i("MINUTES-#$#$#$#$#", String.valueOf(M));
//                Log.i("HOUR OF DAY-(((((: ", String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
//                Log.i("MINUTE OF DAY-)))): ", String.valueOf(calendar.get(Calendar.MINUTE)));
//                if(triggered == 1)
//                {
//                    try {
//                        Thread.sleep(60000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                triggered = 0;
                Calendar calendar = Calendar.getInstance();
                if (H != calendar.get(Calendar.HOUR_OF_DAY) | M != calendar.get(Calendar.MINUTE)) {
                    sqLiteDatabase = openOrCreateDatabase("alarm", 0, null);
                    try {
                        hours = calendar.get(Calendar.HOUR_OF_DAY);
                        mins = calendar.get(Calendar.MINUTE);
                        Cursor cursor1 = sqLiteDatabase.rawQuery("SELECT * from alarm WHERE hour='" + hours + "'" +
                                "AND minutes='" + mins + "' AND status='"+true+"' ", null);


                        if (cursor1.getCount() > 0) {
                            Log.i("HAVE SOMETHING CURSOR", "CURSOR IS GREATER THAN 0");
                            if (cursor1.moveToFirst()) {
                                cursor1.moveToFirst();
                                Log.i("HOUR_OF_CUROSR_%%%%%%%%", "HOUR_!!!!!!!!!!!!!!!!!!!!!!!!11111111");
                                while (!cursor1.isAfterLast()) {
                                    Log.i("GET_CURSOR_HOUR_!!!!!!", cursor1.getString(1));
                                    Log.i("GET_CURSOR-MINUTE-!!!!", cursor1.getString(2));
                                    Log.i("GET_CURSOR-TITLE-!!!!", cursor1.getString(3));
                                    Log.i("GET_CURSOR-STATUS-!!!!", cursor1.getString(4));
                                    cal_alarm.set(Calendar.HOUR_OF_DAY, Integer.parseInt(cursor1.getString(1)));
                                    cal_alarm.set(Calendar.MINUTE, Integer.parseInt(cursor1.getString(2)));
                                    cal_alarm.set(Calendar.SECOND, 0);

                                    monn = Boolean.parseBoolean(cursor1.getString(5));
                                    tuess = Boolean.parseBoolean(cursor1.getString(6));
                                    wedd = Boolean.parseBoolean(cursor1.getString(7));
                                    thurss = Boolean.parseBoolean(cursor1.getString(8));
                                    frii = Boolean.parseBoolean(cursor1.getString(9));
                                    satt = Boolean.parseBoolean(cursor1.getString(10));
                                    sunn = Boolean.parseBoolean(cursor1.getString(11));

                                    cursor1.moveToNext();
                                }
//                                if(triggered == 1)
//                                {
//                                    Thread.sleep(60000);
//                                    triggered = 0;
//                                }
                                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                if (satt == true) {
                                    if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 7) {
                                        Log.i("SAT_ALARM_LISt-222222", "TRUE");
//                                        if(triggered != 1) {
                                            Intent intent = new Intent(AlarmList.this, BroadcastReceiver.class);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmList.this, 0, intent, 0);
                                            am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                                        }
                                    }
                                }
                                if (frii == true) {
                                    if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 6) {
                                        Log.i("ALARM-TRIGGERED_2222222", "FRIDAY_222222");
//                                        if(triggered != 1) {
                                            Intent intent = new Intent(AlarmList.this, BroadcastReceiver.class);
                                            //Log.i("ALARM-TRIGGERED_22222","FRIDAY_22222");
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmList.this, 0, intent, 0);
                                            //Log.i("ALARM-TRIGGERED","FRIDAY");
                                            am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
                                        //}
                                    }
                                }
                                if (thurss == true) {
                                    if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 5) {
                                        Log.i("THURS_ALARM_LISt-2222", "TRUE");
//                                        if(triggered != 1) {
                                            Intent intent = new Intent(AlarmList.this, BroadcastReceiver.class);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmList.this, 0, intent, 0);

                                            Log.i("ALARM-GETTIME-MILLIS: ", String.valueOf(cal_alarm.getTimeInMillis()));
                                            Date date = new Date(cal_alarm.getTimeInMillis());
                                            Log.i("DATE_@@@@@@", String.valueOf(date));
                                            am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
                                        //}
                                    }
                                }
                                if (wedd == true) {
                                    if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 4) {
                                        Log.i("WED_222", "TRUE");
//                                        if(triggered != 1) {
                                            Intent intent = new Intent(AlarmList.this, BroadcastReceiver.class);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmList.this, 0, intent, 0);
                                            am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
                                       // }
                                    }
                                }
                                if (tuess == true) {
                                    if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 3) {
                                        Log.i("TUES_ALARM_LISt-22222", "TRUE");
//                                        if(triggered != 1) {
                                            Intent intent = new Intent(AlarmList.this, BroadcastReceiver.class);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmList.this, 0, intent, 0);
                                            am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
                                        //}
                                    }
                                }
                                if (monn == true) {
                                    if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 2) {
                                        Log.i("MON_2222", "TRUE");
//                                        if(triggered != 1) {
                                            Intent intent = new Intent(AlarmList.this, BroadcastReceiver.class);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmList.this, 0, intent, 0);

                                            am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
                                        //}
                                    }
                                }
                                if (sunn == true) {
                                    if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 1) {
                                        Log.i("SUN_ALARM_LISt-22222", "TRUE");
//                                        if(triggered != 1) {
                                            Intent intent = new Intent(AlarmList.this, BroadcastReceiver.class);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmList.this, 0, intent, 0);

                                            am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
                                        //}
                                    }
                                }
                            }
                            Log.i("THREAD","THREAD SLEEPING FOR 1 MINUTE");
                            Thread.sleep(60000);
                        }
                    } catch (Exception e) {
                        Log.i("GOT_EXCEPTION", e.getMessage());
                    }


//                    Log.i("DAY-OF-WEEK: ", String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)));
//                    Log.i("HOUR OF DAY: ", String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
//                    Log.i("MINUTE OF DAY: ", String.valueOf(calendar.get(Calendar.MINUTE)));
//
//                    Log.i("GETTIME-MILLIS@@@@@ : ", String.valueOf(cal_alarm.getTimeInMillis()));
//                    Date date = new Date(cal_alarm.getTimeInMillis());
//                    Log.i("GETTING_DATE_#########", String.valueOf(date));
                   // SystemClock.sleep(60000);
                }
            }
        },10000,10000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
        Log.d("lifecycle","onPause invoked");
    }
        public void AddAlarm(View view){
            Intent intent = new Intent(AlarmList.this,Alarm.class);

            intent.putExtra("name", "");
            intent.putExtra("description", "");

            startActivity(intent);

        }
}