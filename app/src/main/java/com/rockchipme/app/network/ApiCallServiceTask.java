package com.rockchipme.app.network;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.rockchipme.app.BuildConfig;
import com.rockchipme.app.R;
import com.rockchipme.app.helpers.Constants;

import java.io.IOException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by Sibin on 13/08/2018.
 */

public class ApiCallServiceTask {
    private onApiFinish onApiFinishListener;
    private Context context;

    private LinearLayout rlProgressBarMain;
    private ProgressBar progressBar;

    private OkHttpClient client = new OkHttpClient();

    public ApiCallServiceTask(Context context) {
        this.context = context;
        onApiFinishListener = (onApiFinish) context;

        rlProgressBarMain = ((Activity) context).findViewById(R.id.llProgressBar);
        progressBar = ((Activity) context).findViewById(R.id.progressBar);
    }

    public void requestApi(final ApiCallRequest apiCallRequest) {
        if (apiCallRequest.showProgress) {
//            showProgress(apiCallRequest.title);
            showProgressBar(apiCallRequest.progressBg);
        } else {
//            closeProgress();
            hideProgressBar(false, apiCallRequest.progressBg);
        }

//        if (apiCallRequest.from.equals(SearchActivity.FROM_SEARCH)) {
//            cancelApiCallWithTag(client, apiCallRequest.from);
//        }
        cancelApiCallWithTag(client, apiCallRequest.from);

        final Request request;//= new Request.Builder().url(url).post(body).build();

        Request.Builder builder = new Request.Builder();


        String URL = apiCallRequest.url;

        if (!URL.contains(Constants.SERVER_URL)) {
            URL = Constants.SERVER_URL + apiCallRequest.url;
        }

        if (Constants.HASH_KEY == null || Constants.HASH_KEY.trim().length() < 1) {
            try {
                if (!FacebookSdk.isInitialized()) {
                    FacebookSdk.sdkInitialize(context.getApplicationContext());
                }
                Constants.HASH_KEY = FacebookSdk.getApplicationSignature(context);
                Constants.HASH_KEY = URLEncoder.encode(Constants.HASH_KEY, "UTF-8");
//            Log.d(Constants.APP_TAG, "HASH_KEY:"+Constants.HASH_KEY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        builder.addHeader("key", Constants.HASH_KEY);
        builder.addHeader("platform", "ANDROID");
        builder.tag(apiCallRequest.from);
        builder.url(URL);
        if (apiCallRequest.requestBody != null) {
            builder.post(apiCallRequest.requestBody);
        }
        request = builder.build();


        try {
            if (BuildConfig.DEBUG) {
                Log.d(Constants.APP_TAG, apiCallRequest.from + "api url: " + URL);
                if (request.body() != null) {
                    final Buffer buffer = new Buffer();
                    request.body().writeTo(buffer);
                    Log.d(Constants.APP_TAG, apiCallRequest.from + " api parameters: " + buffer.readUtf8());
                }

                if (request.headers() != null) {
                    Log.d(Constants.APP_TAG, apiCallRequest.from + " api Headers: " + request.headers().toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                try {
                    ((Activity) context).runOnUiThread(() -> {
                        ApiCallResponse apiCallResponse = new ApiCallResponse();
                        apiCallResponse.FROM = apiCallRequest.from;
                        apiCallResponse.ERROR_TYPE = ApiCallResponse.FAILED;
                        apiCallResponse.response = null;
                        apiCallResponse.apiCallRequest = apiCallRequest;

                        if (!call.isCanceled()) {
                            onApiFinishListener.onApiFinished(apiCallResponse);
                        }

                    });
                    Log.e(Constants.APP_TAG, "ApiCallServiceTask: Connection Failed, exception : " + e.getMessage());
                    e.printStackTrace();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
//                closeProgress();
                hideProgressBar(true, apiCallRequest.progressBg);
                connectionFailedToast(call);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String resp = "";
                if (response != null && response.body() != null) {
                    resp = response.body().string();
                }
                final String response2 = resp;
                Log.d(Constants.APP_TAG, apiCallRequest.from + " response: " + response2);
                try {
                    ((Activity) context).runOnUiThread(() -> {
                        ApiCallResponse apiCallResponse = new ApiCallResponse();
                        apiCallResponse.FROM = apiCallRequest.from;
                        apiCallResponse.ERROR_TYPE = ApiCallResponse.SUCCESS;
                        apiCallResponse.response = response2;
                        apiCallResponse.apiCallRequest = apiCallRequest;

                        if (response2 == null || response2.trim().length() < 1) {
                            apiCallResponse.ERROR_TYPE = ApiCallResponse.JSON_ERROR;
                        }
                        onApiFinishListener.onApiFinished(apiCallResponse);
                        hideProgressBar(apiCallResponse.ERROR_TYPE != ApiCallResponse.SUCCESS, apiCallRequest.progressBg);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void connectionFailedToast(Call call) {
        ((Activity) context).runOnUiThread(() -> {
            if (call != null && !call.isCanceled()) {
                if (isNetworkAvailable(context)) {
                    Toast.makeText(context, "Please connect internet", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(context, "Connection Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelApiCallWithTag(OkHttpClient client, Object tag) {
        try {
            for (Call call : client.dispatcher().queuedCalls()) {
                if (call.request().tag().equals(tag)) {
                    call.cancel();
                    Log.e(Constants.APP_TAG, "ApiCall isCanceled:" + call.isCanceled());
                }
            }
            for (Call call : client.dispatcher().runningCalls()) {
                if (call.request().tag().equals(tag)) {
                    call.cancel();
                    Log.e(Constants.APP_TAG, "ApiCall isCanceled:" + call.isCanceled());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface onApiFinish {
        void onApiFinished(ApiCallResponse responseBus);
    }

    private void hideProgressBar(final boolean isApiFailed, final String progressBg) {
        ((Activity) context).runOnUiThread(() -> {
            try {
                if (isApiFailed) {
                    if (progressBg != null && progressBg.equals(ApiCallRequest.TRANSPARENT)) {
                        if (rlProgressBarMain != null) {
                            rlProgressBarMain.setVisibility(View.GONE);
                        }
                    } else {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                } else {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    if (rlProgressBarMain != null) {
                        rlProgressBarMain.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void showProgressBar(final String progressBg) {
        ((Activity) context).runOnUiThread(() -> {
            try {
                if (rlProgressBarMain != null) {
                    if (progressBg != null && progressBg.equals(ApiCallRequest.TRANSPARENT)) {
                        rlProgressBarMain.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                    } else {
                        rlProgressBarMain.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    }
                    if (rlProgressBarMain != null) {
                        rlProgressBarMain.setVisibility(View.VISIBLE);
                    }

                }
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = null;
        if (cm != null) {
            info = cm.getActiveNetworkInfo();
        }
        return info != null && info.isAvailable() && info.isConnected();
    }

}
