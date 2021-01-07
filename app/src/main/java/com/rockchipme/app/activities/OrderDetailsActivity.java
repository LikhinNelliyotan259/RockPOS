package com.rockchipme.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.rockchipme.app.R;
import com.rockchipme.app.adapters.OrderedItemsAdapter;
import com.rockchipme.app.helpers.AddressHelper;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.helpers.EventBusResponse;
import com.rockchipme.app.helpers.Utils;
import com.rockchipme.app.models.OrderDetailsResponse;
import com.rockchipme.app.models.Orders;
import com.rockchipme.app.models.Products;
import com.rockchipme.app.network.ApiCallRequest;
import com.rockchipme.app.network.ApiCallResponse;
import com.rockchipme.app.network.ApiCallServiceTask;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Alisons on 9/27/2017.
 */
public class OrderDetailsActivity extends BaseActivity implements ApiCallServiceTask.onApiFinish{

    boolean isFromNotification = false;
    TextView tvOrderId,tvOrderedDate, tvOrderStatus, tvOrderStatusValue, tvTotal, tvTax,
            tvDelivery, tvDiscountAmount, tvGrandTotal, tvDeliveryAddress, tvEmail;
    Button btnOrderCancel;
    OrderedItemsAdapter orderedItemsAdapter;
    String orderId;
    Orders orders = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_ordered_details;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        isFromNotification = getIntent().getBooleanExtra("isFromNotification", false);

        initialize();
        setUpToolBar(false, "ORDER DETAILS", false);

        orderId = getIntent().getStringExtra("orderId");

