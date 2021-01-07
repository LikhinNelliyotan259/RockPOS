package com.rockchipme.app.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.rockchipme.app.R;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.helpers.EventBusResponse;
import com.rockchipme.app.helpers.Validations;
import com.rockchipme.app.models.AddressResponse;
import com.rockchipme.app.network.ApiCallRequest;
import com.rockchipme.app.network.ApiCallResponse;
import com.rockchipme.app.network.ApiCallServiceTask;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Alisons on 9/23/2017.
 */
public class CreateAddressActivty extends BaseActivity implements ApiCallServiceTask.onApiFinish {

    private Validations validations;

    private Button btn_save;
    private TextInputLayout tilFullName, tilMobile, tilPincode, tilHouseNo, tilStreetName, tilLandmark, tilTown, tilState, tilAddressType;
    private EditText etFullName, etMobile, etPincode, etHouseNo, etStreetName, etLandmark, etTown, etState, etPhoneNumberCode;
    private RadioGroup rg_addressType;
    private RadioButton rb_home, rb_office;

    boolean reStoreAddress;

    String addressId, streetName;

    double latitude, longtitude;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activty_create_address;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        validations = new Validations();
        reStoreAddress = false;

        initialize();
        setUpToolBar(false, "DELIVERY ADDRESS", false);

//        if (getIntent().getStringExtra("use")!=null && getIntent().getStringExtra("use").equalsIgnoreCase("edit")){
//
//            if (getIntent().getStringExtra("address_id")!=null && !getIntent().getStringExtra("address_id").trim().equals("")) {
//                address_id = getIntent().getStringExtra("address_id");
//                getAddress(address_id);
//            }
//        }

        if (getIntent().getStringExtra("address_id") != null && !getIntent().getStringExtra("address_id").trim().equals("")) {
            addressId = getIntent().getStringExtra("address_id");
            getAddress(addressId);
        }

