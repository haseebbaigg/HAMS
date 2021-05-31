package com.example.text_recognition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase2;


    public static Context context;

    public static int gotAlarm = 0;

    EditText et;
    EditText desc;
    ImageView iv;
    Button button;
    public static TextView qr_code;
    Button qrButton;

    CardView cardView;

    Uri resultUri;

    LinearLayout linearLayout;
    ArrayList<Button> buttonList;


    static SQLiteDatabase sqLiteDatabase;

    Cursor cursor;
    Cursor check_insert;

    DrawerLayout drawerLayout;

    private static final int CAMERA_CODE = 200;
    private static final int STORAGE_CODE = 400;
    private static final int GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];

    Uri imageUri;

    StringBuilder stringBuilder;

    String EtText = null;
    String imgUri;
    String description = null;
    String QRCode = null;
    String stringb = null;

    String GetName = null;
    String GetDesc = null;
    String GetImg = null;
    String GetQr = null;
    String GetSB = null;
    int button_size  = 0;
    ArrayList<String> ButtonTextList = new ArrayList<>();
    ArrayList<String> ButtonArrayList = new ArrayList<>();

    ListView listView;
    ArrayList<String> arrayList1 = new ArrayList<>();

    String Id[], Hour[], Min[], Title[], Mon[], Tues[], Wed[], Thurs[], Fri[], Sat[], Sun[], Status[];


    SQLiteDatabase medicineDatabase;
    Cursor cursor3;

    Calendar cal_alarm;
    String Idd, HOUR, MIN, TITLE, MON, TUES, WED, THURS, FRI, SAT, SUN, STATUS;

    Handler handler = new Handler();

    public static Timer timer = new Timer();

    static int add_med = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("Click Button to Insert Image");

        medicineDatabase = openOrCreateDatabase("medicine",0,null);
        //medicineDatabase.execSQL("DROP TABLE medicine");

        getSupportActionBar().hide();

        context = MainActivity.this;

        drawerLayout = findViewById(R.id.drawer_layout);

        et = findViewById(R.id.editText);
        desc = findViewById(R.id.medicine_Text);
        button = findViewById(R.id.button6);

        cardView = findViewById(R.id.preview);

        qr_code = findViewById(R.id.qr_text);
        qrButton = findViewById(R.id.qr_btn);

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent scanIntent = new Intent(MainActivity.this, ScanCodeActivity.class);
                startActivity(scanIntent);
            }
        });

        button.setEnabled(false);

        iv = findViewById(R.id.img);
        linearLayout = findViewById(R.id.buttonLayout);
