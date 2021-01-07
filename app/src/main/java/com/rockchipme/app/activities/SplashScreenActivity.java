package com.rockchipme.app.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.facebook.FacebookSdk;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.rockchipme.app.BuildConfig;
import com.rockchipme.app.R;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.helpers.PreferenceUtil;
import com.rockchipme.app.models.HomeResponse;
import com.rockchipme.app.models.Notification;
import com.rockchipme.app.models.RestaurantDetails;
import com.rockchipme.app.models.VersionResponse;
import com.rockchipme.app.network.ApiCallRequest;
import com.rockchipme.app.network.ApiCallResponse;
import com.rockchipme.app.network.ApiCallServiceTask;
import com.squareup.picasso.Picasso;

import java.net.URLEncoder;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Alisons on 9/12/2017.
 */
public class SplashScreenActivity extends Activity implements ApiCallServiceTask.onApiFinish{

    String apiKey = "";
    SharedPreferences loginPref;
    SharedPreferences.Editor loginEditor;
    ImageView ivLogo;
    ApiCallServiceTask apiCallServiceTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        loginPref = getSharedPreferences(Constants.PREF_LOGIN, MODE_PRIVATE);
        loginEditor = loginPref.edit();
        apiCallServiceTask = new ApiCallServiceTask(this);
        try {
            FacebookSdk.sdkInitialize(getApplicationContext());
            Constants.HASH_KEY = FacebookSdk.getApplicationSignature(this);
            Constants.HASH_KEY = URLEncoder.encode(Constants.HASH_KEY, "UTF-8");
//            Log.d(Constants.APP_TAG, "HASH_KEY:"+Constants.HASH_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ivLogo = findViewById(R.id.ivSplachLogo);
        try {
            Picasso.with(this).load(loginPref.getString(Constants.logo, "")).placeholder(R.drawable.app_icon).into(ivLogo);
        } catch (Exception e){e.printStackTrace();}

        checkGpsEnabled();

//        splash_wait();
//        dumyyApiCall();
    }

