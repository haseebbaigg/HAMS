package com.example.text_recognition;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Alarm extends AppCompatActivity {

    TimePicker tp;

    int edit_minutes;
    int edit_hours;
    Date date;

    Calendar cal_alarm;
    TextView textView;
    Switch aSwitch;

    SQLiteDatabase sqLiteDatabase;
    SQLiteStatement sqLiteStatement;

    public static Context context;

    String alertPermission[];
    String foreGroundPermission[];
    CheckBox mon, tues, wed, thurs, fri, sat, sun;
    boolean mond =false;
    boolean tuesd = false;
    boolean wednesd = false;
    boolean thursd = false;
    boolean frid = false;
    boolean saturd = false;
    boolean sund = false;
    boolean value;


    Intent intent;
    String Title = null;
    String Image= null;
    String Description = null;
    String QR = null;
    String SB= null;

    int size = 0;
    ArrayList Buttonlist = new ArrayList();

    String HOUR = null;
    String MINUTE = null;
    String STATUS = null;
    String MON = null;
    String TUES = null;
    String WED =null;
    String THURS= null;
    String FRI =null;
    String SAT = null;
    String SUN = null;
    String ID = null;

    DrawerLayout drawerLayout;

    static int alarm = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        //sqLiteDatabase.delete("Alarm","1",null);

        drawerLayout = findViewById(R.id.drawer_layout);

        //MainActivity.timer.cancel();

        tp = findViewById(R.id.timePicker);
        mon = findViewById(R.id.mon);
        tues = findViewById(R.id.tues);
        wed = findViewById(R.id.wed);
        thurs = findViewById(R.id.thurs);
        fri = findViewById(R.id.fri);
        sat = findViewById(R.id.sat);
        sun = findViewById(R.id.sun);

        date = new Date();
        context = Alarm.this;

        aSwitch = findViewById(R.id.switch1);
        textView = findViewById(R.id.alarmStatus);

        textView.setText("");

        intent = getIntent();

        cal_alarm = Calendar.getInstance();

        HOUR = intent.getStringExtra("hour");
        MINUTE = intent.getStringExtra("min");
        STATUS = intent.getStringExtra("status");
        MON = intent.getStringExtra("mon");
        TUES = intent.getStringExtra("tues");
        WED = intent.getStringExtra("wed");
        THURS = intent.getStringExtra("thurs");
        FRI = intent.getStringExtra("fri");
        SAT = intent.getStringExtra("sat");
        SUN = intent.getStringExtra("sun");
        ID = intent.getStringExtra("id");
        //Log.i("STATUS",STATUS);

        try{
            Title = intent.getStringExtra("name");
        }
        catch (Exception e)
        {
            Log.i("TITLE EXCEPTION",e.getMessage());
        }

        try {
            size = intent.getIntExtra("ButtonSize", 0);
            Buttonlist = intent.getStringArrayListExtra("ButtonList");
            if (Title == null) {
                Log.i("Title_!!!!!!!!!!!!!1", Title.toString());
            }
            Image = intent.getStringExtra("imageUri");
            if (Image == null) {
                Log.i("IMG_!!!!!!!!!!!!!1", Image.toString());
            }
            Description = intent.getStringExtra("description");
            if (Description == null) {
                Log.i("DESC_!!!!!!!!!!!!!1", Description.toString());
            }
            QR = intent.getStringExtra("qrcode");
            SB = intent.getStringExtra("stringB");
            if(SB == null) {
                Log.i("SB_!!!!!!!!!!!!!1", SB.toString());
            }

        }
        catch (Exception e)
        {
            Log.i("GOT_EXCEPTION",e.getMessage());
        }
        try {
            Description = intent.getStringExtra("description");
            if (Description == null) {
                Log.i("DESC_!!!!!!!!!!!!!1", Description.toString());
            }
            QR = intent.getStringExtra("qrcode");
            if (QR == null) {
                Log.i("QR_!!!!!!!!!!!!!1", QR.toString());
            }
        }
        catch (Exception e)
        {
            Log.i("EXCEPTION DESC AND QR",e.getMessage());
        }
        //Log.i("Title_!!!!!!!!!!!!!1",Title.toString());

        //Log.i("STATUS-BOOL", String.valueOf(Boolean.parseBoolean(STATUS)));

        if (STATUS != null) {
            //Log.i("STATUS", STATUS);
            aSwitch.setChecked(Boolean.parseBoolean(STATUS));
        }
        value = aSwitch.isChecked();
        if (value == false) {
            textView.setText("Alarm is Off");
        } else {
            textView.setText("Alarm is Onn");
        }

        if (HOUR != null) {
            tp.setHour(Integer.parseInt(HOUR));
        }
        if (MINUTE != null) {
            tp.setMinute(Integer.parseInt(MINUTE));
        }

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    value = true;
                    textView.setText("Alarm is Onn");
                } else {
                    value = false;
                    textView.setText("Alarm is Off");
                }
            }
        });


        mon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    mond = true;
                }
                else {
                    mond = false;
                }
            }
        });

        tues.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    tuesd = true;
                }
                else {
                    tuesd = false;
                }
            }
        });

        wed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    wednesd = true;
                }
                else {
                    wednesd = false;
                }
            }
        });

        thurs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    thursd = true;
                }
                else {
                    thursd = false;
                }
            }
        });

        fri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    frid = true;
                }
                else {
                    frid = false;
                }
            }
        });

        sat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    saturd = true;
                }
                else {
                    saturd = false;
                }
            }
        });

        sun.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    sund = true;
                }
                else {
                    sund = false;
                }
            }
        });

        sqLiteDatabase = this.openOrCreateDatabase("alarm", 0, null);
        //sqLiteDatabase.execSQL("DROP TABLE alarm");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS alarm (id INTEGER PRIMARY KEY AUTOINCREMENT" +
                ",hour VARCHAR , minutes VARCHAR , title VARCHAR,status VARCHAR," +
                "mon BOOLEAN,tues BOOLEAN,wed BOOLEAN,thurs BOOLEAN, fri BOOLEAN,sat BOOLEAN,sun BOOLEAN)");


        alertPermission = new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW};
        foreGroundPermission = new String[]{Manifest.permission.FOREGROUND_SERVICE};


        tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                edit_hours = hourOfDay;
                edit_minutes = minute;
                cal_alarm.set(Calendar.HOUR_OF_DAY, edit_hours);
                cal_alarm.set(Calendar.MINUTE, edit_minutes);
                cal_alarm.set(Calendar.SECOND, 0);


                Log.i("EDIT_HOUR", String.valueOf(hourOfDay));
                Log.i("EDIT_MINUTE", String.valueOf(minute));
            }
        });
        if(HOUR != null & MINUTE != null ) {
            edit_hours = Integer.parseInt(HOUR);
            edit_minutes = Integer.parseInt(MINUTE);
        }
        Log.i("EDIT_HOUR", String.valueOf(edit_hours));
        Log.i("EDIT_MINUTE", String.valueOf(edit_minutes));
        //sqLiteDatabase.delete("alarm","1",null);
        Log.i("VALUE", String.valueOf(value));
