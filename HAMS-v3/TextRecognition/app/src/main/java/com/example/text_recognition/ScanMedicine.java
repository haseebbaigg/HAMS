package com.example.text_recognition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.security.auth.Destroyable;

public class ScanMedicine extends AppCompatActivity {

    public static int scan_activity = 0;

    EditText nameText;
    EditText descText;
    ImageView imageView;

    String Med_name = null;
    String Med_desc = null;
    String Med_img = null;

    String GetName = null;
    String GetDesc = null;
    String GetImg = null;

    private static final int CAMERA_CODE = 200;
    private static final int GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission [];
    SQLiteDatabase sqLiteDatabase;
    SQLiteDatabase alarmDatabase;

    Cursor cursor;

    ListView listView;

    Uri imageUri;

    String Id[], Hour[], Min[], Title[], Mon[], Tues[], Wed[], Thurs[], Fri[], Sat[], Sun[], Status[];
    ArrayList<String> arrayList1 = new ArrayList<>();


    DrawerLayout drawerLayout;

    Boolean str_con = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_medicine);

        //getSupportActionBar().hide();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("Click Button to Insert Image");
        getSupportActionBar().hide();

        drawerLayout = findViewById(R.id.drawer_layout);

        String demo = "softin°\n" +
                "    tablets\n" +
                "    loratadine 10 mg";

        String a = demo.replaceAll("\n\\s\\s","");
        Log.i("DEMOOOOOO",a);

        Boolean t = a.contains("softin");
        Log.i("GOT SOFTIN TEXT ", String.valueOf(t));

        nameText = findViewById(R.id.med_name);
        descText = findViewById(R.id.med_desc);
        imageView = findViewById(R.id.image);


        listView = findViewById(R.id.listView);
        listView.setAdapter(null);

        cameraPermission = new String[]{Manifest.permission.CAMERA};

        sqLiteDatabase = this.openOrCreateDatabase("medicine",0,null);
        Intent intent1 = getIntent();
        GetName = intent1.getStringExtra("PassName");
        GetDesc = intent1.getStringExtra("PassDesc");
        GetImg = intent1.getStringExtra("PassImg");

        if (GetName != null) {
            nameText.setText(GetName.toLowerCase());
            Med_name= GetName.toLowerCase().toString();
        }
        if (GetDesc != null) {
            descText.setText(GetDesc.toLowerCase());
            Med_desc = descText.getText().toString();
        }
        if (GetImg != null) {
            Log.i("GET_IMG", GetImg);
            imageView.setImageURI(Uri.parse(GetImg));
            Med_img = GetImg;
        }
        if (GetName != null) {
            Log.i("GET_NAME_INTENT",GetName);
            Log.i("MED_NAME_STRING",Med_name);
            alarmDatabase = openOrCreateDatabase("alarm", 0, null);

            Cursor cursor = alarmDatabase.rawQuery("SELECT * from alarm WHERE title='" + GetName + "' ORDER BY id", null);

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

                if (Integer.parseInt(hour) == 12) {
                    Converted_Hour = Integer.parseInt(hour);
                    Log.i("HOUR==12", String.valueOf(Converted_Hour));
                    stringBuilder.append(Converted_Hour);
                }
                if (Integer.parseInt(hour) > 12) {
                    Converted_Hour = Integer.parseInt(hour) - 12;
                    Log.i("HOUR>12", String.valueOf(Converted_Hour));
                    stringBuilder.append(Converted_Hour);
                } else if (Integer.parseInt(hour) < 12) {
                    if (Integer.parseInt(hour) == 0) {
                        Converted_Hour = Integer.parseInt(hour) + 12;
                        Log.i("HOUR==0", String.valueOf(Converted_Hour));
                        stringBuilder.append(Converted_Hour);
                    }
                    else {
                        Log.i("HOUR<12", String.valueOf(cursor.getString(1)));
                        stringBuilder.append(cursor.getString(1));
                    }
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
                if (Integer.parseInt(hour) < 12) {
                    stringBuilder.append("AM");
                } else if (Integer.parseInt(hour) >= 12) {
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
                    Intent intent2 = new Intent(ScanMedicine.this, Alarm.class);


                    if (Med_name == null) {
                        Log.i("Title_#############", "TITLE _ NULL");
                    }
                    if (Med_desc == null) {
                        Log.i("DESC_############", "DESC _ NULL");
                    }
                    if (Med_img== null) {
                        Log.i("IMG_#############", "IMG _ NULL");
                    }
                    if (Med_name != null & Med_desc != null & Med_img != null) {

                        scan_activity = 1;

                        Log.i("Title_!!!!!!!!@@@@@", Med_name.toString());
                        Log.i("DESC_!!!!!!!!@@@@@", Med_desc);
                        Log.i("IMG_!!!!!!!!@@@@@", Med_img.toString());


                        intent2.putExtra("name", Med_name.toString());
                        intent2.putExtra("description", Med_desc);
                        intent2.putExtra("imageUri", Med_img);

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

        //----------------------------------------------------------------------------------------------------------


        //pickGallery();
        if(scan_activity == 0) {
            showImageImportDialog();
        }
        scan_activity = 0;


        //-------------------------------------------------------------------------------------------------------------

    }

    public void ClickOption(View view)
    {
        PopupMenu popupMenu = new PopupMenu(ScanMedicine.this, view);//View will be an anchor for PopupMenu
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

    private void showImageImportDialog() {
        String[] items = {"Camera","Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0)
                {
                    if(!checkCameraPermission())
                    {
                        requestCameraPermission();
                    }
                    else
                    {
                        getSupportActionBar().hide();
                        pickCamera();
                    }
                }
                if(which == 1)
                {
                    getSupportActionBar().hide();
                        pickGallery();
                }
            }
        });
        builder.create().show();
    }

    private void pickGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,GALLERY_CODE);
    }

    private void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"NEW PICTURE");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image To Text");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return result && result1;
    }


    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().hide();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_CODE:
                if(grantResults.length>0)
                {
                    boolean CameraAllowed = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean StorageAllowed = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(CameraAllowed && StorageAllowed)
                    {
                        pickCamera();
                    }
                    else
                    {
                        Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case GALLERY_CODE:
                if(grantResults.length>0) {
                    pickGallery();
                }
                    else {
                        Toast.makeText(this,"NO Gallery Denied",Toast.LENGTH_SHORT).show();
                    }
                }
        }

    @SuppressLint("LongLogTag")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Log.i("ACTIVITY_RESULT","HERE");
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_CODE) {
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
                Uri resultUri = result.getUri();
                //Log.i("REQUEST_CODE","OK");
                Med_img = resultUri.toString();
                imageView.setImageURI(resultUri);

                BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if (!recognizer.isOperational()) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                } else {



                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);

                    StringBuilder stringBuilder = new StringBuilder();

                    ArrayList<String> arrayList = new ArrayList<>();

                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItems = items.valueAt(i);
                        stringBuilder.append(myItems.getValue());
                        Log.i("TEXT_BLOCK", items.valueAt(i).getValue().toLowerCase());
                        Log.i("TEXT_BLOCK_LENGTH", String.valueOf(items.valueAt(i).getValue().toLowerCase().length()));
                        if (items.valueAt(i).getValue().toLowerCase().length() > 5) {
                            String value = items.valueAt(i).getValue().toLowerCase().replaceAll("\\p{Punct}", "");
                            arrayList.add(value);
                        }

                        stringBuilder.append(" ");
                    }
                    Log.i("String BUILDER : ", stringBuilder.toString());

                    Cursor cursor1 = sqLiteDatabase.rawQuery("SELECT * From medicine", null);
                    cursor1.moveToFirst();

                    while (!cursor1.isAfterLast()) {
                        Log.i("PIC_ID", String.valueOf(cursor1.getString(0)));
                        Log.i("NAME", String.valueOf(cursor1.getString(1)));
                        Log.i("INFO", String.valueOf(cursor1.getString(2)));
                        Log.i("DESC", String.valueOf(cursor1.getString(3)));
                        cursor1.moveToNext();
                    }

                    nameText.setText("");
                    descText.setText("");
                    int cursor_result = 0;

                    Cursor top_row;

                    String exceptional_name = null;
                    String exceptional_desc = null;
                    String matching_name = null;
                    for (int i = 0; i < arrayList.size(); i++) {
                        Log.i("ARRAYLIST", String.valueOf(arrayList.get(i)));
                        cursor = sqLiteDatabase.rawQuery("SELECT * FROM medicine WHERE info LIKE '%" + arrayList.get(i).toLowerCase() + "%' OR " +
                                        "name ='" + arrayList.get(i).toLowerCase() + "' "
                                , null);
//                        cursor = sqLiteDatabase.rawQuery("SELECT * FROM medicine WHERE name LIKE '%" + arrayList.get(i).toLowerCase() + "' OR " +
//                                        "info LIKE '%"+arrayList.get(i).toLowerCase()+"%' "
//                                , null);

                        top_row = sqLiteDatabase.rawQuery("SELECT * FROM medicine LIMIT 1", null);


                        if (top_row.moveToFirst()) {
                            //Log.i("TOP_ROW_PIC_ID",String.valueOf(top_row.getString(0)));
                            //Log.i("TOP_ROW_NAME", String.valueOf(top_row.getString(1)));
                            //Log.i("TOP_ROW_INFO",String.valueOf(top_row.getString(2)));
                            //Log.i("TOP_ROW_DESC",String.valueOf(top_row.getString(3)));
                            matching_name = String.valueOf(top_row.getString(1));
                        }

                        if (cursor.getCount() <= 0 && cursor_result == 0) {
                            Log.i("GET_COUNT", "000000000000000000000");
                            cursor_result = 0;
                        } else if (cursor.getCount() > 0) {
                            Log.i("GET_COUNT", "11111111111111111111111111");
                            Log.i("CURSOR_MOVE_FIRST_11111", String.valueOf(cursor.moveToFirst()));
                            Log.i("CURSOR_GET_STRING", cursor.getString(0));
                            cursor_result = 1;
                            exceptional_name = String.valueOf(cursor.getString(1));
                            exceptional_desc = String.valueOf(cursor.getString(3));
                        }
                        Log.i("CURSOR_MOVE_FIRST_22222", String.valueOf(cursor.moveToFirst()));
                    }
                    if (cursor_result == 1) {
                        Log.i("INSIDE_CURSOR_RESULT", "CCCCCCCCCCCCCCCCCCCC");
                        Log.i("CURSOR_MOVE_FIRST_33333", String.valueOf(cursor.moveToFirst()));
                        Log.i("EXCEPTIONAL", exceptional_name == matching_name ? "EQUAL" : "NOT EQUAL");
                        if (cursor.moveToFirst()) {
                            Log.i("CURSOR_MOVE_FIRST", "MFMFMFMFMFMFMFMFMFMFMF");
                            Log.i("ID", String.valueOf(cursor.getString(0)));
                            Log.i("NAME", String.valueOf(cursor.getString(1)));
                            Log.i("INFO", String.valueOf(cursor.getString(2)));
                            Log.i("DESC", String.valueOf(cursor.getString(3)));
                            nameText.setText(cursor.getString(1));
                            descText.setText(cursor.getString(3));
                        }
                        try {
//                            listView.setAdapter(null);
                            nameText.setText(exceptional_name);
                            descText.setText(exceptional_desc);
                            Log.i("ALARM_DB_REACHED__!!!!", "DB_ALARM");
                            Log.i("CURSOR_ALARM_DB", cursor.toString());
                            Log.i("MEDICINE_NAME_TEXT", nameText.getText().toString().toLowerCase());
                            alarmDatabase = openOrCreateDatabase("alarm", 0, null);
                            Cursor cursor2 = alarmDatabase.rawQuery("SELECT * from alarm WHERE title='" + nameText.getText().toString() + "' ORDER BY id", null);
                            Log.i("ALARM_DB_REACHED__!!!!", "DB_ALARM");
                            Log.i("CURSOR_ALARM_DB", cursor2.toString());
                            Log.i("CURSOR ALARM SIZE", String.valueOf(cursor2.getCount()));

                            if (cursor2.moveToFirst()) {
                                Log.i("CURCOR_CCCCCCC", cursor2.toString());
                                Id = new String[cursor2.getCount()];
                                Hour = new String[cursor2.getCount()];
                                Min = new String[cursor2.getCount()];
                                Title = new String[cursor2.getCount()];
                                Status = new String[cursor2.getCount()];
                                Mon = new String[cursor2.getCount()];
                                Tues = new String[cursor2.getCount()];
                                Wed = new String[cursor2.getCount()];
                                Thurs = new String[cursor2.getCount()];
                                Fri = new String[cursor2.getCount()];
                                Sat = new String[cursor2.getCount()];
                                Sun = new String[cursor2.getCount()];

                                Log.i("LENGTH", String.valueOf(Hour.length));
                                cursor2.moveToFirst();
                                StringBuilder stringBuilder1 = new StringBuilder();
                                arrayList1.clear();
                                while (!cursor2.isAfterLast()) {
                                    stringBuilder1.setLength(0);
                                    Log.i("ID", String.valueOf(cursor2.getString(0)));
                                    Log.i("Hour", String.valueOf(cursor2.getString(1)));
                                    //Log.i("Minnnnnnnnnnnn", cursor2.getString(2));
                                    if (cursor2.getString(2).length() > 1) {
                                        Log.i("Min", String.valueOf(cursor2.getString(2)));
                                    } else {
                                        Log.i("Min", 0 + cursor2.getString(2));
                                    }
                                    Log.i("Title", String.valueOf(cursor2.getString(3)));
                                    Log.i("M", String.valueOf(cursor2.getString(5)));
                                    Log.i("T", String.valueOf(cursor2.getString(6)));
                                    Log.i("W", String.valueOf(cursor2.getString(7)));
                                    Log.i("TH", String.valueOf(cursor2.getString(8)));
                                    Log.i("F", String.valueOf(cursor2.getString(9)));
                                    Log.i("S", String.valueOf(cursor2.getString(10)));
                                    Log.i("SUN", String.valueOf(cursor2.getString(11)));
                                    Log.i("SWITCH", String.valueOf(cursor2.getString(4)));

                                    String hour = cursor2.getString(1);
                                    Log.i("SIMPLE_HOUR", hour);
                                    int Converted_Hour = 0;
                                    if (Integer.parseInt(hour) == 12) {
                                        Log.i("HOUR == 12", String.valueOf(cursor2.getString(1)));
                                        stringBuilder1.append(cursor2.getString(1));
                                    }
                                    if (Integer.parseInt(hour) > 12) {
                                        Converted_Hour = Integer.parseInt(hour) - 12;
                                        Log.i("HOUR>12", String.valueOf(Converted_Hour));
                                        stringBuilder1.append(Converted_Hour);
                                    } else if (Integer.parseInt(hour) < 12) {
                                        if (Integer.parseInt(hour) == 0) {
                                            Converted_Hour = Integer.parseInt(hour) + 12;
                                            Log.i("HOUR==0", String.valueOf(Converted_Hour));
                                            stringBuilder1.append(Converted_Hour);
                                        } else {
                                            Log.i("HOUR<12", String.valueOf(cursor2.getString(1)));
                                            stringBuilder1.append(cursor2.getString(1));
                                        }
                                    }
                                    //stringBuilder.append(cursor.getString(1));
                                    stringBuilder1.append(" : ");
                                    Log.i("minutes-length", String.valueOf(cursor2.getString(2).length()));
                                    if (cursor2.getString(2).length() > 1) {
                                        Log.i("MINUTES>1", String.valueOf(cursor2.getString(2)));
                                        stringBuilder1.append(cursor2.getString(2));
                                    } else {
                                        String min = cursor2.getString(2);
                                        String add_min = "0" + min;
                                        Log.i("MINUTES<1", String.valueOf(add_min));
                                        stringBuilder1.append(add_min);
                                    }
                                    stringBuilder1.append(" ");
                                    if (Integer.parseInt(hour) < 12) {
                                        stringBuilder1.append("AM");
                                    } else if (Integer.parseInt(hour) >= 12) {
                                        stringBuilder1.append("PM");
                                    }
                                    stringBuilder1.append("\n");
                                    stringBuilder1.append(cursor2.getString(3));
                                    Log.i("STRING_BUILDER", stringBuilder1.toString());

                                    arrayList1.add(stringBuilder1.toString());

                                    cursor2.moveToNext();
                                }


                                cursor2.moveToFirst();
                                for (int i = 0; i < Hour.length; i++) {
                                    Id[i] = String.valueOf(cursor2.getString(0));
                                    Hour[i] = String.valueOf(cursor2.getString(1));
                                    Log.i("hour[i]-loop", Hour[i]);
                                    Min[i] = String.valueOf(cursor2.getString(2));
                                    Log.i("Minute[i]-loop", Min[i]);
                                    Title[i] = String.valueOf(cursor2.getString(3));
                                    Log.i("Title[i]-loop", Title[i]);
                                    Status[i] = String.valueOf(cursor2.getString(4));
                                    Mon[i] = String.valueOf(cursor2.getString(5));
                                    Tues[i] = String.valueOf(cursor2.getString(6));
                                    Wed[i] = String.valueOf(cursor2.getString(7));
                                    Thurs[i] = String.valueOf(cursor2.getString(8));
                                    Fri[i] = String.valueOf(cursor2.getString(9));
                                    Sat[i] = String.valueOf(cursor2.getString(10));
                                    Log.i("Sat[i]-loop", Sat[i]);
                                    Sun[i] = String.valueOf(cursor2.getString(11));
                                    cursor2.moveToNext();
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
                                        Intent intent2 = new Intent(ScanMedicine.this, Alarm.class);
                                        Med_name = nameText.getText().toString().toLowerCase();
                                        Med_desc = descText.getText().toString().toLowerCase();
                                        if (Med_name != null & Med_desc != null & Med_img != null) {

                                            scan_activity = 1;
                                            intent2.putExtra("name", Med_name.toString());
                                            intent2.putExtra("description", Med_desc);
                                            intent2.putExtra("imageUri", Med_img);

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


                                            Log.i("Title_!!!!!!!!@@@@@", Med_name.toString());
                                            Log.i("DESC_!!!!!!!!@@@@@", Med_desc);
                                            Log.i("IMG_!!!!!!!!@@@@@", Med_img.toString());

                                            startActivity(intent2);
                                        }
                                    }
                                });
                            }
                            //}
                        } catch (Exception e) {
                            Log.i("MEDICINE_EXCEPTION", e.getMessage());
                        }