    final int RC_REQUEST_LOCATION = 101;
    private void checkGpsEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String permissions[] = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (ActivityCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, permissions[1]) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, permissions, RC_REQUEST_LOCATION);
        } else {
            if (locationManager != null) {
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!isGpsEnabled) {
                    showSettingsAlert();
                } else {
                    callApiCall();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_REQUEST_LOCATION && grantResults.length>0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                checkGpsEnabled();
            }{
                callApiCall();
            }
        }
    }

    private void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog
                .setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                (dialog, which) -> {
                    Intent intent = new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, 15);
                });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel",
                (dialog, which) -> {
                    dialog.cancel();
                    callApiCall();
                });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 15){
            callApiCall();
        }
    }

    boolean isApiCalling = false;

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (!isApiCalling){
//            callApiCall();
//        }
//    }

    Notification notification = null;
    private void callApiCall(){
        apiKey = loginPref.getString(Constants.api_key,"");
        if (apiKey != null && apiKey.trim().length() > 1){
            if (getIntent().getSerializableExtra("notification")!=null &&
                    getIntent().getSerializableExtra("notification") instanceof Notification) {
                notification = (Notification) getIntent().getSerializableExtra("notification");
                getVersionInfoApi();
                return;
            }
            getHomeDataApi(apiKey);
        } else {
            getVersionInfoApi();
        }
    }

    private void notificationRedirect(){
        try {
            Log.d(Constants.APP_TAG, "SplashScreen notificationRedirect");
            if (notification == null) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("from", "splash");
                startActivity(intent);
                finish();
                return;
            }
            Intent intent = null;
            String typeValue = notification.value;
            switch (notification.type) {
                case "1":
                    intent = new Intent(getApplicationContext(), OrderDetailsActivity.class);
                    intent.putExtra("orderId", typeValue);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    break;
                case "2":
                    intent = new Intent(getApplicationContext(), ProductListActivity.class);
//                    "data":[{"catId":"5","image":"img_5","pdtCount":"36","name":"JUICE"}]
                    intent.putExtra("catId", typeValue);
                    try {
                        if (notification.notificationdate != null && notification.notificationdate.size() > 0) {
                            intent.putExtra("itemsCount", notification.notificationdate.get(0).pdtCount);
                            intent.putExtra("catName", notification.notificationdate.get(0).name);
                            intent.putExtra("catImg", notification.notificationdate.get(0).getImage());
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                    } catch (Exception e) {
                    }
                    break;
                case "3":
                    intent = new Intent(getApplicationContext(), ItemDetailsActivity.class);
                    intent.putExtra("pdtId", typeValue);
                    break;
                case "4":
                    String url = typeValue+"";
                    if (url.trim().length() > 0 && !url.startsWith("https://") && !url.startsWith("http://")) {
                        url = "http://" + url;
                    }
                    if (url.trim().length() > 0) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse(url));
                        startActivity(browserIntent);
                    }
                    intent = new Intent(SplashScreenActivity.this, HomeScreenActivity.class);
                    break;
                default:
                    intent = new Intent(SplashScreenActivity.this, HomeScreenActivity.class);
                    break;
            }
            if (intent != null) {
                intent.putExtra("isFromNotification", true);
                startActivity(intent);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
            }
        } catch (Exception e){e.printStackTrace();}
    }

    private final String FROM_GET_HOME_DATA = "FROM_GET_HOME_DATA_SPLASH";
    private void getHomeDataApi(String apiKey) {
        if (apiKey.equals("")){
            return;
        }

        try {
            PreferenceUtil preferenceUtil = new PreferenceUtil(this);
            if (preferenceUtil.getOutletStoreDetails().rest_key.trim().length() > 0) {
                Constants.REST_KEY = preferenceUtil.getOutletStoreDetails().rest_key;
            }
        } catch (Exception e){e.printStackTrace();}
        String token = FirebaseInstanceId.getInstance().getToken();
        token = token == null ? "" : token;
//        Log.e(Constants.APP_TAG, "firebase token:"+token);

        RequestBody body=new FormBody.Builder()
                .add("rest_key", Constants.REST_KEY)
                .add("rest_key_main", Constants.REST_KEY_MAIN)
                .add("api_key", apiKey)
                .add("deviceToken", token)
                .add("appVersion", BuildConfig.VERSION_CODE+"")
                .add("deviceOS", "ANDROID")
                .add("deviceModel", getDeviceName())
                .build();
        isApiCalling = true;
        apiCallServiceTask.requestApi(new ApiCallRequest(FROM_GET_HOME_DATA, Constants.getHomeData_api, body,true, "Loading..."));
    }

    private final String FROM_GET_VERSION_INFO = "FROM_GET_VERSION_INFO";
    private void getVersionInfoApi() {
        RequestBody body=new FormBody.Builder()
                .add("rest_key", Constants.REST_KEY)
                .add("rest_key_main", Constants.REST_KEY_MAIN)
                .add("appVersion", BuildConfig.VERSION_CODE+"")
                .add("deviceOS", "ANDROID")
                .build();
        isApiCalling = true;
        apiCallServiceTask.requestApi(new ApiCallRequest(FROM_GET_VERSION_INFO, Constants.getAppVersionInfo_api, body, true,"Loading..."));
    }

    private void getVersionInfoApiResponse(String response) {
        try {
            final VersionResponse versionResponse = new Gson().fromJson(response, VersionResponse.class);
            if (versionResponse == null || versionResponse.responseText == null) {
                return;
            }

            if (versionResponse.responseText.version!=null && versionResponse.responseText.version.size()>0) {
                if (versionResponse.responseText.version.get(0).forceUpdate.trim().equals("1")) {
                    appForceUpdate();
                } else {
                    try {
                        int i = Integer.parseInt(versionResponse.responseText.version.get(0).version);
                        if (BuildConfig.VERSION_CODE < i) {
                            appUpdate();
                        } else {
                            notificationRedirect();
                        }
                    } catch (Exception e) {
                        notificationRedirect();
                        e.printStackTrace();
                    }
                }
            } else {
                notificationRedirect();
            }



        } catch (Exception e){e.printStackTrace();}
    }

    private void getHomeDataApiResponse(final String response) {
        try {
            final HomeResponse homeResponse = new Gson().fromJson(response, HomeResponse.class);
            if (homeResponse == null || homeResponse.responseText == null) {
                return;
            }

            List<RestaurantDetails> restaurantDetailsList = homeResponse.responseText.restaurantDetailsList;
            if (restaurantDetailsList != null && restaurantDetailsList.size()>0){
                loginEditor.putString(Constants.logo, restaurantDetailsList.get(0).getLogo()).commit();
                try {
                    Picasso.with(this).load(loginPref.getString(Constants.logo, "")).placeholder(R.drawable.app_icon).into(ivLogo);
                } catch (Exception e){e.printStackTrace();}
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        try {
                            if (new PreferenceUtil(getApplicationContext()).getOutletStoreDetails().rest_key.trim().length() < 1) {
                                Intent intent = new Intent(getApplicationContext(), OutletsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                return;
                            }
                        } catch (Exception e){e.printStackTrace();}

                        Intent intent = new Intent(SplashScreenActivity.this, HomeScreenActivity.class);
                        intent.putExtra("homeData", response);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } catch (Exception e){e.printStackTrace();}
                }
            },1000);

        } catch (Exception e){e.printStackTrace();}
    }

    private String getDeviceName() {
        try{
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            if (model.startsWith(manufacturer)) {
                return model;
            }
            return manufacturer + " " + model;
        }catch (Exception e){
        }
        return "";
    }


    private void appUpdate(){
        if (!Constants.isShowUpdatePopUp){
            return;
        }
        Constants.isShowUpdatePopUp = false;
        if(!isFinishing()){
            SweetAlertDialog dialog = new SweetAlertDialog(SplashScreenActivity.this, SweetAlertDialog.WARNING_TYPE);
            dialog.setTitleText("Update Available");
            String appName = getResources().getString(R.string.app_name);
            dialog.setContentText("A new version of "+ appName +" is available");
            dialog.setConfirmText("Update");
            dialog.setCancelText("Close");
            dialog.setConfirmClickListener(sweetAlertDialog -> {
                notificationRedirect();
                sweetAlertDialog.dismiss();
                openPlayStore();
            });
            dialog.setCancelClickListener(sweetAlertDialog -> {
                notificationRedirect();
                sweetAlertDialog.dismiss();
            });
            dialog.show();
        }
    }

    public void appForceUpdate(){
        if(!isFinishing()){
            SweetAlertDialog dialog = new SweetAlertDialog(SplashScreenActivity.this, SweetAlertDialog.WARNING_TYPE);
            dialog.setTitleText("Update Available");


            String appName = getResources().getString(R.string.app_name);
            dialog.setContentText("A new version of "+ appName +" is available");
            dialog.setConfirmText("Update");
            dialog.setCancelText("Exit");
            dialog.setCancelable(false);
            dialog.setConfirmClickListener(sweetAlertDialog -> {
                sweetAlertDialog.dismiss();
                openPlayStore();
            });
            dialog.setCancelClickListener(sweetAlertDialog -> {
                sweetAlertDialog.dismiss();
                getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                try{
                    ActivityCompat.finishAffinity(SplashScreenActivity.this);
                } catch (Exception e){e.printStackTrace();}
                finish();
            });
            dialog.show();
        }
    }

    private void openPlayStore(){
        String PACKAGE_NAME = getApplicationContext().getPackageName();
        Log.d(Constants.APP_TAG, "PACKAGE_NAME:"+PACKAGE_NAME);
        String link = "https://play.google.com/store/apps/details?id=" + PACKAGE_NAME + "&hl=en";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(browserIntent);
    }

    @Override
    public void onApiFinished(ApiCallResponse responseBus) {
        isApiCalling = false;
        if (responseBus.ERROR_TYPE == ApiCallResponse.FAILED){
            onApiFailed(responseBus);
            return;
        }
        switch (responseBus.FROM){
            case FROM_GET_HOME_DATA:
                getHomeDataApiResponse(responseBus.response);
                break;
            case FROM_GET_VERSION_INFO:
                getVersionInfoApiResponse(responseBus.response);
                break;
        }
    }

    public void onApiFailed(final ApiCallResponse responseBus) {
        try {
            findViewById(R.id.btnRetry).setVisibility(View.VISIBLE);
            findViewById(R.id.btnRetry).setOnClickListener(v -> {
                try {
                    findViewById(R.id.btnRetry).setVisibility(View.INVISIBLE);
                    switch (responseBus.FROM){
                        case FROM_GET_HOME_DATA:
                            getHomeDataApi(apiKey);
                            break;
                        case FROM_GET_VERSION_INFO:
                            getVersionInfoApi();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e){e.printStackTrace();}
    }
}

