package com.globalpaysolutions.realtimelocationtest;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class VehiclesActivity extends AppCompatActivity implements GeoQueryEventListener, OnMapReadyCallback
{
    private static final LatLng AV_LA_CAPILLA = new LatLng(13.686005,-89.242484);

    private static final String TAG = VehiclesActivity.class.getCanonicalName();
    private static final GeoLocation INITIAL_CENTER = new GeoLocation(AV_LA_CAPILLA.latitude, AV_LA_CAPILLA.longitude);
    private static final int INITIAL_ZOOM_LEVEL = 14;

    DatabaseReference  mRootReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUsersRef = mRootReference.child("UserLocations");

    private GoogleMap mGoogleMap;
    private Circle mSearchCircle;
    private GeoFire mGeoFire;
    private GeoQuery mGeoQuery;

    private Map<String, Marker> mMarkers;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicles);

        try
        {
            // setup GeoFire
            this.mGeoFire = new GeoFire(mUsersRef);
            // radius in km
            this.mGeoQuery = this.mGeoFire.queryAtLocation(INITIAL_CENTER, 1);

            // setup markers
            this.mMarkers = new HashMap<String, Marker>();

            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.vehiclesMap);
            mapFragment.getMapAsync(this);
            writeLocation();

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
        this.mGeoQuery.addGeoQueryEventListener(this);
    }

    @Override
    public void onKeyEntered(String key, GeoLocation location)
    {
        Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)));
        mMarkers.put(key, marker);
    }

    @Override
    public void onKeyExited(String key)
    {

    }

    @Override
    public void onKeyMoved(String key, GeoLocation location)
    {
        Marker marker = this.mMarkers.get(key);
        if (marker != null)
        {
            this.animateMarkerTo(marker, location.latitude, location.longitude);
        }
    }

    @Override
    public void onGeoQueryReady()
    {

    }

    @Override
    public void onGeoQueryError(DatabaseError error)
    {

    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mGoogleMap = googleMap;

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
            mGoogleMap.setMyLocationEnabled(true);
        }
        mGoogleMap.setTrafficEnabled(false);
        mGoogleMap.setIndoorEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);

        LatLng latLngCenter = new LatLng(INITIAL_CENTER.latitude, INITIAL_CENTER.longitude);
        this.mSearchCircle = mGoogleMap.addCircle(new CircleOptions().center(latLngCenter).radius(2000));
        this.mSearchCircle.setFillColor(Color.argb(66, 231, 141, 189));
        this.mSearchCircle.setStrokeColor(Color.parseColor("#e78dbd"));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLngCenter).zoom(INITIAL_ZOOM_LEVEL).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


    }

    // Animation handler for old APIs without animation support
    private void animateMarkerTo(final Marker marker, final double lat, final double lng)
    {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long DURATION_MS = 3000;
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final LatLng startPosition = marker.getPosition();
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                float elapsed = SystemClock.uptimeMillis() - start;
                float t = elapsed / DURATION_MS;
                float v = interpolator.getInterpolation(t);

                double currentLat = (lat - startPosition.latitude) * v + startPosition.latitude;
                double currentLng = (lng - startPosition.longitude) * v + startPosition.longitude;
                marker.setPosition(new LatLng(currentLat, currentLng));

                // if animation is not finished yet, repeat
                if (t < 1)
                {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    public void writeLocation()
    {
        mGeoFire.setLocation("User1", new GeoLocation(37.7853889, -122.4056973));

        mGeoFire.getLocation("Userlocation", new LocationCallback()
        {
            @Override
            public void onLocationResult(String key, GeoLocation location)
            {
                if (location != null)
                {
                    //System.out.println();
                    Log.i(TAG, String.format("The location for key %s is [%f,%f]", key, location.latitude, location.longitude));
                }
                else
                {
                    //System.out.println());
                    Log.e(TAG, String.format("There is no location for key %s in GeoFire", key));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                System.err.println("There was an error getting the GeoFire location: " + databaseError);
                Log.e(TAG,"There was an error getting the GeoFire location: " + databaseError );
            }
        });
    }

    public void addLocation(View view)
    {
        counter = counter + 1;
        String user = "User" + String.valueOf(counter);
        mGeoFire.setLocation(user, new GeoLocation(AV_LA_CAPILLA.latitude, AV_LA_CAPILLA.longitude));
    }
}
