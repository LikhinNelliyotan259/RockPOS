package com.rockchipme.app.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rockchipme.app.R;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.helpers.Validations;
import com.rockchipme.app.network.ApiCallRequest;
import com.rockchipme.app.network.ApiCallResponse;
import com.rockchipme.app.network.ApiCallServiceTask;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static android.Manifest.permission.READ_CONTACTS;
import static com.rockchipme.app.helpers.Constants.APP_TAG;

/**
 * Created by Alisons on 9/16/2017.
 */
public class ForgotPasswordActivity extends Activity implements ApiCallServiceTask.onApiFinish {

    TextInputLayout tilEmail;
    AutoCompleteTextView act_forgotPaswd_Email;
    LinearLayout ln_forgotPaswd;
    Button btn_forgotPaswd_Button;
    private static final int REQUEST_READ_CONTACTS =11;

    ApiCallServiceTask okHttpCommon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        setContentView(R.layout.activity_login);
        
        setContentView(R.layout.activity_forgot_password);

        okHttpCommon = new ApiCallServiceTask(this);
        
        initialize();
        
        populateAutoComplete();

        btn_forgotPaswd_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptForgotPaswd();
            }
        });

    }

    private void initialize() {
        act_forgotPaswd_Email = findViewById(R.id.actv_forgotPaswd_Email);
        tilEmail = findViewById(R.id.til_forgotPaswd_Email);
        btn_forgotPaswd_Button = findViewById(R.id.forgotPaswd_button);
        ln_forgotPaswd = findViewById(R.id.ln_forgotPaswd);
//        pbForgotPaswd = (ProgressBar) findViewById(R.id.pbForgotPaswd);
        
        
        setFont();
    }

    public void setFont(){
        Typeface font_yekan= Typeface.createFromAsset(getAssets(), "Montserrat_SemiBold_2.otf");
        tilEmail.setTypeface(font_yekan);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        addEmailsToAutoComplete(getData());
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(act_forgotPaswd_Email, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addEmailsToAutoComplete(getData());
            }
        }
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(ForgotPasswordActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
        act_forgotPaswd_Email.setAdapter(adapter);
    }

    private ArrayList<String> getData() {
        ArrayList<String> accountsList = new ArrayList<>();
        try {
            Account[] accounts = AccountManager.get(this).getAccountsByType("com.google");
            for (Account account : accounts) {
                accountsList.add(account.name);
            }
        } catch (Exception e) {
            Log.i(APP_TAG, "Exception:" + e);
        }
        return accountsList;
    }




    private void attemptForgotPaswd() {
//        if (mAuthTask != null) {
//            return;
//        }
        tilEmail.setError(null);
        String email = act_forgotPaswd_Email.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.error_field_required));
            act_forgotPaswd_Email.requestFocus();
            return;
        }
        if (!new Validations().isValidEmail(email)) {
            tilEmail.setError(getString(R.string.error_invalid_email));
            act_forgotPaswd_Email.requestFocus();
            return;
        }
        requestForgotPaswd(email);
    }



    private final String FROM_FORGOT_PASSWORD = "FROM_FORGOT_PASSWORD";
    private void requestForgotPaswd(String email) {
        RequestBody body = new FormBody.Builder()
                .add("email",email)
                .add("rest_key", Constants.REST_KEY_MAIN)
                .build();
        okHttpCommon.requestApi(new ApiCallRequest(FROM_FORGOT_PASSWORD, Constants.forgotPassword_api, body, true, ApiCallRequest.TRANSPARENT));
    }

    @Override
    public void onApiFinished(ApiCallResponse responseBus) {
        try {
            String response = responseBus.response;
            Log.e(Constants.APP_TAG, "ForgotPaswdActivity response= " + response);

            if (responseBus.FROM.equals(FROM_FORGOT_PASSWORD)) {
                JSONObject object = new JSONObject(response);
                if (object.optString("response_code").trim().equalsIgnoreCase("200")) {
                    Toast.makeText(ForgotPasswordActivity.this, object.optJSONObject("response_text").optString("Success"), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    tilEmail.setError(object.optJSONObject("response_text").optString("message"));
                    tilEmail.requestFocus();
                    Toast.makeText(ForgotPasswordActivity.this, object.optJSONObject("response_text").optString("message"), Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e){e.printStackTrace();}
    }
}