//        b1 = findViewById(R.id.button1);
//        b2 = findViewById(R.id.button2);
//        b3 = findViewById(R.id.button3);
//        b4 = findViewById(R.id.button4);
//        b5 = findViewById(R.id.button5);

        listView = findViewById(R.id.listView);
        cal_alarm = Calendar.getInstance();

        cardView.setVisibility(View.VISIBLE);

        buttonList = new ArrayList<>();

        medicineDatabase = this.openOrCreateDatabase("medicine", 0, null);

        if (stringBuilder == null) {
            et.setHint("Capture/Choose Image");
        }


        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        Intent intent1 = getIntent();
        GetName = intent1.getStringExtra("PassName");
        GetDesc = intent1.getStringExtra("PassDesc");
        GetImg = intent1.getStringExtra("PassImg");
        GetQr = intent1.getStringExtra("PassQR");
        GetSB = intent1.getStringExtra("PassSB");
        button_size = intent1.getIntExtra("ButtonListSize", 0);
        ButtonArrayList = intent1.getStringArrayListExtra("ButtonList");

        if (GetName != null) {
            et.setText(GetName.toLowerCase());
            EtText = GetName.toLowerCase().toString();
        }
        if (GetDesc != null) {
            desc.setText(GetDesc.toLowerCase());
            description = desc.getText().toString();
        }
        if (GetImg != null) {
            Log.i("GET_IMG", GetImg);
            iv.setImageURI(Uri.parse(GetImg));
            imgUri = GetImg;
        }
        if (GetQr != null) {
            Log.i("GET_QR", GetQr);
            qr_code.setText(GetQr.toLowerCase());
            QRCode = qr_code.getText().toString();
        }
        if (GetSB != null) {
            Log.i("GET_IMG", GetSB);
            stringb = GetSB.toLowerCase();
        }


        if (GetName != null) {
            sqLiteDatabase = openOrCreateDatabase("alarm", 0, null);

            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * from alarm WHERE title='" + GetName + "' ORDER BY id", null);

            //Log.i("CURCOR_CCCCCCC", cursor.toString());
            Id = new String[cursor.getCount()];
            Hour = new String[cursor.getCount()];
            Min = new String[cursor.getCount()];
            Title = new String[cursor.getCount()];
            Status = new String[cursor.getCount()];
            Mon = new String[cursor.getCount()];
            Tues = new String[cursor.getCount()];
            Wed = new String[cursor.getCount()];
            Thurs = new String[cursor.getCount()];
            Fri = new String[cursor.getCount()];
            Sat = new String[cursor.getCount()];
            Sun = new String[cursor.getCount()];

            Log.i("LENGTH", String.valueOf(Hour.length));
            cursor.moveToFirst();
            StringBuilder stringBuilder = new StringBuilder();
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
                    Converted_Hour = Integer.parseInt(hour);
                    stringBuilder.append(Converted_Hour);
                }
                if (Integer.parseInt(hour) > 12) {
                    Converted_Hour = Integer.parseInt(hour) - 12;
                    Log.i("HOUR>12", String.valueOf(Converted_Hour));
                    stringBuilder.append(Converted_Hour);
                } else if (Integer.parseInt(hour) < 12) {
                    Log.i("HOUR<12", String.valueOf(cursor.getString(1)));
                    stringBuilder.append(cursor.getString(1));
                }
                //stringBuilder.append(cursor.getString(1));
                stringBuilder.append(" : ");
                Log.i("minutes-length", String.valueOf(cursor.getString(2).length()));
                if (cursor.getString(2).length() > 1) {
                    Log.i("MINUTES>1", String.valueOf(cursor.getString(2)));
                    stringBuilder.append(cursor.getString(2));
                } else {
                    String min = cursor.getString(2);
                    String add_min = "0" + min;
                    Log.i("MINUTES<1", String.valueOf(add_min));
                    stringBuilder.append(add_min);
                }
                stringBuilder.append(" ");
                if (Integer.parseInt(hour) == 12) {
                    stringBuilder.append("AM");
                }
                if (Integer.parseInt(hour) < 12) {
                    stringBuilder.append("AM");
                } else if (Integer.parseInt(hour) > 12) {
                    stringBuilder.append("PM");
                }
                stringBuilder.append("\n");
                stringBuilder.append(cursor.getString(3));
                Log.i("STRING_BUILDER", stringBuilder.toString());
                arrayList1.add(stringBuilder.toString());

                cursor.moveToNext();
            }


            cursor.moveToFirst();
            for (int i = 0; i < Hour.length; i++) {
                Id[i] = String.valueOf(cursor.getString(0));
                Hour[i] = String.valueOf(cursor.getString(1));
                Log.i("hour[i]-loop", Hour[i]);
                Min[i] = String.valueOf(cursor.getString(2));
                Log.i("Minute[i]-loop", Min[i]);
                Title[i] = String.valueOf(cursor.getString(3));
                Log.i("Title[i]-loop", Title[i]);
                Status[i] = String.valueOf(cursor.getString(4));
                Mon[i] = String.valueOf(cursor.getString(5));
                Tues[i] = String.valueOf(cursor.getString(6));
                Wed[i] = String.valueOf(cursor.getString(7));
                Thurs[i] = String.valueOf(cursor.getString(8));
                Fri[i] = String.valueOf(cursor.getString(9));
                Sat[i] = String.valueOf(cursor.getString(10));
                Log.i("Sat[i]-loop", Sat[i]);
                Sun[i] = String.valueOf(cursor.getString(11));
                cursor.moveToNext();
            }
            for (int i = 0; i < arrayList1.size(); i++) {
                Log.i("ARRAY_LIST_ELEMENTS", arrayList1.get(i));
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList1);
            listView.setAdapter(arrayAdapter);

            listView.setOnTouchListener(new ListView.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }

                    // Handle ListView touch events.
                    v.onTouchEvent(event);
                    return true;
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Intent intent = new Intent(AlarmList.this,MainActivity.class);
                    //Toast.makeText(getApplicationContext(), "Hour :" + hour[position] + " Min :" + min[position] + " FRI : " + fri[position], Toast.LENGTH_LONG).show();
                    Intent intent2 = new Intent(MainActivity.this, Alarm.class);


                    if (EtText == null) {
                        Log.i("Title_#############", "TITLE _ NULL");
                    }
                    if (description == null) {
                        Log.i("DESC_############", "DESC _ NULL");
                    }
                    if (imgUri == null) {
                        Log.i("IMG_#############", "IMG _ NULL");
                    }
                    if (EtText != null & description != null & imgUri != null) {
//                        Log.i("Title_!!!!!!!!@@@@@", EtText.toString());
//                        Log.i("DESC_!!!!!!!!@@@@@", description);
//                        Log.i("IMG_!!!!!!!!@@@@@", imgUri.toString());


                        intent2.putExtra("name", EtText.toString());
                        intent2.putExtra("description", description);
                        intent2.putExtra("imageUri", imgUri);

                        intent2.putExtra("id", Id[position]);
                        intent2.putExtra("hour", Hour[position]);
                        intent2.putExtra("min", Min[position]);
                        intent2.putExtra("title", Title[position]);
                        intent2.putExtra("mon", Mon[position]);
                        intent2.putExtra("tues", Tues[position]);
                        intent2.putExtra("wed", Wed[position]);
                        intent2.putExtra("thurs", Thurs[position]);
                        intent2.putExtra("fri", Fri[position]);
                        intent2.putExtra("sat", Sat[position]);
                        intent2.putExtra("sun", Sun[position]);
                        intent2.putExtra("status", Status[position]);

                        Log.i("Intent-Extra-Id", Id[position].toString());
                        Log.i("Intent-Extra-hour", Hour[position].toString());
                        Log.i("Intent-Extra-Min", Min[position].toString());
                        Log.i("Intent-Extra-Sat", Sat[position].toString());
                        Log.i("Intent-Extra-tues", Tues[position].toString());

                        startActivity(intent2);
                    }

                }
            });
        }

        if (et.getText().toString().trim().length() > 0 & desc.getText().toString().trim().length() > 0) {
            button.setEnabled(true);
        }

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    button.setEnabled(false);
                } else {
                    button.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 0) {
                    button.setEnabled(false);
                } else {
                    button.setEnabled(true);
                }
            }
        });

        desc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    button.setEnabled(false);
                } else {
                    button.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 0) {
                    button.setEnabled(false);
                } else {
                    button.setEnabled(true);
                }
            }
        });

        if (AlarmList.alarmlist == 1) {

            cardView.setVisibility(View.GONE);
            Log.i("ALARM_LIST", "CLICK ON ALARM LIST _ &&&&&&&&&&&&&&&&&&&&&&&&");
            Intent intent = getIntent();
            Idd = intent.getStringExtra("id");
            HOUR = intent.getStringExtra("hour");
            MIN = intent.getStringExtra("min");
            TITLE = intent.getStringExtra("title");

            Log.i("GOT_TITLE-^^^^^^^^^^", TITLE);

            medicineDatabase = openOrCreateDatabase("medicine", 0, null);
            Cursor cursor = medicineDatabase.rawQuery("SELECT * FROM medicine WHERE name='" + TITLE + "'", null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Log.i("MEDICINE_NAME**********", cursor.getString(1));
                et.setText(cursor.getString(1));
                desc.setText(cursor.getString(3));
                cursor.moveToNext();
            }
            sqLiteDatabase2 = openOrCreateDatabase("alarm", 0, null);
            Cursor cursor1 = sqLiteDatabase2.rawQuery("SELECT * from alarm WHERE title='" + TITLE + "' ORDER BY id", null);

            Id = new String[cursor1.getCount()];
            Hour = new String[cursor1.getCount()];
            Min = new String[cursor1.getCount()];
            Title = new String[cursor1.getCount()];
            Status = new String[cursor1.getCount()];
            Mon = new String[cursor1.getCount()];
            Tues = new String[cursor1.getCount()];
            Wed = new String[cursor1.getCount()];
            Thurs = new String[cursor1.getCount()];
            Fri = new String[cursor1.getCount()];
            Sat = new String[cursor1.getCount()];
            Sun = new String[cursor1.getCount()];

            cursor1.moveToFirst();
            ArrayList<String> alarmlist = new ArrayList<>();
            StringBuilder stringBuilder1 = new StringBuilder();
            while (!cursor1.isAfterLast()) {
                stringBuilder1.setLength(0);
                String hour = cursor1.getString(1);
                int Converted_Hour = 0;
                if (Integer.parseInt(hour) == 0) {
                    Converted_Hour = Integer.parseInt(hour) + 12;
                    stringBuilder1.append(Converted_Hour);
                } else if (Integer.parseInt(hour) == 12) {
                    Converted_Hour = Integer.parseInt(hour);
                    stringBuilder1.append(Converted_Hour);
                } else if (Integer.parseInt(hour) > 12) {
                    Converted_Hour = Integer.parseInt(hour) - 12;
                    stringBuilder1.append(Converted_Hour);
                } else if (Integer.parseInt(hour) < 12) {
                    stringBuilder1.append(cursor1.getString(1));
                }
                //stringBuilder.append(cursor.getString(1));
                stringBuilder1.append(" : ");
                if (cursor1.getString(2).length() > 1) {
                    stringBuilder1.append(cursor1.getString(2));
                } else {
                    String min = cursor1.getString(2);
                    String add_min = "0" + min;
                    stringBuilder1.append(add_min);
                }
                stringBuilder1.append(" ");
                if (Integer.parseInt(hour) < 12) {
                    stringBuilder1.append("AM");
                } else if (Integer.parseInt(hour) > 12) {
                    stringBuilder1.append("PM");
                } else if (Integer.parseInt(hour) == 12) {
                    stringBuilder1.append("PM");
                }
                stringBuilder1.append("\n");
                stringBuilder1.append(cursor1.getString(3));
                alarmlist.add(stringBuilder1.toString());
                cursor1.moveToNext();
            }
            //Log.i("STRING BUILDER ALARM",stringBuilder1.toString());

            cursor1.moveToFirst();
            for (int i = 0; i < Hour.length; i++) {
                //Log.i("FOR LOOP","GETTING FOR LOOP");
                //Log.i("IDDDDDDDDDDDD",cursor1.getString(0));
                Id[i] = String.valueOf(cursor1.getString(0));
                //Log.i("FOR LOOP HOUR","GETTING FOR LOOP HOUR");
                Hour[i] = String.valueOf(cursor1.getString(1));
                //Log.i("FOR LOOP MINUTE","GETTING FOR LOOP MINUTE");
                Min[i] = String.valueOf(cursor1.getString(2));
                Title[i] = String.valueOf(cursor1.getString(3));
                Status[i] = String.valueOf(cursor1.getString(4));
                Mon[i] = String.valueOf(cursor1.getString(5));
                Tues[i] = String.valueOf(cursor1.getString(6));
                Wed[i] = String.valueOf(cursor1.getString(7));
                Thurs[i] = String.valueOf(cursor1.getString(8));
                Fri[i] = String.valueOf(cursor1.getString(9));
                Sat[i] = String.valueOf(cursor1.getString(10));
                Sun[i] = String.valueOf(cursor1.getString(11));

                cursor1.moveToNext();
            }

            Log.i("ARRAY ADAPTER", "GETTING ARRAY ADAPTER");
            ArrayAdapter arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, alarmlist);
            listView.setAdapter(arrayAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Intent intent = new Intent(AlarmList.this,MainActivity.class);
                    Log.i("POSITION", String.valueOf(position));
                    Log.i("HOUR-$$$$$$$$$$$", Hour[position]);
                    Log.i("MINUTE-$$$$$$$$$", Min[position]);
                    Log.i("TITLE-$$$$$$$$$$", Title[position]);

                    //Toast.makeText(getApplicationContext(), "Hour :" + hour[position] + " Min :" + min[position] + " FRI : " + fri[position], Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, Alarm.class);
                    intent.putExtra("id", Id[position]);
                    intent.putExtra("hour", Hour[position]);
                    intent.putExtra("min", Min[position]);
                    intent.putExtra("title", Title[position]);
                    intent.putExtra("mon", Mon[position]);
                    intent.putExtra("tues", Tues[position]);
                    intent.putExtra("wed", Wed[position]);
                    intent.putExtra("thurs", Thurs[position]);
                    intent.putExtra("fri", Fri[position]);
                    intent.putExtra("sat", Sat[position]);
                    intent.putExtra("sun", Sun[position]);
                    intent.putExtra("status", Status[position]);

                    startActivity(intent);
                }
            });

        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                Calendar calendar = Calendar.getInstance();
