package com.rockchipme.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rockchipme.app.R;
import com.rockchipme.app.adapters.OrdersAdapter;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.helpers.EventBusResponse;
import com.rockchipme.app.models.OrderHistoryResponse;
import com.rockchipme.app.models.Orders;
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

/**
 * Created by Alisons on 9/27/2017.
 */
public class OrderHistoryActivty extends BaseActivity implements ApiCallServiceTask.onApiFinish {
    Toolbar toolbar;
    TextView tvToolbar, tv_noHistory;

    boolean isFromNotification = false;

    RecyclerView rv_orders;

    OrdersAdapter ordersAdapter;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_order_history;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        isFromNotification = getIntent().getBooleanExtra("isFromNotification", false);

        initialize();
        setUpToolBar(false, "ORDER HISTORY", false);

        getOrders();


    }

    private void initialize() {

        tvToolbar = (TextView) findViewById(R.id.toolbar_title);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        tv_noHistory = (TextView) findViewById(R.id.tv_no_ordrersHistory);

        rv_orders = (RecyclerView) findViewById(R.id.rv_orders);

        //setting adapter
        rv_orders.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_orders.setLayoutManager(layoutManager);
        ordersAdapter = new OrdersAdapter(this, currency);
        rv_orders.setAdapter(ordersAdapter);

    }

    private final String FROM_GET_ORDERS = "FROM_GET_ORDERS";

    private void getOrders() {
        RequestBody body = new FormBody.Builder()
//                .add("api_key", getSharedPreferences(Constants.PREF_LOGIN, MODE_PRIVATE).getString(Constants.api_key, ""))
                .add("api_key", apiKey)
                .add("rest_key", Constants.REST_KEY)
                .build();
        apiCallServiceTask.requestApi(new ApiCallRequest(FROM_GET_ORDERS, Constants.getOrders_api, body, true, ApiCallRequest.WHITE));
    }

    private void getOrderResponse(String response) {
        try {
            OrderHistoryResponse orderResponse = new Gson().fromJson(response, OrderHistoryResponse.class);
            if (orderResponse != null && orderResponse.responseText != null) {
                if (orderResponse.responseText.success == 1) {
                    List<Orders> ordersList = orderResponse.responseText.ordersList;
                    if (ordersList != null && ordersList.size() > 0) {
                        tv_noHistory.setVisibility(View.GONE);
                        rv_orders.setVisibility(View.VISIBLE);
                        setListAdapter(ordersList);
                    }
                } else {
                    if (orderResponse.responseText.message != null && orderResponse.responseText.message.trim().length() > 0) {
                        Toast.makeText(OrderHistoryActivty.this, orderResponse.responseText.message, Toast.LENGTH_SHORT).show();
                    }
                    checkEmptyScreen();
                    rv_orders.setVisibility(View.GONE);
                    tv_noHistory.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        String from = getIntent().getStringExtra("from");
        if ((from != null && from.equalsIgnoreCase("placeOrder")) || isFromNotification) {
            Intent intent = new Intent(this, HomeScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusResponse eventBusResponse) {
        if (eventBusResponse != null) {

            if (ordersAdapter == null) {
                return;
            }
            List<Orders> ordersList = ordersAdapter.getOrdersList();
            if (ordersList == null) {
                return;
            }

            if (eventBusResponse.type == EventBusResponse.CANCEL_ORDER) {
                String orderId = eventBusResponse.orderId;
                if (orderId != null) {
                    changetOrderStatusToCancel(ordersList, orderId);
                }
            }
            if (eventBusResponse.type == EventBusResponse.ORDER_UPDATE) {
                String orderId = eventBusResponse.orderId;
                String grandToatl = eventBusResponse.grandTotal;
                if (orderId != null && grandToatl != null) {
                    updateOrderAmount(ordersList, orderId, grandToatl);
                }
            }
        }
    }

    private void updateOrderAmount(List<Orders> ordersList, String orderId, String grandToatl) {
        for (int i = 0; i < ordersList.size(); i++) {
            if (ordersList.get(i).orderId.equalsIgnoreCase(orderId)) {
                ordersList.get(i).grandTotal = grandToatl;
                setListAdapter(ordersList);
            }
        }
    }

    private void changetOrderStatusToCancel(List<Orders> ordersList, String orderId) {
        for (int i = 0; i < ordersList.size(); i++) {
            if (ordersList.get(i).orderId.equalsIgnoreCase(orderId)) {
                ordersList.get(i).orderStatus = "4";
                setListAdapter(ordersList);
            }
        }

    }

    private void setListAdapter(List<Orders> ordersList) {
        if (ordersList == null) {
            ordersList = new ArrayList<>();
        }
        if (ordersAdapter != null) {
            ordersAdapter.setAdapterData(ordersList);
        }
        checkEmptyScreen();
    }

    private void checkEmptyScreen() {
        if (ordersAdapter != null) {
            if (ordersAdapter.getOrdersList().size() > 0) {
                setEmptyScreen(false);
            } else {
                setEmptyScreen(true);
            }
        } else {
            setEmptyScreen(true);
        }
    }

    @Override
    public void onApiFinished(ApiCallResponse responseBus) {
        try {
            switch (responseBus.FROM) {
                case FROM_GET_ORDERS:
                    getOrderResponse(responseBus.response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}