//        Log.i("STATUS", String.valueOf(STATUS));
        //Log.i("TUES_OUTSIDE", TUES);

        if (MON != null) {
            mon.setChecked(Boolean.parseBoolean(MON));
        }
        if (TUES != null) {
            Log.i("TUES", TUES);
            tues.setChecked(Boolean.parseBoolean(TUES));
        }
        if (WED != null) {
            wed.setChecked(Boolean.parseBoolean(WED));
        }
        if (THURS != null) {
            thurs.setChecked(Boolean.parseBoolean(THURS));
        }
        if (FRI != null) {
            fri.setChecked(Boolean.parseBoolean(FRI));
        }
        if (SAT != null) {
            sat.setChecked(Boolean.parseBoolean(SAT));
        }
        if (SUN != null) {
            sun.setChecked(Boolean.parseBoolean(SUN));
        }
    }

    @SuppressLint("LongLogTag")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setTimer(View view) {

        //sqLiteDatabase.delete("alarm","1",null);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (!Settings.canDrawOverlays(this)) {
            Intent intent2 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            //startActivityForResult(intent, 0);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent2);
        }

        //Calendar cal_now = Calendar.getInstance();
        //Calendar testing = Calendar.getInstance();

        //testing.setTime(date);

        //cal_now.setTime(date);


        Log.i("DATE", date.toString());


