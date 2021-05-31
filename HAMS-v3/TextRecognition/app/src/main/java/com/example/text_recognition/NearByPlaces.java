package com.example.text_recognition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class NearByPlaces<synchorized> extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    Location lastLocation;
    Marker currentUserMarker;

    static final int requestUserLocationCode = 99;
    int ProximityRadius = 1500;

    double latitude,longtitude;

    EditText search;
    ImageButton searchBtn,hospitalBtn,pharmacyBtn;

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_near_by_places);

        drawerLayout = findViewById(R.id.drawer_layout);

        search = findViewById(R.id.locationSearch);
        searchBtn = findViewById(R.id.searchBtn);
        hospitalBtn = findViewById(R.id.hospitalBtn);
        pharmacyBtn = findViewById(R.id.pharmacyBtn);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public boolean checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},requestUserLocationCode);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},requestUserLocationCode);
            }
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case requestUserLocationCode:
                if(grantResults.length > 0 &&  grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if(googleApiClient == null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this,"Location Permission Denied",Toast.LENGTH_LONG).show();
                }
                return;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            buildGoogleApiClient();

            mMap.setMyLocationEnabled(true);
        }

    }

    @SuppressLint("LongLogTag")
    public void SearchLocation(View view)
    {
        String address = search.getText().toString();

        List<Address> addressList = null;
        MarkerOptions userMarkerOptions = new MarkerOptions();

        if(!TextUtils.isEmpty(address))
        {
            Geocoder geocoder = new Geocoder(this);

            try {
                addressList = geocoder.getFromLocationName(address,20);
                Log.i("ADDRESS LIST : ",(geocoder.getFromLocationName(address,20)).toString());
                if(addressList != null)
                {
                    for (int i=0;i<addressList.size();i++)
                    {
                        Address addressUser = addressList.get(i);
                        Log.i("ADDRESS USER : ", String.valueOf(addressUser));
                        LatLng latLng = new LatLng(addressUser.getLatitude(),addressUser.getLongitude());
                        userMarkerOptions.position(latLng);
                        userMarkerOptions.title(address);
                        userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE ));
                        mMap.addMarker(userMarkerOptions);

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
                    }
                }
                else {
                    Toast.makeText(this,"Location Not Found",Toast.LENGTH_LONG).show();

                }
            } catch (IOException e) {
                Log.i("EXCEPTION GEOCODER ADDRESS:",e.getMessage());
                e.printStackTrace();
            }

        }
        else {
            Toast.makeText(this,"Please Enter Location Name",Toast.LENGTH_LONG).show();
        }
    }

    public void HospitalClick(View view)
    {
        mMap.clear();
        String hospital = "hospital";
        String url = getUrl(latitude,longtitude,hospital);
        Object transferData[] = new Object[2];
        NearBy getNearByPlaces = new NearBy();
        transferData[0] = mMap;
        transferData[1] = url;

        Toast.makeText(this,"Searching For NearBy Hospitals",Toast.LENGTH_LONG).show();

        getNearByPlaces.execute(transferData);

        Toast.makeText(this,"Showing NearBy Hospitals",Toast.LENGTH_SHORT).show();

    }
    public void PharmacyClick(View view)
    {
        String pharmacy = "pharmacy";
        String url = getUrl(latitude,longtitude,pharmacy);
        Object transferData[] = new Object[2];
        NearBy getNearByPlaces = new NearBy();
        transferData[0] = mMap;
        transferData[1] = url;

        Toast.makeText(this,"Searching For NearBy Pharmacy",Toast.LENGTH_LONG).show();

        getNearByPlaces.execute(transferData);

        Toast.makeText(this,"Showing NearBy Pharmacy",Toast.LENGTH_SHORT).show();
    }

    public String getUrl(double latitude,double longitude,String nearbyPlace)
    {
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location="+latitude + "," + longitude);
        googleURL.append("&radius="+ProximityRadius);
        googleURL.append("&type="+nearbyPlace);
        googleURL.append("&sensor=true");
        googleURL.append("&key="+"AIzaSyBY3q_JjOxHaK3mZf47f3wm0AbkKUBNTPs");

        Log.i("GOOGLE PLACE API :" ,googleURL.toString());

        return googleURL.toString();
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longtitude = location.getLongitude();

        lastLocation = location;
        if(currentUserMarker != null)
        {
            currentUserMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("User Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED ));
        currentUserMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.zoomBy(12));

        if(googleApiClient != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
        recreate();
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