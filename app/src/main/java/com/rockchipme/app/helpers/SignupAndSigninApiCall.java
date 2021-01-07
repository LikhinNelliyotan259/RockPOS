package com.rockchipme.app.helpers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.rockchipme.app.activities.LoginActivity;
import com.rockchipme.app.network.ApiCallRequest;
import com.rockchipme.app.network.ApiCallServiceTask;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Alisons on 10/5/2017.
 */
public class SignupAndSigninApiCall implements GoogleApiClient.OnConnectionFailedListener {

    public static final String FROM_LOGIN = "FROM_LOGIN";
    public void requestLogin(ApiCallServiceTask okHttpCommon, String email, String pswd, String api_key) {
        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .add("password", pswd)
                .add("rest_key", Constants.REST_KEY_MAIN)
                .add("api_key", api_key)
                .add("device_os", Constants.OS)
                .add("device_token", "").build();
//        okHttpCommon.requestApi(FROM_LOGIN, true, body, Constants.SERVER_URL + Constants.user_signin_api, "LOGIN...");
        okHttpCommon.requestApi(new ApiCallRequest(FROM_LOGIN, Constants.user_signin_api, body, true, ApiCallRequest.TRANSPARENT));
    }

    //request skipUser
    public static final String FROM_SKIP_LOGIN = "FROM_SKIP_LOGIN";
    public void requestSkipUSer(ApiCallServiceTask okHttpCommon, SharedPreferences prefLogin) {
        RequestBody body = new FormBody.Builder()
                .add("rest_key", Constants.REST_KEY_MAIN)
                .add("device_os", Constants.OS)
                .add("device_id", prefLogin.getString(Constants.device_id, ""))
                .add("device_token", "").build();
//        okHttpCommon.requestApi(new ApiCallRequest(FROM_SKIP_LOGIN, true, body, Constants.SERVER_URL + Constants.skipRegistration_api, "SKIPPING..."));
        okHttpCommon.requestApi(new ApiCallRequest(FROM_SKIP_LOGIN, Constants.skipRegistration_api, body, true, ApiCallRequest.TRANSPARENT));

    }


    public static final String FROM_SIGNUP = "FROM_SIGNUP";
    public void signUpApiCall(ApiCallServiceTask okHttpCommon, SharedPreferences prefLogin, String userName, String email,
                              String phoneNumber, String password, String account_type, String social_id, String photoUrl) {
        String api_key = "";
        if (prefLogin.getBoolean(Constants.isSkipUser, false)) {
            api_key = prefLogin.getString(Constants.api_key, "");
        }
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("username", userName.replaceAll("\\s+", " "));
        builder.add("email", email);
        builder.add("phone", phoneNumber);
        builder.add("password", password);
        builder.add("device_id", prefLogin.getString(Constants.device_id, ""));
        builder.add("account_type", account_type);
        builder.add("rest_key", Constants.REST_KEY_MAIN);
        builder.add("social_id", social_id==null ? "" : social_id);
        builder.add("photo", photoUrl);
        builder.add("device_os", Constants.OS);
        builder.add("device_token", "");
        builder.add("api_key", api_key);
        RequestBody body = builder.build();

        String progressTitle = "Loading...";
        if (account_type.equalsIgnoreCase("email")) {
            progressTitle = "SIGNUP...";
        }else {
            progressTitle = "SIGNIN...";
        }
//        okHttpCommon.requestApi(new ApiCallRequest(FROM_SIGNUP, true, body, Constants.SERVER_URL + Constants.signup_api, progressTitle));
        okHttpCommon.requestApi(new ApiCallRequest(FROM_SIGNUP, Constants.signup_api, body, true, ApiCallRequest.TRANSPARENT));
    }