//        cal_alarm.set(Calendar.HOUR_OF_DAY,edit_hours);
//        cal_alarm.set(Calendar.MINUTE,edit_minutes);
//        cal_alarm.set(Calendar.SECOND,0);

        Log.i("CALENDAR_HOUR", String.valueOf(Calendar.HOUR_OF_DAY));
        Log.i("CALENDAR_MIN", String.valueOf(Calendar.MINUTE));
        Log.i("CALENDAR_SEC", String.valueOf(Calendar.SECOND));

        if (ID != null) {
            //Log.i("ID_!!!!!!!!!!!!!!!!!", ID);
            //Log.i("MON_CHECK", String.valueOf(mon.isChecked()));
//                String update = "UPDATE alarm SET hour=?,minutes=?,status=?,mon=?,tues=?,wed=?,thurs=?,fri=?,sat=?,sun=?" +
//                        " WHERE id=ID IN (?,?,?,?,?,?,?,?,?,?)";
//                sqLiteStatement = sqLiteDatabase.compileStatement(update);
//                sqLiteStatement.bindString(1, String.valueOf(edit_hours).toLowerCase());
//                sqLiteStatement.bindString(2, String.valueOf(edit_minutes).toLowerCase());
//                sqLiteStatement.bindString(4, String.valueOf(value));
            sqLiteDatabase.execSQL("UPDATE alarm SET hour= '" + edit_hours + "',minutes = '" + edit_minutes + "'" +
                    ", status='" + value + "', mon='" + mon.isChecked() + "', tues='" + tues.isChecked() + "'" +
                    ", wed='" + wed.isChecked() + "', thurs='" + thurs.isChecked() + "', fri='" + fri.isChecked() + "'" +
                    ",sat='" + sat.isChecked() + "', sun='" + sun.isChecked() + "' WHERE id='" + ID + "'");
        } else {
            String insert = "INSERT INTO alarm (hour,minutes,title,status,mon,tues,wed,thurs,fri,sat,sun) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
            sqLiteStatement = sqLiteDatabase.compileStatement(insert);
            sqLiteStatement.bindString(1, String.valueOf(edit_hours).toLowerCase());
            sqLiteStatement.bindString(2, String.valueOf(edit_minutes).toLowerCase());
            sqLiteStatement.bindString(3, Title.toLowerCase());
            sqLiteStatement.bindString(4, String.valueOf(value));
        }
        Log.i("VALUE OF STATUS",String.valueOf(value));