        if (orderId!=null){
            getOrderDetailsApi();
        }
        btnOrderCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderedItemsAdapter!=null) {
                    orderedItemsAdapter.showWarningDialog("Cancel your order",null,"", true);
                }
            }
        });
    }

    private void initialize() {

        tvOrderId = findViewById(R.id.tvOrderId);
        tvOrderedDate = findViewById(R.id.tvOrderedDate);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        tvOrderStatusValue = findViewById(R.id.tvOrderStatusValue);

        tvEmail = findViewById(R.id.tvEmail);
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress);

        tvTotal = findViewById(R.id.tvTotal);
        tvDelivery = findViewById(R.id.tvDeliveryFeePrice);
        tvGrandTotal = findViewById(R.id.tvGrandTotal);
        tvTax = findViewById(R.id.tvStateTax);
        tvDiscountAmount = findViewById(R.id.tvSavedPrice);

        findViewById(R.id.ivRemoveCoupon).setVisibility(View.GONE);

        btnOrderCancel = findViewById(R.id.btnCancelOrder);

        RecyclerView rvOrderedItems = findViewById(R.id.rvOrderedItems);
        orderedItemsAdapter = new OrderedItemsAdapter(this);
        rvOrderedItems.setAdapter(orderedItemsAdapter);
    }

    private final String FROM_GET_ORDER_DETAILS = "FROM_GET_ORDER_DETAILS";
    private void getOrderDetailsApi(){
        RequestBody body=new FormBody.Builder()
                .add("rest_key", Constants.REST_KEY)
                .add("api_key", apiKey)
                .add("orderId", orderId)
                .build();
        apiCallServiceTask.requestApi(new ApiCallRequest(FROM_GET_ORDER_DETAILS,Constants.getOrderDetails_api, body, true,ApiCallRequest.WHITE));
    }

    private final String FROM_EDIT_ORDER = "FROM_EDIT_ORDER";
    public void editOrdersApi(final Products product, String quantity) {
        if (prefLogin==null || orders==null || orders.orderStatus==null || !orders.orderStatus.equalsIgnoreCase("0") ||
                product == null || product.pdtId == null){
            orderedItemsAdapter.isUpdateQuantityLoading = false;
            Log.e(Constants.APP_TAG,"editOrdersApi() return");
            return;
        }
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("rest_key", Constants.REST_KEY);
        builder.add("api_key", prefLogin.getString(Constants.api_key, ""));
        builder.add("orderId", orders.orderId);
        builder.add("pdtId", product.pdtId);
        builder.add("cart_id", product.cartId);
        builder.add("quantity", quantity);


        if (product.selectedForce != null && product.selectedForce.size() > 0) {
            builder.add("force", product.selectedForce.get(0).pdtId);
        }
        if (product.unit_id != null && product.unit_id.trim().length() > 0) {
            builder.add("unit", product.unit_id);
        }
        if (product.selectedModifiers != null && product.selectedModifiers.size() > 0) {
            String comma = "";
            StringBuilder modifiers = new StringBuilder();
            for (Products i : product.selectedModifiers) {
                modifiers.append(comma).append(i.pdtId);
                comma = ",";
            }
            builder.add("modifiers", modifiers.toString());
        }


        apiCallServiceTask.requestApi(new ApiCallRequest(FROM_EDIT_ORDER, Constants.editOrder_api, builder.build(), true,ApiCallRequest.TRANSPARENT));
    }

    private final String FROM_CANCEL_ORDER = "FROM_CANCEL_ORDER";
    public void cancelOrderApi(){
        if (orders==null || orders.orderId==null || prefLogin==null){
            return;
        }
        RequestBody body=new FormBody.Builder()
                .add("rest_key", Constants.REST_KEY)
                .add("api_key", prefLogin.getString(Constants.api_key, ""))
                .add("orderId", orders.orderId)
                .build();
        apiCallServiceTask.requestApi(new ApiCallRequest(FROM_CANCEL_ORDER,  Constants.cancelOrder_api, body, true,ApiCallRequest.TRANSPARENT));
    }


    private void getOrderDetailsApiResponse(String response) {
        try {
            OrderDetailsResponse orderDetailsResponse = new Gson().fromJson(response, OrderDetailsResponse.class);
            tvOrderStatus.setText(R.string.order_status);

            if (orderDetailsResponse == null || orderDetailsResponse.responseText == null) {
                return;
            }
            if (orderDetailsResponse.responseText.orders !=null){
                orders = orderDetailsResponse.responseText.orders;
            }


//            switch (orderDetailsResponse.responseText.success){
//                case ""
//            }
            if (orderDetailsResponse.responseText.success == 4) {
                if (orders!=null){
                    orders.orderStatus="4";
                }
                try{
                    EventBusResponse eventBusResponse = new EventBusResponse();
                    eventBusResponse.type = EventBusResponse.CANCEL_ORDER;
                    eventBusResponse.orderId = orderId;
                    EventBus.getDefault().post(eventBusResponse);
                } catch (Exception e){e.printStackTrace();}
                showSuccessDialog(orderDetailsResponse.responseText.message);
            } else  if (orderDetailsResponse.responseText.success != 1) {
                showErrorDialog(orderDetailsResponse.responseText.message);
            }
            initializeResponsedata(orders);
        } catch (Exception e){e.printStackTrace();}
    }

    private void initializeResponsedata(Orders orders) {

//        tvOrderStatus.setText(R.string.deliver_date);
        if (orders==null){
//            finish();
            Log.e(Constants.APP_TAG, "order object is null");
            return;
        }
        btnOrderCancel.setVisibility(View.GONE);
        switch (orders.orderStatus){
            case "0":
                tvOrderStatusValue.setText("Pending");
                btnOrderCancel.setVisibility(View.VISIBLE);
                break;
            case "1":
                tvOrderStatusValue.setText("Confirmed");
                break;
            case "2":
                tvOrderStatusValue.setText("In delivery process");
                break;
            case "3":
                tvOrderStatus.setText(R.string.deliver_date);
                tvOrderStatusValue.setText(orders.deliveryDate);
                break;
            case "4":
                tvOrderStatusValue.setText("Cancelled");
                break;
        }

//        "orderStatus = 0-pending,1-confirmed,2-In delivery procees,3-delivered,4-canncelled
//        eg. deliveryDate=2018-04-13 15:15:15"

        tvDeliveryAddress.setText(new AddressHelper(this).getAddress(orders.deliveyAddress, true));
        tvEmail.setText(prefLogin.getString(Constants.email,""));

        tvOrderId.setText("Order ID " + orders.orderId);
        tvOrderedDate.setText(Utils.convertDateFormat(orders.orderedDate));
        tvGrandTotal.setText(currency + " " + orders.grandTotal);

        try{
            EventBusResponse eventBusResponse = new EventBusResponse();
            eventBusResponse.type = EventBusResponse.ORDER_UPDATE;
            eventBusResponse.orderId = orderId;
            eventBusResponse.grandTotal = orders.grandTotal;
            EventBus.getDefault().post(eventBusResponse);
        } catch (Exception e){e.printStackTrace();}

//        tvTax.setText(orders.tax);
        if (orders.couponsList!=null && orders.couponsList.size()>0) {
            tvDiscountAmount.setText("- " + currency + " " + orders.couponDiscountAmount);
            if (findViewById(R.id.groupCoupon)!=null)
                findViewById(R.id.groupCoupon).setVisibility(View.VISIBLE);
        } else {
            if (findViewById(R.id.groupCoupon)!=null)
                findViewById(R.id.groupCoupon).setVisibility(View.GONE);
        }
        tvDelivery.setText(currency + " " + orders.deliveryCharge);
        tvTotal.setText(currency + " " + orders.total);

        setListAdapter(orders);
    }

    @Override
    public void onBackPressed() {

        if(isFromNotification){
            setResult(Activity.RESULT_CANCELED);
            Intent mIntent = new Intent(getApplicationContext(), HomeScreenActivity.class);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mIntent);
            finish();
            ActivityCompat.finishAffinity(OrderDetailsActivity.this);
        }
        super.onBackPressed();
    }

    private void setListAdapter(Orders orders){
        if (orderedItemsAdapter != null){
            orderedItemsAdapter.setAdapterData(orders, currency);
            orderedItemsAdapter.enableQuantityUpdate();
        }
    }

    private void showErrorDialog(String title){
        title = title==null ? "" : title;
        Log.e(Constants.APP_TAG,"showErrorDialog()");
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(title)
                .show();

    }
    private void showSuccessDialog(String title){
        title = title==null ? "" : title;
        Log.e(Constants.APP_TAG,"showSuccessDialog()");
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Success")
                .setContentText(title)
                .show();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusResponse eventBusResponse){

    }

    @Override
    public void onApiFinished(ApiCallResponse responseBus) {
        getOrderDetailsApiResponse(responseBus.response);
    }
}