    //remove from SharedPreferences after logout
    public void removeFromSP(SharedPreferences.Editor editor_login,SharedPreferences.Editor editor_cart){
        try {
//            editor_login.putBoolean(Constants.isSkipUser, false);
////            editor_login.putBoolean(Constants.isLogin, false);
//            editor_login.remove(Constants.api_key);
//            editor_login.remove(Constants.username);
//            editor_login.remove(Constants.email);
//            editor_login.remove(Constants.accounType);
//            editor_login.remove(Constants.status);
//            editor_login.remove(Constants.phone);
//            editor_login.remove(Constants.photo);
//
//            //remove address details
//            editor_login.putBoolean(Constants.isAddressAdded, false);
//            editor_login.remove(Constants.addressId);
//            editor_login.remove(Constants.addressType);
//            editor_login.remove(Constants.fullName);
//            editor_login.remove(Constants.mobileNumber);
//            editor_login.remove(Constants.pinCode);
//            editor_login.remove(Constants.houseNo);
//            editor_login.remove(Constants.street);
//            editor_login.remove(Constants.landmark);
//            editor_login.remove(Constants.town);
//            editor_login.remove(Constants.state);
//            editor_login.remove(Constants.longitude);
//            editor_login.remove(Constants.latitude);
//
//            final boolean rm_login = editor_login.commit();
            final boolean rm_login =  editor_login.clear().commit();
            Log.e(Constants.APP_TAG, "is removed login details in shared preference: " + rm_login);

            //remove cartDetails
//            editor_cart.putBoolean(Constants.isCouponAdded, false);
//            editor_cart.remove(Constants.couponCode);
//            editor_cart.remove(Constants.couponType);
//            editor_cart.remove(Constants.coupon_perc);
//            editor_cart.remove(Constants.couponRate);
//            editor_cart.remove(Constants.couponId);
//            editor_cart.remove(Constants.couponMinimum);
//
//            editor_cart.remove(Constants.grandTotal);
//            editor_cart.remove(Constants.subTotal);
//            editor_cart.remove(Constants.cartCount);
//
//            final boolean rm_cart = editor_cart.commit();

            final boolean rm_cart = editor_cart.clear().commit();

            Log.e(Constants.APP_TAG, "is removed cart details in shared preference: " + rm_cart);

        } catch (Exception e){e.printStackTrace();}

    }



    public GoogleSignInClient getmGoogleApiClient(AppCompatActivity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .build();
//
//        // Build a GoogleApiClient with access to the Google Sign-In API and the
//        // options specified by gso.
//        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(activity)
//                .enableAutoManage(activity /* FragmentActivity */,/*(OnConnectionFailedListener)*/ this /* OnConnectionFailedListener */)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();

        return GoogleSignIn.getClient(activity, gso);

//        return mGoogleApiClient;
    }


//    signupAndSigninApiCall.revokeAccess(mGoogleApiClient);
    public void revokeAccess(final GoogleApiClient mGoogleApiClient) {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.e(Constants.APP_TAG,"revokeAccess is success= "+status.isSuccess());
                        Log.e(Constants.APP_TAG," is gplus connected= "+mGoogleApiClient.isConnected());
                        if (status.isSuccess()){
                            return;
                        }
                    }
                });
    }


    public void GplusSignOut(final AppCompatActivity activity) {
        try {

            GoogleSignInClient googleSignInClient = getmGoogleApiClient(activity);

//            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                    new ResultCallback<Status>() {
//                        @Override
//                        public void onResult(@NonNull Status status) {
//                            Log.d(Constants.APP_TAG, "GPlusSignOut :"+status.getStatusMessage());
////                            revokeAccess(mGoogleApiClient);
//                        }
//                    });
//
//            GoogleSignInClient mGoogleApiClient = getmGoogleApiClient(activity);
//            //facebook sdk
////        callbackManager = CallbackManager.Factory.create();


            googleSignInClient.signOut()
                    .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // ...
                        }
                    });

        } catch (Exception e){e.printStackTrace();}
    }

    public void  fbLogOut(){
        try{
//            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
//                    .Callback() {
//                @Override
//                public void onCompleted(GraphResponse graphResponse) {
//                    LoginManager.getInstance().logOut();
//                    Log.e(Constants.APP_TAG, "Fb logout");
//                }
//            }).executeAsync();
            LoginManager.getInstance().logOut();
        } catch (Exception e){
            Log.e(Constants.APP_TAG, "Fb logout error");
            e.printStackTrace();
        }
    }


    public void logout(AppCompatActivity activity, SharedPreferences pref_login, SharedPreferences.Editor cart_editor){

        try {



            GplusSignOut(activity);
//            if (pref_login.getString(Constants.accounType, "").equals(Constants.accounType_Gplus)) {
                /**if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    GplusSignOut(mGoogleApiClient);
                    Log.e(Constants.APP_TAG, "gplus logout");
                } else {
                    Log.e(Constants.APP_TAG, "gplus not connect");
//                return;
                }**/
//            }
//            if (pref_login.getString(Constants.accounType, "").equals(Constants.accounType_FB)) {
            LoginManager.getInstance().logOut();
//                fbLogOut();
//            }
        } catch (Exception e){e.printStackTrace();}

        removeFromSP(pref_login.edit(), cart_editor);
        Intent intent = new Intent(activity,LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("from","logout");
        activity.startActivity(intent);
//        activity.finish();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
