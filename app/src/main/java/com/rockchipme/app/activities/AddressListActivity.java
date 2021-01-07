package com.rockchipme.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rockchipme.app.R;
import com.rockchipme.app.adapters.AddressAdapter;
import com.rockchipme.app.helpers.ActivityRequestCode;
import com.rockchipme.app.helpers.AddressHelper;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.helpers.EventBusResponse;
import com.rockchipme.app.models.AddressResponse;
import com.rockchipme.app.network.ApiCallRequest;
import com.rockchipme.app.network.ApiCallResponse;
import com.rockchipme.app.network.ApiCallServiceTask;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Alisons on 9/25/2017.
 */
public class AddressListActivity extends BaseActivity implements ApiCallServiceTask.onApiFinish {

    private Button btn_createAddress;
    private List<AddressResponse.Address> addressList;
    private AddressAdapter adapterAddress;
    private AddressHelper addressHelper;

//    SharedPreferences pref_activities;
//    SharedPreferences.Editor activities_editor;


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_address_list;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        addressHelper = new AddressHelper(this);

//        pref_activities = getSharedPreferences(Constants.PREF_REFRESH_ACTIVITIES,MODE_PRIVATE);
//        activities_editor = pref_activities.edit();
//        activities_editor.putBoolean(Constants.selectAddressActivity,false);
//        activities_editor.commit();

        initialize();
        setUpToolBar(false, "SELECT ADDRESS", false);

        getAddress();

        // set the adapter object to the Recyclerview

        btn_createAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressListActivity.this, CreateAddressActivty.class);
                intent.putExtra("use", "create");
                intent.putExtra("from", "selectAddress");
                startActivityForResult(intent, ActivityRequestCode.RC_ADDRESS_SAVED);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (resultCode==RESULT_OK) {
        if (requestCode == ActivityRequestCode.RC_ADDRESS_SAVED) {
            if (resultCode == RESULT_OK) {
                Log.e(Constants.APP_TAG, "result received");
                getAddress();
            }
        }
//        }
    }


    private final String FROM_GET_ADDRESS = "FROM_GET_ADDRESS";

    private void getAddress() {
        RequestBody body = new FormBody.Builder()
//                .add("api_key",prefLogin.getString(Constants.api_key,""))
                .add("api_key", apiKey)
                .add("rest_key", Constants.REST_KEY)
                .build();
        apiCallServiceTask.requestApi(new ApiCallRequest(FROM_GET_ADDRESS, Constants.getAddress_api, body, true, ApiCallRequest.WHITE));
    }

    private final String FROM_DELETE_ADDRESS = "FROM_DELETE_ADDRESS";

    public void deleteAddressApi(String addressId) {
        RequestBody body = new FormBody.Builder()
                .add("api_key", apiKey)
                .add("rest_key", Constants.REST_KEY)
                .add("addressId", addressId)
                .build();
//        apiCallServiceTask.requestApi(ApiResponseBus.RC_DELETE_ADDRESS,true,body, Constants.SERVER_URL+ Constants.deleteAddress_api,"");
        apiCallServiceTask.requestApi(new ApiCallRequest(FROM_DELETE_ADDRESS, Constants.deleteAddress_api, body, true, ApiCallRequest.TRANSPARENT));
    }

    private void initialize() {
        RecyclerView rv_address = (RecyclerView) findViewById(R.id.rv_address);
        btn_createAddress = (Button) findViewById(R.id.btn_create_address);

        rv_address.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayout.VERTICAL, false);
        rv_address.setLayoutManager(layoutManager);

        boolean isCrateNewAddress = false;
        try {
            if (getIntent().getExtras() != null) {
                if (getIntent().getExtras().getString("from", "").equalsIgnoreCase("createAddress")) {
                    isCrateNewAddress = true;

                }
            }
        } catch (Exception e) {
            Log.e(Constants.APP_TAG, "selectAddressActivity excep1: " + e.toString());
        }
        adapterAddress = new AddressAdapter(this, isCrateNewAddress, prefLogin);
        rv_address.setAdapter(adapterAddress);

    }

    private void getAddressResponse(String response) {
        try {
            AddressResponse addressResponse = new Gson().fromJson(response, AddressResponse.class);
            if (addressResponse == null || addressResponse.responseText == null) {
                Log.e(Constants.APP_TAG, "getAddressResponse() return");
                return;
            }
            if (addressResponse.responseText.success == 1) {
                addressList = addressResponse.responseText.addressList;
            } else if (addressResponse.responseText.status != null && !addressResponse.responseText.status.equals("")) {
                addressHelper.clearSharedPrefernce();
                Toast.makeText(AddressListActivity.this, addressResponse.responseText.status, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyAdapterDatas(addressList);

    }

    private void deleteAddress(JSONObject response) {
        JSONObject jsonObject = response.optJSONObject("response_text");
        if (response.optString("response_code").trim().equals("200")) {
//            refreshActivities();
            Log.e(Constants.APP_TAG, "deleted success");

            if (prefLogin.getBoolean(Constants.isAddressAdded, false)) {
                if (adapterAddress.getAddressId().equalsIgnoreCase(prefLogin.getString(Constants.addressId, ""))) {
                    addressHelper.clearSharedPrefernce();
                }
            }
//            getAddress();
            addressList.remove(adapterAddress.getDeleted_position());
            Log.e(Constants.APP_TAG, "address deleted position: " + adapterAddress.getDeleted_position());
            adapterAddress.notifyItemRemoved(adapterAddress.getDeleted_position());
//            adapterAddress.notifyItemRangeChanged(adapterAddress.getDeleted_position(), adapterAddress.getAddressList().size());
            adapterAddress.notifyDataSetChanged();
        } else {
            Toast.makeText(AddressListActivity.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
        }

        checkEmptyScreen();
    }

    @Override
    public void onBackPressed() {
        try {
            if (addressList != null && addressList.size() > 0) {
//                addressHelper.storeAddress(addressList, adapterAddress.getSelectedPosition());
                int position = addressList.size() > adapterAddress.getSelectedPosition() ? adapterAddress.getSelectedPosition() : 0;
                addressHelper.storeAddress(addressList.get(position));
            }
        } catch (Exception e) {
        }
        super.onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusResponse eventBusResponse) {

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (pref_activities.getBoolean(Constants.selectAddressActivity,false)){
//            getAddress();
//        }
//    }

    private void notifyAdapterDatas(List<AddressResponse.Address> addressList) {
        if (adapterAddress != null) {
            if (addressList == null) {
                addressList = new ArrayList<>();
            }
            adapterAddress.setAddressList(addressList);
        }
        checkEmptyScreen();
    }

    private void checkEmptyScreen() {
        if (adapterAddress != null) {
            if (adapterAddress.getAddressList().size() > 0) {
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
            String response = responseBus.response;
            JSONObject object = new JSONObject(response);

            switch (responseBus.FROM) {
                case FROM_GET_ADDRESS:
                    getAddressResponse(response);
                    break;
                case FROM_DELETE_ADDRESS:
                    deleteAddress(object);
                    break;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
