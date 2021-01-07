package com.rockchipme.app.activities;

import android.os.Bundle;
import android.webkit.WebView;

import com.rockchipme.app.R;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.helpers.EventBusResponse;
import com.rockchipme.app.models.TermsAndConditionsResponse;
import com.rockchipme.app.network.ApiCallRequest;
import com.rockchipme.app.network.ApiCallResponse;
import com.rockchipme.app.network.ApiCallServiceTask;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Alisons on 4/25/2018.
 */

public class TermsAndCondActivity extends BaseActivity implements ApiCallServiceTask.onApiFinish{
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_terms;
    }

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpToolBar(false, "TERMS AND CONDITIONS", false);

        termsAndConditionsApi();
        webView = findViewById(R.id.webView);
//        hideSearchMenu();
    }

    private final String FROM_TERMS_AND_CONDI = "FROM_TERMS_AND_CONDI";
    private void termsAndConditionsApi() {
        RequestBody body=new FormBody.Builder()
                .add("rest_key", Constants.REST_KEY)
                .add("api_key", apiKey)
                .build();
        apiCallServiceTask.requestApi(new ApiCallRequest(FROM_TERMS_AND_CONDI,  Constants.termsAndConditions_api, body, true,"Loading..."));
    }

    private void termsAndConditionsApiResponse(String response) {
        try {
            TermsAndConditionsResponse response1 = new Gson().fromJson(response, TermsAndConditionsResponse.class);

            if (response1 != null && response1.responseText != null && response1.responseText.termsData != null &&
                    response1.responseText.termsData.termsContent != null) {
                webView.loadData(response1.responseText.termsData.termsContent, "text/html", "UTF-8");
                setEmptyScreen(false);
            } else {
                setEmptyScreen(true);
            }
        }catch (Exception e){e.printStackTrace();}
    }



    @Subscribe (threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusResponse eventBusResponse){

    }

    @Override
    public void onApiFinished(ApiCallResponse responseBus) {
        switch (responseBus.FROM){
            case FROM_TERMS_AND_CONDI:
                termsAndConditionsApiResponse(responseBus.response);
                break;
        }
    }
}