//                        else if (exceptional_name != matching_name)
//                        {
//                            Log.i("EXCEPTIONAL_CASE-NAME",exceptional_name);
//                            Log.i("EXCEPTIONAL_CASE-MATCH",matching_name);
//                            nameText.setText(exceptional_name);
//                            descText.setText(exceptional_desc);
//                        }
                    }
                    //---------------------------------------------------------------------------------------------
                    else if (cursor_result == 0) {
                        arrayList1.clear();
                        Cursor cursor3 = sqLiteDatabase.rawQuery("SELECT * from medicine", null);
                        cursor3.moveToFirst();
                        while (!cursor3.isAfterLast()) {

                            for (int i = 0; i < arrayList.size(); i++) {
                                Log.i("ARRAYLIST-ELEMETNS -22222", arrayList.get(i));
                                String arr = arrayList.get(i).toString();
                                String array_edited = arr.replaceAll("\n\\s\\s","");
                                Log.i("ARRAY WITHOUT NEXT LINE : ",array_edited);
                                Log.i("GOT CURSOR AT 1",cursor3.getString(1));
                                str_con = array_edited.contains(String.valueOf(cursor3.getString(1)));
                                Log.i("STR_CONNNNNNNNNNNNNN", String.valueOf(str_con));
                                if (str_con) {
                                    nameText.setText(cursor3.getString(1));
                                    descText.setText(cursor3.getString(3));
                                }

                            }
                            cursor3.moveToNext();
                        }
                        Log.i("MEDICINE_NAME_TEXT", nameText.getText().toString().toLowerCase());
                        alarmDatabase = openOrCreateDatabase("alarm", 0, null);
                        Cursor cursor2 = alarmDatabase.rawQuery("SELECT * from alarm WHERE title='" + nameText.getText().toString() + "' ORDER BY id", null);
                        Log.i("ALARM_DB_REACHED 2222222", "DB_ALARM");
                        Log.i("CURSOR_ALARM_DB 22222222", cursor2.toString());
                        Log.i("CURSOR ALARM SIZE 2222222", String.valueOf(cursor2.getCount()));

                        if (cursor2.moveToFirst()) {
                            Log.i("CURCOR_CCCCCCC 2222222", cursor2.toString());
                            Id = new String[cursor2.getCount()];
                            Hour = new String[cursor2.getCount()];
                            Min = new String[cursor2.getCount()];
                            Title = new String[cursor2.getCount()];
                            Status = new String[cursor2.getCount()];
                            Mon = new String[cursor2.getCount()];
                            Tues = new String[cursor2.getCount()];
                            Wed = new String[cursor2.getCount()];
                            Thurs = new String[cursor2.getCount()];
                            Fri = new String[cursor2.getCount()];
                            Sat = new String[cursor2.getCount()];
                            Sun = new String[cursor2.getCount()];

                            Log.i("LENGTH", String.valueOf(Hour.length));
                            cursor2.moveToFirst();
                            StringBuilder stringBuilder1 = new StringBuilder();
                            arrayList1.clear();
                            while (!cursor2.isAfterLast()) {
                                stringBuilder1.setLength(0);
                                Log.i("ID 222", String.valueOf(cursor2.getString(0)));
                                Log.i("Hour 222", String.valueOf(cursor2.getString(1)));
                                //Log.i("Minnnnnnnnnnnn", cursor2.getString(2));
                                if (cursor2.getString(2).length() > 1) {
                                    Log.i("Min 2222", String.valueOf(cursor2.getString(2)));
                                } else {
                                    Log.i("Min 2222", 0 + cursor2.getString(2));
                                }
                                Log.i("Title 2222", String.valueOf(cursor2.getString(3)));
                                Log.i("M 222", String.valueOf(cursor2.getString(5)));
                                Log.i("T 222", String.valueOf(cursor2.getString(6)));
                                Log.i("W 222", String.valueOf(cursor2.getString(7)));
                                Log.i("TH 222", String.valueOf(cursor2.getString(8)));
                                Log.i("F 222", String.valueOf(cursor2.getString(9)));
                                Log.i("S 222", String.valueOf(cursor2.getString(10)));
                                Log.i("SUN 222", String.valueOf(cursor2.getString(11)));
                                Log.i("SWITCH 222", String.valueOf(cursor2.getString(4)));

                                String hour = cursor2.getString(1);
                                Log.i("SIMPLE_HOUR 2222", hour);
                                int Converted_Hour = 0;
                                if (Integer.parseInt(hour) == 12) {
                                    Log.i("HOUR == 12 (2222)", String.valueOf(cursor2.getString(1)));
                                    stringBuilder1.append(cursor2.getString(1));
                                }
                                if (Integer.parseInt(hour) > 12) {
                                    Converted_Hour = Integer.parseInt(hour) - 12;
                                    Log.i("HOUR>12 (2222)", String.valueOf(Converted_Hour));
                                    stringBuilder1.append(Converted_Hour);
                                } else if (Integer.parseInt(hour) < 12) {
                                    if (Integer.parseInt(hour) == 0) {
                                        Converted_Hour = Integer.parseInt(hour) + 12;
                                        Log.i("HOUR==0 (2222)", String.valueOf(Converted_Hour));
                                        stringBuilder1.append(Converted_Hour);
                                    } else {
                                        Log.i("HOUR<12 (2222)", String.valueOf(cursor2.getString(1)));
                                        stringBuilder1.append(cursor2.getString(1));
                                    }
                                }
                                //stringBuilder.append(cursor.getString(1));
                                stringBuilder1.append(" : ");
                                Log.i("minutes-length (2222)", String.valueOf(cursor2.getString(2).length()));
                                if (cursor2.getString(2).length() > 1) {
                                    Log.i("MINUTES>1 (2222)", String.valueOf(cursor2.getString(2)));
                                    stringBuilder1.append(cursor2.getString(2));
                                } else {
                                    String min = cursor2.getString(2);
                                    String add_min = "0" + min;
                                    Log.i("MINUTES<1 (2222)", String.valueOf(add_min));
                                    stringBuilder1.append(add_min);
                                }
                                stringBuilder1.append(" ");
                                if (Integer.parseInt(hour) < 12) {
                                    stringBuilder1.append("AM");
                                } else if (Integer.parseInt(hour) >= 12) {
                                    stringBuilder1.append("PM");
                                }
                                stringBuilder1.append("\n");
                                stringBuilder1.append(cursor2.getString(3));
                                Log.i("STRING_BUILDER (2222)", stringBuilder1.toString());

                                arrayList1.add(stringBuilder1.toString());

                                cursor2.moveToNext();
                            }


                            cursor2.moveToFirst();
                            for (int i = 0; i < Hour.length; i++) {
                                Id[i] = String.valueOf(cursor2.getString(0));
                                Hour[i] = String.valueOf(cursor2.getString(1));
                                Log.i("hour[i]-loop (2222)", Hour[i]);
                                Min[i] = String.valueOf(cursor2.getString(2));
                                Log.i("Minute[i]-loop (2222)", Min[i]);
                                Title[i] = String.valueOf(cursor2.getString(3));
                                Log.i("Title[i]-loop (2222)", Title[i]);
                                Status[i] = String.valueOf(cursor2.getString(4));
                                Mon[i] = String.valueOf(cursor2.getString(5));
                                Tues[i] = String.valueOf(cursor2.getString(6));
                                Wed[i] = String.valueOf(cursor2.getString(7));
                                Thurs[i] = String.valueOf(cursor2.getString(8));
                                Fri[i] = String.valueOf(cursor2.getString(9));
                                Sat[i] = String.valueOf(cursor2.getString(10));
                                Log.i("Sat[i]-loop", Sat[i]);
                                Sun[i] = String.valueOf(cursor2.getString(11));
                                cursor2.moveToNext();
                            }
                            for (int i = 0; i < arrayList1.size(); i++) {
                                Log.i("ARRAY_LIST_ELEMENTS (2222)", arrayList1.get(i));
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
                                @SuppressLint("LongLogTag")
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    //Intent intent = new Intent(AlarmList.this,MainActivity.class);
                                    //Toast.makeText(getApplicationContext(), "Hour :" + hour[position] + " Min :" + min[position] + " FRI : " + fri[position], Toast.LENGTH_LONG).show();
                                    Intent intent2 = new Intent(ScanMedicine.this, Alarm.class);
                                    Med_name = nameText.getText().toString().toLowerCase();
                                    Med_desc = descText.getText().toString().toLowerCase();
                                    if (Med_name != null & Med_desc != null & Med_img != null) {

                                        scan_activity = 1;
                                        intent2.putExtra("name", Med_name.toString());
                                        intent2.putExtra("description", Med_desc);
                                        intent2.putExtra("imageUri", Med_img);

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


                                        Log.i("Title_!!!!!!!!@@@@@ (2222)", Med_name.toString());
                                        Log.i("DESC_!!!!!!!!@@@@@ (2222)", Med_desc);
                                        Log.i("IMG_!!!!!!!!@@@@@ (2222)", Med_img.toString());

                                        startActivity(intent2);
                                    }
                                }
                            });
                        }
                        if(str_con ==  false)
                        {
                            Toast.makeText(this, "Medicine Record Not Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            //-------------------------------------------------------------------------------------------------------
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception exception = result.getError();
                Toast.makeText(this, "CROP IMAGE ERROR" + exception, Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void AddMedicineAlarm(View view)
    {
        scan_activity = 1;
        Med_desc = descText.getText().toString();

        Med_name = nameText.getText().toString();
        Log.i("ETTEXT_ETETET", Med_name);
        if (Med_name == null) {
            Log.i("Title_#############", "TITLE NULL");
        }
        if (Med_desc == null) {
            Log.i("DESC_############", "NULL");
        }
        if (Med_img == null) {
            Log.i("IMG_#############", "NULL");
        }
        if (Med_name != null & Med_desc != null & Med_img != null) {
            Log.i("Title_!!!!!!!!@@@@@", Med_name.toString());
            Log.i("DESC_!!!!!!!!@@@@@", Med_desc);
            Log.i("IMG_!!!!!!!!@@@@@", Med_img.toString());
            Intent intent = new Intent(ScanMedicine.this, Alarm.class);
            if (Med_name == null) {
                Toast.makeText(ScanMedicine.this, "Please Add Medicine Name", Toast.LENGTH_LONG).show();
            } else {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add("Nothing");
                intent.putExtra("name", Med_name.toString());
                intent.putExtra("description", Med_desc);
                intent.putExtra("imageUri", Med_img);
                intent.putExtra("ButtonSize",0);
                intent.putStringArrayListExtra("ButtonList",arrayList);
                startActivity(intent);
            }
        } else {
            Toast.makeText(ScanMedicine.this, "null passed", Toast.LENGTH_LONG).show();
        }
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
        redirectActivity(this,Alarm.class);
    }
    public void ClickAddMedicine(View view)
    {
        redirectActivity(this,MainActivity.class);
    }

    public void ClickScanMedicine(View view)
    {
        recreate();
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
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
}