        etStreetName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(CreateAddressActivty.this), Constants.PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.e(Constants.APP_TAG, "CreateAddressActivty excep: " + e.toString());
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e(Constants.APP_TAG, "CreateAddressActivty excep 2: " + e.toString());
                }
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressValidation();
            }
        });
    }

    private final String FROM_GET_ADDRESS = "FROM_GET_ADDRESS";

    private void getAddress(String address_id) {
        RequestBody body = new FormBody.Builder()
//                .add("api_key",getSharedPreferences(Constants.PREF_LOGIN,MODE_PRIVATE).getString(Constants.api_key,""))
                .add("api_key", apiKey)
                .add("rest_key", Constants.REST_KEY)
                .add("addressId", address_id)
                .build();
        apiCallServiceTask.requestApi(new ApiCallRequest(FROM_GET_ADDRESS, Constants.getAddress_api, body, true, ApiCallRequest.TRANSPARENT));
    }

    private void initialize() {
        tilFullName = (TextInputLayout) findViewById(R.id.til_fullName);
        tilHouseNo = (TextInputLayout) findViewById(R.id.tilHouseNo);
        tilMobile = (TextInputLayout) findViewById(R.id.til_mobileNo);

        tilPincode = findViewById(R.id.tilPincode);
        tilLandmark = findViewById(R.id.tilLandmark);
        tilTown = findViewById(R.id.tilTown);

        tilStreetName = (TextInputLayout) findViewById(R.id.til_streetName);
        tilState = (TextInputLayout) findViewById(R.id.tilState);
        tilAddressType = (TextInputLayout) findViewById(R.id.til_addressType);


        etFullName = (EditText) findViewById(R.id.et_fullName);
        etHouseNo = (EditText) findViewById(R.id.etHouseNo);
        etPincode = (EditText) findViewById(R.id.etPincode);
        etMobile = (EditText) findViewById(R.id.et_mobileNo);
        etStreetName = (EditText) findViewById(R.id.et_streetName);
        etLandmark = findViewById(R.id.etLandmark);
        etState = (EditText) findViewById(R.id.etState);
        etTown = findViewById(R.id.etTown);
        etPhoneNumberCode = (EditText) findViewById(R.id.etPhoneNumberCode);

        btn_save = (Button) findViewById(R.id.btn_saveAddress);

        rg_addressType = (RadioGroup) findViewById(R.id.rg_addressType);
        rb_home = (RadioButton) findViewById(R.id.rb_home);
        rb_office = (RadioButton) findViewById(R.id.rb_office);

        etStreetName.setFocusable(false);
        etPhoneNumberCode.setFocusable(false);
        etPhoneNumberCode.setInputType(InputType.TYPE_NULL);

        setFont();
    }


    public void setFont() {
        Typeface font_yekan = Typeface.createFromAsset(getAssets(), "Montserrat_SemiBold_2.otf");
        tilFullName.setTypeface(font_yekan);

    }

    private final String FROM_ADD_ADDRESS = "FROM_ADD_ADDRESS";
    private void addressValidation() {
        String type, fullName, mobile, pincode, landmark, houseNo, state, town;
        tilFullName.setError(null);
        tilMobile.setError(null);
        tilHouseNo.setError(null);
        tilPincode.setError(null);
        tilTown.setError(null);
        tilState.setError(null);
        tilStreetName.setError(null);

        tilAddressType.setError(null);


        fullName = etFullName.getText().toString().trim();
        mobile = etMobile.getText().toString().trim();
        pincode = etPincode.getText().toString().trim();

        landmark = etLandmark.getText().toString().trim();
        houseNo = etHouseNo.getText().toString().trim();
        streetName = etStreetName.getText().toString().trim();
        state = etState.getText().toString().trim();
        town = etTown.getText().toString().trim();

        switch (rg_addressType.getCheckedRadioButtonId()) {
            case R.id.rb_home:
                type = "home";
                break;
            case R.id.rb_office:
                type = "office";
                break;
            default:
                type = "";
                break;
        }


        if (type == null || type.trim().equals("")) {
            tilAddressType.setError(getString(R.string.error_field_required));
            rg_addressType.requestFocus();
        } else if (TextUtils.isEmpty(fullName)) {
            tilFullName.setError(getString(R.string.error_field_required));
            etFullName.requestFocus();
//        } else if (!validations.isValidName(fullName)){
//            tilFullName.setError(getString(R.string.error_field_required));
//            etFullName.requestFocus();
        } else if (TextUtils.isEmpty(mobile)) {
            tilMobile.setError(getString(R.string.error_field_required));
            etMobile.requestFocus();
        } else if (!validations.isValidPhone(mobile)) {
            tilMobile.setError(getString(R.string.error_invalid_phone));
            etMobile.requestFocus();
        } else if (TextUtils.isEmpty(houseNo)) {
            tilHouseNo.setError(getString(R.string.error_field_required));
            etHouseNo.requestFocus();
        } else if (TextUtils.isEmpty(pincode)) {
            tilPincode.setError(getString(R.string.error_field_required));
            etPincode.requestFocus();
        } else if (pincode.length() != 6) {
            tilPincode.setError(getString(R.string.error_invalid_pincode));
            etPincode.requestFocus();
        } else if (TextUtils.isEmpty(streetName)) {
            tilStreetName.setError(getString(R.string.error_field_required));
            etStreetName.requestFocus();
            etStreetName.setFocusable(true);
        } else if (TextUtils.isEmpty(town)) {
            tilTown.setError(getString(R.string.error_field_required));
            etTown.requestFocus();
        } else if (TextUtils.isEmpty(state)) {
            tilState.setError(getString(R.string.error_field_required));
            etState.requestFocus();
        } else {

            hideKeyBoard();

            String url = Constants.addAddress_api;
            FormBody.Builder builder = new FormBody.Builder();
            builder.add("rest_key", Constants.REST_KEY);
            builder.add("api_key", getSharedPreferences(Constants.PREF_LOGIN, MODE_PRIVATE).getString(Constants.api_key, ""));
            if (addressId != null && !addressId.equals("")) {
                builder.add("addressId", addressId);
                url = Constants.updateAddress_api;
            }
            builder.add("addressType", type);
            builder.add("fullName", fullName);
            builder.add("mobileNumber", mobile);
            builder.add("pinCode", pincode);
            builder.add("houseNo", houseNo);
            builder.add("street", streetName);
            builder.add("landmark", landmark);
            builder.add("town", town);
            builder.add("state", state);
            builder.add("latitude", String.valueOf(latitude));
            builder.add("longitude", String.valueOf(longtitude));
            RequestBody body = builder.build();
            apiCallServiceTask.requestApi(new ApiCallRequest(FROM_ADD_ADDRESS, url, body, true, ApiCallRequest.TRANSPARENT));


//            if (extras != null) {
//                if (extras.getString("use", "").equalsIgnoreCase("edit")) {
//                    address_id = extras.getString("address_id");
//                    if (getSharedPreferences(Constants.PREF_LOGIN, MODE_PRIVATE).getBoolean(Constants.isAddressAdded, false)) {
//                        if (getSharedPreferences(Constants.PREF_LOGIN, MODE_PRIVATE).getString(Constants.addressId, "").equalsIgnoreCase(address_id)) {
//                            editAddress(address_id, true);
//                            return;
//                        }
//                    }
//                    editAddress(address_id, false);
//                } else {
//                    createAddress();
//                }
//
//            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                LatLng lat = place.getLatLng();

                latitude = lat.latitude;
                longtitude = lat.longitude;

                etStreetName.setText(place.getName().toString());
                streetName = place.getName().toString();
                Log.e(Constants.APP_TAG, "Address :" + place.getAddress().toString());
            }
        }
    }

    public void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void getAddressResponse(String response) {
        AddressResponse addressResponse = new Gson().fromJson(response, AddressResponse.class);
        if (addressResponse != null && addressResponse.responseText != null && addressResponse.responseText.addressList != null) {
            setAddressToFields(addressResponse.responseText.addressList);
        } else {
            Toast.makeText(CreateAddressActivty.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void setAddressToFields(List<AddressResponse.Address> addressList) {
        if (addressList != null && !addressList.isEmpty()) {
            for (int i = 0; i < addressList.size(); i++) {
                if (addressId != null && addressId.equalsIgnoreCase(addressList.get(i).addressId)) {
                    if (addressList.get(i).addressType.equalsIgnoreCase("office")) {
                        rb_office.setChecked(true);
                    } else {
                        rb_home.setChecked(true);
                    }
                    etFullName.setText(addressList.get(i).fullName);
                    etStreetName.setText(addressList.get(i).street);
                    etMobile.setText(addressList.get(i).mobileNumber);
                    etHouseNo.setText(addressList.get(i).houseNo);
                    etPincode.setText(addressList.get(i).pinCode);
                    etLandmark.setText(addressList.get(i).landmark);
                    etTown.setText(addressList.get(i).town);
                    etState.setText(addressList.get(i).state);
//                    et.setText(addressList.get(i).);

//                    et_moreInfo.setText(addressList.get(i).);
//                    .setText(addressList.get(i).);

                    latitude = Double.parseDouble(addressList.get(i).latitude);
                    longtitude = Double.parseDouble(addressList.get(i).longitude);
                }
            }
        }


    }

    private void createAddressResponse(JSONObject response, String resp_code) {

        JSONObject jsonObject = response.optJSONObject("response_text");
        if (jsonObject.has("message")) {
            Toast.makeText(CreateAddressActivty.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
        }

        if (resp_code.equals("200")) {
//            try{
            if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equalsIgnoreCase("basket")) {
                Intent intent1 = new Intent(this, AddressListActivity.class);
                intent1.putExtra("from", "createAddress");
                startActivity(intent1);
                finish();
                return;
            }

//            refreshActivities();
            Intent intent = getIntent();
            setResult(RESULT_OK, intent);
            finish();
//        }catch (Exception e){Log.e("")}


        }

    }

    private void editAddressResponse(JSONObject response, String resp_code) {

        JSONObject jsonObject = response.optJSONObject("response_text");
        if (jsonObject.has("message")) {
            Toast.makeText(CreateAddressActivty.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
        }
        String responseForEditAddress;
        List<AddressResponse.Address> addressList;

        if (resp_code.equals("200")) {
            if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equalsIgnoreCase("basket")) {
                Intent intent1 = new Intent(this, AddressListActivity.class);
                intent1.putExtra("from", "createAddress");
                startActivity(intent1);
                finish();
                return;
            }

//            refreshActivities();
            Intent intent = getIntent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusResponse eventBusResponse) {

    }

    @Override
    public void onApiFinished(ApiCallResponse responseBus) {
        try {
            String response = responseBus.response;
            Log.e(Constants.APP_TAG, "CreateAddressActivty response: " + response);

            JSONObject object = new JSONObject(response);

            String resp_code = object.optString("response_code");

            switch (responseBus.FROM) {
                case FROM_GET_ADDRESS:
                    getAddressResponse(response);
                    break;
//                case ApiResponseBus.RC_EDIT_ADDRESS:
//                    editAddressResponse(object, resp_code);
//                    break;
                case FROM_ADD_ADDRESS:
                    createAddressResponse(object, resp_code);
                    break;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
