package com.rockchipme.app.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rockchipme.app.R;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.helpers.SignupAndSigninApiCall;
import com.rockchipme.app.helpers.Validations;
import com.rockchipme.app.network.ApiCallResponse;
import com.rockchipme.app.network.ApiCallServiceTask;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;
import static com.rockchipme.app.helpers.Constants.APP_TAG;

public class SignUpActivity extends AppCompatActivity implements ApiCallServiceTask.onApiFinish {

    private static final int REQUEST_READ_CONTACTS = 0;
    Validations validations;
    ApiCallServiceTask okHttpCommon;

    SharedPreferences prefLogin;
    SharedPreferences.Editor editor_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_sign_up);

        prefLogin = getSharedPreferences(Constants.PREF_LOGIN, MODE_PRIVATE);
        editor_login = prefLogin.edit();

        initializeViews();
        validations = new Validations();
        okHttpCommon = new ApiCallServiceTask(this);

        populateAutoComplete();

        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptSignup();
                    return true;
                }
                return false;
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignup();
            }
        });
    }

    TextInputLayout tilUserName, tilEmail, tilPhoneNumberCode, tilPhoneNumber, tilPassword, tilConfirmPassword;
    AutoCompleteTextView atvEmail;
    EditText etUserName, etPhoneNumberCode, etPhoneNumber, etPassword, etConfirmPassword;
    Button buttonSignUp;

    public void initializeViews() {
        tilUserName = findViewById(R.id.tilUserName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPhoneNumberCode = findViewById(R.id.tilPhoneNumberCode);
        tilPhoneNumber = findViewById(R.id.tilPhoneNumber);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        etUserName = findViewById(R.id.etUserName);
        atvEmail = findViewById(R.id.atvEmail);
        etPhoneNumberCode = findViewById(R.id.etPhoneNumberCode);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        buttonSignUp = findViewById(R.id.buttonSignUp);


        findViewById(R.id.tvSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        setDrawerToEditText();

        setFont();
        disableEditText(etPhoneNumberCode);
    }

    public void setFont() {
        Typeface font_yekan = Typeface.createFromAsset(getAssets(), "Montserrat_SemiBold_2.otf");
        tilUserName.setTypeface(font_yekan);
        tilEmail.setTypeface(font_yekan);
        tilPhoneNumberCode.setTypeface(font_yekan);
        tilPhoneNumber.setTypeface(font_yekan);
        tilPassword.setTypeface(font_yekan);
        tilConfirmPassword.setTypeface(font_yekan);
    }

    private void setDrawerToEditText() {
//        atvEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_mail, 0);
//        etUserName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_name, 0);
//        etUserName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_name, 0);
//        etPhoneNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_phone, 0);

        atvEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    atvEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_mail1, 0);
                } else {
                    atvEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_mail, 0);
                }
            }
        });

        etUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etUserName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_name1, 0);
                } else {
                    etUserName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_name, 0);
                }
            }
        });

        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_password1, 0);
                } else {
                    etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_password, 0);
                }
            }
        });

        etPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etPhoneNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_phone1, 0);
                } else {
                    etPhoneNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_phone, 0);
                }
            }
        });
    }


    private void disableEditText(EditText editText) {
        editText.setKeyListener(null);
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
            Snackbar.make(atvEmail, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
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
                populateAutoComplete();
            }
        }
    }

    private void attemptSignup() {
        tilUserName.setError(null);
        tilEmail.setError(null);
        tilPhoneNumber.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
        String userName = etUserName.getText().toString().trim();
        String email = atvEmail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();


        if (TextUtils.isEmpty(userName)) {
            tilUserName.setError(getString(R.string.error_field_required));
            etUserName.requestFocus();
            return;
        }


        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.error_field_required));
            atvEmail.requestFocus();
            return;
        }
        if (!validations.isValidEmail(email)) {
            tilEmail.setError(getString(R.string.error_invalid_email));
            atvEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            tilPhoneNumber.setError(getString(R.string.error_field_required));
            etPhoneNumber.requestFocus();
            return;
        }

        if (!validations.isValidPhone(phoneNumber.replace(" ", ""))) {
            tilPhoneNumber.setError(getString(R.string.error_invalid_phone));
            etPhoneNumber.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.error_field_required));
            etPassword.requestFocus();
            return;
        }

        if (!validations.isValidPassword(password)) {
            tilPassword.setError(getString(R.string.error_invalid_password));
            etPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.error_field_required));
            etConfirmPassword.requestFocus();
            return;
        }

        if (!validations.passwordMatch(password, confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.error_password_doesnt_match));
            etConfirmPassword.requestFocus();
            return;
        }

        hideKeyBoard();
        new SignupAndSigninApiCall().signUpApiCall(okHttpCommon, prefLogin, userName, email, phoneNumber, password, "email", "", "");
    }


    private ArrayList<String> getData() {
        ArrayList<String> accountsList = new ArrayList<String>();
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

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(SignUpActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
        atvEmail.setAdapter(adapter);
    }

//    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
//
//        String userName, email, phoneNumber, password;
//
//        UserLoginTask(String userName, String email, String phoneNumber, String password) {
//            this.userName = userName;
//            this.email = email;
//            this.phoneNumber = phoneNumber;
//            this.password = password;
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//            return signUpApiCall(userName, email, phoneNumber, password);
//        }
//
//        @Override
//        protected void onPostExecute(final Boolean success) {
//            mAuthTask = null;
//            showProgress(false);
//            if (success) {
//            } else {
//
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            mAuthTask = null;
//            showProgress(false);
//        }
//    }


    public void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onApiFinished(ApiCallResponse responseBus) {
        try {
            String response = responseBus.response;
            Log.e(Constants.APP_TAG, "SignupActivity response: " + response);
            if (responseBus.FROM.equals(SignupAndSigninApiCall.FROM_SIGNUP)) {
                JSONObject object = new JSONObject(response);
                if (object.optString("response_code").trim().equals("200")) {
                    Toast.makeText(getApplicationContext(), object.optJSONObject("response_text").optString("Success"), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), object.optJSONObject("response_text").optJSONObject("message").optString("errormsg"), Toast.LENGTH_SHORT).show();
//                    tilEmail.setError(object.optJSONObject("response_text").optJSONObject("message").optString("errormsg")+"");
//                    tilEmail.requestFocus();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

