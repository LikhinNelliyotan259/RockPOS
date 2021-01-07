package com.rockchipme.app.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rockchipme.app.R;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.helpers.EventBusResponse;
import com.rockchipme.app.helpers.SignupAndSigninApiCall;
import com.rockchipme.app.helpers.Validations;
import com.rockchipme.app.network.ApiCallRequest;
import com.rockchipme.app.network.ApiCallResponse;
import com.rockchipme.app.network.ApiCallServiceTask;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Alisons on 4/12/2018.
 */

public class SettingsActivity extends BaseActivity implements ApiCallServiceTask.onApiFinish{
    AlertDialog dialog;
    TextInputLayout tilOldPassword;

    private boolean isSkippedUser;

    TextView tvChangePassword, tvLogout;
    LinearLayout llChangePassword;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setUpToolBar(false, "SETTINGS", false);


        initializeViews();

//        FacebookSdk.sdkInitialize(getApplicationContext());

        llChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordPopUp();
            }
        });

        findViewById(R.id.llNotification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NotificationListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        findViewById(R.id.llTermsAndCondi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TermsAndCondActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        findViewById(R.id.llLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSkippedUser){
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    new SignupAndSigninApiCall().logout(SettingsActivity.this, prefLogin, editorCart);
                }
            }
        });


    }

    private void initializeViews() {
        llChangePassword = findViewById(R.id.llChange);

        tvChangePassword = findViewById(R.id.tvChangePassword);
        tvLogout = findViewById(R.id.tvLogout);

        isSkippedUser = prefLogin.getBoolean(Constants.isSkipUser, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isSkippedUser = prefLogin.getBoolean(Constants.isSkipUser, false);
        if (isSkippedUser || prefLogin.getString(Constants.accounType,"").equals(Constants.accounType_FB) ||
                prefLogin.getString(Constants.accounType,"").equals(Constants.accounType_Gplus)) {
            llChangePassword.setClickable(false);
            tvChangePassword.setTextColor(getResources().getColor(R.color.gray_text_color));
        } else {
            llChangePassword.setClickable(true);
            tvChangePassword.setTextColor(getResources().getColor(R.color.white));
        }

        if (isSkippedUser){
            tvLogout.setText("LOGIN/SIGNUP");
        } else {
            tvLogout.setText(R.string.logout);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusResponse eventBusResponse){

    }


    private void showChangePasswordPopUp(){
        final Validations validations = new Validations();
        dialog = (new AlertDialog.Builder(SettingsActivity.this))
                .create();
        View promptsView = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.layout_change_password_popup, null);
        dialog.setView(promptsView, 0, 0, 0, 0);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        tilOldPassword = (TextInputLayout) promptsView.findViewById(R.id.tilOldPassword);
        final TextInputLayout tilPassword = (TextInputLayout) promptsView.findViewById(R.id.tilPassword);
        final TextInputLayout tilConfirmPassword = (TextInputLayout) promptsView.findViewById(R.id.tilConfirmPassword);

        final EditText etOldPassword = (EditText) promptsView.findViewById(R.id.etOldPassword);
        final EditText etPassword = (EditText) promptsView.findViewById(R.id.etPassword);
        final EditText etConfirmPassword = (EditText) promptsView.findViewById(R.id.etConfirmPassword);
        final TextView tvCancel = (TextView) promptsView.findViewById(R.id.tvCancel);
        final TextView tvOk = (TextView) promptsView.findViewById(R.id.tvOk);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tilOldPassword.setError(null);
                tilPassword.setError(null);
                tilConfirmPassword.setError(null);
                String oldPassword = etOldPassword.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();
                if(oldPassword.equals("")){
                    tilOldPassword.setError(getString(R.string.error_field_required));
                    etOldPassword.requestFocus();
                    return;
                }else if(password.equals("")){
                    tilPassword.setError(getString(R.string.error_field_required));
                    etPassword.requestFocus();
                    return;
                }
                else if(!validations.isValidPassword(password)){
                    tilPassword.setError(getString(R.string.error_invalid_password));
                    etPassword.requestFocus();
                    return;
                }else if(confirmPassword.equals("")){
                    tilConfirmPassword.setError(getString(R.string.error_field_required));
                    etConfirmPassword.requestFocus();
                    return;
                }else if(!validations.passwordMatch(password, confirmPassword)){
                    tilConfirmPassword.setError(getString(R.string.error_password_doesnt_match));
                    etConfirmPassword.requestFocus();
                    return;
                }
                changePasswordApi(oldPassword, confirmPassword, password);
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private final String FROM_CHANGE_PASSWORD = "FROM_CHANGE_PASSWORD";
    private void changePasswordApi(String oldPassword, String cPassword, String password) {
        RequestBody body=new FormBody.Builder()
                .add("rest_key", Constants.REST_KEY_MAIN)
                .add("api_key", apiKey)
                .add("cur_password", oldPassword)
                .add("password", cPassword)
                .add("cpassword", password)
                .build();
        apiCallServiceTask.requestApi(new ApiCallRequest(FROM_CHANGE_PASSWORD, Constants.changePassword_api, body, true,"Loading..."));
    }

    @Override
    public void onApiFinished(ApiCallResponse responseBus) {
        try {
            switch (responseBus.FROM) {
                case FROM_CHANGE_PASSWORD:
                    try {
                        Log.e(Constants.APP_TAG, "ChangePassword api response:" + responseBus.response);
                        JSONObject object = new JSONObject(responseBus.response);
                        if (object.optString("response_code").trim().equalsIgnoreCase("200")) {
                            Toast.makeText(SettingsActivity.this, object.optJSONObject("response_text").optString("Success"), Toast.LENGTH_SHORT).show();
                            finish();
                            if (dialog != null && dialog.isShowing())
                                dialog.dismiss();
                        } else {
                            String message = object.optJSONObject("response_text").optString("message");
                            if (message != null && message.trim().length() > 0) {
                                if (tilOldPassword != null) {
                                    tilOldPassword.setError(message);
                                    tilOldPassword.requestFocus();
                                }
                                Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e){e.printStackTrace();}
                    break;
//            case :
            }
        } catch (Exception e){e.printStackTrace();}
    }
}
