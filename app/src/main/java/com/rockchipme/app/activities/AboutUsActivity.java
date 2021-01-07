package com.rockchipme.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rockchipme.app.R;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.helpers.EventBusResponse;
import com.rockchipme.app.models.AboutUsResponse;
import com.rockchipme.app.models.RestaurantDetails;
import com.rockchipme.app.network.ApiCallRequest;
import com.rockchipme.app.network.ApiCallResponse;
import com.rockchipme.app.network.ApiCallServiceTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Alisons on 4/25/2018.
 */

public class AboutUsActivity extends BaseActivity implements ApiCallServiceTask.onApiFinish {
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_about_us;
    }

    CircleImageView civLogo;
    TextView tvDescription, tvEmail, tvPhone, tvName, tvWorkingHour;
    LinearLayout llDescription, llEmail, llPhone, llContactInfo, llWorkingHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpToolBar(false, "ABOUT US", false);
        initializeViews();
        aboutUsApi();
    }

    private void initializeViews() {
        tvDescription = findViewById(R.id.tvDescription);
        tvEmail = findViewById(R.id.tvEmailContectUs);
        tvName = findViewById(R.id.tvCompanyName);
        tvPhone = findViewById(R.id.tvPhoneContectUs);
        tvWorkingHour = findViewById(R.id.tvWorkingHour);
//         = findViewById(R.id.);

        llDescription = findViewById(R.id.llDescription);
        llEmail = findViewById(R.id.llEmailContectUs);
        llPhone = findViewById(R.id.llPhoneContectUs);
        llContactInfo = findViewById(R.id.llContactInfo);
        llWorkingHour = findViewById(R.id.llWorkingHour);
//          = findViewById(R.id.);

        civLogo = findViewById(R.id.civCompanyLogo);
    }

    private final String FROM_ABOUT_US = "FROM_ABOUT_US";

    private void aboutUsApi() {
        RequestBody body = new FormBody.Builder()
                .add("rest_key", Constants.REST_KEY)
                .add("api_key", apiKey)
                .build();
//        apiCallServiceTask.requestApi(FFROM_ABOUT_US, false, body, Constants.SERVER_URL+ Constants.aboutUs_api,"Loading...");
        apiCallServiceTask.requestApi(new ApiCallRequest(FROM_ABOUT_US, Constants.aboutUs_api, body, true, ApiCallRequest.WHITE));
    }

    private void aboutUsApiResponse(String response) {
        try {
            AboutUsResponse aboutUsResponse = new Gson().fromJson(response, AboutUsResponse.class);
            if (aboutUsResponse != null && aboutUsResponse.responseText != null) {
                final RestaurantDetails aboutUsData = aboutUsResponse.responseText.aboutUsData;
                if (aboutUsData == null) {
                    setEmptyScreen(true);
                    return;
                }
                setEmptyScreen(false);

                editorCart.putString(Constants.currency, aboutUsData.currency).commit();

                tvName.setText(aboutUsData.name);
                if (checkStrigisNotNull(aboutUsData.description)) {
                    tvDescription.setText(aboutUsData.description);
                } else {
                    llDescription.setVisibility(View.GONE);
                }

                if (checkStrigisNotNull(aboutUsData.phoneNumber) || checkStrigisNotNull(aboutUsData.email)) {
                    if (checkStrigisNotNull(aboutUsData.phoneNumber)) {
                        tvPhone.setText(aboutUsData.phoneNumber);
                    } else {
                        llPhone.setVisibility(View.GONE);
                    }

                    if (checkStrigisNotNull(aboutUsData.email)) {
                        tvEmail.setText(aboutUsData.email);
                    } else {
                        llEmail.setVisibility(View.GONE);
                    }
                } else {
                    llContactInfo.setVisibility(View.GONE);
                }

                if (checkStrigisNotNull(aboutUsData.workingHours)) {
                    tvWorkingHour.setText(aboutUsData.workingHours);
                } else {
                    llWorkingHour.setVisibility(View.GONE);
                }

                try {
                    Picasso.with(this).load(aboutUsData.getLogo()).placeholder(R.drawable.app_icon).into(civLogo);
                } catch (Exception e) {
                    e.printStackTrace();
                }

//            if (llDescription.getVisibility()==View.GONE && llContactInfo.getVisibility()==View.GONE
//                    && llWorkingHour.getVisibility() == View.GONE){
//                llDescriptionMain.setVisibility(View.GONE);
//            }


                llPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", aboutUsData.phoneNumber, null));
                            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(callIntent);
                        } catch (Exception e) {
                        }
                    }
                });

                llEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                            emailIntent.setData(Uri.parse("mailto: " + aboutUsData.email));
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                            emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(Intent.createChooser(emailIntent, "Send feedback"));
                        } catch (Exception e) {
                        }
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusResponse eventBusResponse) {

    }

    private boolean checkStrigisNotNull(String str) {
        if (str == null || str.trim().length() < 1)
            return false;
        return true;
    }

    @Override
    public void onApiFinished(ApiCallResponse responseBus) {
        switch (responseBus.FROM) {
            case FROM_ABOUT_US:
                aboutUsApiResponse(responseBus.response);
                break;
        }
    }
}
