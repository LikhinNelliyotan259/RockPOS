package com.rockchipme.app.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;

public class LocationUpdateHelper {
    private Activity activity;

    public LocationUpdateHelper(Activity activity) {
        this.activity = activity;
        getLocation();
    }

    private LocationUpdateListener updateListener;


    private FusedLocationProviderClient fusedLocationProviderApi;
    private final int REQUEST_CHECK_SETTINGS = 0x1;
    private LocationRequest locationRequest;
    private final int RC_REQUEST_LOCATION = 101;

    private void getLocation() {
        String permissions[] = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (ActivityCompat.checkSelfPermission(activity, permissions[0]) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, permissions[1]) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(activity, permissions, RC_REQUEST_LOCATION);
        } else {
            GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity)
                    .addApi(LocationServices.API).build();
            googleApiClient.connect();

            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10);
            locationRequest.setFastestInterval(10);

            fusedLocationProviderApi = LocationServices.getFusedLocationProviderClient(activity);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                    .setAlwaysShow(true);


            Task<LocationSettingsResponse> result2 =
                    LocationServices.getSettingsClient(activity).checkLocationSettings(builder.build());


            result2.addOnCompleteListener(task -> {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.

                    startLocationUpdates();
                    Log.d(Constants.APP_TAG, "Location is on");

                } catch (ApiException exception) {
                    Log.d(Constants.APP_TAG, "Location is off");
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        activity,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            break;
                    }
                }
            });
        }
    }


    private void stopLocationUpdates() {
        fusedLocationProviderApi.removeLocationUpdates(locationCallback);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderApi.requestLocationUpdates(locationRequest, locationCallback, null);
    }


    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                // Update UI with location data
                // ...
//                Toast.makeText(activity, "location latitude :" + location.getLatitude() + " , longitude: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                locationUpdate(location);
                stopLocationUpdates();
            }
        }
    };


    public void setLocationUpdateListener(LocationUpdateListener updateListener){
        this.updateListener = updateListener;
    }

    private void locationUpdate(Location location){
        if (updateListener != null){
            updateListener.locationUpdated(location);
        }
    }


    public interface LocationUpdateListener {
        void locationUpdated(Location location);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_REQUEST_LOCATION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                checkGpsEnabled();
                getLocation();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        stopLocationUpdates();
                        break;
                    default:
                        break;
                }
                break;
        }
    }


}