//
//                Boolean monn = false, tuess = false, wedd = false, thurss = false, frii = false, satt = false, sunn = false;
//
//                try {
//                    sqLiteDatabase = openOrCreateDatabase("alarm", 0, null);
//                    Cursor cursor1 = sqLiteDatabase.rawQuery("SELECT * from alarm WHERE hour='" + calendar.get(Calendar.HOUR_OF_DAY) + "'" +
//                            "AND minutes='" + calendar.get(Calendar.MINUTE) + "'", null);
//
//                    Log.i("GOT ALARM", String.valueOf(gotAlarm));
//                    Log.i("INSIDE_HANDLER", "HANDLER_!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//                    if (cursor1.getCount() > 0) {
//                        Log.i("HAVE SOMETHING", "CURSOR IS GREATER THAN 0");
//                        if (cursor1.moveToFirst()) {
//                            timer.cancel();
//                            cursor1.moveToFirst();
//                            Log.i("HOUR_OF_CUROSR_$$$$$$$$", "HOUR_!!!!!!!!!!!!!!!!!!!!!!!!11111111");
//                            while (!cursor1.isAfterLast()) {
//                                Log.i("GET_CURSOR_HOUR_@@@@@", cursor1.getString(1));
//                                Log.i("GET_CURSOR-MINUTE-@@@@@", cursor1.getString(2));
//                                cal_alarm.set(Calendar.HOUR_OF_DAY, Integer.parseInt(cursor1.getString(1)));
//                                cal_alarm.set(Calendar.MINUTE, Integer.parseInt(cursor1.getString(2)));
//                                cal_alarm.set(Calendar.SECOND, 0);
//
//                                monn = Boolean.parseBoolean(cursor1.getString(5));
//                                tuess = Boolean.parseBoolean(cursor1.getString(6));
//                                wedd = Boolean.parseBoolean(cursor1.getString(7));
//                                thurss = Boolean.parseBoolean(cursor1.getString(8));
//                                frii = Boolean.parseBoolean(cursor1.getString(9));
//                                satt = Boolean.parseBoolean(cursor1.getString(10));
//                                sunn = Boolean.parseBoolean(cursor1.getString(11));
//
//                                cursor1.moveToNext();
//                            }
//                            Log.i("MONNNNNNNNN", monn.toString());
//                            Log.i("TUESDAAAAAA", tuess.toString());
//                            Log.i("WEDDDDDDDDD", wedd.toString());
//                            Log.i("THURSSSSSSS", thurss.toString());
//                            Log.i("FRIIIIIIIII", frii.toString());
//                            Log.i("SATTTTTTTTT", satt.toString());
//                            Log.i("SUNDDDDDDDD", sunn.toString());
//                            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//                            if (satt == true) {
//                                if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 7) {
//                                    Log.i("SAT_ALARM_LISt-1111", "TRUE");
//                                    Intent intent = new Intent(MainActivity.this, BroadcastReceiver.class);
//                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
//
//                                    am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                                }
//                            }
//                            if (frii == true) {
//                                if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 6) {
//                                    Log.i("ALARM-TRIGGERED_1111111", "FRIDAY_1111111");
//                                    Intent intent = new Intent(MainActivity.this, BroadcastReceiver.class);
//                                    //Log.i("ALARM-TRIGGERED_22222","FRIDAY_22222");
//                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
//                                    //Log.i("ALARM-TRIGGERED","FRIDAY");
//                                    am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                                    Log.i("ALARM-TRIGGERED_!!!!!!", "FRIDAY_!!!!!!!!");
//                                }
//                            }
//                            if (thurss == true) {
//                                if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 5) {
//                                    Log.i("THURS_ALARM_LISt-1111", "TRUE");
//                                    Intent intent = new Intent(MainActivity.this, BroadcastReceiver.class);
//                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
//
//                                    Log.i("ALARM-GETTIME-MILLIS: ", String.valueOf(cal_alarm.getTimeInMillis()));
//                                    Date date = new Date(cal_alarm.getTimeInMillis());
//                                    Log.i("DATE_!!!!!!!!", String.valueOf(date));
//                                    am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                                }
//                            }
//                            if (wedd == true) {
//                                if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 4) {
//                                    Log.i("WED_111", "TRUE");
//                                    Intent intent = new Intent(MainActivity.this, BroadcastReceiver.class);
//                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
//                                    am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                                }
//                            }
//                            if (tuess == true) {
//                                if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 3) {
//                                    Log.i("TUES_ALARM_LISt-11111", "TRUE");
//                                    Intent intent = new Intent(MainActivity.this, BroadcastReceiver.class);
//                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
//                                    am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                                }
//                            }
//                            if (monn == true) {
//                                if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 2) {
//                                    Log.i("MON_1111", "TRUE");
//                                    Intent intent = new Intent(MainActivity.this, BroadcastReceiver.class);
//                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
//
//                                    am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                                }
//                            }
//                            if (sunn == true) {
//                                if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 1) {
//                                    Log.i("SUN_ALARM_LISt-1111111", "TRUE");
//                                    Intent intent = new Intent(MainActivity.this, BroadcastReceiver.class);
//                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
//
//                                    am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                                }
//                            }
//                        }
//                        Thread.sleep(60000);
//                    }
//                } catch (Exception e) {
//                    Log.i("EXCEPTION TIMER",e.getMessage());
//                }
//            }
//        }, 10000, 10000);
//    }

    //            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Calendar calendar = Calendar.getInstance();
