package com.example.text_recognition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class DoctorActivity extends AppCompatActivity {

    Button button,button1;
    String doc;

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        drawerLayout = findViewById(R.id.drawer_layout);

        button = findViewById(R.id.DocregisterBtn);
        button1 = findViewById(R.id.DocloginBtn);

        button = findViewById(R.id.DocregisterBtn);
        button1 = findViewById(R.id.DocloginBtn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DoctorActivity.this,RegisterUser.class);
                intent.putExtra("DoctorRegister","DoctorRegistration");
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorActivity.this,LoginActivity.class);
                intent.putExtra("DoctorLog","DoctorLogin");
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
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
        redirectActivity(this,ScanMedicine.class);
    }
    public void ClickQRScan(View view)
    {
        redirectActivity(this,ScanQR.class);
    }

    public void ClickMap(View view)
    {
    }

    public void ClickChat(View view)
    {
        recreate();
    }

//
//    public void ClickOption(View view){
//        Log.i("OPEN","OPEN OPTIONS MENU");
//        PopupMenu popupMenu = new PopupMenu(StartActivity.this, view);//View will be an anchor for PopupMenu
//        popupMenu.inflate(R.menu.chat_menu);
//        Menu menu = popupMenu.getMenu();
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                int id = item.getItemId();
//                if(item.getItemId() ==  R.id.logout)
//                {
//                    FirebaseAuth.getInstance().signOut();
//                   // userRef.child("online").setValue(ServerValue.TIMESTAMP);chat
//
//                    Intent intent = new Intent(StartActivity.this,StartActivity.class);
//                    startActivity(intent);
//                }
//                if(item.getItemId() == R.id.account_setting)
//                {
//                    Intent intent = new Intent(StartActivity.this,AccountSetting.class);
//                    startActivity(intent);
//                }
//                if(item.getItemId() == R.id.users)
//                {
//                    Intent intent = new Intent(StartActivity.this,UsersActivity.class);
//                    startActivity(intent);
//                }
//                if(item.getItemId() ==  R.id.doctors)
//                {
//                    Intent intent = new Intent(StartActivity.this,AllDoctorsActivity.class);
//                    startActivity(intent);
//                }
//                if(item.getItemId() == R.id.friends)
//                {
//                    Intent intent = new Intent(StartActivity.this,FriendsActivity.class);
//                    startActivity(intent);
//                }
//                return true;
//            }
//        });
//        popupMenu.show();
//    }

    public static void redirectActivity(Activity activity, Class aclass) {
        Intent intent = new Intent(activity,aclass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
        Log.d("lifecycle","onPause invoked");
    }
}