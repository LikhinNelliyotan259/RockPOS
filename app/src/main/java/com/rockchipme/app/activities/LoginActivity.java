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
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rockchipme.app.R;
import com.rockchipme.app.helpers.AddressHelper;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.helpers.PreferenceUtil;
import com.rockchipme.app.helpers.SignupAndSigninApiCall;
import com.rockchipme.app.helpers.Validations;
import com.rockchipme.app.models.LoginResponse;
import com.rockchipme.app.models.SkipLoginResponse;
import com.rockchipme.app.models.UserDetails;
import com.rockchipme.app.network.ApiCallResponse;
import com.rockchipme.app.network.ApiCallServiceTask;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static android.Manifest.permission.READ_CONTACTS;
import static com.rockchipme.app.helpers.Constants.APP_TAG;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, ApiCallServiceTask.onApiFinish {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references

    //shared preference for user details
    private SharedPreferences prefLogin;
    private SharedPreferences.Editor editor_login;

    private TextView tv_forgot_paswd;

    private ImageView iv_fblogin, iv_gplusLogin;

    private String from = "";

    private ApiCallServiceTask okHttpCommon;
//    String response;

    private Validations validations;

    private CallbackManager callbackManager;

    private SignupAndSigninApiCall signupAndSigninApiCall;

    private GoogleSignInClient mGoogleSignInClient;

    private boolean gplusClick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_login);


        //facbook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        gplusClick = false;

        okHttpCommon = new ApiCallServiceTask(this);
        signupAndSigninApiCall = new SignupAndSigninApiCall();
        validations = new Validations();

        if (getIntent().getStringExtra("from") != null) {
            from = getIntent().getStringExtra("from");
        }

//        initializeViews();
        initialize();

        populateAutoComplete();


        mGoogleSignInClient = signupAndSigninApiCall.getmGoogleApiClient(this);


        iv_gplusLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                revokeAccess();

//                gplusLogin();
            }
        });


        iv_fblogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", /*"user_friends",*/ "email"));
                fbLogin();
            }
        });

        tvSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), SignUpActivity.class), Constants.SIGNUP_ACTIVITY_RESULT_CODE);
            }
        });

        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    hideKeyBoard();
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        populateAutoComplete();
    }

    private void revokeAccess() {
//        signupAndSigninApiCall.revokeAccess(mGoogleApiClient);
        gplusLogin();
    }

    TextInputLayout tilEmail, tilPassword;
    AutoCompleteTextView atvEmail;
    EditText etPassword;
    LinearLayout svLogin, llNotNow;
    TextView tvSignUp;

    public void initialize() {

        prefLogin = getSharedPreferences(Constants.PREF_LOGIN, MODE_PRIVATE);
        editor_login = prefLogin.edit();

        iv_fblogin = findViewById(R.id.iv_fbLogin);
        iv_gplusLogin = findViewById(R.id.iv_gplusLogin);

        atvEmail = findViewById(R.id.atvEmail);
//        try {
        tilEmail = findViewById(R.id.tilEmail);
//        }catch (Exception e){Log.e("aaaaaa",e.toString());}
        tilPassword = findViewById(R.id.tilPassword);
        etPassword = findViewById(R.id.etPassword);
        tvSignUp = findViewById(R.id.tvSignUp);
        svLogin = findViewById(R.id.svLogin);
        tv_forgot_paswd = findViewById(R.id.tv_forgotPaswd);
        llNotNow = findViewById(R.id.llNotNow);
        llNotNow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                signupAndSigninApiCall.requestSkipUSer(okHttpCommon, prefLogin);

            }
        });
        tv_forgot_paswd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        setFont();

        try {
            String device_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            editor_login.putString(Constants.device_id, device_id).commit();

            String uniqueID = UUID.randomUUID().toString();
            Log.e(Constants.APP_TAG, "uniqueID= " + uniqueID);

        } catch (Exception e) {
            Log.e(Constants.APP_TAG, "SplashScreen excep1: " + e.toString());
        }

        setDrawerToEditText();

    }

    private void setDrawerToEditText() {

//        atvEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_mail, 0);
//        etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_password, 0);

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
    }

    public void setFont() {
        Typeface font_yekan = Typeface.createFromAsset(getAssets(), "Montserrat_SemiBold_2.otf");
        tilEmail.setTypeface(font_yekan);
        tilPassword.setTypeface(font_yekan);
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
                    .setAction(android.R.string.ok, new OnClickListener() {
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

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addEmailsToAutoComplete(getData());
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
//        if (mAuthTask != null) {
//            return;
//        }
        tilEmail.setError(null);
        tilPassword.setError(null);
        String email = atvEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

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
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.error_field_required));
            etPassword.requestFocus();
            return;
        }
