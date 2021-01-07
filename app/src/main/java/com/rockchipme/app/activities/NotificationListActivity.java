package com.rockchipme.app.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.rockchipme.app.R;
import com.rockchipme.app.adapters.NotificationsListAdapter;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.helpers.EventBusResponse;
import com.rockchipme.app.models.Notification;
import com.rockchipme.app.models.NotificationResponse;
import com.rockchipme.app.network.ApiCallRequest;
import com.rockchipme.app.network.ApiCallResponse;
import com.rockchipme.app.network.ApiCallServiceTask;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

//import com.rockchipme.app.adapters.NotificationsAdapter;

public class NotificationListActivity extends BaseActivity implements ApiCallServiceTask.onApiFinish {
@Override
protected int getLayoutResourceId() {
    return R.layout.activity_notifications_list;
}

    RecyclerView rvNotificationsList;
    NotificationsListAdapter notificationsListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpToolBar(false, "NOTIFICATIONS", false);

        rvNotificationsList = findViewById(R.id.rvNotificationsList);
        rvNotificationsList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvNotificationsList.setLayoutManager(layoutManager);
        notificationsListAdapter = new NotificationsListAdapter(this);
        rvNotificationsList.setAdapter(notificationsListAdapter);

        getNotificationsApi();
    }


    private final String FROM_GET_NOTIFICATIONS = "FROM_GET_NOTIFICATIONS";
    private void getNotificationsApi() {
        RequestBody body=new FormBody.Builder()
                .add("rest_key", Constants.REST_KEY)
                .add("api_key", apiKey)
                .build();
        apiCallServiceTask.requestApi(new ApiCallRequest(FROM_GET_NOTIFICATIONS, Constants.getNotifications_api, body, true,ApiCallRequest.WHITE));
    }

    private void getNotificationApiResponse(String response) {
        try {
            Log.e(Constants.APP_TAG, "NotificationActivity response:" + response);
            NotificationResponse notificationResponse = new Gson().fromJson(response, NotificationResponse.class);

            if (notificationResponse.responseText.success == 0 && notificationResponse.responseText.status != null &&
                    !notificationResponse.responseText.status.trim().equals("")) {
                Toast.makeText(this, notificationResponse.responseText.status.trim(), Toast.LENGTH_SHORT).show();
            }

            List<Notification> notificationsList = notificationResponse.responseText.notificationsList;
            notifyAdapter(notificationsList);
        } catch (Exception e){
            e.printStackTrace();
            notifyAdapter(null);
        }
    }


    private void notifyAdapter(List<Notification> notificationsList) {
        if (notificationsList == null){
            notificationsList = new ArrayList<>();
        }
        if (notificationsList.size()>0 && rvNotificationsList!=null){
            rvNotificationsList.setVisibility(View.VISIBLE);
            setEmptyScreen(false);
        } else {
            setEmptyScreen(true);
        }
        if (notificationsListAdapter!=null){
            notificationsListAdapter.setAdapterData(notificationsList);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusResponse eventBusResponse){

    }

    @Override
    public void onApiFinished(ApiCallResponse responseBus) {
        switch (responseBus.FROM){
            case FROM_GET_NOTIFICATIONS:
                getNotificationApiResponse(responseBus.response);
                break;
        }
    }
}
