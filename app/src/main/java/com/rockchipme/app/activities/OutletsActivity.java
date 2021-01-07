package com.rockchipme.app.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.rockchipme.app.R;
import com.rockchipme.app.adapters.OutletsAdapter;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.helpers.EventBusResponse;
import com.rockchipme.app.helpers.LocationUpdateHelper;
import com.rockchipme.app.helpers.PreferenceUtil;
import com.rockchipme.app.models.OutletsResponse;
import com.rockchipme.app.models.RestaurantDetails;
import com.rockchipme.app.network.ApiCallRequest;
import com.rockchipme.app.network.ApiCallResponse;
import com.rockchipme.app.network.ApiCallServiceTask;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import okhttp3.FormBody;

/**
 * Created by Alisons on 4/25/2018.
 */

public class OutletsActivity extends BaseActivity implements ApiCallServiceTask.onApiFinish{
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_outlets;
    }

    OutletsAdapter outletsAdapter;
    private boolean isFromDrawerMenu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpToolBar(false, "OUTLETS", false);
        isFromDrawerMenu = getIntent().getBooleanExtra("isFromDrawerMenu", false);
        if (!isFromDrawerMenu){
            hideBackButton();
        }

        initializeViews();

        getOutletsApi(ApiCallRequest.WHITE);
        if (locationUpdateHelperIsNotNull()){
            locationUpdateHelper.setLocationUpdateListener(this::storeLocation);
        }

    }

    private void initializeViews() {
        RecyclerView rvOutlets = findViewById(R.id.rvOutlets);
        outletsAdapter = new OutletsAdapter(this);
        rvOutlets.setAdapter(outletsAdapter);
    }

    private LocationUpdateHelper locationUpdateHelper;
    private boolean locationUpdateHelperIsNotNull(){
        if (locationUpdateHelper == null){
            locationUpdateHelper = new LocationUpdateHelper(this);
        }
        return true;
    }

    public static final String FROM_GET_OUTLETS = "FROM_GET_OUTLETS";
    private void getOutletsApi(String progressBg) {
        FormBody.Builder builder =  new FormBody.Builder();
        builder.add("rest_key", Constants.REST_KEY_MAIN);
        builder.add("api_key", apiKey);

        if (location != null){
            builder.add("latitude", location.getLatitude()+"");
            builder.add("longitude", location.getLongitude()+"");
        }
        apiCallServiceTask.requestApi(new ApiCallRequest(FROM_GET_OUTLETS, Constants.getOutlets_api, builder.build(), true, progressBg));
    }


    Location location;
    private void storeLocation(Location location1){
        location = location1;
        if (location1 != null) {
            getOutletsApi(ApiCallRequest.TRANSPARENT);
            Log.d(Constants.APP_TAG, "latitude:" + location1.getLatitude());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationUpdateHelperIsNotNull()){
            locationUpdateHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void storeOutletDetails(RestaurantDetails restaurantDetails){
        new PreferenceUtil(this).setOutletStoreDetails(restaurantDetails);
        Constants.REST_KEY = restaurantDetails.rest_key;
        openHome();
    }

    private void aboutUsApiResponse(String response) {
        try {
            Log.d(Constants.APP_TAG, "outlet ApiResponse:"+response);
            OutletsResponse outletsResponse = new Gson().fromJson(response, OutletsResponse.class);
            if (outletsResponse == null || outletsResponse.responseText == null ||
                    outletsResponse.responseText.outlets == null || outletsResponse.responseText.outlets.size()<1){
                findViewById(R.id.layoutEmptyScreen).setVisibility(View.VISIBLE);
                return;
            } else {
                findViewById(R.id.layoutEmptyScreen).setVisibility(View.GONE);
            }

            if (outletsResponse.responseText.outlets.size() == 1 && !isFromDrawerMenu){
                storeOutletDetails(outletsResponse.responseText.outlets.get(0));
                return;
            }
            outletsAdapter.setAdapterDatas(outletsResponse.responseText.outlets);
        } catch (Exception e){e.printStackTrace();}
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusResponse eventBusResponse){

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (locationUpdateHelperIsNotNull()){
            locationUpdateHelper.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onApiFinished(ApiCallResponse responseBus) {
        switch (responseBus.FROM){
            case FROM_GET_OUTLETS:
                aboutUsApiResponse(responseBus.response);
                break;
        }
    }
}