//        if (!validations.isValidPassword(password)) {
//            tilPassword.setError(getString(R.string.error_invalid_password));
//            etPassword.requestFocus();
//            return;
//        }

//        hideKeyBoard();
//        showProgress(true);
//        mAuthTask = new UserLoginTask(email, password);
//        mAuthTask.execute((Void) null);
        String api_key = "";
        if (prefLogin.getBoolean(Constants.isSkipUser, false)) {
            api_key = prefLogin.getString(Constants.api_key, "");
//            Log.e(Constants.APP_TAG,"LoginActivty, Skippeduser apikey: "+api_key);
        }
        signupAndSigninApiCall.requestLogin(okHttpCommon, email, password, api_key);
    }


    //fb login
    private void fbLogin() {
//        if (signupAndSigninApiCall!=null) {
//            signupAndSigninApiCall.fbLogOut();
//        }
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                try {
                    String accessToken = AccessToken.getCurrentAccessToken().getToken();

                    if (accessToken != null) {
                        Log.e(Constants.APP_TAG, "fb_accesstoken: " + accessToken);

//                    LoginManager.getInstance().logOut();

                        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
//                                    Log.e(Constants.APP_TAG, "fb_response " + object.toString());
                                    final String username = object.optString("name");
                                    final String email = object.optString("email");
                                    final String social_id = object.optString("id");
                                    final String photoUrl = object.optJSONObject("picture").optJSONObject("data").optString("url");
                                    final String link = object.optString("link");

                                    if (email == null || email.trim().length() < 1) {
                                        Toast.makeText(LoginActivity.this, "can't access your email", Toast.LENGTH_SHORT).show();
                                        if (signupAndSigninApiCall != null) {
                                            signupAndSigninApiCall.fbLogOut();
                                        }
                                        return;
                                    }

                                    if (signupAndSigninApiCall == null) {
                                        signupAndSigninApiCall = new SignupAndSigninApiCall();
                                    }
                                    signupAndSigninApiCall.signUpApiCall(okHttpCommon, prefLogin, username, email, "", "", "fb", social_id, photoUrl);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,email,name,link,picture.width(150).height(150)");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancel() {

                Log.e(Constants.APP_TAG, "facebook login canceled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(Constants.APP_TAG, "fb_error: " + error.toString());
            }
        });
    }

    private void gplusLogin() {
        if (signupAndSigninApiCall == null){
            signupAndSigninApiCall = new SignupAndSigninApiCall();
        }
        if (mGoogleSignInClient == null) {
            mGoogleSignInClient = signupAndSigninApiCall.getmGoogleApiClient(this);
        }
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
    }


    private void handleSignInResult(GoogleSignInResult result) {
        //GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//        Log.e("Login:Gplus", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            final String username = acct.getDisplayName();
            final String email = acct.getEmail();
            final String social_id = acct.getId();
            String personPhoto = "";
            try {
                if (acct.getPhotoUrl() != null) {
                    personPhoto = acct.getPhotoUrl().toString();
                }
            } catch (Exception e) {
                Log.e(Constants.APP_TAG, "gplus photos err " + e.toString());
            }

            signupAndSigninApiCall.signUpApiCall(okHttpCommon, prefLogin, username, email, "", "", "gplus", social_id, personPhoto);

//

        } else {
            Log.e(Constants.APP_TAG, "gplus not working");
        }
    }

    private void loginResponse(String response) {
        try {
            LoginResponse loginResponse = new Gson().fromJson(response, LoginResponse.class);

            UserDetails userDetails = loginResponse.responseText.userDetails;

            if (userDetails != null) {
                if (userDetails.success.equals("1")) {
                    afterGotTheUserData(userDetails);
                }
            } else {
                editor_login.putBoolean(Constants.isSkipUser, true).commit();
                Toast.makeText(this, loginResponse.responseText.message.errormsg, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //store usersData to shared preference
    private void afterGotTheUserData(UserDetails userDetails) {
        editor_login.putBoolean(Constants.isSkipUser, false);
        editor_login.putBoolean(Constants.isLogin, true);
        editor_login.putString(Constants.api_key, userDetails.apiKey);
        editor_login.putString(Constants.username, userDetails.username);
        editor_login.putString(Constants.email, userDetails.email);
        editor_login.putString(Constants.accounType, userDetails.accountType);
        editor_login.putString(Constants.status, userDetails.status);
        editor_login.putString(Constants.phone, userDetails.phone);
        editor_login.putString(Constants.photo, userDetails.photo);

        if (userDetails.address != null && userDetails.address.size() > 0) {
            Log.e(Constants.APP_TAG, "address not empty");

            new AddressHelper(this).storeAddress(userDetails.address, userDetails.address.get(0).addressId);

        } else {
            Log.e(Constants.APP_TAG, "address empty");
            new AddressHelper(this).clearSharedPrefernce();
        }
        editor_login.commit();
        Log.e(Constants.APP_TAG, "account detals  stored :" + editor_login.commit());

        hideKeyBoard();

        if (from != null && (from.equalsIgnoreCase("splash") || from.equalsIgnoreCase("logout"))) {
            openHome();
        } else {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            finish();
        }

    }

    private void skipUserResponse(String response) {
        try {
            SkipLoginResponse loginResponse = new Gson().fromJson(response, SkipLoginResponse.class);
            SkipLoginResponse.ResponseText responseText = loginResponse.responseText;
            if (responseText.api_key != null && responseText.api_key.trim().length() > 1) {
                editor_login.putBoolean(Constants.isSkipUser, true);
                editor_login.putBoolean(Constants.isLogin, false);
                editor_login.putString(Constants.api_key, responseText.api_key);
                editor_login.putString(Constants.photo, responseText.profile_pic);
                editor_login.commit();
                openHome();
            } else {
                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openHome() {
        try {
            if (new PreferenceUtil(this).getOutletStoreDetails().rest_key.trim().length() < 1) {
                Intent intent = new Intent(getApplicationContext(), OutletsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        ActivityCompat.finishAffinity(this);
        finish();
    }

    private void socialLoginResponse(String response) {
        try {
            LoginResponse loginResponse = new Gson().fromJson(response, LoginResponse.class);

            UserDetails userDetails = loginResponse.responseText.userDetails;

            if (userDetails != null) {
                if (userDetails.success.equals("2")) {
                    Toast.makeText(LoginActivity.this, "Already registered email", Toast.LENGTH_SHORT).show();
                    atvEmail.setText(userDetails.email);
                    return;
                }
                if (userDetails.success.equals("1")) {
                    //store user data to shared preference
                    afterGotTheUserData(userDetails);

                }
            } else {
                editor_login.putBoolean(Constants.isSkipUser, true).commit();
                Toast.makeText(this, loginResponse.responseText.message.errormsg, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
        atvEmail.setAdapter(adapter);
    }

    public void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.SIGNUP_ACTIVITY_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                // get String data from Intent
            }
        }


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == Constants.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            Log.e(Constants.APP_TAG, "gplus isSuccess:" + result.isSuccess() + " status:"+result.getStatus());
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(Constants.APP_TAG, "onConnectionFailed " + connectionResult.getErrorMessage());
    }

    @Override
    public void onApiFinished(ApiCallResponse response) {
        try {
            switch (response.FROM) {
                case SignupAndSigninApiCall.FROM_LOGIN:
                    loginResponse(response.response);
                    break;
                case SignupAndSigninApiCall.FROM_SKIP_LOGIN:
                    skipUserResponse(response.response);
                    break;
                case SignupAndSigninApiCall.FROM_SIGNUP:
                    socialLoginResponse(response.response);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

