package com.ucd.android.qwikscan.screens;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ucd.android.qwikscan.R;
import com.ucd.android.qwikscan.map.BackgroundTasks;

import java.util.HashMap;
import java.util.List;

public class MapFragmentView extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<LocationSettingsResult> {


    MapView mapView;
    private GoogleMap fragGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private AlertDialog alertDialog;

    private int MY_LOCATION_REQUEST_CODE = 10;
    private BackgroundTasks backgroundTasks = new BackgroundTasks();

    /** Method that is called when the View is created
     *  The Google API Client to be used with the Map View is created and initialized here
     *
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.map_fragment, container, false);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                fragGoogleMap = googleMap;


            }
        });
        return rootView;
    }

    /** Method that is called when the Google API client connects
     *
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        enableLocation();
    }

    /**
     * Method that checks if GPS is enabled, gets the permission for it and retrieves user location
     */

    private void enableLocation() {
        boolean isGpsEnabled = ((LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGpsEnabled) {
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                fragGoogleMap.setMyLocationEnabled(true);
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                double latitude = 0.0;
                double longitude = 0.0;
                if (mLastLocation != null) {
                    latitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();

                }
                if(latitude == 0.0 && longitude == 0.0){
                    mLocationRequest = LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                            .setFastestInterval(1 * 1000);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(mLocationRequest);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                                    builder.build());
                    result.setResultCallback(this);

                }
                else {
                    addNearbyShops(latitude, longitude);
                }
            } else {
                // Asking user permission to access its location
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_LOCATION_REQUEST_CODE);
            }
        }
        else {
            // Making request to user's location
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                    .setFastestInterval(1 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                            builder.build());
            result.setResultCallback(this);


        }
    }

    /**
     * Method that creates a request for the Google Places API and starts an asnychronous task that makes the request
     * @param mLatitude
     * @param mLongitude
     */
    private void addNearbyShops(double mLatitude, double mLongitude) {

        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location=" + mLatitude + "," + mLongitude);
        sb.append("&radius=5000");
        sb.append("&keyword=" + getString(R.string.store_name));

        sb.append("&sensor=true");
        sb.append("&key=" + getString(R.string.map_key));

        // Creating a new non-ui thread task to download json data

        BackgroundTasks.NearbyStoresTask storesTask = backgroundTasks.new NearbyStoresTask(this);

        storesTask.execute(sb.toString());


    }

    /**
     * Method that is called by the asynchronous task when it's finished downloading data from the Google Places API
     * @param list
     */
    public void asyncTaskDone(List<HashMap<String, String>> list) {
        // Clears all the existing markers
        fragGoogleMap.clear();

        for (int i = 0; i < list.size(); i++) {

            // Creating a marker
            MarkerOptions markerOptions = new MarkerOptions();

            // Getting a object from the list
            HashMap<String, String> hmPlace = list.get(i);

            // Getting latitude
            double lat = Double.parseDouble(hmPlace.get("lat"));

            // Getting longitude
            double lng = Double.parseDouble(hmPlace.get("lng"));

            // Getting name
            String name = hmPlace.get("store_name");

            // Getting vicinity
            String vicinity = hmPlace.get("vicinity");

            LatLng latLng = new LatLng(lat, lng);

            // Setting the position for the marker
            markerOptions.position(latLng);

            // Setting the title for the marker.
            //This will be displayed on taping the marker
            markerOptions.title(name + " : " + vicinity);

            // Placing a marker on the touched position
            fragGoogleMap.addMarker(markerOptions);

            LatLng current = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder().target(current).zoom(12).build();
            fragGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    /** Since we need only the location once. Once the location changes, we cancel the location request
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        enableLocation();
        if(mLocationRequest != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        }
    }

    /**
     * Callback for requesting user permissions.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length >= 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocation();
            } else {
                enableLocation();
            }
        }
    }

    /**
     * Callback for location request
     * @param locationSettingsResult
     */
    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()){
            case LocationSettingsStatusCodes.SUCCESS:
                enableLocation();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                if(alertDialog == null) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage("Location Services need to be enabled to see nearest stores. Please enable and try again");
                    alertDialogBuilder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }


                enableLocation();

                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Toast.makeText(
                        getActivity(),
                        "Location Services Settings unavailable. Nearest stores won't work",
                        Toast.LENGTH_LONG)
                        .show();


                break;


        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    /** Disconnect the Api client
     *
     */
    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

}