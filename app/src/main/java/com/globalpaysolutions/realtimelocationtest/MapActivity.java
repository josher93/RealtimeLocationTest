package com.globalpaysolutions.realtimelocationtest;

import android.*;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.globalpaysolutions.realtimelocationtest.models.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private GoogleMap googleMap;
    private static final LatLng AV_LA_CAPILLA = new LatLng(13.686005,-89.242484);
    private static final String TAG = MapActivity.class.getSimpleName();

   /* DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mConditionRef = mRootReference.child("condition");*/

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mDatabase = FirebaseDatabase.getInstance().getReference();


        try
        {
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //insertUsers();
        //updateUsers();
    }

    @Override
    public void onMapReady(GoogleMap pGoogleMap)
    {
        googleMap = pGoogleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (!shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION) && !shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION))
            {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 3);
            }
        }
        else
        {
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.setTrafficEnabled(false);
        googleMap.setIndoorEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(false);

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(AV_LA_CAPILLA)      // Sets the center of the map to Mountain View
                .zoom(19)                   // Sets the zoom
                //.bearing(90)                // Sets the orientation of the camera to east
                //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void insertUsers()
    {
        try
        {
            for(int i = 0; i <= 8; i++)
            {
                //User user = new User(i, "", 13, 13);
                User user = new User();
                user.setId(i);
                user.setKey(mDatabase.child("Users").push().getKey());
                user.setLatitude(13);
                user.setLongitude(13);

                mDatabase.child("Users").child("User" + String.valueOf(i)).setValue(user);
                Log.i(TAG, "User n° " + String.valueOf(i) + " added");
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void updateUsers()
    {
        try
        {
            for(int i = 0; i <= 8; i++)
            {
                mDatabase.child("Users").child("User" + String.valueOf(i)).child("latitude").setValue(15);
                mDatabase.child("Users").child("User" + String.valueOf(i)).child("longitude").setValue(16);

                //mDatabase.child("Users").child("User" + String.valueOf(i)).setValue(user);
                Log.i(TAG, "User n° " + String.valueOf(i) + " added");
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void markUsers()
    {
        /*Marker markerB = mMap.addMarker(new MarkerOptions().position(friendLocation)
                .title(friendUser.name + "-" + distance).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        markerB.showInfoWindow();*/
    }

    ChildEventListener locationsListener = new ChildEventListener()
    {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s)
        {

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s)
        {
            // A comment has changed, use the key to determine if we are displaying this
            // comment and if so displayed the changed comment.
            User newUser = dataSnapshot.getValue(User.class);
            String userKey = dataSnapshot.getKey();
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot)
        {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s)
        {

        }

        @Override
        public void onCancelled(DatabaseError databaseError)
        {

        }
    };
}