//
//                    Boolean monn = false, tuess = false, wedd = false, thurss = false, frii = false, satt = false, sunn = false;
//
//                    sqLiteDatabase = openOrCreateDatabase("alarm", 0, null);
//                        Cursor cursor1 = sqLiteDatabase.rawQuery("SELECT * from alarm WHERE hour='" + calendar.get(Calendar.HOUR_OF_DAY) + "'" +
//                                "AND minutes='" + calendar.get(Calendar.MINUTE) + "'", null);
//
//                        Log.i("GOT ALARM", String.valueOf(gotAlarm));
//                        Log.i("INSIDE_HANDLER", "HANDLER_!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//                        if(gotAlarm != 1) {
//                            if (cursor1.getCount() > 0) {
//                                stopHandler();
//                                Log.i("HAVE SOMETHING", "CURSOR IS GREATER THAN 0");
//                                if (cursor1.moveToFirst()) {
//                                    cursor1.moveToFirst();
//                                    Log.i("HOUR_OF_CUROSR_$$$$$$$$", "HOUR_!!!!!!!!!!!!!!!!!!!!!!!!11111111");
//                                    while (!cursor1.isAfterLast()) {
//                                        Log.i("GET_CURSOR_HOUR_@@@@@", cursor1.getString(1));
//                                        Log.i("GET_CURSOR-MINUTE-@@@@@", cursor1.getString(2));
//                                        cal_alarm.set(Calendar.HOUR_OF_DAY, Integer.parseInt(cursor1.getString(1)));
//                                        cal_alarm.set(Calendar.MINUTE, Integer.parseInt(cursor1.getString(2)));
//                                        cal_alarm.set(Calendar.SECOND, 0);
//
//                                        monn = Boolean.parseBoolean(cursor1.getString(5));
//                                        tuess = Boolean.parseBoolean(cursor1.getString(6));
//                                        wedd = Boolean.parseBoolean(cursor1.getString(7));
//                                        thurss = Boolean.parseBoolean(cursor1.getString(8));
//                                        frii = Boolean.parseBoolean(cursor1.getString(9));
//                                        satt = Boolean.parseBoolean(cursor1.getString(10));
//                                        sunn = Boolean.parseBoolean(cursor1.getString(11));
//
//                                        cursor1.moveToNext();
//                                    }
//                                    Log.i("MONNNNNNNNN", monn.toString());
//                                    Log.i("TUESDAAAAAA", tuess.toString());
//                                    Log.i("WEDDDDDDDDD", wedd.toString());
//                                    Log.i("THURSSSSSSS", thurss.toString());
//                                    Log.i("FRIIIIIIIII", frii.toString());
//                                    Log.i("SATTTTTTTTT", satt.toString());
//                                    Log.i("SUNDDDDDDDD", sunn.toString());
//                                    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//                                    if (satt == true) {
//                                        if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 7) {
//                                            Log.i("SAT_ALARM_LISt-1111", "TRUE");
//                                            Intent intent = new Intent(MainActivity.this, BroadcastReceiver.class);
//                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
//
//                                            am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                                        }
//                                    }
//                                    if (frii == true) {
//                                        if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 6) {
//                                            Log.i("ALARM-TRIGGERED_1111111", "FRIDAY_1111111");
//                                            Intent intent = new Intent(MainActivity.this, BroadcastReceiver.class);
//                                            //Log.i("ALARM-TRIGGERED_22222","FRIDAY_22222");
//                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
//                                            //Log.i("ALARM-TRIGGERED","FRIDAY");
//                                            am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                                            Log.i("ALARM-TRIGGERED_!!!!!!", "FRIDAY_!!!!!!!!");
//                                        }
//                                    }
//                                    if (thurss == true) {
//                                        if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 5) {
//                                            Log.i("THURS_ALARM_LISt-1111", "TRUE");
//                                            Intent intent = new Intent(MainActivity.this, BroadcastReceiver.class);
//                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
//
//                                            Log.i("ALARM-GETTIME-MILLIS: ", String.valueOf(cal_alarm.getTimeInMillis()));
//                                            Date date = new Date(cal_alarm.getTimeInMillis());
//                                            Log.i("DATE_!!!!!!!!", String.valueOf(date));
//                                            am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                                        }
//                                    }
//                                    if (wedd == true) {
//                                        if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 4) {
//                                            Log.i("WED_111", "TRUE");
//                                            Intent intent = new Intent(MainActivity.this, BroadcastReceiver.class);
//                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
//                                            am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                                        }
//                                    }
//                                    if (tuess == true) {
//                                        if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 3) {
//                                            Log.i("TUES_ALARM_LISt-11111", "TRUE");
//                                            Intent intent = new Intent(MainActivity.this, BroadcastReceiver.class);
//                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
//                                            am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                                        }
//                                    }
//                                    if (monn == true) {
//                                        if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 2) {
//                                            Log.i("MON_1111", "TRUE");
//                                            Intent intent = new Intent(MainActivity.this, BroadcastReceiver.class);
//                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
//
//                                            am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                                        }
//                                    }
//                                    if (sunn == true) {
//                                        if (cal_alarm.get(Calendar.DAY_OF_WEEK) == 1) {
//                                            Log.i("SUN_ALARM_LISt-1111111", "TRUE");
//                                            Intent intent = new Intent(MainActivity.this, BroadcastReceiver.class);
//                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
//
//                                            am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
//                                        }
//                                    }
//                                }
//                            }
//                            if (cursor1.getCount() <= 0) {
//                                Log.i("DELAY OF HALF MINUTE", "HALFFFFFFFFFFFFFF MINUTEEEEEEEEEEEE");
//                                handler.postDelayed(this, 30000);
//                            }
//
//                        }
//
//
////                Log.i("DAY-OF-WEEK: ", String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)));
////                Log.i("HOUR OF DAY: ", String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
////                Log.i("MINUTE OF DAY: ", String.valueOf(calendar.get(Calendar.MINUTE)));
////
////                Log.i("GETTIME-MILLIS@@@@@ : ", String.valueOf(cal_alarm.getTimeInMillis()));
////                Date date = new Date(cal_alarm.getTimeInMillis());
////                Log.i("GETTING_DATE_#########", String.valueOf(date));
//
//                }
//
//            },0);
//        }
//        public void stopHandler(){
//        gotAlarm = 1;
//        handler.removeMessages(0);
//        handler.removeCallbacksAndMessages(null);
//        }



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
        redirectActivity(this,Alarm.class);
    }
    public void ClickAddMedicine(View view)
    {
        recreate();
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
    public void ClickOption(View view)
    {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);//View will be an anchor for PopupMenu
        popupMenu.inflate(R.menu.main_menu);
        Menu menu = popupMenu.getMenu();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.title) {
                    Log.i("MENU ITEM","SETTING_CLICKED-!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11");
                    showImageImportDialog();
                }
                return false;
            }
        });
        popupMenu.show();
    }

    public static void redirectActivity(Activity activity, Class aclass) {
        Intent intent = new Intent(activity,aclass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().hide();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

//    public void MenuClick(View view)
//    {
//        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);//View will be an anchor for PopupMenu
//        popupMenu.inflate(R.menu.main_menu);
//        Menu menu = popupMenu.getMenu();
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                int id = item.getItemId();
//                if (id == R.id.title) {
//                    Log.i("MENU ITEM","SETTING_CLICKED-!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11");
//                    showImageImportDialog();
//                }
//                return false;
//            }
//        });
//        popupMenu.show();
//    }


//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.title) {
//            showImageImportDialog();
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void showImageImportDialog() {
        String[] items = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        getSupportActionBar().hide();
                        pickCamera();
                    }
                }
                if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        getSupportActionBar().hide();
                        pickGallery();
                    }
                }
            }
        });
        builder.create().show();
    }

    private void pickGallery() {
        Log.i("BUTTON_LIST_SIZE_1", String.valueOf(buttonList.size()));
        et.setText("");
        desc.setText("");
        iv.setImageURI(Uri.parse(""));
        linearLayout.setVisibility(View.GONE);
        Log.i("BUTTON_LIST_SIZE_2", String.valueOf(buttonList.size()));

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);


    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return result;
    }

    private void pickCamera() {
        et.setText("");
        desc.setText("");
        iv.setImageURI(Uri.parse(""));
        linearLayout.setVisibility(View.GONE);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NEW PICTURE");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image To Text");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_CODE);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return result && result1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_CODE:
                if (grantResults.length > 0) {
                    boolean CameraAllowed = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean StorageAllowed = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (CameraAllowed && StorageAllowed) {
                        pickCamera();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_CODE:
                if (grantResults.length > 0) {
                    boolean StorageAllowed = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (StorageAllowed) {
                        pickGallery();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Log.i("ACTIVITY_RESULT","HERE");
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_CODE) {
                Log.i("GALLERY CODE","GALLERY CODE");
                getSupportActionBar().show();
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                getSupportActionBar().show();
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
        }
        //Log.i("CROP_IMG",String.valueOf(CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE));
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            //Log.i("CROP_IMAGE","REQUEST");
            //Log.i("REQ_CODE",String.valueOf(requestCode));
            //Log.i("203",String.valueOf(RESULT_OK));
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                //Log.i("REQUEST_CODE","OK");
                imgUri = resultUri.toString();
                Log.i("RESULT_URI_RRRRRRRR", resultUri.toString());
                Log.i("IMG_URI_IMGIMGIMGIMG", imgUri);
                iv.setImageURI(resultUri);


                BitmapDrawable bitmapDrawable = (BitmapDrawable) iv.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();


                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if (!recognizer.isOperational()) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);

                    ArrayList<String> arrayList = new ArrayList<>();
                    linearLayout.setVisibility(View.VISIBLE);

                    listView.setVisibility(View.VISIBLE);

                    int cursor_result = 0;

                    String med_name = null;

                    stringBuilder = new StringBuilder();
                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItems = items.valueAt(i);
                        String replaceItems;
                        items.valueAt(i).getValue().toLowerCase().replaceAll("\\p{Punct}", "");
                        items.valueAt(i).getValue().toLowerCase().replaceAll("\'", "");
                        //replaceItems  = myItems.getValue().toLowerCase().replaceAll("\'","");
                        stringBuilder.append(items.valueAt(i).getValue().toLowerCase());
                        stringBuilder.append(" ");
                        Log.i("FIRST_TEXT", String.valueOf(myItems.getValue()));
                        //Log.i("BUILDER:", stringBuilder.toString());
                        arrayList.add(items.valueAt(i).getValue().toLowerCase());
                        ButtonTextList.add(items.valueAt(i).getValue().toLowerCase());
                        cursor3 = medicineDatabase.rawQuery("SELECT * from medicine WHERE info ='" + myItems.getValue().toLowerCase().replaceAll("\\p{Punct}","") + "' " +
                                "OR name='"+myItems.getValue().toLowerCase().replaceAll("\\p{Punct}","")+"' ", null);

                        if (cursor3.getCount() <= 0 && cursor_result == 0) {
                            Log.i("GET_COUNT", "000000000000000000000");
                            cursor_result = 0;
                        } else if (cursor3.getCount() > 0) {
                            Log.i("GET_COUNT", "11111111111111111111111111");
                            Log.i("CURSOR_COUNT***********", String.valueOf(cursor3.getCount()));
                            if(cursor3.moveToFirst())
                            {
                                Log.i("FIRST_CURSOR_ELEMENT", String.valueOf(cursor3.getString(1)));
                                med_name = String.valueOf(cursor3.getString(1));
                            }
                            cursor_result = 1;
                        }
                    }
                    Log.i("Strng_BUILDER 2 ", stringBuilder.toString());
                    Log.i("Array_LIST_LLLLLLLL", String.valueOf(arrayList.size()));
                    buttonList.clear();
                    Log.i("BUTTON_LIST_SIZEEEEEEEE", String.valueOf(buttonList.size()));

                    linearLayout.removeAllViews();

                    for (int i = 0; i < arrayList.size(); i++) {
                        final Button button1 = new Button(this);
                        button1.getText().toString().replaceAll("\\p{Punct} \\p{Po}","");
                        buttonList.add(button1);
                        button1.setTag(i);
                        button1.setText(arrayList.get(i).toString().replaceAll("\\p{Punct} \\p{Po}",""));
                        Log.i("ARRAY_LIST[i]-[[[[[[[[[",arrayList.get(i));
                        Log.i("BUTTON_LIST_GETTTT", String.valueOf(buttonList.get(buttonList.size()-1)));
                        button1.setMaxLines(1);
                        button1.setEllipsize(TextUtils.TruncateAt.END);
                        //Log.i("BUTTONS",button1.getText().toString());
                        button1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                et.setText(button1.getText().toString().toLowerCase().replaceAll("\\p{Punct} \\p{Po}",""));
                            }
                        });
                        linearLayout.addView(buttonList.get(buttonList.size() - 1));
                    }

                    Log.i("Linear_button_list", String.valueOf(buttonList.get(buttonList.size()-1)));
                    Log.i("BUTTON_LIST_SIZE", String.valueOf(buttonList.size()));
                    Log.i("BUTTON_LIST_ARRAY", String.valueOf(buttonList));
                    arrayList.clear();
                    et.setHint("Choose Medicine Name from buttons");
                    Log.i("RESULT_URI_RERERERERERE", resultUri.toString());
                    Log.i("IMG_URI_IUIUIUIUIUIUIU", imgUri);



                    try {
                        Cursor cursor123 = medicineDatabase.rawQuery("SELECT * from medicine", null);
                        if (cursor123.getCount() > 0) {
                            cursor123.moveToFirst();
                            while (!cursor123.isAfterLast()) {
                                Log.i("MEDICINE_TITLE---------", cursor123.getString(1));
                                //med_name = cursor123.getString(1);
                                Log.i("MEDICINE_TITLE_COUNT---", String.valueOf(cursor123.getCount()));
                                cursor123.moveToNext();
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.i("MEDICINE_EXCEPTION",e.getMessage());
                    }


                    sqLiteDatabase2 = openOrCreateDatabase("alarm", 0, null);

                    Cursor cursor1 = sqLiteDatabase2.rawQuery("SELECT * from alarm", null);
                    if(cursor1.getCount() > 0) {
                        cursor1.moveToFirst();
                        while (!cursor1.isAfterLast()) {
                            Log.i("ALARM_TITLE++++++++++++", cursor1.getString(3));
                            cursor1.moveToNext();
                        }
                    }
                    Log.i("STRING_BUILDER_CURSOR", stringBuilder.toString().toLowerCase());

                    listView.setAdapter(null);
                    if (cursor_result == 1) {

                        try {
                            //if (cursor3.moveToFirst()) {
                            Log.i("MEDICINE_NAME_ALARM",med_name);
                                //Log.i("CURSOR_INDEX_1",cursor3.getString(1));
                                Cursor cursor = sqLiteDatabase2.rawQuery("SELECT * from alarm WHERE title ='" + med_name+ "' ORDER BY id", null);
                                Log.i("ALARM_DB_REACHED__!!!!", "DB_ALARM");
                                Log.i("CURSOR_ALARM_DB", cursor.toString());
                                if (cursor.moveToFirst()) {
                                    Log.i("CURCOR_CCCCCCC", cursor.toString());
                                    Id = new String[cursor.getCount()];
                                    Hour = new String[cursor.getCount()];
                                    Min = new String[cursor.getCount()];
                                    Title = new String[cursor.getCount()];
                                    Status = new String[cursor.getCount()];
                                    Mon = new String[cursor.getCount()];
                                    Tues = new String[cursor.getCount()];
                                    Wed = new String[cursor.getCount()];
                                    Thurs = new String[cursor.getCount()];
                                    Fri = new String[cursor.getCount()];
                                    Sat = new String[cursor.getCount()];
                                    Sun = new String[cursor.getCount()];

                                    Log.i("LENGTH", String.valueOf(Hour.length));
                                    cursor.moveToFirst();
                                    StringBuilder stringBuilder = new StringBuilder();
                                    while (!cursor.isAfterLast()) {
                                        stringBuilder.setLength(0);
                                        Log.i("ID", String.valueOf(cursor.getString(0)));
                                        Log.i("Hour", String.valueOf(cursor.getString(1)));
                                        if (cursor.getString(2).length() > 1) {
                                            Log.i("Min", String.valueOf(cursor.getString(2)));
                                        } else {
                                            Log.i("Min", 0 + cursor.getString(2));
                                        }
                                        Log.i("Title", String.valueOf(cursor.getString(3)));
                                        Log.i("M", String.valueOf(cursor.getString(5)));
                                        Log.i("T", String.valueOf(cursor.getString(6)));
                                        Log.i("W", String.valueOf(cursor.getString(7)));
                                        Log.i("TH", String.valueOf(cursor.getString(8)));
                                        Log.i("F", String.valueOf(cursor.getString(9)));
                                        Log.i("S", String.valueOf(cursor.getString(10)));
                                        Log.i("SUN", String.valueOf(cursor.getString(11)));
                                        Log.i("SWITCH", String.valueOf(cursor.getString(4)));

                                        String hour = cursor.getString(1);
                                        Log.i("SIMPLE_HOUR", hour);
                                        int Converted_Hour = 0;
                                        if(Integer.parseInt(hour) == 12)
                                        {
                                            stringBuilder.append(cursor.getString(1));
                                        }
                                        if (Integer.parseInt(hour) > 12) {
                                            Converted_Hour = Integer.parseInt(hour) - 12;
                                            Log.i("HOUR>12", String.valueOf(Converted_Hour));
                                            stringBuilder.append(Converted_Hour);
                                        } else if (Integer.parseInt(hour) < 12) {
                                            Log.i("HOUR<12", String.valueOf(cursor.getString(1)));
                                            stringBuilder.append(cursor.getString(1));
                                        }
                                        //stringBuilder.append(cursor.getString(1));
                                        stringBuilder.append(" : ");
                                        Log.i("minutes-length", String.valueOf(cursor.getString(2).length()));
                                        if (cursor.getString(2).length() > 1) {
                                            Log.i("MINUTES>1", String.valueOf(cursor.getString(2)));
                                            stringBuilder.append(cursor.getString(2));
                                        } else {
                                            String min = cursor.getString(2);
                                            String add_min = "0" + min;
                                            Log.i("MINUTES<1", String.valueOf(add_min));
                                            stringBuilder.append(add_min);
                                        }
                                        stringBuilder.append(" ");
                                        if (Integer.parseInt(hour) == 12) {
                                            stringBuilder.append("AM");
                                        }
                                        if (Integer.parseInt(hour) < 12) {
                                            stringBuilder.append("AM");
                                        } else if (Integer.parseInt(hour) > 12) {
                                            stringBuilder.append("PM");
                                        }
                                        stringBuilder.append("\n");
                                        stringBuilder.append(cursor.getString(3));
                                        Log.i("STRING_BUILDER", stringBuilder.toString());
                                        arrayList1.add(stringBuilder.toString());

                                        cursor.moveToNext();
                                    }


                                    cursor.moveToFirst();
                                    for (int i = 0; i < Hour.length; i++) {
                                        Id[i] = String.valueOf(cursor.getString(0));
                                        Hour[i] = String.valueOf(cursor.getString(1));
                                        Log.i("hour[i]-loop", Hour[i]);
                                        Min[i] = String.valueOf(cursor.getString(2));
                                        Log.i("Minute[i]-loop", Min[i]);
                                        Title[i] = String.valueOf(cursor.getString(3));
                                        Log.i("Title[i]-loop", Title[i]);
                                        Status[i] = String.valueOf(cursor.getString(4));
                                        Mon[i] = String.valueOf(cursor.getString(5));
                                        Tues[i] = String.valueOf(cursor.getString(6));
                                        Wed[i] = String.valueOf(cursor.getString(7));
                                        Thurs[i] = String.valueOf(cursor.getString(8));
                                        Fri[i] = String.valueOf(cursor.getString(9));
                                        Sat[i] = String.valueOf(cursor.getString(10));
                                        Log.i("Sat[i]-loop", Sat[i]);
                                        Sun[i] = String.valueOf(cursor.getString(11));
                                        cursor.moveToNext();
                                    }
                                    for (int i = 0; i < arrayList1.size(); i++) {
                                        Log.i("ARRAY_LIST_ELEMENTS", arrayList1.get(i));
                                    }
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList1);
                                    listView.setAdapter(arrayAdapter);

                                    listView.setOnTouchListener(new ListView.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            int action = event.getAction();
                                            switch (action) {
                                                case MotionEvent.ACTION_DOWN:
                                                    // Disallow ScrollView to intercept touch events.
                                                    v.getParent().requestDisallowInterceptTouchEvent(true);
                                                    break;

                                                case MotionEvent.ACTION_UP:
                                                    // Allow ScrollView to intercept touch events.
                                                    v.getParent().requestDisallowInterceptTouchEvent(false);
                                                    break;
                                            }

                                            // Handle ListView touch events.
                                            v.onTouchEvent(event);
                                            return true;
                                        }
                                    });

                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            //Intent intent = new Intent(AlarmList.this,MainActivity.class);
                                            //Toast.makeText(getApplicationContext(), "Hour :" + hour[position] + " Min :" + min[position] + " FRI : " + fri[position], Toast.LENGTH_LONG).show();
                                            Intent intent2 = new Intent(MainActivity.this, Alarm.class);
                                            EtText = et.getText().toString().toLowerCase();
                                            description = desc.getText().toString().toLowerCase();
                                            if (EtText != null & description != null & imgUri != null) {

                                                intent2.putExtra("name", EtText.toString());
                                                intent2.putExtra("description", description);
                                                intent2.putExtra("imageUri", imgUri);

                                                intent2.putExtra("id", Id[position]);
                                                intent2.putExtra("hour", Hour[position]);
                                                intent2.putExtra("min", Min[position]);
                                                intent2.putExtra("title", Title[position]);
                                                intent2.putExtra("mon", Mon[position]);
                                                intent2.putExtra("tues", Tues[position]);
                                                intent2.putExtra("wed", Wed[position]);
                                                intent2.putExtra("thurs", Thurs[position]);
                                                intent2.putExtra("fri", Fri[position]);
                                                intent2.putExtra("sat", Sat[position]);
                                                intent2.putExtra("sun", Sun[position]);
                                                intent2.putExtra("status", Status[position]);

                                                Log.i("Intent-Extra-Id", Id[position].toString());
                                                Log.i("Intent-Extra-hour", Hour[position].toString());
                                                Log.i("Intent-Extra-Min", Min[position].toString());
                                                Log.i("Intent-Extra-Sat", Sat[position].toString());
                                                Log.i("Intent-Extra-tues", Tues[position].toString());


                                                Log.i("Title_!!!!!!!!@@@@@", EtText.toString());
                                                Log.i("DESC_!!!!!!!!@@@@@", description);
                                                Log.i("IMG_!!!!!!!!@@@@@", imgUri.toString());

                                                startActivity(intent2);
                                            }
                                        }
                                    });
                                }
                            //}
                        }
                        catch(Exception e)
                        {
                            Log.i("MEDICINE_EXCEPTION",e.getMessage());
                        }
                    }
                }
            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception exception = result.getError();
                        Toast.makeText(this, "CROP IMAGE ERROR" + exception, Toast.LENGTH_SHORT).show();
                    }

                }
                super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("LongLogTag")
    public void Save(View view) {
//        sqLiteDatabase.execSQL("DROP TABLE medicine");
//        sqLiteDatabase.delete("medicine","1",null);

        Log.i("QR_CODE_GET",qr_code.getText().toString().toLowerCase());
        QRCode = qr_code.getText().toString().toLowerCase();
        Log.i("QR_CODE-GETTINGSSSSS",QRCode.toString().toLowerCase());
//        Log.i("String-Builder_CODE_GET",stringb.toLowerCase());

        if (imgUri != null) {
            Log.i("BUTTON+LIST_GET",String.valueOf(buttonList));
            Log.i("BUTTON_LIST_SIZE", String.valueOf(buttonList.size()));
            Log.i("BUTTON_LIST_SIZE_INTENT", String.valueOf(button_size));

            Log.i("GET_BUTTON_LIST_ARRAY", String.valueOf(ButtonArrayList));
            //Log.i("GET_BUTTON_ARRAYSIZE", String.valueOf(ButtonArrayList.size()));
            //buttonList.get(i).getText().toString().toLowerCase()

            if (TextUtils.isEmpty(et.getText().toString()) | TextUtils.isEmpty(desc.getText().toString())) {
                Log.i("MEDICINE_INFO_EMPTY", "EMPTY_@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                Toast.makeText(MainActivity.this, "Please Enter Medicine Information To Proceed", Toast.LENGTH_LONG).show();
            } else {
                if (button_size > 0 & String.valueOf(ButtonArrayList) != "null") {
                    for (int i = 0; i < button_size; i++) {
                        Log.i("INSIDE_FOR_LOOP", "INSIDE FOR LOOP CONDITION");
                        try {
                            Log.i("EtText---------------", EtText.toLowerCase());
                            Log.i("B-LIST-ARRAY-ELEMENTS", ButtonArrayList.get(i).toLowerCase());
                            medicineDatabase = openOrCreateDatabase("medicine",0,null);
                            check_insert = medicineDatabase.rawQuery("SELECT * FROM medicine WHERE name='" + EtText.toLowerCase() + "' OR " +
                                            "info LIKE '%" + ButtonArrayList.get(i).toLowerCase().replaceAll("\\p{Punct}","") + "%'"
                                    , null);
                            Log.i("CHECK_INSERT", "FOR LOOP CHECK INSERT CURSOR");
                        } catch (Exception e) {
                            Log.i("MED_SELECT_EXCEPTION", e.getMessage());
                        }
                    }
                } else {
                    Log.i("BUTTON_LIST_SIZE", String.valueOf(buttonList.size()));
                    for (int i = 0; i < buttonList.size(); i++) {
                        Log.i("INSIDE_FOR_LOOP_222", "INSIDE FOR LOOP CONDITION");
//                        Log.i("ET_TEXT_GETTING",EtText);
                        if(EtText == null)
                        {
                            EtText = et.getText().toString().toLowerCase();
//                            Log.i("ET_TEXT_Lower_CASE222",EtText.toLowerCase());
                        }
                        if(description == null)
                        {
                            description = desc.getText().toString().toLowerCase();
//                            Log.i("ET_TEXT_Lower_CASE222",EtText.toLowerCase());
                        }



                        try {
                            medicineDatabase = openOrCreateDatabase("medicine",0,null);
                            if(!QRCode.isEmpty()) {
                                Log.i("INSIDE QRCODE","QR CODE IS NOT NULLLLL");
                                Log.i("ET_TEXT_Lower_CASE222",EtText.toLowerCase());
                                Log.i("BUTTON_List_Stirng_Lower_Case222",buttonList.get(i).toString().toLowerCase());
                                Log.i("QR_CODE_LOWER_CASE222",qr_code.toString().toLowerCase());
                                check_insert = medicineDatabase.rawQuery("SELECT * FROM medicine WHERE name='" + EtText.toLowerCase() + "' OR " +
                                                "info LIKE '%" + buttonList.get(i).getText().toString().toLowerCase() + "%'" +
                                                "OR qrcode='" + qr_code.toString().toLowerCase() + "'"
                                        , null);
                            }
                            else {
                                Log.i("INSIDE QRCODE","QR CODE IS NULLLLL");
                                Log.i("ET_TEXT_Lower_CASE222",EtText.toLowerCase());
                                Log.i("BUTTON_List_Stirng_Lower_Case222",buttonList.get(i).toString().toLowerCase());
                                check_insert = medicineDatabase.rawQuery("SELECT * FROM medicine WHERE name='" + EtText.toLowerCase() + "' OR " +
                                                "info LIKE '%" + buttonList.get(i).getText().toString().toLowerCase() + "%'"
                                        , null);
                            }
                        } catch (Exception e) {
                            Log.i("MED_SELECT_EXCEPTION-22", e.getMessage());
                        }
                    }
                }
//                Log.i("ERROR_EEEEEEEEEE", "ERROR");
                Log.i("GETCOUNT", String.valueOf(check_insert.getCount()));
                    if (check_insert.getCount() > 0) {
                        sqLiteDatabase2 = openOrCreateDatabase("alarm", 0, null);
                        try {
                            Cursor cursor1 = sqLiteDatabase2.rawQuery("SELECT *from alarm ORDER BY id DESC LIMIT 1", null);
                            cursor1.moveToFirst();
                            while (!cursor1.isAfterLast()) {
//                                Log.i("ALARM-ID-()()()()",cursor1.getString(0));
//                                Log.i("HOUR_()()()()",cursor1.getString(1));
//                                Log.i("MINUTE-()()()()",cursor1.getString(2));
                                sqLiteDatabase2.execSQL("DELETE FROM alarm WHERE id='" + cursor1.getString(0) + "'");
                                cursor1.moveToNext();
                            }
                        } catch (Exception e) {
                            Log.i("DELETING EXCEPTION", e.getMessage());
                        }
                        Toast.makeText(this, "MEDICINE Record Already Exists", Toast.LENGTH_SHORT).show();
                        et.setText("");
                        desc.setText("");
                        qr_code.setText("");
                        iv.setImageURI(Uri.parse(""));
                        linearLayout.setVisibility(View.GONE);
                        listView.setVisibility(View.GONE);
                        buttonList.clear();

                        try {
                            check_insert.moveToFirst();
                            while (!check_insert.isAfterLast()) {
                                Log.i("MEDICINE_NAME-111111111", check_insert.getString(1));
                                Log.i("MEDICINE_INFO-222222222", check_insert.getString(2));
                                check_insert.moveToNext();
                            }
                        } catch (Exception e) {
                            Log.i("MED_ELEMENTS_EXCEPTION", e.getMessage());
                        }

                    }

                 else {

                     Log.i("QR CODE GET ****",QRCode);

//                     Log.i("STRINGB _ GET -####",stringb.toLowerCase());
                     if(QRCode == null)
                     {
                         QRCode = qr_code.getText().toString();
                         Log.i("WITHOUT GET QRCODE",QRCode.toLowerCase());
                     }
                     if(stringb == null)
                     {
                         stringb = stringBuilder.toString().toLowerCase();
                         Log.i("STRINGB WITHOUT GET",stringb.toLowerCase());
                     }
                     Log.i("QR_CODE_GETTING",QRCode.toString());
                     if(QRCode.isEmpty()) {
                        QRCode = "DUMMY QR_CODE";
                     }
                         medicineDatabase = openOrCreateDatabase("medicine",0,null);
                         Log.i("QR_CODe","QR CODE IS NOT NULLLLLLLLLLLLLLLLLLLLLLLLLLLLL ");
                         String insert = "INSERT INTO medicine (name,info,description,qrcode) VALUES (?,?,?,?)";
                         SQLiteStatement sqLiteStatement = medicineDatabase.compileStatement(insert);
                         sqLiteStatement.bindString(1, EtText.toLowerCase().replaceAll("\\p{Punct}", ""));
                         sqLiteStatement.bindString(2, stringb.toLowerCase());
                         sqLiteStatement.bindString(3, description.toLowerCase());
                         sqLiteStatement.bindString(4, QRCode.toLowerCase());
                         sqLiteStatement.execute();

                    //cursor = medicineDatabase.rawQuery("SELECT * From medicine", null);
//        int NameIndex = cursor.getColumnIndex("name");
//        int InfoIndex = cursor.getColumnIndex("info");
//        int DescIndex = cursor.getColumnIndex("description");

                    //cursor = sqLiteDatabase.rawQuery("SELECT * From medicine WHERE name = '"+et.getText()+"'",null);

//
                    medicineDatabase = openOrCreateDatabase("medicine",0,null);
                    cursor = medicineDatabase.rawQuery("SELECT * from medicine "
                , null);

                    cursor.moveToFirst();

                    if(!QRCode.isEmpty()) {
                        while (!cursor.isAfterLast()) {
                            Log.i("ID", String.valueOf(cursor.getString(0)));
                            Log.i("NAME", String.valueOf(cursor.getString(1)));
                            Log.i("INFO", String.valueOf(cursor.getString(2)));
                            Log.i("DESC", String.valueOf(cursor.getString(3)));

                            Log.i("QR", String.valueOf(cursor.getString(4)));

                            cursor.moveToNext();
                        }
                    }
                    else
                    {
                        while (!cursor.isAfterLast()) {
                            Log.i("ID", String.valueOf(cursor.getString(0)));
                            Log.i("NAME", String.valueOf(cursor.getString(1)));
                            Log.i("INFO", String.valueOf(cursor.getString(2)));
                            Log.i("DESC", String.valueOf(cursor.getString(3)));

                            cursor.moveToNext();
                        }
                    }

//        cursor.moveToFirst();
//
//        while (!cursor.isAfterLast())
//        {
//            Log.i("NAME", String.valueOf(cursor.getString(1)));
//            Log.i("INFO",String.valueOf(cursor.getString(2)));
//            Log.i("DESC",String.valueOf(cursor.getString(3)));
//            cursor.moveToNext();
//        }
                    et.setText("");
                    desc.setText("");
                    iv.setImageURI(Uri.parse(""));
                    linearLayout.setVisibility(View.GONE);
                    buttonList.clear();

                    Intent intent = new Intent(MainActivity.this, AlarmList.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        }
        else if(GetName != null & GetDesc != null)
        {
            //Log.i("GET_BUTTON_ARRAYSIZE", String.valueOf(ButtonArrayList.size()));
            //buttonList.get(i).getText().toString().toLowerCase()

            if (TextUtils.isEmpty(et.getText().toString()) | TextUtils.isEmpty(desc.getText().toString())) {
                Log.i("MEDICINE_INFO_EMPTY", "EMPTY_@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                Toast.makeText(MainActivity.this, "Please Enter Medicine Information To Proceed", Toast.LENGTH_LONG).show();
            } else {
                        Log.i("INSIDE_FOR_LOOP_222", "INSIDE FOR LOOP CONDITION");
                        try {
                            check_insert = medicineDatabase.rawQuery("SELECT * FROM medicine WHERE name='" + EtText.toLowerCase() + "' OR " +
                                            "qrcode='" + qr_code.toString().toLowerCase() + "'"
                                    , null);
                            Log.i("CHECK_INSERT-2222", "FOR LOOP CHECK INSERT CURSOR");
                        } catch (Exception e) {
                            Log.i("MED_SELECT_EXCEPTION-22", e.getMessage());
                        }
                Log.i("ERROR_EEEEEEEEEE", "ERROR");
                //Log.i("GETCOUNT", String.valueOf(check_insert.getCount()));
                if (check_insert.getCount() > 0) {
                    sqLiteDatabase2 = openOrCreateDatabase("alarm", 0, null);
                    try {
                        Cursor cursor1 = sqLiteDatabase2.rawQuery("SELECT *from alarm ORDER BY id DESC LIMIT 1", null);
                        cursor1.moveToFirst();
                        while (!cursor1.isAfterLast()) {
//                                Log.i("ALARM-ID-()()()()",cursor1.getString(0));
//                                Log.i("HOUR_()()()()",cursor1.getString(1));
//                                Log.i("MINUTE-()()()()",cursor1.getString(2));
                            sqLiteDatabase2.execSQL("DELETE FROM alarm WHERE id='" + cursor1.getString(0) + "'");
                            cursor1.moveToNext();
                        }
                    } catch (Exception e) {
                        Log.i("DELETING EXCEPTION", e.getMessage());
                    }
                    Toast.makeText(this, "MEDICINE Record Already Exists", Toast.LENGTH_SHORT).show();
                    et.setText("");
                    desc.setText("");
                    iv.setImageURI(Uri.parse(""));
                    linearLayout.setVisibility(View.GONE);
                    listView.setVisibility(View.GONE);
                    buttonList.clear();

                    try {
                        check_insert.moveToFirst();
                        while (!check_insert.isAfterLast()) {
                            Log.i("MEDICINE_NAME-111111111", check_insert.getString(1));
                            Log.i("MEDICINE_INFO-222222222", check_insert.getString(2));
                            check_insert.moveToNext();
                        }
                    } catch (Exception e) {
                        Log.i("MED_ELEMENTS_EXCEPTION", e.getMessage());
                    }

                }

                else {

                    Log.i("QR CODE GET ****",QRCode+ 0000);
//                    Log.i("STRINGB _ GET -####",stringb.toLowerCase());
                    if(QRCode.trim().length() == 0)
                    {
                        QRCode = qr_code.getText().toString();
                        Log.i("WITHOUT GET QRCODE",QRCode.toLowerCase());
                    }

                    String insert = "INSERT INTO medicine (name,description,qrcode) VALUES (?,?,?)";
                    SQLiteStatement sqLiteStatement = medicineDatabase.compileStatement(insert);
                    sqLiteStatement.bindString(1, EtText.toLowerCase().replaceAll("\\p{Punct}",""));
                    sqLiteStatement.bindString(2, description.toLowerCase());
                    sqLiteStatement.bindString(3,QRCode.toLowerCase());
                    sqLiteStatement.execute();

                    //cursor = medicineDatabase.rawQuery("SELECT * From medicine", null);
//        int NameIndex = cursor.getColumnIndex("name");
//        int InfoIndex = cursor.getColumnIndex("info");
//        int DescIndex = cursor.getColumnIndex("description");

                    //cursor = sqLiteDatabase.rawQuery("SELECT * From medicine WHERE name = '"+et.getText()+"'",null);

//
                    medicineDatabase = openOrCreateDatabase("medicine",0,null);
                    cursor = medicineDatabase.rawQuery("SELECT * from medicine "
                            , null);

                    cursor.moveToFirst();

                    while (!cursor.isAfterLast()) {
                        Log.i("ID", String.valueOf(cursor.getString(0)));
                        Log.i("NAME", String.valueOf(cursor.getString(1)));
                        Log.i("DESC", String.valueOf(cursor.getString(2)));
                        Log.i("QR", String.valueOf(cursor.getString(3)));

                        cursor.moveToNext();
                    }

//        cursor.moveToFirst();
//
//        while (!cursor.isAfterLast())
//        {
//            Log.i("NAME", String.valueOf(cursor.getString(1)));
//            Log.i("INFO",String.valueOf(cursor.getString(2)));
//            Log.i("DESC",String.valueOf(cursor.getString(3)));
//            cursor.moveToNext();
//        }
                    et.setText("");
                    desc.setText("");
                    iv.setImageURI(Uri.parse(""));
                    linearLayout.setVisibility(View.GONE);
                    buttonList.clear();

                    Intent intent = new Intent(MainActivity.this, AlarmList.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

        }
        else {
            Toast.makeText(this, "Please Capture/Choose Image", Toast.LENGTH_LONG).show();
        }
    }

    public void AddMedicineAlarm(View view) {


        description = desc.getText().toString();

        EtText = et.getText().toString().replaceAll("\\p{Punct}","");

        QRCode = qr_code.getText().toString();

//        Log.i("STRING BUILDER _INTENT",stringBuilder.toString().toLowerCase());

        Log.i("ETTEXT_ETETET", EtText);
        if (EtText == null) {
            Log.i("Title_#############", "TITLE NULL");
        }
        if (description == null) {
            Log.i("DESC_############", "NULL");
        }
        if (imgUri == null) {
            Log.i("IMG_#############", "NULL");
        }
        if (EtText != null & description != null & imgUri != null) {
            Log.i("Title_!!!!!!!!@@@@@", EtText.toString());
            Log.i("DESC_!!!!!!!!@@@@@", description);
            Log.i("IMG_!!!!!!!!@@@@@", imgUri.toString());
            Intent intent = new Intent(MainActivity.this, Alarm.class);
            if (EtText == null) {
                Toast.makeText(MainActivity.this, "Please Add Medicine Name", Toast.LENGTH_LONG).show();
            } else {
                intent.putExtra("name", EtText.toString());
                intent.putExtra("description", description);
                intent.putExtra("imageUri", imgUri);
                intent.putExtra("qrcode",qr_code.getText().toString());
                if(String.valueOf(stringBuilder) != "null") {
                    Log.i("STRING_BUILDER",String.valueOf((stringBuilder)));
                    intent.putExtra("stringB", String.valueOf(stringBuilder).toLowerCase());
                    intent.putExtra("ButtonSize", buttonList.size());
                    intent.putStringArrayListExtra("ButtonList", ButtonTextList);
                }
                else if(button_size > 0 & ButtonArrayList != null)
                {
                    Log.i("STRING_BBBB --------",String.valueOf(stringb));
                    intent.putExtra("stringB",stringb);
                    intent.putExtra("ButtonSize",button_size);
                    intent.putStringArrayListExtra("ButtonList",ButtonArrayList);
                }
                add_med = 1;
                startActivity(intent);
            }
        }
        else  if(EtText != null && description != null)
        {
            Intent intent = new Intent(MainActivity.this, Alarm.class);
            intent.putExtra("name", EtText.toString());
            intent.putExtra("description", description);
            intent.putExtra("qrcode",qr_code.getText().toString());

            add_med = 1;
            startActivity(intent);
        }
        else {
            Toast.makeText(MainActivity.this, "null passed", Toast.LENGTH_LONG).show();
        }
    }
}