//        if (value == true) {
            if (sun.isChecked()) {
                if (ID == null) {
                        sqLiteStatement.bindString(11, String.valueOf(sund));
                }
//                if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 1) {
//                    Intent intent = new Intent(Alarm.this, BroadcastReceiver.class);
//                    PendingIntent pendingIntent = PendingIntent.getBroadcast(Alarm.this, 0, intent, 0);
//
//                    am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                }
            }
            if (sat.isChecked()) {
                //Log.i("SAT", String.valueOf(sat.isChecked()));
                if (ID == null) {
                        sqLiteStatement.bindString(10, String.valueOf(saturd));
                }
//                if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 7) {
//                    Intent intent = new Intent(Alarm.this, BroadcastReceiver.class);
//                    PendingIntent pendingIntent = PendingIntent.getBroadcast(Alarm.this, 0, intent, 0);
//
//                    am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                }
            }
            if (fri.isChecked()) {
                if (ID == null) {
                        sqLiteStatement.bindString(9, String.valueOf(frid));
                }
//                Log.i("DAY_OF_WEEK", String.valueOf(cal_alarm.get(Calendar.DAY_OF_WEEK)));
//                if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 6) {
//                    //Log.i("ALARM-TRIGGERED_1111111","FRIDAY_1111111");
//                    Intent intent = new Intent(Alarm.this, BroadcastReceiver.class);
//                    //Log.i("ALARM-TRIGGERED_22222","FRIDAY_22222");
//                    PendingIntent pendingIntent = PendingIntent.getBroadcast(Alarm.this, 0, intent, 0);
//                    //Log.i("ALARM-TRIGGERED","FRIDAY");
//                    am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                    Log.i("ALARM-TRIGGERED_!!!!!!","FRIDAY_!!!!!!!!");
//                }
            }
            if (thurs.isChecked()) {
                if (ID == null) {
                        sqLiteStatement.bindString(8, String.valueOf(thursd));
                }
//                if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 5) {
//                    Intent intent = new Intent(Alarm.this, BroadcastReceiver.class);
//                    PendingIntent pendingIntent = PendingIntent.getBroadcast(Alarm.this, 0, intent, 0);
//
//                    am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                }
            }
            if (wed.isChecked()) {
                if (ID == null) {
                        sqLiteStatement.bindString(7, String.valueOf(wednesd));
                }
//                Log.i("WEDDDDDDDDDDDDD", "TRUE");
//                if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 4) {
//                    Log.i("WED_1111111111", "TRUE");
//                    Intent intent2 = new Intent(Alarm.this,BroadcastReceiver.class);
//                    PendingIntent pendingIntent = PendingIntent.getBroadcast(Alarm.this, 0, intent, 0);
//
//                    am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                }
            }
            if (tues.isChecked()) {
                if (ID == null) {
                        sqLiteStatement.bindString(6, String.valueOf(tuesd));
                }
//                //Log.i("TUES", "TRUE");
//                if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 3) {
//
//                    Intent intent = new Intent(Alarm.this, BroadcastReceiver.class);
//                    PendingIntent pendingIntent = PendingIntent.getBroadcast(Alarm.this, 0, intent, 0);
//                    am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                }
            }
            if (mon.isChecked()) {
                if (ID == null) {
                        sqLiteStatement.bindString(5, String.valueOf(mond));
                }
//                Log.i("MONDAY", "TRUE");
//                if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 2) {
//                    Log.i("MON_1", "TRUE");
//                    Intent intent = new Intent(Alarm.this, BroadcastReceiver.class);
//                    PendingIntent pendingIntent = PendingIntent.getBroadcast(Alarm.this, 0, intent, 0);
//
//                    am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                }
            }
             else {
//                 Log.i("ELSE BROADCAST","BROADCAST_######################################");
//                Intent intent = new Intent(Alarm.this, BroadcastReceiver.class);
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(Alarm.this, 0, intent, 0);
//                am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
            }
            Log.i("EDIT_HOUR_!!!!!!!!!!!!!", String.valueOf(edit_hours));
            Log.i("EDIT_MINUTE_!!!!!!!!!!!", String.valueOf(edit_minutes));
            Log.i("MEDICINE_NAME_!!!!!!!!!", String.valueOf(Title));
            Log.i("WEEK_MONDAY_!!!!!!!!!!!", String.valueOf(mond));
            Log.i("WEEK_TUESDAY_!!!!!!!!!!", String.valueOf(tuesd));
            Log.i("WEEK_WEDNESDAY_!!!!!!!!", String.valueOf(wednesd));
            Log.i("WEEK_THURSDAY_!!!!!!!!!", String.valueOf(thursd));
            Log.i("WEEK_FRIDAY_!!!!!!!!!!!", String.valueOf(frid));
            Log.i("WEEK_SATURDAY_!!!!!!!!!", String.valueOf(saturd));
            Log.i("WEEK_SUNDAY_!!!!!!!!!!!", String.valueOf(sund));


            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * from  alarm WHERE hour = '"+edit_hours+"' AND minutes = '"+edit_minutes+"'" +
                    "AND title='"+Title+"' ",null);

            if (cursor.moveToFirst()) {
                Toast.makeText(this, "Record Already Exists", Toast.LENGTH_SHORT).show();
            }
            else {
                if (ID == null) {
                    sqLiteStatement.execute();
                }
            }

            sqLiteDatabase = openOrCreateDatabase("alarm", 0, null);
            Cursor cursor1 = sqLiteDatabase.rawQuery("SELECT * from alarm", null);
            cursor1.moveToFirst();
            ArrayList<String> arrayList = new ArrayList<>();
            StringBuilder stringBuilder = new StringBuilder();
            while (!cursor1.isAfterLast()) {
                arrayList.add(cursor1.getString(1));
                arrayList.add(cursor1.getString(2));
                arrayList.add(cursor1.getString(3));
                arrayList.add(cursor1.getString(4));
                arrayList.add(cursor1.getString(5));
                arrayList.add(cursor1.getString(6));
                arrayList.add(cursor1.getString(7));
                arrayList.add(cursor1.getString(8));
                arrayList.add(cursor1.getString(9));
                arrayList.add(cursor1.getString(10));
                arrayList.add(cursor1.getString(11));


                cursor1.moveToNext();


            }
            stringBuilder.append(arrayList);
            stringBuilder.append("\n :");
            Log.i("STRING_BUILDER_ALARM",stringBuilder.toString());
            Log.i("ALARM_@@@@", String.valueOf(alarm));

            if(alarm == 1){
                Alarm.alarm = 0;
                Log.i("INSIDE INTENT","INTENT!!!!!!!!!!!!!!!!!!!!!");
                Intent intent = new Intent(Alarm.this,AlarmList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            else if(ScanMedicine.scan_activity == 1){
                ScanMedicine.scan_activity = 1;
                Intent intent = new Intent(Alarm.this,ScanMedicine.class);

                intent.putExtra("PassName", Title);
                intent.putExtra("PassImg", Image);
                intent.putExtra("PassDesc", Description);

                intent.putExtra("Alarm_Hour", edit_hours);
                intent.putExtra("Alarm_Min", edit_minutes);
                intent.putExtra("Alarm_Status", String.valueOf(value));

                intent.putExtra("MONDAY_CHECK", mond);
                intent.putExtra("TUESDAY_CHECK", tuesd);
                intent.putExtra("WEDNESDAY_CHECK", wednesd);
                intent.putExtra("THURSDAY_CHECK", thursd);
                intent.putExtra("FRIDAY_CHECK", frid);
                intent.putExtra("SATURDAY_CHECK", saturd);
                intent.putExtra("SUNDAY_CHECK", sund);

                intent.putExtra("ButtonListSize", size);
                intent.putStringArrayListExtra("ButtonList", Buttonlist);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else if(ScanQR.qr_activity == 1)
            {
                ScanQR.qr_activity = 0;
                Intent intent = new Intent(Alarm.this,ScanQR.class);

                Log.i("PASSING NAME" , Title);
                intent.putExtra("PassName", Title);
                intent.putExtra("PassDesc", Description);

                intent.putExtra("Alarm_Hour", edit_hours);
                intent.putExtra("Alarm_Min", edit_minutes);
                intent.putExtra("Alarm_Status", String.valueOf(value));

                intent.putExtra("MONDAY_CHECK", mond);
                intent.putExtra("TUESDAY_CHECK", tuesd);
                intent.putExtra("WEDNESDAY_CHECK", wednesd);
                intent.putExtra("THURSDAY_CHECK", thursd);
                intent.putExtra("FRIDAY_CHECK", frid);
                intent.putExtra("SATURDAY_CHECK", saturd);
                intent.putExtra("SUNDAY_CHECK", sund);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else if(MainActivity.add_med == 1){
                MainActivity.add_med = 0;
                Log.i("INSIDE INTENT","INTENT!!!!!!!!!!!!!!!!!!!!!");
                Intent intent = new Intent(Alarm.this, MainActivity.class);

                intent.putExtra("PassName", Title);
                intent.putExtra("PassImg", Image);
                intent.putExtra("PassDesc", Description);
                intent.putExtra("PassQR",QR);
                intent.putExtra("PassSB",SB);

                intent.putExtra("Alarm_Hour", edit_hours);
                intent.putExtra("Alarm_Min", edit_minutes);
                intent.putExtra("Alarm_Status", String.valueOf(value));

                intent.putExtra("MONDAY_CHECK", mond);
                intent.putExtra("TUESDAY_CHECK", tuesd);
                intent.putExtra("WEDNESDAY_CHECK", wednesd);
                intent.putExtra("THURSDAY_CHECK", thursd);
                intent.putExtra("FRIDAY_CHECK", frid);
                intent.putExtra("SATURDAY_CHECK", saturd);
                intent.putExtra("SUNDAY_CHECK", sund);

                intent.putExtra("ButtonListSize", size);
                intent.putStringArrayListExtra("ButtonList", Buttonlist);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else
            {
                Alarm.alarm = 0;
                Log.i("INSIDE ALARM LIST","INTENT!!!!!!!!!!!!!!!!!!!!! ALARM LIST");
                Intent intent = new Intent(Alarm.this,AlarmList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
//        } else {
//            Toast.makeText(getApplicationContext(), "Alarm Is Off", Toast.LENGTH_SHORT).show();
//        }
        if(value == false)
        {
            Toast.makeText(getApplicationContext(), "Alarm Is Off", Toast.LENGTH_SHORT).show();
        }

        //Log.i("CALENDAR _DAY_WEEK", String.valueOf(cal_alarm.get(Calendar.DAY_OF_WEEK)));

        //Log.i("DAY WEEK", String.valueOf(cal_alarm.get(Calendar.DAY_OF_WEEK)));

        //Log.i("CAL_ALARM", cal_alarm.toString());
        //Log.i("CAL_ALARM MILLIS", String.valueOf(cal_alarm.getTimeInMillis()));

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
        redirectActivity(this,AlarmList.class);
    }
    public void ClickAddAlarm(View view)
    {
        recreate();
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
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
}